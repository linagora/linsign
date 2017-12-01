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
package org.linagora.linsign.application.signature;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.linagora.linsign.application.model.dao.Document;
import org.linagora.linsign.application.model.dao.MySignaturePolicy;
import org.linagora.linsign.application.utility.UtilityLinSignAPP;
import org.linagora.linsign.application.utility.UtilityLinSignAPP.NoSpaceException;
import org.linagora.linsign.sddss.core.SignatureModel;
import org.linagora.linsign.sddss.dao.PDFVisibleProperties;

import eu.europa.esig.dss.token.DSSPrivateKeyEntry;

public class MySignature {

	private final static Logger LOGGER = Logger.getLogger(MySignature.class.getName());
	private final static String PDF_EXT = ".pdf";

	public static List<Document> singatureDocument(String userAgent, DSSPrivateKeyEntry selectedKeyForSign,
			List<PDFVisibleProperties> pdfVisibleProperties, String signaturePolicy, List<Document> issDocument)
			throws Exception {

		try {
			List<Document> documentListResult = new ArrayList<Document>();

			SignatureModel signModel = new SignatureModel();
			signModel.setTokenConnection(UtilityLinSignAPP.generateTokenConnection(userAgent));
			signModel.setSelectedPrivateKey(selectedKeyForSign);

			MySignaturePolicy mySignaturePolicy = UtilityLinSignAPP.generatePolicy(signaturePolicy);
			signModel.setMySignaturePolicy(mySignaturePolicy);

			for (PDFVisibleProperties pdfProp : pdfVisibleProperties) {
				for (Document doc : issDocument) {
					if (pdfProp.getReference().equals(doc.getTitle())) {
						signModel.setPdfMarkProperties(pdfProp);

						signModel.setSelectedFile(UtilityLinSignAPP.writeFile(doc));
						signModel.setTargetFile(File.createTempFile("signedFile", PDF_EXT));

						SignatureController signController = new SignatureController(signModel);
						signController.signDocument();

						documentListResult.add(new Document(doc.getTitle(), doc.getIssuer(), doc.getCreationDate(),
								readContentIntoByteArray(signModel.getTargetFile()), doc.getReference()));
						break;
					}
				}
			}

			return documentListResult;

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
	}

	private static byte[] readContentIntoByteArray(File file) throws NoSpaceException, IOException {
		File f = File.createTempFile("sizeManager", ".txt");
		f.deleteOnExit();
		if (file.length() > f.getUsableSpace())
			throw new NoSpaceException("Not enough space");

		FileInputStream fileInputStream = null;
		byte[] bFile = new byte[(int) file.length()];
		try {
			// convert file into array of bytes
			fileInputStream = new FileInputStream(file);
			fileInputStream.read(bFile);
			fileInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bFile;
	}

}
