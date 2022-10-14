package de.cronos.demo.mapping.customers.summary;

import java.time.LocalDate;

public record CustomerRecord(String firstName, String lastName, LocalDate birthday, Integer orders) {
}
