package gqltosql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gqltosql.schema.DModel;

public class SqlAstNode {

	private Map<String, SqlTable> tables = new HashMap<>();
	private String type;
	private String table;
	private boolean needType;
	private String path;

	public SqlAstNode(String path, String type, String table) {
		this.path = path;
		this.type = type;
		this.table = table;
		tables.put(type, new SqlTable(type, table));
	}

	public SqlQueryContext createCtx() {
		SqlQueryContext ctx = new SqlQueryContext(this, 0);
		ctx.getQuery().setFrom(getTableName(), ctx.getFrom());
		ctx.getQuery().addWhere(ctx.getFrom() + "._id in ?1");
		return ctx;
	}

	public void setNeedType(boolean needType) {
		this.needType = needType;
	}

	public boolean needType() {
		return needType;
	}

	public void addColumn(DModel<?> type, ISqlColumn column) {
		SqlTable tbl = tables.get(type.getType());
		if (tbl == null) {
			tables.put(type.getType(), tbl = new SqlTable(type.getType(), type.getTableName()));
		}
		tbl.addColumn(column);
	}

	public String getTableName() {
		return table;
	}

	public String getType() {
		return type;
	}

	public Map<String, SqlTable> getTables() {
		return tables;
	}

	public String getPath() {
		return path;
	}

	@Override
	public String toString() {
		return type;
	}

	public void selectColumns(SqlQueryContext ctx) {
		if (needType()) {
			StringBuilder b = new StringBuilder();
			b.append("(case");
			getTables().forEach((t, f) -> b.append(" when ").append(ctx.getTableAlias(t))
					.append("._id is not null then '").append(t).append('\''));
			b.append("else 'no-type' end)");
			ctx.addSelection(b.toString(), "__typename");
		}
		SqlQueryContext typeCtx = ctx.subType(getType());
		getTables().get(getType()).addSelections(typeCtx);

		getTables().forEach((type, table) -> {
			if (type.equals(getType())) {
				return;
			}
			SqlQueryContext sub = ctx.subType(type);
			String join = sub.getFrom();
			sub.addJoin(table.getTableName(), join, join + "._id = " + typeCtx.getFrom() + "._id");
			table.addSelections(sub);
		});
	}

	public JSONArray executeQuery(EntityManager em, Set<Long> ids, Map<Long, SqlRow> byId) throws Exception {
		if (ids.isEmpty()) {
			return new JSONArray();
		}
		SqlQueryContext ctx = createCtx();
		ctx.addSqlColumns(this);
		SqlQuery query = ctx.getQuery();
		String sql = query.createSQL();
		System.out.println("Path: " + getPath());
		System.out.println("Execute SQL: " + sql);
		System.out.println("Ids : " + ids);
		Query q = em.createNativeQuery(sql);
		q.setParameter(1, ids);
		List<?> rows = q.getResultList();
		QueryReader reader = query.getReader();
		JSONArray result = new JSONArray();
		List<SqlRow> list = new ArrayList<>();
		for (Object r : rows) {
			JSONObject obj = reader.read(r, byId);
			result.put(obj);
			list.add((SqlRow) obj);
		}
		if (!list.isEmpty()) {
			executeSubQuery(em, (t) -> list.stream());
		}
		return result;
	}

	private void executeSubQuery(EntityManager em, Function<String, Stream<SqlRow>> listSupplier) throws Exception {
		for (Map.Entry<String, SqlTable> e : tables.entrySet()) {
			String type = e.getKey();
			SqlTable table = e.getValue();
			for (ISqlColumn c : table.getColumns()) {
				if (c instanceof RefSqlColumn) {
					SqlAstNode sub = ((RefSqlColumn) c).getSub();
					sub.executeSubQuery(em, (t) -> listSupplier.apply(type).map(o -> {
						try {
							SqlRow row = (SqlRow) o.getJSONObject(c.getFieldName());
							return row.isOfType(t) ? row : null;
						} catch (JSONException ex) {
							return null;
						}
					}).filter(Objects::nonNull));
					continue;
				}
				SqlAstNode sub = c.getSubQuery();
				if (sub != null) {
					Stream<SqlRow> list = listSupplier.apply(type);
					Map<Long, SqlRow> objById = new HashMap<>();
					list.forEach(o -> {
						try {
							objById.put(o.getLong("id"), o);
						} catch (JSONException ex) {
						}
					});
					JSONArray array = sub.executeQuery(em, objById.keySet(), objById);
					c.updateSubField(objById, array);
				}
			}

		}
	}
}
