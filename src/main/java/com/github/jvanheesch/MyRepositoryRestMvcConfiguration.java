package com.github.jvanheesch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.RepositoryResourceMappings;
import org.springframework.data.rest.webmvc.BasePathAwareHandlerMapping;
import org.springframework.data.rest.webmvc.RepositoryRestHandlerMapping;
import org.springframework.data.rest.webmvc.support.DelegatingHandlerMapping;
import org.springframework.data.rest.webmvc.support.JpaHelper;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.HandlerMapping;

import java.util.*;

@Configuration
public class MyRepositoryRestMvcConfiguration {
    @Autowired
    ApplicationContext applicationContext;

    @Bean
    public DelegatingHandlerMapping myRestHandlerMapping(
            RepositoryRestConfiguration repositoryRestConfiguration,
            RepositoryResourceMappings resourceMappings,
            Repositories repositories,
            JpaHelper jpaHelper
    ) {
        Map<String, CorsConfiguration> corsConfigurations = repositoryRestConfiguration.getCorsRegistry()
                .getCorsConfigurations();

        RepositoryRestHandlerMapping repositoryMapping = new RepositoryRestHandlerMapping(resourceMappings,
                repositoryRestConfiguration, repositories);
        repositoryMapping.setJpaHelper(jpaHelper);
        repositoryMapping.setApplicationContext(applicationContext);
        repositoryMapping.setCorsConfigurations(corsConfigurations);
        repositoryMapping.afterPropertiesSet();

        BasePathAwareHandlerMapping basePathMapping = new BasePathAwareHandlerMapping(repositoryRestConfiguration) {
            @Override
            protected void extendInterceptors(List<Object> interceptors) {
                super.extendInterceptors(interceptors);

                Optional.of(jpaHelper)
                        .map(JpaHelper::getInterceptors) //
                        .orElseGet(() -> Collections.emptyList()) //
                        .forEach(interceptors::add);
            }
        };
        basePathMapping.setApplicationContext(applicationContext);
        basePathMapping.setCorsConfigurations(corsConfigurations);
        basePathMapping.afterPropertiesSet();

        List<HandlerMapping> mappings = new ArrayList<HandlerMapping>();
        mappings.add(basePathMapping);
        mappings.add(repositoryMapping);

        return new DelegatingHandlerMapping(mappings) {
            @Override
            public int getOrder() {
                return Math.subtractExact(super.getOrder(), 1);
            }
        };
    }
}
