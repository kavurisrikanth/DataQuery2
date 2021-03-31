package classes;

import rest.GraphQLInputContext;

public class LimitAndOffsetStudents2Request implements rest.IGraphQLInput {
  public long limit;
  public long offset;

  public LimitAndOffsetStudents2Request() {}

  public LimitAndOffsetStudents2Request(long limit, long offset) {
    this.limit = limit;
    this.offset = offset;
  }

  @Override
  public void fromInput(GraphQLInputContext ctx) {
    if (ctx.has("limit")) {
      limit = ctx.readInteger("limit");
    }
    if (ctx.has("offset")) {
      offset = ctx.readInteger("offset");
    }
  }
}
