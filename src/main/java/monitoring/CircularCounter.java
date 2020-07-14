package monitoring;

/**
 * Compute the total and average for specified rolling window size
 */
public class CircularCounter {

    private final int[] array;
    private final int n;
    private int total;
    private boolean ready;
    private int cur;

    public CircularCounter(int n) {
        this.n = n;
        this.array = new int[n];
    }

    public void push(int val) {
        total -= array[cur];
        array[cur] = val;
        total += array[cur];
        cur = (cur + 1) % n;
        if (!ready && cur == 0) {
            ready = true;
        }
    }

    public int getTotal() {
        if (ready) {
            return total;
        }
        return -1;
    }

    public double getAverage() {
        if (ready) {
            return (double) total / n;
        }
        return Double.NaN;
    }

    public boolean isReady() {
        return ready;
    }

    public int size() {
        return n;
    }

    public int get(int i) {
        return array[i];
    }
}
