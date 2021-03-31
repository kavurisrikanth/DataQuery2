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
		if (val.getClass().isArray()) {
			row = (Object[]) val;
		} else {
			row = new Object[] { val };
		}
		Long rowId = readId(row, this.id);
		if (rowId == null) {
			for (QueryTypeReader r : byType) {
				Long id = readId(row, r.getId());
				if (id != null) {
					rowId = id;
					break;
				}
			}
		}
		SqlRow obj = null;
		if (rowId != null) {
			obj = byId.get(rowId);
		}
		if (obj == null) {
			obj = new SqlRow();
		}
		if (rowId != null) {
			obj.put("id", rowId);
		}
		for (QueryTypeReader tr : byType) {
			tr.read(row, obj);
		}
		if(obj.length() == 0) {
			return null;
		}
		return obj;
//		if (obj.has("id") || obj.has("_parent")) {
//			return obj;
//		} else {
//			return null; //TODO Embedded
//		}
	}

	private Long readId(Object[] row, int id) {
		if (id == -1) {
			return null;
		}
		BigInteger bid = ((BigInteger) row[id]);
		if (bid == null) {
			return null;
		}
		return ((BigInteger) row[id]).longValue();
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
