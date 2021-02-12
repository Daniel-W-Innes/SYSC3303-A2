package util;

import java.io.IOException;
import java.util.Properties;

/**
 * Configuration file loader. Loads the configFile from given configFileName.
 */
public class Config {
    private static final String configFileName = "config.cfg";
    private final Properties configFile;

    /**
     * Load configuration file.
     *
     * @throws IOException if there is a problem loading the file
     */
    public Config() throws IOException {
        configFile = new java.util.Properties();
        configFile.load(this.getClass().getClassLoader().
                getResourceAsStream(configFileName));

    }

    /**
     * Get a property from the configuration file.
     *
     * @param key The key of the property in the file
     * @return The property as a string
     */
    public String getProperty(String key) {
        return this.configFile.getProperty(key);
    }

    /**
     * Get a property as a int from the configuration file.
     *
     * @param key The key of the property in the file
     * @return The property as a int
     * @throws NumberFormatException  if the property does not contain a parsable integer.
     */
    public int getIntProperty(String key) {
        return Integer.parseInt(getProperty(key));
    }
}