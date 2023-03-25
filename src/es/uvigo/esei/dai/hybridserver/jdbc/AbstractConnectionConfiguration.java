package es.uvigo.esei.dai.hybridserver.jdbc;

import java.io.File;
import java.util.Properties;

public abstract class AbstractConnectionConfiguration implements ConnectionConfiguration {
	public final static File DIR_SQL = new File("sql");
	
	private String userName;
	private String password;

	public AbstractConnectionConfiguration() {
		super();
	}

	public AbstractConnectionConfiguration(String userName, String password) {
		this.setUserName(userName);
		this.setPassword(password);
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public Properties getConnectionProperties() {
		final Properties properties = new Properties();
		
		if (this.userName != null)
			properties.put("user", this.userName);
		
		if (this.password != null)
			properties.put("password", this.password);
		
		return properties;
	}

}