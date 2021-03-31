package gqltosql.schema;

import java.util.Collection;
import java.util.function.Function;

import graphql.language.Field;

public interface IDataFetcher {

	Object fetchPrimitive(Field field, Object value);

	Object fetchReference(Field field, DModel<?> type, Object value);

	<T, R> Object fetchCollection(Collection<T> value, Function<T, R> fetcher);
}
