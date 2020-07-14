package monitoring;

/**
 * Computes the average and total for a rolling window based on timestamp
 */
public class TimeseriesCircularCounter {

    private final CircularCounter counter;
    private final long interval;
    private final int buckets;
    private long lastTimestamp;
    private int currentVal;

    public TimeseriesCircularCounter(long length, long interval) {
        this.interval = interval;
        this.buckets = (int) (length / interval);
        this.counter = new CircularCounter(buckets);
    }

    public boolean increment(long timestamp) {
        return increment(timestamp, 1);
    }

    /**
     * @param timestamp Timestamp for the datapoint
     * @param diff      Value to be added on top of the existing value
     * @return Whether the increment caused the rolling window to move to the next slot
     */
    public boolean increment(long timestamp, int diff) {
        boolean updated = false;
        if (lastTimestamp == 0) {
            lastTimestamp = timestamp;
        } else if (timestamp - lastTimestamp >= interval) {
            // push at most n+1 times which will clear the entire array
            int steps = (int) Math.min(buckets + 1, (timestamp - lastTimestamp) / interval);
            for (int i = 0; i < steps; i++) {
                counter.push(currentVal);
                currentVal = 0;
            }
            lastTimestamp = timestamp;
            updated = true;
        }

        currentVal += diff;
        return updated;
    }

    public double getAverage() {
        return counter.getAverage();
    }

    public int getTotal() {
        return counter.getTotal();
    }

    public boolean isReady() {
        return counter.isReady();
    }

    public int size() {
        return counter.size();
    }

    public int get(int i) {
        return counter.get(i);
    }
}
