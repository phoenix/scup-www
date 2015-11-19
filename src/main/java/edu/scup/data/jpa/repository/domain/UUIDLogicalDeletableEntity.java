package edu.scup.data.jpa.repository.domain;

import org.hibernate.annotations.Type;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class UUIDLogicalDeletableEntity extends UUIDPersistable implements LogicalDeletable {
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
