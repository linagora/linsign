package eu.europa.esig.dss.web.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class FilterRules {

	private boolean isSucces = false;
	private boolean isError = false;
	private boolean isInfos = false;

	private String issuer;
	private String certificat;
	private String dateAfterStr;
	private String hoursAfter;
	private String dateBeforeStr;
	private String hoursBefore;

	private Date dateAfter = null;
	private Date dateBefore = null;

	private int countRules = 0;
	private boolean statusRules = false;

	private String errorLogPrinter = null;

	public FilterRules() {
		super();
	}

	public void setFilter(String key, String value) {
		if (value == null || value.isEmpty())
			return;
		if (key.equals("issuer"))
			issuer = value.toLowerCase();
		else if (key.equals("certificat"))
			certificat = value.toLowerCase();
		else if (key.equals("dateafter"))
			dateAfterStr = value.toLowerCase();
		else if (key.equals("datebefore"))
			dateBeforeStr = value.toLowerCase();
		else if (key.equals("hoursafter"))
			hoursAfter = value.toLowerCase();
		else if (key.equals("hoursbefore"))
			hoursBefore = value.toLowerCase();
		else if (key.equals("succes"))
			isSucces = true;
		else if (key.equals("error"))
			isError = true;
		else if (key.equals("infos"))
			isInfos = true;
	}

	public void generateRulesNumber() {
		countRules = 0;
		statusRules = false;
		if (isError || isSucces || isInfos)
			statusRules = true;

		if (issuer != null)
			++countRules;
		if (certificat != null)
			++countRules;
		if (dateAfterStr != null)
			++countRules;
		if (dateBeforeStr != null)
			++countRules;

		Pattern pattern = Pattern.compile("^([0-9]|0[0-9]|1[0-9]|2[0-3]):([0-5][0-9]|[0-9])$");
		if (hoursAfter == null || !pattern.matcher(hoursAfter).find())
			hoursAfter = "00:00";
		if (hoursBefore == null || !pattern.matcher(hoursBefore).find())
			hoursBefore = "00:00";

		try {
			if (dateBeforeStr != null)
				dateBefore = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSSSSS")
						.parse(dateBeforeStr + " " + hoursBefore + ":00.000000");
			if (dateAfterStr != null)
				dateAfter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSSSSS")
						.parse(dateAfterStr + " " + hoursAfter + ":00.000000");

			if(dateBefore != null && dateAfter != null && dateBefore.before(dateAfter)){
				dateBefore = null;
				--countRules;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			errorLogPrinter = e.getMessage();
		}
	}

	public int getCountRules() {
		return countRules;
	}

	public boolean isStatusRules() {
		return statusRules;
	}

	public boolean isSucces() {
		return isSucces;
	}

	public void setSucces(boolean isSucces) {
		this.isSucces = isSucces;
	}

	public boolean isError() {
		return isError;
	}

	public void setError(boolean isError) {
		this.isError = isError;
	}

	public boolean isInfos() {
		return isInfos;
	}

	public void setInfos(boolean isInfos) {
		this.isInfos = isInfos;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getCertificat() {
		return certificat;
	}

	public String getHoursAfter() {
		return hoursAfter;
	}

	public void setHoursAfter(String hoursAfter) {
		this.hoursAfter = hoursAfter;
	}

	public String getHoursBefore() {
		return hoursBefore;
	}

	public void setHoursBefore(String hoursBefore) {
		this.hoursBefore = hoursBefore;
	}

	public void setCountRules(int countRules) {
		this.countRules = countRules;
	}

	public void setStatusRules(boolean statusRules) {
		this.statusRules = statusRules;
	}

	public void setCertificat(String certificat) {
		this.certificat = certificat;
	}

	public String getDateAfterStr() {
		return dateAfterStr;
	}

	public void setDateAfterStr(String dateAfterStr) {
		this.dateAfterStr = dateAfterStr;
	}

	public String getDateBeforeStr() {
		return dateBeforeStr;
	}

	public void setDateBeforeStr(String dateBeforeStr) {
		this.dateBeforeStr = dateBeforeStr;
	}

	public Date getDateAfter() {
		return dateAfter;
	}

	public void setDateAfter(Date dateAfter) {
		this.dateAfter = dateAfter;
	}

	public Date getDateBefore() {
		return dateBefore;
	}

	public void setDateBefore(Date dateBefore) {
		this.dateBefore = dateBefore;
	}

	@Override
	public String toString() {
		return "FilterRules [isSucces=" + isSucces + ", isError=" + isError + ", isInfos=" + isInfos + ", issuer="
				+ issuer + ", certificat=" + certificat + ", dateAfterStr=" + dateAfterStr + ", hoursAfter="
				+ hoursAfter + ", dateBeforeStr=" + dateBeforeStr + ", hoursBefore=" + hoursBefore + ", dateAfter="
				+ dateAfter + ", dateBefore=" + dateBefore + ", countRules=" + countRules + ", statusRules="
				+ statusRules + ", errorLogPrinter=" + errorLogPrinter + "]";
	}

}
