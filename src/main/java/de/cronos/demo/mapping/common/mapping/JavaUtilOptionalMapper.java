package de.cronos.demo.mapping.common.mapping;

import org.mapstruct.Mapper;

import java.util.Optional;

@Mapper
public abstract class JavaUtilOptionalMapper {

    public <T> T extract(Optional<T> source) {
        return source.orElse(null);
    }

    public <T> Optional<T> wrap(T source) {
        return Optional.ofNullable(source);
    }

}
