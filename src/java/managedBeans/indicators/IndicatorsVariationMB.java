/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.indicators;

import beans.util.LineLegendItemSource;
import beans.connection.ConnectionJdbcMB;
import beans.enumerators.VariablesEnum;
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
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import managedBeans.login.LoginMB;
import beans.util.SpanColumns;
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
import org.jfree.chart.plot.IntervalMarker;
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
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * This class allows to identify the number of cases has increased or decreased
 * one type of injury comparing 2 periods of time.
 *
 * @author SANTOS
 */
@ManagedBean(name = "indicatorsVariationMB")
@SessionScoped
public class IndicatorsVariationMB {

    /**
     * Creates a new instance of IndicatorsMB
     */
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
    private String initialDateStrA = "";
    private String endDateStrA = "";
    private String initialDateStrB = "";
    private String endDateStrB = "";
    private boolean invertMatrix = false;
    private Calendar c1A = Calendar.getInstance();
    private Calendar c2A = Calendar.getInstance();
    private Calendar c1B = Calendar.getInstance();
    private Calendar c2B = Calendar.getInstance();
    private FacesMessage message = null;
    private ConnectionJdbcMB connectionJdbcMB;
    private String titlePage = "SIGEODEP -  INDICADORES GENERALES PARA LESIONES FATALES";
    private String titleIndicator = "SIGEODEP -  INDICADORES GENERALES PARA LESIONES FATALES";
    private String subTitleIndicator = "NUMERO DE CASOS POR LESION";
    private boolean showItems = true;
    private String currentVariableGraph1;
    private String currentValueGraph1;
    private List<String> valuesGraph1 = new ArrayList<>();
    private String firstVariablesCrossSelected = null;
    private String initialValue = "";
    private String endValue = "";
    private String dataTableHtml;
    private String dataTableHtmlDiference;
    private String sql = "";
    private String[] headers2;//CABECERA 2 CUANDO EL CRUCE SE REALIZA SOBRE 3 VARIABLES
    private LoginMB loginMB;
    private String[][] matrixResult;//MATRIZ DE RESULTADOS
    private Date initialDateA = new Date();
    private Date endDateA = new Date();
    private Date initialDateB = new Date();
    private Date endDateB = new Date();
    private String currentTemporalDisaggregation;
    private List<String> temporalDisaggregationTypes = new ArrayList<>();
    private List<String> variablesList = new ArrayList<>();//lista de nombres de variables disponibles que sepueden cruzar(se visualizan en pagina)
    private List<String> variablesCrossList = new ArrayList<>();//ista de nombres de variables que se van a cruzar(se visualizan en pagina)
    private List<String> currentVariablesSelected = new ArrayList<>();//lista de nombres seleccionados en la lista de variables disponibles
    private List<String> currentVariablesCrossSelected = new ArrayList<>();//lista de nombres seleccionados en la lista de variables a cruzar    
    private List<String> currentCategoricalValuesList = new ArrayList<>();
    private List<String> currentCategoricalValuesSelected;
    private ArrayList<SpanColumns> headers1;//CABECERA 1 CUANDO EL CRUCE SE REALIZA SOBRE 3 VARIABLES
    private ArrayList<Variable> variablesListData;//lista de variables que tiene el indicador
    private ArrayList<Variable> variablesCrossData = new ArrayList<>();//lista de variables a cruzar
    private ArrayList<String> valuesCategoryList;//lista de valores para una categoria
    private ArrayList<String> columNames;//NOMBRES DE LAS COLUMNAS, (SI EL CRUCE ES DE TRES VARIABLES ESTA SEPARADO POR EL CARACTER: }  )
    private ArrayList<String> columNamesFinal;//NOMBRES DE LAS COLUMNAS, (SI EL CRUCE ES DE TRES VARIABLES ESTA SEPARADO POR EL CARACTER: }  )
    private ArrayList<String> rowNames;//NOMBRES DE LAS FILAS    
    private Variable currentVariableConfiguring;
    private int numberCross = 2;//maximo numero de variables a cruzar
    private int currentYear = 0;
    private boolean btnExportDisabled = true;
    private boolean btnAddVariableDisabled = true;
    private boolean btnAddCategoricalValueDisabled = true;
    private boolean btnRemoveCategoricalValueDisabled = true;
    private boolean btnRemoveVariableDisabled = true;
    private boolean renderedDynamicDataTable = true;
    private String diferentTemporalWarning = "";//mensaje que indica si los rangos tiene diferente tamaño
    private boolean showCalculation = false;//mostrar la resta
    private boolean colorType = true;
    private boolean showGraphic = false;//mostrar seccion de graficos
    private boolean showTableResult = false;//mostrar tabla de resultados
    int colorId = 0;
    int typeFill = 0;
    private boolean showFrames = true;
    private boolean showEmpty = true;
    private CopyManager cpManager;
    private StringBuilder sb;
    private int tuplesProcessed;
    private String sourceTable = "";//tabla adicional que se usara en la seccion "FROM" de la consulta sql
    private boolean separateRecords = false;
    private int heightGraph = 460;
    private int widthGraph = 660;
    private int sizeFont = 12;
    private List<String> typesGraph = new ArrayList<>();
    private String currentTypeGraph = "barras";
    String variablesName = "";
    String categoryAxixLabel = "";
    String indicatorName = "";
    DefaultCategoryDataset dataset = null;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy", new Locale("ES"));
    double increment = 0;

    /**
     * This method is the class constructor, is responsible for instantiating
     * the current connection to the database, verify that the user has
     * successfully logged in, this method instance items needed to start
     * working as well as are the start and end date, also the temporary
     * dissagregations.
     */
    public IndicatorsVariationMB() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
        loginMB = (LoginMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{loginMB}", LoginMB.class);
        Calendar c = Calendar.getInstance();
        currentYear = c.get(Calendar.YEAR);
        //-----------------------------------------------
        initialDateA.setDate(1);
        initialDateA.setMonth(0);
        initialDateA.setYear(c.get(Calendar.YEAR) - 1901);
        endDateA.setDate(31);
        endDateA.setMonth(11);
        endDateA.setYear(c.get(Calendar.YEAR) - 1901);
        //-----------------------------------------------
        initialDateB.setDate(1);
        initialDateB.setMonth(0);
        initialDateB.setYear(c.get(Calendar.YEAR) - 1900);
        endDateB.setDate(31);
        endDateB.setMonth(11);
        endDateB.setYear(c.get(Calendar.YEAR) - 1900);
        //-----------------------------------------------
        temporalDisaggregationTypes = new ArrayList<>();
        temporalDisaggregationTypes.add("Anual");
        temporalDisaggregationTypes.add("Mensual");
        //temporalDisaggregationTypes.add("Semanal");
        temporalDisaggregationTypes.add("Diaria");
        currentTemporalDisaggregation = "Mensual";

    }

    /**
     * This method is responsible to display messages on the screen for that the
     * user can see what is happening.
     */
    public void showMessage() {
        if (message != null) {
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        if (diferentTemporalWarning != null && diferentTemporalWarning.length() != 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Alerta", diferentTemporalWarning));
        }
    }

    /**
     * This method is responsible of obtain the names and values of the
     * variables that are going to cross to be visible on the graph where the
     * results of the cross shown.
     */
    public void loadValuesGraph() {
        valuesGraph1 = new ArrayList<>();
        currentVariableGraph1 = "";
        currentValueGraph1 = null;

        if (variablesCrossData.size() > 2) {
            currentVariableGraph1 = variablesCrossData.get(2).getName();
            for (int j = 0; j < variablesCrossData.get(2).getValuesConfigured().size(); j++) {
                valuesGraph1.add(variablesCrossData.get(2).getValuesConfigured().get(j));
                currentValueGraph1 = variablesCrossData.get(2).getValuesConfigured().get(j);
            }
        }
        createImage();
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
     * This method is responsible to validate the date range checking different
     * possibilities that the user has the moment of completing these fields as:
     * the start date must be less than the end date, the start date can not be
     * less than 2002, if the disaggregation is daily then there must be at
     * least one difference day, if the disaggregation is monthly must be at
     * least one month difference, if the disaggregation is annual must be at
     * least one year difference.
     *
     * @return
     */
    private boolean validateDateRange() {

        c1A.setTime(initialDateA);
        c2A.setTime(endDateA);
        c1B.setTime(initialDateB);
        c2B.setTime(endDateB);

        //fecha no puede ser menor a 2002 ni mayor al año del sistema
        if (c1A.get(Calendar.YEAR) < 2002 || c1A.get(Calendar.YEAR) > currentYear + 1) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La fecha inicial del rango A debe estar entre el año 2002 y " + (currentYear + 1));
            return false;
        }
        if (c2A.get(Calendar.YEAR) < 2002 || c2A.get(Calendar.YEAR) > currentYear + 1) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La fecha final del rango A debe estar entre el año 2002 y " + (currentYear + 1));
            return false;
        }
        if (c1B.get(Calendar.YEAR) < 2002 || c1B.get(Calendar.YEAR) > currentYear + 1) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La fecha inicial del rango B debe estar entre el año 2002 y " + (currentYear + 1));
            return false;
        }
        if (c2B.get(Calendar.YEAR) < 2002 || c2B.get(Calendar.YEAR) > currentYear + 1) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La fecha final del rango B debe estar entre el año 2002 y " + (currentYear + 1));
            return false;
        }

        //fecha inicial no sea superior a fecha final
        if (c1A.compareTo(c2A) > 0) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "En el rango B la fecha inicial debe ser inferior o igual a la fecha final");
            return false;
        } else {
            initialDateStrA = formato.format(initialDateA);
            endDateStrA = formato.format(endDateA);
        }
        if (c1B.compareTo(c2B) > 0) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "En el rango B la fecha inicial debe ser inferior o igual a la fecha final");
            return false;
        } else {
            initialDateStrB = formato.format(initialDateB);
            endDateStrB = formato.format(endDateB);
        }
        //se cumpla con la desagrgacion temporal
        if (currentTemporalDisaggregation.compareTo("Diaria") == 0) {
            if (getDateDifference(initialDateA, endDateA, "diaria") < 1) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "En el rango A debe haber como mínimo 1 dia");
                return false;
            }
            if (getDateDifference(initialDateB, endDateB, "diaria") < 1) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "En el rango B debe haber como mínimo 1 dia");
                return false;
            }
        }
        if (currentTemporalDisaggregation.compareTo("Mensual") == 0) {
            if (getDateDifference(initialDateA, endDateA, "mensual") < 1) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "En el rango A debe haber como mínimo 1 mes");
                return false;
            }
            if (getDateDifference(initialDateB, endDateB, "mensual") < 1) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "En el rango B debe haber como mínimo 1 mes");
                return false;
            }
        }
        if (currentTemporalDisaggregation.compareTo("Anual") == 0) {
            if (getDateDifference(initialDateA, endDateA, "anual") < 1) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "En el rango A debe haber como mínimo 1 año ");
                return false;
            }
            if (getDateDifference(initialDateB, endDateB, "anual") < 1) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "En el rango B debe haber como mínimo 1 año ");
                return false;
            }
        }
        return true;
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
                + "       indicator_id = " + (currentIndicator.getIndicatorId() + 100) + " OR \n\r" //datos ordenados completos(los que tienen y no tienen conteo )
                + "       indicator_id = " + (currentIndicator.getIndicatorId() + 200) + " \n\r" //ocurrencias
                + "    ) \n\r";
        //System.out.println("ELIMINACIONES \n " + sql);
        connectionJdbcMB.non_query(sql);
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
                + " DELETE FROM \n"
                + "    indicators_records \n"
                + "    \n"
                + " WHERE \n"
                + "    record_id IN \n"
                + "    ( \n"
                + "       SELECT \n"
                + "          record_id \n"
                + "       FROM  \n"
                + "          indicators_records \n"
                + "       WHERE  \n"
                + "          count = 0 AND \n"
                + "          user_id = " + loginMB.getCurrentUser().getUserId() + " AND  \n"
                + "          indicator_id = " + currentIndicator.getIndicatorId() + " AND  \n"
                + "          record_id IN \n"
                + "          ( \n"
                + "             SELECT  \n"
                + "                record_id \n"
                + "             FROM  \n"
                + "                indicators_records \n"
                + "             WHERE  \n"
                + "                count = 0 AND \n"
                + "                user_id = " + loginMB.getCurrentUser().getUserId() + " AND  \n"
                + "                indicator_id = " + (currentIndicator.getIndicatorId() + 100) + " \n"
                + "          ) \n"
                + "     ) AND user_id = " + loginMB.getCurrentUser().getUserId() + "  \n"
                + "       AND (indicator_id = " + (currentIndicator.getIndicatorId() + 100) + " \n"
                + "       OR   indicator_id = " + currentIndicator.getIndicatorId() + " )";
        //System.out.println("CONSULTA ELIMINACION \n " + sql);
        connectionJdbcMB.non_query(sql);
        //---------------------------------------------------------            
        //ELIMINO LOS NOMBRES DE COLUMNAS NO NECESARIOS
        //---------------------------------------------------------            
        ResultSet rs = null;
        if (variablesCrossData.size() < 3) { //una o dos variables
            sql = ""
                    + " SELECT column_1 FROM  indicators_records \n"
                    + " WHERE  user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "        (indicator_id = " + currentIndicator.getIndicatorId() + " OR  \n"
                    + "        indicator_id = " + (currentIndicator.getIndicatorId() + 100) + ")  \n"
                    + " GROUP BY column_1  ORDER BY MIN(record_id) \n";

        }
        if (variablesCrossData.size() == 3) {
            sql = ""
                    + " SELECT column_1 ||'}'|| column_2 FROM indicators_records \n"
                    + " WHERE  user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "        (indicator_id = " + currentIndicator.getIndicatorId() + " OR  \n"
                    + "        indicator_id = " + (currentIndicator.getIndicatorId() + 100) + ")  \n"
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
            System.out.println("Error 1 en " + this.getClass().getName() + ":" + e.toString());
        }
        //---------------------------------------------------------            
        //ELIMINO LOS NOMBRES DE FILAS NO NECESARIOS
        //---------------------------------------------------------            
        sql = "";
        if (variablesCrossData.size() == 2) {
            sql = ""
                    + " SELECT column_2 FROM indicators_records \n"
                    + " WHERE user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "       (indicator_id = " + currentIndicator.getIndicatorId() + " OR  \n"
                    + "        indicator_id = " + (currentIndicator.getIndicatorId() + 100) + ")  \n"
                    + " GROUP BY column_2 ORDER BY MIN(record_id) \n";
        }
        if (variablesCrossData.size() == 3) {
            sql = ""
                    + " SELECT column_3 FROM indicators_records \n"
                    + " WHERE user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "       (indicator_id = " + currentIndicator.getIndicatorId() + " OR  \n"
                    + "        indicator_id = " + (currentIndicator.getIndicatorId() + 100) + ")  \n"
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
                System.out.println("Error 2 en " + this.getClass().getName() + ":" + e.toString());
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
        diferentTemporalWarning = "";
        variablesCrossData = new ArrayList<>();//lista de variables a cruzar            
        message = null;
        categoryAxixLabel = "";
        boolean continueProcess = validateDateRange();//VALIDACION DE FECHAS

        if (continueProcess) {//ELIMINO DATOS DE UN PROCESO ANTERIOR
            removeIndicatorRecords();
        }

        if (continueProcess) {//NUMERO DE VARIABLES A CRUZAR SEA MENOR O IGUAL AL LIMITE ESTABLECIDO
            if (variablesCrossList.size() <= numberCross) {
                continueProcess = true;
            } else {
                continueProcess = false;
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "En la lista de variables a cruzar deben haber " + numberCross + " o menos variables");
            }
        }
        //------------------------------------------------------
        //---------------- RANGO A -----------------------------
        //------------------------------------------------------
        if (continueProcess) {//AGREGO LAS VARIABLES INDICADAS POR EL USUARIO
            variablesCrossData = new ArrayList<>();//lista de variables a cruzar            
            variablesCrossData.add(createTemporalDisaggregationVariable(initialDateA, endDateA));//variable de desagregacion temporal
            for (int j = 0; j < variablesCrossList.size(); j++) {
                for (int i = 0; i < variablesListData.size(); i++) {
                    if (variablesListData.get(i).getName().compareTo(variablesCrossList.get(j)) == 0) {
                        variablesCrossData.add(variablesListData.get(i));
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
        if (continueProcess) {//ALMACENO EN BASE DE DATOS LOS REGISTROS DEL RANGO A
            saveIndicatorRecords(createIndicatorConsult(initialDateStrA, endDateStrA), 200);
        }
        if (continueProcess) {//DE SER NECESARIO SEPARO LOS REGISTROS(cuando son variables con relaciones de uno a muchos)
            if (separateRecords) {
                separateRecordsFunction(200);
            }
        }
        if (continueProcess) {//CREO TODAS LAS POSIBLES COMBINACIONES DEL RANGO A
            createCombinations(200, 0);//se determina si las 200 tienen "sin dato" y genera cobinaciones en 100
        }
        if (continueProcess) {//AGRUPO LOS VALORES DEL RANGO A
            groupingOfValues(200, 0);//agrupa los datos de 200; actuliza los datos de 100;elimina los datos de 200
        }
        //------------------------------------------------------
        //---------------- RANGO B -----------------------------
        //------------------------------------------------------
        if (continueProcess) {//AGREGO LAS VARIABLES INDICADAS POR EL USUARIO
            variablesCrossData = new ArrayList<>();//lista de variables a cruzar            
            variablesCrossData.add(createTemporalDisaggregationVariable(initialDateB, endDateB));//variable de desagregacion temporal
            for (int j = 0; j < variablesCrossList.size(); j++) {
                for (int i = 0; i < variablesListData.size(); i++) {
                    if (variablesListData.get(i).getName().compareTo(variablesCrossList.get(j)) == 0) {
                        variablesCrossData.add(variablesListData.get(i));
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
        if (continueProcess) {//ALMACENO EN BASE DE DATOS LOS REGISTROS DEL RANGO B
            saveIndicatorRecords(createIndicatorConsult(initialDateStrB, endDateStrB), 200);
        }
        if (continueProcess) {//DE SER NECESARIO SEPARO LOS REGISTROS(cuando son variables con relaciones de uno a muchos)
            if (separateRecords) {
                separateRecordsFunction(200);
            }
        }
        if (continueProcess) {//CREO TODAS LAS POSIBLES COMBINACIONES DEL RANGO B
            createCombinations(200, 100);//se determina si las 200 tienen "sin dato" y genera cobinaciones en 0
        }
        if (continueProcess) {//AGRUPO LOS VALORES DEL RANGO B
            groupingOfValues(200, 100);//agrupa los datos de 200; actuliza los datos de 100;elimina los datos de 0
        }
        if (!showEmpty) {
            removeEmpty();
        }
        if (continueProcess) {//CONSTRUYO LA MATRIZ RESULTANTE PARA B
            createMatrixDifference();//matriz de resultados diferencia
        }
        if (continueProcess) {
            dataTableHtml = createDataTableResult();
            loadValuesGraph();//creo el grafico
            showGraphic = true;
            showTableResult = true;
            //btnExportDisabled = false;
            //message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Cruze realizado");
        }
    }

    /**
     * This method is responsible of separate the records when relationships
     * variable are realized from one to many.
     *
     * @param increment
     */
    private void separateRecordsFunction(int increment) {
        ResultSet rs;
        ResultSet rs2;
        String valueColumn;
        String[] splitRegisters;
        String[] splitForSql;
        try {
            //determinar el maximo
            sql = ""
                    + " SELECT  \n\r"
                    + "	MAX(record_id) \n\r"
                    + " FROM \n\r"
                    + "	indicators_records \n\r"
                    + " WHERE \n\r"
                    + "     user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n\r"
                    + "     indicator_id = " + (currentIndicator.getIndicatorId() + increment) + " \n\r";
            rs = connectionJdbcMB.consult(sql);
            rs.next();
            tuplesProcessed = rs.getInt(1);
            sql = ""
                    + " SELECT  \n\r"
                    + "	* \n\r"
                    + " FROM \n\r"
                    + "	indicators_records \n\r"
                    + " WHERE \n\r"
                    + "     user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n\r"
                    + "     indicator_id = " + (currentIndicator.getIndicatorId() + increment) + " \n\r";
            cpManager = new CopyManager((BaseConnection) connectionJdbcMB.getConn());
            for (int col = 1; col < 4; col++) {//se repite tres veces por cada una de las columnas
                sb = new StringBuilder();
                rs = connectionJdbcMB.consult(sql);
                while (rs.next()) {
                    if (rs.getString("column_" + col).indexOf("<=>") != -1) {
                        valueColumn = rs.getString("column_" + col);
                        valueColumn = valueColumn.substring(1, valueColumn.length() - 1);
                        splitRegisters = valueColumn.split(",");
                        for (int i = 0; i < splitRegisters.length; i++) {
                            splitForSql = splitRegisters[i].split("<=>");
                            if (splitForSql.length == 4) {
                                tuplesProcessed++;
                                if (splitForSql[1].compareTo("abuse_types") == 0) {
                                    //para el caso de "tipos de maltrato"="abuse_types" se debe agrupar valores
                                    if (splitForSql[3].compareTo("9") == 0 //        9;"PORNOGRAFIA CON NNA"
                                            || splitForSql[3].compareTo("10") == 0// 10;"TRATA DE PERSONAL PARA EXPLOTACION SEXUAL"
                                            || splitForSql[3].compareTo("11") == 0// 11;"ABUSO SEXUAL"
                                            || splitForSql[3].compareTo("12") == 0// 12;"ACOSO SEXUAL"
                                            || splitForSql[3].compareTo("13") == 0// 13;"ASALTO SEXUAL"
                                            || splitForSql[3].compareTo("14") == 0// 14;"EXPLOTACION SEXUAL"
                                            || splitForSql[3].compareTo("15") == 0)//15;"TURISMO SEXUAL"
                                    {
                                        splitForSql[3] = "3";//3;"VIOLENCIA SEXUAL"
                                    }
                                }
                                rs2 = connectionJdbcMB.consult("SELECT " + splitForSql[0] + " FROM " + splitForSql[1] + " WHERE " + splitForSql[2] + " = " + splitForSql[3]);
                                rs2.next();
                                if (col == 1) {
                                    sb.
                                            append(rs.getInt("user_id")).append("\t"). //  user_id integer NOT NULL, -- usuario que genera el indicador
                                            append(rs.getInt("indicator_id")).append("\t"). //  indicator_id integer NOT NULL, -- identificador del indicador
                                            append(tuplesProcessed).append("\t"). //  record_id integer NOT NULL, -- identificador del registro para ordenamiento
                                            append(rs2.getString(1)).append("\t"). //  column_1 text, -- valor de la primer variable cruzada
                                            append(rs.getString("column_2")).append("\t"). //  column_2 text, -- valor de la segunda variable cruzada
                                            append(rs.getString("column_3")).append("\t"). //  column_3 text, -- valor de la tercer variable cruzada
                                            append(0).append("\t"). //  count integer, -- numero de coincidencias para este cruce
                                            append(0).append("\n");                             //  population integer,
                                }
                                if (col == 2) {
                                    sb.
                                            append(rs.getInt("user_id")).append("\t"). //  user_id integer NOT NULL, -- usuario que genera el indicador
                                            append(rs.getInt("indicator_id")).append("\t"). //  indicator_id integer NOT NULL, -- identificador del indicador
                                            append(tuplesProcessed).append("\t"). //  record_id integer NOT NULL, -- identificador del registro para ordenamiento
                                            append(rs.getString("column_1")).append("\t"). //  column_1 text, -- valor de la primer variable cruzada
                                            append(rs2.getString(1)).append("\t"). //  column_2 text, -- valor de la segunda variable cruzada
                                            append(rs.getString("column_3")).append("\t"). //  column_3 text, -- valor de la tercer variable cruzada
                                            append(0).append("\t"). //  count integer, -- numero de coincidencias para este cruce
                                            append(0).append("\n");                             //  population integer,
                                }
                                if (col == 3) {
                                    sb.
                                            append(rs.getInt("user_id")).append("\t"). //  user_id integer NOT NULL, -- usuario que genera el indicador
                                            append(rs.getInt("indicator_id")).append("\t"). //  indicator_id integer NOT NULL, -- identificador del indicador
                                            append(tuplesProcessed).append("\t"). //  record_id integer NOT NULL, -- identificador del registro para ordenamiento
                                            append(rs.getString("column_1")).append("\t"). //  column_1 text, -- valor de la primer variable cruzada
                                            append(rs.getString("column_2")).append("\t"). //  column_2 text, -- valor de la segunda variable cruzada
                                            append(rs2.getString(1)).append("\t"). //  column_3 text, -- valor de la tercer variable cruzada
                                            append(0).append("\t"). //  count integer, -- numero de coincidencias para este cruce
                                            append(0).append("\n");                             //  population integer,
                                }
                            }
                        }
                    }
                }
                //REALIZO LA INSERCION
                cpManager.copyIn("COPY indicators_records FROM STDIN", new StringReader(sb.toString()));
                sb.delete(0, sb.length()); //System.out.println("Procesando... filas " + tuplesProcessed + " cargadas");
                //elimino registro que contengan 
                String sqlRemove = ""
                        + " DELETE  \n\r"
                        + " FROM \n\r"
                        + "	    indicators_records \n\r"
                        + " WHERE \n\r"
                        + "     user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n\r"
                        + "     indicator_id = " + (currentIndicator.getIndicatorId() + increment) + " AND \n\r"
                        + "     ( column_" + col + " like '%<=>%')";
                connectionJdbcMB.non_query(sqlRemove);
            }
        } catch (SQLException | IOException e) {
            System.out.println("Error 5 en " + this.getClass().getName() + ":" + e.toString());
        }
    }

    /**
     * This method is responsible to perform all possible combinations for the
     * data to be order according as the configuration is found.
     *
     * @param incrementTarget
     * @param incrementSource
     */
    private void createCombinations(int incrementTarget, int incrementSource) {
        //---------------------------------------------------------
        //FORMAR POSIBLES COMBINACIONES PARA QUE LOS DATOS QUEDEN ORDENADOS SEGUN COMO SE ENCUENTRE LA CONFIGURACION
        //---------------------------------------------------------
        columNames = new ArrayList<>();
        rowNames = new ArrayList<>();
        //columnTypeName = new ArrayList<String>();
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
                            append(currentIndicator.getIndicatorId() + incrementSource).append("\t").
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
                for (int i = 0; i < values1.size(); i++) {
                    columNames.add(values1.get(i));
                    for (int j = 0; j < values2.size(); j++) {
                        if (i == 0) {
                            rowNames.add(values2.get(j));
                        }
                        sb.
                                append(loginMB.getCurrentUser().getUserId()).append("\t").
                                append(currentIndicator.getIndicatorId() + incrementSource).append("\t").
                                append(id).append("\t").
                                append(values1.get(i)).append("\t").
                                append(values2.get(j)).append("\t").
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
                for (int i = 0; i < values1.size(); i++) {
                    for (int j = 0; j < values2.size(); j++) {
                        columNames.add(values1.get(i) + "}" + values2.get(j));
                        for (int k = 0; k < values3.size(); k++) {
                            if (i == 0 && j == 0) {
                                rowNames.add(values3.get(k));
                            }
                            sb.
                                    append(loginMB.getCurrentUser().getUserId()).append("\t").
                                    append(currentIndicator.getIndicatorId() + incrementSource).append("\t").
                                    append(id).append("\t").
                                    append(values1.get(i)).append("\t").
                                    append(values2.get(j)).append("\t").
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
            System.out.println("Error 3 en " + this.getClass().getName() + ":" + e.toString());
        }
    }

    /**
     * This method stores in the database the records of the cross that is
     * realice, the records is stored in the table indicators_records
     *
     * @param sqlConsult
     * @param increment
     */
    private void saveIndicatorRecords(String sqlConsult, int increment) {
        //------------------------------------------------------------------
        //AGEGAR UNA CONSULTA A LA TABLA indicators_records 
        //------------------------------------------------------------------
        try {
            cpManager = new CopyManager((BaseConnection) connectionJdbcMB.getConn());
            ResultSet rs = connectionJdbcMB.consult(sqlConsult);
            sb = new StringBuilder();
            tuplesProcessed = 0;
            int ncol = rs.getMetaData().getColumnCount();
            boolean haveNulls;
            while (rs.next()) {
                haveNulls = false;
                for (int i = 1; i <= ncol; i++) {//agrego solo los que no tengan valores nulos
                    if (rs.getString(i) == null || rs.getString(i).length() == 0 || rs.getString(i).compareToIgnoreCase("null") == 0) {
                        haveNulls = true;
                    }
                }
                if (!haveNulls) {//si no tiene nullos es agregado
                    tuplesProcessed++;
                    sb.
                            append(loginMB.getCurrentUser().getUserId()).append("\t").
                            append(currentIndicator.getIndicatorId() + increment).append("\t").
                            append(tuplesProcessed).append("\t");

                    for (int i = 1; i <= ncol; i++) {//datos del cruce
                        sb.append(rs.getString(i)).append("\t");
                    }
                    for (int i = 0; i < 3 - ncol; i++) {//variables no usadas(vacias)
                        sb.append("-").append("\t");
                    }
                    sb.append(0).append("\t").append(0).append("\n");//count y poblacion quedan como cero
                }
            }
            //REALIZO LA INSERCION
            cpManager.copyIn("COPY indicators_records FROM STDIN", new StringReader(sb.toString()));
            sb.delete(0, sb.length()); //System.out.println("Procesando... filas " + tuplesProcessed + " cargadas");
        } catch (SQLException | IOException e) {
            System.out.println("Error 4 en " + this.getClass().getName() + ":" + e.toString());
        }
    }

    /**
     * This method is responsible of group all the values obtained from the
     * cross of variables which were saved on indicators_records, arrange it
     * according to the options specified by the user.
     *
     * @param incrementTarget
     * @param incrementSource
     */
    private void groupingOfValues(int incrementTarget, int incrementSource) {
        //------------------------------------------------------------------
        //SE AGRUPAN LOS VALORES Y SE REALIZA EL CONTEO
        //------------------------------------------------------------------                
        sql = ""
                + " SELECT  \n\r"
                + "	column_1, \n\r"
                + "	column_2, \n\r"
                + "	column_3, \n\r"
                + "     count(*)  \n\r"
                + " FROM \n\r"
                + "	indicators_records \n\r"
                + " WHERE \n\r"
                + "     user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n\r"
                + "     indicator_id = " + (currentIndicator.getIndicatorId() + incrementTarget) + " \n\r"
                + " GROUP BY \n\r"
                + "	column_1, \n\r"
                + "	column_2, \n\r"
                + "	column_3 \n\r"
                + " ORDER BY \n\r"
                + "	column_1, \n\r"
                + "	column_2, \n\r"
                + "	column_3 \n\r";
        ResultSet rs = connectionJdbcMB.consult(sql);
        try {//actualizo el valor count de los registros currentIndicator.getIndicatorId() apartir de  currentIndicator.getIndicatorId()+100
            while (rs.next()) {
                sql = " "
                        + " UPDATE \n\r"
                        + "    indicators_records \n\r"
                        + " SET \n\r"
                        + "    count = " + rs.getString("count") + " \n\r"
                        + " WHERE \n\r"
                        + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n\r"
                        + "    indicator_id = " + (currentIndicator.getIndicatorId() + incrementSource) + " AND \n\r"
                        + "    column_1 like '" + rs.getString("column_1") + "' AND \n\r"
                        + "    column_2 like '" + rs.getString("column_2") + "' AND \n\r"
                        + "    column_3 like '" + rs.getString("column_3") + "' \n\r";
                connectionJdbcMB.non_query(sql);
            }
            sql = ""
                    + " DELETE FROM \n\r"
                    + "    indicators_records \n\r"
                    + " WHERE \n\r"
                    + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n\r"
                    + "    indicator_id = " + (currentIndicator.getIndicatorId() + incrementTarget) + " \n\r";
            connectionJdbcMB.non_query(sql);//elimino los valores del indicador incrementTarget
        } catch (Exception e) {
            System.out.println("Error 5 en " + this.getClass().getName() + ":" + e.toString());
        }
    }

    /**
     * Este metodo se encarga de agregar una tabla adicional si es necesario,
     * esto se hace en la seccion FROM y en la seccion WHERE de la consulta SQL.
     *
     * @param tableName
     */
    private void addToSourceTable(String tableName) {
        /*
         * cuando se necesita una tabla adicional se agrega
         * en la seccion FROM y en la seccion WHERE de la consulta SQL
         */
        if (tableName.indexOf("sivigila_event") == 0 && sourceTable.indexOf("sivigila_event") == -1) {
            sourceTable = sourceTable + " LEFT JOIN sivigila_event USING (non_fatal_injury_id) \n";
        }
        if (tableName.indexOf("sivigila_aggresor") == 0 && sourceTable.indexOf("sivigila_aggresor") == -1) {
            sourceTable = sourceTable + " JOIN  sivigila_aggresor USING (sivigila_agresor_id) \n";
        }
        if (tableName.indexOf("sivigila_victim") == 0 && sourceTable.indexOf("sivigila_victim") == -1) {
            sourceTable = sourceTable + " JOIN  sivigila_victim USING (sivigila_victim_id) \n";
        }
        if (tableName.indexOf("fatal_injury_murder") == 0 && sourceTable.indexOf("fatal_injury_murder") == -1) {
            sourceTable = sourceTable + " LEFT JOIN fatal_injury_murder USING (fatal_injury_id) \n";
        }
        if (tableName.indexOf("fatal_injury_traffic") == 0 && sourceTable.indexOf("fatal_injury_traffic") == -1) {
            sourceTable = sourceTable + " LEFT JOIN fatal_injury_traffic USING (fatal_injury_id) \n";
        }
        if (tableName.indexOf("fatal_injury_suicide") == 0 && sourceTable.indexOf("fatal_injury_suicide") == -1) {
            sourceTable = sourceTable + " LEFT JOIN fatal_injury_suicide USING (fatal_injury_id) \n";
        }
        if (tableName.indexOf("fatal_injury_accident") == 0 && sourceTable.indexOf("fatal_injury_accident") == -1) {
            sourceTable = sourceTable + " LEFT JOIN fatal_injury_accident USING (fatal_injury_id) \n";
        }
        if (tableName.indexOf("non_fatal_interpersonal") == 0 && sourceTable.indexOf("non_fatal_interpersonal") == -1) {
            sourceTable = sourceTable + " LEFT JOIN non_fatal_interpersonal USING (non_fatal_injury_id) \n";
        }
        if (tableName.indexOf("non_fatal_self_inflicted") == 0 && sourceTable.indexOf("non_fatal_self_inflicted") == -1) {
            sourceTable = sourceTable + " LEFT JOIN non_fatal_self_inflicted USING (non_fatal_injury_id) \n";
        }
        if (tableName.indexOf("non_fatal_transport") == 0 && sourceTable.indexOf("non_fatal_transport") == -1) {
            sourceTable = sourceTable + " LEFT JOIN non_fatal_transport USING (non_fatal_injury_id) \n";
        }
        if (tableName.indexOf("non_fatal_domestic_violence") == 0 && sourceTable.indexOf("non_fatal_domestic_violence") == -1) {
            sourceTable = sourceTable + " LEFT JOIN non_fatal_domestic_violence USING (non_fatal_injury_id) \n";
        }
    }

    /**
     * This method is responsible for establishing the case to be handled in the
     * SQL query depending on the value of the parameters.
     *
     * @param sqlReturn
     * @param source_table
     * @param column_whit_name
     * @param category_table
     * @param column_whit_id
     * @param as_name
     * @return
     */
    private String createCase(String sqlReturn, String source_table, String column_whit_name, String category_table, String column_whit_id, String as_name) {
        String strReturn = sqlReturn + ""
                + " CASE \n\r"
                + "    WHEN " + source_table + " is null THEN 'SIN DATO' \n\r"
                + "    ELSE \n\r"
                + "    ( \n\r"
                + "       SELECT \n\r"
                + "          " + column_whit_name + " \n\r"
                + "       FROM \n\r"
                + "          " + category_table + " \n\r"
                + "       WHERE \n\r"
                + "          " + column_whit_id + " = " + source_table + " \n\r"
                + "    )"
                + " END AS " + as_name;
        return strReturn;
    }

    /**
     * This method is responsible for assemble the necessary query to the
     * indicator that the user is working, where are determined all necessary
     * parameters that must have the SQL query to perform the crossing variable
     * as is the type of temporal the disaggregation, type of injury, age,
     * available variables.
     *
     * @param initialDateStr
     * @param endDateStr
     * @return
     */
    private String createIndicatorConsult(String initialDateStr, String endDateStr) {
        String sqlReturn = " SELECT  \n\r";
        separateRecords = false;
        sourceTable = "";//tabla adicional que se usara en la seccion "FROM" de la consulta sql
        String filterSourceTable = "";//filtro adicional usado en la seccion "WHERE" de la consulta sql
        ResultSet rs = null;
        for (int i = 0; i < variablesCrossData.size(); i++) {
            switch (VariablesEnum.convert(variablesCrossData.get(i).getGeneric_table())) {//nombre de variable 
                case temporalDisaggregation://DETERMINAR LA DESAGREGACION TEMPORAL -----------------------                   
                    sqlReturn = sqlReturn + "   CASE \n\r";
                    for (int j = 0; j < variablesCrossData.get(i).getValuesId().size(); j++) {
                        String[] splitDates = variablesCrossData.get(i).getValuesId().get(j).split("}");
                        sqlReturn = sqlReturn + "       WHEN ( \n\r";
                        sqlReturn = sqlReturn + "           " + currentIndicator.getInjuryType() + ".injury_date >= to_date('" + splitDates[0] + "','dd/MM/yyyy') AND \n\r";
                        sqlReturn = sqlReturn + "           " + currentIndicator.getInjuryType() + ".injury_date <= to_date('" + splitDates[1] + "','dd/MM/yyyy') \n\r";
                        sqlReturn = sqlReturn + "       ) THEN '" + variablesCrossData.get(i).getValues().get(j) + "'  \n\r";
                    }
                    sqlReturn = sqlReturn + "   END AS fecha";
                    break;
                case injuries_fatal://TIPO DE LESION FATAL-----------------------
                    sqlReturn = sqlReturn + "   CASE (SELECT injury_id FROM injuries WHERE injury_id=" + currentIndicator.getInjuryType() + ".injury_id) \n\r";
                    for (int j = 0; j < variablesCrossData.get(i).getValues().size(); j++) {
                        sqlReturn = sqlReturn + "       WHEN '" + variablesCrossData.get(i).getValuesId().get(j) + "' THEN '" + variablesCrossData.get(i).getValues().get(j) + "'  \n\r";
                    }
                    sqlReturn = sqlReturn + "   END AS tipo_lesion";
                    break;
                case injuries_non_fatal://TIPO DE LESION NO FATAL-----------------------
                    sqlReturn = sqlReturn + "   CASE (SELECT injury_id FROM injuries WHERE injury_id=" + currentIndicator.getInjuryType() + ".injury_id) \n\r";
                    for (int j = 0; j < variablesCrossData.get(i).getValues().size(); j++) {
                        sqlReturn = sqlReturn + "       WHEN '" + variablesCrossData.get(i).getValuesId().get(j) + "' THEN '" + variablesCrossData.get(i).getValues().get(j) + "'  \n\r";
                    }
                    sqlReturn = sqlReturn + "       WHEN '55' THEN 'VIOLENCIA INTRAFAMILIAR'  \n\r";
                    sqlReturn = sqlReturn + "       WHEN '56' THEN 'VIOLENCIA INTRAFAMILIAR'  \n\r";
                    sqlReturn = sqlReturn + "   END AS tipo_lesion";
                    break;
                case age://DETERMINAR EDAD -----------------------          
                    if (variablesCrossData.get(i).getSource_table().compareTo("victims.victim_age") == 0) {
                        sqlReturn = sqlReturn + "   CASE \n\r";
                        sqlReturn = sqlReturn + "       WHEN (victims.victim_age is null)  THEN 'SIN DATO' \n\r";
                        for (int j = 0; j < variablesCrossData.get(i).getValuesConfigured().size(); j++) {
                            if (variablesCrossData.get(i).getValuesConfigured().get(j).compareTo("SIN DATO") != 0) {
                                String[] splitAge = variablesCrossData.get(i).getValuesConfigured().get(j).split("/");
                                if (splitAge[1].compareTo("n") == 0) {
                                    splitAge[1] = "200";
                                }
                                sqlReturn = sqlReturn + ""
                                        + "       WHEN (( \n\r"
                                        + "           CASE \n\r"
                                        + "               WHEN (victims.age_type_id = 2 or victims.age_type_id = 3) THEN 1 \n\r"
                                        + "               WHEN (victims.age_type_id = 1 or victims.age_type_id is null) THEN victims.victim_age \n\r"
                                        + "           END \n\r"
                                        + "       ) between " + splitAge[0] + " and " + splitAge[1] + ") THEN '" + variablesCrossData.get(i).getValuesConfigured().get(j) + "'  \n\r";
                            }
                        }
                        sqlReturn = sqlReturn + "   END AS edad";
                    }
                    if (variablesCrossData.get(i).getSource_table().compareTo("sivigila_aggresor.age") == 0) {
                        addToSourceTable("sivigila_event");
                        addToSourceTable("sivigila_aggresor");
                        sqlReturn = sqlReturn + "   CASE \n\r";
                        sqlReturn = sqlReturn + "       WHEN (sivigila_aggresor.age is null)  THEN 'SIN DATO' \n\r";
                        for (int j = 0; j < variablesCrossData.get(i).getValuesConfigured().size(); j++) {
                            if (variablesCrossData.get(i).getValuesConfigured().get(j).compareTo("SIN DATO") != 0) {
                                String[] splitAge = variablesCrossData.get(i).getValuesConfigured().get(j).split("/");
                                if (splitAge[1].compareTo("n") == 0) {
                                    splitAge[1] = "200";
                                }
                                sqlReturn = sqlReturn + ""
                                        + "       WHEN ( sivigila_aggresor.age between " + splitAge[0] + " and " + splitAge[1] + ") THEN '" + variablesCrossData.get(i).getValuesConfigured().get(j) + "'  \n\r";
                            }
                        }
                        sqlReturn = sqlReturn + "   END AS edad_agresor";
                    }
                    break;
                case hour://HORA -----------------------
                    sqlReturn = sqlReturn + ""
                            + "   CASE \n\r"
                            + "       WHEN (" + currentIndicator.getInjuryType() + ".injury_time is null)  THEN 'SIN DATO' \n\r";
                    for (int j = 0; j < variablesCrossData.get(i).getValuesConfigured().size(); j++) {
                        if (variablesCrossData.get(i).getValuesConfigured().get(j).compareTo("SIN DATO") != 0) {
                            String[] splitAge = variablesCrossData.get(i).getValuesConfigured().get(j).split("/");
                            String[] splitAge2 = splitAge[0].split(":");
                            String[] splitAge3 = splitAge[1].split(":");
                            sqlReturn = sqlReturn + ""
                                    + "       WHEN (extract(hour from " + currentIndicator.getInjuryType() + ".injury_time) \n\r"
                                    + "       between " + splitAge2[0] + " and " + splitAge3[0] + ") THEN '" + variablesCrossData.get(i).getValuesConfigured().get(j) + "'  \n\r";
                        }
                    }
                    sqlReturn = sqlReturn + "   END AS hora";
                    break;
                case neighborhoods://NOMBRE DEL BARRIO -----------------------
                    sqlReturn = sqlReturn + ""
                            + "   CASE \n\r"
                            + "      WHEN " + currentIndicator.getInjuryType() + ".injury_neighborhood_id is null THEN 'SIN DATO' \n\r"
                            + "      ELSE (SELECT neighborhood_name FROM neighborhoods WHERE neighborhood_id = " + currentIndicator.getInjuryType() + ".injury_neighborhood_id) \n\r"
                            // + "           AND neighborhood_area != 2) \n\r"
                            + "   END AS barrio";
                    break;
                case communes://COMUNA -----------------------
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + currentIndicator.getInjuryType() + ".injury_neighborhood_id is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          communes.commune_name \n\r"
                            + "       FROM \n\r"
                            + "          public.communes, \n\r"
                            + "          public.neighborhoods \n\r"
                            + "       WHERE \n\r"
                            + "          neighborhoods.neighborhood_suburb = communes.commune_id AND \n\r"
                            + "          neighborhoods.neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id \n\r"
                            + "    )"
                            + " END AS comuna";
                    break;
                case quadrants://CUADRANTE -----------------------
                    //sqlReturn = sqlReturn + "   CAST((SELECT neighborhood_quadrant FROM neighborhoods WHERE neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id) as text) as cuadrante \n\r";
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + currentIndicator.getInjuryType() + ".quadrant_id is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          quadrants.quadrant_name \n\r"
                            + "       FROM \n\r"
                            + "          public.quadrants \n\r"
                            + "       WHERE \n\r"
                            + "          quadrants.quadrant_id=" + currentIndicator.getInjuryType() + ".quadrant_id \n\r"
                            + "    )"
                            + " END AS cuadrante";
                    break;
                case corridors://CORREDOR -----------------------
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + currentIndicator.getInjuryType() + ".injury_neighborhood_id is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          corridors.corridor_name \n\r"
                            + "       FROM \n\r"
                            + "          public.corridors, \n\r"
                            + "          public.neighborhoods \n\r"
                            + "       WHERE \n\r"
                            + "          neighborhoods.neighborhood_corridor = corridors.corridor_id AND \n\r"
                            + "          neighborhoods.neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id \n\r"
                            + "    )"
                            + " END AS corredor";
                    break;
                case areas://ZONA -----------------------        
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + currentIndicator.getInjuryType() + ".injury_neighborhood_id is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          areas.area_name \n\r"
                            + "       FROM \n\r"
                            + "          public.areas, \n\r"
                            + "          public.neighborhoods \n\r"
                            + "       WHERE \n\r"
                            + "          neighborhoods.neighborhood_area = areas.area_id AND \n\r"
                            + "          neighborhoods.neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id \n\r"
                            + "    )"
                            + " END AS zona";
                    break;
                case genders://GENERO  ----------------------
                    if (variablesCrossData.get(i).getSource_table().compareTo("sivigila_aggresor.gender") == 0) {
                        addToSourceTable("sivigila_event");
                        addToSourceTable("sivigila_aggresor");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "gender_name", "genders", "gender_id", "genero_agresor");
                    }
                    if (variablesCrossData.get(i).getSource_table().compareTo("victims.gender_id") == 0) {
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "gender_name", "genders", "gender_id", "genero_victima");
                    }
                    break;
                case days://DIA SEMANA ---------------------
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + currentIndicator.getInjuryType() + ".injury_day_of_week is null THEN 'SIN DATO' \n\r"
                            + "    ELSE ( " + currentIndicator.getInjuryType() + ".injury_day_of_week )"
                            + " END AS dia_semana";
                    break;
                case year://AÑO -----------------------
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + currentIndicator.getInjuryType() + ".injury_date is null THEN 'SIN DATO' \n\r"
                            + "    ELSE ( CAST(extract(year from " + currentIndicator.getInjuryType() + ".injury_date)::int as text) )"
                            + " END AS anyo";
                    break;
                case month://MES 
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + currentIndicator.getInjuryType() + ".injury_date is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          months.month_name \n\r"
                            + "       FROM \n\r"
                            + "          public.months \n\r"
                            + "       WHERE \n\r"
                            + "          (extract(month from " + currentIndicator.getInjuryType() + ".injury_date)::int) = months.month_id \n\r"
                            + "    )"
                            + " END AS mes";
                    break;
                case sivigila_educational_level://escolaridad agresor
                    if (variablesCrossData.get(i).getSource_table().compareTo("sivigila_aggresor.educational_level_id") == 0) {
                        addToSourceTable("sivigila_event");
                        addToSourceTable("sivigila_aggresor");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "sivigila_educational_level_name", "sivigila_educational_level", "sivigila_educational_level_id", "escolaridad_agresor");
                    }
                    if (variablesCrossData.get(i).getSource_table().compareTo("sivigila_victim.educational_level_id") == 0) {
                        addToSourceTable("sivigila_event");
                        addToSourceTable("sivigila_victim");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "sivigila_educational_level_name", "sivigila_educational_level", "sivigila_educational_level_id", "escolaridad_victima");
                    }
                    break;
                case sivigila_vulnerability://factor de vulnerabilidad
                    addToSourceTable("sivigila_event");
                    addToSourceTable("sivigila_victim");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "sivigila_vulnerability_name", "sivigila_vulnerability", "sivigila_vulnerability_id", "factor_vulnerabilidad");
                    break;
                case sivigila_group://";"grupo agresor"
                    addToSourceTable("sivigila_event");
                    addToSourceTable("sivigila_aggresor");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "sivigila_group_name", "sivigila_group", "sivigila_group_id", "grupo_agresor");
                    break;
                case degree:
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "name_degree", "degree", "degree_id", "grado_quemadura");
                    break;
                case kinds_of_injury://RELACION DE UNO A MUCHOS
                    separateRecords = true;
                    sqlReturn = sqlReturn + ""
                            + " CASE \n"
                            + "    WHEN( SELECT cast(array_agg('kind_injury_name<=>kinds_of_injury<=>kind_injury_id<=>'||kind_injury_id) as text \n)"
                            + "          FROM non_fatal_kind_of_injury \n"
                            + "          WHERE non_fatal_kind_of_injury.non_fatal_injury_id=non_fatal_injuries.non_fatal_injury_id ) is null THEN 'SIN DATO'\n"
                            + "    ELSE( SELECT cast(array_agg('kind_injury_name<=>kinds_of_injury<=>kind_injury_id<=>'||kind_injury_id) as text \n)"
                            + "          FROM non_fatal_kind_of_injury \n"
                            + "          WHERE non_fatal_kind_of_injury.non_fatal_injury_id=non_fatal_injuries.non_fatal_injury_id )\n"
                            + " END AS naturaleza_lesion \n";
                    break;
                case anatomical_locations://RELACION DE UNO A MUCHOS
                    separateRecords = true;
                    sqlReturn = sqlReturn + ""
                            + " CASE \n"
                            + "    WHEN (SELECT cast(array_agg('anatomical_location_name<=>anatomical_locations<=>anatomical_location_id<=>'||anatomical_location_id) as text \n)"
                            + "          FROM non_fatal_anatomical_location \n"
                            + "          WHERE non_fatal_anatomical_location.non_fatal_injury_id=non_fatal_injuries.non_fatal_injury_id ) is null THEN 'SIN DATO'\n"
                            + "    ELSE (SELECT cast(array_agg('anatomical_location_name<=>anatomical_locations<=>anatomical_location_id<=>'||anatomical_location_id) as text \n)"
                            + "         FROM non_fatal_anatomical_location \n"
                            + "         WHERE non_fatal_anatomical_location.non_fatal_injury_id=non_fatal_injuries.non_fatal_injury_id )\n"
                            + " END AS sitio_anatomico \n";
                    break;

                case actions_to_take://RELACION DE UNO A MUCHOS
                    separateRecords = true;
                    sqlReturn = sqlReturn + ""
                            + " CASE \n"
                            + "    WHEN (SELECT cast(array_agg('action_name<=>actions_to_take<=>action_id<=>'||action_id) as text \n)"
                            + "          FROM domestic_violence_action_to_take \n"
                            + "          WHERE domestic_violence_action_to_take.non_fatal_injury_id=non_fatal_injuries.non_fatal_injury_id ) is null THEN 'SIN DATO'\n"
                            + "    ELSE (SELECT cast(array_agg('action_name<=>actions_to_take<=>action_id<=>'||action_id) as text \n)"
                            + "          FROM domestic_violence_action_to_take \n"
                            + "          WHERE domestic_violence_action_to_take.non_fatal_injury_id=non_fatal_injuries.non_fatal_injury_id )\n"
                            + " END AS acciones_a_realizar \n";
                    break;
                case security_elements://RELACION DE UNO A MUCHOS
                    separateRecords = true;
                    sqlReturn = sqlReturn + ""
                            + " CASE \n"
                            + "    WHEN (SELECT cast(array_agg('security_element_name<=>security_elements<=>security_element_id<=>'||security_element_id) as text \n)"
                            + "          FROM non_fatal_transport_security_element \n"
                            + "          WHERE non_fatal_transport_security_element.non_fatal_injury_id=non_fatal_injuries.non_fatal_injury_id ) is null THEN 'SIN DATO'\n"
                            + "    ELSE (SELECT cast(array_agg('security_element_name<=>security_elements<=>security_element_id<=>'||security_element_id) as text \n)"
                            + "          FROM non_fatal_transport_security_element \n"
                            + "          WHERE non_fatal_transport_security_element.non_fatal_injury_id=non_fatal_injuries.non_fatal_injury_id )\n"
                            + " END AS elementos_seguridad \n";
                    break;
                case vulnerable_groups://";"grupo poblacional"//RELACION DE UNO A MUCHOS
                    separateRecords = true;
                    sqlReturn = sqlReturn + ""
                            + " CASE \n"
                            + "    WHEN (SELECT cast(array_agg('vulnerable_group_name<=>vulnerable_groups<=>vulnerable_group_id<=>'||vulnerable_group_id) as text \n)"
                            + "          FROM victim_vulnerable_group \n"
                            + "          WHERE victim_vulnerable_group.victim_id=victims.victim_id ) is null THEN 'SIN DATO'\n"
                            + "    ELSE (SELECT cast(array_agg('vulnerable_group_name<=>vulnerable_groups<=>vulnerable_group_id<=>'||vulnerable_group_id) as text \n)"
                            + "          FROM victim_vulnerable_group \n"
                            + "          WHERE victim_vulnerable_group.victim_id=victims.victim_id )\n"
                            + " END AS grupo_poblacional \n";
                    break;
                case abuse_types://";"tipo maltrato" //RELACION DE UNO A MUCHOS
                    separateRecords = true;
                    sqlReturn = sqlReturn + ""
                            + " CASE \n"
                            + "    WHEN (SELECT cast(array_agg('abuse_type_name<=>abuse_types<=>abuse_type_id<=>'||abuse_type_id) as text \n)"
                            + "          FROM domestic_violence_abuse_type \n"
                            + "          WHERE domestic_violence_abuse_type.non_fatal_injury_id=non_fatal_injuries.non_fatal_injury_id ) is null THEN 'SIN DATO'\n"
                            + "    ELSE (SELECT cast(array_agg('abuse_type_name<=>abuse_types<=>abuse_type_id<=>'||abuse_type_id) as text \n)"
                            + "          FROM domestic_violence_abuse_type \n"
                            + "          WHERE domestic_violence_abuse_type.non_fatal_injury_id=non_fatal_injuries.non_fatal_injury_id )\n"
                            + " END AS tipo_maltrato \n";
                    break;
                case ethnic_groups://";"pertenecia étnica"
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "ethnic_group_name", "ethnic_groups", "ethnic_group_id", "grupo_etnico");
                    break;
                case aggressor_types:
                    //tipo agresor vif
                    if (variablesCrossData.get(i).getSource_table().compareTo("domestic_violence_aggressor_type") == 0) {
                        separateRecords = true;
                        sqlReturn = sqlReturn + ""
                                + " CASE \n"
                                + "    WHEN (SELECT cast(array_agg('aggressor_type_name<=>aggressor_types<=>aggressor_type_id<=>'||aggressor_type_id) as text \n)"
                                + "          FROM domestic_violence_aggressor_type \n"
                                + "          WHERE domestic_violence_aggressor_type.non_fatal_injury_id=non_fatal_injuries.non_fatal_injury_id ) is null THEN 'SIN DATO'\n"
                                + "    ELSE (SELECT cast(array_agg('aggressor_type_name<=>aggressor_types<=>aggressor_type_id<=>'||aggressor_type_id) as text \n)"
                                + "          FROM domestic_violence_aggressor_type \n"
                                + "          WHERE domestic_violence_aggressor_type.non_fatal_injury_id=non_fatal_injuries.non_fatal_injury_id ) \n"
                                + " END AS tipo_agresor \n";
                    }
                    //relacion familiar victima sivigila"
                    if (variablesCrossData.get(i).getSource_table().compareTo("sivigila_aggresor.relative_id") == 0) {
                        addToSourceTable("sivigila_event");
                        addToSourceTable("sivigila_aggresor");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "aggressor_type_name", "aggressor_types", "aggressor_type_id", "relacion_familiar_victima");
                    }
                    break;
                case sivigila_no_relative://";"relación no familiar víctima"
                    addToSourceTable("sivigila_event");
                    addToSourceTable("sivigila_aggresor");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "sivigila_no_relative_name", "sivigila_no_relative", "sivigila_no_relative_id", "relacion_no_familiar_victima");
                    break;
                case sivigila_tip_ss://";"tipo régimen"                    
                    addToSourceTable("sivigila_event");
                    addToSourceTable("sivigila_victim");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "sivigila_tip_ss_name", "sivigila_tip_ss", "sivigila_tip_ss_id", "tipo_regimen");
                    break;
                case public_health_actions://acciones en salud publica //RELACION DE UNO A MUCHOS
                    separateRecords = true;
                    sqlReturn = sqlReturn + ""
                            + " CASE \n"
                            + "    WHEN (SELECT cast(array_agg('action_name<=>public_health_actions<=>action_id<=>'||action_id) as text \n)"
                            + "          FROM sivigila_event_public_health \n"
                            + "          WHERE sivigila_event_public_health.non_fatal_injury_id=non_fatal_injuries.non_fatal_injury_id ) is null THEN 'SIN DATO'\n"
                            + "    ELSE (SELECT cast(array_agg('action_name<=>public_health_actions<=>action_id<=>'||action_id) as text \n)"
                            + "          FROM sivigila_event_public_health \n"
                            + "          WHERE sivigila_event_public_health.non_fatal_injury_id=non_fatal_injuries.non_fatal_injury_id )\n"
                            + " END AS aciones_salud \n";
                    break;
                case jobs:
                    if (variablesCrossData.get(i).getSource_table().compareTo("sivigila_aggresor.occupation") == 0) {
                        addToSourceTable("sivigila_event");
                        addToSourceTable("sivigila_aggresor");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "job_name", "jobs", "job_id", "ocupacion_agresor");
                    }
                    if (variablesCrossData.get(i).getSource_table().compareTo("victims.job_id") == 0) {
                        sqlReturn = createCase(sqlReturn, "victims.job_id", "job_name", "jobs", "job_id", "ocupacion_victima");
                    }
                    break;
                case sivigila_mechanism:
                    addToSourceTable("sivigila_event");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "sivigila_mechanism_name", "sivigila_mechanism", "sivigila_mechanism_id", "mecanismo_sivigila");
                    break;
                case insurance:
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "insurance_name", "insurance", "insurance_id", "aseguradora");
                    break;
                case contexts://(interpersonale en comunidad)
                    addToSourceTable("non_fatal_interpersonal");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "context_name", "contexts", "context_id", "contexto");
                    break;
                case aggressor_genders://(interpersonale en comunidad)
                    addToSourceTable("non_fatal_interpersonal");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "gender_name", "aggressor_genders", "gender_id", "sexo_agresor");
                    break;
                case relationships_to_victim://(interpersonale en comunidad)
                    addToSourceTable("non_fatal_interpersonal");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "relationship_victim_name", "relationships_to_victim", "relationship_victim_id", "relacion_agresor_victima");
                    break;
                case precipitating_factors://(violencia autoinflingida)
                    addToSourceTable("non_fatal_self_inflicted");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "precipitating_factor_name", "precipitating_factors", "precipitating_factor_id", "factor_precipitante");
                    break;
                case transport_users://(accidentes transito)
                    addToSourceTable("non_fatal_transport");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "transport_user_name", "transport_users", "transport_user_id", "tipo_usuario");
                    break;
                case transport_counterparts://(accidentes transito)
                    addToSourceTable("non_fatal_transport");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "transport_counterpart_name", "transport_counterparts", "transport_counterpart_id", "tipo_transporte_contraparte");
                    break;
                case transport_types://(accidentes transito)
                    addToSourceTable("non_fatal_transport");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "transport_type_name", "transport_types", "transport_type_id", "tipo_transporte_victima");
                    break;
                case destinations_of_patient://(Violencia Interpersonal en Familia)(lesiones en transporte)
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "destination_patient_name", "destinations_of_patient", "destination_patient_id", "destino_paciente");
                    break;
                case activities://(Violencia Interpersonal en Familia)(lesiones en transporte)
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "activity_name", "activities", "activity_id", "actividad_realizada");
                    break;
                case non_fatal_places://(Violencia Interpersonal en Familia)(lesiones en transporte)(interpersonal en comunidad)
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "non_fatal_place_name", "non_fatal_places", "non_fatal_place_id", "lugar_hecho");
                    break;
                case use_alcohol_drugs://(Violencia Interpersonal en Familia)(lesiones en transporte)                    
                    if (variablesCrossData.get(i).getSource_table().indexOf("use_alcohol_id") != -1) {
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "use_alcohol_drugs_name", "use_alcohol_drugs", "use_alcohol_drugs_id", "uso_alcohol");
                    }
                    if (variablesCrossData.get(i).getSource_table().indexOf("use_drugs_id") != -1) {
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "use_alcohol_drugs_name", "use_alcohol_drugs", "use_alcohol_drugs_id", "uso_drogas");
                    }
                    break;
                case mechanisms://(Violencia Interpersonal en Familia)(lesiones en transporte)
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "mechanism_name", "mechanisms", "mechanism_id", "mecanismo");
                    break;
                case protective_measures://(transito)
                    addToSourceTable("fatal_injury_traffic");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "protective_measures_name", "protective_measures", "protective_measures_id", "medidas_proteccion");
                    break;
                case victim_characteristics://(transito)
                    addToSourceTable("fatal_injury_traffic");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "characteristic_name", "victim_characteristics", "characteristic_id", "caracteristicas_victima");
                    break;
                case road_types://(transito)
                    addToSourceTable("fatal_injury_traffic");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "road_type_name", "road_types", "road_type_id", "tipo_via");
                    break;
                case service_types://(transito)
                    //contraparte
                    if (variablesCrossData.get(i).getSource_table().indexOf("counterpart_service_type.service_type_id") != -1) {
                        separateRecords = true;
                        sqlReturn = sqlReturn + ""
                                + "CASE \n"
                                + "    WHEN( SELECT cast(array_agg('service_type_name<=>service_types<=>service_type_id<=>'||service_type_id) as text)  \n"
                                + "          FROM counterpart_service_type \n"
                                + "          WHERE counterpart_service_type.fatal_injury_id=fatal_injuries.fatal_injury_id) is null THEN 'SIN DATO'\n"
                                + "    ELSE (SELECT cast(array_agg('service_type_name<=>service_types<=>service_type_id<=>'||service_type_id) as text)  \n"
                                + "          FROM counterpart_service_type \n"
                                + "          WHERE counterpart_service_type.fatal_injury_id=fatal_injuries.fatal_injury_id)     \n"
                                + "    END AS servicio_contraparte \n";
                    }
                    //victima
                    if (variablesCrossData.get(i).getSource_table().indexOf("fatal_injury_traffic.service_type_id") != -1) {
                        addToSourceTable("fatal_injury_traffic");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "service_type_name", "service_types", "service_type_id", "servicio_victima");
                    }
                    break;
                case involved_vehicles://(transito)
                    //contraparte
                    if (variablesCrossData.get(i).getSource_table().indexOf("counterpart_involved_vehicle.involved_vehicle_id") != -1) {
                        separateRecords = true;
                        sqlReturn = sqlReturn + ""
                                + "CASE \n"
                                + "    WHEN( SELECT cast(array_agg('involved_vehicle_name<=>involved_vehicles<=>involved_vehicle_id<=>'||involved_vehicle_id) as text)  \n"
                                + "          FROM counterpart_involved_vehicle \n"
                                + "          WHERE counterpart_involved_vehicle.fatal_injury_id=fatal_injuries.fatal_injury_id) is null THEN 'SIN DATO'\n"
                                + "    ELSE (SELECT cast(array_agg('involved_vehicle_name<=>involved_vehicles<=>involved_vehicle_id<=>'||involved_vehicle_id) as text)  \n"
                                + "          FROM counterpart_involved_vehicle \n"
                                + "          WHERE counterpart_involved_vehicle.fatal_injury_id=fatal_injuries.fatal_injury_id)     \n"
                                + "    END AS vehiculo_contraparte \n";
                    }
                    //victima
                    if (variablesCrossData.get(i).getSource_table().indexOf("fatal_injury_traffic.involved_vehicle_id") != -1) {
                        addToSourceTable("fatal_injury_traffic");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "involved_vehicle_name", "involved_vehicles", "involved_vehicle_id", "vehiculo_victima");
                    }
                    break;
                case accident_classes://(transito)
                    addToSourceTable("fatal_injury_traffic");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "accident_class_name", "accident_classes", "accident_class_id", "clase_accidente");
                    break;
                case boolean3:
                    if (variablesCrossData.get(i).getSource_table().compareTo("fatal_injury_suicide.previous_attempt") == 0) {
                        addToSourceTable("fatal_injury_suicide");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "boolean_name", "boolean3", "boolean_id", "intento_previo");
                    }
                    if (variablesCrossData.get(i).getSource_table().compareTo("fatal_injury_suicide.mental_antecedent") == 0) {
                        addToSourceTable("fatal_injury_suicide");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "boolean_name", "boolean3", "boolean_id", "antecedentes_mentales");
                    }
                    if (variablesCrossData.get(i).getSource_table().compareTo("non_fatal_self_inflicted.previous_attempt") == 0) {
                        addToSourceTable("non_fatal_self_inflicted");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "boolean_name", "boolean3", "boolean_id", "intento_previo");
                    }
                    if (variablesCrossData.get(i).getSource_table().compareTo("non_fatal_self_inflicted.mental_antecedent") == 0) {
                        addToSourceTable("non_fatal_self_inflicted");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "boolean_name", "boolean3", "boolean_id", "antecedentes_mentales");
                    }
                    if (variablesCrossData.get(i).getSource_table().compareTo("non_fatal_interpersonal.previous_antecedent") == 0) {
                        addToSourceTable("non_fatal_interpersonal");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "boolean_name", "boolean3", "boolean_id", "antecedente_previo");
                    }
                    if (variablesCrossData.get(i).getSource_table().compareTo("sivigila_event.further_fieldwork") == 0) {
                        addToSourceTable("sivigila_event");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "boolean_name", "boolean3", "boolean_id", "trabajo_campo");
                    }
                    if (variablesCrossData.get(i).getSource_table().compareTo("sivigila_victim.antecedent") == 0) {
                        addToSourceTable("sivigila_event");
                        addToSourceTable("sivigila_victim");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "boolean_name", "boolean3", "boolean_id", "antecedentes_similares");
                    }
                    if (variablesCrossData.get(i).getSource_table().compareTo("sivigila_aggresor.live_together") == 0) {
                        addToSourceTable("sivigila_event");
                        addToSourceTable("sivigila_aggresor");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "boolean_name", "boolean3", "boolean_id", "convive_con_agresor");
                    }
                    if (variablesCrossData.get(i).getSource_table().compareTo("sivigila_aggresor.alcohol_or_drugs") == 0) {
                        addToSourceTable("sivigila_event");
                        addToSourceTable("sivigila_aggresor");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "boolean_name", "boolean3", "boolean_id", "presencia_alcohol_agresor");
                    }
                    if (variablesCrossData.get(i).getSource_table().compareTo("sivigila_event.recommended_protection") == 0) {
                        addToSourceTable("sivigila_event");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "boolean3.boolean_name", "boolean3", "boolean_id", "recomienda_proteccion");
                    }
                    if (variablesCrossData.get(i).getSource_table().compareTo("sivigila_event.conflict_zone") == 0) {
                        addToSourceTable("sivigila_event");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "boolean3.boolean_name", "boolean3", "boolean_id", "zona_conflicto");
                    }
                    break;
                case related_events://EVENTOS RELACIONADOS (suicidio)
                    addToSourceTable("fatal_injury_suicide");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "related_event_name", "related_events", "related_event_id", "eventos_relacionados");
                    break;
                case accident_mechanisms://MECANISMO (muerte accidental)                     
                    addToSourceTable("fatal_injury_accident");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "accident_mechanism_name", "accident_mechanisms", "accident_mechanism_id", "mecanismo_accidente");
                    break;
                case suicide_mechanisms://MECANISMO (suicidio)
                    addToSourceTable("fatal_injury_suicide");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "suicide_mechanism_name", "suicide_mechanisms", "suicide_mechanism_id", "mecanismo_suicidio");
                    break;
                case murder_contexts://CONTEXTO (homicidios)
                    addToSourceTable("fatal_injury_murder");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "murder_context_name", "murder_contexts", "murder_context_id", "contexto");
                    break;
                case alcohol_levels://CONSUMO ALCOHOL                     
                    if (variablesCrossData.get(i).getSource_table().compareTo("fatal_injury_traffic.alcohol_level_counterpart_id") == 0) {//(transito)
                        addToSourceTable("fatal_injury_traffic");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "alcohol_level_name", "alcohol_levels", "alcohol_level_id", "consumo_alcohol_contraparte");
                    } else {//(homicidios)(suicidios)(muerte accidental)
                        sqlReturn = createCase(sqlReturn, currentIndicator.getInjuryType() + ".alcohol_level_victim_id", "alcohol_level_name", "alcohol_levels", "alcohol_level_id", "consumo_alcohol");
                    }
                    break;
                case places://SITIO EVENTO (homicidios)(muerte accidental)                                       
                    sqlReturn = createCase(sqlReturn, currentIndicator.getInjuryType() + ".injury_place_id", "place_name", "places", "place_id", "lugar_hecho");
                    break;
                case weapon_types://TIPO DE ARMA (homicidios)
                    addToSourceTable("fatal_injury_murder");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "weapon_type_name", "weapon_types", "weapon_type_id", "tipo_arma");
                    break;
                case non_fatal_data_sources:
                    if ((currentIndicator.getIndicatorId() >= 33 && currentIndicator.getIndicatorId() <= 39) || (currentIndicator.getIndicatorId() >= 68 && currentIndicator.getIndicatorId() <= 75)) {//se lista las receptoras                        
                        addToSourceTable("non_fatal_domestic_violence");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "non_fatal_data_source_name", "non_fatal_data_sources", "non_fatal_data_source_id", "institucion_salud");
                    } else {//se lista las de salud
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "non_fatal_data_source_name", "non_fatal_data_sources", "non_fatal_data_source_id", "institucion_salud");
                    }
                    break;
            }
            if (i == variablesCrossData.size() - 1) {//si es la ultima instruccion se agrega salto de linea
                sqlReturn = sqlReturn + " \n\r";
            } else {//si no es la ultima instruccion se agrega coma y salto de linea
                sqlReturn = sqlReturn + ", \n\r";
            }
        }
        sqlReturn = sqlReturn + ""
                + "   FROM  \n"
                + "       " + currentIndicator.getInjuryType() + "\n"
                + "       " + sourceTable
                + "       JOIN victims USING (victim_id) \n"
                + "   WHERE  \n\r"
                + "       " + filterSourceTable;
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
        //System.out.println("CONSULTA (indicators variation) \n " + sqlReturn);
        return sqlReturn;
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
        for (int i = 0; i < variablesListData.size(); i++) {
            if (variablesListData.get(i).getName().compareTo(firstVariablesCrossSelected) == 0) {
                variablesListData.get(i).setValuesConfigured(Arrays.asList(splitConfiguration));
                variablesListData.get(i).setValuesId(Arrays.asList(splitConfiguration));
                variablesListData.get(i).setValues(Arrays.asList(splitConfiguration));
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

    /**
     * This method allows to add a new range to the category hour for this we
     * must realize a series of validations such as the start hour should be
     * less than the end hour, must be within the range 0-23
     *
     * @return
     */
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
     * Este metodo se encarga de habilitar o deshabilitar los botones de agregar
     * y quitar variables segun la seleccion que haya hecho el usuario.
     */
    public void changeVariable() {
        btnAddVariableDisabled = true;
        btnRemoveVariableDisabled = true;
        if (currentVariablesSelected != null) {
            if (!currentVariablesSelected.isEmpty()) {
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
            btnRemoveCategoricalValueDisabled = true;
            btnRemoveVariableDisabled = false;
            firstVariablesCrossSelected = currentVariablesCrossSelected.get(0);
            //filtro los años segun la fecha

            currentCategoricalValuesSelected = new ArrayList<>();
            for (int i = 0; i < variablesListData.size(); i++) {
                if (variablesListData.get(i).getName().compareTo(firstVariablesCrossSelected) == 0) {
                    if (variablesListData.get(i).getGeneric_table().compareTo("year") == 0) {
                        //variablesListData.get(i).filterYear(initialDate, endDate);
                    }
                    currentCategoricalValuesList = variablesListData.get(i).getValuesConfigured();
                    currentVariableConfiguring = variablesListData.get(i);
                    btnAddCategoricalValueDisabled = !currentVariableConfiguring.isConfigurable();
                    break;
                }
            }
        }

    }

    /**
     * This method is responsible of enable or disable the buttons to add and
     * remove variables according to the selection realized by that user.
     */
    public void changeVariableCross() {
    }

    /**
     * This method is responsible for adding a new variable selected by the
     * user, this is done to make the cross of variables.
     */
    public void addVariableClick() {
        String error = "";
        boolean nextStep = true;
        if (currentVariablesSelected == null) {
            error = "Debe seleccionarse una variable";
            nextStep = false;
        }
        if (nextStep) {
            for (int i = 0; i < variablesList.size(); i++) {
                for (int j = 0; j < currentVariablesSelected.size(); j++) {
                    //System.out.println("comparar: " + variablesList.get(i)+ " CON "+currentVariablesSelected.get(j));
                    if (variablesList.get(i).compareTo(currentVariablesSelected.get(j)) == 0) {//esta es la variable encontrada que saldra de la lista                    
                        //System.out.println("quitar: " + variablesList.get(i));
                        variablesList.remove(i);//la quito de este listado                    
                        variablesCrossList.add(currentVariablesSelected.get(j));//la agrego al otro                        
                        btnAddVariableDisabled = true;
                        i = -1;
                        break;
                    }
                }
            }
            currentVariablesSelected = null;
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
            for (int i = 0; i < variablesCrossList.size(); i++) {
                for (int j = 0; j < currentVariablesCrossSelected.size(); j++) {
                    if (variablesCrossList.get(i).compareTo(currentVariablesCrossSelected.get(j)) == 0) {//esta es la variable encontrada que saldra de la lista                    
                        variablesCrossList.remove(i);//la quito de este listado                    
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
        ResultSet rs;
        ResultSet rs2;
        increment = 0;
        categoryAxixLabel = "";
        try {
            sql = ""
                    + " SELECT \n"
                    + "    * \n"
                    + " FROM \n"
                    + "    indicators_records \n"
                    + " WHERE \n"
                    + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "    indicator_id = " + currentIndicator.getIndicatorId() + "  \n";
//            if (currentVariableGraph1 != null && currentVariableGraph1.length() != 0) {
//                variablesName = "Desagregado por: " + currentVariableGraph1 + " = " + determineHeader(currentValueGraph1);
//                sql = sql + " AND column_2 LIKE '" + currentValueGraph1 + "' ";
//            }
//            if (currentVariableGraph2 != null && currentVariableGraph2.length() != 0) {
//                sql = sql + " AND column_3 LIKE '" + currentValueGraph2 + "' ";
//                variablesName = variablesName + ", " + currentVariableGraph2 + " = " + determineHeader(currentValueGraph2);
//            }
            if (currentVariableGraph1 != null && currentVariableGraph1.length() != 0) {
                variablesName = "Desagregado por: " + currentVariableGraph1 + " = " + determineHeader(currentValueGraph1);
                sql = sql + " AND column_3 LIKE '" + currentValueGraph1 + "' ";
            }
            rs = connectionJdbcMB.consult(sql + " ORDER BY record_id");
            sql = ""
                    + " SELECT \n"
                    + "    * \n"
                    + " FROM \n"
                    + "    indicators_records \n"
                    + " WHERE \n"
                    + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "    indicator_id = " + (currentIndicator.getIndicatorId() + 100) + "  \n";
            if (currentVariableGraph1 != null && currentVariableGraph1.length() != 0) {
                sql = sql + " AND column_3 LIKE '" + currentValueGraph1 + "' ";
            }
//            if (currentVariableGraph2 != null && currentVariableGraph2.length() != 0) {
//                sql = sql + " AND column_3 LIKE '" + currentValueGraph2 + "' ";
//            }
            rs2 = connectionJdbcMB.consult(sql + " ORDER BY record_id");
            String strDateName;
            int totalInt;
            while (rs.next()) {
                rs2.next();
                totalInt = (rs.getInt("count") - rs2.getInt("count")) * -1;
                if (increment < Math.sqrt(totalInt * totalInt)) {
                    increment = Math.sqrt(totalInt * totalInt);
                }
                strDateName = rs.getString("column_1") + " - " + rs2.getString("column_1");
                datSet.setValue(totalInt, determineHeader(rs.getString("column_2")), strDateName);
            }
            increment = increment * 0.005;//grosor linea
        } catch (SQLException ex) {
            increment = increment * 0.005;//grosor linea
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
     * This method is responsible for create an image with all the results
     * obtained from the crossing of variables.
     */
    public void createImage() {
        try {

            JFreeChart chart = null;
            dataset = createDataSet();
            //-----CREACION DEL TITULO
            indicatorName = currentIndicator.getIndicatorName() + " - Municipo de Pasto.\n";
            indicatorName = indicatorName + variablesName + "\n"
                    + "Rango A: (" + sdf.format(initialDateA) + " - " + sdf.format(endDateA) + ")\n"
                    + "Rango B: (" + sdf.format(initialDateB) + " - " + sdf.format(endDateB) + ")";


            //-----SELECCION DE TIPO DE GRAFICO
            if (currentTypeGraph.compareTo("barras") == 0) {
                chart = ChartFactory.createBarChart(indicatorName, categoryAxixLabel, "Variación", dataset, PlotOrientation.VERTICAL, true, true, false);
                CategoryPlot plot = (CategoryPlot) chart.getPlot();
                ((BarRenderer) plot.getRenderer()).setBarPainter(new StandardBarPainter());//quitar gradiente
            }
            if (currentTypeGraph.compareTo("barras apiladas") == 0) {
                chart = ChartFactory.createStackedBarChart(indicatorName, categoryAxixLabel, "Variación", dataset, PlotOrientation.VERTICAL, true, true, false);
                CategoryPlot plot = (CategoryPlot) chart.getPlot();
                ((BarRenderer) plot.getRenderer()).setBarPainter(new StandardBarPainter());//quitar gradiente
            }
            if (currentTypeGraph.compareTo("lineas") == 0) {
                chart = ChartFactory.createLineChart(indicatorName, categoryAxixLabel, "Variación", dataset, PlotOrientation.VERTICAL, true, true, false);
                CategoryPlot plot = (CategoryPlot) chart.getPlot();
                ((LineAndShapeRenderer) plot.getRenderer()).setShapesVisible(true);//mostrar vertices
                ((LineAndShapeRenderer) plot.getRenderer()).setShape(new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0));//tipo de vertices(circulo)                
                plot.getRenderer().setStroke(new BasicStroke(2));//grosor de linea

            }
            if (currentTypeGraph.compareTo("areas") == 0) {
                chart = ChartFactory.createAreaChart(indicatorName, categoryAxixLabel, "Variación", dataset, PlotOrientation.VERTICAL, true, true, false);
                ((CategoryPlot) chart.getPlot()).setForegroundAlpha(0.4f);//transparencia
            }
            //-------------------------------------------
//            //--REORDENAMIENTO DE LAS SERIES
            CategoryPlot plot = (CategoryPlot) chart.getPlot();
            if (variablesCrossData.size() < 2) {//sin leyenda cuando solo se desagrega por tiempo
                chart.getLegend().visible = false;
            } else {
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
            }
            //-----CONFIGURACIONES DEL GRAFICO
            IntervalMarker intervalmarker = new IntervalMarker(-1 * increment, increment, Color.black);
            plot.addRangeMarker(intervalmarker);//agregar linea de centro

            chart.getTitle().setFont(new Font("arial", Font.BOLD, 15));//fuente titulo
            plot.setBackgroundPaint(Color.white);//fondo blanco
            plot.setOutlineVisible(false);//grafico sin borde             
            ((CategoryAxis) plot.getDomainAxis()).setCategoryLabelPositions(CategoryLabelPositions.UP_45);//rotar etiquetas
            if (showItems) {//mostrar items
                CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("0"));
                plot.getRenderer().setItemLabelGenerator(generator);
                plot.getRenderer().setItemLabelFont(new Font("arial", Font.BOLD, sizeFont));
                plot.getRenderer().setItemLabelsVisible(true);
            }
            //----------GENERAR PNG A PARTIR DEL JCHART
            File chartFile = new File("grafico");
            ChartUtilities.saveChartAsPNG(chartFile, chart, widthGraph, heightGraph);
            chartImage = new DefaultStreamedContent(new FileInputStream(chartFile), "image/png");
        } catch (Exception e) {
            System.out.println("Error 6 en " + this.getClass().getName() + ":" + e.toString());
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
        currentVariableConfiguring = null;
        variablesCrossList = new ArrayList<>();
        currentVariablesSelected = new ArrayList<>();
        currentVariablesCrossSelected = new ArrayList<>();
        firstVariablesCrossSelected = null;
        currentCategoricalValuesList = new ArrayList<>();
        currentCategoricalValuesSelected = new ArrayList<>();
        titlePage = currentIndicator.getIndicatorGroup();
        titleIndicator = currentIndicator.getIndicatorGroup();
        subTitleIndicator = currentIndicator.getIndicatorName();
        variablesListData = getVariablesIndicator();
        currentVariableGraph1 = "";
        //currentVariableGraph2 = "";
        valuesGraph1 = new ArrayList<>();
        //valuesGraph2 = new ArrayList<>();
        currentValueGraph1 = "";
        //currentValueGraph2 = "";

        typesGraph = new ArrayList<>();

        typesGraph.add("barras");
        typesGraph.add("barras apiladas");
        typesGraph.add("areas");
        typesGraph.add("lineas");

        //numberCross = currentIndicator.getNumberCross();
        variablesList = new ArrayList<>();//SelectItem[variablesListData.size()];
        for (int i = 0; i < variablesListData.size(); i++) {
            variablesList.add(variablesListData.get(i).getName());
        }
        //dynamicDataTableGroup = new OutputPanel();//creo el panel grid
        dataTableHtml = "";

        dataTableHtmlDiference = "";
        chartImage = null;
        variablesCrossList = new ArrayList<>();//SelectItem[variablesListData.size()];
        btnAddVariableDisabled = true;
        btnRemoveVariableDisabled = true;
        currentVariablesSelected = null;
        currentVariablesCrossSelected = null;
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
        String iniDateString;
        String endDateString;
        SimpleDateFormat sdf_s;

        if (currentTemporalDisaggregation.compareTo("Diaria") == 0) {
            diferenceRank = getDateDifference(initialDate, endDate, "diaria");
            sdf_s = new SimpleDateFormat("dd MMM yyyy", new Locale("ES"));
            for (int i = 0; i < diferenceRank; i++) {
                cal1.setTime(initialDate);
                cal1.add(Calendar.DATE, i);
                iniDateString = formato.format(cal1.getTime());
                valuesName.add(sdf_s.format(cal1.getTime()));//agrego el dia en formato: 14 Junio 2013
                valuesId.add(iniDateString + "}" + iniDateString);
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
                iniDateString = formato.format(cal1.getTime());
                daysMax = cal1.getActualMaximum(Calendar.DAY_OF_MONTH); // numero maximo de dias del mes
                cal1.set(Calendar.DATE, daysMax);//coloco el dia en el maximo para el mes                
                endDateString = formato.format(cal1.getTime());
                valuesName.add(sdf_s.format(cal1.getTime()));//agrego el dia en formato: Junio 2013
                valuesId.add(iniDateString + "}" + endDateString);
                valuesConf.add(sdf_s.format(cal1.getTime()));
            }
        }
        if (currentTemporalDisaggregation.compareTo("Anual") == 0) {
            diferenceRank = getDateDifference(initialDate, endDate, "anual");
            sdf_s = new SimpleDateFormat("yyyy", new Locale("ES"));
            for (int i = 0; i < diferenceRank; i++) {
                cal1.setTime(initialDate);
                cal1.set(Calendar.DATE, 1);//coloco el dia en 1
                cal1.set(Calendar.MONTH, 0);//coloco el mes en enero(0)
                cal1.add(Calendar.YEAR, i);//fecha inicial se la aumenta i años                
                iniDateString = formato.format(cal1.getTime());
                cal1.set(Calendar.DATE, 31);//coloco el dia en 31
                cal1.set(Calendar.MONTH, 11);//coloco el mes en diciembre(11)
                endDateString = formato.format(cal1.getTime());
                valuesName.add(sdf_s.format(cal1.getTime()));//agrego el dia en formato: Junio 2013
                valuesId.add(iniDateString + "}" + endDateString);
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
            case non_fatal_data_sources:
            case neighborhoods://barrio,
            case communes://comuna,
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
        int posCol = 0;
        int posF;
        int posI;

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
        String value;
        //columna(s) inicial        
        if (variablesCrossData.size() == 2 || variablesCrossData.size() == 1) {
            for (int i = 0; i < columNamesFinal.size(); i++) {
                posCol = 0;
                fila = sheet.createRow(posRow + i);
                celda = fila.createCell(posCol);
                setValueCell(celda, determineHeader(columNamesFinal.get(i)));
                posCol++;
                for (int l = 0; l < rowNames.size(); l++) {
                    celda = fila.createCell(posCol);
                    if (!showCalculation) {
                        value = matrixResult[fila.getRowNum() - 1][l].split("<br/>")[0].replace("<b>", "").replace("</b>", "");
                        //celda.setCellValue(value);
                    } else {
                        value = matrixResult[fila.getRowNum() - 1][l].replace("<br/>", " ").replace("<b>", "").replace("</b>", "");
                        //celda.setCellValue(value);
                    }
                    setValueCell(celda, value);
                    posCol++;
                }
            }
        }
        if (variablesCrossData.size() == 3) {
            String currentVar = "";
            String[] splitVars;
            for (int i = 0; i < columNamesFinal.size(); i++) {
                splitVars = columNamesFinal.get(i).split("\\}");//separo las dos variables
                //String first = splitVars[0];//invierto el orden de llegada
                //splitVars[0] = splitVars[1];
                //splitVars[1] = first;
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
                            //celda.setCellValue(value);
                        } else {
                            value = matrixResult[fila.getRowNum() - 1][l].replace("<br/>", " ").replace("<b>", "").replace("</b>", "");
                            //celda.setCellValue(value);
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
        headers2 = new String[columNamesFinal.size()];
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
            for (int i = 0; i < columNamesFinal.size(); i++) {
                celda = fila.createCell((short) posI);// +2 por que faltal las filas               
                posI++;
                texto = new HSSFRichTextString(determineHeader(columNamesFinal.get(i)));// Se crea el contenido de la celda y se mete en ella.
                celda.setCellValue(texto);
            }
        }
        if (variablesCrossData.size() == 3) {
            //-------------------------------------------------------------------
            //CABECERA COMPUESTA            
            String currentVar = "";
            String[] splitVars;
            for (int i = 0; i < columNamesFinal.size(); i++) {
                splitVars = columNamesFinal.get(i).split("\\}");//separo las dos variables                
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
                //posF++;
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
        //-------------------------------------------------------------------              
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
     * variables in the matrix of vertical way
     *
     * @return
     */
    private String verticalResult() {

        headers1 = new ArrayList<>();
        headers2 = new String[columNamesFinal.size()];
        String height = "height:20px;";
        if (showCalculation) {
            height = "height:30px;";
        }

        String strReturn = " ";
        strReturn = strReturn + "    <table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\r\n";
        strReturn = strReturn + "            <tr>\r\n";
        strReturn = strReturn + "                <td>\r\n";
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

            for (int i = 0; i < columNamesFinal.size(); i++) {
                strReturn = strReturn + "                            <tr>\r\n";
                strReturn = strReturn + "                                <td>\r\n";
                strReturn = strReturn + "                                    <div style=\"overflow:hidden; " + height + " width:200px; white-space: nowrap;\">" + determineHeader(columNamesFinal.get(i)) + "</div>\r\n";
                strReturn = strReturn + "                                </td>\r\n";
                strReturn = strReturn + "                            </tr>\r\n";
            }
        }
        if (variablesCrossData.size() == 3) {//COLUMNA COMPUESTA            
            String currentVar = "";
            String[] splitVars;
            for (int i = 0; i < columNamesFinal.size(); i++) {
                splitVars = columNamesFinal.get(i).split("\\}");//separo las dos variables
//                String first = splitVars[0];//invierto el orden de llegada
//                splitVars[0] = splitVars[1];//
//                splitVars[1] = first;
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
        for (int j = 0; j < columNamesFinal.size(); j++) {//AGREGO LOS REGISTROS DE LA MATRIZ
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
                strReturn = strReturn + "                                </td > \r\n";
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

        if (columNamesFinal.isEmpty() && rowNames.isEmpty()) {
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
        headers2 = new String[columNamesFinal.size()];
        String height = "height:20px;";
        if (showCalculation) {
            height = "height:30px;";
        }

        String strReturn = " ";
        strReturn = strReturn + "<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\r\n";
        strReturn = strReturn + "            <tr>\r\n";
        strReturn = strReturn + "                <td>\r\n";
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
            for (int i = 0; i < columNamesFinal.size(); i++) {
                strReturn = strReturn + "                                <td>\r\n";
                strReturn = strReturn + "                                    <div style=\"overflow:hidden; height:20px; width:200px; white-space: nowrap;\">" + determineHeader(columNamesFinal.get(i)) + "</div>\r\n";
                strReturn = strReturn + "                                </td>\r\n";
            }
            strReturn = strReturn + "                            </tr>\r\n";
        }
        if (variablesCrossData.size() == 3) {  //CABECERA COMPUESTA            
            String currentVar = "";
            String[] splitVars;
            for (int i = 0; i < columNamesFinal.size(); i++) {
                splitVars = columNamesFinal.get(i).split("\\}");//separo las dos variables
//                String first = splitVars[0];//invierto el orden de llegada
//                splitVars[0] = splitVars[1];
//                splitVars[1] = first;
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
        for (int j = 0; j < rowNames.size(); j++) {
            //----------------------------------------------------------------------
            //NOMBRE PARA CADA FILA            
            strReturn = strReturn + "                            <tr>\r\n";
            strReturn = strReturn + "                                <td ><div style=\"overflow:hidden; " + height + " width:200px; white-space: nowrap;\">" + determineHeader(rowNames.get(j)) + "</div></td>\r\n";
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
                strReturn = strReturn + "                                </td > \r\n";
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

        if (columNamesFinal.isEmpty() && rowNames.isEmpty()) {
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
     * This method is responsible to create an array of results with the
     * percentage difference between two ranges of selected period
     */
    private void createMatrixDifference() {
        try {
            columNamesFinal = new ArrayList<>();
            int totalA;
            int totalB;
            ResultSet rs;
            ResultSet rs2;
            //---------------------------------------------------------            
            //SE CREA LA MATRIZ DE RESULTADOS (iniciada en 0 )
            //---------------------------------------------------------
            matrixResult = new String[columNames.size()][rowNames.size()];
            for (int i = 0; i < columNames.size(); i++) {
                columNamesFinal.add("");//para que tenga tamaño de columnNames
                for (int j = 0; j < rowNames.size(); j++) {
                    matrixResult[i][j] = "0";
                }
            }

            //rs = connectionJdbcMB.consult("SELECT * FROM " + pivotTableName + " ORDER BY id ASC");
            //rs2 = connectionJdbcMB.consult("SELECT * FROM " + prepivotTableName + " ORDER BY id ASC");
            sql = ""
                    + " SELECT \n"
                    + "    * \n"
                    + " FROM \n"
                    + "    indicators_records \n"
                    + " WHERE \n"
                    + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "    indicator_id = " + (currentIndicator.getIndicatorId() + 0) + "  \n"
                    + " ORDER BY \n"
                    + "    record_id \n";
            rs = connectionJdbcMB.consult(sql);
            sql = ""
                    + " SELECT \n"
                    + "    * \n"
                    + " FROM \n"
                    + "    indicators_records \n"
                    + " WHERE \n"
                    + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "    indicator_id = " + (currentIndicator.getIndicatorId() + 100) + "  \n"
                    + " ORDER BY \n"
                    + "    record_id \n";
            rs2 = connectionJdbcMB.consult(sql);



            boolean find;
            String lastCalculate = "";
            while (rs.next()) {
                if (rs2.next()) {//hay resultado en rangoA y rangoB para realizar diferencia
                    find = false;
                    lastCalculate = rs.getString("column_1") + " - " + rs2.getString("column_1");//ultimo calculo realizado
                    for (int i = 0; i < columNames.size(); i++) {
                        for (int j = 0; j < rowNames.size(); j++) {
                            if (variablesCrossData.size() == 1) {//ES UNA VARIABLE                            
                                if (rs2.getString("column_1").compareTo(columNames.get(i)) == 0) {
                                    totalA = Integer.parseInt(rs.getString("count"));
                                    totalB = Integer.parseInt(rs2.getString("count"));
                                    columNamesFinal.set(i, rs.getString("column_1") + " - " + rs2.getString("column_1"));
                                    matrixResult[i][j] = "<b>" + String.valueOf((totalA - totalB) * -1) + "</b><br/>(" + totalA + "-" + totalB + ")";
                                    find = true;
                                }
                            }
                            if (variablesCrossData.size() == 2) {//SON DOS VARIABLES                            
                                if (rs2.getString("column_1").compareTo(columNames.get(i)) == 0 && rs2.getString("column_2").compareTo(rowNames.get(j)) == 0) {
                                    totalA = Integer.parseInt(rs.getString("count"));
                                    totalB = Integer.parseInt(rs2.getString("count"));
                                    columNamesFinal.set(i, rs.getString("column_1") + " - " + rs2.getString("column_1"));
                                    matrixResult[i][j] = "<b>" + String.valueOf((totalA - totalB) * -1) + "</b><br/>(" + totalA + "-" + totalB + ")";
                                    find = true;
                                }
                            }
                            if (variablesCrossData.size() == 3) {//SON TRES VARIABLES                            
                                if (columNames.get(i).compareTo(rs2.getString("column_1") + "}" + rs2.getString("column_2")) == 0 && rs2.getString("column_3").compareTo(rowNames.get(j)) == 0) {
                                    totalA = Integer.parseInt(rs.getString("count"));
                                    totalB = Integer.parseInt(rs2.getString("count"));
                                    columNamesFinal.set(i, rs.getString("column_1") + " - " + rs2.getString("column_1") + "}" + rs.getString("column_2"));
                                    matrixResult[i][j] = "<b>" + String.valueOf((totalA - totalB) * -1) + "</b><br/>(" + totalA + "-" + totalB + ")";
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
                } else {//hay mas resultados en en rangoA 
                    diferentTemporalWarning = "La comparacion solo se realizo hasta: " + lastCalculate + " por que el periodo de tiempo en el rangoA es mas largo";
                    break;
                }
            }
            if (rs2.next()) {//hay mas resultados en rangoB 
                diferentTemporalWarning = "La comparacion solo se realizo hasta: " + lastCalculate + " por que el periodo de tiempo en el rangoB es mas largo";

            }
        } catch (SQLException | NumberFormatException e) {
            System.out.println("Error 8 en " + this.getClass().getName() + ":" + e.toString());
        }
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
    //FUNCIOES PARA REALIZAR LA CARGA DE UN INDICADOR
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    public void loadIndicator6() {
        loadIndicator(6);
    }

    public void loadIndicator13() {
        loadIndicator(13);
    }

    public void loadIndicator20() {
        loadIndicator(20);
    }

    public void loadIndicator27() {
        loadIndicator(27);
    }

    public void loadIndicator34() {
        loadIndicator(34);
    }

    public void loadIndicator41() {
        loadIndicator(41);
    }

    public void loadIndicator48() {
        loadIndicator(48);
    }

    public void loadIndicator55() {
        loadIndicator(55);
    }

    public void loadIndicator62() {
        loadIndicator(62);
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

    public Date getInitialDateA() {
        return initialDateA;
    }

    public void setInitialDateA(Date initialDateA) {
        this.initialDateA = initialDateA;
    }

    public Date getEndDateA() {
        return endDateA;
    }

    public void setEndDateA(Date endDateA) {
        this.endDateA = endDateA;
    }

    public Date getEndDateB() {
        return endDateB;
    }

    public void setEndDateB(Date endDateB) {
        this.endDateB = endDateB;
    }

    public Date getInitialDateB() {
        return initialDateB;
    }

    public void setInitialDateB(Date initialDateB) {
        this.initialDateB = initialDateB;
    }

    public List<String> getVariablesCrossList() {
        return variablesCrossList;
    }

    public void setVariablesCrossList(List<String> variablesCrossList) {
        this.variablesCrossList = variablesCrossList;
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

    public List<String> getCurrentVariablesSelected() {
        return currentVariablesSelected;
    }

    public void setCurrentVariablesSelected(List<String> currentVariablesSelected) {
        this.currentVariablesSelected = currentVariablesSelected;
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

    public boolean isRenderedDynamicDataTable() {
        return renderedDynamicDataTable;
    }

    public void setRenderedDynamicDataTable(boolean renderedDynamicDataTable) {
        this.renderedDynamicDataTable = renderedDynamicDataTable;
    }

    public String getDataTableHtml() {
        return dataTableHtml;
    }

    public void setDataTableHtml(String dataTableHtml) {
        this.dataTableHtml = dataTableHtml;
    }

    public String getDataTableHtmlDiference() {
        return dataTableHtmlDiference;
    }

    public void setDataTableHtmlDiference(String dataTableHtmlDiference) {
        this.dataTableHtmlDiference = dataTableHtmlDiference;
    }

    public boolean isShowCalculation() {
        return showCalculation;
    }

    public void setShowCalculation(boolean showCalculation) {
        this.showCalculation = showCalculation;
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

    public String getCurrentVariableGraph1() {
        return currentVariableGraph1;
    }

    public void setCurrentVariableGraph1(String currentVariableGraph1) {
        this.currentVariableGraph1 = currentVariableGraph1;
    }
    
    public String getCurrentValueGraph1() {
        return currentValueGraph1;
    }

    public void setCurrentValueGraph1(String currentValueGraph1) {
        this.currentValueGraph1 = currentValueGraph1;
    }
    
    public List<String> getValuesGraph1() {
        return valuesGraph1;
    }

    public void setValuesGraph1(List<String> valuesGraph1) {
        this.valuesGraph1 = valuesGraph1;
    }
    
    public boolean isShowItems() {
        return showItems;
    }

    public void setShowItems(boolean showItems) {
        this.showItems = showItems;
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

    public List<String> getTypesGraph() {
        return typesGraph;
    }

    public void setTypesGraph(List<String> typesGraph) {
        this.typesGraph = typesGraph;
    }

    public String getCurrentTypeGraph() {
        return currentTypeGraph;
    }

    public void setCurrentTypeGraph(String currentTypeGraph) {
        this.currentTypeGraph = currentTypeGraph;
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
