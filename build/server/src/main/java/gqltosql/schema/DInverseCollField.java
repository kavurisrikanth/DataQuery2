package gqltosql.schema;

import java.util.List;
import java.util.function.Function;

import graphql.language.Field;

public class DInverseCollField<T> extends DField<T> {

	private Function<T, List<?>> getter;

	public DInverseCollField(DModel<T> decl, String name, String column, DModel<?> ref, Function<T, List<?>> getter) {
		super(decl, name, column);
		this.getter = getter;
		setRef(ref);
	}

	@Override
	public FieldType getType() {
		return FieldType.InverseCollection;
	}

	@Override
	public Object getValue(IDataFetcher fetcher, Field field, T _this) {
		return fetcher.fetchCollection(getter.apply(_this), c -> fetcher.fetchReference(field, getReference(), c));
	}
}
