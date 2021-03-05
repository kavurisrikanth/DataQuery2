package graphql.input;

import java.time.LocalDateTime;

public class OneTimePasswordEntityInput implements store.IEntityInput {
  private long id;
  public boolean success;
  public String errorMsg;
  public String token;
  public LocalDateTime expiry;

  public String _type() {
    return "OneTimePassword";
  }

  public long getId() {
    return this.id;
  }

  public void setId(long id) {
    this.id = id;
  }
}
