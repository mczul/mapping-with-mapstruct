package de.cronos.demo.mapping.tags.events;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
@Builder
public class CreateTagEvent {

    @NotBlank
    String name;

}
