package edu.scup.util;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;

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

    /**
     * Copy the property values of the given source bean into the given target bean,
     * ignoring the given "ignoreProperties".
     * <p>Note: The source and target classes do not have to match or even be derived
     * from each other, as long as the properties match. Any bean properties that the
     * source bean exposes but the target bean does not will silently be ignored.
     * <p>This is just a convenience method. For more complex transfer needs,
     * consider using a full BeanWrapper.
     *
     * @param source           the source bean
     * @param target           the target bean
     * @param ignoreProperties array of property names to ignore
     * @throws BeansException if the copying failed
     * @see BeanWrapper
     */
    public static void copyPropertiesExclude(Object source, Object target, String... ignoreProperties) throws BeansException {
        org.springframework.beans.BeanUtils.copyProperties(source, target, ignoreProperties);
    }
}
