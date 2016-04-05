<%-- 
    Document   : data
    Created on : Jun 6, 2012, 6:41:46 PM
    Author     : and
--%>
<%@page import="managedBeans.geo2.MyFeatureCollection"%>
<%-- Set the content type header with the JSP directive --%>
<%@ page contentType="application/json" %>

<%-- Set the content disposition header --%>
<%
    String features = request.getParameter("features");
    HttpSession sessionOk = request.getSession();
    String db_user = sessionOk.getAttribute("db_user").toString();
    String db_pass = sessionOk.getAttribute("db_pass").toString();
    String db_host = sessionOk.getAttribute("db_host").toString();
    String db_name = sessionOk.getAttribute("db_name").toString();
    
    MyFeatureCollection f = new MyFeatureCollection(db_user, db_pass, db_host, db_name);
    String toJson = f.getFeaturesGeoJSON(features).toString();
    response.setContentType("application/json");
    response.setHeader("Content-Disposition", "inline");
    response.getWriter().write(toJson);
%>