package de.cronos.demo.mapping.common.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.util.Optional;

@NoRepositoryBean
public interface ReadonlyRepository<T, ID> extends Repository<T, ID> {

    Page<T> findAll(Pageable pageable);

    Optional<T> findById(ID id);

}
