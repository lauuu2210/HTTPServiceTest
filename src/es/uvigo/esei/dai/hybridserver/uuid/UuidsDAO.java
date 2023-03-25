package es.uvigo.esei.dai.hybridserver.uuid;

import java.util.ArrayList;

public interface UuidsDAO {
	public Page create(Page page);

	public Page delete(String id);

	public Page get(String uuid);

	public String getString(String uuid);

	public ArrayList<String> lista();

	public String listaString();
}
