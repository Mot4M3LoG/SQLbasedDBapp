import java.util.ArrayList;
import java.util.List;

public class Column {
    private final String name;
    private final List<String> contents;

    public Column(String name) {
        this.name = name;
        this.contents = new ArrayList<>();
    }

    public Column(Column column, String name){
        this.name = name;
        this.contents = column.getContents();
    }

    public String getName() {
        return name;
    }

    public List<String> getContents() {
        return contents;
    }

    public int getLength() { return contents.size();}

    public void addValue(String value){
        contents.add(value);
    }
    public void clearContents(){
        contents.clear();
    }
}
