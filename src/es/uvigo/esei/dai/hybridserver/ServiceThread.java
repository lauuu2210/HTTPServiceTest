package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import es.uvigo.esei.dai.hybridserver.http.HTTPHeaders;
import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;
import es.uvigo.esei.dai.hybridserver.http.MIME;
import es.uvigo.esei.dai.hybridserver.uuid.Page;
import es.uvigo.esei.dai.hybridserver.uuid.UuidController;
import es.uvigo.esei.dai.hybridserver.uuid.UuidDBDAO;
import es.uvigo.esei.dai.hybridserver.xml.XmlController;
import es.uvigo.esei.dai.hybridserver.xml.XmlDBDAO;
import es.uvigo.esei.dai.hybridserver.xsd.XsdController;
import es.uvigo.esei.dai.hybridserver.xsd.XsdDBDAO;
import es.uvigo.esei.dai.hybridserver.xslt.XsltController;
import es.uvigo.esei.dai.hybridserver.xslt.XsltDBDAO;

public class ServiceThread implements Runnable {
	private final Socket socket;
	private List<ServerConfiguration> servers = null;
	int servicePort;
	UuidDBDAO bd;
	XmlDBDAO base;
	XsdDBDAO baseXSD;
	XsltDBDAO baseXSLT;

	public ServiceThread(Socket clientSocket, UuidDBDAO bd, XmlDBDAO base, XsdDBDAO baseXSD, XsltDBDAO baseXSLT,
			List<ServerConfiguration> servers, int servicePort) throws IOException {
		this.socket = clientSocket;
		this.servers = servers;
		this.servicePort = servicePort;
		this.bd = bd;
		this.base = base;
		this.baseXSD = baseXSD;
		this.baseXSLT = baseXSLT;
	}

	public void run() {

		HTTPResponse res = new HTTPResponse();

		try (Socket socket = this.socket) {
			try {

				UuidController baseHtml = new UuidController(servers, servicePort, bd);
				XmlController baseXml = new XmlController(servers, servicePort, base);
				XsdController baseXSDController = new XsdController(servers, servicePort, baseXSD);
				XsltController baseXSLTController = new XsltController(servers, servicePort, baseXSLT);

				OutputStream clientOutput;
				clientOutput = socket.getOutputStream();
				HTTPHeaders head = HTTPHeaders.HTTP_1_1;

				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String rslt = peticion(in);

				// System.out.println(rslt.toString());
				Reader read = new StringReader(rslt);
				HTTPRequest request = new HTTPRequest(read);

				// System.out.println(request.getResourceName());

				switch (request.getMethod()) {

				case GET:

					res.setVersion(head.getHeader());
					res.setStatus(HTTPResponseStatus.S400);
					switch (request.getResourceName()) {

					case "html":

						if ((baseHtml.getString(request.getResourceParameters().get("uuid"))) != "") {

							res.setContent(baseHtml.getString(request.getResourceParameters().get("uuid")));
							res.putParameter("Content-Type", "text/html");

							res.setStatus(HTTPResponseStatus.S200);

						} else if (request.getResourceParameters().isEmpty()) {

							res.setContent(baseHtml.listaString());
							res.setStatus(HTTPResponseStatus.S200);

						} else {

							res.setStatus(HTTPResponseStatus.S404);

						}

						break;

					case "xml":

						if (baseXml.getString(request.getResourceParameters().get("uuid")) != "") {

							if (request.getResourceChain().contains("&xslt=")) {
								String[] cadena = request.getResourceChain().split("&");

								String xslt = cadena[1].replace("xslt=", "");
								String xsd = baseXSLTController.getXsd(xslt);
								String contentXsd = baseXSDController.getString(xsd);
								String contentXslt = baseXSLTController.getString(xslt);
								String xml = request.getResourceParameters().get("uuid");
								String contentXml = baseXml.getString(xml);

//								System.out.println("Xsd asociado a xslt " + baseXSLTController.getXsd(xslt));
//								System.out.println("Contenido asociado a XSLT" + baseXSLTController.getString(xslt));
//								System.out.println("Contenido asociado al xsd" + baseXSDController.getString(xsd));
//
//								System.out.println("Xml " + request.getResourceParameters().get("uuid"));
//								System.out.println("Contenido Xml " + contentXml);

								System.out.println("");

								if (xsd == "") {
									res.setStatus(HTTPResponseStatus.S404);

								} else {
									if (validar(contentXsd, contentXml)) {
										try {
											res.setContent(transformar(contentXslt, contentXml));
										} catch (TransformerException e) {
											res.setStatus(HTTPResponseStatus.S404);
										} catch (SAXException e) {
											res.setStatus(HTTPResponseStatus.S404);
										} catch (ParserConfigurationException e) {
											res.setStatus(HTTPResponseStatus.S404);
										}
										res.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
										res.setStatus(HTTPResponseStatus.S200);
									} else {
										res.setStatus(HTTPResponseStatus.S400);
									}

								}

							} else {
								res.setContent(baseXml.getString(request.getResourceParameters().get("uuid")));
								res.putParameter("Content-Type", MIME.APPLICATION_XML.getMime());
								res.setStatus(HTTPResponseStatus.S200);
							}

						} else if (request.getResourceParameters().isEmpty()) {

							res.setContent(baseXml.listaString());
							res.setStatus(HTTPResponseStatus.S200);

						} else {

							res.setStatus(HTTPResponseStatus.S404);

						}
						break;

					case "xsd":
						if ((baseXSDController.getString(request.getResourceParameters().get("uuid"))) != "") {

							res.setContent(baseXSDController.getString(request.getResourceParameters().get("uuid")));
							res.putParameter("Content-Type", MIME.APPLICATION_XML.getMime());
							res.setStatus(HTTPResponseStatus.S200);

						} else if (request.getResourceParameters().isEmpty()) {

							res.setContent(baseXSDController.listaString());
							res.setStatus(HTTPResponseStatus.S200);

						} else {

							res.setStatus(HTTPResponseStatus.S404);

						}
						break;

					case "xslt":

						if ((baseXSLTController.getString(request.getResourceParameters().get("uuid"))) != "") {

							res.setContent(baseXSLTController.getString(request.getResourceParameters().get("uuid")));
							res.putParameter("Content-Type", "application/xml");
							res.setStatus(HTTPResponseStatus.S200);

						} else if (request.getResourceParameters().isEmpty()) {

							res.setContent(baseXSLTController.listaString());
							res.setStatus(HTTPResponseStatus.S200);

						} else {
							res.setStatus(HTTPResponseStatus.S404);

						}
						break;
					case "":
						res.setVersion(head.getHeader());
						res.setStatus(HTTPResponseStatus.S200);
						res.setContent("Hybrid Server");
						break;

					default:

						res.setVersion(head.getHeader());
						res.setStatus(HTTPResponseStatus.S400);
						break;
					}

					res.putParameter("Content-Language", "en");
					escribirRespuesta(res, clientOutput, in);
					break;

				case DELETE:

					res.setVersion(head.getHeader());

					switch (request.getResourceName()) {
					case "html":

						if (baseHtml.get(request.getResourceParameters().get("uuid")).getContent() != "") {
							res.setContent(baseHtml.delete(request.getResourceParameters().get("uuid")).getContent());
							res.setStatus(HTTPResponseStatus.S200);

						} else {
							res.setStatus(HTTPResponseStatus.S404);
						}

						break;
					case "xml":

						if (baseXml.get(request.getResourceParameters().get("uuid")).getContent() != "") {
							res.setContent(baseXml.delete(request.getResourceParameters().get("uuid")).getContent());
							res.setStatus(HTTPResponseStatus.S200);

						} else {
							res.setStatus(HTTPResponseStatus.S404);
						}

						break;

					case "xsd":

						if (baseXSDController.get(request.getResourceParameters().get("uuid")).getContent() != "") {
							res.setContent(
									baseXSDController.delete(request.getResourceParameters().get("uuid")).getContent());
							res.putParameter("Content-Type", "application/xml");
							res.setStatus(HTTPResponseStatus.S200);

						} else {
							res.setStatus(HTTPResponseStatus.S404);
						}

						break;

					case "xslt":

						if (baseXSLTController.get(request.getResourceParameters().get("uuid")).getContent() != "") {
							res.setContent(baseXSLT.delete(request.getResourceParameters().get("uuid")).getContent());
							res.putParameter("Content-Type", "application/xml");
							res.setStatus(HTTPResponseStatus.S200);

						} else {
							res.setStatus(HTTPResponseStatus.S404);
						}

						break;
					default:
						res.setStatus(HTTPResponseStatus.S400);
						break;

					}

					res.putParameter("Content-Language", "en");
					escribirRespuesta(res, clientOutput, in);
					break;

				case POST:

					res.setVersion(head.getHeader());

					switch (request.getResourceName()) {

					case "html":

						if (request.getContent().startsWith("html")) {
							UUID random = UUID.randomUUID();
							String uuid = random.toString();
							String link = "<a href=\"html?uuid=" + uuid + "\">" + uuid + "</a>";

							res.setContent(link);
							baseHtml.create(new Page(uuid, request.getContent().replace("html=", "")));
							res.setStatus(HTTPResponseStatus.S200);

						} else {
							res.setStatus(HTTPResponseStatus.S400);
						}

						res.putParameter("Content-Language", "en");
						escribirRespuesta(res, clientOutput, in);

						break;

					case "xml":

						if (request.getContent().startsWith("xml")) {
							UUID random = UUID.randomUUID();
							String uuid = random.toString();
							String link = "<a href=\"xml?uuid=" + uuid + "\">" + uuid + "</a>";

							res.setContent(link);
							baseXml.create(uuid, request.getContent().replace("xml=", ""));
							res.setStatus(HTTPResponseStatus.S200);

						} else {
							res.setStatus(HTTPResponseStatus.S400);
						}

						res.putParameter("Content-Language", "en");
						escribirRespuesta(res, clientOutput, in);

						break;

					case "xsd":

						if (request.getContent().startsWith("xsd")) {
							UUID random = UUID.randomUUID();
							String uuid = random.toString();
							String link = "<a href=\"xsd?uuid=" + uuid + "\">" + uuid + "</a>";

							res.setContent(link);
							baseXSDController.create(uuid, request.getContent().replace("xsd=", ""));
							res.setStatus(HTTPResponseStatus.S200);

						} else {
							res.setStatus(HTTPResponseStatus.S400);
						}

						res.putParameter("Content-Language", "en");
						escribirRespuesta(res, clientOutput, in);

						break;

					case "xslt":

						if (request.getContent().startsWith("xsd")) {

							String[] cadena = request.getContent().split("&");

							System.out.println(cadena[1].replace("xsd=", ""));

							if (baseXSLTController.get(cadena[0].replace("xsd=", "")).getContent() != "") {
								// System.out.println(baseXSLTController.getString(cadena[0].replace("xsd=",
								// "")));
								res.setStatus(HTTPResponseStatus.S404);
							} else {
								if (baseXSDController.get(cadena[0].replace("xsd=", "")).getContent() != "") {
									UUID random = UUID.randomUUID();
									String uuid = random.toString();
									String link = "<a href=\"xslt?uuid=" + uuid + "\">" + uuid + "</a>";

									res.setContent(link);
									baseXSLTController.create(uuid, cadena[1].replace("xslt=", ""),
											cadena[0].replace("xsd=", ""));
									res.setStatus(HTTPResponseStatus.S200);
								} else {
									res.setStatus(HTTPResponseStatus.S404);
								}

							}

						} else {

							res.setStatus(HTTPResponseStatus.S400);
						}
						res.putParameter("Content-Language", "en");

						escribirRespuesta(res, clientOutput, in);
						break;
					default:
						res.setStatus(HTTPResponseStatus.S400);
						res.putParameter("Content-Language", "en");
						break;
					}
				default:
					break;

				}
			} catch (IOException e) {
				e.printStackTrace();

			} catch (HTTPParseException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			System.err.println("Error en hilo de servicio: " + e.getMessage());
		}

	}

	private String peticion(BufferedReader in) throws IOException {
		String line;
		StringBuilder rslt = new StringBuilder();
		do {
			while ((line = in.readLine()) != null) {
				if (line.trim().isEmpty()) {
					break;
				}
				rslt.append(line + "\r\n");
			}

			line = "";
			int caracter = 0;
			while (in.ready() && caracter != -1) {

				caracter = in.read();
				rslt.append((char) caracter);
			}
		} while (rslt.length() <= 0);

		return rslt.toString();
	}

	private void escribirRespuesta(HTTPResponse res, OutputStream clientOutput, BufferedReader in) {
		try {
			clientOutput.write(res.toString().getBytes());
			clientOutput.flush();
			in.close();
			clientOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean validar(String xsd, String xml) throws SAXException, IOException {
		InputStream stream = new ByteArrayInputStream(xsd.getBytes(StandardCharsets.UTF_8));
		Source source = new StreamSource(stream);

		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemaFactory.newSchema(source);
		source = new StreamSource(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
		Validator validator = schema.newValidator();
		try {
			validator.validate(source);
			return true;
		} catch (SAXException e) {
			return false;
		}
	}

	private String transformar(String xslt, String xml)
			throws TransformerException, SAXException, ParserConfigurationException {

		String xmlTransf = "";
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer(new StreamSource(new StringReader(xslt)));
			StringWriter writer = new StringWriter();
			transformer.transform(new StreamSource(new StringReader(xml)), new StreamResult(writer));
			String transformedXml = writer.toString();
			return transformedXml;

		} catch (TransformerException e) {
			return xmlTransf;
		}

	}

}
