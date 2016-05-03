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

    private String searchedRNAddress = "";
    private String searchedNBHAddress = "";
    private String searchedNBHNeighborhood = "";

    private String resultAddress = "";
    private String resultNeighborhood = "";
    private String resultCommune = "";
    private double resultLongitude = 0;
    private double resultLatitude = 0;

    private List<String> neighborhoods = new ArrayList<>();

    private final ConnectionJdbcMB connectionJdbcMB;
    private String sql;

    /**
     * Creates a new instance of addressSearchMB
     */
    public addressSearchMB() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
    }

    @PostConstruct
    public void init() {
        resultAddress = "";
        resultNeighborhood = "";
        resultCommune = "";
        resultLongitude = 0;
        resultLatitude = 0;
        loadNeighborhoods();
    }

    /*ROAD NETWORK CASE METHODS*/
    public void roadNetworkAddressSearch() {
        sql = ""
                + "WITH geocoded AS(\n"
                + "	SELECT\n"
                + "		geocode_address('" + searchedRNAddress + "') AS result\n"
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
                resultAddress = rs.getString("address");
                resultNeighborhood = rs.getString("neighborhood");
                resultCommune = rs.getString("commune");
                resultLongitude = rs.getDouble("lon");
                resultLatitude = rs.getDouble("lat");
                JsfUtil.addSuccessMessage("Dirección encontrada");

            } else {
                resultLongitude = 0;
                resultLatitude = 0;
                JsfUtil.addErrorMessage("Direccion no encontrada.");
            }

        } catch (SQLException e) {
            System.out.println("Error 1 en " + this.getClass().getName() + ":" + e.toString());
        }
    }

    /*NEIGHBORHOOD BLOCK HOUSEID CASE METHODS*/
    public void neighborhoodblockhouseidAddressSearch() {
        sql = ""
                + "WITH query AS (\n"
                + "	SELECT \n"
                + "		geocode_address('" + searchedNBHNeighborhood + "','" + searchedNBHAddress + "') AS result\n"
                + ")\n"
                + "SELECT\n"
                + "	(result).address,\n"
                + "	(result).neighborhood,\n"
                + "	(result).commune,\n"
                + "	(result).lon,\n"
                + "	(result).lat\n"
                + "FROM\n"
                + "	query\n"
                + "WHERE\n"
                + "	(result).lon IS NOT NULL AND (result).lat IS NOT NULL;";

        try {
            ResultSet rs = connectionJdbcMB.consult(sql);

            if (rs.next()) {
                resultAddress = rs.getString("address");
                resultNeighborhood = rs.getString("neighborhood");
                resultCommune = rs.getString("commune");
                resultLongitude = rs.getDouble("lon");
                resultLatitude = rs.getDouble("lat");
                JsfUtil.addSuccessMessage("Dirección encontrada");
            } else {
                resultLongitude = 0;
                resultLatitude = 0;
                JsfUtil.addErrorMessage("Dirección no encontrada");
            }

        } catch (SQLException e) {
            System.out.println("Error 2 en " + this.getClass().getName() + ":" + e.toString());
        }
    }

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
            System.out.println("Error 3 en " + this.getClass().getName() + ":" + e.toString());
        }
    }

    /*GETTERS AND SETTERS*/
    public String getSearchedRNAddress() {
        return searchedRNAddress;
    }

    public void setSearchedRNAddress(String searchedRNAddress) {
        this.searchedRNAddress = searchedRNAddress;
    }

    public String getSearchedNBHAddress() {
        return searchedNBHAddress;
    }

    public void setSearchedNBHAddress(String searchedNBHAddress) {
        this.searchedNBHAddress = searchedNBHAddress;
    }

    public String getSearchedNBHNeighborhood() {
        return searchedNBHNeighborhood;
    }

    public void setSearchedNBHNeighborhood(String searchedNBHNeighborhood) {
        this.searchedNBHNeighborhood = searchedNBHNeighborhood;
    }

    public String getResultAddress() {
        return resultAddress;
    }

    public void setResultAddress(String resultAddress) {
        this.resultAddress = resultAddress;
    }

    public String getResultNeighborhood() {
        return resultNeighborhood;
    }

    public void setResultNeighborhood(String resultNeighborhood) {
        this.resultNeighborhood = resultNeighborhood;
    }

    public String getResultCommune() {
        return resultCommune;
    }

    public void setResultCommune(String resultCommune) {
        this.resultCommune = resultCommune;
    }

    public double getResultLongitude() {
        return resultLongitude;
    }

    public void setResultLongitude(double resultLongitude) {
        this.resultLongitude = resultLongitude;
    }

    public double getResultLatitude() {
        return resultLatitude;
    }

    public void setResultLatitude(double resultLatitude) {
        this.resultLatitude = resultLatitude;
    }

    public List<String> getNeighborhoods() {
        return neighborhoods;
    }

    public void setNeighborhoods(List<String> neighborhoods) {
        this.neighborhoods = neighborhoods;
    }

}
