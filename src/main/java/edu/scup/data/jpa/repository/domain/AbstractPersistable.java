package edu.scup.data.jpa.repository.domain;

import java.io.Serializable;

public class AbstractPersistable<PK extends Serializable>
        extends org.springframework.data.jpa.domain.AbstractPersistable<PK> {
    private static final long serialVersionUID = -2216539062322460976L;

    @Override
    public void setId(final PK id) {
        super.setId(id);
    }
}
