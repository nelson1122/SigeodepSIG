/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.duplicateSets;

import beans.connection.ConnectionJdbcMB;
import beans.util.RowDataTable;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import managedBeans.recordSets.RecordSetsMB;
import model.dao.*;
import model.pojo.*;

/**
 *
 * @author SANTOS
 */
/**
 * This class is responsible to detect duplicate records, this method displays
 * in a table the data of victims who may have duplicate records, when the user
 * selects the record of a victim, then the system is responsible to display the
 * " LISTADO DE POSIBLES DUPLICADOS PARA EL REGISTRO SELECCIONADO " that so the
 * user can select a duplicate record and then can delete it.
 *
 */
@ManagedBean(name = "duplicateSetsLcenfMB")
@SessionScoped
public class DuplicateSetsLcenfMB implements Serializable {

    //--------------------
    @EJB
    TagsFacade tagsFacade;
    private List<Tags> tagsList;
    //private NonFatalInjuries currentNonFatalInjury;
    @EJB
    NonFatalDomesticViolenceFacade nonFatalDomesticViolenceFacade;
    @EJB
    NonFatalInterpersonalFacade nonFatalInterpersonalFacade;
    @EJB
    NonFatalSelfInflictedFacade nonFatalSelfInflictedFacade;
    @EJB
    NonFatalTransportFacade nonFatalTransportFacade;
    @EJB
    AgeTypesFacade ageTypesFacade;
    @EJB
    MunicipalitiesFacade municipalitiesFacade;
    @EJB
    DepartamentsFacade departamentsFacade;
    @EJB
    VictimsFacade victimsFacade;
    @EJB
    NonFatalInjuriesFacade nonFatalInjuriesFacade;
    @EJB
    InjuriesFacade injuriesFacade;
    private List<RowDataTable> rowDataTableList;
    private List<RowDataTable> rowDuplicatedTableList;
    private RowDataTable selectedRowDataTable;
    private RowDataTable selectedRowDuplicatedTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    private String name = "";
    private String newName = "";
    private boolean btnViewDisabled = true;
    private boolean btnRemoveDisabled = true;
    private String data = "-";
    private String hours = "";
    private String minutes = "";
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy hh:mm");
    private String openForm = "";
    private RecordSetsMB recordSetsMB;
    ConnectionJdbcMB connectionJdbcMB;
    private int tuplesNumber = 0;
    private int tuplesProcessed = 0;
    private String initialDateStr = "";
    private String endDateStr = "";
    
    /**
     * Get current instance of the connection to the database
     */
    @PostConstruct
    private void initialize() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
    }

    /**
     * This method is the class constructor.
     */
    public DuplicateSetsLcenfMB() {
    }

    public String openForm() {
        return openForm;
    }

    /**
     * This method is used to display messages of the actions that are
     * realizing.
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
     * This method generates a list of all records that are possibly duplicate
     * of a selected victim.
     */
    public void loadDuplicatedRecords() {
        /*
         * saca la lista con todos lo s campos de los registros que pueden ser
         * duplicados de un
         */
        if (selectedRowDuplicatedTable != null) {
            try {
                rowDataTableList = new ArrayList<>();
                RowDataTable newRow = connectionJdbcMB.loadNonFatalInjuryRecord(selectedRowDuplicatedTable.getColumn1());
                if (newRow == null) {
                    printMessage(FacesMessage.SEVERITY_WARN, "Registro eliminado", "Se ha eliminado el registro con el cual se estaba comparando");
                } else {
                    rowDataTableList.add(newRow);
                    String sql = "";
                    sql = sql + "SELECT ";
                    sql = sql + "t1.victim_id ";
                    sql = sql + "FROM ";
                    sql = sql + "duplicate t1, duplicate t2 ";
                    sql = sql + "WHERE ";
                    sql = sql + "t2.victim_id = " + selectedRowDuplicatedTable.getColumn1() + " ";
                    sql = sql + "AND t1.victim_id != t2.victim_id ";
                    sql = sql + "AND levenshtein(t1.victim_nid, t2.victim_nid) < 4 ";
                    sql = sql + "AND levenshtein(t1.victim_name, t2.victim_name) < 4 ";
                    ResultSet resultSetCount = connectionJdbcMB.consult(sql);
                    int cont = 0;

                    while (resultSetCount.next()) {
                        cont++;
                        rowDataTableList.add(connectionJdbcMB.loadNonFatalInjuryRecord(resultSetCount.getString("victim_id")));
                    }
                    if (cont == 0) {
                        printMessage(FacesMessage.SEVERITY_WARN, "Sin datos", "Este registro ya no tiene posibles duplicados");
                    } else {
                        printMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se encontraron " + String.valueOf(cont) + " posibles duplicados");
                    }
                    selectedRowDataTable = null;
                }
            } catch (SQLException ex) {
                //Logger.getLogger(DuplicateRecordsMB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * This method is called the recordsets class when the user presses the
     * button “DETECTAR DUPLICADOS”. This method is used to display a list of
     * all possible duplicates that exist given a starting date and an ending
     * date.
     *
     * @param selectedRowsDataTableTags
     */
    public void loadValues(RowDataTable[] selectedRowsDataTableTags) {
        /*
         * se llama a esta funcion desde record sets cuando se presiona el boton
         * "registros duplicados"
         */
        FacesContext context = FacesContext.getCurrentInstance();
        recordSetsMB = (RecordSetsMB) context.getApplication().evaluateExpressionGet(context, "#{recordSetsMB}", RecordSetsMB.class);
        recordSetsMB.setProgress(0);
        tuplesNumber = 0;
        tuplesProcessed = 0;

        selectedRowDataTable = null;
        rowDataTableList = new ArrayList<>();
        data = "- ";
        //CREO LA LISTA DE TAGS SELECCIONADOS
        tagsList = new ArrayList<>();
        for (int i = 0; i < selectedRowsDataTableTags.length; i++) {
            data = data + selectedRowsDataTableTags[i].getColumn2() + " -";
            tagsList.add(tagsFacade.find(Integer.parseInt(selectedRowsDataTableTags[i].getColumn1())));
            //tuplesNumber = tuplesNumber + nonFatalInjuriesFacade.countFromTag(tagsList.get(i).getTagId());
        }
        selectedRowDuplicatedTable = null;
        selectedRowDataTable = null;
        rowDataTableList = new ArrayList<>();
        rowDuplicatedTableList = new ArrayList<>();
        btnViewDisabled = true;
        btnRemoveDisabled = true;

        /*
         * saca una lista con el nombre, identificacion y numero registros que
         * posiblemente son duplicados
         */
        try {
            connectionJdbcMB.non_query("DROP TABLE IF EXISTS duplicate");
            String sql = ""
                    + "create TABLE duplicate as \n"
                    + "   SELECT \n"
                    + "      victims.victim_id, \n"
                    + "      victims.victim_nid, \n"
                    + "      victims.victim_name \n"
                    + "   FROM \n"
                    + "      public.victims, \n"
                    + "      public.non_fatal_injuries \n"
                    + "   WHERE  \n"
                    + "      non_fatal_injuries.victim_id = victims.victim_id AND ( \n";
            for (int i = 0; i < tagsList.size(); i++) {
                if (i == 0) {
                    sql = sql + "     victims.tag_id = " + tagsList.get(i).getTagId().toString() + " \n";
                } else {
                    sql = sql + "     OR victims.tag_id = " + tagsList.get(i).getTagId().toString() + " \n";
                }
            }
            //limitar rango de fecha
            sql = sql + "    ) AND non_fatal_injuries.injury_date >= to_date('" + initialDateStr + "','dd/MM/yyyy') AND \n";
            sql = sql + "    non_fatal_injuries.injury_date <= to_date('" + endDateStr + "','dd/MM/yyyy') \n";
            //System.out.println("SQL001: \n" + sql);
            connectionJdbcMB.non_query(sql);
            //CUENTO EL NUMERO DE REGISTROS EN LA CONSULTA---------------------------
            tuplesNumber = 0;
            sql = ""
                    + "   SELECT \n"
                    + "      count(*) \n"
                    + "   FROM \n"
                    + "      public.victims, \n"
                    + "      public.non_fatal_injuries \n"
                    + "   WHERE  \n"
                    + "      non_fatal_injuries.victim_id = victims.victim_id AND ( \n";
            for (int i = 0; i < tagsList.size(); i++) {
                if (i == 0) {
                    sql = sql + "     victims.tag_id = " + tagsList.get(i).getTagId().toString() + " \n";
                } else {
                    sql = sql + "     OR victims.tag_id = " + tagsList.get(i).getTagId().toString() + " \n";
                }
            }
            sql = sql + "    ) AND non_fatal_injuries.injury_date >= to_date('" + initialDateStr + "','dd/MM/yyyy') AND \n";
            sql = sql + "    non_fatal_injuries.injury_date <= to_date('" + endDateStr + "','dd/MM/yyyy') \n";
            ResultSet rs = connectionJdbcMB.consult(sql);
            if (rs.next()) {
                tuplesNumber = rs.getInt(1);
            }
            //------------


            rowDuplicatedTableList = new ArrayList<>();

            ResultSet resultSetFileData = connectionJdbcMB.consult("Select * from duplicate");
            ArrayList<String> addedRecords = new ArrayList<>();
            boolean first;
            boolean found;
            int countRegisters;
            while (resultSetFileData.next()) {
                //contamos el numero de registros que pueden ser posibles repeticiones
                //si supera la validacion se agregamos a la lista
                found = false;
                for (int i = 0; i < addedRecords.size(); i++) {//saber si ya fue evaluado
                    if (resultSetFileData.getString("victim_id").compareTo(addedRecords.get(i)) == 0) {
                        found = true;
                    }
                }
                if (!found) {//el elemento no ha sido evaluado ni adicionado
                    sql = "";
                    sql = sql + "SELECT ";
                    sql = sql + "t1.victim_id ";
                    sql = sql + "FROM ";
                    sql = sql + "duplicate t1, duplicate t2 ";
                    sql = sql + "WHERE ";
                    sql = sql + "t2.victim_id = " + resultSetFileData.getString("victim_id") + " ";
                    sql = sql + "AND t1.victim_id != t2.victim_id ";
                    sql = sql + "AND levenshtein(t1.victim_nid, t2.victim_nid) < 4 ";
                    sql = sql + "AND levenshtein(t1.victim_name, t2.victim_name) < 4 ";
                    ResultSet resultSetCount = connectionJdbcMB.consult(sql);
                    first = true;
                    countRegisters = 0;
                    while (resultSetCount.next()) {
                        countRegisters++;
                        if (first) {
                            addedRecords.add(resultSetFileData.getString("victim_id"));
                            first = false;
                        }
                        addedRecords.add(resultSetCount.getString("victim_id"));
                    }
                    if (countRegisters != 0) {//adiciono el registro a la tabla
                        rowDuplicatedTableList.add(new RowDataTable(
                                resultSetFileData.getString("victim_id"),
                                resultSetFileData.getString("victim_nid"),
                                resultSetFileData.getString("victim_name"),
                                String.valueOf(countRegisters)));
                    }
                }
                tuplesProcessed++;
                recordSetsMB.setProgress((int) (tuplesProcessed * 100) / tuplesNumber);
                System.out.println(recordSetsMB.getProgress());
            }
            recordSetsMB.setProgress(100);
            selectedRowDataTable = null;
        } catch (SQLException ex) {
            recordSetsMB.setProgress(100);
            System.out.println("Error: " + ex.toString());
        }
    }

    /**
     * This method creates a new array to store all possible duplicate records
     * of the selected victim
     */
    public void rowDuplicatedTableListSelect() {
        selectedRowDataTable = null;
        rowDataTableList = new ArrayList<>();
        btnViewDisabled = true;
        btnRemoveDisabled = true;
        if (selectedRowDuplicatedTable != null) {
            loadDuplicatedRecords();
        }
    }

    /**
     * This method enables the delete button after selecting a row of a
     * duplicate record.
     */
    public void rowDataTableListSelect() {
        //currentNonFatalInjury = null;
        btnRemoveDisabled = true;
        if (selectedRowDataTable != null) {
            if (selectedRowDataTable.getColumn1().compareTo("COMPARADO") != 0) {
                btnRemoveDisabled = false;
            }
            //currentNonFatalInjury = nonFatalInjuriesFacade.find(Integer.parseInt(selectedRowDataTable.getColumn1()));
        }
    }

    /**
     * This method is used to delete a duplicate record has been selected.
     */
    public void deleteRegistry() {
        if (selectedRowDataTable != null) {
            NonFatalInjuries nonFatalInjuriesSelect = nonFatalInjuriesFacade.find(Integer.parseInt(selectedRowDataTable.getColumn1()));
            if (nonFatalInjuriesSelect != null) {
                if (nonFatalInjuriesSelect.getNonFatalDomesticViolence() != null) {
                    nonFatalDomesticViolenceFacade.remove(nonFatalInjuriesSelect.getNonFatalDomesticViolence());
                }
                if (nonFatalInjuriesSelect.getNonFatalInterpersonal() != null) {
                    nonFatalInterpersonalFacade.remove(nonFatalInjuriesSelect.getNonFatalInterpersonal());
                }
                if (nonFatalInjuriesSelect.getNonFatalSelfInflicted() != null) {
                    nonFatalSelfInflictedFacade.remove(nonFatalInjuriesSelect.getNonFatalSelfInflicted());
                }
                if (nonFatalInjuriesSelect.getNonFatalTransport() != null) {
                    nonFatalTransportFacade.remove(nonFatalInjuriesSelect.getNonFatalTransport());
                }
                nonFatalInjuriesFacade.remove(nonFatalInjuriesSelect);
                victimsFacade.remove(nonFatalInjuriesSelect.getVictimId());
                //----------------------------------------------------------

                printMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha realizado la eliminacion de los registros seleccionados");
            } else {
                printMessage(FacesMessage.SEVERITY_WARN, "Alerta", "El registro seleccionado es quien se esta comparando, por tanto no se puede eliminar");
            }
            //quito los elementos seleccionados de rowsDataTableList seleccion de 
            for (int i = 0; i < rowDataTableList.size(); i++) {
                if (selectedRowDataTable.getColumn1().compareTo(rowDataTableList.get(i).getColumn1()) == 0) {
                    rowDataTableList.remove(i);
                    break;
                }
            }
            //deselecciono los controles
            selectedRowDataTable = null;
            btnRemoveDisabled = true;
        }
    }

    public List<RowDataTable> getRowDataTableList() {
        return rowDataTableList;
    }

    public void setRowDataTableList(List<RowDataTable> rowDataTableList) {
        this.rowDataTableList = rowDataTableList;
    }

    public List<RowDataTable> getRowDuplicatedTableList() {
        return rowDuplicatedTableList;
    }

    public void setRowDuplicatedTableList(List<RowDataTable> rowDuplicatedTableList) {
        this.rowDuplicatedTableList = rowDuplicatedTableList;
    }

    public RowDataTable getSelectedRowDuplicatedTable() {
        return selectedRowDuplicatedTable;
    }

    public void setSelectedRowDuplicatedTable(RowDataTable selectedRowDuplicatedTable) {
        this.selectedRowDuplicatedTable = selectedRowDuplicatedTable;
    }

    public RowDataTable getSelectedRowDataTable() {
        return selectedRowDataTable;
    }

    public void setSelectedRowDataTable(RowDataTable selectedRowDataTable) {
        this.selectedRowDataTable = selectedRowDataTable;
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

    public boolean isBtnViewDisabled() {
        return btnViewDisabled;
    }

    public void setBtnViewDisabled(boolean btnViewDisabled) {
        this.btnViewDisabled = btnViewDisabled;
    }

    public boolean isBtnRemoveDisabled() {
        return btnRemoveDisabled;
    }

    public void setBtnRemoveDisabled(boolean btnRemoveDisabled) {
        this.btnRemoveDisabled = btnRemoveDisabled;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getInitialDateStr() {
        return initialDateStr;
    }

    public void setInitialDateStr(String initialDateStr) {
        this.initialDateStr = initialDateStr;
    }

    public String getEndDateStr() {
        return endDateStr;
    }

    public void setEndDateStr(String endDateStr) {
        this.endDateStr = endDateStr;
    }
}
