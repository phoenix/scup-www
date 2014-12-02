package edu.scup.web.servlet.tags.easyui;

import org.springframework.web.servlet.tags.form.TagWriter;

import javax.servlet.jsp.JspException;

/**
 * 列表工具条标签
 */
public class DataGridToolBarTag extends AbstractHtmlElementTag implements Cloneable {
    private static final long serialVersionUID = -4700257309735164138L;
    protected String url;
    private String exp;//判断链接是否显示的表达式
    private String funcName;//自定义函数名称
    private String onclick;
    private String operationCode;//按钮的操作Code

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    protected int writeTagContent(TagWriter tagWriter) throws JspException {
        getEDataGridTag().addToolbar(this.clone());
        return EVAL_PAGE;
    }

    public String getOnclick() {
        return onclick;
    }

    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }

    public void setOperationCode(String operationCode) {
        this.operationCode = operationCode;
    }

    @Override
    public DataGridToolBarTag clone() {
        try {
            return (DataGridToolBarTag) super.clone();
        } catch (CloneNotSupportedException ignored) {
            return null;
        }
    }
}
