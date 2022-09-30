package de.cronos.demo.mapping.customers.events;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.Optional;

@Value
@Builder
public class CreateCustomerEvent {

    @NotEmpty
    @Email
    String email;

    @NotEmpty
    String firstName;

    @NotEmpty
    String lastName;

    @Past
    Optional<LocalDate> birthday;

}
