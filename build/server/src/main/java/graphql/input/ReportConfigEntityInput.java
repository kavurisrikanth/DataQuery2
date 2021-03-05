package graphql.input;

import java.util.List;

public class ReportConfigEntityInput implements store.IEntityInput {
  private long id;
  public String identity;
  public List<ReportConfigOptionEntityInput> values;

  public String _type() {
    return "ReportConfig";
  }

  public long getId() {
    return this.id;
  }

  public void setId(long id) {
    this.id = id;
  }
}
