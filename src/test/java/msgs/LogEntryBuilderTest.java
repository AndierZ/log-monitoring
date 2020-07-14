package msgs;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class LogEntryBuilderTest {

    private DummyOutputStream out;

    @Before
    public void setup() {
        out = new DummyOutputStream();
    }

    @Test
    public void test1() throws IOException {
        // the first message
        LogEntryBuilder builder = new LogEntryBuilder();
        builder.newMsg().setTimestamp(100).setSection("api").send(out);

        ByteBuffer buf = createBuffer(out.getBytes());
        LogEntryParser parser = new LogEntryParser();
        parser.wrap(buf);

        Assert.assertEquals(LogEntryMeta.MSG_TYPE, parser.getMsgType());
        // 4 - msg len
        // 4 - msg type
        // 8 - timestamp
        // 4 - bytes
        // 4 - status
        // 7 - section ("api")
        // 4 - remoteHost (null)
        // 4 - authUser (null)
        // 4 - method (null)
        // 4 - route (null)

        int len = 4 + 4 + 8 + 4 + 4 + 7 + 4 + 4 + 4 + 4;
        Assert.assertEquals(len, parser.getMsgLen());
        Assert.assertEquals(100, parser.getTimestamp());
        Assert.assertEquals("api", parser.getSection());

        // Reading multiple times should be ok
        Assert.assertEquals(LogEntryMeta.MSG_TYPE, parser.getMsgType());
        Assert.assertEquals(len, parser.getMsgLen());
        Assert.assertEquals(100, parser.getTimestamp());
        Assert.assertEquals("api", parser.getSection());

        out.reset();

        // the first message
        builder.newMsg().setTimestamp(200).setSection("report").send(out);

        buf = createBuffer(out.getBytes());
        parser.wrap(buf);

        // 7 -> 10 ("api" -> "report")
        len = 4 + 4 + 8 + 4 + 4 + 10 + 4 + 4 + 4 + 4;
        Assert.assertEquals(LogEntryMeta.MSG_TYPE, parser.getMsgType());
        Assert.assertEquals(len, parser.getMsgLen());
        Assert.assertEquals(200, parser.getTimestamp());
        Assert.assertEquals("report", parser.getSection());

        // Reading multiple times should be ok
        Assert.assertEquals(LogEntryMeta.MSG_TYPE, parser.getMsgType());
        Assert.assertEquals(len, parser.getMsgLen());
        Assert.assertEquals(200, parser.getTimestamp());
        Assert.assertEquals("report", parser.getSection());

        out.reset();
    }

    private ByteBuffer createBuffer(List<Byte> bytes) {
        ByteBuffer buf = ByteBuffer.allocate(bytes.size());
        for (int i = 0; i < bytes.size(); i++) {
            buf.put(bytes.get(i));
        }
        buf.flip();
        return buf;
    }

    private class DummyOutputStream extends OutputStream {

        private List<Byte> cache = new ArrayList<>();

        @Override
        public void write(int b) {
            // NO-OP
        }

        @Override
        public void write(byte[] array, int offset, int length) {
            for (int i = offset; i < length; i++) {
                cache.add(array[i]);
            }
        }

        private void reset() {
            cache.clear();
        }

        private List<Byte> getBytes() {
            return cache;
        }
    }
}
