package apps.consumer;

public class CircularHitCounter {


    private final int[] count;
    private final int buckets;
    private boolean ready;
    private double avg;

    public CircularHitCounter(int length, int interval) {
        // length = 10,000 ms
        // interval = 1000 ms
        // 10 buckets
        this.buckets = length / interval;
        this.count = new int[buckets];
    }

    public void increment(long timestamp) {
        // find next available spot


        // if already taken, clear that slot, decrement avg
        // increment that slot by one
    }

    public double getAverage() {
        if (this.ready) {
            return this.avg;
        }
        return Double.NaN;
    }

    public boolean isReady() {
        return this.ready;
    }
}
