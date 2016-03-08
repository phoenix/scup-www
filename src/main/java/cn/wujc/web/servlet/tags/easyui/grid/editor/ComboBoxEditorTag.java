package cn.wujc.web.servlet.tags.easyui.grid.editor;

import com.fasterxml.jackson.databind.ObjectMapper;
import cn.wujc.web.servlet.tags.easyui.form.ComboBoxTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;

public class ComboBoxEditorTag extends BodyTagSupport {
    private static final Logger LOG = LoggerFactory.getLogger(ComboBoxEditorTag.class);
    private ComboBoxTag comboBox;

    public ComboBoxTag getComboBox() {
        return comboBox;
    }

    public void setComboBox(ComboBoxTag comboBox) {
        this.comboBox = comboBox;
    }

    @Override
    public int doEndTag() throws JspException {
        try {
            pageContext.getOut().print(new ObjectMapper().writeValueAsString(comboBox));
        } catch (IOException e) {
            LOG.error("", e);
        }
        return EVAL_PAGE;
    }
}
