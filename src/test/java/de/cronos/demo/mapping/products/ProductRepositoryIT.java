package de.cronos.demo.mapping.products;

import de.cronos.demo.mapping.products.summary.ProductRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Products: JPA & Repository")
@DisplayNameGeneration(ReplaceUnderscores.class)
class ProductRepositoryIT {
    @Autowired
    protected ProductRepository underTest;

    @Nested
    @DisplayName("queries")
    class Queries {

        @Test
        @Sql("classpath:db/simple.sql")
        void loadProductRecords_should_return_expected() {
            // given
            final var expected = underTest.findAll().stream()
                    .map(product -> new ProductRecord(product.getId(), product.getName()))
                    .toList();
            assertThat(expected).isNotEmpty();

            // when
            final var actual = underTest.loadProductRecords(Pageable.unpaged());

            // then
            assertThat(actual.getContent()).containsExactlyElementsOf(expected);
        }


    }

}
