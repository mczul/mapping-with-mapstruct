package de.cronos.demo.mapping.orders.events;

import de.cronos.demo.mapping.orders.OrderState;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Value
@Builder
public class QueryOrderEvent {

    @NotNull
    Optional<String> customerMail;

    @NotNull
    Optional<OrderState> orderState;

}
