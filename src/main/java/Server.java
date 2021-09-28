import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import utilites.DatabaseCollectionManager;
import utilites.DatabaseManager;
import utilites.DatabaseUserManager;
import utilites.Decryptor;

import java.util.Scanner;

public class Server {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Драйвер управления базой данных не найден!");
            System.exit(0);
        }
        DatabaseManager databaseManager = new DatabaseManager();
        DatabaseUserManager databaseUserManager = new DatabaseUserManager(databaseManager);
        DatabaseCollectionManager databaseCollectionManager = new DatabaseCollectionManager(databaseManager, databaseUserManager);
        System.out.println("Enter the PORT: ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        int port = 0;
        try {
            port = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            logger.warn("Wrong PORT - Server.main()");
            System.exit(1);
        }
        logger.info("Port is accepted - {}", Server.class.getSimpleName());
        Decryptor decryptor = new Decryptor(port, databaseUserManager, databaseCollectionManager);
    }
}
