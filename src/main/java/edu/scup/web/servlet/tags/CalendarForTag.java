package edu.scup.web.servlet.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

public class CalendarForTag extends TagSupport {
    private static final long serialVersionUID = 2072208417521196930L;

    @Override
    public int doEndTag() throws JspException {
        String contextPath = pageContext.getServletContext().getContextPath();
        StringBuilder sb = new StringBuilder();
        sb.append("<script>")
                .append("$(function() {\n")
                .append("\t$.datepicker.setDefaults($.extend({showMonthAfterYear: true,showButtonPanel: true,changeYear: true},$.datepicker.regional['zh-CN']));\n")
                .append("\t$(\"#").append(getId()).append("\").datepicker({showOn: 'button', buttonImage: '")
                .append(contextPath)
                .append("/assets/images/calendar.png', buttonImageOnly: true,buttonText:'设置日期',showAnim:'show',duration:'normal'});\n")
                .append("});\n")
                .append("</script>");

        try {
            pageContext.getOut().print(sb.toString());
        } catch (IOException e) {
            throw new JspException(e);
        }
        return Tag.EVAL_PAGE;
    }
}
