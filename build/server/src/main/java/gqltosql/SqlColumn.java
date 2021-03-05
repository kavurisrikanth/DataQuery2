package gqltosql;

import java.util.Map;

import org.json.JSONArray;

public class SqlColumn implements ISqlColumn {

	private String column;
	private String field;

	public SqlColumn(String column, String field) {
		this.column = column;
		this.field = field;
	}

	@Override
	public void addColumn(SqlQueryContext ctx) {
		ctx.addSelection(ctx.getFrom() + '.' + column, field);
	}

	@Override
	public String getFieldName() {
		return field;
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
