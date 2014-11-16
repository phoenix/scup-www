package edu.scup.web.servlet.tags.easyui;

import com.alibaba.fastjson.JSON;
import edu.scup.web.servlet.tags.easyui.vo.DataGridColumn;
import edu.scup.web.sys.dao.CommonDao;
import edu.scup.web.sys.entity.SType;
import edu.scup.web.sys.entity.STypeGroup;
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

public class EDataGridColumnTag extends AbstractHtmlElementTag {
    private TagWriter tagWriter;
    @Autowired
    private SystemService systemService;
    @Autowired
    private CommonDao commonDao;

    private String field;
    private String dictionary;
    private String dictionaryUri;
    private boolean query;
    private String columnTitle;
    private String checkbox;
    private String editor;
    private String formatter;
    private boolean sortable;
    private String order;

    @Override
    protected int writeTagContent(TagWriter tagWriter) throws JspException {
        this.tagWriter = tagWriter;
        if (systemService == null) {
            WebApplicationContext wac = getRequestContext().getWebApplicationContext();
            AutowireCapableBeanFactory acbf = wac.getAutowireCapableBeanFactory();
            acbf.autowireBean(this);
        }

        DataGridColumn column = new DataGridColumn();
        column.setQuery(query);
        column.setField(field);
        column.setDictionary(dictionary);
        column.setTitle(columnTitle);
        getEDataGridTag().addColumn(column);

        tagWriter.startTag("th");
        writeOptionalAttributes(tagWriter);
        tagWriter.writeAttribute("field", field);
        tagWriter.writeOptionalAttributeValue("checkbox", checkbox);
        tagWriter.writeOptionalAttributeValue("formatter", formatter);
        tagWriter.writeOptionalAttributeValue("sortable", String.valueOf(sortable));
        tagWriter.writeOptionalAttributeValue("order", order);
        boolean dicCombobox = StringUtils.equals("combobox", editor) && StringUtils.isNotBlank(dictionary);
        if (dicCombobox) {
            tagWriter.writeAttribute("editor", "{type: 'combobox', options: { data: " + DATA_DEFINE_PREFIX + dictionary
                    + ",valueField: 'value',textField: 'display',required:true}}");
        } else {
            tagWriter.writeOptionalAttributeValue("editor", editor);
        }

        if (StringUtils.isNotBlank(dictionary) && StringUtils.isBlank(formatter)) {
            tagWriter.writeAttribute("formatter", "format_" + dictionary);
        }
        tagWriter.appendValue(columnTitle);
        tagWriter.endTag();
        if (StringUtils.isNotBlank(dictionary)) {
            StringBuilder js = getSnippets(KEY_JS);
            //增加formatter函数
            StringBuilder formatter = getSnippets(FUNC_FORMATTER_PREFIX + dictionary);
            //定义字典
            StringBuilder dataDefine = getSnippets(DATA_DEFINE_PREFIX + dictionary);
            boolean addFormatter = formatter.length() < 10;
            boolean defineData = dataDefine.length() < 10;
            if (!addFormatter && !defineData) {
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
                List<SType> typeList = STypeGroup.allTypes.get(dictionary.toLowerCase());

                if (typeList != null && !typeList.isEmpty()) {
                    for (SType type : typeList) {
                        kv.put(type.getTypeCode(), type.getTypeName());
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
                            .append(DATA_DEFINE_PREFIX).append(dictionary).append(".push({'display':k,'value':v})})\n")
                            .append("})\n");
                    js.append(dict).append("\n");
                }
            }
        }
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

    public void setField(String field) {
        this.field = field;
    }

    public void setColumnTitle(String columnTitle) {
        this.columnTitle = columnTitle;
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

    public void setCheckbox(String checkbox) {
        this.checkbox = checkbox;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public void setFormatter(String formatter) {
        this.formatter = formatter;
    }

    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
