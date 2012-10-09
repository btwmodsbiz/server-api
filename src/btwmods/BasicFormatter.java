package btwmods;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class BasicFormatter extends Formatter {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z");

	@Override
	public String format(LogRecord record) {
		return "[" + dateFormat.format(new Date()) + "] " + record.getMessage() + "\n";
	}
}