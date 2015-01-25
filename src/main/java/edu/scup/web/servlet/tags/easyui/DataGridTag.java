package edu.scup.web.servlet.tags.easyui;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.scup.web.servlet.tags.easyui.vo.ColumnValue;
import edu.scup.web.servlet.tags.easyui.vo.DataGridColumn;
import edu.scup.web.servlet.tags.easyui.vo.DataGridUrl;
import edu.scup.web.servlet.tags.easyui.vo.OptTypeDirection;
import edu.scup.web.sys.Globals;
import edu.scup.web.sys.dao.CommonDao;
import edu.scup.web.sys.entity.SDict;
import edu.scup.web.sys.entity.SDictGroup;
import edu.scup.web.sys.service.SystemService;
import edu.scup.web.sys.util.ContextHolderUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

/**
 * 类描述：DATAGRID标签处理类
 */
public class DataGridTag extends RequestContextAwareTag {
    private static final long serialVersionUID = -8935249060219993990L;
    private static final Logger LOG = LoggerFactory.getLogger(DataGridTag.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    protected String fields = "";// 显示字段
    protected String searchFields = "";// 查询字段  Author:qiulu  Date:20130618 for：添加对区间查询的支持
    protected String name;// 表格标示
    protected String title;// 表格标示
    protected String idField = "id";// 主键字段
    protected boolean treegrid = false;// 是否是树形列表
    protected List<DataGridUrl> urlList = new ArrayList<>();// 列表操作显示
    protected List<DataGridUrl> toolBarList = new ArrayList<>();// 工具条列表
    protected List<DataGridColumn> columnList = new ArrayList<>();// 列表操作显示
    protected List<ColumnValue> columnValueList = new ArrayList<>();// 值替换集合
    protected List<ColumnValue> columnStyleList = new ArrayList<>();// 颜色替换集合
    public Map<String, Object> map;// 封装查询条件
    private String actionUrl;// 分页提交路径
    public int allCount;
    public int curPageNo;
    public int pageSize = 10;
    public boolean pagination = true;// 是否显示分页
    private String width;
    private String height;
    private boolean checkbox = false;// 是否显示复选框
    private boolean showPageList = true;// 定义是否显示页面列表
    private boolean openFirstNode = false;//是不是展开第一个节点
    private boolean fit = false;// 是否允许表格自动缩放，以适应父容器
    private boolean fitColumns = true;// 当为true时，自动展开/合同列的大小，以适应的宽度，防止横向滚动.
    private String sortName;//定义的列进行排序
    private String sortOrder = "asc";//定义列的排序顺序，只能是"递增"或"降序".
    private boolean showRefresh = true;// 定义是否显示刷新按钮
    private boolean showText = true;// 定义是否显示刷新按钮
    private String style = "easyui";// 列表样式easyui,datatables
    private String onLoadSuccess;// 数据加载完成调用方法
    private String onClick;// 单击事件调用方法
    private String onDblClick;// 双击事件调用方法
    private String queryMode = "single";//查询模式
    private String entityName;//对应的实体对象
    private String rowStyler;//rowStyler函数
    private String extendParams;//扩展参数,easyui有的,但是jeecg没有的参数进行扩展
    private boolean autoLoadData = true; // 列表是否自动加载数据
    //private boolean frozenColumn=false; // 是否是冰冻列    默认不是
    //json转换中的系统保留字
    protected static Map<String, String> syscode = new HashMap<>();

    static {
        syscode.put("class", "clazz");
    }

    @Autowired
    private SystemService systemService;
    @Autowired
    private CommonDao commonDao;

    @Override
    protected int doStartTagInternal() throws Exception {
        if (systemService == null) {
            WebApplicationContext wac = getRequestContext().getWebApplicationContext();
            AutowireCapableBeanFactory acbf = wac.getAutowireCapableBeanFactory();
            acbf.autowireBean(this);
        }
        // 清空资源
        urlList.clear();
        toolBarList.clear();
        columnValueList.clear();
        columnStyleList.clear();
        columnList.clear();
        fields = "";
        searchFields = "";
        return EVAL_PAGE;
    }

    public void setOnLoadSuccess(String onLoadSuccess) {
        this.onLoadSuccess = onLoadSuccess;
    }

    public void setOnClick(String onClick) {
        this.onClick = onClick;
    }

    public void setOnDblClick(String onDblClick) {
        this.onDblClick = onDblClick;
    }

    public void setShowText(boolean showText) {
        this.showText = showText;
    }

    public void setPagination(boolean pagination) {
        this.pagination = pagination;
    }

    public void setCheckbox(boolean checkbox) {
        this.checkbox = checkbox;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setTreegrid(boolean treegrid) {
        this.treegrid = treegrid;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setIdField(String idField) {
        this.idField = idField;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFit(boolean fit) {
        this.fit = fit;
    }

    public void setShowPageList(boolean showPageList) {
        this.showPageList = showPageList;
    }

    public void setShowRefresh(boolean showRefresh) {
        this.showRefresh = showRefresh;
    }

    /**
     * 设置询问操作URL
     */
    public void setConfUrl(String url, String title, String message, String exp, String operationCode) {
        DataGridUrl dataGridUrl = new DataGridUrl();
        dataGridUrl.setTitle(title);
        dataGridUrl.setUrl(url);
        dataGridUrl.setType(OptTypeDirection.Confirm);
        dataGridUrl.setMessage(message);
        dataGridUrl.setExp(exp);
        installOperationCode(dataGridUrl, operationCode, urlList);
    }

    /**
     * 设置删除操作URL
     */
    public void setDelUrl(String url, String title, String message, String exp, String funname, String operationCode) {
        DataGridUrl dataGridUrl = new DataGridUrl();
        dataGridUrl.setTitle(title);
        dataGridUrl.setUrl(url);
        dataGridUrl.setType(OptTypeDirection.Del);
        dataGridUrl.setMessage(message);
        dataGridUrl.setExp(exp);
        dataGridUrl.setFunname(funname);
        installOperationCode(dataGridUrl, operationCode, urlList);
    }

    /**
     * 设置默认操作URL
     */
    public void setDefUrl(String url, String title, String exp, String operationCode) {
        DataGridUrl dataGridUrl = new DataGridUrl();
        dataGridUrl.setTitle(title);
        dataGridUrl.setUrl(url);
        dataGridUrl.setType(OptTypeDirection.Deff);
        dataGridUrl.setExp(exp);
        installOperationCode(dataGridUrl, operationCode, urlList);

    }

    /**
     * 设置工具条
     *
     * @param height2
     * @param width2
     */
    public void setToolbar(String url, String title, String iconCls, String exp, String onclick, String funname, String operationCode, String width2, String height2) {
        DataGridUrl dataGridUrl = new DataGridUrl();
        dataGridUrl.setTitle(title);
        dataGridUrl.setUrl(url);
        dataGridUrl.setType(OptTypeDirection.ToolBar);
        dataGridUrl.setIconCls(iconCls);
        dataGridUrl.setOnclick(onclick);
        dataGridUrl.setExp(exp);
        dataGridUrl.setFunname(funname);
        dataGridUrl.setWidth(String.valueOf(width2));
        dataGridUrl.setHeight(String.valueOf(height2));
        installOperationCode(dataGridUrl, operationCode, toolBarList);

    }

    /**
     * 设置自定义函数操作URL
     */
    public void setFunUrl(String title, String exp, String funname, String operationCode) {
        DataGridUrl dataGridUrl = new DataGridUrl();
        dataGridUrl.setTitle(title);
        dataGridUrl.setType(OptTypeDirection.Fun);
        dataGridUrl.setExp(exp);
        dataGridUrl.setFunname(funname);
        installOperationCode(dataGridUrl, operationCode, urlList);

    }

    /**
     * 设置自定义函数操作URL
     */
    public void setOpenUrl(String url, String title, String width, String height, String exp, String operationCode, String openModel) {
        DataGridUrl dataGridUrl = new DataGridUrl();
        dataGridUrl.setTitle(title);
        dataGridUrl.setUrl(url);
        dataGridUrl.setWidth(width);
        dataGridUrl.setHeight(height);
        dataGridUrl.setType(OptTypeDirection.valueOf(openModel));
        dataGridUrl.setExp(exp);
        installOperationCode(dataGridUrl, operationCode, urlList);

    }

    /**
     * <b>Summary: </b> setColumn(设置字段)
     */
    public void setColumn(String title, String field, Integer width, String rowspan,
                          String colspan, String align, boolean sortable, boolean checkbox,
                          String formatter, boolean hidden, String replace,
                          String treefield, boolean image, String imageSize,
                          boolean query, String url, String funname,
                          String arg, String queryMode, String dictionary,
                          boolean frozenColumn, String extend,
                          String style, String downloadName, boolean isAuto, String extendParams) {
        DataGridColumn dataGridColumn = new DataGridColumn();
        dataGridColumn.setAlign(align);
        dataGridColumn.setCheckbox(checkbox);
        dataGridColumn.setColspan(colspan);
        dataGridColumn.setField(field);
        dataGridColumn.setFormatter(formatter);
        dataGridColumn.setHidden(hidden);
        dataGridColumn.setRowspan(rowspan);
        dataGridColumn.setSortable(sortable);
        dataGridColumn.setTitle(title);
        dataGridColumn.setWidth(width);
        dataGridColumn.setTreefield(treefield);
        dataGridColumn.setImage(image);
        dataGridColumn.setImageSize(imageSize);
        dataGridColumn.setReplace(replace);
        dataGridColumn.setQuery(query);
        dataGridColumn.setUrl(url);
        dataGridColumn.setFunname(funname);
        dataGridColumn.setArg(arg);
        dataGridColumn.setQueryMode(queryMode);
        dataGridColumn.setDictionary(dictionary);
        dataGridColumn.setFrozenColumn(frozenColumn);
        dataGridColumn.setExtend(extend);
        dataGridColumn.setStyle(style);
        dataGridColumn.setDownloadName(downloadName);
        dataGridColumn.setAutocomplete(isAuto);
        dataGridColumn.setExtendParams(extendParams);
        columnList.add(dataGridColumn);
        if ("opt".equals(field)) {
            fields += field + ",";
            if ("group".equals(queryMode)) {
                searchFields += field + "," + field + "_begin," + field + "_end,";
            } else {
                searchFields += field + ",";
            }
        }
        if (replace != null) {
            String[] test = replace.split(",");
            String text = "";
            String value = "";
            for (String string : test) {
                text += string.substring(0, string.indexOf("_")) + ",";
                value += string.substring(string.indexOf("_") + 1) + ",";
            }
            setColumn(field, text, value);

        }
        if (!StringUtils.isBlank(dictionary)) {
            if (dictionary.contains(",")) {
                String[] dic = dictionary.split(",");
                String text = "";
                String value = "";
                String sql = "select " + dic[1] + " as field," + dic[2]
                        + " as text from " + dic[0];
                List<Map<String, Object>> list = commonDao.findForJdbc(sql);
                for (Map<String, Object> map : list) {
                    text += map.get("text") + ",";
                    value += map.get("field") + ",";
                }
                if (list.size() > 0)
                    setColumn(field, text, value);
            } else {
                String text = "";
                String value = "";
                List<SDict> typeList = SDictGroup.getAllDicts().get(dictionary.toLowerCase());
                if (typeList != null && !typeList.isEmpty()) {
                    for (SDict type : typeList) {
                        text += type.getDictName() + ",";
                        value += type.getDictCode() + ",";
                    }
                    setColumn(field, text, value);
                }
            }
        }
        if (StringUtils.isNotEmpty(style)) {
            String[] temp = style.split(",");
            String text = "";
            String value = "";
            if (temp.length == 1 && !temp[0].contains("_")) {
                text = temp[0];
            } else {
                for (String string : temp) {
                    text += string.substring(0, string.indexOf("_")) + ",";
                    value += string.substring(string.indexOf("_") + 1) + ",";
                }
            }
            setStyleColumn(field, text, value);
        }
    }

    /**
     * 设置 颜色替换值
     *
     * @param field
     * @param text
     * @param value
     */
    private void setStyleColumn(String field, String text, String value) {
        ColumnValue columnValue = new ColumnValue();
        columnValue.setName(field);
        columnValue.setText(text);
        columnValue.setValue(value);
        columnStyleList.add(columnValue);
    }

    /**
     * <b>Summary: </b> setColumn(设置字段替换值)
     *
     * @param name
     * @param text
     * @param value
     */
    public void setColumn(String name, String text, String value) {
        ColumnValue columnValue = new ColumnValue();
        columnValue.setName(name);
        columnValue.setText(text);
        columnValue.setValue(value);
        columnValueList.add(columnValue);
    }

    public int doEndTag() throws JspException {
        try {
            JspWriter out = this.pageContext.getOut();
            if (style.equals("easyui")) {
                out.print(end().toString());
            } else {
                out.print(datatables().toString());
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return EVAL_PAGE;
    }

    /**
     * datatables构造方法
     *
     * @return
     */
    public StringBuffer datatables() {
        StringBuffer sb = new StringBuffer();
        sb.append("<script type=\"text/javascript\">");
        sb.append("$(document).ready(function() {");
        sb.append("var oTable = $(\'#").append(name).append("\').dataTable({");
        // sb.append(
        // "\"sDom\" : \"<\'row\'<\'span6\'l><\'span6\'f>r>t<\'row\'<\'span6\'i><\'span6\'p>>\",");
        sb.append("\"bProcessing\" : true,");// 当datatable获取数据时候是否显示正在处理提示信息"
        sb.append("\"bPaginate\" : true,"); // 是否分页"
        sb.append("\"sPaginationType\" : \"full_numbers\",");// 分页样式full_numbers,"
        sb.append("\"bFilter\" : true,");// 是否使用内置的过滤功能"
        sb.append("\"bSort\" : true, ");// 排序功能"
        sb.append("\"bAutoWidth\" : true,");// 自动宽度"
        sb.append("\"bLengthChange\" : true,");// 是否允许用户自定义每页显示条数"
        sb.append("\"bInfo\" : true,");// 页脚信息"
        sb.append("\"sAjaxSource\" : \"userController.do?test\",");
        sb.append("\"bServerSide\" : true,");// 指定从服务器端获取数据
        sb.append("\"oLanguage\" : {" + "\"sLengthMenu\" : \" _MENU_ 条记录\"," + "\"sZeroRecords\" : \"没有检索到数据\"," + "\"sInfo\" : \"第 _START_ 至 _END_ 条数据 共 _TOTAL_ 条\"," + "\"sInfoEmtpy\" : \"没有数据\"," + "\"sProcessing\" : \"正在加载数据...\"," + "\"sSearch\" : \"搜索\"," + "\"oPaginate\" : {" + "\"sFirst\" : \"首页\"," + "\"sPrevious\" : \"前页\", " + "\"sNext\" : \"后页\"," + "\"sLast\" : \"尾页\"" + "}" + "},"); // 汉化
        // 获取数据的处理函数 \"data\" : {_dt_json : JSON.stringify(aoData)},
        sb.append("\"fnServerData\" : function(sSource, aoData, fnCallback, oSettings) {");
        // + "\"data\" : {_dt_json : JSON.stringify(aoData)},"
        sb.append("oSettings.jqXHR = $.ajax({" + "\"dataType\" : \'json\'," + "\"type\" : \"POST\"," + "\"url\" : sSource," + "\"data\" : aoData," + "\"success\" : fnCallback" + "});},");
        sb.append("\"aoColumns\" : [ ");
        int i = 0;
        for (DataGridColumn column : columnList) {
            i++;
            sb.append("{");
            sb.append("\"sTitle\":\"").append(column.getTitle()).append("\"");
            if (column.getField().equals("opt")) {
                sb.append(",\"mData\":\"").append(idField).append("\"");
                sb.append(",\"sWidth\":\"20%\"");
                sb.append(",\"bSortable\":false");
                sb.append(",\"bSearchable\":false");
                sb.append(",\"mRender\" : function(data, type, rec) {");
                this.getOptUrl(sb);
                sb.append("}");
            } else {
                int colwidth = (column.getWidth() == null) ? column.getTitle().length() * 15 : column.getWidth();
                sb.append(",\"sName\":\"").append(column.getField()).append("\"");
                sb.append(",\"mDataProp\":\"").append(column.getField()).append("\"");
                sb.append(",\"mData\":\"").append(column.getField()).append("\"");
                sb.append(",\"sWidth\":\"").append(colwidth).append("\"");
                sb.append(",\"bSortable\":").append(column.isSortable()).append("");
                sb.append(",\"bVisible\":").append(column.isHidden()).append("");
                sb.append(",\"bSearchable\":").append(column.isQuery()).append("");
            }
            sb.append("}");
            if (i < columnList.size())
                sb.append(",");
        }

        sb.append("]" + "});" + "});" + "</script>");
        sb.append("<table width=\"100%\"  class=\"").append(style).append("\" id=\"").append(name).append("\" toolbar=\"#").append(name).append("tb\"></table>");
        return sb;

    }

    public void setStyle(String style) {
        this.style = style;
    }

    /**
     * easyui构造方法
     *
     * @return
     */
    public StringBuffer end() {
        String grid = "";
        StringBuffer sb = new StringBuffer();
        width = (width == null) ? "auto" : width;
        height = (height == null) ? "auto" : height;
        sb.append("<script type=\"text/javascript\">");
        sb.append("$(function(){");
        sb.append(this.getNoAuthOperButton());
        if (treegrid) {
            grid = "treegrid";
            sb.append("$(\'#").append(name).append("\').treegrid({");
            sb.append("idField:'id',");
            sb.append("treeField:'text',");
        } else {
            grid = "datagrid";
            sb.append("$(\'#").append(name).append("\').datagrid({");
            sb.append("idField: '").append(idField).append("',");
        }
        if (title != null) {
            sb.append("title: \'").append(title).append("\',");
        }

        if (autoLoadData)
            sb.append("url:\'").append(actionUrl).append(actionUrl.contains("?") ? "&" : "?").append("field=").append(fields).append("\',");
        else
            sb.append("url:\'',");
        if (StringUtils.isNotEmpty(rowStyler)) {
            sb.append("rowStyler: function(index,row){ return ").append(rowStyler).append("(index,row);},");
        }
        if (StringUtils.isNotEmpty(extendParams)) {
            sb.append(extendParams);
        }
        if (fit) {
            sb.append("fit:true,");
        } else {
            sb.append("fit:false,");
        }
        sb.append("loadMsg: \'数据加载中...\',");
        sb.append("pageSize: ").append(pageSize).append(",");
        sb.append("pagination:").append(pagination).append(",");
        sb.append("pageList:[").append(pageSize).append(",").append(pageSize * 2).append(",").append(pageSize * 3).append("],");
        if (StringUtils.isNotBlank(sortName)) {
            sb.append("sortName:'").append(sortName).append("',");
        }
        sb.append("sortOrder:'").append(sortOrder).append("',");
        sb.append("rownumbers:true,");
        sb.append("singleSelect:").append(!checkbox).append(",");
        if (fitColumns) {
            sb.append("fitColumns:true,");
        } else {
            sb.append("fitColumns:false,");
        }
        sb.append("showFooter:true,");
        sb.append("frozenColumns:[[");
        this.getField(sb, 0);
        sb.append("]],");

        sb.append("columns:[[");
        this.getField(sb);
        sb.append("]],");
        sb.append("onLoadSuccess:function(data){$(\"#").append(name).append("\").").append(grid).append("(\"clearSelections\");");
        if (openFirstNode && treegrid) {
            sb.append(" if(data==null){");
            sb.append(" var firstNode = $(\'#").append(name).append("\').treegrid('getRoots')[0];");
            sb.append(" $(\'#").append(name).append("\').treegrid('expand',firstNode.id)}");
        }
        if (StringUtils.isNotEmpty(onLoadSuccess)) {
            sb.append(onLoadSuccess).append("(data);");
        }
        sb.append("},");
        if (StringUtils.isNotEmpty(onDblClick)) {
            sb.append("onDblClickRow:function(rowIndex,rowData){").append(onDblClick).append("(rowIndex,rowData);},");
        }
        if (treegrid) {
            sb.append("onClickRow:function(rowData){");
        } else {
            sb.append("onClickRow:function(rowIndex,rowData){");
        }
        /**行记录赋值*/
        sb.append("rowid=rowData.id;");
        sb.append("gridname=\'").append(name).append("\';");
        if (StringUtils.isNotEmpty(onClick)) {
            if (treegrid) {
                sb.append("").append(onClick).append("(rowData);");
            } else {
                sb.append("").append(onClick).append("(rowIndex,rowData);");
            }
        }
        sb.append("}");
        sb.append("});");
        this.setPager(sb, grid);
        sb.append("});");
        sb.append("function reloadTable(){");
        sb.append("try{");
        sb.append("	$(\'#\'+gridname).datagrid(\'reload\');");
        sb.append("	$(\'#\'+gridname).treegrid(\'reload\');");
        sb.append("}catch(ex){}");
        sb.append("}");
        sb.append("function reload").append(name).append("(){").append("$(\'#").append(name).append("\').").append(grid).append("(\'reload\');").append("}");
        sb.append("function get").append(name).append("Selected(field){return getSelected(field);}");
        sb.append("function getSelected(field){" + "var row = $(\'#\'+gridname).").append(grid).append("(\'getSelected\');").append("if(row!=null)").append("{").append("value= row[field];").append("}").append("else").append("{").append("value=\'\';").append("}").append("return value;").append("}");
        sb.append("function get").append(name).append("Selections(field){").append("var ids = [];").append("var rows = $(\'#").append(name).append("\').").append(grid).append("(\'getSelections\');").append("for(var i=0;i<rows.length;i++){").append("ids.push(rows[i][field]);").append("}").append("ids.join(\',\');").append("return ids").append("};");
        sb.append("function getSelectRows(){");
        sb.append("	return $(\'#").append(name).append("\').datagrid('getChecked');");
        sb.append("}");
        if (columnList.size() > 0) {
            sb.append("function ").append(name).append("search(){");
            sb.append("var queryParams=$(\'#").append(name).append("\').datagrid('options').queryParams;");
            sb.append("$(\'#").append(name).append("tb\').find('*').each(function(){queryParams[$(this).attr('name')]=$(this).val();});");
            sb.append("$(\'#").append(name).append("\').").append(grid).append("({url:'").append(actionUrl).append(actionUrl.contains("?") ? "&" : "?").append("&field=").append(searchFields).append("',pageNumber:1});").append("}");

            //高级查询执行方法
            sb.append("function dosearch(params){");
            sb.append("var jsonparams=$.parseJSON(params);");
            sb.append("$(\'#").append(name).append("\').").append(grid).append("({url:'").append(actionUrl).append(actionUrl.contains("?") ? "&" : "?").append("&field=").append(searchFields).append("',queryParams:jsonparams});").append("}");

            if (toolBarList.size() > 0) {
                //searchbox框执行方法
                searchboxFun(sb, grid);
            }
            //生成重置按钮功能js
            sb.append("function searchReset(name){");
            sb.append(" $(\"#\"+name+\"tb\").find(\":input\").val(\"\");");
            String func = name.trim() + "search();";
            sb.append(func);
            sb.append("}");
        }
        sb.append("</script>");
        sb.append("<table width=\"100%\"   id=\"").append(name).append("\" toolbar=\"#").append(name).append("tb\"></table>");
        sb.append("<div id=\"").append(name).append("tb\" style=\"padding:3px; height: auto\">");
        if (hasQueryColum(columnList)) {
            sb.append("<div name=\"searchColums\">");
            //如果表单是组合查询
            if ("group".equals(getQueryMode())) {
                for (DataGridColumn col : columnList) {
                    if (col.isQuery()) {
                        sb.append("<span style=\"display:-moz-inline-box;display:inline-block;\">");
                        sb.append("<span style=\"display:-moz-inline-box;display:inline-block;width: 80px;text-align:right;text-overflow:ellipsis;-o-text-overflow:ellipsis; overflow: hidden;white-space:nowrap; \" title=\"").append(col.getTitle()).append("\">").append(col.getTitle()).append("：</span>");
                        if ("single".equals(col.getQueryMode())) {
                            if (!StringUtils.isEmpty(col.getReplace())) {
                                sb.append("<select name=\"search_EQ_").append(col.getField().replaceAll("_", "\\.")).append("\" WIDTH=\"100\" style=\"width: 104px\"> ");
                                sb.append("<option value =\"\" >---请选择---</option>");
                                String[] test = col.getReplace().split(",");
                                String text = "";
                                String value = "";
                                for (String string : test) {
                                    text = string.split("_")[0];
                                    value = string.split("_")[1];
                                    sb.append("<option value =\"").append(value).append("\">").append(text).append("</option>");
                                }
                                sb.append("</select>");
                            } else if (!StringUtils.isEmpty(col.getDictionary())) {
                                if (col.getDictionary().contains(",")) {
                                    String[] dic = col.getDictionary().split(",");
                                    String sql = "select " + dic[1] + " as field," + dic[2]
                                            + " as text from " + dic[0];

                                    List<Map<String, Object>> list = commonDao.findForJdbc(sql);
                                    sb.append("<select name=\"search_EQ_").append(col.getField().replaceAll("_", "\\.")).append("\" WIDTH=\"100\" style=\"width: 104px\"> ");
                                    sb.append("<option value =\"\" >---请选择---</option>");
                                    for (Map<String, Object> map : list) {
                                        sb.append(" <option value=\"").append(map.get("field")).append("\">");
                                        sb.append(map.get("text"));
                                        sb.append(" </option>");
                                    }
                                    sb.append("</select>");
                                } else {
                                    Map<String, List<SDict>> typedatas = SDictGroup.getAllDicts();
                                    List<SDict> types = typedatas.get(col.getDictionary().toLowerCase());
                                    sb.append("<select name=\"search_EQ_").append(col.getField().replaceAll("_", "\\.")).append("\" WIDTH=\"100\" style=\"width: 104px\"> ");
                                    sb.append("<option value =\"\" >---请选择---</option>");
                                    for (SDict type : types) {
                                        sb.append(" <option value=\"").append(type.getDictCode()).append("\">");
                                        sb.append(type.getDictName());
                                        sb.append(" </option>");
                                    }
                                    sb.append("</select>");
                                }
                            } else if (col.isAutocomplete()) {
                                sb.append(getAutoSpan(col.getField().replaceAll("_", "\\."), extendAttribute(col.getExtend())));
                            } else {
                                sb.append("<input type=\"text\" name=\"").append(col.getField().replaceAll("_", "\\.")).append("\"  ").append(extendAttribute(col.getExtend())).append(" style=\"width: 100px\" />");
                            }
                        } else if ("group".equals(col.getQueryMode())) {
                            sb.append("<input type=\"text\" name=\"").append(col.getField()).append("_begin\"  style=\"width: 94px\" ").append(extendAttribute(col.getExtend())).append("/>");
                            sb.append("<span style=\"display:-moz-inline-box;display:inline-block;width: 8px;text-align:right;\">~</span>");
                            sb.append("<input type=\"text\" name=\"").append(col.getField()).append("_end\"  style=\"width: 94px\" ").append(extendAttribute(col.getExtend())).append("/>");
                        }
                        sb.append("</span>");
                    }
                }
            }
            sb.append("</div>");
        }
        if (toolBarList.size() == 0 && !hasQueryColum(columnList)) {
            sb.append("<div style=\"height:0px;\" >");
        } else {
            sb.append("<div style=\"height:30px;\" class=\"datagrid-toolbar\">");
        }
        sb.append("<span style=\"float:left;\" >");
        if (toolBarList.size() > 0) {
            for (DataGridUrl toolBar : toolBarList) {
                sb.append("<a href=\"#\" class=\"easyui-linkbutton\" plain=\"true\" iconCls=\"").append(toolBar.getIconCls()).append("\" ");
                if (StringUtils.isNotEmpty(toolBar.getOnclick())) {
                    sb.append("onclick=").append(toolBar.getOnclick()).append("");
                } else {
                    sb.append("onclick=\"").append(toolBar.getFunname()).append("(");
                    if (!"doSubmit".equals(toolBar.getFunname())) {
                        sb.append("\'").append(toolBar.getTitle()).append("\',");
                    }
                    String width = toolBar.getWidth().contains("%") ? "'" + toolBar.getWidth() + "'" : toolBar.getWidth();
                    String height = toolBar.getHeight().contains("%") ? "'" + toolBar.getHeight() + "'" : toolBar.getHeight();
                    sb.append("\'").append(toolBar.getUrl()).append("\',\'").append(name).append("\',").append(width).append(",").append(height).append(")\"");
                }
                sb.append(">").append(toolBar.getTitle()).append("</a>");
            }
        }
        sb.append("</span>");
        if ("group".equals(getQueryMode()) && hasQueryColum(columnList)) {//如果表单是组合查询
            sb.append("<span style=\"float:right\">");
            sb.append("<a href=\"#\" class=\"easyui-linkbutton\" iconCls=\"icon-search\" onclick=\"").append(name).append("search()\">查询</a>");
            sb.append("<a href=\"#\" class=\"easyui-linkbutton\" iconCls=\"icon-reload\" onclick=\"searchReset('").append(name).append("')\">重置</a>");
            sb.append("</span>");
        } else if ("single".equals(getQueryMode()) && hasQueryColum(columnList)) {//如果表单是单查询
            sb.append("<span style=\"float:right\">");
            sb.append("<input id=\"").append(name).append("searchbox\" class=\"easyui-searchbox\"  data-options=\"searcher:").append(name).append("searchbox,prompt:\'请输入关键字\',menu:\'#").append(name).append("mm\'\"></input>");
            sb.append("<div id=\"").append(name).append("mm\" style=\"width:120px\">");
            for (DataGridColumn col : columnList) {
                if (col.isQuery()) {
                    sb.append("<div data-options=\"name:\'").append(col.getField().replaceAll("_", "\\.")).append("\',iconCls:\'icon-ok\' ").append(extendAttribute(col.getExtend())).append(" \">").append(col.getTitle()).append("</div>  ");
                }
            }
            sb.append("</div>");
            sb.append("</span>");
        }
        sb.append("</div>");
        return sb;
    }

    /**
     * 生成扩展属性
     *
     * @param field
     * @return
     */
    private String extendAttribute(String field) {
        return "";
        /*if (StringUtils.isEmpty(field)) {
            return "";
        }
        field = dealSyscode(field, 1);
        StringBuilder re = new StringBuilder();
        try {
            JsonNode obj = mapper.readTree(field);
            for (Map.Entry<String, Object> o : obj) {
                String key = o.getKey();
                JsonNode nextObj = obj.get(key);
                Iterator<JsonNode> itvalue = nextObj.iterator();
                re.append(key).append("=").append("\"");
                if (nextObj.size() <= 1) {
                    String onlykey = itvalue.next().ge;
                    if ("value".equals(onlykey)) {
                        re.append(nextObj.get(onlykey)).append("");
                    } else {
                        re.append(onlykey).append(":").append(nextObj.get(onlykey)).append("");
                    }
                } else {
                    while (itvalue.hasNext()) {
                        String multkey = itvalue.next().getKey();
                        String multvalue = nextObj.getString(multkey);
                        re.append(multkey).append(":").append(multvalue).append(",");
                    }
                    re.deleteCharAt(re.length() - 1);
                }
                re.append("\" ");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return dealSyscode(re.toString(), 2);*/
    }

    /**
     * 处理否含有json转换中的保留字
     *
     * @param field
     * @param flag  1:转换 2:还原
     * @return
     */
    private String dealSyscode(String field, int flag) {
        String change = field;
        for (Object o : syscode.keySet()) {
            String key = String.valueOf(o);
            String value = String.valueOf(syscode.get(key));
            if (flag == 1) {
                change = field.replaceAll(key, value);
            } else if (flag == 2) {
                change = field.replaceAll(value, key);
            }
        }
        return change;
    }

    /**
     * 判断是否存在查询字段
     *
     * @return hasQuery true表示有查询字段,false表示没有
     */
    protected boolean hasQueryColum(List<DataGridColumn> columnList) {
        boolean hasQuery = false;
        for (DataGridColumn col : columnList) {
            if (col.isQuery()) {
                hasQuery = true;
            }
        }
        return hasQuery;
    }

    /**
     * 拼装操作地址
     *
     * @param sb
     */
    protected void getOptUrl(StringBuffer sb) {
        //注：操作列表会带入合计列中去，故加此判断
        sb.append("if(!rec.id){return '';}");
        List<DataGridUrl> list = urlList;
        sb.append("var href='';");
        for (DataGridUrl dataGridUrl : list) {
            String url = dataGridUrl.getUrl();
            MessageFormat formatter = new MessageFormat("");
            if (dataGridUrl.getValue() != null) {
                String[] testvalue = dataGridUrl.getValue().split(",");
                List value = new ArrayList<Object>();
                for (String string : testvalue) {
                    value.add("\"+rec." + string + " +\"");
                }
                url = formatter.format(url, value.toArray());
            }
            if (url != null && dataGridUrl.getValue() == null) {

                url = formatUrl(url);
            }
            String exp = dataGridUrl.getExp();// 判断显示表达式
            if (StringUtils.isNotEmpty(exp)) {
                String[] ShowbyFields = exp.split("&&");
                for (String ShowbyField : ShowbyFields) {
                    int beginIndex = ShowbyField.indexOf("#");
                    int endIndex = ShowbyField.lastIndexOf("#");
                    String exptype = ShowbyField.substring(beginIndex + 1, endIndex);// 表达式类型
                    String field = ShowbyField.substring(0, beginIndex);// 判断显示依据字段
                    String[] values = ShowbyField.substring(endIndex + 1, ShowbyField.length()).split(",");// 传入字段值
                    String value = "";
                    for (int i = 0; i < values.length; i++) {
                        value += "'" + "" + values[i] + "" + "'";
                        if (i < values.length - 1) {
                            value += ",";
                        }
                    }
                    if ("eq".equals(exptype)) {
                        sb.append("if($.inArray(rec.").append(field).append(",[").append(value).append("])>=0){");
                    }
                    if ("ne".equals(exptype)) {
                        sb.append("if($.inArray(rec.").append(field).append(",[").append(value).append("])<0){");
                    }
                    if ("empty".equals(exptype) && value.equals("'true'")) {
                        sb.append("if(rec.").append(field).append("==''){");
                    }
                    if ("empty".equals(exptype) && value.equals("'false'")) {
                        sb.append("if(rec.").append(field).append("!=''){");
                    }
                }
            }

            if (OptTypeDirection.Confirm.equals(dataGridUrl.getType())) {
                sb.append("href+=\"[<a href=\'#\' onclick=confirm(\'" + url + "\',\'" + dataGridUrl.getMessage() + "\',\'" + name + "\')> \";");
            }
            if (OptTypeDirection.Del.equals(dataGridUrl.getType())) {
                sb.append("href+=\"[<a href=\'#\' onclick=delObj(\'" + url + "\',\'" + name + "\')>\";");
            }
            if (OptTypeDirection.Fun.equals(dataGridUrl.getType())) {
                String name = TagUtil.getFunction(dataGridUrl.getFunname());
                String parmars = TagUtil.getFunParams(dataGridUrl.getFunname());
                sb.append("href+=\"[<a href=\'#\' onclick=" + name + "(" + parmars + ")>\";");
            }
            if (OptTypeDirection.OpenWin.equals(dataGridUrl.getType())) {
                sb.append("href+=\"[<a href=\'#\' onclick=openwindow('" + dataGridUrl.getTitle() + "','" + url + "','" + name + "'," + dataGridUrl.getWidth() + "," + dataGridUrl.getHeight() + ")>\";");
            }                                                            //update-end--Author:liuht  Date:20130228 for：弹出窗口设置参数不生效
            if (OptTypeDirection.Deff.equals(dataGridUrl.getType())) {
                sb.append("href+=\"[<a href=\'" + url + "' title=\'" + dataGridUrl.getTitle() + "\'>\";");
            }
            if (OptTypeDirection.OpenTab.equals(dataGridUrl.getType())) {
                sb.append("href+=\"[<a href=\'#\' onclick=addOneTab('" + dataGridUrl.getTitle() + "','" + url + "')>\";");
            }
            sb.append("href+=\"" + dataGridUrl.getTitle() + "</a>]\";");

            if (StringUtils.isNotEmpty(exp)) {
                for (int i = 0; i < exp.split("&&").length; i++) {
                    sb.append("}");
                }

            }
        }
        sb.append("return href;");
    }

    /**
     * 列自定义函数
     *
     * @param sb
     * @param column
     */
    protected void getFun(StringBuffer sb, DataGridColumn column) {
        String url = column.getUrl();
        url = formatUrl(url);
        sb.append("var href=\"<a style=\'color:red\' href=\'#\' onclick=").append(column.getFunname()).append("('").append(column.getTitle()).append("','").append(url).append("')>\";");
        sb.append("return href+value+\'</a>\';");

    }

    /**
     * 格式化URL
     *
     * @return
     */
    protected String formatUrl(String url) {
        MessageFormat formatter = new MessageFormat("");
        String parurlvalue = "";
        if (url.contains("&")) {
            String beforeurl = url.substring(0, url.indexOf("&"));// 截取请求地址
            String parurl = url.substring(url.indexOf("&") + 1, url.length());// 截取参数
            String[] pras = parurl.split("&");
            List value = new ArrayList<Object>();
            int j = 0;
            for (String pra : pras) {
                if (pra.contains("{") || pra.contains("#")) {
                    String field = pra.substring(pra.indexOf("{") + 1, pra.lastIndexOf("}"));
                    parurlvalue += "&" + pra.replace("{" + field + "}", "{" + j + "}");
                    value.add("\"+rec." + field + " +\"");
                    j++;
                } else {
                    parurlvalue += "&" + pra;
                }
            }
            url = formatter.format(beforeurl + parurlvalue, value.toArray());
        }
        return url;

    }

    /**
     * 拼接字段  普通列
     *
     * @param sb
     */
    protected void getField(StringBuffer sb) {
        getField(sb, 1);
    }

    /**
     * 拼接字段
     *
     * @param sb
     * @frozen 0 冰冻列    1 普通列
     */
    protected void getField(StringBuffer sb, int frozen) {
        // 复选框
        if (checkbox && frozen == 0) {
            sb.append("{field:\'ck\',checkbox:\'true\'},");
        }
        int i = 0;
        for (DataGridColumn column : columnList) {
            i++;
            if ((column.isFrozenColumn() && frozen == 0) || (!column.isFrozenColumn() && frozen == 1)) {
                String field;
                if (treegrid) {
                    field = column.getTreefield();
                } else {
                    field = column.getField();
                }
                sb.append("{field:\'").append(field).append("\',title:\'").append(column.getTitle()).append("\'");
                if (column.getWidth() != null) {
                    sb.append(",width:").append(column.getWidth());
                }
                if (column.getAlign() != null) {
                    sb.append(",align:\'").append(column.getAlign()).append("\'");
                }
                if (StringUtils.isNotEmpty(column.getExtendParams())) {
                    sb.append(",").append(column.getExtendParams().substring(0,
                            column.getExtendParams().length() - 1));
                }
                // 隐藏字段
                if (!column.isHidden()) {
                    sb.append(",hidden:true");
                }
                if (!treegrid) {
                    // 字段排序
                    if ((column.isSortable()) && (field.indexOf("_") <= 0 && field != "opt")) {
                        sb.append(",sortable:").append(column.isSortable()).append("");
                    }
                }
                // 显示图片
                if (column.isImage()) {
                    sb.append(",formatter:function(value,rec,index){");
                    sb.append(" return '<img border=\"0\" src=\"'+value+'\"/>';}");
                }
                // 自定义显示图片
                if (column.getImageSize() != null) {
                    String[] tld = column.getImageSize().split(",");
                    sb.append(",formatter:function(value,rec,index){");
                    sb.append(" return '<img width=\"").append(tld[0]).append("\" height=\"").append(tld[1]).append("\" border=\"0\" src=\"'+value+'\"/>';}");
                    tld = null;
                }
                if (column.getDownloadName() != null) {
                    sb.append(",formatter:function(value,rec,index){");
                    sb.append(" return '<a target=\"_blank\" href=\"'+value+'\">").append(column.getDownloadName()).append("</a>';}");
                }
                // 自定义链接
                if (column.getUrl() != null) {
                    sb.append(",formatter:function(value,rec,index){");
                    this.getFun(sb, column);
                    sb.append("}");
                }
                if (column.getFormatter() != null) {
                    sb.append(",formatter:function(value,rec,index){");
                    sb.append(" return new Date().format('").append(column.getFormatter()).append("',value);}");
                }
                // 加入操作
                if (column.getField().equals("opt")) {
                    sb.append(",formatter:function(value,rec,index){");
                    // sb.append("return \"");
                    this.getOptUrl(sb);
                    sb.append("}");
                }
                // 值替換
                if (columnValueList.size() > 0 && !column.getField().equals("opt")) {
                    String testString = "";
                    for (ColumnValue columnValue : columnValueList) {
                        if (columnValue.getName().equals(column.getField())) {
                            String[] value = columnValue.getValue().split(",");
                            String[] text = columnValue.getText().split(",");
                            sb.append(",formatter:function(value,rec,index){");
                            for (int j = 0; j < value.length; j++) {
                                testString += "if(value=='" + value[j] + "'){return \'" + text[j] + "\'}";
                            }
                            sb.append(testString);
                            sb.append("else{return value}");
                            sb.append("}");
                        }
                    }

                }
                // 背景设置
                if (columnStyleList.size() > 0 && !column.getField().equals("opt")) {
                    String testString = "";
                    for (ColumnValue columnValue : columnStyleList) {
                        if (columnValue.getName().equals(column.getField())) {
                            String[] value = columnValue.getValue().split(",");
                            String[] text = columnValue.getText().split(",");
                            sb.append(",styler:function(value,rec,index){");
                            if ((value.length == 0 || StringUtils.isEmpty(value[0])) && text.length == 1) {
                                if (text[0].contains("(")) {
                                    testString = " return \'" + text[0].replace("(", "(value,rec,index") + "\'";
                                } else {
                                    testString = " return \'" + text[0] + "\'";
                                }
                            } else {
                                for (int j = 0; j < value.length; j++) {
                                    testString += "if(value=='" + value[j] + "'){return \'" + text[j] + "\'}";
                                }
                            }
                            sb.append(testString);
                            sb.append("}");
                        }
                    }

                }
                sb.append("}");
                // 去除末尾,
                if (i < columnList.size()) {
                    sb.append(",");
                }
            }
        }
    }

    /**
     * 设置分页条信息
     *
     * @param sb
     */
    protected void setPager(StringBuffer sb, String grid) {
        sb.append("$(\'#").append(name).append("\').").append(grid).append("(\'getPager\').pagination({");
        sb.append("beforePageText:\'\'," + "afterPageText:\'/{pages}\',");
        if (showText) {
            sb.append("displayMsg:\'{from}-{to}共{total}条\',");
        } else {
            sb.append("displayMsg:\'\',");
        }
        if (showPageList) {
            sb.append("showPageList:true,");
        } else {
            sb.append("showPageList:false,");
        }
        sb.append("showRefresh:").append(showRefresh).append("");
        sb.append("});");// end getPager
        sb.append("$(\'#").append(name).append("\').").append(grid).append("(\'getPager\').pagination({");
        sb.append("onBeforeRefresh:function(pageNumber, pageSize){ $(this).pagination(\'loading\');$(this).pagination(\'loaded\'); }");
        sb.append("});");
    }

    //列表查询框函数
    protected void searchboxFun(StringBuffer sb, String grid) {
        sb.append("function ").append(name).append("searchbox(value,name){");
        sb.append("var queryParams=$(\'#").append(name).append("\').datagrid('options').queryParams;");
        sb.append("queryParams[name]=value;queryParams.searchfield=name;$(\'#").append(name).append("\').").append(grid).append("(\'reload\');}");
        sb.append("$(\'#").append(name).append("searchbox\').searchbox({");
        sb.append("searcher:function(value,name){");
        sb.append("").append(name).append("searchbox(value,name);");
        sb.append("},");
        sb.append("menu:\'#").append(name).append("mm\',");
        sb.append("prompt:\'请输入查询关键字\'");
        sb.append("});");
    }

    @SuppressWarnings("unchecked")
    public String getNoAuthOperButton() {
        List<String> nolist = (List<String>) super.pageContext.getRequest().getAttribute("noauto_operationCodes");
        StringBuffer sb = new StringBuffer();
        if (ContextHolderUtils.getCurrentUser().getName().equals("admin") || !Globals.BUTTON_AUTHORITY_CHECK) {
        } else {
            if (nolist != null && nolist.size() > 0) {
                for (String s : nolist) {
                    sb.append("$('#" + name + "tb\').find(\"" + s.replaceAll(" ", "") + "\").hide();");
                }
            }
        }
        String rt = sb.toString();
        LOG.info("----getNoAuthOperButton-------{}", rt);
        return sb.toString();
    }


    /**
     * 描述：组装菜单按钮操作权限
     * dateGridUrl：url
     * operationCode：操作码
     * optList： 操作列表
     */
    @SuppressWarnings("unchecked")
    private void installOperationCode(DataGridUrl dataGridUrl, String operationCode, List optList) {
        if (ContextHolderUtils.getCurrentUser().getName().equals("admin") || !Globals.BUTTON_AUTHORITY_CHECK) {
            optList.add(dataGridUrl);
        } else if (!StringUtils.isEmpty(operationCode) && !"null".equalsIgnoreCase(operationCode)) {
            Set<String> operationCodes = (Set<String>) super.pageContext.getRequest().getAttribute("operationCodes");
            if (null != operationCodes) {
                for (String MyoperationCode : operationCodes) {
                    if (MyoperationCode.equals(operationCode)) {
                        optList.add(dataGridUrl);
                    }
                }
            }
        } else {
            optList.add(dataGridUrl);
        }
    }

    /**
     * 获取自动补全的panel
     *
     * @param filed
     * @return
     * @author JueYue
     */
    private String getAutoSpan(String filed, String extend) {
        String id = filed.replaceAll("\\.", "_");
        StringBuilder nsb = new StringBuilder();
        nsb.append("<script type=\"text/javascript\">");
        nsb.append("$(document).ready(function() {").append("$(\"#").append(getEntityName()).append("_").append(id).append("\").autocomplete(\"commonController.do?getAutoList\",{")
                .append("max: 5,minChars: 2,width: 200,scrollHeight: 100,matchContains: true,autoFill: false,extraParams:{").append("featureClass : \"P\",style : \"full\",	maxRows : 10,labelField : \"").append(filed).append("\",valueField : \"").append(filed).append("\",").append("searchField : \"").append(filed).append("\",entityName : \"").append(getEntityName()).append("\",trem: function(){return $(\"#").append(getEntityName()).append("_").append(id).append("\").val();}}");
        nsb.append(",parse:function(data){return jeecgAutoParse.call(this,data);}");
        nsb.append(",formatItem:function(row, i, max){return row['").append(filed).append("'];} ");
        nsb.append("}).result(function (event, row, formatted) {");
        nsb.append("$(\"#").append(getEntityName()).append("_").append(id).append("\").val(row['").append(filed).append("']);}); });")
                .append("</script>").append("<input type=\"text\" id=\"").append(getEntityName()).append("_").append(id).append("\" name=\"").append(filed).append("\" datatype=\"*\" ").append(extend).append(" nullmsg=\"\" errormsg=\"输入错误\"/>");
        return nsb.toString();
    }

    /**
     * 获取实体类名称,没有这根据规则设置
     *
     * @return
     */
    private String getEntityName() {
        if (StringUtils.isEmpty(entityName)) {
            entityName = actionUrl.substring(0, actionUrl.indexOf("Controller"));
            entityName = (entityName.charAt(0) + "").toUpperCase() + entityName.substring(1) + "Entity";
        }
        return entityName;
    }

    public boolean isFitColumns() {
        return fitColumns;
    }

    public void setFitColumns(boolean fitColumns) {
        this.fitColumns = fitColumns;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getQueryMode() {
        return queryMode;
    }

    public void setQueryMode(String queryMode) {
        this.queryMode = queryMode;
    }

    public boolean isAutoLoadData() {
        return autoLoadData;
    }

    public void setAutoLoadData(boolean autoLoadData) {
        this.autoLoadData = autoLoadData;
    }

    public void setOpenFirstNode(boolean openFirstNode) {
        this.openFirstNode = openFirstNode;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public void setRowStyler(String rowStyler) {
        this.rowStyler = rowStyler;
    }

    public void setExtendParams(String extendParams) {
        this.extendParams = extendParams;
    }

}
