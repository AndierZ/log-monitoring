package apps.producer;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import common.LoggerFactory;
import msgs.MessageSender;
import org.json.simple.JSONArray;
import org.slf4j.Logger;

import java.io.*;
import java.net.Socket;

public class MessageDistributionThread implements Runnable {

    private static Logger LOGGER = LoggerFactory.newLogger();

    private final DataOutputStream dataOutputStream;
    private final CSVReader csvReader;
    private final MessageSender sender;

    /**
     * @param filePath Path to the log file to be parsed and sent
     * @param consumerAddresses List of addresses to send messages to
     * @param sender Sender that builds the message and send to output stream
     * @throws IOException
     */
    public MessageDistributionThread(String filePath, JSONArray consumerAddresses, MessageSender sender) throws IOException {
        this.sender = sender;
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(filePath)));
        String[] hostport = consumerAddresses.get(0).toString().split(":");
        Socket socket = new Socket(hostport[0], Integer.valueOf(hostport[1]));
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        CSVParser parser = new CSVParserBuilder()
                .withSeparator(',')
                .withIgnoreQuotations(true)
                .build();

        this.csvReader = new CSVReaderBuilder(bufferedReader)
                .withSkipLines(0)
                .withCSVParser(parser)
                .build();
    }

    @Override
    public void run() {
        try {
            for (String[] strings : this.csvReader) {
                this.sender.send(strings, this.dataOutputStream);
            }
        } catch (Exception e) {
            LOGGER.error("BufferedReader stopped unexpectedly.", e);
        } finally {
            try {
                this.csvReader.close();
                this.dataOutputStream.close();
            } catch (IOException e) {
                LOGGER.error("MessageDistributionThread close failed.", e);
            }
        }
    }
}