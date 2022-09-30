package de.cronos.demo.mapping.orders.summary;

import de.cronos.demo.mapping.customers.summary.CustomerInfo;
import de.cronos.demo.mapping.orders.OrderState;
import de.cronos.demo.mapping.products.summary.ProductInfo;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

@Value
@Builder
public class OrderDetails {

    UUID id;

    CustomerInfo customer;

    OrderState state;

    ProductInfo product;

    Integer quantity;

    OffsetDateTime estimatedShipment;

    Instant created;

    Instant lastModified;

}
