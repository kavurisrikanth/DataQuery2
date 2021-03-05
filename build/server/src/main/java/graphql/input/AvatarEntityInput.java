package graphql.input;

public class AvatarEntityInput implements store.IEntityInput {
  private long id;
  public D3EImageEntityInput image;
  public String createFrom;

  public String _type() {
    return "Avatar";
  }

  public long getId() {
    return this.id;
  }

  public void setId(long id) {
    this.id = id;
  }
}
