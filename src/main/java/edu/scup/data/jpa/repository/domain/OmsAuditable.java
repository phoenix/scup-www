package edu.scup.data.jpa.repository.domain;

import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.Date;

/**
 * spring-data自带的Auditable实现有一些不方便的地方:
 * 1、依赖joda-time
 * 2、要配置orm.xml和application.xml
 * 3、修改Entity的时候,如果把页面传过来的数据修改后直接入库,会丢失创建信息
 * 因此重复造一个轮子
 * @param <ID>
 */
public interface OmsAuditable<ID extends Serializable> extends Persistable<ID> {

    /**
     * Returns the user who created this entity.
     *
     * @return the createdBy
     */
    String getCreatedBy();

    /**
     * Sets the user who created this entity.
     *
     * @param createdBy the creating entity to set
     */
    void setCreatedBy(final String createdBy);

    /**
     * Returns the creation date of the entity.
     *
     * @return the createdDate
     */
    Date getCreatedDate();

    /**
     * Sets the creation date of the entity.
     *
     * @param creationDate the creation date to set
     */
    void setCreatedDate(final Date creationDate);

    /**
     * Returns the user who modified the entity lastly.
     *
     * @return the lastModifiedBy
     */
    String getLastModifiedBy();

    /**
     * Sets the user who modified the entity lastly.
     *
     * @param lastModifiedBy the last modifying entity to set
     */
    void setLastModifiedBy(final String lastModifiedBy);

    /**
     * Returns the date of the last modification.
     *
     * @return the lastModifiedDate
     */
    Date getLastModifiedDate();

    /**
     * Sets the date of the last modification.
     *
     * @param lastModifiedDate the date of the last modification to set
     */
    void setLastModifiedDate(final Date lastModifiedDate);
}
