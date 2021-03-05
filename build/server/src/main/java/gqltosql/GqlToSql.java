package gqltosql;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.json.JSONArray;
import org.json.JSONObject;

import d3e.core.SetExt;
import gqltosql.schema.DField;
import gqltosql.schema.DModel;
import gqltosql.schema.IModelSchema;
import graphql.language.Field;
import graphql.language.FragmentSpread;
import graphql.language.InlineFragment;
import graphql.language.Selection;
import graphql.language.TypeName;

public class GqlToSql {

	private static final String TYPENAME = "__typename";
	private IModelSchema schema;
	private EntityManager em;

	public GqlToSql(EntityManager em, IModelSchema schema) {
		this.em = em;
		this.schema = schema;
	}

	public JSONArray execute(String parentType, Field field, List<SqlRow> objs) throws Exception {
		if (objs.isEmpty()) {
			return new JSONArray();
		}
		SqlAstNode sqlNode = prepareSqlNode(field.getSelectionSet().getSelections(), parentType);
		Set<Long> ids = new HashSet<>();
		Map<Long, SqlRow> byId = new HashMap<>();
		for (SqlRow obj : objs) {
			long id = obj.getLong("id");
			ids.add(id);
			byId.put(id, obj);
		}
		JSONArray result = sqlNode.executeQuery(em, ids, byId);
		return result;
	}

	public JSONObject execute(String parentType, Field field, Long id) throws Exception {
		JSONArray array = execute(parentType, field, SetExt.asSet(id));
		if (array.length() != 0) {
			return array.getJSONObject(0);
		}
		return null;
	}

	public JSONArray execute(String parentType, Field field, Set<Long> ids) throws Exception {
		SqlAstNode sqlNode = prepareSqlNode(field.getSelectionSet().getSelections(), parentType);
		JSONArray result = sqlNode.executeQuery(em, ids, new HashMap<>());
		return result;
	}

	private SqlAstNode prepareSqlNode(List<Selection> selections, String parentType) {
		DModel<?> dm = schema.getType(parentType);
		SqlAstNode node = new SqlAstNode("this", dm.getType(), dm.getTableName());
		addReferenceField(node, selections, dm);
		return node;
	}

	private void addField(SqlAstNode node, Field field, DModel<?> parentType) {
		if (field.getName().equals(TYPENAME)) {
			node.setNeedType(true);
			return;
		}
		DField<?> df = parentType.getField(field.getName());
		switch (df.getType()) {
		case Primitive:
			addPrimitiveField(node, field, df);
			break;
		case Reference:
			addReferenceField(node, field, df);
			break;
		case PrimitiveCollection:
			addPrimitiveCollectionField(node, field, df);
			break;
		case ReferenceCollection:
			addReferenceCollectionField(node, field, df);
			break;
		default:
			break;
		}
	}

	private void addPrimitiveCollectionField(SqlAstNode node, Field field, DField<?> df) {
		DModel<?> dcl = df.declType();
		SqlAstNode sub = new SqlPrimitiveCollAstNode(node.getPath() + "." + field.getName(),
				df.getCollTableName(dcl.getTableName()),
				dcl.getTableName() + (schema.hasParent(dcl.getType()) ? "_" : "") + "_id", df.getColumnName(),
				field.getName());
		addColumn(node, df, new CollSqlColumn(sub, field.getName()));
	}

	private void addReferenceCollectionField(SqlAstNode node, Field field, DField<?> df) {
		DModel<?> dcl = df.declType();
		DModel<?> dm = df.getReference();
		SqlCollAstNode sub = new SqlCollAstNode(node.getPath() + "." + field.getName(), dm.getType(), dm.getTableName(),
				df.getCollTableName(dcl.getTableName()),
				dcl.getTableName() + (schema.hasParent(dcl.getType()) ? "_" : "") + "_id", df.getColumnName());
		addReferenceField(sub, field.getSelectionSet().getSelections(), df.getReference());
		addColumn(node, df, new RefCollSqlColumn(sub, field.getName()));
	}

	private void addReferenceField(SqlAstNode node, Field field, DField<?> df) {
		DModel<?> dm = df.getReference();
		SqlAstNode sub = new SqlAstNode(node.getPath() + "." + field.getName(), dm.getType(), dm.getTableName());
		addReferenceField(sub, field.getSelectionSet().getSelections(), df.getReference());
		addColumn(node, df, new RefSqlColumn(sub, df.getColumnName(), field.getName()));
	}

	private void addColumn(SqlAstNode node, DField<?> df, ISqlColumn column) {
		node.addColumn(df.declType(), column);
	}

	private void addReferenceField(SqlAstNode node, List<Selection> selections, DModel<?> parentType) {
		for (Selection<?> selection : selections) {
			if (selection instanceof FragmentSpread) {
				throw new RuntimeException("TODO: FragmentSpread not implemented yet");
			} else if (selection instanceof InlineFragment) {
				InlineFragment in = (InlineFragment) selection;
				TypeName typeName = in.getTypeCondition();
				DModel<?> dm = schema.getType(typeName.getName());
				addReferenceField(node, in.getSelectionSet().getSelections(), dm);
			} else {
				addField(node, (Field) selection, parentType);
			}
		}
	}

	private void addPrimitiveField(SqlAstNode node, Field field, DField<?> df) {
		addColumn(node, df, new SqlColumn(df.getColumnName(), field.getName()));
	}
}
