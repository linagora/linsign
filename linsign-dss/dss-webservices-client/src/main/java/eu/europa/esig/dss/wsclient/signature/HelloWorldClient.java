package eu.europa.esig.dss.wsclient.signature;
 
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

//import eu.europa.esig.dss.ws.impl.SignatureServiceImpl;

import eu.europa.esig.dss.wsclient.signature.SignatureService;
import eu.europa.esig.dss.wsclient.signature.SignatureService_Service;
 
public class HelloWorldClient{
 
	public static void main(String[] args) throws Exception {
 
		URL url = new URL("http://localhost:8080/linsign-webapp/wservice/signatureService?wsdl");
		String serviceURL = "http://localhost:8080/linsign-webapp/wservice";
 
		WsParameters wsParameters = new WsParameters();
		wsParameters.setPdfMark("John DOE");

		//1st argument service URI, refer to wsdl document above
		//2nd argument is service name, refer to wsdl document above
		
//        QName qname = new QName("http://impl.ws.dss.esig.europa.eu/", "SignatureService"); 
//        Service service = Service.create(url, qname); 
//        SignatureService signature = service.getPort(SignatureService.class);
 
		SignatureService_Service.setROOT_SERVICE_URL(serviceURL);
		final SignatureService_Service signatureService_service = new SignatureService_Service();
		final SignatureService signatureServiceImplPort = signatureService_service.getSignatureServiceImplPort();

        String checkCert = null;
        WsChainCertificate wsCert = new WsChainCertificate();

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

        checkCert =  signatureServiceImplPort.checkCertificate(wsCert);

        //System.out.print(wsCert.getX509Certificate().toString());
        System.out.print(checkCert);
        
	}
 }