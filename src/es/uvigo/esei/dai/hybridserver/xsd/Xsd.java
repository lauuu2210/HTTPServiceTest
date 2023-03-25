package es.uvigo.esei.dai.hybridserver.xsd;

public class Xsd {
	
	String uuid;
	String content;
	
	public Xsd(String uuid, String content) {
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
