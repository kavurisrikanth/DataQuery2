package graphql.input;

public abstract class UserEntityInput implements store.IEntityInput {
  private long id;
  public boolean isActive;

  public String _type() {
    return "User";
  }

  public long getId() {
    return this.id;
  }

  public void setId(long id) {
    this.id = id;
  }
}
