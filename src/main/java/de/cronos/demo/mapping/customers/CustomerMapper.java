package de.cronos.demo.mapping.customers;

import de.cronos.demo.mapping.common.mapping.JavaUtilOptionalMapper;
import de.cronos.demo.mapping.customers.events.CreateCustomerEvent;
import de.cronos.demo.mapping.customers.summary.CustomerDetails;
import de.cronos.demo.mapping.customers.summary.CustomerInfo;
import de.cronos.demo.mapping.customers.summary.CustomerRecord;
import de.cronos.demo.mapping.orders.OrderMapper;
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
