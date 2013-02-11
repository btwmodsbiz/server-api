package btwmods.io;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

public class QueuedWriteString extends QueuedWrite {
	
	public final String line;

	public QueuedWriteString(File file, String line, TYPE type) {
		super(file, type);
		this.line = line;
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write(line);
	}

	@Override
	public String toString() {
		return line;
	}
	
}