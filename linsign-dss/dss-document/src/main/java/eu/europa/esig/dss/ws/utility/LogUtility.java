package eu.europa.esig.dss.ws.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtility {

	private final boolean printLog;
	private final Logger LOG = LoggerFactory.getLogger(LogUtility.class);

	public LogUtility() {
		printLog = ConfigUtility.isLogTrace();
	}
	
	public void printInfo(String msg) {
		if (printLog) {
			LOG.info(msg);
		}
	}

	public void printError(String msg) {
		if (printLog) {
			LOG.error(msg);
		}
	}

	public void printData(String function, String msg) {
		if (printLog) {
			LOG.info("Log information in function : " + function);
			LOG.info("\t" + msg);
		}
	}

	public void printDataWithObject(String function, String msg, String variableName, Object obj) {
		if (printLog) {
			LOG.info("Log information in function : " + function);
			LOG.info("\t" + msg);
			if (obj == null)
				LOG.info("\tThe " + variableName + " is null");
			else
				LOG.info("\tValue of " + variableName + " is " + obj.toString());
		}
	}

	public void printObject(String function, String variableName, Object obj) {
		if (printLog) {
			LOG.info("Log information in function : " + function);
			if (obj == null)
				LOG.info("\tThe " + variableName + " is null");
			else
				LOG.info("\tValue of " + variableName + " is " + obj.toString());
		}
	}
}
