package eu.europa.esig.dss.ws.utility;

import java.util.Calendar;

import eu.europa.esig.dss.TSLConstant;
import eu.europa.esig.dss.tsl.ServiceInfo;

public class LngServiceInfos extends ServiceInfo {

	public LngServiceInfos() {

		setTspName("DSS, Mock Office DSS-CA");
		//setType(TSLConstant.CA_QC);
		setType(TSLConstant.QC_NO_SSCD);
		setServiceName("Local Certificate");
		setStatus(TSLConstant.SERVICE_STATUS_UNDERSUPERVISION_119612);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -15);
		setStatusStartDate(calendar.getTime());
		setStatusEndDate(null);
		setTlWellSigned(true);
	}
}