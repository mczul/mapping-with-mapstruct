package de.cronos.demo.mapping.customers.summary;

import java.time.LocalDate;
import java.util.UUID;

public record CustomerRecord(UUID id, String firstName, String lastName, LocalDate birthday, Integer numberOfOrders) {
}
