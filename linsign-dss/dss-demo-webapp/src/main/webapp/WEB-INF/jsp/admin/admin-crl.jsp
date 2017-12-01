<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>


<script>

</script>


<h2>
	<spring:message code="label.crl.text" />
</h2>
<br>
You are on the adminitration console of CRL
<br>
<strong>Certificate Revoked Lists</strong>

<table class="table">
	<thead>
		<tr>
			<th><spring:message code="label.crl.AC" /></th>
			<th><spring:message code="label.crl.date.update" /></th>
			<th><spring:message code="label.crl.date.effective" /></th>
			<th><spring:message code="label.crl.certificate" /></th>
			<!-- ><th><spring:message code="label.crl.action" /></th>            -->
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${crls}" var="crl">
			<tr>
				<td>${crl.issuerX500Principal}</td>
				<td>${crl.thisUpdate}</td>
				<td>${crl.nextUpdate}</td>
				<td>${crl.revokedCertificates}</td>
				<!--   <td> 
                	<button type="button" class="btn btn-primary" onClick="alert('Up to date the list')">Update</button>
                	<button type="button" class="btn btn-primary" onClick="alert('Download CRL list')">Download</button>
                </td> -->
			</tr>
		</c:forEach>
	</tbody>

</table>