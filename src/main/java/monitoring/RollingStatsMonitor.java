package monitoring;

import msgs.LogEntryParser;
import org.json.simple.JSONObject;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public abstract class RollingStatsMonitor extends StatsMonitor {

    protected final TimeseriesCircularCounter counter;
    protected final double threshold;

    private final long deadband;
    private final StringBuilder sb;
    private final String key;

    private long lastActivationTime;

    public RollingStatsMonitor(JSONObject config) {
        super(config);
        this.sb = new StringBuilder();
        this.key = (String) config.get("key");
        long period = TimeUnit.SECONDS.toMillis((long) config.get("period_secs"));
        long interval = TimeUnit.SECONDS.toMillis((long) config.get("interval_secs"));
        this.threshold = ((Long) config.get("threshold")).doubleValue();
        this.deadband = TimeUnit.SECONDS.toMillis((long) config.get("deadband_secs"));
        this.counter = new TimeseriesCircularCounter(period, interval);
    }

    abstract protected double getCounterVal();

    abstract protected String formatCounterVal();

    public void onMsg(LogEntryParser parser) {
        long timestamp = parser.getTimestamp();
        boolean updated = increment(parser);
        if (!updated) {
            return;
        }

        if (this.counter.isReady()) {
            if (getCounterVal() >= threshold) {
                if (this.lastActivationTime == 0) {
                    sb.setLength(0);
                    sb.append(new Date(timestamp));
                    sb.append(". ");
                    if (key != null) sb.append(key).append(" - ");
                    sb.append(alertName());
                    sb.append(" generated an alert - ");
                    sb.append(formatCounterVal());
                    sb.append(", triggered at time ").append(new Date(timestamp));
                    output(sb.toString());
                    this.lastActivationTime = timestamp;
                }
            } else {
                // Reset alert if traffic has dropped and it's been more than one second
                if (this.lastActivationTime > 0 && timestamp - lastActivationTime > deadband) {
                    sb.setLength(0);
                    sb.append(new Date(timestamp));
                    sb.append(". ");
                    if (key != null) sb.append(key).append(" - ");
                    sb.append(alertName());
                    sb.append(" alert recovered - ");
                    sb.append(formatCounterVal());
                    sb.append(", recovered at time ").append(new Date(timestamp));
                    output(sb.toString());
                    this.lastActivationTime = 0;
                }
            }
        }
    }

    @Override
    protected void output(String alert) {
        System.out.println(alert);
    }
}
