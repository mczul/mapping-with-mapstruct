package de.cronos.demo.mappingwithmapstruct.orders;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
@Builder
public class OrderDetails {

    UUID id;

    UUID customerId;

    OrderState state;

    String articleName;

    Integer articleQuantity;

    Instant created;

    Instant lastModified;

}
