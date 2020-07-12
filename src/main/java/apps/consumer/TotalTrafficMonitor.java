package apps.consumer;

import common.TimeseriesCircularCounter;
import msgs.LogEntryParser;
import org.json.simple.JSONObject;

public class TotalTrafficMonitor extends RollingStatsMonitor {

    public TotalTrafficMonitor(JSONObject config) {
        super(config);
    }

    @Override
    protected void increment(TimeseriesCircularCounter counter, LogEntryParser parser) {
        counter.increment(parser.getTimestamp());
    }

    @Override
    protected boolean activateAlert(TimeseriesCircularCounter counter, double threshold) {
        return counter.getAverage() > threshold;
    }

    @Override
    protected String alertName() {
        return "High traffic";
    }
}
