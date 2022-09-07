package de.cronos.demo.mapping.common.mapping;

import org.mapstruct.Mapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import static de.cronos.demo.mapping.common.AppConstants.DEFAULT_ZONE;

@Mapper
public interface JavaUtilTimeMapper {

    /*
        ----------------------------------------------------------------------------------------------------------------
        --- LocalDateTime ----------------------------------------------------------------------------------------------
        ----------------------------------------------------------------------------------------------------------------
    */


    /**
     * Does what most developers might expect when {@link OffsetDateTime#toLocalDateTime()}
     * (as it could use {@link ZoneId#systemDefault()} internally)
     */
    default LocalDateTime toLocalDateTime(OffsetDateTime source) {
        if (source == null) {
            return null;
        }

        return source.toLocalDateTime()
                .minusSeconds(source.getOffset().getTotalSeconds()) // UTC
                .plusSeconds(DEFAULT_ZONE.getRules().getOffset(source.toInstant()).getTotalSeconds()); // local date & time at DEFAULT_ZONE
    }

    default LocalDateTime toLocalDateTime(Instant source) {
        if (source == null) {
            return null;
        }

        return source.atZone(DEFAULT_ZONE).toLocalDateTime();
    }

    /*
        ----------------------------------------------------------------------------------------------------------------
        --- OffsetDateTime ---------------------------------------------------------------------------------------------
        ----------------------------------------------------------------------------------------------------------------
    */


    default OffsetDateTime toOffsetDateTime(LocalDateTime source) {
        return source.atZone(DEFAULT_ZONE).toOffsetDateTime();
    }

    default OffsetDateTime toOffsetDateTime(Instant source) {
        return source.atZone(DEFAULT_ZONE).toOffsetDateTime();
    }

}
