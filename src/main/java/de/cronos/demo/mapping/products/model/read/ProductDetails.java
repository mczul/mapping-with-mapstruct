package de.cronos.demo.mapping.products.model.read;

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
