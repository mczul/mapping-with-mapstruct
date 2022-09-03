package de.cronos.demo.mappingwithmapstruct.customers.events;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Value
@Builder
public class UpdateCustomerEvent {

    @NotNull
    UUID customerId;

    Optional<String> email;

    Optional<String> firstName;

    Optional<String> lastName;

    Optional<LocalDate> birthday;

}
