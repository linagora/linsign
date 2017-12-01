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
package org.linagora.linsign.sddss.keystore;

/*
 * #%L
 * signature-client
 * %%
 * Copyright (C) 2013 - 2015 gSafe
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Provider;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

public class KeyStoreEntry {

	private KeyStore loadedStore;
	private String alias;

	private String issuer; // cache
	private String issuerCN; // cache
	private String subject; // cache
	private String subjectCN; // cache
	private String expirationDate; // cache

	private Date notAfter;// cache
	private Date notBefore;// cache
	private boolean[] keyusage; // cache
	// private List<String> extkeyusage; //cache

	/**
	 * Creates a new instance of KeyStoreEntry
	 */
	public KeyStoreEntry(KeyStore loadedStore, String alias) throws KeyStoreException, CertificateException {
		this.loadedStore = loadedStore;
		Certificate cert = loadedStore.getCertificate(alias);
		if (!(cert instanceof X509Certificate)) {
			throw new java.security.cert.CertificateException("Certificate entry should be an X509Certificate");
		}

		X509Certificate x509cert = (X509Certificate) cert;
		this.alias = alias;
		X500Principal subjPrinc = x509cert.getSubjectX500Principal();
		X500Principal issuerPrinc = x509cert.getIssuerX500Principal();
		this.issuer = issuerPrinc.toString();
		this.issuerCN = new X500Parser(issuerPrinc).getCN();
		this.subject = subjPrinc.toString();
		this.subjectCN = new X500Parser(subjPrinc).getCN();
		this.expirationDate = new SimpleDateFormat().format(x509cert.getNotAfter());
		this.keyusage = x509cert.getKeyUsage();
		this.notAfter = x509cert.getNotAfter();
		this.notBefore = x509cert.getNotBefore();
		// this.extkeyusage = x509cert.getExtendedKeyUsage();
	}

	public String getAlias() {
		return this.alias;
	}

	public String getIssuer() {
		return this.issuer;
	}

	public String getIssuerCN() {
		return this.issuerCN;
	}

	public String getSubject() {
		return this.subject;
	}

	public String getSubjectCN() {
		return this.subjectCN;
	}

	public String getExpirationDate() {
		return this.expirationDate;
	}

	public boolean[] getKeyusage() {
		return keyusage;
	}

	// public List<String> getExtkeyusage() {return extkeyusage;}
	public Date getNotAfter() {
		return notAfter;
	};

	public Date getNotBefore() {
		return notBefore;
	};

	public Provider getProvider() {
		return this.loadedStore.getProvider();
	}

	public KeyStore getLoadedKeyStore() {
		return this.loadedStore;
	}

	public Certificate[] getCertificateChain() throws java.security.KeyStoreException {
		return this.loadedStore.getCertificateChain(this.alias);
	}

	@Override
	public String toString() {
		return this.subjectCN + "[" + this.issuerCN + "]";
	}

}
