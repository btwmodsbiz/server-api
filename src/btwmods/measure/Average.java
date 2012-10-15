package btwmods.measure;

public class Average implements Comparable<Average> {
	
	/**
	 * The number of values to use to calculate the average.
	 */
	public static final int RESOLUTION = 100;
	
	private int resolution;
	private int tick = -1;
	private long[] history;
	private long total = 0;
	private double average = 0;
	
	public Average() {
		this(RESOLUTION);
	}
	
	public Average(int resolution) {
		this.resolution = resolution;
		history = new long[resolution];
	}
	
	public int getResolution() {
		return resolution;
	}
	
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
		return (double)total / (double)Math.min(tick + 1, resolution);
	}
	
	public void resetCurrent() {
		tick++;
		
		// Remove the old value from the total.
		if (tick >= resolution) {
			total -= history[tick % resolution];
		}

		history[tick % resolution] = 0;
	}
	
	/**
	 * Record the current value that will be used to calculate the average.
	 */
	public void record(long value) {
		resetCurrent();

		// Add the new value to the total and store it in the history.
		total += (history[tick % resolution] = value);
	}
	
	public void incrementCurrent(long value) {
		total += value;
		history[tick % resolution] += value;
	}
	
	public static Average[] createInitializedArray(int size) {
		Average[] averages = new Average[size];
		for (int i = 0; i < size; i++) {
			averages[i] = new Average();
		}
		return averages;
	}

	@Override
	public int compareTo(Average o) {
		return new Long(total).compareTo(o.total);
	}
}
