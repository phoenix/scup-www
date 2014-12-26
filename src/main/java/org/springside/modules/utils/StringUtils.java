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
            if ("0".equals(str)) {
                return Boolean.FALSE;
            }
            if ("1".equals(str)) {
                return Boolean.TRUE;
            }
            return isBlank(str) ? null : Boolean.parseBoolean(str.trim());
        }
        if (Date.class.isAssignableFrom(clz)) {
            try {
                str = str.trim();
                if(str.length() == 10){
                    return new SimpleDateFormat("yyyy-MM-dd").parse(str);
                }
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str);
            } catch (ParseException e) {
                throw Exceptions.unchecked(e);
            }
        }
        return str;
    }
}
