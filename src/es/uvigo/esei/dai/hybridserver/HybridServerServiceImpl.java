package es.uvigo.esei.dai.hybridserver;

import javax.jws.WebService;

import es.uvigo.esei.dai.hybridserver.uuid.UuidsDAO;
import es.uvigo.esei.dai.hybridserver.xml.XmlDAO;
import es.uvigo.esei.dai.hybridserver.xsd.XsdDAO;
import es.uvigo.esei.dai.hybridserver.xslt.XsltDAO;

@WebService(endpointInterface = "es.uvigo.esei.dai.hybridserver.HybridServerService")

public class HybridServerServiceImpl implements HybridServerService {

	UuidsDAO bd;
	XmlDAO base;
	XsdDAO baseXSD;
	XsltDAO baseXSLT;

	public HybridServerServiceImpl(UuidsDAO dao, XmlDAO base, XsdDAO baseXSD, XsltDAO baseXSLT) {
		this.bd = dao;
		this.base = base;
		this.baseXSD = baseXSD;
		this.baseXSLT = baseXSLT;
	}

	@Override
	public String getHtml(String uuid) {

		return bd.get(uuid).getContent();
	}

	@Override
	public String getXml(String uuid) {
		return base.get(uuid).getContent();
	}

	@Override
	public String getXsd(String uuid) {
		return baseXSD.get(uuid).getContent();
	}

	@Override
	public String getXsdXslt(String uuid) {
		return baseXSLT.get(uuid).getXsd();
	}

	@Override
	public String getXslt(String uuid) {
		return baseXSLT.get(uuid).getContent();
	}

	@Override
	public String listHtml(int servicePort) {
		return bd.listaString();
	}

	@Override
	public String listXml(int servicePort) {
		return base.listaString();
	}

	@Override
	public String listXsd(int servicePort) {
		return baseXSD.listaString();
	}

	@Override
	public String listXslt(int servicePort) {
		return baseXSLT.listaString();
	}

}
