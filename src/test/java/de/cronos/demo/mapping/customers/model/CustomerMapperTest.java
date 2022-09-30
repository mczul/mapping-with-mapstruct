package de.cronos.demo.mapping.customers.model;

import de.cronos.demo.mapping.common.mapping.JavaUtilOptionalMapper;
import de.cronos.demo.mapping.orders.OrderMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * We should always try to reduce redundancy - this is really important when we need to maintain lots of test driven
 * code. But on the other hand: It's dangerous to follow blindly the DRY principle in cases where tight coupling
 * of test code is not reflected by the aspects under test.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Customer: Mapping")
@DisplayNameGeneration(ReplaceUnderscores.class)
public class CustomerMapperTest {
    @Mock
    protected JavaUtilOptionalMapper optionalMapper;
    @Mock
    protected OrderMapper orderMapper;
    @InjectMocks
    protected CustomerMapperImpl underTest;

    public static CustomerEntity randomCustomer() {
        final var randomId = Math.abs(new Random().nextInt(1_000));

        return CustomerEntity.builder()
                .id(UUID.randomUUID())
                .email("user_%d@gmail.com".formatted(randomId))
                .firstName("Max #%d".formatted(randomId))
                .lastName("Mustermann #%d".formatted(randomId))
                .birthday(LocalDate.of(1980, 6, 1).plusDays(randomId))
                .created(Instant.now().minus(randomId, ChronoUnit.HOURS))
                .lastModified(Instant.now().minus(randomId, ChronoUnit.MINUTES))
                .orders(mock(List.class))
                .build();
    }

    @Nested
    class ToInfo {

        @ParameterizedTest
        // Could be a @ValueSource... just a showcase for more complex mappings
        // with different source and targt attribute names
        @CsvSource({
                "id, id",
                "email, email",
                "firstName, firstName",
                "lastName, lastName",
                "birthday, birthday"
        })
        void from_random_entity(String sourceAttributeName, String targetAttributeName) {
            // given
            final var source = randomCustomer();

            // when
            final var actual = underTest.toInfo(source);

            // then
            assertThat(actual).isNotNull();
            assertThat(actual).hasFieldOrPropertyWithValue(
                    sourceAttributeName,
                    ReflectionTestUtils.invokeGetterMethod(source, targetAttributeName)
            );
        }

    }

    @Nested
    class ToDetails {

        @ParameterizedTest
        @CsvSource({
                "id, id",
                "email, email",
                "firstName, firstName",
                "lastName, lastName",
                "birthday, birthday",
                "created, created",
                "lastModified, lastModified"
        })
        void from_random_entity(String sourceAttributeName, String targetAttributeName) {
            // given
            final var source = randomCustomer();

            // when
            final var actual = underTest.toDetails(source);

            // then
            assertThat(actual).isNotNull();
            assertThat(actual).hasFieldOrPropertyWithValue(
                    sourceAttributeName,
                    ReflectionTestUtils.invokeGetterMethod(source, targetAttributeName)
            );
        }

    }


}
