package btwmods;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Util {
	public static final DecimalFormat DECIMAL_FORMAT_3 = new DecimalFormat("0.000");
	public static final DecimalFormat DECIMAL_FORMAT_2 = new DecimalFormat("0.00");
	public static final DecimalFormat DECIMAL_FORMAT_1 = new DecimalFormat("0.0");
	public static final DecimalFormat DECIMAL_FORMAT_3MAX = new DecimalFormat("0.###");
	public static final DecimalFormat DECIMAL_FORMAT_2MAX = new DecimalFormat("0.##");
	public static final DecimalFormat DECIMAL_FORMAT_1MAX = new DecimalFormat("0.#");
	
	public static String getStackTrace() {
		return getStackTrace(new Throwable(""));
	}
	
	/**
	 * @see <a href="http://stackoverflow.com/a/1069342">Original source</a>
	 */
	@SuppressWarnings("javadoc")
	public static String getStackTrace(Throwable e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
	
	public static void printStackTrace(PrintWriter writer) {
		printStackTrace(writer, new Throwable(""));
	}
	
	public static void printStackTrace(PrintWriter writer, Throwable e) {
		e.printStackTrace(writer);
	}

	public static void printStackTrace(PrintStream out) {
		printStackTrace(out, new Throwable(""));
	}
	
	public static void printStackTrace(PrintStream out, Throwable e) {
		printStackTrace(new PrintWriter(out), e);
	}

	public static int getWorldIndexFromDimension(int dimension) {
		if (dimension == -1) return 1;
		else if (dimension == 1) return 2;
		return 0;
	}
	
	public static String convertStackTrace(StackTraceElement[] elements) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);

		for (int i = 0; i < elements.length; i++)
			printWriter.println("\tat " + elements[i]);

		printWriter.flush();
		printWriter.close();
		return stringWriter.toString();
	}
	
	public static List<String> combineIntoMaxLengthMessages(List<String> parts, int maxMessageLength) {
		return combineIntoMaxLengthMessages(parts, maxMessageLength, null, false);
	}
	
	public static List<String> combineIntoMaxLengthMessages(List<String> parts, int maxMessageLength, String separator, boolean separatorBetweenPages) {
		if (separator == null) separator = "";
		
		ArrayList<String> messages = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < parts.size(); i++) {
			boolean lastHadSeparator = i - 1 < parts.size() - 1;
			boolean addSeparator = separator.length() != 0 && i < parts.size() - 1;
			
			if (sb.length() != 0 && sb.length() + parts.get(i).length() + (addSeparator ? separator.length() : 0) > maxMessageLength) {
				if (lastHadSeparator && !separatorBetweenPages)
					messages.add(sb.toString().substring(0, sb.length() - separator.length()));
				else
					messages.add(sb.toString());
				
				sb.setLength(0);
			}
			
			sb.append(parts.get(i));
			if (addSeparator) sb.append(separator);
		}
		
		if (sb.length() > 0)
			messages.add(sb.toString());
		
		return messages;
	}
}
