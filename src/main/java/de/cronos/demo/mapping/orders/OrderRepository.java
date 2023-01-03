package de.cronos.demo.mapping.orders;

import de.cronos.demo.mapping.customers.CustomerEntity_;
import de.cronos.demo.mapping.orders.events.QueryOrderEvent;
import de.cronos.demo.mapping.products.ProductEntity_;
import de.cronos.demo.mapping.tags.TagEntity_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID>, JpaSpecificationExecutor<OrderEntity> {

    @Query("""
                SELECT o
                FROM OrderEntity o
                WHERE o.id = :id
                AND o.state IN (
                    de.cronos.demo.mapping.orders.OrderState.NEW,
                    de.cronos.demo.mapping.orders.OrderState.ACCEPTED
                )
            """)
    Optional<OrderEntity> findCancelableById(UUID id);

    default Specification<OrderEntity> buildSpec(QueryOrderEvent queryOrderEvent) {
        return (Root<OrderEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            final var predicates = new ArrayList<Predicate>();

            // Order state
            queryOrderEvent.getOrderState().ifPresent(state -> predicates.add(
                    criteriaBuilder.equal(root.get(OrderEntity_.state), state))
            );

            // Customer email
            queryOrderEvent.getCustomerMail().ifPresent(mail -> predicates.add(
                    criteriaBuilder.like(root.get(OrderEntity_.customer).get(CustomerEntity_.email), "%" + mail + "%"))
            );

            // Referenced tags
            queryOrderEvent.getReferencedTagIds().forEach(tagId -> {
                final var existsSubquery = query.subquery(String.class);
                final var subRoot = existsSubquery.from(OrderEntity.class);
                final var subTagJoin = subRoot.join(OrderEntity_.product)
                        .join(ProductEntity_.tags);
                existsSubquery.select(criteriaBuilder.literal("x"));
                existsSubquery.where(
                        criteriaBuilder.and(
                                criteriaBuilder.equal(subRoot.get(OrderEntity_.id), root.get(OrderEntity_.id)),
                                criteriaBuilder.equal(subTagJoin.get(TagEntity_.id), tagId)
                        )
                );

                predicates.add(criteriaBuilder.exists(existsSubquery));
            });

            return predicates.stream().reduce(
                    criteriaBuilder.conjunction(),
                    criteriaBuilder::and
            );
        };
    }
}
