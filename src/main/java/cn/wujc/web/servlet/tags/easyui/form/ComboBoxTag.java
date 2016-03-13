package cn.wujc.web.servlet.tags.easyui.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

@JsonIgnoreProperties({"parent", "values"})
public class ComboBoxTag extends TagSupport {
    private static final Logger LOG = LoggerFactory.getLogger(ComboBoxTag.class);
    private static final ObjectMapper MAPPER = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
            .configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false)
            .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
            .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

    //The underlying data value name to bind to this ComboBox. Default "value"
    private String valueField;
    //The underlying data field name to bind to this ComboBox. Default "text"
    private String textField;
    //Indicate what field to be grouped.
    private String groupField;
    //return group text to display on group item.
    private String groupFormatter;
    //Defines how to load list data when text changed. Set to 'remote' if the combobox loads from server.
    //When set to 'remote' mode, what the user types will be sent as the http request parameter named 'q' to server to retrieve the new data.
    //Default 'local'
    private String mode;
    //A URL to load list data from remote.
    private String url;
    //The http method to retrieve data. Default post
    private String method;
    //The list data to be loaded.example:
    //data: [{label: 'java',value: 'Java'},{label: 'perl',value: 'Perl'},{label: 'ruby',value: 'Ruby'}]
    @JsonIgnore
    private String jsonData;
    private JsonNode data;
    //The additional parameters that will be sent to server when requesting remote data.Default {}
    private String queryParams;
    //Defines how to filter the local data when 'mode' is set to 'local'. The function takes two parameters:
    //q: the user typed text. row: the list row data. Return true to allow the row to be displayed.
    private String filter;
    //Defineds how to render the row. The function takes one parameter: row.
    private String formatter;
    //Defines how to load data from remote server. Return false can abort this action.
    //function(param,success,error)
    //param: the parameter object to pass to remote server.
    //success(data): the callback function that will be called when retrieve data successfully.
    //error(): the callback function that will be called when failed to retrieve data.
    private String loader;
    //Return the filtered data to display.
    private String loadFilter;

    public String getValueField() {
        return valueField;
    }

    public void setValueField(String valueField) {
        this.valueField = valueField;
    }

    public String getTextField() {
        return textField;
    }

    public void setTextField(String textField) {
        this.textField = textField;
    }

    public String getGroupField() {
        return groupField;
    }

    public void setGroupField(String groupField) {
        this.groupField = groupField;
    }

    public String getGroupFormatter() {
        return groupFormatter;
    }

    public void setGroupFormatter(String groupFormatter) {
        this.groupFormatter = groupFormatter;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) throws IOException {
        this.jsonData = jsonData;
        this.data = MAPPER.readTree(jsonData);
    }

    public JsonNode getData() {
        return data;
    }

    public void setData(JsonNode data) {
        this.data = data;
    }

    public String getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(String queryParams) {
        this.queryParams = queryParams;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getFormatter() {
        return formatter;
    }

    public void setFormatter(String formatter) {
        this.formatter = formatter;
    }

    public String getLoader() {
        return loader;
    }

    public void setLoader(String loader) {
        this.loader = loader;
    }

    public String getLoadFilter() {
        return loadFilter;
    }

    public void setLoadFilter(String loadFilter) {
        this.loadFilter = loadFilter;
    }

    @Override
    public int doEndTag() throws JspException {
        try {
            pageContext.getOut().write(MAPPER.writeValueAsString(this));
        } catch (IOException e) {
            LOG.error("", e);
        }
        return EVAL_PAGE;
    }
}
