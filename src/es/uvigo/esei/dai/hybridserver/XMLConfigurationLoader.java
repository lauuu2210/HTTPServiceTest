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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XMLConfigurationLoader {
	public Configuration load(File xmlFile) throws SAXException, ParserConfigurationException, IOException {
		String path = "./configuration.xsd";
		Document configuration = parseConfiguration(xmlFile, path);
		return generateConfiguration(configuration);

	}

	private Document parseConfiguration(File xml, String path)
			throws SAXException, ParserConfigurationException, IOException {

		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemaFactory.newSchema(new File(path));

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(true);
		factory.setSchema(schema);

		DocumentBuilder builder = factory.newDocumentBuilder();
		builder.setErrorHandler(new SimpleErrorHandler());

		return builder.parse(xml);
	}

	private Configuration generateConfiguration(Document doc) throws SAXException {
		Configuration config = new Configuration();
		List<ServerConfiguration> servers = new ArrayList<>();
		try {

			config.setHttpPort(Integer.parseInt(doc.getElementsByTagName("http").item(0).getTextContent()));
			config.setWebServiceURL(doc.getElementsByTagName("webservice").item(0).getTextContent());
			config.setNumClients(Integer.parseInt(doc.getElementsByTagName("numClients").item(0).getTextContent()));
			config.setDbUser(doc.getElementsByTagName("user").item(0).getTextContent());
			config.setDbPassword(doc.getElementsByTagName("password").item(0).getTextContent());
			config.setDbURL(doc.getElementsByTagName("url").item(0).getTextContent());

			for (int i = 0; i < doc.getElementsByTagName("server").getLength(); i++) {

				ServerConfiguration sr = new ServerConfiguration(
						doc.getElementsByTagName("server").item(i).getAttributes().item(1).getNodeValue(),
						doc.getElementsByTagName("server").item(i).getAttributes().item(4).getNodeValue(),
						doc.getElementsByTagName("server").item(i).getAttributes().item(2).getNodeValue(),
						doc.getElementsByTagName("server").item(i).getAttributes().item(3).getNodeValue(),
						doc.getElementsByTagName("server").item(i).getAttributes().item(0).getNodeValue());

				servers.add(i, sr);
			}
			config.setServers(servers);
		} catch (Exception x) {
			x.printStackTrace();
		}

		return config;
	}

}
