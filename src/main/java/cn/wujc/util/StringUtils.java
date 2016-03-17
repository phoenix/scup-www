package cn.wujc.util;

import org.springside.modules.utils.Exceptions;

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
                if (str.length() == 10) {
                    return new SimpleDateFormat("yyyy-MM-dd").parse(str);
                }
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str);
            } catch (ParseException e) {
                throw Exceptions.unchecked(e);
            }
        }
        return str;
    }

    public static String defaultIfBlank(final String str, final String defaultStr) {
        return StringUtils.isBlank(str) ? defaultStr : str;
    }

    /**
     * 将字符串转换为驼峰形式,例如 is_boy转换为isBoy
     *
     * @param value              要转换的字符串
     * @param startWithLowerCase 转换后的字符串是否以小写开始
     * @return
     */
    public static String camelize(String value, boolean startWithLowerCase) {
        String[] strings = split(value.toLowerCase(), "_");
        for (int i = startWithLowerCase ? 1 : 0; i < strings.length; i++) {
            strings[i] = capitalize(strings[i]);
        }
        return StringUtils.join(strings);
    }

    /**
     * 根据单词数分割字符串 abcd,按照2个单词分割为[ab,cd],按照3个单词分割为[abc,d]
     *
     * @param wordsCount 每几个单词进行分割
     * @return
     */
    public static String[] split(String str, int wordsCount) {
        String regex = "(?<=\\G\\w{" + wordsCount + "})";
        return str.split(regex);
    }
}
