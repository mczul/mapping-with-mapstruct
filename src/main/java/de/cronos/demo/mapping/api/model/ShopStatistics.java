package de.cronos.demo.mapping.api.model;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.time.LocalDateTime;

@Value
@Builder
public class ShopStatistics {

    @Value
    @Builder
    public static class ProductStatistics {

        Long total;

        String quality;

        String lastComplainReceived;

    }

    ProductStatistics products;

    Boolean secure;

    Boolean open;

    Boolean sapWorking;

    LocalDateTime proudSince;

    Instant soldOutUntil;

}
