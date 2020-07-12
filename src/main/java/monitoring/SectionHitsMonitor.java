package monitoring;

import msgs.LogEntryParser;
import org.json.simple.JSONObject;

public class SectionHitsMonitor extends KeyedFixedStatsMonitor {

    public SectionHitsMonitor(JSONObject config) {
        super(config);
    }

    @Override
    protected boolean increment(LogEntryParser parser) {
        hits.put(parser.getSection(), hits.getOrDefault(parser.getSection(), 0) + 1);
        return true;
    }

    @Override
    protected String alertName() {
        return "Section hit";
    }
}
