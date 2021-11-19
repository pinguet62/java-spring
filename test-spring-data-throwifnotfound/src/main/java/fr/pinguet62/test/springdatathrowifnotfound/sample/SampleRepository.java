package fr.pinguet62.test.springdatathrowifnotfound.sample;

import fr.pinguet62.test.springdatathrowifnotfound.config.ThrowIfNotFound;
import fr.pinguet62.test.springdatathrowifnotfound.config.ThrowIfNotFoundJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SampleRepository extends ThrowIfNotFoundJpaRepository<SampleEntity, Integer> {

    @ThrowIfNotFound
    SampleEntity findByName(String name);

    SampleEntity findByIdOrName(Integer id, String name);
}
