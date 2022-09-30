package de.cronos.demo.mapping.orders;

import de.cronos.demo.mapping.common.mapping.IdLookupMapper;
import de.cronos.demo.mapping.customers.CustomerEntity;
import de.cronos.demo.mapping.customers.CustomerMapper;
import de.cronos.demo.mapping.customers.IdToCustomer;
import de.cronos.demo.mapping.orders.events.PlaceOrderEvent;
import de.cronos.demo.mapping.orders.summary.OrderDetails;
import de.cronos.demo.mapping.orders.summary.OrderInfo;
import de.cronos.demo.mapping.products.IdToProduct;
import de.cronos.demo.mapping.products.ProductEntity;
import de.cronos.demo.mapping.products.ProductMapper;
import de.cronos.demo.mapping.shipment.ShipmentService;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Mapper(uses = {
        CustomerMapper.class,
        ProductMapper.class,
        IdLookupMapper.class
})
public abstract class OrderMapper {
    // TODO: Constructor injection with Spring DI not supported?!
    @Autowired
    protected ShipmentService shipmentService;

    /*
        ----------------------------------------------------------------------------------------------------------------
        --- Info -------------------------------------------------------------------------------------------------------
        ----------------------------------------------------------------------------------------------------------------
    */

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerEmail", source = "customer.email")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productId", source = "product.id")
    public abstract OrderInfo toInfo(OrderEntity domain);

    /*
        ----------------------------------------------------------------------------------------------------------------
        --- Details ----------------------------------------------------------------------------------------------------
        ----------------------------------------------------------------------------------------------------------------
    */

    // see https://mapstruct.org/documentation/dev/api/org/mapstruct/AfterMapping.html
    @AfterMapping
    protected void enhanceDetails(OrderEntity domain, @MappingTarget OrderDetails.OrderDetailsBuilder builder) {
        shipmentService.estimate(domain)
                .ifPresent(builder::estimatedShipment);
    }

    @Mapping(target = "estimatedShipment", ignore = true)
    public abstract OrderDetails toDetails(OrderEntity domain);

    /*
        ----------------------------------------------------------------------------------------------------------------
        --- Domain -----------------------------------------------------------------------------------------------------
        ----------------------------------------------------------------------------------------------------------------
    */

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "state", constant = "NEW")
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract OrderEntity from(CustomerEntity customer, ProductEntity product, Integer quantity);

    // Argument names are used to find target properties
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "customer", source = "customerId", qualifiedBy = {IdToCustomer.class})
    @Mapping(target = "product", source = "productId", qualifiedBy = {IdToProduct.class})
    @Mapping(target = "state", constant = "NEW")
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract OrderEntity from(UUID customerId, UUID productId, Integer quantity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    // Strategy pattern!
    @Mapping(target = "customer", source = "customerId", qualifiedBy = {IdToCustomer.class})
    @Mapping(target = "product", source = "productId", qualifiedBy = {IdToProduct.class})
    @Mapping(target = "state", constant = "NEW")
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract OrderEntity from(PlaceOrderEvent source);

}

