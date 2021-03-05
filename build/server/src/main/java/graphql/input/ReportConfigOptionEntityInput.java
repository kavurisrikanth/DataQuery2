package graphql.input;

public class ReportConfigOptionEntityInput implements store.IEntityInput {
  private long id;
  public String identity;
  public String value;

  public String _type() {
    return "ReportConfigOption";
  }

  public long getId() {
    return this.id;
  }

  public void setId(long id) {
    this.id = id;
  }
}
