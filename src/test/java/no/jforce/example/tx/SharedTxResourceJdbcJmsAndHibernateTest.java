package no.jforce.example.tx;

import no.jforce.jms.oracle.domain.WorkItemRepository;
import no.jforce.jms.oracle.domain.orm.hibernate.WorkItem;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.jms.Queue;
import javax.sql.DataSource;

import static org.junit.Assert.*;

/**
 * Test that demonstrates transactions spanning three logical resources; JDBC, JMS and Hibernate. All
 * resources share the same {@code DataSource} and therefore a common {@code Connection} is shared among
 * all three resour4ces.
 */
@ContextConfiguration(locations = {"classpath:spring-context.xml", "classpath:spring-hibernate.xml"})
public class SharedTxResourceJdbcJmsAndHibernateTest extends SharedTxResourceJdbcAndJmsTest {

    @Autowired
    protected DataSource dataSource;

    @Autowired
    protected TransactionTemplate tx;

    @Autowired
    protected JmsTemplate jms;

    @Autowired
    protected Queue queue;

    @Autowired
    private WorkItemRepository repository;

    @Test
    public void threeResourceCommit() throws Exception {

        int beforeCount = getRowCount();

        tx.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                insertJms();
                insertJdbc();
                insertHibernate();
            }
        });

        assertEquals("Expected two new inserts.", beforeCount + 2, getRowCount());
        assertNotNull("Expected a JMS message.", jms.receive(queue));
    }

    @Test
    public void threeResourceStatusRollback() throws Exception {

        int beforeCount = getRowCount();

        tx.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                insertJms();
                insertJdbc();
                insertHibernate();
                transactionStatus.setRollbackOnly();
            }
        });

        assertEquals("Expected no inserts surviving the rollback.", beforeCount, getRowCount());
        assertNull("Expected no available JMS message.", jms.receive(queue));
    }

    @Test
    public void threeResourceRollbackAfterThrowable() throws Exception {

        int beforeCount = getRowCount();

        try {
            tx.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                    insertJms();
                    insertJdbc();
                    insertHibernate();
                    throw new RuntimeException("Deliberate exception in order to force rollback.");
                }
            });
        } catch (Throwable t) {
            //
        }

        assertEquals("Expected no inserts surviving the rollback.", beforeCount, getRowCount());
        assertNull("Expected no available JMS message.", jms.receive(queue));
    }

    private void insertHibernate() {
        WorkItem workItem = new WorkItem();
        workItem.setText("Message from jUnit-test.");
        repository.save(workItem);
    }

}
