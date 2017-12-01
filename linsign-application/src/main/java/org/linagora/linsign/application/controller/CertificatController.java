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
package org.linagora.linsign.application.controller;

import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

import eu.europa.esig.dss.token.KSPrivateKeyEntry;
import eu.europa.esig.dss.wsclient.signature.WsChainCertificate;

public class CertificatController {

	public X509Certificate setSelectedKey(List<KeyStore> keystore, int keyIndex, String filter) {
		PrivateKeyEntry pkKeys = null;

		try {
			pkKeys = getKeys(keystore, keyIndex, filter);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (pkKeys != null) {
			return (X509Certificate) pkKeys.getCertificate();
		}
		return null;
	}

	public boolean verifyKey(KeyStore keystore, String alias, X509Certificate cert, String filter)
			throws KeyStoreException {
		CharSequence strToFilter = cert.getSubjectDN().toString().toLowerCase();
		filter = filter.toLowerCase();

		if (keystore.isKeyEntry(alias) && (filter == null || Pattern.compile(filter).matcher(strToFilter).find()))
			return true;
		return false;
	}

	public PrivateKeyEntry getKeys(List<KeyStore> keystoreList, int keyIndex, String filter) throws KeyStoreException {

		int i = 0;
		try {
			for (KeyStore keystore : keystoreList) {
				for (Enumeration<String> list = keystore.aliases(); list.hasMoreElements();) {
					String alias = list.nextElement();
					X509Certificate cert = (X509Certificate) keystore.getCertificate(alias);

					if (verifyKey(keystore, alias, cert, filter) && cert.getKeyUsage() != null
							&& cert.getKeyUsage()[KeyStoreController.FLAG_NONREPUDIATION]) {
						if (i == keyIndex)
							return (KeyStore.PrivateKeyEntry) keystore.getEntry(alias,
									new KeyStore.PasswordProtection(null));
						i++;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public KSPrivateKeyEntry setSelectedKeyForSign(List<KeyStore> keystore, int keyIndex, String filter) {
		try {
			PrivateKeyEntry pkEntry = getKeys(keystore, keyIndex, filter);
			return new KSPrivateKeyEntry(pkEntry);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public WsChainCertificate prepareCertificatToService(X509Certificate cert) {
		try {
			WsChainCertificate wsCert = new WsChainCertificate();
			wsCert.setX509Certificate(cert.getEncoded());
			return wsCert;
		} catch (CertificateEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
