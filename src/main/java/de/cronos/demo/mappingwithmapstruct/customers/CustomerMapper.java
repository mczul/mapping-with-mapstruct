package de.cronos.demo.mappingwithmapstruct.customers;

import de.cronos.demo.mappingwithmapstruct.customers.CustomerInfo.CustomerInfoBuilder;
import de.cronos.demo.mappingwithmapstruct.customers.events.CreateCustomerEvent;
import de.cronos.demo.mappingwithmapstruct.orders.OrderMapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(uses = {
        OrderMapper.class
})
public interface CustomerMapper {

    // see https://mapstruct.org/documentation/dev/api/org/mapstruct/AfterMapping.html
    @AfterMapping
    default void enhanceInfo(CustomerEntity domain, @MappingTarget CustomerInfoBuilder builder) {
        // this simple PoC could easily be implemented using MapStruct expressions...
        // but @AfterMapping enables complex solutions
        builder.numberOfOrders(domain.orders.size()).build();
    }

    @Mapping(target = "numberOfOrders", ignore = true)
    CustomerInfo toInfo(CustomerEntity domain);

    CustomerDetails toDetails(CustomerEntity domain);

    // For demo purposes: Entity creation with MapStruct qa support (compile error on accidentally forgotten fields)...
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "orders", ignore = true)
    CustomerEntity from(CreateCustomerEvent event);

}
