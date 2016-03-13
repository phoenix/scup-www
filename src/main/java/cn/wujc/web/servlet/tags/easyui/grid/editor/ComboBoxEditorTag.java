package cn.wujc.web.servlet.tags.easyui.grid.editor;

import cn.wujc.web.servlet.tags.easyui.grid.EDataGridColumnTag;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ComboBoxEditorTag extends BodyTagSupport {
    private static final Logger LOG = LoggerFactory.getLogger(ComboBoxEditorTag.class);
    private static final ObjectMapper MAPPER = new ObjectMapper().configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false)
            .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

    @Override
    public int doAfterBody() throws JspException {
        EDataGridColumnTag parent = (EDataGridColumnTag) findAncestorWithClass(this, EDataGridColumnTag.class);
        if (parent == null) {
            throw new JspException("ComboBoxEditorTag must in EDataGridColumnTag");
        }
        String comboBox = bodyContent.getString();
        Map<String, Object> editor = new HashMap<>();
        editor.put("type", "combobox");
        try {
            editor.put("options", MAPPER.readTree(comboBox));
        } catch (IOException e) {
            throw new JspException(e);
        }
        try {
            parent.setEditor(MAPPER.writeValueAsString(editor));
        } catch (JsonProcessingException e) {
            LOG.error("", e);
        }
        return SKIP_BODY;
    }
}
