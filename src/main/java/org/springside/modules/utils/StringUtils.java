package org.springside.modules.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtils extends org.apache.commons.lang3.StringUtils {

    public static Object stringToObject(String str, Class clz) {
        if (Integer.class.isAssignableFrom(clz)) {
            return isBlank(str) ? 0 : Integer.parseInt(str.trim());
        }
        if (Long.class.isAssignableFrom(clz)) {
            return isBlank(str) ? 0 : Long.parseLong(str.trim());
        }
        if (Boolean.class.isAssignableFrom(clz)) {
            return isBlank(str) ? 0 : Boolean.parseBoolean(str.trim());
        }
        if (Date.class.isAssignableFrom(clz)) {
            try {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str.trim());
            } catch (ParseException e) {
                throw Exceptions.unchecked(e);
            }
        }
        return str;
    }
}
