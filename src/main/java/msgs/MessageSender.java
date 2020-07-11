package msgs;

import java.io.DataOutputStream;
import java.io.IOException;

public interface MessageSender {

    void send(String[] line, DataOutputStream dataOutputStream) throws IOException;
}
