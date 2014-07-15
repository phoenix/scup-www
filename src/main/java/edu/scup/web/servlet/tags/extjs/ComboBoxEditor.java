package edu.scup.web.servlet.tags.extjs;

import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Ext.form.field.ComboBox
 */
public class ComboBoxEditor extends TagSupport {
    /**
     * The {@link java.util.Collection}, {@link java.util.Map} or array of objects used to generate the inner
     * '<code>option</code>' tags.
     */
    private Object items;

    /**
     * The name of the property mapped to the '<code>value</code>' attribute
     * of the '<code>option</code>' tag.
     */
    private String itemValue;

    /**
     * The name of the property mapped to the inner text of the
     * '<code>option</code>' tag.
     */
    private String itemLabel;

    @Override
    public int doEndTag() throws JspException {
        StringBuilder sb = new StringBuilder("new Ext.form.field.ComboBox({")
                .append("forceSelection:true,queryMode: 'local',")
                .append("displayField: '").append(itemLabel).append("',")
                .append("store:[");

        if (items instanceof Map) {
            Map itemsMap = (Map) items;
            for (Object key : itemsMap.keySet()) {
                sb.append("['").append(itemsMap.get(key)).append("', '").append(key).append("'],");
            }
            sb.deleteCharAt(sb.length() - 1);

        } else if (items instanceof List) {
            for (Object item : (List) items) {
                try {
                    sb.append("['").append(BeanUtils.getProperty(item, itemValue))
                            .append("', '")
                            .append(BeanUtils.getProperty(item, itemLabel)).append("'],");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("]");
        sb.append("})");
        try {
            pageContext.getOut().print(sb.toString());
        } catch (IOException e) {
            throw new JspException(e);
        }
        return Tag.EVAL_PAGE;
    }

    public void setItems(Object items) {
        this.items = items;
    }

    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }

    public void setItemLabel(String itemLabel) {
        this.itemLabel = itemLabel;
    }
}
