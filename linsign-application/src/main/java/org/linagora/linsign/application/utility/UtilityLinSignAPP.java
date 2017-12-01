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
package org.linagora.linsign.application.utility;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

import org.linagora.linsign.application.langue.SelectorLanguage;
import org.linagora.linsign.application.langue.Text;
import org.linagora.linsign.application.model.dao.Document;
import org.linagora.linsign.application.model.dao.MySignaturePolicy;
import org.linagora.linsign.application.signature.SigningUtils;
import org.linagora.linsign.sddss.core.PinInputDialog;
import org.linagora.linsign.sddss.dao.PDFVisibleProperties;

import eu.europa.esig.dss.token.MSCAPISignatureToken;
import eu.europa.esig.dss.token.Pkcs11SignatureToken;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import eu.europa.esig.dss.wsclient.signature.DigestAlgorithm;
import eu.europa.esig.dss.wsclient.signature.SignatureForm;
import eu.europa.esig.dss.wsclient.signature.SignatureLevel;
import eu.europa.esig.dss.wsclient.signature.SignaturePackaging;
import eu.europa.esig.dss.wsclient.signature.WsSignaturePolicy;

public class UtilityLinSignAPP {

	private final static Logger LOGGER = Logger.getLogger(UtilityLinSignAPP.class.getName());
	private final static String PDF_EXT = ".pdf";

	public static List<PDFVisibleProperties> createPdfMarkProperties(String[] pdfMark) {
		if (pdfMark == null) {
			LOGGER.info("No pdf mark properties");
		} else {
			// Desérialisé pdfMark
			try {
				List<PDFVisibleProperties> myPdfList = new ArrayList<PDFVisibleProperties>();
				for (String pdfString : pdfMark) {
					ObjectInputStream oisPdfMark = new ObjectInputStream(
							new ByteArrayInputStream(DatatypeConverter.parseBase64Binary(pdfString)));
					PDFVisibleProperties myPdf = (PDFVisibleProperties) oisPdfMark.readObject();
					myPdfList.add(myPdf);
				}
				return myPdfList;
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		return null;
	}

	public static MySignaturePolicy generatePolicy(String policyReference) throws Exception, KeyStoreException {
		try {
			WsSignaturePolicy policy = SigningUtils.getSignaturePolicy(policyReference);

			SignaturePackaging signaturePackaging = null;
			SignatureForm signatureForm = null;
			SignatureLevel signatureLevel = null;
			DigestAlgorithm signatureAlgo = null;

			if (policy.getSignatureFormat().contentEquals("XAdES"))
				signatureForm = SignatureForm.XAdES;
			else if (policy.getSignatureFormat().contentEquals("CAdES"))
				signatureForm = SignatureForm.CAdES;
			else if (policy.getSignatureFormat().contentEquals("PAdES"))
				signatureForm = SignatureForm.PAdES;

			if (policy.getSignaturePackaging().contentEquals("ENVELOPED"))
				signaturePackaging = SignaturePackaging.ENVELOPED;
			else if (policy.getSignaturePackaging().contentEquals("ENVELOPING"))
				signaturePackaging = SignaturePackaging.ENVELOPING;
			else if (policy.getSignaturePackaging().contentEquals("DETACHED"))
				signaturePackaging = SignaturePackaging.DETACHED;

			if (policy.getSignatureLevel().contentEquals("B")) {
				if (policy.getSignatureFormat().contentEquals("XAdES"))
					signatureLevel = SignatureLevel.XAdES_BASELINE_B;
				else if (policy.getSignatureFormat().contentEquals("CAdES"))
					signatureLevel = SignatureLevel.CAdES_BASELINE_B;
				else if (policy.getSignatureFormat().contentEquals("PAdES"))
					signatureLevel = SignatureLevel.PAdES_BASELINE_B;
			} else if (policy.getSignatureLevel().contentEquals("T")) {
				if (policy.getSignatureFormat().contentEquals("XAdES"))
					signatureLevel = SignatureLevel.XAdES_BASELINE_T;
				else if (policy.getSignatureFormat().contentEquals("CAdES"))
					signatureLevel = SignatureLevel.CAdES_BASELINE_T;
				else if (policy.getSignatureFormat().contentEquals("PAdES"))
					signatureLevel = SignatureLevel.PAdES_BASELINE_T;
			} else if (policy.getSignatureLevel().contentEquals("LT")) {
				if (policy.getSignatureFormat().contentEquals("XAdES"))
					signatureLevel = SignatureLevel.XAdES_BASELINE_LT;
				else if (policy.getSignatureFormat().contentEquals("CAdES"))
					signatureLevel = SignatureLevel.CAdES_BASELINE_LT;
				else if (policy.getSignatureFormat().contentEquals("PAdES"))
					signatureLevel = SignatureLevel.PAdES_BASELINE_LT;
			} else if (policy.getSignatureLevel().contentEquals("LTA")) {
				if (policy.getSignatureFormat().contentEquals("XAdES"))
					signatureLevel = SignatureLevel.XAdES_BASELINE_LTA;
				else if (policy.getSignatureFormat().contentEquals("CAdES"))
					signatureLevel = SignatureLevel.CAdES_BASELINE_LTA;
				else if (policy.getSignatureFormat().contentEquals("PAdES"))
					signatureLevel = SignatureLevel.PAdES_BASELINE_LTA;
			}

			if (policy.getSignatureAlgorithm().contentEquals("SHA1"))
				signatureAlgo = DigestAlgorithm.SHA1;
			else if (policy.getSignatureAlgorithm().contentEquals("SHA256"))
				signatureAlgo = DigestAlgorithm.SHA256;
			else if (policy.getSignatureAlgorithm().contentEquals("SHA512"))
				signatureAlgo = DigestAlgorithm.SHA512;

			/*
			 * 
			 * signatureForm =
			 * SignatureForm.valueOf(policy.getSignatureFormat());
			 * signatureLevel = SignatureLevel.valueOf(strSignatureLevel);
			 * signatureAlgo =
			 * DigestAlgorithm.valueOf(policy.getSignatureAlgorithm());
			 * signaturePackaging =
			 * SignaturePackaging.valueOf(policy.getSignaturePackaging());
			 */
			return new MySignaturePolicy(signaturePackaging, signatureForm, signatureLevel, signatureAlgo);

		} catch (Exception e) {
			LOGGER.info("Error to parse enum for policy");
			// TODO Manage when some value of policy is not coresponding to enum
			// value
			LOGGER.log(Level.SEVERE, e.getMessage(), e);

		}
		return null;
	}

	public static SignatureTokenConnection generateTokenConnection(String userAgent) {
		if (userAgent.equals("MSIE") || (userAgent.equals("Chrome")))
			return new MSCAPISignatureToken(new PinInputDialog(null));
		else if (userAgent.contains("Firefox"))
			return new Pkcs11SignatureToken(null);
		return null; // TODO userAgent not suported
	}

	public static void writeFileTest(Document doc) {
		FileOutputStream fileOuputStream = null;
		try {
			File file = File.createTempFile(doc.getTitle().replaceAll("\\s", ""), PDF_EXT);
			fileOuputStream = new FileOutputStream(file);
			fileOuputStream.write(doc.getFileContent());

		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			try {
				fileOuputStream.close();
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}

	public static File writeFile(Document doc) {
		String path = System.getProperty("java.io.tmpdir");
		path += "/" + doc.getTitle().replaceAll("\\s", "") + PDF_EXT;

		FileOutputStream fileOuputStream = null;
		File file = null;
		try {
			file = new File(path);
			file.deleteOnExit();

			fileOuputStream = new FileOutputStream(file);
			fileOuputStream.write(doc.getFileContent());

			System.out.println("File create to :" + path);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			try {
				fileOuputStream.close();
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		return file;
	}

	public static File writeFilePreview(Document doc) throws IOException {
		FileOutputStream fileOuputStream = null;
		File file = null;
		try {
			file = File.createTempFile(doc.getTitle().replaceAll("\\s", ""), PDF_EXT);
			fileOuputStream = new FileOutputStream(file);
			fileOuputStream.write(doc.getFileContent());
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			try {
				fileOuputStream.close();
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		return file;
	}

	public static String infosCert(X509Certificate userCertificateSelected, SelectorLanguage langue, String alias) {
		DateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy");

		String result = langue.printText(Text.SELECT_CERTIFICAT) + " : ";
		result += "\n\t " + langue.printText(Text.NAME_CN) + " : " + alias;
		result += "\n\t " + langue.printText(Text.ID_CERTIFICAT) + " : " + userCertificateSelected.getIssuerDN();
		result += "\n\t " + langue.printText(Text.VALIDITY_CERTIFICAT) + " : "
				+ targetFormat.format(userCertificateSelected.getNotBefore());
		result += " " + langue.printText(Text.SEPERATOR_DATE) + " "
				+ targetFormat.format(userCertificateSelected.getNotAfter());
		result += "\n\t " + langue.printText(Text.SERIAL_NUMBER) + " : " + userCertificateSelected.getSerialNumber();
		result += "\n\t " + langue.printText(Text.CERTIFICATE_ID) + " : " + userCertificateSelected.getSubjectDN();

		return result;
	}

	public static boolean memoryDiskSpace(long expecteFiledSize) throws NoSpaceException {
		try {
			File f = File.createTempFile("sizeManager", ".txt");
			f.deleteOnExit();
			if (expecteFiledSize > f.getUsableSpace())
				throw new NoSpaceException("Not enough space");

			return true;
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return false;
	}

	public static class NoSpaceException extends Exception {
		public NoSpaceException(String message) {
			super(message);
		}
	}
}
