package es.uvigo.esei.dai.hybridserver.xml;

public class Xml {
	
	String uuid;
	String content;
	
	public Xml(String uuid, String content) {
		this.uuid = uuid;
		this.content = content;
	}
	
	public String getUuid() {
		return this.uuid;
	}
	
	public String getContent() {
		return this.content;
	}
	
	public void setUuid(String newUuid) {
		this.uuid = newUuid;
	}
	
	public void setContent(String newContent) {
		this.content = newContent;
	}
	

}
