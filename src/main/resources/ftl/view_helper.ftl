<#setting number_format="#">
<#import "/spring.ftl" as spring />
<#assign contextPath = springMacroRequestContext.getContextPath()>
<#assign h=JspTaglibs["/web-helper"]>
<#assign form=JspTaglibs["http://www.springframework.org/tags/form"]>

<#macro stylesheet_link_tag css...>
    <#list css as c>
    <#if c?starts_with("/")>
    <link href="${contextPath}${c}.css?${file_last_modified_at(c+".css")}" media="screen" rel="stylesheet" type="text/css" />
    <#else>
    <link href="${contextPath}/stylesheets/${c}.css?${file_last_modified_at("/stylesheets/"+c+".css")}" media="screen" rel="stylesheet" type="text/css" />
    </#if>
    </#list>
</#macro>

<#macro javascript_include_tag scripts...>
    <#list scripts as script>
    <#if script?starts_with("/")>
    <script src="${contextPath}${script}.js?${file_last_modified_at(script+".js")}" type="text/javascript"></script>
    <#else>
    <script src="${contextPath}/javascripts/${script}.js?${file_last_modified_at("/javascripts/"+script+".js")}" type="text/javascript"></script>
    </#if>
    </#list>
</#macro>

<#macro calendar_for id>
<script>
    $(function() {
    $.datepicker.setDefaults($.extend({showMonthAfterYear: true,showButtonPanel: true,changeYear: true},$.datepicker.regional['zh-CN']));
		$("#${id}").datepicker({showOn: 'button', buttonImage: '${contextPath}/images/calendar.png', buttonImageOnly: true,buttonText:'设置日期',showAnim:'show',duration:'normal'});
	});
</script>
</#macro>

<#assign cycle_mark=0>
<#macro cycle something...>
<#t> ${something[cycle_mark]}    <#assign cycle_mark=cycle_mark+1>   <#if cycle_mark gte something?size > <#assign cycle_mark=0> </#if>
</#macro>