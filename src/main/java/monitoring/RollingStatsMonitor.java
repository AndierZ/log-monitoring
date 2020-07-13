package monitoring;

import common.Constants;
import msgs.MessageParser;
import org.json.simple.JSONObject;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public abstract class RollingStatsMonitor<T extends MessageParser> extends SingleStatsMonitor<T> {

    protected final TimeseriesCircularCounter counter;
    protected final double threshold;

    private final long deadband;
    private final StringBuilder sb;
    private final String key;

    private long lastActivationTime;

    public RollingStatsMonitor(JSONObject config) {
        super(config);
        this.sb = new StringBuilder();
        this.key = (String) config.get(Constants.KEY);
        long period = TimeUnit.SECONDS.toMillis((long) config.get(Constants.PERIOD_SECS));
        long interval = TimeUnit.SECONDS.toMillis((long) config.get(Constants.INTERVAL_SECS));
        this.threshold = ((Long) config.get(Constants.THRESHOLD)).doubleValue();
        this.deadband = TimeUnit.SECONDS.toMillis((long) config.get(Constants.DEADBAND_SECS));
        this.counter = new TimeseriesCircularCounter(period, interval);
    }

    abstract protected double getCounterVal();

    abstract protected String formatCounterVal();

    public void onMsg(T parser) {
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
