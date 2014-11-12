package edu.scup.web.servlet.tags.easyui;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * 列表工具条标签
 */
public class DataGridToolBarTag extends TagSupport {
    private static final long serialVersionUID = -4700257309735164138L;
    protected String url;
    protected String title;
    private String exp;//判断链接是否显示的表达式
    private String funname;//自定义函数名称
    private String iconCls;//图标
    private String onclick;
    private String width;
    private String height;
    private String operationCode;//按钮的操作Code

    public int doStartTag() throws JspTagException {
        return EVAL_PAGE;
    }

    public int doEndTag() throws JspTagException {
        Tag t = findAncestorWithClass(this, DataGridTag.class);
        DataGridTag parent = (DataGridTag) t;
        parent.setToolbar(url, title, iconCls, exp, onclick, funname, operationCode, width, height);
        return EVAL_PAGE;
    }

    public void setFunname(String funname) {
        this.funname = funname;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIconCls(String iconCls) {
        this.iconCls = iconCls;
    }

    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }

    public void setOperationCode(String operationCode) {
        this.operationCode = operationCode;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }


}
