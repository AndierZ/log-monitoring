package apps.producer;

import common.App;
import common.Constants;
import common.Context;
import common.LoggerFactory;
import msgs.LogEntryBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoricalLogReader extends App {

    private static Logger LOGGER = LoggerFactory.newLogger();
    private final ExecutorService executorService;
    private List<LogEntryDistributionThread> fileParsers = new ArrayList<>();

    public HistoricalLogReader(Context context) throws IOException {
        super(context);
        JSONObject config = context.getConfig();
        JSONArray logFiles = (JSONArray) config.get(Constants.LOG_FILES);
        JSONArray consumerAddresses = (JSONArray) config.get(Constants.CONSUMER_ADDRESSES);

        for(int i=0; i< logFiles.size(); i++) {
            String filePath = logFiles.get(i).toString();
            fileParsers.add(new LogEntryDistributionThread(filePath, consumerAddresses));
        }

        this.executorService = Executors.newFixedThreadPool(10);
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
        private final LogEntryBuilder logEntryBuilder;

        public LogEntryDistributionThread(String filePath, JSONArray consumerAddresses) throws IOException {
            this.bufferedReader = new BufferedReader(new FileReader(new File(filePath)));
            String[] hostport = consumerAddresses.get(0).toString().split(":");
            Socket socket = new Socket(hostport[0], Integer.valueOf(hostport[1]));
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
            this.logEntryBuilder = new LogEntryBuilder();
        }

        @Override
        public void run() {
            try {
                int i = 0;
                while (this.bufferedReader.ready()) {
                    String l = this.bufferedReader.readLine();
                    this.logEntryBuilder.newMsg().setTimestamp(i++).setSection(l).send(this.dataOutputStream);
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
