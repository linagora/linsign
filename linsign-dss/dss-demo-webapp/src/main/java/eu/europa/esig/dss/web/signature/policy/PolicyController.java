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
package eu.europa.esig.dss.web.signature.policy;

import java.io.File;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

import com.google.gson.Gson;

import eu.europa.esig.dss.web.signature.policy.SignPolicies.Policy;
import eu.europa.esig.dss.web.signature.policy.SignPolicies.Policy.Signature;
import eu.europa.esig.dss.ws.utility.ConfigUtility;

/**
 * Administration controller
 */
@Controller
@RequestMapping(value = "/admin")
public class PolicyController {
	
	private Properties props = ConfigUtility.getProperties();
	private String pathPolicy = props.getProperty("pathPolicy");		
	

	@RequestMapping(value = "/admin-policy-delete", method = RequestMethod.GET, params = {"policyOID"})
	public String showUpdatePolicy(final Model model, @RequestParam(value="policyOID") String policyOID) {
		List<Policy> listPolicies = null;
		File file = new File(pathPolicy + "/linsign-signature-policies.xml");
		SignPolicies policiesLocal = new SignPolicies();
		JAXBContext jaxbContext;
		
		 try {
			jaxbContext = JAXBContext.newInstance(SignPolicies.class);
			policiesLocal = (SignPolicies)jaxbContext.createUnmarshaller().unmarshal(file);
			listPolicies = (List<Policy>) policiesLocal.getPolicy();						
		  } catch (JAXBException e) {
			e.printStackTrace();
		  }
		 
		 for (int i=0; i < listPolicies.size(); i++ )
			 if (listPolicies.get(i).getOid().equals(policyOID)) {
				 listPolicies.remove(i);
				 break;
				 
			 }
				 
		 try {
			jaxbContext = JAXBContext.newInstance(SignPolicies.class);			 
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(policiesLocal, file);
			jaxbMarshaller.marshal(policiesLocal, System.out);
		  } catch (JAXBException e) {
			e.printStackTrace();
		  }
		 			
		model.addAttribute("policies", listPolicies);
		return "redirect:/admin/admin-policy";
	}
	
	@RequestMapping(value = "/admin-policy", method = RequestMethod.GET)
	public String showPolicy(final Model model) {
		List<Policy> listPolicies = null;
		SignPolicies policiesLocal = new SignPolicies();
		try {
			File file = new File(pathPolicy + "/linsign-signature-policies.xml");			 
			JAXBContext jaxbContext = JAXBContext.newInstance(SignPolicies.class);
			policiesLocal = (SignPolicies)jaxbContext.createUnmarshaller().unmarshal(file);			
		  } catch (JAXBException e) {
			e.printStackTrace();
		}

		listPolicies = policiesLocal.getPolicy();
		
		model.addAttribute("policies", listPolicies);
		return "admin-policy";
	}

	
	
	@RequestMapping(value = "/admin-policy-edit", method = RequestMethod.GET)
	public String showPolicyEdit(final Model model) {
		return "admin-policy-edit";
	}

	@RequestMapping(value = "/admin-policy-add", method = RequestMethod.GET)
	public String showPolicyAdd(final Model model) {
		return "admin-policy-add";
	}

	
	@ModelAttribute("policyFormUpdate")
	public  SignPolicies.Policy setupFormUpdate(final WebRequest webRequest) {
		String policyOID = webRequest.getParameter("policyOID");
		SignPolicies.Policy form = new SignPolicies.Policy();
		SignPolicies policiesLocal = null;
		
		try {
			File file = new File(pathPolicy + "/linsign-signature-policies.xml");			 
			JAXBContext jaxbContext = JAXBContext.newInstance(SignPolicies.class);
			policiesLocal = (SignPolicies)jaxbContext.createUnmarshaller().unmarshal(file);			
		  } catch (JAXBException e) {
			e.printStackTrace();
		}		
		List<Policy> listPolicies = (List<Policy>) policiesLocal.getPolicy();
		 
		for (int i=0; i < listPolicies.size(); i++ )
			 if (listPolicies.get(i).getOid().equals(policyOID)) {
				 form = listPolicies.get(i);
				 break;				 
			 }		
		return form;
	}	
	
	@ModelAttribute("policyFormAdd")
	public  SignPolicies.Policy setupFormAdd(final Model model, final WebRequest webRequest) {
		String policyOID = webRequest.getParameter("policyOID");
		SignPolicies.Policy form = new SignPolicies.Policy();
		SignPolicies policiesLocal = null;
		
		try {
			File file = new File(pathPolicy + "/linsign-signature-policies.xml");			 
			JAXBContext jaxbContext = JAXBContext.newInstance(SignPolicies.class);
			policiesLocal = (SignPolicies)jaxbContext.createUnmarshaller().unmarshal(file);			
		  } catch (JAXBException e) {
			e.printStackTrace();
		}		
		List<Policy> listPolicies = (List<Policy>) policiesLocal.getPolicy();
		 
		for (int i=0; i < listPolicies.size(); i++ )
			 if (listPolicies.get(i).getOid().equals(policyOID)) {
				 form = listPolicies.get(i);
				 break;				 
			 }		
		
		String policiesJson = new Gson().toJson(listPolicies);							
		model.addAttribute("policiesJson", policiesJson);		
		model.addAttribute("policies", listPolicies);
		return form;
	}	

	
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public String updatePolicies(final Model model, @ModelAttribute("policyFormUpdate") final SignPolicies.Policy form) 
	{		
		Signature sign = form.getSignature();	
		Policy policy = new Policy();
		policy.setSignature(sign);
		policy.setOid(form.getOid());		
		policy.setLabel(form.getLabel());		
		policy.setDocument(form.getDocument());	

		File file = new File(pathPolicy + "/linsign-signature-policies.xml");
		SignPolicies policiesLocal = new SignPolicies();
		JAXBContext jaxbContext;
		List<Policy> listPolicy = null;
		String oid = (String)form.getOid();
		
		 try {
			jaxbContext = JAXBContext.newInstance(SignPolicies.class);
			policiesLocal = (SignPolicies)jaxbContext.createUnmarshaller().unmarshal(file);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			listPolicy = (List<Policy>) policiesLocal.getPolicy();
		  } catch (JAXBException e) {
			e.printStackTrace();
		  }
			
		  for (int i=0; i < listPolicy.size(); i++ )
			  if (listPolicy.get(i).getOid().equals(oid)) {
				 listPolicy.remove(i);
				 listPolicy.add(i, policy);
				 break;				 
			 }					

		try {
			jaxbContext = JAXBContext.newInstance(SignPolicies.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();			
			jaxbMarshaller.marshal(policiesLocal, file);
			jaxbMarshaller.marshal(policiesLocal, System.out);

		     } catch (JAXBException e) {
		    	  e.printStackTrace();
		      }		
		model.addAttribute("policies", listPolicy);
		return "admin-policy";		
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String addPolicy(@ModelAttribute("policyFormAdd") final SignPolicies.Policy form) 
	{		
		Signature sign = form.getSignature();	
		Policy policy = new Policy();
		policy.setSignature(sign);
		policy.setOid(form.getOid());
		
		policy.setLabel(form.getLabel());		
		policy.setDocument(form.getDocument());
				
		try {
			File file = new File(pathPolicy + "/linsign-signature-policies.xml");
			JAXBContext jaxbContext = JAXBContext.newInstance(SignPolicies.class);
			SignPolicies policiesLocal = (SignPolicies)jaxbContext.createUnmarshaller().unmarshal(file);

			List<Policy> list = (List<Policy>) policiesLocal.getPolicy();
			list.add(policy);

			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(policiesLocal, file);
			jaxbMarshaller.marshal(policiesLocal, System.out);

		  } catch (JAXBException e) {
		    	  e.printStackTrace();
		  }
				
		return "redirect:/admin/admin-policy";
	}
}
