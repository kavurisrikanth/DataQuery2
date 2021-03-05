package graphql.input;

public class UserSessionEntityInputUnionX implements store.IEntityInput {
  public String type;

  public String _type() {
    return type;
  }

  public long getId() {
    return 0l;
  }

  public UserSessionEntityInput getValue() {
    switch (type) {
      default:
        {
          return null;
        }
    }
  }
}
