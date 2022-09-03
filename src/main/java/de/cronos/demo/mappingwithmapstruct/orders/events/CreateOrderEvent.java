package de.cronos.demo.mappingwithmapstruct.orders.events;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;
@Value
@Builder
public class CreateOrderEvent {

    UUID customerId;

    String articleName;

    Integer articleQuantity;

}
