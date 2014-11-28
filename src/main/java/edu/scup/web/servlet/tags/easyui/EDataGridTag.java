package edu.scup.web.servlet.tags.easyui;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import edu.scup.web.servlet.tags.easyui.vo.DataGridColumn;
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

public class EDataGridTag extends AbstractHtmlElementTag {
    private TagWriter tagWriter;

    @Autowired
    private SystemService systemService;
    @Autowired
    private CommonDao commonDao;

    private String url;
    private String saveUrl;
    private String updateUrl;
    private String destroyUrl;
    private String toolbar;
    private String pagination = "true";
    private String showRowNumbers = "true";
    private String fitColumns;
    private String singleSelect;
    private String idField = "id";
    private String sortName;
    private String sortOrder;
    private List<EDataGridColumnTag> columns = new ArrayList<>();

    @Override
    protected int writeTagContent(TagWriter tagWriter) throws JspException {
        this.tagWriter = tagWriter;
        if (systemService == null) {
            WebApplicationContext wac = getRequestContext().getWebApplicationContext();
            AutowireCapableBeanFactory acbf = wac.getAutowireCapableBeanFactory();
            acbf.autowireBean(this);
        }
        //清空资源
        setValue(VALUE_MAP_KEY, new HashMap<String, StringBuilder>());
        columns.clear();

        tagWriter.startTag("table");
        writeOptionalAttributes(tagWriter);
        tagWriter.writeAttribute("class", "easyui-datagrid");
        tagWriter.writeAttribute("url", url);
        tagWriter.writeAttribute("pagination", pagination);
        tagWriter.writeAttribute("loadMsg", "数据加载中...");
        tagWriter.writeAttribute("rownumbers", showRowNumbers);
        tagWriter.writeAttribute("pageSize", "15");
        tagWriter.writeAttribute("pageList", "[15,30,50]");
        tagWriter.writeOptionalAttributeValue("fitColumns", fitColumns);
        tagWriter.writeOptionalAttributeValue("singleSelect", singleSelect);
        tagWriter.writeAttribute("toolbar", getToolbar());
        tagWriter.writeAttribute("idField", idField);
        tagWriter.writeOptionalAttributeValue("sortName", sortName);
        tagWriter.writeOptionalAttributeValue("sortOrder", sortOrder);

        tagWriter.forceBlock();

        tagWriter.startTag("thead");
        tagWriter.startTag("tr");
        tagWriter.forceBlock();
        return EVAL_PAGE;
    }

    @Override
    public int doEndTag() throws JspException {
        this.tagWriter.endTag();
        this.tagWriter.endTag();
        this.tagWriter.endTag();
        addToolbar();
        addScripts();
        return EVAL_PAGE;
    }

    private void addToolbar() throws JspException {
        tagWriter.startTag("div");
        tagWriter.writeAttribute("id", getToolbar().replace("#", ""));
        tagWriter.forceBlock();
        List<EDataGridColumnTag> queryColumns = findQueryableColumns();
        if (!queryColumns.isEmpty()) {
            tagWriter.startTag("div");
            tagWriter.writeAttribute("id", "searchColumns");
        }
        for (EDataGridColumnTag column : queryColumns) {
            tagWriter.startTag("div");
            tagWriter.writeAttribute("style", "display: inline-block;padding: 10px;");
            tagWriter.forceBlock();
            tagWriter.startTag("span");
            tagWriter.appendValue(column.getTitle());
            tagWriter.endTag();
            String dictionary = column.getDictionary();
            if (dictionary != null) {
                tagWriter.startTag("select");
                tagWriter.writeAttribute("name", "search_EQ_" + column.getField());
                tagWriter.startTag("option");
                tagWriter.writeAttribute("value", "");
                tagWriter.appendValue("---请选择---");
                tagWriter.endTag();
                List<SDict> typeList = SDictGroup.allTypes.get(dictionary);

                if (typeList != null && !typeList.isEmpty()) {
                    for (SDict type : typeList) {
                        tagWriter.startTag("option");
                        tagWriter.writeAttribute("value", type.getDictCode());
                        tagWriter.appendValue(type.getDictName());
                        tagWriter.endTag();
                    }
                }
                tagWriter.endTag();
            }
            tagWriter.endTag();
        }
        if (!queryColumns.isEmpty()) {
            tagWriter.endTag();
            tagWriter.startTag("div");
            tagWriter.writeAttribute("style", "float:right;");
            tagWriter.appendValue("<a href=\"javascript:void(0)\" class=\"easyui-linkbutton\" iconCls=\"icon-search\" onclick=\"_" + id + "search()\">查询</a>");
            tagWriter.appendValue("<a href=\"javascript:void(0)\" class=\"easyui-linkbutton\" iconCls=\"icon-reload\" onclick=\"_searchReset" + id + "()\">重置</a>");
            tagWriter.endTag();
        }
        tagWriter.startTag("div");
        StringBuilder links = new StringBuilder();
        links.append("<a href=\"javascript:void(0)\" class=\"easyui-linkbutton\" iconCls=\"icon-add\" plain=\"true\" onclick=\"javascript:$('#").append(id).append("').edatagrid('addRow')\">新增</a>");
        links.append("<a href=\"javascript:void(0)\" class=\"easyui-linkbutton\" iconCls=\"icon-remove\" plain=\"true\" onclick=\"javascript:$('#").append(id).append("').edatagrid('destroyRow')\">删除</a>");
        links.append("<a href=\"javascript:void(0)\" class=\"easyui-linkbutton\" iconCls=\"icon-save\" plain=\"true\" onclick=\"javascript:$('#").append(id).append("').edatagrid('saveRow')\">保存</a>");
        links.append("<a href=\"javascript:void(0)\" class=\"easyui-linkbutton\" iconCls=\"icon-undo\" plain=\"true\" onclick=\"javascript:$('#").append(id).append("').edatagrid('cancelRow')\">取消</a>");
        tagWriter.appendValue(links.toString());
        tagWriter.endTag();
        tagWriter.endTag();
    }

    private void addScripts() throws JspException {
        tagWriter.startTag("script");
        tagWriter.writeAttribute("type", "text/javascript");
        StringBuilder js = new StringBuilder();
        js.append(getSnippets(KEY_JS)).append("\r\n");
        js.append("function _").append(id).append("search(){\n\tvar queryParams=$('#").append(id)
                .append("').datagrid('options').queryParams;\n\t$('").append(getToolbar())
                .append("').find('*').each(function(){queryParams[$(this).attr('name')]=$(this).val();});\n\t$('#")
                .append(id).append("').datagrid({url:'").append(url).append("',pageNumber:1});\n}\n");
        js.append("function _searchReset").append(id).append("(){ $('").append(getToolbar()).append("').find(\":input\").val(\"\");_").append(id).append("search();}\n");
        js.append("$(function () {\n");
        js.append("$.extend($.fn.pagination.defaults,{beforePageText:'',afterPageText:'/{pages}',displayMsg:'{from}-{to}共{total}条',showPageList:true,showRefresh:true});\n");
        //begin edatagrid
        js.append("$('#").append(id).append("').edatagrid({\r\n\tsaveUrl: '").append(saveUrl)
                .append("',\r\n\tupdateUrl: '").append(updateUrl).append("',\r\n\tdestroyUrl:'").append(destroyUrl).append("',\n")
                .append("\tcolumns: [[\n");
        List<String> columnsString = new ArrayList<>();
        for (EDataGridColumnTag column : this.columns) {
            Map<String, Object> json = new HashMap<>();
            json.put("field", column.getField());
            json.put("title", column.getColumnTitle());
            json.put("sortable", column.isSortable());
            json.put("width", column.getWidth());
            json.put("style", column.getCssStyle());
            json.put("id", column.getId());
            json.put("class", column.getCssClass());
            json.put("checkbox", column.getCheckbox());
            json.put("order", column.getOrder());
            String editor = column.getEditor();
            if (editor != null) {
                if (editor.startsWith("{") && editor.endsWith("}")) {
                    json.put("editor", JSON.parse(editor));
                } else {
                    json.put("editor", editor);
                }
            }
            String rt = JSON.toJSONString(json);
            if(column.getFormatter() != null){
                rt = rt.substring(0,rt.length()-1)+",formatter:"+column.getFormatter()+"}";
            }
            columnsString.add(rt);
        }
        js.append(StringUtils.join(columnsString, ",\n"));
        js.append("\n]]\n")
                .append("});");

        js.append("\n});");
        tagWriter.appendValue("\r\n");
        tagWriter.appendValue(js.toString());
        tagWriter.endTag();
    }

    private List<EDataGridColumnTag> findQueryableColumns() {
        List<EDataGridColumnTag> rt = new ArrayList<>();
        for (EDataGridColumnTag column : columns) {
            if (column.isQuery()) {
                rt.add(column);
            }
        }
        return rt;
    }

    private String getToolbar() {
        return StringUtils.isBlank(toolbar) ? "#toolbar_" + id : toolbar;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setToolbar(String toolbar) {
        this.toolbar = toolbar;
    }

    public void setPagination(String pagination) {
        this.pagination = pagination;
    }

    public void setShowRowNumbers(String showRowNumbers) {
        this.showRowNumbers = showRowNumbers;
    }

    public void setFitColumns(String fitColumns) {
        this.fitColumns = fitColumns;
    }

    public void setSingleSelect(String singleSelect) {
        this.singleSelect = singleSelect;
    }

    public void setSaveUrl(String saveUrl) {
        this.saveUrl = saveUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

    public void setDestroyUrl(String destroyUrl) {
        this.destroyUrl = destroyUrl;
    }

    public void addColumn(EDataGridColumnTag column) {
        this.columns.add(column);
    }

    public void setIdField(String idField) {
        this.idField = idField;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}
