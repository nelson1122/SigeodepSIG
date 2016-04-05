/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.geo2;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONWriter;
import org.mapfish.geo.MfFeature;
import org.mapfish.geo.MfFeatureCollection;
import org.mapfish.geo.MfGeoJSONWriter;

/**
 * The class MyFeatureCollection is responsible to obtain the appropriate values
 * to establish the geometry of the polygons of neighborhood, corridor,commune
 * and quadrant, the values that have the polygon
 *
 * @author and
 */
public class MyFeatureCollection {

    Writer w;
    int max;
    private GeoDBConnection geo;
    private int indicator_id;
    private int user_id;
    private String vars;
    private RangeFactory rf;
    private ArrayList<Range> ranges;
    private String user;
    private String pass;
    private String host;
    private String name;

    /**
     * This method is the constructor of the class, also is responsible for
     * connecting to the database.
     *
     * @param user
     * @param pass
     * @param host
     * @param name
     */
    public MyFeatureCollection(String user, String pass, String host, String name) {
        this.user = user;
        this.pass = pass;
        this.host = host;
        this.name = name;
        this.geo = new GeoDBConnection();
    }

    /**
     * This method is responsible for passing the parameters obtained to
     * variable instantiated in this class.
     *
     * @param indicator_id
     * @param user_id
     * @param vars
     * @param rf
     */
    public MyFeatureCollection(int indicator_id, int user_id, String vars, RangeFactory rf) {
        this.indicator_id = indicator_id;
        this.user_id = user_id;
        this.vars = vars;
        this.rf = rf;
        this.geo = new GeoDBConnection();
    }

    public void setConnection(String user, String pass, String host, String name) {
        this.user = user;
        this.pass = pass;
        this.host = host;
        this.name = name;
    }

    /**
     * This method is responsible for obtaining the appropriate values to
     * establish the geometry of the polygon neighborhood, commune, quadrant and
     * corridor
     *
     * @return
     */
    public Writer getGeoJSON() {
        try {
            //GeoDBConnection geo = new GeoDBConnection();
            //geo = (GeoDBConnection) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{geoDBConnectionMB}", GeoDBConnection.class);

            this.geo.createConnection(user, pass, host, name);
            
            geo.setUser_id(user_id);
            geo.setIndicator_id(indicator_id);
            geo.setVars(vars);

            String[] columns = vars.split(",");
            int order = 1;
            String column_order = "column_";
            Collection<MfFeature> polygons = null;
            for (String column : columns) {
                if (column.equalsIgnoreCase("barrio")) {
                    column_order += order;
                    geo.setGeo_column(column_order);
                    polygons = geo.getPolygons(rf);
                    break;
                }
                if (column.equalsIgnoreCase("comuna")) {
                    column_order += order;
                    geo.setGeo_column(column_order);
                    polygons = geo.getCommunesPolygons(rf);
                    break;
                }
                if (column.equalsIgnoreCase("cuadrante")) {
                    column_order += order;
                    geo.setGeo_column(column_order);
                    polygons = geo.getQuadrantsPolygons(rf);
                    break;
                }
                if (column.equalsIgnoreCase("corredor")) {
                    column_order += order;
                    geo.setGeo_column(column_order);
                    polygons = geo.getCorridorsPolygons(rf);
                    break;
                }
                order++;
            }
            MfFeatureCollection collection = new MfFeatureCollection(polygons);
            w = new StringWriter();
            JSONWriter writer = new JSONWriter(w);
            MfGeoJSONWriter gjw = new MfGeoJSONWriter(writer);
            gjw.encodeFeatureCollection(collection, 0, 0.0, 0.0);
            
            geo.destroyConnection();
        } catch (JSONException ex) {
            Logger.getLogger(MyFeatureCollection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return w;
    }

    /**
     * This method allows to obtain the values for establish the structure that
     * will have the polygon of a neighborhood.
     *
     * @return
     */
    public Writer getNeighborhoodGeoJSON() {
        try {
            geo.createConnection(user, pass, host, name);
            
            Collection<MfFeature> polygons = geo.getNeighborhoodPolygons();
            MfFeatureCollection collection = new MfFeatureCollection(polygons);
            w = new StringWriter();
            JSONWriter writer = new JSONWriter(w);
            MfGeoJSONWriter gjw = new MfGeoJSONWriter(writer);
            gjw.encodeFeatureCollection(collection, 0, 0.0, 0.0);
            
            geo.destroyConnection();
        } catch (JSONException ex) {
            Logger.getLogger(MyFeatureCollection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return w;
    }

    /**
     * This method will take care of getting the features that must have the
     * polygons according to the structure GeoJSON is a format for encoding a
     * variety of geographic data structures.
     *
     * @param features
     * @return
     */
    public Writer getFeaturesGeoJSON(String features) {
        try {
            geo.createConnection(user, pass, host, name);
            Collection<MfFeature> polygons = geo.getFeaturesPolygons(features);
            MfFeatureCollection collection = new MfFeatureCollection(polygons);
            w = new StringWriter();
            JSONWriter writer = new JSONWriter(w);
            MfGeoJSONWriter gjw = new MfGeoJSONWriter(writer);
            gjw.encodeFeatureCollection(collection, 0, 0.0, 0.0);
            geo.destroyConnection();
        } catch (JSONException ex) {
            Logger.getLogger(MyFeatureCollection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return w;
    }
    /**
     * This method is responsible for obtain the data to be used in cake.
     *
     * @param WHERE
     * @param geo_column
     * @param column
     * @return
     */
    public String getPieData(String WHERE, String geo_column, String column) {
        geo.createConnection(user, pass, host, name);
        String pie_data = geo.getPieData(WHERE, geo_column, column, user_id, indicator_id);
        geo.destroyConnection();
        
        return pie_data;
    }

    public String getMapName() {
        geo.createConnection(user, pass, host, name);
        String map_name = geo.getMapName(indicator_id);
        geo.destroyConnection();
        
        return map_name;
    }

    public ArrayList<Range> getRanges() {
        return ranges;
    }

    public void setRanges(ArrayList<Range> ranges) {
        this.ranges = ranges;
    }
}
