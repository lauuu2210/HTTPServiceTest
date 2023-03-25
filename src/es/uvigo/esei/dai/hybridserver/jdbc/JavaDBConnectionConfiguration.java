package es.uvigo.esei.dai.hybridserver.jdbc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class JavaDBConnectionConfiguration extends AbstractConnectionConfiguration implements ConnectionConfiguration {
	private String subSubProtocol;
	private String dbName;
	private Map<String, String> attributes;

	public JavaDBConnectionConfiguration() {
		this(null, null, null, null);
	}
	
	public JavaDBConnectionConfiguration(
		String userName,
		String password,
		String subsubprotocol,
		String dbName
	) {
		super(userName, password);
		
		this.setSubSubProtocol(subsubprotocol);
		this.setDbName(dbName);
		this.attributes = new HashMap<String, String>();
	}
	
	public String getSubSubProtocol() {
		return subSubProtocol;
	}

	public void setSubSubProtocol(String subSubProtocol) {
		this.subSubProtocol = subSubProtocol;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	
	public Map<String, String> getAttributes() {
		return Collections.unmodifiableMap(this.attributes);
	}
	
	public String putAttribute(String attribute, String value) {
		return this.attributes.put(attribute, value);
	}
	
	public String getAttribute(String attribute) {
		return this.attributes.get(attribute);
	}
	
	public String removeAttribute(String attribute) {
		return this.attributes.remove(attribute);
	}

	@Override
	public String getConnectionString() {
		final StringBuilder sb = new StringBuilder("jdbc:derby:");
		
		if (this.getSubSubProtocol() != null)
			sb.append(this.getSubSubProtocol()).append(':');
		
		if (this.getDbName() != null)
			sb.append(this.getDbName());
		
		for (Map.Entry<String, String> attribute : this.getAttributes().entrySet()) {
			sb.append(';').append(attribute.getKey())
				.append('=').append(attribute.getValue());
		}
		
	    return sb.toString();
	}

}
