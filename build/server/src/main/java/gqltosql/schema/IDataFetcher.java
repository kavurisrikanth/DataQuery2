package gqltosql.schema;

import java.util.List;
import java.util.function.Function;

import graphql.language.Field;

public interface IDataFetcher {

	Object fetchPrimitive(Field field, Object value);

	Object fetchReference(Field field, DModel<?> type, Object value);

	<T, R> Object fetchCollection(List<T> value, Function<T, R> fetcher);
}
