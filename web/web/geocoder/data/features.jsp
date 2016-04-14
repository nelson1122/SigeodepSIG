<%-- 
    Document   : features
    Created on : Apr 9, 2016, 8:18:59 PM
    Author     : nelson
--%>

<%@page import="org.json.JSONObject"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="managedBeans.geocoder.InjuriesCountMB"%>
<%
    InjuriesCountMB data = new InjuriesCountMB();
    JSONObject json =  data.loadGeoJSON();
    response.setContentType("application/json");
    response.setHeader("Content-Disposition", "inline");
    response.getWriter().write(json.toString());


%>
