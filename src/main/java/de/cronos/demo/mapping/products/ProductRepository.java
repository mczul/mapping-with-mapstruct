package de.cronos.demo.mapping.products;

import de.cronos.demo.mapping.products.model.ProductEntity;
import de.cronos.demo.mapping.products.model.read.ProductRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {

    @Query("""
                SELECT new de.cronos.demo.mapping.products.model.read.ProductRecord(p.id, p.name)
                FROM ProductEntity p
            """)
    Page<ProductRecord> loadProductRecords(Pageable pageable);

}
