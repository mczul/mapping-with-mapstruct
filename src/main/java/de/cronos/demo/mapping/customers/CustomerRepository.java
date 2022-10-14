package de.cronos.demo.mapping.customers;

import de.cronos.demo.mapping.customers.statistics.CustomerStatistics;
import de.cronos.demo.mapping.customers.summary.CustomerRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query(value = """
                SELECT new de.cronos.demo.mapping.customers.summary.CustomerRecord(c.firstName, c.lastName, c.birthday, size(c.orders))
                FROM CustomerEntity c
            """)
    Page<CustomerRecord> loadCustomerRecords(Pageable pageable);

    @Query(nativeQuery = true, value = """
                SELECT acs.email AS email
                     , acs.first_name AS firstName, acs.last_name AS lastName, acs.birthday AS birthday
                     , acs.last_order_placed AS lastOrderPlaced, acs.last_product_name_ordered AS lastProductOrdered
                     , acs.last_quantity_ordered AS lastQuantityOrdered, acs.last_order_state AS lastOrderState
                FROM active_customer_statistics acs
            """)
    Page<CustomerStatistics> loadStatistics(Pageable pageable);

}
