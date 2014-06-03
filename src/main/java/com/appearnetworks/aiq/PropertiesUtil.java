package com.appearnetworks.aiq;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Qambber Hussain, Appear Networks.
 */
public class PropertiesUtil {
    private static PropertiesUtil ourInstance = new PropertiesUtil();

    public static PropertiesUtil getInstance() {
        return ourInstance;
    }

    private PropertiesUtil() {
    }

    public Properties loadProperties(String propertiesPath) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(propertiesPath));
        return properties;
    }
}
