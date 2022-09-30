package de.cronos.demo.mapping.customers.summary;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.UUID;

@Value
@Builder
public class CustomerInfo {

    UUID id;

    String email;

    String firstName;

    String lastName;

    LocalDate birthday;

}
