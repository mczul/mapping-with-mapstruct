package de.cronos.demo.mapping.customers;

import de.cronos.demo.mapping.customers.model.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {

    @Query("""
                SELECT c
                FROM CustomerEntity c
                INNER JOIN c.orders o
                WHERE o.id = :orderId
            """)
    Optional<CustomerEntity> findByOrderId(UUID orderId);

}
