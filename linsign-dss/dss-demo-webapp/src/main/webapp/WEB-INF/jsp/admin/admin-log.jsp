<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>


<script>

</script>

<h2><spring:message code="label.log.text"/></h2><br>
You are on the adminitration console of Logs Administration <br>
<strong>Operation Logs</strong>

<table class="table">
    <thead>
        <tr>
            <th><spring:message code="label.log.date" /></th>
            <th><spring:message code="label.log.status" /></th>
            <th><spring:message code="label.log.operation" /></th>
            <th><spring:message code="label.log.user" /></th>
            <th><spring:message code="label.log.info" /></th>            
        </tr>
    </thead>

    <tbody>
         <c:forEach items="${logs}" var="log">
            <tr>
                <td>${log.date}</td>
                <td>${log.status}</td>
                <td>${log.operation}</td>
                <td>${log.user}</td>
                <td>${log.info}</td>                                                
            </tr>      
          </c:forEach>          
    </tbody>
    
    	<%--For displaying Previous link except for the 1st page --%>
	<c:if test="${currentPage != 0}">
		<td><a href="admin-log?page=${currentPage - 1}" method="get">Previous</a></td>
	</c:if>

	<%--For displaying Page numbers. 
	The when condition does not display a link for the current page--%>
	<table border="1" cellpadding="5" cellspacing="5">
		<tr>
			<c:forEach begin="1" end="${noOfPages}" var="i">
				<c:choose>
					<c:when test="${currentPage eq i}">
						<td>${i}</td>
					</c:when>
					<c:otherwise>
						<td><a href="admin-log?page=${i}" method="get">${i}</a>
				            <a href="<spring:url value="/admin/admin-log?page=4"/>"> -- ${i}</a>
						</td>
						
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</tr>
	</table>
	
	<%--For displaying Next link --%>
	<c:if test="${currentPage lt noOfPages-1}">
		<td><a href="admin-log?page=${currentPage + 1}" method="get">Next</a></td>
	</c:if>
    
</table>