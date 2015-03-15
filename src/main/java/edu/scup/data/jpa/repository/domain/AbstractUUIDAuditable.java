package edu.scup.data.jpa.repository.domain;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Abstract base class for auditable entities. Stores the audition values in persistent fields.
 *
 * @see org.springframework.data.jpa.domain.AbstractAuditable
 */
@MappedSuperclass
public abstract class AbstractUUIDAuditable<U> extends UUIDPersistable
        implements Auditable<U, String> {

    private static final long serialVersionUID = -2363991186256484787L;

    @ManyToOne
    private U createdBy;

    @ManyToOne
    private U lastModifiedBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    public U getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(final U createdBy) {

        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(final Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public U getLastModifiedBy() {

        return lastModifiedBy;
    }

    @Override
    public void setLastModifiedBy(final U lastModifiedBy) {

        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
