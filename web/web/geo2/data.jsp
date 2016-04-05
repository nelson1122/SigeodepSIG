<%-- 
    Document   : data
    Created on : Jun 6, 2012, 6:41:46 PM
    Author     : and
--%>
<%@page import="managedBeans.geo2.RangeFactory"%>
<%@page import="managedBeans.geo2.MyFeatureCollection"%>
<%-- Set the content type header with the JSP directive --%>
<%@ page contentType="application/json" %>

<%-- Set the content disposition header --%>
<%
    int indicator_id = Integer.parseInt(request.getParameter("indicator_id"));
    int user_id = Integer.parseInt(request.getParameter("user_id"));
    String vars = request.getParameter("vars");
    RangeFactory rf = new RangeFactory(request.getParameter("rf"));
    
    HttpSession sessionOk = request.getSession();
    String db_user = sessionOk.getAttribute("db_user").toString();
    String db_pass = sessionOk.getAttribute("db_pass").toString();
    String db_host = sessionOk.getAttribute("db_host").toString();
    String db_name = sessionOk.getAttribute("db_name").toString();
    
    MyFeatureCollection f = new MyFeatureCollection(indicator_id, user_id, vars, rf);
    f.setConnection(db_user, db_pass, db_host, db_name);
    String toJson = f.getGeoJSON().toString();
    response.setContentType("application/json");
    response.setHeader("Content-Disposition", "inline");
    response.getWriter().write(toJson);
%>