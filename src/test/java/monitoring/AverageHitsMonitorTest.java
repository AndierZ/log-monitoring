package monitoring;

import common.TestContext;
import msgs.MutableLogEntryParser;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class AverageHitsMonitorTest {

    @Test
    public void test1() {
        JSONObject config = new JSONObject();
        config.put("period_secs", 4l);
        config.put("interval_secs", 2l);
        config.put("threshold", 10l);
        config.put("deadband_secs", 1l);
        TestContext context = new TestContext(null);
        AverageHitsMonitor monitor = new AverageHitsMonitor(context, config);

        long timestamp = 10_000;
        MutableLogEntryParser msg = new MutableLogEntryParser();
        // 20 messages, 200ms apart. 4s in total. completing first 2 intervals
        // Need one more message to move to the next interval and trigger the alert
        // count by interval: (10, 10)
        timestamp = sendMessages(timestamp, 20, 200, monitor, msg);
        Assert.assertEquals(0, context.outputCount());

        // 1 messages, 200ms apart. 0.2s in total
        // count by interval: (10, 10, 1)
        timestamp = sendMessages(timestamp, 1, 200, monitor, msg);
        Assert.assertEquals(1, context.outputCount());
        String alert = "Wed Dec 31 19:00:14 EST 1969. High traffic generated an alert - hits = 10, triggered at time Wed Dec 31 19:00:14 EST 1969";
        Assert.assertEquals(alert, context.getLastAlert());

        // 9 messages, 200ms apart. 1.8s in total, completing 3rd interval
        // count by interval: (10, 10, 10)
        timestamp = sendMessages(timestamp, 9, 200, monitor, msg);
        Assert.assertEquals(1, context.outputCount());

        // 1 message, 400ms apart. 0.4s in total, completed the 3rd interval and started the 4th
        // count by interval: (10, 10, 10, 1)
        timestamp = sendMessages(timestamp, 1, 400, monitor, msg);
        // traffic remained elevated in the 3rd interval, but no new alerts
        Assert.assertEquals(1, context.outputCount());

        // 5 message, 400ms apart. 2s in total, completed the 4th interval and started the 5th
        // count by interval: (10, 10, 10, 5, 1)
        timestamp = sendMessages(timestamp, 5, 400, monitor, msg);
        // traffic dropped in the 4th interval, recovering from the alert
        alert = "Wed Dec 31 19:00:18 EST 1969. High traffic alert recovered - hits = 7, recovered at time Wed Dec 31 19:00:18 EST 1969";
        Assert.assertEquals(2, context.outputCount());
        Assert.assertEquals(alert, context.getLastAlert());
    }

    private static long sendMessages(long timestamp, int count, int interval, AverageHitsMonitor monitor, MutableLogEntryParser msg) {
        for (int i = 0; i < count; i++) {
            msg.setTimestamp(timestamp);
            monitor.onMsg(msg);
            timestamp += interval;
        }
        return timestamp;
    }
}
