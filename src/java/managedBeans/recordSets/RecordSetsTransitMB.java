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
import managedBeans.forms.TransitMB;
import model.dao.*;
import model.pojo.FatalInjuries;
import model.pojo.FatalInjuryTraffic;
import model.pojo.Tags;
import model.pojo.Victims;
import org.apache.poi.hssf.usermodel.*;
import org.primefaces.model.LazyDataModel;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "recordSetsTransitMB")
@SessionScoped
/**
 * This class handles record set that correspond to Transit accident deaths
 */
public class RecordSetsTransitMB implements Serializable {

    @EJB
    TagsFacade tagsFacade;
    @EJB
    VictimsFacade victimsFacade;
    @EJB
    FatalInjuryTrafficFacade fatalInjuryTrafficFacade;
    @EJB
    FatalInjuriesFacade fatalInjuriesFacade;
    private List<Tags> tagsList;
    private Tags currentTag;
    private FatalInjuryTraffic currentFatalInjuryTraffic;
    private RowDataTable[] selectedRowsDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    private String name = "";
    private String newName = "";
    private boolean btnEditDisabled = true;
    private String data = "-";
    private TransitMB transitMB;
    private String openForm = "";
    private LazyDataModel<RowDataTable> table_model;
    private ArrayList<RowDataTable> rowsDataTableArrayList;
    private ConnectionJdbcMB connection;
    private String exportFileName = "";
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
    public RecordSetsTransitMB() {
        tagsList = new ArrayList<Tags>();
        table_model = new LazyRecordSetsDataModel(0, "", FormsEnum.SCC_F_029);
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
     * This method is responsible to display the corresponding form of a
     * selected victim.
     *
     * @return
     */
    public String openForm() {
        return openForm;
    }

    /**
     * Open the corresponding form to a selected record.
     */
    public void openInForm() {
        FacesContext context = FacesContext.getCurrentInstance();
        transitMB = (TransitMB) context.getApplication().evaluateExpressionGet(context, "#{transitMB}", TransitMB.class);
        transitMB.loadValues(tagsList, currentFatalInjuryTraffic);
        openForm = "transit";
    }

    /**
     * load the information corresponding to a victim within the form
     *
     * @param selectedRowsDataTableTags
     */
    void loadValues(RowDataTable[] selectedRowsDataTableTags) {
        try {
            //CREO LA LISTA DE TAGS SELECCIONADOS   
            exportFileName = "TRANSITO - " + initialDateStr + " - " + endDateStr;
            tagsList = new ArrayList<Tags>();
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
            sql = sql + " public.fatal_injuries \n";
            sql = sql + " WHERE \n";
            sql = sql + " fatal_injuries.victim_id = victims.victim_id AND ( \n";
            for (int i = 0; i < tagsList.size(); i++) {
                if (i == tagsList.size() - 1) {
                    sql = sql + " victims.tag_id = " + String.valueOf(tagsList.get(i).getTagId()) + " \n";
                } else {
                    sql = sql + " victims.tag_id = " + String.valueOf(tagsList.get(i).getTagId()) + " OR \n";
                }
            }
            sql = sql + " ) AND fatal_injuries.injury_date >= to_date('" + initialDateStr + "','dd/MM/yyyy') AND \n";
            sql = sql + " fatal_injuries.injury_date <= to_date('" + endDateStr + "','dd/MM/yyyy') \n";
            ResultSet resultSet = connection.consult(sql);
            totalRecords = "0";
            if (resultSet.next()) {
                totalRecords = String.valueOf(resultSet.getInt(1));
            }
            //System.out.println("Total de registros = " + totalRecords);
            //DETERMINO EL ID DE CADA REGISTRO            
            sql = "\n";
            sql = sql + " SELECT \n";
            sql = sql + " fatal_injuries.victim_id \n";
            sql = sql + " FROM \n";
            sql = sql + " public.victims, \n";
            sql = sql + " public.fatal_injuries \n";
            sql = sql + " WHERE \n";
            sql = sql + " fatal_injuries.victim_id = victims.victim_id AND ( \n";
            for (int i = 0; i < tagsList.size(); i++) {
                if (i == tagsList.size() - 1) {
                    sql = sql + " victims.tag_id = " + String.valueOf(tagsList.get(i).getTagId()) + " \n";
                } else {
                    sql = sql + " victims.tag_id = " + String.valueOf(tagsList.get(i).getTagId()) + " OR \n";
                }
            }
            sql = sql + " ) AND fatal_injuries.injury_date >= to_date('" + initialDateStr + "','dd/MM/yyyy') AND \n";
            sql = sql + " fatal_injuries.injury_date <= to_date('" + endDateStr + "','dd/MM/yyyy') \n";

            //CONSTRUYO EL TABLE_MODEL
            table_model = new LazyRecordSetsDataModel(Integer.parseInt(totalRecords), sql, FormsEnum.SCC_F_029);

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
            rowsDataTableArrayList = new ArrayList<RowDataTable>();
            ResultSet resultSet = connection.consult(sql);
            while (resultSet.next()) {
                rowsDataTableArrayList.add(connection.loadFatalInjuryTraafficRecord(resultSet.getString(1)));
                tuplesProcessed++;
                progress = (int) (tuplesProcessed * 100) / tuplesNumber;
                //System.out.println(progress);
            }
        } catch (SQLException ex) {
            Logger.getLogger(RecordSetsHomicideMB.class.getName()).log(Level.SEVERE, null, ex);
        }
        progress = 100;
    }

    /**
     * This method is responsible to export all records found.
     *
     * @param document
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

            createCell(cellStyle, row, colPosition++, "CODIGO INTERNO");//"100">#{rowX.column1}</p:column>
            createCell(cellStyle, row, colPosition++, "CODIGO");//"100">#{rowX.column23}</p:column>
            createCell(cellStyle, row, colPosition++, "DIA HECHO");//100">#{rowX.column37}</p:column>
            createCell(cellStyle, row, colPosition++, "MES HECHO");//100">#{rowX.column37}</p:column>
            createCell(cellStyle, row, colPosition++, "AÑO HECHO");//100">#{rowX.column37}</p:column>
            createCell(cellStyle, row, colPosition++, "FECHA HECHO");//"100">#{rowX.column13}</p:column>
            createCell(cellStyle, row, colPosition++, "DIA EN SEMANA");//"100">#{rowX.column20}</p:column>
            createCell(cellStyle, row, colPosition++, "HORA HECHO");//"100">#{rowX.column14}</p:column>
            createCell(cellStyle, row, colPosition++, "DIRECCION HECHO");//"400">#{rowX.column15}</p:column>
            createCell(cellStyle, row, colPosition++, "BARRIO HECHO");//"250">#{rowX.column16}</p:column>
            createCell(cellStyle, row, colPosition++, "CUADRANTE HECHO");
            createCell(cellStyle, row, colPosition++, "COMUNA BARRIO HECHO");//"250">#{rowX.column16}</p:column>
            createCell(cellStyle, row, colPosition++, "AREA HECHO");//"100">#{rowX.column24}</p:column>
            createCell(cellStyle, row, colPosition++, "TIPO DE VIA");//"100">#{rowX.column37}</p:column>
            createCell(cellStyle, row, colPosition++, "CLASE DE ACCIDENTE");//"250">#{rowX.column38}</p:column>
            createCell(cellStyle, row, colPosition++, "NUMERO VICTIMAS");//"100">#{rowX.column18}</p:column>
            createCell(cellStyle, row, colPosition++, "NUMERO LESIONADOS");//"100">#{rowX.column34}</p:column>
            createCell(cellStyle, row, colPosition++, "NOMBRES Y APELLIDOS");//"400">#{rowX.column4}</p:column>
            createCell(cellStyle, row, colPosition++, "SEXO");//"100">#{rowX.column8}</p:column>
            createCell(cellStyle, row, colPosition++, "TIPO EDAD");//"100">#{rowX.column6}</p:column>
            createCell(cellStyle, row, colPosition++, "EDAD");//"100">#{rowX.column7}</p:column>
            createCell(cellStyle, row, colPosition++, "OCUPACION");//"100">#{rowX.column9}</p:column>
            createCell(cellStyle, row, colPosition++, "TIPO IDENTIFICACION");//"100">#{rowX.column2}</p:column>                                 
            createCell(cellStyle, row, colPosition++, "IDENTIFICACION");//"100">#{rowX.column3}</p:column>                                
            createCell(cellStyle, row, colPosition++, "EXTRANJERO");//"100">#{rowX.column5}</p:column>
            createCell(cellStyle, row, colPosition++, "DEPARTAMENTO RESIDENCIA");//"100">#{rowX.column12}</p:column>
            createCell(cellStyle, row, colPosition++, "MUNICIPIO RESIDENCIA");//"100">#{rowX.column11}</p:column>
            createCell(cellStyle, row, colPosition++, "BARRIO RESIDENCIA");//"250">#{rowX.column10}</p:column>
            createCell(cellStyle, row, colPosition++, "COMUNA BARRIO RESIDENCIA");//"250">#{rowX.column10}</p:column>
            createCell(cellStyle, row, colPosition++, "PAIS PROCEDENCIA");//"100">#{rowX.column25}</p:column>
            createCell(cellStyle, row, colPosition++, "DEPARTAMENTO PROCEDENCIA");//"100">#{rowX.column26}</p:column>
            createCell(cellStyle, row, colPosition++, "MUNICIPIO PROCEDENCIA");//"100">#{rowX.column27}</p:column>        
            createCell(cellStyle, row, colPosition++, "CARACTERISTICAS DE LA VICTIMA");//"200">#{rowX.column35}</p:column>
            createCell(cellStyle, row, colPosition++, "MEDIDAS DE PROTECCION");//"250">#{rowX.column36}</p:column>
            createCell(cellStyle, row, colPosition++, "TIPO VEHICULO VICTIMA");//"100">#{rowX.column39}</p:column>
            createCell(cellStyle, row, colPosition++, "TIPO VEHICULO CONTRAPARTE 1");//"100">#{rowX.column28}</p:column>
            createCell(cellStyle, row, colPosition++, "TIPO VEHICULO CONTRAPARTE 2");//"100">#{rowX.column29}</p:column>
            createCell(cellStyle, row, colPosition++, "TIPO VEHICULO CONTRAPARTE 3");//"100">#{rowX.column30}</p:column>
            createCell(cellStyle, row, colPosition++, "TIPO DE SERVICIO VICTIMA");//"100">#{rowX.column40}</p:column>        
            createCell(cellStyle, row, colPosition++, "TIPO SERVICIO CONTRAPARTE 1");//"100">#{rowX.column31}</p:column>
            createCell(cellStyle, row, colPosition++, "TIPO SERVICIO CONTRAPARTE 2");//"100">#{rowX.column32}</p:column>
            createCell(cellStyle, row, colPosition++, "TIPO SERVICIO CONTRAPARTE 3");//"100">#{rowX.column33}</p:column>
            createCell(cellStyle, row, colPosition++, "NARRACION DEL HECHO");//"700">#{rowX.column19}</p:column>
            createCell(cellStyle, row, colPosition++, "NIVEL DE ALCOHOL VICTIMA");//"100">#{rowX.column21}</p:column>
            createCell(cellStyle, row, colPosition++, "TIPO NIVEL ALCOHOL VICTIMA");//"100">#{rowX.column22}</p:column>
            createCell(cellStyle, row, colPosition++, "NIVEL DE ALCOHOL CONTRAPARTE");//"100">#{rowX.column41}</p:column>
            createCell(cellStyle, row, colPosition++, "TIPO NIVEL DE ALCOHOL CONTRAPARTE");//"100">#{rowX.column42}</p:column>

            String[] splitDate;
            for (int i = 0; i < rowsDataTableArrayList.size(); i++) {
                colPosition = 0;
                RowDataTable rowDataTableList = rowsDataTableArrayList.get(i);
                rowPosition++;
                row = sheet.createRow(rowPosition);

                createCell(row, colPosition++, rowDataTableList.getColumn1());//"CODIGO INTERNO");//"100">#{rowX.column1}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn23());//"CODIGO");//"100">#{rowX.column23}</p:column>
                if (rowDataTableList.getColumn13() != null) {
                    splitDate = rowDataTableList.getColumn13().split("/");
                    if (splitDate.length == 3) {
                        createCell(row, colPosition++, splitDate[0]);//"DIA HECHO");
                        createCell(row, colPosition++, splitDate[1]);//"MES HECHO");
                        createCell(row, colPosition++, splitDate[2]);//"AÑO HECHO");
                    } else {
                        colPosition = colPosition + 3;
                    }
                } else {
                    colPosition = colPosition + 3;
                }
                createCell(row, colPosition++, rowDataTableList.getColumn13());//"FECHA HECHO");//"100">#{rowX.column13}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn20());//"DIA EN SEMANA");//"100">#{rowX.column20}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn14());//"HORA HECHO");//"100">#{rowX.column14}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn15());//"DIRECCION HECHO");//"400">#{rowX.column15}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn16());//"BARRIO HECHO");//"250">#{rowX.column16}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn45());//CUADRANTE HECHO
                createCell(row, colPosition++, rowDataTableList.getColumn44());//"COMUNA BARRIO HECHO");//"250">#{rowX.column16}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn24());//"AREA HECHO");//"100">#{rowX.column24}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn37());//"TIPO DE VIA");//"100">#{rowX.column37}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn38());//"CLASE DE ACCIDENTE");//"250">#{rowX.column38}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn18());//"NUMERO VICTIMAS");//"100">#{rowX.column18}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn34());//"NUMERO LESIONADOS");//"100">#{rowX.column34}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn4());//"NOMBRES Y APELLIDOS");//"400">#{rowX.column4}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn8());//"SEXO");//"100">#{rowX.column8}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn6());//"TIPO EDAD");//"100">#{rowX.column6}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn7());//"EDAD");//"100">#{rowX.column7}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn9());//"OCUPACION");//"100">#{rowX.column9}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn2());//"TIPO IDENTIFICACION");//"100">#{rowX.column2}</p:column>                                 
                createCell(row, colPosition++, rowDataTableList.getColumn3());//"IDENTIFICACION");//"100">#{rowX.column3}</p:column>                                
                createCell(row, colPosition++, rowDataTableList.getColumn5());//"EXTRANJERO");//"100">#{rowX.column5}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn12());//"DEPARTAMENTO RESIDENCIA");//"100">#{rowX.column12}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn11());//"MUNICIPIO RESIDENCIA");//"100">#{rowX.column11}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn10());//"BARRIO RESIDENCIA");//"250">#{rowX.column10}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn43());//"COMUNA BARRIO RESIDENCIA");//"250">#{rowX.column10}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn25());//"PAIS PROCEDENCIA");//"100">#{rowX.column25}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn26());//"DEPARTAMENTO PROCEDENCIA");//"100">#{rowX.column26}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn27());//"MUNICIPIO PROCEDENCIA");//"100">#{rowX.column27}</p:column>        
                createCell(row, colPosition++, rowDataTableList.getColumn35());//"CARACTERISTICAS DE LA VICTIMA");//"200">#{rowX.column35}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn36());//"MEDIDAS DE PROTECCION");//"250">#{rowX.column36}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn39());//"TIPO VEHICULO VICTIMA");//"100">#{rowX.column39}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn28());//"TIPO VEHICULO CONTRAPARTE 1");//"100">#{rowX.column28}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn29());//"TIPO VEHICULO CONTRAPARTE 2");//"100">#{rowX.column29}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn30());//"TIPO VEHICULO CONTRAPARTE 3");//"100">#{rowX.column30}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn40());//"TIPO DE SERVICIO VICTIMA");//"100">#{rowX.column40}</p:column>        
                createCell(row, colPosition++, rowDataTableList.getColumn31());//"TIPO SERVICIO CONTRAPARTE 1");//"100">#{rowX.column31}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn32());//"TIPO SERVICIO CONTRAPARTE 2");//"100">#{rowX.column32}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn33());//"TIPO SERVICIO CONTRAPARTE 3");//"100">#{rowX.column33}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn19());//"NARRACION DEL HECHO");//"700">#{rowX.column19}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn21());//"NIVEL DE ALCOHOL VICTIMA");//"100">#{rowX.column21}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn22());//"TIPO NIVEL ALCOHOL VICTIMA");//"100">#{rowX.column22}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn41());//"NIVEL DE ALCOHOL CONTRAPARTE");//"100">#{rowX.column41}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn42());//"TIPO NIVEL DE ALCOHOL CONTRAPARTE");//"100">#{rowX.column42}</p:column>
            }
        } catch (Exception ex) {
            Logger.getLogger(RecordSetsHomicideMB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method enables or disables the button “MOSTRAR FORMULARIO” according
     * to the selected rows and then display the content.
     */
    public void load() {
        currentFatalInjuryTraffic = null;
        btnEditDisabled = true;
        if (selectedRowsDataTable != null) {
            if (selectedRowsDataTable.length == 1) {
                currentFatalInjuryTraffic = fatalInjuryTrafficFacade.find(Integer.parseInt(selectedRowsDataTable[0].getColumn1()));
            }
            if (selectedRowsDataTable.length > 1) {
                btnEditDisabled = true;
            } else {
                btnEditDisabled = false;
            }
        }
    }

    /**
     * This method is used to remove a registry of the database.
     */
    public void deleteRegistry() {
        if (selectedRowsDataTable != null && selectedRowsDataTable.length != 0) {
            List<FatalInjuryTraffic> fatalInjuryTrafficList = new ArrayList<>();
            for (int j = 0; j < selectedRowsDataTable.length; j++) {
                fatalInjuryTrafficList.add(fatalInjuryTrafficFacade.find(Integer.parseInt(selectedRowsDataTable[j].getColumn1())));
            }
            for (int j = 0; j < fatalInjuryTrafficList.size(); j++) {
                FatalInjuries auxFatalInjuries = fatalInjuryTrafficList.get(j).getFatalInjuries();
                Victims auxVictims = fatalInjuryTrafficList.get(j).getFatalInjuries().getVictimId();
                fatalInjuryTrafficFacade.remove(fatalInjuryTrafficList.get(j));
                fatalInjuriesFacade.remove(auxFatalInjuries);
                victimsFacade.remove(auxVictims);
            }
            //deselecciono los controles
            selectedRowsDataTable = null;
            btnEditDisabled = true;
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

    public boolean isBtnEditDisabled() {
        return btnEditDisabled;
    }

    public void setBtnEditDisabled(boolean btnEditDisabled) {
        this.btnEditDisabled = btnEditDisabled;
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
