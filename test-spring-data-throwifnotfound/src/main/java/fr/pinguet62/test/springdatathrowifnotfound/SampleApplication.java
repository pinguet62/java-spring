package fr.pinguet62.test.springdatathrowifnotfound;

import fr.pinguet62.test.springdatathrowifnotfound.config.ThrowIfNotFoundRepositoryFactoryBean;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = ThrowIfNotFoundRepositoryFactoryBean.class)
public class SampleApplication {
}
