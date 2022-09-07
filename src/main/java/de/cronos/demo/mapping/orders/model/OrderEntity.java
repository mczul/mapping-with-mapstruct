package de.cronos.demo.mapping.orders.model;

import de.cronos.demo.mapping.customers.model.CustomerEntity;
import de.cronos.demo.mapping.products.model.ProductEntity;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

// ugly, yep. See https://github.com/projectlombok/lombok/issues/557
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(of = {"id"})
@Builder
@With
@Entity
@Table(name = "customer_orders")
@EntityListeners(AuditingEntityListener.class)
public class OrderEntity {
    @Id
    @GeneratedValue
    @Column(name = "id")
    protected UUID id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "customer_id")
    protected CustomerEntity customer;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "product_id")
    protected ProductEntity product;

    @NotNull
    @Min(1)
    @Column(name = "quantity")
    protected Integer quantity;

    @NotNull
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    protected OrderState state;

    @CreatedDate
    @Column(name = "created")
    protected Instant created;

    @LastModifiedDate
    @Column(name = "last_modified")
    protected Instant lastModified;

}
