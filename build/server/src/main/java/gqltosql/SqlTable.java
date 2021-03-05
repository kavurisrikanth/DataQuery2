package gqltosql;

import java.util.ArrayList;
import java.util.List;

public class SqlTable {

	private String table;
	private String type;
	private List<ISqlColumn> columns = new ArrayList<>();

	public SqlTable(String type, String table) {
		this.type = type;
		this.table = table;
	}

	public void addColumn(ISqlColumn column) {
		if (column.getFieldName().equals("id")) {
			return;
		}
		for (ISqlColumn c : columns) {
			if (c.getFieldName().equals(column.getFieldName())) {
				return;
			}
		}
		columns.add(column);
	}

	public String getTableName() {
		return table;
	}

	public String getType() {
		return type;
	}

	public List<ISqlColumn> getColumns() {
		return columns;
	}

	public void addSelections(SqlQueryContext ctx) {
		ctx.addSelection(ctx.getFrom() + "._id", "id");
		getColumns().forEach(c -> c.addColumn(ctx));
	}

	@Override
	public String toString() {
		return table;
	}
}
