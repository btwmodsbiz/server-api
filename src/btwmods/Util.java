package btwmods;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Util {
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
}
