package org.springside.modules.persistence;

import com.google.common.collect.Lists;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jpa.criteria.path.SingularAttributePath;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springside.modules.utils.StringUtils;
import edu.scup.data.jpa.repository.domain.LogicalDeletable;

import javax.persistence.criteria.*;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Operator增加IN,ISNULL,NOTNULL
 * 增加LogicalDeleted逻辑删除处理
 * 修复一对多不能查询的bug
 * 修复String类型的Date不能查询的bug
 * IN 查询时先判断参数是否为Collection,再强制转换
 */
public class DynamicSpecifications {

    public static <T> Specification<T> bySearchFilter(final Collection<SearchFilter> filters, final Class<T> entityClazz) {
        return bySearchFilter(filters, entityClazz, true);
    }

    public static <T> Specification<T> bySearchFilter(final Collection<SearchFilter> filters, final Class<T> entityClazz, final boolean autoExcludeLogicalDeleted) {
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                List<Predicate> predicates = Lists.newArrayList();
                if (!CollectionUtils.isEmpty(filters)) {
                    for (SearchFilter filter : filters) {
                        // nested path translate, 如Task的名为"user.name"的filedName, 转换为Task.user.name属性
                        String[] names = StringUtils.split(filter.fieldName, ".");
                        Path expression = root.get(names[0]);
                        if (Collection.class.isAssignableFrom(expression.getJavaType())) {//修复一对多不能查询的bug
                            //先只支持一级的collection,Spring-Data-JPA默认使用leftJoin,这里也需要用LEFT，否则在增加一个Order的时候，spring-data-jpa会再去创建一个leftJoin
                            expression = root.join(names[0], JoinType.LEFT);
                        }
                        for (int i = 1; i < names.length; i++) {
                            expression = expression.get(names[i]);
                        }

                        Object value = filter.value;

                        Class clz = expression.getJavaType();
                        if (!clz.isPrimitive() && !clz.equals(value.getClass()) && !Collection.class.isAssignableFrom(value.getClass()) && !value.getClass().isArray()) {
                            if (clz.equals(Serializable.class) && Persistable.class.isAssignableFrom(entityClazz)
                                    && "id".equals(((SingularAttributePath) expression).getAttribute().getName())) {//处理PK
                                Type type = entityClazz.getGenericSuperclass();
                                if (type instanceof ParameterizedType) {
                                    Type[] gens = ((ParameterizedType) type).getActualTypeArguments();
                                    if (gens != null && gens.length > 0) {//约定第一个泛型为主键类型
                                        clz = (Class) gens[0];
                                    }
                                }
                            }
                            value = StringUtils.stringToObject(String.valueOf(value), clz);
                        }

                        // logic operator
                        switch (filter.operator) {
                            case EQ:
                                predicates.add(builder.equal(expression, value));
                                break;
                            case LIKE:
                                predicates.add(builder.like(expression, "%" + value + "%"));
                                break;
                            case GT:
                                predicates.add(builder.greaterThan(expression, (Comparable) value));
                                break;
                            case LT:
                                predicates.add(builder.lessThan(expression, (Comparable) value));
                                break;
                            case GTE:
                                predicates.add(builder.greaterThanOrEqualTo(expression, (Comparable) value));
                                break;
                            case LTE:
                                predicates.add(builder.lessThanOrEqualTo(expression, (Comparable) value));
                                break;
                            case IN:
                                if (value instanceof Collection) {
                                    predicates.add(expression.in((Collection) value));
                                } else {
                                    predicates.add(expression.in((Object[]) value));
                                }
                                break;
                            case ISNULL:
                                predicates.add(builder.isNull(expression));
                                break;
                            case NOTNULL:
                                predicates.add(builder.isNotNull(expression));
                                break;
                        }
                    }
                }

                if (autoExcludeLogicalDeleted && LogicalDeletable.class.isAssignableFrom(entityClazz)) {
                    Path<Boolean> deletedPath = root.get(LogicalDeletable.LOGICAL_DELETED_COLUMN_NAME);
                    predicates.add(builder.isFalse(deletedPath));
                }

                // 将所有条件用 and 联合起来
                if (predicates.size() > 0) {
                    return builder.and(predicates.toArray(new Predicate[predicates.size()]));
                }

                return builder.conjunction();
            }
        };
    }

    public static Criterion[] buildCriterions(final Collection<SearchFilter> filters, final Class entityClazz) {
        List<Criterion> criterionList = new ArrayList<>();
        for (SearchFilter filter : filters) {
            String expression = filter.fieldName;
            Object value = filter.value;
            switch (filter.operator) {
                case EQ:
                    criterionList.add(Restrictions.eq(expression, value));
                    break;
                case LIKE:
                    criterionList.add(Restrictions.like(expression, (String) value, MatchMode.ANYWHERE));
                    break;
                case GT:
                    criterionList.add(Restrictions.gt(expression, value));
                    break;
                case LT:
                    criterionList.add(Restrictions.lt(expression, value));
                    break;
                case GTE:
                    criterionList.add(Restrictions.ge(expression, value));
                    break;
                case LTE:
                    criterionList.add(Restrictions.le(expression, value));
                    break;
                case IN:
                    criterionList.add(Restrictions.in(expression, (Collection) value));
                    break;
                case ISNULL:
                    criterionList.add(Restrictions.isNull(expression));
                    break;
                case NOTNULL:
                    criterionList.add(Restrictions.isNotNull(expression));
                    break;
                case INARRAY:
                    try {
                        Class<?> type = entityClazz.getDeclaredField(expression).getType().getComponentType();
                        org.hibernate.type.Type dbType = StringType.INSTANCE;
                        if (Integer.class.isAssignableFrom(type)) {
                            value = StringUtils.stringToObject((String) value, Integer.class);
                            dbType = IntegerType.INSTANCE;
                        } else if (Long.class.isAssignableFrom(type)) {
                            value = StringUtils.stringToObject((String) value, Long.class);
                            dbType = LongType.INSTANCE;
                        }
                        criterionList.add(Restrictions.sqlRestriction("? =ANY (" + ImprovedNamingStrategy.INSTANCE.columnName(expression) + ")", value, dbType));
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
        if (LogicalDeletable.class.isAssignableFrom(entityClazz)) {//增加逻辑删除选项
            criterionList.add(Restrictions.eq(LogicalDeletable.LOGICAL_DELETED_COLUMN_NAME, false));
        }
        return criterionList.toArray(new Criterion[criterionList.size()]);
    }
}
