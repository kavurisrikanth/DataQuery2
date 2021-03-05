package gqltosql.schema;

import java.util.function.Function;

import graphql.language.Field;

public class DRefField<T> extends DField<T> {

	private Function<T, ?> getter;

	public DRefField(DModel<T> decl, String name, String column, DModel<?> ref, Function<T, ?> getter) {
		super(decl, name, column);
		setRef(ref);
		this.getter = getter;
	}

	@Override
	public FieldType getType() {
		return FieldType.Reference;
	}

	@Override
	public Object getValue(IDataFetcher fetcher, Field field, T _this) {
		return fetcher.fetchReference(field, getReference(), getter.apply(_this));
	}
}
