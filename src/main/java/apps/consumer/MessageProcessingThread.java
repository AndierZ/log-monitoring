package apps.consumer;

import common.Context;
import common.LoggerFactory;
import msgs.LogEntryMeta;
import msgs.MessageParser;
import msgs.ParserFactory;
import org.slf4j.Logger;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * @param <T> An implementation of MessageParser for a specific message
 */
public class MessageProcessingThread<T extends MessageParser> implements Runnable {

    private static Logger LOGGER = LoggerFactory.newLogger();

    private final DataInputStream dataInputStream;
    private final Socket socket;
    private final ByteBuffer buf;
    private final MessageHandler<T> handler;
    private final int msgType;
    private final T parser;


    /**
     * Reads bytes from given socket, parses fields into a readable object, and invokes callbacks on the message handler
     * @param context   The given context
     * @param socket    The socket for receiving messages
     * @param msgType   The msgType consistent with the implemented type T
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public MessageProcessingThread(Context context, Socket socket, int msgType) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        this.socket = socket;
        this.handler = new MessageHandler<>(context);
        this.dataInputStream = new DataInputStream(socket.getInputStream());
        this.buf = ByteBuffer.allocate(LogEntryMeta.MAX_LEN);
        this.msgType = msgType;
        this.parser = (T) ParserFactory.getParser(msgType);
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
                if (this.msgType == msgType) {
                    parser.wrap(buf);
                    handler.handle(parser);
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