package de.cronos.demo.mappingwithmapstruct.orders;

import de.cronos.demo.mappingwithmapstruct.customers.CustomerEntity;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

// ugly, yep. See https://github.com/projectlombok/lombok/issues/557
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Entity
@Table(name = "customer_order")
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

    @NotBlank
    @Column(name = "article_name")
    protected String articleName;

    @NotNull
    @Min(1)
    @Column(name = "article_quantity")
    protected Integer articleQuantity;

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
        if (this == o) return true;
        if (!(o instanceof OrderEntity)) return false;

        OrderEntity that = (OrderEntity) o;

        return getId() != null & Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        if (getId() == null) {
            return getClass().hashCode();
        }
        return getId().hashCode();
    }
}
