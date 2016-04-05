package managedBeans.geo2;

import beans.connection.ConnectionJdbcMB;
import beans.util.Variable;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import java.awt.Color;
import java.io.IOException;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;
import org.mapfish.geo.MfFeature;
import org.mapfish.geo.MfGeometry;
import org.primefaces.event.RowEditEvent;

/**
 *
 * The class GeoDBConnection is responsible of the connection to the database,
 * select the database manager system and create the url considering variables
 * as: indicator_id, user_id, vars and the cross of the variable rf.
 */
@ManagedBean(name = "geoDBConnectionMB")
@SessionScoped
public class GeoDBConnection implements Serializable {

    private ConnectionJdbcMB connectionJdbcMB;
    private String msj;
    private String url = "";
    String bd;
    String login;
    String table;
    public Connection conn;
    Statement st;
    ResultSet rs;
    private String geo_column;
    private int user_id;
    private int indicator_id;
    private String vars;
    private ArrayList<Variable> order;
    private RangeFactory rf;
    private Integer bins;
    private ArrayList<Range> ranges;
    private ArrayList<Double> numbers;
    private int gap;
    private int splitMethod;
    private Ramp selectedRamp;
    private ArrayList<Ramp> ramps;
    private Color startColor;
    private Color middleColor;
    private Color endColor;
    private boolean hasToRender;
    
    
    
    /**
     * This method is the constructor of the class, also establishes the
     * connection to the database and initiates the variables: bins, gap,
     * splitMethod, selectedRamp ,startColor ,middleColor , endColor , ramps,
     * numbers ,hasToRender .
     * 
     */
    public GeoDBConnection() {
        bins = 3;
        gap = bins;
        splitMethod = -1;
        this.selectedRamp = RampConverter.rampDB.get(0);
        this.startColor = Color.GREEN;
        this.middleColor = Color.YELLOW;
        this.endColor = Color.RED;
        ramps = RampConverter.rampDB;
        numbers = new ArrayList<>();
        hasToRender = false;
    }

    /**
     * This class is responsible for select the manager system of database and
     * allow the server connection .
     *
     * @param user: system user
     * @param pass: user password
     * @param host: server
     * @param name: database name
     */

    public void setConnection(Connection conn) {
        this.conn = conn;
    }

    public void createConnection(String user, String pass, String host, String name) {
        url = "jdbc:postgresql://" + host + "/" + name;
        if (conn == null) {
            try {
                Class.forName("org.postgresql.Driver").newInstance();// seleccionar SGBD
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                System.out.println("Error1: " + e.toString() + " --- Clase: " + this.getClass().getName());
            }
            try {
                // Realizar la conexion
                conn = DriverManager.getConnection(url, user, pass);// Realizar la conexion
            } catch (SQLException ex) {
                Logger.getLogger(MyFeatureCollection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @PreDestroy
    public void destroyConnection(){
        try {
            if(conn != null && !conn.isClosed()){
                conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(GeoDBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is responsible for creating the url, taking into account
     * varaibles as indicator_id, user_id, vars and the crossing of the rf
     * variable.
     */
    public void crearURL() {
        try {
            ExternalContext ext = FacesContext.getCurrentInstance().getExternalContext();
            String path = ((ServletContext) ext.getContext()).getContextPath();
            String cross = "";
            for (Variable var : order) {
                cross += var.getName() + ",";
            }
            ext.redirect(path + "/web/geo2/index.html?indicator_id=" + indicator_id + "&user_id=" + user_id + "&vars=" + cross + "&rf=" + rf.exportRanges());
        } catch (IOException ex) {
            Logger.getLogger(GeoDBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * This method allows refresh the indicator of data using the variables: *
     * user_id, indicator_id and order.
     *
     * @param user_id: user id
     * @param indicator_id: indicator id
     * @param order
     */
    public void refreshIndicatorData(int user_id, int indicator_id, ArrayList<Variable> order) {
        //createConnection(msj, vars, msj, table);
        this.setUser_id(user_id);
        this.setIndicator_id(indicator_id);
        this.order = order;
        String column_order = "column_";

        int order2 = 1;
        for (Variable column : order) {
            if (column.getName().equalsIgnoreCase("barrio")) {
                column_order += order2;
                this.setGeo_column(column_order);
                this.bins = 3;
                this.getTestNumbers();
                break;
            }
            if (column.getName().equalsIgnoreCase("comuna")) {
                column_order += order2;
                this.setGeo_column(column_order);
                this.bins = 3;
                this.getCommunesNumbers();
                break;
            }
            if (column.getName().equalsIgnoreCase("cuadrante")) {
                column_order += order2;
                this.setGeo_column(column_order);
                this.bins = 3;
                this.getQuadrantsNumbers();
                break;
            }
            if (column.getName().equalsIgnoreCase("corredor")) {
                column_order += order2;
                this.setGeo_column(column_order);
                this.bins = 3;
                this.getCorridorsNumbers();
                break;
            }
            order2++;
        }
        this.calculateGap();
        rf = new RangeFactory();
        rf.setNumbers(this.numbers);
        createRanges();
    }

    /**
     * This method is responsible for calculate difference of for that places
     * certain restrictions such as: if the gap> 10 then gap = 10 if the gap <1
     * then gap 1
     */
    public void calculateGap() {
        Set<Double> uniqueNumbers = new HashSet<>(numbers);
        gap = uniqueNumbers.size();
        if (gap > 10) {
            gap = 10;
        }
        if (gap < 1) {
            gap = 1;
        }
    }

    /**
     * This method is responsible for creating the range, also provides methods
     * of separation so you can create ranges.
     */
    public void createRanges() {
        if (this.rf != null) {
            System.out.println(numbers);
            System.out.println(getMaxNumberOfRanges());
            if (getMaxNumberOfRanges() < 3) {
                rf.setBins(getMaxNumberOfRanges());
            } else {
                rf.setBins(this.bins);
            }
            rf.setSplitMethod(splitMethod);
            rf.setSelectedRamp(selectedRamp);
            rf.createRanges();
            this.ranges = rf.getRanges();
        }
    }

    /**
     * This method allows established the maximum number of ranges
     *
     * @return
     */
    private int getMaxNumberOfRanges() {
        Set<Double> uniques = new HashSet<>(numbers);
        return uniques.size();
    }

    public void onEdit(RowEditEvent event) {
        System.out.println(((Range) event.getObject()).getLabel());
    }

    /**
     * This method is responsible for performing a query when exists a
     * connection in case otherwise sends a message that says no exists
     * connection to the database .
     *
     * @param query
     * @return
     */
    public ResultSet consult(String query) {
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

    /*
     * New methods for geo!!!
     */
    /**
     * This method is responsible for obtaining the polygons using a sql query
     * that uses the tables neighborhoods, communes, quadrants and corridors.
     *
     * @param rf
     * @return
     */
    public List<MfFeature> getPolygons(RangeFactory rf) {
        String query = "SELECT "
                + "         record_id, "
                + "         neighborhood_name, commune_name, "
                + "         quadrant_name, corridor_name, "
                + "         count, n.geom "
                + " FROM "
                + "         neighborhoods n "
                + " JOIN "
                + "         communes "
                + " ON "
                + "         (neighborhood_suburb = commune_id) "
                + " JOIN "
                + "         neighborhood_quadrant "
                + " USING "
                + "         (neighborhood_id) "
                + " JOIN "
                + "         quadrants  "
                + " USING "
                + "         (quadrant_id) "
                + " JOIN "
                + "         corridors "
                + " ON "
                + "         (neighborhood_corridor = corridor_id) "
                + " INNER JOIN "
                + "         (SELECT "
                + "                 min(record_id) AS record_id, " + geo_column + ", sum(count) AS count "
                + "         FROM "
                + "                 indicators_records "
                + "         WHERE "
                + "                 count > 0 "
                + "                 AND user_id = " + user_id + " "
                + "                 AND indicator_id = " + indicator_id + " "
                + "         GROUP BY "
                + "                 " + geo_column + " "
                + "         ORDER BY "
                + "                 2) AS foo "
                + " ON 	"
                + "      (" + geo_column + " LIKE neighborhood_name)  "
                + " WHERE "
                + "         n.geom IS NOT NULL "
                + " ORDER BY "
                + "         count DESC";
        WKTReader wktReader = new WKTReader();
        List<MfFeature> polygons = new ArrayList<>();
        System.out.println(query);
        ResultSet records = this.consult(query);
        try {
            while (records.next()) {
                final int fid = records.getInt("record_id");
                final String fname = records.getString("neighborhood_name");
                final String fsuburb = records.getString("commune_name");
                final String fquadrant = records.getString("quadrant_name");
                final String fcorridor = records.getString("corridor_name");
                final Double fvalue = new Double(records.getInt("count"));
                final String fcolour = "#" + rf.getColorByValue(fvalue);
                String geom = records.getString("geom");
                final MfGeometry fgeom = new MfGeometry(wktReader.read(geom));
                MfFeature feature = new MfFeature() {
                    String name = fname;
                    String suburb = fsuburb;
                    String quadrant = fquadrant;
                    String corridor = fcorridor;
                    private double value = fvalue;
                    private String colour = fcolour;
                    private Integer id = fid;

                    @Override
                    public String getFeatureId() {
                        return id.toString();
                    }

                    @Override
                    public MfGeometry getMfGeometry() {
                        return fgeom;
                    }

                    @Override
                    public void toJSON(JSONWriter writer) throws JSONException {
                        writer.key("name");
                        writer.value(name);
                        writer.key("suburb");
                        writer.value(suburb);
                        writer.key("quadrant");
                        writer.value(quadrant);
                        writer.key("corridor");
                        writer.value(corridor);
                        writer.key("value");
                        writer.value(value);
                        writer.key("colour");
                        writer.value(colour);
                        writer.key("id");
                        writer.value(id);
                    }
                };
                polygons.add(feature);
            }
            return polygons;
        } catch (ParseException | SQLException ex) {
            Logger.getLogger(GeoDBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * This method allows obtain the number of communes by sql query with which
     * counting is done communes.
     *
     * @return
     */
    public ArrayList<Double> getCommunesNumbers() {
        numbers = new ArrayList<>();
        String query = "SELECT "
                + "        count "
                + " FROM "
                + "         communes "
                + " INNER JOIN "
                + "         (SELECT "
                + "                 min(record_id) AS record_id, " + geo_column + ", sum(count) AS count "
                + "         FROM "
                + "                 indicators_records "
                + "         WHERE "
                + "                 count > 0 "
                + "                 AND user_id = " + user_id + " "
                + "                 AND indicator_id = " + indicator_id + " "
                + "         GROUP BY "
                + "                 " + geo_column + " "
                + "         ORDER BY "
                + "                 2) AS foo "
                + " ON 	"
                + "      (" + geo_column + " LIKE commune_name)  "
                + " WHERE "
                + "         geom IS NOT NULL "
                + " ORDER BY "
                + "         count";
        System.out.println(query);
        ResultSet records = this.consult(query);
        try {
            while (records.next()) {
                numbers.add(new Double(records.getString("count")));
            }
            return numbers;
        } catch (SQLException ex) {
            Logger.getLogger(GeoDBConnection.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * This method allows obtain the polygons of a commune using a query also
     * perform a count thereof .
     *
     * @param rf
     * @return
     */
    public List<MfFeature> getCommunesPolygons(RangeFactory rf) {
        String query = "SELECT "
                + "        record_id, commune_name, count, geom "
                + " FROM "
                + "         communes "
                + " INNER JOIN "
                + "         (SELECT "
                + "                 min(record_id) AS record_id, " + geo_column + ", sum(count) AS count "
                + "         FROM "
                + "                 indicators_records "
                + "         WHERE "
                + "                 count > 0 "
                + "                 AND user_id = " + user_id + " "
                + "                 AND indicator_id = " + indicator_id + " "
                + "         GROUP BY "
                + "                 " + geo_column + " "
                + "         ORDER BY "
                + "                 2) AS foo "
                + " ON 	"
                + "      (" + geo_column + " LIKE commune_name)  "
                + " WHERE "
                + "         geom IS NOT NULL "
                + " ORDER BY "
                + "         count DESC";
        WKTReader wktReader = new WKTReader();
        List<MfFeature> polygons = new ArrayList<>();
        ResultSet records = this.consult(query);
        try {
            while (records.next()) {
                final int fid = records.getInt("record_id");
                final String fname = records.getString("commune_name");
                final Double fvalue = new Double(records.getInt("count"));
                final String fcolour = "#" + rf.getColorByValue(fvalue);
                String geom = records.getString("geom");
                final MfGeometry fgeom = new MfGeometry(wktReader.read(geom));
                MfFeature feature = new MfFeature() {
                    String name = fname;
                    private double value = fvalue;
                    private String colour = fcolour;
                    private Integer id = fid;

                    @Override
                    public String getFeatureId() {
                        return id.toString();
                    }

                    @Override
                    public MfGeometry getMfGeometry() {
                        return fgeom;
                    }

                    @Override
                    public void toJSON(JSONWriter writer) throws JSONException {
                        writer.key("name");
                        writer.value(name);
                        writer.key("value");
                        writer.value(value);
                        writer.key("colour");
                        writer.value(colour);
                        writer.key("id");
                        writer.value(id);
                    }
                };
                polygons.add(feature);
            }
            return polygons;
        } catch (ParseException | SQLException ex) {
            Logger.getLogger(GeoDBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * allows obtain the number of tests using a query that is responsible for
     * perform a count of the neighborhoods table
     *
     * @return
     */
    public ArrayList<Double> getTestNumbers() {
        numbers = new ArrayList<>();
        String query = "SELECT "
                + "        count "
                + " FROM "
                + "         neighborhoods "
                + " INNER JOIN "
                + "         (SELECT "
                + "                 min(record_id) AS record_id, " + geo_column + ", sum(count) AS count "
                + "         FROM "
                + "                 indicators_records "
                + "         WHERE "
                + "                 count > 0 "
                + "                 AND user_id = " + user_id + " "
                + "                 AND indicator_id = " + indicator_id + " "
                + "         GROUP BY "
                + "                 " + geo_column + " "
                + "         ORDER BY "
                + "                 2) AS foo "
                + " ON 	"
                + "      (" + geo_column + " LIKE neighborhood_name)  "
                + " WHERE "
                + "         geom IS NOT NULL "
                + " ORDER BY "
                + "         count";
        ResultSet records = this.consult(query);
        System.out.println(query);
        try {
            while (records.next()) {
                numbers.add(new Double(records.getString("count")));
            }
            return numbers;
        } catch (SQLException ex) {
            Logger.getLogger(GeoDBConnection.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * This method allows obtain the polygons of a neighborhood through a sql
     * query
     *
     * @return
     */
    public List<MfFeature> getNeighborhoodPolygons() {
        String query = ""
                + "SELECT "
                + "	osm_id, name, the_geom "
                + "FROM "
                + "	barrios_900913";
        WKTReader wktReader = new WKTReader();
        List<MfFeature> polygons = new ArrayList<>();
        ResultSet records = this.consult(query);
        try {
            while (records.next()) {
                final int fid = records.getInt("osm_id");
                final String fname = records.getString("name");
                String geom = records.getString("the_geom");
                final MfGeometry fgeom = new MfGeometry(wktReader.read(geom));
                MfFeature feature = new MfFeature() {
                    String name = fname;
                    private Integer id = fid;

                    @Override
                    public String getFeatureId() {
                        return id.toString();
                    }

                    @Override
                    public MfGeometry getMfGeometry() {
                        return fgeom;
                    }

                    @Override
                    public void toJSON(JSONWriter writer) throws JSONException {
                        writer.key("name");
                        writer.value(name);
                        writer.key("id");
                        writer.value(id);
                    }
                };
                polygons.add(feature);
            }
            return polygons;
        } catch (ParseException | SQLException ex) {
            Logger.getLogger(GeoDBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * This method allows obtain the characteristics of polygons, in addition to
     * perform some validations that provide information if is a corridor,
     * commune or quadrant.
     *
     * @param f
     * @return
     */
    public List<MfFeature> getFeaturesPolygons(String f) {
        if (f.compareToIgnoreCase("comunas") == 0) {
            f = "commune";
        } else if (f.compareToIgnoreCase("corredores") == 0) {
            f = "corridor";
        } else if (f.compareToIgnoreCase("cuadrantes") == 0) {
            f = "quadrant";
        }
        String query = ""
                + "SELECT "
                + "	" + f + "_id AS id, " + f + "_name AS name, geom "
                + "FROM "
                + "	" + f + "s "
                + "WHERE "
                + "     geom IS NOT NULL";
        WKTReader wktReader = new WKTReader();
        List<MfFeature> polygons = new ArrayList<>();
        ResultSet records = this.consult(query);
        try {
            while (records.next()) {
                final int fid = records.getInt("id");
                final String fname = records.getString("name");
                String geom = records.getString("geom");
                final MfGeometry fgeom = new MfGeometry(wktReader.read(geom));
                MfFeature feature = new MfFeature() {
                    String name = fname;
                    private Integer id = fid;

                    @Override
                    public String getFeatureId() {
                        return id.toString();
                    }

                    @Override
                    public MfGeometry getMfGeometry() {
                        return fgeom;
                    }

                    @Override
                    public void toJSON(JSONWriter writer) throws JSONException {
                        writer.key("name");
                        writer.value(name);
                        writer.key("id");
                        writer.value(id);
                    }
                };
                polygons.add(feature);
            }
            return polygons;
        } catch (ParseException | SQLException ex) {
            Logger.getLogger(GeoDBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * This method allows obtain the number of quadrants by a sql query with
     * which the is performed the count of the quadrant.
     *
     * @return
     */
    public ArrayList<Double> getQuadrantsNumbers() {
        numbers = new ArrayList<>();
        String query = "SELECT "
                + "        count "
                + " FROM "
                + "         quadrants "
                + " INNER JOIN "
                + "         (SELECT "
                + "                 min(record_id) AS record_id, " + geo_column + ", sum(count) AS count "
                + "         FROM "
                + "                 indicators_records "
                + "         WHERE "
                + "                 count > 0 "
                + "                 AND user_id = " + user_id + " "
                + "                 AND indicator_id = " + indicator_id + " "
                + "         GROUP BY "
                + "                 " + geo_column + " "
                + "         ORDER BY "
                + "                 2) AS foo "
                + " ON 	"
                + "      (" + geo_column + " LIKE quadrant_name)  "
                + " WHERE "
                + "         geom IS NOT NULL "
                + " ORDER BY "
                + "         count";
        System.out.println(query);
        ResultSet records = this.consult(query);
        try {
            while (records.next()) {
                numbers.add(new Double(records.getString("count")));
            }
            return numbers;
        } catch (SQLException ex) {
            Logger.getLogger(GeoDBConnection.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * This method allows obtain the polygons of a quadrant using a sql query
     *
     * @param rf
     * @return
     */
    public List<MfFeature> getQuadrantsPolygons(RangeFactory rf) {
        String query = ""
                + " SELECT "
                + "     record_id, quadrant_name, count, q.geom, "
                + "     REPLACE(CAST(array_agg(neighborhood_name) AS TEXT),'\"','') AS neighborhoods "
                + " FROM "
                + "     quadrants  q"
                + " JOIN 	"
                + "     neighborhood_quadrant "
                + " USING	"
                + "     (quadrant_id) "
                + " JOIN	"
                + "     neighborhoods "
                + " USING	"
                + "     (neighborhood_id) "
                + " INNER JOIN "
                + "     (SELECT "
                + "             min(record_id) AS record_id, " + geo_column + ", sum(count) AS count "
                + "     FROM "
                + "             indicators_records "
                + "     WHERE "
                + "             count > 0 "
                + "             AND user_id = " + user_id + " "
                + "             AND indicator_id = " + indicator_id + " "
                + "     GROUP BY "
                + "             " + geo_column + " "
                + "     ORDER BY "
                + "             2) AS foo "
                + " ON 	"
                + "     (" + geo_column + " LIKE quadrant_name)  "
                + " WHERE "
                + "     q.geom IS NOT NULL "
                + " GROUP BY "
                + "     record_id, quadrant_name, count, q.geom "
                + " ORDER BY "
                + "     count DESC";
        System.out.println(query);
        WKTReader wktReader = new WKTReader();
        List<MfFeature> polygons = new ArrayList<>();
        ResultSet records = this.consult(query);
        try {
            while (records.next()) {
                final int fid = records.getInt("record_id");
                final String fname = records.getString("quadrant_name");
                final String fneighborhoods = records.getString("neighborhoods");
                final Double fvalue = new Double(records.getInt("count"));
                final String fcolour = "#" + rf.getColorByValue(fvalue);
                String geom = records.getString("geom");
                final MfGeometry fgeom = new MfGeometry(wktReader.read(geom));
                MfFeature feature = new MfFeature() {
                    String name = fname;
                    String neighborhoods = fneighborhoods;
                    private double value = fvalue;
                    private String colour = fcolour;
                    private Integer id = fid;

                    @Override
                    public String getFeatureId() {
                        return id.toString();
                    }

                    @Override
                    public MfGeometry getMfGeometry() {
                        return fgeom;
                    }

                    @Override
                    public void toJSON(JSONWriter writer) throws JSONException {
                        writer.key("name");
                        writer.value(name);
                        writer.key("neighborhoods");
                        writer.value(neighborhoods);
                        writer.key("value");
                        writer.value(value);
                        writer.key("colour");
                        writer.value(colour);
                        writer.key("id");
                        writer.value(id);
                    }
                };
                polygons.add(feature);
            }
            return polygons;
        } catch (ParseException | SQLException ex) {
            Logger.getLogger(GeoDBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * This method allows obtain the number of corridors using a sql query with
     * which performed corridors count .
     *
     * @return
     */
    public ArrayList<Double> getCorridorsNumbers() {
        numbers = new ArrayList<>();
        String query = "SELECT "
                + "        count "
                + " FROM "
                + "         corridors "
                + " INNER JOIN "
                + "         (SELECT "
                + "                 min(record_id) AS record_id, " + geo_column + ", sum(count) AS count "
                + "         FROM "
                + "                 indicators_records "
                + "         WHERE "
                + "                 count > 0 "
                + "                 AND user_id = " + user_id + " "
                + "                 AND indicator_id = " + indicator_id + " "
                + "         GROUP BY "
                + "                 " + geo_column + " "
                + "         ORDER BY "
                + "                 2) AS foo "
                + " ON 	"
                + "      (" + geo_column + " LIKE corridor_name)  "
                + " WHERE "
                + "         geom IS NOT NULL "
                + " ORDER BY "
                + "         count";
        System.out.println(query);
        ResultSet records = this.consult(query);
        try {
            while (records.next()) {
                numbers.add(new Double(records.getString("count")));
            }
            return numbers;
        } catch (SQLException ex) {
            Logger.getLogger(GeoDBConnection.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * This method allows obtain the polygons of a corridor using a sql query
     *
     * @param rf
     * @return
     */
    public List<MfFeature> getCorridorsPolygons(RangeFactory rf) {
        String query = "SELECT "
                + "        record_id, corridor_name, count, geom "
                + " FROM "
                + "         corridors "
                + " INNER JOIN "
                + "         (SELECT "
                + "                 min(record_id) AS record_id, " + geo_column + ", sum(count) AS count "
                + "         FROM "
                + "                 indicators_records "
                + "         WHERE "
                + "                 count > 0 "
                + "                 AND user_id = " + user_id + " "
                + "                 AND indicator_id = " + indicator_id + " "
                + "         GROUP BY "
                + "                 " + geo_column + " "
                + "         ORDER BY "
                + "                 2) AS foo "
                + " ON 	"
                + "      (" + geo_column + " LIKE corridor_name)  "
                + " WHERE "
                + "         geom IS NOT NULL "
                + " ORDER BY "
                + "         count DESC";
        WKTReader wktReader = new WKTReader();
        List<MfFeature> polygons = new ArrayList<>();
        ResultSet records = this.consult(query);
        try {
            while (records.next()) {
                final int fid = records.getInt("record_id");
                final String fname = records.getString("corridor_name");
                final Double fvalue = new Double(records.getInt("count"));
                final String fcolour = "#" + rf.getColorByValue(fvalue);
                String geom = records.getString("geom");
                final MfGeometry fgeom = new MfGeometry(wktReader.read(geom));
                MfFeature feature = new MfFeature() {
                    String name = fname;
                    private double value = fvalue;
                    private String colour = fcolour;
                    private Integer id = fid;

                    @Override
                    public String getFeatureId() {
                        return id.toString();
                    }

                    @Override
                    public MfGeometry getMfGeometry() {
                        return fgeom;
                    }

                    @Override
                    public void toJSON(JSONWriter writer) throws JSONException {
                        writer.key("name");
                        writer.value(name);
                        writer.key("value");
                        writer.value(value);
                        writer.key("colour");
                        writer.value(colour);
                        writer.key("id");
                        writer.value(id);
                    }
                };
                polygons.add(feature);
            }
            return polygons;
        } catch (ParseException | SQLException ex) {
            Logger.getLogger(GeoDBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * This method allows establish which are for perform a analysis of the
     * diagram of cake, also Retrieves information using a sql query to the
     * indicators_records table.
     *
     * @param WHERE
     * @param geo_column
     * @param column
     * @param user_id
     * @param indicator_id
     * @return
     */
    public String getPieData(String WHERE, String geo_column, String column, int user_id, int indicator_id) {
        String query = "SELECT "
                + "	min(record_id) AS id, " + column + " AS label, sum(count) AS count "
                + "FROM "
                + "	indicators_records "
                + "WHERE "
                + "	" + geo_column + " IN " + WHERE + " "
                + "     AND user_id = " + user_id + " "
                + "     AND indicator_id = " + indicator_id + ""
                + "GROUP BY "
                + "	" + column + " "
                + "ORDER BY "
                + "	1";
        ResultSet records = this.consult(query);
        try {
            JSONObject obj = new JSONObject();
            obj.put("title", "a name for this data");
            JSONArray values = new JSONArray();
            JSONArray labels = new JSONArray();
            while (records.next()) {
                String label = records.getString("label");
                int count = records.getInt("count");
                if (count != 0) {
                    labels.put(label + " %% [###]");
                    values.put(count);
                }
            }
            obj.put("labels", labels);
            obj.put("values", values);
            return obj.toString();
        } catch (JSONException | SQLException ex) {
            Logger.getLogger(GeoDBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return "EPIC FAIL!!!";
        }
    }

    /**
     * This method is responsible for obtaining the name of the map by using the
     * variable indicator_id by a sql query that contains the indicator_name
     * which will be assigned as the title this.
     *
     * @param indicator_id
     * @return
     */
    public String getMapName(int indicator_id) {
        try {
            String query = "SELECT indicator_name FROM indicators WHERE indicator_id=" + indicator_id;
            ResultSet records = this.consult(query);
            JSONObject obj = new JSONObject();
            while (records.next()) {
                String map_name = records.getString("indicator_name");
                obj.put("title", map_name);
            }
            return obj.toString();
        } catch (JSONException | SQLException ex) {
            Logger.getLogger(GeoDBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    /**
     * @param user_id the user_id to set
     */
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    /**
     * @param indicator_id the indicator_id to set
     */
    public void setIndicator_id(int indicator_id) {
        this.indicator_id = indicator_id;
    }

    /**
     * @param vars the vars to set
     */
    public void setVars(String vars) {
        this.vars = vars;
    }

    public String getGeo_column() {
        return geo_column;
    }

    public void setGeo_column(String geo_column) {
        this.geo_column = geo_column;
    }

    public RangeFactory getRf() {
        return rf;
    }

    public void setRf(RangeFactory rf) {
        this.rf = rf;
    }

    public ArrayList<Range> getRanges() {
        return ranges;
    }

    public void setRanges(ArrayList<Range> ranges) {
        this.ranges = ranges;
    }

    public Integer getBins() {
        return bins;
    }

    public void setBins(Integer bins) {
        this.bins = bins;
    }

    public int getGap() {
        return gap;
    }

    public void setGap(int gap) {
        this.gap = gap;
    }

    public int getSplitMethod() {
        return splitMethod;
    }

    public void setSplitMethod(int splitMethod) {
        this.splitMethod = splitMethod;
        rf.setSplitMethod(splitMethod);
    }

    public Ramp getSelectedRamp() {
        return selectedRamp;
    }

    public void setSelectedRamp(Ramp selectedRamp) {
        this.selectedRamp = selectedRamp;
    }

    public ArrayList<Ramp> getRamps() {
        return ramps;
    }

    public void setRamps(ArrayList<Ramp> ramps) {
        this.ramps = ramps;
    }

    public Color getStartColor() {
        return startColor;
    }

    public void setStartColor(Color startColor) {
        this.startColor = startColor;
    }

    public Color getMiddleColor() {
        return middleColor;
    }

    public void setMiddleColor(Color middleColor) {
        this.middleColor = middleColor;
    }

    public Color getEndColor() {
        return endColor;
    }

    public void setEndColor(Color endColor) {
        this.endColor = endColor;
    }

    public ArrayList<Double> getNumbers() {
        return numbers;
    }

    public void setNumbers(ArrayList<Double> numbers) {
        this.numbers = numbers;
    }

    public boolean isHasToRender() {
        return hasToRender;
    }

    public void setHasToRender(boolean hasToRender) {
        this.hasToRender = hasToRender;
    }
}
