package utilities.properties;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class PropertiesUtils {

    private String path;

    private static PropertiesUtils INSTANCE;

    private PropertiesUtils() {}

    public static PropertiesUtils getInstance() {
        if (INSTANCE == null)
            INSTANCE = new PropertiesUtils();

        return INSTANCE;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPropertyValue(final String key) {
        String value = null;
        final File configFile = new File(path);

        try {
            final FileReader reader = new FileReader(configFile);
            final Properties props = new Properties();
            props.load(reader);

            value = props.getProperty(key);

            reader.close();
        } catch (final FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
        return value;
    }

    public void createProperty(final String key, final String value) {
        final File configFile = new File(path);

        try {
            final FileReader reader = new FileReader(configFile);
            final Properties props = new Properties();
            props.load(reader);

            props.setProperty(key, value);

            final FileWriter writer = new FileWriter(configFile);
            props.store(writer, "Application Settings");
            writer.close();
        } catch (final FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }
}