package edu.scup.data.jpa.repository.support;

import com.google.common.collect.Lists;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.transform.ResultTransformer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.PersistenceProvider;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springside.modules.persistence.DynamicSpecifications;
import org.springside.modules.persistence.SearchFilter;
import edu.scup.data.jpa.repository.JpaRepositoryExt;
import edu.scup.data.jpa.repository.domain.LogicalDeletable;
import edu.scup.data.jpa.repository.domain.OmsAuditable;
import edu.scup.util.ReflectionUtils;
import edu.scup.web.util.OmsCurrentUser;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.util.*;

public class SimpleJpaRepositoryExt<T, ID extends Serializable>
        extends SimpleJpaRepository<T, ID> implements JpaRepositoryExt<T, ID> {
    private final JpaEntityInformation<T, ?> entityInformation;
    private final EntityManager em;
    private final PersistenceProvider provider;
    private final Class<T> entityClazz;

    public SimpleJpaRepositoryExt(JpaEntityInformation<T, ?> tJpaEntityInformation, EntityManager entityManager) {
        super(tJpaEntityInformation, entityManager);
        this.entityInformation = tJpaEntityInformation;
        this.provider = PersistenceProvider.fromEntityManager(entityManager);
        this.entityClazz = tJpaEntityInformation.getJavaType();
        this.em = entityManager;
    }

    public SimpleJpaRepositoryExt(Class<T> domainClass, EntityManager em) {
        this(JpaEntityInformationSupport.getMetadata(domainClass, em), em);
    }

    @Override
    @Transactional
    public void deleteAll(Iterable<? extends ID> ids) {
        Assert.notNull(ids, "The given Iterable of ids not be null!");

        for (ID id : ids) {
            delete(id);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public TypedQuery<T> createTypedQuery(String jpql, Object... params) {
        Assert.hasText(jpql, "queryString不能为空");
        TypedQuery<T> query = (TypedQuery<T>) em.createQuery(jpql);
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                //ordinal parameters are 1-based
                query.setParameter(i + 1, params[i]);
            }
        }
        return query;
    }

    @Override
    public TypedQuery<T> createCacheableTypedQuery(String jpql, Object... params) {
        return createTypedQuery(jpql, params).setHint("org.hibernate.cacheable", true);
    }

    @Override
    public Query createQuery(String jpql, Object... params) {
        Assert.hasText(jpql, "queryString不能为空");
        Query query = em.createQuery(jpql);
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                //ordinal parameters are 1-based
                query.setParameter(i + 1, params[i]);
            }
        }
        return query;
    }

    @Override
    public Query createCacheableQuery(String jpql, Object... params) {
        return createQuery(jpql, params).setHint("org.hibernate.cacheable", true);
    }

    @Override
    public Query createNativeQuery(String sql, Object... params) {
        Assert.hasText(sql, "queryString不能为空");
        Query query = em.createNativeQuery(sql);
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                //ordinal parameters are 1-based
                query.setParameter(i + 1, params[i]);
            }
        }
        return query;
    }

    @Override
    public Query createCacheableNativeQuery(String sql, Class resultClass, Object... params) {
        return createNativeQuery(sql, resultClass, params).setHint("org.hibernate.cacheable", true);
    }

    @Override
    public T findOneByJpql(String jpql, Object... params) {
        List<T> result = createCacheableTypedQuery(jpql, params).setMaxResults(1).getResultList();
        return CollectionUtils.isEmpty(result) ? null : result.get(0);
    }

    @Override
    public T findOne(final ID id, boolean includeLogicalDeleted) {
        if (includeLogicalDeleted || !LogicalDeletable.class.isAssignableFrom(entityClazz)) {
            return findOne(id);
        } else {
            return findOne(new Specification<T>() {

                @Override
                public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                    List<Predicate> predicates = Lists.newArrayList();
                    Path<Boolean> deletedPath = root.get(LogicalDeletable.LOGICAL_DELETED_COLUMN_NAME);
                    predicates.add(builder.isFalse(deletedPath));
                    Iterable<String> idAttributeNames = entityInformation.getIdAttributeNames();
                    if (entityInformation.hasCompositeId()) {
                        for (String idAttributeName : idAttributeNames) {
                            predicates.add(builder.equal(root.get(idAttributeName), entityInformation.getCompositeIdAttributeValue(id, idAttributeName)));
                        }
                    } else {
                        predicates.add(builder.equal(root.get(idAttributeNames.iterator().next()), id));
                    }
                    return builder.and(predicates.toArray(new Predicate[predicates.size()]));
                }
            });
        }
    }

    @Override
    public void logicalDelete(ID id) {
        T entity = findOne(id);
        if (entity instanceof LogicalDeletable) {
            LogicalDeletable logicalDeletable = (LogicalDeletable) entity;
            logicalDeletable.setDeleted(true);
            save(entity);
        } else {
            throw new RuntimeException("该实体不能被逻辑删除");
        }
    }

    @Transactional
    @Override
    @SuppressWarnings("unchecked")
    public <S extends T> S save(S entity) {
        if (entity instanceof OmsAuditable) {
            OmsAuditable auditEntity = (OmsAuditable) entity;
            if (auditEntity.isNew()) {
                auditEntity.setCreatedBy(OmsCurrentUser.getLoginname());
                auditEntity.setCreatedDate(new Date());
            } else {
                if (auditEntity.getCreatedDate() == null) {//可能没有将数据库中的audit信息赋值给要入库的对象
                    OmsAuditable db = (OmsAuditable) findOne((ID) auditEntity.getId());
                    auditEntity.setCreatedBy(db.getCreatedBy());
                    auditEntity.setCreatedDate(db.getCreatedDate());
                    if (entity instanceof LogicalDeletable) {
                        ((LogicalDeletable) entity).setDeleted(((LogicalDeletable) db).isDeleted());
                    }
                }
                auditEntity.setLastModifiedDate(new Date());
                auditEntity.setLastModifiedBy(OmsCurrentUser.getLoginname());
            }
        }
        return super.save(entity);
    }

    @Override
    public Query createNativeQuery(String sql, Class resultClass, Object... params) {
        Assert.hasText(sql, "queryString不能为空");
        Query query = em.createNativeQuery(sql, resultClass);
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                //ordinal parameters are 1-based
                query.setParameter(i + 1, params[i]);
            }
        }
        return query;
    }

    @Override
    public Query createCacheableNativeQuery(String sql, Object... params) {
        return createNativeQuery(sql, params).setHint("org.hibernate.cacheable", true);
    }

    @Override
    public Page<T> findPage(Pageable pageRequest, Collection<SearchFilter> filters) {
        return findPage(pageRequest, filters, true);
    }

    @Override
    public Page<T> findPage(Pageable pageRequest, Collection<SearchFilter> filters, boolean autoExcludeLogicalDeleted) {
        Assert.notNull(pageRequest, "page不能为空");
        Specification<T> specifications = DynamicSpecifications.bySearchFilter(filters, entityClazz, autoExcludeLogicalDeleted);
        return findAll(specifications, pageRequest);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Page<T> findHPage(Pageable pageRequest, Collection<SearchFilter> filters) {
        Criterion[] criterions = DynamicSpecifications.buildCriterions(filters, entityClazz);
        Criteria criteria = createCriteria(entityClazz, criterions);
        Long total = countCriteriaResult(criteria);
        criteria.setFirstResult(pageRequest.getOffset());
        criteria.setMaxResults(pageRequest.getPageSize());

        List<T> content = total > pageRequest.getOffset() ? criteria.list() : Collections.<T>emptyList();

        return new PageImpl<>(content, pageRequest, total);
    }

    /**
     * Merge the state of the given entity into the current persistence context.
     *
     * @param entity entity instance
     * @return the managed instance that the state was merged to
     */
    public T merge(T entity) {
        return em.merge(entity);
    }

    @Override
    public Session getSession() {
        return em.unwrap(Session.class);
    }

    @Override
    public Criteria createCriteria(final Criterion... criterions) {
        Criteria criteria = getSession().createCriteria(entityClazz);
        for (Criterion c : criterions) {
            criteria.add(c);
        }
        return criteria;
    }

    @Override
    public Criteria createCriteria(Class entityClass, Criterion... criterions) {
        Criteria criteria = getSession().createCriteria(entityClass);
        for (Criterion c : criterions) {
            criteria.add(c);
        }
        return criteria;
    }

    private long countCriteriaResult(final Criteria c) {
        CriteriaImpl impl = (CriteriaImpl) c;

        // 先把Projection、ResultTransformer、OrderBy取出来,清空三者后再执行Count操作
        Projection projection = impl.getProjection();
        ResultTransformer transformer = impl.getResultTransformer();

        List<CriteriaImpl.OrderEntry> orderEntries = ReflectionUtils.getFieldValue(impl, "orderEntries");
        ReflectionUtils.setFieldValue(impl, "orderEntries", new ArrayList());

        // 执行Count查询
        Long totalCountObject = (Long) c.setProjection(Projections.rowCount()).uniqueResult();
        long totalCount = (totalCountObject != null) ? totalCountObject : 0;

        // 将之前的Projection,ResultTransformer和OrderBy条件重新设回去
        c.setProjection(projection);

        if (projection == null) {
            c.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
        }
        if (transformer != null) {
            c.setResultTransformer(transformer);
        }
        ReflectionUtils.setFieldValue(impl, "orderEntries", orderEntries);

        return totalCount;
    }
}
