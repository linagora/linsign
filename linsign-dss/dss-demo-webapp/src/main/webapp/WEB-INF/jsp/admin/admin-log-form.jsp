<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>


 <script>
     $(function () {
         $("#datepickerbefore").datepicker();
     });
 </script>
   <script>
     $(function () {
         $("#datepickerafter").datepicker();
     });
 </script>

<h2><spring:message code="label.log.text"/></h2><br>
You are on the adminitration console of Logs Administration <br>
<strong>Operation Logs</strong>

<form action = "admin-log" method = "POST">
                
	<input type = "checkbox" name = "succes" id="succes" /> SUCCES
	<input type = "checkbox" name = "error"  id="error" /> ERROR
	<input type = "checkbox" name = "infos" id="infos" /> INFOS
	<br />
	Issuer: <input type = "text" name = "issuer" id="issuer" />
	<br />
	Certificat: <input type = "text" name = "certificat" id="certificat" />
	<br />
	Start Date:  <input type="text" name="dateafter" id="datepickerafter"> 
	<input type = "text" name = "hoursafter" id="hoursafter" placeholder="hh:mm"/>
	<br />
	End Date: <input type="text" name="datebefore" id="datepickerbefore"> 
	<input type = "text" name = "hoursbefore" id="hoursbefore" placeholder="hh:mm"/>
	<br />

	<br />
	<input type = "submit" value = "Filter Logs" />
	
</form>