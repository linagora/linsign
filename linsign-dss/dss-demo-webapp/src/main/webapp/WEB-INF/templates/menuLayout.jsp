<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div class="list-group hidden-print">
    <c:set var="currentPage" value="${requestScope['javax.servlet.forward.servlet_path']}" />
    <c:choose> 
        <%-- ADMIN PART --%>
        <c:when test="${fn:contains(currentPage, 'admin')}">
            <a href="<spring:url value="/admin/general"/>" class="list-group-item${currentPage eq '/admin/general' ? ' active' : ''}"><spring:message code="label.general" /></a>
            <a href="<spring:url value="/admin/proxy"/>" class="list-group-item${currentPage eq '/admin/proxy' ? ' active' : ''}"><spring:message code="label.proxy" /></a>
            <a href="<spring:url value="/admin/tsl-info"/>" class="list-group-item${currentPage eq '/admin/tsl-info' ? ' active' : ''}"><spring:message code="label.tsl" /></a>
            <a href="<spring:url value="/admin/admin-policy"/>" class="list-group-item${currentPage eq '/admin/policy' ? ' active' : ''}"><spring:message code="label.policy" /></a>

            <a href="<spring:url value="/admin/admin-crl"/>" class="list-group-item${currentPage eq '/admin/crl' ? ' active' : ''}"><spring:message code="label.crl" /></a>
            <a href="<spring:url value="/admin/admin-log-form"/>" class="list-group-item${currentPage eq '/admin/log-form' ? ' active' : ''}"><spring:message code="label.log" /></a>

        </c:when>
        <%-- NO ADMIN PART --%>
        <c:otherwise>
            <a href="<spring:url value="/signature" />" class="list-group-item${currentPage eq '/signature' ? ' active' : ''}"><spring:message code="label.signature.applet" /></a>
            <a href="<spring:url value="/extension" />" class="list-group-item${currentPage eq '/extension' ? ' active' : ''}"><spring:message code="label.extend" /></a>
            <a href="<spring:url value="/validation" />" class="list-group-item${currentPage eq '/validation' ? ' active' : ''}"><spring:message code="label.validate" /></a>
        </c:otherwise>
    </c:choose>
</div>

<div class="panel panel-default">
    <div class="panel-heading">Useful links</div>
    <div class="list-group hidden-print">
        <a href="https://joinup.ec.europa.eu/software/tlmanager/release/all" class="list-group-item"><spring:message code="label.tlmanager.tl.eu" /></a>
     </div>     
</div>