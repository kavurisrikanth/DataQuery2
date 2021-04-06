package main;

import d3e.core.D3ELocalResourceHandler;
import d3e.core.D3EResourceHandler;
import d3e.core.GraphQLFilter;
import d3e.core.TransactionWrapper;
import gqltosql.GqlToSql;
import gqltosql.schema.IModelSchema;
import javax.persistence.EntityManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@SpringBootApplication
@EnableJpaAuditing
@EnableTransactionManagement
@EnableScheduling
@ComponentScan({
  "DataQuery2",
  "classes",
  "graphql",
  "helpers",
  "models",
  "repository",
  "security",
  "storage",
  "d3e.core",
  "store",
  "parser",
  "rest",
  "lists"
})
@EntityScan({
  "DataQuery2",
  "classes",
  "graphql",
  "helpers",
  "models",
  "repository",
  "security",
  "storage",
  "d3e.core",
  "org.meta"
})
@EnableJpaRepositories("repository.jpa")
@EnableSolrRepositories("repository.solr")
public class DataQuery2 {
  public static void main(String[] args) {
    SpringApplication.run(DataQuery2.class, args);
  }

  @Bean
  public FilterRegistrationBean<GraphQLFilter> graphQLFilter(TransactionWrapper wrapper) {
    FilterRegistrationBean<GraphQLFilter> registrationBean =
        new FilterRegistrationBean<GraphQLFilter>();
    registrationBean.setFilter(new GraphQLFilter(wrapper));
    registrationBean.addUrlPatterns("/graphql");
    return registrationBean;
  }

  @Bean
  public ServletServerContainerFactoryBean createServletServerContainerFactoryBean() {
    ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
    container.setMaxTextMessageBufferSize(327680);
    container.setMaxBinaryMessageBufferSize(327680);
    return container;
  }

  @Bean
  @Primary
  public D3EResourceHandler getResourceHandler() {
    return new D3ELocalResourceHandler();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public GqlToSql gqlToSql(EntityManager em, IModelSchema schema) {
    return new GqlToSql(em, schema);
  }
}
