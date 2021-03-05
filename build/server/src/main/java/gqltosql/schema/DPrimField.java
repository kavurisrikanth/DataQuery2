package gqltosql.schema;

import java.util.function.Function;

import graphql.language.Field;

public class DPrimField<T> extends DField<T> {

	private Function<T, ?> getter;

	public DPrimField(DModel<T> decl, String name, String column, Function<T, ?> getter) {
		super(decl, name, column);
		this.getter = getter;
	}

	@Override
	public FieldType getType() {
		return FieldType.Primitive;
	}

	@Override
	public Object getValue(IDataFetcher fetcher, Field field, T _this) {
		return fetcher.fetchPrimitive(field, getter.apply(_this));
	}
}
