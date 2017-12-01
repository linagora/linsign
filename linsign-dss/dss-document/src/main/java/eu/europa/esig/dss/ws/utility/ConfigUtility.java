package eu.europa.esig.dss.ws.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ConfigUtility {

	private final static String DEFAULT_FILE_CONFIG_NAME = "config";
	private final static String DEFAULT_CA_NAME = "pathCA";
	private final static String DEFAULT_CRL_NAME = "pathCRL";
	private final static String DEFAULT_POLICY_NAME = "pathPolicy";
	private final static String DEFAULT_LOG_NAME = "pathLog";

	private final static String LOG_TRACE = "logTrace";
	private final static String VALIDATE_TL_AND_CRL = "validateTLandCRL";

	public static Properties getProperties() {
		Properties policyProps = new Properties();
		try {
			InputStream is;
			is = Thread.currentThread().getContextClassLoader().getResourceAsStream(DEFAULT_FILE_CONFIG_NAME);
			policyProps.load(is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return policyProps;
	}

	public static boolean isTLAndCRLValidationRequired() {
		return getProperties().getProperty(VALIDATE_TL_AND_CRL).equals("true");
	}

	public static boolean isLogTrace() {
		return getProperties().getProperty(LOG_TRACE).equals("true");
	}

	public static List<X509Certificate> getCAFileList() {
		List<X509Certificate> lca = new ArrayList<X509Certificate>();

		Properties properties = getProperties();
		String localPathCA = properties.getProperty(DEFAULT_CA_NAME);

		File folder = new File(localPathCA);
		ArrayList<File> files = new ArrayList<File>(Arrays.asList(folder.listFiles()));
		for (File file : files) {
			if (file.getAbsolutePath().contains(".crt") || file.getAbsolutePath().contains(".pem")) {
				X509Certificate certCA = getCertFile(file.getAbsolutePath());
				if (certCA != null) {
					lca.add(certCA);
				}
			}
		}
		return lca;
	}

	public static List<X509CRL> getCRLList() {
		List<X509CRL> lcrl = new ArrayList<X509CRL>();

		Properties properties = getProperties();
		String localPathCRL = properties.getProperty(DEFAULT_CRL_NAME);

		File folder = new File(localPathCRL);
		ArrayList<File> files = new ArrayList<File>(Arrays.asList(folder.listFiles()));
		for (File file : files) {
			if (file.getAbsolutePath().contains(".crl")) {
				X509CRL crl = getCRLFile(file.getAbsolutePath());
				if (crl != null)
					lcrl.add(crl);
			}

		}
		return lcrl;
	}

	private static X509Certificate getCertFile(String localPath) {
		try {
			InputStream inputCert = new FileInputStream(localPath);
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			return (X509Certificate) cf.generateCertificate(inputCert);
		} catch (Exception e) {
			// Not a valid X509
			e.printStackTrace();
		}
		return null;
	}

	private static X509CRL getCRLFile(String localPath) {
		try {
			FileInputStream inputStream = new FileInputStream(localPath);
			CertificateFactory cf = CertificateFactory.getInstance("X509");
			return (X509CRL) cf.generateCRL(inputStream);
		} catch (Exception e) {
			// Not a valid CRL
			e.printStackTrace();
		}
		return null;
	}

}
