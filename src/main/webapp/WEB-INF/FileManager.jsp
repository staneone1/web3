<%@ page pageEncoding="UTF-8"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page isELIgnored="false" %>
<%@ page session="true" %>

<fmt:setLocale value="${sessionScope.lang}" />
<fmt:setBundle basename="messages" />

<!DOCTYPE html>
<html lang="${sessionScope.lang}">
<head>
  <title><fmt:message key="welcome" /></title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" type="text/css" media="screen" href="${url}/styles/filemanager.css" />
  <link rel="shortcut icon" href="${url}/img/folder.gif" type="image/gif" />
</head>

<body bgcolor="#D3D3D3">

<h3><fmt:message key="welcome" /></h3>

<a href="?sessionLocale=en"><fmt:message key="label.lang.en" /><img src="${url}/img/english.gif" title="ENGLISH" width="16" height="16" alt="EN" border="1"></a>
<a href="?sessionLocale=sp"><fmt:message key="label.lang.sp" /><img src="${url}/img/spanish.gif" title="SPANISH" width="16" height="16" alt="SP" border="1"></a>
<a href="?sessionLocale=gr"><fmt:message key="label.lang.gr" /><img src="${url}/img/germany.gif" title="GERMAN" width="16" height="16" alt="GR" border="1"></a>
<c:if test="${not empty param.sessionLocale}">

</c:if>
<p>

<span style="color: rgb(139, 0, 0);">${actionresult}</span>

<c:if test="${!fatalerror}">

<form name="files" action="${self}${path}" method="post">

<table border="1">
<tr>
<td class="title" style="border-right-width: 0;">

<img src="${url}/img/openfolder.gif" title="current folder" width="16" height="16" alt="DIR" border="0">

<c:set var="parentlink" value="" scope="request"/>

<c:forEach var="parent" items="${folder.parents}" varStatus="status">

<c:choose>
  <c:when test="${parent.isActive}">
    <a href="${self}${parent.link}">${parent.display}</a>
  </c:when>
  <c:otherwise>
    ${parent.display}
  </c:otherwise>
</c:choose>

    <c:if test="${!status.last}"><c:set var="parentlink" value="${self}${parent.link}" scope="request"/>
</c:if>

</c:forEach>

&nbsp;

<c:if test="${parentlink != ''}">
<a href="${parentlink}"><img src="${url}/img/up-one-dir.gif" title="to parent folder" width="16" height="16" alt="UP" border="0"></a>
</c:if>
&nbsp;
    <a href="${self}${path}"><img src="${url}/img/reload.gif" title="reload folder" width="16" height="16" alt="RELOAD" border="0"></a>

</td>
</tr>

</table>


<table class="files" border="1">
<thead>

<tr>
<td class="header-center" style="width: 5%;">
<script>
function doChkAll(oChkBox) {
var bChecked = oChkBox.checked;
var docFrmChk = document.forms['files'].index;
for (var i = 0; i < docFrmChk.length; i++) {
docFrmChk[i].checked = bChecked;
}
}
</script>
<small>
<fmt:message key="check"/>
<input type="checkbox" name="chkAll" onclick="doChkAll(this);">
</small>
</td>


<td class="header-center" style="width: 40%;"><small><fmt:message key="filename"/></small>&nbsp;


</td>

<td class="header-center" style="width: 3em;"><small><fmt:message key="type"/></small></td>

<td class="header-center" style="width: 10%;"><small><fmt:message key="size"/></small>
&nbsp;

&nbsp;

</td>

<td class="header-center" style="width: 15%;"><small><fmt:message key="lastmod"/> </small>

&nbsp;
</td>

<%--<td class="header-center"><small><fmt:message key="attrib"/></small></td>--%>


</tr>

</thead>

<tbody>


<c:forEach var="file" items="${folder.files}">

<tr>
<td class="row-right">

<c:if test="${!file.isDirectory && file.type=='txt'}">
    <a href='${pageContext.request.contextPath}/open?name=${fn:replace(file, '\\', '/')}';><img src="${url}/img/pencil.gif" title="opedit ${file.name}" width="16" height="16" alt="OPN" border="0"></a>

</c:if>


<small><input type="checkbox" name="index" value="${file.id}"></small></td>

<td class="row-left"><small><c:choose>
  <c:when test="${file.type=='dir'}">
   <a href="${self}${file.path}"><img src="${url}/img/folder.gif" title="folder" width="16" height="16" alt="DIR" border="0"></a>
   <a href="${self}${file.path}">${file.name}</a>
  </c:when>
<c:when test="${file.type=='pdf'}">
    <a href="${self}${file.path}"><img src="${url}/img/pdf.gif" title="pdf" width="16" height="16" alt="PDF" border="0"></a>
    <a href="${self}${file.path}">${file.name}</a>
</c:when>
    <c:when test="${file.type=='txt'}">
        <a href="${self}${file.path}"><img src="${url}/img/txt.gif" title="txt" width="16" height="16" alt="TXT" border="0"></a>
        <a href="${self}${file.path}">${file.name}</a>
    </c:when>
    <c:when test="${file.type=='pptx'}">
        <a href="${self}${file.path}"><img src="${url}/img/pptx.gif" title="pptx" width="16" height="16" alt="PPTX" border="0"></a>
        <a href="${self}${file.path}">${file.name}</a>
    </c:when>
    <c:when test="${file.type=='zip'}">
        <a href="${self}${file.path}"><img src="${url}/img/zip.gif" title="zip" width="16" height="16" alt="ZIP" border="0"></a>
        <a href="${self}${file.path}">${file.name}</a>
    </c:when>
  <c:otherwise>
  <a href="${self}${file.path}"><img src="${url}/img/file.gif" title="file" width="16" height="16" alt="FILE" border="0"></a>
  <a href="${self}${file.path}">${file.name}</a>
  </c:otherwise>
 </c:choose>  </small></td>
<td class="row-center">${file.type}</td>


<td class="row-center">${file.size} </td>

<td class="row-center">${file.lastModified}</td>

<%--<td class="row-center">${file.attributes}</td>--%>



</tr>

</c:forEach>



</tbody>
</table>

    <table border="1">
        <tbody>
        <tr>
            <td colspan="3" class="header-left"><fmt:message key="notess"/></td>
        </tr>

            <c:forEach var="item" items="${dirs}">

                <tr>
                    <td class="row-left"><fmt:message key="notesfor"/> <c:out value="${item}:"/> </td>
                    <td><c:out value="${notes[item]}"/></td>
                    <td class="row-center">
                        <form action="${pageContext.request.contextPath}/createNote?nameNote=${item}" method="post">
                            <input name="note" STYLE="color: #000000; background-color: #D3D3D3" /><button input type="submit" name="command" value="Add note"><fmt:message key="add"/></button>
                        </form>
                    </td>
                </tr>

            </c:forEach>



        </tbody>
    </table>

<table border="1">
<tbody>

<tr>
<td colspan="4" class="header-left"><fmt:message key="clipboard"/></td>
</tr>

<tr>
<td class="row-left"><button input type="submit" name="command" value=Cut><fmt:message key="cutt"/> </button></td>
<td class="row-left"><button input type="submit" name="command" value=Copy><fmt:message key="copyt"/> </button></td>
<td class="row-left"><button input type="submit" name="command" value=Paste><fmt:message key="pastet"/> </button></td>
<td class="row-left"><button input type="submit" name="command" value=Clear><fmt:message key="cleart"/> </button></td>
<tr>

<c:if test="${not empty sessionScope.clipBoardContent}">
<tr><td colspan="0" style="font-size:small;">Clipboard contains ${sessionScope.clipBoardContent.fileCount} files to ${sessionScope.clipBoardContent.kind}.
</td></tr>
<tr><td colspan="0" style="font-size:small;">${sessionScope.clipBoardContent.files}</td></tr>
</c:if>

<tr>
<td colspan="3" class="header-left"><fmt:message key="actions"/></td>
</tr>

<tr>
<td class="row-left"><button input   type="submit" name="command" value="Rename to"><fmt:message key="rename"/> </button><input style="background-color: #D3D3D3" name="renameto" type="text"></td>


<td class="row-left"><button input type="submit" name="command" value=Delete><fmt:message key="del"/> </button></td>

<td class="row-left"> <button input type="submit" name="command" value="DeleteRecursively" title="Delete selected folders recursively"><fmt:message key="delrec"/></button><fmt:message key="type+"/><input style="background-color: #D3D3D3" name="confirm" type="text" size="3" title="Confirm with +"></td>

</tr>
</tr>

</tbody>
</table>

</form>


<table border="1">
    <tbody>
    <tr>
        <td colspan="2" class="row-left">
            <form action="${self}${path}" method="post">
                <button input  type="submit" name="command" value="Create dir"><fmt:message key="createdir"/> </button>
                <input style="background-color: #D3D3D3" name="newdir" type="text">
            </form>
        </td>
    </tr>
    <tr><td colspan="2" class="row-left">
        <form action="${self}${path}" method="post">
            <button input  type="submit" name="command" value="Create file"><fmt:message key="createfile"/> </button>
            <input style="background-color: #D3D3D3" name="newfile" type="text">
        </form>
    </td></tr>
    </tbody>
</table>


<form action="${self}${path}"
      method="post" enctype="multipart/form-data">

    <table border="1">
        <tbody>
        <tr>
            <td class="title">
                <fmt:message key="upload"/>
            </td>

        </tr>


        <tr>
            <td bgcolor="#D3D3D3" class="row-left"><fmt:message key="choose"/><input type="file" name="file"></td>

        </tr>


        <tr>
            <td class="row-left"><button input type="submit" name="command" value="Upload"><fmt:message key="up"/> </button></td>
        </tr>

        </tbody>
    </table>
</form>



</c:if>

<table border="0">

</table>

</body>

</html>