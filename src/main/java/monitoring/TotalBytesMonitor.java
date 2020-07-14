package monitoring;

import common.Context;
import msgs.LogEntryParser;
import org.json.simple.JSONObject;

import java.text.DecimalFormat;

/**
 * Keeps track of total bytes over a rolling window and generates alerts if above certain threshold
 */
public class TotalBytesMonitor extends RollingStatsMonitor<LogEntryParser> {

    private static DecimalFormat DF = new DecimalFormat("#0.00");

    public TotalBytesMonitor(Context context, JSONObject config) {
        super(context, config);
    }

    @Override
    protected double getCounterVal() {
        return counter.getTotal();
    }

    protected String formatCounterVal() {
        return "traffic = " + DF.format(getCounterVal() / 1024 / 1024) + " Mb";
    }

    @Override
    protected boolean increment(long timestamp, LogEntryParser parser) {
        return counter.increment(timestamp, parser.getBytes());
    }

    @Override
    protected String alertName() {
        return "High total bytes";
    }
}
