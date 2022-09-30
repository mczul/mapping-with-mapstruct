package de.cronos.demo.mapping.orders.events;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Value
@Builder
public class CancelOrderEvent {

    @NotNull
    UUID orderId;

}
