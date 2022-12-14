package de.cronos.demo.mapping.customers;

import de.cronos.demo.mapping.orders.OrderEntity;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
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
@Table(name = "customers")
@EntityListeners(AuditingEntityListener.class)
public class CustomerEntity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    protected UUID id;

    @Version
    @Column(name = "version")
    protected Short version;

    @NotBlank
    @Email
    @Column(name = "email")
    protected String email;

    @NotBlank
    @Column(name = "first_name")
    protected String firstName;

    @NotBlank
    @Column(name = "last_name")
    protected String lastName;

    @Column(name = "birthday")
    protected LocalDate birthday;

    @NotNull
    @PastOrPresent
    @CreatedDate
    @Column(name = "created")
    protected Instant created;

    @NotNull
    @PastOrPresent
    @LastModifiedDate
    @Column(name = "last_modified")
    protected Instant lastModified;

    // caution: lazy by default but still a potential scaling issue
    @OneToMany(mappedBy = "customer")
    @ToString.Exclude
    protected List<OrderEntity> orders;

    @Override
    public boolean equals(Object o) {
        if (id == null) {
            return false;
        }

        if (this == o) {
            return true;
        }

        if (!(o instanceof final CustomerEntity that)) {
            return false;
        }

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


}
