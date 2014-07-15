package edu.scup.data.jpa.repository.domain;

public interface LogicalDeletable {
    String LOGICAL_DELETED_COLUMN_NAME = "deleted";

    void setDeleted(final boolean deleted);

    boolean isDeleted();
}
