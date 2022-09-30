package de.cronos.demo.mapping.products;

import de.cronos.demo.mapping.common.data.ReadonlyRepository;
import de.cronos.demo.mapping.products.model.read.ProductStatistics;

import java.util.UUID;

public interface ProductStatisticsRepository extends ReadonlyRepository<ProductStatistics, UUID> {

}
