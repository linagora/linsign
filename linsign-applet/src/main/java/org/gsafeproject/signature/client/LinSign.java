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
package org.gsafeproject.signature.client;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.linagora.linsign.applet.core.PinInputDialog;
import org.linagora.linsign.applet.core.SignatureModel;
import org.linagora.linsign.applet.core.SigningUtils;
import org.linagora.linsign.client.keystore.KeyStoreUtils;
import org.linagora.linsign.exceptions.KeystoreAccessException;

import eu.europa.esig.dss.token.KSPrivateKeyEntry;
import eu.europa.esig.dss.token.MSCAPISignatureToken;
import eu.europa.esig.dss.token.Pkcs11SignatureToken;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import eu.europa.esig.dss.wsclient.signature.DSSException_Exception;
import eu.europa.esig.dss.wsclient.signature.DigestAlgorithm;
import eu.europa.esig.dss.wsclient.signature.SignatureForm;
import eu.europa.esig.dss.wsclient.signature.SignatureLevel;
import eu.europa.esig.dss.wsclient.signature.SignaturePackaging;
import eu.europa.esig.dss.wsclient.signature.SignatureService;
import eu.europa.esig.dss.wsclient.signature.SignatureService_Service;
import eu.europa.esig.dss.wsclient.signature.WsChainCertificate;
import eu.europa.esig.dss.wsclient.signature.WsSignaturePolicy;

public class LinSign {

	private static final int FLAG_NONREPUDIATION = 1;
	private SignatureModel signModel = new SignatureModel();
	private SignatureController signController = new SignatureController(signModel);
	public List<KeyStore> ksl;
	private byte[] dataToSign;
	private byte[] signedData;

	private File fileToSign;
	private File signedFile;

	private SignatureTokenConnection tokenConnection;

	private SignatureLevel signatureLevel;
	private SignaturePackaging signaturePackaging;
	private SignatureForm signatureForm;
	private DigestAlgorithm signatureAlgo;
	private String browserType = null;
	private X509Certificate cert;

	private boolean isFileReady;

	public LinSign(String userAgent, String wsSignatureURL, String signaturePolicy)
			throws KeyStoreException, Exception {

		browserType = userAgent;
		System.out.println(browserType);
		System.out.println("wsLinSignURL = " + wsSignatureURL);
		// TODO : set for more browser type, chrome & Safari Mac

		// The keystore of Chrome is the same as MSIE on Windows
		if (browserType.equals("Chrome"))
			browserType = "MSIE";

		try {
			ksl = KeyStoreUtils.getBrowserKeyStore(browserType);

			for (KeyStore keystore : ksl) {
				for (Enumeration<String> list = keystore.aliases(); list.hasMoreElements();) {
					String alias = list.nextElement();
					if (keystore.isKeyEntry(alias)) {
						System.out.println(
								"------------------------------------------------------------------------------------------------------------------");
						System.out.println("Alias = " + alias);
						cert = (X509Certificate) keystore.getCertificate(alias);
						System.out.println("Serial number : " + cert.getSubjectDN());
					}
				}
			}

		} catch (KeystoreAccessException e) {
			e.printStackTrace();
		}

		// HV comt
		// set pdfMark for test
		PDFVisibleProperties pdfMarkInput = new PDFVisibleProperties();
		pdfMarkInput.setPdfSignatureX1(50);
		pdfMarkInput.setPdfSignatureY1(20);
		pdfMarkInput.setPdfSignaturePageNb(1);
		pdfMarkInput.setPdfSignatureText("Signé électroniquement par $signer_name en date du $signing_time.");

		Font fontSignatureMark = new Font("TIMES_ROMAN", 0, 4);
		pdfMarkInput.setPdfSignatureFont(fontSignatureMark);
		PdfMarkProperties(pdfMarkInput);

		try {
			fileToSign = File.createTempFile("fileToSign", ".pdf");
			signedFile = File.createTempFile("signedFile", ".pdf");

			FileOutputStream outputStream = new FileOutputStream(fileToSign);
			FileOutputStream outputData = new FileOutputStream(signedFile);

			outputStream.close();
			outputData.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		SignatureService_Service.setROOT_SERVICE_URL(wsSignatureURL);

		initParameters(signaturePolicy);
	}

	public byte[] getSignedData() {
		try {
			FileInputStream inputStream = new FileInputStream(signedFile);
			signedData = new byte[(int) signedFile.length()];
			inputStream.read(signedData);
			inputStream.close();
			isFileReady = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return signedData;
	}

	public void setDataToSign(byte[] inputData) {
		this.dataToSign = inputData;

		FileOutputStream outputStream;
		try {
			outputStream = new FileOutputStream(fileToSign);
			outputStream.write(dataToSign);
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initParameters(String policyReference) throws Exception, KeyStoreException {

		WsSignaturePolicy policy = SigningUtils.getSignaturePolicy(policyReference);
		System.out.println("SignatureFormat = " + policy.getSignatureFormat() + " Signature Lelvel = "
				+ policy.getSignatureLevel() + " Signature Packaging = " + policy.getSignaturePackaging());

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

		signModel.setFormat(signatureForm.toString());
		signModel.setPackaging(signaturePackaging);
		signModel.setLevel(signatureLevel.toString());
		signModel.setSignatureDigestAlgorithm(signatureAlgo);

		signModel.setSelectedFile(fileToSign);
		signModel.setTargetFile(signedFile);

		if (browserType.equals("MSIE") || (browserType.equals("Chrome")))
			tokenConnection = new MSCAPISignatureToken(new PinInputDialog(null));
		else if (browserType.contains("Firefox"))
			tokenConnection = new Pkcs11SignatureToken(null);

		signModel.setTokenConnection(tokenConnection);
	}

	public void PdfMarkProperties(PDFVisibleProperties pdfMark) {
		signModel.setPdfMarkProperties(pdfMark);
	}

	public Properties getProperties(String policyReference) {
		Properties policyProps = new Properties();
		String policyFile = null;

		if (policyReference.contains("PDF"))
			policyFile = "PDF-TS-CRL-CLIENT-SIGN.properties";
		else
			policyFile = "XADES-TS-CRL-CLIENT-SIGN.properties";

		try {
			InputStream is;
			is = Thread.currentThread().getContextClassLoader().getResourceAsStream(policyFile);
			policyProps.load(is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return policyProps;
	}

	public String checkCertificate(X509Certificate cert) throws DSSException_Exception {
		String checkCert = null;

		final SignatureService_Service signatureService_service = new SignatureService_Service();
		final SignatureService signatureServiceImplPort = signatureService_service.getSignatureServiceImplPort();

		WsChainCertificate wsCert = new WsChainCertificate();

		try {
			wsCert.setX509Certificate(cert.getEncoded());
		} catch (CertificateEncodingException e) {
			e.printStackTrace();
		}

		checkCert = signatureServiceImplPort.checkCertificate(wsCert);

		return checkCert;

	}

	public String setSelectedKey(int keyIndex) {
		X509Certificate selectedCert;
		String checkCertif = null;

		PrivateKeyEntry pkEntry = null;
		List<PrivateKeyEntry> listKeys = null;

		try {
			listKeys = this.getKeys();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if ((listKeys != null) && (keyIndex < listKeys.size())) {
			pkEntry = listKeys.get(keyIndex);
			cert = (X509Certificate) listKeys.get(keyIndex).getCertificate();
			System.out.println("Key for signature : " + cert.getSubjectDN());
			System.out.println(
					"------------------------------------------------------------------------------------------------------------------");

			System.out.println(cert.getIssuerDN());

			KSPrivateKeyEntry kspkEntry = new KSPrivateKeyEntry(pkEntry);
			signModel.setSelectedPrivateKey(kspkEntry);
		}

		selectedCert = cert;
		WsChainCertificate wsCert = new WsChainCertificate();

		try {
			wsCert.setX509Certificate(selectedCert.getEncoded());
		} catch (CertificateEncodingException e) {
			e.printStackTrace();
		}

		try {
			checkCertif = SigningUtils.checkCertificate(wsCert);
			System.out.println(checkCertif);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return checkCertif;
	}

	public void SignFile() {
		try {
			signController.signDocument();
			isFileReady = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<KeyStore> getKeyStore() {
		return ksl;
	}

	public List<PrivateKeyEntry> getKeys() throws KeyStoreException {
		List<PrivateKeyEntry> listKeys = new ArrayList<PrivateKeyEntry>();
		List<BigInteger> containsList = new ArrayList<BigInteger>();

		PrivateKeyEntry pkEntry;

		try {
			for (KeyStore keystore : ksl) {
				for (Enumeration<String> list = keystore.aliases(); list.hasMoreElements();) {
					String alias = list.nextElement();
					X509Certificate cert = (X509Certificate) keystore.getCertificate(alias);

					if (keystore.isKeyEntry(alias) && cert != null && cert.getKeyUsage() != null
							&& cert.getKeyUsage()[FLAG_NONREPUDIATION]) {
						pkEntry = (KeyStore.PrivateKeyEntry) keystore.getEntry(alias,
								new KeyStore.PasswordProtection(null));
						
						if (cert.getSerialNumber() != null && !containsList.contains(cert.getSerialNumber())) {
							containsList.add(cert.getSerialNumber());
							listKeys.add(pkEntry);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listKeys;
	}

	public boolean isFileReady() {
		return isFileReady;
	}
}