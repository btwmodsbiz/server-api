package btwmods.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import btwmods.ModLoader;
import btwmods.io.QueuedWrite.TYPE;

public class AsynchronousFileWriter {
	
	private final String threadName;
	private final ConcurrentLinkedQueue<QueuedWrite> writeQueue = new ConcurrentLinkedQueue<QueuedWrite>();
	private volatile WriterThread writerThread = null;
	private int threadCheckCounter = 0;
	private File tempFile = null;
	
	public AsynchronousFileWriter(String threadName) {
		this.threadName = threadName;
	}
	
	public int getWriteCount() {
		return threadCheckCounter;
	}
	
	public boolean isThreadRunning() {
		return writerThread != null && writerThread.isRunning();
	}
	
	private void checkThread() {
		if (!isThreadRunning()) {
			new Thread(writerThread = new WriterThread(), "AsyncWriter: " + threadName).start();
		}
	}
	
	private File getTempFile() throws IOException {
		if (tempFile == null) {
			tempFile = File.createTempFile("asyncwriter_", null, ModLoader.modDataDir);
			tempFile.deleteOnExit();
		}
		
		return tempFile;
	}
	
	public void queueWrite(QueuedWrite write) {
		writeQueue.add(write);
		
		if (threadCheckCounter % 50 == 0) {
			checkThread();
		}
		
		threadCheckCounter++;
	}
	
	private class WriterThread implements Runnable {
		private volatile boolean isRunning = true;
		
		public boolean isRunning() {
			return isRunning;
		}
		
		@Override
		public void run() {
			Map<File, List<QueuedWrite>> writesByFile = new LinkedHashMap<File, List<QueuedWrite>>();
			
			while (writerThread == this) {
				
				// Dequeue the lines and group by file.
				QueuedWrite write = null;
				while ((write = writeQueue.poll()) != null) {
					List lines = write.type == TYPE.APPEND ? writesByFile.get(write.file) : null;
					if (lines == null)
						writesByFile.put(write.file, lines = new ArrayList<QueuedWrite>());
					
					lines.add(write);
				}
				
				// Write the lines to each file.
				writeToFiles(writesByFile);
				
				// Clear the dequeued lines.
				writesByFile.clear();
				
				try {
					Thread.sleep(40L);
				} catch (InterruptedException e) {
					
				}
			}
			
			isRunning = false;
		}
		
		private void writeToFiles(Map<File, List<QueuedWrite>> writesByFile) {
			BufferedWriter writer = null;
			for (Entry<File, List<QueuedWrite>> writes : writesByFile.entrySet()) {
				if (writes.getValue().size() > 0) {
					QueuedWrite first = writes.getValue().get(0);
					File file = null;
					
					try {
						file = first.type == TYPE.OVERWRITE_SAFE ? getTempFile() : writes.getKey();
					}
					catch (IOException e) {
						ModLoader.outputError(e, AsynchronousFileWriter.class.getSimpleName() + " failed to create temp file file \"" + writes.getKey().getPath() + "\":" + e.getMessage(), Level.SEVERE);	
					}
					
					if (file != null) {
						try {
							writer = new BufferedWriter(new FileWriter(file, first.type == TYPE.APPEND));
							
							for (QueuedWrite write : writes.getValue())
								write.write(writer);
							
							if (first.type == TYPE.OVERWRITE_SAFE)
								getTempFile().renameTo(writes.getKey());
							
						} catch (IOException e) {
							ModLoader.outputError(e, AsynchronousFileWriter.class.getSimpleName() + " failed to write to \"" + writes.getKey().getPath() + "\":" + e.getMessage(), Level.SEVERE);
						}
						
						finally {
							if (writer != null) {
								try {
									writer.close();
								} catch (IOException e) {
									
								}
							}
						}
					}
				}
			}
		}
	}
}
