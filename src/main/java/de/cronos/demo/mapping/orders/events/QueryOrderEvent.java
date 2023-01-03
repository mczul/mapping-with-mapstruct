package de.cronos.demo.mapping.orders.events;

import de.cronos.demo.mapping.orders.OrderState;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Value
@Builder
public class QueryOrderEvent {

    @NotNull
    Optional<@Size(min = 3) String> customerMail;

    @NotNull
    Optional<OrderState> orderState;

    /**
     * Caution: All of the tag ids must be assigned to a referenced product
     */
    @NotNull
    List<UUID> referencedTagIds = new ArrayList<>();

}
