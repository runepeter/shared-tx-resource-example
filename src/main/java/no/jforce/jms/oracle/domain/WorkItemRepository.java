package no.jforce.jms.oracle.domain;

import no.jforce.jms.oracle.domain.orm.hibernate.WorkItem;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkItemRepository {

    WorkItem get(Long id);

    Long save(WorkItem workItem);

}
