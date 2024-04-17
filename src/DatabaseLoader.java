import java.io.File;
import java.io.IOException;
import java.util.List;

public class DatabaseLoader {

    public static void loadDatabase(Database database, String databaseFilePath) {
        String databaseFileName = "database.txt";
        File databaseFile = new File(databaseFileName);

        if (!databaseFile.exists()) {
            try {
                if (databaseFile.createNewFile()) {
                    System.out.println("Database file created: " + databaseFileName);
                } else {
                    System.out.println("Failed to create database file.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        MyReader reader = new MyReader(databaseFilePath);
        String line;
        Table currentTable = null;
        while ((line = reader.readRow()) != null) {
            if (line.startsWith("Table:")) {
                String tableName = line.substring("Table:".length()).trim();
                currentTable = new Table(tableName);
                database.addTable(currentTable);
            } else if (line.startsWith("Column:")) {
                String columnName = line.substring("Column:".length()).trim();
                if (currentTable != null) {
                    currentTable.addColumn(new Column(columnName));
                }
            }
        }

        List<Table> tables = database.getTables();
        for (Table table : tables) {
            loadTableData(table);
        }
    }

    private static void loadTableData(Table table) {
        MyReader reader = new MyReader(table.getName() + ".txt");

        reader.readRow();

        List<Column> columns = table.getColumns();
        String line;
        while ((line = reader.readRow()) != null) {
            String[] values = line.split(",");

            for (int i = 0; i < values.length; i++) {
                String value = values[i].trim();
                columns.get(i).getContents().add(value);
            }
        }
    }
}
