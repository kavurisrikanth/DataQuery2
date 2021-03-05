package gqltosql;

import org.json.JSONObject;

public class SimpleValue implements IValue {

	private String field;
	private int index;

	public SimpleValue(String field, int index) {
		this.field = field;
		this.index = index;
	}

	@Override
	public void read(Object[] row, JSONObject obj) throws Exception {
		obj.put(field, row[index]);
	}
	
	@Override
	public String toString() {
		return field;
	}
}
