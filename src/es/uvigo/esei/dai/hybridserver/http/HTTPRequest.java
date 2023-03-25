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
package es.uvigo.esei.dai.hybridserver.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HTTPRequest {

	HTTPRequestMethod method;
	String resourceChain = "";
	String[] resourcePath;
	String resourceName = "";
	Map<String, String> resourceParameters;
	String httpVersion = "";
	Map<String, String> headerParameters;
	String content;
	String contentCode;

	public HTTPRequest(Reader reader) throws IOException, HTTPParseException {

		headerParameters = new LinkedHashMap<>();
		resourceParameters = new LinkedHashMap<>();

		BufferedReader in = new BufferedReader(reader);
		String line;
		StringBuilder cadena = new StringBuilder();
		while ((line = in.readLine()) != null) {
			if (!line.trim().isEmpty()) {
				cadena.append(line + "\r\n");
			}
		}
		in.close();
		reader.close();
		if (cadena.toString().contains("POST")) {
			String[] text = cadena.toString().split("\r\n");
			content = text[text.length - 1];
		}
		String texto = cadena.toString().replace(": ", "\t");
		texto = texto.replace(" ", "\t");
		texto = texto.replace("\r\n", "\t");

		String[] words = texto.split("\t");
		// System.out.println(words[0]);
		switch (words[0]) {
		case "POST":
			parsePost(words);
			break;

		case "GET":
			parseGet(words);
			break;
		case "DELETE":
			parseDelete(words);
			break;
		default:
			throw new HTTPParseException();
		}
	}

	private void parseGet(String[] words) throws HTTPParseException {
		method = HTTPRequestMethod.GET;

		resourceChain = words[1];

		if (!words[2].contains("HTTP")) {
			throw new HTTPParseException();
		} else {
			httpVersion = words[2];
		}
		for (int i = 0; i < words.length; i++) {
			if (words[i].equals("Host")) {
				headerParameters.put("Host", words[i + 1]);
			}
			if (words[i].equals("Accept")) {
				headerParameters.put("Accept", words[i + 1]);
			}
			if (words[i].equals("Accept-Encoding")) {
				headerParameters.put("Accept-Encoding", words[i + 1]);
			}
		}
		if (headerParameters.get("Host").equals("Accept") || headerParameters.get("Host").equals("Accept-Encoding")) {
			throw new HTTPParseException();
		}

		String aux = resourceChain.replace("?", "\t");
		String[] stg = aux.split("\t");
		resourceName = stg[0].replaceFirst("/", "");

		if (!(resourceName.length() == 0)) {
			String[] path = resourceName.split("/");
			resourcePath = new String[path.length];
			resourcePath = path;
		} else {
			resourcePath = new String[0];
		}

		if (stg.length > 1) {
			String parameters = stg[1].replace("&", "\t");
			String[] parameter = parameters.split("\t");
			String[] insert = new String[2];
			for (int i = 0; i < parameter.length; i++) {
				insert = parameter[i].split("=");
				resourceParameters.put(insert[0], insert[1]);
			}
		}

	}

	private void parsePost(String[] words) throws HTTPParseException, UnsupportedEncodingException {
		method = HTTPRequestMethod.POST;

		resourceChain = words[1];

		if (!words[2].contains("HTTP")) {
			throw new HTTPParseException();
		} else {
			httpVersion = words[2];
		}
		for (int i = 0; i < words.length; i++) {
			if (words[i].equals("Host")) {
				headerParameters.put("Host", words[i + 1]);
			}
			if (words[i].equals("Content-Type")) {
				headerParameters.put("Content-Type", words[i + 1]);
			}
			if (words[i].equals("Content-Length")) {
				headerParameters.put("Content-Length", words[i + 1]);
			}
		}
		if (headerParameters.get("Host").equals("Content-Type")
				|| headerParameters.get("Host").equals("Content-Length")) {
			throw new HTTPParseException();
		}

		String aux = resourceChain.replace("?", "\t");
		String[] stg = aux.split("\t");
		resourceName = stg[0].replaceFirst("/", "");

		if (!(resourceName.length() == 0)) {
			String[] path = resourceName.split("/");
			resourcePath = new String[path.length];
			resourcePath = path;
		} else {
			resourcePath = new String[0];
		}

		if (Integer.parseInt(headerParameters.get("Content-Length")) == 0) {
			content = null;
		} else {
			content = java.net.URLDecoder.decode(content, "UTF-8");
			String parameters = content.replace("&", "\t");
			String[] parameter = parameters.split("\t");
			String[] insert = new String[2];
			for (int i = 0; i < parameter.length; i++) {
				insert = parameter[i].split("=");
				resourceParameters.put(insert[0], insert[1]);
			}
		}

	}

	private void parseDelete(String[] words) throws HTTPParseException {
		method = HTTPRequestMethod.DELETE;

		resourceChain = words[1];

		if (!words[2].contains("HTTP")) {
			throw new HTTPParseException();
		} else {
			httpVersion = words[2];
		}
		for (int i = 0; i < words.length; i++) {
			if (words[i].equals("Host")) {
				headerParameters.put("Host", words[i + 1]);
			}
			if (words[i].equals("Accept")) {
				headerParameters.put("Accept", words[i + 1]);
			}
			if (words[i].equals("Accept-Encoding")) {
				headerParameters.put("Accept-Encoding", words[i + 1]);
			}
		}
		if (headerParameters.get("Host").equals("Accept") || headerParameters.get("Host").equals("Accept-Encoding")) {
			throw new HTTPParseException();
		}

		String aux = resourceChain.replace("?", "\t");
		String[] stg = aux.split("\t");
		resourceName = stg[0].replaceFirst("/", "");

		if (!(resourceName.length() == 0)) {
			String[] path = resourceName.split("/");
			resourcePath = new String[path.length];
			resourcePath = path;
		} else {
			resourcePath = new String[0];
		}

		if (stg.length > 1) {
			String parameters = stg[1].replace("&", "\t");
			String[] parameter = parameters.split("\t");
			String[] insert = new String[2];
			for (int i = 0; i < parameter.length; i++) {
				insert = parameter[i].split("=");
				resourceParameters.put(insert[0], insert[1]);
			}
		}
	}

	public HTTPRequestMethod getMethod() {
		return method;
	}

	public String getResourceChain() {
		return resourceChain;
	}

	public String[] getResourcePath() {
		return resourcePath;
	}

	public String getResourceName() {
		return resourceName;
	}

	public Map<String, String> getResourceParameters() {
		return resourceParameters;
	}

	public String getHttpVersion() {
		return httpVersion;
	}

	public Map<String, String> getHeaderParameters() {
		return headerParameters;
	}

	public String getContent() {
		if (content == null) {
			return null;
		}
		return content;
	}

	public int getContentLength() {
		if (headerParameters.get("Content-Length") == null) {
			return 0;
		}
		return Integer.parseInt(headerParameters.get("Content-Length"));
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(this.getMethod().name()).append(' ').append(this.getResourceChain())
				.append(' ').append(this.getHttpVersion()).append("\r\n");

		for (Map.Entry<String, String> param : this.getHeaderParameters().entrySet()) {
			sb.append(param.getKey()).append(": ").append(param.getValue()).append("\r\n");
		}

		if (this.getContentLength() > 0) {
			sb.append("\r\n").append(this.getContent());
		}

		return sb.toString();
	}
}
