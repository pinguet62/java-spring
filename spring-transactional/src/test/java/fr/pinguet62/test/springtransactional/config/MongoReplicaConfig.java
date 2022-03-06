package fr.pinguet62.test.springtransactional.config;

import com.mongodb.BasicDBList;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoDatabase;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.config.MongoCmdOptions;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.process.config.RuntimeConfig;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

public class MongoReplicaConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public MongodExecutable firstMongodExecutable(MongoProperties properties, @Qualifier("firstEmbeddedMongoConfiguration") MongodConfig mongodConfig, RuntimeConfig runtimeConfig, ApplicationContext context) {
        EmbeddedMongoAutoConfiguration embeddedMongoAutoConfiguration = new EmbeddedMongoAutoConfiguration(properties);
        mongodConfig = MongodConfig.builder().from(mongodConfig)
                .cmdOptions(MongoCmdOptions.builder().from(mongodConfig.cmdOptions())
                        .useNoJournal(false)
                        .build())
                .build();
        return embeddedMongoAutoConfiguration.embeddedMongoServer(mongodConfig, runtimeConfig, context);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public MongodExecutable secondMongodExecutable(MongoProperties properties, @Qualifier("secondEmbeddedMongoConfiguration") MongodConfig mongodConfig, RuntimeConfig runtimeConfig, ApplicationContext context) {
        EmbeddedMongoAutoConfiguration embeddedMongoAutoConfiguration = new EmbeddedMongoAutoConfiguration(properties);
        mongodConfig = MongodConfig.builder().from(mongodConfig)
                .cmdOptions(MongoCmdOptions.builder().from(mongodConfig.cmdOptions())
                        .useNoJournal(false)
                        .build())
                .build();
        return embeddedMongoAutoConfiguration.embeddedMongoServer(mongodConfig, runtimeConfig, context);
    }

    @Bean("firstEmbeddedMongoConfiguration")
    public MongodConfig firstEmbeddedMongoConfiguration(MongoProperties properties, EmbeddedMongoProperties embeddedProperties) throws IOException {
        EmbeddedMongoAutoConfiguration embeddedMongoAutoConfiguration = new EmbeddedMongoAutoConfiguration(properties);
        return embeddedMongoAutoConfiguration.embeddedMongoConfiguration(embeddedProperties);
    }

    @Bean("secondEmbeddedMongoConfiguration")
    public MongodConfig secondEmbeddedMongoConfiguration(MongoProperties properties, EmbeddedMongoProperties embeddedProperties) throws IOException {
        EmbeddedMongoAutoConfiguration embeddedMongoAutoConfiguration = new EmbeddedMongoAutoConfiguration(properties);
        return embeddedMongoAutoConfiguration.embeddedMongoConfiguration(embeddedProperties);
    }

    @Component
    static class ReplSetInitiate {
        @Autowired
        private MongoClient mongoClient;

        @Autowired
        private List<MongodConfig> mongodConfigs;

        @PostConstruct
        public void init() throws InterruptedException {
            Document config = new Document("_id", "rs0");
            BasicDBList members = new BasicDBList();
            for (int i = 0; i < mongodConfigs.size(); i++) {
                MongodConfig mongodConfig = mongodConfigs.get(i);
                members.add(new Document("_id", i).append("host", mongodConfig.net().getBindIp() + ":" + mongodConfig.net().getPort()));
            }
            config.put("members", members);

            MongoDatabase adminDatabase = mongoClient.getDatabase("admin");
            Mono.from(adminDatabase.runCommand(new Document("replSetInitiate", config))).block();

            // TODO loop with timeout
            Thread.sleep(15_000);
        }
    }
}
