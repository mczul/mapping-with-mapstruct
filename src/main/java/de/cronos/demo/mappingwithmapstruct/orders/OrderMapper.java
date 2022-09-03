package de.cronos.demo.mappingwithmapstruct.orders;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface OrderMapper {

    @Mapping(target = "customerId", source = "customer.id")
    OrderInfo toInfo(OrderEntity domain);

    @Mapping(target = "customerId", source = "customer.id")
    OrderDetails toDetails(OrderEntity domain);

}
