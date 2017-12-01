/**
 *  LinSign - Electronic signature application
 *  
 *  Copyright © 2008--2017 LINAGORA, www.linagora.com
 *  
 *  SPDX-License-Identifier: AGPL-3.0
 *  
 *  This file is part of LinSign.
 *  
 *  LinSign is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of
 *  the License, or (at your option) any later version.
 *  
 *  LinSign is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with LinSign.  If not, see <http://www.gnu.org/licenses/agpl.html>.
 */
package org.linagora.linsign.application.service;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.linagora.linsign.application.model.api.AbstractService;
import org.linagora.linsign.application.model.dao.Document;
import org.linagora.linsign.application.model.dao.Status;
import org.linagora.linsign.application.utility.PropertyFile;
import org.linagora.linsign.application.utility.UtilityLinSignAPP;
import org.linagora.linsign.application.utility.UtilityLinSignAPP.NoSpaceException;
import org.w3c.dom.DOMException;
import org.w3c.dom.NodeList;

public class ServiceIss implements AbstractService {

	private final static Logger LOGGER = Logger.getLogger(ServiceIss.class.getName());

	private final String configFileName = "issConfig.properties";

	private String webServiceIss;

	private final String getDocuments;
	private final String saveDocumentsAfterSignature;
	private final String saveSignatureCancel;
	private final String saveSignatureDone;
	private final String saveSignatureError;
	private final String saveSignPageAccessed;

	private final String targetNameSpace;
	private final String targetNameSpacePath;

	private final String clientToken;
	private final String clientId;
	private final String clientPassword;

	private final int CODE_ERROR_RANGE = Status.USER_END_PROCESS.getCode();

	private boolean statusErrorRecursive = false;

	private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

	public ServiceIss(String webServiceIss, String clientToken, String clientId, String clientPassword) {
		PropertyFile propertyFile = new PropertyFile();
		Properties prop = propertyFile.getPropValues(configFileName);

		// nameSpace = webServiceIss+targetNameSpace;

		this.getDocuments = prop.getProperty("getDocuments");
		this.saveDocumentsAfterSignature = prop.getProperty("saveDocumentsAfterSignature");
		this.saveSignatureCancel = prop.getProperty("saveSignatureCancel");
		this.saveSignatureDone = prop.getProperty("saveSignatureDone");
		this.saveSignatureError = prop.getProperty("saveSignatureError");
		this.saveSignPageAccessed = prop.getProperty("saveSignPageAccessed");
		this.targetNameSpace = prop.getProperty("targetNameSpace");
		this.targetNameSpacePath = prop.getProperty("targetNameSpacePath");

		this.clientId = clientId;
		this.clientPassword = clientPassword;
		this.clientToken = clientToken;

		this.webServiceIss = webServiceIss;
	}

	public String getWebServiceIss() {
		return webServiceIss;
	}

	public void setWebServiceIss(String webServiceIss) {
		this.webServiceIss = webServiceIss;
	}

	private SOAPMessage connectionSoap(String nameRequest) {
		return connectionSoap(nameRequest, null, null);
	}

	private SOAPMessage connectionSoap(String nameRequest, List<Document> myDocumentList, String errorMsg) {
		try {
			LOGGER.log(Level.INFO, "Call " + nameRequest);

			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			SOAPMessage mySOAPRequest;
			if (myDocumentList == null)
				mySOAPRequest = createSoapSimpleRequest(nameRequest, errorMsg);
			else
				mySOAPRequest = createSoapStockRequest(nameRequest, myDocumentList);

			SOAPMessage soapResponse = soapConnection.call(mySOAPRequest, webServiceIss);
			soapConnection.close();

			return soapResponse;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error SOAP ISS");
			e.printStackTrace();
		}

		return null;
	}

	private SOAPMessage createSoapStockRequest(String nameRequest, List<Document> myDocumentList) throws Exception {
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();
		SOAPPart soapPart = soapMessage.getSOAPPart();

		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration(targetNameSpace, targetNameSpacePath);

		// SOAP Body
		SOAPBody soapBody = envelope.getBody();
		SOAPElement soapBodyElem = soapBody.addChildElement(nameRequest, targetNameSpace);
		SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("clientId", targetNameSpace);
		soapBodyElem1.addTextNode(clientId);
		SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("clientPassword", targetNameSpace);
		soapBodyElem2.addTextNode(clientPassword);
		SOAPElement soapBodyElem3 = soapBodyElem.addChildElement("sigToken", targetNameSpace);
		soapBodyElem3.addTextNode(clientToken);

		for (Document doc : myDocumentList) {
			SOAPElement soapBodyElemDocument = soapBodyElem.addChildElement("documents", targetNameSpace);

			SOAPElement soapBodyElemDocumentTitle = soapBodyElemDocument.addChildElement("title", targetNameSpace);
			soapBodyElemDocumentTitle.addTextNode(doc.getTitle());

			SOAPElement soapBodyElemDocumentIssuer = soapBodyElemDocument.addChildElement("issuer", targetNameSpace);
			soapBodyElemDocumentIssuer.addTextNode(doc.getIssuer());

			Date date = formatter.parse(doc.getCreationDate().toString());
			SOAPElement soapBodyElemDocumentCreationDate = soapBodyElemDocument.addChildElement("creationDate",
					targetNameSpace);
			soapBodyElemDocumentCreationDate.addTextNode(formatter.format(date));

			SOAPElement soapBodyElemDocumentFileContent = soapBodyElemDocument.addChildElement("fileContent",
					targetNameSpace);
			soapBodyElemDocumentFileContent.addTextNode(DatatypeConverter.printBase64Binary(doc.getFileContent()));

			SOAPElement soapBodyElemDocumentReference = soapBodyElemDocument.addChildElement("reference",
					targetNameSpace);
			soapBodyElemDocumentReference.addTextNode(doc.getReference());
		}

		MimeHeaders headers = soapMessage.getMimeHeaders();
		headers.addHeader("SOAPAction", webServiceIss + nameRequest);

		soapMessage.saveChanges();
		return soapMessage;
	}

	private SOAPMessage createSoapSimpleRequest(String nameRequest, String msgError) throws Exception {
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();
		SOAPPart soapPart = soapMessage.getSOAPPart();

		// SOAP Envelope
		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration(targetNameSpace, targetNameSpacePath);

		// SOAP Body
		SOAPBody soapBody = envelope.getBody();
		SOAPElement soapBodyElem = soapBody.addChildElement(nameRequest, targetNameSpace);
		SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("clientId", targetNameSpace);
		soapBodyElem1.addTextNode(clientId);
		SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("clientPassword", targetNameSpace);
		soapBodyElem2.addTextNode(clientPassword);
		SOAPElement soapBodyElem3 = soapBodyElem.addChildElement("sigToken", targetNameSpace);
		soapBodyElem3.addTextNode(clientToken);
		if (nameRequest.equals(this.saveSignatureError)) {
			SOAPElement soapBodyElem4 = soapBodyElem.addChildElement("message", targetNameSpace);
			soapBodyElem4.addTextNode(msgError);
		}

		MimeHeaders headers = soapMessage.getMimeHeaders();
		headers.addHeader("SOAPAction", webServiceIss + nameRequest);

		soapMessage.saveChanges();
		return soapMessage;
	}

	@Override
	public List<Document> getFiles(String idSignature) {
		SOAPMessage message = null;
		try {
			List<Document> myDoc = new ArrayList<Document>();
			message = connectionSoap(getDocuments);

			SOAPBody body;

			body = message.getSOAPBody();

			NodeList mainNodeDocumentList = body.getElementsByTagName(targetNameSpace + ":getDocumentsResponse");

			for (int k = 0; k < mainNodeDocumentList.getLength(); k++) {
				NodeList nodeDocumentList = mainNodeDocumentList.item(k).getChildNodes();
				for (int j = 0; j < nodeDocumentList.getLength(); j++) {
					if (nodeDocumentList.item(j).getNodeName().equals(targetNameSpace + ":return")) {
						Document doc = new Document();
						NodeList nodeDocumentData = nodeDocumentList.item(j).getChildNodes();
						for (int i = 0; i < nodeDocumentData.getLength(); i++) {
							if (nodeDocumentData.item(i).getNodeName().contains("title")) {
								doc.setTitle(nodeDocumentData.item(i).getFirstChild().getNodeValue());
							} else if (nodeDocumentData.item(i).getNodeName().contains("issuer")) {
								doc.setIssuer(nodeDocumentData.item(i).getFirstChild().getNodeValue());
							} else if (nodeDocumentData.item(i).getNodeName().contains("creationDate")) {
								Date date = formatter.parse(nodeDocumentData.item(i).getFirstChild().getNodeValue());
								doc.setCreationDate(new Timestamp(date.getTime()));
							} else if (nodeDocumentData.item(i).getNodeName().contains("fileContent")) {
								String stringFile = nodeDocumentData.item(i).getFirstChild().getNodeValue();
								byte[] decodedValue = DatatypeConverter.parseBase64Binary(stringFile);
								UtilityLinSignAPP.memoryDiskSpace(decodedValue.length);
								doc.setFileContent(decodedValue);
							} else if (nodeDocumentData.item(i).getNodeName().contains("reference")) {
								doc.setReference(nodeDocumentData.item(i).getFirstChild().getNodeValue());
							}
						}
						myDoc.add(doc);
					}
				}
			}
			return myDoc;
		} catch (NoSpaceException e) {
			LOGGER.log(Level.SEVERE, "Error not enought space");
			e.printStackTrace();
		} catch (SOAPException e) {
			LOGGER.log(Level.SEVERE, "Error to parse getDocument response");
			e.printStackTrace();
		} catch (DOMException e) {
			LOGGER.log(Level.SEVERE, "Error to parse getDocument date response");
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (isError(message)) {
			LOGGER.log(Level.INFO, "Get file failed");
			sendStatus(idSignature, Status.ERROR_ISS_WS_GET_FILE_TO_SIGN);
		}
		return null;
	}

	@Override
	public void sendStatus(String idSignature, Status status) {
		SOAPMessage message;
		boolean errorStatus = false;

		if (status.equals(Status.PROCESS_START)) {
			message = connectionSoap(saveSignPageAccessed);
		} else if (status.equals(Status.PROCESS_END)) {
			message = connectionSoap(saveSignatureDone);
		} else if (status.equals(Status.USER_END_PROCESS)) {
			message = connectionSoap(saveSignatureCancel);
		} else if (status.getCode() >= CODE_ERROR_RANGE) {
			message = connectionSoap(saveSignatureError, null, status.getText());
			errorStatus = true;
		} else {
			LOGGER.log(Level.SEVERE, "Status service infos : " + status.getName() + " - (Done)");
			return;
		}

		if (errorStatus) {
			LOGGER.log(Level.SEVERE, "Can't connect to the server");
		} else if (message != null && isError(message)) {
			if (statusErrorRecursive == false) {
				LOGGER.log(Level.SEVERE, "Send status failed");
				sendStatus(idSignature, Status.INTERNAL_ERROR);
				statusErrorRecursive = true;
			} else {
				statusErrorRecursive = false;
			}
		}
	}

	@Override
	public void stockSignedFiles(String idSignature, List<Document> signedFiles) {
		SOAPMessage message = connectionSoap(saveDocumentsAfterSignature, signedFiles, null);

		if (message != null && isError(message)) {
			LOGGER.log(Level.SEVERE, "Stock signed files failed");
			sendStatus(idSignature, Status.ERROR_ISS_STOCK_SIGNED_FILE);
		}
	}

	private boolean isError(SOAPMessage message) {
		if (message == null) {
			LOGGER.log(Level.SEVERE, "Soap response message is null");
			return true;
		}

		SOAPBody body;
		try {
			body = message.getSOAPBody();

			if (body.hasFault()) {
				LOGGER.log(Level.SEVERE, "Status infos : - Error response");

				SOAPFault newFault = body.getFault();
				QName code = newFault.getFaultCodeAsQName();
				String string = newFault.getFaultString();
				String actor = newFault.getFaultActor();
				LOGGER.log(Level.SEVERE, "SOAP fault contains: ");
				LOGGER.log(Level.SEVERE, "  Fault code = " + code.toString());
				LOGGER.log(Level.SEVERE, "  Local name = " + code.getLocalPart());
				LOGGER.log(Level.SEVERE,
						"  Namespace prefix = " + code.getPrefix() + ", bound to " + code.getNamespaceURI());
				LOGGER.log(Level.SEVERE, "  Fault string = " + string);

				if (actor != null) {
					LOGGER.log(Level.SEVERE, "  Fault actor = " + actor);
				}
				Detail newDetail = newFault.getDetail();
				if (newDetail != null) {
					Iterator entries = newDetail.getDetailEntries();
					while (entries.hasNext()) {
						DetailEntry newEntry = (DetailEntry) entries.next();
						String value = newEntry.getValue();
						LOGGER.log(Level.SEVERE, "  Detail entry = " + value);
					}
				}
				return true;
			}
		} catch (SOAPException e) {
			e.printStackTrace();
		}

		return false;
	}

	@SuppressWarnings("unused")
	private void printSoapMessage(SOAPMessage mySOAPRequest) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			mySOAPRequest.writeTo(out);
			String strMsg = new String(out.toByteArray());
			System.out.println(strMsg);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to print soapMessage", e);
		}
	}
}
