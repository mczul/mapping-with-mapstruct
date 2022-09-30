package de.cronos.demo.mapping.products.summary;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
@Builder
public class ProductDetails {

    UUID id;

    String name;

    Instant created;

    Instant lastModified;

}
