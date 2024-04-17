import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateTableQueryParser{
    public static void processCreateQuery(String query, Database DB) throws IllegalArgumentException{
        String regex = "(?i)\\bCREATE\\s+TABLE\\s+(\\w+)\\b";
        try {
            String tableName = DistinguishQuery.extractTableName(query, regex);
            List<String> columns = extractColumnNames(query);

            Table newTable = new Table(tableName);
            for (String columnName : columns) {
                newTable.addColumn(new Column(columnName));
            }
            DB.addTable(newTable);

            modifyDatabaseFile(tableName, columns);
            createTableFile(tableName, columns);
        } catch(IllegalArgumentException e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public static List<String> extractColumnNames(String query) throws IllegalArgumentException{
        List<String> columnNames = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\(([^)]+)\\)");
        Matcher matcher = pattern.matcher(query);

        if (matcher.find()) {
            String columnsPart = matcher.group(1);
            String[] columns = columnsPart.split("\\s*,\\s*");
            for (String column : columns) {
                columnNames.add(column.trim());
            }
        } else {
            throw new IllegalArgumentException("Invalid CREATE query");
        }

        return columnNames;
    }

    private static void modifyDatabaseFile(String tableName, List<String> columns){
        MyWriter writer = new MyWriter("database.txt", true);

        writer.writeLine("\n");
        writer.writeLine("Table: " + tableName + "\n");
        for (String column : columns){
            writer.writeLine("Column: " + column + "\n");
        }
        writer.close();
    }

    private static void createTableFile(String tableName, List<String> columns){
        MyWriter writer = new MyWriter(tableName + ".txt", false);
        StringBuilder header = new StringBuilder();

        for (String column : columns){
            header.append(column).append("\t");
        }
        writer.writeLine(header.toString());
    }
}
