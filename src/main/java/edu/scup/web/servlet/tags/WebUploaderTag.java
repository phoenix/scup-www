package edu.scup.web.servlet.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

public class WebUploaderTag extends TagSupport {
    private static final long serialVersionUID = -367837073592524212L;
    private String idSuffix;
    private String cssClass;

    @Override
    public int doEndTag() throws JspException {
        StringBuilder ele = new StringBuilder();
        ele.append("<div id=\"uploader_").append(idSuffix).append("\" class=\"uploader ").append(cssClass).append("\">\n");
        ele.append("\t<div class=\"queueList\">\n");
        ele.append("\t\t<div id=\"dndArea_").append(idSuffix).append("\" class=\"placeholder\">\n");
        ele.append("\t\t\t<div id=\"filePicker_").append(idSuffix).append("\"></div>\n");
        ele.append("\t\t</div>\n");
        ele.append("\t</div>\n");
        ele.append("\t<div class=\"statusBar\" style=\"display:none;\">\n");
        ele.append("\t\t<div class=\"progress\">\n");
        ele.append("\t\t\t<span class=\"text\">0%</span>\n");
        ele.append("\t\t\t<span class=\"percentage\"></span>\n");
        ele.append("\t\t</div>\n");
        ele.append("\t\t<div class=\"info\"></div>\n");
        ele.append("\t\t<div class=\"btns\">\n");
        ele.append("\t\t\t<div id=\"filePicker2_").append(idSuffix).append("\" class=\"filePicker2\"></div>\n");
        ele.append("\t\t\t<div class=\"uploadBtn\">开始上传</div>\n");
        ele.append("\t\t</div>\n");
        ele.append("\t</div>\n");
        ele.append("</div>\n");

        try {
            pageContext.getOut().print(ele.toString());
        } catch (IOException e) {
            throw new JspException(e);
        }

        return EVAL_PAGE;
    }

    public String getIdSuffix() {
        return idSuffix;
    }

    public void setIdSuffix(String idSuffix) {
        this.idSuffix = idSuffix;
    }

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }
}
