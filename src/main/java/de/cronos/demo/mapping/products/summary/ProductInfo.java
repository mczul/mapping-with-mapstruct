package de.cronos.demo.mapping.products.summary;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class ProductInfo {

    UUID id;

    String name;

}
