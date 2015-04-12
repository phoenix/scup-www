package edu.scup.data.jpa.repository;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springside.modules.persistence.SearchFilter;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@NoRepositoryBean
public interface JpaRepositoryExt<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    void deleteAll(Iterable<? extends ID> ids);

    /**
     * 根据查询JPQL与参数列表创建Query对象.
     * 与find()函数可进行更加灵活的操作.
     *
     * @param params 数量可变的参数,按顺序绑定.
     */
    TypedQuery<T> createTypedQuery(final String jpql, final Object... params);

    TypedQuery<T> createCacheableTypedQuery(final String jpql, final Object... params);

    Query createQuery(final String jpql, final Object... params);

    Query createCacheableQuery(final String jpql, final Object... params);

    Query createNativeQuery(final String sql, final Object... params);

    Query createCacheableNativeQuery(final String sql, final Object... params);

    Query createNativeQuery(final String sql, Class resultClass, final Object... params);

    Query createCacheableNativeQuery(final String sql, Class resultClass, final Object... params);

    T findOneByJpql(final String jpql, final Object... params);

    T findOne(ID id, boolean includeLogicalDeleted);

    T findFirst(final Collection<SearchFilter> filters, final Sort sort);

    void logicalDelete(ID id);

    /**
     * 按属性过滤条件列表分页查找对象.
     */
    Page<T> findPage(final Pageable pageRequest, final Collection<SearchFilter> filters);

    List<T> findAll(final Collection<SearchFilter> filters);

    List<T> findLimit(final Collection<SearchFilter> filters, final Pageable pageRequest);

    /**
     * 按属性过滤条件列表分页查找对象.
     */
    Page<T> findPage(final Pageable pageRequest, final Collection<SearchFilter> filters, boolean autoExcludeLogicalDeleted);

    /**
     * <p>按属性过滤条件列表分页查找对象,支持Array等特殊native sql查询</p>
     * 对于标准JPA支持的查询,建议使用{@link #findPage(Pageable, Collection)}
     */
    Page<T> findHPage(final Pageable pageRequest, final Collection<SearchFilter> filters);

    /**
     * Merge the state of the given entity into the current persistence context.
     *
     * @param entity entity instance
     * @return the managed instance that the state was merged to
     */
    T merge(T entity);

    Session getSession();

    Criteria createCriteria(final Criterion... criterions);

    Criteria createCriteria(Class entityClass, Criterion... criterions);
}
