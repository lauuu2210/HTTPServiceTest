
/**
 *  HybridServer
 *  Copyright (C) 2022 Miguel Reboiro-Jato
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.ws.Endpoint;

import es.uvigo.esei.dai.hybridserver.jdbc.ConnectionUtils;
import es.uvigo.esei.dai.hybridserver.jdbc.JavaDBConnectionConfiguration;
import es.uvigo.esei.dai.hybridserver.uuid.UuidDBDAO;
import es.uvigo.esei.dai.hybridserver.uuid.UuidsDAO;
import es.uvigo.esei.dai.hybridserver.xml.XmlDAO;
import es.uvigo.esei.dai.hybridserver.xml.XmlDBDAO;
import es.uvigo.esei.dai.hybridserver.xsd.XsdDAO;
import es.uvigo.esei.dai.hybridserver.xsd.XsdDBDAO;
import es.uvigo.esei.dai.hybridserver.xslt.XsltDAO;
import es.uvigo.esei.dai.hybridserver.xslt.XsltDBDAO;

public class HybridServer {

	private int service_port;
	private int nclients;
	private String db_url;
	private String db_user;
	private String db_password;
	private String webservice;
	private List<ServerConfiguration> servers = null;

	private Thread serverThread;
	private boolean stop;
	Properties properties;
	Configuration configuration;

	private Endpoint end;

	public HybridServer() {

		service_port = 8888;
		nclients = 50;
		db_url = "jdbc:mysql://localhost:3306/hstestdb";
		db_user = "hsdb";
		db_password = "hsdbpass";
	}

	public HybridServer(Configuration properties) {
		this.configuration = properties;
		service_port = properties.getHttpPort();
		nclients = properties.getNumClients();
		db_url = properties.getDbURL();
		db_user = properties.getDbUser();
		db_password = properties.getDbPassword();
		webservice = properties.getWebServiceURL();
		servers = properties.getServers();
	}

	public HybridServer(Properties properties) {
		this.properties = properties;
		service_port = Integer.parseInt((String) properties.get("port"));
		nclients = Integer.parseInt((String) properties.get("numClients"));
		db_url = (String) properties.get("db.url");
		db_user = (String) properties.get("db.user");
		db_password = (String) properties.get("db.password");
	}

	public void start() throws SQLException {
		


		UuidDBDAO bd = new UuidDBDAO(db_url, db_user, db_password, service_port);
		XmlDBDAO base = new XmlDBDAO(db_url, db_user, db_password);
		XsdDBDAO baseXSD = new XsdDBDAO(db_url, db_user, db_password);
		XsltDBDAO baseXSLT = new XsltDBDAO(db_url, db_user, db_password);

		if (webservice != null) {
			end = Endpoint.publish(webservice, new HybridServerServiceImpl(bd, base, baseXSD, baseXSLT));
		}

		this.serverThread = new Thread() {
			public void run() {
				try (ServerSocket serverSocket = new ServerSocket(service_port)) {
					final ExecutorService threadPool = Executors.newFixedThreadPool(nclients);
					while (true) {

						if (stop)
							break;
						threadPool.execute(new ServiceThread(serverSocket.accept(), bd, base, baseXSD, baseXSLT,
								servers, service_port));

					}
				} catch (IOException e) {
					System.err.println("Server socket could not be created");
				}
			}
		};

		this.stop = false;
		this.serverThread.start();
	}

	public void stop() {

		if (webservice != null) {
			end.stop();
		}

		this.stop = true;

		try (Socket socket = new Socket("localhost", service_port)) {
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try {
			this.serverThread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		this.serverThread = null;
	}

	public int getPort() {
		return service_port;
	}
}