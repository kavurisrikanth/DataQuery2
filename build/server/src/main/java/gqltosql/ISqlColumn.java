package gqltosql;

import java.util.Map;

import org.json.JSONArray;

public interface ISqlColumn {

	String getFieldName();

	void addColumn(SqlQueryContext ctx);

	SqlAstNode getSubQuery();

	void updateSubField(Map<Long, SqlRow> parents, JSONArray all) throws Exception;
}
