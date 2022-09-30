package de.cronos.demo.mapping.common.mapping;

import de.cronos.demo.mapping.customers.CustomerRepository;
import de.cronos.demo.mapping.customers.model.CustomerEntity;
import de.cronos.demo.mapping.customers.model.IdToCustomer;
import de.cronos.demo.mapping.products.IdToProduct;
import de.cronos.demo.mapping.products.ProductEntity;
import de.cronos.demo.mapping.products.ProductRepository;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Mapper
public abstract class IdLookupMapper {
    @Autowired
    protected CustomerRepository customerRepository;

    @Autowired
    protected ProductRepository productRepository;

    @IdToCustomer
    public CustomerEntity toCustomer(UUID id) {
        if (id == null) {
            return null;
        }
        return customerRepository.findById(id).orElseThrow();
    }

    @IdToProduct
    public ProductEntity findProductById(UUID id) {
        if (id == null) {
            return null;
        }
        return productRepository.findById(id).orElseThrow();
    }

}
