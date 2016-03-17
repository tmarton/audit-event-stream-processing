package net.atos.tmarton.utils.writers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

/**
 * Created by tmarton on 1/6/16.
 */
public class AuditEventFileWriter {

    private static final String FILE_NAME_PREFIX = "/home/tmarton/Documents/Diplomka/json_docs/input_events_";

    private BlockingQueue<String> queue;

    public AuditEventFileWriter(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    public void consume() {

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        try (FileWriter file = new FileWriter(FILE_NAME_PREFIX + (new SimpleDateFormat("MM_dd_yyyy").format(new Date())) + ".json")) {
            while (true) {
                String auditEvent = queue.take();
                if ("\\u001a".equals(auditEvent)) {
                    return;
                }
                file.write(auditEvent + "\n");
            }
        } catch (InterruptedException | IOException ex) {
            // do nothing
        }

    }
}
