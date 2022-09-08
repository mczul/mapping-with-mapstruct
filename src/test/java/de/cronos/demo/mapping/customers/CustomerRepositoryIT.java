package de.cronos.demo.mapping.customers;

import de.cronos.demo.mapping.customers.model.CustomerEntity;
import de.cronos.demo.mapping.orders.OrderRepository;
import de.cronos.demo.mapping.orders.model.OrderEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Customer: JPA & Repository")
@DisplayNameGeneration(ReplaceUnderscores.class)
class CustomerRepositoryIT {
    @Autowired
    protected OrderRepository orderRepository;
    @Autowired
    protected CustomerRepository underTest;

    @Nested
    @DisplayName("queries")
    class Queries {

        @Test
        @Sql("classpath:db/simple.sql")
        void findByOrderId_should_find_expected() {
            // given
            final var expectations = orderRepository.findAll().stream()
                    .collect(Collectors.groupingBy(OrderEntity::getCustomer));
            // Checking testing conditions is still part of the "given" block
            assertThat(expectations).hasSizeGreaterThan(1);

            // when
            expectations.forEach((customer, orders) -> {
                final var actualCustomerIds = orders.stream()
                        .map(OrderEntity::getId)
                        .map(underTest::findByOrderId)
                        .map(Optional::orElseThrow)
                        .map(CustomerEntity::getId)
                        .distinct().toList();

                // then
                assertThat(actualCustomerIds).as("Unexpected number of customers").hasSize(1);
                final var actualCustomerId = actualCustomerIds.stream().findFirst().orElseThrow();
                assertThat(actualCustomerId).isEqualTo(customer.getId());
            });
        }

    }

}
