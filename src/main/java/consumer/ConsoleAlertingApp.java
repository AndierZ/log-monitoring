package consumer;

import common.App;
import common.Constants;
import common.Context;
import common.LoggerFactory;
import org.json.simple.JSONObject;
import org.slf4j.Logger;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ConsoleAlertingApp extends App {

    private static Logger LOGGER = LoggerFactory.newLogger();

    private final ServerSocket serverSocket;

    public ConsoleAlertingApp(Context context) throws IOException {
        super(context);
        JSONObject config = context.getConfig();
        this.serverSocket = new ServerSocket(Integer.valueOf(config.get(Constants.PORT).toString()));

    }

    @Override
    protected void start() throws IOException {
        while(true) {
            Socket socket = this.serverSocket.accept();
            InputStream inputStream = socket.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            while(true) {
                String message = dataInputStream.readUTF();
                System.out.println("Recieved message " + message);
            }
//            socket.close();
        }
    }

    @Override
    protected void shutdown() throws IOException {
        this.serverSocket.close();
    }

    private class LogEntryProcessor {
        private LogEntryProcessor(Socket socket) throws IOException {
            InputStream inputStream = socket.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);

            // read the message from the socket
            String message = dataInputStream.readUTF();
            System.out.println("Recieved message " + message);
            socket.close();
        }
    }
}
