package monitoring;

import common.TestContext;
import msgs.MutableLogEntryParser;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class RankedSectionHitsMonitorTest {

    @Test
    public void test1() {
        JSONObject config = new JSONObject();
        config.put("max_display_count", 2l);
        config.put("interval_secs", 5l);
        TestContext context = new TestContext(null);
        RankedSectionHitsMonitor monitor = new RankedSectionHitsMonitor(context, config);

        long timestamp = 10_000;
        MutableLogEntryParser msg = new MutableLogEntryParser();
        // send 10 messages in 10 seconds
        // hits by section for each interval:
        //      api     (5, 5)
        timestamp = sendMessages(timestamp, "api", 10, 1_000, monitor, msg);
        Assert.assertEquals(1, context.outputCount());
        String alert = "Wed Dec 31 19:00:15 EST 1969. Section hit (top 2): (api - 5)";
        Assert.assertEquals(alert, context.getLastAlert());

        // send 10 messages in 5 seconds
        // hits by section for each interval:
        //      api     (5, 5, 4)
        //      report  (0, 0, 6)
        timestamp = sendMessages(timestamp, "api", 4, 500, monitor, msg);
        Assert.assertEquals(2, context.outputCount());
        alert = "Wed Dec 31 19:00:20 EST 1969. Section hit (top 2): (api - 5)";
        timestamp = sendMessages(timestamp, "report", 6, 500, monitor, msg);

        // send 1 message so timestamp moves to the next interval and triggers alert
        // hits by section for each interval:
        //      api     (5, 5, 4, 0)
        //      report  (0, 0, 6, 1)
        timestamp = sendMessages(timestamp, "report", 1, 500, monitor, msg);
        Assert.assertEquals(3, context.outputCount());
        alert = "Wed Dec 31 19:00:25 EST 1969. Section hit (top 2): (report - 6), (api - 4)";
        Assert.assertEquals(alert, context.getLastAlert());

        // send 9 messages in 4.5 seconds
        // hits by section for each interval:
        //      api     (5, 5, 4, 5)
        //      report  (0, 0, 6, 1)
        //      admin   (0, 0, 0, 4)
        timestamp = sendMessages(timestamp, "api", 5, 500, monitor, msg);
        timestamp = sendMessages(timestamp, "admin", 4, 500, monitor, msg);

        // send 1 message to move timestamp and trigger alert
        // hits by section for each interval:
        //      api     (5, 5, 4, 5, 0)
        //      report  (0, 0, 6, 1, 0)
        //      admin   (0, 0, 0, 4, 1)
        timestamp = sendMessages(timestamp, "admin", 1, 500, monitor, msg);
        Assert.assertEquals(4, context.outputCount());
        alert = "Wed Dec 31 19:00:30 EST 1969. Section hit (top 2): (api - 5), (admin - 4)";
        Assert.assertEquals(alert, context.getLastAlert());
    }

    private static long sendMessages(long timestamp, String section, int count, int interval, RankedSectionHitsMonitor monitor, MutableLogEntryParser msg) {
        for (int i = 0; i < count; i++) {
            msg.setTimestamp(timestamp);
            msg.setSection(section);
            monitor.onMsg(msg);
            timestamp += interval;
        }
        return timestamp;
    }
}
