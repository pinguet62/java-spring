package fr.pinguet62.test.springdatathrowifnotfound.config;

import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;

import javax.persistence.EntityManager;

public class ThrowIfNotFoundRepositoryFactory extends JpaRepositoryFactory {

    public ThrowIfNotFoundRepositoryFactory(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return ThrowIfNotFoundRepository.class;
    }

}
