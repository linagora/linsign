package eu.europa.esig.dss.ws.utility;

import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.List;

import eu.europa.esig.dss.tsl.ServiceInfo;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.x509.CertificateSource;
import eu.europa.esig.dss.x509.CertificateToken;
import eu.europa.esig.dss.x509.CommonTrustedCertificateSource;
import eu.europa.esig.dss.x509.crl.ListCRLSource;
import eu.europa.esig.dss.x509.crl.OfflineCRLSource;

public class ValidationUtility {

	public static CertificateVerifier linsignCertificateVerifierGenerator(CertificateVerifier verifier)
			throws Exception {
		if (!ConfigUtility.isTLAndCRLValidationRequired())
			return verifier;

		if (verifier == null)
			throw new Exception("Certificate Verifier can't be null");

		if (verifier.getSignatureCRLSource() == null
				|| verifier.getSignatureCRLSource().getContainedX509CRLs().isEmpty()) {

			List<X509CRL> crlList = ConfigUtility.getCRLList();
			List<X509Certificate> caList = ConfigUtility.getCAFileList();

			OfflineCRLSource offCrlSource = ValidationUtility.generateSignatureCRLSource(crlList);
			verifier.setCrlSource(offCrlSource);

			verifier.setSignatureCRLSource(ValidationUtility.generateSignatureCRLSource(crlList));
			verifier.setTrustedCertSource(getCAFile(caList));
		}
		return verifier;
	}

	public static ListCRLSource generateSignatureCRLSource(List<X509CRL> crlList) {
		ListCRLSource lcrl = new ListCRLSource();
		MyCRLSource mockCrl = new MyCRLSource(crlList);
		lcrl.addAll(mockCrl);
		return lcrl;
	}

	public static CertificateSource getCAFile(List<X509Certificate> caList) {
		CommonTrustedCertificateSource certSource = new CommonTrustedCertificateSource();
		ServiceInfo mockServiceInfo = new LngServiceInfos();
		for (X509Certificate x509Ca : caList) {
			CertificateToken certToken = new CertificateToken(x509Ca);
			certSource.addCertificate(certToken, mockServiceInfo);
		}
		return certSource;
	}
}
