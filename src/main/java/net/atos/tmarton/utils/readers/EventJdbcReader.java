package net.atos.tmarton.utils.readers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siemens.dxt.persistence.digest.AuditEvent;
import net.atos.tmarton.utils.writers.AuditEventFileWriter;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by tmarton on 1/6/16.
 */
public class EventJdbcReader<T> {

    private static EntityManagerFactory emf;
    private static final int PAGE_SIZE = 1000;

    private Class<T> type;
    private BlockingQueue<String> queue;

    static {
        emf = Persistence.createEntityManagerFactory("AuditEventPU");
    }

    public EventJdbcReader(Class<T> type, BlockingQueue<String> queue) {
        this.type = type;
        this.queue = queue;
    }

    public void produce() {
        List<AuditEvent> result = new ArrayList<>();
        EntityManager em = emf.createEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        int pageNumber = 1;

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        countQuery.select(cb.count(countQuery.from(type)));
        Long totalCount = em.createQuery(countQuery).getSingleResult();


        CriteriaQuery<T> eventQuery = cb.createQuery(type);
        ParameterExpression<String> param = cb.parameter(String.class);


        Root<T> ae = eventQuery.from(type);
        eventQuery.select(ae);
        eventQuery.where(cb.equal(ae.get("auditMessage").get("identificationSource"), param));
        eventQuery.orderBy(cb.asc(ae.get("id")));

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        TypedQuery<T> query = em.createQuery(eventQuery);
        while (pageNumber < totalCount.intValue()) {
            query.setParameter(param, "DirX Identity");
            query.setFirstResult(pageNumber - 1);
            query.setMaxResults(PAGE_SIZE);
            query.getResultList().stream().forEach(e -> {
                try {
                    queue.add(mapper.writeValueAsString(e));
                } catch (JsonProcessingException ex) {

                }
            });
            pageNumber += PAGE_SIZE;
        }
        queue.add("\\u001a");
    }

    public static void main(String args[]) throws InterruptedException {
        BlockingQueue<String> queue = new LinkedBlockingDeque<>();
        EventJdbcReader producer = new EventJdbcReader(AuditEvent.class, queue);
        Thread producerThread = new Thread(() -> producer.produce());
        AuditEventFileWriter consumer = new AuditEventFileWriter(queue);
        Thread consumerThread = new Thread(() -> consumer.consume());

        producerThread.start();
        consumerThread.start();

        producerThread.join();
        consumerThread.join();
        System.out.println("ok");
    }

}
