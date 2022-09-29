package de.cronos.demo.mapping.customers.model.read;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.cronos.demo.mapping.orders.model.OrderState;

import java.time.Instant;
import java.time.LocalDate;

public interface CustomerStatistics {

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
