package gqltosql;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class CollSqlColumn implements ISqlColumn {

	private String field;
	private SqlAstNode sub;

	public CollSqlColumn(SqlAstNode sub, String field) {
		this.sub = sub;
		this.field = field;
	}

	@Override
	public String getFieldName() {
		return field;
	}

	@Override
	public void addColumn(SqlQueryContext ctx) {
	}

	@Override
	public String toString() {
		return field;
	}

	@Override
	public SqlAstNode getSubQuery() {
		return sub;
	}

	@Override
	public void updateSubField(Map<Long, SqlRow> parents, JSONArray all) throws Exception {
		Map<Long, JSONArray> values = new HashMap<>();
		for (int i = 0; i < all.length(); i++) {
			JSONObject obj = all.getJSONObject(i);
			Long parentId = obj.getLong("_parent");
			JSONArray val = values.get(parentId);
			if (val == null) {
				values.put(parentId, val = new JSONArray());
			}
			val.put(obj.get(getFieldName()));
		}
		for (Map.Entry<Long, SqlRow> e : parents.entrySet()) {
			JSONArray val = values.get(e.getKey());
			if (val == null) {
				val = new JSONArray();
			}
			e.getValue().put(field, val);
		}
	}
}
