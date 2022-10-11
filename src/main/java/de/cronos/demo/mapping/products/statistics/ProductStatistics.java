package de.cronos.demo.mapping.products.statistics;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Value
@EqualsAndHashCode(of = {"id"})
@ToString
@Immutable
@Entity
@Table(name = "product_statistics")
public class ProductStatistics {

    @Embeddable
    @Value
    public static class Orders {

        @Column(name = "last_successful_order")
        Instant lastSuccessful = null;

        @Column(name = "average_order_quantity")
        BigDecimal averageQuantity = null;

        @Column(name = "successful_orders")
        Long successful = null;

    }

    @Id
    @Column(name = "id")
    UUID id = null;

    @Column(name = "name")
    String name = null;

    @Column(name = "created")
    Instant created = null;

    @Column(name = "last_modified")
    Instant lastModified = null;

    @Embedded
    Orders orders = null;

}
