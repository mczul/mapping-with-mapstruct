package de.cronos.demo.mapping.customers.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.cronos.demo.mapping.orders.OrderState;

import java.time.Instant;
import java.time.LocalDate;

public interface CustomerStatistics {

    // No ID attribute due to tricky and unintended conversion aspect for this low level and lightweight projection type

    String getEmail();

    String getFirstName();

    // JSON mapping is still customizable
    @JsonProperty("surname")
    String getLastName();

    LocalDate getBirthday();

    Instant getLastOrderPlaced();

    String getLastProductOrdered();

    Integer getLastQuantityOrdered();

    OrderState getLastOrderState();

}
