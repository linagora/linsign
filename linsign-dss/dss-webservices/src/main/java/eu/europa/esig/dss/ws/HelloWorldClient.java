package eu.europa.esig.dss.ws;
 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import eu.europa.esig.dss.ws.impl.SignatureServiceImpl;
 
public class HelloWorldClient{
 
	public static void main(String[] args) throws Exception {
 
		URL url = new URL("http://localhost:8080/linsign-webapp/wservice/signatureService?wsdl");
 
		WSParameters wsParameters = new WSParameters();
		wsParameters.setPdfMark("John DOE");

		//1st argument service URI, refer to wsdl document above
		//2nd argument is service name, refer to wsdl document above
        QName qname = new QName("http://impl.ws.dss.esig.europa.eu/", "SignatureService"); 
        Service service = Service.create(url, qname); 
        SignatureService signature = service.getPort(SignatureService.class);
 
        //System.out.println(signature.getTextMark(wsParameters));
        String checkCert = null;        
        WSChainCertificate wsCert = new WSChainCertificate();

        File file = new File("/home/john/user.pem");
        byte[] bFile = new byte[(int) file.length()];
        FileInputStream fileInputStream = null;
        
        try
        {
           //convert file into array of bytes
           fileInputStream = new FileInputStream(file);
           fileInputStream.read(bFile);
           fileInputStream.close();
        }
        catch (Exception e)
         {
           e.printStackTrace();
        } 

        wsCert.setX509Certificate(bFile);

        checkCert = signature.checkCertificate(wsCert);

        //System.out.print(wsCert.getX509Certificate().toString());        
        System.out.print(checkCert);
	
	}
	
	public static boolean verifyCA(X509Certificate cert , X509Certificate certCA)
	{
		boolean check = true;
        try
        {
        	cert.verify(certCA.getPublicKey());
        }  catch (Exception e)
        {
            e.printStackTrace();
            if (e.getMessage() != null) check = false;
         } 
		return check;		
	}
	
	public static X509Certificate getCertFile(String localPath) throws FileNotFoundException, CertificateException
	{		
		X509Certificate cert ;
		InputStream inputCert = new FileInputStream(localPath);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        cert = (X509Certificate) cf.generateCertificate(inputCert);      
		return cert;
	}
	
	public static X509Certificate findLocalCA(X509Certificate cert, String localPath)	
	{		
		File folder = new File (localPath);
		ArrayList<File> files = new ArrayList<File>(Arrays.asList(folder.listFiles()));
		X509Certificate certCA = null;
		
		for (File file : files)
		{
			try {
				certCA = getCertFile(file.getAbsolutePath());
				if (verifyCA(cert, certCA)) 
				{
					return certCA;  
				}
			} catch (FileNotFoundException e) {		
				e.printStackTrace();
			} catch (CertificateException e) {
				e.printStackTrace();
			}
		}
		
		return certCA;		
	}

	
	public static X509CRL findLocalCRL(X509Certificate cert, String localPathCRL, String localPathCA) throws CRLException	
	{
		X509CRL foundCRL = null;						
		X509Certificate certCA = findLocalCA(cert, localPathCA);
		if (certCA == null) return foundCRL;
		else {		
			File folder = new File (localPathCRL);
			ArrayList<File> files = new ArrayList<File>(Arrays.asList(folder.listFiles()));
			X509CRL crl = null;
		
			for (File file : files)
			{
				System.out.println(file.getAbsolutePath());
				try {
					crl = getCRLFile(file.getAbsolutePath());
					if (verifyCRL(crl, certCA)) 
					{
						return crl;  
					}
				} catch (FileNotFoundException e) {		
					e.printStackTrace();
				} catch (CertificateException e) {
					e.printStackTrace();
				}
			}
		}
		
		return foundCRL;
	}
	
	public static X509CRL getCRLFile(String localPath) throws FileNotFoundException, CertificateException, CRLException
	{		
		X509CRL crl = null;
        FileInputStream inputStream = new FileInputStream(localPath);     
	    CertificateFactory cf = CertificateFactory.getInstance("X509");
		crl = (X509CRL)cf.generateCRL(inputStream);		
		return crl;
	}
	
	public static boolean verifyCRL(X509CRL crl , X509Certificate certCA)
	{
		boolean check = true;
        try
        {
        	crl.verify(certCA.getPublicKey());
        }  catch (Exception e)
        {
            e.printStackTrace();
            if (e.getMessage() != null) check = false;
         } 
		return check;		
	}
	
 }