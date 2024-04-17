import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateQueryParser{
    public static void processUpdateQuery(String query, Database DB){
        try {
            String regex = "(?i)\\bUPDATE\\s+(\\w+)\\s+SET\\s+.*";
            String tableName = DistinguishQuery.extractTableName(query, regex);
            Table table = DB.getTableByName(tableName);

            List<Column> columns = extractColumnsString(query, table);
            List<String> values = extractValuesToChange(query);

            Condition condition = DistinguishQuery.extractCondition(query);
            List<Integer> selectedRows = DistinguishQuery.applyConditions(table, condition);

            updateTableData(selectedRows, table, columns, values);
            DistinguishQuery.printTableToFile(tableName, table.getColumns(), table);
        } catch(FileHandlingError e){
            System.out.println("Error: " + e.getMessage());
        } catch(IllegalArgumentException e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private static List<Column> extractColumnsString(String query, Table table) throws IllegalArgumentException {
        String regex = "(?i)\\bSET\\s+([^=]+\\s*=\\s*[^,]+(?:\\s*,\\s*[^=]+\\s*=\\s*[^,]+)*)\\b";
        Pattern columnsPattern = Pattern.compile(regex);
        Matcher columnsMatcher = columnsPattern.matcher(query);

        if (columnsMatcher.find()) {
            String columnsString = columnsMatcher.group(1);

            if (columnsString != null && !columnsString.isEmpty()) {
                String[] columnPairs = columnsString.split("\\s*,\\s*");
                List<Column> columns = new ArrayList<>();

                for (String columnPair : columnPairs) {
                    String[] parts = columnPair.split("\\s*=\\s*");
                    String columnName = parts[0].trim();

                    Column column = table.getColumnByName(columnName);

                    if (column == null) {
                        throw new IllegalArgumentException("Column '" + columnName + "' does not exist in the table.");
                    }

                    columns.add(column);
                }

                return columns;
            }
        } else {
            throw new IllegalArgumentException("Invalid UPDATE Query.");
        }

        return table.getColumns();
    }

    private static List<String> extractValuesToChange(String query) throws IllegalArgumentException{
        String regex = "(?i)\\bSET\\s+((?:\\w+\\s*=\\s*\\S+\\s*,\\s*)*\\w+\\s*=\\s*\\S+)\\s*(?:\\bWHERE\\s+(.+))?\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(query);

        List<String> values = new ArrayList<>();

        if (matcher.find()) {
            String columnsAndValues = matcher.group(1);

            String[] pairs = columnsAndValues.split("\\s*,\\s*");

            for (String pair : pairs) {
                String[] parts = pair.split("\\s*=\\s*");

                if (parts.length == 2) {
                    values.add(parts[1].trim());
                } else {
                    throw new IllegalArgumentException("Invalid column-value pair: " + pair);
                }
            }

            return values;
        } else {
            throw new IllegalArgumentException("Invalid UPDATE Query.");
        }
    }

    private static void updateTableData(List<Integer> selectedRows, Table table, List<Column> columns, List<String> values){
        for (int rowIndex : selectedRows) {
            for (int i = 0; i < columns.size(); i++) {
                String columnName = columns.get(i).getName();
                String value = values.get(i);

                int columnIndex = table.getColumnIndex(columnName);

                List<String> columnContents = table.getColumns().get(columnIndex).getContents();

                columnContents.set(rowIndex, value);
            }
        }
    }
}
