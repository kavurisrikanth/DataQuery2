package graphql;

import com.coxautodev.graphql.tools.GraphQLResolver;

import store.DBObject;
import store.DatabaseObject;

@org.springframework.stereotype.Component
public class DatabaseObjectResolver implements GraphQLResolver<DatabaseObject>{
	DBObject getObject(DatabaseObject obj) {
		return null;
	}
}
