import java.util.ArrayList;
import java.util.List;

public class Table {
    private final String name;
    private final List<Column> columns;

    public Table(String name) {
        this.name = name;
        this.columns = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public boolean hasColumn(String searchedName){
        for (Column aColumn : columns){
            if (aColumn.getName().equals(searchedName)) {
                return true;
            }
        }
        return false;
    }

    public int getColumnIndex(String searchedName){
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).getName().equals(searchedName)) {
                return i;
            }
        }
        return -1;
    }

    public Column getColumnByName(String searchedName){
        for (Column aColumn : columns) {
            if (aColumn.getName().equals(searchedName)) {
                return aColumn;
            }
        }
        return null;
    }

    public void addColumn(Column column) {
        columns.add(column);
    }

    public int getColumnsNumber(){
        int i = 0;
        for (Column ignored : columns){
            i++;
        }
        return i;
    }

    // Add methods for modifying columns and processing queries as needed
}
