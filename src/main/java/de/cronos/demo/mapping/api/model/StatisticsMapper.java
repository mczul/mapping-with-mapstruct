package de.cronos.demo.mapping.api.model;

import de.cronos.demo.mapping.common.mapping.SapBoolean;
import de.cronos.demo.mapping.common.mapping.ShopBoolean;
import de.cronos.demo.mapping.products.ProductRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Mapper
public abstract class StatisticsMapper {
    public static final String SOURCE_FIELD_LAST_COMPLAIN = "lastComplain";
    public static final String SOURCE_FIELD_SHOP_SECURE = "shopSecure";
    public static final String SOURCE_FIELD_SHOP_OPEN = "shopOpen";
    public static final String SOURCE_FIELD_SAP_ACTIVE = "sapActive";

    @Autowired
    protected ProductRepository productRepository;

    @ShopBoolean
    public Boolean fromShopBooleanString(String source) {
        if (source == null) {
            return null;
        }
        return "yeeeehaaa".equalsIgnoreCase(source);
    }

    @SapBoolean
    public Boolean fromSapBooleanString(String source) {
        if (source == null) {
            return null;
        }
        return "X".equalsIgnoreCase(source);
    }

    // default MapStruct conversion from String to Boolean
    @Mapping(target = "secure", source = SOURCE_FIELD_SHOP_SECURE)
    // Shop specific conversion from String to Boolean
    @Mapping(target = "open", source = SOURCE_FIELD_SHOP_OPEN, qualifiedBy = {ShopBoolean.class})
    // SAP specific conversion from String to Boolean
    @Mapping(target = "sapWorking", source = SOURCE_FIELD_SAP_ACTIVE, qualifiedBy = {SapBoolean.class})
    // Java expression as the source value
    @Mapping(target = "products.total", expression = "java(productRepository.count())")
    // Constant string to target (including automatic type conversion)
    @Mapping(target = "products.quality", constant = "wow!")
    // Map key as the source
    @Mapping(target = "products.lastComplainReceived", source = SOURCE_FIELD_LAST_COMPLAIN)
    // default MapStruct conversion from String to LocalDateTime
    @Mapping(target = "proudSince", constant = "1970-01-01T12:34:56")
    // default MapStruct conversion from OffsetDateTime as string to Instant
    @Mapping(target = "soldOutUntil", constant = "2022-12-31T23:59:59+10:00")
    public abstract ShopStatistics basedOn(Map<String, String> source);


}
