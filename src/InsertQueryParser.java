import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InsertQueryParser{
    public static void processInsertQuery(String query, Database DB) throws IllegalArgumentException{
        try {
            String tableName = extractTableName(query);
            Table table = DB.getTableByName(tableName);

            if (table != null){
                List<Column> columns = extractColumns(query, table);
                List<List<String>> values = parseInsertValues(query, table.getColumnsNumber());

                addValuesToColumns(columns, values);

                DistinguishQuery.printTableToFile(tableName, columns, table);
            }else{
                throw new IllegalArgumentException("Table" + tableName + "not found");
            }
        } catch(IllegalArgumentException | FileHandlingError e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static String extractTableName(String query) throws IllegalArgumentException{
        Pattern tableNamePattern = Pattern.compile("(?i)\\bINSERT INTO\\s+(\\w+)\\b");
        Matcher tableNameMatcher = tableNamePattern.matcher(query);

        if (tableNameMatcher.find()) {
            return tableNameMatcher.group(1);
        } else {
            throw new IllegalArgumentException("Table name not found in the query.");
        }
    }
    private static List<Column> extractColumns(String query, Table table) throws IllegalArgumentException{
        Pattern columnsPattern = Pattern.compile("(?i)\\bINSERT INTO \\w+ (?:\\(([^)]+)\\))?(?!\\s*VALUES)");
        Matcher columnsMatcher = columnsPattern.matcher(query);

        if (columnsMatcher.find()) {
            String columnsString = columnsMatcher.group(1);

            if (columnsString != null && !columnsString.isEmpty()) {
                return DistinguishQuery.extractColumns(columnsString, table);
            }

        }else{
            throw new IllegalArgumentException("Invalid INSERT Query.");
        }
        return table.getColumns();
    }

    private static List<List<String>> parseInsertValues(String query, int numColumns) throws IllegalArgumentException {
        List<List<String>> valuesList = new ArrayList<>();
        try {
            String valuesContent = extractValuesContent(query);
            valuesList = splitValues(valuesContent);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        return valuesList;
    }

    private static String extractValuesContent(String query) throws IllegalArgumentException {
        Pattern valuesPattern = Pattern.compile("(?i)\\bVALUES\\s*(.+)$");
        Matcher valuesMatcher = valuesPattern.matcher(query);

        if (valuesMatcher.find()) {
            return valuesMatcher.group(1);
        } else {
            throw new IllegalArgumentException("No VALUES found in the query.");
        }
    }

    private static List<List<String>> splitValues(String valuesContent) {
        List<List<String>> valuesList = new ArrayList<>();
        Pattern valuesSetPattern = Pattern.compile("\\(([^)]+)\\)");

        Matcher valuesSetMatcher = valuesSetPattern.matcher(valuesContent);

        while (valuesSetMatcher.find()) {
            String valuesSetString = valuesSetMatcher.group(1);
            String[] valuesArray = valuesSetString.split("\\s*,\\s*");
            valuesList.add(Arrays.asList(valuesArray));
        }

        return valuesList;
    }


    private static void addValuesToColumns(List<Column> columns, List<List<String>> valuesList) throws IllegalArgumentException {
        for (List<String> values : valuesList) {
            if (values.size() != columns.size()) {
                throw new IllegalArgumentException("Number of values does not match the number of columns.");
            }

            for (int i = 0; i < columns.size(); i++) {
                Column column = columns.get(i);
                String value = values.get(i);
                column.addValue(value);
            }
        }
    }
}
