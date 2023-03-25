package es.uvigo.esei.dai.hybridserver.xml;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import es.uvigo.esei.dai.hybridserver.HybridServerService;
import es.uvigo.esei.dai.hybridserver.ServerConfiguration;
import es.uvigo.esei.dai.hybridserver.uuid.UuidDBDAO;

public class XmlController {

	List<ServerConfiguration> servers;
	int servicePort;
	XmlDBDAO bd;

	private Map<String, String> list;
	private int numServers;

	public XmlController(List<ServerConfiguration> servers, int servicePort, XmlDBDAO bd) {
		this.servers = servers;
		this.bd = bd;
		this.list = new HashMap<>();
		this.servicePort = servicePort;

		if (servers != null) {
			for (ServerConfiguration config : servers) {
				if (!config.getName().equals("Down Server")) {
					list.put(config.getName(), config.getWsdl());
				}
			}

			numServers = list.size();
		}
	}

	public Xml create(String uuid, String content) {
		return bd.create(uuid, content);
	}

	public Xml delete(String id) {
		return bd.delete(id);
	}

	public Xml get(String uuid) {
		return bd.get(uuid);
	}

	public String getString(String uuid) {
		String content = "";

		if (bd.getString(uuid) != "") {
			content = bd.getString(uuid);
		} else {
			int i = 0;
			boolean found = false;

			Service webService = null;
			String response = "";

			while (!found && i < numServers) {
				ServerConfiguration selection = servers.get(i);
				QName name = new QName("http://hybridserver.dai.esei.uvigo.es/",
						selection.getService() + "ImplService");
				try {
					webService = Service.create(new URL(selection.getWsdl()), name);

				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				HybridServerService hybridServer = webService.getPort(HybridServerService.class);

				try {
					response = hybridServer.getXml(uuid);
				} catch (Exception t) {
					// t.printStackTrace();
				}

				if (!response.equals("")) {
					content = response;
					found = true;
				}
				i++;

			}
		}

		return content;
	}

	public ArrayList<String> lista() {
		return bd.lista();
	}

	public String listaString() {
		HybridServerService hybridServer;
		String toret = "<html><head></head><body><p>Local Server</p>";

		toret += bd.listaString();

		for (int i = 0; i < numServers; i++) {
			System.out.println("entra");

			toret += "<p>Server" + i + "</p>";
			ServerConfiguration selection = servers.get(i);

			hybridServer = connection(selection.getWsdl(), selection.getService());

			toret += hybridServer.listXml(servicePort);
		}

		return toret + "</body></html>";
	}

	public HybridServerService connection(String urlString, String service) {
		HybridServerService toret = null;
		try {
			URL url = new URL(urlString);
			QName name = new QName("http://hybridserver.dai.esei.uvigo.es/", service + "ImplService");
			Service webService = Service.create(url, name);

			toret = webService.getPort(HybridServerService.class);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return toret;
	}
}
