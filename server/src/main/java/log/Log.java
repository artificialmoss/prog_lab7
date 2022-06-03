package log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class that stores the logger used by the server
 */
public class Log {
    private final static Logger logger = LogManager.getRootLogger();

    public static Logger getLogger() {
        return logger;
    }
}
