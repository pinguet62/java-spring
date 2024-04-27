package fr.pinguet62.springdata.throwifnotfound;

import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class ThrowIfNotFoundRepositoryFactoryBean<T extends Repository<S, ID>, S, ID> extends JpaRepositoryFactoryBean<T, S, ID> {

    public ThrowIfNotFoundRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        RepositoryFactorySupport factory = new ThrowIfNotFoundRepositoryFactory(entityManager);
        factory.addRepositoryProxyPostProcessor(new ThrowIfNotFoundRepositoryProxyPostProcessor());
        return factory;
    }
}
