package rest;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import d3e.core.DFile;
import rest.DocumentReader.IdGenerator;
import store.DatabaseObject;
import store.EntityHelper;
import store.EntityHelperService;

public class JSONInputContext extends GraphQLInputContext implements IDocumentReader {

	private JSONObject json;

	public static <T> T fromJsonString(String doc, String type, long id) {
		if (doc == null) {
			return null;
		}
		try {
			JSONObject json = new JSONObject(doc);
			JSONInputContext ctx = new DocumentReader(json, EntityHelperService.getInstance(), null, null, null,
					new IdGenerator(id * 1000));
			return ctx.readObject(type, true);
		} catch (JSONException e) {
			return null;
		}
	}

	public static String toJsonString(DatabaseObject obj) {
		if (obj == null) {
			return null;
		}
		JSONInputContext ctx = new JSONInputContext(new JSONObject(), EntityHelperService.getInstance(), null, null,
				null);
		String type = obj.getClass().getSimpleName();
		ctx.writeString("__typename", type);
		ctx.writeObj(type, obj);
		return ctx.json.toString();
	}

	public JSONInputContext(JSONObject json, EntityHelperService helperService, Map<Long, Object> inputObjectCache,
			Map<String, DFile> files, JSONObject variables) {
		super(helperService, inputObjectCache, files, variables);
		this.json = json;
	}

	@Override
	protected GraphQLInputContext createContext(String field) {
		try {
			JSONObject obj = json.getJSONObject(field);
			if (obj == null) {
				return null;
			}
			return createReadContext(obj);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean has(String field) {
		return json.has(field);
	}

	@Override
	public <T> T readRef(String field, String type) {
		try {
			Object obj = json.get(field);
			if (obj == null) {
				return null;
			}
			if (obj instanceof JSONObject) {
				GraphQLInputContext ctx = createContext(field);
				return ctx.readObject(type, false);
			} else if (obj == JSONObject.NULL) {
				return null;
			} else {
				return (T) readRef(helperService.get(type), json.getLong(field));
			}
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T> T readChild(String field, String type) {
		GraphQLInputContext ctx = createContext(field);
		if (ctx == null) {
			return null;
		}
		return ctx.readObject(type, true);
	}

	@Override
	public <T> T readUnion(String field, String type) {
		try {
			JSONObject obj = json.getJSONObject(field);
			if (obj == null) {
				return null;
			}
			String _type = obj.getString("__typeName");
			JSONInputContext ctx = createReadContext(obj.getJSONObject("value" + _type));
			return ctx.readObject(_type, true);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public long readLong(String field) {
		try {
			return json.getLong(field);
		} catch (JSONException e) {
			return 0;
		}
	}

	@Override
	public String readString(String field) {
		try {
			if (json.isNull(field)) {
				return null;
			}
			return json.getString(field);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public long readInteger(String field) {
		try {
			return json.getLong(field);
		} catch (JSONException e) {
			return 0;
		}
	}

	@Override
	public double readDouble(String field) {
		try {
			return json.getDouble(field);
		} catch (JSONException e) {
			return 0.0;
		}
	}

	@Override
	public boolean readBoolean(String field) {
		try {
			return json.getBoolean(field);
		} catch (JSONException e) {
			return false;
		}
	}

	@Override
	public List<Long> readLongColl(String field) {
		try {
			JSONArray array = json.getJSONArray(field);
			int length = array.length();
			List<Long> res = new ArrayList<>();
			for (int i = 0; i < length; i++) {
				res.add(array.getLong(i));
			}
			return res;
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<String> readStringColl(String field) {
		try {
			JSONArray array = json.getJSONArray(field);
			int length = array.length();
			List<String> res = new ArrayList<>();
			for (int i = 0; i < length; i++) {
				res.add(array.getString(i));
			}
			return res;
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T> List<T> readUnionColl(String field, String type) {
		try {
			JSONArray array = json.getJSONArray(field);
			int length = array.length();
			List<T> res = new ArrayList<>();
			for (int i = 0; i < length; i++) {
				JSONObject obj = array.getJSONObject(i);
				String _type = obj.getString("__typeName");
				JSONInputContext ctx = createReadContext(obj.getJSONObject("value" + _type));
				res.add(ctx.readObject(_type, true));
			}
			return res;
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T> List<T> readChildColl(String field, String type) {
		try {
			JSONArray array = json.getJSONArray(field);
			int length = array.length();
			List<T> res = new ArrayList<>();
			for (int i = 0; i < length; i++) {
				JSONInputContext ctx = createReadContext(array.getJSONObject(i));
				res.add(ctx.readObject(type, true));
			}
			return res;
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	protected JSONInputContext createReadContext(JSONObject json) {
		return new JSONInputContext(json, helperService, inputObjectCache, files, variables);
	}

	@Override
	public <T> List<T> readRefColl(String field, String type) {
		try {
			JSONArray array = json.getJSONArray(field);
			int length = array.length();
			List<T> res = new ArrayList<>();
			for (int i = 0; i < length; i++) {
				Object obj = array.get(i);
				if (obj instanceof JSONObject) {
					JSONInputContext ctx = createReadContext((JSONObject) obj);
					res.add(ctx.readObject(type, false));
				} else {
					res.add((T) readRef(helperService.get(type), array.getLong(i)));
				}
			}
			return res;
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T extends Enum<?>> List<T> readEnumColl(String field, Class<T> cls) {
		try {
			JSONArray array = json.getJSONArray(field);
			int length = array.length();
			List<T> res = new ArrayList<>();
			for (int i = 0; i < length; i++) {
				res.add(readEnumInternal(array.getString(i), cls));
			}
			return res;
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<DFile> readDFileColl(String field) {
		try {
			JSONArray array = json.getJSONArray(field);
			int length = array.length();
			List<DFile> res = new ArrayList<>();
			for (int i = 0; i < length; i++) {
				JSONInputContext ctx = createReadContext(array.getJSONObject(i));
				res.add(readDFileInternal(ctx));
			}
			return res;
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Duration readDuration(String field) {
		throw new RuntimeException("Unsupported");
	}

	@Override
	public LocalDateTime readDateTime(String field) {
		throw new RuntimeException("Unsupported");
	}

	@Override
	public LocalDate readDate(String field) {
		throw new RuntimeException("Unsupported");
	}

	@Override
	public LocalTime readTime(String field) {
		throw new RuntimeException("Unsupported");
	}

	@Override
	public void writeLongColl(String field, List<Long> coll) {
		try {
			JSONArray array = new JSONArray();
			for (Long t : coll) {
				array.put(t);
			}
			json.put(field, array);
		} catch (JSONException e) {
		}
	}

	@Override
	public void writeStringColl(String field, List<String> coll) {
		try {
			JSONArray array = new JSONArray();
			for (String t : coll) {
				array.put(t);
			}
			json.put(field, array);
		} catch (JSONException e) {
		}
	}

	@Override
	public <T> void writeChildColl(String field, List<T> coll) {
		try {
			JSONArray array = new JSONArray();
			for (T t : coll) {
				String type = t.getClass().getSimpleName();
				JSONInputContext ctx = createWriteContext();
				ctx.writeString("__typename", type);
				ctx.writeObj(type, t);
				array.put(ctx.json);
			}
			json.put(field, array);
		} catch (JSONException e) {
		}
	}

	@Override
	public <T> void writeUnionColl(String field, List<T> coll) {
		try {
			JSONArray array = new JSONArray();
			for (T t : coll) {
				JSONObject union = new JSONObject();
				String type = t.getClass().getSimpleName();
				union.put("__typename", type);
				JSONInputContext ctx = createWriteContext();
				ctx.writeObj(type, t);
				union.put("value" + type, ctx.json);
				array.put(union);
			}
			json.put(field, array);
		} catch (JSONException e) {
		}
	}

	@Override
	public <T extends Enum<?>> void writeEnumColl(String field, List<T> coll) {
		try {
			JSONArray array = new JSONArray();
			for (T t : coll) {
				array.put(t.name());
			}
			json.put(field, array);
		} catch (JSONException e) {
		}
	}

	@Override
	public <T> void writeChild(String field, T obj) {
		if (obj == null) {
			return;
		}
		try {
			String type = obj.getClass().getSimpleName();
			JSONInputContext ctx = createWriteContext();
			ctx.writeString("__typename", type);
			ctx.writeObj(type, obj);
			json.put(field, ctx.json);
		} catch (JSONException e) {
		}
	}

	@Override
	public <T> void writeUnion(String field, T obj) {
		if (obj == null) {
			return;
		}
		try {
			JSONObject union = new JSONObject();
			String type = obj.getClass().getSimpleName();
			union.put("__typename", type);
			JSONInputContext ctx = createWriteContext();
			ctx.writeObj(type, obj);
			union.put("value" + type, ctx.json);
			json.put(field, union);
		} catch (JSONException e) {
		}
	}

	@Override
	public <T> void writeEmbedded(String field, T obj) {
		try {
			JSONInputContext ctx = createWriteContext();
			String type = obj.getClass().getSimpleName();
			ctx.writeObj(type, obj);
			json.put(field, ctx.json);
		} catch (JSONException e) {
		}
	}

	private <T> void writeObj(String type, T obj) {
		EntityHelper helper = helperService.get(type);
		helper.toJson(obj, this);
	}

	private JSONInputContext createWriteContext() {
		JSONInputContext ctx = new JSONInputContext(new JSONObject(), helperService, null, null, null);
		return ctx;
	}

	@Override
	public <T extends Enum<?>> void writeEnum(String field, T val) {
		if (val == null) {
			return;
		}
		try {
			json.put(field, val.name());
		} catch (JSONException e) {
		}
	}

	@Override
	public void writeLong(String field, long val) {
		try {
			json.put(field, val);
		} catch (JSONException e) {
		}
	}

	@Override
	public void writeString(String field, String val) {
		try {
			json.put(field, val);
		} catch (JSONException e) {
		}
	}

	@Override
	public void writeInteger(String field, long val) {
		try {
			json.put(field, val);
		} catch (JSONException e) {
		}
	}

	@Override
	public void writeDouble(String field, double val) {
		try {
			json.put(field, val);
		} catch (JSONException e) {
		}
	}

	@Override
	public void writeBoolean(String field, boolean val) {
		try {
			json.put(field, val);
		} catch (JSONException e) {
		}
	}

	@Override
	public void writeDuration(String field, Duration val) {
		throw new RuntimeException("Unsupported");
	}

	@Override
	public void writeDateTime(String field, LocalDateTime val) {
		throw new RuntimeException("Unsupported");
	}

	@Override
	public void writeTime(String field, LocalTime val) {
		throw new RuntimeException("Unsupported");
	}
}
