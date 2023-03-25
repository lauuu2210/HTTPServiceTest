package es.uvigo.esei.dai.hybridserver.jdbc;

import java.util.Properties;

public interface ConnectionConfiguration {
	public String getConnectionString();
	public Properties getConnectionProperties();
}
