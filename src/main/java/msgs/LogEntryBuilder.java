package msgs;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Builds binary message for a LogEntry message
 */
public class LogEntryBuilder {

    private final ByteBuffer buf = ByteBuffer.allocate(LogEntryMeta.MAX_LEN);

    private long timestamp;
    private int bytes;
    private int status;
    private String section;
    private String remoteHost;
    private String authUser;
    private String method;
    private String route;

    public LogEntryBuilder newMsg() {
        reset();
        return this;
    }

    public LogEntryBuilder setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public LogEntryBuilder setBytes(int bytes) {
        this.bytes = bytes;
        return this;
    }

    public LogEntryBuilder setStatus(int status) {
        this.status = status;
        return this;
    }

    public LogEntryBuilder setSection(String section) {
        this.section = section;
        return this;
    }

    public LogEntryBuilder setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
        return this;
    }

    public LogEntryBuilder setAuthUser(String authUser) {
        this.authUser = authUser;
        return this;
    }

    public LogEntryBuilder setMethod(String method) {
        this.method = method;
        return this;
    }

    public LogEntryBuilder setRoute(String route) {
        this.route = route;
        return this;
    }

    private void reset() {
        timestamp = 0;
        bytes = 0;
        status = 0;
        section = null;
        remoteHost = null;
        authUser = null;
        method = null;
        route = null;
    }

    public void send(OutputStream out) throws IOException {
        buf.clear();
        // place holder for msg length
        buf.putInt(0);
        buf.putInt(LogEntryMeta.MSG_TYPE);
        // Initial message length: 4 for MSG_LEN. 4 for MSG_TYPE
        int len = 8;
        len += appendLong(timestamp, buf);
        len += appendInt(bytes, buf);
        len += appendInt(status, buf);
        len += appendString(section, buf);
        len += appendString(remoteHost, buf);
        len += appendString(authUser, buf);
        len += appendString(method, buf);
        len += appendString(route, buf);
        buf.putInt(0, len);
        this.buf.flip();
        out.write(buf.array(), 0, buf.limit());
    }

    private int appendInt(Integer val, ByteBuffer buf) {
        buf.putInt(val);
        return 4;
    }

    private int appendString(String val, ByteBuffer buf) {
        if (val == null) {
            buf.putInt(0);
            return 4;
        } else {
            byte[] bytes = val.getBytes(StandardCharsets.UTF_8);
            buf.putInt(bytes.length);
            buf.put(bytes);
            return 4 + bytes.length;
        }
    }

    private int appendLong(Long val, ByteBuffer buf) {
        buf.putLong(val);
        return 8;
    }
}
