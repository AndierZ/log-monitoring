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
        this.out = new DummyOutputStream();
    }

    @Test
    public void test1() throws IOException {
        // the first message
        LogEntryBuilder builder = new LogEntryBuilder();
        builder.newMsg().setTimestamp(100).setSection("api").send(this.out);

        ByteBuffer buf = createBuffer(this.out.getBytes());
        LogEntryParser parser = new LogEntryParser();
        parser.wrap(buf);

        Assert.assertEquals(LogEntryMeta.MSG_TYPE, parser.getMsgType());
        // 4 - Msg len
        // 4 - Msg type
        // 8 - Timestamp
        // 4 - String len
        // 3 - String bytes
        Assert.assertEquals(4 + 4 + 8 + 4 + 3, parser.getMsgLen());
        Assert.assertEquals(100, parser.getTimestamp());
        Assert.assertEquals("api", parser.getSection());

        // Reading multiple times should be ok
        Assert.assertEquals(LogEntryMeta.MSG_TYPE, parser.getMsgType());
        Assert.assertEquals(4 + 4 + 8 + 4 + 3, parser.getMsgLen());
        Assert.assertEquals(100, parser.getTimestamp());
        Assert.assertEquals("api", parser.getSection());

        this.out.reset();

        // the first message
        builder.newMsg().setTimestamp(200).setSection("report").send(this.out);

        buf = createBuffer(this.out.getBytes());
        parser.wrap(buf);

        Assert.assertEquals(LogEntryMeta.MSG_TYPE, parser.getMsgType());
        Assert.assertEquals(4 + 4 + 8 + 4 + 6, parser.getMsgLen());
        Assert.assertEquals(200, parser.getTimestamp());
        Assert.assertEquals("report", parser.getSection());

        // Reading multiple times should be ok
        Assert.assertEquals(LogEntryMeta.MSG_TYPE, parser.getMsgType());
        Assert.assertEquals(4 + 4 + 8 + 4 + 6, parser.getMsgLen());
        Assert.assertEquals(200, parser.getTimestamp());
        Assert.assertEquals("report", parser.getSection());

        this.out.reset();

    }

    private ByteBuffer createBuffer(List<Byte> bytes) {
        ByteBuffer buf = ByteBuffer.allocate(bytes.size());
        for(int i=0; i<bytes.size(); i++) {
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
            for(int i=offset; i<length; i++) {
                cache.add(array[i]);
            }
        }

        private void reset() {
            cache.clear();
        }

        private List<Byte> getBytes() {
            return this.cache;
        }
    }
}
