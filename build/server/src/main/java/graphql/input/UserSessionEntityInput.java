package graphql.input;

public abstract class UserSessionEntityInput implements store.IEntityInput {
  private long id;
  public String userSessionId;

  public String _type() {
    return "UserSession";
  }

  public long getId() {
    return this.id;
  }

  public void setId(long id) {
    this.id = id;
  }
}
