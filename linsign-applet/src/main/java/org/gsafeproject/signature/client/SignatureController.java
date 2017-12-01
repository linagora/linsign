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

import java.awt.print.PageFormat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.DatatypeConverter;
import javax.xml.crypto.dsig.CanonicalizationMethod;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.linagora.linsign.applet.core.SignatureModel;
import org.linagora.linsign.applet.core.SigningUtils;

/*
 * #%L
 * gsafe-signature-controller
 * %%
 * Copyright (C) 2013 - 2014 gSafe, 
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This code is inspired from the code sources of DS-DSS project      
 * 
 */

import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.DSSXMLUtils;
import eu.europa.esig.dss.FileDocument;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import eu.europa.esig.dss.wsclient.signature.DigestAlgorithm;
import eu.europa.esig.dss.wsclient.signature.DssTransform;
import eu.europa.esig.dss.wsclient.signature.EncryptionAlgorithm;
import eu.europa.esig.dss.wsclient.signature.Policy;
import eu.europa.esig.dss.wsclient.signature.SignatureLevel;
import eu.europa.esig.dss.wsclient.signature.SignaturePackaging;
import eu.europa.esig.dss.wsclient.signature.WsChainCertificate;
import eu.europa.esig.dss.wsclient.signature.WsParameters;
import eu.europa.esig.dss.wsclient.signature.WsdssReference;
import eu.europa.esig.dss.x509.CertificateToken;

public class SignatureController {

	
	private SignatureModel model;
	private final int MAX_Y_PORTRAIT = 840;
	private final int MAX_Y_LANDSCAPE = 570;

	public SignatureController(SignatureModel model) {
		this.model = model;
	}

	public void doRefreshPrivateKeys() {

		try {
			final SignatureTokenConnection tokenConnection = model.getTokenConnection();
			model.setPrivateKeys(tokenConnection.getKeys());
		} catch (final DSSException e) {
			// FIXME
			e.printStackTrace();
		}
	}	
			
	public void signDocument() throws IOException, NoSuchAlgorithmException, DSSException {

		final File fileToSign = model.getSelectedFile();
		final SignatureTokenConnection tokenConnection = model.getTokenConnection();
		final DSSPrivateKeyEntry privateKey = model.getSelectedPrivateKey();

		final WsParameters parameters = new WsParameters();

		parameters.setSigningCertificateBytes(privateKey.getCertificate().getEncoded());

		List<WsChainCertificate> chainCertificateList = parameters.getChainCertificateList();
		WsChainCertificate certificate = new WsChainCertificate();
		certificate.setX509Certificate(privateKey.getCertificate().getEncoded());
		chainCertificateList.add(certificate);
		CertificateToken[] certificateChain = privateKey.getCertificateChain();
		if (ArrayUtils.isNotEmpty(certificateChain)){
			for (CertificateToken certificateToken : certificateChain) {
				WsChainCertificate c = new WsChainCertificate();
				c.setX509Certificate(certificateToken.getEncoded());
				chainCertificateList.add(c);
			}
		}

		parameters.setEncryptionAlgorithm(EncryptionAlgorithm.fromValue(privateKey.getEncryptionAlgorithm().name()));

		parameters.setSigningDate(DSSXMLUtils.createXMLGregorianCalendar(new Date()));

		DigestAlgorithm digestAlgorithm = model.getSignatureDigestAlgorithm();
		if (digestAlgorithm == null) {
			parameters.setDigestAlgorithm(DigestAlgorithm.SHA256);
		} else {
			parameters.setDigestAlgorithm(digestAlgorithm);
		}


		if (model.isTslSignatureCheck()) {
			prepareTSLSignature(parameters, fileToSign);
		} else {
			prepareCommonSignature(model, parameters);
		}

		final DSSDocument signedDocument = SigningUtils.signDocument(fileToSign, parameters, privateKey, tokenConnection);

		final FileOutputStream fileOutputStream = new FileOutputStream(model.getTargetFile());
		final InputStream inputStream = signedDocument.openStream();
		IOUtils.copy(inputStream, fileOutputStream);
		IOUtils.closeQuietly(inputStream);
		IOUtils.closeQuietly(fileOutputStream);
	}
	
	

	private void prepareCommonSignature(SignatureModel model, WsParameters parameters) {

		final String signatureLevelString = model.getLevel();
		parameters.setSignatureLevel(SignatureLevel.valueOf(signatureLevelString));
		parameters.setSignaturePackaging(model.getPackaging());

		if (model.isClaimedCheck()) {
			parameters.getClaimedSignerRole().add(model.getClaimedRole());
		}

		if (model.isSignaturePolicyCheck()) {

			final byte[] hashValue = DatatypeConverter.parseBase64Binary(model.getSignaturePolicyValue());
			final Policy policy = new Policy();
			policy.setId(model.getSignaturePolicyId());
			final DigestAlgorithm policyDigestAlgorithm = DigestAlgorithm.valueOf(model.getSignaturePolicyAlgo());
			policy.setDigestAlgorithm(policyDigestAlgorithm);
			policy.setDigestValue(hashValue);
			parameters.setSignaturePolicy(policy);
		}
				
		if (model.getPdfMarkProperties() != null)
		{
			int maxY = MAX_Y_PORTRAIT;
			if (PageFormat.PORTRAIT != model.getPdfMarkProperties().getPdfSignatureOrientation()) {
				maxY = MAX_Y_LANDSCAPE;
			}

			parameters.setPdfMark(model.getPdfMarkProperties().getPdfSignatureText());
			parameters.setPdfX(model.getPdfMarkProperties().getPdfSignatureX1());
			parameters.setPdfY(maxY - model.getPdfMarkProperties().getPdfSignatureY1());
			parameters.setPdfPage(model.getPdfMarkProperties().getPdfSignaturePageNb());
			
			parameters.setPdfFontName(model.getPdfMarkProperties().getPdfSignatureFont().getName());
			parameters.setPdfFontStyle(model.getPdfMarkProperties().getPdfSignatureFont().getStyle());
			parameters.setPdfFontSize(model.getPdfMarkProperties().getPdfSignatureFont().getSize());		
		}
	}
	
	
	private void prepareTSLSignature(WsParameters parameters, File fileToSign) {
		parameters.setSignatureLevel(SignatureLevel.XAdES_BASELINE_B);
		parameters.setSignaturePackaging(SignaturePackaging.ENVELOPED);

		final List<WsdssReference> references = new ArrayList<WsdssReference>();

		WsdssReference dssReference = new WsdssReference();
		dssReference.setId("xml_ref_id");
		dssReference.setUri("");
		dssReference.setContents(SigningUtils.toWsDocument(new FileDocument(fileToSign)));
		dssReference.setDigestMethodAlgorithm(parameters.getDigestAlgorithm());

		final List<DssTransform> transforms = new ArrayList<DssTransform>();

		DssTransform dssTransform = new DssTransform();
		dssTransform.setAlgorithm(CanonicalizationMethod.ENVELOPED);
		transforms.add(dssTransform);
		dssReference.getTransforms().add(dssTransform);

		dssTransform = new DssTransform();
		dssTransform.setAlgorithm(CanonicalizationMethod.EXCLUSIVE);
		transforms.add(dssTransform);
		dssReference.getTransforms().add(dssTransform);

		references.add(dssReference);

		parameters.getReferences().addAll(references);
	}
}

