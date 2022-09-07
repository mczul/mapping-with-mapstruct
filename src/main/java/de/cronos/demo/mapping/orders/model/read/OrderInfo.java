package de.cronos.demo.mapping.orders.model.read;

import de.cronos.demo.mapping.orders.model.OrderState;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.UUID;

@Value
@EqualsAndHashCode(of = "id")
@Builder
public class OrderInfo {

    UUID id;

    UUID customerId;

    String customerEmail;

    UUID productId;

    String productName;

    Integer quantity;

    OrderState state;

}
