package de.cronos.demo.mapping.products;

import de.cronos.demo.mapping.products.statistics.ProductStatisticsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Products: JPA & Repository")
@DisplayNameGeneration(ReplaceUnderscores.class)
class ProductStatisticsRepositoryIT {
    @Autowired
    protected ProductRepository productRepository;
    @Autowired
    protected ProductStatisticsRepository underTest;

    @Test
    @Sql("classpath:db/simple.sql")
    void findAll_should_return_expected() {
        // given
        final var expected = productRepository.findAll().stream()
                .map(ProductEntity::getId)
                .toList();
        assertThat(expected).isNotEmpty();

        // when
        final var actual = underTest.findAll(Pageable.unpaged());

        // then
        assertThat(actual).hasSameSizeAs(expected);
        assertThat(actual.getContent()).allSatisfy(statistics -> {
            assertThat(statistics.getId()).isIn(expected);
            assertThat(statistics.getName()).isNotBlank();
            assertThat(statistics.getLastModified()).isNotNull();
        });
    }

}
