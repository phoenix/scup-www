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

public class EDataGridTag extends AbstractHtmlElementTag {
    private static final long serialVersionUID = 3888335455632937097L;
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
    private List<DataGridToolBarTag> toolbars = new ArrayList<>();
    private static final Map<String, String> selfFuncMap = new HashMap<>();
    private static final Map<String, String> selfFuncCssMap = new HashMap<>();

    static {
        selfFuncMap.put("addRow", "新增");
        selfFuncMap.put("destroyRow", "删除");
        selfFuncMap.put("saveRow", "保存");
        selfFuncMap.put("cancelRow", "取消");

        selfFuncCssMap.put("addRow", "icon-add");
        selfFuncCssMap.put("destroyRow", "icon-remove");
        selfFuncCssMap.put("saveRow", "icon-save");
        selfFuncCssMap.put("cancelRow", "icon-undo");
    }

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
        toolbars.clear();

        tagWriter.startTag("div");
        tagWriter.writeAttribute("id", "_dlg");
        tagWriter.forceBlock();
        tagWriter.endTag();

        tagWriter.startTag("table");
        writeOptionalAttributes(tagWriter);
        tagWriter.writeAttribute("class", "easyui-datagrid");
        tagWriter.writeAttribute("url", url);
        tagWriter.writeAttribute("pagination", pagination);
        tagWriter.writeAttribute("loadMsg", "数据加载中...");
        tagWriter.writeAttribute("rownumbers", showRowNumbers);
        tagWriter.writeAttribute("pageSize", "20");
        tagWriter.writeAttribute("pageList", "[20,50,100]");
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
            tagWriter.appendValue(column.getColumnTitle());
            tagWriter.endTag();
            String dictionary = column.getDictionary();
            if ("single".equals(column.getQueryMode())) {
                if (dictionary != null) {
                    tagWriter.startTag("select");
                    tagWriter.writeAttribute("name", "search_EQ_" + column.getField());
                    tagWriter.startTag("option");
                    tagWriter.writeAttribute("value", "");
                    tagWriter.appendValue("---请选择---");
                    tagWriter.endTag();
                    List<SDict> typeList = SDictGroup.getAllDicts().get(dictionary);

                    if (typeList != null && !typeList.isEmpty()) {
                        for (SDict type : typeList) {
                            tagWriter.startTag("option");
                            tagWriter.writeAttribute("value", type.getDictCode());
                            tagWriter.appendValue(type.getDictName());
                            tagWriter.endTag();
                        }
                    }
                    tagWriter.endTag();
                } else {
                    tagWriter.startTag("input");
                    tagWriter.writeAttribute("name", "search_EQ_" + column.getField());
                    tagWriter.endTag();
                }
            } else if ("group".equals(column.getQueryMode()) || "dateGroup".equals(column.getQueryMode())) {
                tagWriter.startTag("input");
                tagWriter.writeAttribute("type", "text");
                tagWriter.writeAttribute("name", "search_GTE_" + column.getField());
                tagWriter.writeAttribute("id", column.getField() + "_begin");
                tagWriter.writeAttribute("style", "width: 94px");
                tagWriter.endTag();

                tagWriter.startTag("span");
                tagWriter.writeAttribute("style", "display:-moz-inline-box;display:inline-block;width: 8px;text-align:right;");
                tagWriter.forceBlock();
                tagWriter.appendValue("~");
                tagWriter.endTag();

                tagWriter.startTag("input");
                tagWriter.writeAttribute("type", "text");
                tagWriter.writeAttribute("name", "search_LTE_" + column.getField());
                tagWriter.writeAttribute("id", column.getField() + "_end");
                tagWriter.writeAttribute("style", "width: 94px");
                tagWriter.endTag();

                String contextPath = pageContext.getServletContext().getContextPath();
                if ("dateGroup".equals(column.getQueryMode())) {
                    getSnippets(KEY_JS).append("\n")
                            .append("$.datepicker.setDefaults($.extend({showMonthAfterYear: true,showButtonPanel: true,changeYear: true},$.datepicker.regional['zh-CN']));\n")
                            .append("$(\"#").append(column.getField()).append("_begin").append("\").datepicker({showOn: 'button', buttonImage: '")
                            .append(contextPath)
                            .append("/assets/images/calendar.png', buttonImageOnly: true,buttonText:'设置日期',showAnim:'show',duration:'normal'});\n")
                            .append("$(\"#").append(column.getField()).append("_end").append("\").datepicker({showOn: 'button', buttonImage: '")
                            .append(contextPath)
                            .append("/assets/images/calendar.png', buttonImageOnly: true,buttonText:'设置日期',showAnim:'show',duration:'normal'});\n");
                }
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
        for (DataGridToolBarTag toolBarTag : toolbars) {
            String funcName = toolBarTag.getFuncName();
            boolean selfOperate = selfFuncMap.containsKey(funcName);
            links.append("<a ");
            if (StringUtils.isNotBlank(toolBarTag.getId())) {
                links.append("id='").append(toolBarTag.getId()).append("' ");
            }
            links.append(" href=\"");
            if (funcName == null && toolBarTag.getUrl() != null) {
                links.append("javascript:window.open('").append(toolBarTag.getUrl()).append("')");
            } else {
                links.append("javascript:void(0)");
            }
            links.append("\" class=\"easyui-linkbutton\" iconCls=\"")
                    .append((selfOperate && toolBarTag.getCssClass() == null) ? selfFuncCssMap.get(funcName) : toolBarTag.getCssClass())
                    .append("\" plain=\"true\" onclick=\"");
            if (toolBarTag.getOnclick() != null) {
                links.append(toolBarTag.getOnclick()).append("\">").append(toolBarTag.getTitle()).append("</a>");
            } else if (selfOperate) {
                links.append("javascript:$('#").append(id).append("').edatagrid('").append(funcName).append("')\">")
                        .append(toolBarTag.getTitle() == null ? selfFuncMap.get(funcName) : toolBarTag.getTitle()).append("</a>");
            } else {
                links.append(funcName).append("('").append(toolBarTag.getTitle()).append("','").append(toolBarTag.getUrl()).append("')\">").append(toolBarTag.getTitle()).append("</a>");
            }
        }
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
            String rt = JSON.toJSONString(json);
            StringBuilder sb = new StringBuilder(rt.substring(0, rt.length() - 1));
            if (column.getFormatter() != null) {
                sb.append(",formatter:").append(column.getFormatter());
            }
            if (editor != null) {
                sb.append(",editor:").append(editor.startsWith("{") && editor.endsWith("}") ? editor : "\'" + editor + "'");
            }
            sb.append("}");
            columnsString.add(sb.toString());
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

    public void setIdField(String idField) {
        this.idField = idField;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void addColumn(EDataGridColumnTag column) {
        this.columns.add(column);
    }

    public void addToolbar(DataGridToolBarTag dataGridToolBarTag) {
        this.toolbars.add(dataGridToolBarTag);
    }
}
