/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 *
 * This file is part of the "DSS - Digital Signature Services" project.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.esig.dss.ws.impl;

import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.cert.CRLException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import javax.jws.WebService;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.esig.dss.AbstractSignatureParameters;
import eu.europa.esig.dss.BLevelParameters;
import eu.europa.esig.dss.ChainCertificate;
import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.DSSUtils;
import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.EncryptionAlgorithm;
import eu.europa.esig.dss.Policy;
import eu.europa.esig.dss.SignatureLevel;
import eu.europa.esig.dss.SignatureValue;
import eu.europa.esig.dss.ToBeSigned;
import eu.europa.esig.dss.asic.ASiCSignatureParameters;
import eu.europa.esig.dss.cades.CAdESSignatureParameters;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.pades.SignatureImageParameters;
import eu.europa.esig.dss.pades.SignatureImageTextParameters;
import eu.europa.esig.dss.signature.DocumentSignatureService;
import eu.europa.esig.dss.signature.SignaturePackaging;
import eu.europa.esig.dss.ws.DSSWSUtils;
import eu.europa.esig.dss.ws.SignatureService;
import eu.europa.esig.dss.ws.WSChainCertificate;
import eu.europa.esig.dss.ws.WSDSSReference;
import eu.europa.esig.dss.ws.WSDocument;
import eu.europa.esig.dss.ws.WSParameters;
import eu.europa.esig.dss.ws.WSSignaturePolicy;
import eu.europa.esig.dss.ws.impl.LinsignLogs.Log;
import eu.europa.esig.dss.ws.utility.ConfigUtility;
import eu.europa.esig.dss.x509.CertificateToken;
import eu.europa.esig.dss.x509.SignatureForm;
import eu.europa.esig.dss.xades.DSSReference;
import eu.europa.esig.dss.xades.XAdESSignatureParameters;

/**
 * Implementation of the Interface for the Contract of the Signature Web
 * Service.
 *
 *
 */

@WebService(endpointInterface = "eu.europa.esig.dss.ws.SignatureService", serviceName = "SignatureService")
public class SignatureServiceImpl implements SignatureService {

	private static final Logger LOG = LoggerFactory.getLogger(SignatureServiceImpl.class);

	private static final Logger LSLOGS = LoggerFactory.getLogger("LinSignLogs");

	private DocumentSignatureService<XAdESSignatureParameters> xadesService;

	private DocumentSignatureService<CAdESSignatureParameters> cadesService;

	private DocumentSignatureService<PAdESSignatureParameters> padesService;

	private DocumentSignatureService<ASiCSignatureParameters> asicService;

	/**
	 * @param xadesService
	 *            the xadesService to set
	 */
	public void setXadesService(DocumentSignatureService<XAdESSignatureParameters> xadesService) {
		this.xadesService = xadesService;
	}

	/**
	 * @param cadesService
	 *            the cadesService to set
	 */
	public void setCadesService(DocumentSignatureService<CAdESSignatureParameters> cadesService) {
		this.cadesService = cadesService;
	}

	/**
	 * @param padesService
	 *            the padesService to set
	 */
	public void setPadesService(DocumentSignatureService<PAdESSignatureParameters> padesService) {
		this.padesService = padesService;
	}

	/**
	 * @param asicService
	 *            the asicService to set
	 */
	public void setAsicService(DocumentSignatureService<ASiCSignatureParameters> asicService) {
		this.asicService = asicService;
	}

	private DocumentSignatureService getServiceForSignatureLevel(final SignatureLevel signatureLevel) {
		SignatureForm signatureForm = signatureLevel.getSignatureForm();
		switch (signatureForm) {
		case XAdES:
			return xadesService;
		case CAdES:
			return cadesService;
		case PAdES:
			return padesService;
		case ASiC_E:
		case ASiC_S:
			return asicService;
		default:
			throw new IllegalArgumentException("Unrecognized format " + signatureLevel);
		}
	}

	private AbstractSignatureParameters createParameters(final WSParameters wsParameters) throws DSSException {
		if (wsParameters == null) {
			return null;
		}

		SignatureForm signatureForm = wsParameters.getSignatureLevel().getSignatureForm();
		AbstractSignatureParameters params = null;
		switch (signatureForm) {
		case XAdES:
			params = new XAdESSignatureParameters();
			break;
		case CAdES:
			params = new CAdESSignatureParameters();
			break;
		case PAdES:
			params = new PAdESSignatureParameters();
			setPDFVisibleMark(wsParameters, params);
			break;
		case ASiC_E:
		case ASiC_S:
			params = new ASiCSignatureParameters();
			break;
		default:
			throw new IllegalArgumentException("Unrecognized format " + signatureForm);
		}

		setSignatureLevel(wsParameters, params);

		setSignaturePackaging(wsParameters, params);

		setEncryptionAlgorithm(wsParameters, params);

		setDigestAlgorithm(wsParameters, params);

		setSigningDate(wsParameters, params);

		setSigningCertificateAndChain(wsParameters, params);

		setSignWithExpiredCertificate(wsParameters, params);

		setSignaturePolicy(wsParameters, params);

		setClaimedSignerRole(wsParameters, params);

		setContentIdentifierPrefix(wsParameters, params);
		setContentIdentifierSuffix(wsParameters, params);

		setCommitmentTypeIndication(wsParameters, params);

		setSignerLocation(wsParameters, params);

		// setPDFVisibleMark(wsParameters, params);

		if (SignatureForm.XAdES.equals(signatureForm)) {
			setSignedInfoCanonicalizationMethod(wsParameters, (XAdESSignatureParameters) params);
			setReferences(wsParameters, (XAdESSignatureParameters) params);
		}

		if (SignatureForm.ASiC_E.equals(signatureForm) || SignatureForm.ASiC_S.equals(signatureForm)) {
			setAsicSignatureForm(wsParameters, (ASiCSignatureParameters) params);
			setAsicMimeType(wsParameters, (ASiCSignatureParameters) params);
			setAsicZipComment(wsParameters, (ASiCSignatureParameters) params);
			setAsicEnclosedSignature(wsParameters, (ASiCSignatureParameters) params);
		}

		return params;
	}

	private void setPDFVisibleMark(WSParameters wsParameters, AbstractSignatureParameters params) {
		// Initialize visual signature
		final byte[] signingCertBytes = wsParameters.getSigningCertificateBytes();
		CertificateToken x509SigningCertificate = DSSUtils.loadCertificate(signingCertBytes);

		SignatureImageParameters imageParameters = new SignatureImageParameters();
		// the origin is the left and top corner of the page
		imageParameters.setxAxis(wsParameters.getPdfX()); // 30
		imageParameters.setyAxis(wsParameters.getPdfY()); // 780
		imageParameters.setPage(wsParameters.getPdfPage());

		SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss 'GMT' XXX");
		String strDate = sdfDate.format(wsParameters.getSigningDate());

		// Initialize text to generate for visual signature
		SignatureImageTextParameters textParameters = new SignatureImageTextParameters();

		Font fontPdfMark = null;
		String fontPath = null;
		if (wsParameters.getPdfFontName().contentEquals("Helvetica"))
			fontPath = "fonts/helvetica.ttf";
		else if (wsParameters.getPdfFontName().contentEquals("TimesRoman"))
			fontPath = "fonts/times-roman.ttf";
		else if (wsParameters.getPdfFontName().contentEquals("Verdana"))
			fontPath = "fonts/verdana.ttf";
		else if (wsParameters.getPdfFontName().contentEquals("Arial"))
			fontPath = "fonts/arial.ttf";
		else
			fontPath = "fonts/courier.ttf";

		try {
			InputStream dataFont = Thread.currentThread().getContextClassLoader().getResourceAsStream(fontPath);
			Font ttfBase = Font.createFont(Font.TRUETYPE_FONT, dataFont);
			fontPdfMark = ttfBase.deriveFont(wsParameters.getPdfFontStyle(), wsParameters.getPdfFontSize());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		textParameters.setFont(fontPdfMark);

		if (wsParameters.getPdfFontColor() != 0)
			textParameters.setTextColor(new Color(wsParameters.getPdfFontColor()));
		else
			textParameters.setTextColor(Color.BLACK);

		String pdfTextMark = wsParameters.getPdfMark();

		if (pdfTextMark.contains("$signer_name"))
			pdfTextMark = pdfTextMark.replace("$signer_name", x509SigningCertificate.getSubjectShortName());
		if (pdfTextMark.contains("$signing_time"))
			pdfTextMark = pdfTextMark.replace("$signing_time", strDate);
		textParameters.setText(pdfTextMark);

		// textParameters.setText(wsParameters.getPdfMark() +
		// x509SigningCertificate.getSubjectShortName() + " en date du " +
		// strDate);

		imageParameters.setTextParameters(textParameters);

		if (params instanceof PAdESSignatureParameters) {
			((PAdESSignatureParameters) params).setImageParameters(imageParameters);
			((PAdESSignatureParameters) params).setSignatureSize(9472 * 2);
		}
	}

	private void setSignaturePolicy(WSParameters wsParameters, AbstractSignatureParameters params) {
		final Policy signaturePolicy = wsParameters.getSignaturePolicy();
		params.bLevel().setSignaturePolicy(signaturePolicy);
	}

	private void setSignerLocation(WSParameters wsParameters, AbstractSignatureParameters params) {
		final BLevelParameters.SignerLocation signerLocation = wsParameters.getSignerLocation();
		params.bLevel().setSignerLocation(signerLocation);
	}

	private void setCommitmentTypeIndication(WSParameters wsParameters, AbstractSignatureParameters params) {
		final List<String> commitmentTypeIndication = wsParameters.getCommitmentTypeIndication();
		params.bLevel().setCommitmentTypeIndications(commitmentTypeIndication);
	}

	private void setContentIdentifierSuffix(WSParameters wsParameters, AbstractSignatureParameters params) {
		final String contentIdentifierSuffix = wsParameters.getContentIdentifierSuffix();
		params.bLevel().setContentIdentifierSuffix(contentIdentifierSuffix);
	}

	private void setContentIdentifierPrefix(WSParameters wsParameters, AbstractSignatureParameters params) {
		final String contentIdentifierPrefix = wsParameters.getContentIdentifierPrefix();
		params.bLevel().setContentIdentifierPrefix(contentIdentifierPrefix);
	}

	private void setSignedInfoCanonicalizationMethod(WSParameters wsParameters, XAdESSignatureParameters params) {
		final String signedInfoCanonicalizationMethod = wsParameters.getSignedInfoCanonicalizationMethod();
		params.setSignedInfoCanonicalizationMethod(signedInfoCanonicalizationMethod);
	}

	private void setEncryptionAlgorithm(WSParameters wsParameters, AbstractSignatureParameters params) {
		final EncryptionAlgorithm encryptionAlgorithm = wsParameters.getEncryptionAlgorithm();
		params.setEncryptionAlgorithm(encryptionAlgorithm);
	}

	private void setDigestAlgorithm(final WSParameters wsParameters, final AbstractSignatureParameters params) {
		final DigestAlgorithm digestAlgorithm = wsParameters.getDigestAlgorithm();
		params.setDigestAlgorithm(digestAlgorithm);
	}

	private void setClaimedSignerRole(final WSParameters wsParameters, final AbstractSignatureParameters params) {
		final List<String> claimedSignerRoles = wsParameters.getClaimedSignerRole();
		if (claimedSignerRoles != null) {
			for (final String claimedSignerRole : claimedSignerRoles) {
				params.bLevel().addClaimedSignerRole(claimedSignerRole);
			}
		}
	}

	private void setSigningCertificateAndChain(final WSParameters wsParameters,
			final AbstractSignatureParameters params) {
		final byte[] signingCertBytes = wsParameters.getSigningCertificateBytes();
		if (signingCertBytes == null) {
			return;
		}
		final CertificateToken x509SigningCertificate = DSSUtils.loadCertificate(signingCertBytes);
		params.setSigningCertificate(x509SigningCertificate);

		final List<ChainCertificate> chainCertificates = new ArrayList<ChainCertificate>();
		chainCertificates.add(new ChainCertificate(x509SigningCertificate, true));
		final List<WSChainCertificate> wsChainCertificateList = wsParameters.getChainCertificateList();
		if (CollectionUtils.isNotEmpty(wsChainCertificateList)) {
			for (final WSChainCertificate wsChainCertificate : wsChainCertificateList) {
				final CertificateToken x509Certificate = DSSUtils
						.loadCertificate(wsChainCertificate.getX509Certificate());
				final ChainCertificate chainCertificate = new ChainCertificate(x509Certificate,
						wsChainCertificate.isSignedAttribute());
				if (!chainCertificates.contains(chainCertificate)) {
					chainCertificates.add(chainCertificate);
				}
			}
		}
		params.setCertificateChain(chainCertificates);
	}

	/**
	 * Allows to change the default behaviour regarding the use of an expired
	 * certificate.
	 *
	 * @param wsParameters
	 * @param params
	 */
	private void setSignWithExpiredCertificate(final WSParameters wsParameters,
			final AbstractSignatureParameters params) {
		final boolean signWithExpiredCertificate = wsParameters.getSignWithExpiredCertificate();
		params.setSignWithExpiredCertificate(signWithExpiredCertificate);
	}

	private void setSigningDate(final WSParameters wsParameters, final AbstractSignatureParameters params) {
		final Date signingDate = wsParameters.getSigningDate();
		params.bLevel().setSigningDate(signingDate);
	}

	private void setSignaturePackaging(final WSParameters wsParameters, final AbstractSignatureParameters params) {
		final SignaturePackaging signaturePackaging = wsParameters.getSignaturePackaging();
		params.setSignaturePackaging(signaturePackaging);
	}

	private void setSignatureLevel(final WSParameters wsParameters, final AbstractSignatureParameters params) {
		final SignatureLevel signatureLevel = wsParameters.getSignatureLevel();
		params.setSignatureLevel(signatureLevel);
	}

	private void setReferences(final WSParameters wsParameters, final XAdESSignatureParameters params) {
		final List<WSDSSReference> wsReferences = wsParameters.getReferences();
		if (wsReferences == null) {
			return;
		}
		final List<DSSReference> dssReferences = new ArrayList<DSSReference>();
		for (final WSDSSReference wsDssReference : wsReferences) {

			final DSSReference dssReference = new DSSReference();
			dssReference.setId(wsDssReference.getId());
			dssReference.setType(wsDssReference.getType());
			dssReference.setUri(wsDssReference.getUri());
			dssReference.setDigestMethodAlgorithm(wsDssReference.getDigestMethodAlgorithm());
			final DSSDocument contentsDssDocument = DSSWSUtils.createDssDocument(wsDssReference.getContents());
			dssReference.setContents(contentsDssDocument);
			dssReference.setTransforms(wsDssReference.getTransforms());
			dssReferences.add(dssReference);
		}
		params.setReferences(dssReferences);
	}

	private void setAsicZipComment(final WSParameters wsParameters, final ASiCSignatureParameters params) {
		params.aSiC().setZipComment(wsParameters.getAsicZipComment());
	}

	private void setAsicMimeType(final WSParameters wsParameters, final ASiCSignatureParameters params) {
		params.aSiC().setMimeType(wsParameters.getAsicMimeType());
	}

	private void setAsicSignatureForm(final WSParameters wsParameters, final ASiCSignatureParameters params) {
		params.aSiC().setUnderlyingForm(wsParameters.getAsicSignatureForm());
	}

	private void setAsicEnclosedSignature(final WSParameters wsParameters, final ASiCSignatureParameters params) {
		final DSSDocument dssDocument = DSSWSUtils.createDssDocument(wsParameters.getAsicEnclosedSignature());
		params.aSiC().setEnclosedSignature(dssDocument);
	}

	@Override
	public byte[] getDataToSign(final WSDocument wsDocument, final WSParameters wsParameters) throws DSSException {
		String exceptionMessage;
		Log logEntry = new Log();
		logEntry.setOperation("Signature");

		final AbstractSignatureParameters params = createParameters(wsParameters);
		logEntry.setUser(params.getCertificateChain().get(0).getX509Certificate().getSubjectShortName());
		try {
			if (LOG.isInfoEnabled()) {
				LOG.info("WsGetDataToSign: begin");
			}
			// final AbstractSignatureParameters params =
			// createParameters(wsParameters);
			final DSSDocument dssDocument = DSSWSUtils.createDssDocument(wsDocument);

			final DocumentSignatureService service = getServiceForSignatureLevel(params.getSignatureLevel());
			ToBeSigned dataToSign = service.getDataToSign(dssDocument, params);
			if (LOG.isInfoEnabled()) {
				LOG.info("WsGetDataToSign: end");
			}

			logEntry.setStatus("SUCCES");
			logEntry.setInfo("Signature succesfully");
			setLog(logEntry);

			return dataToSign.getBytes();
		} catch (Throwable e) {
			exceptionMessage = e.getMessage();
			LOG.error("WsGetDataToSign: ended with exception", e);

			logEntry.setStatus("ERROR");
			logEntry.setInfo(exceptionMessage);
			setLog(logEntry);
			throw new DSSException(exceptionMessage);
		}
	}

	@Override
	public WSDocument signDocument(final WSDocument wsDocument, final WSParameters wsParameters,
			final byte[] signatureValue) throws DSSException {
		String exceptionMessage;
		Log logEntry = new Log();
		logEntry.setOperation("Signature");

		final AbstractSignatureParameters params = createParameters(wsParameters);
		logEntry.setUser(params.getCertificateChain().get(0).getX509Certificate().getSubjectDN().getName());
		try {
			if (LOG.isInfoEnabled()) {
				LOG.info("WsSignDocument: begin");
			}

			final DSSDocument dssDocument = DSSWSUtils.createDssDocument(wsDocument);
			final DocumentSignatureService service = getServiceForSignatureLevel(params.getSignatureLevel());

			SignatureValue value = new SignatureValue();
			value.setValue(signatureValue);
			final DSSDocument signatureDssDocument = service.signDocument(dssDocument, params, value);
			WSDocument SignatureWsDocument = new WSDocument(signatureDssDocument);
			if (LOG.isInfoEnabled()) {
				LOG.info("WsSignDocument: end");
			}

			logEntry.setStatus("SUCCES");
			logEntry.setInfo("Signature succesful, file signed is " + wsDocument.getName() + " ; IssuerDN : "
					+ params.getCertificateChain().get(0).getX509Certificate().getIssuerDN().getName());
			setLog(logEntry);

			return SignatureWsDocument;
		} catch (Throwable e) {
			exceptionMessage = e.getMessage();
			logEntry.setStatus("ERROR");
			logEntry.setInfo(exceptionMessage + " ; IssuerDN : "
					+ params.getCertificateChain().get(0).getX509Certificate().getIssuerDN().getName());
			setLog(logEntry);
			LOG.error("WsSignDocument: ended with exception", e);
			throw new DSSException(exceptionMessage);
		}
	}

	@Override
	public WSDocument extendSignature(final WSDocument wsDocument, final WSParameters wsParameters)
			throws DSSException {
		try {
			if (LOG.isInfoEnabled()) {
				LOG.info("WsExtendSignature: begin");
			}
			final AbstractSignatureParameters params = createParameters(wsParameters);
			final DSSDocument dssDocument = DSSWSUtils.createDssDocument(wsDocument);
			final DocumentSignatureService service = getServiceForSignatureLevel(params.getSignatureLevel());
			final DSSDocument signatureDssDocument = service.extendDocument(dssDocument, params);
			final WSDocument signatureWsDocument = new WSDocument(signatureDssDocument);
			if (LOG.isInfoEnabled()) {
				LOG.info("WsExtendSignature: end");
			}
			return signatureWsDocument;
		} catch (Throwable e) {
			LOG.error("WsExtendSignature: end with exception", e);
			throw new DSSException(e.getMessage());
		}
	}

	@Override
	public String checkCertificate(final WSChainCertificate wscert) throws DSSException {
		Properties crlProp = ConfigUtility.getProperties();
		try {
			X509Certificate cert = null;
			InputStream is = new ByteArrayInputStream(wscert.getX509Certificate());
			try {
				CertificateFactory cf = CertificateFactory.getInstance("X.509");
				cert = (X509Certificate) cf.generateCertificate(is);
			} catch (Exception e) {
				e.printStackTrace();
			}

			X509Certificate rootCA = findLocalCA(cert, crlProp.getProperty("pathCA"));
			X509CRL crl = findLocalCRL(cert, crlProp.getProperty("pathCRL"), crlProp.getProperty("pathCA"));

			return checkCertificateCRL(rootCA, cert, crl);

		} catch (Throwable e) {
			throw new DSSException(e.getMessage());
		}
	}

	public String checkCertificateCRL(X509Certificate certroot, X509Certificate cert, X509CRL crl) {
		String statusCert = null;

		Log logEntry = new Log();
		logEntry.setOperation("Certificate");
		logEntry.setUser(cert.getSubjectDN().getName());
		try {

			CertPath cp = null;
			Vector<X509Certificate> certs = new Vector<X509Certificate>();

			// load the cert to be checked
			certs.add(cert);

			// init cert path
			CertificateFactory cf = CertificateFactory.getInstance("X509");
			cp = (CertPath) cf.generateCertPath(certs);

			// load the root CA cert
			X509Certificate rootCACert = certroot;

			// init trusted certs
			TrustAnchor ta = new TrustAnchor(rootCACert, null);
			Set<TrustAnchor> trustedCerts = new HashSet<TrustAnchor>();
			trustedCerts.add(ta);

			// init PKIX parameters
			PKIXParameters params = new PKIXParameters(trustedCerts);
			// False for bypass the critical security of the certificat
			params.setPolicyQualifiersRejected(false);

			if (cert.hasUnsupportedCriticalExtension()) {
				String strLog = "Unsuported OID [";
				Set critSet = cert.getCriticalExtensionOIDs();
				if (critSet != null && !critSet.isEmpty()) {
					for (Iterator i = critSet.iterator(); i.hasNext();) {
						String oid = (String) i.next();
						if (i.hasNext())
							strLog += oid + " ,";
						else
							strLog += oid;

					}
					strLog.substring(0, strLog.length() - 1);
					strLog += "]";
				}

				// TODO NEW LOG
				logEntry.setOperation("Certificate");
				logEntry.setUser(cert.getSubjectDN().getName());
				logEntry.setStatus("INFOS");
				logEntry.setInfo(strLog);
				setLog(logEntry);
			}

			// Load the CRL from local repo
			params.addCertStore(CertStore.getInstance("Collection",
					new CollectionCertStoreParameters(Collections.singletonList(crl))));
			// perform validation
			CertPathValidator cpv = CertPathValidator.getInstance("PKIX");
			PKIXCertPathValidatorResult cpv_result = (PKIXCertPathValidatorResult) cpv.validate(cp, params);
			X509Certificate trustedCert = (X509Certificate) cpv_result.getTrustAnchor().getTrustedCert();

			if (trustedCert != null)
				statusCert = "TRUSTED_CERT";

			logEntry.setStatus("SUCCES");
			logEntry.setInfo("Certificate verified ; IssuerDN : " + cert.getIssuerDN().getName());
			setLog(logEntry);

		} catch (Exception e) {
			String eMessage = e.getMessage();

			if (eMessage.contains("timestamp check failed"))
				statusCert = "EXPIRED_CERT";
			else if (eMessage.contains("Path does not chain with any of the trust anchors"))
				statusCert = "NOT_TRUSTED_CERT";
			else if (eMessage.contains("Certificate has been revoked"))
				statusCert = "REVOKED_CERT";
			else if (eMessage.contains("Could not determine revocation status"))
				statusCert = "UNKNOWN_REVOCATION_CERT";
			else
				statusCert = "NOT_YET_VERIFIED_CERT";

			logEntry.setStatus("ERROR");
			logEntry.setInfo(statusCert + " ; IssuerDN : " + cert.getIssuerDN().getName());
			setLog(logEntry);

			e.printStackTrace();

		}
		System.out.println(statusCert);
		return statusCert;
	}

	public X509Certificate getCA(String path) {
		X509Certificate certCA = null;
		FileInputStream inCert = null;

		try {
			inCert = new FileInputStream(path);
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			certCA = (X509Certificate) cf.generateCertificate(inCert);
			inCert.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return certCA;
	}

	public boolean verifyCA(X509Certificate cert, X509Certificate certCA) {
		boolean check = true;
		try {
			cert.verify(certCA.getPublicKey());
		} catch (Exception e) {
			// e.printStackTrace();
			if (e.getMessage() != null)
				check = false;
		}
		return check;
	}

	public X509Certificate getCertFile(String localPath) // throws
	// FileNotFoundException,
	// CertificateException
	{
		X509Certificate cert = null;
		InputStream inputCert = null;
		try {
			inputCert = new FileInputStream(localPath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CertificateFactory cf = null;
		try {
			cf = CertificateFactory.getInstance("X.509");
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			cert = (X509Certificate) cf.generateCertificate(inputCert);
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cert;
	}

	public X509Certificate findLocalCA(X509Certificate cert, String localPath) {
		File folder = new File(localPath);
		ArrayList<File> files = new ArrayList<File>(Arrays.asList(folder.listFiles()));
		X509Certificate certCA = null;

		for (File file : files) {
			certCA = getCertFile(file.getAbsolutePath());
			if (verifyCA(cert, certCA)) {
				System.out.println(file.getAbsolutePath());
				return certCA;
			}
		}

		return certCA;
	}

	public X509CRL findLocalCRL(X509Certificate cert, String localPathCRL, String localPathCA) // throws
	// CRLException
	{
		X509CRL foundCRL = null;
		X509Certificate certCA = findLocalCA(cert, localPathCA);
		if (certCA == null)
			return foundCRL;
		else {
			File folder = new File(localPathCRL);
			ArrayList<File> files = new ArrayList<File>(Arrays.asList(folder.listFiles()));
			X509CRL crl = null;

			for (File file : files) {
				crl = getCRLFile(file.getAbsolutePath());
				if (verifyCRL(crl, certCA)) {
					System.out.println(file.getAbsolutePath());
					return crl;
				}
			}
		}

		return foundCRL;
	}

	public X509CRL getCRLFile(String localPath) // throws FileNotFoundException,
	// CertificateException,
	// CRLException
	{
		X509CRL crl = null;
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(localPath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CertificateFactory cf = null;
		try {
			cf = CertificateFactory.getInstance("X509");
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			crl = (X509CRL) cf.generateCRL(inputStream);
		} catch (CRLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return crl;
	}

	public boolean verifyCRL(X509CRL crl, X509Certificate certCA) {
		boolean check = true;
		try {
			crl.verify(certCA.getPublicKey());
		} catch (Exception e) {
			// e.printStackTrace();
			if (e.getMessage() != null)
				check = false;
		}
		return check;
	}

	@Override
	public WSSignaturePolicy getSignaturePolicy(final String signaturePolicy) throws DSSException {
		WSSignaturePolicy wsPolicy = new WSSignaturePolicy();
		Properties policyPath = ConfigUtility.getProperties();

		List<SignPolicies.Policy> listPolicies = null;
		SignPolicies policies = new SignPolicies();
		try {
			File file = new File(policyPath.getProperty("pathPolicy") + "/linsign-signature-policies.xml");
			JAXBContext jaxbContext = JAXBContext.newInstance(SignPolicies.class);
			policies = (SignPolicies) jaxbContext.createUnmarshaller().unmarshal(file);
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		listPolicies = policies.getPolicy();
		if (listPolicies != null)
			for (int i = 0; i < listPolicies.size(); i++)
				if (listPolicies.get(i).getLabel().equals(signaturePolicy)) {
					wsPolicy.setSignatureAlgorithm("SHA256");
					wsPolicy.setSignatureFormat(listPolicies.get(i).getSignature().getFormat());
					wsPolicy.setSignatureLevel(listPolicies.get(i).getSignature().getLevel());
					wsPolicy.setSignaturePackaging(listPolicies.get(i).getSignature().getPackaging());
					break;
				}

		return wsPolicy;
	}

	public Properties getPolicyProperties(String policyReference, String pathPolicy) {
		Properties policyProps = new Properties();
		File policyFile = new File(pathPolicy + "/" + policyReference);

		if (policyFile.exists() && !policyFile.isDirectory()) {
			try {
				FileInputStream inputStream = new FileInputStream(pathPolicy + "/" + policyReference);
				policyProps.load(inputStream);
				inputStream.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else
			throw new DSSException();

		return policyProps;
	}

	public void setLog(Log logEntry) {
		LSLOGS.info("", logEntry);
	}
}
