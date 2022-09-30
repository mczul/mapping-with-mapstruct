package de.cronos.demo.mapping.orders;

import de.cronos.demo.mapping.PersistenceConfig;
import de.cronos.demo.mapping.customers.CustomerRepository;
import de.cronos.demo.mapping.orders.events.QueryOrderEvent;
import de.cronos.demo.mapping.products.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Orders: JPA & Repository")
@DisplayNameGeneration(ReplaceUnderscores.class)
@Import({PersistenceConfig.class})
class OrderRepositoryIT {
    @Autowired
    protected CustomerRepository customerRepository;
    @Autowired
    protected ProductRepository productRepository;

    @Autowired
    protected TestEntityManager entityManager;

    @Autowired
    protected OrderRepository underTest;

    @Nested
    @DisplayName("inserts & updates")
    class InsertAndUpdate {

        @ParameterizedTest
        @CsvSource({
                "NEW, 1",
                "ACCEPTED, 2",
                "IN_PROGRESS, 3",
                "SUCCESS, 4",
                "FAILURE, 5",
        })
        @Sql("classpath:db/simple.sql")
        void insert_with_modified_references_must_not_cascade(OrderState expectedState, Integer expectedQuantity) {
            // given
            final var customer = customerRepository.findAll().stream().findFirst().orElseThrow();
            final var product = productRepository.findAll().stream().findFirst().orElseThrow();

            final var newAndDetached = OrderEntity.builder()
                    .id(null)
                    .state(expectedState)
                    .customer(customer.withLastName(customer.getLastName() + "-Meyer"))
                    .product(product.withName(product.getName() + " (NEW!)"))
                    .quantity(expectedQuantity)
                    .created(null)
                    .lastModified(null)
                    .build();

            // when
            final var actual = underTest.saveAndFlush(newAndDetached);
            final var loadedCustomer = customerRepository.findById(actual.getCustomer().getId()).orElseThrow();
            final var loadedProduct = productRepository.findById(actual.getProduct().getId()).orElseThrow();

            // then
            assertThat(actual.getId()).isNotNull();
            assertThat(actual.getCreated()).isStrictlyBetween(Instant.now().minusSeconds(1), Instant.now());
            assertThat(actual.getLastModified()).isStrictlyBetween(Instant.now().minusSeconds(1), Instant.now());
            assertThat(loadedCustomer.getLastName()).isEqualTo(customer.getLastName());
            assertThat(loadedProduct.getName()).isEqualTo(product.getName());
        }

        @Test
        @Sql("classpath:db/simple.sql")
        void update_and_increment_optimistic_lock_version() {
            // given
            final var source = underTest.findAll().stream().findFirst().orElseThrow();
            final var expectedCreated = source.getCreated();
            final var sourceLastModified = source.getLastModified();
            entityManager.detach(source);

            // when
            final var actual = underTest.saveAndFlush(source.withQuantity(source.getQuantity() + 1));

            // then
            assertThat(actual.getId()).isEqualTo(source.getId());
            assertThat(actual.getCreated()).isEqualTo(expectedCreated);
            assertThat(actual.getLastModified()).isAfter(sourceLastModified);
            assertThat(actual.getLastModified()).isStrictlyBetween(Instant.now().minusSeconds(1), Instant.now());
            assertThat(actual.getVersion()).isGreaterThan(source.getVersion());
        }

    }

    @Nested
    @DisplayName("deletes")
    class Delete {

    }

    @Nested
    @DisplayName("queries")
    class Query {

        @ParameterizedTest
        @CsvSource(value = {
                "null, null",
                "NEW, null",
                "null, @gmail.c",
                "null, @web.d",
                "ACCEPTED, @web.d",
                "ACCEPTED, @gmail.c"
        }, nullValues = "null")
        @Sql("classpath:db/simple.sql")
        void find_all_by_given_query_spec(OrderState state, String customerMail) {
            // given
            final var query = QueryOrderEvent.builder()
                    .orderState(Optional.ofNullable(state))
                    .customerMail(Optional.ofNullable(customerMail))
                    .build();
            final var spec = underTest.buildSpec(query);
            final var expected = underTest.findAll().stream()
                    .filter(orderEntity -> state == null || orderEntity.getState() == state)
                    .filter(orderEntity -> customerMail == null || orderEntity.getCustomer().getEmail().contains(customerMail))
                    .toList();

            // when
            final var actual = underTest.findAll(spec);

            // then
            assertThat(actual).hasSameElementsAs(expected);
        }
    }

}
