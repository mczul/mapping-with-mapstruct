package de.cronos.demo.mapping.products;

import de.cronos.demo.mapping.products.events.CreateProductEvent;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
public class ProductMapperTest {
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

        @Test
        void dummy() {
            // given
            final var event = CreateProductEvent.builder()
                    .name("Whatever")
                    .build();

            // when
//            final var actual = underTest.from(event);

            // then

        }

    }


}
