package gqltosql.schema;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import d3e.core.DFile;
import graphql.language.Field;
import graphql.language.InlineFragment;
import graphql.language.Selection;
import graphql.language.SelectionSet;
import graphql.language.TypeName;
import store.DatabaseObject;

public class GraphQLDataFetcher implements IDataFetcher {

	private IModelSchema schema;

	public GraphQLDataFetcher(IModelSchema schema) {
		this.schema = schema;
	}

	public Object fetch(Field field, String type, Object value) {
		return fetchReference(field, schema.getType(type), value);
	}

	public JSONArray fetchList(Field field, String type, List<?> value) {
		JSONArray array = new JSONArray();
		value.forEach(v -> array.put(fetch(field, type, v)));
		return array;
	}

	@Override
	public Object fetchPrimitive(Field field, Object value) {
		if (value == null) {
			return JSONObject.NULL;
		}
		if (value instanceof DFile) {
			return fetchDFile(field, (DFile) value);
		}
		return value;
	}

	private Object fetchDFile(Field field, DFile value) {
		JSONObject res = new JSONObject();
		for (Selection s : field.getSelectionSet().getSelections()) {
			if (s instanceof Field) {
				Field f = (Field) s;
				try {
					if (f.getName().equals("id")) {
						res.put("id", value.getId());
					} else if (f.getName().equals("name")) {
						res.put("name", value.getName());
					} else if (f.getName().equals("size")) {
						res.put("size", value.getSize());
					}
				} catch (JSONException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return res;
	}

	@Override
	public <T, R> Object fetchCollection(Collection<T> value, Function<T, R> fetcher) {
		JSONArray array = new JSONArray();
		value.forEach(v -> array.put(fetcher.apply(v)));
		return array;
	}

	@Override
	public Object fetchReference(Field field, DModel<?> type, Object value) {
		if (value == null) {
			return JSONObject.NULL;
		}
		if (type == null) {
			return value;
		}
		JSONObject res = new JSONObject();
		SelectionSet set = field.getSelectionSet();
		fetchReferenceInternal(set, res, type, value);
		return res;
	}

	private void fetchReferenceInternal(SelectionSet set, JSONObject res, DModel<?> type, Object value) {
		for (Selection s : set.getSelections()) {
			if (s instanceof Field) {
				Field f = (Field) s;
				DField df = type.getField(f.getName());
				try {
					if (df != null) {
						res.put(f.getName(), df.getValue(this, f, value));
					} else if (f.getName().equals("__typename")) {
						res.put("__typename", value.getClass().getSimpleName());
					} else if (f.getName().equals("localId")) {
						DatabaseObject db = (DatabaseObject) value;
						res.put("localId", db.getLocalId());
					}
				} catch (JSONException e) {
					throw new RuntimeException(e);
				}
			} else if (s instanceof InlineFragment) {
				InlineFragment in = (InlineFragment) s;
				TypeName typeName = in.getTypeCondition();
				if (value.getClass().getSimpleName().equals(typeName.getName())) {
					DModel<?> dm = schema.getType(typeName.getName());
					fetchReferenceInternal(in.getSelectionSet(), res, dm, value);
				}
			}
		}
	}
}
