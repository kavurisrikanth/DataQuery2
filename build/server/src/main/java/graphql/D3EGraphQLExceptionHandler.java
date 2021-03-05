package graphql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;

@Component
public class D3EGraphQLExceptionHandler implements DataFetcherExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(D3EGraphQLExceptionHandler.class);

  @Override
  public DataFetcherExceptionHandlerResult onException(DataFetcherExceptionHandlerParameters handlerParameters) {
    Throwable exception = handlerParameters.getException();
    D3EGraphQLException error = new D3EGraphQLException(exception);
    log.warn(error.getMessage(), exception);

    return DataFetcherExceptionHandlerResult.newResult().error(error).build();
  }
}
