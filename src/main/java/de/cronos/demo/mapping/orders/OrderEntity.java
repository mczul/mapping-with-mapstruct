package de.cronos.demo.mapping.orders;

import de.cronos.demo.mapping.customers.CustomerEntity;
import de.cronos.demo.mapping.products.ProductEntity;
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
@Getter
@Setter
@ToString
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

    @Version
    @Column(name = "version")
    protected Short version;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY) // no positive effects of EAGER initialized references
    @JoinColumn(name = "customer_id")
    @ToString.Exclude
    protected CustomerEntity customer;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY) // no positive effects of EAGER initialized references
    @JoinColumn(name = "product_id")
    @ToString.Exclude
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

    @Override
    public boolean equals(Object o) {
        if (id == null) {
            return false;
        }

        if (this == o) {
            return true;
        }

        if (!(o instanceof final OrderEntity that)) {
            return false;
        }

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
