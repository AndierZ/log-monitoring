package msgs;

import java.nio.ByteBuffer;

public class LogEntryParser implements MessageParser {

    private ByteBuffer buf;

    @Override
    public void wrap(ByteBuffer buf) {
        this.buf = buf;
    }

    public int getMsgLen() {
        return this.buf.getInt(LogEntryMeta.MSG_LEN_OFFSET);
    }

    @Override
    public int getMsgType() {
        return this.buf.getInt(LogEntryMeta.MSG_TYPE_OFFSET);
    }

    public long getTimestamp() {
        return this.buf.getLong(LogEntryMeta.TIMESTAMP_OFFSET);
    }

    public String getSection() {
        int len = this.buf.getInt(LogEntryMeta.SECTION_OFFSET);
        return new String(this.buf.array(), LogEntryMeta.SECTION_OFFSET + 4, len);
    }
}
