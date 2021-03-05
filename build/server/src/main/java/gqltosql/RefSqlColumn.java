package gqltosql;

import java.util.Map;

import org.json.JSONArray;

public class RefSqlColumn implements ISqlColumn {

	private SqlAstNode sub;
	private String column;
	private String field;

	public RefSqlColumn(SqlAstNode sub, String column, String field) {
		this.sub = sub;
		this.column = column;
		this.field = field;
	}

	@Override
	public String getFieldName() {
		return field;
	}

	public SqlAstNode getSub() {
		return sub;
	}

	@Override
	public void addColumn(SqlQueryContext ctx) {
		QueryReader reader = ctx.addRefSelection(ctx.getFrom() + '.' + column, field);
		SqlQueryContext prefix = ctx.subPrefix(getFieldName());
		String join = prefix.getTableAlias(sub.getType());
		ctx.addJoin(sub.getTableName(), join, join + "._id = " + ctx.getFrom() + '.' + column);
		SqlQueryContext sc = prefix.subReader(reader);
		sc.addSqlColumns(sub);
	}

	@Override
	public String toString() {
		return field;
	}

	@Override
	public SqlCollAstNode getSubQuery() {
		return null;
	}

	@Override
	public void updateSubField(Map<Long, SqlRow> parents, JSONArray all) throws Exception {
	}
}
