package no.jforce.jms.oracle;

import oracle.jms.AQjmsSession;
import org.springframework.beans.factory.FactoryBean;

import javax.jms.QueueConnectionFactory;

/**
 * Creates a reference to Oracle AQ Queue {@code queueName}.
 */
public class OracleQueueFactoryBean implements FactoryBean {

    private final QueueConnectionFactory queueConnectionFactory;

    private final String queueSchema;
    private final String queueName;

    public OracleQueueFactoryBean(final QueueConnectionFactory queueConnectionFactory,
                                  final String queueSchema,
                                  final String queueName) {
        this.queueConnectionFactory = queueConnectionFactory;
        this.queueName = queueName;
        this.queueSchema = queueSchema;
    }

    public Object getObject() throws Exception {

        AQjmsSession session = (AQjmsSession) queueConnectionFactory.createQueueConnection().createQueueSession(true, 0);

        return session.getQueue(queueSchema, queueName);
    }

    public Class getObjectType() {
        return javax.jms.Queue.class;
    }

    public boolean isSingleton() {
        return false; // TODO runepeter: Determine what the correct value should be.
    }

}
