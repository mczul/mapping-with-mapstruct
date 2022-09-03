package de.cronos.demo.mappingwithmapstruct.orders;

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

    OrderState state;

    String articleName;

    Integer articleQuantity;

}
