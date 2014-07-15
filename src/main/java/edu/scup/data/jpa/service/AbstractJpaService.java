package edu.scup.data.jpa.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.persistence.SearchFilter;
import edu.scup.data.jpa.repository.JpaRepositoryExt;

import java.io.Serializable;
import java.util.Collection;

@Transactional(readOnly = true)
public abstract class AbstractJpaService<T, ID extends Serializable> {

    /**
     * Retrieves an entity by its id.
     *
     * @param id must not be {@literal null}.
     * @return the entity with the given id or {@literal null} if none found
     * @throws IllegalArgumentException if {@code id} is {@literal null}
     */
    public T findOne(ID id) {
        return getDao().findOne(id);
    }

    public T findOne(ID id, boolean includeLogicalDeleted) {
        return getDao().findOne(id, includeLogicalDeleted);
    }

    public Page<T> paginate(final Pageable pageable, final Collection<SearchFilter> filters) {
        return getDao().findPage(pageable, filters);
    }

    public Page<T> paginate(final Pageable pageable, final Collection<SearchFilter> filters, boolean autoExcludeLogicalDeleted) {
        return getDao().findPage(pageable, filters, autoExcludeLogicalDeleted);
    }

    /**
     * Saves a given entity. Use the returned instance for further operations as the save operation might have changed the
     * entity instance completely.
     *
     * @param entity
     * @return the saved entity
     */
    @Transactional
    public T save(T entity) {
        return getDao().save(entity);
    }

    /**
     * Logical Deletes the entity with the given ids.
     *
     * @param id must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@code id} is {@literal null}
     */
    @Transactional
    public void logicalDelete(ID id) {
        getDao().logicalDelete(id);
    }

    protected abstract JpaRepositoryExt<T, ID> getDao();
}
