package monitoring;

import msgs.LogEntryParser;
import org.json.simple.JSONObject;

public class TotalHitsMonitor extends RollingStatsMonitor {

    public TotalHitsMonitor(JSONObject config) {
        super(config);
    }

    @Override
    protected boolean increment(LogEntryParser parser) {
        return counter.increment(parser.getTimestamp());
    }

    @Override
    protected double getCounterVal() {
        return counter.getAverage();
    }

    @Override
    protected String formatCounterVal() {
        return "hits = " + (int) getCounterVal();
    }

    @Override
    protected String alertName() {
        return "High traffic";
    }
}
