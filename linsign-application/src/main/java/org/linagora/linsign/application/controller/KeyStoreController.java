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
import java.security.KeyStoreException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.linagora.linsign.application.langue.Language;
import org.linagora.linsign.application.langue.SelectorLanguage;
import org.linagora.linsign.application.view.certificat.CertificatView;
import org.linagora.linsign.sddss.keystore.KeyStoreUtils;

public class KeyStoreController {
	private final static Logger LOGGER = Logger.getLogger(KeyStoreController.class.getName());
	public static final int FLAG_NONREPUDIATION = 1;

	private static SelectorLanguage selectLangue = new SelectorLanguage(Language.EN);
	private static String filter = null;

	public KeyStoreController(SelectorLanguage selector, String myFilter) {
		if (selector == null)
			selectLangue = selector;
		else
			selectLangue = new SelectorLanguage(Language.EN);

		if (myFilter != null)
			filter = myFilter.toLowerCase();
		else
			filter = null;
	}

	public boolean verifyKey(KeyStore keystore, String alias, X509Certificate cert) throws KeyStoreException {
		CharSequence strToFilter = cert.getSubjectDN().toString().toLowerCase();
		if (keystore.isKeyEntry(alias) && (filter == null || Pattern.compile(filter).matcher(strToFilter).find()))
			return true;
		return false;
	}

	private boolean checkCertificatFlag(X509Certificate cert) {
		if (cert != null && cert.getKeyUsage() != null && cert.getKeyUsage()[FLAG_NONREPUDIATION])
			return true;
		return false;
	}

	public List<KeyStore> selectKeyStore(String browserType) throws KeyStoreException, Exception {
		KeyStoreUtils keyUtil = new KeyStoreUtils(selectLangue);
		List<KeyStore> keystoreList = null;

		if (browserType == null) {
			LOGGER.log(Level.SEVERE, "No browser selected");
			return null;
		}

		LOGGER.info("BrowserType = " + browserType);
		if (browserType.equals("Chrome"))
			browserType = "MSIE";

		keystoreList = keyUtil.getBrowserKeyStore(browserType);
		for (KeyStore keystore : keystoreList) {
			for (Enumeration<String> list = keystore.aliases(); list.hasMoreElements();) {
				String alias = list.nextElement();
				X509Certificate cert = (X509Certificate) keystore.getCertificate(alias);
				if (verifyKey(keystore, alias, cert)) {
					LOGGER.info("-------------------------------------------------------------------------------");
					LOGGER.info("Alias = " + alias);
					LOGGER.info("Serial number : " + cert.getSubjectDN());
				}
			}
		}

		return keystoreList;
	}

	public List<String> listAliasKeyStore(KeyStore keystore) throws KeyStoreException {
		List<String> keyStoreAlias = new ArrayList<String>();

		for (Enumeration<String> list = keystore.aliases(); list.hasMoreElements();) {
			String alias = list.nextElement();
			X509Certificate cert = (X509Certificate) keystore.getCertificate(alias);

			if (verifyKey(keystore, alias, cert)) {
				LOGGER.info("-------------------------------------------------------------------------------");
				LOGGER.info("Alias = " + alias);
				LOGGER.info("Serial number : " + cert.getSubjectDN());

				keyStoreAlias.add(alias);
			}
		}
		return keyStoreAlias;
	}

	public List<CertificatView> generateListCertView(List<KeyStore> keystoreList) throws KeyStoreException {
		List<CertificatView> listCert = new ArrayList<CertificatView>();
		for (KeyStore keystore : keystoreList) {
			for (Enumeration<String> list = keystore.aliases(); list.hasMoreElements();) {
				String alias = list.nextElement();
				X509Certificate cert = (X509Certificate) keystore.getCertificate(alias);
				if (verifyKey(keystore, alias, cert) && checkCertificatFlag(cert)) {
					try {
						// Generate the certificate name (to manage duplicate
						// alias problem)
						X500Name x500name = new JcaX509CertificateHolder(cert).getSubject();
						RDN cn = x500name.getRDNs(BCStyle.CN)[0];
						String s = IETFUtils.valueToString(cn.getFirst().getValue());
						alias = s;
					} catch (CertificateEncodingException e) {
						e.printStackTrace();

					}

					CertificatView myCertificat = new CertificatView(alias, cert.getSubjectDN().toString(),
							cert.getNotBefore(), cert.getNotAfter(), cert.getSerialNumber());
					if (!listCert.contains(myCertificat)) {
						listCert.add(myCertificat);
					}
				}
			}
		}

		return listCert;
	}
}
