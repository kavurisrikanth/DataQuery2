package graphql.events;

public class ChangeEvent<T> {
  public T model;
  public ChangeEventType changeType;
}
