package edu.scup.web.servlet.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;
import java.io.IOException;

public class LinkToTag extends BodyTagSupport {
    private String action;
    private String confirm;
    private String method;
    private String cssClass;

    @Override
    public int doEndTag() throws JspException {
        String contextPath = pageContext.getServletContext().getContextPath();
        StringBuilder sb = new StringBuilder();
        if(!action.startsWith("/"))
            action = "/"+action;
        sb.append("<a href=\"").append(contextPath).append(action).append("\" ");
        if(cssClass != null)
            sb.append("class=\"").append(cssClass).append("\" ");
        if(confirm!=null)
            sb.append("data-confirm=\"").append(confirm).append("\" ");
        if(method!=null)
            sb.append("data-method=\"").append(method).append("\" rel=\"nofollow\"");
        sb.append(">");
        sb.append(bodyContent.getString());
        sb.append("</a>");

        try {
            pageContext.getOut().print(sb.toString());
        } catch (IOException e) {
            throw new JspException(e);
        }
        return Tag.EVAL_PAGE;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }
}
