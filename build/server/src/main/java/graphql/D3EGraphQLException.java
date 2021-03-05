package graphql;

import java.util.List;
import java.util.Map;

import graphql.language.SourceLocation;

@SuppressWarnings("serial")
public class D3EGraphQLException implements GraphQLError {
  private Throwable exception;

  public D3EGraphQLException(Throwable exception) {
    this.exception = exception;
  }

  @Override
  public Map<String, Object> getExtensions() {
    return null;
  }

  public Throwable getException() {
    return null;
  }

  @Override
  public List<SourceLocation> getLocations() {
    return null;
  }

  @Override
  public ErrorClassification getErrorType() {
    return null;
  }

  @Override
  public List<Object> getPath() {
    return null;
  }

  @Override
  public String getMessage() {
    return this.exception == null ? "Something went wrong with your GraphQL request." : this.exception.getMessage();
  }
}
