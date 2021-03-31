package lists;

import javax.persistence.Query;

import store.DatabaseObject;

public abstract class AbsDataQueryImpl {
	protected void setParameter(Query query, String name, DatabaseObject value) {
		if (value == null) {
			query.setParameter(name, 0l);
		} else {
			query.setParameter(name, value.getId());
		}
	}

	protected void setParameter(Query query, String name, Enum<?> value) {
		if (value == null) {
			query.setParameter(name, "");
		} else {
			query.setParameter(name, value.name());
		}
	}

	protected void setParameter(Query query, String name, Object value) {
		query.setParameter(name, value);
	}
}
