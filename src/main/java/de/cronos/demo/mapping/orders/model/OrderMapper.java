package de.cronos.demo.mapping.orders.model;

import de.cronos.demo.mapping.customers.model.CustomerMapper;
import de.cronos.demo.mapping.orders.model.read.OrderDetails;
import de.cronos.demo.mapping.orders.model.read.OrderInfo;
import de.cronos.demo.mapping.products.model.ProductMapper;
import de.cronos.demo.mapping.shipment.ShipmentService;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(uses = {
        CustomerMapper.class,
        ProductMapper.class
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

}

