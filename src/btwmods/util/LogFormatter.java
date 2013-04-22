package btwmods.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import btwmods.Util;

public class LogFormatter extends Formatter {
	@Override
	public String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();
		sb.append(Util.DATE_FORMAT_LOGS.format(record.getMillis()));
		
		sb.append(" [").append(record.getLevel().getName()).append("] ")
			.append(formatMessage(record)).append('\n');
		
		Throwable throwable = record.getThrown();
		if (throwable != null) {
			StringWriter stringWriter = new StringWriter();
			throwable.printStackTrace(new PrintWriter(stringWriter));
			sb.append(stringWriter.toString());
		}
		
		return sb.toString();
	}
}