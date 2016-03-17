package net.atos.tmarton.utils.readers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siemens.dxt.persistence.digest.AuditEvent;
import net.atos.tmarton.utils.writers.AuditEventFileWriter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by tmarton on 1/24/16.
 */
public class EventFileReader<T> {

    private static final Logger LOG = LoggerFactory.getLogger(EventFileReader.class);

    private static final String GENERAL_FILE_NAME = "/home/tmarton/Documents/Diplomka/json_docs/one_event.json";

    private Class<T> type;
    private BlockingQueue queue;
    private String fileName;

    public EventFileReader(Class<T> type, BlockingQueue queue) {
        this.type = type;
        this.queue = queue;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void produce() {
        try {
            Path filePath = Paths.get(StringUtils.isBlank(fileName) ? GENERAL_FILE_NAME : fileName);
            BufferedReader reader =
                    Files.newBufferedReader(filePath, Charset.defaultCharset());

            JsonFactory f = new MappingJsonFactory();
            JsonParser jp = f.createParser(reader);

            while (true) {
                T event = jp.readValueAs(type);

                if (event == null) {
                    break;
                }

                queue.add(event);
            }
            jp.close();
        } catch (IOException ex) {
            LOG.error("EventFileReader:produce() Some io error happened during producing event.", ex);
        }
    }

    // not very effective, wasteful serialization/deserialization
    public void produceAsString() {
        try {
            Path filePath = Paths.get(StringUtils.isBlank(fileName) ? GENERAL_FILE_NAME : fileName);
            BufferedReader reader =
                    Files.newBufferedReader(filePath, Charset.defaultCharset());

            JsonFactory f = new MappingJsonFactory();
            JsonParser jp = f.createParser(reader);

            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

            while (true) {
                T event = jp.readValueAs(type);

                if (event == null) {
                    break;
                }

                queue.add(mapper.writeValueAsString(event));
            }
            jp.close();
        } catch (IOException ex) {
            LOG.error("EventFileReader:produceAsString() Some io error happened during producing event.", ex);
        }
    }

    public static void main(String args[]) throws InterruptedException {
        BlockingQueue<String> queue = new LinkedBlockingDeque<>();
        EventFileReader<AuditEvent> producer = new EventFileReader<AuditEvent>(AuditEvent.class, queue);
        Thread producerThread = new Thread(() -> producer.produceAsString());
        AuditEventFileWriter consumer = new AuditEventFileWriter(queue);
        Thread consumerThread = new Thread(() -> consumer.consume());

        producerThread.start();
        consumerThread.start();

        producerThread.join();
        consumerThread.join();
        System.out.println("ok");
    }
}
