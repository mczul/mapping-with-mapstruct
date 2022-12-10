package de.cronos.demo.mapping.products;

import de.cronos.demo.mapping.common.mapping.IdLookupMapper;
import de.cronos.demo.mapping.products.events.CreateProductEvent;
import de.cronos.demo.mapping.tags.TagMapperImpl;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        TagMapperImpl.class,
        ProductMapperImpl.class
})
public class ProductMapperTest {
    @MockBean
    protected IdLookupMapper idLookupMapper;
    @Autowired
    protected ProductMapperImpl underTest;

    public static CreateProductEvent randomCreateProductEvent() {
        final var randomId = Math.abs(new Random().nextInt(1_000));

        return CreateProductEvent.builder()
                .name("Product #%d".formatted(randomId))
                .build();
    }

    public static ProductEntity randomProduct() {
        final var randomId = Math.abs(new Random().nextInt(1_000));

        return ProductEntity.builder()
                .id(UUID.randomUUID())
                .name("Product #%d".formatted(randomId))
                .created(Instant.now().minus(randomId, ChronoUnit.HOURS))
                .lastModified(Instant.now().minus(randomId, ChronoUnit.MINUTES))
                .build();
    }

    @Nested
    class From {
        @ParameterizedTest
        @CsvSource(value = {
                "name,name"
        })
        void simple_create_event(String sourceProperty, String targetProperty) {
            // given
            final var event = randomCreateProductEvent();

            // when
            final var actual = underTest.from(event);

            // then
            assertThat(actual).isNotNull();
            assertThat(ReflectionTestUtils.getField(actual, targetProperty))
                    .isEqualTo(ReflectionTestUtils.getField(event, sourceProperty));
        }

    }

}
