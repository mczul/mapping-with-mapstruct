package de.cronos.demo.mapping.tags.summary;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class TagInfo {

    UUID id;

    String name;

}
