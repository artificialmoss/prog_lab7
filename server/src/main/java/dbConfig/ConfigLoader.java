package dbConfig;

import dbConfig.exceptions.NoUserInfoException;
import log.Log;

import java.util.NoSuchElementException;

public class ConfigLoader {
    private String username;
    private String password;
    private final ConfigFileLoader loader;
    private final ConsoleConfigReader reader = new ConsoleConfigReader(10);

    public ConfigLoader(String configFilePath) {
        this.loader = new ConfigFileLoader(configFilePath);
    }

    public void getConfig() {
        try {
            loader.loadConfig();
            username = loader.getUsername();
            password = loader.getPassword();
        } catch (NoUserInfoException e) {
            try {
                username = reader.readUsername();
                password = reader.readPassword();
            } catch (NoUserInfoException e2) {
                Log.getLogger().fatal("Couldn't read user info to access the database. " +
                        "The application will be closed.");
                System.exit(-1);
            } catch (NoSuchElementException e3) {
                Log.getLogger().info("You have entered the end of file symbol. " +
                        "The application will be closed");
                System.exit(-2);
            }
        }
        Log.getLogger().info("User info has been found.");
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
