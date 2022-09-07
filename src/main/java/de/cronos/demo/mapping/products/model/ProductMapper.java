package de.cronos.demo.mapping.products.model;

import de.cronos.demo.mapping.products.model.read.ProductDetails;
import de.cronos.demo.mapping.products.model.read.ProductInfo;
import org.mapstruct.Mapper;

@Mapper
public interface ProductMapper {

    /*
        ----------------------------------------------------------------------------------------------------------------
        --- Info -------------------------------------------------------------------------------------------------------
        ----------------------------------------------------------------------------------------------------------------
    */

    ProductInfo toInfo(ProductEntity domain);

    ProductDetails toDetails(ProductEntity domain);

}
