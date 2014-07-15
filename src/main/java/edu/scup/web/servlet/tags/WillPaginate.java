package edu.scup.web.servlet.tags;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.Map;

public class WillPaginate extends TagSupport {
    private static final long serialVersionUID = 8628384333451451696L;
    private Page page;

    @Override
    public int doEndTag() throws JspException {
        String contextPath = pageContext.getServletContext().getContextPath();
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        StringBuilder paramsSb = new StringBuilder();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            for (String value : entry.getValue()) {
                if (StringUtils.isNotEmpty(value) && !"page".equalsIgnoreCase(entry.getKey())) {
                    paramsSb.append("&").append(entry.getKey()).append("=").append(value);
                }
            }
        }
        String params = paramsSb.toString();

        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"pagination\">");
        if (page.isFirstPage())
            sb.append("<span class=\"previous_page disabled\">« 前一页</span>");
        else
            sb.append("<a class=\"prev_page\" rel=\"prev\" href=\"?page=").append(page.getNumber() - 1).append(params).append("\">« 前一页</a> ");

        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            pathInfo = request.getAttribute("javax.servlet.forward.request_uri").toString();
        }
        int i = 1;
        int defaultSecondDotPos = 10;
        int firstDotPos = Math.min(3, page.getNumber() - 2), secondDotPos = Math.max(defaultSecondDotPos, page.getNumber() + 4);
        while (i <= secondDotPos && i < page.getTotalPages()) {
            if (i != 1 && page.getNumber() > defaultSecondDotPos - 3 && i == firstDotPos && i < page.getNumber()) {
                sb.append("<span class=\"gap\">…</span>");
                i = Math.max(page.getNumber() - 4, firstDotPos + 1);
            } else
                appendPageLink(sb, page, i, contextPath, pathInfo, params);
            i++;
        }
        if (i < page.getTotalPages()) {
            sb.append("<span class=\"gap\">…</span>");
        }
        appendPageLink(sb, page, page.getTotalPages(), contextPath, pathInfo, params);

        if (page.isLastPage())
            sb.append("<span class=\"next_page disabled\">后一页 »</span>");
        else
            sb.append("<a class=\"next_page\" rel=\"next\" href=\"?page=").append(page.getNumber() + 1).append(params).append("\">后一页 »</a>");

        sb.append("(共 ").append(page.getTotalElements()).append(" 条记录)");
        sb.append("</div>");
        try {
            pageContext.getOut().print(sb.toString());
        } catch (IOException e) {
            throw new JspException(e);
        }
        return Tag.EVAL_PAGE;
    }

    private StringBuilder appendPageLink(StringBuilder sb, Page page, int currentDisplayPage, String contextPath, String pathInfo, String params) {
        if (page.getNumber() + 1 == currentDisplayPage)
            sb.append("<em>").append(currentDisplayPage).append("</em> \n");
        else
            sb.append("<a href=\"").append(contextPath).append(pathInfo).append("?page=").append(currentDisplayPage - 1).append(params).append("\">").append(currentDisplayPage).append("</a> \n");
        return sb;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}
