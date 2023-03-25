package es.uvigo.esei.dai.hybridserver.xslt;

import es.uvigo.esei.dai.hybridserver.xsd.Xsd;

public class Xslt {
	
	String uuid;
	String content;
	String xsd;
	
	public Xslt(String uuid, String content, String xsd) {
		this.uuid = uuid;
		this.content = content;
		this.xsd = xsd;
	}
	
	public String getUuid() {
		return this.uuid;
	}
	
	public String getContent() {
		return this.content;
	}
	
	public String getXsd() {
		return this.xsd;
	}
	
	public void setUuid(String newUuid) {
		this.uuid = newUuid;
	}
	
	public void setContent(String newContent) {
		this.content = newContent;
	}
	
	public void setXsd(String newXsd) {
		this.xsd = newXsd;
	}
	

}
