package de.cronos.demo.mapping.tags;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@With
@Entity
@Table(name = "tags")
@EntityListeners(AuditingEntityListener.class)
public class TagEntity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    protected UUID id;

    @Version
    @Column(name = "version")
    protected Short version;

    @NotBlank
    @Column(name = "name")
    protected String name;

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

        if (!(o instanceof final TagEntity that)) {
            return false;
        }

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
