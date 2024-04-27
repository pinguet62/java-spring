package fr.pinguet62.springdata.throwifnotfound.sample;

import fr.pinguet62.springdata.throwifnotfound.ThrowIfNotFoundRepositoryFactoryBean;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = ThrowIfNotFoundRepositoryFactoryBean.class)
public class SampleApplication {
}
