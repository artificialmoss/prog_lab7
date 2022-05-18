import log.Log;
import org.apache.logging.log4j.Logger;
import run.CommandManager;

/**
 * Server main class, contains main method
 * @author Ryzhova Evgenia
 */
public class Server {
    public static void main(String[] args) {
        String filepath = System.getenv("LAB5_PATH"); //для гелиоса
        //String filepath = "server\\src\\main\\resources\\collection.json"; //для идеи
        //String filepath = "src\\main\\resources\\collection.json"; //для джарников

        int defaultPort = 1312;
        int port;

        Logger logger = Log.getLogger();

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
                logger.info("Port set to " + port + ".");
            } catch (NumberFormatException e) {
                port = defaultPort;
                logger.info("Invalid port format. Port set to default value (" + port + ").");
            }
        } else {
            port = defaultPort;
            logger.info("No port specified. Port set to default value (" + port +").");
        }

        CommandManager commandManager = new CommandManager(filepath, port);

        logger.trace("Valid server commands: " +
                "\"save\" saves the collection, " +
                "\"exit\" closes the application. " +
                "CTRL + D executes save and exit consequently.");

        commandManager.run();
    }
}
