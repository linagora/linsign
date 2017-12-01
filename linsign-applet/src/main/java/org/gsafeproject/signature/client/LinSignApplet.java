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
package org.gsafeproject.signature.client;

import java.applet.Applet;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import netscape.javascript.JSObject;

public class LinSignApplet extends Applet {

	private String userAgent;
	private String wsLinSignURL;
	private String signaturePolicy;

	public LinSign instance;

	private String jsFunctionToExecute;

	/**
	 * Initialization method that will be called after the applet is loaded into
	 * the browser.
	 */
	public void init() {

		System.setSecurityManager(null);

		try {
			userAgent = getParameter("userAgent");
			System.out.println("UserAgent = " + userAgent);

			wsLinSignURL = getParameter("wsLinSignURL");
			System.out.println("wsLinSignURL = " + wsLinSignURL);

			signaturePolicy = getParameter("signaturePolicy");
			System.out.println("signaturePolicy = " + signaturePolicy);

			jsFunctionToExecute = getParameter("jsFunctionToExecute");
			System.out.println("jsFunctionToExecute = " + jsFunctionToExecute);

			try {
				instance = new LinSign(userAgent, wsLinSignURL, signaturePolicy);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {				
			e.printStackTrace();
		}
		this.setVisible(false);
		System.out.println("------------------------------------------------------------------------------------------------------------------");    	
		System.out.println("Keystore size : " +  this.getKeys().size());
		System.out.println("------------------------------------------------------------------------------------------------------------------");		
		List<PrivateKeyEntry> listKeys = this.getKeys();
		X509Certificate cert;

		if (listKeys != null)
		{
			for (int i = 0; i < listKeys.size(); i++)
			{
				cert = (X509Certificate)listKeys.get(i).getCertificate();
				System.out.println("Serial number : " + cert.getSubjectDN());
				System.out.println("Serial number : " + cert.getSerialNumber().toString());
				System.out.println("------------------------------------------------------------------------------------------------------------------");					
			}
		}
	}


	public String setSelectedCertificate(int keyIndex)
	{
		return instance.setSelectedKey(keyIndex);    	    	
	}

	public List<KeyStore> getKeyStore()
	{
		return instance.getKeyStore();    	    	
	}    

	public List<PrivateKeyEntry> getKeys()
	{
		List<PrivateKeyEntry> listKeys = new ArrayList<PrivateKeyEntry>();
		try {
			listKeys = instance.getKeys();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return listKeys;    	    	
	}    

	public void signDocument()
	{
		instance.SignFile();    	
	}

	public void setDataToSign(String datToSign)
	{
		if (datToSign == null) System.out.println("input data is null");
		else
		{	
			System.out.println("input data is OK");
			instance.setDataToSign(DatatypeConverter.parseBase64Binary(datToSign));
		}
	}

	public boolean isFileReady(){
		return instance.isFileReady();
	}

	public String[] getSignedData()
	{
	   String outputData = null;
	   String[] signedData = null;
	   final int TAILLE_MAX = 1048576;
	   try{
	      outputData = DatatypeConverter.printBase64Binary(instance.getSignedData());
	      final int tailleTab = (int)Math.ceil((double) outputData.length() / TAILLE_MAX);
	      signedData = new String[tailleTab];

	      int j = 0;
	      for(long i = 0;i < outputData.length();i += TAILLE_MAX){
	         signedData[j] = outputData.substring((int)i, (i + TAILLE_MAX < outputData.length () ? (int)i + TAILLE_MAX : outputData.length()));
	         j++;
	      }
	   }catch (Exception e){
	      e.printStackTrace();
	   }
	   return signedData;
	}

	public void setPdfMarkProperties(String pdfMark)
	{
		if (pdfMark == null) System.out.println("No pdf mark properties");
		else
		{
			// Desérialisé pdfMark
			try {
				ObjectInputStream oisPdfMark =  new ObjectInputStream(new ByteArrayInputStream(DatatypeConverter.parseBase64Binary(pdfMark))) ;
				PDFVisibleProperties inputPdfMark = (PDFVisibleProperties)oisPdfMark.readObject() ;

				instance.PdfMarkProperties(inputPdfMark);
			} catch(Exception e){
				e.printStackTrace();
			}
		}
	}    

	public void start(){
		if(jsFunctionToExecute != null) {
			JSObject window = JSObject.getWindow(this);
			window.call(jsFunctionToExecute, new Object[]{});
		}
	}
}