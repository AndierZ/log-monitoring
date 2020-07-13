package monitoring;

import common.Context;
import msgs.LogEntryParser;
import org.json.simple.JSONObject;

public class TotalHitsMonitor extends RollingStatsMonitor<LogEntryParser> {

    public TotalHitsMonitor(Context context, JSONObject config) {
        super(context, config);
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
