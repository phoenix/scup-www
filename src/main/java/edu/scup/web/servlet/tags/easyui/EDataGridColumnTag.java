package edu.scup.web.servlet.tags.easyui;

import com.alibaba.fastjson.JSON;
import edu.scup.web.sys.dao.CommonDao;
import edu.scup.web.sys.entity.SDict;
import edu.scup.web.sys.entity.SDictGroup;
import edu.scup.web.sys.service.SystemService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.tags.form.TagWriter;

import javax.servlet.jsp.JspException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EDataGridColumnTag extends AbstractHtmlElementTag implements Cloneable {
    private static final long serialVersionUID = 1403854988552009583L;

    private TagWriter tagWriter;
    @Autowired
    private SystemService systemService;
    @Autowired
    private CommonDao commonDao;

    private String field;
    private String dictionary;
    private String dictionaryUri;
    private boolean query;
    private String queryMode = "single";//字段查询模式：single单字段查询；group范围查询；dateGroup日期范围查询
    private String columnTitle;
    private String checkbox;
    private String editor;
    private String formatter;
    private boolean sortable;
    private String order;
    private boolean isImage;
    private String imageSize;
    private String validType;
    private boolean required;

    @Override
    protected int writeTagContent(TagWriter tagWriter) throws JspException {
        this.tagWriter = tagWriter;
        if (systemService == null) {
            WebApplicationContext wac = getRequestContext().getWebApplicationContext();
            AutowireCapableBeanFactory acbf = wac.getAutowireCapableBeanFactory();
            acbf.autowireBean(this);
        }

        EDataGridColumnTag columnTag = this.clone();

        boolean dicCombobox = StringUtils.equals("combobox", editor) && StringUtils.isNotBlank(dictionary);
        if (dicCombobox) {
            columnTag.setEditor("{type: 'combobox', options: { data: " + DATA_DEFINE_PREFIX + dictionary
                    + ",valueField: 'value',textField: 'display',required:" + required + "}}");
        } else if (StringUtils.equals("validatebox", editor) && StringUtils.isNotBlank(validType)) {
            columnTag.setEditor("{type: 'validatebox', options: {validType:'" + validType + "'}}");
        }

        if (StringUtils.isNotBlank(dictionary) && StringUtils.isBlank(formatter)) {
            columnTag.setFormatter("format_" + dictionary);
        } else if (isImage) {
            String style = "";
            if (StringUtils.isNotBlank(imageSize)) {
                String[] size = imageSize.split(",");
                style += "width=" + size[0] + " ";
                if (size.length > 1) {
                    style += "height=" + size[1] + " ";
                }
            }
            columnTag.setFormatter("function(value,rec,index){return '<image border=0 " + style + " src='+value+'/>';}");
        }
        if (StringUtils.isNotBlank(dictionary)) {
            StringBuilder js = getSnippets(KEY_JS);
            //增加formatter函数
            StringBuilder formatter = getSnippets(FUNC_FORMATTER_PREFIX + dictionary);
            //定义字典
            StringBuilder dataDefine = getSnippets(DATA_DEFINE_PREFIX + dictionary);
            boolean addFormatter = formatter.length() < 10;
            boolean defineData = dataDefine.length() < 10;
            if (!addFormatter && !defineData) {
                getEDataGridTag().addColumn(columnTag);
                return EVAL_PAGE;
            }
            if (addFormatter) {
                formatter.append("function format_").append(dictionary).append("(value,rec,index){\r\n");
            }
            Map<String, Object> kv = new HashMap<>();
            if (dictionary.contains(",")) {
                String[] dic = dictionary.split(",");
                String sql = "select " + dic[1] + " as field," + dic[2]
                        + " as text from " + dic[0];
                List<Map<String, Object>> list = commonDao.findForJdbc(sql);
                for (Map<String, Object> map : list) {
                    kv.putAll(map);
                }
                setFormatter(formatter, kv);
            } else {
                List<SDict> typeList = SDictGroup.getAllDicts().get(dictionary.toLowerCase());

                if (typeList != null && !typeList.isEmpty()) {
                    for (SDict type : typeList) {
                        kv.put(type.getDictCode(), type.getDictName());
                    }
                }
                setFormatter(formatter, kv);
            }
            //定义字典
            if (defineData) {
                defineComboboxData(dictionary, dataDefine, kv);
                js.append(dataDefine).append("\r\n");
            }
            formatter.append("\t").append("}");
            js.append(formatter).append("\n");
            if (dictionaryUri != null) {
                //增加remote字典
                StringBuilder dict = getSnippets(FUNC_FORMATTER_PREFIX + dictionaryUri);
                if (dict.length() < 10) {
                    dict.append("var _r_dict_").append(dictionary).append("=[];\n");
                    dict.append("$.post('").append(dictionaryUri).append("',function(data){\n")
                            .append("\t_.forEach(data,function(v,k){")
                            .append(DATA_DEFINE_PREFIX).append(dictionary).append(".push({'display':v.text,'value':v.value})})\n")
                            .append("})\n");
                    js.append(dict).append("\n");
                }
            }
        }
        getEDataGridTag().addColumn(columnTag);
        return EVAL_PAGE;
    }

    private void setFormatter(StringBuilder formatter, Map<String, Object> map) {
        formatter.append("\t").append("var r=_.find(").append(DATA_DEFINE_PREFIX).append(dictionary)
                .append(",function(d){return d.value==value});\n")
                .append("\treturn (r&&r.display) || value;\n");
    }

    private void defineComboboxData(String dictionaryName, StringBuilder formatter, Map<String, Object> map) {
        List<Map<String, Object>> data = new ArrayList<>();
        for (String key : map.keySet()) {
            Map<String, Object> m = new HashMap<>();
            m.put("value", key);
            m.put("display", map.get(key));
            data.add(m);
        }
        formatter.append("var ").append(DATA_DEFINE_PREFIX).append(dictionaryName).append("=").append(JSON.toJSONString(data)).append(";\r\n");
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getColumnTitle() {
        return columnTitle;
    }

    public void setColumnTitle(String columnTitle) {
        this.columnTitle = columnTitle;
    }

    public String getDictionary() {
        return dictionary;
    }

    public void setDictionary(String dictionary) {
        if (dictionary.startsWith("http") || dictionary.startsWith("/")) {
            this.dictionaryUri = dictionary;
        }
        this.dictionary = dictionary.replaceAll("[^a-zA-Z]", "_");
    }

    public void setQuery(boolean query) {
        this.query = query;
    }

    public boolean isQuery() {
        return query;
    }

    public String getCheckbox() {
        return checkbox;
    }

    public void setCheckbox(String checkbox) {
        this.checkbox = checkbox;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor.trim();
    }

    public String getFormatter() {
        return formatter;
    }

    public void setFormatter(String formatter) {
        this.formatter = formatter;
    }

    public boolean isSortable() {
        return sortable;
    }

    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public void setImage(boolean image) {
        this.isImage = image;
    }

    public void setImageSize(String imageSize) {
        this.imageSize = imageSize;
    }

    public String getValidType() {
        return validType;
    }

    public void setValidType(String validType) {
        this.validType = validType;
    }

    public String getQueryMode() {
        return queryMode;
    }

    public void setQueryMode(String queryMode) {
        this.queryMode = queryMode;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public EDataGridColumnTag clone() {
        try {
            return (EDataGridColumnTag) super.clone();
        } catch (CloneNotSupportedException ignored) {
            return null;
        }
    }
}
