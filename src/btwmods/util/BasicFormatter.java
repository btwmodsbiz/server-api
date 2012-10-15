package btwmods.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class BasicFormatter extends Formatter {
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z");

	@Override
	public String format(LogRecord record) {
		return "[" + dateFormat.format(new Date()) + "] " + record.getMessage() + "\n";
	}
}