package de.cronos.demo.mappingwithmapstruct.customers;

import de.cronos.demo.mappingwithmapstruct.orders.OrderEntity;
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
import java.util.Objects;
import java.util.UUID;

// ugly, yep. See https://github.com/projectlombok/lombok/issues/557
@NoArgsConstructor
@AllArgsConstructor
@Builder
@With
@Getter
@Setter
@ToString
@Entity
@Table(name = "customer")
@EntityListeners(AuditingEntityListener.class)
public class CustomerEntity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    protected UUID id;

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
    protected List<OrderEntity> orders;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomerEntity)) return false;

        CustomerEntity that = (CustomerEntity) o;

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
