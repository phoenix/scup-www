package org.springside.modules.persistence;

import org.apache.commons.lang3.StringUtils;
import org.springside.modules.web.Servlets;

import javax.servlet.ServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Operator增加IN,NOTNULL
 * 增加方法parseFromServletRequest,parseFromMap
 */
public class SearchFilter {

    public enum Operator {
        EQ, LIKE, GT, LT, GTE, LTE, IN, ISNULL, NOTNULL, INARRAY
    }

    public String fieldName;
    public Object value;
    public Operator operator;

    public SearchFilter(String fieldName, Operator operator, Object value) {
        this.fieldName = fieldName;
        this.value = value;
        this.operator = operator;
    }

    /**
     * searchParams中key的格式为OPERATOR_FIELDNAME
     */
    public static Map<String, SearchFilter> parse(Map<String, Object> searchParams) {
        Map<String, SearchFilter> filters = new HashMap<>();

        for (Entry<String, Object> entry : searchParams.entrySet()) {
            // 过滤掉空值
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String && StringUtils.isBlank((String) value)) {
                continue;
            }

            // 拆分operator与filedAttribute
            String[] names = StringUtils.split(key, "_");
            if (names.length != 2) {
                throw new IllegalArgumentException(key + " is not a valid search filter name");
            }
            String filedName = names[1];
            Operator operator = Operator.valueOf(names[0]);

            // 创建searchFilter
            SearchFilter filter = new SearchFilter(filedName, operator, value);
            filters.put(key, filter);
        }

        return filters;
    }

    public static Map<String, SearchFilter> parseFromServletRequest(ServletRequest request) {
        Map<String, Object> searchParams = Servlets.getParametersStartingWith(request, "search_");
        return parse(searchParams);
    }

    public static List<SearchFilter> getFiltersFromServletRequest(ServletRequest request) {
        return new ArrayList<>(parseFromServletRequest(request).values());
    }

    public static Map<String, SearchFilter> parseFromMap(Map<String, Object> query) {
        Map<String, Object> searchParams = new HashMap<>();
        for (String key : query.keySet()) {
            if (StringUtils.startsWith(key, "search_")) {
                String unPrefixed = key.substring("search_".length());
                searchParams.put(unPrefixed, query.get(key));
            }
        }
        return parse(searchParams);
    }

    public static List<SearchFilter> getFiltersFromMap(Map<String, Object> query) {
        return new ArrayList<>(parseFromMap(query).values());
    }
}
