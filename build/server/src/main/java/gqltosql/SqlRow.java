package gqltosql;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;

public class SqlRow extends JSONObject {

	private Set<String> types = new HashSet<>();

	public SqlRow() {
	}

	public void addType(String type) {
		types.add(type);
	}

	public boolean isOfType(String type) {
		return types.contains(type);
	}
}
