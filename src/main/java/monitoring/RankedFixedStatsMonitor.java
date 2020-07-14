package monitoring;

import common.Constants;
import common.Context;
import msgs.MessageParser;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * Checks metric at regular fixed interval
 * Rank the metrics based on the key specified by the implementing class
 * Outputs top metrics
 * @param <T>
 */
public abstract class RankedFixedStatsMonitor<T extends MessageParser> extends SingleStatsMonitor<T> {

    private long prevTimestamp;
    private final long interval;
    private final int maxDisplayCount;
    private final StringBuilder sb = new StringBuilder();
    protected final Map<String, Integer> hits = new HashMap<>();

    public RankedFixedStatsMonitor(Context context, JSONObject config) {
        super(context, config);
        this.maxDisplayCount = ((Long) config.get(Constants.MAX_DISPLAY_COUNT)).intValue();
        this.interval = TimeUnit.SECONDS.toMillis((long) config.get(Constants.INTERVAL_SECS));
    }

    public void onMsg(T parser) {
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

            context.out.alertSink.accept(sb.substring(0, sb.length()-1));

            hits.clear();
            prevTimestamp = timestamp;
        }
        increment(parser);
    }
}
