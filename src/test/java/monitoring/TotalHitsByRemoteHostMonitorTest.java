package monitoring;

import common.TestContext;
import msgs.MutableLogEntryParser;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class TotalHitsByRemoteHostMonitorTest {

    @Test
    public void test1() {
        JSONObject config = new JSONObject();
        config.put("period_secs", 4l);
        config.put("interval_secs", 2l);
        config.put("threshold", 10l);
        config.put("deadband_secs", 1l);
        TestContext context = new TestContext(null);
        TotalHitsByRemoteHostMonitor monitor = new TotalHitsByRemoteHostMonitor(context, config);

        long timestamp1 = 10_000;
        MutableLogEntryParser msg = new MutableLogEntryParser();
        // 20 messages, 200ms apart. 4s in total. completed first 2 intervals
        // Need one more message to move to the next interval and trigger the alert
        // count by interval: host1 -> (10, 10)
        timestamp1 = sendMessages(timestamp1, "host1", 20, 200, monitor, msg);
        Assert.assertEquals(0, context.outputCount());

        // 1 messages, 200ms apart. 0.2s in total
        // count by interval: host1 -> (10, 10, 1)
        timestamp1 = sendMessages(timestamp1, "host1", 1, 200, monitor, msg);
        Assert.assertEquals(1, context.outputCount());
        String alert = "Wed Dec 31 19:00:14 EST 1969. RemoteHost: host1 - High traffic generated an alert - hits = 10, triggered at time Wed Dec 31 19:00:14 EST 1969";
        Assert.assertEquals(alert, context.getLastAlert());

        // 9 messages, 200ms apart. 1.8s in total, completing 3rd interval
        // count by interval: host1 -> (10, 10, 10)
        // count by interval: host2 -> ( 0,  0, 10)
        // timestamps for two users are processed independently
        long timestamp2 = timestamp1;
        timestamp1 = sendMessages(timestamp1, "host1", 9, 200, monitor, msg);
        timestamp2 = sendMessages(timestamp2, "host2", 10, 200, monitor, msg);
        Assert.assertEquals(1, context.outputCount());

        // 1 message, 400ms apart. 0.4s in total, completed the 3rd interval and started the 4th
        // count by interval: host1 -> (10, 10, 10, 1)
        timestamp1 = sendMessages(timestamp1, "host1", 1, 400, monitor, msg);
//        timestamp = sendMessages(timestamp, "host2", 10, 300, monitor, msg);
        // traffic remained elevated in the 3rd interval, but no new alerts
        Assert.assertEquals(1, context.outputCount());

        // 5 message, 400ms apart. 2s in total, completed the 4th interval and started the 5th
        // count by interval: host1 -> (10, 10, 10, 5, 1)
        timestamp1 = sendMessages(timestamp1, "host1", 5, 400, monitor, msg);
        // traffic dropped in the 4th interval, recovering from the alert
        alert = "Wed Dec 31 19:00:18 EST 1969. RemoteHost: host1 - High traffic alert recovered - hits = 7, recovered at time Wed Dec 31 19:00:18 EST 1969";
        Assert.assertEquals(2, context.outputCount());
        Assert.assertEquals(alert, context.getLastAlert());

        // 11 messages, 200ms apart. 2.2s in total, completed 4rd interval and started the 5th
        // count by interval: host1 -> (10, 10, 10,  5, 1)
        // count by interval: host2 -> ( 0,  0, 10, 10, 1)
        timestamp2 = sendMessages(timestamp2, "host2", 11, 200, monitor, msg);
        alert = "Wed Dec 31 19:00:18 EST 1969. RemoteHost: host2 - High traffic generated an alert - hits = 10, triggered at time Wed Dec 31 19:00:18 EST 1969";
        Assert.assertEquals(3, context.outputCount());
        Assert.assertEquals(alert, context.getLastAlert());
    }

    private static long sendMessages(long timestamp, String remoteHost, int count, int interval, TotalHitsByRemoteHostMonitor monitor, MutableLogEntryParser msg) {
        for (int i = 0; i < count; i++) {
            msg.setTimestamp(timestamp);
            msg.setRemoteHost(remoteHost);
            monitor.onMsg(msg);
            timestamp += interval;
        }
        return timestamp;
    }
}
