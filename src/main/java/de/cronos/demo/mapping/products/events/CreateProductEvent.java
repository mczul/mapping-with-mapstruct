package de.cronos.demo.mapping.products.events;

import de.cronos.demo.mapping.tags.events.CreateTagEvent;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.UUID;

import static javax.validation.constraints.Pattern.Flag.CASE_INSENSITIVE;

@Value
@Builder
public class CreateProductEvent {

    @NotBlank
    @Pattern(regexp = "^[a-z\\s_-]{3,40}$", flags = {CASE_INSENSITIVE})
    String name;

    @Singular
    @NotNull
    List<UUID> existingTagIds;

    @Singular
    List<CreateTagEvent> createTagEvents;

}
