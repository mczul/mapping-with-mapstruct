package de.cronos.demo.mapping.orders;

import de.cronos.demo.mapping.customers.model.CustomerEntity_;
import de.cronos.demo.mapping.orders.events.QueryOrderEvent;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.LinkedList;
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
            final var predicates = new LinkedList<Predicate>();

            // Order state
            queryOrderEvent.getOrderState().ifPresent(state -> predicates.push(
                    criteriaBuilder.equal(root.get(OrderEntity_.state), state))
            );

            // Customer email
            queryOrderEvent.getCustomerMail().ifPresent(mail -> predicates.push(
                    criteriaBuilder.like(root.get(OrderEntity_.customer).get(CustomerEntity_.email), "%" + mail + "%"))
            );

            return predicates.stream().reduce(
                    criteriaBuilder.conjunction(),
                    (predicate, conjunction) -> criteriaBuilder.and(conjunction, predicate)
            );
        };
    }
}
