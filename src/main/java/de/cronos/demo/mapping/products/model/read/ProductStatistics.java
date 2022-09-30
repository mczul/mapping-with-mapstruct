package de.cronos.demo.mapping.products.model.read;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@Getter
@EqualsAndHashCode(of = {"id"})
@ToString
@Entity
@Immutable
@Table(name = "product_statistics")
public class ProductStatistics {

    @Id
    @Column(name = "id")
    UUID id;

    @Column(name = "name")
    String name;

    @Column(name = "created")
    Instant created;

    @Column(name = "last_modified")
    Instant lastModified;

    @Column(name = "last_successful_order")
    Instant lastSuccessfulOrder;

    @Column(name = "average_order_quantity")
    BigDecimal averageOrderQuantity;

    @Column(name = "successful_orders")
    Long successfulOrders;

}
