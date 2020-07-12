package monitoring;

import msgs.LogEntryParser;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class FixedStatsMonitor extends StatsMonitor {

    private long prevTimestamp;
    private final long interval;
    private final int maxDisplayCount;
    private final StringBuilder sb = new StringBuilder();
    protected final Map<String, Integer> hits = new HashMap<>();

    public FixedStatsMonitor(JSONObject config) {
        super(config);
        this.maxDisplayCount = ((Long) config.get("max_display_count")).intValue();
        this.interval = TimeUnit.SECONDS.toMillis((long) config.get("interval_secs"));
    }

    public void onMsg(LogEntryParser parser) {
        long timestamp = parser.getTimestamp();
        increment(parser);
        if (prevTimestamp == 0) {
            prevTimestamp = timestamp;
            return;
        }
        if (timestamp - prevTimestamp >= interval) {
            List<Map.Entry<String, Integer>> entries = new ArrayList<>(hits.entrySet());
            entries.sort((a, b) -> b.getValue() - a.getValue());

            sb.setLength(0);
            sb.append("Time: ")
                    .append(new Date(timestamp))
                    .append(". ");

            if (entries.isEmpty()) {
                sb.append("No data");
            } else {
                sb.append(alertName());
                sb.append(" top ");
                sb.append(maxDisplayCount);
                sb.append(": ");
                for(int i = 0; i<Math.min(maxDisplayCount, entries.size()); i++) {
                    sb.append(" (")
                            .append(entries.get(i).getKey())
                            .append(", ")
                            .append(entries.get(i).getValue())
                            .append("),");
                }
            }

            output(sb.substring(0, sb.length()-1));

            this.hits.clear();
            prevTimestamp = timestamp;
        }
    }

    @Override
    protected void output(String alert) {
        System.out.println(alert);
    }
}
