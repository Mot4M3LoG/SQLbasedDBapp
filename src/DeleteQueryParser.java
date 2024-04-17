import java.util.List;

public class DeleteQueryParser{
    public static void processDeleteQuery(String query, Database DB) throws IllegalArgumentException {
        try {
            String regex = "(?i)\\bFROM\\s+(\\w+)\\b";
            String tableName = DistinguishQuery.extractTableName(query, regex);
            Table table = DB.getTableByName(tableName);

            Condition condition = DistinguishQuery.extractCondition(query);
            List<Integer> selectedRows = DistinguishQuery.applyConditions(table, condition);

            updateTableData(selectedRows, table);
            DistinguishQuery.printTableToFile(tableName, table.getColumns(), table);
        } catch(FileHandlingError e){
            System.out.println("Error: " + e.getMessage());
        } catch(IllegalArgumentException e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private static void updateTableData(List<Integer> selectedRows, Table table){
        Table tempTable = new Table(table.getName());

        for (Column column : table.getColumns()){
            Column tempColumn = new Column(column.getName());
            for(int i = 0; i < column.getLength(); i++){
                if(!selectedRows.contains(i)){
                    tempColumn.addValue(column.getContents().get(i));
                }
            }
            tempTable.addColumn(tempColumn);
        }

        table.getColumns().clear();
        for (Column column : tempTable.getColumns()){
            table.addColumn(column);
        }
    }
}
