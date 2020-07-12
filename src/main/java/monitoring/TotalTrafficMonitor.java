package monitoring;

import msgs.LogEntryParser;
import org.json.simple.JSONObject;

public class TotalTrafficMonitor extends RollingStatsMonitor {

    public TotalTrafficMonitor(JSONObject config) {
        super(config);
    }

    @Override
    protected boolean increment(LogEntryParser parser) {
        return counter.increment(parser.getTimestamp());
    }

    @Override
    protected boolean activateAlert() {
        return counter.getAverage() > threshold;
    }

    @Override
    protected String alertName() {
        return "High traffic";
    }
}
