package gqltosql.schema;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DModel<T> {

	private String type;
	private String table;
	private Map<String, DField<T>> fields = new HashMap<>();
	private DModel<?> parent;
	private boolean embedded;
	private boolean entity;
	private boolean document;
	private DModelType modelType;

	public DModel(String type, String table, DModelType modelType) {
		this.type = type;
		this.table = table;
		this.modelType = modelType;
	}

	public DModel(String type, String table) {
		this(type, table, false);
	}

	public DModel(String type, String table, boolean embedded) {
		this.type = type;
		this.table = table;
		this.embedded = embedded;
		this.entity = true;
	}

	public DModelType getModelType() {
		return modelType;
	}

	public void setEntity(boolean entity) {
		this.entity = entity;
	}

	public boolean isEmbedded() {
		return modelType == DModelType.EMBEDDED;
	}

	public String getTableName() {
		return table;
	}

	public DField<?> getField(String name) {
		DField<?> f = fields.get(name);
		if (f != null) {
			return f;
		}
		if (parent != null) {
			return parent.getField(name);
		}
		return null;
	}

	public boolean hasField(String name) {
		return getField(name) != null;
	}

	public String getType() {
		return type;
	}

	public boolean hasDeclField(String name) {
		return fields.containsKey(name);
	}

	public void setParent(DModel<?> parent) {
		this.parent = parent;
	}

	public DModel<?> getParent() {
		return parent;
	}

	public void addField(DField<T> field) {
		fields.put(field.getName(), field);
		field.setDecl(this);
	}

	public void addPrimitive(String name, String column, Function<T, ?> getter) {
		addField(new DPrimField<T>(this, name, column, getter));
	}

	public void addReference(String name, String column, DModel<?> ref, Function<T, ?> getter) {
		addField(new DRefField<T>(this, name, column, ref, getter));
	}

	public void addPrimitiveCollection(String name, String column, String collTable, Function<T, List<?>> getter) {
		addField(new DPrimCollField<T>(this, name, column, collTable, getter));
	}

	public void addReferenceCollection(String name, String column, String collTable, DModel<?> ref,
			Function<T, Collection<?>> getter) {
		addField(new DRefCollField<T>(this, name, column, collTable, ref, getter));
	}

	public <V> void addFlatCollection(String name, String column, String collTable, DModel<?> ref,
			Function<T, Collection<?>> getter, String... flatPaths) {
		addField(new DFlatField<T, V>(this, name, column, collTable, ref, getter, flatPaths));
	}

	public void addInverseCollection(String name, String column, DModel<?> ref, Function<T, List<?>> getter) {
		addField(new DInverseCollField<T>(this, name, column, ref, getter));
	}
}
