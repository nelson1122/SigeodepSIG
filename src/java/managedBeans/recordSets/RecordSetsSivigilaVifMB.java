/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.recordSets;

import beans.connection.ConnectionJdbcMB;
import beans.enumerators.FormsEnum;
import beans.util.RowDataTable;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import model.dao.*;
import model.pojo.NonFatalDomesticViolence;
import model.pojo.NonFatalInjuries;
import model.pojo.SivigilaAggresor;
import model.pojo.SivigilaEvent;
import model.pojo.SivigilaVictim;
import model.pojo.Tags;
import model.pojo.Victims;
import org.apache.poi.hssf.usermodel.*;
import org.primefaces.model.LazyDataModel;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "recordSetsSivigilaVifMB")
@SessionScoped
/**
 * This class handles record set that correspond to SIVIGILA VIF
 */
public class RecordSetsSivigilaVifMB implements Serializable {
    //--------------------

    @EJB
    TagsFacade tagsFacade;
    @EJB
    VictimsFacade victimsFacade;
    @EJB
    NonFatalDomesticViolenceFacade nonFatalDomesticViolenceFacade;
    @EJB
    NonFatalInjuriesFacade nonFatalInjuriesFacade;
    @EJB
    SivigilaEventFacade sivigilaEventFacade;
    @EJB
    SivigilaVictimFacade sivigilaVictimFacade;
    @EJB
    SivigilaAggresorFacade sivigilaAggresorFacade;
    private List<Tags> tagsList;
    private RowDataTable[] selectedRowsDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    private String name = "";
    private String newName = "";
    private String data = "-";
    private String exportFileName = "";
    private LazyDataModel<RowDataTable> table_model;
    private ArrayList<RowDataTable> rowsDataTableArrayList;
    private ConnectionJdbcMB connection;
    private String totalRecords = "0";
    private String initialDateStr = "";
    private String endDateStr = "";
    private int tuplesNumber;
    private Integer tuplesProcessed;
    private int progress = 0;//PROGRESO AL CREAR XLS
    private String sql = "";

    /**
     * This method Instance tag list, the table model and gets the current
     * instance of the connection to the database.
     */
    public RecordSetsSivigilaVifMB() {
        tagsList = new ArrayList<>();
        table_model = new LazyRecordSetsDataModel(0, "", FormsEnum.SCC_F_033);
        connection = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
    }

    /**
     * This method is used to display messages about the actions that the user
     * is performing.
     *
     * @param s
     * @param title
     * @param messageStr
     */
    public void printMessage(FacesMessage.Severity s, String title, String messageStr) {
        FacesMessage msg = new FacesMessage(s, title, messageStr);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    /**
     * load the information corresponding to a victim within the form
     *
     * @param selectedRowsDataTableTags
     */
    void loadValues(RowDataTable[] selectedRowsDataTableTags) {
        try {
            //CREO LA LISTA DE TAGS SELECCIONADOS        
            exportFileName = "SIVIGILA VIF - " + initialDateStr + " - " + endDateStr;
            tagsList = new ArrayList<>();
            data = "";
            for (int i = 0; i < selectedRowsDataTableTags.length; i++) {
                if (i == 0) {
                    data = data + " " + selectedRowsDataTableTags[i].getColumn2() + "  ";
                } else {
                    data = data + " || " + selectedRowsDataTableTags[i].getColumn2();
                }
                tagsList.add(tagsFacade.find(Integer.parseInt(selectedRowsDataTableTags[i].getColumn1())));
            }
            //DETERMINO TOTAL DE REGISTROS
            sql = "\n";
            sql = sql + " SELECT \n";
            sql = sql + " count(*) \n";
            sql = sql + " FROM \n";
            sql = sql + " public.victims, \n";
            sql = sql + " public.non_fatal_injuries \n";
            sql = sql + " WHERE \n";
            sql = sql + " non_fatal_injuries.victim_id = victims.victim_id AND ( \n";
            for (int i = 0; i < tagsList.size(); i++) {
                if (i == tagsList.size() - 1) {
                    sql = sql + " victims.tag_id = " + String.valueOf(tagsList.get(i).getTagId()) + " \n";
                } else {
                    sql = sql + " victims.tag_id = " + String.valueOf(tagsList.get(i).getTagId()) + " OR \n";
                }
            }
            sql = sql + ") AND non_fatal_injuries.injury_date >= to_date('" + initialDateStr + "','dd/MM/yyyy') AND \n";
            sql = sql + " non_fatal_injuries.injury_date <= to_date('" + endDateStr + "','dd/MM/yyyy') \n";
            ResultSet resultSet = connection.consult(sql);
            totalRecords = "0";
            if (resultSet.next()) {
                totalRecords = String.valueOf(resultSet.getInt(1));
            }
            //System.out.println("Total de registros = " + totalRecords);
            //DETERMINO EL ID DE CADA REGISTRO            
            sql = "\n";
            sql = sql + " SELECT \n";
            sql = sql + " non_fatal_injuries.victim_id \n";
            sql = sql + " FROM \n";
            sql = sql + " public.victims, \n";
            sql = sql + " public.non_fatal_injuries \n";
            sql = sql + " WHERE \n";
            sql = sql + " non_fatal_injuries.victim_id = victims.victim_id AND ( \n";
            for (int i = 0; i < tagsList.size(); i++) {
                if (i == tagsList.size() - 1) {
                    sql = sql + " victims.tag_id = " + String.valueOf(tagsList.get(i).getTagId()) + " \n";
                } else {
                    sql = sql + " victims.tag_id = " + String.valueOf(tagsList.get(i).getTagId()) + " OR \n";
                }
            }
            sql = sql + ") AND non_fatal_injuries.injury_date >= to_date('" + initialDateStr + "','dd/MM/yyyy') AND \n";
            sql = sql + " non_fatal_injuries.injury_date <= to_date('" + endDateStr + "','dd/MM/yyyy') \n";

            //CONSTRUYO EL TABLE_MODEL
            table_model = new LazyRecordSetsDataModel(Integer.parseInt(totalRecords), sql, FormsEnum.SIVIGILA_VIF);

        } catch (SQLException ex) {
            Logger.getLogger(RecordSetsLcenfMB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is used when the user wants to export all records found. this
     * method creates a cell acording to the specified parameters
     *
     * @param cellStyle
     * @param fila
     * @param position
     * @param value
     */
    private void createCell(HSSFCellStyle cellStyle, HSSFRow fila, int position, String value) {
        HSSFCell cell;
        cell = fila.createCell((short) position);// Se crea una cell dentro de la fila                        
        cell.setCellValue(new HSSFRichTextString(value));
        cell.setCellStyle(cellStyle);
    }

    /**
     * This method is used when the user wants to export all records found. this
     * method creates a cell acording to the specified parameters
     *
     * @param fila
     * @param position
     * @param value
     */
    private void createCell(HSSFRow fila, int position, String value) {
        HSSFCell cell;
        cell = fila.createCell((short) position);// Se crea una cell dentro de la fila                        
        cell.setCellValue(new HSSFRichTextString(value));
    }

    /**
     * This method is responsible to export all records found.
     */
    public void postProcessXLS1() {
        try {
            progress = 0;
            tuplesNumber = Integer.parseInt(totalRecords);
            tuplesProcessed = 0;
            rowsDataTableArrayList = new ArrayList<>();
            ResultSet resultSet = connection.consult(sql);
            while (resultSet.next()) {
                rowsDataTableArrayList.add(connection.loadSivigilaVifRecord(resultSet.getString(1)));
                tuplesProcessed++;
                progress = (int) (tuplesProcessed * 100) / tuplesNumber;
                //System.out.println(progress);
            }
            progress = 100;
        } catch (SQLException ex) {
            Logger.getLogger(RecordSetsHomicideMB.class.getName()).log(Level.SEVERE, null, ex);
        }
        progress = 100;
    }

    /**
     * This method is responsible for exporting all records found
     */
    public void postProcessXLS(Object document) {
        try {
            progress = 0;
            tuplesNumber = Integer.parseInt(totalRecords);
            tuplesProcessed = 0;

            int rowPosition = 0;
            int colPosition = 0;
            HSSFWorkbook book = (HSSFWorkbook) document;
            HSSFSheet sheet = book.getSheetAt(0);// Se toma hoja del libro
            HSSFRow row;
            HSSFCellStyle cellStyle = book.createCellStyle();
            HSSFFont font = book.createFont();
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            cellStyle.setFont(font);

            row = sheet.createRow(rowPosition);// Se crea una fila dentro de la hoja

            createCell(cellStyle, row, colPosition++, "CODIGO INTERNO");
            createCell(cellStyle, row, colPosition++, "INSTITUCION RECEPTORA");
            createCell(cellStyle, row, colPosition++, "NOMBRES Y APELLIDOS");
            createCell(cellStyle, row, colPosition++, "TIPO IDENTIFICACION");
            createCell(cellStyle, row, colPosition++, "IDENTIFICACION");
            createCell(cellStyle, row, colPosition++, "TIPO EDAD");
            createCell(cellStyle, row, colPosition++, "EDAD CANTIDAD");
            createCell(cellStyle, row, colPosition++, "GENERO");
            createCell(cellStyle, row, colPosition++, "OCUPACION");
            createCell(cellStyle, row, colPosition++, "DIRECCION RESIDENCIA");
            createCell(cellStyle, row, colPosition++, "ASEGURADORA");
            createCell(cellStyle, row, colPosition++, "PERTENENCIA ETNICA");
            createCell(cellStyle, row, colPosition++, "GRUPO POBLACIONAL");
            createCell(cellStyle, row, colPosition++, "MUNICIPIO RESIDENCIA");
            createCell(cellStyle, row, colPosition++, "DEPARTAMENTO RESIDENCIA");
            createCell(cellStyle, row, colPosition++, "TELEFONO");
            createCell(cellStyle, row, colPosition++, "FECHA NACIMIENTO");
            createCell(cellStyle, row, colPosition++, "ESCOLARIDAD VICTIMA");
            createCell(cellStyle, row, colPosition++, "FACTOR DE VULNERABILIDAD");
            createCell(cellStyle, row, colPosition++, "OTRO FACTOR VULNERABILIDAD");
            createCell(cellStyle, row, colPosition++, "ANTECEDENTES HECHO SIMILAR");
            createCell(cellStyle, row, colPosition++, "PRESENCIA ALCOHOL VICTIMA");
            createCell(cellStyle, row, colPosition++, "TIPO DE REGIMEN");
            //------------------------------------------------------------
            //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA NO FATAL
            //------------------------------------------------------------      
            createCell(cellStyle, row, colPosition++, "BARRIO EVENTO");
            createCell(cellStyle, row, colPosition++, "CUADRANTE EVENTO");
            createCell(cellStyle, row, colPosition++, "COMUNA BARRIO EVENTO");
            createCell(cellStyle, row, colPosition++, "DIRECCION EVENTO");
            createCell(cellStyle, row, colPosition++, "AREA");
            createCell(cellStyle, row, colPosition++, "ZONA DE CONFLICTO");
            createCell(cellStyle, row, colPosition++, "FECHA EVENTO");
            createCell(cellStyle, row, colPosition++, "HORA EVENTO");
            createCell(cellStyle, row, colPosition++, "FECHA CONSULTA");
            createCell(cellStyle, row, colPosition++, "HORA CONSULTA");
            createCell(cellStyle, row, colPosition++, "ESCENARIO");
            //------------------------------------------------------------
            //DATOS AGRESOR
            //------------------------------------------------------------   
            createCell(cellStyle, row, colPosition++, "EDAD AGRESOR");
            createCell(cellStyle, row, colPosition++, "GENERO AGRESOR");
            createCell(cellStyle, row, colPosition++, "OCUPACION AGRESOR");
            createCell(cellStyle, row, colPosition++, "ESCOLARIDAD AGRESOR");
            createCell(cellStyle, row, colPosition++, "RELACION FAMILIAR VICTIMA");
            createCell(cellStyle, row, colPosition++, "OTRA RELACION FAMILIAR");
            createCell(cellStyle, row, colPosition++, "CONVIVE CON AGRESOR");
            createCell(cellStyle, row, colPosition++, "RELACION NO FAMILIAR VICTIMA");
            createCell(cellStyle, row, colPosition++, "OTRA RELACION NO FAMILIAR");
            createCell(cellStyle, row, colPosition++, "GRUPO AGRESOR");
            createCell(cellStyle, row, colPosition++, "OTRO GRUPO AGRESOR");
            createCell(cellStyle, row, colPosition++, "PRESENCIA ALCOHOL AGRESOR");
            createCell(cellStyle, row, colPosition++, "ARMAS UTILIZADAS");
            createCell(cellStyle, row, colPosition++, "SUSTANCIA INTOXICACION");
            createCell(cellStyle, row, colPosition++, "OTRA ARMA");
            createCell(cellStyle, row, colPosition++, "OTRO MECANISMO");
            createCell(cellStyle, row, colPosition++, "NATURALEZA VIOLENCIA");
            createCell(cellStyle, row, colPosition++, "ASP1 ATENCION PSICOSOCIAL");
            createCell(cellStyle, row, colPosition++, "ASP2 PROFILAXIS ITS");
            createCell(cellStyle, row, colPosition++, "ASP3 ANTICONCEPCION DE EMERGENCIA");
            createCell(cellStyle, row, colPosition++, "ASP4 ORIENTACION IVE");
            createCell(cellStyle, row, colPosition++, "ASP5 ATENCION EN SALUD MENTAL ESPECIALIZADA");
            createCell(cellStyle, row, colPosition++, "ASP6 INFORME A LA AUTORIDAD");
            createCell(cellStyle, row, colPosition++, "ASP7 OTRO");
            createCell(cellStyle, row, colPosition++, "RECOMINEDA PROTECCION");
            createCell(cellStyle, row, colPosition++, "TRABAJO DE CAMPO");
            createCell(cellStyle, row, colPosition++, "PROFESIONAL SALUD");


            String[] splitDate;
            for (int i = 0; i < rowsDataTableArrayList.size(); i++) {
                colPosition = 0;
                RowDataTable rowDataTableList = rowsDataTableArrayList.get(i);
                rowPosition++;
                row = sheet.createRow(rowPosition);

                createCell(row, colPosition++, rowDataTableList.getColumn1());//CODIGO INTERNO" width="50">#{rowX.column1}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn42());//INSTITUCION DE SALUD" width="200">#{rowX.column42}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn2());//NOMBRES Y APELLIDOS" width="400">#{rowX.column2}</p:column>                                                
                createCell(row, colPosition++, rowDataTableList.getColumn3());//TIPO IDENTIFICACION" width="200">#{rowX.column3}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn4());//IDENTIFICACION" width="100">#{rowX.column4}</p:column>                
                createCell(row, colPosition++, rowDataTableList.getColumn5());//TIPO EDAD" width="100">#{rowX.column5}</p:column>                
                createCell(row, colPosition++, rowDataTableList.getColumn6());//EDAD CANTIDAD" width="100">#{rowX.column6}</p:column>                
                createCell(row, colPosition++, rowDataTableList.getColumn7());//GENERO" width="100">#{rowX.column7}</p:column>                
                createCell(row, colPosition++, rowDataTableList.getColumn8());//OCUPACION" width="300">#{rowX.column8}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn9());//DIRECCION RESIDENCIA" width="400">#{rowX.column9}</p:column>                                
                createCell(row, colPosition++, rowDataTableList.getColumn10());//ASEGURADORA" width="300">#{rowX.column10}</p:column>                                
                createCell(row, colPosition++, rowDataTableList.getColumn11());//PERTENENCIA ETNICA" width="100">#{rowX.column11}</p:column>  
                createCell(row, colPosition++, rowDataTableList.getColumn12());//GRUPO POBLACIONAL" width="400">#{rowX.column12}</p:column>                                
                createCell(row, colPosition++, rowDataTableList.getColumn13());//MUNICIPIO RESIDENCIA" width="100">#{rowX.column13}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn14());//DEPARTAMENTO RESIDENCIA" width="100">#{rowX.column14}</p:column>                                
                createCell(row, colPosition++, rowDataTableList.getColumn15());//TELEFONO" width="100">#{rowX.column15}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn16());//FECHA NACIMIENTO" width="100">#{rowX.column16}</p:column>                                         
                createCell(row, colPosition++, rowDataTableList.getColumn18());//ESCOLARIDAD VICTIMA" width="400">#{rowX.column18}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn19());//FACTOR DE VULNERABILIDAD" width="400">#{rowX.column19}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn20());//OTRO FACTOR VULNERABILIDAD" width="400">#{rowX.column20}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn21());//ANTECEDENTES HECHO SIMILAR" width="400">#{rowX.column21}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn41());//PRESENCIA ALCOHOL VICTIMA" width="400">#{rowX.column41}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn17());//TIPO DE REGIMEN" width="400">#{rowX.column17}</p:column>
                //------------------------------------------------------------
                //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA NO FATAL
                //------------------------------------------------------------        
                createCell(row, colPosition++, rowDataTableList.getColumn22());//BARRIO EVENTO" width="250">#{rowX.column22}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn71());//CUADRANTE EVENTO
                createCell(row, colPosition++, rowDataTableList.getColumn23());//COMUNA BARRIO EVENTO" width="100">#{rowX.column23}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn40());//DIRECCION EVENTO" width="400">#{rowX.column40}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn43());//AREA" width="400">#{rowX.column43}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn60());//ZONA DE CONFLICTO" width="400">#{rowX.column60}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn26());//FECHA EVENTO" width="100">#{rowX.column26}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn27());//HORA EVENTO" width="100">#{rowX.column27}</p:column>                                
                createCell(row, colPosition++, rowDataTableList.getColumn24());//FECHA CONSULTA" width="100">#{rowX.column24}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn25());//HORA CONSULTA" width="100">#{rowX.column25}</p:column>                                
                createCell(row, colPosition++, rowDataTableList.getColumn39());//ESCENARIO" width="200">#{rowX.column39}</p:column>                                                                 
                //------------------------------------------------------------
                //DATOS AGRESOR
                //------------------------------------------------------------        
                createCell(row, colPosition++, rowDataTableList.getColumn44());//EDAD AGRESOR" width="400">#{rowX.column44}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn45());//GENERO AGRESOR" width="400">#{rowX.column45}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn46());//OCUPACION AGRESOR" width="400">#{rowX.column46}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn47());//ESCOLARIDAD AGRESOR" width="400">#{rowX.column47}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn48());//RELACION FAMILIAR VICTIMA" width="400">#{rowX.column48}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn49());//OTRA RELACION FAMILIAR" width="400">#{rowX.column49}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn50());//CONVIVE CON AGRESOR" width="400">#{rowX.column50}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn51());//RELACION NO FAMILIAR VICTIMA" width="400">#{rowX.column51}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn52());//OTRA RELACION NO FAMILIAR" width="400">#{rowX.column52}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn53());//GRUPO AGRESOR" width="400">#{rowX.column53}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn54());//OTRO GRUPO AGRESOR" width="400">#{rowX.column54}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn55());//PRESENCIA ALCOHOL AGRESOR" width="400">#{rowX.column55}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn56());//ARMAS UTILIZADAS" width="400">#{rowX.column56}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn57());//SUSTANCIA INTOXICACION" width="400">#{rowX.column57}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn58());//OTRA ARMA" width="400">#{rowX.column58}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn59());//OTRO MECANISMO" width="400">#{rowX.column59}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn29());//NATURALEZA VIOLENCIA" width="100">#{rowX.column29}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn62());//ASP1 ATENCION PSICOSOCIAL" width="100">#{rowX.column62}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn63());//ASP2 PROFILAXIS ITS" width="100">#{rowX.column63}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn64());//ASP3 ANTICONCEPCION DE EMERGENCIA" width="100">#{rowX.column64}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn65());//ASP4 ORIENTACION IVE" width="100">#{rowX.column65}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn66());//ASP5 ATENCION EN SALUD MENTAL ESPECIALIZADA" width="100">#{rowX.column66}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn67());//ASP6 INFORME A LA AUTORIDAD" width="100">#{rowX.column67}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn68());//ASP7 OTRO" width="100">#{rowX.column68}</p:column>                                                
                createCell(row, colPosition++, rowDataTableList.getColumn69());//RECOMINEDA PROTECCION" width="100">#{rowX.column69}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn70());//TRABAJO DE CAMPO" width="100">#{rowX.column70}</p:column>                                
                createCell(row, colPosition++, rowDataTableList.getColumn28());//PROFESIONAL SALUD" width="100">#{rowX.column28}</p:column>                                
            }
        } catch (Exception ex) {
            Logger.getLogger(RecordSetsHomicideMB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is used to remove a registry of the database.
     */
    public void deleteRegistry() {
        if (selectedRowsDataTable != null && selectedRowsDataTable.length != 0) {
            for (int j = 0; j < selectedRowsDataTable.length; j++) {
                NonFatalInjuries auxNonFatalInjury = nonFatalInjuriesFacade.find(Integer.parseInt(selectedRowsDataTable[j].getColumn1()));
                Victims auxVictim = auxNonFatalInjury.getVictimId();
                NonFatalDomesticViolence auxDomesticViolence = auxNonFatalInjury.getNonFatalDomesticViolence();
                SivigilaEvent auxSivigilaEvent = auxDomesticViolence.getSivigilaEvent();
                SivigilaVictim auxSivigilaVictim = auxSivigilaEvent.getSivigilaVictimId();
                SivigilaAggresor auxSivigilaAggresor = auxSivigilaEvent.getSivigilaAgresorId();
                sivigilaEventFacade.remove(auxSivigilaEvent);
                nonFatalDomesticViolenceFacade.remove(auxDomesticViolence);
                nonFatalInjuriesFacade.remove(auxNonFatalInjury);
                victimsFacade.remove(auxVictim);
                sivigilaVictimFacade.remove(auxSivigilaVictim);
                sivigilaAggresorFacade.remove(auxSivigilaAggresor);
            }//deselecciono los controles
            selectedRowsDataTable = null;
            totalRecords = String.valueOf(Integer.parseInt(totalRecords) - 1);
            printMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha realizado la eliminacion de los registros seleccionados");
        } else {
            printMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar un o varios registros a eliminar");
        }
    }
    
    public RowDataTable[] getSelectedRowsDataTable() {
        return selectedRowsDataTable;
    }

    public void setSelectedRowsDataTable(RowDataTable[] selectedRowsDataTable) {
        this.selectedRowsDataTable = selectedRowsDataTable;
    }

    public int getCurrentSearchCriteria() {
        return currentSearchCriteria;
    }

    public void setCurrentSearchCriteria(int currentSearchCriteria) {
        this.currentSearchCriteria = currentSearchCriteria;
    }

    public String getCurrentSearchValue() {
        return currentSearchValue;
    }

    public void setCurrentSearchValue(String currentSearchValue) {
        this.currentSearchValue = currentSearchValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }
    
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public LazyDataModel<RowDataTable> getTable_model() {
        return table_model;
    }

    public void setTable_model(LazyDataModel<RowDataTable> table_model) {
        this.table_model = table_model;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(String totalRecords) {
        this.totalRecords = totalRecords;
    }

    public String getEndDateStr() {
        return endDateStr;
    }

    public void setEndDateStr(String endDateStr) {
        this.endDateStr = endDateStr;
    }

    public String getInitialDateStr() {
        return initialDateStr;
    }

    public void setInitialDateStr(String initialDateStr) {
        this.initialDateStr = initialDateStr;
    }

    public String getExportFileName() {
        return exportFileName;
    }

    public void setExportFileName(String exportFileName) {
        this.exportFileName = exportFileName;
    }
}
