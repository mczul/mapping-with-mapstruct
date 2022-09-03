package de.cronos.demo.mappingwithmapstruct.customers.events;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import java.time.LocalDate;

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
    LocalDate birthday;

}
