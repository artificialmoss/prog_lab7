package configuration;

import configuration.exceptions.NoUserInfoException;
import log.Log;
import org.apache.logging.log4j.Logger;
import utils.GetFile;
import utils.exceptions.NoFileException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigFileLoader implements GetFile {
    private final String userConfigPath;
    private String username;
    private String password;
    private final Logger logger = Log.getLogger();

    public ConfigFileLoader(String userConfigPath) {
        this.userConfigPath = userConfigPath;
    }

    public ConfigFileLoader loadConfig() {
        try {
            getReadableFile(userConfigPath);
            String[] res = new String(Files.readAllBytes(Paths.get(userConfigPath)),
                    StandardCharsets.UTF_8).split(":");
            username = res[3];
            password = res[4];
            return this;
        } catch (NoFileException e) {
            logger.error("No readable config file found. Will try to read user info from console.");
            throw new NoUserInfoException();
        } catch (IOException e) {
            logger.error("Can't read the config file. Will try to read user info from console.");
            throw new NoUserInfoException();
        }
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}
