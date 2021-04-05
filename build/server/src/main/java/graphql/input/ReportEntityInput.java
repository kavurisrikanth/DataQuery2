package graphql.input;

public class ReportEntityInput implements store.IEntityInput {
  private long id;
  public double marks;
  public long student;

  public String _type() {
    return "Report";
  }

  public long getId() {
    return this.id;
  }

  public void setId(long id) {
    this.id = id;
  }
}
