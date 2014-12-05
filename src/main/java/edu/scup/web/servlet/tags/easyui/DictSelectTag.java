package edu.scup.web.servlet.tags.easyui;

import edu.scup.web.sys.entity.SDict;
import edu.scup.web.sys.entity.SDictGroup;
import org.springframework.web.servlet.tags.form.AbstractHtmlInputElementTag;
import org.springframework.web.servlet.tags.form.TagWriter;

import javax.servlet.jsp.JspException;
import java.util.List;

public class DictSelectTag extends AbstractHtmlInputElementTag {
    private static final long serialVersionUID = -8169719589862313362L;

    private String dictionary;
    private String type = "select";// 控件类型select|radio|checkbox

    @Override
    protected int writeTagContent(TagWriter tagWriter) throws JspException {
        Object boundValue = getBoundValue();
        if ("select".equals(type)) {
            tagWriter.startTag("select");
            tagWriter.writeAttribute("name", getPath());
            writeDefaultAttributes(tagWriter);

            List<SDict> typeList = SDictGroup.getAllDicts().get(dictionary.toLowerCase());
            tagWriter.startTag("option");
            tagWriter.writeAttribute("value", "");
            tagWriter.forceBlock();
            tagWriter.appendValue("请选择");
            tagWriter.endTag();
            if (typeList != null && !typeList.isEmpty()) {
                for (SDict type : typeList) {
                    tagWriter.startTag("option");
                    tagWriter.writeAttribute("value", type.getDictCode());
                    if (type.getDictCode().equals(boundValue)) {
                        tagWriter.writeAttribute("selected", "");
                    }
                    tagWriter.forceBlock();
                    tagWriter.appendValue(type.getDictName());
                    tagWriter.endTag();
                }
            }
            tagWriter.endTag();
        } else if ("radio".equals(type)) {
            List<SDict> typeList = SDictGroup.getAllDicts().get(dictionary.toLowerCase());
            if (typeList != null && !typeList.isEmpty()) {
                for (SDict type : typeList) {
                    tagWriter.startTag("label");
                    tagWriter.writeAttribute("class", "radio-inline");
                    tagWriter.startTag("input");
                    tagWriter.writeAttribute("type", "radio");
                    tagWriter.writeAttribute("name", getPath());
                    tagWriter.writeAttribute("value", type.getDictCode());
                    if (type.getDictCode().equals(boundValue)) {
                        tagWriter.writeAttribute("checked", "");
                    }
                    tagWriter.forceBlock();
                    tagWriter.appendValue(type.getDictName());
                    tagWriter.endTag();
                    tagWriter.endTag();
                }
            }
        }

        return SKIP_BODY;
    }

    public String getDictionary() {
        return dictionary;
    }

    public void setDictionary(String dictionary) {
        this.dictionary = dictionary;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
