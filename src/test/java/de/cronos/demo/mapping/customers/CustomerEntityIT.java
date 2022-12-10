package de.cronos.demo.mapping.customers;

import de.cronos.demo.mapping.orders.OrderEntity;
import de.cronos.demo.mapping.orders.OrderState;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

@DisplayName("JPA Entities: Customer")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DataJpaTest
class CustomerEntityIT {

    @Autowired
    protected CustomerRepository repository;

    @Test
    void save_with_new_orders_only() {
        // given
        final var detached = CustomerEntity.builder()
                .id(null)
                .version(null)
                .firstName("Foo")
                .lastName("Bar")
                .email("foo@bar.com")
                .birthday(LocalDate.now().minusYears(42))
                .orders(List.of(OrderEntity.builder()
                        .id(null)
                        .version(null)
                        .product(null)
                        .quantity(42)
                        .state(OrderState.NEW)
                        .build()))
                .created(null)
                .lastModified(null)
                .build();

        // when
        final var actual = repository.save(detached);

        // then
        Assertions.assertThat(actual.getId()).isNotNull();

    }

}
