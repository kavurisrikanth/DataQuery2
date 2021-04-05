package main;

import com.coxautodev.graphql.tools.PerFieldObjectMapperProvider;
import com.coxautodev.graphql.tools.SchemaParserDictionary;
import com.coxautodev.graphql.tools.SchemaParserOptions;
import com.coxautodev.graphql.tools.SchemaParserOptions.Builder;
import d3e.core.D3ELocalResourceHandler;
import d3e.core.D3EResourceHandler;
import d3e.core.GraphQLFilter;
import d3e.core.TransactionWrapper;
import gqltosql.GqlToSql;
import gqltosql.schema.IModelSchema;
import graphql.D3EGraphQLExceptionHandler;
import graphql.events.AnonymousUserChangeEvent;
import graphql.events.OneTimePasswordChangeEvent;
import graphql.events.ReportChangeEvent;
import graphql.events.StudentChangeEvent;
import graphql.events.UserChangeEvent;
import graphql.events.UserSessionChangeEvent;
import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.ExecutionStrategy;
import graphql.input.AnonymousUserEntityInput;
import graphql.input.EmailMessageEntityInput;
import graphql.input.SMSMessageEntityInput;
import javax.persistence.EntityManager;
import models.AnonymousUser;
import models.Avatar;
import models.D3EImage;
import models.EmailMessage;
import models.OneTimePassword;
import models.Report;
import models.ReportConfig;
import models.ReportConfigOption;
import models.SMSMessage;
import models.Student;
import models.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
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
  public SchemaParserOptions getSchemaParserOptions(
      @Autowired(required = false) PerFieldObjectMapperProvider perFieldObjectMapperProvider) {
    Builder builder = SchemaParserOptions.newOptions();
    if (perFieldObjectMapperProvider != null) {
      builder.objectMapperProvider(perFieldObjectMapperProvider);
    }
    return builder.preferGraphQLResolver(true).build();
  }

  @Bean
  public SchemaParserDictionary getSchemaParser() {
    SchemaParserDictionary dictionary = new SchemaParserDictionary();
    dictionary.add("AnonymousUserChangeEvent", AnonymousUserChangeEvent.class);
    dictionary.add("OneTimePasswordChangeEvent", OneTimePasswordChangeEvent.class);
    dictionary.add("ReportChangeEvent", ReportChangeEvent.class);
    dictionary.add("StudentChangeEvent", StudentChangeEvent.class);
    dictionary.add("UserChangeEvent", UserChangeEvent.class);
    dictionary.add("UserSessionChangeEvent", UserSessionChangeEvent.class);
    dictionary.add("AnonymousUser", AnonymousUser.class);
    dictionary.add("Avatar", Avatar.class);
    dictionary.add("D3EImage", D3EImage.class);
    dictionary.add("EmailMessage", EmailMessage.class);
    dictionary.add("OneTimePassword", OneTimePassword.class);
    dictionary.add("Report", Report.class);
    dictionary.add("ReportConfig", ReportConfig.class);
    dictionary.add("ReportConfigOption", ReportConfigOption.class);
    dictionary.add("SMSMessage", SMSMessage.class);
    dictionary.add("Student", Student.class);
    dictionary.add("UserSession", UserSession.class);
    dictionary.add("AnonymousUserEntityInput", AnonymousUserEntityInput.class);
    dictionary.add("EmailMessageEntityInput", EmailMessageEntityInput.class);
    dictionary.add("SMSMessageEntityInput", SMSMessageEntityInput.class);
    return dictionary;
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
  public ExecutionStrategy getExecutionStrategy() {
    return new AsyncExecutionStrategy(new D3EGraphQLExceptionHandler());
  }

  @Bean
  public GqlToSql gqlToSql(EntityManager em, IModelSchema schema) {
    return new GqlToSql(em, schema);
  }
}
