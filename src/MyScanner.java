import java.util.Scanner;

public class MyScanner {
    private final Scanner scanner;

    public MyScanner() {
        this.scanner = new Scanner(System.in);
    }

    public String nextLine() {
        return scanner.nextLine();
    }

    public void close() {
        scanner.close();
    }
}
