package no.jforce.example.tx;

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
 * Tests <tt>Shared Transaction Resource</tt>-pattern using JDBC and JMS via a shared (Oracle) DataSource.
 */
@ContextConfiguration(locations = {"classpath:spring-context.xml"})
public class SharedTxResourceJdbcAndJmsTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    protected DataSource dataSource;

    @Autowired
    protected TransactionTemplate tx;

    @Autowired
    protected JmsTemplate jms;

    @Autowired
    protected Queue queue;

    @Test
    public void jmsAndJdbcCommit() throws Exception {

        int beforeCount = getRowCount();

        tx.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                insertJdbc();
                insertJms();
            }
        });

        assertEquals("Expected one additional row inserted.", beforeCount + 1, getRowCount());
        assertNotNull("Expected an available JMS message.", jms.receive(queue));
    }

    @Test
    public void jmsAndJdbcStatusRollback() throws Exception {

        int beforeCount = getRowCount();

        tx.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                insertJdbc();
                insertJms();
                transactionStatus.setRollbackOnly();
            }
        });

        assertEquals("Expected no inserts surviving the rollback.", beforeCount, getRowCount());
        assertNull("Expected no available JMS message.", jms.receive(queue));
    }

    @Test
    public void jmsAndJdbcRollbackAfterThrowable() throws Exception {

        int beforeCount = getRowCount();

        try {
            tx.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                    insertJdbc();
                    insertJms();
                    throw new RuntimeException("Deliberate exception in order to force rollback.");
                }
            });
        } catch (Throwable e) {
            // ignore
        }

        assertEquals("Expected no inserts surviving the rollback.", beforeCount, getRowCount());
        assertNull("Expected no available JMS message.", jms.receive(queue));
    }

    protected void insertJms() {
        jms.convertAndSend(queue, "Message from jUnit-test.");
    }

    protected void insertJdbc() {
        SimpleJdbcTemplate jdbc = new SimpleJdbcTemplate(dataSource);
        jdbc.update("INSERT INTO work_item_tbl(id, text) VALUES(jalla_seq.nextval, 'Message from jUnit-test.')");
    }

    protected int getRowCount() {
        SimpleJdbcTemplate jdbc = new SimpleJdbcTemplate(dataSource);
        return jdbc.queryForInt("SELECT COUNT(*) FROM work_item_tbl");
    }

}


