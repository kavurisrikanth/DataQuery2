package store;

import d3e.core.DFile;
import d3e.core.DFileEntityInput;
import graphql.input.ObjectRef;

public interface InputHelper {
	boolean has(String name);

	InputHelper sub(String name);

	InputHelper subUnion(String name);

	public <T extends DatabaseObject, I extends IEntityInput> T readRef(ObjectRef ref);

	public <T extends DatabaseObject, I extends IEntityInput> T readRef(String type, long id);

	public <T, I extends IEntityInput> T readChild(I input, String field);

  	public <T, I extends IEntityInput> void readEmbedded(T obj, I input, String field);

	public <T extends DatabaseObject, I extends IEntityInput> T readUnionChild(I input, String field);

	public <T extends DatabaseObject, I extends IEntityInput> T readUpdate(EntityHelper<T, I> helper, long id, I input,
			String field);

	public DFile readDFile(DFileEntityInput input, String field);
}
