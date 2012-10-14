package btwmods.measure;

public class Average {
	
	/**
	 * The number of values to use to calculate the average.
	 */
	public static final int RESOLUTION = 100;
	
	private int tick = -1;
	private long[] history = new long[RESOLUTION];
	private long total = 0;
	private double average = 0;
	
	/**
	 * The number of times a value has been recorded.
	 */
	public int getTick() {
		return tick;
	}
	
	/**
	 * WARNING: Do not modify the returned long. It is *not* a clone.
	 */
	public long[] getHistory() {
		return history;
	}
	
	public long getTotal() { 
		return total;
	}
	
	public double getAverage() {
		return (double)total / (double)RESOLUTION;
	}
	
	public void resetCurrent() {
		tick++;
		
		// Remove the old value from the total.
		if (tick >= RESOLUTION) {
			total -= history[tick % RESOLUTION];
		}

		history[tick % RESOLUTION] = 0;
	}
	
	/**
	 * Record the current value that will be used to calculate the average.
	 */
	public void record(long value) {
		resetCurrent();

		// Add the new value to the total and store it in the history.
		total += history[tick % RESOLUTION] = value;
	}
	
	public void incrementCurrent(long value) {
		total += value;
		history[tick % RESOLUTION] += value;
	}
	
	public static Average[] createInitializedArray(int size) {
		Average[] averages = new Average[size];
		for (int i = 0; i < size; i++) {
			averages[i] = new Average();
		}
		return averages;
	}
}
