package store;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import classes.MutateResultStatus;
import d3e.core.DFile;
import d3e.core.DFileEntityInput;
import d3e.core.ListExt;
import graphql.input.ObjectRef;
import models.CreatableObject;

public class InputHelperImpl implements InputHelper {

	private Map<String, Object> data;
	private Map<Long, Object> inputObjectCache;
	private Map<String, DFile> files;
	private String currentField;
	private Iterator<Map<String, Object>> currentColl;
	private final EntityMutator mutator;

	public InputHelperImpl(EntityMutator mutator, Map<String, Object> data) {
		this(mutator, data, new HashMap<>(), new HashMap<>());
	}

	public InputHelperImpl(EntityMutator mutator, Map<String, Object> data,
			Map<Long, Object> inputObjectCache, Map<String, DFile> files) {
		this.mutator = mutator;
		this.data = data;
		this.inputObjectCache = inputObjectCache;
		this.files = files;
	}

	@Override
	public boolean has(String name) {
		return this.data.containsKey(name);
	}

	@Override
	public <T extends DatabaseObject, I extends IEntityInput> T readRef(ObjectRef ref) {
		if (ref == null) {
			return null;
		}
		return readRef(ref.getType(), ref.getId());
	}

	@Override
	public <T extends DatabaseObject, I extends IEntityInput> T readRef(String type, long id) {
		EntityHelper<T, I> helper = mutator.getHelper(type);
		return readRef(helper, id);
	}

	private <T, I extends IEntityInput> T readRef(EntityHelper<T, I> helper, long id) {
		if (id > 0) {
			T obj = helper.getById(id);
			if (obj == null) {
			  throw new ValidationFailedException(MutateResultStatus.BadRequest, ListExt.asList("Nothing found for current id."));
			}
      		return obj; 
		}
		if (id == 0) {
			return null;
		}
		T obj = (T) inputObjectCache.get(id);
		if (obj == null) {
			obj = (T) helper.newInstance();
			if (obj instanceof DatabaseObject) {
				((DatabaseObject) obj).localId = id;
				inputObjectCache.put(id, (DatabaseObject) obj);
			}
		}
		return obj;
	}

	@Override
	public <T, I extends IEntityInput> T readChild(I input, String field) {
		EntityHelper<T, I> helper = (EntityHelper<T, I>) mutator.getHelper(input._type());
		T obj;
		InputHelper sub = sub(field);
		if (sub.has("id")) {
			obj = readRef(helper, input.getId());
			if (obj == null) {
				return null;
			}
		} else {
			obj = (T) helper.newInstance();
		}
		helper.fromInput(input, obj, sub);
		return obj;
	}

	@Override
  	public <T, I extends IEntityInput> void readEmbedded(T obj, I input, String field) {
		if(input == null) {
      		return;
		}
		EntityHelper<T, I> helper = (EntityHelper<T, I>) mutator.getHelper(input._type());
		helper.fromInput(input, obj, sub(field));
	}

	@Override
	public <T extends DatabaseObject, I extends IEntityInput> T readUnionChild(I input, String field) {
		EntityHelper<T, I> helper = (EntityHelper<T, I>) mutator.getHelper(input._type());
		T obj;
		InputHelper sub = subUnion(field);
		if (sub.has("id")) {
			obj = readRef(helper, input.getId());
			if (obj == null) {
				return null;
			}
		} else {
			obj = (T) helper.newInstance();
		}
		helper.fromInput(input, obj, sub);
		return obj;
	}

	@Override
	public InputHelper subUnion(String name) {
		InputHelperImpl sub = sub(name);
		return sub.sub("value" + sub.data.get("type").toString());
	}

	@SuppressWarnings("unchecked")
	@Override
	public InputHelperImpl sub(String name) {
		if (name.equals(currentField)) {
			if (currentColl.hasNext()) {
				return new InputHelperImpl(mutator, currentColl.next(), inputObjectCache, files);
			} else {
				return new InputHelperImpl(mutator, new HashMap<>(), inputObjectCache, files);
			}
		}
		Object obj = data.get(name);
		if (obj instanceof Collection) {
			currentField = name;
			currentColl = ((Collection) obj).iterator();
			return sub(name);
		} else {
			currentField = null;
			currentColl = null;
		}
		if (obj == null) {
			obj = new HashMap<>();
		}
		return new InputHelperImpl(mutator, (Map<String, Object>) obj, inputObjectCache, files);
	}

	@Override
	public <T extends DatabaseObject, I extends IEntityInput> T readUpdate(EntityHelper<T, I> helper, long id, I input,
			String field) {
		// Check id
		if (id <= 0) {
			throw new ValidationFailedException(MutateResultStatus.BadRequest, ListExt.asList("Invalid id."));
		}

		// Get and Check current value
		T current = helper.getById(id);
		if (current == null) {
			throw new ValidationFailedException(MutateResultStatus.BadRequest, ListExt.asList("Nothing found for current id."));
		}

		// Set old if creatable
		if (current instanceof CreatableObject) {
			((CreatableObject) current).setOld((CreatableObject) helper.getOld(id));
		}

		// Get new value
		return readChild(input, field);
	}
	
	public DFile readDFile(DFileEntityInput input, String field) {
		if (input == null) {
			return null;
		}
		InputHelper helper = sub(field);
		if (helper == null) {
			return null;
		}
		String id = input.getId();
		DFile entity = files.get(id);
		if(entity!=null) {
			return entity;
		} else {
			entity = new DFile();
		}
		if (helper.has("size")) {
			entity.setSize(input.getSize());
		}
		if (helper.has("name")) {
			entity.setName(input.getName());
		}
		if (helper.has("id")) {
			entity.setId(input.getId());
		}
		return entity;
	}
}
