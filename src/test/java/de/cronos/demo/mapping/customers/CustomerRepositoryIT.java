package de.cronos.demo.mapping.customers;

import de.cronos.demo.mapping.customers.summary.CustomerRecord;
import de.cronos.demo.mapping.orders.OrderEntity;
import de.cronos.demo.mapping.orders.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
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
            // Checking conditions is still part of the "given" block
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

        @Test
        @Sql("classpath:db/simple.sql")
        void loadCustomerRecords_should_return_expected() {
            // given
            final var expected = underTest.findAll().stream()
                    .map(entity -> new CustomerRecord(
                            entity.getId(), entity.getFirstName(), entity.getLastName(), entity.getBirthday(),
                            entity.orders.size())
                    )
                    .toList();
            assertThat(expected).isNotEmpty();

            // when
            final var actual = underTest.loadCustomerRecords(Pageable.unpaged());

            // then
            assertThat(actual).containsExactlyElementsOf(expected);
        }

        @Test
        @Sql("classpath:db/simple.sql")
        void loadStatistics_should_return_expected() {
            // given
            final var expected = underTest.findAll().stream()
                    .map(CustomerEntity::getOrders)
                    .flatMap(Collection::stream)
                    .filter(order -> order.getCreated().isAfter(Instant.now().minus(30, ChronoUnit.DAYS)))
                    .map(OrderEntity::getCustomer)
                    .distinct()
                    .toList();
            assertThat(expected).isNotEmpty();

            // when
            final var actual = underTest.loadStatistics(Pageable.unpaged());

            // then
            assertThat(actual.getContent().size()).isEqualTo(actual.getTotalElements());
            assertThat(actual.getContent().size()).isEqualTo(expected.size());
            assertThat(actual.getContent()).allMatch(actualEntry ->
                    expected.stream().anyMatch(expectedEntry ->
                            Objects.equals(actualEntry.getEmail(), expectedEntry.getEmail()) &&
                                    Objects.equals(
                                            actualEntry.getLastOrderPlaced(),
                                            expectedEntry.getOrders().stream().map(OrderEntity::getCreated).max(Comparator.naturalOrder()).orElseThrow()
                                    )));
        }

    }

}
