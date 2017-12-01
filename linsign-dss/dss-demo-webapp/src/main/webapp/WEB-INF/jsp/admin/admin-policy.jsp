
<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<script>
	
</script>


<h2>
	<spring:message code="label.policies" />
</h2>
<br>
You are on the adminitration console of signature policy
<br>
<strong>Signature Policy</strong>

<table class="table">
	<thead>
		<tr>
			<th><spring:message code="label.policy.oid" /></th>
			<th><spring:message code="label.policy.name" /></th>
			<th><spring:message code="label.policy.action" /></th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${policies}" var="policy">
			<tr>
				<td>${policy.oid}</td>
				<td>${policy.label}</td>
				<td>
					<button type="submit" class="btn btn-primary"
						onClick="window.location.href = '<c:url value="/admin/admin-policy-edit"/>'+ '?policyOID=' + '${policy.oid}'">Modify</button>
					<button type="submit" class="btn btn-primary"
						onClick="var answer = confirm('Do you want to delete this policy?'); 
                	if (answer) window.location = '<c:url value="/admin/admin-policy-delete"/>' + '?policyOID=' + '${policy.oid}'">Delete</button>
				</td>
			</tr>
		</c:forEach>
		<tr>
			<td><p></p></td>
			<td><p></p></td>

			<td>
				<button type="submit" class="btn btn-primary"
					onClick="window.location.href = '<c:url value="/admin/admin-policy-add"/>'">Add</button>
			</td>
		</tr>
	</tbody>

</table>