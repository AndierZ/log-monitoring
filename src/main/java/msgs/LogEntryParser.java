package msgs;

import java.nio.ByteBuffer;

/**
 * Wrapper to read LogEntry fields out of a binary buffer
 */
public class LogEntryParser implements MessageParser {

    private int msgLen;
    private int msgType;
    private long timestamp;
    private int bytes;
    private int status;
    private String section;
    private String remoteHost;
    private String authUser;
    private String method;
    private String route;

    LogEntryParser() {

    }

    @Override
    public void wrap(ByteBuffer buf) {
        this.msgLen = buf.getInt();
        this.msgType = buf.getInt();
        this.timestamp = buf.getLong();
        this.bytes = buf.getInt();
        this.status = buf.getInt();
        this.section = getString(buf);
        this.remoteHost = getString(buf);
        this.authUser = getString(buf);
        this.method = getString(buf);
        this.route = getString(buf);
    }

    private String getString(ByteBuffer buf) {
        // TODO If there aren't many distinct values consider String.intern()
        int len = buf.getInt();
        if (len == 0) {
            return null;
        }
        int pos = buf.position();
        buf.position(pos + len);
        return new String(buf.array(), pos, len);
    }

    public int getMsgLen() {
        return msgLen;
    }

    @Override
    public int getMsgType() {
        return msgType;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    public int getBytes() {
        return bytes;
    }

    public int getStatus() {
        return status;
    }

    public String getSection() {
        return section;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public String getAuthUser() {
        return authUser;
    }

    public String getMethod() {
        return method;
    }

    public String getRoute() {
        return route;
    }
}
