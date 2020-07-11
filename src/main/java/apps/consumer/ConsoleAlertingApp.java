package apps.consumer;

import common.App;
import common.Constants;
import common.Context;
import common.LoggerFactory;
import msgs.LogEntryBuilder;
import msgs.LogEntryParser;
import org.json.simple.JSONObject;
import org.slf4j.Logger;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConsoleAlertingApp extends App {

    private static Logger LOGGER = LoggerFactory.newLogger();

    private final ServerSocket serverSocket;
    private final ExecutorService executorService;

    public ConsoleAlertingApp(Context context) throws IOException {
        super(context);
        JSONObject config = context.getConfig();
        this.serverSocket = new ServerSocket(Integer.valueOf(config.get(Constants.PORT).toString()));
        this.executorService = Executors.newScheduledThreadPool(10);
    }

    @Override
    protected void start() throws IOException {
        while(true) {
            Socket socket = this.serverSocket.accept();
            LogEntryProcessingThread t = new LogEntryProcessingThread(socket);
            executorService.submit(t);
        }
    }

    @Override
    protected void shutdown() throws IOException {
        this.serverSocket.close();
    }

    private class LogEntryProcessingThread implements Runnable {

        private final DataInputStream dataInputStream;
        private final Socket socket;
        private final LogEntryParser parser;
        private final ByteBuffer buf;

        private LogEntryProcessingThread(Socket socket) throws IOException {
            this.socket = socket;
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.parser = new LogEntryParser();
            this.buf = ByteBuffer.allocate(LogEntryBuilder.MAX_LEN);
        }

        @Override
        public void run() {
            while(true) {
                try {
                    int msgLength = dataInputStream.readInt();
                    int bytesRead = 4;
                    this.buf.clear();
                    this.buf.putInt(msgLength);
                    while(bytesRead < msgLength) {
                        buf.put(dataInputStream.readByte());
                        bytesRead++;
                    }
                    buf.flip();
                    parser.wrap(buf);
                    System.out.println(parser.getMsgLen() + "," + parser.getMsgType() + "," + parser.getTimestamp() + "," + parser.getSection());
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
}
