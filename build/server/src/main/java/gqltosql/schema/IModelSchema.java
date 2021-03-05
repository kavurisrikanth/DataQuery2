package gqltosql.schema;

public interface IModelSchema {

	public DModel<?> getType(String type);

	public default boolean hasParent(String type) {
		DModel<?> dm = getType(type);
		return dm.getParent() != null;
	}
}
