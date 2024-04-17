public class Main {
    public static void main(String[] args) {
        Database TheDatabase = new Database();
        DatabaseLoader.loadDatabase(TheDatabase, "database.txt");

        System.out.println("Welcome to your database!\n");
        MyScanner terminalReader = new MyScanner();
        do {
            System.out.println("Enter your command:\n");
            String query = terminalReader.nextLine();
            String checkExit = DistinguishQuery.parseQuery(query, TheDatabase);
            if (checkExit.equals("EXIT")){
                break;
            }
        }while(true);
        terminalReader.close();
    }
}

