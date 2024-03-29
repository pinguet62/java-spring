package fr.pinguet62.springboot.embeddedelasticsearch.sample;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends ElasticsearchRepository<CarDocument, String> {
}
