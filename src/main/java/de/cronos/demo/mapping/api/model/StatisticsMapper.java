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

    @Mapping(target = "secure", source = SOURCE_FIELD_SHOP_SECURE)
    @Mapping(target = "open", source = SOURCE_FIELD_SHOP_OPEN, qualifiedBy = {ShopBoolean.class})
    @Mapping(target = "sapWorking", source = SOURCE_FIELD_SAP_ACTIVE, qualifiedBy = {SapBoolean.class})
    @Mapping(target = "products.total", expression = "java(productRepository.count())")
    @Mapping(target = "products.quality", constant = "wow!")
    @Mapping(target = "products.lastComplainReceived", source = SOURCE_FIELD_LAST_COMPLAIN)
    @Mapping(target = "proudSince", constant = "1970-01-01T12:34:56")
    @Mapping(target = "soldOutUntil", constant = "2022-12-31T23:59:59+10:00")
    public abstract ShopStatistics basedOn(Map<String, String> source);


}
