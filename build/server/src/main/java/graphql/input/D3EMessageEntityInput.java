package graphql.input;

import java.time.LocalDateTime;
import java.util.List;

public abstract class D3EMessageEntityInput implements store.IEntityInput {
  private long id;
  public String from;
  public List<String> to;
  public String body;
  public LocalDateTime createdOn;

  public String _type() {
    return "D3EMessage";
  }

  public long getId() {
    return this.id;
  }

  public void setId(long id) {
    this.id = id;
  }
}
