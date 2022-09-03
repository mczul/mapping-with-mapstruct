package de.cronos.demo.mappingwithmapstruct.customers;

import de.cronos.demo.mappingwithmapstruct.orders.OrderInfo;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Value
@Builder
public class CustomerDetails {

    UUID id;

    String email;

    String firstName;

    String lastName;

    LocalDate birthday;

    Instant created;

    Instant lastModified;

    // caution: model fits only for scenario "many customers with few orders"!
    List<OrderInfo> orders;

}
