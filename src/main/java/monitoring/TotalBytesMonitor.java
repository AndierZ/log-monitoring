package monitoring;

import common.Context;
import msgs.LogEntryParser;
import org.json.simple.JSONObject;

public class TotalBytesMonitor extends RollingStatsMonitor<LogEntryParser> {

    public TotalBytesMonitor(Context context, JSONObject config) {
        super(context, config);
    }

    @Override
    protected double getCounterVal() {
        return counter.getTotal();
    }

    protected String formatCounterVal() {
        return "traffic = " + getCounterVal() / 1024 / 1024 + " Mb";
    }

    @Override
    protected boolean increment(LogEntryParser parser) {
        return counter.increment(parser.getTimestamp(), parser.getBytes());
    }

    @Override
    protected String alertName() {
        return "High total bytes";
    }
}
