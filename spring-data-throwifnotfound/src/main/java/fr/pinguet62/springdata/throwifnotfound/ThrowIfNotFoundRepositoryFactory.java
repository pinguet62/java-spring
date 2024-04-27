package fr.pinguet62.springdata.throwifnotfound;

import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;

public class ThrowIfNotFoundRepositoryFactory extends JpaRepositoryFactory {

    public ThrowIfNotFoundRepositoryFactory(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return ThrowIfNotFoundRepository.class;
    }
}
