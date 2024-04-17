import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DistinguishQuery{
    public static String parseQuery(String query, Database DB) {
        String pattern = "(?i)\\b(SELECT|INSERT INTO|UPDATE|DELETE FROM|CREATE TABLE|EXIT)\\b";
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(query);

        try {
            if (matcher.find()) {
                String command = matcher.group(1);
                switch (command.toUpperCase()) {
                    case "SELECT":
                        SelectQueryParser.processSelectQuery(query, DB);  //TESTED
                        break;
                    case "INSERT INTO":
                        InsertQueryParser.processInsertQuery(query, DB); //TESTED
                        break;
                    case "UPDATE":
                        UpdateQueryParser.processUpdateQuery(query, DB); //TESTED
                        break;
                    case "DELETE FROM":
                        DeleteQueryParser.processDeleteQuery(query, DB); //TESTED
                        break;
                    case "CREATE TABLE":
                        CreateTableQueryParser.processCreateQuery(query, DB); //TESTED
                        break;
                    case "EXIT":
                        System.out.println("Exiting the program"); //TESTED
                        return "EXIT";
                    default:
                        System.out.println("Invalid query");
                        break;
                }
            } else {
                System.out.println("Invalid query");
            }
        } catch(IllegalArgumentException e){
            System.out.println("Error: " + e.getMessage());
        }

        return "";
    }

    public static List<Column> extractColumns(String columnsString, Table table) throws IllegalArgumentException{
        List<Column> columns = new ArrayList<>();
        String[] columnNames = columnsString.split("\\s*,\\s*");
        for (String columnName : columnNames) {
            String trimmedColumn = columnName.trim();
            if (table.hasColumn(trimmedColumn)) {
                columns.add(table.getColumnByName(trimmedColumn));
            }
            else {
                throw new IllegalArgumentException("Column '" + trimmedColumn + "' does not exist in the table.");
            }
        }
        return columns;
    }

    public static String extractTableName(String query, String regex){
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(query);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            System.out.println("Invalid query: Table name not found.");
            return null;
        }
    }
    public static List<Integer> applyConditions(Table table, Condition conditions) throws IllegalArgumentException{
        List<Integer> selectedRows = new ArrayList<>();

        if (conditions.isEmpty()) {
            int numRows = table.getColumns().get(0).getLength();
            for (int i = 0; i < numRows; i++) {
                selectedRows.add(i);
            }
            return selectedRows;
        }
        String columnName = conditions.getColumn();
        String operator = conditions.getOperator().toUpperCase();
        String conditionVal = conditions.getConditionValue();

        if (!table.hasColumn(columnName)){
            throw new IllegalArgumentException("Column '" + columnName + "' does not exist in the table.");
        }
        Column conditionedColumn = table.getColumns().get(table.getColumnIndex(columnName));

        for (int i = 0; i < table.getColumns().get(0).getLength(); i++) {
            String valueToCompare = conditionedColumn.getContents().get(i);
            if (checkIfRowSatisfiesCondition(valueToCompare, operator, conditionVal)) {
                selectedRows.add(i);
            }
        }
        return selectedRows;
    }

    public static boolean checkIfRowSatisfiesCondition(String columnVal, String operator, String condVal) {
        boolean isNum = isNumeric(columnVal) && isNumeric(condVal);

        if (isNum) {
            double cellDoubleValue = Double.parseDouble(columnVal);
            double targetDoubleValue = Double.parseDouble(condVal);

            return switch (operator) {
                case "=" -> cellDoubleValue == targetDoubleValue;
                case "!=", "NOT" -> cellDoubleValue != targetDoubleValue;
                case ">" -> cellDoubleValue > targetDoubleValue;
                case "<" -> cellDoubleValue < targetDoubleValue;
                case "<=" -> cellDoubleValue <= targetDoubleValue;
                case ">=" -> cellDoubleValue >= targetDoubleValue;
                default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
            };
        } else {
            return switch (operator) {
                case "=" -> columnVal.equals(condVal);
                case "!=", "NOT" -> !columnVal.equals(condVal);
                default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
            };
        }
    }

    public static boolean isNumeric(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void printTableToFile(String tableName, List<Column> columns, Table table) throws FileHandlingError{
        MyWriter myWriter = new MyWriter(tableName + ".txt", false);

        StringBuilder tempString = new StringBuilder();
        for (Column column : columns){
            tempString.append(column.getName()).append("\t");
        }
        myWriter.writeLine(tempString.toString());
        myWriter.writeLine("\n");

        for (int i = 0; i < columns.get(0).getLength(); i++) {
            int columnIndex = 0;
            StringBuilder row = new StringBuilder();
            for (Column column : table.getColumns()) {
                row.append(column.getContents().get(i));
                if (columnIndex < columns.size() - 1) {
                    row.append(", ");
                }
                columnIndex++;
            }
            row.append("\n");
            myWriter.writeLine(row.toString());
        }
        myWriter.close();
    }

    public static Condition extractCondition(String query){
        String regex = "(?i)\\bWHERE\\b\\s*(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(query);

        if (matcher.find()) {
            String condition = matcher.group(1).trim();
            String[] splitCondition = condition.split("\\s+");
            return new Condition(splitCondition[0], splitCondition[1], splitCondition[2]);
        } else {
            return new Condition();
        }
    }
}

