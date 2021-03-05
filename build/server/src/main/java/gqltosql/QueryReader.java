package gqltosql;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

public class QueryReader {

	private List<QueryTypeReader> byType = new ArrayList<>();
	private int id;

	public QueryReader(int id) {
		this.id = id;
	}

	public JSONObject read(Object val, Map<Long, SqlRow> byId) throws Exception {
		Object[] row;
		if(val.getClass().isArray()) {
			row = (Object[]) val;
		} else {
			row = new Object[] {val};
		}
		SqlRow obj = null;
		if (id != -1) {
			BigInteger bid = ((BigInteger) row[id]);
			if (bid != null) {
				Long _id = ((BigInteger) row[id]).longValue();
				obj = byId.get(_id);
			}
		}

		if (obj == null) {
			obj = new SqlRow();
		}

		for (QueryTypeReader tr : byType) {
			tr.read(row, obj);
		}
		if (obj.has("id") || obj.has("_parent")) {
			return obj;
		} else {
			return null;
		}
	}

	public QueryTypeReader getTypeReader(String type) {
		for (QueryTypeReader tr : byType) {
			if (tr.getType().equals(type)) {
				return tr;
			}
		}
		QueryTypeReader tr = new QueryTypeReader(type);
		byType.add(tr);
		return tr;
	}
}
