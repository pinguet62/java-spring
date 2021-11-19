package fr.pinguet62.springboot.embeddedelasticsearch.sample;

import fr.pinguet62.springboot.embeddedelasticsearch.EmbeddedElasticsearch;
import fr.pinguet62.springboot.embeddedelasticsearch.EmbeddedElasticsearchIndex;
import fr.pinguet62.springboot.embeddedelasticsearch.EmbeddedElasticsearchType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest(classes = SampleConfig.class, properties = "spring.data.elasticsearch.cluster-nodes = localhost:9300")
@EmbeddedElasticsearch(version = "6.8.13", startTimeout = 30_000, indexes = {
        @EmbeddedElasticsearchIndex(name = "cars", types = {
                @EmbeddedElasticsearchType(name = "car", definition = "classpath:cars-mapping.json"),
        }),
        @EmbeddedElasticsearchIndex(name = "paper_books", types = {
                @EmbeddedElasticsearchType(name = "paper_book", definition = "classpath:paper-book-mapping.json"),
        }),
})
class SampleTest {

    @Autowired
    PaperBookRepository repository;

    @Test
    void test() {
        repository.save(new PaperBookDocument(null, "author", "title", "description"));

        Iterable<PaperBookDocument> result = repository.findAll();
        List<PaperBookDocument> items = stream(result.spliterator(), false).collect(toList());
        assertThat(items, hasSize(1));
    }
}
