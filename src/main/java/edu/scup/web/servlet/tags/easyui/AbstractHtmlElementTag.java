package edu.scup.web.servlet.tags.easyui;

import org.springframework.web.servlet.tags.RequestContextAwareTag;
import org.springframework.web.servlet.tags.form.TagWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.Map;

public abstract class AbstractHtmlElementTag extends RequestContextAwareTag {
    public static final String KEY_JS = "js";
    public static final String FUNC_FORMATTER_PREFIX = "formatter_";
    public static final String DATA_DEFINE_PREFIX = "data_define_";
    public static final String VALUE_MAP_KEY = "GRID_MAP";

    protected String cssClass;
    protected String cssStyle;
    protected String title;
    protected String width;
    protected String height;

    /**
     * Provide a simple template method that calls {@link #createTagWriter()} and passes
     * the created {@link org.springframework.web.servlet.tags.form.TagWriter} to the {@link #writeTagContent(org.springframework.web.servlet.tags.form.TagWriter)} method.
     *
     * @return the value returned by {@link #writeTagContent(org.springframework.web.servlet.tags.form.TagWriter)}
     */
    @Override
    protected final int doStartTagInternal() throws Exception {
        return writeTagContent(createTagWriter());
    }

    protected void writeOptionalAttributes(TagWriter tagWriter) throws JspException {
        tagWriter.writeOptionalAttributeValue("style", cssStyle);
        tagWriter.writeOptionalAttributeValue("title", title);
        tagWriter.writeOptionalAttributeValue("id", id);
        tagWriter.writeOptionalAttributeValue("width", width);
        tagWriter.writeOptionalAttributeValue("class", cssClass);
    }

    protected TagWriter createTagWriter() {
        return new TagWriter(this.pageContext);
    }

    @SuppressWarnings("unchecked")
    protected StringBuilder getSnippets(String key) {
        TagSupport tag = getEDataGridTag();
        Map<String, StringBuilder> map = (Map<String, StringBuilder>) tag.getValue(VALUE_MAP_KEY);
        StringBuilder sb = map.get(key);
        if (sb == null) {
            sb = new StringBuilder();
            map.put(key, sb);
        }
        return sb;
    }

    protected EDataGridTag getEDataGridTag() {
        Tag tag = this;
        while (!EDataGridTag.class.equals(tag.getClass())) {
            tag = getParent();
        }
        return (EDataGridTag) tag;
    }

    /**
     * Subclasses should implement this method to perform tag content rendering.
     *
     * @return valid tag render instruction as per {@link javax.servlet.jsp.tagext.Tag#doStartTag()}.
     */
    protected abstract int writeTagContent(TagWriter tagWriter) throws JspException;

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public String getCssClass() {
        return cssClass;
    }

    public String getCssStyle() {
        return cssStyle;
    }

    public void setCssStyle(String cssStyle) {
        this.cssStyle = cssStyle;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getWidth() {
        return width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }
}
