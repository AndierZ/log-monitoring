package monitoring;

import common.Constants;
import common.Context;
import msgs.MessageParser;
import org.json.simple.JSONObject;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Keeps track of a metric based on a rolling window
 * @param <T>
 */
public abstract class RollingStatsMonitor<T extends MessageParser> extends SingleStatsMonitor<T> {

    protected final TimeseriesCircularCounter counter;
    protected final double threshold;


    /**
     * Delay between alert updates
     */
    private final long deadband;
    private final StringBuilder sb;


    /**
     * Additional prefix information to be displayed before the alert
     */
    private String prefix;
    private long lastActivationTime;

    public RollingStatsMonitor(Context context, JSONObject config) {
        super(context, config);
        this.sb = new StringBuilder();
        long period = TimeUnit.SECONDS.toMillis((long) config.get(Constants.PERIOD_SECS));
        long interval = TimeUnit.SECONDS.toMillis((long) config.get(Constants.INTERVAL_SECS));
        this.threshold = ((Long) config.get(Constants.THRESHOLD)).doubleValue();
        this.deadband = TimeUnit.SECONDS.toMillis((long) config.get(Constants.DEADBAND_SECS));
        this.counter = new TimeseriesCircularCounter(period, interval);
    }


    /**
     * @param prefix Additional information to be included in the alert
     */
    protected void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * @return Value to be evaluated against the threshold, e.g. average or total for the rolling window
     */
    abstract protected double getCounterVal();


    /**
     * @return Format the value for display purposes
     */
    abstract protected String formatCounterVal();

    public void onMsg(T parser) {
        long timestamp = parser.getTimestamp();
        boolean updated = increment(parser);
        if (!updated) {
            return;
        }

        if (counter.isReady()) {
            if (getCounterVal() >= threshold) {
                // Raise alert if haven't already
                if (lastActivationTime == 0) {
                    sb.setLength(0);
                    sb.append(new Date(timestamp));
                    sb.append(". ");
                    if (prefix != null) sb.append(prefix).append(" - ");
                    sb.append(alertName());
                    sb.append(" generated an alert - ");
                    sb.append(formatCounterVal());
                    sb.append(", triggered at time ").append(new Date(timestamp));
                    context.out.alertSink.accept(sb.toString());
                    lastActivationTime = timestamp;
                }
            } else {
                // Reset alert if traffic has dropped and it's been more than one second
                if (lastActivationTime > 0 && timestamp - lastActivationTime >= deadband) {
                    sb.setLength(0);
                    sb.append(new Date(timestamp));
                    sb.append(". ");
                    if (prefix != null) sb.append(prefix).append(" - ");
                    sb.append(alertName());
                    sb.append(" alert recovered - ");
                    sb.append(formatCounterVal());
                    sb.append(", recovered at time ").append(new Date(timestamp));
                    context.out.alertSink.accept(sb.toString());
                    lastActivationTime = 0;
                }
            }
        }
    }
}
