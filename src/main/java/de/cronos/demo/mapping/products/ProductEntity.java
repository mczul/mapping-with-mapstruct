package de.cronos.demo.mapping.products;

import de.cronos.demo.mapping.tags.TagEntity;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

// ugly, yep. See https://github.com/projectlombok/lombok/issues/557
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Builder
@With
@Entity
@Table(name = "products")
@EntityListeners(AuditingEntityListener.class)
public class ProductEntity {
    @Id
    @GeneratedValue
    @Column(name = "id")
    @ToString.Include
    protected UUID id;

    @Version
    @Column(name = "version")
    @ToString.Include
    protected Short version;

    @NotBlank
    @Column(name = "name")
    @ToString.Include
    protected String name;

    @OneToMany(cascade = {CascadeType.PERSIST})
    @JoinTable(
            name = "product_tags",
            joinColumns = {@JoinColumn(name = "product_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")}
    )
    protected Set<TagEntity> tags;

    @CreatedDate
    @Column(name = "created")
    protected Instant created;

    @LastModifiedDate
    @Column(name = "last_modified")
    @ToString.Include
    protected Instant lastModified;

    @Override
    public boolean equals(Object o) {
        if (id == null) {
            return false;
        }

        if (this == o) {
            return true;
        }

        if (!(o instanceof final ProductEntity that)) {
            return false;
        }

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
