package es.uvigo.esei.dai.hybridserver;

import javax.jws.WebService;

import java.util.ArrayList;

import javax.jws.WebMethod;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)

public interface HybridServerService {

	public String getHtml(String uuid);

	public String getXml(String uuid);

	public String getXsd(String uuid);

	public String getXsdXslt(String uuid);

	public String getXslt(String uuid);

	public String listHtml(int servicePort);

	public String listXml(int servicePort);

	public String listXsd(int servicePort);

	public String listXslt(int servicePort);
}
