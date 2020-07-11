package msgs;

import java.nio.ByteBuffer;

public class HeartbeatParser {

    private static int TIMESTAMP_OFFSET = 4;
    private ByteBuffer buf;

    public void wrap(ByteBuffer buf) {
        this.buf = buf;
    }

    public long getTimestamp() {
        return this.buf.getLong(TIMESTAMP_OFFSET);
    }
}
