import java.util.ArrayList;
import java.util.List;

public class Database {
    private final List<Table> tables;

    public Database() {
        this.tables = new ArrayList<>();
    }

    public List<Table> getTables() {
        return tables;
    }

    public Table getTableByName(String name) {
        for (Table table : tables) {
            if (table.getName().equalsIgnoreCase(name)) {
                return table;
            }
        }
        return null;
    }

    public void addTable(Table table) {
        tables.add(table);
    }

}
