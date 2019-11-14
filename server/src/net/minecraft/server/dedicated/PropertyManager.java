package net.minecraft.server.dedicated;

import net.minecraft.logging.Log;

import java.io.*;
import java.util.Properties;

public class PropertyManager {

	private static final Log LOGGER = Log.MAIN;

	/**
	 * The server properties object.
	 */
	private final Properties serverProperties = new Properties();

	/**
	 * The server properties file.
	 */
	private final File serverPropertiesFile;

	public PropertyManager(File propertiesFile) {
		this.serverPropertiesFile = propertiesFile;

		if (propertiesFile.exists()) {

			try (FileInputStream fileinputstream = new FileInputStream(propertiesFile)) {
				this.serverProperties.load(fileinputstream);
			} catch (Exception exception) {
				LOGGER.error("Failed to load " + propertiesFile, exception);
				generateNewProperties();
			}
		} else {
			LOGGER.warn(propertiesFile + " does not exist");
			this.generateNewProperties();
		}
	}

	/**
	 * Generates a new properties file.
	 */
	public void generateNewProperties() {
		LOGGER.info("Generating new properties file");
		this.saveProperties();
	}

	/**
	 * Writes the properties to the properties file.
	 */
	public void saveProperties() {
		FileOutputStream fileoutputstream = null;

		try {
			fileoutputstream = new FileOutputStream(this.serverPropertiesFile);
			this.serverProperties.store(fileoutputstream, "Minecraft server properties");
		} catch (Exception exception) {
			LOGGER.error("Failed to save " + this.serverPropertiesFile, exception);
			generateNewProperties();
		} finally {
			if (fileoutputstream != null) {
				try {
					fileoutputstream.close();
				} catch (IOException ignored) {
				}
			}
		}
	}

	/**
	 * Returns this PropertyManager's file object used for property saving.
	 */
	public File getPropertiesFile() {
		return this.serverPropertiesFile;
	}

	/**
	 * Returns a string property. If the property doesn't exist the default is returned.
	 */
	public String getStringProperty(String key, String defaultValue) {
		if (!this.serverProperties.containsKey(key)) {
			this.serverProperties.setProperty(key, defaultValue);
			this.saveProperties();
			this.saveProperties();
		}

		return this.serverProperties.getProperty(key, defaultValue);
	}

	/**
	 * Gets an integer property. If it does not exist, set it to the specified value.
	 */
	public int getIntProperty(String key, int defaultValue) {
		try {
			return Integer.parseInt(this.getStringProperty(key, "" + defaultValue));
		} catch (Exception var4) {
			this.serverProperties.setProperty(key, "" + defaultValue);
			this.saveProperties();
			return defaultValue;
		}
	}

	public long getLongProperty(String key, long defaultValue) {
		try {
			return Long.parseLong(this.getStringProperty(key, "" + defaultValue));
		} catch (Exception var5) {
			this.serverProperties.setProperty(key, "" + defaultValue);
			this.saveProperties();
			return defaultValue;
		}
	}

	/**
	 * Gets a boolean property. If it does not exist, set it to the specified value.
	 */
	public boolean getBooleanProperty(String key, boolean defaultValue) {
		try {
			return Boolean.parseBoolean(this.getStringProperty(key, "" + defaultValue));
		} catch (Exception var4) {
			this.serverProperties.setProperty(key, "" + defaultValue);
			this.saveProperties();
			return defaultValue;
		}
	}

	/**
	 * Saves an Object with the given property name.
	 */
	public void setProperty(String key, Object value) {
		this.serverProperties.setProperty(key, "" + value);
	}

}
