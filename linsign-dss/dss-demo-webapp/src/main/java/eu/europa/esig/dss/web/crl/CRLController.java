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
package eu.europa.esig.dss.web.crl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.europa.esig.dss.ws.utility.ConfigUtility;

/**
 * Administration controller
 */
@Controller
@RequestMapping(value = "/admin")
public class CRLController {
	
	@RequestMapping(value = "/admin-crl", method = RequestMethod.GET)
	public String showCRL(final Model model) {
		List<X509CRL> listCRL = null;
		Properties props = ConfigUtility.getProperties();
		String pathCRL = props.getProperty("pathCRL");
		listCRL = getCRLList(pathCRL);
		model.addAttribute("crls", listCRL);
		return "admin-crl";
	}

	public X509CRL getCRLFile(String localPath) {
		X509CRL crl = null;
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(localPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		CertificateFactory cf = null;
		try {
			cf = CertificateFactory.getInstance("X509");
		} catch (CertificateException e) {
			e.printStackTrace();
		}

		try {
			crl = (X509CRL) cf.generateCRL(inputStream);
		} catch (CRLException e) {
			e.printStackTrace();
		}
		return crl;

	}

	public ArrayList<X509CRL> getCRLList(String localPathCRL) {
		ArrayList<X509CRL> listCRL = new ArrayList<X509CRL>();

		File folder = new File(localPathCRL);
		ArrayList<File> files = new ArrayList<File>(Arrays.asList(folder.listFiles()));
		X509CRL crl = null;
		for (File file : files) {
			crl = getCRLFile(file.getAbsolutePath());
			if (crl != null)
				listCRL.add(crl);

		}
		return listCRL;
	}

}
