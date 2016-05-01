<%-- 
    Document   : features
    Created on : Apr 27, 2016, 10:33:55 PM
    Author     : nelson
--%>

<%@page import="managedBeans.geocoder.GeoDBConnection"%>
<%@ page contentType="application/json" %>
<%
    // Instancio un objeto de mi clase
    
    HttpSession sessionOk = request.getSession();
    String db_user = sessionOk.getAttribute("db_user").toString();
    String db_pass = sessionOk.getAttribute("db_pass").toString();
    String db_host = sessionOk.getAttribute("db_host").toString();
    String db_name = sessionOk.getAttribute("db_name").toString();
    
    GeoDBConnection connection = new GeoDBConnection(db_host, db_name, db_user, db_pass);
    connection.createConnection();
    // Configuro los header de la página como JSON
    response.setContentType("application/json");
    response.setHeader("Content-Disposition", "inline");
    // Escribo el texto del GeoJSON que construye mi clase
    response.getWriter().write(geojson.getGeoJSON());
%>