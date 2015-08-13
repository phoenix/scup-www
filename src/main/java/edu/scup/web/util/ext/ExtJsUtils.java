package edu.scup.web.util.ext;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springside.modules.persistence.SearchFilter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ExtJsUtils {
    private static final Logger logger = LoggerFactory.getLogger(ExtJsUtils.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    public static Pageable getPage(HttpServletRequest request) {
        int page = 0;
        try {
            page = Integer.parseInt(request.getParameter("page")) - 1;
        } catch (NumberFormatException ignored) {
        }
        int size = 20;
        try {
            size = Integer.parseInt(request.getParameter("limit"));
        } catch (NumberFormatException ignored) {
        }

        return new PageRequest(page, size);
    }

    public static PageRequest getPage(HttpServletRequest request, Sort.Direction direction, String... properties) {
        Pageable pageable = getPage(request);

        return new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), direction, properties);
    }

    public static List<SearchFilter> getFilters(HttpServletRequest request) {
        String filterJson = request.getParameter("filter");
        List<SearchFilter> filters = new ArrayList<>();
        if (filterJson == null) {
            return filters;
        }
        List<Map> list = null;
        try {
            list = mapper.readValue(filterJson, mapper.getTypeFactory().constructCollectionType(List.class, Map.class));
        } catch (IOException e) {
            logger.error("", e);
        }
        for (Map map : list) {
            try {
                SearchFilter.Operator operator = SearchFilter.Operator.valueOf(map.get("operator").toString().toUpperCase());
                SearchFilter filter = new SearchFilter(map.get("property").toString(), operator, map.get("value"));
                filters.add(filter);
            } catch (IllegalArgumentException e) {
                logger.error("", e);
            }
        }
        return filters;
    }
}
