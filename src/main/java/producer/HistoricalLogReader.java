package producer;

import common.Constants;
import common.Context;
import common.LoggerFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoricalLogReader extends LogReader {

    private static Logger LOGGER = LoggerFactory.newLogger();
    private final ExecutorService executorService;
    private List<Runnable> fileParsers = new ArrayList<>();

    public HistoricalLogReader(Context context) throws IOException {
        super(context);
        // read list of files
        JSONObject config = context.getConfig();
        JSONArray logFiles = (JSONArray) config.get(Constants.LOG_FILES);
        JSONArray consumerAddresses = (JSONArray) config.get(Constants.CONSUMER_ADDRESSES);

        for(int i=0; i< logFiles.size(); i++) {
            String filePath = logFiles.get(i).toString();
            fileParsers.add(new LogEntryDistributionThread(filePath, consumerAddresses));
        }

        this.executorService = Executors.newFixedThreadPool(10);
        // read list of consumer
        // create new worker thread per file
    }

    @Override
    protected void start() {
        this.fileParsers.forEach(this.executorService::submit);
    }

    @Override
    protected void shutdown() {
        this.executorService.shutdown();
    }

    private class LogEntryDistributionThread implements Runnable {

        private final BufferedReader bufferedReader;
        private final DataOutputStream dataOutputStream;

        public LogEntryDistributionThread(String filePath, JSONArray consumerAddresses) throws IOException {
            this.bufferedReader = new BufferedReader(new FileReader(new File(filePath)));
            String[] hostport = consumerAddresses.get(0).toString().split(":");
            Socket socket = new Socket(hostport[0], Integer.valueOf(hostport[1]));
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        }

        private void send(String l) throws IOException {
            this.dataOutputStream.writeUTF(l);
        }

        @Override
        public void run() {
            try {
                while (this.bufferedReader.ready()) {
                    String l = this.bufferedReader.readLine();
                    send(l);
                }
            } catch (IOException e) {
                LOGGER.error("BufferedReader stopped unexpectedly.", e);
            } finally {
                try {
                    this.bufferedReader.close();
                    this.dataOutputStream.close();
                } catch (IOException e) {
                    LOGGER.error("LogEntryDistributionThread close failed.", e);
                }
            }
        }
    }
}
