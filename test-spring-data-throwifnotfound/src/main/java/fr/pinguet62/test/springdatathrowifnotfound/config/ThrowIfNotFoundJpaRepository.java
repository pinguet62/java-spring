package fr.pinguet62.test.springdatathrowifnotfound.config;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ThrowIfNotFoundJpaRepository<T, ID> extends JpaRepository<T, ID> {

    T findByIdOrThrow(ID id);

}
