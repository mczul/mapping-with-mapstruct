package de.cronos.demo.mapping.products;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.UUID;

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

}
