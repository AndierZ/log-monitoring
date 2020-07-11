package msgs;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class HeartbeatBuilder {

    private static int MSG_TYPE = 1;
    private static int MSG_LEN = 4 + 8;

    private final ByteBuffer buf = ByteBuffer.allocate(MSG_LEN);

    public HeartbeatBuilder newMsg() {
        buf.clear();
        buf.putInt(MSG_TYPE);
        return this;
    }

    public HeartbeatBuilder setTimestamp(long timestamp) {
        buf.putLong(timestamp);
        return this;
    }

    public void send(OutputStream out) throws IOException {
        this.buf.flip();
        out.write(buf.array(), 0, buf.limit());
    }
}
