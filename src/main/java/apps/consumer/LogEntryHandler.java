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

    private long prevTimestamp;
    private long timestamp;

    public LogEntryHandler(Context context) {
        super(context);
        this.maxDisplayCount = (int) context.getConfig().getOrDefault("max_display_count", 5);
        this.statsInterval = TimeUnit.SECONDS.toMillis((int) context.getConfig().getOrDefault("stats_interval", 10));
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
    }

    @Override
    public int getMessageType() {
        return LogEntryMeta.MSG_TYPE;
    }

}