package de.cronos.demo.mapping.customers.model.read;

import java.time.LocalDate;

public record CustomerRecord(String firstName, String lastName, LocalDate birthday) {
}
