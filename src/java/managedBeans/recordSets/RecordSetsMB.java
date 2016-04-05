/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.recordSets;

import beans.connection.ConnectionJdbcMB;
import beans.enumerators.FormsEnum;
import beans.enumerators.VariablesEnum;
import beans.util.RowDataTable;
import java.io.Serializable;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import managedBeans.duplicateSets.*;
import model.dao.*;
import model.pojo.*;
import org.joda.time.DateTime;
import org.primefaces.context.RequestContext;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "recordSetsMB")
@SessionScoped
public class RecordSetsMB implements Serializable {

    @EJB
    TagsFacade tagsFacade;
    @EJB
    FormsFacade formsFacade;
    private RowDataTable[] selectedRowsDataTable;
    private RowDataTable selectedRowsDataTable2;
    private List<RowDataTable> rowDataTableList;
    private List<RowDataTable> rowDataTableList2;
    private List<Tags> tagsList;
    private Tags currentTag;
    private int currentSearchCriteria = 0;
    private int currentSearchCriteria2 = 0;
    private String currentSearchValue = "";
    private String currentSearchValue2 = "";
    private String name = "";
    private String newName = "";
    private String openRecordSets;
    private String openDuplicateSets;
    private String formName = "";
    private String groupName = "";
    private String editGroupName = "";
    private String editFormName = "";
    private boolean btnEditDisabled = true;
    private boolean btnRemoveDisabled = true;
    private boolean btnViewDisabled = true;
    private boolean btnDuplicateDisabled = true;
    private boolean btnJoinDisabled = true;
    private RecordSetsLcenfMB recordSetsLcenfMB;
    private RecordSetsAccidentalMB recordSetsAccidentalMB;
    private RecordSetsHomicideMB recordSetsHomicideMB;
    private RecordSetsSuicideMB recordSetsSuicideMB;
    private RecordSetsTransitMB recordSetsTransitMB;
    private RecordSetsVifMB recordSetsVifMB;
    private RecordSetsSivigilaVifMB recordSetsSivigilaVifMB;
    private DuplicateSetsLcenfMB duplicateSetsLcenfMB;
    private DuplicateSetsAccidentalMB duplicateSetsAccidentalMB;
    private DuplicateSetsHomicideMB duplicateSetsHomicideMB;
    private DuplicateSetsSuicideMB duplicateSetsSuicideMB;
    private DuplicateSetsTransitMB duplicateSetsTransitMB;
    private DuplicateSetsVifMB duplicateSetsVifMB;
    private DuplicateSetsSIvigilaVifMB duplicateSetsSivigilaVifMB;
    private SelectItem[] forms;
    private Short currentForm = 0;
    private int progress = 0;//PROGRESO AL ABRIR CONJUNTOS
    private int progressDelete = 0;//PROGRESO AL ELIMINAR CONJUNTOS
    private int progressSplit = 0;//PROGRESO AL ELIMINAR CONJUNTOS    
    private Date initialDateView = new Date();
    private Date endDateView = new Date();
    private Date initialDateDuplicate = new Date();
    private Date endDateDuplicate = new Date();
    private ConnectionJdbcMB connectionJdbcMB;
    private SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
    private FacesMessage msg;
    String sizeData = "Número de registros no determinado";
    int sizeInt = 0;

    /**
     * This method is the class constructor, this method is responsible to
     * instantiate all the variables needed for the management of recordset,
     * algo this method gets the current connection to the database.
     */
    public RecordSetsMB() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
        Calendar c = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        initialDateView.setDate(1);
        initialDateView.setMonth(0);
        initialDateView.setYear(2002 - 1900);
        endDateView.setDate(c.get(Calendar.DATE));
        endDateView.setMonth(c.get(Calendar.MONTH));
        endDateView.setYear(c.get(Calendar.YEAR) - 1900);

        endDateDuplicate.setDate(c.get(Calendar.DATE));
        endDateDuplicate.setMonth(c.get(Calendar.MONTH));
        endDateDuplicate.setYear(c.get(Calendar.YEAR) - 1900);

        initialDateDuplicate.setDate(1);
        initialDateDuplicate.setMonth(0);
        initialDateDuplicate.setYear(2002 - 1900);
    }

    public void onCompleteLoad() {
        progress = 100;
    }

    public void onCompleteDelete() {
        progressDelete = 0;
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onCompleteSplit() {
        progressSplit = 0;
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /**
     * This method displays the records set corresponding to a type of injury
     * selected.
     *
     * @return
     */
    public String openRecordSets() {
        return openRecordSets;
    }

    /**
     * This method is responsible to determine the number of records which the
     * method it going to realize the detection of duplicates.
     */
    public void determineSizeData() {
        //determinar el numero de registros sobre los que se va a realizar la deteccion de duplicados
        sizeData = "Número de registros no determinado";
        sizeInt = 0;
        ResultSet rs;
        try {
            if (selectedRowsDataTable != null && selectedRowsDataTable.length != 0) {//se ha seleccionado algo de la tabla de conjuntos
                for (int i = 0; i < selectedRowsDataTable.length; i++) {//ciclo por cada uno de los conunjuntos seleccionados
                    switch (FormsEnum.convert(selectedRowsDataTable[i].getColumn3().replace("-", "_"))) {
                        case SCC_F_028:
                        case SCC_F_029:
                        case SCC_F_030:
                        case SCC_F_031:
                            rs = connectionJdbcMB.consult(""
                                    + " select "
                                    + "   count(*) "
                                    + " from "
                                    + "   fatal_injuries, victims"
                                    + " where "
                                    + "   victims.victim_id = fatal_injuries.victim_id AND "
                                    + "   victims.tag_id =  " + selectedRowsDataTable[i].getColumn1() + " AND "
                                    + "   fatal_injuries.injury_date >= to_date('" + formato.format(initialDateDuplicate) + "','dd/MM/yyyy') AND "
                                    + "   fatal_injuries.injury_date <= to_date('" + formato.format(endDateDuplicate) + "','dd/MM/yyyy') \n");
                            if (rs.next()) {
                                sizeInt = sizeInt + rs.getInt(1);
                            }
                            break;
                        case SCC_F_032:
                        case SCC_F_033:
                        case SIVIGILA_VIF:
                            rs = connectionJdbcMB.consult(""
                                    + " select "
                                    + "   count(*) "
                                    + " from "
                                    + "   non_fatal_injuries, victims"
                                    + " where "
                                    + "   victims.victim_id = non_fatal_injuries.victim_id AND "
                                    + "   victims.tag_id =  " + selectedRowsDataTable[i].getColumn1() + " AND "
                                    + "   non_fatal_injuries.injury_date >= to_date('" + formato.format(initialDateDuplicate) + "','dd/MM/yyyy') AND "
                                    + "   non_fatal_injuries.injury_date <= to_date('" + formato.format(endDateDuplicate) + "','dd/MM/yyyy') \n");
                            if (rs.next()) {
                                sizeInt = sizeInt + rs.getInt(1);
                            }
                            break;
                    }
                }
                if (sizeInt <= 3000) {
                    sizeData = "Número de registros: " + sizeInt;
                } else {
                    sizeData = "Número de registros: " + sizeInt + ", el número de registros debe ser inerior a 3000";
                }
            }
        } catch (Exception e) {
            System.err.println("error: " + e.getMessage());
        }
    }

    /**
     * This method is responsible to detecte the duplicate records that are in a
     * given set
     */
    public void detectDuplicateClick() {
        openRecordSets = null;
        openDuplicateSets = null;

        if (sizeInt > 3000) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La detección de duĺicados debe realizarce en un número inferior a 3000 registros."));

        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            if (selectedRowsDataTable != null && selectedRowsDataTable.length != 0) {
                if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-028") == 0) {
                    duplicateSetsHomicideMB = (DuplicateSetsHomicideMB) context.getApplication().evaluateExpressionGet(context, "#{duplicateSetsHomicideMB}", DuplicateSetsHomicideMB.class);
                    duplicateSetsHomicideMB.setInitialDateStr(formato.format(initialDateDuplicate));
                    duplicateSetsHomicideMB.setEndDateStr(formato.format(endDateDuplicate));
                    duplicateSetsHomicideMB.loadValues(selectedRowsDataTable);
                    openRecordSets = "recordSetsHomicide";
                    openDuplicateSets = "duplicateSetsHomicide";
                } else if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-029") == 0) {
                    duplicateSetsTransitMB = (DuplicateSetsTransitMB) context.getApplication().evaluateExpressionGet(context, "#{duplicateSetsTransitMB}", DuplicateSetsTransitMB.class);
                    duplicateSetsTransitMB.setInitialDateStr(formato.format(initialDateDuplicate));
                    duplicateSetsTransitMB.setEndDateStr(formato.format(endDateDuplicate));
                    duplicateSetsTransitMB.loadValues(selectedRowsDataTable);
                    openRecordSets = "recordSetsTransit";
                    openDuplicateSets = "duplicateSetsTransit";
                } else if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-030") == 0) {
                    duplicateSetsSuicideMB = (DuplicateSetsSuicideMB) context.getApplication().evaluateExpressionGet(context, "#{duplicateSetsSuicideMB}", DuplicateSetsSuicideMB.class);
                    duplicateSetsSuicideMB.setInitialDateStr(formato.format(initialDateDuplicate));
                    duplicateSetsSuicideMB.setEndDateStr(formato.format(endDateDuplicate));
                    duplicateSetsSuicideMB.loadValues(selectedRowsDataTable);
                    openRecordSets = "recordSetsSuicide";
                    openDuplicateSets = "duplicateSetsSuicide";
                } else if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-031") == 0) {
                    duplicateSetsAccidentalMB = (DuplicateSetsAccidentalMB) context.getApplication().evaluateExpressionGet(context, "#{duplicateSetsAccidentalMB}", DuplicateSetsAccidentalMB.class);
                    duplicateSetsAccidentalMB.setInitialDateStr(formato.format(initialDateDuplicate));
                    duplicateSetsAccidentalMB.setEndDateStr(formato.format(endDateDuplicate));
                    duplicateSetsAccidentalMB.loadValues(selectedRowsDataTable);
                    openRecordSets = "recordSetsAccidental";
                    openDuplicateSets = "duplicateSetsAccidental";
                } else if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-032") == 0) {
                    duplicateSetsLcenfMB = (DuplicateSetsLcenfMB) context.getApplication().evaluateExpressionGet(context, "#{duplicateSetsLcenfMB}", DuplicateSetsLcenfMB.class);
                    duplicateSetsLcenfMB.setInitialDateStr(formato.format(initialDateDuplicate));
                    duplicateSetsLcenfMB.setEndDateStr(formato.format(endDateDuplicate));
                    duplicateSetsLcenfMB.loadValues(selectedRowsDataTable);
                    openRecordSets = "recordSetsLCENF";
                    openDuplicateSets = "duplicateSetsLCENF";
                } else if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-033") == 0 || selectedRowsDataTable[0].getColumn3().compareTo("SIVIGILA-VIF") == 0) {
                    int countTagsSivigila = 0;
                    int countTagsVif = 0;
                    for (int i = 0; i < selectedRowsDataTable.length; i++) {
                        if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-033") == 0) {
                            countTagsVif++;
                        }
                        if (selectedRowsDataTable[0].getColumn3().compareTo("SIVIGILA-VIF") == 0) {
                            countTagsSivigila++;
                        }
                    }
                    int caso = 0;
                    if (countTagsVif == 0 && countTagsSivigila != 0) {//solo cargas de sivigila
                        caso = 1;//abre sivigila
                    } else if (countTagsVif != 0 && countTagsSivigila == 0) {//solo cargas de vif
                        caso = 2;//abre vif
                    } else if (countTagsVif != 0 && countTagsSivigila != 0) {//cargas de vif y de sivigila
                        caso = 2;//abre vif para los dos
                    }
                    switch (caso) {
                        case 1://abrir duplicados de SIVIGILA
                            duplicateSetsSivigilaVifMB = (DuplicateSetsSIvigilaVifMB) context.getApplication().evaluateExpressionGet(context, "#{duplicateSetsSivigilaVifMB}", DuplicateSetsSIvigilaVifMB.class);
                            duplicateSetsSivigilaVifMB.setInitialDateStr(formato.format(initialDateDuplicate));
                            duplicateSetsSivigilaVifMB.setEndDateStr(formato.format(endDateDuplicate));
                            duplicateSetsSivigilaVifMB.loadValues(selectedRowsDataTable);
                            openRecordSets = "recordSetsSivigilaVif";
                            openDuplicateSets = "duplicateSetsSivigilaVif";
                            break;
                        case 2://abrir duplicados de VIF
                            duplicateSetsVifMB = (DuplicateSetsVifMB) context.getApplication().evaluateExpressionGet(context, "#{duplicateSetsVifMB}", DuplicateSetsVifMB.class);
                            duplicateSetsVifMB.setInitialDateStr(formato.format(initialDateDuplicate));
                            duplicateSetsVifMB.setEndDateStr(formato.format(endDateDuplicate));
                            duplicateSetsVifMB.loadValues(selectedRowsDataTable);
                            openRecordSets = "recordSetsVIF";
                            openDuplicateSets = "duplicateSetsVIF";
                            break;
                    }
                } else {

                    printMessage(FacesMessage.SEVERITY_WARN, "Alerta", "no se pudo cargar ");
                }
            } else {
                printMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar uno o varios conjuntos para iniciar la detección");
            }
        }
        progress = 100;
    }

    /**
     * This method is responsible to open the list of duplicate records when the
     * user press the button "INICIAR DETECCION".
     *
     * @return
     */
    public String openDuplicateSets() {
        return openDuplicateSets;
    }

    /**
     * This method is responsible for Show All Records that are part of a data
     * set, This method is called when the user presses the button "Mostrar
     * Datos".
     */
    public void selectTagClick() {

        if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-028") == 0) {
            FacesContext context = FacesContext.getCurrentInstance();
            recordSetsHomicideMB = (RecordSetsHomicideMB) context.getApplication().evaluateExpressionGet(context, "#{recordSetsHomicideMB}", RecordSetsHomicideMB.class);
            recordSetsHomicideMB.setInitialDateStr(formato.format(initialDateView));
            recordSetsHomicideMB.setEndDateStr(formato.format(endDateView));
            recordSetsHomicideMB.loadValues(selectedRowsDataTable);
            openRecordSets = "recordSetsHomicide";
            openDuplicateSets = "duplicateSetsHomicide";
        } else if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-029") == 0) {
            FacesContext context = FacesContext.getCurrentInstance();
            recordSetsTransitMB = (RecordSetsTransitMB) context.getApplication().evaluateExpressionGet(context, "#{recordSetsTransitMB}", RecordSetsTransitMB.class);
            recordSetsTransitMB.setInitialDateStr(formato.format(initialDateView));
            recordSetsTransitMB.setEndDateStr(formato.format(endDateView));
            recordSetsTransitMB.loadValues(selectedRowsDataTable);
            openRecordSets = "recordSetsTransit";
            openDuplicateSets = "duplicateSetsTransit";
        } else if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-030") == 0) {
            FacesContext context = FacesContext.getCurrentInstance();
            recordSetsSuicideMB = (RecordSetsSuicideMB) context.getApplication().evaluateExpressionGet(context, "#{recordSetsSuicideMB}", RecordSetsSuicideMB.class);
            recordSetsSuicideMB.setInitialDateStr(formato.format(initialDateView));
            recordSetsSuicideMB.setEndDateStr(formato.format(endDateView));
            recordSetsSuicideMB.loadValues(selectedRowsDataTable);
            openRecordSets = "recordSetsSuicide";
            openDuplicateSets = "duplicateSetsSuicide";
        } else if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-031") == 0) {
            FacesContext context = FacesContext.getCurrentInstance();
            recordSetsAccidentalMB = (RecordSetsAccidentalMB) context.getApplication().evaluateExpressionGet(context, "#{recordSetsAccidentalMB}", RecordSetsAccidentalMB.class);
            recordSetsAccidentalMB.setInitialDateStr(formato.format(initialDateView));
            recordSetsAccidentalMB.setEndDateStr(formato.format(endDateView));
            recordSetsAccidentalMB.loadValues(selectedRowsDataTable);
            openRecordSets = "recordSetsAccidental";
            openDuplicateSets = "duplicateSetsAccidental";
        } else if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-032") == 0) {
            FacesContext context = FacesContext.getCurrentInstance();
            recordSetsLcenfMB = (RecordSetsLcenfMB) context.getApplication().evaluateExpressionGet(context, "#{recordSetsLcenfMB}", RecordSetsLcenfMB.class);
            recordSetsLcenfMB.setInitialDateStr(formato.format(initialDateView));
            recordSetsLcenfMB.setEndDateStr(formato.format(endDateView));
            recordSetsLcenfMB.loadValues(selectedRowsDataTable);
            openRecordSets = "recordSetsLCENF";
            openDuplicateSets = "duplicateSetsLCENF";
        } else if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-033") == 0) {
            boolean enableControls = true;
            for (int i = 0; i < selectedRowsDataTable.length; i++) {
                if (selectedRowsDataTable[i].getColumn3().compareTo("SIVIGILA-VIF") == 0) {//es tipo sivigila
                    enableControls = false;//se encontro uno de tipo vif se carga tipo vif                    
                    break;
                }
            }
            FacesContext context = FacesContext.getCurrentInstance();
            recordSetsVifMB = (RecordSetsVifMB) context.getApplication().evaluateExpressionGet(context, "#{recordSetsVifMB}", RecordSetsVifMB.class);
            recordSetsVifMB.setInitialDateStr(formato.format(initialDateView));
            recordSetsVifMB.setEndDateStr(formato.format(endDateView));
            recordSetsVifMB.loadValues(selectedRowsDataTable);
            recordSetsVifMB.setRenderControls(enableControls);//permite o no ver en formulario y eliminar registros
            openRecordSets = "recordSetsVIF";
            openDuplicateSets = "duplicateSetsVIF";

        } else if (selectedRowsDataTable[0].getColumn3().compareTo("SIVIGILA-VIF") == 0) {
            boolean enableControls = true;
            for (int i = 0; i < selectedRowsDataTable.length; i++) {
                if (selectedRowsDataTable[i].getColumn3().compareTo("SCC-F-033") == 0) {//es tipo vif
                    enableControls = false;//se encontro uno de tipo vif se carga tipo vif
                    FacesContext context = FacesContext.getCurrentInstance();
                    recordSetsVifMB = (RecordSetsVifMB) context.getApplication().evaluateExpressionGet(context, "#{recordSetsVifMB}", RecordSetsVifMB.class);
                    recordSetsVifMB.setInitialDateStr(formato.format(initialDateView));
                    recordSetsVifMB.setEndDateStr(formato.format(endDateView));
                    recordSetsVifMB.loadValues(selectedRowsDataTable);
                    recordSetsVifMB.setRenderControls(enableControls);//no permite mostrar en formulario ni eliminar registros
                    openRecordSets = "recordSetsVIF";
                    openDuplicateSets = "duplicateSetsVIF";
                    break;
                }
            }
            if (enableControls) {//sino se encontro uno de vif se carga sivigila
                FacesContext context = FacesContext.getCurrentInstance();
                recordSetsSivigilaVifMB = (RecordSetsSivigilaVifMB) context.getApplication().evaluateExpressionGet(context, "#{recordSetsSivigilaVifMB}", RecordSetsSivigilaVifMB.class);
                recordSetsSivigilaVifMB.setInitialDateStr(formato.format(initialDateView));
                recordSetsSivigilaVifMB.setEndDateStr(formato.format(endDateView));
                recordSetsSivigilaVifMB.loadValues(selectedRowsDataTable);
                openRecordSets = "recordSetsSivigilaVif";
                openDuplicateSets = "duplicateSetsSivigilaVif";
            }
        } else {
            openRecordSets = null;
            openDuplicateSets = null;
            printMessage(FacesMessage.SEVERITY_WARN, "CONSULTA SIN DATOS", "CONSULTA SIN DATOS");
        }
        progress = 0;
    }

    /**
     * This method is responsible to display messages on the screen so the user
     * can see what is happening.
     *
     * @param s
     * @param title
     * @param messageStr
     */
    public void printMessage(FacesMessage.Severity s, String title, String messageStr) {
        FacesMessage msg2 = new FacesMessage(s, title, messageStr);
        FacesContext.getCurrentInstance().addMessage(null, msg2);
    }

    /**
     * This method is called when a row that contains a set of registers is
     * selected, this method enables or disables buttons of rename, delete,
     * display data, detect duplicates and group sets, this activation or
     * deactivation depends of which sets are selected in the table
     */
    public void load() {

        currentTag = null;
        String currentTagName = "";
        boolean equalTagName = true;
        boolean sivigilaAndVifTagName = true;
        btnEditDisabled = false;
        btnRemoveDisabled = false;
        btnViewDisabled = true;
        btnJoinDisabled = true;
        btnDuplicateDisabled = true;
        name = "";
        determineSizeData();
        if (selectedRowsDataTable != null) {
            //SI ALGUNO DE LOS SELECCIONADOS ES GENERAL NO REALIZAR ELIMINACION
            for (int i = 0; i < selectedRowsDataTable.length; i++) {//verificar si esta seleccionado uno de las cargas por defecto
                if (Integer.parseInt(selectedRowsDataTable[i].getColumn1()) < 8) {
                    btnRemoveDisabled = true;
                    btnEditDisabled = true;
                }
            }
            if (selectedRowsDataTable.length == 1) {//ES SOLO UNA CARGA
                btnDuplicateDisabled = false;//permitir detectar duplicados
                btnViewDisabled = false;//permitir mostrar datos                
                currentTag = tagsFacade.find(Integer.parseInt(selectedRowsDataTable[0].getColumn1()));
                editFormName = selectedRowsDataTable[0].getColumn3();//nombre cuando se edita
                editGroupName = selectedRowsDataTable[0].getColumn2();//ficha cuando se edita
            } else {//ES MAS DE UNA CARGA                
                //VERIFICAR SI SON DEL MISMO TIPO
                //btnEditDisabled = true;//no permitir edicion de nombre
                for (int i = 0; i < selectedRowsDataTable.length; i++) {//verificar si esta seleccionado uno de las cargas por defecto
                    if (currentTagName.length() == 0) {
                        currentTagName = selectedRowsDataTable[0].getColumn3();
                    } else if (currentTagName.compareTo(selectedRowsDataTable[i].getColumn3()) != 0) {
                        equalTagName = false;
                    }
                }
                if (equalTagName) {//SI SON DEL MISMO TIPO                                         
                    btnDuplicateDisabled = false;//permitir detectar duplicados
                    btnViewDisabled = false;//permitir mostrar datos           
                    btnJoinDisabled = false;//permitir unir conjuntos
                    btnViewDisabled = false;
                } else {//SI NO SON DEL MISMO TIPO VERIFICAR SI SON VIF O SIVIGILA LOS SELECCIONADOS
                    for (int i = 0; i < selectedRowsDataTable.length; i++) {//verificar si esta seleccionado uno de las cargas por defecto
                        if (selectedRowsDataTable[i].getColumn4().compareTo("SIVIGILA-VIF") != 0
                                && selectedRowsDataTable[i].getColumn4().compareTo("VIF") != 0) {
                            sivigilaAndVifTagName = false;
                        }
                    }
                    if (sivigilaAndVifTagName) {//los nombres son sivigila o vif
                        btnDuplicateDisabled = false;//permitir detectar duplicados
                        btnViewDisabled = false;//permitir mostrar datos           
                    } else {
                        btnDuplicateDisabled = true;//no permitir detectar duplicados
                        btnViewDisabled = true;//no permitir mostrar datos           
                    }
                }
            }
        }
    }

    /**
     * This method is responsible to delete a selected record from a dataset of
     * homicides
     *
     * @param currentTagRemove
     */
    private void removeMurder(Tags currentTagRemove) {
        /*
         * Eliminacion de registros que pertenecen a un conjunto de registros de homicidios
         */
        try {
            //FATAL INJURY MURDER
            connectionJdbcMB.non_query(""
                    + " DELETE FROM fatal_injury_murder where fatal_injury_id IN \n"
                    + "(select fatal_injury_id from fatal_injuries where victim_id IN \n"
                    + "(select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + "))");
            progressDelete = 30;
            //FATAL INJURY
            connectionJdbcMB.non_query(""
                    + " DELETE FROM fatal_injuries where victim_id IN \n"
                    + "(select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + ")");
            progressDelete = 60;
            //VICTIMS
            connectionJdbcMB.non_query(""
                    + " DELETE FROM victims where tag_id=" + currentTagRemove.getTagId() + "");
            progressDelete = 95;
        } catch (Exception e) {
        }
    }

    /**
     * This method is responsible to delete a selected record from a dataset of
     * accidents
     *
     * @param currentTagRemove
     */
    private void removeAccident(Tags currentTagRemove) {
        try {
            //FATAL INJURY ACCIDENT
            connectionJdbcMB.non_query(""
                    + " DELETE FROM fatal_injury_accident where fatal_injury_id IN \n"
                    + "(select fatal_injury_id from fatal_injuries where victim_id IN \n"
                    + "(select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + "))");
            progressDelete = 30;
            //FATAL INJURY
            connectionJdbcMB.non_query(""
                    + " DELETE FROM fatal_injuries where victim_id IN \n"
                    + "(select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + ")");
            progressDelete = 60;
            //VICTIMS
            connectionJdbcMB.non_query(""
                    + " DELETE FROM victims where tag_id=" + currentTagRemove.getTagId() + "");
            progressDelete = 95;
        } catch (Exception e) {
        }
    }

    /**
     * This method is responsible to delete a selected record from a dataset of
     * suicides
     *
     * @param currentTagRemove
     */
    private void removeSuicide(Tags currentTagRemove) {
        try {
            //FATAL INJURY SUICIDE
            connectionJdbcMB.non_query(""
                    + " DELETE FROM fatal_injury_suicide where fatal_injury_id IN \n"
                    + "(select fatal_injury_id from fatal_injuries where victim_id IN \n"
                    + "(select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + "))");
            progressDelete = 30;
            //FATAL INJURY
            connectionJdbcMB.non_query(""
                    + " DELETE FROM fatal_injuries where victim_id IN \n"
                    + "(select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + ")");
            progressDelete = 60;
            //VICTIMS
            connectionJdbcMB.non_query(""
                    + " DELETE FROM victims where tag_id=" + currentTagRemove.getTagId() + "");
            progressDelete = 95;
        } catch (Exception e) {
        }
    }

    /**
     * This method is responsible to delete a selected record from a dataset of
     * transit accidents
     *
     * @param currentTagRemove
     */
    private void removeTransit(Tags currentTagRemove) {
        try {
            //counterpart_involved_vehicle
            connectionJdbcMB.non_query(""
                    + " DELETE FROM counterpart_service_type where fatal_injury_id IN \n"
                    + "(select fatal_injury_id from fatal_injuries where victim_id IN \n"
                    + "(select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + "))");
            progressDelete = 10;
            //counterpart_involved_vehicle
            connectionJdbcMB.non_query(""
                    + " DELETE FROM counterpart_involved_vehicle where fatal_injury_id IN \n"
                    + "(select fatal_injury_id from fatal_injuries where victim_id IN \n"
                    + "(select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + "))");
            progressDelete = 20;

            //FATAL INJURY SUICIDE
            connectionJdbcMB.non_query(""
                    + " DELETE FROM fatal_injury_traffic where fatal_injury_id IN \n"
                    + "(select fatal_injury_id from fatal_injuries where victim_id IN \n"
                    + "(select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + "))");
            progressDelete = 30;
            //FATAL INJURY
            connectionJdbcMB.non_query(""
                    + " DELETE FROM fatal_injuries where victim_id IN \n"
                    + "(select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + ")");
            progressDelete = 60;

            //VICTIMS
            connectionJdbcMB.non_query(""
                    + " DELETE FROM victims where tag_id=" + currentTagRemove.getTagId() + "");
            progressDelete = 95;

        } catch (Exception e) {
        }
    }

    /**
     * This method is responsible to delete a selected record from a dataset of
     * LCENF
     *
     * @param currentTagRemove
     */
    private void removeLCENF(Tags currentTagRemove) {
        try {
            //OTHERS
            connectionJdbcMB.non_query(""
                    + " DELETE FROM others where victim_id IN "
                    + " (select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + ") ");
            progressDelete = 5;

            //victim_vulnerable_group
            connectionJdbcMB.non_query(""
                    + " DELETE FROM victim_vulnerable_group where victim_id IN "
                    + " (select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + ") ");
            progressDelete = 10;

            //domestic_violence_aggressor_type
            connectionJdbcMB.non_query(""
                    + " DELETE FROM domestic_violence_aggressor_type where non_fatal_injury_id IN "
                    + " (select non_fatal_injury_id from non_fatal_injuries where victim_id IN "
                    + " (select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + "))");
            progressDelete = 15;

            //domestic_violence_action_to_take
            connectionJdbcMB.non_query(""
                    + " DELETE FROM domestic_violence_action_to_take where non_fatal_injury_id IN "
                    + " (select non_fatal_injury_id from non_fatal_injuries where victim_id IN "
                    + " (select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + "))");
            progressDelete = 20;

            //domestic_violence_abuse_type
            connectionJdbcMB.non_query(""
                    + " DELETE FROM domestic_violence_abuse_type where non_fatal_injury_id IN "
                    + " (select non_fatal_injury_id from non_fatal_injuries where victim_id IN "
                    + " (select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + "))");
            progressDelete = 25;

            //domestic_violence_aggressor_type
            connectionJdbcMB.non_query(""
                    + " DELETE FROM domestic_violence_aggressor_type where non_fatal_injury_id IN "
                    + " (select non_fatal_injury_id from non_fatal_injuries where victim_id IN "
                    + " (select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + "))");
            progressDelete = 30;

            //non_fatal_domestic_violence
            connectionJdbcMB.non_query(""
                    + " DELETE FROM non_fatal_domestic_violence where non_fatal_injury_id IN "
                    + " (select non_fatal_injury_id from non_fatal_injuries where victim_id IN "
                    + " (select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + "))");
            progressDelete = 35;

            //non_fatal_anatomical_location
            connectionJdbcMB.non_query(""
                    + " DELETE FROM non_fatal_anatomical_location where non_fatal_injury_id IN "
                    + " (select non_fatal_injury_id from non_fatal_injuries where victim_id IN "
                    + " (select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + "))");
            progressDelete = 40;

            //non_fatal_diagnosis
            connectionJdbcMB.non_query(""
                    + " DELETE FROM non_fatal_diagnosis where non_fatal_injury_id IN "
                    + " (select non_fatal_injury_id from non_fatal_injuries where victim_id IN "
                    + " (select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + "))");
            progressDelete = 45;

            //non_fatal_interpersonal
            connectionJdbcMB.non_query(""
                    + " DELETE FROM non_fatal_interpersonal where non_fatal_injury_id IN "
                    + " (select non_fatal_injury_id from non_fatal_injuries where victim_id IN "
                    + " (select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + ")) ");
            progressDelete = 50;

            //non_fatal_kind_of_injury
            connectionJdbcMB.non_query(""
                    + " DELETE FROM non_fatal_kind_of_injury where non_fatal_injury_id IN "
                    + " (select non_fatal_injury_id from non_fatal_injuries where victim_id IN "
                    + " (select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + ")) ");
            progressDelete = 55;

            //non_fatal_self_inflicted
            connectionJdbcMB.non_query(""
                    + " DELETE FROM non_fatal_self_inflicted where non_fatal_injury_id IN "
                    + " (select non_fatal_injury_id from non_fatal_injuries where victim_id IN "
                    + " (select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + ")) ");
            progressDelete = 60;

            //non_fatal_transport_security_element
            connectionJdbcMB.non_query(""
                    + " DELETE FROM non_fatal_transport_security_element where non_fatal_injury_id IN "
                    + " (select non_fatal_injury_id from non_fatal_injuries where victim_id IN "
                    + " (select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + "))");
            progressDelete = 65;

            //non_fatal_transport
            connectionJdbcMB.non_query(""
                    + " DELETE FROM non_fatal_transport where non_fatal_injury_id IN "
                    + " (select non_fatal_injury_id from non_fatal_injuries where victim_id IN "
                    + " (select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + ")) ");
            progressDelete = 70;

            //non_fatal_transport_security_element
            connectionJdbcMB.non_query(""
                    + " DELETE FROM non_fatal_transport_security_element where non_fatal_injury_id IN "
                    + " (select non_fatal_injury_id from non_fatal_injuries where victim_id IN "
                    + " (select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + ")) ");
            progressDelete = 75;



            //NON FATAL INJURY
            connectionJdbcMB.non_query(""
                    + " DELETE FROM non_fatal_injuries where victim_id IN "
                    + " (select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + ") ");
            progressDelete = 80;
            //VICTIMS
            connectionJdbcMB.non_query(""
                    + " DELETE FROM victims where tag_id=" + currentTagRemove.getTagId() + " ");
            progressDelete = 95;
        } catch (Exception e) {
        }
    }

    /**
     * This method is responsible to delete a selected record from a dataset of
     * domestic violence
     *
     * @param currentTagRemove
     */
    private void removeVIF(Tags currentTagRemove) {

        //OTHERS
        connectionJdbcMB.non_query(""
                + " DELETE FROM others where victim_id IN "
                + " (select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + ") ");
        progressDelete = 10;

        //victim_vulnerable_group
        connectionJdbcMB.non_query(""
                + " DELETE FROM victim_vulnerable_group where victim_id IN "
                + " (select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + ") ");
        progressDelete = 15;

        //domestic_violence_aggressor_type
        connectionJdbcMB.non_query(""
                + " DELETE FROM domestic_violence_aggressor_type where non_fatal_injury_id IN "
                + " (select non_fatal_injury_id from non_fatal_injuries where victim_id IN "
                + " (select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + "))");
        progressDelete = 20;

        //domestic_violence_action_to_take
        connectionJdbcMB.non_query(""
                + " DELETE FROM domestic_violence_action_to_take where non_fatal_injury_id IN "
                + " (select non_fatal_injury_id from non_fatal_injuries where victim_id IN "
                + " (select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + "))");
        progressDelete = 25;

        //domestic_violence_abuse_type
        connectionJdbcMB.non_query(""
                + " DELETE FROM domestic_violence_abuse_type where non_fatal_injury_id IN "
                + " (select non_fatal_injury_id from non_fatal_injuries where victim_id IN "
                + " (select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + "))");
        progressDelete = 30;
        //non_fatal_domestic_violence
        connectionJdbcMB.non_query(""
                + " DELETE FROM non_fatal_domestic_violence where non_fatal_injury_id IN "
                + " (select non_fatal_injury_id from non_fatal_injuries where victim_id IN "
                + " (select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + "))");
        progressDelete = 35;

        //non_fatal_anatomical_location
        connectionJdbcMB.non_query(""
                + " DELETE FROM non_fatal_anatomical_location where non_fatal_injury_id IN "
                + " (select non_fatal_injury_id from non_fatal_injuries where victim_id IN "
                + " (select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + "))");
        progressDelete = 40;

        //non_fatal_kind_of_injury
        connectionJdbcMB.non_query(""
                + " DELETE FROM non_fatal_kind_of_injury where non_fatal_injury_id IN "
                + " (select non_fatal_injury_id from non_fatal_injuries where victim_id IN "
                + " (select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + ")) ");
        progressDelete = 45;

        //NON FATAL INJURY
        connectionJdbcMB.non_query(""
                + " DELETE FROM non_fatal_injuries where victim_id IN "
                + " (select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + ") ");
        progressDelete = 80;

        //VICTIMS
        connectionJdbcMB.non_query(""
                + " DELETE FROM victims where tag_id=" + currentTagRemove.getTagId() + " ");
        progressDelete = 95;
    }

    /**
     * This method is responsible to delete a selected record from a dataset of
     * Sivigila VIF
     *
     * @param currentTagRemove
     */
    private void removeSivigilaVif(Tags currentTagRemove) {
        //victim_vulnerable_group
        connectionJdbcMB.non_query(""
                + " DELETE FROM victim_vulnerable_group where victim_id IN "
                + " (select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + ") ");
        progressDelete = 10;

        //domestic_violence_aggressor_type
        connectionJdbcMB.non_query(""
                + " DELETE FROM domestic_violence_aggressor_type where non_fatal_injury_id IN "
                + " (select non_fatal_injury_id from non_fatal_injuries where victim_id IN "
                + " (select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + "))");
        progressDelete = 50;

        //domestic_violence_action_to_take
        connectionJdbcMB.non_query(""
                + " DELETE FROM domestic_violence_action_to_take where non_fatal_injury_id IN "
                + " (select non_fatal_injury_id from non_fatal_injuries where victim_id IN "
                + " (select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + "))");
        progressDelete = 60;

        //domestic_violence_abuse_type
        connectionJdbcMB.non_query(""
                + " DELETE FROM domestic_violence_abuse_type where non_fatal_injury_id IN "
                + " (select non_fatal_injury_id from non_fatal_injuries where victim_id IN "
                + " (select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + "))");
        progressDelete = 70;

        //sivigila_event_public_health
        connectionJdbcMB.non_query(""
                + " DELETE FROM sivigila_event_public_health where non_fatal_injury_id IN "
                + " (select non_fatal_injury_id from non_fatal_injuries where victim_id IN "
                + " (select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + "))");
        progressDelete = 10;

        //sivigila_event
        connectionJdbcMB.non_query(""
                + " DELETE FROM sivigila_event where non_fatal_injury_id IN "
                + " (select non_fatal_injury_id from non_fatal_injuries where victim_id IN "
                + " (select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + ")) ");
        progressDelete = 20;

        //sivigila_aggresor
        connectionJdbcMB.non_query(""
                + " DELETE FROM sivigila_aggresor where sivigila_agresor_id NOT IN "
                + " (select sivigila_agresor_id from sivigila_event) ");
        progressDelete = 30;

        //sivigila_victim
        connectionJdbcMB.non_query(""
                + " DELETE FROM sivigila_victim where sivigila_victim_id NOT IN "
                + " (select sivigila_victim_id from sivigila_event) ");
        progressDelete = 40;

        //non_fatal_domestic_violence
        connectionJdbcMB.non_query(""
                + " DELETE FROM non_fatal_domestic_violence where non_fatal_injury_id IN "
                + " (select non_fatal_injury_id from non_fatal_injuries where victim_id IN "
                + " (select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + "))");
        progressDelete = 45;

        //non_fatal_anatomical_location
        connectionJdbcMB.non_query(""
                + " DELETE FROM non_fatal_anatomical_location where non_fatal_injury_id IN "
                + " (select non_fatal_injury_id from non_fatal_injuries where victim_id IN "
                + " (select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + "))");
        progressDelete = 80;

        //NON FATAL INJURY
        connectionJdbcMB.non_query(""
                + " DELETE FROM non_fatal_injuries where victim_id IN "
                + " (select victim_id from victims where tag_id=" + currentTagRemove.getTagId() + ") ");
        progressDelete = 90;
        //VICTIMS
        connectionJdbcMB.non_query(""
                + " DELETE FROM victims where tag_id=" + currentTagRemove.getTagId() + " ");
        progressDelete = 95;
    }

    /**
     * This method is responsible to determine if the name already exists, if
     * not this method increases the digit 1,2,3 until this method find one that
     * does not exist.
     *
     * @param name
     * @return
     */
    private String determineTagName(String name) {
        /*
         * determina si el nombre ya existe sino aumentarle 1,2,3...
         * hasta que encuetre uno que no exista
         */
        String nameReturn = "";
        String tagName = name;
        int count = 0;
        boolean sameName;
        List<Tags> tagsList2 = tagsFacade.findAll();
        while (true) {
            sameName = false;
            for (int i = 0; i < tagsList2.size(); i++) {
                if (tagsList2.get(i).getTagName().compareTo(tagName) == 0) {
                    sameName = true;
                    break;
                }
            }
            if (!sameName) {//si el nombre no existe salir
                nameReturn = tagName;
                break;
            } else {
                count++;
                tagName = name + " " + count;
            }
        }
        return nameReturn;
    }

    /**
     * This method is responsible to ungroup a record set in the original record
     * set.
     */
    public void ungroupTags() {
        System.out.print("ENTRANDO EN DESAGRUPACION DE CONJUNTOS");
        boolean continueProcess = true;
        //EXISTAN FILAS SELECCIONADAS
        if (selectedRowsDataTable2 == null) {
            continueProcess = false;
            printMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar el conjunto a desagrupar");
        }
        if (continueProcess) {
            try {
                //crear un nuevo registro en tags
                String newTagName = determineTagName(selectedRowsDataTable2.getColumn5());
                connectionJdbcMB.non_query(""
                        + " INSERT INTO tags VALUES("
                        + selectedRowsDataTable2.getColumn4()
                        + ",'" + newTagName + "'"
                        + ",'' "
                        + ",'' "
                        + ",'" + selectedRowsDataTable2.getColumn6() + "')");
                //actualizar las victimas                
                connectionJdbcMB.update(
                        "victims",
                        "tag_id = " + selectedRowsDataTable2.getColumn4(),
                        "first_tag_id = " + selectedRowsDataTable2.getColumn4() + "");
                //el nombre del conjunto pudo haber cambiado
                connectionJdbcMB.update(
                        "ungrouped_tags",
                        "ungrouped_tag_name = '" + newTagName + "'",
                        "ungrouped_tag_id = " + selectedRowsDataTable2.getColumn4() + "");
                //el conjunto ya no esta agrupado por lo cual tiene igual current_tag_id y ungrouped_tag_id
                connectionJdbcMB.update(
                        "ungrouped_tags",
                        "current_tag_id = ungrouped_tag_id",
                        "ungrouped_tag_id = " + selectedRowsDataTable2.getColumn4() + "");
                printMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Los conjuntos: \""
                        + selectedRowsDataTable2.getColumn2()
                        + "\" y \"" + newTagName + "\" se han desagrupado");
            } catch (Exception e) {
                printMessage(FacesMessage.SEVERITY_ERROR, "Error", e.toString());
            }
            //totalProcess = 100;
            selectedRowsDataTable = null;
            selectedRowsDataTable2 = null;
            currentTag = null;
            createDynamicTable();
            createDynamicTable2();
        }
    }

    /**
     * This method allows the user to group a set in an existing set to maintain
     * order in the data set, to group sets the user needs to select at least 2
     * sets.
     */
    public void joinTags() {
        System.out.print("ENTRANDO EN UNION DE CONJUNTOS");
        currentTag = null;
        List<Tags> tagsListAux = new ArrayList<>();
        boolean continueProcess = true;

        if (selectedRowsDataTable == null) {//EXISTAN FILAS SELECCIONADAS
            continueProcess = false;
            printMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se deben seleccionar mínimo dos conjuntos a agrupar");
        }
        if (continueProcess && selectedRowsDataTable.length < 2) {//DEBE ESCOGER DOS CONJUNTOS
            continueProcess = false;
            printMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se deben seleccionar mínimo dos conjuntos a agrupar");
        }
        if (continueProcess) {//LOS CONJUNtOS SEAN DEL MISMO TIPO
            String typeTag = selectedRowsDataTable[0].getColumn3();
            for (int i = 0; i < selectedRowsDataTable.length; i++) {
                if (selectedRowsDataTable[0].getColumn3().compareTo(typeTag) != 0) {
                    continueProcess = false;
                    break;
                }
            }
            if (!continueProcess) {
                printMessage(FacesMessage.SEVERITY_ERROR, "Error", "Los conjuntos deben corresponder a un mimo tipo");
            }
        }
        if (continueProcess) {
            //CREO LA LISTA DE TAGS SELECCIONADOS
            tagsList = new ArrayList<>();
            for (int i = 0; i < selectedRowsDataTable.length; i++) {
                if (Integer.parseInt(selectedRowsDataTable[i].getColumn1()) < 8) {
                    //si es general lo coloco de primero
                    tagsListAux.add(tagsFacade.find(Integer.parseInt(selectedRowsDataTable[i].getColumn1())));
                    for (int j = 0; j < tagsList.size(); j++) {
                        tagsListAux.add(tagsList.get(j));
                    }
                    tagsList = tagsListAux;
                } else {
                    tagsList.add(tagsFacade.find(Integer.parseInt(selectedRowsDataTable[i].getColumn1())));
                }
            }
            //current tag sera la que permanezca, las otras se unen a current tag
            currentTag = tagsList.get(0);
            tagsList.remove(0);

            for (int i = 0; i < tagsList.size(); i++) {
                //Modifico tag_id de cada victima
                connectionJdbcMB.update(
                        "victims",
                        "tag_id = " + String.valueOf(currentTag.getTagId()) + "",
                        "tag_id = " + String.valueOf(tagsList.get(i).getTagId()) + "");
                //Modifico ungruped_tags.current_tag_id
                connectionJdbcMB.update(
                        "ungrouped_tags",
                        "current_tag_id = " + String.valueOf(currentTag.getTagId()) + "",
                        "current_tag_id = " + String.valueOf(tagsList.get(i).getTagId()) + "");
                //elimino de la tabla tags
                connectionJdbcMB.remove("tags", "tag_id = " + String.valueOf(tagsList.get(i).getTagId()) + "");
            }
            printMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Los conjuntos seleccionados fueron agrupados en uno solo");
            //totalProcess = 100;
            selectedRowsDataTable = null;
            selectedRowsDataTable2 = null;
            currentTag = null;
            createDynamicTable();
            createDynamicTable2();
        }
    }

    /**
     * This method allows the user to delete a selected set, this method only
     * allows eliminate sets that are not in the system.
     */
    public void deleteTag() {
        currentTag = null;
        System.out.print("ENTRANDO EN ELIMINAR CONJUNTO");
        if (selectedRowsDataTable != null) {
            //CREO LA LISTA DE TAGS SELECCIONADOS
            boolean defaultSetSelected = false;
            tagsList = new ArrayList<>();
            for (int i = 0; i < selectedRowsDataTable.length; i++) {
                if (Integer.parseInt(selectedRowsDataTable[i].getColumn1()) < 8) {
                    defaultSetSelected = true;
                } else {
                    tagsList.add(tagsFacade.find(Integer.parseInt(selectedRowsDataTable[i].getColumn1())));
                }
            }
            if (defaultSetSelected) {//SE SELECCIONO UN CONJUNTO POR DEFECTO DEL SISTEMA
                //totalProcess = 100;
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Se ha seleccionado un conjunto por defecto del sistema "
                        + "por lo cual no se puede realizar la eliminación");
            } else {
                for (int i = 0; i < tagsList.size(); i++) {
                    //ELIMINACION DE REGISTROS
                    if (tagsList.get(i).getFormId().getFormId().compareTo("SCC-F-028") == 0) {
                        removeMurder(tagsList.get(i));
                    } else if (tagsList.get(i).getFormId().getFormId().compareTo("SCC-F-029") == 0) {
                        removeTransit(tagsList.get(i));
                    } else if (tagsList.get(i).getFormId().getFormId().compareTo("SCC-F-030") == 0) {
                        removeSuicide(tagsList.get(i));
                    } else if (tagsList.get(i).getFormId().getFormId().compareTo("SCC-F-031") == 0) {
                        removeAccident(tagsList.get(i));
                    } else if (tagsList.get(i).getFormId().getFormId().compareTo("SCC-F-032") == 0) {
                        removeLCENF(tagsList.get(i));
                    } else if (tagsList.get(i).getFormId().getFormId().compareTo("SCC-F-033") == 0) {
                        removeVIF(tagsList.get(i));
                    } else if (tagsList.get(i).getFormId().getFormId().compareTo("SIVIGILA-VIF") == 0) {
                        removeSivigilaVif(tagsList.get(i));
                    }

                    connectionJdbcMB.remove("tags", "tag_id = " + tagsList.get(i).getTagId());
                    connectionJdbcMB.remove("ungrouped_tags", "ungrouped_tag_id = " + tagsList.get(i).getTagId());
                    connectionJdbcMB.remove("ungrouped_tags", "current_tag_id = " + tagsList.get(i).getTagId());
                }
                progressDelete = 100;
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Los conjuntos seleccionados fueron eliminados");
                //totalProcess = 100;
                btnEditDisabled = false;
                btnRemoveDisabled = false;
                selectedRowsDataTable = null;
                currentTag = null;
            }
        }
        createDynamicTable();
        createDynamicTable2();
        btnEditDisabled = true;
        btnRemoveDisabled = true;
    }

    /**
     * This method allows rename a selected set, for realize the modification,
     * the set must not be of the system.
     */
    public void updateRegistry() {


        if (selectedRowsDataTable != null) {
            if (editGroupName.trim().length() != 0) {
                try {
                    //deerminar si el nombre ingresado ya existe
                    ResultSet rs = connectionJdbcMB.consult("SELECT * FROM tags where tag_name LIKE '" + editGroupName.trim().toUpperCase() + "'");
                    if (rs.next()) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "NOMBRE EXISTE", "El nombre ingresado ya esta registrado");
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                    } else {
                        editGroupName = editGroupName.trim().toUpperCase();
                        connectionJdbcMB.update("tags", "tag_name = '" + editGroupName + "'", "tag_id = " + selectedRowsDataTable[0].getColumn1());
                        connectionJdbcMB.update("ungrouped_tags", "ungrouped_tag_name = '" + editGroupName + "'", "ungrouped_tag_id = " + selectedRowsDataTable[0].getColumn1());
                        editGroupName = "";
                        currentTag = null;
                        selectedRowsDataTable = null;
                        createDynamicTable();
                        createDynamicTable2();
                        btnEditDisabled = true;
                        btnRemoveDisabled = true;
                        btnViewDisabled = true;
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Registro actualizado");
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                    }

                } catch (Exception e) {
                }

            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "SIN NOMBRE", "Se debe digitar un nombre");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }

    /**
     * This method creates a new data set, for it is necessary to assign a name
     * and this name must be different to existing names.
     */
    public void saveRegistry() {
        int a = 0;
        a++;
        System.out.print("ENTRANDO EN NUEVO");
        if (groupName.length() != 0) {
            if (tagsFacade.findByName(groupName.toUpperCase()) == null) {
                int maxIdTag = tagsFacade.findMax() + 1;
                Tags newTag = new Tags(maxIdTag);
                newTag.setTagFileInput("-");
                newTag.setTagFileStored("-");
                newTag.setFormId(formsFacade.find(formName));
                newTag.setTagName(groupName);
                tagsFacade.create(newTag);
                createDynamicTable();
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Se ha registrado el nuevo conjunto");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "NOMBRE EXISTE", "El nombre ingresado ya esta registrado");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "SIN NOMBRE", "Se debe digitar un nombre");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    /**
     * This method is responsible to create the table where shows all datasets
     * existing in the database.
     */
    public final void createDynamicTable() {
        selectedRowsDataTable = null;
        btnEditDisabled = true;
        btnRemoveDisabled = true;
        btnViewDisabled = true;
        currentSearchValue = currentSearchValue.toUpperCase();
        rowDataTableList = new ArrayList<>();
        if (currentSearchValue.trim().length() == 0) {
            tagsList = tagsFacade.findCriteria(0, null);
        } else {
            tagsList = tagsFacade.findCriteria(currentSearchCriteria, currentSearchValue);
        }
        if (tagsList.isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "SIN DATOS", "No existen resultados para esta busqueda");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }

        for (int i = 0; i < tagsList.size(); i++) {
            rowDataTableList.add(new RowDataTable(
                    tagsList.get(i).getTagId().toString(),
                    tagsList.get(i).getTagName(),
                    tagsList.get(i).getFormId().getFormId(),
                    tagsList.get(i).getFormId().getFormName()));
        }
    }

    /**
     * This method is responsible to create the table where displays all groups
     * of sets.
     */
    public final void createDynamicTable2() {
        selectedRowsDataTable2 = null;
        currentSearchValue2 = currentSearchValue2.toUpperCase();
        rowDataTableList2 = new ArrayList<>();
        try {
            ResultSet rs = connectionJdbcMB.consult(""
                    + " SELECT \n"
                    + "    *,(SELECT ungrouped_tag_name FROM ungrouped_tags WHERE ungrouped_tag_id = ut.current_tag_id) as current_name \n"
                    + " FROM \n"
                    + "    ungrouped_tags as ut\n"
                    + " WHERE \n"
                    + "   ut.ungrouped_tag_id != ut.current_tag_id \n"
                    + " ORDER BY \n"
                    + "   ut.ungrouped_tag_date DESC");
            int count = 0;
            while (rs.next()) {
                count++;
                RowDataTable newRowDataTable = new RowDataTable();
                newRowDataTable.setColumn1(String.valueOf(count));
                newRowDataTable.setColumn2(rs.getString("current_name"));
                newRowDataTable.setColumn3(rs.getString("ungrouped_tag_date"));
                newRowDataTable.setColumn4(rs.getString("ungrouped_tag_id"));
                newRowDataTable.setColumn5(rs.getString("ungrouped_tag_name"));
                newRowDataTable.setColumn6(rs.getString("form_id"));
                if (currentSearchValue2 != null && currentSearchValue2.trim().length() != 0) {
                    if (currentSearchCriteria2 == 2) {
                        if (newRowDataTable.getColumn2().toUpperCase().indexOf(currentSearchValue2.toUpperCase()) != -1) {
                            rowDataTableList2.add(newRowDataTable);
                        }
                    } else {
                        if (newRowDataTable.getColumn5().toUpperCase().indexOf(currentSearchValue2.toUpperCase()) != -1) {
                            rowDataTableList2.add(newRowDataTable);
                        }
                    }
                } else {
                    rowDataTableList2.add(newRowDataTable);
                }
            }
        } catch (Exception e) {
        }
    }

    /**
     * This method Resets all controls and load the data of the tables of sets
     * and groups.
     */
    @PostConstruct
    public void reset() {

        determineSizeData();
        List<Forms> formsList = formsFacade.findAll();
        forms = new SelectItem[formsList.size()];
        for (int i = 0; i < formsList.size(); i++) {
            forms[i] = new SelectItem(formsList.get(i).getFormId(), formsList.get(i).getFormName());
        }
        createDynamicTable();
        createDynamicTable2();
    }

    public List<RowDataTable> getRowDataTableList() {
        return rowDataTableList;
    }

    public void setRowDataTableList(List<RowDataTable> rowDataTableList) {
        this.rowDataTableList = rowDataTableList;
    }

    public List<RowDataTable> getRowDataTableList2() {
        return rowDataTableList2;
    }

    public void setRowDataTableList2(List<RowDataTable> rowDataTableList2) {
        this.rowDataTableList2 = rowDataTableList2;
    }

    public RowDataTable[] getSelectedRowsDataTable() {
        return selectedRowsDataTable;
    }

    public void setSelectedRowsDataTable(RowDataTable[] selectedRowsDataTable) {
        this.selectedRowsDataTable = selectedRowsDataTable;
    }

    public RowDataTable getSelectedRowsDataTable2() {
        return selectedRowsDataTable2;
    }

    public void setSelectedRowsDataTable2(RowDataTable selectedRowsDataTable2) {
        this.selectedRowsDataTable2 = selectedRowsDataTable2;
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

    public boolean isBtnRemoveDisabled() {
        return btnRemoveDisabled;
    }

    public void setBtnRemoveDisabled(boolean btnRemoveDisabled) {
        this.btnRemoveDisabled = btnRemoveDisabled;
    }

    public boolean isBtnViewDisabled() {
        return btnViewDisabled;
    }

    public void setBtnViewDisabled(boolean btnViewDisabled) {
        this.btnViewDisabled = btnViewDisabled;
    }

    public String getOpenRecordSets() {
        return openRecordSets;
    }

    public void setOpenRecordSets(String openRecordSets) {
        this.openRecordSets = openRecordSets;
    }

    public String getOpenDuplicateSets() {
        return openDuplicateSets;
    }

    public void setOpenDuplicateSets(String openDuplicateSets) {
        this.openDuplicateSets = openDuplicateSets;
    }

    public Short getCurrentForm() {
        return currentForm;
    }

    public SelectItem[] getForms() {
        return forms;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getEditFormName() {
        return editFormName;
    }

    public void setEditFormName(String editFormName) {
        this.editFormName = editFormName;
    }

    public String getEditGroupName() {
        return editGroupName;
    }

    public void setEditGroupName(String editGroupName) {
        this.editGroupName = editGroupName;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getProgressSplit() {
        return progressSplit;
    }

    public void setProgressSplit(int progressSplit) {
        this.progressSplit = progressSplit;
    }

    public int getProgressDelete() {
        return progressDelete;
    }

    public void setProgressDelete(int progressDelete) {
        this.progressDelete = progressDelete;
    }

    public Date getEndDateView() {
        return endDateView;
    }

    public void setEndDateView(Date endDateView) {
        this.endDateView = endDateView;
    }

    public Date getInitialDateView() {
        return initialDateView;
    }

    public void setInitialDateView(Date initialDateView) {
        this.initialDateView = initialDateView;
    }

    public Date getInitialDateDuplicate() {
        return initialDateDuplicate;
    }

    public void setInitialDateDuplicate(Date initialDateDuplicate) {
        this.initialDateDuplicate = initialDateDuplicate;
    }

    public Date getEndDateDuplicate() {
        return endDateDuplicate;
    }

    public void setEndDateDuplicate(Date endDateDuplicate) {
        this.endDateDuplicate = endDateDuplicate;
    }

    public boolean isBtnDuplicateDisabled() {
        return btnDuplicateDisabled;
    }

    public void setBtnDuplicateDisabled(boolean btnDuplicateDisabled) {
        this.btnDuplicateDisabled = btnDuplicateDisabled;
    }

    public boolean isBtnJoinDisabled() {
        return btnJoinDisabled;
    }

    public void setBtnJoinDisabled(boolean btnJoinDisabled) {
        this.btnJoinDisabled = btnJoinDisabled;
    }

    public int getCurrentSearchCriteria2() {
        return currentSearchCriteria2;
    }

    public void setCurrentSearchCriteria2(int currentSearchCriteria2) {
        this.currentSearchCriteria2 = currentSearchCriteria2;
    }

    public String getCurrentSearchValue2() {
        return currentSearchValue2;
    }

    public void setCurrentSearchValue2(String currentSearchValue2) {
        this.currentSearchValue2 = currentSearchValue2;
    }

    public String getSizeData() {
        return sizeData;
    }

    public void setSizeData(String sizeData) {
        this.sizeData = sizeData;
    }
}
