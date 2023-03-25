package es.uvigo.esei.dai.hybridserver.jdbc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class MySQLConnectionConfiguration extends AbstractConnectionConfiguration implements ConnectionConfiguration {
	private String serverName;
	private String dbName;
	private int portNumber;
	private final Map<String, String> properties;

	public MySQLConnectionConfiguration() {
		this(null, null, null, null, -1);
	}
	
	public MySQLConnectionConfiguration(
		String userName, String password,
		String serverName, String dbName, int portNumber
	) throws IllegalArgumentException {
		super(userName, password);
		
		this.setServerName(serverName);
		this.setDbName(dbName);
		this.setPortNumber(portNumber);
		
		this.properties = new HashMap<String, String>();
	}
	
	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public int getPortNumber() {
		return portNumber;
	}
	
	public Map<String, String> getProperties() {
		return Collections.unmodifiableMap(this.properties);
	}
	
	public String putProperty(String property, String value) {
		return this.properties.put(property, value);
	}
	
	public String getProperty(String property) {
		return this.properties.get(property);
	}
	
	public String removeProperty(String property) {
		return this.properties.remove(property);
	}

	public void setPortNumber(int portNumber) 
	throws IllegalArgumentException {
		if (portNumber > 65535)
			throw new IllegalAccessError("Port number must be in range [0, 65535] or negative for disable");
		
		this.portNumber = portNumber;
	}

	@Override
	public String getConnectionString() {
		final StringBuilder sb = new StringBuilder("jdbc:mysql://");
		
		if (this.getServerName() != null)
			sb.append(this.getServerName());
		
		
		if (this.getPortNumber() >= 0)
			sb.append(':').append(this.getPortNumber());
		
		sb.append('/');
		
		if (this.getDbName() != null)
			sb.append(this.getDbName());
		
		boolean isFirst = true;
		for (Map.Entry<String, String> property : this.properties.entrySet()) {
			sb.append(isFirst ? '?' : '&');
			isFirst = false;
			
			sb.append(property.getKey()).append("=")
				.append(property.getValue());
		}
		
		return sb.toString();
	}
}
