package no.jforce.jms.oracle.domain.orm.hibernate;

import no.jforce.jms.oracle.domain.WorkItemRepository;
import org.hibernate.SessionFactory;

public class HibernateWorkItemRepository implements WorkItemRepository {

    private final SessionFactory sessionFactory;

    public HibernateWorkItemRepository(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public WorkItem get(final Long id) {
        return (WorkItem) sessionFactory.getCurrentSession()
                .createQuery("from no.jforce.jms.oracle.domain.orm.hibernate.WorkItem wi where wi.id=?")
                .setParameter(0, id)
                .list().get(0);
    }

    public Long save(final WorkItem workItem) {
        return (Long) sessionFactory.getCurrentSession()
                .save(workItem);
    }
    
}
