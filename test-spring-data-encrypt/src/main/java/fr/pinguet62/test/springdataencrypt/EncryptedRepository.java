package fr.pinguet62.test.springdataencrypt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EncryptedRepository extends JpaRepository<EncryptedEntity, Integer> {
}
