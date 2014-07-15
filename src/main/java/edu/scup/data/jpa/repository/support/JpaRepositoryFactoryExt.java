package edu.scup.data.jpa.repository.support;

import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class JpaRepositoryFactoryExt extends JpaRepositoryFactory {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Creates a new {@link org.springframework.data.jpa.repository.support.JpaRepositoryFactory}.
     *
     * @param entityManager must not be {@literal null}
     */
    public JpaRepositoryFactoryExt(EntityManager entityManager) {
        super(entityManager);
        this.entityManager = entityManager;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getTargetRepository(RepositoryMetadata metadata) {
        return new SimpleJpaRepositoryExt(getEntityInformation(metadata.getDomainType()),entityManager);
    }

    @Override
    public Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return SimpleJpaRepositoryExt.class;
    }
}