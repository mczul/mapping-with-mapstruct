package de.cronos.demo.mapping.customers;

import de.cronos.demo.mapping.customers.model.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {

}
