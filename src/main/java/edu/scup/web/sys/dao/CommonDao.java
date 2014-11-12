package edu.scup.web.sys.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;

@Repository
public class CommonDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private EntityManager entityManager;

    public List<Map<String, Object>> findForJdbc(String sql, Object... objs) {
        return this.jdbcTemplate.queryForList(sql, objs);
    }

    /**
     * 执行JPQL语句操作更新
     *
     * @param jpql
     * @return
     */
    public Integer executeJpql(String jpql) {
        return entityManager.createQuery(jpql).executeUpdate();
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
