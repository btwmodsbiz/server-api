package btwmods;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityList;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityXPOrb;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.Packet3Chat;

public class Util {
	public static final String COLOR_BLACK  = "\u00a70";
	public static final String COLOR_NAVY   = "\u00a71";
	public static final String COLOR_GREEN  = "\u00a72";
	public static final String COLOR_TEAL   = "\u00a73";
	public static final String COLOR_MAROON = "\u00a74";
	public static final String COLOR_PURPLE = "\u00a75";
	public static final String COLOR_GOLD   = "\u00a76";
	public static final String COLOR_SILVER = "\u00a77";
	public static final String COLOR_GREY   = "\u00a78";
	public static final String COLOR_BLUE   = "\u00a79";
	public static final String COLOR_LIME   = "\u00a7a";
	public static final String COLOR_AQUA   = "\u00a7b";
	public static final String COLOR_RED    = "\u00a7c";
	public static final String COLOR_PINK   = "\u00a7d";
	public static final String COLOR_YELLOW = "\u00a7e";
	public static final String COLOR_WHITE  = "\u00a7f";
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
	
	public static int getWorldIndexFromName(String name) throws IllegalArgumentException {
		if (name.equalsIgnoreCase("Overworld"))
			return 0;
		else if (name.equalsIgnoreCase("Nether"))
			return 1;
		else if (name.equalsIgnoreCase("TheEnd") || name.equalsIgnoreCase("End"))
			return 2;
		else
			throw new IllegalArgumentException(name + " is not a valid dimension name.");
	}
	
	public static int getWorldDimensionFromName(String name) throws IllegalArgumentException {
		if (name.equalsIgnoreCase("Overworld"))
			return 0;
		else if (name.equalsIgnoreCase("Nether"))
			return -1;
		else if (name.equalsIgnoreCase("TheEnd") || name.equalsIgnoreCase("End"))
			return 1;
		else
			throw new IllegalArgumentException(name + " is not a valid dimension name.");
	}
	
	public static String getWorldNameFromDimension(int dimension) {
		if (dimension == 0)
			return "Overworld";
		if (dimension == -1)
			return "Nether";
		else if (dimension == 1)
			return "TheEnd";
		else
			return null;
	}
	
	public static String getWorldNameFromIndex(int index) {
		if (index == 0)
			return "Overworld";
		if (index == 1)
			return "Nether";
		else if (index == 2)
			return "TheEnd";
		else
			return null;
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
	
	public static void sendInMinimumMessages(ICommandSender sender, List<String> parts, String separator) {
		for (String message : Util.combineIntoMaxLengthMessages(parts, Packet3Chat.maxChatLength, separator, false)) {
			sender.sendChatToPlayer(message);
		}
	}
	
	public static String formatSeconds(long seconds) {
		return formatSeconds(seconds, false);
	}
	
	public static String formatSeconds(long seconds, boolean useShortFormat) {
		StringBuilder sb = new StringBuilder();
		
		long hours = seconds / 3600L;
		seconds -= hours * 3600L;
		
		long minutes = seconds / 60L;
		seconds -= minutes * 60L;
		
		if (hours > 0)
			sb.append(hours).append(useShortFormat ? "h" : (hours == 1 ? " hour" : " hours"));
		
		if (minutes > 0) {
			if (sb.length() > 0)
				sb.append(" ");
			
			sb.append(minutes).append(useShortFormat ? "m" : (minutes == 1 ? " minute" : " minutes"));
		}
		
		if (seconds > 0 || sb.length() == 0) {
			if (sb.length() > 0)
				sb.append(" ");
			
			sb.append(seconds).append(useShortFormat ? "s" : (seconds == 1 ? " second" : " seconds"));
		}
		
		return sb.toString();
	}
	
	public static String getEntityName(Entity entity) {
		String name;
		
		if (entity instanceof EntityItem) {
			name = ((EntityItem)entity).func_92059_d().getItemName();
		}
		else if (entity instanceof EntityPlayer) {
			name = entity.getClass().getSimpleName();
		}
		else {
			String nameLookup = EntityList.getEntityString(entity);
			
			String extra = "";
			if (entity instanceof EntityXPOrb && ((EntityXPOrb)entity).m_bNotPlayerOwned) {
				extra = " (Dragon)";
			}
			name = (nameLookup == null ? entity.getEntityName() : nameLookup) + extra;
		}
		
		return name;
	}
}
