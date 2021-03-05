package graphql.input;

public class UserEntityInputUnionX implements store.IEntityInput {
  public String type;
  public AnonymousUserEntityInput valueAnonymousUser;

  public String _type() {
    return type;
  }

  public long getId() {
    return 0l;
  }

  public UserEntityInput getValue() {
    switch (type) {
      case "AnonymousUser":
        {
          return this.valueAnonymousUser;
        }
      default:
        {
          return null;
        }
    }
  }
}
