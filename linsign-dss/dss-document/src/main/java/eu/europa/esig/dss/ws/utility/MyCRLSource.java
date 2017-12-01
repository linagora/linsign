package eu.europa.esig.dss.ws.utility;

import java.io.InputStream;
import java.security.cert.X509CRL;
import java.util.ArrayList;
import java.util.List;

import eu.europa.esig.dss.DSSUtils;
import eu.europa.esig.dss.x509.crl.OfflineCRLSource;

/**
 * This class allows to provide a CRL source based on the list of
 * individual CRL(s);
 *
 */
public class MyCRLSource extends OfflineCRLSource {
	/**
	 * This constructor allows to build a CRL source from a list of
	 * resource paths.
	 *
	 * @param paths
	 */
	
	public MyCRLSource() {
		if(x509CRLList == null);
			x509CRLList = new ArrayList<X509CRL>();
	}
	
	public void addCrl(X509CRL x509) {
		x509CRLList.add(x509);
	}

	public MyCRLSource(final String... paths) {
		x509CRLList = new ArrayList<X509CRL>();
		for (final String pathItem : paths) {
			final InputStream inputStream = getClass().getResourceAsStream(pathItem);
			addCRLToken(inputStream);
		}
	}

	/**
	 * This constructor allows to build a mock CRL source from a list of
	 * <code>InputStream</code>.
	 *
	 * @param inputStreams
	 *            the list of <code>InputStream</code>
	 */
	public MyCRLSource(final InputStream... inputStreams) {
		x509CRLList = new ArrayList<X509CRL>();
		for (final InputStream inputStream : inputStreams) {
			addCRLToken(inputStream);
		}
	}

	/**
	 * This constructor allows to build a mock CRL source from a list of
	 * <code>X509CRL</code>.
	 *
	 * @param crls
	 *            the list of <code>X509CRL</code>
	 */
	public MyCRLSource(final X509CRL... crls) {
		x509CRLList = new ArrayList<X509CRL>();
		for (X509CRL x509crl : crls) {
			x509CRLList.add(x509crl);
		}
	}

	public MyCRLSource(List<X509CRL> crls) {
		x509CRLList = new ArrayList<X509CRL>();
		for (X509CRL x509crl : crls) {
			x509CRLList.add(x509crl);
		}
	}

	private void addCRLToken(final InputStream inputStream) {
		final X509CRL x509CRL = DSSUtils.loadCRL(inputStream);
		if (!x509CRLList.contains(x509CRL)) {
			x509CRLList.add(x509CRL);
		}
	}
}
