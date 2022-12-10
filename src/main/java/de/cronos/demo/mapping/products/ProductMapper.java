package de.cronos.demo.mapping.products;

import de.cronos.demo.mapping.common.mapping.IdLookupMapper;
import de.cronos.demo.mapping.products.events.CreateProductEvent;
import de.cronos.demo.mapping.products.summary.ProductDetails;
import de.cronos.demo.mapping.products.summary.ProductInfo;
import de.cronos.demo.mapping.tags.TagMapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(uses = {TagMapper.class})
public abstract class ProductMapper {
    @Autowired
    protected IdLookupMapper idLookupMapper;
    @Autowired
    protected TagMapper tagMapper;

    /*
        ----------------------------------------------------------------------------------------------------------------
        --- Domain -----------------------------------------------------------------------------------------------------
        ----------------------------------------------------------------------------------------------------------------
    */

    @AfterMapping
    protected void enrichDomain(CreateProductEvent event, @MappingTarget ProductEntity.ProductEntityBuilder builder) {
        final var persistentTagStream = event.getExistingTagIds().stream()
                .map(idLookupMapper::findTagById);
        final var newTagStream = event.getCreateTagEvents().stream()
                .map(tagMapper::from);
        builder.tags(
                Stream.concat(persistentTagStream, newTagStream).collect(Collectors.toSet())
        );
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract ProductEntity from(CreateProductEvent event);

    /*
        ----------------------------------------------------------------------------------------------------------------
        --- Info -------------------------------------------------------------------------------------------------------
        ----------------------------------------------------------------------------------------------------------------
    */

    public abstract ProductInfo toInfo(ProductEntity domain);

    /*
        ----------------------------------------------------------------------------------------------------------------
        --- Details ----------------------------------------------------------------------------------------------------
        ----------------------------------------------------------------------------------------------------------------
    */

    public abstract ProductDetails toDetails(ProductEntity domain);

}
