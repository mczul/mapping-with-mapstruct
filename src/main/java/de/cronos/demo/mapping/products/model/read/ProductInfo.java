package de.cronos.demo.mapping.products.model.read;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class ProductInfo {

    UUID id;

    String name;

}
