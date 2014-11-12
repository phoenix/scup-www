package edu.scup.web.sys.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

        return new PageRequest(page, size);
    }
}
