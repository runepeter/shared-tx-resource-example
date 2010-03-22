package no.jforce.jms.oracle.domain.orm.hibernate;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "work_item_tbl")
public class WorkItem implements Serializable {

    @Id
    @SequenceGenerator(name = "jallaGen", sequenceName = "JALLA_SEQ")
    @GeneratedValue(generator = "jallaGen", strategy = GenerationType.SEQUENCE)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id = null;

    @Column(name = "text")
    private String text;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }
    
}
