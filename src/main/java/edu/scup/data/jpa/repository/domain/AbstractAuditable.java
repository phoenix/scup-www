package edu.scup.data.jpa.repository.domain;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * Abstract base class for auditable entities. Stores the audition values in persistent fields.
 *
 * @param <PK> the type of the auditing type's idenifier
 * @see org.springframework.data.jpa.domain.AbstractAuditable
 */
@MappedSuperclass
public abstract class AbstractAuditable<U, PK extends Serializable> extends AbstractPersistable<PK>
        implements Auditable<U, PK> {

    private static final long serialVersionUID = -2363991186256484787L;

    @ManyToOne
    @NotFound(action= NotFoundAction.IGNORE)
    private U createdBy;

    @ManyToOne
    @NotFound(action= NotFoundAction.IGNORE)
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

    /**
     * Sets the id of the entity.
     *
     * @param id the id to set
     */
    public void setId(final PK id) {
        super.setId(id);
    }
}
