package graphql.input;

import d3e.core.DFileEntityInput;

public class D3EImageEntityInput implements store.IEntityInput {
  private long id;
  public long size;
  public long width;
  public long height;
  public DFileEntityInput file;

  public String _type() {
    return "D3EImage";
  }

  public long getId() {
    return this.id;
  }

  public void setId(long id) {
    this.id = id;
  }
}
