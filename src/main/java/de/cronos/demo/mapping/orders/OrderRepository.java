package de.cronos.demo.mapping.orders;

import de.cronos.demo.mapping.orders.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {

    @Query("""
                SELECT o
                FROM OrderEntity o
                WHERE o.id = :id
                AND o.state IN (
                    de.cronos.demo.mapping.orders.model.OrderState.NEW,
                    de.cronos.demo.mapping.orders.model.OrderState.ACCEPTED
                )
            """)
    Optional<OrderEntity> findCancelableById(UUID id);

}
