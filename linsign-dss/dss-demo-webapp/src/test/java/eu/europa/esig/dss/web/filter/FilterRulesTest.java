package eu.europa.esig.dss.web.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import eu.europa.esig.dss.web.dao.FilterRules;

public class FilterRulesTest {

	private FilterRules filter;

	@Before
	public void init() {
		filter = new FilterRules();
	}

	@Test
	public void noRules() {
		assertNotNull(filter);
		filter.generateRulesNumber();

		assertEquals(0, filter.getCountRules());
		assertEquals(false, filter.isStatusRules());
	}

	
	@Test
	public void statusOnly() {
		filter.setFilter("succes", "on");
		filter.setFilter("infos", "on");
		filter.generateRulesNumber();

		assertEquals(0, filter.getCountRules());
		assertEquals(true, filter.isStatusRules());
	}

	
	@Test
	public void twoRules() {
		filter.setFilter("issuer", "An user");
		filter.setFilter("certificat", "An certificat");
		filter.generateRulesNumber();

		assertNotNull(filter);
		assertEquals(2, filter.getCountRules());
		assertEquals(false, filter.isStatusRules());
	}

	@Test
	public void twoRulesWithStatus() {
		filter.setFilter("issuer", "An user");
		filter.setFilter("succes", "on");
		filter.generateRulesNumber();

		assertNotNull(filter);
		assertEquals(1, filter.getCountRules());
		assertEquals(true, filter.isStatusRules());
	}

	@Test
	public void dateFilter() throws ParseException {
		filter.setFilter("dateafter", "07/18/2017");
		filter.setFilter("hoursafter", "2:9");
		filter.generateRulesNumber();

		assertNotNull(filter);
		assertEquals(1, filter.getCountRules());
		assertEquals(false, filter.isStatusRules());
		assertEquals(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSSSSS").parse("07/18/2017 2:9:00.000000"),
				filter.getDateAfter());
	}

	@Test
	public void dateFilterBetween() throws ParseException {
		filter.setFilter("dateafter", "07/18/2017");
		filter.setFilter("hoursafter", "2:9");

		filter.setFilter("datebefore", "09/19/2019");
		filter.setFilter("hoursbefore", "23:59");
		filter.generateRulesNumber();

		assertNotNull(filter);
		assertEquals(2, filter.getCountRules());
		assertEquals(false, filter.isStatusRules());

		assertEquals("07/18/2017", filter.getDateAfterStr());
		assertEquals("2:9", filter.getHoursAfter());

		assertEquals("09/19/2019", filter.getDateBeforeStr());
		assertEquals("23:59", filter.getHoursBefore());

		assertEquals(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSSSSS").parse("07/18/2017 2:9:00.000000"),
				filter.getDateAfter());

		assertEquals(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSSSSS").parse("09/19/2019 23:59:00.000000"),
				filter.getDateBefore());
	}

	@Test
	public void wrongKey() {
		filter.setFilter("fakeKey", "Some randome value");
		filter.setFilter("fakeKeyBis", "07/18/2017");
		filter.generateRulesNumber();

		assertEquals(0, filter.getCountRules());
		assertEquals(false, filter.isStatusRules());
	}
	
	@Test
	public void fakeHours() {
		filter.setFilter("dateafter", "07/18/2017");
		filter.setFilter("datebefore", "09/19/2019");

		filter.setFilter("hoursafter", "29:52");
		filter.setFilter("hoursbefore", "6:92");

		filter.generateRulesNumber();

		assertEquals(2, filter.getCountRules());
		assertEquals(false, filter.isStatusRules());
		
		assertEquals("00:00", filter.getHoursAfter());
		assertEquals("00:00", filter.getHoursBefore());
	}
	
	@After
	public void tearDown() {
		filter = null;
	}

}
