/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.indicators;

import beans.connection.ConnectionJdbcMB;
import beans.enumerators.VariablesEnum;
import static beans.enumerators.VariablesEnum.accident_classes;
import static beans.enumerators.VariablesEnum.accident_mechanisms;
import static beans.enumerators.VariablesEnum.activities;
import static beans.enumerators.VariablesEnum.aggressor_genders;
import static beans.enumerators.VariablesEnum.alcohol_levels;
import static beans.enumerators.VariablesEnum.alcohol_levels_counterparts;
import static beans.enumerators.VariablesEnum.alcohol_levels_victim;
import static beans.enumerators.VariablesEnum.boolean3;
import static beans.enumerators.VariablesEnum.contexts;
import static beans.enumerators.VariablesEnum.destinations_of_patient;
import static beans.enumerators.VariablesEnum.involved_vehicles;
import static beans.enumerators.VariablesEnum.mechanisms;
import static beans.enumerators.VariablesEnum.murder_contexts;
import static beans.enumerators.VariablesEnum.non_fatal_places;
import static beans.enumerators.VariablesEnum.places;
import static beans.enumerators.VariablesEnum.precipitating_factors;
import static beans.enumerators.VariablesEnum.protective_measures;
import static beans.enumerators.VariablesEnum.related_events;
import static beans.enumerators.VariablesEnum.relationships_to_victim;
import static beans.enumerators.VariablesEnum.road_types;
import static beans.enumerators.VariablesEnum.service_types;
import static beans.enumerators.VariablesEnum.suicide_mechanisms;
import static beans.enumerators.VariablesEnum.transport_counterparts;
import static beans.enumerators.VariablesEnum.transport_types;
import static beans.enumerators.VariablesEnum.transport_users;
import static beans.enumerators.VariablesEnum.use_alcohol_drugs;
import static beans.enumerators.VariablesEnum.victim_characteristics;
import static beans.enumerators.VariablesEnum.weapon_types;
import beans.util.LineLegendItemSource;
import beans.util.SpanColumns;
import beans.util.Variable;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Paint;
import java.awt.TexturePaint;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import managedBeans.login.LoginMB;
import model.dao.IndicatorsConfigurationsFacade;
import model.dao.IndicatorsFacade;
import model.pojo.Indicators;
import model.pojo.IndicatorsConfigurations;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Interval;
import org.joda.time.Months;
import org.joda.time.Years;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.primefaces.component.column.Column;
import org.primefaces.component.outputpanel.OutputPanel;
import org.primefaces.component.row.Row;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * This class is responsible for determine the risk that to is found a
 * population group in a reference period to a determined type of injury and
 * also involves a characteristic that affects the behavior of the homicide rate
 * as age, gender and geographic component. The indicartor of ‘Tasa específica’
 * differs of the indicator ‘tasa’ where the population is taken from a table
 * that gives us the number of inhabitants by gender, urban area and age, which
 * is supplied by DANE.
 *
 * @author SANTOS
 */
@ManagedBean(name = "indicatorsSpecifiedRateMB")
@SessionScoped
public class IndicatorsSpecifiedRateMB {

    @EJB
    IndicatorsFacade indicatorsFacade;
    @EJB
    IndicatorsConfigurationsFacade indicatorsConfigurationsFacade;
    private List<String> currentConfigurationSelected = new ArrayList<>();
    ;
    private List<String> configurationsList = new ArrayList<>();
    private String newConfigurationName = "";
    private Indicators currentIndicator;
    private StreamedContent chartImage;
    private SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy", new Locale("ES"));
    private OutputPanel dynamicDataTableGroup; // Placeholder.
    private FacesMessage message = null;
    private ConnectionJdbcMB connectionJdbcMB;
    private String titlePage = "SIGEODEP -  INDICADORES GENERALES PARA LESIONES FATALES";
    private String titleIndicator = "SIGEODEP -  INDICADORES GENERALES PARA LESIONES FATALES";
    private String subTitleIndicator = "NUMERO DE CASOS POR LESION";
    //private String currentGraphType;
    private String currentVariableGraph;
    private String currentValueGraph;
    private String firstVariablesCrossSelected = null;
    private String initialValue = "";
    private String endValue = "";
    private String dataTableHtml;
    private String[] headers2;//CABECERA 2 CUANDO EL CRUCE SE REALIZA SOBRE 3 VARIABLES
    private String[][] matrixResult;//MATRIZ DE RESULTADOS
    private Date initialDate = new Date();
    private Date endDate = new Date();
    private String initialDateStr;
    private String endDateStr;
    private boolean invertMatrix = false;
    private boolean showGraphic = false;//mostrar seccion de graficos
    private boolean showTableResult = false;//mostrar tabla de resultados
    private int multiplierK = 0;
    //private List<String> variablesGraph = new ArrayList<>();
    private List<String> valuesGraph = new ArrayList<>();
    private List<String> variablesList = new ArrayList<>();//lista de nombres de variables disponibles que sepueden cruzar(se visualizan en pagina)
    private List<String> listOfCrossVariablesNames = new ArrayList<>();//ista de nombres de variables que se van a cruzar(se visualizan en pagina)
    private List<String> currentAvailableVariablesNamesSelected = new ArrayList<>();//lista de nombres seleccionados en la lista de variables disponibles
    private List<String> currentVariablesCrossSelected = new ArrayList<>();//lista de nombres seleccionados en la lista de variables a cruzar    
    private List<String> currentCategoricalValuesList = new ArrayList<>();
    private List<String> currentCategoricalValuesSelected;
    private ArrayList<SpanColumns> headers1;//CABECERA 1 CUANDO EL CRUCE SE REALIZA SOBRE 3 VARIABLES
    private ArrayList<Variable> completeListOfVariableData;//lista de variables que tiene el indicador
    private ArrayList<Variable> variablesCrossData = new ArrayList<>();//lista de variables a cruzar
    private ArrayList<String> valuesCategoryList;//lista de valores para una categoria
    private ArrayList<String> columNames;//NOMBRES DE LAS COLUMNAS, (SI EL CRUCE ES DE TRES VARIABLES ESTA SEPARADO POR EL CARACTER: }  )
    private ArrayList<String> rowNames;//NOMBRES DE LAS FILAS    
    private String sql;
    private String currentSpatialDisaggregation;
    private String currentTemporalDisaggregation;
    private List<String> spatialDisaggregationTypes = new ArrayList<>();
    private List<String> temporalDisaggregationTypes = new ArrayList<>();
    private String currentMultipler;
    private List<String> multiplers = new ArrayList<>();
    int colorId = 0;
    int typeFill = 0;
    private boolean showFrames = true;
    private boolean showItems = true;
    private Variable currentVariableConfiguring;
    private int numberCross = 2;//maximo numero de variables a cruzar
    private int currentYear = 0;
    private LoginMB loginMB;
    private boolean btnExportDisabled = true;
    private boolean btnAddVariableDisabled = true;
    private boolean btnAddCategoricalValueDisabled = true;
    private boolean btnRemoveCategoricalValueDisabled = true;
    private boolean btnRemoveVariableDisabled = true;
    private boolean renderedDynamicDataTable = true;
    private boolean showCalculation = false;//mostrar la resta
    private boolean colorType = true;
    private boolean showEmpty = false;
    private CopyManager cpManager;
    private StringBuilder sb;
    private int tuplesProcessed;
    //private String sourceTable = "";//tabla adicional que se usara en la seccion "FROM" de la consulta sql
    //private boolean separateRecords = false;
    private int heightGraph = 460;
    private int widthGraph = 660;
    private int sizeFont = 12;
    private List<String> typesGraph = new ArrayList<>();
    private String currentTypeGraph = "barras";
    DecimalFormat formateador = new DecimalFormat("0.00");
    String variablesName = "";
    String categoryAxixLabel = "";
    String indicatorName = "";
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy", new Locale("ES"));
    DefaultCategoryDataset dataset = null;

    /**
     * This method is the class constructor, is responsible for instantiating
     * the current connection to the database, verify that the user has
     * successfully logged in, this method instance items needed to start to
     * working.
     */
    public IndicatorsSpecifiedRateMB() {
        //-------------------------------------------------
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
        loginMB = (LoginMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{loginMB}", LoginMB.class);
        Calendar c = Calendar.getInstance();
        currentYear = c.get(Calendar.YEAR);

        initialDate.setDate(1);
        initialDate.setMonth(0);
        initialDate.setYear(c.get(Calendar.YEAR) - 1900);

        endDate.setDate(c.get(Calendar.DATE));
        endDate.setMonth(c.get(Calendar.MONTH));
        endDate.setYear(c.get(Calendar.YEAR) - 1900);


    }

    /**
     * This method is responsible to display messages on the screen for that the
     * user can see what is happening.
     */
    public void showMessage() {
        if (message != null) {
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    /**
     * This method is responsible of obtain the names and values of the
     * variables that are going to cross to be visible on the graph where the
     * results of the cross shown.
     */
    public void loadValuesGraph() {
        valuesGraph = new ArrayList<>();
        for (int i = 0; i < variablesCrossData.size(); i++) {
            if (variablesCrossData.get(i).getName().compareTo(currentVariableGraph) == 0) {
                for (int j = 0; j < variablesCrossData.get(i).getValuesConfigured().size(); j++) {
                    valuesGraph.add(variablesCrossData.get(i).getValuesConfigured().get(j));
                    currentValueGraph = valuesGraph.get(0);
                }
                break;
            }
        }
        createImage();
    }

    /**
     * This method is responsible to delete all empty results presented at the
     * time of cross of variables specified by the user, as delete the rows and
     * columns where exist records empty.
     */
    private void removeEmpty() {
        //------------------------------------------------------------------
        //SE ELIMINAN LOS VALORES VACIOS
        //------------------------------------------------------------------
        sql = ""
                + " DELETE FROM \n\r"
                + "    indicators_records \n\r"
                + " WHERE \n\r"
                + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n\r"
                + "    indicator_id = " + currentIndicator.getIndicatorId() + " AND \n\r"
                + "    count = 0 ";
        connectionJdbcMB.non_query(sql);
        //---------------------------------------------------------            
        //ELIMINO LOS NOMBRES DE COLUMNAS NO NECESARIOS
        //---------------------------------------------------------            
        ResultSet rs;
        if (variablesCrossData.size() < 3) { //una o dos variables
            sql = ""
                    + " SELECT column_1 FROM  indicators_records \n"
                    + " WHERE  user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "        indicator_id = " + currentIndicator.getIndicatorId() + "  \n"
                    + " GROUP BY column_1  ORDER BY MIN(record_id) \n";

        }
        if (variablesCrossData.size() == 3) {
            sql = ""
                    + " SELECT column_1 ||'}'|| column_2 FROM indicators_records \n"
                    + " WHERE  user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "        indicator_id = " + currentIndicator.getIndicatorId() + "  \n"
                    + " GROUP BY  column_1 ||'}'|| column_2  ORDER BY MIN(record_id) \n";
        }
        try {
            rs = connectionJdbcMB.consult(sql);
            ArrayList<String> columnNamesFound = new ArrayList<>();
            while (rs.next()) {
                columnNamesFound.add(rs.getString(1));
            }
            int columnFound;
            for (int i = 0; i < columNames.size(); i++) {
                columnFound = -1;
                for (int j = 0; j < columnNamesFound.size(); j++) {
                    if (columNames.get(i).compareTo(columnNamesFound.get(j)) == 0) {
                        columnFound = j;
                        break;
                    }
                }
                if (columnFound == -1) {
                    columNames.remove(i);
                    i = -1;//vuelvo a empezar
                }
            }
        } catch (Exception e) {
        }
        //---------------------------------------------------------            
        //ELIMINO LOS NOMBRES DE FILAS NO NECESARIOS
        //---------------------------------------------------------            
        sql = "";
        if (variablesCrossData.size() == 2) {
            sql = ""
                    + " SELECT column_2 FROM indicators_records \n"
                    + " WHERE user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "       indicator_id = " + currentIndicator.getIndicatorId() + "  \n"
                    + " GROUP BY column_2 ORDER BY MIN(record_id) \n";
        }
        if (variablesCrossData.size() == 3) {
            sql = ""
                    + " SELECT column_3 FROM indicators_records \n"
                    + " WHERE user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "       indicator_id = " + currentIndicator.getIndicatorId() + "  \n"
                    + " GROUP BY column_3 ORDER BY MIN(record_id) \n";

        }
        if (sql.length() != 0) {
            try {
                rs = connectionJdbcMB.consult(sql);
                ArrayList<String> rowsNamesFound = new ArrayList<>();
                while (rs.next()) {
                    rowsNamesFound.add(rs.getString(1));
                }
                int rowFound;
                for (int i = 0; i < rowNames.size(); i++) {
                    rowFound = -1;
                    for (int j = 0; j < rowsNamesFound.size(); j++) {
                        if (rowNames.get(i).compareTo(rowsNamesFound.get(j)) == 0) {
                            rowFound = j;
                            break;
                        }
                    }
                    if (rowFound == -1) {
                        rowNames.remove(i);
                        i = -1;//vuelvo a empezar
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * This method is responsible to do the cross of variables according to
     * specified by the user, validates the date range, the number of variables
     * is less than or equal to the limit set, This method gets the variables to
     * cross, This method does all possible crosses, the results are grouped and
     * results matrix is created and finally the result table and the graph is
     * shown.
     */
    public void process() {
        showGraphic = false;
        showTableResult = false;
        btnExportDisabled = true;
        variablesCrossData = new ArrayList<>();//lista de variables a cruzar            
        boolean continueProcess = true;
        message = null;
        valuesGraph = new ArrayList<>();
        currentValueGraph = "";
        currentVariableGraph = "";
        categoryAxixLabel = "";

        if (continueProcess) {//VALIDO LAS FECHAS
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c1.setTime(initialDate);
            c2.setTime(endDate);
            if (c1.compareTo(c2) > 0) {
                continueProcess = false;
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La fecha inicial debe ser inferior o igual a la fecha final");
            } else {
                initialDateStr = formato.format(initialDate);
                endDateStr = formato.format(endDate);
            }
        }

        if (continueProcess) {//NUMERO DE VARIABLES A CRUZAR SEA MENOR O IGUAL AL LIMITE ESTABLECIDO
            if (listOfCrossVariablesNames.size() <= 2 && listOfCrossVariablesNames.size() > 0) {
                continueProcess = true;
            } else {
                continueProcess = false;
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "En la lista de variables a cruzar deben haber minimo 1 y maximo 2 variables");
            }
        }

        if (continueProcess) {//AGREGUE ZONA, COMUNA O MUNICIPIO
            continueProcess = false;
            for (int i = 0; i < listOfCrossVariablesNames.size(); i++) {
                if (listOfCrossVariablesNames.get(i).compareTo("zona") == 0) {
                    continueProcess = true;
                    break;
                }
                if (listOfCrossVariablesNames.get(i).compareTo("comuna") == 0) {
                    continueProcess = true;
                    break;
                }
                if (listOfCrossVariablesNames.get(i).compareTo("municipio") == 0) {
                    continueProcess = true;
                    break;
                }
            }
            if (!continueProcess) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Debe adicionar la variable zona, comuna o municipio para determinar que población se usará");
            }
        }

        if (continueProcess) {//AGREGO LAS VARIABLES INDICADAS POR EL USUARIO
            variablesCrossData.add(createTemporalDisaggregationVariable(initialDate, endDate));//variable de desagregacion temporal
            for (int j = 0; j < listOfCrossVariablesNames.size(); j++) {
                for (int i = 0; i < completeListOfVariableData.size(); i++) {
                    if (completeListOfVariableData.get(i).getName().compareTo(listOfCrossVariablesNames.get(j)) == 0) {
                        variablesCrossData.add(completeListOfVariableData.get(i));
                    }
                }
            }
        }

        if (continueProcess) {//CADA VARIABLE A CRUZAR TENGA VALORES CONFIGURADOS
            for (int i = 0; i < variablesCrossData.size(); i++) {
                if (variablesCrossData.get(i).getValuesConfigured().isEmpty()) {
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La variable " + variablesCrossData.get(i).getName() + " no tiene valores configurados, para continuar debe ser configurada.");
                    continueProcess = false;
                }
            }
        }

        if (continueProcess) {//CARGO LOS COMBOS PARA EL GRAFICO
            if (variablesCrossData.size() == 3) {
                currentVariableGraph = variablesCrossData.get(2).getName();
                for (int j = 0; j < variablesCrossData.get(2).getValuesConfigured().size(); j++) {
                    valuesGraph.add(variablesCrossData.get(2).getValuesConfigured().get(j));
                    currentValueGraph = variablesCrossData.get(2).getValuesConfigured().get(j);
                }
            }
        }
        if (continueProcess) {//ELIMINO DATOS DE UN PROCESO ANTERIOR
            removeIndicatorRecords();
        }
        if (continueProcess) {
            //ALMACENO EN BASE DE DATOS LOS REGISTROS DE ESTE CRUCE
            saveIndicatorRecords(createIndicatorConsult());
            //DETRMINO LA POBLACION
            calculatePopulation();
            //CONVIERTO A CATEGORIAS
            convertToCategorical();
            //CREO TODAS LAS POSIBLES COMBINACIONES
            createCombinations();
            //AGRUPO LOS VALORES
            groupingOfValues();
        }


        if (!showEmpty) {
            removeEmpty();
        }
        if (continueProcess) {//MATRIZ DE RESULTADOS
            createMatrixResult();
        }
        if (continueProcess) {//CREO LA TABLA DE RESULTADOS Y EL GRAFICO            
            dataTableHtml = createDataTableResult();
            createImage();//creo el grafico
            showGraphic = true;
            showTableResult = true;
            //btnExportDisabled = false;
            //message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Cruze realizado");
        }
    }

    /**
     * This method is responsible of group all the values obtained from the
     * cross of variables which were saved on indicators_records, arrange it
     * according to the options specified by the user.
     */
    private void groupingOfValues() {
        //------------------------------------------------------------------
        //SE AGRUPAN LOS VALORES Y SE REALIZA EL CONTEO
        //------------------------------------------------------------------                

        sql = ""
                + " SELECT  \n"
                + "	column_1, \n"
                + "	column_2, \n"
                + "	column_3, \n"
                + "     count(*),  \n"
                + "     population  \n"
                + " FROM \n"
                + "	indicators_records \n"
                + " WHERE \n"
                + "     user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                + "     indicator_id = " + (currentIndicator.getIndicatorId() + 100) + " \n"
                + " GROUP BY \n"
                + "	column_1, \n"
                + "	column_2, \n"
                + "	column_3, \n"
                + "     population  \n";
        ResultSet rs = connectionJdbcMB.consult(sql);
        try {//actualizo el valor count de los registros currentIndicator.getIndicatorId() apartir de  currentIndicator.getIndicatorId()+100
            while (rs.next()) {
                sql = " "
                        + " UPDATE \n"
                        + "    indicators_records \n"
                        + " SET \n"
                        + "    count = " + rs.getString("count") + ", \n"
                        + "    population = " + rs.getString("population") + " \n"
                        + " WHERE \n"
                        + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                        + "    indicator_id = " + currentIndicator.getIndicatorId() + " AND \n"
                        + "    column_1 like '" + rs.getString("column_1") + "' AND \n"
                        + "    column_2 like '" + rs.getString("column_2") + "' AND \n"
                        + "    column_3 like '" + rs.getString("column_3") + "' \n";
                connectionJdbcMB.non_query(sql);
            }
            sql = ""
                    + " DELETE FROM \n"
                    + "    indicators_records \n"
                    + " WHERE \n"
                    + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "    indicator_id = 1" + currentIndicator.getIndicatorId() + " \n";
            connectionJdbcMB.non_query(sql);//elimino los valores del indicador 100
        } catch (Exception e) {
            System.out.println("Error 1 en " + this.getClass().getName() + ":" + e.toString());
        }
    }

    /**
     * This method is responsible of separate the records when relationships
     * variable are realized from one to many.
     */
    private void createCombinations() {
        //---------------------------------------------------------
        //FORMAR POSIBLES COMBINACIONES PARA QUE LOS DATOS QUEDEN ORDENADOS SEGUN COMO SE ENCUENTRE LA CONFIGURACION
        //osea que permite la eliminacion de las categorias que no se encuentren dentro de la configuracion
        //---------------------------------------------------------
        columNames = new ArrayList<>();
        rowNames = new ArrayList<>();
        try {
            List<String> values1, values2, values3;
            //---------------------------------------------------------
            //REALIZO LAS POSIBLES COMBINACIONES
            //---------------------------------------------------------            
            cpManager = new CopyManager((BaseConnection) connectionJdbcMB.getConn());
            sb = new StringBuilder();
            tuplesProcessed = 0;
            int id = 0;
            if (variablesCrossData.size() == 1) {
                values1 = variablesCrossData.get(0).getValuesConfigured();
                for (int i = 0; i < values1.size(); i++) {
                    columNames.add(values1.get(i));
                    sb.
                            append(loginMB.getCurrentUser().getUserId()).append("\t").
                            append(currentIndicator.getIndicatorId()).append("\t").
                            append(id).append("\t").
                            append(values1.get(i)).append("\t").
                            append("-").append("\t").
                            append("-").append("\t").
                            append(0).append("\t").
                            append(0).append("\n");
                    id++;
                }
                rowNames.add("Valor");
            } else if (variablesCrossData.size() == 2) {
                values1 = variablesCrossData.get(0).getValuesConfigured();
                values2 = variablesCrossData.get(1).getValuesConfigured();
                for (int i = 0; i < values2.size(); i++) {
                    rowNames.add(values2.get(i));
                    for (int j = 0; j < values1.size(); j++) {
                        if (i == 0) {
                            columNames.add(values1.get(j));
                        }
                        sb.
                                append(loginMB.getCurrentUser().getUserId()).append("\t").
                                append(currentIndicator.getIndicatorId()).append("\t").
                                append(id).append("\t").
                                append(values1.get(j)).append("\t").
                                append(values2.get(i)).append("\t").
                                append("-").append("\t").
                                append(0).append("\t").
                                append(0).append("\n");
                        id++;
                    }
                }
            } else if (variablesCrossData.size() == 3) {
                values1 = variablesCrossData.get(0).getValuesConfigured();
                values2 = variablesCrossData.get(1).getValuesConfigured();
                values3 = variablesCrossData.get(2).getValuesConfigured();
                for (int i = 0; i < values2.size(); i++) {
                    for (int j = 0; j < values1.size(); j++) {
                        columNames.add(values1.get(j) + "}" + values2.get(i));
                        for (int k = 0; k < values3.size(); k++) {
                            if (i == 0 && j == 0) {
                                rowNames.add(values3.get(k));
                            }
                            sb.
                                    append(loginMB.getCurrentUser().getUserId()).append("\t").
                                    append(currentIndicator.getIndicatorId()).append("\t").
                                    append(id).append("\t").
                                    append(values1.get(j)).append("\t").
                                    append(values2.get(i)).append("\t").
                                    append(values3.get(k)).append("\t").
                                    append(0).append("\t").
                                    append(0).append("\n");
                            id++;
                        }
                    }
                }
            }
            //REALIZO LA INSERCION
            cpManager.copyIn("COPY indicators_records FROM STDIN", new StringReader(sb.toString()));
            sb.delete(0, sb.length()); //System.out.println("Procesando... filas " + tuplesProcessed + " cargadas");
        } catch (SQLException | IOException e) {
            System.out.println("Error 2 en " + this.getClass().getName() + ":" + e.toString());
        }
    }

    /**
     * This method stores in the database the records of the cross that is
     * realice, the records is stored in the table indicators_records
     *
     * @param sqlConsult
     */
    private void saveIndicatorRecords(String sqlConsult) {
        //------------------------------------------------------------------
        //AGEGAR UNA CONSULTA A LA TABLA indicators_records 
        //------------------------------------------------------------------
        try {
            cpManager = new CopyManager((BaseConnection) connectionJdbcMB.getConn());
            ResultSet rs = connectionJdbcMB.consult(sqlConsult);
            sb = new StringBuilder();
            tuplesProcessed = 0;
//            int ncol = 0;
            //determino si una de las columnas es poblacion
//            for (int i = 0; i < rs.getMetaData().getColumnCount() + 1; i++) {
//                if (rs.getMetaData().getColumnName(i + 1).compareTo("poblacion") == 0) {
//                    ncol = rs.getMetaData().getColumnCount() - 1;
//                    break;
//                } else {
//                    ncol = rs.getMetaData().getColumnCount();
//                }
//            }
            int ncol = rs.getMetaData().getColumnCount();
            int haveNulls;
            while (rs.next()) {
                haveNulls = 0;
                for (int i = 1; i <= ncol; i++) {//agrego solo los que no tengan valores nulos
                    if (rs.getString(i) == null || rs.getString(i).length() == 0 || rs.getString(i).compareToIgnoreCase("null") == 0) {
                        haveNulls++;
                    }
                }
                if (haveNulls == 0) {//si no tiene nullos es agregado
                    tuplesProcessed++;
                    sb.
                            append(loginMB.getCurrentUser().getUserId()).append("\t").
                            append(currentIndicator.getIndicatorId() + 100).append("\t").
                            append(tuplesProcessed).append("\t");

                    for (int i = 1; i <= ncol; i++) {//datos del cruce
                        sb.append(rs.getString(i)).append("\t");
                    }
                    for (int i = 0; i < 3 - ncol; i++) {//variables no usadas(vacias)
                        sb.append("-").append("\t");
                    }
                    //if (rs.getString("poblacion").compareTo("SIN DATO") == 0) {
                    sb.append(0).append("\t").append(0).append("\n");//count y poblacion quedan como 0
                    //} else {
                    //    sb.append(0).append("\t").append(Integer.valueOf(rs.getString("poblacion"))).append("\n");//count queda como 0
                    //}

                }
            }
            //REALIZO LA INSERCION
            cpManager.copyIn("COPY indicators_records FROM STDIN", new StringReader(sb.toString()));
            sb.delete(0, sb.length()); //System.out.println("Procesando... filas " + tuplesProcessed + " cargadas");
            //System.out.println("Numero de nulos = " + haveNulls);
        } catch (SQLException | IOException e) {
            System.out.println("Error 3 en " + this.getClass().getName() + ":" + e.toString());
        }
    }

    /**
     * Este metodo se encarga de agregar una tabla adicional si es necesario,
     * esto se hace en la seccion FROM y en la seccion WHERE de la consulta SQL.
     *
     * @param tableName
     */
    public void calculatePopulation() {
        try {
            /*
             * calcular la poblacion de cada uno de los registros
             */
            /**
             * This method is responsible for establishing the case to be
             * handled in the SQL query depending on the value of the
             * parameters.
             *
             * @param sqlReturn
             * @param source_table
             * @param column_whit_name
             * @param category_table
             * @param column_whit_id
             * @param as_name
             * @return
             */
            /**
             * This method is responsible for assemble the necessary query to
             * the indicator that the user is working, where are determined all
             * necessary parameters that must have the SQL query to perform the
             * crossing variable as is the type of temporal the disaggregation,
             * type of injury, age, available variables.
             *
             * @return
             */
            ResultSet rs = connectionJdbcMB.consult(""
                    + " SELECT \n"
                    + "    * \n"
                    + " FROM \n"
                    + "    indicators_records \n"
                    + " WHERE \n"
                    + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "    indicator_id = 1" + currentIndicator.getIndicatorId());
            while (rs.next()) {
                if (rs.getInt("count") == 0) {//la poblacion es igual a cero, se debe calcular
                    sql = "UPDATE indicators_records set population = ( \n\r";
                    for (int i = 0; i < variablesCrossData.size(); i++) {
                        if (variablesCrossData.get(i).getName().compareTo("comuna") == 0) {//determinar si se usa populations_by_commune o populations_by_area
                            sql = sql + " Select SUM(population) from populations_by_commune where \n\r";
                            break;
                        }
                        if (variablesCrossData.get(i).getName().compareTo("zona") == 0) {
                            sql = sql + " Select SUM(population) from populations_by_area where \n\r";//determinar si se usa populations_by_commune o populations_by_area
                            break;
                        }
                        if (variablesCrossData.get(i).getName().compareTo("municipio") == 0) {
                            sql = sql + " SELECT sum(population) from populations_by_area where \n\r";
                            break;
                        }
                    }

                    if (sql.indexOf("populations_by_") == -1) {//no se cruza ni zona ni comuna entonces se usa comuna
                        sql = sql + " Select SUM(population) from populations_by_commune where ";
                    }
                    for (int i = 0; i < variablesCrossData.size(); i++) {
                        if (variablesCrossData.get(i).getName().compareTo("comuna") == 0) {
                            sql = sql + "\n commune_id = column_" + (i + 1) + "::smallint AND ";
                        }
                        if (variablesCrossData.get(i).getName().compareTo("genero") == 0) {
                            sql = sql + "\n gender_id = column_" + (i + 1) + "::smallint AND  ";
                        }
                        if (variablesCrossData.get(i).getName().compareTo("zona") == 0) {
                            sql = sql + "\n area_id = column_" + (i + 1) + "::smallint AND ";
                        }
                        if (variablesCrossData.get(i).getName().compareTo("edad") == 0) {
                            //determino la instuccion para la edad

                            String ages = "IN (-1)";
                            int age = Integer.parseInt(rs.getString("column_" + (i + 1)));
                            for (int j = 0; j < variablesCrossData.get(i).getValuesConfigured().size(); j++) {
                                if (variablesCrossData.get(i).getValuesConfigured().get(j).compareTo("SIN DATO") != 0) {
                                    String[] splitAge = variablesCrossData.get(i).getValuesConfigured().get(j).split("/");
                                    if (splitAge[1].compareTo("n") == 0) {
                                        splitAge[1] = "200";
                                    }

                                    if (Integer.parseInt(splitAge[0]) <= age && Integer.parseInt(splitAge[1]) >= age) {
                                        ages = "IN (";
                                        for (int k = Integer.parseInt(splitAge[0]); k <= Integer.parseInt(splitAge[1]); k++) {
                                            ages = ages + String.valueOf(k) + ",";
                                        }
                                        ages = ages.substring(0, ages.length() - 1);//quito ultima coma
                                        ages = ages + ")";
                                        break;
                                    }
                                }
                            }
                            sql = sql + "\n age " + ages + "  AND ";
                        }
                    }
                    sql = sql + "\n year_population = extract(year from to_date(column_1,'yyyy-MM-dd'))::integer \n)";
                    sql = sql
                            + " WHERE  \n"
                            + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                            + "    indicator_id = 1" + currentIndicator.getIndicatorId() + " AND \n"
                            + "    column_1 like '" + rs.getString("column_1") + "' AND \n"
                            + "    column_2 like '" + rs.getString("column_2") + "' AND \n"
                            + "    column_3 like '" + rs.getString("column_3") + "'";
                    //System.out.println("CONSULTA 002 \n" + sql);        
                    connectionJdbcMB.non_query(sql);
                }
            }
            connectionJdbcMB.non_query(""
                    + " UPDATE "
                    + "   indicators_records "
                    + " set "
                    + "   population = 0 "
                    + " where "
                    + "    population is null AND "
                    + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND "
                    + "    indicator_id = 1" + currentIndicator.getIndicatorId());//colocar en cero las casillas vacias

        } catch (SQLException ex) {
            Logger.getLogger(IndicatorsSpecifiedRateMB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void convertToCategorical() {
        /*
         * convertir los valores de indicator_records en valores categoricos
         * ejemplos: si la variable es genero y aparece 1           se convierte en 'MASCULINO'
         *           si la variable es edad   y aparece 20          se convierte en '16 a 20 años' 
         *           si la variable es fecha  y aparece 22-01-2014  se convierte en 'enero de 2014'   
         */

        for (int i = 0; i < variablesCrossData.size(); i++) {
            if (variablesCrossData.get(i).getGeneric_table().compareTo("temporalDisaggregation") == 0) {
                sql = "UPDATE indicators_records SET column_" + (i + 1) + " = (" + "\n   CASE \n\r";
                sql = sql + "       WHEN column_" + (i + 1) + "= 'SIN DATO' THEN 'SIN DATO'";

                for (int j = 0; j < variablesCrossData.get(i).getValuesId().size(); j++) {
                    String[] splitDates = variablesCrossData.get(i).getValuesId().get(j).split("}");
                    sql = sql + "       WHEN ( \n\r";
                    sql = sql + "           to_date(column_" + (i + 1) + ",'yyyy-MM-dd')" + " >= to_date('" + splitDates[0] + "','dd/MM/yyyy') AND \n\r";
                    sql = sql + "           to_date(column_" + (i + 1) + ",'yyyy-MM-dd')" + " <= to_date('" + splitDates[1] + "','dd/MM/yyyy') \n\r";
                    sql = sql + "       ) THEN '" + variablesCrossData.get(i).getValues().get(j) + "'  \n\r";
                }
                sql = sql + " END) \n"
                        + " WHERE  \n"
                        + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                        + "    indicator_id = 1" + currentIndicator.getIndicatorId();
                connectionJdbcMB.non_query(sql);//System.out.println("CONSULTA (actualizar fecha specified rate) \n " + sql);
            }
            if (variablesCrossData.get(i).getName().compareTo("comuna") == 0) {
                sql = "UPDATE indicators_records SET column_" + (i + 1) + " = ( \n"
                        + "       SELECT \n\r"
                        + "          communes.commune_name \n\r"
                        + "       FROM \n\r"
                        + "          public.communes "
                        + "       WHERE \n\r"
                        + "          column_" + (i + 1) + "::integer = communes.commune_id \n"
                        + "    ) "
                        + " WHERE  \n"
                        + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                        + "    indicator_id = 1" + currentIndicator.getIndicatorId();
                connectionJdbcMB.non_query(sql);//System.out.println("CONSULTA (actualizar comuna specified rate) \n " + sql);
            }
            if (variablesCrossData.get(i).getName().compareTo("genero") == 0) {
                sql = "UPDATE indicators_records SET column_" + (i + 1) + " = ( \n"
                        + "       SELECT \n\r"
                        + "          gender_name \n\r"
                        + "       FROM \n\r"
                        + "          genders \n\r"
                        + "       WHERE \n\r"
                        + "          gender_id = column_" + (i + 1) + "::integer \n\r"
                        + "    )"
                        + " WHERE  \n"
                        + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                        + "    indicator_id = 1" + currentIndicator.getIndicatorId();
                connectionJdbcMB.non_query(sql);//System.out.println("CONSULTA (actualizar genero specified rate) \n " + sql);
            }
            if (variablesCrossData.get(i).getName().compareTo("zona") == 0) {
                sql = "UPDATE indicators_records SET column_" + (i + 1) + " = ("
                        + "       SELECT \n\r"
                        + "          areas.area_name \n\r"
                        + "       FROM \n\r"
                        + "          public.areas \n"
                        + "       WHERE \n\r"
                        + "          column_" + (i + 1) + "::integer =  areas.area_id \n\r"
                        + "     )"
                        + " WHERE  \n"
                        + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                        + "    indicator_id = 1" + currentIndicator.getIndicatorId();
                connectionJdbcMB.non_query(sql);//System.out.println("CONSULTA (actualizar area specified rate) \n " + sql);
            }
            if (variablesCrossData.get(i).getName().compareTo("edad") == 0) {
                sql = "UPDATE indicators_records SET column_" + (i + 1) + " = (" + "\n   CASE \n\r";
                for (int j = 0; j < variablesCrossData.get(i).getValuesConfigured().size(); j++) {
                    if (variablesCrossData.get(i).getValuesConfigured().get(j).compareTo("SIN DATO") != 0) {
                        String[] splitAge = variablesCrossData.get(i).getValuesConfigured().get(j).split("/");
                        if (splitAge[1].compareTo("n") == 0) {
                            splitAge[1] = "200";
                        }
                        sql = sql + ""
                                + "       WHEN (column_" + (i + 1) + "::integer"
                                + "        between " + splitAge[0] + " and " + splitAge[1] + ") THEN '" + variablesCrossData.get(i).getValuesConfigured().get(j) + "'  \n\r";
                    }
                }
                sql = sql + " END) "
                        + " WHERE  \n"
                        + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                        + "    indicator_id = 1" + currentIndicator.getIndicatorId();
                connectionJdbcMB.non_query(sql);//System.out.println("CONSULTA (actualizar edad specified rate) \n " + sql);
            }
        }
        //los valores que queden null se vuelven sin dato
        connectionJdbcMB.non_query("UPDATE indicators_records SET column_1='SIN DATO' "
                + " WHERE  \n"
                + "    column_1 is null AND "
                + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                + "    indicator_id = 1" + currentIndicator.getIndicatorId());

        connectionJdbcMB.non_query("UPDATE indicators_records SET column_2='SIN DATO' "
                + " WHERE column_2 is null AND "
                + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                + "    indicator_id = 1" + currentIndicator.getIndicatorId());
        connectionJdbcMB.non_query("UPDATE indicators_records SET column_3='SIN DATO' "
                + "WHERE column_3 is null AND "
                + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                + "    indicator_id = 1" + currentIndicator.getIndicatorId());

    }

    private String createIndicatorConsult() {
        String sqlReturn = " SELECT  \n\r";
        for (int i = 0; i < variablesCrossData.size(); i++) {
            switch (VariablesEnum.convert(variablesCrossData.get(i).getGeneric_table())) {//nombre de variable 
                case temporalDisaggregation://DETERMINAR LA DESAGREGACION TEMPORAL -----------------------                   
                    sqlReturn = sqlReturn + " " + currentIndicator.getInjuryType() + ".injury_date as fecha \n\r ";
                    break;
                case age://DETERMINAR EDAD -----------------------          
                    sqlReturn = sqlReturn + " victims.victim_age AS edad ";
                    break;
                case communes://COMUNA -----------------------
                    sqlReturn = sqlReturn
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          communes.commune_id \n\r"
                            + "       FROM \n\r"
                            + "          public.communes, \n\r"
                            + "          public.neighborhoods \n\r"
                            + "       WHERE \n\r"
                            + "          neighborhoods.neighborhood_suburb = communes.commune_id AND \n\r"
                            + "          neighborhoods.neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id \n\r"
                            + "    ) AS comuna";
                    break;
                case municipality:
                    sqlReturn = sqlReturn + "(select 'Municipio'::text) as municipio";
                    break;
                case areas://ZONA -----------------------        
                    sqlReturn = sqlReturn + ""
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          areas.area_id \n\r"
                            + "       FROM \n\r"
                            + "          public.areas, \n\r"
                            + "          public.neighborhoods \n\r"
                            + "       WHERE \n\r"
                            + "          neighborhoods.neighborhood_area = areas.area_id AND \n\r"
                            + "          neighborhoods.neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id \n\r"
                            + "    ) AS zona";
                    break;
                case genders://GENERO  ----------------------                    
                    sqlReturn = sqlReturn + variablesCrossData.get(i).getSource_table() + " AS genero";
                    break;
            }
            if (i == variablesCrossData.size() - 1) {//si es la ultima instruccion se agrega salto de linea
                sqlReturn = sqlReturn + " \n\r";
            } else {//si no es la ultima instruccion se agrega coma y salto de linea
                sqlReturn = sqlReturn + ", \n\r";
            }
        }
        //finalizo la instruccion
        sqlReturn = sqlReturn + ""
                + "   FROM  \n"
                + "       " + currentIndicator.getInjuryType() + "\n"
                + "       JOIN victims USING (victim_id) \n"
                + "   WHERE  \n\r";
        if (currentIndicator.getIndicatorId() > 4) { //si no es uno de los indicadores generales se filtra por tipo de lesion
            if (currentIndicator.getIndicatorId() > 32 && currentIndicator.getIndicatorId() < 40) {//si es interpersonal en familia injury_id=53,55,56
                sqlReturn = sqlReturn + "       " + currentIndicator.getInjuryType() + ".injury_id IN (53,55,56) AND \n\r";
            } else {
                sqlReturn = sqlReturn + "       " + currentIndicator.getInjuryType() + ".injury_id = " + currentIndicator.getInjuryId().toString() + " AND \n\r";
            }
        }
        sqlReturn = sqlReturn + ""
                + "       " + currentIndicator.getInjuryType() + ".injury_date >= to_date('" + initialDateStr + "','dd/MM/yyyy') AND \n\r"
                + "       " + currentIndicator.getInjuryType() + ".injury_date <= to_date('" + endDateStr + "','dd/MM/yyyy'); ";
        //System.out.println("CONSULTA (specified rate) \n " + sqlReturn);
        return sqlReturn;
    }

    /**
     * This method is responsible of delete all records that have saved for a
     * cross above variables.
     */
    private void removeIndicatorRecords() {
        //---------------------------------------------------------        
        //elimino los datos de este indicador
        //---------------------------------------------------------
        sql = ""
                + " DELETE FROM \n\r"
                + "    indicators_records \n\r"
                + " WHERE \n\r"
                + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n\r"
                + "    ( \n\r"
                + "       indicator_id = " + currentIndicator.getIndicatorId() + " OR \n\r" //datos ordenados completos(los que tienen y no tienen conteo )
                + "       indicator_id = " + (currentIndicator.getIndicatorId() + 100) + " \n\r" //ocurrencias
                + "    ) \n\r";
        //System.out.println("ELIMINACIONES \n " + sql);
        connectionJdbcMB.non_query(sql);
    }

    /**
     * if the user have not categorical varables selected at the time of
     * crossing variables, this method is responsible for disabling the button
     * that allows the user to remove categorical variables.
     */
    public void changeCategoticalList() {
        if (!currentCategoricalValuesSelected.isEmpty()) {
            btnRemoveCategoricalValueDisabled = false;
        }
    }

    /**
     * This method is responsible of delete the settings that has a categorical
     * variable to leave it in its original form.
     *
     * @return
     */
    public int btnRemoveConfigurationClick() {
        //System.out.println("currentConfigurationSelected es " + currentConfigurationSelected);
        if (currentConfigurationSelected == null || currentConfigurationSelected.isEmpty()) {//VALOR INICIAL INGRESADO
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar una configuración de la lista"));
            return 0;
        }
        List<IndicatorsConfigurations> indicatorsConfigurationsList = indicatorsConfigurationsFacade.findAll();
        for (int i = 0; i < indicatorsConfigurationsList.size(); i++) {
            for (int j = 0; j < currentConfigurationSelected.size(); j++) {
                if (indicatorsConfigurationsList.get(i).getConfigurationName().compareTo(currentConfigurationSelected.get(j)) == 0) {
                    indicatorsConfigurationsFacade.remove(indicatorsConfigurationsList.get(i));
                    btnLoadConfigurationClick();
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "La configuración ha sido eliminada"));
                    return 0;
                }
            }
        }
        return 0;
    }

    /**
     * This method is responsible of load a configuration of a categorical
     * variable created previously.
     *
     * @return
     */
    public int btnOpenConfigurationClick() {
        //realizar la carga de la configuracion indicada
        if (currentConfigurationSelected == null || currentConfigurationSelected.isEmpty()) {//VALOR INICIAL INGRESADO
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar una configuración de la lista"));
            return 0;
        }
        currentCategoricalValuesList = new ArrayList<>();
        IndicatorsConfigurations indicatorsConfigurationsSelected = indicatorsConfigurationsFacade.findByName(currentConfigurationSelected.get(0));

        if (firstVariablesCrossSelected.compareTo(indicatorsConfigurationsSelected.getVariableName()) != 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La configuracion corresponde a la variable ("
                    + indicatorsConfigurationsSelected.getVariableName() + ") se debe abrir una configuracion para"
                    + " una variable de tipo (" + firstVariablesCrossSelected + ")"));
            return 0;
        }

        String[] splitConfiguration = indicatorsConfigurationsSelected.getConfiguredValues().split("\t");
        currentCategoricalValuesList.addAll(Arrays.asList(splitConfiguration));

        for (int i = 0; i < variablesCrossData.size(); i++) {
            if (variablesCrossData.get(i).getName().compareTo(firstVariablesCrossSelected) == 0) {
                variablesCrossData.get(i).setValuesConfigured(Arrays.asList(splitConfiguration));
                variablesCrossData.get(i).setValuesId(Arrays.asList(splitConfiguration));
                variablesCrossData.get(i).setValues(Arrays.asList(splitConfiguration));
                break;
            }
        }
        for (int i = 0; i < completeListOfVariableData.size(); i++) {
            if (completeListOfVariableData.get(i).getName().compareTo(firstVariablesCrossSelected) == 0) {
                completeListOfVariableData.get(i).setValuesConfigured(Arrays.asList(splitConfiguration));
                completeListOfVariableData.get(i).setValuesId(Arrays.asList(splitConfiguration));
                completeListOfVariableData.get(i).setValues(Arrays.asList(splitConfiguration));
                break;
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Configuración cargada"));
        return 0;
    }

    /**
     * This method is responsible of reload all existing configurations on the
     * selected categorical variable.
     */
    public void btnLoadConfigurationClick() {
        //recargar las configuraciones existentes
        //System.out.println("inicia carga de configuraciones");
        currentConfigurationSelected = new ArrayList<>();
        configurationsList = new ArrayList<>();
        List<IndicatorsConfigurations> indicatorsConfigurationsList = indicatorsConfigurationsFacade.findAll();
        for (int i = 0; i < indicatorsConfigurationsList.size(); i++) {
            if (currentVariablesCrossSelected.get(0).compareTo(indicatorsConfigurationsList.get(i).getVariableName()) == 0) {
                configurationsList.add(indicatorsConfigurationsList.get(i).getConfigurationName());
            }
            //System.out.println("inicia carga de configuraciones");
        }
    }

    /**
     * This method allows the user to save a configuration to a variable
     * specifies, to save the user must assign a name and this name must be
     * different to the names of the settings already realized.
     *
     * @return
     */
    public int btnSaveConfigurationClick() {
        if (newConfigurationName.trim().length() == 0) {//VALOR INICIAL INGRESADO
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Digite el nombre para la nueva configuración"));
            return 0;
        }
        //determino si el nombre ya esta ingresado
        boolean founConfiguration = false;
        List<IndicatorsConfigurations> indicatorsConfigurationsList = indicatorsConfigurationsFacade.findAll();
        for (int i = 0; i < indicatorsConfigurationsList.size(); i++) {
            if (indicatorsConfigurationsList.get(i).getConfigurationName().compareTo(newConfigurationName) == 0) {
                founConfiguration = true;
                break;
            }
        }
        if (!founConfiguration) {
            if (!currentCategoricalValuesList.isEmpty()) {
                IndicatorsConfigurations newIndicatorsConfigurations = new IndicatorsConfigurations(indicatorsConfigurationsFacade.findMax() + 1);
                //System.out.println("El valor de id_configuration es : " + newIndicatorsConfigurations.getConfigurationId());
                newIndicatorsConfigurations.setConfigurationName(newConfigurationName);
                newIndicatorsConfigurations.setVariableName(firstVariablesCrossSelected);
                String configuredValues = "";
                for (int i = 0; i < currentCategoricalValuesList.size(); i++) {
                    configuredValues = configuredValues + currentCategoricalValuesList.get(i);
                    if (i != currentCategoricalValuesList.size() - 1) {
                        configuredValues = configuredValues + "\t";
                    }
                }
                newIndicatorsConfigurations.setConfiguredValues(configuredValues);
                indicatorsConfigurationsFacade.create(newIndicatorsConfigurations);
                btnLoadConfigurationClick();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "La cofiguración ha sido almacenada"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No existen categorias para almacenar"));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El nombre registrado ya fue ingresado, por favor digite uno diferente"));
        }

        return 0;
    }

    /**
     * This method allows the user to add a new category to variables of open
     * fields such as the age where the user can define ranges.
     *
     * @return
     */
    public int btnAddCategoricalValueClick() {
        if (initialValue.trim().length() == 0) {//VALOR INICIAL INGRESADO
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Digite un valor inicial"));
            return 0;
        }
        if (endValue.trim().length() == 0) {//VALOR FINAL INGRESADO
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Digite un valor final"));
            return 0;
        }

        if (currentVariableConfiguring != null) {
            if (currentVariableConfiguring.getName().compareTo("hora") == 0) {
                addCategoricalHour();
                return 0;
            }
            if (currentVariableConfiguring.getName().compareTo("edad") == 0) {
                addCategoricalAge();
                return 0;
            }
        }
        return 0;
    }

    private int addCategoricalHour() {
        int i;
        int e;
        String msj = "";
        try {
            //asadero conjunto la colina 3207065386

            //Valor inicial y final numericos
            i = Integer.parseInt(initialValue);
            e = Integer.parseInt(endValue);

            if (i >= e) {//valor inicial mayor que valor final
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El valor inicial debe ser menor que el valor final"));
                return 0;
            }
            if (i < 0 || i > 23) {//valores entre 0 y 23
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El valor inicial para hora debe estar entre 0 y 23"));
                return 0;
            }
            if (e < 0 || e > 23) {//valores entre 0 y 23
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El valor final para hora debe estar entre 0 y 23"));
                return 0;
            }

            //los valores no esten contenidos dentro de otro
            int initialValueFoundInteger;
            int endValueFoundInteger;
            int initialValueAddInteger;
            int endValueAddInteger;
            boolean continueProcces = true;

            for (int j = 0; j < currentVariableConfiguring.getValuesConfigured().size(); j++) {
                if (currentVariableConfiguring.getValuesConfigured().get(j).compareToIgnoreCase("SIN DATO") != 0) {
                    String[] splitValues = currentVariableConfiguring.getValuesConfigured().get(j).split("/");
                    initialValueFoundInteger = Integer.parseInt(splitValues[0].split(":")[0]);
                    endValueFoundInteger = Integer.parseInt(splitValues[1].split(":")[0]);
                    initialValueAddInteger = Integer.parseInt(initialValue);
                    endValueAddInteger = Integer.parseInt(endValue);
                    for (int k = initialValueFoundInteger; k <= endValueFoundInteger; k++) {
                        for (int l = initialValueAddInteger; l < endValueAddInteger; l++) {
                            if (k == l) {
                                continueProcces = false;
                                msj = "El nuevo rango " + initialValue + ":00/" + endValue + ":59 foma parte del rango " + currentVariableConfiguring.getValuesConfigured().get(j);
                            }
                            if (!continueProcces) {
                                break;
                            }
                        }
                        if (!continueProcces) {
                            break;
                        }
                    }
                    if (!continueProcces) {
                        break;
                    }
                }
            }
            if (!continueProcces) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", msj));
                return 0;
            }

            //completo los valores a dos cifras
            if (initialValue.length() == 1) {
                initialValue = "0" + initialValue;
            }
            if (endValue.length() == 1) {
                endValue = "0" + endValue;
            }

            initialValue = initialValue + ":00";
            endValue = endValue + ":59";

            //se procede a guardar la nueva categoria
            currentVariableConfiguring.getValuesConfigured().add(initialValue + "/" + endValue);



            currentCategoricalValuesList = new ArrayList<>();
            for (int j = 0; j < currentVariableConfiguring.getValuesConfigured().size(); j++) {
                currentCategoricalValuesList.add(currentVariableConfiguring.getValuesConfigured().get(j));
            }
            initialValue = "";
            endValue = "";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha adicionado la categoría"));
            return 0;


            //MODIFICAR XHTM PARA QUE SEA SOLO DOS CIFRAS
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Los valores inicial y final para hora deben ser numéricos y estar entre 0 y 23"));
            return 0;
        }
    }

    /**
     * This method allows adding a new range to the Year category, for it must
     * realize a series of validations for example the initial year must be less
     * than the final year, the range defined should not be within an existing
     * range, the input values must be numeric.
     *
     * @return
     */
    private int addCategoricalAge() {
        int i;
        int e;
        boolean isN = false;//determinar si valor final es N
        if (endValue.compareToIgnoreCase("n") == 0) {
            endValue = "201";
            isN = true;
        }

        try {
            //valor inicial y final deben ser numericos
            i = Integer.parseInt(initialValue);
            e = Integer.parseInt(endValue);

            if (i < 0 && e < 0) {//valor inicial y final mayor o igual a cero
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Los valores deben ser iguales o mayores que cero"));
                return 0;
            }
            if (i >= e) {//Valor inicial mayor que valor final
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El valor inicial debe ser menor que el valor final"));
                return 0;
            }
            if (isN) {
                if (i >= 200) {//inicial menore que 200
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Los valores deben ser iguales o mayores que cero"));
                    return 0;
                }
            } else {
                if (i >= 200 || e >= 200) {//valor inicial y final menores que 200
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Los valores deben ser iguales o mayores que cero"));
                    return 0;
                }
            }

            //los valores no esten contenidos dentro de otro
            int initialValueFoundInteger;
            int endValueFoundInteger;
            int initialValueAddInteger;
            int endValueAddInteger;
            boolean continueProcces = true;
            String msj = "";
            for (int j = 0; j < currentVariableConfiguring.getValuesConfigured().size(); j++) {
                if (currentVariableConfiguring.getValuesConfigured().get(j).compareToIgnoreCase("SIN DATO") != 0) {
                    String[] splitValues = currentVariableConfiguring.getValuesConfigured().get(j).split("/");
                    initialValueFoundInteger = Integer.parseInt(splitValues[0]);
                    if (splitValues[1].compareTo("n") == 0) {
                        endValueFoundInteger = 201;
                    } else {
                        endValueFoundInteger = Integer.parseInt(splitValues[1]);
                    }
                    initialValueAddInteger = Integer.parseInt(initialValue);
                    endValueAddInteger = Integer.parseInt(endValue);
                    for (int k = initialValueFoundInteger; k <= endValueFoundInteger; k++) {
                        for (int l = initialValueAddInteger; l < endValueAddInteger; l++) {
                            if (k == l) {
                                continueProcces = false;
                                if (isN) {
                                    msj = "El nuevo rango " + initialValue + "/n foma parte del rango " + currentVariableConfiguring.getValuesConfigured().get(j);
                                } else {
                                    msj = "El nuevo rango " + initialValue + "/" + endValue + " foma parte del rango " + currentVariableConfiguring.getValuesConfigured().get(j);
                                }
                            }
                            if (!continueProcces) {
                                break;
                            }
                        }
                        if (!continueProcces) {
                            break;
                        }
                    }
                    if (!continueProcces) {
                        break;
                    }
                }
            }
            if (!continueProcces) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", msj));
                return 0;
            }
            if (isN) {
                endValue = "n";
            }
            if (initialValue.length() == 1) {
                initialValue = "0" + initialValue;
            }
            if (endValue.length() == 1 && endValue.compareTo("n") != 0) {
                endValue = "0" + endValue;
            }
            currentVariableConfiguring.getValuesConfigured().add(initialValue + "/" + endValue);
            currentCategoricalValuesList = new ArrayList<>();
            for (int j = 0; j < currentVariableConfiguring.getValuesConfigured().size(); j++) {
                currentCategoricalValuesList.add(currentVariableConfiguring.getValuesConfigured().get(j));
            }
            initialValue = "";
            endValue = "";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha adicionado la categoría"));
            return 0;
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Los valores deben ser numéricos"));
            return 0;
        }
    }

    /**
     * This method allows the user remove a category values that the user wishes
     * to do, the user must select the value the user to want to remove and then
     * to press the button.
     */
    public void btnRemoveCategoryValueClick() {
        //btnRemoveCategoricalValueDisabled = false;
        if (currentVariableConfiguring != null) {
            for (int i = 0; i < currentCategoricalValuesSelected.size(); i++) {
                for (int j = 0; j < currentVariableConfiguring.getValuesConfigured().size(); j++) {
                    if (currentVariableConfiguring.getValuesConfigured().get(j).compareTo(currentCategoricalValuesSelected.get(i)) == 0) {
                        currentVariableConfiguring.getValuesConfigured().remove(j);
                        break;
                    }
                }
            }
            //recargo la lista de valores de la categoria
            currentCategoricalValuesList = new ArrayList<>();
            for (int j = 0; j < currentVariableConfiguring.getValuesConfigured().size(); j++) {
                currentCategoricalValuesList.add(currentVariableConfiguring.getValuesConfigured().get(j));
            }
        }
    }

    /**
     * This method is responsible of restore the values of a categorical
     * variable, if the values were modified or removed, then this method sets
     * the default values that had this variable.
     */
    public void btnResetCategoryListClick() {
        currentCategoricalValuesSelected = new ArrayList<>();
        //btnRemoveCategoricalValueDisabled = false;
        if (currentVariableConfiguring != null) {
            //paso los elementos de la lista: values a valuesConfiguration
            currentVariableConfiguring.setValuesConfigured(new ArrayList<String>());
            for (int j = 0; j < currentVariableConfiguring.getValues().size(); j++) {
                currentVariableConfiguring.getValuesConfigured().add(currentVariableConfiguring.getValues().get(j));
            }
            //recargo la lista de valores de la categoria
            currentCategoricalValuesList = new ArrayList<>();
            for (int j = 0; j < currentVariableConfiguring.getValues().size(); j++) {
                currentCategoricalValuesList.add(currentVariableConfiguring.getValues().get(j));
            }
        }
    }

    /**
     * This method is responsible of enable or disable the buttons to add and
     * remove variables according to the selection realized by that user.
     */
    public void changeVariable() {
        btnAddVariableDisabled = true;
        btnRemoveVariableDisabled = true;
        if (currentAvailableVariablesNamesSelected != null) {
            if (!currentAvailableVariablesNamesSelected.isEmpty()) {
                btnAddVariableDisabled = false;
            }
        }
        if (currentVariablesCrossSelected != null) {
            if (!currentVariablesCrossSelected.isEmpty()) {
                btnRemoveVariableDisabled = false;
            }
        }
    }

    /**
     * This method is responsible of load the values of a categorical variable
     * selected.
     */
    public void changeCrossVariable() {
        btnRemoveVariableDisabled = true;
        btnRemoveCategoricalValueDisabled = true;
        currentCategoricalValuesSelected = new ArrayList<>();
        currentVariableConfiguring = null;
        initialValue = "";
        endValue = "";
        //cargo la lista de valores categoricos para la variable
        if (!currentVariablesCrossSelected.isEmpty()) {
            for (String str : currentVariablesCrossSelected) {
                if (str.contains("municipio")) {
                    currentVariablesCrossSelected = new ArrayList<>();
                    return;
                }
            }
            btnRemoveCategoricalValueDisabled = true;
            btnRemoveVariableDisabled = false;
            firstVariablesCrossSelected = currentVariablesCrossSelected.get(0);
            //filtro los años segun la fecha
            currentCategoricalValuesSelected = new ArrayList<>();
            for (int i = 0; i < completeListOfVariableData.size(); i++) {
                if (completeListOfVariableData.get(i).getName().compareTo(firstVariablesCrossSelected) == 0) {
                    if (completeListOfVariableData.get(i).getGeneric_table().compareTo("year") == 0) {
                        completeListOfVariableData.get(i).filterYear(initialDate, endDate);
                    }
                    currentCategoricalValuesList = completeListOfVariableData.get(i).getValuesConfigured();
                    currentVariableConfiguring = completeListOfVariableData.get(i);
                    btnAddCategoricalValueDisabled = !currentVariableConfiguring.isConfigurable();
                    break;
                }
            }
        }
    }

    /**
     * This method is responsible for adding a new variable selected by the
     * user, this is done to make the cross of variables.
     */
    public void addVariableClick() {
        String error = "";
        boolean nextStep = true;
        if (currentAvailableVariablesNamesSelected == null) {
            error = "Debe seleccionarse una variable";
            nextStep = false;
        }
        if (nextStep) {
            for (int i = 0; i < variablesList.size(); i++) {
                for (int j = 0; j < currentAvailableVariablesNamesSelected.size(); j++) {
                    //System.out.println("comparar: " + variablesList.get(i)+ " CON "+currentAvailableVariablesNamesSelected.get(j));
                    if (variablesList.get(i).compareTo(currentAvailableVariablesNamesSelected.get(j)) == 0) {//esta es la variable encontrada que saldra de la lista                    
                        //System.out.println("quitar: " + variablesList.get(i));
                        variablesList.remove(i);//la quito de este listado                    
                        listOfCrossVariablesNames.add(currentAvailableVariablesNamesSelected.get(j));//la agrego al otro                        
                        btnAddVariableDisabled = true;
                        i = -1;

                        if (currentAvailableVariablesNamesSelected.get(j).compareTo("zona") == 0) {
                            for (int k = 0; k < listOfCrossVariablesNames.size(); k++) {//si hay comuna en listOfCrossVariablesNames se quita
                                if (listOfCrossVariablesNames.get(k).compareTo("comuna") == 0) {
                                    variablesList.add(listOfCrossVariablesNames.get(k));
                                    listOfCrossVariablesNames.remove(k);
                                    k = -1;
                                }
                            }
                        }
                        if (currentAvailableVariablesNamesSelected.get(j).compareTo("comuna") == 0) {
                            for (int k = 0; k < listOfCrossVariablesNames.size(); k++) {//si hay zona en listOfCrossVariablesNames se quita
                                if (listOfCrossVariablesNames.get(k).compareTo("zona") == 0) {
                                    variablesList.add(listOfCrossVariablesNames.get(k));
                                    listOfCrossVariablesNames.remove(k);
                                    k = -1;
                                }
                            }
                        }


                        break;
                    }
                }
            }
            currentAvailableVariablesNamesSelected = null;
        }
        if (error.length() != 0) {//hay  errores al relacionar la variables 
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Alerta", error));
        }
    }

    /**
     * This method is responsible to remove a variable selected by the user, the
     * variables that can be removed are in the box "variables a cruzar".
     */
    public void removeVariableClick() {
        String error = "";
        boolean nextStep = true;
        if (currentVariablesCrossSelected == null) {
            error = "Debe seleccionarse una variable";
            nextStep = false;
        }
        if (nextStep) {
            for (String str : currentVariablesCrossSelected) {
                if (str.contains("municipio") || str.contains("zona") || str.contains("comuna")) {
                    currentVariablesCrossSelected = new ArrayList<>();
                    return;
                }
            }
            for (int i = 0; i < listOfCrossVariablesNames.size(); i++) {
                for (int j = 0; j < currentVariablesCrossSelected.size(); j++) {
                    if (listOfCrossVariablesNames.get(i).compareTo(currentVariablesCrossSelected.get(j)) == 0) {//esta es la variable encontrada que saldra de la lista                    
                        listOfCrossVariablesNames.remove(i);//la quito de este listado                    
                        variablesList.add(currentVariablesCrossSelected.get(j));//la agrego al otro                        
                        btnRemoveVariableDisabled = true;
                        i = -1;
                        break;
                    }
                }
            }
            currentVariablesCrossSelected = null;
        }
        if (error.length() != 0) {//hay  errores al relacionar la variables 
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Alerta", error));
        }
    }

    /**
     * This method is responsible to take the results of cross of variables and
     * assemble the structure DefaultCategoryDataset for that the library do the
     * bar graph, lines or areas.
     *
     * @return
     */
    private DefaultCategoryDataset createDataSet() {
        /*
         * creacion del conjunto de datos para generar grafico
         */
        DefaultCategoryDataset datSet = new DefaultCategoryDataset();
        try {
            int pos = 0;
            double value;
            sql = ""
                    + " SELECT \n"
                    + "    * \n"
                    + " FROM \n"
                    + "    indicators_records \n"
                    + " WHERE \n"
                    + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "    indicator_id = " + currentIndicator.getIndicatorId() + "  \n";
            ResultSet rs;

            variablesName = "Desagregado por: ";
            if (currentTemporalDisaggregation.compareTo("Anual") == 0) {
                variablesName = variablesName + "años, ";
            }
            if (currentTemporalDisaggregation.compareTo("Mensual") == 0) {
                variablesName = variablesName + "meses, ";
            }
//            if (currentTemporalDisaggregation.compareTo("Diaria") == 0) {
//                variablesName = variablesName + "dias, ";
//            }

            if (variablesCrossData.size() == 2) {
                rs = connectionJdbcMB.consult(sql + " ORDER BY record_id");
                while (rs.next()) {
                    value = Double.parseDouble(formateador.format(Double.parseDouble("0")).replace(",", "."));
                    if (rs.getString("count").compareTo("0") != 0 && rs.getInt("population") != 0) {
                        value = Double.parseDouble(formateador.format((Double.parseDouble(rs.getString("count")) / rs.getInt("population")) * multiplierK).replace(",", "."));
                    }
                    datSet.setValue(value, determineHeader(rs.getString("column_2")), determineHeader(rs.getString("column_1")));
                }
                variablesName = variablesName + variablesCrossData.get(1).getName();
            }
            if (variablesCrossData.size() == 3) {
                //determino el numero de columna a filtrar (variable en variableCrossData                
                for (int i = 0; i < variablesCrossData.size(); i++) {
                    if (variablesCrossData.get(i).getName().compareTo(currentVariableGraph) == 0) {
                        pos = i;
                        break;
                    }
                }
                //adiciono la instruccion WHERE a la consulta
                sql = sql + " AND column_" + String.valueOf(pos + 1) + " LIKE '" + currentValueGraph + "' ";
                rs = connectionJdbcMB.consult(sql + " ORDER BY record_id");
                if (pos == 1) {
                    variablesName = variablesName + variablesCrossData.get(2).getName() + ", " + variablesCrossData.get(1).getName() + " = " + determineHeader(currentValueGraph);
                    while (rs.next()) {
                        value = Double.parseDouble(formateador.format(Double.parseDouble("0")).replace(",", "."));
                        if (rs.getString("count").compareTo("0") != 0 && rs.getInt("population") != 0) {
                            value = Double.parseDouble(formateador.format((Double.parseDouble(rs.getString("count")) / rs.getInt("population")) * multiplierK).replace(",", "."));
                        }
                        datSet.setValue(value, determineHeader(rs.getString("column_3")), determineHeader(rs.getString("column_1")));
                    }
                    categoryAxixLabel = variablesCrossData.get(2).getName();
                }
                if (pos == 2) {
                    variablesName = variablesName + variablesCrossData.get(1).getName() + ", " + variablesCrossData.get(2).getName() + " = " + determineHeader(currentValueGraph);
                    while (rs.next()) {
                        value = Double.parseDouble(formateador.format(Double.parseDouble("0")).replace(",", "."));
                        if (rs.getString("count").compareTo("0") != 0 && rs.getInt("population") != 0) {
                            value = Double.parseDouble(formateador.format((Double.parseDouble(rs.getString("count")) / rs.getInt("population")) * multiplierK).replace(",", "."));
                        }
                        datSet.setValue(value, determineHeader(rs.getString("column_2")), determineHeader(rs.getString("column_1")));
                    }
                    categoryAxixLabel = variablesCrossData.get(1).getName();
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error 4 en " + this.getClass().getName() + ":" + ex.toString());
        }
        return datSet;
    }

    /**
     * This method is responsible of paint the rectangle of the diagram of bars
     * that show with the result of the cross of variable
     *
     * @param c2
     * @param c1
     * @return
     */
    private TexturePaint createTexturePaint(Color c2, Color c1) {
        // 0,5  1,5  2,5  3,5  4,5  5,5
        // 0,4  1,4  2,4  3,4  4,4  5,4
        // 0,3  1,3  2,3  3,3  4,3  5,3
        // 0,2  1,2  2,2  3,2  4,2  5,2
        // 0,1  1,1  2,1  3,1  4,1  5,1
        // 0,0  1,0  2,0  3,0  4,0  5,0
        BufferedImage bi = new BufferedImage(6, 6, BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.getGraphics();
        g.setColor(c1);
        g.fillRect(0, 0, 6, 6);
        g.setColor(c2);
        TexturePaint tp = null;
        if (typeFill < 1 || typeFill > 7) {
            typeFill = 1;
        }
        switch (typeFill) {
            case 1:
                g.drawLine(0, 3, 5, 3);//cuadros
                g.drawLine(3, 5, 3, 0);//cuadros
                tp = new TexturePaint(bi, new Rectangle2D.Double(0, 0, 6, 6));
                break;
            case 2:
                g.drawLine(0, 0, 5, 5);// abajo izq > arriba derecha
                g.drawLine(1, 0, 4, 5);// abajo izq > arriba derecha
                tp = new TexturePaint(bi, new Rectangle2D.Double(0, 0, 6, 6));
                break;
            case 3:
                g.drawOval(2, 3, 2, 2);//circulos
                tp = new TexturePaint(bi, new Rectangle2D.Double(0, 0, 6, 6));
                break;
            case 4:
                g.drawLine(0, 5, 5, 5);//ladrillo
                g.drawLine(0, 5, 0, 2);
                g.drawLine(0, 2, 5, 2);
                g.drawLine(3, 0, 3, 2);
                tp = new TexturePaint(bi, new Rectangle2D.Double(0, 0, 6, 6));
                break;
            case 5:
                g.drawLine(1, 1, 4, 4);//cruz
                g.drawLine(1, 4, 4, 1);//cruz
                tp = new TexturePaint(bi, new Rectangle2D.Double(0, 0, 6, 6));
                break;
            case 6:
                g.drawLine(0, 0, 5, 5);//rombos
                g.drawLine(0, 5, 5, 0);//rombos
                tp = new TexturePaint(bi, new Rectangle2D.Double(0, 0, 6, 6));
                break;
            case 7:
                g.drawLine(0, 5, 5, 0);// abajo der > arriba izq
                g.drawLine(1, 5, 4, 0);// abajo der > arriba izq
                tp = new TexturePaint(bi, new Rectangle2D.Double(0, 0, 6, 6));
                break;
        }
        typeFill++;
        return tp;

    }

    /**
     * This method is responsible for obtaining the color code corresponding to
     * the assigned identification sent as a parameter.
     *
     * @param id
     * @return
     */
    private Color getColorById(int id) {
        switch (id) {
            case 2:
                return Color.RED;
            case 3:
                return Color.YELLOW;
            case 4:
                return Color.CYAN;
            case 5:
                return new Color(128, 0, 128);//MORADO                
            case 6:
                return Color.GREEN;
            case 7:
                return Color.MAGENTA;
            case 8:
                return Color.LIGHT_GRAY;
            case 9:
                return new Color(20, 20, 255);//AZUL
            case 0:
                return new Color(139, 69, 19);//CAFE
            case 1:
                return new Color(40, 200, 150);//VERDE PERLA                
        }
        return new Color(10, 10, 10);
    }

    /**
     * This method is responsible of determine which color to be paint the
     * rectangles show the results of cross of variables.
     *
     * @return
     */
    private Paint determineColor() {
        /*
         * determinar con que color pintar de 99 posibles
         */
        colorId++;
        int modPos;
        if (showFrames) {
            modPos = colorId % 99;
        } else {
            modPos = colorId % 20;
        }
        switch (modPos) {
            case 0:
                return new Color(255, 0, 0);//ROJO                
            case 1:
                return new Color(255, 255, 0);//AMARILLO
            case 2:
                return new Color(20, 20, 255);//AZUL
            case 3:
                return new Color(255, 0, 255);//MAGENTA                
            case 4:
                return new Color(0, 255, 0);//VERDE                
            case 5:
                return new Color(0, 255, 255);//CYAN                
            case 6:
                return new Color(0, 140, 0);//VERDE OSCURO
            case 7:
                return new Color(255, 130, 0);//NARANJA                
            case 8:
                return new Color(128, 0, 128);//MORADO                
            case 9:
                return new Color(160, 0, 0);//ROJO OSCURO
            case 10:
                return new Color(0, 0, 160);//AZUL OSCURO
            case 11:
                return new Color(0, 139, 139);//CYAN OSCURO
            case 12:
                return new Color(139, 69, 19);//CAFE
            case 13:
                return new Color(255, 192, 203);//ROJO CLARO
            case 14:
                return new Color(152, 252, 152);//VERDE CLARO
            case 15:
                return new Color(173, 216, 230);//AZUL CLARO
            case 16:
                return new Color(255, 255, 140);//AMARILLO CLARO                            
            case 17:
                return new Color(40, 200, 150);//VERDE PERLA
            case 18:
                return new Color(189, 183, 80);//AMARILLO OSCURO
            case 19:
                return new Color(190, 150, 210);//LILA            
            default://se debe crear una trama
                String posStr = String.valueOf(colorId);//tomo este valor y lo separo
                int color1;//color segun el primer digito de colorId
                int color2;//color segun el segundo digito de colorId
                color1 = Integer.parseInt(String.valueOf(posStr.charAt(0)));
                color2 = Integer.parseInt(String.valueOf(posStr.charAt(1)));
                if (color1 == color2) {//no pueden ser del mismo color
                    return determineColor();//se vuelve a llamar para que incremente el color
                }
                return createTexturePaint(getColorById(color1), getColorById(color2));
        }
    }

    /**
     * This method is responsible for creating an image with all the results
     * obtained from the crossing of variables.
     */
    public void createImage() {
        if (!variablesCrossData.isEmpty()) {
            try {
                JFreeChart chart = null;
                dataset = createDataSet();
                //-----CREACION DEL TITULO
                indicatorName = currentIndicator.getIndicatorName() + " - Municipo de Pasto.\n";
                indicatorName = indicatorName + variablesName + "\nPeriodo: " + sdf.format(initialDate) + " a " + sdf.format(endDate) + " - Cifras por " + currentMultipler + " habitantes";
                //-----SELECCION DE TIPO DE GRAFICO
                if (currentTypeGraph.compareTo("barras") == 0) {
                    chart = ChartFactory.createBarChart(indicatorName, categoryAxixLabel, "Tasa Específica", dataset, PlotOrientation.VERTICAL, true, true, false);
                    CategoryPlot plot = (CategoryPlot) chart.getPlot();
                    ((BarRenderer) plot.getRenderer()).setBarPainter(new StandardBarPainter());//quitar gradiente
                }
                if (currentTypeGraph.compareTo("lineas") == 0) {
                    chart = ChartFactory.createLineChart(indicatorName, categoryAxixLabel, "Tasa Específica", dataset, PlotOrientation.VERTICAL, true, true, false);
                    CategoryPlot plot = (CategoryPlot) chart.getPlot();
                    ((LineAndShapeRenderer) plot.getRenderer()).setShapesVisible(true);//mostrar vertices
                    ((LineAndShapeRenderer) plot.getRenderer()).setShape(new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0));//tipo de vertices(circulo)                
                    plot.getRenderer().setStroke(new BasicStroke(2));//grosor de linea
                }
                if (currentTypeGraph.compareTo("barras apiladas") == 0) {
                    chart = ChartFactory.createStackedBarChart(indicatorName, categoryAxixLabel, "Tasa Específica", dataset, PlotOrientation.VERTICAL, true, true, false);
                    CategoryPlot plot = (CategoryPlot) chart.getPlot();
                    ((BarRenderer) plot.getRenderer()).setBarPainter(new StandardBarPainter());//quitar gradiente
                }
                if (currentTypeGraph.compareTo("areas") == 0) {
                    chart = ChartFactory.createAreaChart(indicatorName, categoryAxixLabel, "Tasa Específica", dataset, PlotOrientation.VERTICAL, true, true, false);
                    ((CategoryPlot) chart.getPlot()).setForegroundAlpha(0.4f);//transparencia
                }
                //-------------------------------------------
                //--REORDENAMIENTO DE LAS SERIES
                CategoryPlot plot = (CategoryPlot) chart.getPlot();
//                //if (!showEmpty) {                
                LegendItemCollection itemsLeyendaAnterior = plot.getLegendItems();
                LegendItemCollection itemsLeyendaNuevo = new LegendItemCollection();
                Paint co;
                LegendItem li;
                List<String> values = variablesCrossData.get(1).getValuesConfigured();
                colorId = -1;//color relleno
                typeFill = 1;//tipo de relleno
                for (int posValue = 0; posValue < values.size(); posValue++) {
                    for (int posLeyend = 0; posLeyend < itemsLeyendaAnterior.getItemCount(); posLeyend++) {
                        if (determineHeader(values.get(posValue)).compareTo(itemsLeyendaAnterior.get(posLeyend).getLabel()) == 0) {
                            co = determineColor();
                            li = itemsLeyendaAnterior.get(posLeyend);
                            li.setFillPaint(co);
                            li.setLinePaint(co);
                            li.setShape(new Rectangle2D.Float(0, 0, 10, 10));
                            li.setOutlinePaint(co);
                            plot.getRenderer().setSeriesPaint(posLeyend, co);//cambio color
                            itemsLeyendaNuevo.add(li);
                        }
                    }
                }
                LineLegendItemSource source = new LineLegendItemSource(itemsLeyendaNuevo);
                chart.removeLegend();
                LegendTitle legend = new LegendTitle(source);
                chart.addLegend(legend);
                chart.getLegend().setPosition(RectangleEdge.BOTTOM);
                chart.getLegend().visible = true;
                //}
                //-----------------------------------------
                //-----CONFIGURACIONES DEL GRAFICO
                chart.getTitle().setFont(new Font("arial", Font.BOLD, 15));//fuente titulo

                plot.setBackgroundPaint(Color.white);//fondo blanco
                plot.setOutlineVisible(false);//grafico sin borde             
                ((CategoryAxis) plot.getDomainAxis()).setCategoryLabelPositions(CategoryLabelPositions.UP_45);//rotar etiquetas
                if (showItems) {//mostrar items
                    CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("0.00"));
                    //CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("0"));
                    plot.getRenderer().setItemLabelGenerator(generator);
                    plot.getRenderer().setItemLabelsVisible(true);
                    plot.getRenderer().setItemLabelFont(new Font("arial", Font.BOLD, sizeFont));
                }
                //----------GENERAR PNG A PARTIR DEL JCHART
                File chartFile = new File("grafico");
                ChartUtilities.saveChartAsPNG(chartFile, chart, widthGraph, heightGraph);
                chartImage = new DefaultStreamedContent(new FileInputStream(chartFile), "image/png");
            } catch (Exception e) {
                System.out.println("Error 5 en " + this.getClass().getName() + ":" + e.toString());
            }
        }
    }

    /**
     * This method is responsible to restore all modifications have been
     * realized to the form that allows to realize the cross of variables as
     * well as the settings for the categorical variables.
     */
    public void reset() {
        showGraphic = false;
        showTableResult = false;
        btnExportDisabled = true;
        dataTableHtml = "";
        chartImage = null;
        currentVariableConfiguring = null;
        currentAvailableVariablesNamesSelected = new ArrayList<>();
        currentVariablesCrossSelected = new ArrayList<>();
        firstVariablesCrossSelected = null;
        currentCategoricalValuesList = new ArrayList<>();
        currentCategoricalValuesSelected = new ArrayList<>();
        titlePage = currentIndicator.getIndicatorGroup();
        titleIndicator = currentIndicator.getIndicatorGroup();
        subTitleIndicator = currentIndicator.getIndicatorName();
        completeListOfVariableData = getVariablesIndicator();
//        variablesGraph = new ArrayList<>();
        valuesGraph = new ArrayList<>();
        currentValueGraph = "";
        currentVariableGraph = "";
        numberCross = currentIndicator.getNumberCross();
        variablesList = new ArrayList<>();//SelectItem[completeListOfVariableData.size()];
        for (int i = 0; i < completeListOfVariableData.size(); i++) {
            variablesList.add(completeListOfVariableData.get(i).getName());
        }
        dynamicDataTableGroup = new OutputPanel();//creo el panel grid


        btnAddVariableDisabled = true;
        btnRemoveVariableDisabled = true;
        currentAvailableVariablesNamesSelected = null;
        currentVariablesCrossSelected = null;

        listOfCrossVariablesNames = new ArrayList<>();//SelectItem[completeListOfVariableData.size()];
        completeListOfVariableData.add(createVariable("comuna", "communes", false, ""));//agrego de ultima la desagregacion espacial
        listOfCrossVariablesNames.add("comuna");//agrego de primera en la lista de nombres a cruzar                        

        spatialDisaggregationTypes = new ArrayList<>();
        spatialDisaggregationTypes.add("Zona");
        spatialDisaggregationTypes.add("Comuna");
        spatialDisaggregationTypes.add("Municipio");
        currentSpatialDisaggregation = "Comuna";

        temporalDisaggregationTypes = new ArrayList<>();
        temporalDisaggregationTypes.add("Anual");
        temporalDisaggregationTypes.add("Mensual");
        currentTemporalDisaggregation = "Mensual";

        typesGraph = new ArrayList<>();
        typesGraph.add("lineas");
        typesGraph.add("barras");
        typesGraph.add("barras apiladas");
        typesGraph.add("areas");

        multiplers = new ArrayList<>();
        multiplers.add("1.000");
        multiplers.add("10.000");
        multiplers.add("100.000");
        multiplers.add("1'000.000");
        currentMultipler = "100.000";
    }

    /**
     * This method is responsible to add a spatial disaggregation in
     * box"variables a cruzar". this variable is added when it is selected in
     * the list of variables of spatial disaggregation
     */
    public void changeSpatialDisaggregation() {

        btnAddVariableDisabled = true;
        currentAvailableVariablesNamesSelected = null;
        //elimino la primera de variables a cruzar y la ultima de listaVariblesData
        listOfCrossVariablesNames.remove(0);
        completeListOfVariableData.remove(completeListOfVariableData.size() - 1);
        //copeo las demas variables
        List<String> listOfCrossVariablesNamesAux = new ArrayList<>();
        for (int i = 0; i < listOfCrossVariablesNames.size(); i++) {
            listOfCrossVariablesNamesAux.add(listOfCrossVariablesNames.get(i));
            listOfCrossVariablesNames.remove(0);
            i--;
        }
        if (currentSpatialDisaggregation.compareTo("Comuna") == 0) {
            completeListOfVariableData.add(createVariable("comuna", "communes", false, ""));//agrego de ultima la desagregacion espacial
            listOfCrossVariablesNames.add("comuna");//agrego de primera en la lista de nombres a cruzar                        
        }
        if (currentSpatialDisaggregation.compareTo("Zona") == 0) {
            completeListOfVariableData.add(createVariable("zona", "areas", false, ""));//agrego de ultima la desagregacion espacial
            listOfCrossVariablesNames.add("zona");//agrego de primera en la lista de nombres a cruzar                                    
        }
        if (currentSpatialDisaggregation.compareTo("Municipio") == 0) {
            completeListOfVariableData.add(createVariable("municipio", "municipality", false, ""));//agrego de ultima la desagregacion espacial
            listOfCrossVariablesNames.add("municipio");//agrego de primera en la lista de nombres a cruzar                                    
        }
        //agrego las variables que copie anteriormente(todas menos la primera que este en el listado de variables acruzar)
        for (int i = 0; i < listOfCrossVariablesNamesAux.size(); i++) {
            listOfCrossVariablesNames.add(listOfCrossVariablesNamesAux.get(i));
        }
    }

    /**
     * This method is responsible for find the difference that exist between the
     * dates supplied by the user to perform the cross of variables, also
     * specifies the type of disaggregation that handles the difference in
     * dates.
     *
     * @param date1
     * @param date2
     * @param typeDifference
     * @return
     */
    private int getDateDifference(Date date1, Date date2, String typeDifference) {
        Interval interval = new Interval(new DateTime(date1), (new DateTime(date2)).plusDays(1));
        if (typeDifference.compareTo("anual") == 0) {
            Years years34 = Years.yearsIn(interval);
            //System.out.println("Años" + years34.getYears());
            return years34.getYears();
        } else if (typeDifference.compareTo("mensual") == 0) {
            Months months11 = Months.monthsIn(interval);
            //System.out.println("Meses" + months11.getMonths());
            return months11.getMonths();
        } else if (typeDifference.compareTo("diaria") == 0) {
            Days days15 = Days.daysIn(interval);
            //System.out.println("Dias" + days15.getDays());
            return days15.getDays();
        }
        return 0;
    }

    /**
     * This method is responsible to create the temporal disaggregation to work
     * in variables indicated by the user, the disaggregation may be a day,
     * month or year, this disaggregation is shown in the graph where are all
     * the results of cross of variables.
     *
     * @param initialDate
     * @param endDate
     * @return
     */
    private Variable createTemporalDisaggregationVariable(Date initialDate, Date endDate) {
        Variable newVariable = new Variable("Desagregación temporal", "temporalDisaggregation", false, "");
        int diferenceRank;
        int daysMax;
        Calendar cal1 = Calendar.getInstance();
        ArrayList<String> valuesName = new ArrayList<>();//NOMBRE DE LOS VALORES QUE PUEDE TOMAR LA VARIABLE POR DEFECTO(NOMBRE EN LA CATEGORIA)
        ArrayList<String> valuesId = new ArrayList<>();  //IDENTIFICADORES DE LOS VALORES QUE PUEDE TOMAR LA VARIABLE POR DEFECTO(ID EN LA CATEGORIA)
        ArrayList<String> valuesConf = new ArrayList<>();//NOMBRE DE LOS VALORES CONFIGURADOS POR EL USUARIO QUE PUEDE TOMAR LA VARIABLE        
        // different date might have different offset 
        SimpleDateFormat sdf_s;
        String initialDateString;
        String endDateString;
        if (currentTemporalDisaggregation.compareTo("Diaria") == 0) {
            diferenceRank = getDateDifference(initialDate, endDate, "diaria");
            sdf_s = new SimpleDateFormat("dd MMM yyyy", new Locale("ES"));
            for (int i = 0; i < diferenceRank; i++) {
                cal1.setTime(initialDate);
                cal1.add(Calendar.DATE, i);
                initialDateString = formato.format(cal1.getTime());
                valuesName.add(sdf_s.format(cal1.getTime()));//agrego el dia en formato: 14 Junio 2013
                valuesId.add(initialDateString + "}" + initialDateString);
                valuesConf.add(sdf_s.format(cal1.getTime()));
            }
        }
        if (currentTemporalDisaggregation.compareTo("Mensual") == 0) {
            diferenceRank = getDateDifference(initialDate, endDate, "mensual");
            sdf_s = new SimpleDateFormat("MMM yyyy", new Locale("ES"));
            for (int i = 0; i < diferenceRank; i++) {
                cal1.setTime(initialDate);
                cal1.set(Calendar.DATE, 1);//coloco el dia en 1
                cal1.add(Calendar.MONTH, i);//fecha inicial se la aumenta i meses                
                initialDateString = formato.format(cal1.getTime());
                daysMax = cal1.getActualMaximum(Calendar.DAY_OF_MONTH); // numero maximo de dias del mes
                cal1.set(Calendar.DATE, daysMax);//coloco el dia en el maximo para el mes                
                endDateString = formato.format(cal1.getTime());
                valuesName.add(sdf_s.format(cal1.getTime()));//agrego el dia en formato: Junio 2013
                valuesId.add(initialDateString + "}" + endDateString);
                valuesConf.add(sdf_s.format(cal1.getTime()));
            }
        }
        if (currentTemporalDisaggregation.compareTo("Anual") == 0) {
            diferenceRank = getDateDifference(initialDate, endDate, "anual");
            //System.out.println("(DIFERENCIA EN AÑOS) :" + diferenceRank);
            sdf_s = new SimpleDateFormat("yyyy", new Locale("ES"));
            for (int i = 0; i < diferenceRank; i++) {
                cal1.setTime(initialDate);
                cal1.set(Calendar.DATE, 1);//coloco el dia en 1
                cal1.set(Calendar.MONTH, 0);//coloco el mes en enero(0)
                cal1.add(Calendar.YEAR, i);//fecha inicial se la aumenta i años                
                initialDateString = formato.format(cal1.getTime());
                cal1.set(Calendar.DATE, 31);//coloco el dia en 31
                cal1.set(Calendar.MONTH, 11);//coloco el mes en diciembre(11)
                endDateString = formato.format(cal1.getTime());
                valuesName.add(sdf_s.format(cal1.getTime()));//agrego el dia en formato: Junio 2013
                valuesId.add(initialDateString + "}" + endDateString);
                valuesConf.add(sdf_s.format(cal1.getTime()));
            }
        }
        newVariable.setValues(valuesName);
        newVariable.setValuesId(valuesId);
        newVariable.setValuesConfigured(valuesConf);
        return newVariable;
    }

    /**
     * This method is responsible to create a variable so it can be selected by
     * the user and used to realize the cross of variables, for it is necesary
     * of type of variable, the values correspond to variable and it should be
     * the structure of these values.
     *
     * @param name
     * @param generic_table
     * @param conf
     * @param source_table
     * @return
     */
    private Variable createVariable(String name, String generic_table, boolean conf, String source_table) {
        //conf me indica si es permitida la configuracion de esta variable
        Variable newVariable = new Variable(name, generic_table, conf, source_table);
        //cargo la lista de valores posibles
        ArrayList<String> valuesName = new ArrayList<>();//NOMBRE DE LOS VALORES QUE PUEDE TOMAR LA VARIABLE POR DEFECTO(NOMBRE EN LA CATEGORIA)
        ArrayList<String> valuesId = new ArrayList<>();  //IDENTIFICADORES DE LOS VALORES QUE PUEDE TOMAR LA VARIABLE POR DEFECTO(ID EN LA CATEGORIA)
        ArrayList<String> valuesConf = new ArrayList<>();//NOMBRE DE LOS VALORES CONFIGURADOS POR EL USUARIO QUE PUEDE TOMAR LA VARIABLE        
        int infInt;
        int supInt;
        String infStr;
        String supStr;
        switch (VariablesEnum.convert(generic_table)) {//
            case age:
                infInt = 0;
                supInt = 4;
                for (int i = 0; i < 16; i++) {
                    infStr = String.valueOf(infInt);
                    supStr = String.valueOf(supInt);
                    if (infStr.length() == 1) {
                        infStr = "0" + infStr;
                    }
                    if (supStr.length() == 1) {
                        supStr = "0" + supStr;
                    }
                    valuesName.add(infStr + "/" + supStr);
                    valuesConf.add(infStr + "/" + supStr);
                    valuesId.add(infStr + "/" + supStr);
                    infInt = infInt + 5;
                    supInt = supInt + 5;
                }
                valuesName.add("80/n");
                valuesConf.add("80/n");
                valuesId.add("80/n");
                break;
            case injuries_fatal:
                valuesId.add("10");
                valuesName.add("HOMICIDIO");
                valuesConf.add("HOMICIDIO");
                valuesId.add("11");
                valuesName.add("MUERTE EN ACCIDENTE DE TRANSITO");
                valuesConf.add("MUERTE EN ACCIDENTE DE TRANSITO");
                valuesId.add("12");
                valuesName.add("SUICIDIO");
                valuesConf.add("SUICIDIO");
                valuesId.add("13");
                valuesName.add("MUERTE ACCIDENTAL");
                valuesConf.add("MUERTE ACCIDENTAL");
                break;
            case injuries_non_fatal:
                valuesId.add("50");
                valuesName.add("VIOLENCIA INTERPERSONAL");
                valuesConf.add("VIOLENCIA INTERPERSONAL");
                valuesId.add("51");
                valuesName.add("LESION EN ACCIDENTE DE TRANSITO");
                valuesConf.add("LESION EN ACCIDENTE DE TRANSITO");
                valuesId.add("52");
                valuesName.add("INTENCIONAL AUTOINFLINGIDA");
                valuesConf.add("INTENCIONAL AUTOINFLINGIDA");
                valuesId.add("53");
                valuesName.add("VIOLENCIA INTRAFAMILIAR");
                valuesConf.add("VIOLENCIA INTRAFAMILIAR");
                valuesId.add("54");
                valuesName.add("NO INTENCIONAL");
                valuesConf.add("NO INTENCIONAL");
//                valuesId.add("55");//si se agrgegan aqui aparecen duplicado en tabla de resultados y grafico
//                valuesName.add("VIOLENCIA INTRAFAMILIAR");//se agregan en la consulta sql
//                valuesConf.add("VIOLENCIA INTRAFAMILIAR");
//                valuesId.add("56");
//                valuesName.add("VIOLENCIA INTRAFAMILIAR");
//                valuesConf.add("VIOLENCIA INTRAFAMILIAR");
                break;
            case hour:
                infInt = 0;
                supInt = 2;
                for (int i = 0; i < 8; i++) {
                    valuesName.add(String.valueOf(infInt) + ":00/" + String.valueOf(supInt) + ":59");
                    valuesConf.add(String.valueOf(infInt) + ":00/" + String.valueOf(supInt) + ":59");
                    valuesId.add(String.valueOf(infInt) + ":00/" + String.valueOf(supInt) + ":59");
                    infInt = supInt + 1;
                    supInt = supInt + 3;
                }
                break;
            case day:
                break;
            case month:
                valuesId.add("1");
                valuesName.add("ENERO");
                valuesConf.add("ENERO");
                valuesId.add("2");
                valuesName.add("FEBRERO");
                valuesConf.add("FEBRERO");
                valuesId.add("3");
                valuesName.add("MARZO");
                valuesConf.add("MARZO");
                valuesId.add("4");
                valuesName.add("ABRIL");
                valuesConf.add("ABRIL");
                valuesId.add("5");
                valuesName.add("MAYO");
                valuesConf.add("MAYO");
                valuesId.add("6");
                valuesName.add("JUNIO");
                valuesConf.add("JUNIO");
                valuesId.add("7");
                valuesName.add("JULIO");
                valuesConf.add("JULIO");
                valuesId.add("8");
                valuesName.add("AGOSTO");
                valuesConf.add("AGOSTO");
                valuesId.add("9");
                valuesName.add("SEPTIEMBRE");
                valuesConf.add("SEPTIEMBRE");
                valuesId.add("10");
                valuesName.add("OCTUBRE");
                valuesConf.add("OCTUBRE");
                valuesId.add("11");
                valuesName.add("NOVIEMBRE");
                valuesConf.add("NOVIEMBRE");
                valuesId.add("12");
                valuesName.add("DICIEMBRE");
                valuesConf.add("DICIEMBRE");
                break;
            case year:
                //System.out.println("AÑO ACTUAL" + currentYear);
                for (int i = 2003; i <= currentYear; i++) {
                    valuesName.add(String.valueOf(i));
                    valuesConf.add(String.valueOf(i));
                    valuesId.add(String.valueOf(i));
                }
                break;
            case abuse_types:
                //1;"FISICO"
                valuesId.add("1");
                valuesName.add("FISICO");
                valuesConf.add("FISICO");
                //2;"PSICOLOGICO / VERBAL"
                valuesId.add("2");
                valuesName.add("PSICOLOGICO / VERBAL");
                valuesConf.add("PSICOLOGICO / VERBAL");
                //3;"VIOLENCIA SEXUAL"
                valuesId.add("3");
                valuesName.add("VIOLENCIA SEXUAL");
                valuesConf.add("VIOLENCIA SEXUAL");
                //4;"NEGLIGENCIA"
                valuesId.add("4");
                valuesName.add("NEGLIGENCIA");
                valuesConf.add("NEGLIGENCIA");
                //5;"ABANDONO"
                valuesId.add("5");
                valuesName.add("ABANDONO");
                valuesConf.add("ABANDONO");
                //6;"INSTITUCIONAL"
                valuesId.add("6");
                valuesName.add("INSTITUCIONAL");
                valuesConf.add("INSTITUCIONAL");
                //7;"SIN DATO"
                valuesId.add("7");
                valuesName.add("SIN DATO");
                valuesConf.add("SIN DATO");
                //8;"OTRO"
                valuesId.add("8");
                valuesName.add("OTRO");
                valuesConf.add("OTRO");

                //9;"PORNOGRAFIA CON NNA"
                //10;"TRATA DE PERSONAL PARA EXPLOTACION SEXUAL"
                //11;"ABUSO SEXUAL"
                //12;"ACOSO SEXUAL"
                //13;"ASALTO SEXUAL"
                //14;"EXPLOTACION SEXUAL"
                //15;"TURISMO SEXUAL"

                break;
            case communes://comuna,
                try {
                    ResultSet rs = connectionJdbcMB.consult(
                            "Select * from " + generic_table + " where area_id = 1 order by 1");
                    while (rs.next()) {
                        valuesName.add(rs.getString(2));
                        valuesConf.add(rs.getString(2));
                        valuesId.add(rs.getString(1));
                    }
                } catch (Exception e) {
                }
                break;
            case municipality:
                valuesName.add("Municipio");
                valuesConf.add("Municipio");
                valuesId.add("Municipio");
                break;
            case non_fatal_data_sources:
            case neighborhoods://barrio,            
            case corridors://corredor,
            case areas://zona,
            case genders://genero,
            case days://dia semana
            case quadrants://cuadrante
            case activities:
            case boolean3:
            case victim_characteristics:
            case accident_classes:
            case alcohol_levels:
            case use_alcohol_drugs:
            case alcohol_levels_counterparts:
            case alcohol_levels_victim:
            case murder_contexts:
            case contexts:
            case destinations_of_patient:
            case related_events:
            case precipitating_factors:
            case aggressor_genders:
            case places:
            case non_fatal_places:
            case mechanisms:
            case accident_mechanisms:
            case suicide_mechanisms:
            case protective_measures:
            case relationships_to_victim:
            case weapon_types:
            case degree:
            case service_types:
            case transport_counterparts:
            case transport_types:
            case transport_users:
            case involved_vehicles:
            case road_types:

            //case abuse_types:
            case aggressor_types:
            case ethnic_groups:
            case insurance:
            case jobs:
            case public_health_actions:
            case sivigila_educational_level:
            case sivigila_group:
            case sivigila_mechanism:
            case sivigila_no_relative:
            case sivigila_tip_ss:
            case sivigila_vulnerability:
            case vulnerable_groups:
            case kinds_of_injury:
            case anatomical_locations:
            case actions_to_take:
            case security_elements:
            case source_vif:
            case NOVALUE://es una tabla categorica
                try {
                    //ResultSet rs = connectionJdbcMB.consult("Select * from " + generic_table);
                    ResultSet rs = connectionJdbcMB.consult("Select * from " + generic_table + " order by 1");
                    while (rs.next()) {
                        valuesName.add(rs.getString(2));
                        valuesConf.add(rs.getString(2));
                        valuesId.add(rs.getString(1));
                    }
                } catch (Exception e) {
                }
                break;
        }
        //agregar valor "SIN DATO"
        if (generic_table.compareTo("injuries_fatal") != 0 && generic_table.compareTo("injuries_non_fatal") != 0) {
            //detemino si ya existe "SIN DATO" el los valores
            boolean existNoData = false;
            for (int i = 0; i < valuesName.size(); i++) {
                if (valuesName.get(i).compareTo("SIN DATO") == 0) {
                    existNoData = true;
                    break;
                }
            }
            if (!existNoData) {
                valuesName.add("SIN DATO");
                valuesConf.add("SIN DATO");
                valuesId.add("SIN DATO");
            }
        }
        newVariable.setValues(valuesName);
        newVariable.setValuesId(valuesId);
        newVariable.setValuesConfigured(valuesConf);
        return newVariable;
    }

    /**
     * This method is responsible for obtain a list of all variables available
     * for work on this indicator.
     *
     * @return
     */
    public ArrayList<Variable> getVariablesIndicator() {
        ArrayList<Variable> arrayReturn = new ArrayList<>();
        currentIndicator = indicatorsFacade.find(currentIndicator.getIndicatorId());
        for (int i = 0; i < currentIndicator.getIndicatorsVariablesList().size(); i++) {
            arrayReturn.add(
                    createVariable(
                    currentIndicator.getIndicatorsVariablesList().get(i).getIndicatorsVariablesPK().getVariableName(),
                    currentIndicator.getIndicatorsVariablesList().get(i).getCategory(),
                    currentIndicator.getIndicatorsVariablesList().get(i).getAddValues(),
                    currentIndicator.getIndicatorsVariablesList().get(i).getSourceTable()));
        }
        return arrayReturn;
    }

    /**
     * This method is responsible to configure the bgcolor of the table that
     * contains the result of the cross of variable.
     *
     * @return
     */
    private String getColorType() {
        if (colorType) {
            return "bgcolor=\"#DDDDFF\"";
        } else {
            return "bgcolor=\"#FFFFFF\"";
        }
    }

    /**
     * This method is responsible of enable or disable the bgcolor of the table
     * that contains the result of the cross of variable.
     */
    private void changeColorType() {
        if (colorType) {
            colorType = false;
        } else {
            colorType = true;
        }
    }

    /**
     * This method is responsible of determine the header for the table and
     * columns containing all results of the cross of variable.
     *
     * @param value
     * @return
     */
    private String determineHeader(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) != '0' && value.charAt(i) != '1' && value.charAt(i) != '2'
                    && value.charAt(i) != '3' && value.charAt(i) != '4' && value.charAt(i) != '5'
                    && value.charAt(i) != '6' && value.charAt(i) != '7' && value.charAt(i) != '8'
                    && value.charAt(i) != '9' && value.charAt(i) != ' ' && value.charAt(i) != 'n'
                    && value.charAt(i) != '-' && value.charAt(i) != ':' && value.charAt(i) != '/') {
                return value;
            }
        }
        if (value.indexOf("SIN DATO") == -1) {
            if (value.indexOf("/") != -1) {
                if (value.indexOf(":") != -1) {
                    String newValue = value.replace("/", " a ");
                    return newValue + " Horas";
                } else {
                    String newValue = value.replace("/", " a ");
                    return newValue + " Años";
                }
            }
        }
        return value;
    }

    /**
     * This method allows to export the results of the cross of variable to a
     * excel file of vertical way.
     *
     * @param document
     */
    private void exportVerticalResult(Object document) {
        /*
         * Exportar los datos a un archivo excell de forma vertical
         */
        /*
         * Exportar los datos a un archivo excell de forma vertical
         */
        HSSFWorkbook book = (HSSFWorkbook) document;
        HSSFSheet sheet = book.getSheetAt(0);// Se toma hoja del libro
        HSSFRow fila;
        HSSFCell celda;
        HSSFRichTextString texto;

        headers1 = new ArrayList<>();
        headers2 = new String[columNames.size()];
        int posRow = 0;
        int posCol;
        int posF;
        int posI;
        String value;

        //fila inicial        
        fila = sheet.createRow(posRow);
        posI = 1;    // 1 por que faltal nombres de columna                                       
        if (variablesCrossData.size() == 3) {
            posI = 2;    // 1 por que faltal nombres de columna                                       
        }

        for (int i = 0; i < rowNames.size(); i++) {//NOMBRE PARA CADA COLUMNA                        
            celda = fila.createCell((short) posI + i);
            setValueCell(celda, determineHeader(rowNames.get(i)));
        }
        posRow = 1;

        //columna(s) inicial        
        if (variablesCrossData.size() == 2 || variablesCrossData.size() == 1) {
            for (int i = 0; i < columNames.size(); i++) {
                posCol = 0;
                fila = sheet.createRow(posRow + i);
                celda = fila.createCell(posCol);
                setValueCell(celda, determineHeader(columNames.get(i)));
                posCol++;
                for (int l = 0; l < rowNames.size(); l++) {
                    celda = fila.createCell(posCol);
                    if (!showCalculation) {
                        value = matrixResult[fila.getRowNum() - 1][l].split("<br/>")[0].replace("<b>", "").replace("</b>", "");
                    } else {
                        value = matrixResult[fila.getRowNum() - 1][l].replace("<br/>", " ").replace("<b>", "").replace("</b>", "");
                    }
                    setValueCell(celda, value);
                    posCol++;
                }

            }
        }
        if (variablesCrossData.size() == 3) {
            String currentVar = "";
            String[] splitVars;
            for (int i = 0; i < columNames.size(); i++) {
                splitVars = columNames.get(i).split("\\}");//separo las dos variables
                String first = splitVars[0];//invierto el orden de llegada
                splitVars[0] = splitVars[1];
                splitVars[1] = first;
                if (splitVars[0].compareTo(currentVar) == 0) {//ya existe solo le aumento el numero de columnas unidas al ultimo de la lista "headers1"
                    int num = headers1.get(headers1.size() - 1).getColumns();
                    headers1.get(headers1.size() - 1).setColumns(num + 1);
                } else {//no existe la columna la debo crear y adicionar a la lista                    
                    currentVar = splitVars[0];
                    SpanColumns newSpanColumn = new SpanColumns();
                    newSpanColumn.setLabel(splitVars[0]);
                    newSpanColumn.setColumns(1);
                    headers1.add(newSpanColumn);
                }
                headers2[i] = splitVars[1];//a la segunda cabecera le agrego la segunda variable separada
            }
            //AGREGO LA CABECERA 1 

            posF = 0;
            //posI = 1;
            for (int j = 0; j < headers1.size(); j++) {
                posI = posF + 1;
                for (int i = 0; i < headers1.get(j).getColumns(); i++) {
                    posF++;
                }
                sheet.addMergedRegion(new CellRangeAddress(posI, posF, 0, 0));
                //posF++;
            }
            posI = 0;
            for (int i = 0; i < headers1.size(); i++) {
                fila = sheet.createRow(posRow);
                celda = fila.createCell(0);
                texto = new HSSFRichTextString(determineHeader(headers1.get(i).getLabel()));// Se crea el contenido y se asigna
                celda.setCellValue(texto);

                for (int j = 0; j < headers1.get(i).getColumns(); j++) {
                    posCol = 1;
                    if (j != 0) {
                        fila = sheet.createRow(posRow + j);
                    }
                    celda = fila.createCell(posCol);
                    texto = new HSSFRichTextString(determineHeader(headers2[posI]));// Se crea el contenido y se asigna                    
                    celda.setCellValue(texto);
                    posCol++;
                    for (int l = 0; l < rowNames.size(); l++) {
                        celda = fila.createCell(posCol);
                        if (!showCalculation) {
                            value = matrixResult[fila.getRowNum() - 1][l].split("<br/>")[0].replace("<b>", "").replace("</b>", "");
                        } else {
                            value = matrixResult[fila.getRowNum() - 1][l].replace("<br/>", " ").replace("<b>", "").replace("</b>", "");
                        }
                        setValueCell(celda, value);
                        posCol++;
                    }
                    posI++;
                }
                posRow = (short) (posRow + headers1.get(i).getColumns());
            }
        }
    }

    /**
     * This method allows to export the results of the cross of variable to a
     * excel file of horizontal way
     *
     * @param document
     */
    private void exportHorizontalResult(Object document) {
        /*
         * Exportar los datos a un archivo excell de forma horizontal
         */
        HSSFWorkbook book = (HSSFWorkbook) document;
        HSSFSheet sheet = book.getSheetAt(0);// Se toma hoja del libro
        HSSFRow fila;
        HSSFCell celda;
        HSSFRichTextString texto;

        headers1 = new ArrayList<>();
        headers2 = new String[columNames.size()];
        int posRow = 0;
        int posF;
        int posI;
        //-------------------------------------------------------------------
        //TABLA QUE CONTIENE LA CABECERA
        //-------------------------------------------------------------------                        

        if (variablesCrossData.size() == 2 || variablesCrossData.size() == 1) {
            fila = sheet.createRow(posRow);// Se crea una fila dentro de la hoja            
            posRow++;
            posI = 1;
            for (int i = 0; i < columNames.size(); i++) {
                celda = fila.createCell((short) posI);// +2 por que faltal las filas               
                posI++;
                texto = new HSSFRichTextString(determineHeader(columNames.get(i)));// Se crea el contenido de la celda y se mete en ella.
                celda.setCellValue(texto);
            }
        }
        if (variablesCrossData.size() == 3) {
            //-------------------------------------------------------------------
            //CABECERA COMPUESTA            
            String currentVar = "";
            String[] splitVars;
            for (int i = 0; i < columNames.size(); i++) {
                splitVars = columNames.get(i).split("\\}");//separo las dos variables
                String first = splitVars[0];//invierto el orden de llegada
                splitVars[0] = splitVars[1];
                splitVars[1] = first;
                if (splitVars[0].compareTo(currentVar) == 0) {//ya existe solo le aumento el numero de columnas unidas al ultimo de la lista "headers1"
                    int num = headers1.get(headers1.size() - 1).getColumns();
                    headers1.get(headers1.size() - 1).setColumns(num + 1);
                } else {//no existe la columna la debo crear y adicionar a la lista                    
                    currentVar = splitVars[0];
                    SpanColumns newSpanColumn = new SpanColumns();
                    newSpanColumn.setLabel(splitVars[0]);
                    newSpanColumn.setColumns(1);
                    headers1.add(newSpanColumn);
                }
                headers2[i] = splitVars[1];//a la segunda cabecera le agrego la segunda variable separada
            }
            //AGREGO LA CABECERA 1 
            fila = sheet.createRow(posRow);// Se crea una fila dentro de la hoja
            posRow++;
            posF = 0;

            for (int j = 0; j < headers1.size(); j++) {
                posI = posF + 1;
                for (int i = 0; i < headers1.get(j).getColumns(); i++) {
                    posF++;
                }
                sheet.addMergedRegion(new CellRangeAddress(0, 0, posI, posF));
            }
            short posColumn = 1;// +2 por que faltal las filas               
            for (int i = 0; i < headers1.size(); i++) {
                celda = fila.createCell(posColumn);
                posColumn = (short) (posColumn + headers1.get(i).getColumns());
                texto = new HSSFRichTextString(determineHeader(headers1.get(i).getLabel()));// Se crea el contenido de la celda y se mete en ella.
                celda.setCellValue(texto);
            }
            fila = sheet.createRow(posRow);// Se crea una fila dentro de la hoja
            posRow++;
            posI = 1;// +1 por que faltal nombre de filas
            for (int i = 0; i < headers2.length; i++) {
                celda = fila.createCell((short) posI);
                posI++;
                texto = new HSSFRichTextString(determineHeader(headers2[i]));// Se crea el contenido de la celda y se mete en ella.
                celda.setCellValue(texto);
            }
        }
        //-------------------------------------------------------------------
        //TABLA QUE CONTIENE LOS DATOS DE LA MATRIZ
        for (int j = 0; j < rowNames.size(); j++) {
            fila = sheet.createRow(posRow);
            posRow++;
            //nombre fila
            celda = fila.createCell(0);
            celda.setCellValue(new HSSFRichTextString(determineHeader(rowNames.get(j))));

            posI = 1;// 1 por que faltal nombres de fila                               
            for (int i = 0; i < columNames.size(); i++) {
                celda = fila.createCell((short) posI);
                String value;
                if (!showCalculation) {
                    value = matrixResult[i][j].split("<br/>")[0].replace("<b>", "").replace("</b>", "");
                    //celda.setCellValue(value);
                } else {
                    value = matrixResult[i][j].replace("<br/>", " ").replace("<b>", "").replace("</b>", "");
                    //celda.setCellValue(value);
                }
                setValueCell(celda, value);
                posI++;
            }
        }
    }

    /**
     * This method is called when the user presses the button "exportar", this
     * method determines the orientation of the results and then this method
     * calls the method in charge of export these results.
     *
     * @param document
     */
    public void postProcessXLS(Object document) {
        if (invertMatrix) {
            exportVerticalResult(document);
        } else {
            exportHorizontalResult(document);
        }
    }

    /**
     * This method is responsible of determine whether the value to stored in a
     * cell in the excel file must be numeric or string
     *
     * @param celda
     * @param strValue
     */
    private void setValueCell(HSSFCell celda, String strValue) {
        /*determina si el valor a almacenar en una celda del 
         archivo excell debe ser numerica o cadena*/
        try {
            double value = Double.parseDouble(strValue.replace(",", "."));
            celda.setCellValue(value);
        } catch (Exception e) {
            celda.setCellValue(new HSSFRichTextString(strValue));
        }
    }

    /**
     * This method is responsible of invert the results matrix of the cross
     * variables horizontally to vertically and vertically to horizontally.
     */
    public void invertMatrixClick() {
        if (invertMatrix) {
            invertMatrix = false;
        } else {
            invertMatrix = true;
        }
        if (dataTableHtml != null && dataTableHtml.length() != 0) {
            dataTableHtml = createDataTableResult();
        }
    }

    /**
     * This method is responsible for order the results of the cross of
     * variables in the matrix of vertical way.
     *
     * @return
     */
    private String verticalResult() {
        headers1 = new ArrayList<>();
        headers2 = new String[columNames.size()];
        String height = "height:20px;";
        if (showCalculation) {
            height = "height:30px;";
        }
        String strReturn = " ";
        strReturn = strReturn + "    <table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\r\n";
        strReturn = strReturn + "            <tr>\r\n";
        strReturn = strReturn + "                <td>\r\n";
        strReturn = strReturn + "                Cifras por: " + String.valueOf(multiplierK) + " habitantes\r\n";
        strReturn = strReturn + "                </td>\r\n";
        strReturn = strReturn + "                <td class=\"ui-widget-header\">\r\n";

        //TABLA QUE CONTIENE LA CABECERA//-------------------------------------------------------------------                                
        strReturn = strReturn + "                    <div id=\"divHeader\" style=\"overflow:hidden;width:434px;\">\r\n";
        strReturn = strReturn + "                        <table width=\"200px\" cellspacing=\"0\" cellpadding=\"0\" border=\"1\" >\r\n";
        strReturn = strReturn + "                            <tr>\r\n";
        for (int j = 0; j < rowNames.size(); j++) {//NOMBRE PARA CADA COLUMNA                        
            strReturn = strReturn + "                                <td><div style=\"overflow:hidden; height:20px; width:200px; white-space: nowrap;\">" + determineHeader(rowNames.get(j)) + "</div></td>\r\n";
        }
        strReturn = strReturn + "                            </tr>\r\n";
        strReturn = strReturn + "                        </table>\r\n";
        strReturn = strReturn + "                    </div>\r\n";
        //FIN TABLA QUE CONTIENE LA CABECERA//-------------------------------------------------------------------

        strReturn = strReturn + "                </td>\r\n";
        strReturn = strReturn + "            </tr>\r\n";
        strReturn = strReturn + "            <tr>\r\n";
        strReturn = strReturn + "                <td valign=\"top\" class=\"ui-widget-header\">\r\n";

        //TABLA QUE CONTIENE LA PRIMER COLUMNA//-------------------------------------------------------------------        
        strReturn = strReturn + "                    <div id=\"firstcol\" style=\"overflow: hidden; height:280px\">\r\n";//tamaño del div izquierdo
        strReturn = strReturn + "                        <table cellspacing=\"0\" cellpadding=\"0\" border=\"1\" >\r\n";
        if (variablesCrossData.size() == 2 || variablesCrossData.size() == 1) {//COLUMNA SIMPLE

            for (int i = 0; i < columNames.size(); i++) {
                strReturn = strReturn + "                            <tr>\r\n";
                strReturn = strReturn + "                                <td>\r\n";
                strReturn = strReturn + "                                    <div style=\"overflow:hidden; " + height + " width:200px; white-space: nowrap;\">" + determineHeader(columNames.get(i)) + "</div>\r\n";
                strReturn = strReturn + "                                </td>\r\n";
                strReturn = strReturn + "                            </tr>\r\n";
            }
        }
        if (variablesCrossData.size() == 3) {//COLUMNA COMPUESTA            
            String currentVar = "";
            String[] splitVars;
            for (int i = 0; i < columNames.size(); i++) {
                splitVars = columNames.get(i).split("\\}");//separo las dos variables
                String first = splitVars[0];//invierto el orden de llegada
                splitVars[0] = splitVars[1];
                splitVars[1] = first;

                if (splitVars[0].compareTo(currentVar) == 0) {//ya existe solo le aumento el numero de columnas unidas al ultimo de la lista "headers1"
                    int num = headers1.get(headers1.size() - 1).getColumns();
                    headers1.get(headers1.size() - 1).setColumns(num + 1);
                } else {//no existe la columna la debo crear y adicionar a la lista                    
                    currentVar = splitVars[0];
                    SpanColumns newSpanColumn = new SpanColumns();
                    newSpanColumn.setLabel(splitVars[0]);
                    newSpanColumn.setColumns(1);
                    headers1.add(newSpanColumn);
                }
                headers2[i] = splitVars[1];//a la segunda cabecera le agrego la segunda variable separada
            }
            int posh = 0;
            for (int i = 0; i < headers1.size(); i++) {//AGREGO LA COLUMNA 1 
                strReturn = strReturn + "                            <tr>\r\n";
                strReturn = strReturn + "                                <td rowspan=\"" + (headers1.get(i).getColumns() + 1) + "\">\r\n";
                strReturn = strReturn + "                                    <div style=\"overflow:hidden; " + height + " width:100px; white-space: nowrap;\">" + determineHeader(headers1.get(i).getLabel()) + "</div>\r\n";
                strReturn = strReturn + "                                </td>\r\n";
                strReturn = strReturn + "                            </tr>\r\n";
                for (int j = 0; j < headers1.get(i).getColumns(); j++) {//AGREGO LA COLUMNA 2 
                    strReturn = strReturn + "                            <tr>\r\n";
                    strReturn = strReturn + "                                <td>\r\n";
                    strReturn = strReturn + "                                    <div style=\"overflow:hidden; " + height + " width:100px; white-space: nowrap;\">" + determineHeader(headers2[posh]) + "</div>\r\n";
                    strReturn = strReturn + "                                </td>\r\n";
                    strReturn = strReturn + "                            </tr>\r\n";
                    posh++;
                }
            }
        }
        strReturn = strReturn + "                        </table>\r\n";
        strReturn = strReturn + "                    </div>\r\n";
        //FIN TABLA QUE CONTIENE LA PRIMER COLUMNA//-------------------------------------------------------------------

        strReturn = strReturn + "                </td>\r\n";
        strReturn = strReturn + "                <td valign=\"top\">\r\n";

        //TABLA QUE CONTIENE LOS DATOS DE LA MATRIZ//-------------------------------------------------------------------        
        strReturn = strReturn + "                    <div id=\"table_div\" style=\"overflow: scroll;width:450px;height:300px;position:relative\" onscroll=\"fnScroll()\" >\r\n";//div que maneja la tabla
        strReturn = strReturn + "                        <table cellspacing=\"0\" cellpadding=\"0\" border=\"1\" style=\"margin-left: 1px; margin-top: 1px;\">\r\n";
        for (int j = 0; j < columNames.size(); j++) {//AGREGO LOS REGISTROS DE LA MATRIZ        
            if (j == 0) {
                strReturn = strReturn + "                            <tr " + getColorType() + " >\r\n";
            } else {
                strReturn = strReturn + "                            <tr " + getColorType() + " >\r\n";
            }
            for (int i = 0; i < rowNames.size(); i++) {
                String value;
                if (showCalculation) {
                    value = matrixResult[j][i];
                } else {
                    String[] splitColumn = matrixResult[j][i].split("<br/>");
                    value = splitColumn[0];
                }
                strReturn = strReturn + "                                <td> \r\n";//mantenga dimension
                strReturn = strReturn + "                                <div style=\"overflow:hidden; " + height + " width:200px; white-space: nowrap;\">" + value + "</div>\r\n";
                strReturn = strReturn + "                                </td> \r\n";
            }
            strReturn = strReturn + "                            </tr>\r\n";
            changeColorType();//cambiar de color las filas de blanco a azul
        }
        strReturn = strReturn + "                        </table>\r\n";
        strReturn = strReturn + "                    </div>\r\n";
        //FIN TABLA QUE CONTIENE LOS DATOS DE LA MATRIZ//-------------------------------------------------------------------        

        strReturn = strReturn + "                </td>\r\n";
        strReturn = strReturn + "            </tr>\r\n";
        strReturn = strReturn + "        </table>\r\n";

        if (columNames.isEmpty() && rowNames.isEmpty()) {
            strReturn = "<font color=\"Red\"><b> En este rango de fechas no existen registros para realizar el cruce</b></font><br/>";
        }
        //System.out.println("---------------------------------\n" + strReturn + "\n---------------------------------");
        return strReturn;
    }

    /**
     * This method is responsible for order the results of the cross of
     * variables in the matrix of horizontal way
     *
     * @return
     */
    private String horizontalResult() {
        headers1 = new ArrayList<>();
        headers2 = new String[columNames.size()];
        String height = "height:20px;";
        if (showCalculation) {
            height = "height:30px;";
        }
        String strReturn = " ";
        strReturn = strReturn + "<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\r\n";
        strReturn = strReturn + "            <tr>\r\n";
        strReturn = strReturn + "                <td>\r\n";
        strReturn = strReturn + "                Cifras por: " + String.valueOf(multiplierK) + " habitantes\r\n";
        strReturn = strReturn + "                </td>\r\n";
        strReturn = strReturn + "                <td class=\"ui-widget-header\">\r\n";
        //-------------------------------------------------------------------
        //TABLA QUE CONTIENE LA CABECERA
        //-------------------------------------------------------------------        
        strReturn = strReturn + "                    <div id=\"divHeader\" style=\"overflow:hidden;width:434px;\">\r\n";//484 TAMAÑO DIV HEADER SUPERIOR(16=heigth del scroll)
        strReturn = strReturn + "                        <table cellspacing=\"0\" cellpadding=\"0\" border=\"1\" >\r\n";
        if (variablesCrossData.size() == 2 || variablesCrossData.size() == 1) {
            //-------------------------------------------------------------------
            //CABECERA SIMPLE
            strReturn = strReturn + "                            <tr>\r\n";
            for (int i = 0; i < columNames.size(); i++) {
                strReturn = strReturn + "                                <td>\r\n";
                strReturn = strReturn + "                                    <div style=\"overflow:hidden; height:20px; width:200px; white-space: nowrap;\">" + determineHeader(columNames.get(i)) + "</div>\r\n";
                strReturn = strReturn + "                                </td>\r\n";
            }
            strReturn = strReturn + "                            </tr>\r\n";
        }
        if (variablesCrossData.size() == 3) {
            //-------------------------------------------------------------------
            //CABECERA COMPUESTA            
            String currentVar = "";
            String[] splitVars;
            for (int i = 0; i < columNames.size(); i++) {
                splitVars = columNames.get(i).split("\\}");//separo las dos variables
                String first = splitVars[0];//invierto el orden de llegada
                splitVars[0] = splitVars[1];
                splitVars[1] = first;
                if (splitVars[0].compareTo(currentVar) == 0) {//ya existe solo le aumento el numero de columnas unidas al ultimo de la lista "headers1"
                    int num = headers1.get(headers1.size() - 1).getColumns();
                    headers1.get(headers1.size() - 1).setColumns(num + 1);
                } else {//no existe la columna la debo crear y adicionar a la lista                    
                    currentVar = splitVars[0];
                    SpanColumns newSpanColumn = new SpanColumns();
                    newSpanColumn.setLabel(splitVars[0]);
                    newSpanColumn.setColumns(1);
                    headers1.add(newSpanColumn);
                }
                headers2[i] = splitVars[1];//a la segunda cabecera le agrego la segunda variable separada
            }
            //AGREGO LA CABECERA 1 A El PANEL_GRID
            strReturn = strReturn + "                            <tr>\r\n";
            for (int i = 0; i < headers1.size(); i++) {
                strReturn = strReturn + "                                <td colspan=\"" + headers1.get(i).getColumns() + "\">\r\n";
                strReturn = strReturn + "                                    <div style=\"overflow:hidden; height:20px; width:200px; white-space: nowrap;\">" + determineHeader(headers1.get(i).getLabel()) + "</div>\r\n";
                strReturn = strReturn + "                                </td>\r\n";
            }
            strReturn = strReturn + "                            </tr>\r\n";

            strReturn = strReturn + "                            <tr>\r\n";
            //AGREGO LA CABECERA 2 A El PANEL_GRID
            for (int i = 0; i < headers2.length; i++) {
                strReturn = strReturn + "                                <td>\r\n";
                strReturn = strReturn + "                                    <div style=\"overflow:hidden; height:20px; width:200px; white-space: nowrap;\">" + determineHeader(headers2[i]) + "</div>\r\n";
                strReturn = strReturn + "                                </td>\r\n";
            }
            strReturn = strReturn + "                            </tr>\r\n";
        }
        strReturn = strReturn + "                        </table>\r\n";
        strReturn = strReturn + "                    </div>\r\n";
        strReturn = strReturn + "                </td>\r\n";
        strReturn = strReturn + "            </tr>\r\n";
        strReturn = strReturn + "            <tr>\r\n";
        strReturn = strReturn + "                <td valign=\"top\" class=\"ui-widget-header\">\r\n";

        //-------------------------------------------------------------------
        //TABLA QUE CONTIENE LA PRIMER COLUMNA
        //-------------------------------------------------------------------        

        strReturn = strReturn + "                    <div id=\"firstcol\" style=\"overflow: hidden;height:280px\">\r\n";//tamaño del div izquierdo
        strReturn = strReturn + "                        <table width=\"200px\" cellspacing=\"0\" cellpadding=\"0\" border=\"1\" >\r\n";

        //rowNames.add("Totales");
        for (int j = 0; j < rowNames.size(); j++) {
            //----------------------------------------------------------------------
            //NOMBRE PARA CADA FILA            
            strReturn = strReturn + "                            <tr>\r\n";
            strReturn = strReturn + "                                <td><div style=\"overflow:hidden; " + height + " width:200px; white-space: nowrap;\">" + determineHeader(rowNames.get(j)) + "</div></td>\r\n";
            strReturn = strReturn + "                            </tr>\r\n";
        }
        strReturn = strReturn + "                        </table>\r\n";
        strReturn = strReturn + "                    </div>\r\n";
        strReturn = strReturn + "                </td>\r\n";
        strReturn = strReturn + "                <td valign=\"top\">\r\n";
        //-------------------------------------------------------------------
        //TABLA QUE CONTIENE LOS DATOS DE LA MATRIZ
        //-------------------------------------------------------------------              
        strReturn = strReturn + "                    <div id=\"table_div\" style=\"overflow: scroll;width:450px;height:300px;position:relative\" onscroll=\"fnScroll()\" >\r\n";//div que maneja la tabla        
        strReturn = strReturn + "                        <table cellspacing=\"0\" cellpadding=\"0\" border=\"1\" style=\"margin-left: 1px; margin-top: 1px;\">\r\n";

        for (int j = 0; j < rowNames.size(); j++) {//AGREGO LOS REGISTROS DE LA MATRIZ        
            if (j == 0) {
                strReturn = strReturn + "                            <tr " + getColorType() + " >\r\n";
            } else {
                strReturn = strReturn + "                            <tr " + getColorType() + " >\r\n";
            }
            for (int i = 0; i < columNames.size(); i++) {
                String value;
                if (showCalculation) {
                    value = matrixResult[i][j];
                } else {
                    String[] splitColumn = matrixResult[i][j].split("<br/>");
                    value = splitColumn[0];
                }
                strReturn = strReturn + "                                <td> \r\n";//mantenga dimension
                strReturn = strReturn + "                                <div style=\"overflow:hidden; " + height + " width:200px; white-space: nowrap;\">" + value + "</div>\r\n";
                strReturn = strReturn + "                                </td> \r\n";
            }
            strReturn = strReturn + "                            </tr>\r\n";
            changeColorType();//cambiar de color las filas de blanco a azul
        }
        //-------------------------------------------------------------------
        //FINALIZA
        //-------------------------------------------------------------------        
        strReturn = strReturn + "                        </table>\r\n";
        strReturn = strReturn + "                    </div>\r\n";
        strReturn = strReturn + "                </td>\r\n";
        strReturn = strReturn + "            </tr>\r\n";
        strReturn = strReturn + "        </table>\r\n";

        if (columNames.isEmpty() && rowNames.isEmpty()) {
            strReturn = "<font color=\"Red\"><b> En este rango de fechas no existen registros para realizar el cruce</b></font><br/>";
        }

        //System.out.println(strReturn);
        return strReturn;
    }

    /**
     * This method is responsible of realize the cross of variable and call to
     * the methods responsibles of ordering the result of vertical or horizontal
     * way and displays in screen
     *
     * @return
     */
    private String createDataTableResult() {
        btnExportDisabled = true;
        if (matrixResult.length == 0) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Cruze realizado, no hay registros en este rango de fechas");
            return "<font color=\"Red\"><b> En este rango de fechas no existen registros para realizar el cruce</b></font><br/>";
        } else {
            if (invertMatrix) {
                if (matrixResult[0].length > 250) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Cruze realizado, la tabla de resultados contiene mas de 250 columnas, para su exportacion se debe filtrar los datos o invertir la tabla.");
                } else {
                    btnExportDisabled = false;
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Cruze realizado");
                }
                return verticalResult();
            } else {
                if (matrixResult.length > 250) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Cruze realizado, la tabla de resultados contiene mas de 250 columnas, para su exportacion se debe filtrar los datos o invertir la tabla.");
                } else {
                    btnExportDisabled = false;
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Cruze realizado");
                }
                return horizontalResult();
            }
        }
    }

    /**
     * This method is responsible of create the result matrix where the matrix
     * displays all results of crossing variables.
     */
    public void createMatrixResult() {        //System.out.println("INICIA CREAR MATRIZ xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        try {
            //columNames = new ArrayList<>();
            //rowNames = new ArrayList<>();
            ResultSet rs;
//            //---------------------------------------------------------            
//            //DETERMINO NOMBRES DE COLUMNAS PARA MATIRZ SALIDA
//            //---------------------------------------------------------            
//            if (variablesCrossData.size() < 3) { //una o dos variables
//                sql = ""
//                        + " SELECT \n"
//                        + "    column_1 \n"
//                        + " FROM \n"
//                        + "    indicators_records \n"
//                        + " WHERE \n"
//                        + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
//                        + "    indicator_id = " + currentIndicator.getIndicatorId() + "  \n"
//                        + " GROUP BY \n"
//                        + "    column_1 \n"
//                        + " ORDER BY \n"
//                        + "    MIN(record_id) \n";
//                rs = connectionJdbcMB.consult(sql);
//            }
//            if (variablesCrossData.size() == 3) {
//                sql = ""
//                        + " SELECT "
//                        + "    column_1 ||'}'|| column_2 "
//                        + " FROM \n"
//                        + "    indicators_records \n"
//                        + " WHERE \n"
//                        + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
//                        + "    indicator_id = " + currentIndicator.getIndicatorId() + "  \n"
//                        + " GROUP BY \n"
//                        + "    column_1 ||'}'|| column_2 "
//                        + " ORDER BY \n"
//                        + "    MIN(record_id) \n";
//                rs = connectionJdbcMB.consult(sql);
//            }
//            while (rs.next()) {
//                columNames.add(rs.getString(1));
//            }
//            //---------------------------------------------------------            
//            //DETERMINO NOMBRES DE FILAS PARA MATIRZ SALIDA
//            //---------------------------------------------------------            
//            if (variablesCrossData.size() == 1) {
//                rowNames.add("Valor");
//            }
//            if (variablesCrossData.size() == 2) {
//                sql = ""
//                        + " SELECT \n"
//                        + "    column_2 \n"
//                        + " FROM \n"
//                        + "    indicators_records \n"
//                        + " WHERE \n"
//                        + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
//                        + "    indicator_id = " + currentIndicator.getIndicatorId() + "  \n"
//                        + " GROUP BY \n"
//                        + "    column_2 \n"
//                        + " ORDER BY \n"
//                        + "    MIN(record_id) \n";
//                rs = connectionJdbcMB.consult(sql);
//            }
//            if (variablesCrossData.size() == 3) {
//                sql = ""
//                        + " SELECT \n"
//                        + "    column_3 \n"
//                        + " FROM \n"
//                        + "    indicators_records \n"
//                        + " WHERE \n"
//                        + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
//                        + "    indicator_id = " + currentIndicator.getIndicatorId() + "  \n"
//                        + " GROUP BY \n"
//                        + "    column_3 \n"
//                        + " ORDER BY \n"
//                        + "    MIN(record_id) \n";
//                rs = connectionJdbcMB.consult(sql);
//            }
//            while (rs.next()) {
//                rowNames.add(rs.getString(1));
//            }
            //---------------------------------------------------------            
            //SE CREA LA MATRIZ DE RESULTADOS (iniciada en 0 )
            //---------------------------------------------------------
            matrixResult = new String[columNames.size()][rowNames.size()];
            for (int i = 0; i < columNames.size(); i++) {
                for (int j = 0; j < rowNames.size(); j++) {
                    matrixResult[i][j] = "0";
                }
            }
            //DETERMINAR MULTIPLICADOR
            multiplierK = Integer.parseInt(currentMultipler.replace(".", "").replace("'", ""));

            //CONSULTAMOS TODOS LOS REGISTROS
            sql = ""
                    + " SELECT \n"
                    + "    * \n"
                    + " FROM \n"
                    + "    indicators_records \n"
                    + " WHERE \n"
                    + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "    indicator_id = " + currentIndicator.getIndicatorId() + "  \n";
            rs = connectionJdbcMB.consult(sql);
            while (rs.next()) {
                boolean find = false;
                String value;
                for (int i = 0; i < columNames.size(); i++) {
                    for (int j = 0; j < rowNames.size(); j++) {
                        if (variablesCrossData.size() == 1) {//ES UNA VARIABLE                            
                            if (rs.getString("column_1").compareTo(columNames.get(i)) == 0) {
                                value = formateador.format(Double.parseDouble("0"));
                                //if (rs.getString("count").compareTo("0") != 0 && rs.getInt("population") != 0) {
                                if (rs.getInt("population") != 0) {
                                    value = formateador.format((Double.parseDouble(rs.getString("count")) / Double.parseDouble(rs.getString("population"))) * multiplierK);
                                }
                                matrixResult[i][j] = "<b>" + value + "</b><br/>(" + rs.getString("count") + "/" + rs.getString("population") + ")";
                                find = true;
                            }
                        }
                        if (variablesCrossData.size() == 2) {//SON DOS VARIABLES                            
                            if (rs.getString("column_1").compareTo(columNames.get(i)) == 0 && rs.getString("column_2").compareTo(rowNames.get(j)) == 0) {
                                value = formateador.format(Double.parseDouble("0"));
                                //if (rs.getString("count").compareTo("0") != 0 && rs.getInt("population") != 0) {
                                if (rs.getInt("population") != 0) {
                                    value = formateador.format((Double.parseDouble(rs.getString("count")) / Double.parseDouble(rs.getString("population"))) * multiplierK);
                                }
                                matrixResult[i][j] = "<b>" + value + "</b><br/>(" + rs.getString("count") + "/" + rs.getString("population") + ")";
                                find = true;
                            }
                        }
                        if (variablesCrossData.size() == 3) {//SON TRES VARIABLES                            
                            if (columNames.get(i).compareTo(rs.getString("column_1") + "}" + rs.getString("column_2")) == 0 && rs.getString("column_3").compareTo(rowNames.get(j)) == 0) {
                                value = formateador.format(Double.parseDouble("0"));
                                //if (rs.getString("count").compareTo("0") != 0 && rs.getInt("population") != 0) {
                                if (rs.getInt("population") != 0) {
                                    value = formateador.format((Double.parseDouble(rs.getString("count")) / Double.parseDouble(rs.getString("population"))) * multiplierK);
                                }
                                matrixResult[i][j] = "<b>" + value + "</b><br/>(" + rs.getString("count") + "/" + rs.getString("population") + ")";
                                find = true;
                            }
                        }
                        if (find) {
                            break;
                        }
                    }
                    if (find) {
                        break;
                    }
                }
            }
        } catch (SQLException | NumberFormatException e) {
            System.out.println("Error 7 en " + this.getClass().getName() + ":" + e.toString());
        }
    }

    /**
     * This method is responsible to create the column of population
     * corresponding to a categorical value selected. When a cross of variables
     * is realized for specified rate is necessary to determine which is the
     * population for age, gender or area. This method determines the population
     * depending on the cross that is performed.
     */
    private void addColumToRow(Row row1, String get, String style, int colSpan, int rowSpan) {
        Column column = new Column();
        HtmlOutputText text = new HtmlOutputText();
        column.setStyleClass(style);
        text.setValue(get);
        column.getChildren().add(text);
        column.setStyle("width: 200px;");
        column.setRowspan(rowSpan);
        column.setColspan(colSpan);
        row1.getChildren().add(column);
    }

    /**
     * This method is responsible of loaded the indicator in the which the user
     * is working
     *
     * @param n
     */
    private void loadIndicator(int n) {
        currentIndicator = indicatorsFacade.find(n);
        reset();
    }

    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    //FUNCIONES PARA REALIZAR LA CARGA DE UN INDICADOR
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    public void loadIndicator10() {
        loadIndicator(10);
    }

    public void loadIndicator17() {
        loadIndicator(17);
    }

    public void loadIndicator24() {
        loadIndicator(24);
    }

    public void loadIndicator31() {
        loadIndicator(31);
    }

    public void loadIndicator38() {
        loadIndicator(38);
    }

    public void loadIndicator45() {
        loadIndicator(45);
    }

    public void loadIndicator52() {
        loadIndicator(52);
    }

    public void loadIndicator59() {
        loadIndicator(59);
    }

    public void loadIndicator66() {
        loadIndicator(66);
    }

    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    //FUNCIONES GET AND SET
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    public String getSubTitleIndicator() {
        return subTitleIndicator;
    }

    public void setSubTitleIndicator(String subTitleIndicator) {
        this.subTitleIndicator = subTitleIndicator;
    }

    public String getTitleIndicator() {
        return titleIndicator;
    }

    public void setTitleIndicator(String titleIndicator) {
        this.titleIndicator = titleIndicator;
    }

    public String getTitlePage() {
        return titlePage;
    }

    public void setTitlePage(String titlePage) {
        this.titlePage = titlePage;
    }

    public StreamedContent getChartImage() {
        return chartImage;
    }

    public void setChartImage(StreamedContent chartImage) {
        this.chartImage = chartImage;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(Date initialDate) {
        this.initialDate = initialDate;
    }

    public List<String> getListOfCrossVariablesNames() {
        return listOfCrossVariablesNames;
    }

    public void setListOfCrossVariablesNames(List<String> listOfCrossVariablesNames) {
        this.listOfCrossVariablesNames = listOfCrossVariablesNames;
    }

    public List<String> getVariablesList() {
        return variablesList;
    }

    public void setVariablesList(List<String> variablesList) {
        this.variablesList = variablesList;
    }

    public List<String> getCurrentVariablesCrossSelected() {
        return currentVariablesCrossSelected;
    }

    public void setCurrentVariablesCrossSelected(List<String> currentVariablesCrossSelected) {
        this.currentVariablesCrossSelected = currentVariablesCrossSelected;
    }

    public List<String> getCurrentAvailableVariablesNamesSelected() {
        return currentAvailableVariablesNamesSelected;
    }

    public void setCurrentAvailableVariablesNamesSelected(List<String> currentAvailableVariablesNamesSelected) {
        this.currentAvailableVariablesNamesSelected = currentAvailableVariablesNamesSelected;
    }

    public boolean isBtnAddVariableDisabled() {
        return btnAddVariableDisabled;
    }

    public void setBtnAddVariableDisabled(boolean btnAddVariableDisabled) {
        this.btnAddVariableDisabled = btnAddVariableDisabled;
    }

    public boolean isBtnRemoveVariableDisabled() {
        return btnRemoveVariableDisabled;
    }

    public void setBtnRemoveVariableDisabled(boolean btnRemoveVariableDisabled) {
        this.btnRemoveVariableDisabled = btnRemoveVariableDisabled;
    }

    public ArrayList<String> getValuesCategoryList() {
        return valuesCategoryList;
    }

    public void setValuesCategoryList(ArrayList<String> valuesCategoryList) {
        this.valuesCategoryList = valuesCategoryList;
    }

    public List<String> getCurrentCategoricalValuesList() {
        return currentCategoricalValuesList;
    }

    public void setCurrentCategoricalValuesList(List<String> currentCategoricalValuesList) {
        this.currentCategoricalValuesList = currentCategoricalValuesList;
    }

    public List<String> getCurrentCategoricalValuesSelected() {
        return currentCategoricalValuesSelected;
    }

    public void setCurrentCategoricalValuesSelected(List<String> currentCategoricalValuesSelected) {
        this.currentCategoricalValuesSelected = currentCategoricalValuesSelected;
    }

    public String getFirstVariablesCrossSelected() {
        return firstVariablesCrossSelected;
    }

    public void setFirstVariablesCrossSelected(String firstVariablesCrossSelected) {
        this.firstVariablesCrossSelected = firstVariablesCrossSelected;
    }

    public boolean isBtnRemoveCategoricalValueDisabled() {
        return btnRemoveCategoricalValueDisabled;
    }

    public void setBtnRemoveCategoricalValueDisabled(boolean btnRemoveCategoricalValueDisabled) {
        this.btnRemoveCategoricalValueDisabled = btnRemoveCategoricalValueDisabled;
    }

    public boolean isBtnAddCategoricalValueDisabled() {
        return btnAddCategoricalValueDisabled;
    }

    public void setBtnAddCategoricalValueDisabled(boolean btnAddCategoricalValueDisabled) {
        this.btnAddCategoricalValueDisabled = btnAddCategoricalValueDisabled;
    }

    public String getEndValue() {
        return endValue;
    }

    public void setEndValue(String endValue) {
        this.endValue = endValue;
    }

    public String getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(String initialValue) {
        this.initialValue = initialValue;
    }

    public String getDataTableHtml() {
        return dataTableHtml;
    }

    public void setDataTableHtml(String dataTableHtml) {
        this.dataTableHtml = dataTableHtml;
    }

    public OutputPanel getDynamicDataTableGroup() {
        return dynamicDataTableGroup;
    }

    public void setDynamicDataTableGroup(OutputPanel dynamicDataTableGroup) {
        this.dynamicDataTableGroup = dynamicDataTableGroup;
    }

    public boolean isRenderedDynamicDataTable() {
        return renderedDynamicDataTable;
    }

    public void setRenderedDynamicDataTable(boolean renderedDynamicDataTable) {
        this.renderedDynamicDataTable = renderedDynamicDataTable;
    }

    public String getCurrentValueGraph() {
        return currentValueGraph;
    }

    public void setCurrentValueGraph(String currentValueGraph) {
        this.currentValueGraph = currentValueGraph;
    }

    public String getCurrentVariableGraph() {
        return currentVariableGraph;
    }

    public void setCurrentVariableGraph(String currentVariableGraph) {
        this.currentVariableGraph = currentVariableGraph;
    }

    public List<String> getValuesGraph() {
        return valuesGraph;
    }

    public void setValuesGraph(List<String> valuesGraph) {
        this.valuesGraph = valuesGraph;
    }

    public String getCurrentSpatialDisaggregation() {
        return currentSpatialDisaggregation;
    }

    public void setCurrentSpatialDisaggregation(String currentSpatialDisaggregation) {
        this.currentSpatialDisaggregation = currentSpatialDisaggregation;
    }

    public List<String> getSpatialDisaggregationTypes() {
        return spatialDisaggregationTypes;
    }

    public void setSpatialDisaggregationTypes(List<String> spatialDisaggregationTypes) {
        this.spatialDisaggregationTypes = spatialDisaggregationTypes;
    }

    public boolean isShowCalculation() {
        return showCalculation;
    }

    public void setShowCalculation(boolean showCalculation) {
        this.showCalculation = showCalculation;
    }

    public String getCurrentMultipler() {
        return currentMultipler;
    }

    public void setCurrentMultipler(String currentMultipler) {
        this.currentMultipler = currentMultipler;
    }

    public List<String> getMultiplers() {
        return multiplers;
    }

    public void setMultiplers(List<String> multiplers) {
        this.multiplers = multiplers;
    }

    public boolean isShowItems() {
        return showItems;
    }

    public void setShowItems(boolean showItems) {
        this.showItems = showItems;
    }

    public String getCurrentTemporalDisaggregation() {
        return currentTemporalDisaggregation;
    }

    public void setCurrentTemporalDisaggregation(String currentTemporalDisaggregation) {
        this.currentTemporalDisaggregation = currentTemporalDisaggregation;
    }

    public List<String> getTemporalDisaggregationTypes() {
        return temporalDisaggregationTypes;
    }

    public void setTemporalDisaggregationTypes(List<String> temporalDisaggregationTypes) {
        this.temporalDisaggregationTypes = temporalDisaggregationTypes;
    }

    public String getNewConfigurationName() {
        return newConfigurationName;
    }

    public void setNewConfigurationName(String newConfigurationName) {
        this.newConfigurationName = newConfigurationName;
    }

    public List<String> getCurrentConfigurationSelected() {
        return currentConfigurationSelected;
    }

    public void setCurrentConfigurationSelected(List<String> currentConfigurationSelected) {
        this.currentConfigurationSelected = currentConfigurationSelected;
    }

    public List<String> getConfigurationsList() {
        return configurationsList;
    }

    public void setConfigurationsList(List<String> configurationsList) {
        this.configurationsList = configurationsList;
    }

    public boolean isBtnExportDisabled() {
        return btnExportDisabled;
    }

    public void setBtnExportDisabled(boolean btnExportDisabled) {
        this.btnExportDisabled = btnExportDisabled;
    }

    public boolean isShowEmpty() {
        return showEmpty;
    }

    public void setShowEmpty(boolean showEmpty) {
        this.showEmpty = showEmpty;
    }

    public String getCurrentTypeGraph() {
        return currentTypeGraph;
    }

    public void setCurrentTypeGraph(String currentTypeGraph) {
        this.currentTypeGraph = currentTypeGraph;
    }

    public List<String> getTypesGraph() {
        return typesGraph;
    }

    public void setTypesGraph(List<String> typesGraph) {
        this.typesGraph = typesGraph;
    }

    public int getHeightGraph() {
        return heightGraph;
    }

    public void setHeightGraph(int heightGraph) {
        this.heightGraph = heightGraph;
    }

    public int getWidthGraph() {
        return widthGraph;
    }

    public void setWidthGraph(int widthGraph) {
        this.widthGraph = widthGraph;
    }

    public boolean isShowFrames() {
        return showFrames;
    }

    public void setShowFrames(boolean showFrames) {
        this.showFrames = showFrames;
    }

    public boolean isInvertMatrix() {
        return invertMatrix;
    }

    public void setInvertMatrix(boolean invertMatrix) {
        this.invertMatrix = invertMatrix;
    }

    public int getSizeFont() {
        return sizeFont;
    }

    public void setSizeFont(int sizeFont) {
        this.sizeFont = sizeFont;
    }

    public boolean isShowGraphic() {
        return showGraphic;
    }

    public void setShowGraphic(boolean showGraphic) {
        this.showGraphic = showGraphic;
    }

    public boolean isShowTableResult() {
        return showTableResult;
    }

    public void setShowTableResult(boolean showTableResult) {
        this.showTableResult = showTableResult;
    }
}
