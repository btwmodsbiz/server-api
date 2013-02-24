package btwmods.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

public class RotatedFileHandler extends StreamHandler {
	
	public final File directory;
	public final SimpleDateFormat dateFormat;
	public final String fileNamePattern;
	
	private String currentDate = null;
	
	public RotatedFileHandler(File directory, String dateFormat, String fileNamePattern) throws IOException {
		this.directory = directory;
		this.dateFormat = new SimpleDateFormat(dateFormat);
		this.fileNamePattern = fileNamePattern;
		checkOutputFile();
	}
	
	private void checkOutputFile() throws IOException {
		String date = dateFormat.format(new Date());
		if (currentDate == null || !currentDate.equals(date)) {
			currentDate = date;
			setOutputStream(new FileOutputStream(new File(directory, fileNamePattern.replaceAll("%DATE%", date)), true));
		}
	}

	@Override
	public synchronized void publish(LogRecord record) {
		try {
			checkOutputFile();
			super.publish(record);
			flush();
		} catch (IOException e) {
			System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
		}
	}
}
