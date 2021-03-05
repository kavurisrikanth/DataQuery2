package gqltosql;

import org.json.JSONObject;

public interface IValue {

	void read(Object[] row, JSONObject obj) throws Exception;

}
