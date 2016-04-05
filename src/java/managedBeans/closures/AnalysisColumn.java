/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.closures;

/**
 * AnalysisColumn is responsible for performing analysis columns according to
 * data which will perform the imputation. It also provides a detailed report of
 * invalid data have not yet been loaded into the data warehouse and those who
 * are registered and to determine if fashion column.
 *
 * @author santos
 */
public class AnalysisColumn {
    /*
     * clase que representa el analisis realizado a una columna a imputar
     * no todos los valores seran usados, depende de la columna que se trabaje
     */

    private String columnName = null;//nombre de la columna en base de datos
    private String variableName = null;//nombre de la variable que representa la columna
    //---------------variables para analisis sin cache(datos a cargar a la bodega)--------------------
    private double nullPercentagePerColumnWhitOutCache = -1;//porcentage de nulos sin cache
    private int nullCountPerColumnWhitOutCache = -1;//conteo de nulos sin cache
    private int notApplicableCountPerColumnWhitOutCache = -1;//conteo de registros que no aplica
    private int notNullCountPerColumnWhitOutCache = -1;//conteo de no nulos sin cache
    private int countRedordsWhitOutCache = -1;//conteo de registros sin cache    
    private String modePerColumnWhitOutCache = null;//moda que tiene la columna sin cache
    private String fiveFrecuentsWhitOutCache = null;//frecuentes (maximo 5) sin cache
    //---------------variables para analisis con cache--------------------
    private double nullPercentagePerColumnWhitCache = -1;//porcentage de nulos con cache(carga(nulos y no nulos) + no nulos del cache)   
    private int notNullCountPerColumnWhitCache = -1;//conteo de no nulos con cache(no nulos carga + no nulos del cache)   
    private int notNullCountPerColumnInCache = -1;//conteo de no nulos en cache
    private int notApplicableCountPerColumnWhitCache = -1;//conteo de registros que no aplica
    private int countRedordsInCache = -1;//conteo de registros en cache
    private String modePerColumnWhitCache = null;//moda que tiene la columna con cache
    private String fiveFrecuentsWhitCache = null;//frecuentes (maximo 5) con cache

    /**
     * This method is the class constructor
     */
    public AnalysisColumn() {
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public double getNullPercentagePerColumnWhitOutCache() {
        return nullPercentagePerColumnWhitOutCache;
    }

    public void setNullPercentagePerColumnWhitOutCache(double nullPercentagePerColumnWhitOutCache) {
        this.nullPercentagePerColumnWhitOutCache = nullPercentagePerColumnWhitOutCache;
    }

    public int getNullCountPerColumnWhitOutCache() {
        return nullCountPerColumnWhitOutCache;
    }

    public void setNullCountPerColumnWhitOutCache(int nullCountPerColumnWhitOutCache) {
        this.nullCountPerColumnWhitOutCache = nullCountPerColumnWhitOutCache;
    }

    public int getCountRedordsWhitOutCache() {
        return countRedordsWhitOutCache;
    }

    public void setCountRedordsWhitOutCache(int countRedordsWhitOutCache) {
        this.countRedordsWhitOutCache = countRedordsWhitOutCache;
    }

    public String getModePerColumnWhitOutCache() {
        return modePerColumnWhitOutCache;
    }

    public void setModePerColumnWhitOutCache(String modePerColumnWhitOutCache) {
        this.modePerColumnWhitOutCache = modePerColumnWhitOutCache;
    }

    public String getFiveFrecuentsWhitOutCache() {
        return fiveFrecuentsWhitOutCache;
    }

    public void setFiveFrecuentsWhitOutCache(String fiveFrecuentsWhitOutCache) {
        this.fiveFrecuentsWhitOutCache = fiveFrecuentsWhitOutCache;
    }

    public double getNullPercentagePerColumnWhitCache() {
        return nullPercentagePerColumnWhitCache;
    }

    public void setNullPercentagePerColumnWhitCache(double nullPercentagePerColumnWhitCache) {
        this.nullPercentagePerColumnWhitCache = nullPercentagePerColumnWhitCache;
    }

    public int getNotNullCountPerColumnWhitCache() {
        return notNullCountPerColumnWhitCache;
    }

    public void setNotNullCountPerColumnWhitCache(int notNullCountPerColumnWhitCache) {
        this.notNullCountPerColumnWhitCache = notNullCountPerColumnWhitCache;
    }

    public int getNotNullCountPerColumnInCache() {
        return notNullCountPerColumnInCache;
    }

    public void setNotNullCountPerColumnInCache(int notNullCountPerColumnInCache) {
        this.notNullCountPerColumnInCache = notNullCountPerColumnInCache;
    }

    public int getCountRedordsInCache() {
        return countRedordsInCache;
    }

    public void setCountRedordsInCache(int countRedordsInCache) {
        this.countRedordsInCache = countRedordsInCache;
    }

    public String getModePerColumnWhitCache() {
        return modePerColumnWhitCache;
    }

    public void setModePerColumnWhitCache(String modePerColumnWhitCache) {
        this.modePerColumnWhitCache = modePerColumnWhitCache;
    }

    public String getFiveFrecuentsWhitCache() {
        return fiveFrecuentsWhitCache;
    }

    public void setFiveFrecuentsWhitCache(String fiveFrecuentsWhitCache) {
        this.fiveFrecuentsWhitCache = fiveFrecuentsWhitCache;
    }

    public int getNotNullCountPerColumnWhitOutCache() {
        return notNullCountPerColumnWhitOutCache;
    }

    public void setNotNullCountPerColumnWhitOutCache(int notNullCountPerColumnWhitOutCache) {
        this.notNullCountPerColumnWhitOutCache = notNullCountPerColumnWhitOutCache;
    }

    public int getNotApplicableCountPerColumnWhitOutCache() {
        return notApplicableCountPerColumnWhitOutCache;
    }

    public void setNotApplicableCountPerColumnWhitOutCache(int notApplicableCountPerColumnWhitOutCache) {
        this.notApplicableCountPerColumnWhitOutCache = notApplicableCountPerColumnWhitOutCache;
    }

    public int getNotApplicableCountPerColumnWhitCache() {
        return notApplicableCountPerColumnWhitCache;
    }

    public void setNotApplicableCountPerColumnWhitCache(int notApplicableCountPerColumnWhitCache) {
        this.notApplicableCountPerColumnWhitCache = notApplicableCountPerColumnWhitCache;
    }
}
