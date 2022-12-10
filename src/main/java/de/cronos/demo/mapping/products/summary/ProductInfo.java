package de.cronos.demo.mapping.products.summary;

import de.cronos.demo.mapping.tags.summary.TagInfo;
import lombok.Builder;
import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
@Builder
public class ProductInfo {

    UUID id;

    String name;

    Set<TagInfo> tags;

}
