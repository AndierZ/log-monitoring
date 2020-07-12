package monitoring;

public class TimeseriesCircularCounter {

    private final CircularCounter counter;
    private final long interval;
    private final int buckets;

    private long lastTimestamp;
    private int currentVal;

    public TimeseriesCircularCounter(long length, long interval) {
        this.buckets = (int) (length / interval);
        this.interval = interval;
        this.counter = new CircularCounter(buckets);
    }

    public boolean increment(long timestamp) {
        return increment(timestamp, 1);
    }

    public boolean increment(long timestamp, int diff) {
        boolean updated = false;
        if (lastTimestamp == 0) {
            lastTimestamp = timestamp;
        } else if (timestamp - lastTimestamp >= this.interval) {
            // push at most n+1 times which will clear the entire array
            int steps = (int) Math.min(buckets+1, (timestamp - lastTimestamp) / interval);
            for(int i=0; i<steps; i++) {
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
        return this.counter.isReady();
    }

    public int size() {
        return this.counter.size();
    }

    public int get(int i) {
        return this.counter.get(i);
    }
}
