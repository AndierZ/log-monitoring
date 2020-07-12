package monitoring;

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
        this.cur = (this.cur + 1) % n;
        if (!ready && this.cur == 0) {
            ready = true;
        }
    }

    public int getTotal() {
        if (this.ready) {
            return this.total;
        }
        return -1;
    }

    public double getAverage() {
        if (ready) {
            return (double)this.total / this.n;
        }
        return Double.NaN;
    }

    public boolean isReady() {
        return ready;
    }

    public int size() {
        return this.n;
    }

    public int get(int i) {
        return this.array[i];
    }
}
