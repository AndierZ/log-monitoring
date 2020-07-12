package monitoring;

import msgs.MutableLogEntryParser;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TotalBytesMonitorTest {

    class TestTotalBytesMonitor extends TotalBytesMonitor {

        private final List<String> outputs = new ArrayList<>();

        public TestTotalBytesMonitor(JSONObject config) {
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
        config.put("threshold", 40*1024*1024l);
        config.put("deadband_secs", 1l);
        TestTotalBytesMonitor monitor = new TestTotalBytesMonitor(config);

        long timestamp = 10_000;
        MutableLogEntryParser msg = new MutableLogEntryParser();
        // 20 messages, 200ms apart. 4s in total. completing first 2 intervals
        // Need one more message to move to the next interval and trigger the alert
        // bytes by interval: (20, 20)
        timestamp = sendMessages(timestamp, 2,20, 200, monitor, msg);
        Assert.assertEquals(0, monitor.outputCount());

        // 1 messages, 200ms apart. 0.2s in total
        // bytes by interval: (20, 20, 2)
        timestamp = sendMessages(timestamp, 2,1, 200, monitor, msg);
        Assert.assertEquals(1, monitor.outputCount());
        String alert = "Wed Dec 31 19:00:14 EST 1969. High total bytes generated an alert - traffic = 40.0 Mb, triggered at time Wed Dec 31 19:00:14 EST 1969";
        Assert.assertEquals(alert, monitor.getLastAlert());

        // 9 messages, 200ms apart. 1.8s in total, completing 3rd interval
        // bytes by interval: (20, 20, 20)
        timestamp = sendMessages(timestamp, 2,9, 200, monitor, msg);
        Assert.assertEquals(1, monitor.outputCount());

        // 1 message, 400ms apart. 0.4s in total, completed the 3rd interval and started the 4th
        // bytes by interval: (20, 20, 20, 2)
        timestamp = sendMessages(timestamp, 2,1, 400, monitor, msg);
        // traffic remained elevated in the 3rd interval, but no new alerts
        Assert.assertEquals(1, monitor.outputCount());

        // 5 message, 400ms apart. 2s in total, completed the 4th interval and started the 5th
        // bytes by interval: (20, 20, 20, 10, 2)
        timestamp = sendMessages(timestamp, 2,5, 400, monitor, msg);
        // traffic dropped in the 4th interval, recovering from the alert
        alert = "Wed Dec 31 19:00:18 EST 1969. High total bytes alert recovered - traffic = 30.0 Mb, recovered at time Wed Dec 31 19:00:18 EST 1969";
        Assert.assertEquals(2, monitor.outputCount());
        Assert.assertEquals(alert, monitor.getLastAlert());
    }

    private static long sendMessages(long timestamp, int mb, int count, int interval, TestTotalBytesMonitor monitor, MutableLogEntryParser msg) {
        for(int i=0; i<count; i++) {
            msg.setTimestamp(timestamp);
            msg.setBytes(mb*1024*1024);
            monitor.onMsg(msg);
            timestamp += interval;
        }
        return timestamp;
    }
}
