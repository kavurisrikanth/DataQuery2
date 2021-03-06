package gqltosql.schema;

import graphql.language.Field;

public abstract class DField<T> {

	private String name;
	private String column;
	private DModel<?> ref;
	private String collTable;
	private String mappedByColumn;
	private DModel<T> decl;

	public DField(DModel<T> decl, String name, String column) {
		this.decl = decl;
		this.name = name;
		this.column = column;
	}

	public String getName() {
		return name;
	}

	public abstract FieldType getType();

	public DModel<?> getReference() {
		return ref;
	}

	public void setRef(DModel<?> ref) {
		this.ref = ref;
	}

	public String getColumnName() {
		return column;
	}

	public void setCollTable(String collTable) {
		this.collTable = collTable;
	}

	public String getCollTableName(String parentTable) {
		return collTable;
	}

	public DModel<T> declType() {
		return decl;
	}

	public void setDecl(DModel<T> decl) {
		this.decl = decl;
	}

	public String getMappedByColumn() {
		return mappedByColumn;
	}

	public abstract Object getValue(IDataFetcher fetcher, Field field, T _this);
}
