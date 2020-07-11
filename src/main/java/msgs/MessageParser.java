package msgs;

import java.nio.ByteBuffer;

public interface MessageParser {
    void wrap(ByteBuffer buf);
    int getMsgType();
}
