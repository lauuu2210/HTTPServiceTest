package es.uvigo.esei.dai.hybridserver.xsd;

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

public class XsdController {

	List<ServerConfiguration> servers;
	int servicePort;
	XsdDBDAO bd;

	private Map<String, String> list;
	private int numServers;

	public XsdController(List<ServerConfiguration> servers, int servicePort, XsdDBDAO bd) {
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

	public Xsd create(String uuid, String content) {
		return bd.create(uuid, content);
	}

	public Xsd delete(String id) {
		return bd.delete(id);
	}

	public Xsd get(String uuid) {
		return bd.get(uuid);
	}

	public String getString(String uuid) {
		String content = "";

		if (bd.getString(uuid) != "") {
			content = bd.getString(uuid);
		} else {
			int i = 0;
			boolean found = false;

			while (i < numServers && found == false) {
				ServerConfiguration selection = servers.get(i);
				Service webService = null;
				QName name = new QName("http://hybridserver.dai.esei.uvigo.es/",
						selection.getService() + "ImplService");
				try {
					webService = Service.create(new URL(selection.getWsdl()), name);

				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				HybridServerService hybridServer = webService.getPort(HybridServerService.class);
				String response = "";
				try {
					response = hybridServer.getXsd(uuid);
				} catch (Exception t) {
					// t.printStackTrace();
				}

				if (!response.equals("")) {
					content = response;
					found = true;
				} else {
					i++;
				}

			}
		}

		return content;
	}

	public String listaString() {
		HybridServerService hybridServer;
		String toret = "<html><head></head><body><p>Local Server</p>";

		toret += bd.listaString();

		for (int i = 0; i < numServers; i++) {

			toret += "<p>Server" + i + "</p>";
			ServerConfiguration selection = servers.get(i);

			hybridServer = connection(selection.getWsdl(), selection.getService());

			toret += hybridServer.listXsd(servicePort);
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
