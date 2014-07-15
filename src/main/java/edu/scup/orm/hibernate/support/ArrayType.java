package edu.scup.orm.hibernate.support;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.AbstractStandardBasicType;
import org.hibernate.type.descriptor.JdbcTypeNameMapper;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.*;
import java.util.Properties;

public class ArrayType implements UserType, ParameterizedType {
    private static final Logger logger = LoggerFactory.getLogger(ArrayType.class);

    protected static final int SQLTYPE = java.sql.Types.ARRAY;
    private AbstractStandardBasicType elementType;

    @Override
    public Object nullSafeGet(final ResultSet rs, final String[] names, final SessionImplementor sessionImplementor, final Object owner) throws HibernateException, SQLException {
        Array array = rs.getArray(names[0]);
        return array == null ? null : array.getArray();
    }

    @Override
    public void nullSafeSet(final PreparedStatement statement, final Object object, final int i, final SessionImplementor sessionImplementor) throws HibernateException, SQLException {
        Connection connection = statement.getConnection();
        String typeName = JdbcTypeNameMapper.getTypeName(elementType.getSqlTypeDescriptor().getSqlType()).toLowerCase();
        Array array = connection.createArrayOf(typeName, object == null ? new Object[0] : (Object[]) object);
        statement.setArray(i, array);
    }

    @Override
    public Object assemble(final Serializable cached, final Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object deepCopy(final Object o) throws HibernateException {
        return o == null ? null : ((Object[]) o).clone();
    }

    @Override
    public Serializable disassemble(final Object o) throws HibernateException {
        return (Serializable) o;
    }

    @Override
    public boolean equals(final Object x, final Object y) throws HibernateException {
        return x == null ? y == null : x.equals(y);
    }

    @Override
    public int hashCode(final Object o) throws HibernateException {
        return o == null ? 0 : o.hashCode();
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Object replace(final Object original, final Object target, final Object owner) throws HibernateException {
        return original;
    }

    @Override
    public Class returnedClass() {
        return java.lang.reflect.Array.newInstance(elementType.getReturnedClass(), 0).getClass();
    }

    @Override
    public int[] sqlTypes() {
        return new int[]{SQLTYPE};
    }

    @Override
    public void setParameterValues(Properties parameters) {
        try {
            this.elementType = (AbstractStandardBasicType) Class.forName(parameters.getProperty("elementType")).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            logger.error("", e);
        }
    }
}
