package de.cronos.demo.mapping.orders.model.read;

import de.cronos.demo.mapping.customers.model.read.CustomerInfo;
import de.cronos.demo.mapping.orders.model.OrderState;
import de.cronos.demo.mapping.products.model.read.ProductInfo;
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