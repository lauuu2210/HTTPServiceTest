package es.uvigo.esei.dai.hybridserver.xsd;


import java.util.ArrayList;

public interface XsdDAO {
	public Xsd create(String uuid, String content);

	public Xsd delete(String id);

	public Xsd get(String uuid);
	
	public String getString(String uuid);
	
	public ArrayList<String> lista();
	
	public String listaString();
}

