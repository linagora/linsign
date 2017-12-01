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
package eu.europa.esig.dss.token;

import java.security.KeyStore.PrivateKeyEntry;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.List;

import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.EncryptionAlgorithm;
import eu.europa.esig.dss.x509.CertificateToken;

/**
 * Wrapper of a PrivateKeyEntry coming from a KeyStore.
 *
 */
public class KSPrivateKeyEntry implements DSSPrivateKeyEntry {

    private final CertificateToken certificate;

    private final CertificateToken[] certificateChain;

    private final PrivateKey privateKey;

    /**
     * The default constructor for KSPrivateKeyEntry.
     */
    public KSPrivateKeyEntry(final PrivateKeyEntry privateKeyEntry) {

        certificate = new CertificateToken((X509Certificate) privateKeyEntry.getCertificate());
        final List<CertificateToken> x509CertificateList = new ArrayList<CertificateToken>();
        final Certificate[] simpleCertificateChain = privateKeyEntry.getCertificateChain();
        for (final Certificate certificate : simpleCertificateChain) {

            x509CertificateList.add(new CertificateToken((X509Certificate) certificate));
        }
        final CertificateToken[] certificateChain_ = new CertificateToken[x509CertificateList.size()];
        certificateChain = x509CertificateList.toArray(certificateChain_);
        privateKey = privateKeyEntry.getPrivateKey();       
    }

    @Override
    public CertificateToken getCertificate() {
        return certificate;
    }

    @Override
    public CertificateToken[] getCertificateChain() {
        return certificateChain;
    }

    /**
     * @return
     */
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    @Override
    public EncryptionAlgorithm getEncryptionAlgorithm() throws DSSException {

        if (privateKey instanceof RSAPrivateKey) {
            return EncryptionAlgorithm.RSA;
        } else if (privateKey instanceof DSAPrivateKey) {
            return EncryptionAlgorithm.DSA;
        } else if (privateKey instanceof ECPrivateKey) {
            return EncryptionAlgorithm.ECDSA;
        } else if (EncryptionAlgorithm.RSA.getName().equals(privateKey.getAlgorithm())) {
            return EncryptionAlgorithm.RSA;
        } else if (EncryptionAlgorithm.DSA.getName().equals(privateKey.getAlgorithm())) {
            return EncryptionAlgorithm.DSA;
        } else if (EncryptionAlgorithm.ECDSA.getName().equals(privateKey.getAlgorithm())) {
            return EncryptionAlgorithm.ECDSA;
        } else {
            throw new DSSException("Don't find algorithm for PrivateKey of type " + privateKey.getClass());
        }
    }
    
}
