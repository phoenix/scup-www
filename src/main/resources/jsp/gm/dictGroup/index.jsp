<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/jsp/common/taglibs.jsp" %>
<html>
<head>
    <title>字典分组管理</title>
</head>
<body>
<ui:edatagrid id="dictGroup" title="字典分组管理" url="/gm/dictGroup/list"
              saveUrl="/gm/dictGroup/save" updateUrl="/gm/dictGroup/update" destroyUrl="/gm/dictGroup/destroy">
    <ui:edgCol field="dictGroupName" columnTitle="字典分组名称(显示值)" editor="text"/>
    <ui:edgCol field="dictGroupCode" columnTitle="字典分组编码(存储值)" editor="text"/>
    <ui:dgToolBar funcName="addRow"/>
    <ui:dgToolBar funcName="destroyRow"/>
    <ui:dgToolBar funcName="saveRow"/>
    <ui:dgToolBar funcName="cancelRow"/>
</ui:edatagrid>
</body>
</html>
