package apps.consumer;

import common.Context;
import common.TimeseriesCircularCounter;
import msgs.LogEntryMeta;
import msgs.LogEntryParser;
import msgs.MessageHandler;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class LogEntryHandler extends MessageHandler<LogEntryParser> {

    private final int maxDisplayCount;
    private final long statsInterval;
    private final Map<String, Integer> sectionHits = new HashMap<>();
    private final StringBuilder sb = new StringBuilder();
    private final int totalTrafficCheckThreshold;
    private final long totalTrafficCheckDeadband;
    private final TimeseriesCircularCounter counter;

    private long prevTimestamp;
    private long timestamp;
    private long totalTrafficCheckActivatedTime;

    public LogEntryHandler(Context context) {
        super(context);
        this.maxDisplayCount = (int) context.getConfig().getOrDefault("max_display_count", 5);
        this.statsInterval = TimeUnit.SECONDS.toMillis((int) context.getConfig().getOrDefault("stats_interval", 10));
        long totalTrafficCheckPeriod = TimeUnit.SECONDS.toMillis((int) context.getConfig().getOrDefault("total_traffic_check_period", 120));
        long totalTrafficCheckInterval = TimeUnit.SECONDS.toMillis((int) context.getConfig().getOrDefault("total_traffic_check_interval", 1));
        this.totalTrafficCheckThreshold = (int) context.getConfig().getOrDefault("total_traffic_check_threshold", 10);
        this.totalTrafficCheckDeadband = TimeUnit.SECONDS.toMillis((int) context.getConfig().getOrDefault("total_traffic_check_interval", 1));
        this.counter = new TimeseriesCircularCounter(totalTrafficCheckPeriod, totalTrafficCheckInterval);
    }

    @Override
    public void handle(LogEntryParser parser) {
        System.out.println(parser.getMsgLen() + "," + parser.getMsgType() + "," + parser.getTimestamp() + "," + parser.getSection());
        this.timestamp = parser.getTimestamp();
        if (prevTimestamp == 0) {
            prevTimestamp = timestamp;
        }
        sectionHits.put(parser.getSection(), sectionHits.getOrDefault(parser.getSection(), 0) + 1);
        evaluate();
    }

    private void evaluate() {
        if (timestamp - prevTimestamp >= statsInterval) {
            List<Map.Entry<String, Integer>> entries = new ArrayList<>(sectionHits.entrySet());
            entries.sort((a, b) -> b.getValue() - a.getValue());

            sb.setLength(0);
            sb.append("Time: ")
              .append(new Date(timestamp))
              .append(". ");

            if (entries.isEmpty()) {
                sb.append("No data");
            } else {
                sb.append("Sections with most hits:");
                for(int i = 0; i<Math.min(maxDisplayCount, entries.size()); i++) {
                    sb.append(" (")
                            .append(entries.get(i).getKey())
                            .append(", ")
                            .append(entries.get(i).getValue())
                            .append("),");
                }
            }

            // TODO
            // - Requests by type
            // - Requests by status code
            // - Requests by number of bytes
            // - Requests by user
            // - Requests by address

            System.out.println(sb.toString().substring(0, sb.length()-1));

            this.sectionHits.clear();
            prevTimestamp = timestamp;
        }

        this.counter.increment(timestamp);
        if (this.counter.isReady()) {
            if (this.counter.getAverage() > this.totalTrafficCheckThreshold) {
                if (this.totalTrafficCheckActivatedTime == -1) {

                    sb.setLength(0);
                    sb.append("High traffic generated an alert - hits = ");
                    sb.append((int) this.counter.getAverage());
                    sb.append(", triggered at time ").append(new Date(timestamp));
                    System.out.println(sb.toString());
                    this.totalTrafficCheckActivatedTime = timestamp;
                }
            } else {
                // Reset alert if traffic has dropped and it's been more than one second
                if (this.totalTrafficCheckActivatedTime > 0 && timestamp - totalTrafficCheckActivatedTime > totalTrafficCheckDeadband) {
                    sb.setLength(0);
                    sb.append("High traffic alert recovered - hits = ");
                    sb.append((int) this.counter.getAverage());
                    sb.append(", recovered at time ").append(new Date(timestamp));
                    System.out.println(sb.toString());
                    this.totalTrafficCheckActivatedTime = -1;
                }
            }
        }
    }

    @Override
    public int getMessageType() {
        return LogEntryMeta.MSG_TYPE;
    }

}