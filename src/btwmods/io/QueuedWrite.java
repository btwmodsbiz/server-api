package btwmods.io;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

public abstract class QueuedWrite {
	public enum TYPE { OVERWRITE, OVERWRITE_SAFE, APPEND };
	
	public final File file;
	public final TYPE type;
	
	protected QueuedWrite(File file, TYPE type) {
		this.file = file;
		this.type = type;
	}
	
	public abstract void write(Writer writer) throws IOException;
	
	public abstract String toString();
}