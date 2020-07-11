package msgs;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class LogEntryBuilder {

    private boolean timestampSet;
    private boolean sectionSet;
    private final ByteBuffer buf = ByteBuffer.allocate(LogEntryMeta.MAX_LEN);

    public LogEntryBuilder newMsg() {
        timestampSet = false;
        sectionSet = false;
        buf.clear();
        // Initial message length: 4 for MSG_LEN. 4 for MSG_TYPE
        buf.putInt(4 + 4);
        buf.putInt(LogEntryMeta.MSG_TYPE);
        return this;
    }

    public LogEntryBuilder setTimestamp(long timestamp) {
        if (timestampSet) {
            throw new RuntimeException("Timestamp already set");
        }
        buf.putInt(0, buf.getInt(0) + 8);
        buf.putLong(timestamp);
        timestampSet = true;
        return this;
    }

    public LogEntryBuilder setSection(String section) {
        if (!timestampSet) {
            throw new RuntimeException("Must set Timestamp before setting Section");
        }
        if (sectionSet) {
            throw new RuntimeException("Section already set");
        }

        byte[] bytes = section.getBytes(StandardCharsets.UTF_8);
        buf.putInt(0, buf.getInt(0) + 4 + bytes.length);
        buf.putInt(bytes.length);
        buf.put(bytes);
        sectionSet = true;
        return this;
    }

    public void send(OutputStream out) throws IOException {
        if (!timestampSet) {
            throw new RuntimeException("Timestamp not set.");
        }
        if (!sectionSet) {
            throw new RuntimeException("Section not set");
        }
        this.buf.flip();
        out.write(buf.array(), 0, buf.limit());
    }
}
