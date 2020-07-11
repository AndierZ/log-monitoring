package msgs;

import java.nio.ByteBuffer;

public class LogEntryParser {
    private static int MSG_LEN_OFFSET = 0;
    private static int MSG_TYPE_OFFSET = 4;
    private static int TIMESTAMP_OFFSET = 8;
    private static int SECTION_OFFSET = 16;
    private ByteBuffer buf;

    public void wrap(ByteBuffer buf) {
        this.buf = buf;
    }

    public int getMsgLen() {
        return this.buf.getInt(MSG_LEN_OFFSET);
    }

    public int getMsgType() {
        return this.buf.getInt(MSG_TYPE_OFFSET);
    }

    public long getTimestamp() {
        return this.buf.getLong(TIMESTAMP_OFFSET);
    }

    public String getSection() {
        int len = this.buf.getInt(SECTION_OFFSET);
        return new String(this.buf.array(), SECTION_OFFSET + 4, len);
    }
}
