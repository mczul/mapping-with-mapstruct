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
import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@Getter
@EqualsAndHashCode(of = {"id"})
@ToString
@Entity
@Immutable
@Table(name = "products")
public class ProductStatistics {

    @Id
    UUID id;

    @Column(name = "name")
    String name;

    @Column(name = "created")
    Instant created;

    @Column(name = "last_modified")
    Instant lastModified;

}
