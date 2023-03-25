package es.uvigo.esei.dai.hybridserver.xslt;

import java.util.ArrayList;

public interface XsltDAO {
	public Xslt create(String uuid, String content, String xsl);

	public Xslt delete(String id);

	public Xslt get(String uuid);
	
	public String getXsd(String uuid);

	public String getString(String uuid);

	public ArrayList<String> lista();

	public String listaString();
}
