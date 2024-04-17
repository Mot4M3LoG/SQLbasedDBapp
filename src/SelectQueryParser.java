import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectQueryParser{
    public static void processSelectQuery(String query, Database DB) throws IllegalArgumentException{
        String regex = "\\bFROM\\s+(\\w+)\\b";

        String tableName = DistinguishQuery.extractTableName(query, regex);
        Table table = DB.getTableByName(tableName);

        if (table != null) {
            try {
                List<Column> columns = extractColumns(query, table);
                Condition condition = DistinguishQuery.extractCondition(query);

                List<Integer> selectedRows = DistinguishQuery.applyConditions(table, condition);

                printTableData(selectedRows, columns);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Table '" + tableName + "' not found.");
        }
    }

    private static List<Column> extractColumns(String query, Table table) throws IllegalArgumentException{
        String regex = "(?i)\\bSELECT\\s+(\\*|\\w+(?:\\s*,\\s*\\w+)*)\\s+FROM\\s+(\\w+)\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(query);

        if (matcher.find()) {
            String columnsString = matcher.group(1);

            if (columnsString.equals("*")) {
                return table.getColumns();
            }

            try {
                return DistinguishQuery.extractColumns(columnsString, table);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Invalid SELECT Query.");
        }
    }

    private static void printTableData(List<Integer> selectedRows, List<Column> columns){
        for (Column column : columns) {
            System.out.print(column.getName() + "\t");
        }
        System.out.println();

        for (int rowIndex : selectedRows) {
            for (Column column : columns) {
                System.out.print(column.getContents().get(rowIndex) + "\t");
            }
            System.out.println();
        }
    }


}
