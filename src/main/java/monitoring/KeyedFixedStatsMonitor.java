package monitoring;

import msgs.LogEntryParser;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class KeyedFixedStatsMonitor extends StatsMonitor {

    private long prevTimestamp;
    private final long interval;
    private final int maxDisplayCount;
    private final StringBuilder sb = new StringBuilder();
    protected final Map<String, Integer> hits = new HashMap<>();

    public KeyedFixedStatsMonitor(JSONObject config) {
        super(config);
        this.maxDisplayCount = ((Long) config.get("max_display_count")).intValue();
        this.interval = TimeUnit.SECONDS.toMillis((long) config.get("interval_secs"));
    }

    public void onMsg(LogEntryParser parser) {
        long timestamp = parser.getTimestamp();
        if (prevTimestamp == 0) {
            prevTimestamp = timestamp;
        } else if (timestamp - prevTimestamp >= interval) {
            List<Map.Entry<String, Integer>> entries = new ArrayList<>(hits.entrySet());
            entries.sort((a, b) -> b.getValue() - a.getValue());

            sb.setLength(0);
            sb.append(new Date(timestamp));
            sb.append(". ");

            if (entries.isEmpty()) {
                sb.append("No data");
            } else {
                sb.append(alertName());
                sb.append(" (top ");
                sb.append(maxDisplayCount);
                sb.append("):");
                for(int i = 0; i<Math.min(maxDisplayCount, entries.size()); i++) {
                    sb.append(" (");
                    sb.append(entries.get(i).getKey());
                    sb.append(" - ");
                    sb.append(entries.get(i).getValue());
                    sb.append("),");
                }
            }

            output(sb.substring(0, sb.length()-1));

            this.hits.clear();
            prevTimestamp = timestamp;
        }
        increment(parser);
    }

    @Override
    protected void output(String alert) {
        System.out.println(alert);
    }
}