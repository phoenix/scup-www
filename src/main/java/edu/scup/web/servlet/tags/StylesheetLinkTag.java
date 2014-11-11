package edu.scup.web.servlet.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.File;
import java.io.IOException;

public class StylesheetLinkTag extends TagSupport {
    private static final long serialVersionUID = 6806938175433514175L;
    private static String appRoot;
    private String files;

    @Override
    public int doEndTag() throws JspException {
        if (appRoot == null) {
            appRoot = pageContext.getServletContext().getRealPath("/");
        }
        StringBuilder sb = new StringBuilder();
        for (String file : files.split(",")) {
            file = file.trim();
            String filePath = (file.startsWith("/") ? "" : "/stylesheets/") + file + ".css";
            String lastModified = String.valueOf(new File(appRoot + filePath).lastModified() / 1000);
            if ("0".equals(lastModified) && !file.startsWith("/")) {
                filePath = "/assets/stylesheets/" + file + ".css";
                lastModified = String.valueOf(new File(appRoot + filePath).lastModified() / 1000);
            }
            if ("0".equals(lastModified) && !file.startsWith("/")) {
                filePath = "/assets/vendor/" + file + ".css";
                lastModified = String.valueOf(new File(appRoot + filePath).lastModified() / 1000);
            }
            sb.append("<link href=\"").append(pageContext.getServletContext().getContextPath()).append(filePath)
                    .append("?").append(lastModified).append("\" media=\"screen\" rel=\"stylesheet\" type=\"text/css\" />\n");
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
