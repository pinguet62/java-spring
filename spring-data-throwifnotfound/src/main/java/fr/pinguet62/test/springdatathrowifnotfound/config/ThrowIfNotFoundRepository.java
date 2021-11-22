package fr.pinguet62.test.springdatathrowifnotfound.config;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;

public class ThrowIfNotFoundRepository<T, ID> extends SimpleJpaRepository<T, ID> implements ThrowIfNotFoundJpaRepository<T, ID> {

    public ThrowIfNotFoundRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }

    @Override
    public T findByIdOrThrow(ID id) {
        return super.findById(id).orElseThrow(() -> new NotFoundException());
    }
}
