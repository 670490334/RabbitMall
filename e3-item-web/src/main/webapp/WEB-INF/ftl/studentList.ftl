<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<div>
	学生信息:
	<table border="1">
		<tr>
			<td>序号</td>
			<td>姓名</td>
			<td>年龄</td>
		</tr>
		<#list studentList as student>
		<#if student_index %2 == 0>
		<tr bgcolor="red">
		<#else>
		<tr bgcolor="green">
		</#if>
			<td>${student_index}</td>
			<td>${student.name}</td>
			<td>${student.age}</td>
		</tr>
		</#list>
	</table>
	<br>
	当前时间: ${date?datetime}<br>
	null值处理: ${val!"val的值为null"}<br>
	判断val是否为空<br>
	<#if val??>
	val有值
	<#else>
	val值为空
	</#if>
	<br>
	引用模板测试<br>
	<#include "hello.ftl">
</div>
</body>
</html>

