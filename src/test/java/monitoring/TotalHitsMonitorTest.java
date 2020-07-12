package monitoring;

import msgs.MutableLogEntryParser;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TotalHitsMonitorTest {

    class TestTotalHitsMonitor extends TotalHitsMonitor {

        private final List<String> outputs = new ArrayList<>();

        public TestTotalHitsMonitor(JSONObject config) {
            super(config);
        }

        @Override
        protected void output(String s) {
            outputs.add(s);
        }

        private int outputCount() {
            return this.outputs.size();
        }

        private String getLastAlert() {
            return this.outputs.get(this.outputs.size()-1);
        }
    }

    @Test
    public void test1() {
        JSONObject config = new JSONObject();
        config.put("period_secs", 4l);
        config.put("interval_secs", 2l);
        config.put("threshold", 10l);
        config.put("deadband_secs", 1l);
        TestTotalHitsMonitor monitor = new TestTotalHitsMonitor(config);

        long timestamp = 10_000;
        MutableLogEntryParser msg = new MutableLogEntryParser();
        // 20 messages, 200ms apart. 4s in total. completing first 2 intervals
        // Need one more message to move to the next interval and trigger the alert
        // count by interval: (10, 10)
        timestamp = sendMessages(timestamp, 20, 200, monitor, msg);
        Assert.assertEquals(0, monitor.outputCount());

        // 1 messages, 200ms apart. 0.2s in total
        // count by interval: (10, 10, 1)
        timestamp = sendMessages(timestamp, 1, 200, monitor, msg);
        Assert.assertEquals(1, monitor.outputCount());
        String alert = "Wed Dec 31 19:00:14 EST 1969. High traffic generated an alert - hits = 10, triggered at time Wed Dec 31 19:00:14 EST 1969";
        Assert.assertEquals(alert, monitor.getLastAlert());

        // 9 messages, 200ms apart. 1.8s in total, completing 3rd interval
        // count by interval: (10, 10, 10)
        timestamp = sendMessages(timestamp, 9, 200, monitor, msg);
        Assert.assertEquals(1, monitor.outputCount());

        // 1 message, 400ms apart. 0.4s in total, completed the 3rd interval and started the 4th
        // count by interval: (10, 10, 10, 1)
        timestamp = sendMessages(timestamp, 1, 400, monitor, msg);
        // traffic remained elevated in the 3rd interval, but no new alerts
        Assert.assertEquals(1, monitor.outputCount());

        // 5 message, 400ms apart. 2s in total, completed the 4th interval and started the 5th
        // count by interval: (10, 10, 10, 5, 1)
        timestamp = sendMessages(timestamp, 5, 400, monitor, msg);
        // traffic dropped in the 4th interval, recovering from the alert
        alert = "Wed Dec 31 19:00:18 EST 1969. High traffic alert recovered - hits = 7, recovered at time Wed Dec 31 19:00:18 EST 1969";
        Assert.assertEquals(2, monitor.outputCount());
        Assert.assertEquals(alert, monitor.getLastAlert());
    }

    private static long sendMessages(long timestamp, int count, int interval, TestTotalHitsMonitor monitor, MutableLogEntryParser msg) {
        for(int i=0; i<count; i++) {
            msg.setTimestamp(timestamp);
            monitor.onMsg(msg);
            timestamp += interval;
        }
        return timestamp;
    }
}
