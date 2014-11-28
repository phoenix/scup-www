<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/jsp/common/taglibs.jsp" %>
<html>
<head>
    <title>字典管理</title>
</head>
<body>
<ui:edatagrid id="dict" title="字典管理" url="/gm/dict/list"
              saveUrl="/gm/dict/save" updateUrl="/gm/dict/update" destroyUrl="/gm/dict/destroy">
    <ui:edgCol field="dictName" columnTitle="字典名称(显示值)" editor="text"/>
    <ui:edgCol field="dictCode" columnTitle="字典编码(存储值)" editor="text"/>
    <ui:edgCol field="dictGroupId" columnTitle="字典分组" dictionary="/gm/dictGroup/dict" editor="{type:'combobox',options:{url:'/gm/dictGroup/dict'}}"/>
</ui:edatagrid>
</body>
</html>
