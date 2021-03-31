package gqltosql;

import java.util.HashMap;

import org.json.JSONObject;

public class RefValue implements IValue {

	private String field;
	private QueryReader reader;

	public RefValue(String field, int index) {
		this.field = field;
		this.reader = new QueryReader(index);
	}

	public QueryReader getReader() {
		return reader;
	}

	@Override
	public void read(Object[] row, JSONObject obj) throws Exception {
		obj.put(field, reader.read(row, new HashMap<>()));
	}

	@Override
	public String toString() {
		return field;
	}
}
