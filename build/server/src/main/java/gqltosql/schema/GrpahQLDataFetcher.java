package gqltosql.schema;

import java.util.List;
import java.util.function.Function;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import graphql.language.Field;
import graphql.language.InlineFragment;
import graphql.language.Selection;
import graphql.language.SelectionSet;
import graphql.language.TypeName;

public class GrpahQLDataFetcher implements IDataFetcher {

	private IModelSchema schema;

	public GrpahQLDataFetcher(IModelSchema schema) {
		this.schema = schema;
	}

	public Object fetch(Field field, String type, Object value) {
		return fetchReference(field, schema.getType(type), value);
	}

	@Override
	public Object fetchPrimitive(Field field, Object value) {
		if (value == null) {
			return JSONObject.NULL;
		}
		return value;
	}

	@Override
	public <T, R> Object fetchCollection(List<T> value, Function<T, R> fetcher) {
		JSONArray array = new JSONArray();
		value.forEach(v -> array.put(fetcher.apply(v)));
		return array;
	}

	@Override
	public Object fetchReference(Field field, DModel<?> type, Object value) {
		if (value == null) {
			return JSONObject.NULL;
		}
		if(type == null) {
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
						res.put("__typename", type.getTableName());
					}
				} catch (JSONException e) {
					throw new RuntimeException(e);
				}
			} else if (s instanceof InlineFragment) {
				InlineFragment in = (InlineFragment) s;
				TypeName typeName = in.getTypeCondition();
				DModel<?> dm = schema.getType(typeName.getName());
				fetchReferenceInternal(in.getSelectionSet(), res, dm, value);
			}
		}
	}
}
