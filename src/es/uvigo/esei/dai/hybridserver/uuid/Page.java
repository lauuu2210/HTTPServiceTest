package es.uvigo.esei.dai.hybridserver.uuid;

public class Page {
	
	String uuid;
	String content;
	
	public Page(String uuid, String content) {
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
