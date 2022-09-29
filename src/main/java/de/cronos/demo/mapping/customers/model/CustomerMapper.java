package de.cronos.demo.mapping.customers.model;

import de.cronos.demo.mapping.common.mapping.JavaUtilOptionalMapper;
import de.cronos.demo.mapping.customers.model.events.CreateCustomerEvent;
import de.cronos.demo.mapping.customers.model.read.CustomerDetails;
import de.cronos.demo.mapping.customers.model.read.CustomerInfo;
import de.cronos.demo.mapping.customers.model.read.CustomerRecord;
import de.cronos.demo.mapping.orders.model.OrderMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {
        JavaUtilOptionalMapper.class,
        OrderMapper.class
})
public interface CustomerMapper {

    /*
        ----------------------------------------------------------------------------------------------------------------
        --- Info -------------------------------------------------------------------------------------------------------
        ----------------------------------------------------------------------------------------------------------------
    */

    CustomerInfo toInfo(CustomerEntity domain);

    /*
        ----------------------------------------------------------------------------------------------------------------
        --- Details ----------------------------------------------------------------------------------------------------
        ----------------------------------------------------------------------------------------------------------------
    */

    @Mapping(target = "numberOfOrders", expression = "java(domain.getOrders().size())")
    CustomerDetails toDetails(CustomerEntity domain);

    /*
        ----------------------------------------------------------------------------------------------------------------
        --- Record -----------------------------------------------------------------------------------------------------
        ----------------------------------------------------------------------------------------------------------------
    */

    // Doesn't make sense - just to demonstrate MapStruct's ability to handle Java's Record datatype
    CustomerRecord toRecord(CustomerEntity domain);

    /*
        ----------------------------------------------------------------------------------------------------------------
        --- Domain -----------------------------------------------------------------------------------------------------
        ----------------------------------------------------------------------------------------------------------------
    */

    // For demo purposes: Entity creation with MapStruct qa support (compile error on accidentally forgotten fields)...
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "orders", ignore = true)
    CustomerEntity from(CreateCustomerEvent event);

}
