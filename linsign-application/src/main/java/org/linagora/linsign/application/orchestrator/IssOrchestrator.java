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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.linagora.linsign.application.controller.CertificatController;
import org.linagora.linsign.application.model.dao.Document;
import org.linagora.linsign.application.model.dao.Status;
import org.linagora.linsign.application.service.ServiceIss;
import org.linagora.linsign.application.signature.MySignature;
import org.linagora.linsign.sddss.dao.PDFVisibleProperties;

import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.token.KSPrivateKeyEntry;

public class IssOrchestrator {

	private final static Logger LOGGER = Logger.getLogger(IssOrchestrator.class.getName());

	public static boolean signatureOrchestrator(String idSignature, String userAgent, List<Document> issDocument,
			List<KeyStore> keystore, int tableRowSelect, List<PDFVisibleProperties> pdfProperties, String signaturePolicy,
			ServiceIss serviceIss, String filter) {
		try {
			CertificatController certificatSelector = new CertificatController();
			KSPrivateKeyEntry pkEntry = certificatSelector.setSelectedKeyForSign(keystore, tableRowSelect, filter);

			/* SIGN DOCUMENT */
			List<Document> documentToSend = MySignature.singatureDocument(userAgent, pkEntry, pdfProperties,
					signaturePolicy, issDocument);
			serviceIss.sendStatus(idSignature, Status.LINSING_WS_SIGN_FILE);

			/* SEND FILE TO STOCK */
			serviceIss.stockSignedFiles(idSignature, documentToSend);

			serviceIss.sendStatus(idSignature, Status.ISS_SIGNED_FILE_SOTCKED);

			try {
				serviceIss.sendStatus(idSignature, Status.PROCESS_END);
				return true;
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Failed to send process end status");
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			if (e instanceof DSSException) { // TODO need some work here
				if (e.getMessage().contains("utilisateur") || e.getMessage().contains("user"))
					serviceIss.sendStatus(idSignature, Status.USER_END_PROCESS);
				else
					serviceIss.sendStatus(idSignature, Status.ERROR_LINSING_WS_SIGN_FILE);
			}
		}
		return false;
	}

}
