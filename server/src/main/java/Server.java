import log.Log;
import org.apache.logging.log4j.Logger;
import run.ServerManager;

/**
 * Server main class, contains main method
 * @author Ryzhova Evgenia
 */
public class Server {
    public static void main(String[] args) {
        //String userConfigPath = ".pgpass";
        String userConfigPath = "server\\src\\main\\resources\\.pgpass";
        // ssh -L5432:pg:5432 s336774@se.ifmo.ru -p2222

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

        ServerManager serverManager = new ServerManager(userConfigPath, port);

        logger.trace("Valid server commands: " +
                "\"exit\" or CTRL + D closes the application.");

        serverManager.run();
    }
}
