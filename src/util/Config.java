package util;

import java.io.IOException;
import java.util.Properties;

/**
 * Configuration file loader. Loads the properties from given CONFIG_FILE_NAME.
 */
public class Config {
    /**
     * The file name of the configuration file.
     */
    private static final String CONFIG_FILE_NAME = "config.cfg";
    /**
     * The Configuration properties.
     */
    private final Properties properties;

    /**
     * Load configuration file.
     *
     * @throws IOException If there is a problem loading the file.
     */
    public Config() throws IOException {
        properties = new java.util.Properties();
        properties.load(this.getClass().getClassLoader().
                getResourceAsStream(CONFIG_FILE_NAME));
    }

    /**
     * Get a property from the configuration file.
     *
     * @param key The key of the property in the file.
     * @return The property as a string.
     */
    public String getProperty(String key) {
        return this.properties.getProperty(key);
    }

    /**
     * Get a property as a int from the configuration file.
     *
     * @param key The key of the property in the file.
     * @return The property as a int.
     * @throws NumberFormatException If the property does not contain a parsable integer.
     */
    public int getIntProperty(String key) {
        return Integer.parseInt(getProperty(key));
    }
}