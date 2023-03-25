package es.uvigo.esei.dai.hybridserver.xml;


import java.util.ArrayList;

public interface XmlDAO {
	public Xml create(String uuid, String content);

	public Xml delete(String id);

	public Xml get(String uuid);
	
	public String getString(String uuid);
	
	public ArrayList<String> lista();
	
	public String listaString();
}

