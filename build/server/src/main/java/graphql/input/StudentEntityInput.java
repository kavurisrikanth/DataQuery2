package graphql.input;

public class StudentEntityInput implements store.IEntityInput {
  private long id;
  public String name;

  public String _type() {
    return "Student";
  }

  public long getId() {
    return this.id;
  }

  public void setId(long id) {
    this.id = id;
  }
}
