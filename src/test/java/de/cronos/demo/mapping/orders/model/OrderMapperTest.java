package de.cronos.demo.mapping.orders.model;

import de.cronos.demo.mapping.customers.model.CustomerMapperTest;
import de.cronos.demo.mapping.products.model.ProductMapperTest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.UUID;

public class OrderMapperTest {
    public static OrderEntity randomOrder() {
        final var randomId = Math.abs(new Random().nextInt(1_000));

        return OrderEntity.builder()
                .id(UUID.randomUUID())
                .product(ProductMapperTest.randomProduct())
                .customer(CustomerMapperTest.randomCustomer())
                .created(Instant.now().minus(randomId, ChronoUnit.HOURS))
                .lastModified(Instant.now().minus(randomId, ChronoUnit.MINUTES))
                .build();
    }

}
