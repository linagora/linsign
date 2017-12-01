<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<h2><spring:message code="label.policies"/></h2>
You are on the adminitration console of signature policy<br>
<strong>Modify the signature policy</strong>

<script type="text/javascript">
 function testOID() {
   elem = document.getElementById("oidInput");
   var policiesJson = '${policiesJson}';
   var obj = JSON.parse(policiesJson); 	
   var check = false;
   for (var i = 0; i<obj.length; i++)
	   {
	    if (elem.value == obj[i].oid) check=true;
	   }
	return check;	

 }
</script>


<form:form method="post" commandName="policyFormAdd" action="add" cssClass="form-horizontal">
    <div class="form-group">
		<table class="table">
		    <thead>
		        <tr>
		            <th>Fields</th>
		            <th>Value</th>		
		        </tr>        
		    </thead>
		        <tbody>        
		            <tr>
		                <td>OID</td>
						<td>				         
								<form:input id="oidInput" path="oid" cssClass="form-control" disabled="false" onchange="if (testOID() == true) {alert('Duplicate OID, please choice another'); document.getElementById('oidInput').setfocus}"/>
						</td>
		            </tr>      
		            <tr>
		                <td>Name</td>
		                <td><form:input path="label" cssClass="form-control" /></td>
		            </tr>      
		            <tr>
		                <td>Format</td>
		                <td>
		                	<form:select path="signature.format" cssClass="form-control">
		                		<form:option value = "CAdES"/>
		                		<form:option value = "PAdES"/>
		                		<form:option value = "XAdES"/>
		                	</form:select>		   
		                </td>             
		            </tr>
		            <tr>
		                <td>Packaging</td>
		                <td>
		                	<form:select path="signature.packaging" cssClass="form-control">
		                		<form:option value = "ENVELOPPING"/>
		                		<form:option value = "ENVELOPPED"/>
		                		<form:option value = "DETACHED"/>
		                	</form:select>
		                </td>
		            </tr>
		            <tr>
		                <td>Level</td>
		                <td>
		                	<form:select path="signature.level" cssClass="form-control">
		                		<form:option value = "B"/>
		                		<form:option value = "T"/>
		                		<form:option value = "LT"/>
		                		<form:option value = "LTA"/>
		                	</form:select>
		                </td>
		            </tr>
		            <tr>
		                <td><button type="submit" class="btn btn-primary"><spring:message code="label.update"/></button></td>
		                <td><button type="button" name="Cancel"  class="btn btn-primary" onClick="window.location.href = '<c:url value="/admin/admin-policy"/>'">Cancel</button></td>
		            </tr>      		                      		            
		    </tbody>		    
		</table>        
    </div>

</form:form>
