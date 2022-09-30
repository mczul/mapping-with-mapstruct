package de.cronos.demo.mapping.orders;

import de.cronos.demo.mapping.customers.CustomerMapperTest;
import de.cronos.demo.mapping.customers.summary.CustomerInfo;
import de.cronos.demo.mapping.orders.summary.OrderDetails;
import de.cronos.demo.mapping.products.ProductMapperTest;
import de.cronos.demo.mapping.products.summary.ProductInfo;

import java.time.Instant;
import java.time.OffsetDateTime;
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

    public static OrderDetails randomDetails() {
        final var random = Math.abs(new Random().nextInt(1_000));

        return OrderDetails.builder()
                .id(UUID.randomUUID())
                .customer(CustomerInfo.builder().id(UUID.randomUUID()).build())
                .product(ProductInfo.builder().id(UUID.randomUUID()).build())
                .estimatedShipment(OffsetDateTime.now().plus(random, ChronoUnit.HOURS))
                .state(OrderState.NEW)
                .quantity(Math.abs(new Random().nextInt(100)))
                .created(Instant.now().minus(random, ChronoUnit.HOURS))
                .lastModified(Instant.now().minus(random, ChronoUnit.MINUTES))
                .build();

    }

}
