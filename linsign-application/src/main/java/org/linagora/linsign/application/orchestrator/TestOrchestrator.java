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
package org.linagora.linsign.application.orchestrator;

import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.linagora.linsign.application.controller.CertificatController;
import org.linagora.linsign.application.model.dao.Document;
import org.linagora.linsign.application.signature.MySignature;
import org.linagora.linsign.application.signature.SigningUtils;
import org.linagora.linsign.application.utility.UtilityLinSignAPP;
import org.linagora.linsign.sddss.dao.PDFVisibleProperties;

import eu.europa.esig.dss.wsclient.signature.WsChainCertificate;

public class TestOrchestrator {

	private final static Logger LOGGER = Logger.getLogger(TestOrchestrator.class.getName());

	public static boolean signatureOrchestrator(String idSignature, String userAgent, List<Document> issDocument,
			List<KeyStore> keystore, int tableRowSelect, List<PDFVisibleProperties> myPdfProperties,
			String signatureTestPolicy, String filter) {
		try {

			/* SELECT CERTIFICAT */
			CertificatController certificatSelector = new CertificatController();
			X509Certificate userCertificateSelected = certificatSelector.setSelectedKey(keystore, tableRowSelect,
					filter);

			/* VALIDITY CERTIFICAT */
			WsChainCertificate chainCertificate = certificatSelector
					.prepareCertificatToService(userCertificateSelected);
			String resultCertificat = SigningUtils.checkCertificate(chainCertificate);
			LOGGER.info(resultCertificat);

			/* SIGN FILE */
			List<Document> documentToSend = MySignature.singatureDocument(userAgent,
					certificatSelector.setSelectedKeyForSign(keystore, tableRowSelect, filter), myPdfProperties,
					signatureTestPolicy, issDocument);

			/* WRITE FILE ON DISK */
			for (Document doc : documentToSend) {
				UtilityLinSignAPP.writeFileTest(doc);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, e.getMessage());
			return false;
		}
	}

}
