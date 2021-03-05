package d3e.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import store.DatabaseObject;
import store.ICloneable;

public class CloneContext {
	Map<ICloneable, ICloneable> cache = new HashMap<>();
	boolean reverting = false;
	boolean crearId = false;
	private boolean isOld = false;

	public CloneContext(boolean crearId) {
		this.crearId = crearId;
	}

	public CloneContext(boolean crearId, boolean isOld) {
		this.crearId = crearId;
		this.isOld = isOld;
	}

	public static CloneContext forCloneable(ICloneable obj, boolean crearId) {
		return forCloneable(obj, crearId, false);
	}

	public static CloneContext forCloneable(ICloneable obj, boolean crearId, boolean isOld) {
		CloneContext ctx = new CloneContext(crearId, isOld);
		ctx.startClone(obj);
		return ctx;
	}

	public <T extends ICloneable> T startClone(T obj) {
		ICloneable cloned = cloneRef(obj);
		obj.deepCloneIntoObj(cloned, this);
		if (crearId && cloned instanceof DatabaseObject) {
			((DatabaseObject) cloned).setId(0l);
		}
		return (T) cloned;
	}

	public <T extends ICloneable> void collectChilds(List<T> exist) {
		exist.forEach((e) -> collectChild(e));
	}

	public <T extends ICloneable> void collectChild(T exist) {
		if (exist == null) {
			return;
		}
		T newObj = (T) exist.createNewInstance();
		if (newObj instanceof DatabaseObject) {
			((DatabaseObject) newObj).setId(((DatabaseObject) exist).getId());
		}
		cache.put(exist, newObj);
		exist.collectChildValues(this);
	}

	public <T extends ICloneable> void cloneChildList(List<T> exist, Consumer<List<T>> setter) {
		List<T> cloned = cloneRefList(exist);
		setter.accept(cloned);
		for (int i = 0; i < exist.size(); i++) {
			T c = cloned.get(i);
			exist.get(i).deepCloneIntoObj(c, this);
			if (crearId && c instanceof DatabaseObject) {
				((DatabaseObject) c).setId(0);
			}
		}
	}

	public <T extends ICloneable> List<T> cloneRefList(List<T> list) {
		List<T> cloned = ListExt.List();
		list.forEach((l) -> cloned.add(cloneRef(l)));
		return cloned;
	}

	public <T extends ICloneable> Set<T> cloneRefSet(Set<T> list) {
		Set<T> cloned = SetExt.Set();
		list.forEach((l) -> cloned.add(cloneRef(l)));
		return cloned;
	}

	public <T extends ICloneable> void cloneChild(T exist, Consumer<T> setter) {
		if (exist == null) {
			setter.accept(null);
		} else {
			T cloned = cloneRef(exist);
			setter.accept(cloned);
			exist.deepCloneIntoObj(cloned, this);
			if (crearId && cloned instanceof DatabaseObject) {
				((DatabaseObject) cloned).setId(0);
			}
		}
	}

	public <T extends ICloneable> T cloneRef(T obj) {
		if (obj == null) {
			return null;
		}
		if (reverting) {
			return (T) cache.get(obj);
		}
		ICloneable exist;
		if (cache.containsKey(obj)) {
			exist = cache.get(obj);
		} else {
			exist = obj.createNewInstance();
			if (exist instanceof DatabaseObject) {
				((DatabaseObject) exist).setId(((DatabaseObject) obj).getId());
				((DatabaseObject) exist).setIsOld(isOld);
			}
			cache.put(obj, exist);
		}
		return (T) exist;
	}

	public <T extends ICloneable> T getFromCache(T obj) {
		return (T) cache.getOrDefault(obj, null);
	}
}
