package net.atos.tmarton.eventprocessing;

import com.espertech.esper.client.*;
import net.atos.tmarton.utils.readers.EventFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class AuditEventAlerter {

    private static final Logger LOG = LoggerFactory.getLogger(AuditEventAlerter.class);

    public static void main(String[] args) throws InterruptedException {

        Configuration conf = new Configuration();
        conf.addEventType(TransformedAuditEvent.class);

        EPServiceProvider eventEngine = EPServiceProviderManager.getProvider("eventEngine", conf);
        EPRuntime epRuntime = eventEngine.getEPRuntime();

        EPAdministrator administrator = eventEngine.getEPAdministrator();
        EPStatement statement = administrator.createEPL("select * from TransformedAuditEvent(operation='Login') where identificationOutcome != 0");
        statement.addListener((x,y) -> System.out.println("Alert. Some condition has been violated."));

        BlockingQueue queue = new LinkedBlockingDeque<>();
        EventFileReader<TransformedAuditEvent> reader = new EventFileReader<TransformedAuditEvent>(TransformedAuditEvent.class, queue);
        reader.setFileName("/home/tmarton/Documents/Diplomka/json_docs/processed_events");
        Thread readerThread = new Thread(() -> reader.produce());


        Thread senderThread = new Thread(() -> {
            try {
                while (true) {
                    Object event = queue.poll(2000, TimeUnit.MILLISECONDS);
//                    Object event = queue.take();
                    if (event == null) break;
                    epRuntime.sendEvent(event);
                    LOG.info("Sending event to esper runtime.");
                }
            } catch (InterruptedException ex) {
                LOG.error("AuditEventAlerter Can't poll events from queue.", ex);
            }
        });

        readerThread.start();
        senderThread.start();

        readerThread.join();
        senderThread.join();

        System.out.println("OK");
    }
}