package de.cronos.demo.mapping.orders.model.events;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class PlaceOrderEvent {

    UUID customerId;

    UUID productId;

    Integer quantity;

}
