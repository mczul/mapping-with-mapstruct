package de.cronos.demo.mapping.products;

import de.cronos.demo.mapping.products.model.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
}