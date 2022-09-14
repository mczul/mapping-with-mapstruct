package de.cronos.demo.mapping.orders;

import de.cronos.demo.mapping.orders.model.OrderState;
import de.cronos.demo.mapping.orders.model.read.OrderQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Orders: JPA & Repository")
@DisplayNameGeneration(ReplaceUnderscores.class)
class OrderRepositoryIT {

    @Autowired
    protected OrderRepository underTest;

    @Nested
    @DisplayName("queries")
    class Queries {

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
            final var query = OrderQuery.builder()
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
