package gqltosql.schema;

import java.util.Collection;
import java.util.function.Function;

public class DFlatField<T, V> extends DRefCollField<T> {

	private String[] flatPaths;

	public DFlatField(DModel<T> decl, String name, String column, String collTable, DModel<?> ref,
			Function<T, Collection<?>> getter, String... flatPaths) {
		super(decl, name, column, collTable, ref, getter);
		this.flatPaths = flatPaths;
	}

	public String[] getFlatPaths() {
		return flatPaths;
	}
}
