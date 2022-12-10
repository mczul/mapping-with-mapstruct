package de.cronos.demo.mapping.products;

import de.cronos.demo.mapping.tags.TagEntity;
import de.cronos.demo.mapping.tags.TagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("JPA Entities: Product")
@DisplayNameGeneration(ReplaceUnderscores.class)
class ProductEntityIT {
    @Autowired
    protected ProductRepository productRepository;
    @Autowired
    protected TagRepository tagRepository;

    @Test
    @Sql("classpath:db/simple.sql")
    void save_new_product_with_new_tags_only() {
        // given
        final var detached = ProductEntity.builder()
                .id(null)
                .name("My Product")
                .version(null)
                .created(null)
                .lastModified(null)
                .tags(new HashSet<>(List.of(
                                TagEntity.builder()
                                        .id(null)
                                        .version(null)
                                        .name("My fancy first new tag")
                                        .created(null)
                                        .lastModified(null)
                                        .build(),
                                TagEntity.builder()
                                        .id(null)
                                        .version(null)
                                        .name("My fancy second new tag")
                                        .created(null)
                                        .lastModified(null)
                                        .build()
                        ))
                ).build();

        // when
        final var actual = productRepository.save(detached);

        // then
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getVersion()).isNotNull();
        assertThat(actual.getTags()).allSatisfy(tag -> {
            assertThat(tag.getId()).isNotNull();
            assertThat(tag.getVersion()).isNotNull();
        });
    }

    @Test
    @Sql("classpath:db/simple.sql")
    void save_new_product_with_new_and_persistent_tags() {
        final var managedTags = tagRepository.findAllById(List.of(
                // see SQL setup script for valid reference
                UUID.fromString("12345678-90ab-cdef-1234-567890bc0201"),
                UUID.fromString("5211492c-3c45-40a7-b33b-7777292d8830")
        ));
        final var unsavedTags = List.of(
                TagEntity.builder()
                        .id(null)
                        .version(null)
                        .name("My fancy second new tag")
                        .created(null)
                        .lastModified(null)
                        .build()
        );

        // given
        final var detached = ProductEntity.builder()
                .id(null)
                .name("My Product")
                .version(null)
                .created(null)
                .lastModified(null)
                .tags(new HashSet<>(
                        Stream.concat(
                                managedTags.stream(),
                                unsavedTags.stream()
                        ).toList()
                ))
                .build();

        // when
        final var actual = productRepository.save(detached);

        // then
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getVersion()).isNotNull();
        assertThat(actual.getTags()).hasSize(3);
        assertThat(actual.getTags()).allSatisfy(tag -> {
            assertThat(tag.getId()).isNotNull();
            assertThat(tag.getVersion()).isNotNull();
        });
    }

}
