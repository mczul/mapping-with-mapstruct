package de.cronos.demo.mapping.tags;

import de.cronos.demo.mapping.tags.events.CreateTagEvent;
import de.cronos.demo.mapping.tags.summary.TagInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TagMapper {

    /*
        ----------------------------------------------------------------------------------------------------------------
        --- Info -------------------------------------------------------------------------------------------------------
        ----------------------------------------------------------------------------------------------------------------
    */

    TagInfo toInfo(TagEntity domain);

    /*
        ----------------------------------------------------------------------------------------------------------------
        --- Domain -----------------------------------------------------------------------------------------------------
        ----------------------------------------------------------------------------------------------------------------
    */

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    TagEntity from(CreateTagEvent event);

}
