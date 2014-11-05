package edu.scup.web.servlet.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.File;
import java.io.IOException;

public class JavascriptLinkTag extends TagSupport {
    private static final long serialVersionUID = -6514243096501357267L;
    static final String appRoot = new File(JavascriptLinkTag.class.getResource("/").getFile()).getParentFile().getParent();
    private String files;

    @Override
    public int doEndTag() throws JspException {
        StringBuilder sb = new StringBuilder();
        for (String file : files.split(",")) {
            file = file.trim();
            String filePath = (file.startsWith("/") ? "" : "/javascripts/") + file + ".js";
            String lastModified = String.valueOf(new File(appRoot + filePath).lastModified() / 1000);
            if ("0".equals(lastModified) && !file.startsWith("/")) {
                filePath = "/assets/vendor/" + file + ".js";
                lastModified = String.valueOf(new File(appRoot + filePath).lastModified() / 1000);
            }
            sb.append("<script src=\"").append(pageContext.getServletContext().getContextPath()).append(filePath)
                    .append("?").append(lastModified).append("\" type=\"text/javascript\"></script>\n");
        }

        try {
            pageContext.getOut().print(sb.toString());
        } catch (IOException e) {
            throw new JspException(e);
        }
        return Tag.EVAL_PAGE;
    }

    public void setFiles(String files) {
        this.files = files;
    }
}
