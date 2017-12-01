package eu.europa.esig.dss.web.filter;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.validation.constraints.AssertFalse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mchange.util.AssertException;

import eu.europa.esig.dss.web.dao.FilterRules;
import eu.europa.esig.dss.web.log.LinsignLogs.Log;
import eu.europa.esig.dss.web.log.LogController;

public class LogFilterTest {

	private final String logOneFile = "/linsign_log.2017-07-17.xml";
	private final String logTwoFile = "/linsign_log.2017-07-18.xml";
	private final String newLogFile = "/new_linsign_log.2017-06-21.xml";

	private final int numberLogFileOne = 6917;
	private final int numberLogFileTwo = 5980;
	private final int numberNewLogFile = 36;

	private final int totalErrorLog = 11;
	private final int totalSuccesLog = 12919;
	private final int totalInfosLog = 3;

	private FilterRules filter;
	private LogController logerFilterCtrl;
	private List<Log> listLogs;

	@Before
	public void init() {
		filter = new FilterRules();
		logerFilterCtrl = new LogController();
		listLogs = new ArrayList<Log>();
	}

	private void loadLogFile(boolean fileOne, boolean fileTwo, boolean newLog) {
		File resourcesDirectory = new File("src/test/resources/logs");
		String ressourcePath = resourcesDirectory.getAbsolutePath();
		if (fileOne)
			listLogs.addAll(logerFilterCtrl.getFileLog(ressourcePath + logOneFile));
		if (fileTwo)
			listLogs.addAll(logerFilterCtrl.getFileLog(ressourcePath + logTwoFile));
		if (newLog)
			listLogs.addAll(logerFilterCtrl.getFileLog(ressourcePath + newLogFile));
	}

	// numberRules Code
	// 1 : Status Code - Succes | Error | Infos
	// 2 : Issuer
	// 3 : Certificat
	// 4 : Date After
	// 5 : Date Before

	private boolean checkRules(int numberRules, String compareValue) {
		boolean validRules = true;
		for (Log log : listLogs) {
			switch (numberRules) {
			case 1:
				assertEquals(true, compareValue.contains(log.getStatus().toLowerCase()));
				break;
			case 2:
				assertEquals(true, log.getUser().toLowerCase().contains(compareValue));
				break;
			case 3:
				assertEquals(true, log.getInfo().toLowerCase().contains(compareValue));
				break;
			case 4:
				assertEquals(true, generateDate(log.getDate(), true).after(generateDate(compareValue, false)));
				break;
			case 5:
				assertEquals(true, generateDate(log.getDate(), true).before(generateDate(compareValue, false)));
				break;
			default:
				return false;
			}
		}
		return validRules;

	}

	private Date generateDate(String dateValue, boolean formatLog) {
		try {
			if (formatLog)
				return new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH).parse(dateValue);
			else
				return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSSSSS").parse(dateValue + ":00.000000");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		assertEquals(true, false);
		return null;
	}

	@Test
	public void filterStatusError() {
		filter.setFilter("error", "on");
		filter.generateRulesNumber();
		loadLogFile(true, true, true);
		logerFilterCtrl.applyFilters(filter, listLogs);

		assertEquals(true, checkRules(1, "error"));
		assertEquals(totalErrorLog, listLogs.size());
	}

	@Test
	public void filterStatusSucces() {
		filter.setFilter("succes", "on");
		filter.generateRulesNumber();
		loadLogFile(true, true, true);
		logerFilterCtrl.applyFilters(filter, listLogs);
		assertEquals(true, checkRules(1, "succes"));
		assertEquals(totalSuccesLog, listLogs.size());
	}

	@Test
	public void filterStatusInfos() {
		filter.setFilter("infos", "on");
		filter.generateRulesNumber();
		loadLogFile(true, true, true);
		logerFilterCtrl.applyFilters(filter, listLogs);

		assertEquals(true, checkRules(1, "infos"));
		assertEquals(totalInfosLog, listLogs.size());
	}

	@Test
	public void filterStatusSuccesAndInfos() {
		filter.setFilter("succes", "on");
		filter.setFilter("infos", "on");
		filter.generateRulesNumber();
		loadLogFile(true, true, true);
		logerFilterCtrl.applyFilters(filter, listLogs);

		assertEquals(true, checkRules(1, "infos - succes"));
		assertEquals(totalInfosLog + totalSuccesLog, listLogs.size());
	}

	@Test
	public void filterStatusErrorAndInfos() {
		filter.setFilter("error", "on");
		filter.setFilter("infos", "on");
		filter.generateRulesNumber();
		loadLogFile(true, true, true);
		logerFilterCtrl.applyFilters(filter, listLogs);

		assertEquals(true, checkRules(1, "infos - error"));
		assertEquals(totalInfosLog + totalErrorLog, listLogs.size());
	}

	@Test
	public void filterStatusErrorAndSucces() {
		filter.setFilter("succes", "on");
		filter.setFilter("error", "on");
		filter.generateRulesNumber();
		loadLogFile(true, true, true);
		logerFilterCtrl.applyFilters(filter, listLogs);

		assertEquals(true, checkRules(1, "succes - error"));
		assertEquals(totalSuccesLog + totalErrorLog, listLogs.size());
	}

	@Test
	public void filterAllStatus() {
		filter.setFilter("succes", "on");
		filter.setFilter("error", "on");
		filter.setFilter("infos", "on");
		filter.generateRulesNumber();
		loadLogFile(true, true, true);
		logerFilterCtrl.applyFilters(filter, listLogs);

		assertEquals(true, checkRules(1, "succes - error - infos"));
		assertEquals(numberLogFileOne + numberLogFileTwo + numberNewLogFile, listLogs.size());
	}

	@Test
	public void filterUser() {
		String keyValue = "maryse bertossi";
		filter.setFilter("issuer", keyValue);
		filter.generateRulesNumber();
		loadLogFile(true, true, true);

		logerFilterCtrl.applyFilters(filter, listLogs);
		assertEquals(true, checkRules(2, keyValue));
		assertEquals(1, listLogs.size());
	}

	@Test
	public void filterUserWithStatusError() {
		String keyValue = "maryse bertossi";

		filter.setFilter("error", "on");
		filter.setFilter("issuer", keyValue);
		filter.generateRulesNumber();
		loadLogFile(true, true, true);

		logerFilterCtrl.applyFilters(filter, listLogs);
		assertEquals(true, checkRules(1, "error"));
		assertEquals(true, checkRules(2, keyValue));
		assertEquals(1, listLogs.size());
	}

	@Test
	public void filterUserWithStatusSucces() {
		String keyValue = "maryse bertossi";

		filter.setFilter("succes", "on");
		filter.setFilter("issuer", keyValue);
		filter.generateRulesNumber();
		loadLogFile(true, true, true);

		logerFilterCtrl.applyFilters(filter, listLogs);
		assertEquals(true, checkRules(1, "succes"));
		assertEquals(true, checkRules(2, keyValue));
		assertEquals(0, listLogs.size());
	}

	@Test
	public void filterUser2() {
		String keyValue = "cn=m dan bendavid, o=bendavid expertise";
		filter.setFilter("issuer", keyValue);
		filter.generateRulesNumber();
		loadLogFile(true, true, true);

		logerFilterCtrl.applyFilters(filter, listLogs);
		assertEquals(true, checkRules(2, keyValue));
		assertEquals(1, listLogs.size());
	}

	@Test
	public void filterUser2WithStatusError() {
		String keyValue = "cn=m dan bendavid, o=bendavid expertise";

		filter.setFilter("error", "on");
		filter.setFilter("infos", "on");
		filter.setFilter("issuer", keyValue);
		filter.generateRulesNumber();
		loadLogFile(true, true, true);

		logerFilterCtrl.applyFilters(filter, listLogs);
		assertEquals(true, checkRules(1, "error - infos"));
		assertEquals(true, checkRules(2, keyValue));
		assertEquals(0, listLogs.size());
	}

	@Test
	public void filterUser2WithStatusSucces() {
		String keyValue = "cn=m dan bendavid";

		filter.setFilter("succes", "on");
		filter.setFilter("issuer", keyValue);
		filter.generateRulesNumber();
		loadLogFile(true, true, true);

		logerFilterCtrl.applyFilters(filter, listLogs);
		assertEquals(true, checkRules(1, "succes"));
		assertEquals(true, checkRules(2, keyValue));
		assertEquals(2, listLogs.size());
	}

	@Test
	public void filterInfos() {
		String keyValue = "cn=signature et authentification - ordre des experts-comptables";

		filter.setFilter("certificat", keyValue);
		filter.generateRulesNumber();
		loadLogFile(true, true, true);

		logerFilterCtrl.applyFilters(filter, listLogs);
		assertEquals(true, checkRules(3, keyValue));
		assertEquals(747, listLogs.size());
	}

	@Test
	public void filterInfosWithStatusError() {
		String keyValue = "cn=signature et authentification - ordre des experts-comptables";

		filter.setFilter("error", "on");
		filter.setFilter("certificat", keyValue);
		filter.generateRulesNumber();
		loadLogFile(true, true, true);

		logerFilterCtrl.applyFilters(filter, listLogs);
		assertEquals(true, checkRules(1, "error"));
		assertEquals(true, checkRules(3, keyValue));
		assertEquals(0, listLogs.size());
	}

	@Test
	public void filterInfosWithStatusSucces() {
		String keyValue = "cn=signature et authentification - ordre des experts-comptables";

		filter.setFilter("succes", "on");
		filter.setFilter("certificat", keyValue);
		filter.generateRulesNumber();
		loadLogFile(true, true, true);

		logerFilterCtrl.applyFilters(filter, listLogs);
		assertEquals(true, checkRules(1, "succes"));
		assertEquals(true, checkRules(3, keyValue));
		assertEquals(747, listLogs.size());
	}

	@Test
	public void filterUserAndInfos() {
		String keyValueUser = "givenname=dan";
		String keyValueInfos = "cn=signature et authentification - ordre des experts-comptables";

		filter.setFilter("issuer", keyValueUser);
		filter.setFilter("certificat", keyValueInfos);
		filter.generateRulesNumber();
		loadLogFile(true, true, true);

		logerFilterCtrl.applyFilters(filter, listLogs);
		assertEquals(true, checkRules(2, keyValueUser));
		assertEquals(true, checkRules(3, keyValueInfos));
		assertEquals(2, listLogs.size());
	}

	@Test
	public void filterUserAndInfos2() {
		String keyValueUser = "givenname=yann";
		String keyValueInfos = "cn=signature et authentification - ordre des experts-comptables";

		filter.setFilter("issuer", keyValueUser);
		filter.setFilter("certificat", keyValueInfos);
		filter.generateRulesNumber();
		loadLogFile(true, true, true);

		logerFilterCtrl.applyFilters(filter, listLogs);
		assertEquals(true, checkRules(2, keyValueUser));
		assertEquals(true, checkRules(3, keyValueInfos));
		assertEquals(23, listLogs.size());
	}

	@Test
	public void filterUser2InfosAndStatusError() {
		String keyValueUser = "givenname=yann";
		String keyValueInfos = "cn=signature et authentification - ordre des experts-comptables";

		filter.setFilter("error", "on");
		filter.setFilter("issuer", keyValueUser);
		filter.setFilter("certificat", keyValueInfos);
		filter.generateRulesNumber();
		loadLogFile(true, true, true);

		logerFilterCtrl.applyFilters(filter, listLogs);
		assertEquals(true, checkRules(1, "error"));
		assertEquals(true, checkRules(2, keyValueUser));
		assertEquals(true, checkRules(3, keyValueInfos));
		assertEquals(0, listLogs.size());
	}

	@Test
	public void filterUser2InfosAndStatusSucces() {
		String keyValueUser = "givenname=yann";
		String keyValueInfos = "cn=signature et authentification - ordre des experts-comptables";

		filter.setFilter("succes", "on");
		filter.setFilter("issuer", keyValueUser);
		filter.setFilter("certificat", keyValueInfos);
		filter.generateRulesNumber();
		loadLogFile(true, true, true);

		logerFilterCtrl.applyFilters(filter, listLogs);
		assertEquals(true, checkRules(1, "succes"));
		assertEquals(true, checkRules(2, keyValueUser));
		assertEquals(true, checkRules(3, keyValueInfos));
		assertEquals(23, listLogs.size());
	}

	@Test
	public void filterDateAfter() {
		String keyValueDate = "07/18/2017";
		String hoursKeyValue = "00:00";

		filter.setFilter("dateafter", keyValueDate);
		filter.generateRulesNumber();
		loadLogFile(true, true, true);

		logerFilterCtrl.applyFilters(filter, listLogs);
		assertEquals(true, checkRules(4, keyValueDate + " " + hoursKeyValue));
		assertEquals(numberLogFileTwo, listLogs.size());
	}

	@Test
	public void filterDateBefore() {
		String keyValueDate = "07/18/2017";
		String hoursKeyValue = "00:00";

		filter.setFilter("datebefore", keyValueDate);
		filter.generateRulesNumber();
		loadLogFile(true, true, true);

		logerFilterCtrl.applyFilters(filter, listLogs);
		assertEquals(true, checkRules(5, keyValueDate + " " + hoursKeyValue));
		assertEquals(numberLogFileOne + numberNewLogFile, listLogs.size());
	}

	@Test
	public void filterDateAfterWithHours() {
		String keyValueDate = "07/17/2017";
		String hoursKeyValue = "20:00";

		filter.setFilter("dateafter", keyValueDate);
		filter.setFilter("hoursafter", hoursKeyValue);

		filter.generateRulesNumber();
		loadLogFile(true, false, false);

		logerFilterCtrl.applyFilters(filter, listLogs);
		assertEquals(true, checkRules(4, keyValueDate + " " + hoursKeyValue));
		assertEquals(43, listLogs.size());
	}

	@Test
	public void filterDateAfterWithHours2() {
		String keyValueDate = "07/17/2017";
		String hoursKeyValue = "23:59";

		filter.setFilter("dateafter", keyValueDate);
		filter.setFilter("hoursafter", hoursKeyValue);

		filter.generateRulesNumber();
		loadLogFile(true, true, true);

		logerFilterCtrl.applyFilters(filter, listLogs);
		assertEquals(true, checkRules(4, keyValueDate + " " + hoursKeyValue));
		assertEquals(numberLogFileTwo, listLogs.size());
	}

	@Test
	public void filterDateBeforeWithHours() {
		String keyValueDate = "07/18/2017";
		String hoursKeyValue = "0:01";

		filter.setFilter("datebefore", keyValueDate);
		filter.setFilter("hoursbefore", hoursKeyValue);

		filter.generateRulesNumber();
		loadLogFile(true, false, false);

		logerFilterCtrl.applyFilters(filter, listLogs);
		assertEquals(true, checkRules(5, keyValueDate + " " + hoursKeyValue));
		assertEquals(numberLogFileOne, listLogs.size());
	}

	@Test
	public void filterDateBeforeWithHours2() {
		String keyValueDate = "07/17/2017";
		String hoursKeyValue = "8:00";

		filter.setFilter("datebefore", keyValueDate);
		filter.setFilter("hoursbefore", hoursKeyValue);

		filter.generateRulesNumber();
		loadLogFile(true, true, true);

		logerFilterCtrl.applyFilters(filter, listLogs);
		assertEquals(true, checkRules(5, keyValueDate + " " + hoursKeyValue));
		assertEquals(132, listLogs.size());
	}

	@Test
	public void filterDateBeforeAfter() {
		String keyValueDateAfter = "07/17/2017";
		String keyValueDateBefore = "07/18/2017";
		String hoursKeyValue = "00:00";

		filter.setFilter("dateafter", keyValueDateAfter);
		filter.setFilter("datebefore", keyValueDateBefore);

		filter.generateRulesNumber();
		loadLogFile(true, true, true);

		logerFilterCtrl.applyFilters(filter, listLogs);
		assertEquals(true, checkRules(4, keyValueDateAfter + " " + hoursKeyValue));
		assertEquals(true, checkRules(5, keyValueDateBefore + " " + hoursKeyValue));

		assertEquals(numberLogFileOne, listLogs.size());
	}

	@Test
	public void filterDateBeforeAfterWithHours() {
		String keyValueDateAfter = "07/18/2017";
		String keyValueDateBefore = "07/19/2017";
		String hoursKeyValueAfter = "23:00";
		String hoursKeyValueBefore = "01:00";

		filter.setFilter("dateafter", keyValueDateAfter);
		filter.setFilter("hoursafter", hoursKeyValueAfter);

		filter.setFilter("datebefore", keyValueDateBefore);
		filter.setFilter("hoursbefore", hoursKeyValueBefore);

		filter.generateRulesNumber();
		loadLogFile(true, true, true);

		logerFilterCtrl.applyFilters(filter, listLogs);
		assertEquals(true, checkRules(4, keyValueDateAfter + " " + hoursKeyValueAfter));
		assertEquals(true, checkRules(5, keyValueDateBefore + " " + hoursKeyValueBefore));

		assertEquals(2, listLogs.size());
	}

	@Test
	public void filterDateBeforeAfterWithHours2() {
		String keyValueDateAfter = "07/18/2017";
		String keyValueDateBefore = "07/18/2017";
		String hoursKeyValueAfter = "11:09";
		String hoursKeyValueBefore = "12:53";

		filter.setFilter("dateafter", keyValueDateAfter);
		filter.setFilter("hoursafter", hoursKeyValueAfter);

		filter.setFilter("datebefore", keyValueDateBefore);
		filter.setFilter("hoursbefore", hoursKeyValueBefore);

		filter.generateRulesNumber();
		loadLogFile(true, true, true);

		logerFilterCtrl.applyFilters(filter, listLogs);
		assertEquals(true, checkRules(4, keyValueDateAfter + " " + hoursKeyValueAfter));
		assertEquals(true, checkRules(5, keyValueDateBefore + " " + hoursKeyValueBefore));

		assertEquals(1070, listLogs.size());
	}

	@Test
	public void filterDateBeforeAfterWithStatusSucces() {
		String keyValueDateAfter = "07/18/2017";
		String keyValueDateBefore = "07/18/2017";
		String hoursKeyValueAfter = "11:09";
		String hoursKeyValueBefore = "12:53";

		filter.setFilter("succes", "on");

		filter.setFilter("dateafter", keyValueDateAfter);
		filter.setFilter("hoursafter", hoursKeyValueAfter);
		filter.setFilter("datebefore", keyValueDateBefore);
		filter.setFilter("hoursbefore", hoursKeyValueBefore);

		filter.generateRulesNumber();
		loadLogFile(true, true, true);

		logerFilterCtrl.applyFilters(filter, listLogs);
		assertEquals(true, checkRules(1, "succes"));
		assertEquals(true, checkRules(4, keyValueDateAfter + " " + hoursKeyValueAfter));
		assertEquals(true, checkRules(5, keyValueDateBefore + " " + hoursKeyValueBefore));
		assertEquals(1067, listLogs.size());
	}

	@Test
	public void filterDateBeforeAfterWithStatusError() {
		String keyValueDateAfter = "07/18/2017";
		String keyValueDateBefore = "07/18/2017";
		String hoursKeyValueAfter = "11:09";
		String hoursKeyValueBefore = "12:53";

		filter.setFilter("error", "on");

		filter.setFilter("dateafter", keyValueDateAfter);
		filter.setFilter("hoursafter", hoursKeyValueAfter);
		filter.setFilter("datebefore", keyValueDateBefore);
		filter.setFilter("hoursbefore", hoursKeyValueBefore);

		filter.generateRulesNumber();
		loadLogFile(true, true, true);

		logerFilterCtrl.applyFilters(filter, listLogs);
		assertEquals(true, checkRules(1, "error"));
		assertEquals(true, checkRules(4, keyValueDateAfter + " " + hoursKeyValueAfter));
		assertEquals(true, checkRules(5, keyValueDateBefore + " " + hoursKeyValueBefore));
		assertEquals(2, listLogs.size());
	}

	@Test
	public void filterDateBeforeAfterUserWithStatusError() {

		String keyValueUser = "christophe michel";
		String keyValueDateAfter = "07/18/2017";
		String keyValueDateBefore = "07/18/2017";
		String hoursKeyValueAfter = "11:09";
		String hoursKeyValueBefore = "12:53";

		filter.setFilter("error", "on");
		filter.setFilter("issuer", keyValueUser);
		filter.setFilter("dateafter", keyValueDateAfter);
		filter.setFilter("hoursafter", hoursKeyValueAfter);
		filter.setFilter("datebefore", keyValueDateBefore);
		filter.setFilter("hoursbefore", hoursKeyValueBefore);

		filter.generateRulesNumber();
		loadLogFile(true, true, true);

		logerFilterCtrl.applyFilters(filter, listLogs);
		assertEquals(true, checkRules(1, "error"));
		assertEquals(true, checkRules(2, keyValueUser));
		assertEquals(true, checkRules(4, keyValueDateAfter + " " + hoursKeyValueAfter));
		assertEquals(true, checkRules(5, keyValueDateBefore + " " + hoursKeyValueBefore));
		assertEquals(1, listLogs.size());
	}

	@Test
	public void filterDateBeforeAfterInfoWithStatusError() {

		String keyValueInfos = "credit lyonnais authentys entreprise";
		String keyValueDateAfter = "07/18/2017";
		String keyValueDateBefore = "07/18/2017";
		String hoursKeyValueAfter = "11:09";
		String hoursKeyValueBefore = "12:53";

		filter.setFilter("error", "on");
		filter.setFilter("certificat", keyValueInfos);
		filter.setFilter("dateafter", keyValueDateAfter);
		filter.setFilter("hoursafter", hoursKeyValueAfter);
		filter.setFilter("datebefore", keyValueDateBefore);
		filter.setFilter("hoursbefore", hoursKeyValueBefore);

		filter.generateRulesNumber();
		loadLogFile(true, true, true);

		logerFilterCtrl.applyFilters(filter, listLogs);
		assertEquals(true, checkRules(1, "error"));
		assertEquals(true, checkRules(3, keyValueInfos));
		assertEquals(true, checkRules(4, keyValueDateAfter + " " + hoursKeyValueAfter));
		assertEquals(true, checkRules(5, keyValueDateBefore + " " + hoursKeyValueBefore));
		assertEquals(1, listLogs.size());
	}

	@Test
	public void allFilterStatusError() {

		String keyValueUser = "christophe michel";
		String keyValueInfos = "credit lyonnais authentys entreprise";
		String keyValueDateAfter = "07/18/2017";
		String keyValueDateBefore = "07/18/2017";
		String hoursKeyValueAfter = "11:09";
		String hoursKeyValueBefore = "12:53";

		filter.setFilter("error", "on");
		filter.setFilter("issuer", keyValueUser);
		filter.setFilter("certificat", keyValueInfos);
		filter.setFilter("dateafter", keyValueDateAfter);
		filter.setFilter("hoursafter", hoursKeyValueAfter);
		filter.setFilter("datebefore", keyValueDateBefore);
		filter.setFilter("hoursbefore", hoursKeyValueBefore);

		filter.generateRulesNumber();
		loadLogFile(true, true, true);

		logerFilterCtrl.applyFilters(filter, listLogs);
		assertEquals(true, checkRules(1, "error"));
		assertEquals(true, checkRules(2, keyValueUser));
		assertEquals(true, checkRules(3, keyValueInfos));
		assertEquals(true, checkRules(4, keyValueDateAfter + " " + hoursKeyValueAfter));
		assertEquals(true, checkRules(5, keyValueDateBefore + " " + hoursKeyValueBefore));
		assertEquals(1, listLogs.size());
	}

	@Test
	public void allFilterStatusSucces() {

		String keyValueUser = "christophe michel";
		String keyValueInfos = "credit lyonnais authentys entreprise";
		String keyValueDateAfter = "07/18/2017";
		String keyValueDateBefore = "07/18/2017";
		String hoursKeyValueAfter = "11:09";
		String hoursKeyValueBefore = "12:53";

		filter.setFilter("succes", "on");
		filter.setFilter("issuer", keyValueUser);
		filter.setFilter("certificat", keyValueInfos);
		filter.setFilter("dateafter", keyValueDateAfter);
		filter.setFilter("hoursafter", hoursKeyValueAfter);
		filter.setFilter("datebefore", keyValueDateBefore);
		filter.setFilter("hoursbefore", hoursKeyValueBefore);

		filter.generateRulesNumber();
		loadLogFile(true, true, true);

		logerFilterCtrl.applyFilters(filter, listLogs);
		assertEquals(true, checkRules(1, "succes"));
		assertEquals(true, checkRules(2, keyValueUser));
		assertEquals(true, checkRules(3, keyValueInfos));
		assertEquals(true, checkRules(4, keyValueDateAfter + " " + hoursKeyValueAfter));
		assertEquals(true, checkRules(5, keyValueDateBefore + " " + hoursKeyValueBefore));
		assertEquals(0, listLogs.size());
	}

	@Test
	public void noFileLoaded() {

		String keyValueUser = "christophe michel";

		filter.setFilter("succes", "on");
		filter.setFilter("issuer", keyValueUser);

		filter.generateRulesNumber();
		loadLogFile(false, false, false);

		logerFilterCtrl.applyFilters(filter, listLogs);
		assertEquals(true, checkRules(1, "succes"));
		assertEquals(true, checkRules(2, keyValueUser));
		assertEquals(0, listLogs.size());
	}

	@After
	public void tearDown() {
		filter = null;
		logerFilterCtrl = null;
		listLogs.clear();
	}

}
