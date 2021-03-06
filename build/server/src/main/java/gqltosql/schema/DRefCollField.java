package gqltosql.schema;

import java.util.Collection;
import java.util.function.Function;

import graphql.language.Field;

public class DRefCollField<T> extends DField<T> {

	private Function<T, Collection<?>> getter;

	public DRefCollField(DModel<T> decl, String name, String column, String collTable, DModel<?> ref,
			Function<T, Collection<?>> getter) {
		super(decl, name, column);
		setCollTable(collTable);
		setRef(ref);
		this.getter = getter;
	}

	@Override
	public FieldType getType() {
		return FieldType.ReferenceCollection;
	}

	@Override
	public Object getValue(IDataFetcher fetcher, Field field, T _this) {
		return fetcher.fetchCollection(getter.apply(_this), c -> fetcher.fetchReference(field, getReference(), c));
	}
}
