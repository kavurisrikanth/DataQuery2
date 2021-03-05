package graphql.input;

public class AnonymousUserEntityInput extends UserEntityInput {
  public String _type() {
    return "AnonymousUser";
  }
}
