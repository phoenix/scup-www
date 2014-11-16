package edu.scup.web.sys.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.servlet.http.HttpServletRequest;

public class EasyUIUtils {

    public static Pageable getPage(HttpServletRequest request) {
        int page = 0;
        try {
            page = Integer.parseInt(request.getParameter("page")) - 1;
        } catch (NumberFormatException ignored) {
        }
        int size = 20;
        try {
            size = Integer.parseInt(request.getParameter("rows"));
        } catch (NumberFormatException ignored) {
        }
        String sortField = request.getParameter("sort");
        if (StringUtils.isNotBlank(sortField)) {
            Sort sort;
            String direction = request.getParameter("order");
            if (StringUtils.isNotBlank(direction)) {
                sort = new Sort(Sort.Direction.fromString(direction), sortField);
            } else {
                sort = new Sort(sortField);
            }
            return new PageRequest(page, size, sort);
        }

        return new PageRequest(page, size);
    }
}
