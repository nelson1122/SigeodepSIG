/*
 * Esta clase se encarga de la conexión a la base de datos
 */
package managedBeans.geocoder;

import beans.connection.ConnectionJdbcMB;
import java.io.Serializable;
import java.sql.*;
import javax.faces.context.FacesContext;
import managedBeans.login.LoginMB;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GeoDBConnection implements Serializable {

    private String host;
    private String dbname;
    private String user;
    private String passwd;
    private Connection conn;
    private Statement st;
    private ResultSet rs;
    private String msj;
    private String url;
    private JSONObject injuriesRoot;
    
    private String currentUserId;
    private String currentIndicatorId;
    private String currentInjuryType;
    
    private String sourceGeocoderTable;
    private String joinField;

    public GeoDBConnection(String host, String dbname, String user, String passwd) {
        this.host = host;
        this.dbname = dbname;
        this.user = user;
        this.passwd = passwd;
    }
    
    public void setSession(String userId, String indicatorId, String injuryType){
        this.currentUserId = userId;
        this.currentIndicatorId = indicatorId;
        this.currentInjuryType = injuryType;
    }

    public void createConnection() {
        url = "jdbc:postgresql://" + host + "/" + dbname;
        try {
            Class.forName("org.postgresql.Driver").newInstance();
            conn = DriverManager.getConnection(url, user, passwd);
            if (conn != null) {
                System.out.println("Conexion a base de datos " + url + " ... OK");
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException e) {
            System.out.println("Error: " + e.toString() + " --- Clase: " + this.getClass().getName());
        }
    }

    public void disconnect() {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                    System.out.println("Se cerro conexion a: " + url + " ... OK");
                    msj = "Close conection " + url + " ... OK";
                }
            } catch (SQLException e) {
                System.out.println("Error: " + e.toString() + " --- Clase: " + this.getClass().getName());
                msj = "ERROR: " + e.getMessage();
            }
        }
    }

    public ResultSet runQuery(String query) {
        msj = "";
        try {
            if (conn != null) {
                st = conn.createStatement();
                rs = st.executeQuery(query);
                return rs;
            } else {
                msj = "There don't exist connection";
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.toString() + " --- Clase: " + this.getClass().getName());
            msj = "ERROR: " + e.getMessage();
            return null;
        }
    }

    public int runNonQuery(String query) {
        msj = "";
        PreparedStatement stmt;
        int reg;
        reg = 0;
        try {
            if (conn != null) {
                stmt = conn.prepareStatement(query);
                reg = stmt.executeUpdate();
                stmt.close();
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.toString() + " --- Clase: " + this.getClass().getName());
            msj = "ERROR: " + e.getMessage();
        }
        return reg;
    }

    public String getGeoJSON() {
        
        if (currentInjuryType.compareTo("fatal_injuries") == 0) {
            sourceGeocoderTable = "geocoded_fatal_injuries";
            joinField = "fatal_injury_id";
        } else {
            sourceGeocoderTable = "geocoded_non_fatal_injuries";
            joinField = "non_fatal_injury_id";
        }
        
        try {
            JSONArray featuresArray = new JSONArray();
            int processedTuples = 0;
            
            String sql = ""
                    + "SELECT\n"
                    + "	injury_id,\n"
                    + "	lon,\n"
                    + "	lat\n"
                    + "FROM\n"
                    + "	indicators_addresses \n"
                    + "		JOIN " + sourceGeocoderTable + " ON injury_id = " + joinField + "\n"
                    + "WHERE\n"
                    + "	user_id = " + currentUserId + " AND\n"
                    + "	indicator_id = " + (currentIndicatorId + 100) + " AND\n"
                    + "	(lon IS NOT NULL AND lat IS NOT NULL);";
            
            ResultSet rs = runQuery(sql);

            if (rs != null) {
                do {
                    JSONArray coordinates = new JSONArray();
                    coordinates.put(0, rs.getDouble("lon"));
                    coordinates.put(1, rs.getDouble("lat"));

                    JSONObject feature = new JSONObject();
                    feature.put("type", "Point");
                    feature.put("coordinates", coordinates);

                    JSONObject properties = new JSONObject();
                    properties.put("fatal_injury_id", rs.getInt("injury_id"));

                    JSONObject geometry = new JSONObject();
                    geometry.put("type", "Feature");
                    geometry.put("geometry", feature);
                    geometry.put("properties", properties);

                    featuresArray.put(processedTuples, geometry);

                    processedTuples++;
                } while (rs.next());
            }

            injuriesRoot.put("features", featuresArray);
            injuriesRoot.put("type", "FeatureCollection");
        } catch (SQLException | JSONException e) {
            System.out.println("Error Geocoder 2 en " + this.getClass().getName() + ":" + e.toString());
        }

        return injuriesRoot.toString();
    }
}
