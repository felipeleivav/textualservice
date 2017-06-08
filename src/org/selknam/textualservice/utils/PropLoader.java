package org.selknam.textualservice.utils;

import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropLoader {
	private static Logger logger = Logger.getLogger(PropLoader.class);
	
	private static Properties properties;
	
	public static void initialize() {
		try {
			String propName = "TextualService-cfg.properties";
			InputStream propFile = PropLoader.class.getClassLoader().getResourceAsStream(propName);
			properties = new Properties();
			properties.load(propFile);
			logger.debug("Loaded config: "+properties.toString());
		} catch (Exception e) {
			logger.error("Error initializing properties: ", e);
		}
	}
	
	public static String get(String key) {
		return properties.getProperty(key);
	}
	
}
