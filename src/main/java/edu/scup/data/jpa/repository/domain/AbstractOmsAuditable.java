package edu.scup.data.jpa.repository.domain;

import javax.persistence.Column;
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
 * audit 使用OMS的登陆用户名
 */
@MappedSuperclass
public abstract class AbstractOmsAuditable<PK extends Serializable> extends AbstractOmsPersistable<PK>
        implements OmsAuditable<PK> {

    private static final long serialVersionUID = -2363991186256484787L;

    @Column(name = "create_user")
    private String createdBy;

    @Column(name = "modify_user")
    private String lastModifiedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time")
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_time")
    private Date lastModifiedDate;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(final String createdBy) {

        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(final Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {

        return lastModifiedBy;
    }

    public void setLastModifiedBy(final String lastModifiedBy) {

        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
