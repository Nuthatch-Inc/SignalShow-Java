package signals.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Configuration manager for SignalShow user preferences.
 * Handles loading and saving application settings to a properties file.
 * Configuration file is stored in the user's home directory as
 * .signalshow.properties
 */
public class Config {

  private static final String CONFIG_FILENAME = ".signalshow.properties";
  private static final String CONFIG_COMMENT = "SignalShow User Preferences";

  private Properties properties;
  private File configFile;

  /**
   * Create a new Config instance and load existing preferences if available
   */
  public Config() {
    properties = new Properties();

    // Set default values
    setDefaults();

    // Determine config file location (user's home directory)
    String userHome = System.getProperty("user.home");
    configFile = new File(userHome, CONFIG_FILENAME);

    // Load existing configuration if it exists
    load();
  }

  /**
   * Set default values for all configuration options
   */
  private void setDefaults() {
    // Default 1-D graph format: "line" or "area"
    properties.setProperty("default.graph1d.format", "line");

    // Default 2-D display part: "real", "imaginary", "magnitude", "phase"
    properties.setProperty("default.graph2d.part", "magnitude");

    // Default window width and height
    properties.setProperty("default.window.width", "1200");
    properties.setProperty("default.window.height", "800");

    // Default export format: "tiff", "png", "jpg", "bmp", "txt", "csv"
    properties.setProperty("default.export.format", "tiff");
  }

  /**
   * Load configuration from file if it exists
   */
  public void load() {
    if (configFile.exists()) {
      try (FileInputStream input = new FileInputStream(configFile)) {
        properties.load(input);
        System.out.println("Loaded configuration from: " + configFile.getAbsolutePath());
      } catch (IOException e) {
        System.err.println("Error loading configuration: " + e.getMessage());
      }
    } else {
      System.out.println("No existing configuration file found. Using defaults.");
    }
  }

  /**
   * Save current configuration to file
   */
  public void save() {
    try (FileOutputStream output = new FileOutputStream(configFile)) {
      properties.store(output, CONFIG_COMMENT);
      System.out.println("Saved configuration to: " + configFile.getAbsolutePath());
    } catch (IOException e) {
      System.err.println("Error saving configuration: " + e.getMessage());
    }
  }

  /**
   * Get a string property value
   * 
   * @param key Property key
   * @return Property value, or null if not found
   */
  public String getString(String key) {
    return properties.getProperty(key);
  }

  /**
   * Get a string property value with a default
   * 
   * @param key          Property key
   * @param defaultValue Default value if key not found
   * @return Property value, or defaultValue if not found
   */
  public String getString(String key, String defaultValue) {
    return properties.getProperty(key, defaultValue);
  }

  /**
   * Set a string property value
   * 
   * @param key   Property key
   * @param value Property value
   */
  public void setString(String key, String value) {
    properties.setProperty(key, value);
  }

  /**
   * Get an integer property value
   * 
   * @param key          Property key
   * @param defaultValue Default value if key not found or not a valid integer
   * @return Property value as integer, or defaultValue if not found/invalid
   */
  public int getInt(String key, int defaultValue) {
    String value = properties.getProperty(key);
    if (value != null) {
      try {
        return Integer.parseInt(value);
      } catch (NumberFormatException e) {
        System.err.println("Invalid integer value for key '" + key + "': " + value);
      }
    }
    return defaultValue;
  }

  /**
   * Set an integer property value
   * 
   * @param key   Property key
   * @param value Property value
   */
  public void setInt(String key, int value) {
    properties.setProperty(key, String.valueOf(value));
  }

  /**
   * Get a boolean property value
   * 
   * @param key          Property key
   * @param defaultValue Default value if key not found
   * @return Property value as boolean, or defaultValue if not found
   */
  public boolean getBoolean(String key, boolean defaultValue) {
    String value = properties.getProperty(key);
    if (value != null) {
      return Boolean.parseBoolean(value);
    }
    return defaultValue;
  }

  /**
   * Set a boolean property value
   * 
   * @param key   Property key
   * @param value Property value
   */
  public void setBoolean(String key, boolean value) {
    properties.setProperty(key, String.valueOf(value));
  }

  /**
   * Get the default 1-D graph format
   * 
   * @return "line" or "area"
   */
  public String getDefault1DGraphFormat() {
    return getString("default.graph1d.format", "line");
  }

  /**
   * Set the default 1-D graph format
   * 
   * @param format "line" or "area"
   */
  public void setDefault1DGraphFormat(String format) {
    setString("default.graph1d.format", format);
  }

  /**
   * Get the default 2-D display part
   * 
   * @return "real", "imaginary", "magnitude", or "phase"
   */
  public String getDefault2DPart() {
    return getString("default.graph2d.part", "magnitude");
  }

  /**
   * Set the default 2-D display part
   * 
   * @param part "real", "imaginary", "magnitude", or "phase"
   */
  public void setDefault2DPart(String part) {
    setString("default.graph2d.part", part);
  }

  /**
   * Get the default export format
   * 
   * @return File extension like "tiff", "png", "csv", etc.
   */
  public String getDefaultExportFormat() {
    return getString("default.export.format", "tiff");
  }

  /**
   * Set the default export format
   * 
   * @param format File extension like "tiff", "png", "csv", etc.
   */
  public void setDefaultExportFormat(String format) {
    setString("default.export.format", format);
  }
}
