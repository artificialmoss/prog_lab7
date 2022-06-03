package configuration;

import configuration.exceptions.NoUserInfoException;
import log.Log;

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
        } catch (NoUserInfoException e) {
            try {
                username = reader.readUsername();
                password = reader.readPassword();
            } catch (NoUserInfoException e2) {
                Log.getLogger().fatal("Couldn't read user info to access the database. " +
                        "The application will be closed.");
                System.exit(-1);
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
