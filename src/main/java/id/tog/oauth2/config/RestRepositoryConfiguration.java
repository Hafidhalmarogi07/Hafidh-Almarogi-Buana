package id.tog.oauth2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.context.WebApplicationContext;

@Configuration
public class RestRepositoryConfiguration implements RepositoryRestConfigurer {
    @Autowired
    private WebApplicationContext context;

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        Repositories repositories = new Repositories(context);

        for (Class<?> entity: repositories) {
            config.exposeIdsFor(entity);
        }
    }
}
