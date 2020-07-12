package monitoring;

import msgs.LogEntryParser;
import org.json.simple.JSONObject;

public class TotalBytesMonitor extends RollingStatsMonitor {

    public TotalBytesMonitor(JSONObject config) {
        super(config);
    }

    @Override
    protected boolean increment(LogEntryParser parser) {
        return counter.increment(parser.getTimestamp(), parser.getBytes());
    }

    @Override
    protected boolean activateAlert() {
        return counter.getTotal() > threshold;
    }

    @Override
    protected String alertName() {
        return "High total bytes";
    }
}
