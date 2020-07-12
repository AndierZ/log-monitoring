package monitoring;

import msgs.LogEntryParser;
import org.json.simple.JSONObject;

public class TotalBytesMonitor extends RollingStatsMonitor {

    public TotalBytesMonitor(JSONObject config) {
        super(config);
    }

    @Override
    protected double getCounterVal() {
        return counter.getTotal();
    }

    protected String formatCounterVal() {
        return "total traffic = " + getCounterVal() / 1024 / 1024 + " Mb";
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
