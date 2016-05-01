/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.geocoder;

import beans.connection.ConnectionJdbcMB;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author nelson
 */
@ManagedBean
@SessionScoped
public class addressSearchMB {

    private String searchedAddress = "";
    private String searchedNeighborhood = "";
    private List<String> neighborhoods = new ArrayList<>();
    private String address = "";
    private String neighborhood = "";
    private String commune = "";
    private String lon = "";
    private String lat = "";

    private ConnectionJdbcMB connectionJdbcMB;
    private String sql;

    /**
     * Creates a new instance of addressSearchMB
     */
    public addressSearchMB() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
        
    }
    
    @PostConstruct
    public void init(){
        loadNeighborhoods();
    }

    /*ROAD NETWORK CASE METHODS*/
    public void searchRoadNetworkAddress() {
        sql = ""
                + "WITH geocoded AS(\n"
                + "	SELECT\n"
                + "		geocode_address('" + searchedAddress + "') AS result\n"
                + ")\n"
                + "SELECT\n"
                + "	(result).address,\n"
                + "	(result).neighborhood,\n"
                + "	(result).commune,\n"
                + "	(result).lon,\n"
                + "	(result).lat\n"
                + "FROM\n"
                + "	geocoded\n"
                + "WHERE\n"
                + "	(result).lon IS NOT NULL AND (result).lat IS NOT NULL;";

        try {
            ResultSet rs = connectionJdbcMB.consult(sql);

            if (rs.next()) {
                address = rs.getString("address");
                neighborhood = rs.getString("neighborhood");
                commune = rs.getString("commune");
                lon = rs.getString("lon");
                lat = rs.getString("lat");
                JsfUtil.addSuccessMessage("Resultado\nDireccion: " + address + "\nBarrio: " + neighborhood + "\nComuna: " + commune);

            } else {
                JsfUtil.addErrorMessage("DIRECCION NO ENCONTRADA.");

            }

        } catch (SQLException e) {
            System.out.println("Error 1 en " + this.getClass().getName() + ":" + e.toString());
        }
    }

    /*NEIGHBORHOOD BLOCK HOUSEID CASE METHODS*/
    public void loadNeighborhoods() {
        String q = ""
                + "SELECT\n"
                + "	DISTINCT neighborhood_name\n"
                + "FROM\n"
                + "	geocoder.neighborhood_block_houseid_addresses\n"
                + "		NATURAL JOIN geocoder.neighborhoods\n"
                + "ORDER BY\n"
                + "	neighborhood_name;";

        try {
            ResultSet rs = connectionJdbcMB.consult(q);
            while (rs.next()) {
                neighborhoods.add(rs.getString("neighborhood_name"));
            }
        } catch (SQLException e) {

        }
    }

    public void searchNeighborhoodBlockHouseIdAddress() {
        JsfUtil.addSuccessMessage("Variables recibidas\n Direccion:"+ searchedAddress + " Barrio: " + searchedNeighborhood);
    }

    /*GETTERS AND SETTERS*/
    public String getSearchedAddress() {
        return searchedAddress;
    }

    public void setSearchedAddress(String searchedAddress) {
        this.searchedAddress = searchedAddress;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getSearchedNeighborhood() {
        return searchedNeighborhood;
    }

    public void setSearchedNeighborhood(String searchedNeighborhood) {
        this.searchedNeighborhood = searchedNeighborhood;
    }

    public List<String> getNeighborhoods() {
        return neighborhoods;
    }

    public void setNeighborhoods(List<String> neighborhoods) {
        this.neighborhoods = neighborhoods;
    }

}
