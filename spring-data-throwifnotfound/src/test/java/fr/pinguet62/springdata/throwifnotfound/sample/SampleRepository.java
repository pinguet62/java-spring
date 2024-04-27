package fr.pinguet62.springdata.throwifnotfound.sample;

import fr.pinguet62.springdata.throwifnotfound.ThrowIfNotFound;
import fr.pinguet62.springdata.throwifnotfound.ThrowIfNotFoundJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SampleRepository extends ThrowIfNotFoundJpaRepository<SampleEntity, Integer> {

    @ThrowIfNotFound
    SampleEntity findByName(String name);

    SampleEntity findByIdOrName(Integer id, String name);
}
