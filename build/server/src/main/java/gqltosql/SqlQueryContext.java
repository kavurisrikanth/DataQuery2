package gqltosql;

import java.util.HashMap;
import java.util.Map;

public class SqlQueryContext {

	private SqlQuery query;
	private QueryReader reader;
	private QueryTypeReader typeReader;
	private AliasGenerator ag;
	private String prefix;
	private String alias;
	private Map<String, String> tableAliases;

	public SqlQueryContext(SqlAstNode node, int id) {
		this.query = new SqlQuery(new QueryReader(id));
		this.reader = this.query.getReader();
		this.typeReader = this.reader.getTypeReader("_coll");
		this.ag = new AliasGenerator();
		this.tableAliases = new HashMap<>();
		createAllTablesAliases(this.prefix, node);
		this.alias = getTableAlias(node.getType());
	}

	private void createAllTablesAliases(String prefix, SqlAstNode node) {
		node.getTables().forEach((type, table) -> {
			String localPrefix = prefix == null ? type : prefix + "." + type;
			tableAliases.put(localPrefix, nextAlias());
			table.getColumns().forEach(c -> {
				if (c instanceof RefSqlColumn) {
					RefSqlColumn ref = (RefSqlColumn) c;
					createAllTablesAliases(localPrefix + "." + c.getFieldName(), ref.getSub());
				}
			});
		});
	}

	private SqlQueryContext(SqlQueryContext from) {
		this.query = from.query;
		this.reader = from.reader;
		this.typeReader = from.typeReader;
		this.ag = from.ag;
		this.tableAliases = from.tableAliases;
		this.alias = from.alias;
		this.prefix = from.prefix;
	}

	public void addSqlColumns(SqlAstNode node) {
		node.selectColumns(this);
	}

	public SqlQueryContext subType(String type) {
		SqlQueryContext ctx = new SqlQueryContext(this);
		ctx.prefix = this.prefix == null ? type : this.prefix + "." + type;
		ctx.alias = tableAliases.get(ctx.prefix);
		ctx.typeReader = reader.getTypeReader(type);
		return ctx;
	}

	public SqlQueryContext subPrefix(String prefix) {
		SqlQueryContext ctx = new SqlQueryContext(this);
		ctx.prefix = this.prefix == null ? prefix : this.prefix + "." + prefix;
		ctx.alias = tableAliases.get(ctx.prefix);
		return ctx;
	}

	public SqlQueryContext subReader(QueryReader reader) {
		SqlQueryContext ctx = new SqlQueryContext(this);
		ctx.reader = reader;
		return ctx;
	}

	public void addSelection(String column, String field) {
		query.addSelection(column, field, typeReader);
	}

	public QueryReader addRefSelection(String column, String field) {
		return query.addRefSelection(column, field, typeReader);
	}

	public void addJoin(String tableName, String join, String on) {
		query.addJoin(tableName, join, on);
	}

	public void addGroupBy(String column) {
		query.addGroupBy(column);
	}

	public String nextAlias() {
		return ag.next();
	}

	public String getFrom() {
		if (alias == null) {
			System.out.println();
		}
		return alias;
	}

	public SqlQuery getQuery() {
		return query;
	}

	public String getTableAlias(String type) {
		String localPrefix = this.prefix == null ? type : this.prefix + "." + type;
		String a = tableAliases.get(localPrefix);
		if (a == null) {
			System.out.println();
		}
		return a;
	}

}
