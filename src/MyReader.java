import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MyReader {
    private final BufferedReader reader;

    public MyReader(String filePath) {
        try {
            this.reader = new BufferedReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public String readRow(){
        try {
            String line = reader.readLine();
            if (line != null) {
                return line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
