package eu.europa.esig.dss.web.log;

import java.io.IOException;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.EncoderBase;
import eu.europa.esig.dss.ws.impl.LinsignLogs;

public class LinSignLogEncoder extends EncoderBase<ILoggingEvent> {

	private final Marshaller marshaler;

	public LinSignLogEncoder() throws JAXBException, IOException {
		JAXBContext jaxbContext = JAXBContext.newInstance(LinsignLogs.Log.class);
		this.marshaler = jaxbContext.createMarshaller();
	}

	@Override
	public void doEncode(ILoggingEvent event) throws IOException {
		if (outputStream != null && event.getArgumentArray().length > 0
				&& event.getArgumentArray()[0] instanceof LinsignLogs.Log) {
			LinsignLogs.Log log = (LinsignLogs.Log) event.getArgumentArray()[0];
			log.setDate(new Date(event.getTimeStamp()).toString());

			StringBuilder builder = new StringBuilder();
			builder.append("<log date=\"" + log.getDate() + "\">");
			builder.append("<status>" + log.getStatus() + "</status>");
			builder.append("<operation>" + log.getOperation() + "</operation>");
			builder.append("<user>" + log.getUser() + "</user>");
			builder.append("<info>" + log.getInfo() + "</info>");
			builder.append("</log>");
			
			outputStream.write(builder.toString().getBytes());
			outputStream.flush();
		}
	}

	@Override
	public void close() throws IOException {
		if (outputStream != null) {
			outputStream.flush();
		}
	}
}
