package edu.scup.data.jpa.repository.domain;

import org.hibernate.annotations.Type;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public abstract class BaseEntity<U, PK extends Serializable>
        extends AbstractAuditable<U, PK> implements LogicalDeletable {
    private static final long serialVersionUID = 9183162674069358813L;

    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean deleted = false;

    @Override
    public void setDeleted(final boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean isDeleted() {
        return deleted == null ? false : deleted;
    }
}
