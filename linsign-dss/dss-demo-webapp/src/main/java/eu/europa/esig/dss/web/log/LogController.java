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
package eu.europa.esig.dss.web.log;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eu.europa.esig.dss.web.dao.FilterRules;
import eu.europa.esig.dss.web.log.LinsignLogs.Log;
import eu.europa.esig.dss.ws.utility.ConfigUtility;

/**
 * Administration controller
 */
@Controller
@RequestMapping(value = "/admin")
public class LogController {

	@Qualifier("signatureLog")
	private final DateFormat parserSDF = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
	private List<Log> listLogsAll = null;
	private final int recordsPerPage = 15;

	private List<Log> selectByOffset(int noPages) {
		int itemRecord = 0;
		List<Log> listLogs = new ArrayList<Log>();

		for (int i = noPages * recordsPerPage; i < listLogsAll.size(); i++) {

			listLogs.add(listLogsAll.get(i));
			itemRecord++;
			if (itemRecord == recordsPerPage)
				break;
		}

		return listLogs;
	}

	@RequestMapping(value = "/admin-log?page=${page}", method = RequestMethod.GET)
	public String doGet(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "page") int page) throws ServletException, IOException {
		if (request.getParameter("page") != null)
			page = Integer.parseInt(request.getParameter("page"));
		else
			page = 0;

		
		request.setAttribute("logs", selectByOffset(page));
		request.setAttribute("page", page);
		RequestDispatcher view = request.getRequestDispatcher("admin-log");
		view.forward(request, response);

		return "admin-log";
	}

	public class CustomComparatorLogDate implements Comparator<Log> {
		@Override
		public int compare(Log o1, Log o2) {
			if (o1 == null || o2 == null)
				return 0;
			try {
				Date date1 = parserSDF.parse(o1.getDate());
				Date date2 = parserSDF.parse(o2.getDate());
				return date2.compareTo(date1);
			} catch (ParseException e) {
				return 0;
			}

		}
	}

	@RequestMapping(value = "/admin-log-form", method = RequestMethod.GET)
	public String sortLog(final Model model) {
		return "admin-log-form";
	}

	@RequestMapping(value = "/admin-log", method = RequestMethod.POST)
	public String showLog(final Model model, HttpServletRequest request) {
		Properties props = ConfigUtility.getProperties();
		String pathLog = props.getProperty("pathLog");
		listLogsAll = new ArrayList<Log>();

		Map<String, String[]> parameters = request.getParameterMap();
		FilterRules filter = new FilterRules();

		for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
			String key = entry.getKey();
			filter.setFilter(key, request.getParameter(key));
		}
		filter.generateRulesNumber();

		File folder = new File(pathLog);
		ArrayList<File> files = new ArrayList<File>(Arrays.asList(folder.listFiles()));
		for (File file : files) {
			listLogsAll.addAll(getFileLog(file.getAbsolutePath()));
		}

		listLogsAll = applyFilters(filter, listLogsAll);

		Collections.sort(listLogsAll, new CustomComparatorLogDate());

		int noOfPages = (int) Math.ceil(listLogsAll.size() * 1.0 / recordsPerPage);
		model.addAttribute("noOfPages", noOfPages);
		model.addAttribute("logs", selectByOffset(0));
		return "admin-log";
	}

	public List<Log> applyFilters(final FilterRules filter, List<Log> listLogs) {
		boolean statusChecker = false;
		int countRules = 0;

		for (Iterator<Log> iterator = listLogs.iterator(); iterator.hasNext();) {
			Log currentLog = iterator.next();

			if (filter.getCertificat() != null && currentLog.info.toLowerCase().contains(filter.getCertificat()))
				++countRules;
			if (filter.getIssuer() != null && currentLog.user.toLowerCase().contains(filter.getIssuer()))
				++countRules;
			String dateLog = currentLog.getDate();

			try {
				if (filter.getDateAfter() != null && parserSDF.parse(dateLog).after(filter.getDateAfter()))
					++countRules;
			} catch (ParseException e) {
				e.printStackTrace();
			}

			try {
				if (filter.getDateBefore() != null && parserSDF.parse(dateLog).before(filter.getDateBefore()))
					++countRules;
			} catch (ParseException e) {
				e.printStackTrace();
			}

			if ((filter.isError() == true && currentLog.status.equals("ERROR"))
					|| (filter.isSucces() == true && currentLog.status.equals("SUCCES"))
					|| (filter.isInfos() == true && currentLog.status.equals("INFOS")))
				statusChecker = true;

			if (!(statusChecker == filter.isStatusRules() && countRules == filter.getCountRules()))
				iterator.remove();

			statusChecker = false;
			countRules = 0;
		}
		return listLogs;

	}

	public List<Log> getFileLog(String localPath) {
		LinsignLogs logs = new LinsignLogs();
		try {
			File file = new File(localPath);
			JAXBContext jaxbContext = JAXBContext.newInstance(LinsignLogs.class);

			String myLinsignLog = new String(Files.readAllBytes(Paths.get(localPath)));

			if (!myLinsignLog.contains("<linsign-logs>")) { // UnMarshall new
															// file log
				myLinsignLog = "<linsign-logs>" + myLinsignLog + "</linsign-logs>";
				StringReader linsignLogReader = new StringReader(myLinsignLog);
				logs = (LinsignLogs) jaxbContext.createUnmarshaller().unmarshal(linsignLogReader);
			} else { // UnMarshall old file log
				logs = (LinsignLogs) jaxbContext.createUnmarshaller().unmarshal(file);
			}

		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<Log> listLogs = (List<Log>) logs.getLog();
		return listLogs;
	}
}
