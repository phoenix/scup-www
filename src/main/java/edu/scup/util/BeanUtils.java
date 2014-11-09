package edu.scup.util;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

public class BeanUtils extends org.apache.commons.beanutils.BeanUtils {
    private static final Logger LOG = LoggerFactory.getLogger(BeanUtils.class);

    /**
     * 拷贝指定字段的属性
     *
     * @param dest          要写入的bean
     * @param orig          要读取的bean
     * @param propertyNames 要复制的字段
     */
    public static void copyProperties(Object dest, Object orig, String... propertyNames) {
        PropertyUtilsBean utilsBean = BeanUtilsBean.getInstance().getPropertyUtils();
        for (String propertyName : propertyNames) {
            try {
                utilsBean.setProperty(dest, propertyName, utilsBean.getProperty(orig, propertyName));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                LOG.warn("can't find property {} of {}", propertyName, orig);
            }
        }
    }
}
