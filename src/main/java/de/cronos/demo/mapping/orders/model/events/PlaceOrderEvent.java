package de.cronos.demo.mapping.orders.model.events;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Value
@Builder
public class PlaceOrderEvent {

    @NotNull
    UUID customerId;

    @NotNull
    UUID productId;

    @NotNull
    @Min(1)
    Integer quantity;

}
