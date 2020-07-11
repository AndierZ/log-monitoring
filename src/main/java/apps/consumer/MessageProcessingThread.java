package apps.consumer;

import common.Constants;
import common.LoggerFactory;
import msgs.LogEntryMeta;
import msgs.MessageHandler;
import msgs.MessageParser;
import msgs.ParserFactory;
import org.slf4j.Logger;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

public class MessageProcessingThread implements Runnable {

    private static Logger LOGGER = LoggerFactory.newLogger();

    private final DataInputStream dataInputStream;
    private final Socket socket;
    private final ByteBuffer buf;
    private final MessageHandler[] handlers;

    public MessageProcessingThread(Socket socket) throws IOException {
        this.socket = socket;
        this.dataInputStream = new DataInputStream(socket.getInputStream());
        this.handlers = new MessageHandler[Constants.TOTAL_MSG_TYPES];
        this.buf = ByteBuffer.allocate(LogEntryMeta.MAX_LEN);
    }

    public void addHandler(MessageHandler handler) {
        this.handlers[handler.getMessageType()] = handler;
    }

    @SuppressWarnings("unchecked")
    private void onMessage(MessageParser parser) {
        if (this.handlers[parser.getMsgType()] != null) {
            this.handlers[parser.getMsgType()].handle(parser);
        }
    }

    @Override
    public void run() {
        while(true) {
            try {
                this.buf.clear();
                int msgLength = dataInputStream.readInt();
                // 4 for the message length
                int bytesRead = 4;
                this.buf.putInt(msgLength);

                int msgType = dataInputStream.readInt();
                bytesRead += 4;
                this.buf.putInt(msgType);

                while(bytesRead < msgLength) {
                    buf.put(dataInputStream.readByte());
                    bytesRead++;
                }
                buf.flip();
                MessageParser parser = ParserFactory.getParser(msgType);
                if (parser != null) {
                    parser.wrap(buf);
                    onMessage(parser);
                } else {
                    LOGGER.error("Received unrecognized message type {}", msgType);
                }

            } catch (EOFException e) {
                LOGGER.info("DataInputStream ended", e);
                break;
            } catch (IOException e) {
                LOGGER.error("DataInputStream Exception", e);
                break;
            }
        }
        try {
            this.socket.close();
        } catch (IOException e) {
            LOGGER.error("Socket close exception", e);
        }
    }
}