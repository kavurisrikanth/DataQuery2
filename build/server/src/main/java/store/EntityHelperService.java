package store;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import classes.ClassUtils;

@Service
public class EntityHelperService implements org.springframework.beans.factory.InitializingBean {

	@Autowired
	private Map<String, EntityHelper<?, ? extends IEntityInput>> entityHelpers;

	private static EntityHelperService instance;

	@Override
	public void afterPropertiesSet() throws Exception {
		instance = this;
	}

	public static EntityHelperService getInstance() {
		return instance;
	}

	public void setEntityHelpers(Map<String, EntityHelper<?, ? extends IEntityInput>> entityHelpers) {
		this.entityHelpers = entityHelpers;
	}

	public EntityHelper<?, ? extends IEntityInput> get(String name) {
		return entityHelpers.get(name);
	}

	public EntityHelper<?, ? extends IEntityInput> getByObject(Object obj) {
		return entityHelpers.get(ClassUtils.getClass(obj).getSimpleName());
	}

	public void set(String name, EntityHelper<?, ? extends IEntityInput> helper) {
		entityHelpers.put(name, helper);
	}
}
