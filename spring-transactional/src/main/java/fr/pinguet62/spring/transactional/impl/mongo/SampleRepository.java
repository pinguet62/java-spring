package fr.pinguet62.spring.transactional.impl.mongo;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SampleRepository extends ReactiveMongoRepository<SampleDocument, ObjectId> {
}
