package utils;

import java.io.InputStream;
import java.util.Properties;

public class CProperties {
	private static Properties properties;
	private static String host = "";
	private static Integer port = null;
	private static String user = "";
	private static String password = "";
	private static String schema = null;

	static {
		InputStream input;
		properties = new Properties();
		try {
			input = CProperties.class.getClassLoader().getResourceAsStream("data.properties");
				properties.load(input);
				host = properties.getProperty("host");
				port = properties.getProperty("port") != null ? Integer.parseInt(properties.getProperty("port")) : null;
				user = properties.getProperty("user");
				password = properties.getProperty("password");
				schema = properties.getProperty("schema");
		} catch (Exception e) {
			CLogger.writeFullConsole("Error 1: CProperties.class", e);
		} finally {

		}
	}

	public static Properties getProperties() {
		return properties;
	}

	public static void setProperties(Properties properties) {
		CProperties.properties = properties;
	}

	public static String getHost() {
		return host;
	}

	public static void setHost(String host) {
		CProperties.host = host;
	}

	public static Integer getPort() {
		return port;
	}

	public static void setPort(Integer port) {
		CProperties.port = port;
	}

	public static String getUser() {
		return user;
	}

	public static void setUser(String user) {
		CProperties.user = user;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String password) {
		CProperties.password = password;
	}

	public static String getSchema() {
		return schema;
	}

	public static void setSchema(String schema) {
		CProperties.schema = schema;
	}

}
