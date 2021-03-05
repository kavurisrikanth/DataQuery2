package gqltosql.schema;

import java.util.List;
import java.util.function.Function;

import graphql.language.Field;

public class DPrimCollField<T> extends DField<T> {

	private Function<T, List<?>> getter;

	public DPrimCollField(DModel<T> decl, String name, String column, String collTable, Function<T, List<?>> getter) {
		super(decl, name, column);
		setCollTable(collTable);
		this.getter = getter;
	}

	@Override
	public FieldType getType() {
		return FieldType.PrimitiveCollection;
	}

	@Override
	public Object getValue(IDataFetcher fetcher, Field field, T _this) {
		return fetcher.fetchCollection(getter.apply(_this), c -> fetcher.fetchPrimitive(field, c));
	}
}
