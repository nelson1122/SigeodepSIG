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
import managedBeans.forms.HomicideMB;
import model.dao.FatalInjuriesFacade;
import model.dao.FatalInjuryMurderFacade;
import model.dao.TagsFacade;
import model.dao.VictimsFacade;
import model.pojo.FatalInjuries;
import model.pojo.FatalInjuryMurder;
import model.pojo.Tags;
import model.pojo.Victims;
import org.apache.poi.hssf.usermodel.*;
import org.primefaces.model.LazyDataModel;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "recordSetsHomicideMB")
@SessionScoped
/**
 * This class handles record set that correspond to homicides
 */
public class RecordSetsHomicideMB implements Serializable {

    @EJB
    TagsFacade tagsFacade;
    @EJB
    VictimsFacade victimsFacade;
    @EJB
    FatalInjuryMurderFacade fatalInjuryMurderFacade;
    @EJB
    FatalInjuriesFacade fatalInjuriesFacade;
    private List<Tags> tagsList;
    private FatalInjuryMurder currentFatalInjuryMurder;
    private RowDataTable[] selectedRowsDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    private String name = "";
    private String newName = "";
    private boolean btnEditDisabled = true;
    private String data = "-";
    private HomicideMB homicideMB;
    private String openForm = "";
    private LazyDataModel<RowDataTable> table_model;
    private ArrayList<RowDataTable> rowsDataTableArrayList;
    private ConnectionJdbcMB connection;
    private String totalRecords = "0";
    private String initialDateStr = "";
    private String endDateStr = "";
    private int tuplesNumber;
    private Integer tuplesProcessed;
    private int progress = 0;//PROGRESO AL CREAR XLS
    private String exportFileName = "";
    private String sql = "";

    /**
     * This method Instance tag list, the table model and gets the current
     * instance of the connection to the database.
     */
    public RecordSetsHomicideMB() {
        tagsList = new ArrayList<>();
        table_model = new LazyRecordSetsDataModel(0, "", FormsEnum.SCC_F_028);
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
        homicideMB = (HomicideMB) context.getApplication().evaluateExpressionGet(context, "#{homicideMB}", HomicideMB.class);
        homicideMB.loadValues(tagsList, currentFatalInjuryMurder);
        openForm = "homicide";
    }

    /**
     * load the information corresponding to a victim within the form
     *
     * @param selectedRowsDataTableTags
     */
    void loadValues(RowDataTable[] selectedRowsDataTableTags) {

        try {
            //CREO LA LISTA DE TAGS SELECCIONADOS     
            exportFileName = "HOMICIDIOS - " + initialDateStr + " - " + endDateStr;
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
            table_model = new LazyRecordSetsDataModel(Integer.parseInt(totalRecords), sql, FormsEnum.SCC_F_028);

        } catch (SQLException ex) {
            Logger.getLogger(RecordSetsLcenfMB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is used when the user wants to export all records found. this
     * method creates a cell acording to the parameters
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
     * method creates a cell acording to the parameters
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
                rowsDataTableArrayList.add(connection.loadFatalInjuryMurderRecord(resultSet.getString(1)));
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
            createCell(cellStyle, row, colPosition++, "CODIGO");
            createCell(cellStyle, row, colPosition++, "DIA HECHO");
            createCell(cellStyle, row, colPosition++, "MES HECHO");
            createCell(cellStyle, row, colPosition++, "AÑO HECHO");
            createCell(cellStyle, row, colPosition++, "FECHA HECHO");
            createCell(cellStyle, row, colPosition++, "DIA EN SEMANA");
            createCell(cellStyle, row, colPosition++, "HORA HECHO");
            createCell(cellStyle, row, colPosition++, "DIRECCION HECHO");
            createCell(cellStyle, row, colPosition++, "BARRIO HECHO");
            createCell(cellStyle, row, colPosition++, "CUADRANTE HECHO");
            createCell(cellStyle, row, colPosition++, "COMUNA BARRIO HECHO");
            createCell(cellStyle, row, colPosition++, "AREA DEL HECHO");
            createCell(cellStyle, row, colPosition++, "CLASE DE LUGAR");
            createCell(cellStyle, row, colPosition++, "NUMERO DE VICTIMAS");
            createCell(cellStyle, row, colPosition++, "NOMBRES APELLIDOS");
            createCell(cellStyle, row, colPosition++, "SEXO");
            createCell(cellStyle, row, colPosition++, "TIPO EDAD");
            createCell(cellStyle, row, colPosition++, "EDAD");
            createCell(cellStyle, row, colPosition++, "OCUPACION");
            createCell(cellStyle, row, colPosition++, "TIPO IDENTIFICACION");
            createCell(cellStyle, row, colPosition++, "IDENTIFICACION");
            createCell(cellStyle, row, colPosition++, "EXTRANJERO");
            createCell(cellStyle, row, colPosition++, "DEPARTAMENTO RESIDENCIA");
            createCell(cellStyle, row, colPosition++, "MUNICIPIO RESIDENCIA");
            createCell(cellStyle, row, colPosition++, "BARRIO RESIDENCIA");
            createCell(cellStyle, row, colPosition++, "COMUNA BARRIO RESIDENCIA");
            createCell(cellStyle, row, colPosition++, "PAIS PROCEDENCIA");
            createCell(cellStyle, row, colPosition++, "DEPARTAMENTO PROCEDENCIA");
            createCell(cellStyle, row, colPosition++, "MUNICIPIO PROCEDENCIA");
            createCell(cellStyle, row, colPosition++, "ARMA O CAUSA DE MUERTE");
            createCell(cellStyle, row, colPosition++, "CONTEXTO RELACIONADO CON EL HECHO");
            createCell(cellStyle, row, colPosition++, "NARRACION DEL HECHO");
            createCell(cellStyle, row, colPosition++, "NIVEL DE ALCOHOL");
            createCell(cellStyle, row, colPosition++, "TIPO NIVEL DE ALCOHOL");

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
                        createCell(row, colPosition++, splitDate[0]);//"AÑO HECHO");
                        createCell(row, colPosition++, splitDate[1]);//"MES HECHO");
                        createCell(row, colPosition++, splitDate[2]);//"AÑO HECHO");
                    } else {
                        colPosition = colPosition + 3;
                    }
                } else {
                    colPosition = colPosition + 3;
                }

                createCell(row, colPosition++, rowDataTableList.getColumn13());//"FECHA HECHO"
                createCell(row, colPosition++, rowDataTableList.getColumn20());//"DIA EN SEMANA"
                createCell(row, colPosition++, rowDataTableList.getColumn14());//"HORA HECHO"
                createCell(row, colPosition++, rowDataTableList.getColumn15());//"DIRECCION HECHO"
                createCell(row, colPosition++, rowDataTableList.getColumn16());//"BARRIO HECHO"
                createCell(row, colPosition++, rowDataTableList.getColumn32());//CUADRANTE HECHO
                createCell(row, colPosition++, rowDataTableList.getColumn30());//"COMUNA BARRIO HECHO"
                createCell(row, colPosition++, rowDataTableList.getColumn24());//"AREA DEL HECHO"
                createCell(row, colPosition++, rowDataTableList.getColumn17());//"CLASE DE LUGAR"
                createCell(row, colPosition++, rowDataTableList.getColumn18());//"NUMERO DE VICTIMAS"
                createCell(row, colPosition++, rowDataTableList.getColumn4());//"NOMBRES APELLIDOS"
                createCell(row, colPosition++, rowDataTableList.getColumn8());//"SEXO"
                createCell(row, colPosition++, rowDataTableList.getColumn6());//"TIPO EDAD"
                createCell(row, colPosition++, rowDataTableList.getColumn7());//"EDAD"
                createCell(row, colPosition++, rowDataTableList.getColumn9());//"OCUPACION"
                createCell(row, colPosition++, rowDataTableList.getColumn2());//"TIPO IDENTIFICACION"
                createCell(row, colPosition++, rowDataTableList.getColumn3());//"IDENTIFICACION"
                createCell(row, colPosition++, rowDataTableList.getColumn5());//"EXTRANJERO"
                createCell(row, colPosition++, rowDataTableList.getColumn12());//"DEPARTAMENTO RESIDENCIA"
                createCell(row, colPosition++, rowDataTableList.getColumn11());//"MUNICIPIO RESIDENCIA"
                createCell(row, colPosition++, rowDataTableList.getColumn10());//"BARRIO RESIDENCIA"
                createCell(row, colPosition++, rowDataTableList.getColumn31());//"COMUNA BARRIO RESIDENCIA"
                createCell(row, colPosition++, rowDataTableList.getColumn25());//"PAIS PROCEDENCIA"
                createCell(row, colPosition++, rowDataTableList.getColumn26());//"DEPARTAMENTO PROCEDENCIA"
                createCell(row, colPosition++, rowDataTableList.getColumn27());//"MUNICIPIO PROCEDENCIA"
                createCell(row, colPosition++, rowDataTableList.getColumn29());//"ARMA O CAUSA DE MUERTE"
                createCell(row, colPosition++, rowDataTableList.getColumn28());//"CONTEXTO RELACIONADO CON EL HECHO"
                createCell(row, colPosition++, rowDataTableList.getColumn19());//"NARRACION DEL HECHO"
                createCell(row, colPosition++, rowDataTableList.getColumn21());//"NIVEL DE ALCOHOL"
                createCell(row, colPosition++, rowDataTableList.getColumn22());//"TIPO NIVEL DE ALCOHOL"                
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
        currentFatalInjuryMurder = null;
        btnEditDisabled = true;
        if (selectedRowsDataTable != null) {
            if (selectedRowsDataTable.length == 1) {
                currentFatalInjuryMurder = fatalInjuryMurderFacade.find(Integer.parseInt(selectedRowsDataTable[0].getColumn1()));
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
            List<FatalInjuryMurder> fatalInjuryMurderList = new ArrayList<>();
            for (int j = 0; j < selectedRowsDataTable.length; j++) {
                fatalInjuryMurderList.add(fatalInjuryMurderFacade.find(Integer.parseInt(selectedRowsDataTable[j].getColumn1())));
            }
            for (int j = 0; j < fatalInjuryMurderList.size(); j++) {
                FatalInjuries auxFatalInjuries = fatalInjuryMurderList.get(j).getFatalInjuries();
                Victims auxVictims = fatalInjuryMurderList.get(j).getFatalInjuries().getVictimId();
                fatalInjuryMurderFacade.remove(fatalInjuryMurderList.get(j));
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
