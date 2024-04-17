import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class MyWriter {
    private BufferedWriter writer;

    public MyWriter(String filePath, boolean append) {
        try {
            this.writer = new BufferedWriter(new FileWriter(filePath, append));
        } catch (IOException e) {
        e.printStackTrace();
        }
    }

    public void writeLine(String line) {
        try{
            writer.write(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
