package rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import graphql.ExecutionInput;
import graphql.language.Argument;
import graphql.language.ArrayValue;
import graphql.language.BooleanValue;
import graphql.language.Document;
import graphql.language.EnumValue;
import graphql.language.Field;
import graphql.language.FloatValue;
import graphql.language.IntValue;
import graphql.language.NodeUtil;
import graphql.language.NodeUtil.GetOperationResult;
import graphql.language.NullValue;
import graphql.language.ObjectField;
import graphql.language.ObjectValue;
import graphql.language.Selection;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.language.VariableReference;
import graphql.parser.Parser;

public abstract class AbstractQueryService {

	protected List<Field> parseOperations(JSONObject req) throws Exception {
		JSONObject variables;
		if (req.has("variables")) {
			Object obj = req.get("variables");
			variables = obj instanceof JSONObject ? (JSONObject) obj : new JSONObject();
		} else {
			variables = new JSONObject();
		}
		req.put("variables", variables);
		ExecutionInput input = ExecutionInput.newExecutionInput().query(req.getString("query")).build();
		Parser parser = new Parser();
		Document document = parser.parseDocument(input.getQuery());
		GetOperationResult operation = NodeUtil.getOperation(document, input.getOperationName());
		List<Selection> selections = operation.operationDefinition.getSelectionSet().getSelections();
		List<Field> operations = new ArrayList<>();
		for (Selection s : selections) {
			if (s instanceof Field) {
				Field f = (Field) s;
				operations.add(f);
			} else {
				throw new RuntimeException("Unsupported opertation: " + s);
			}
		}
		return operations;
	}

	public static Field inspect(Field field, String path) {
		if (path.isEmpty()) {
			return field;
		}
		String[] subFields = path.split("\\.");
		return inspect(field, 0, subFields);
	}

	protected static Field inspect(Field field, int i, String... subFields) {
		if (i == subFields.length) {
			return field;
		}
		for (Selection s : field.getSelectionSet().getSelections()) {
			if (s instanceof Field) {
				Field f = (Field) s;
				if (f.getName().equals(subFields[i])) {
					return inspect(f, i + 1, subFields);
				}
			}
		}
		return null;
	}

	protected Map<Long, JSONObject> byId(JSONArray list) throws Exception {
		Map<Long, JSONObject> byId = new HashMap<>();
		for (int i = 0; i < list.length(); i++) {
			JSONObject obj = list.getJSONObject(i);
			byId.put(obj.getLong("id"), obj);
		}
		return byId;
	}

	protected Map<String, Object> parseArguments(List<Argument> arguments, JSONObject variables) {
		Map<String, Object> values = new HashMap<>();
		for (Argument a : arguments) {
			Value value = a.getValue();
			values.put(a.getName(), convertValue(value, variables));
		}
		return values;
	}

	protected <T> List<T> parseObjectValueList(JSONObject variables, Object object, Class<T> cls) throws Exception {
		return null;
	}
	
	protected <T> T parseObjectValue(JSONObject variables, Object object, Class<T> cls) throws Exception {
		if (object instanceof JSONObject) {
			T obj = cls.getDeclaredConstructor().newInstance();
			JSONObject jo = (JSONObject) object;
			Iterator<String> keys = jo.keys();
			while (keys.hasNext()) {
				String k = keys.next();
				java.lang.reflect.Field field = cls.getField(k);
				Object value = convertJsonValue(jo.get(k));
				if (value instanceof JSONObject) {
					Object val = parseObjectValue(variables, value, field.getType());
					field.set(obj, val);
				} else {
					field.set(obj, value);
				}
			}
			return obj;
		} else if(object instanceof ObjectValue) {
			ObjectValue ov = (ObjectValue) object;
			T obj = cls.getDeclaredConstructor().newInstance();
			List<ObjectField> fields = ov.getObjectFields();
			for (ObjectField f : fields) {
				java.lang.reflect.Field field = cls.getField(f.getName());
				Value value = f.getValue();
				if (value instanceof ObjectValue) {
					Object val = parseObjectValue(variables, value, field.getType());
					field.set(obj, val);
				} else {
					Object val = convertValue(value, variables);
					field.set(obj, val);
				}
			}
			return obj;
		} else {
			return (T) convertValue((Value) object, variables);
		}
	}

	protected Map<String, Object> prepareArgsMap(Map<String, Object> args, JSONObject variables) {
		Map<String, Object> map = new HashMap<>();
		args.forEach((k, v) -> {
			map.put(k, toMap(v, variables));
		});
		return map;
	}

	private Object toMap(Object value, JSONObject variables) {
		if (value instanceof IntValue) {
			return ((IntValue) value).getValue().longValue();
		} else if (value instanceof StringValue) {
			return ((StringValue) value).getValue();
		} else if (value instanceof BooleanValue) {
			return ((BooleanValue) value).isValue();
		} else if (value instanceof FloatValue) {
			return ((FloatValue) value).getValue().doubleValue();
		} else if (value instanceof NullValue) {
			return null;
		} else if (value instanceof EnumValue) {
			return ((EnumValue) value).getName();
		} else if (value instanceof ArrayValue) {
			List<Value> values = ((ArrayValue) value).getValues();
			return values.stream().map(v -> toMap(v, variables)).collect(Collectors.toList());
		} else if (value instanceof VariableReference) {
			try {
				Object v = variables.get(((VariableReference) value).getName());
				return convertJsonValue(v);
			} catch (JSONException e) {
			}
		} else if (value instanceof ObjectValue) {
			ObjectValue ov = (ObjectValue) value;
			List<ObjectField> fields = ov.getObjectFields();
			Map<String, Object> obj = new HashMap<>();
			fields.forEach(o -> obj.put(o.getName(), toMap(o.getValue(), variables)));
			return obj;
		}
		return null;
	}

	private Object convertValue(Value value, JSONObject variables) {
		if (value instanceof IntValue) {
			return ((IntValue) value).getValue().longValue();
		} else if (value instanceof StringValue) {
			return ((StringValue) value).getValue();
		} else if (value instanceof BooleanValue) {
			return ((BooleanValue) value).isValue();
		} else if (value instanceof FloatValue) {
			return ((FloatValue) value).getValue().doubleValue();
		} else if (value instanceof NullValue) {
			return null;
		} else if (value instanceof EnumValue) {
			return ((EnumValue) value).getName();
		} else if (value instanceof ArrayValue) {
			List<Value> values = ((ArrayValue) value).getValues();
			return values.stream().map(v -> convertValue(v, variables)).collect(Collectors.toList());
		} else if (value instanceof VariableReference) {
			try {
				Object v = variables.get(((VariableReference) value).getName());
				return convertJsonValue(v);
			} catch (JSONException e) {
			}
		} else if (value instanceof ObjectValue) {
			return value;
		}
		return null;
	}

	private Object convertJsonValue(Object v) {
		if (v instanceof Integer) {
			return ((Integer) v).longValue();
		}
		if (v instanceof Float) {
			return ((Float) v).doubleValue();
		}
		return v;
	}
}
