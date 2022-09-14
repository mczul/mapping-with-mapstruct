package de.cronos.demo.mapping.orders.model.read;

import de.cronos.demo.mapping.orders.model.OrderState;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Value
@Builder
public class OrderQuery {

    @NotNull
    Optional<String> customerMail;

    @NotNull
    Optional<OrderState> orderState;

}
