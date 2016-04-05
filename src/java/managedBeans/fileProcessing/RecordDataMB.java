/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.fileProcessing;

import beans.connection.ConnectionJdbcMB;
import beans.enumerators.*;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import managedBeans.login.ApplicationControlMB;
import managedBeans.login.LoginMB;
import model.dao.*;
import model.pojo.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * The RecordDataMB class is responsible for managing everything related to the
 * progress bar data processing and validation.
 *
 * @author santos
 */
@ManagedBean(name = "recordDataMB")
@SessionScoped
public class RecordDataMB implements Serializable {

    @EJB
    FormsFacade formsFacade;
    @EJB
    FieldsFacade fieldsFacade;
    @EJB
    VulnerableGroupsFacade vulnerableGroupsFacade;
    @EJB
    PlacesFacade placesFacade;
    @EJB
    ActivitiesFacade activitiesFacade;
    @EJB
    MurderContextsFacade murderContextsFacade;
    @EJB
    IdTypesFacade idTypesFacade;
    @EJB
    TransportUsersFacade transportUsersFacade;
    @EJB
    NeighborhoodsFacade neighborhoodsFacade;
    @EJB
    GendersFacade gendersFacade;
    @EJB
    TransportTypesFacade transportTypesFacade;
    @EJB
    PrecipitatingFactorsFacade precipitatingFactorsFacade;
    @EJB
    AggressorGendersFacade aggressorGendersFacade;
    @EJB
    VictimCharacteristicsFacade victimCharacteristicsFacade;
    @EJB
    NonFatalPlacesFacade nonFatalPlacesFacade;
    @EJB
    UseAlcoholDrugsFacade useAlcoholDrugsFacade;
    @EJB
    IntentionalitiesFacade intentionalitiesFacade;
    @EJB
    AreasFacade areasFacade;
    @EJB
    DiagnosesFacade diagnosesFacade;
    @EJB
    TransportCounterpartsFacade transportCounterpartsFacade;
    @EJB
    JobsFacade jobsFacade;
    @EJB
    RelatedEventsFacade relatedEventsFacade;
    @EJB
    InvolvedVehiclesFacade involvedVehiclesFacade;
    @EJB
    DaysFacade daysFacade;
    @EJB
    AmpmFacade ampmFacade;
    @EJB
    WeaponTypesFacade weaponTypesFacade;
    @EJB
    AccidentClassesFacade accidentClassesFacade;
    @EJB
    Boolean3Facade boolean3Facade;
    @EJB
    Boolean2Facade boolean2Facade;
    @EJB
    RelationshipsToVictimFacade relationshipsToVictimFacade;
    @EJB
    DestinationsOfPatientFacade destinationsOfPatientFacade;
    @EJB
    AlcoholLevelsFacade alcoholLevelsFacade;
    @EJB
    ServiceTypesFacade serviceTypesFacade;
    @EJB
    EthnicGroupsFacade ethnicGroupsFacade;
    @EJB
    AgeTypesFacade ageTypesFacade;
    @EJB
    ProtectiveMeasuresFacade protectiveMeasuresFacade;
    @EJB
    MechanismsFacade mechanismsFacade;
    @EJB
    ContextsFacade contextsFacade;
    @EJB
    RoadTypesFacade roadTypesFacade;
    @EJB
    RelationGroupFacade relationGroupFacade;
    @EJB
    RelationVariablesFacade relationVariablesFacade;
    @EJB
    RelationValuesFacade relationValuesFacade;
    @EJB
    FatalInjuryAccidentFacade fatalInjuryAccidentFacade;
    @EJB
    FatalInjuryMurderFacade fatalInjuryMurderFacade;
    @EJB
    FatalInjuryTrafficFacade fatalInjuryTrafficFacade;
    @EJB
    FatalInjurySuicideFacade fatalInjurySuicideFacade;
    @EJB
    FatalInjuriesFacade fatalInjuriesFacade;
    @EJB
    SivigilaEventFacade sivigilaEventFacade;
    @EJB
    SivigilaVictimFacade sivigilaVictimFacade;
    @EJB
    SivigilaAggresorFacade sivigilaAggresorFacade;
    @EJB
    NonFatalInjuriesFacade nonFatalInjuriesFacade;
    @EJB
    NonFatalDomesticViolenceFacade nonFatalDomesticViolenceFacade;
    @EJB
    NonFatalInterpersonalFacade nonFatalInterpersonalFacade;
    @EJB
    NonFatalSelfInflictedFacade nonFatalSelfInflictedFacade;
    @EJB
    NonFatalTransportFacade nonFatalTransportFacade;
    @EJB
    VictimsFacade victimsFacade;
    @EJB
    InjuriesFacade injuriesFacade;
    @EJB
    SecurityElementsFacade securityElementsFacade;
    @EJB
    AbuseTypesFacade abuseTypesFacade;
    @EJB
    AggressorTypesFacade aggressorTypesFacade;
    @EJB
    AnatomicalLocationsFacade anatomicalLocationsFacade;
    @EJB
    KindsOfInjuryFacade kindsOfInjuryFacade;
    @EJB
    NonFatalDataSourcesFacade nonFatalDataSourcesFacade;
    @EJB
    SivigilaTipSsFacade sivigilaTipSsFacade;
    @EJB
    InsuranceFacade insuranceFacade;
    @EJB
    TagsFacade tagsFacade;
    @EJB
    UngroupedTagsFacade ungroupedTagsFacade;
    @EJB
    HealthProfessionalsFacade healthProfessionalsFacade;
    @EJB
    UsersFacade usersFacade;
    @EJB
    ActionsToTakeFacade actionsToTakeFacade;
    @EJB
    CounterpartInvolvedVehicleFacade counterpartInvolvedVehicleFacade;
    @EJB
    CounterpartServiceTypeFacade counterpartServiceTypeFacade;
    @EJB
    SuicideMechanismsFacade suicideMechanismsFacade;
    @EJB
    AccidentMechanismsFacade accidentMechanismsFacade;
    @EJB
    GenNnFacade genNnFacade;
    @EJB
    ProjectsFacade projectsFacade;
    @EJB
    SivigilaEducationalLevelFacade sivigilaEducationalLevelFacade;
    @EJB
    SivigilaNoRelativeFacade sivigilaNoRelativeFacade;
    @EJB
    SivigilaGroupFacade sivigilaGroupFacade;
    @EJB
    SivigilaVulnerabilityFacade sivigilaVulnerabilityFacade;
    @EJB
    SivigilaMechanismFacade sivigilaMechanismFacade;
    @EJB
    SivigilaOtherMechanismFacade sivigilaOtherMechanismFacade;
    private RelationGroup currentRelationsGroup;
    private ProjectsMB projectsMB;
    private LoginMB loginMB;
    private ErrorsControlMB errorsControlMB;
    private ArrayList<String> columnsNames;
    private String nameForm = "";
    private RelationVariables relationVar;
    private int tuplesNumber;
    private int tuplesProcessed;
    private boolean btnRegisterDataDisabled = true;
    private FatalInjuryMurder newFatalInjuryMurder;
    private FatalInjurySuicide newFatalInjurySuicide;
    private FatalInjuryTraffic newFatalInjuryTraffic;
    private NonFatalTransport newNonFatalTransport;
    private NonFatalInterpersonal newNonFatalInterpersonal;
    private NonFatalSelfInflicted newNonFatalSelfInflicted;
    CounterpartInvolvedVehicle newCounterpartInvolvedVehicle;
    CounterpartServiceType newCounterpartServiceType;
    List<CounterpartServiceType> serviceTypesList;
    List<CounterpartInvolvedVehicle> involvedVehiclesList;
    private String lastTagNameCreated = "";//ultimo conjunto de registros creado
    private Others newOther;
    private String value = "";
    private String registryData = "";
    private String name = "";
    private String surname = "";
    private String intencionality = "";
    private String fechaev = "";//fecha
    private String fechacon = "";//fecha
    private String dia = "";//dia evento
    private String mes = "";//mes evento
    private String ao = "";//año del evento
    private String sql = "";
    private String dia1 = "";//dia de la consulta
    private String mes1 = "";//mes de la consulta
    private String ao1 = "";//año de la consulta
    private String horas = "";//hora evento
    private String minutos = "";//minuto evento
    private String ampm = "";//ampm evento
    private String horas1 = "";//hora consulta
    private String minutos1 = "";//minuto consulta
    private String ampm1 = "";//ampm consulta                
    private String narrative = "";//narracion
    private String errorOnComplete = "";//errorAlRealizar carga de registros
    private int hourInt = 0;
    private int minuteInt = 0;
    private Date n;
    boolean booleanValue;
    private String[] splitArray;
    private ResultSet resultSetFileData;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Date currentDate;
    private Tags newTag;
    private UngroupedTags newUngroupedTags;
    String fieldType = "";
    private ApplicationControlMB applicationControlMB;
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //MANEJO E LA BARRA DE PROGRESO DEL ALMACENAMIENTO ---------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    private Integer progress;
    private Integer progressValidate;//
    private int errorsNumber = 0;
    private int currentSource = 0;
    boolean continueProcces = false;

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    /**
     * This method shows the progress bar records when performing data
     * processing.
     */
    public void onComplete() {
        if (errorOnComplete.length() == 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha realizado la adición de " + String.valueOf(tuplesProcessed)
                    + " registros en el conjunto de registros: \" " + lastTagNameCreated + " \""));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", errorOnComplete));
        }
    }

    /**
     * This method is responsible for displaying the progress bar of the data
     * validation. The data validation is done in order that these are correct,
     * using this option to indicate errors and alerts determine its correction.
     */
    public void onCompleteValidate() {
        progressValidate = 100;
        if (errorsNumber != 0) {
            btnRegisterDataDisabled = true;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Errores", "Existen: " + String.valueOf(errorsNumber) + " valores que no superaron el proceso de validación, dirijase a la sección de errores para su corrección"));
        } else {
            btnRegisterDataDisabled = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha superado el proceso de validacion, presione el boton registrar datos para que sean almacenados."));
        }
    }

    /**
     * canceled the process of the progress bars of processing and validation.
     */
    public void cancel() {
        progress = null;
        progressValidate = null;
    }
    ConnectionJdbcMB connectionJdbcMB;

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES DE PROPOSITO GENERAL ---------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * allows connection to the database and performs validation of user
     * permissions.
     */
    public RecordDataMB() {
        loginMB = (LoginMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{loginMB}", LoginMB.class);
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
        applicationControlMB = (ApplicationControlMB) FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get("applicationControlMB");
    }

    /**
     * recharge form with initial values.
     */
    public void reset() {
        /*
         * Cargar el formulario con los valores iniciales
         */
        progressValidate = 0;
        btnRegisterDataDisabled = true;
        //btnValidateDisabled = true;
    }

    /**
     * can handle related with the relationships that must exist obligatorily
     * for intentionality, identification and date fact.
     *
     * @return
     */
    private boolean relationshipsRequired() {
        /*
         * deben existir obligatoriamente relaciones para
         * intencionalidad, identificacion y fecha_hecho 
         */
        boolean noErrors = true;
        currentRelationsGroup = relationGroupFacade.find(projectsMB.getCurrentRelationsGroupId());//tomo el grupos de relaciones de valores y de variables        
        switch (FormsEnum.convert(nameForm.replace("-", "_"))) {//tipo de relacion
            case SCC_F_032:
                if (currentRelationsGroup.findRelationVarByNameExpected("intencionalidad") == null) {
                    noErrors = false;
                    errorsNumber++;
                    errorsControlMB.addError(errorsNumber, null, "Falta relación", "No existe manera de determinar la intencionalidad");
                }
            case SCC_F_028:
            case SCC_F_029:
            case SCC_F_030:
            case SCC_F_031:
            case SCC_F_033:
            case SIVIGILA_VIF:
                if (currentRelationsGroup.findRelationVarByNameExpected("fecha_evento") == null) {
                    if (currentRelationsGroup.findRelationVarByNameExpected("dia_evento") == null) {
                        noErrors = false;
                    }
                    if (currentRelationsGroup.findRelationVarByNameExpected("mes_evento") == null) {
                        noErrors = false;
                    }
                    if (currentRelationsGroup.findRelationVarByNameExpected("año_evento") == null) {
                        noErrors = false;
                    }
                }
                if (noErrors == false) {//no se puede determinar la fecha_evento
                    errorsNumber++;
                    errorsControlMB.addError(errorsNumber, null, "Falta relación", "No existe una relación de variables que determine la fecha del evento");
                }
                if (currentRelationsGroup.findRelationVarByNameExpected("numero_identificacion_victima") == null
                        && currentRelationsGroup.findRelationVarByNameExpected("numero_de_identificacion") == null) {
                    noErrors = false;
                    errorsNumber++;
                    errorsControlMB.addError(errorsNumber, null, "Falta relación", "No existe una relación de variables que determine la identificación de la victima");

                }
                break;
        }
        return noErrors;
    }

    /**
     * It is responsible for determining if a name already exists otherwise it
     * should assign 1,2,3, ... and so on until you find one that does not exist
     *
     * @param name: name to determine
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
        List<Tags> tagsList = tagsFacade.findAll();
        while (true) {
            sameName = false;
            for (int i = 0; i < tagsList.size(); i++) {
                if (tagsList.get(i).getTagName().compareTo(tagName) == 0) {
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
     * This method is responsible for determining the registration process.
     *
     * @return
     */
    private ResultSet determineRecords() {
        ResultSet rsReturn = connectionJdbcMB.consult(""
                + " SELECT "
                + "    project_records.project_id, "
                + "    project_records.record_id, "
                + "    array_agg(project_columns.column_name || '<=>' || project_records.data_value) "
                + " FROM "
                + "    project_records,project_columns "
                + " WHERE "
                + "    project_records.project_id = " + projectsMB.getCurrentProjectId() + " AND "
                + "    project_columns.column_id = project_records.column_id "
                + " GROUP BY "
                + "    project_records.project_id, "
                + "    project_records.record_id ");
        return rsReturn;
    }

    /**
     * It is responsible for determining the name of the column with the data to
     * be processed.
     *
     * @return
     */
    private ArrayList<String> determineColumnNames() {
        ArrayList<String> listReturn = new ArrayList<>();
        try {
            sql = ""
                    + " SELECT     "
                    + "	   project_columns.column_name"
                    + " FROM     "
                    + "	   public.project_records,     "
                    + "	   public.project_columns "
                    + " WHERE     "
                    + "	   project_columns.column_id = project_records.column_id AND "
                    + "	   project_records.project_id = " + projectsMB.getCurrentProjectId() + " "
                    + " GROUP BY"
                    + "	   project_columns.column_name,"
                    + "	   project_columns.column_id"
                    + " ORDER BY "
                    + "	   project_columns.column_id";
            ResultSet rs = connectionJdbcMB.consult(sql);            //System.out.println("A002\n" + sql);
            while (rs.next()) {
                listReturn.add(rs.getString(1));
            }
        } catch (Exception e) {
            System.out.println("Error 1 en " + this.getClass().getName() + ":" + e.toString());
        }
        return listReturn;

    }

    /**
     * makes a query to determine the number of tuples.
     *
     * @return
     */
    private int determineTuplesNumber() {
        int intReturn = 0;
        try {
            sql = ""
                    + " SELECT "
                    + "    count (distinct record_id)  "
                    + " FROM "
                    + "    public.project_records, "
                    + "    public.projects "
                    + " WHERE "
                    + "    project_records.project_id = projects.project_id AND "
                    + "    projects.project_id = " + projectsMB.getCurrentProjectId() + "";
            resultSetFileData = connectionJdbcMB.consult(sql);
            resultSetFileData.next();
            intReturn = resultSetFileData.getInt(1);
        } catch (Exception e) {
            System.out.println("Error 2 en " + this.getClass().getName() + ":" + e.toString());
        }

        return intReturn;
    }

    /**
     * It is responsible for determining the data record, in order to obtain a
     * value of a given tuple a column.
     *
     * @param arrayInJava
     * @param columnName
     * @return
     */
    private String determineRegistryData(Object[] arrayInJava, String columnName) {
        /*
         * obtener un valor de una tupla dada una columna
         */
        String returnValue = null;
        try {
            for (int i = 0; i < arrayInJava.length; i++) {
                String splitElement[] = arrayInJava[i].toString().split("<=>");
                if (columnName.compareTo(splitElement[0]) == 0) {
                    returnValue = splitElement[1];
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error 3 en " + this.getClass().getName() + ":" + e.toString());
        }
        return returnValue;
    }

    /**
     * This method it is responsible eliminate or remove the expression "_v" of
     * a data type, so as to take the categorical table.
     *
     * @param field_type
     * @return
     */
    private String remove_v(String field_type) {
        /*
         * remueve '_v' de un tipo de dato (para que tome la tabla categorica)
         */
        String strReturn;
        strReturn = field_type.substring(field_type.length() - 2, field_type.length());
        if (strReturn.compareTo("_v") == 0) {
            strReturn = field_type.substring(0, field_type.length() - 2);
        } else {
            strReturn = field_type;
        }
        return strReturn;
    }

    /**
     * this button (start validation) is responsible for conducting validations,
     * here is generated the errors that occur when analyzing the file.
     */
    public void btnValidateClick() {
        /*
         * click sobre el boton iniciar validacion aqui se generaran los errores
         * que salgan de analizar el archivo
         */
        columnsNames = new ArrayList<>();
        progressValidate = 0;
        continueProcces = true;
        nameForm = projectsMB.getCurrentFormId();
        currentRelationsGroup = relationGroupFacade.find(projectsMB.getCurrentRelationsGroupId());//tomo el grupos_vulnerables de relaciones de valores y de variables
        errorsNumber = 0;
        tuplesProcessed = 0;
        errorsControlMB.reset();
        if (!relationshipsRequired()) {//validar relaciones obligatorias
            continueProcces = false;
            progressValidate = 100;
        }
        if (currentRelationsGroup == null) {
            continueProcces = false;
            progressValidate = 100;
        }
        if (continueProcces) {
            try {
                tuplesNumber = determineTuplesNumber();//determino numero de tuplas  
                columnsNames = determineColumnNames();//determino nombres de columnas
                resultSetFileData = determineRecords();//resulset con los registros a procesar
                while (resultSetFileData.next()) {//recorro cada uno de los registros de la tabla temp                                                
                    //VARIABLES PARA SABER SI ES POSIBLE DETERMINAR LA INTENCIONALIDAD, Y FECHA DEL EVENTO
                    fechaev = "";
                    dia = "";
                    mes = "";
                    ao = "";
                    fechacon = "";
                    dia1 = "";
                    mes1 = "";
                    ao1 = "";
                    intencionality = "";
                    String mechanism = "";
                    String traficValues = "";
                    String interpersonalValues = "";
                    String selftInflictedValues = "";
                    String domesticViolenceValues = "";
                    for (int i = 0; i < columnsNames.size(); i++) {//recorro cada una de las columnas de cada registro                                            
                        registryData = determineRegistryData((Object[]) resultSetFileData.getArray(3).getArray(), columnsNames.get(i));
                        relationVar = currentRelationsGroup.findRelationVarByNameFound(columnsNames.get(i));//determino la relacion de variables
                        value = null;
                        if (relationVar != null && registryData != null) {
                            fieldType = remove_v(relationVar.getFieldType());
                            switch (DataTypeEnum.convert(fieldType)) {//tipo de relacion
                                case text:
                                    break;
                                case integer:
                                    value = isNumeric(registryData);
                                    if (value == null) {
                                        errorsNumber++;//error = "No es entero";
                                        errorsControlMB.addError(errorsNumber, relationVar, registryData, resultSetFileData.getString("record_id"));
                                    }
                                    break;
                                case age:
                                    value = isAge(registryData);
                                    if (value == null) {
                                        errorsNumber++;//error = "dia_evento no corresponde al formato";
                                        errorsControlMB.addError(errorsNumber, relationVar, registryData, resultSetFileData.getString("record_id"));
                                    }
                                    break;
                                case date:
                                    value = isDate(registryData, relationVar.getDateFormat());
                                    if (value == null) {
                                        errorsNumber++;//error = "dia_evento no corresponde al formato";
                                        errorsControlMB.addError(errorsNumber, relationVar, registryData, resultSetFileData.getString("record_id"));
                                    } else {
                                        if (relationVar.getNameExpected().compareTo("fecha_evento") == 0) {
                                            fechaev = value;
                                            if (!validYear(registryData, relationVar.getDateFormat())) {
                                                errorsNumber++;//error = "dia_evento no corresponde al formato";                                                
                                                errorsControlMB.addError(errorsNumber, relationVar, registryData, resultSetFileData.getString("record_id"));
                                            }
                                        }
                                        if (relationVar.getNameExpected().compareTo("fecha_consulta") == 0) {
                                            fechacon = value;
                                            if (!validYear(registryData, relationVar.getDateFormat())) {
                                                errorsNumber++;//error = "dia_evento no corresponde al formato";
                                                errorsControlMB.addError(errorsNumber, relationVar, registryData, resultSetFileData.getString("record_id"));
                                                //errorsControlMB.addError(errorsNumber, null, "Fecha de consulta inválida", "La fecha: " + fechaev + " debe estar dentro del rango del 2002 hasta: " + String.valueOf(new Date().getYear() + 1901));
                                            }
                                        }
                                    }
                                    break;
                                case date_of_birth:
                                    if (!validDateOfBirth(registryData, relationVar.getDateFormat())) {
                                        errorsNumber++;//error = "dia_evento no corresponde al formato";
                                        errorsControlMB.addError(errorsNumber, relationVar, registryData, resultSetFileData.getString("record_id"));
                                        //errorsControlMB.addError(errorsNumber, null, "Fecha de consulta inválida", "La fecha: " + fechaev + " debe estar dentro del rango del 2002 hasta: " + String.valueOf(new Date().getYear() + 1901));
                                    }

                                    break;
                                case military:
                                    value = isMilitary(registryData);
                                    if (value == null) {
                                        errorsNumber++;//la hora_evento militar no puede ser determinada
                                        errorsControlMB.addError(errorsNumber, relationVar, registryData, resultSetFileData.getString("record_id"));
                                    }
                                    break;
                                case hour:
                                    value = isHour(registryData);
                                    if (value == null) {
                                        errorsNumber++;//la hora_evento militar no puede ser determinada
                                        errorsControlMB.addError(errorsNumber, relationVar, registryData, resultSetFileData.getString("record_id"));
                                    }
                                    break;
                                case minute:
                                    value = isMinute(registryData);
                                    if (value == null) {
                                        errorsNumber++;//la hora_evento militar no puede ser determinada
                                        errorsControlMB.addError(errorsNumber, relationVar, registryData, resultSetFileData.getString("record_id"));
                                    }
                                    break;
                                case day:
                                    value = isDay(registryData);
                                    if (relationVar.getNameExpected().compareTo("dia_evento") == 0) {
                                        dia = value;
                                    }
                                    if (relationVar.getNameExpected().compareTo("dia_consulta") == 0) {
                                        dia1 = value;
                                    }
                                    if (value == null) {
                                        errorsNumber++;//la hora_evento militar no puede ser determinada
                                        errorsControlMB.addError(errorsNumber, relationVar, registryData, resultSetFileData.getString("record_id"));
                                    }
                                    break;
                                case month:
                                    value = isMonth(registryData);
                                    if (relationVar.getNameExpected().compareTo("mes_evento") == 0) {
                                        mes = value;
                                    }
                                    if (relationVar.getNameExpected().compareTo("mes_consulta") == 0) {
                                        mes1 = value;
                                    }
                                    if (value == null) {
                                        errorsNumber++;//la hora_evento militar no puede ser determinada
                                        errorsControlMB.addError(errorsNumber, relationVar, registryData, resultSetFileData.getString("record_id"));
                                    }
                                    break;
                                case year:
                                    value = isYear(registryData);
                                    if (relationVar.getNameExpected().compareTo("año_evento") == 0) {
                                        ao = value;
                                    }
                                    if (relationVar.getNameExpected().compareTo("año_consulta") == 0) {
                                        ao1 = value;
                                    }
                                    if (value == null) {
                                        errorsNumber++;//la hora_evento militar no puede ser determinada
                                        errorsControlMB.addError(errorsNumber, relationVar, registryData, resultSetFileData.getString("record_id"));
                                    }
                                    break;
                                case percentage:
                                    value = isPercentage(registryData);
                                    if (value == null) {
                                        errorsNumber++;//el porcentaje no puede ser determinado
                                        errorsControlMB.addError(errorsNumber, relationVar, registryData, resultSetFileData.getString("record_id"));
                                    }
                                    break;
                                case NOVALUE:
                                    value = isCategorical(registryData, relationVar);
                                    if (relationVar.getNameExpected().compareTo("intencionalidad") == 0) {
                                        intencionality = value;
                                    }
                                    if (value == null) {
                                        errorsNumber++;//error = "no esta en la categoria ni es un valor descartado";
                                        errorsControlMB.addError(errorsNumber, relationVar, registryData, resultSetFileData.getString("record_id"));
                                    }
                                    break;
                            }
                        }

                        if (nameForm.replace("-", "_").compareTo("SCC_F_032") == 0) {

                            continueProcces = false;
                            if (value != null) {
                                if (value.trim().length() != 0) {
                                    continueProcces = true;
                                }
                            }
                            if (continueProcces) {
                                switch (SCC_F_032Enum.convert(relationVar.getNameExpected())) {
                                    case mecanismo_objeto_lesion:
                                        mechanism = value;
                                        break;
                                    // ************************************************DATOS PARA LA TABLA non_fatal_transport
                                    case tipo_transporte_lesionado:
                                    case tipo_transporte_contraparte:
                                    case tipo_usuario_transporte:
                                        traficValues = traficValues + " " + relationVar.getNameFound() + "='" + registryData + "',";
                                        break;
                                    // ************************************************DATOS PARA LA TABLA non_fatal_interpersonal                                        
                                    case antecedentes_previos_agresion://boleano->previous_antecedent                             
                                        if (value.equals("1")) {//SI
                                            interpersonalValues = interpersonalValues + " antecedentes_previos_agresion='" + value + "',";
                                        }
                                        break;
                                    case relacion_agresor_victima://categorico->relationships_to_victim
                                    case contexto_en_violencia_interpersonal:
                                    case sexo_agresores:
                                        interpersonalValues = interpersonalValues + " " + relationVar.getNameFound() + "='" + registryData + "',";
                                        break;
                                    // ************************************************DATOS PARA LA TABLA non_fatal_selft-inflicted
                                    case intento_previo_autoinflingida:
                                    case antecedentes_transtorno_mental:
                                    case factores_precipitantes:
                                        if (value.equals("1")) {//SI
                                            selftInflictedValues = selftInflictedValues + " " + relationVar.getNameFound() + "='" + registryData + "',";
                                        }
                                        break;
                                    // ************************************************DATOS PARA LA TABLA non_fatal_transport_security_element
                                    case uso_elementos_seguridad:
                                    case usaba_cinturon:
                                    case usaba_casco_motocicleta:
                                    case usaba_casco_bicicleta:
                                    case usaba_chaleco:
                                    case otro_elemento_seguridad:
                                        if (value.equals("1")) {//SI
                                            traficValues = traficValues + " " + relationVar.getNameFound() + "='" + registryData + "',";
                                        }
                                        break;
                                    // ************************************************DATOS PARA LA TABLA domestic_violence_abuse_type
                                    case maltrato_fisico:
                                    case maltrato_psicologico:
                                    case maltrato_abuso_sexual:
                                    case maltrato_negligencia:
                                    case maltrato_abandono:
                                    case maltrato_institucional:
                                    case maltrato_sin_dato:
                                        if (value.equals("1")) {//SI
                                            domesticViolenceValues = domesticViolenceValues + " " + relationVar.getNameFound() + "='" + registryData + "',";
                                        }
                                        break;
                                    // ************************************************DATOS PARA LA TABLA domestic_violence_aggressor_type
                                    case agresor_padre:
                                    case agresor_madre:
                                    case agresor_padrastro:
                                    case agresor_madrastra:
                                    case agresor_conyuge:
                                    case agresor_hermano:
                                    case agresor_hijo:
                                    case agresor_otro:
                                    case agresor_sin_dato:
                                        if (value.equals("1")) {//SI
                                            domesticViolenceValues = domesticViolenceValues + " " + relationVar.getNameFound() + "='" + registryData + "',";
                                        }
                                        break;
                                    //campos otros--------------------------------      
                                    case otro_factor_precipitante://para autoinflingida intencional
                                        selftInflictedValues = selftInflictedValues + " " + relationVar.getNameFound() + "='" + registryData + "',";
                                        break;
                                    case otro_tipo_relacion_victima://violencia interpersonal
                                        interpersonalValues = interpersonalValues + " " + relationVar.getNameFound() + "='" + registryData + "',";
                                        break;
                                    case cual_otro_tipo_agresor://otro tipo de agresor(vif)
                                        domesticViolenceValues = domesticViolenceValues + " " + relationVar.getNameFound() + "='" + registryData + "',";
                                        break;
                                    case cual_otro_tipo_maltrato://otro tipo de maltrato(vif)                                    
                                        domesticViolenceValues = domesticViolenceValues + " " + relationVar.getNameFound() + "='" + registryData + "',";
                                        break;
                                    case otro_tipo_transporte_usuario://(otro tipo de transporte)
                                        traficValues = traficValues + " " + relationVar.getNameFound() + "='" + registryData + "',";
                                        break;
                                    case otro_tipo_transporte_contraparte://(otro tipo de transporte contraparte)                                    
                                        traficValues = traficValues + " " + relationVar.getNameFound() + "='" + registryData + "',";
                                        break;
                                    case otro_tipo_de_usuario://(otro tipo de usuario)
                                        traficValues = traficValues + " " + relationVar.getNameFound() + "='" + registryData + "',";
                                        break;
                                    default:
                                }
                            }
                        }
                    }

                    //..........................................................
                    //verifico que pueda ser determinada la fecha_evento e intencionalidad
                    boolean existDateEvent = true;
                    fechaev = haveData(fechaev);
                    fechacon = haveData(fechacon);
                    dia = haveData(dia);
                    mes = haveData(mes);
                    ao = haveData(ao);
                    dia1 = haveData(dia1);
                    mes1 = haveData(mes1);
                    ao1 = haveData(ao1);
                    intencionality = haveData(intencionality);
                    if (fechaev == null) {
                        if (dia == null || mes == null || ao == null) {
                            if (fechacon == null) {
                                if (dia1 == null || mes1 == null || ao1 == null) {
                                    existDateEvent = false;
                                }
                            }
                        }
                    }

                    switch (FormsEnum.convert(nameForm.replace("-", "_"))) {//tipo de relacion                        
                        case SCC_F_032:
                            if (intencionality == null || intencionality.length() == 0) {//RELACION PARA LA INTENCIONALIDAD
                                relationVar = currentRelationsGroup.findRelationVarByNameExpected("intencionalidad");//determino la relacion de variables
                                errorsNumber++;
                                errorsControlMB.addError(errorsNumber, relationVar, "", resultSetFileData.getString("record_id"));
                            } else {//VALIDACION DE TIPO DE INTENCIONALIDAD->MECANISMO->DATOS ESPECIFICOS EVENTO
                                validateIntentionalityMechanismAndDataEvent(
                                        resultSetFileData.getString("record_id"),
                                        intencionality,
                                        mechanism,
                                        traficValues,
                                        interpersonalValues,
                                        selftInflictedValues,
                                        domesticViolenceValues);
                            }
                        case SCC_F_028:
                        case SCC_F_029:
                        case SCC_F_030:
                        case SCC_F_031:
                        case SCC_F_033:
                        case SIVIGILA_VIF:
                            //DETERMINAR FECHA DE EVENTO                                
                            if (existDateEvent == false) {//no se puede determinar la fecha_evento
                                relationVar = currentRelationsGroup.findRelationVarByNameExpected("fecha_evento");//determino la relacion de variables
                                errorsNumber++;
                                errorsControlMB.addError(errorsNumber, relationVar, "", resultSetFileData.getString("record_id"));
                            }
                            break;
                    }
                    //..........................................................
                    //currentNumberOfRow++;
                    tuplesProcessed++;
                    progressValidate = (int) (tuplesProcessed * 100) / tuplesNumber;
                    //System.out.println("PROGRESO VALIDANDO: " + String.valueOf(progressValidate));
                }
                progress = 100;//System.out.println("PROGRESO: " + String.valueOf(progressValidate));
                errorsControlMB.setSizeErrorsList(errorsNumber);
                progress = 0;
            } catch (Exception ex) {
                progress = 100;
                System.out.println("Error 4 en " + this.getClass().getName() + ":" + ex.toString());
            }
        }
    }

    /**
     * determines if there are inconsistencies between variables INTENTIONALITY
     * <-> MECHANISM <-> SPECIFIC DATA EVENT
     *
     * @param rowId: id of the row.
     * @param intencionality1: variable that loaded the type of intentionality:
     * NO INTENTIONAL (ACCIDENT) Intentional self-inflicted (SUICIDE) .
     * @param mechanism: variable that loaded the type mechanism of injury: bite
     * person, sexual violence, ...
     * @param traficValues: load the values of traffic.
     * @param interpersonalValues: load the values of interpersonal violence.
     * @param selftInflictedValues: load the values of self-inflicted violence.
     * @param domesticViolenceValues: load the values of domestic violence.
     */
    private void validateIntentionalityMechanismAndDataEvent(String rowId, String intencionality1, String mechanism, String traficValues, String interpersonalValues, String selftInflictedValues, String domesticViolenceValues) {
        /*
         * determina si existen incoherencias entre las variables INTENCIONALIDAD <-> MECANISMO <-> DATOS ESPECIFICOS DEL EVENTO
         */
        if (traficValues.length() != 0) {
            traficValues = traficValues.substring(0, traficValues.length() - 1);
        }
        if (interpersonalValues.length() != 0) {
            interpersonalValues = interpersonalValues.substring(0, interpersonalValues.length() - 1);
        }
        if (selftInflictedValues.length() != 0) {
            selftInflictedValues = selftInflictedValues.substring(0, selftInflictedValues.length() - 1);
        }
        if (domesticViolenceValues.length() != 0) {
            domesticViolenceValues = domesticViolenceValues.substring(0, domesticViolenceValues.length() - 1);
        }

        //------------------------------------------------------------------
        //---------------- VALIDACION ENTRE INTENCIONALIDAD Y MECANISMO ----
        //------------------------------------------------------------------
        if (intencionality1 != null && mechanism.length() != 0) {
            if (intencionality1.compareTo("1") == 0) {//NO INTENCIONAL (ACCIDENTES)
                //--------------------------------------------------------------
                if (mechanism.compareTo("2") == 0) {//VIOLENCIA SEXUAL
                    errorsNumber++;
                    errorsControlMB.addError(errorsNumber * -1, null, "Cuando la intencionalidad es: 'NO INTENCIONAL (ACCIDENTES)' el mecanismo no puede ser 'VIOLENCIA SEXUAL' ", rowId);
                }
                if (mechanism.compareTo("23") == 0) {//MORDEDURA DE PERSONA
                    errorsNumber++;
                    errorsControlMB.addError(errorsNumber * -1, null, "Cuando la intencionalidad es: 'NO INTENCIONAL (ACCIDENTES)' el mecanismo no puede ser 'MORDEDURA DE PERSONA' ", rowId);
                }
                if (interpersonalValues.length() != 0) {
                    errorsNumber++;
                    errorsControlMB.addError(errorsNumber * -1, null, "Cuando la intencionalidad es: 'NO INTENCIONAL (ACCIDENTES)' no pueden haber datos de VIOLENCIA INTERPERSONAL: (" + interpersonalValues + ")", rowId);
                }
                if (selftInflictedValues.length() != 0) {
                    errorsNumber++;
                    errorsControlMB.addError(errorsNumber * -1, null, "Cuando la intencionalidad es: 'NO INTENCIONAL (ACCIDENTES)' no pueden haber datos de VIOLENCIA INTENCIONAL AUTOINFLINGIDA: (" + selftInflictedValues + ")", rowId);
                }
                if (domesticViolenceValues.length() != 0) {
                    errorsNumber++;
                    errorsControlMB.addError(errorsNumber * -1, null, "Cuando la intencionalidad es: 'NO INTENCIONAL (ACCIDENTES)' no pueden haber datos de VIOLENCIA INRAFAMILIAR: (" + domesticViolenceValues + ")", rowId);
                }
            } else if (intencionality1.compareTo("2") == 0) {//AUTOINFLINGIDA INTENCIONAL (SUICIDIO)
                //--------------------------------------------------------------
                if (mechanism.compareTo("2") == 0) {//VIOLENCIA SEXUAL
                    errorsNumber++;
                    errorsControlMB.addError(errorsNumber * -1, null, "Cuando la intencionalidad es: 'AUTOINFLINGIDA INTENCIONAL (SUICIDIO)' el mecanismo no puede ser 'VIOLENCIA SEXUAL' ", rowId);
                }
                if (mechanism.compareTo("1") == 0) {//LESION DE TRANSPORTE
                    errorsNumber++;
                    errorsControlMB.addError(errorsNumber * -1, null, "Cuando la intencionalidad es: 'AUTOINFLINGIDA INTENCIONAL (SUICIDIO)' el mecanismo no puede ser 'LESION DE TRANSPORTE' ", rowId);
                }
                if (mechanism.compareTo("3") == 0) {//CAIDA PROPIA ALTURA
                    errorsNumber++;
                    errorsControlMB.addError(errorsNumber * -1, null, "Cuando la intencionalidad es: 'AUTOINFLINGIDA INTENCIONAL (SUICIDIO)' el mecanismo no puede ser 'CAIDA PROPIA ALTURA' ", rowId);
                }
                if (mechanism.compareTo("16") == 0) {//LESION POR CUERPO EXTRAÑO
                    errorsNumber++;
                    errorsControlMB.addError(errorsNumber * -1, null, "Cuando la intencionalidad es: 'AUTOINFLINGIDA INTENCIONAL (SUICIDIO)' el mecanismo no puede ser 'LESION POR CUERPO EXTRAÑO' ", rowId);
                }
                if (mechanism.compareTo("21") == 0) {//MINAS / MUNICION SIN EXPLOTAR
                    errorsNumber++;
                    errorsControlMB.addError(errorsNumber * -1, null, "Cuando la intencionalidad es: 'AUTOINFLINGIDA INTENCIONAL (SUICIDIO)' el mecanismo no puede ser 'MINAS / MUNICION SIN EXPLOTAR' ", rowId);
                }
                if (mechanism.compareTo("22") == 0) {//OTRO ARTEFACTO EXPLOSIVO
                    errorsNumber++;
                    errorsControlMB.addError(errorsNumber * -1, null, "Cuando la intencionalidad es: 'AUTOINFLINGIDA INTENCIONAL (SUICIDIO)' el mecanismo no puede ser 'OTRO ARTEFACTO EXPLOSIVO' ", rowId);
                }
                if (mechanism.compareTo("23") == 0) {//MORDEDURA DE PERSONA
                    errorsNumber++;
                    errorsControlMB.addError(errorsNumber * -1, null, "Cuando la intencionalidad es: 'AUTOINFLINGIDA INTENCIONAL (SUICIDIO)' el mecanismo no puede ser 'MORDEDURA DE PERSONA' ", rowId);
                }
                if (mechanism.compareTo("24") == 0) {//ANIMAL, CUAL?
                    errorsNumber++;
                    errorsControlMB.addError(errorsNumber * -1, null, "Cuando la intencionalidad es: 'AUTOINFLINGIDA INTENCIONAL (SUICIDIO)' el mecanismo no puede ser 'ANIMAL, CUAL?' ", rowId);
                }
                if (mechanism.compareTo("26") == 0) {//DESASTRE NATURAL, CUAL?
                    errorsNumber++;
                    errorsControlMB.addError(errorsNumber * -1, null, "Cuando la intencionalidad es: 'AUTOINFLINGIDA INTENCIONAL (SUICIDIO)' el mecanismo no puede ser 'DESASTRE NATURAL, CUAL?' ", rowId);
                }
                if (traficValues.length() != 0) {
                    errorsNumber++;
                    errorsControlMB.addError(errorsNumber * -1, null, "Cuando la intencionalidad es: 'AUTOINFLINGIDA INTENCIONAL (SUICIDIO)' no pueden haber datos de LESION DE TRANSPORTE: (" + traficValues + ")", rowId);
                }
                if (interpersonalValues.length() != 0) {
                    errorsNumber++;
                    errorsControlMB.addError(errorsNumber * -1, null, "Cuando la intencionalidad es: 'AUTOINFLINGIDA INTENCIONAL (SUICIDIO)' no pueden haber datos de VIOLENCIA INTERPERSONAL: (" + interpersonalValues + ")", rowId);
                }
                if (domesticViolenceValues.length() != 0) {
                    errorsNumber++;
                    errorsControlMB.addError(errorsNumber * -1, null, "Cuando la intencionalidad es: 'AUTOINFLINGIDA INTENCIONAL (SUICIDIO)' no pueden haber datos de VIOLENCIA INRAFAMILIAR: (" + domesticViolenceValues + ")", rowId);
                }
            } else if (intencionality1.compareTo("3") == 0) {//VIOLENCIA / AGRESION O SOSPECHA
                //------------------------------------------------------------------                
                if (mechanism.compareTo("1") == 0) {//LESION DE TRANSPORTE
                    errorsNumber++;
                    errorsControlMB.addError(errorsNumber * -1, null, "Cuando la intencionalidad es: 'VIOLENCIA / AGRESION O SOSPECHA' el mecanismo no puede ser 'LESION DE TRANSPORTE' ", rowId);
                }
                if (mechanism.compareTo("24") == 0) {//ANIMAL, CUAL?
                    errorsNumber++;
                    errorsControlMB.addError(errorsNumber * -1, null, "Cuando la intencionalidad es: 'VIOLENCIA / AGRESION O SOSPECHA' el mecanismo no puede ser 'ANIMAL, CUAL?' ", rowId);
                }
                if (mechanism.compareTo("26") == 0) {//DESASTRE NATURAL, CUAL?
                    errorsNumber++;
                    errorsControlMB.addError(errorsNumber * -1, null, "Cuando la intencionalidad es: 'VIOLENCIA / AGRESION O SOSPECHA' el mecanismo no puede ser 'DESASTRE NATURAL, CUAL?' ", rowId);
                }
                if (traficValues.length() != 0) {
                    errorsNumber++;
                    errorsControlMB.addError(errorsNumber * -1, null, "Cuando la intencionalidad es: 'VIOLENCIA / AGRESION O SOSPECHA' no pueden haber datos de LESION DE TRANSPORTE: (" + traficValues + ")", rowId);
                }
                if (selftInflictedValues.length() != 0) {
                    errorsNumber++;
                    errorsControlMB.addError(errorsNumber * -1, null, "Cuando la intencionalidad es: 'VIOLENCIA / AGRESION O SOSPECHA' no pueden haber datos de VIOLENCIA INTENCIONAL AUTOINFLINGIDA: (" + selftInflictedValues + ")", rowId);
                }
                if (interpersonalValues.length() != 0 && domesticViolenceValues.length() != 0) {
                    errorsNumber++;
                    errorsControlMB.addError(errorsNumber * -1, null, "No pueden Haber datos de VIOLENCIA INTERPERSONAL Y VIOLENCIA INTRAFAMILIAR en el mismo registro; INTERPERSONAL: (" + interpersonalValues + "), INTRAFAMILIAR: (" + domesticViolenceValues + ")", rowId);
                }
            }
        }
    }

    /**
     * obtains the data from a string.
     *
     * @param a
     * @return
     */
    private String haveData(String a) {
        if (a != null) {
            if (a.length() == 0) {
                a = null;
            }
        }
        return a;
    }

    /**
     * allows to load the record corresponding to the tab the homicide .
     */
    public void registerSCC_F_028() {
        /**
         * *********************************************************************
         * CARGA DE REGISTROS DE LA FICHA HOMICIDIOS
         * ********************************************************************
         */
        tuplesNumber = 0;
        tuplesProcessed = 0;
        progress = 0;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            tuplesNumber = determineTuplesNumber();//determino numero de tuplas  
            resultSetFileData = determineRecords();//resulset con los registros a procesar

            newUngroupedTags = new UngroupedTags();
            newUngroupedTags.setUngroupedTagId(ungroupedTagsFacade.findMax() + 1);
            newUngroupedTags.setUngroupedTagName(determineTagName(projectsMB.getCurrentProjectName()));
            newUngroupedTags.setUngroupedTagDate(new Date());
            newUngroupedTags.setFormId(nameForm);
            newUngroupedTags.setCurrentTagId(ungroupedTagsFacade.findMax() + 1);
            ungroupedTagsFacade.create(newUngroupedTags);

            newTag = new Tags();
            newTag.setTagId(ungroupedTagsFacade.findMax());
            String tagName = determineTagName(projectsMB.getCurrentProjectName());
            newTag.setTagName(tagName);
            newTag.setTagFileInput(projectsMB.getCurrentFileName());
            newTag.setTagFileStored(projectsMB.getCurrentFileName());
            newTag.setFormId(formsFacade.find(nameForm));
            tagsFacade.create(newTag);
            lastTagNameCreated = newTag.getTagName();

            while (resultSetFileData.next()) {//recorro cada uno de los registros de la tabla temp                    
                Victims newVictim = new Victims();
                //newVictim.setVictimId(victimsFacade.findMax() + 1);
                newVictim.setVictimId(applicationControlMB.addVictimsReservedIdentifiers());
                newVictim.setVictimClass((short) 1);
                FatalInjuries newFatalInjurie = new FatalInjuries();
                //newFatalInjurie.setFatalInjuryId(fatalInjuriesFacade.findMax() + 1);
                newFatalInjurie.setFatalInjuryId(applicationControlMB.addFatalReservedIdentifiers());
                newFatalInjurie.setInputTimestamp(new Date());
                newFatalInjurie.setInjuryId(injuriesFacade.find((short) 10));//es 10 por ser homicidio
                newFatalInjurie.setUserId(usersFacade.find(1));//usuario que se encuentre logueado
                newFatalInjurie.setVictimId(newVictim);
                newFatalInjuryMurder = new FatalInjuryMurder();
                newFatalInjuryMurder.setFatalInjuryId(newFatalInjurie.getFatalInjuryId());
                newVictim.setTagId(tagsFacade.find(newTag.getTagId()));
                newVictim.setFirstTagId(newVictim.getTagId().getTagId());
                value = "";
                name = "";
                surname = "";
                dia = "";//dia evento
                mes = "";//mes evento
                ao = "";//año del evento
                dia1 = "";//dia de la consulta
                mes1 = "";//mes de la consulta
                ao1 = "";//año de la consulta
                horas = "";//hora evento
                minutos = "";//minuto evento
                ampm = "";//ampm evento
                horas1 = "";//hora consulta
                minutos1 = "";//minuto consulta
                ampm1 = "";//ampm consulta
                hourInt = 0;
                minuteInt = 0;
                narrative = "";
                Object[] arrayInJava = (Object[]) resultSetFileData.getArray(3).getArray();
                for (int posCol = 0; posCol < arrayInJava.length; posCol++) {
                    value = null;
                    String splitColumnAndValue[] = arrayInJava[posCol].toString().split("<=>");
                    relationVar = currentRelationsGroup.findRelationVarByNameFound(splitColumnAndValue[0]);//determino la relacion de variables
                    if (relationVar != null) {
                        switch (DataTypeEnum.convert(relationVar.getFieldType())) {//determino valor a ingresar usando: isNumeric,isAge... etc
                            case text:
                                value = splitColumnAndValue[1];
                                break;
                            case integer:
                                value = isNumeric(splitColumnAndValue[1]);
                                break;
                            case age:
                                value = isAge(splitColumnAndValue[1]);
                                break;
                            case date:
                                value = isDate(splitColumnAndValue[1], relationVar.getDateFormat());
                                break;
                            case military:
                                value = isMilitary(splitColumnAndValue[1]);
                                break;
                            case hour:
                                value = isHour(splitColumnAndValue[1]);
                                break;
                            case minute:
                                value = isMinute(splitColumnAndValue[1]);
                                break;
                            case day:
                                value = isDay(splitColumnAndValue[1]);
                                break;
                            case month:
                                value = isMonth(splitColumnAndValue[1]);
                                break;
                            case year:
                                value = isYear(splitColumnAndValue[1]);
                                break;
                            case percentage:
                                value = isPercentage(splitColumnAndValue[1]);
                                break;
                            case level:
                                value = isLevel(splitColumnAndValue[1]);
                                break;
                            case NOVALUE:
                                value = isCategorical(splitColumnAndValue[1], relationVar);
                                break;
                        }
                    }
                    continueProcces = false;
                    if (value != null) {
                        if (value.trim().length() != 0) {
                            continueProcces = true;
                        }
                    }
                    if (continueProcces) {
                        switch (SCC_F_028Enum.convert(relationVar.getNameExpected())) {
                            // ************************************************DATOS PARA LA TABLA victims                                
                            case departamento_evento:
                                break;
                            case municipio_evento:
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
                            case certificado_defuncion:
                                newFatalInjurie.setCode(value);
                                break;
                            case dia_evento://dia del evento
                                dia = value;
                                break;
                            case mes_evento://mes evento
                                mes = value;
                                break;
                            case año_evento://año del evento
                                ao = value;
                                break;
                            case fecha_evento:
                                try {
                                    currentDate = dateFormat.parse(value);
                                    newFatalInjurie.setInjuryDate(currentDate);
                                } catch (ParseException ex) {
                                }
                                break;
                            case hora_evento:
                                horas = value;
                                break;
                            case minuto_evento:
                                minutos = value;
                                break;
                            case am_pm:
                                ampm = value;
                                break;
                            case hora_militar_evento:
                                n = new Date();
                                hourInt = Integer.parseInt(value.substring(0, 2));
                                minuteInt = Integer.parseInt(value.substring(2, 4));
                                n.setHours(hourInt);
                                n.setMinutes(minuteInt);
                                n.setSeconds(0);
                                newFatalInjurie.setInjuryTime(n);
                                break;
                            case direccion_evento:
                                newFatalInjurie.setInjuryAddress(value);
                                break;
                            case barrio_evento:
                                newFatalInjurie.setInjuryNeighborhoodId(Integer.parseInt(value));
                                break;
                            case area_evento://ZONA URBANA O RURAL //SE DETERMINA CON EL BARRIO
                                newFatalInjurie.setAreaId(areasFacade.find(Short.parseShort(value)));
                                break;
                            case clase_lugar_evento:
                                newFatalInjurie.setInjuryPlaceId(placesFacade.find(Short.parseShort(value)));
                                break;
                            case dia_semana_evento:
                                newFatalInjurie.setInjuryDayOfWeek(daysFacade.find(Short.parseShort(value)).getDaysName());
                                break;
                            case numero_victimas_fatales:
                                newFatalInjurie.setVictimNumber(Short.parseShort(value));
                                break;
                            case nombres_victima:
                                name = value;
                                break;
                            case apellidos_victima:
                                surname = value;
                                break;
                            case sexo_victima:
                                newVictim.setGenderId(gendersFacade.find(Short.parseShort(value)));
                                break;
                            case tipo_edad_victima:
                                newVictim.setAgeTypeId(Short.parseShort(value));
                                break;
                            case edad_victima:
                                newVictim.setVictimAge(Short.parseShort(value));
                                break;
                            case ocupacion_victima:
                                newVictim.setJobId(jobsFacade.find(Integer.parseInt(value)));
                                break;
                            case municipio_residencia:
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
                            case barrio_residencia_victima:
                                newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(value)));
                                break;
                            case tipo_identificacion_victima:
                                newVictim.setTypeId(idTypesFacade.find(Short.parseShort(value)));
                                break;
                            case numero_identificacion_victima:
                                newVictim.setVictimNid(value);
                                break;
                            case procedencia_victima:
                                splitArray = value.split("-");
                                if (splitArray.length == 1) {
                                    newFatalInjurie.setVictimPlaceOfOrigin(value + "-0-0");
                                }
                                if (splitArray.length == 2) {
                                    newFatalInjurie.setVictimPlaceOfOrigin(value + "-0");
                                }
                                if (splitArray.length == 3) {
                                    newFatalInjurie.setVictimPlaceOfOrigin(value);
                                }
                                break;
                            case arma_o_causa_muerte:
                                newFatalInjuryMurder.setWeaponTypeId(weaponTypesFacade.find(Short.parseShort(value)));
                                break;
                            case contexto_evento:
                                newFatalInjuryMurder.setMurderContextId(murderContextsFacade.find(Short.parseShort(value)));
                                break;
                            case narracion_evento:
                            case narracion_evento_1:
                            case narracion_evento_2:
                                narrative = narrative + " " + value;
                                break;
                            case nivel_alcohol_victima:
                                newFatalInjurie.setAlcoholLevelVictim(Short.parseShort(value));
                                break;
                            case nivel_alcohol_sin_dato:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 2));
                                }
                                break;
                            case nivel_alcohol_pendiente:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 4));
                                }
                                break;
                            case nivel_alcohol_desconocido:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 3));
                                }
                                break;
                            case nivel_alcohol_negativo:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 5));
                                }
                                break;
                            default:
                        }
                    }
                }
                //DETERMINAR AREA SI HAY BARRIO
                if (newFatalInjurie.getAreaId() == null) {
                    if (newFatalInjurie.getInjuryNeighborhoodId() != null) {
                        Neighborhoods ne = neighborhoodsFacade.find(newFatalInjurie.getInjuryNeighborhoodId());
                        short neType = Short.parseShort(ne.getNeighborhoodArea().toString());
                        newFatalInjurie.setAreaId(areasFacade.find(neType));
                    }
                }
                //ASIGNO LA NARRACION DE LOS HECHOS
                if (narrative.trim().length() != 0) {
                    newFatalInjurie.setInjuryDescription(narrative);
                }
                //SI NO SE DETERMINA EL BARRIO SE COLOCA SIN DATO URBANO
                if (newVictim.getVictimNeighborhoodId() == null) {
                    newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find((int) 52001));
                }
                //SI NO SE DETERMINA EL BARRIO SE COLOCA SIN DATO URBANO
                if (newFatalInjurie.getInjuryNeighborhoodId() == null) {
                    newFatalInjurie.setInjuryNeighborhoodId((int) 52001);
                }
                //SI NO HAY FECHA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES dia_evento, mes_evento, año_evento
                if (newFatalInjurie.getInjuryDate() == null) {
                    dia = haveData(dia);
                    mes = haveData(mes);
                    ao = haveData(ao);
                    if (dia != null && mes != null && ao != null) {
                        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                        Date fechaI;
                        fechaI = formato.parse(dia + "/" + mes + "/" + ao);
                        newFatalInjurie.setInjuryDate(fechaI);
                    }
                }
                //SI NO HAY HORA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES hora_evento,minuto_evento,am_pm
                if (newFatalInjurie.getInjuryTime() == null) {
                    horas = haveData(horas);
                    minutos = haveData(minutos);
                    ampm = haveData(ampm);
                    if (horas != null && minutos != null && ampm != null) {
                        hourInt = Integer.parseInt(horas);
                        minuteInt = Integer.parseInt(minutos);
                        if (ampm.compareTo("2") == 0) {
                            hourInt = hourInt + 12;
                            if (hourInt == 24) {
                                hourInt = 0;
                            }
                        }
                        currentDate = new Date();
                        currentDate.setHours(hourInt);
                        currentDate.setMinutes(minuteInt);
                        currentDate.setSeconds(0);
                        newFatalInjurie.setInjuryTime(currentDate);
                    }
                }
                //SI NO HAY DIA DE LA SEMANA DEL EVENTO SE CALCULA
                if (newFatalInjurie.getInjuryDate() != null) {
                    if (newFatalInjurie.getInjuryDayOfWeek() == null) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(newFatalInjurie.getInjuryDate());
                        newFatalInjurie.setInjuryDayOfWeek(intToDay(cal.get(Calendar.DAY_OF_WEEK)));
                    }
                }
                //UNION DE NOMBRES Y APELLIOS
                name = name + " " + surname;
                if (name.trim().length() > 1) {
                    newVictim.setVictimName(name.trim());
                }
                //EDAD Y TIPO DE EDAD
                if (newVictim.getVictimAge() != null) {//HAY EDAD 
                    if (newVictim.getAgeTypeId() == null) {//no hay tipo de edad
                        newVictim.setAgeTypeId((short) 1);//tiṕo de edad años
                    }
                } else {
                    newVictim.setAgeTypeId((short) 4);//tiṕo de edad sin determinar
                }

                //SI NO SE DETERMINA LA EDAD VERIFICAR SI HAY FECHA DE NACIMIENTO
                if (newVictim.getVictimDateOfBirth() != null) {
                    if (newVictim.getVictimAge() == null) {
                        int birthMonths;
                        int eventMonths;

                        Calendar systemCalendar = Calendar.getInstance();
                        Calendar birthCalendar = Calendar.getInstance();
                        birthCalendar.setTime(newVictim.getVictimDateOfBirth());

                        try {//DETERMINO LA EDAD EN MESES
                            birthMonths = birthCalendar.get(Calendar.YEAR);
                            birthMonths = birthMonths * 12;
                            birthMonths = birthMonths + birthCalendar.get(Calendar.MONTH);
                            if (newFatalInjurie.getInjuryDate() != null) {//SE CALCULA EDAD SEGUN LA FECHA DE EVENTO
                                systemCalendar.setTime(newFatalInjurie.getInjuryDate());
                            }
                            eventMonths = systemCalendar.get(Calendar.YEAR);
                            eventMonths = eventMonths * 12;
                            eventMonths = eventMonths + systemCalendar.get(Calendar.MONTH);

                            int ageMonths = eventMonths - birthMonths;
                            if (ageMonths < 0) {
                                System.out.println("ERROR fecha de nacimiento mayor a la del sistema o evento: ");
                            } else {
                                //SI EXISTE EDAD Y NO HAY TIPO DE EDAD DETERMINARLA EN AÑOS
                                int ageYears = (int) (ageMonths / 12);
                                if (ageYears == 0) {
                                    ageYears = 1;
                                }
                                newVictim.setVictimAge((short) ageYears);
                                newVictim.setAgeTypeId((short) 1);//aqui por defecto seria sin dato, si no se conoce
                            }
                        } catch (Exception ex) {
                            System.out.println("Error 5 en " + this.getClass().getName() + ":" + ex.toString());
                        }
                    }
                }
                if (newVictim.getVictimNid() == null) {//NO HAY NUMERO DE IDENTIFICACION 
                    newVictim.setVictimNid(String.valueOf(genNnFacade.findMax() + 1));//asigno un consecutivo a la identificacion
                    newVictim.setVictimClass((short) 2);//nn
                    if (newVictim.getTypeId() == null) {//no hay tipo de identificacion
                        if (newVictim.getVictimAge() != null && newVictim.getAgeTypeId() != null && newVictim.getAgeTypeId() == 1) {//HAY EDAD Y HAY tipo de edad                            
                            if (newVictim.getVictimAge() > 17) {
                                newVictim.setTypeId(idTypesFacade.find((short) 6));//adulto sin identificacion                                
                            } else {
                                newVictim.setTypeId(idTypesFacade.find((short) 7));//menor sin identificacion
                            }
                        } else {//NO HAY EDAD
                            newVictim.setTypeId(idTypesFacade.find((short) 9));//tipo de identificacoin sin determinar
                        }
                    }
                    int newGenNnId = genNnFacade.findMax() + 1;
                    connectionJdbcMB.non_query("UPDATE gen_nn SET cod_nn = " + newGenNnId + " where cod_nn IN (SELECT MAX(cod_nn) from gen_nn)");
                } else {//HAY NUMERO DE IDENTIFICACION
                    if (newVictim.getTypeId() == null) {//no hay tipo de identificacion
                        if (newVictim.getVictimAge() != null && newVictim.getAgeTypeId() != null && newVictim.getAgeTypeId() == 1) {//HAY EDAD Y HAY tipo de edad                            
                            if (newVictim.getVictimAge() > 17) {
                                newVictim.setTypeId(idTypesFacade.find((short) 6));//adulto sin identificacion                                
                            } else {
                                newVictim.setTypeId(idTypesFacade.find((short) 7));//menor sin identificacion
                            }
                        } else {//NO HAY EDAD
                            newVictim.setTypeId(idTypesFacade.find((short) 9));//tipo de identificacoin sin determinar
                        }
                    }
                }
                //CORRESPONDENCIA ENTRE EDAD Y TIPO DE IDENTIFICACION
                if (newVictim.getTypeId() != null) {//no hay tipo de identificacion
                    if (newVictim.getVictimAge() != null) {//HAY EDAD Y HAY tipo de edad
                        if (newVictim.getVictimAge() < 18) {//menor de edad
                            if (newVictim.getTypeId().getTypeId() == (short) 1 ||//cedula de ciudadania
                                    newVictim.getTypeId().getTypeId() == (short) 2 ||//cedula de extranjeria
                                    newVictim.getTypeId().getTypeId() == (short) 3 ||//pasaporte
                                    newVictim.getTypeId().getTypeId() == (short) 6) {//adulto sin identificacion
                                newVictim.setTypeId(idTypesFacade.find((short) 9));//sin determinar
                            }
                        } else {//mayor de edad
                            if (newVictim.getTypeId().getTypeId() == (short) 5 ||//tarjeta de identidad
                                    newVictim.getTypeId().getTypeId() == (short) 4 ||//registro civil
                                    newVictim.getTypeId().getTypeId() == (short) 7) {//menor sin identificacion
                                newVictim.setTypeId(idTypesFacade.find((short) 9));//sin determinar
                            }
                        }

                    }
                }
                //PERSISTO
                try {
                    victimsFacade.create(newVictim);
                    fatalInjuriesFacade.create(newFatalInjurie);
                    fatalInjuryMurderFacade.create(newFatalInjuryMurder);
                    applicationControlMB.removeFatalReservedIdentifiers(newFatalInjurie.getFatalInjuryId());
                    applicationControlMB.removeVictimsReservedIdentifiers(newVictim.getVictimId());
                } catch (Exception e) {
                    System.out.println("Error 6 en " + this.getClass().getName() + ":" + e.toString());
                }
                tuplesProcessed++;
                progress = (int) (tuplesProcessed * 100) / tuplesNumber;
                System.out.println("PROGRESO INGRESANDO HOMICIDIO: " + String.valueOf(progress));
            }
            progress = 100;
            System.out.println("PROGRESO INGRESANDO HOMICIDIO: " + String.valueOf(progress));
        } catch (SQLException ex) {
            System.out.println("Error 7 en " + this.getClass().getName() + ":" + ex.toString());
        } catch (Exception ex) {
            System.out.println("Error 8 en " + this.getClass().getName() + ":" + ex.toString());
        }
    }

    /**
     * allows to load the record corresponding to the tab the traffic accident
     * deaths.
     */
    public void registerSCC_F_029() {
        /**
         * ******************************************************************
         * CARGA DE REGISTROS DE LA FICHA MUERTES POR ACCIDENTE DE TRANSITO
         * *******************************************************************
         */
        tuplesNumber = 0;
        tuplesProcessed = 0;
        progress = 0;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            tuplesNumber = determineTuplesNumber();//determino numero de tuplas  
            resultSetFileData = determineRecords();//resulset con los registros a procesar

            newUngroupedTags = new UngroupedTags();
            newUngroupedTags.setUngroupedTagId(ungroupedTagsFacade.findMax() + 1);
            newUngroupedTags.setUngroupedTagName(determineTagName(projectsMB.getCurrentProjectName()));
            newUngroupedTags.setUngroupedTagDate(new Date());
            newUngroupedTags.setFormId(nameForm);
            newUngroupedTags.setCurrentTagId(ungroupedTagsFacade.findMax() + 1);
            ungroupedTagsFacade.create(newUngroupedTags);

            newTag = new Tags();//VARIABLES PARA CONJUNTOS DE REGISTROS
            newTag.setTagId(ungroupedTagsFacade.findMax());
            newTag.setTagName(determineTagName(projectsMB.getCurrentProjectName()));
            newTag.setTagFileInput(projectsMB.getCurrentFileName());
            newTag.setTagFileStored(projectsMB.getCurrentFileName());
            newTag.setFormId(formsFacade.find(nameForm));
            tagsFacade.create(newTag);

            lastTagNameCreated = newTag.getTagName();


            while (resultSetFileData.next()) {//recorro cada uno de los registros de la tabla temp                    
                Victims newVictim = new Victims();
                newVictim.setVictimId(applicationControlMB.addVictimsReservedIdentifiers());
                newVictim.setVictimClass((short) 1);
                FatalInjuries newFatalInjurie = new FatalInjuries();
                newFatalInjurie.setFatalInjuryId(applicationControlMB.addFatalReservedIdentifiers());
                newFatalInjurie.setInputTimestamp(new Date());
                newFatalInjurie.setInjuryId(injuriesFacade.find((short) 11));//es 10 transito
                newFatalInjurie.setUserId(usersFacade.find(1));//usuario que se encuentre logueado
                newFatalInjurie.setVictimId(newVictim);
                newFatalInjuryTraffic = new FatalInjuryTraffic();
                newFatalInjuryTraffic.setFatalInjuryId(newFatalInjurie.getFatalInjuryId());
                newVictim.setTagId(tagsFacade.find(newTag.getTagId()));
                newVictim.setFirstTagId(newVictim.getTagId().getTagId());
                serviceTypesList = new ArrayList<>();
                involvedVehiclesList = new ArrayList<>();
                value = "";
                name = "";
                surname = "";
                dia = "";//dia evento
                mes = "";//mes evento
                ao = "";//año del evento                    
                dia1 = "";//dia de la consulta
                mes1 = "";//mes de la consulta
                ao1 = "";//año de la consulta
                horas = "";//hora evento
                minutos = "";//minuto evento
                ampm = "";//ampm evento
                horas1 = "";//hora consulta
                minutos1 = "";//minuto consulta
                ampm1 = "";//ampm consulta
                hourInt = 0;
                minuteInt = 0;
                narrative = "";
                Object[] arrayInJava = (Object[]) resultSetFileData.getArray(3).getArray();
                for (int posCol = 0; posCol < arrayInJava.length; posCol++) {
                    value = null;
                    String splitColumnAndValue[] = arrayInJava[posCol].toString().split("<=>");
                    relationVar = currentRelationsGroup.findRelationVarByNameFound(splitColumnAndValue[0]);//determino la relacion de variables
                    if (relationVar != null) {
                        switch (DataTypeEnum.convert(relationVar.getFieldType())) {//determino valor a ingresar usando: isNumeric,isAge... etc
                            case text:
                                value = splitColumnAndValue[1];
                                break;
                            case integer:
                                value = isNumeric(splitColumnAndValue[1]);
                                break;
                            case age:
                                value = isAge(splitColumnAndValue[1]);
                                break;
                            case date:
                                value = isDate(splitColumnAndValue[1], relationVar.getDateFormat());
                                break;
                            case military:
                                value = isMilitary(splitColumnAndValue[1]);
                                break;
                            case hour:
                                value = isHour(splitColumnAndValue[1]);
                                break;
                            case minute:
                                value = isMinute(splitColumnAndValue[1]);
                                break;
                            case day:
                                value = isDay(splitColumnAndValue[1]);
                                break;
                            case month:
                                value = isMonth(splitColumnAndValue[1]);
                                break;
                            case year:
                                value = isYear(splitColumnAndValue[1]);
                                break;
                            case percentage:
                                value = isPercentage(splitColumnAndValue[1]);
                                break;
                            case NOVALUE:
                                value = isCategorical(splitColumnAndValue[1], relationVar);
                                break;
                        }
                    }

                    continueProcces = false;
                    if (value != null) {
                        if (value.trim().length() != 0) {
                            continueProcces = true;
                        }
                    }
                    if (continueProcces) {
                        switch (SCC_F_029Enum.convert(relationVar.getNameExpected())) {
                            // ************************************************DATOS PARA LA TABLA victims                                
                            case departamento_evento:
                                break;
                            case municipio_evento:
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
                            case certificado_defuncion:
                                newFatalInjurie.setCode(value);
                                break;
                            case dia_evento://dia del evento
                                dia = value;
                                break;
                            case mes_evento://mes evento
                                mes = value;
                                break;
                            case año_evento://año del evento
                                ao = value;
                                break;
                            case fecha_evento:
                                try {
                                    currentDate = dateFormat.parse(value);
                                    newFatalInjurie.setInjuryDate(currentDate);
                                } catch (ParseException ex) {
                                }
                                break;
                            case hora_evento:
                                horas = value;
                                break;
                            case minuto_evento:
                                minutos = value;
                                break;
                            case am_pm:
                                ampm = value;
                                break;
                            case hora_militar_evento:
                                n = new Date();
                                hourInt = Integer.parseInt(value.substring(0, 2));
                                minuteInt = Integer.parseInt(value.substring(2, 4));
                                n.setHours(hourInt);
                                n.setMinutes(minuteInt);
                                n.setSeconds(0);
                                newFatalInjurie.setInjuryTime(n);
                                break;
                            case direccion_evento:
                                newFatalInjurie.setInjuryAddress(value);
                                break;
                            case barrio_evento:
                                newFatalInjurie.setInjuryNeighborhoodId(Integer.parseInt(value));
                                break;
                            case area_evento://ZONA URBANA O RURAL //SE DETERMINA CON EL BARRIO
                                newFatalInjurie.setAreaId(areasFacade.find(Short.parseShort(value)));
                                break;
                            case clase_accidente:
                                newFatalInjuryTraffic.setAccidentClassId(accidentClassesFacade.find(Short.parseShort(value)));
                                break;
                            case dia_semana_evento:
                                newFatalInjurie.setInjuryDayOfWeek(daysFacade.find(Short.parseShort(value)).getDaysName());
                                break;
                            case numero_victimas_fatales:
                                newFatalInjurie.setVictimNumber(Short.parseShort(value));
                                break;
                            case numero_lesionados_evento:
                                newFatalInjuryTraffic.setNumberNonFatalVictims(Short.parseShort(value));
                                break;
                            case nombres_victima:
                                name = value;
                                break;
                            case apellidos_victima:
                                surname = value;
                                break;
                            case sexo_victima:
                                newVictim.setGenderId(gendersFacade.find(Short.parseShort(value)));
                                break;
                            case tipo_edad_victima:
                                //newVictim.setGenderId(gendersFacade.find(Short.parseShort(value)));
                                newVictim.setAgeTypeId(Short.parseShort(value));
                                break;
                            case edad_victima:
                                newVictim.setVictimAge(Short.parseShort(value));
                                break;
                            case ocupacion_victima:
                                newVictim.setJobId(jobsFacade.find(Integer.parseInt(value)));
                                break;
                            case municipio_residencia:
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
                            case barrio_residencia_victima:
                                newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(value)));
                                break;
                            case tipo_identificacion_victima:
                                newVictim.setTypeId(idTypesFacade.find(Short.parseShort(value)));
                                break;
                            case numero_identificacion_victima:
                                newVictim.setVictimNid(value);
                                break;
                            case procedencia_victima:
                                splitArray = value.split("-");
                                if (splitArray.length == 1) {
                                    newFatalInjurie.setVictimPlaceOfOrigin(value + "-0-0");
                                }
                                if (splitArray.length == 2) {
                                    newFatalInjurie.setVictimPlaceOfOrigin(value + "-0");
                                }
                                if (splitArray.length == 3) {
                                    newFatalInjurie.setVictimPlaceOfOrigin(value);
                                }

                                break;
                            case caracteristicas_victima:
                                newFatalInjuryTraffic.setVictimCharacteristicId(victimCharacteristicsFacade.find(Short.parseShort(value)));
                                break;
                            case medidas_proteccion:
                                newFatalInjuryTraffic.setProtectionMeasureId(protectiveMeasuresFacade.find(Short.parseShort(value)));
                                break;
                            case vehiculo_involucrado_victima:
                                newFatalInjuryTraffic.setInvolvedVehicleId(involvedVehiclesFacade.find(Short.parseShort(value)));
                                break;
                            case vehiculo_involucrado_contraparte_1:
                            case vehiculo_involucrado_contraparte_2:
                            case vehiculo_involucrado_contraparte_3:
                                newCounterpartInvolvedVehicle = new CounterpartInvolvedVehicle();
                                newCounterpartInvolvedVehicle.setInvolvedVehicleId(involvedVehiclesFacade.find(Short.parseShort(value)));
                                newCounterpartInvolvedVehicle.setFatalInjuryId(newFatalInjurie);
                                newCounterpartInvolvedVehicle.setCounterpartInvolvedVehicleId(counterpartInvolvedVehicleFacade.findMax() + 1 + involvedVehiclesList.size());
                                involvedVehiclesList.add(newCounterpartInvolvedVehicle);
                                break;
                            case tipo_servicio_vehiculo_victima:
                                newFatalInjuryTraffic.setServiceTypeId(serviceTypesFacade.find(Short.parseShort(value)));
                                break;
                            case tipo_servicio_contraparte_1:
                            case tipo_servicio_contraparte_2:
                            case tipo_servicio_contraparte_3:
                                newCounterpartServiceType = new CounterpartServiceType();
                                newCounterpartServiceType.setServiceTypeId(serviceTypesFacade.find(Short.parseShort(value)));
                                newCounterpartServiceType.setFatalInjuryId(newFatalInjurie);
                                newCounterpartServiceType.setCounterpartServiceTypeId(counterpartServiceTypeFacade.findMax() + 1 + serviceTypesList.size());
                                serviceTypesList.add(newCounterpartServiceType);
                                break;
                            case narracion_evento:
                            case narracion_evento_1:
                            case narracion_evento_2:
                                narrative = narrative + " " + value;
                                break;
                            case nivel_alcohol_victima:
                                newFatalInjurie.setAlcoholLevelVictim(Short.parseShort(value));
                                break;
                            case detalle_nivel_alcohol_victima:
                                newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find(Short.parseShort(value)));//con dato
                                break;
                            case nivel_alcohol_culpable:
                                newFatalInjuryTraffic.setAlcoholLevelCounterpart(Short.parseShort(value));
                                break;
                            case detalle_nivel_alcohol_culpable:
                                newFatalInjuryTraffic.setAlcoholLevelCounterpartId(alcoholLevelsFacade.find(Short.parseShort(value)));//con dato
                                break;
                            default:
                        }
                    }
                }

                //SI NO HAY FECHA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES dia_evento, mes_evento, año_evento
                if (newFatalInjurie.getInjuryDate() == null) {
                    dia = haveData(dia);
                    mes = haveData(mes);
                    ao = haveData(ao);
                    if (dia != null && mes != null && ao != null) {
                        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                        Date fechaI;
                        fechaI = formato.parse(dia + "/" + mes + "/" + ao);
                        newFatalInjurie.setInjuryDate(fechaI);
                    }
                }
                //SI NO HAY HORA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES hora_evento,minuto_evento,am_pm
                if (newFatalInjurie.getInjuryTime() == null) {
                    horas = haveData(horas);
                    minutos = haveData(minutos);
                    ampm = haveData(ampm);
                    if (horas != null && minutos != null && ampm != null) {
                        hourInt = Integer.parseInt(horas);
                        minuteInt = Integer.parseInt(minutos);
                        if (ampm.compareTo("2") == 0) {
                            hourInt = hourInt + 12;
                            if (hourInt == 24) {
                                hourInt = 0;
                            }
                        }
                        currentDate = new Date();
                        currentDate.setHours(hourInt);
                        currentDate.setMinutes(minuteInt);
                        currentDate.setSeconds(0);
                        newFatalInjurie.setInjuryTime(currentDate);
                    }
                }
                //SI NO HAY DIA DE LA SEMANA DEL EVENTO SE CALCULA
                if (newFatalInjurie.getInjuryDate() != null) {
                    if (newFatalInjurie.getInjuryDayOfWeek() == null) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(newFatalInjurie.getInjuryDate());
                        newFatalInjurie.setInjuryDayOfWeek(intToDay(cal.get(Calendar.DAY_OF_WEEK)));
                    }
                }

                //ASIGNO LA NARRACION DE LOS HECHOS
                if (narrative.trim().length() != 0) {
                    newFatalInjurie.setInjuryDescription(narrative);
                }
                //SI NO SE DETERMINA EL BARRIO SE COLOCA SIN DATO URBANO
                if (newVictim.getVictimNeighborhoodId() == null) {
                    newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find((int) 52001));
                }
                //SI NO SE DETERMINA EL BARRIO SE COLOCA SIN DATO URBANO
                if (newFatalInjurie.getInjuryNeighborhoodId() == null) {
                    newFatalInjurie.setInjuryNeighborhoodId((int) 52001);
                }

                //UNION DE NOMBRES Y APELLIOS
                name = name + " " + surname;
                if (name.trim().length() > 1) {
                    newVictim.setVictimName(name.trim());
                }
                //EDAD Y TIPO DE EDAD
                if (newVictim.getVictimAge() != null) {//HAY EDAD 
                    if (newVictim.getAgeTypeId() == null) {//no hay tipo de edad
                        newVictim.setAgeTypeId((short) 1);//tiṕo de edad años
                    }
                } else {
                    newVictim.setAgeTypeId((short) 4);//tiṕo de edad sin determinar
                }
                //SI NO SE DETERMINA LA EDAD VERIFICAR SI HAY FECHA DE NACIMIENTO
                if (newVictim.getVictimDateOfBirth() != null) {
                    if (newVictim.getVictimAge() == null) {
                        int birthMonths;
                        int eventMonths;

                        Calendar systemCalendar = Calendar.getInstance();
                        Calendar birthCalendar = Calendar.getInstance();
                        birthCalendar.setTime(newVictim.getVictimDateOfBirth());

                        try {//DETERMINO LA EDAD EN MESES
                            birthMonths = birthCalendar.get(Calendar.YEAR);
                            birthMonths = birthMonths * 12;
                            birthMonths = birthMonths + birthCalendar.get(Calendar.MONTH);
                            if (newFatalInjurie.getInjuryDate() != null) {//SE CALCULA EDAD SEGUN LA FECHA DE EVENTO
                                systemCalendar.setTime(newFatalInjurie.getInjuryDate());
                            }
                            eventMonths = systemCalendar.get(Calendar.YEAR);
                            eventMonths = eventMonths * 12;
                            eventMonths = eventMonths + systemCalendar.get(Calendar.MONTH);

                            int ageMonths = eventMonths - birthMonths;
                            if (ageMonths < 0) {
                                System.out.println("ERROR fecha de nacimiento mayor a la del sistema o evento: ");
                            } else {
                                //SI EXISTE EDAD Y NO HAY TIPO DE EDAD DETERMINARLA EN AÑOS
                                int ageYears = (int) (ageMonths / 12);
                                if (ageYears == 0) {
                                    ageYears = 1;
                                }
                                newVictim.setVictimAge((short) ageYears);
                                newVictim.setAgeTypeId((short) 1);//aqui por defecto seria sin dato, si no se conoce
                            }
                        } catch (Exception ex) {
                            System.out.println("Error 9 en " + this.getClass().getName() + ":" + ex.toString());
                        }
                    }
                }

                if (newVictim.getVictimNid() == null) {//NO HAY NUMERO DE IDENTIFICACION 
                    newVictim.setVictimNid(String.valueOf(genNnFacade.findMax() + 1));//asigno un consecutivo a la identificacion
                    newVictim.setVictimClass((short) 2);//nn
                    if (newVictim.getTypeId() == null) {//no hay tipo de identificacion
                        if (newVictim.getVictimAge() != null && newVictim.getAgeTypeId() != null && newVictim.getAgeTypeId() == 1) {//HAY EDAD Y HAY tipo de edad                            
                            if (newVictim.getVictimAge() > 17) {
                                newVictim.setTypeId(idTypesFacade.find((short) 6));//adulto sin identificacion                                
                            } else {
                                newVictim.setTypeId(idTypesFacade.find((short) 7));//menor sin identificacion
                            }
                        } else {//NO HAY EDAD
                            newVictim.setTypeId(idTypesFacade.find((short) 9));//tipo de identificacoin sin determinar
                        }
                    }
                    int newGenNnId = genNnFacade.findMax() + 1;
                    connectionJdbcMB.non_query("UPDATE gen_nn SET cod_nn = " + newGenNnId + " where cod_nn IN (SELECT MAX(cod_nn) from gen_nn)");
                } else {//HAY NUMERO DE IDENTIFICACION
                    if (newVictim.getTypeId() == null) {//no hay tipo de identificacion
                        if (newVictim.getVictimAge() != null && newVictim.getAgeTypeId() != null && newVictim.getAgeTypeId() == 1) {//HAY EDAD Y HAY tipo de edad                            
                            if (newVictim.getVictimAge() > 17) {
                                newVictim.setTypeId(idTypesFacade.find((short) 6));//adulto sin identificacion                                
                            } else {
                                newVictim.setTypeId(idTypesFacade.find((short) 7));//menor sin identificacion
                            }
                        } else {//NO HAY EDAD
                            newVictim.setTypeId(idTypesFacade.find((short) 9));//tipo de identificacoin sin determinar
                        }
                    }
                }

                //CORRESPONDENCIA ENTRE EDAD Y TIPO DE IDENTIFICACION
                if (newVictim.getTypeId() != null) {//no hay tipo de identificacion
                    if (newVictim.getVictimAge() != null) {//HAY EDAD Y HAY tipo de edad
                        if (newVictim.getVictimAge() < 18) {//menor de edad
                            if (newVictim.getTypeId().getTypeId() == (short) 1 ||//cedula de ciudadania
                                    newVictim.getTypeId().getTypeId() == (short) 2 ||//cedula de extranjeria
                                    newVictim.getTypeId().getTypeId() == (short) 3 ||//pasaporte
                                    newVictim.getTypeId().getTypeId() == (short) 6) {//adulto sin identificacion
                                newVictim.setTypeId(idTypesFacade.find((short) 9));//sin determinar
                            }
                        } else {//mayor de edad
                            if (newVictim.getTypeId().getTypeId() == (short) 5 ||//tarjeta de identidad
                                    newVictim.getTypeId().getTypeId() == (short) 4 ||//registro civil
                                    newVictim.getTypeId().getTypeId() == (short) 7) {//menor sin identificacion
                                newVictim.setTypeId(idTypesFacade.find((short) 9));//sin determinar
                            }
                        }

                    }
                }
                //AGREGO LAS LISTAS
                if (!involvedVehiclesList.isEmpty()) {
                    newFatalInjurie.setCounterpartInvolvedVehicleList(involvedVehiclesList);
                }
                if (!serviceTypesList.isEmpty()) {
                    newFatalInjurie.setCounterpartServiceTypeList(serviceTypesList);
                }
                //PERSISTO
                try {
                    victimsFacade.create(newVictim);
                    fatalInjuriesFacade.create(newFatalInjurie);
                    fatalInjuryTrafficFacade.create(newFatalInjuryTraffic);
                    applicationControlMB.removeFatalReservedIdentifiers(newFatalInjurie.getFatalInjuryId());
                    applicationControlMB.removeVictimsReservedIdentifiers(newVictim.getVictimId());
                } catch (Exception e) {
                    System.out.println("Error 10 en " + this.getClass().getName() + ":" + e.toString());
                }
                tuplesProcessed++;
                progress = (int) (tuplesProcessed * 100) / tuplesNumber;
                System.out.println("PROGRESO INGRESANDO TRANSITO: " + String.valueOf(progress));
            }
            progress = 100;
            System.out.println("PROGRESO INGRESANDO TRANSITO: " + String.valueOf(progress));
        } catch (SQLException ex) {
            System.out.println("Error 11 en " + this.getClass().getName() + ":" + ex.toString());
        } catch (Exception ex) {
            System.out.println("Error 12 en " + this.getClass().getName() + ":" + ex.toString());
        }
    }

    /**
     * allows to load the record corresponding to the tab the suicide.
     */
    public void registerSCC_F_030() {
        /**
         * *********************************************************************
         * CARGA DE REGISTROS DE LA FICHA SUICIDIOS
         * ********************************************************************
         */
        tuplesNumber = 0;
        tuplesProcessed = 0;
        progress = 0;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            tuplesNumber = determineTuplesNumber();//determino numero de tuplas  
            resultSetFileData = determineRecords();//resulset con los registros a procesar

            newUngroupedTags = new UngroupedTags();
            newUngroupedTags.setUngroupedTagId(ungroupedTagsFacade.findMax() + 1);
            newUngroupedTags.setUngroupedTagName(determineTagName(projectsMB.getCurrentProjectName()));
            newUngroupedTags.setUngroupedTagDate(new Date());
            newUngroupedTags.setFormId(nameForm);
            newUngroupedTags.setCurrentTagId(ungroupedTagsFacade.findMax() + 1);
            ungroupedTagsFacade.create(newUngroupedTags);

            newTag = new Tags();//VARIABLES PARA CONJUNTOS DE REGISTROS
            newTag.setTagId(ungroupedTagsFacade.findMax());
            newTag.setTagName(determineTagName(projectsMB.getCurrentProjectName()));
            newTag.setTagFileInput(projectsMB.getCurrentFileName());
            newTag.setTagFileStored(projectsMB.getCurrentFileName());
            newTag.setFormId(formsFacade.find(nameForm));
            tagsFacade.create(newTag);

            lastTagNameCreated = newTag.getTagName();

            while (resultSetFileData.next()) {//recorro cada uno de los registros de la tabla temp                    
                Victims newVictim = new Victims();
                newVictim.setVictimId(applicationControlMB.addVictimsReservedIdentifiers());
                newVictim.setVictimClass((short) 1);
                FatalInjuries newFatalInjurie = new FatalInjuries();
                newFatalInjurie.setFatalInjuryId(applicationControlMB.addFatalReservedIdentifiers());
                newFatalInjurie.setInputTimestamp(new Date());
                newFatalInjurie.setUserId(usersFacade.find(1));//usuario que se encuentre logueado
                newFatalInjurie.setVictimId(newVictim);
                newFatalInjurySuicide = new FatalInjurySuicide();
                newFatalInjurySuicide.setFatalInjuryId(newFatalInjurie.getFatalInjuryId());
                newVictim.setTagId(tagsFacade.find(newTag.getTagId()));
                newVictim.setFirstTagId(newVictim.getTagId().getTagId());
                newFatalInjurie.setInjuryId(injuriesFacade.find((short) 12));//es 12 por ser suicidio
                value = "";
                name = "";
                surname = "";
                dia = "";//dia evento
                mes = "";//mes evento
                ao = "";//año del evento                    
                dia1 = "";//dia de la consulta
                mes1 = "";//mes de la consulta
                ao1 = "";//año de la consulta
                horas = "";//hora evento
                minutos = "";//minuto evento
                ampm = "";//ampm evento
                horas1 = "";//hora consulta
                minutos1 = "";//minuto consulta
                ampm1 = "";//ampm consulta
                hourInt = 0;
                minuteInt = 0;
                narrative = "";
                Object[] arrayInJava = (Object[]) resultSetFileData.getArray(3).getArray();
                for (int posCol = 0; posCol < arrayInJava.length; posCol++) {
                    value = null;
                    String splitColumnAndValue[] = arrayInJava[posCol].toString().split("<=>");
                    relationVar = currentRelationsGroup.findRelationVarByNameFound(splitColumnAndValue[0]);//determino la relacion de variables
                    if (relationVar != null) {
                        switch (DataTypeEnum.convert(relationVar.getFieldType())) {//determino valor a ingresar usando: isNumeric,isAge... etc
                            case text:
                                value = splitColumnAndValue[1];
                                break;
                            case integer:
                                value = isNumeric(splitColumnAndValue[1]);
                                break;
                            case age:
                                value = isAge(splitColumnAndValue[1]);
                                break;
                            case date:
                                value = isDate(splitColumnAndValue[1], relationVar.getDateFormat());
                                break;
                            case military:
                                value = isMilitary(splitColumnAndValue[1]);
                                break;
                            case hour:
                                value = isHour(splitColumnAndValue[1]);
                                break;
                            case minute:
                                value = isMinute(splitColumnAndValue[1]);
                                break;
                            case day:
                                value = isDay(splitColumnAndValue[1]);
                                break;
                            case month:
                                value = isMonth(splitColumnAndValue[1]);
                                break;
                            case year:
                                value = isYear(splitColumnAndValue[1]);
                                break;
                            case percentage:
                                value = isPercentage(splitColumnAndValue[1]);
                                break;
                            case NOVALUE:
                                value = isCategorical(splitColumnAndValue[1], relationVar);
                                break;
                        }
                    }

                    continueProcces = false;
                    if (value != null) {
                        if (value.trim().length() != 0) {
                            continueProcces = true;
                        }
                    }
                    if (continueProcces) {
                        switch (SCC_F_030Enum.convert(relationVar.getNameExpected())) {
                            // ************************************************DATOS PARA LA TABLA victims                                
                            case departamento_evento:
                                break;
                            case municipio_evento:
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
                            case certificado_defuncion:
                                newFatalInjurie.setCode(value);
                                break;
                            case dia_evento://dia del evento
                                dia = value;
                                break;
                            case mes_evento://mes evento
                                mes = value;
                                break;
                            case año_evento://año del evento
                                ao = value;
                                break;
                            case fecha_evento:
                                try {
                                    currentDate = dateFormat.parse(value);
                                    newFatalInjurie.setInjuryDate(currentDate);
                                } catch (ParseException ex) {
                                }
                                break;
                            case hora_evento:
                                horas = value;
                                break;
                            case minuto_evento:
                                minutos = value;
                                break;
                            case am_pm:
                                ampm = value;
                                break;
                            case hora_militar_evento:
                                n = new Date();
                                hourInt = Integer.parseInt(value.substring(0, 2));
                                minuteInt = Integer.parseInt(value.substring(2, 4));
                                n.setHours(hourInt);
                                n.setMinutes(minuteInt);
                                n.setSeconds(0);
                                newFatalInjurie.setInjuryTime(n);
                                break;
                            case direccion_evento:
                                newFatalInjurie.setInjuryAddress(value);
                                break;
                            case barrio_evento:
                                newFatalInjurie.setInjuryNeighborhoodId(Integer.parseInt(value));
                                break;
                            case area_evento://ZONA URBANA O RURAL //SE DETERMINA CON EL BARRIO
                                newFatalInjurie.setAreaId(areasFacade.find(Short.parseShort(value)));
                                break;
                            case clase_lugar_evento:
                                newFatalInjurie.setInjuryPlaceId(placesFacade.find(Short.parseShort(value)));
                                break;
                            case dia_semana_evento:
                                newFatalInjurie.setInjuryDayOfWeek(daysFacade.find(Short.parseShort(value)).getDaysName());
                                break;
                            case numero_victimas_fatales:
                                newFatalInjurie.setVictimNumber(Short.parseShort(value));
                                break;
                            case nombres_victima:
                                name = value;
                                break;
                            case apellidos_victima:
                                surname = value;
                                break;
                            case sexo_victima:
                                newVictim.setGenderId(gendersFacade.find(Short.parseShort(value)));
                                break;
                            case tipo_edad_victima:
                                newVictim.setAgeTypeId(Short.parseShort(value));
                                break;
                            case edad_victima:
                                newVictim.setVictimAge(Short.parseShort(value));
                                break;
                            case ocuacion_victima:
                                newVictim.setJobId(jobsFacade.find(Integer.parseInt(value)));
                                break;
                            case municipio_residencia:
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
                            case barrio_residencia_victima:
                                newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(value)));
                                break;
                            case tipo_identificacion_victima:
                                newVictim.setTypeId(idTypesFacade.find(Short.parseShort(value)));
                                break;
                            case numero_identificacion_victima:
                                newVictim.setVictimNid(value);
                                break;
                            case procedencia_victima:
                                splitArray = value.split("-");
                                if (splitArray.length == 1) {
                                    newFatalInjurie.setVictimPlaceOfOrigin(value + "-0-0");
                                }
                                if (splitArray.length == 2) {
                                    newFatalInjurie.setVictimPlaceOfOrigin(value + "-0");
                                }
                                if (splitArray.length == 3) {
                                    newFatalInjurie.setVictimPlaceOfOrigin(value);
                                }
                                break;
                            case arma_o_causa_muerte:
                                newFatalInjurySuicide.setSuicideDeathMechanismId(suicideMechanismsFacade.find(Short.parseShort(value)));
                                break;
                            case eventos_relacionados_evento:
                                newFatalInjurySuicide.setRelatedEventId(relatedEventsFacade.find(Short.parseShort(value)));
                                break;
                            case intentos_previos:
                                newFatalInjurySuicide.setPreviousAttempt(boolean3Facade.find(Short.parseShort(value)));
                                break;
                            case antecedentes_salud_mental:
                                newFatalInjurySuicide.setMentalAntecedent(boolean3Facade.find(Short.parseShort(value)));
                                break;
                            case narracion_evento:
                            case narracion_evento_1:
                            case narracion_evento_2:
                                narrative = narrative + " " + value;
                                break;
                            case nivel_alcohol_victima:
                                newFatalInjurie.setAlcoholLevelVictim(Short.parseShort(value));
                                break;
                            case nivel_alcohol_sin_dato:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 2));
                                }
                                break;
                            case nivel_alcohol_pendiente:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 4));
                                }
                                break;
                            case nivel_alcohol_desconocido:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 3));
                                }
                                break;
                            case nivel_alcohol_negativo:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 5));
                                }
                                break;
                            default:
                        }
                    }
                }
                //ASIGNO LA NARRACION DE LOS HECHOS
                if (narrative.trim().length() != 0) {
                    newFatalInjurie.setInjuryDescription(narrative);
                }

                //SI NO SE DETERMINA EL BARRIO SE COLOCA SIN DATO URBANO
                if (newVictim.getVictimNeighborhoodId() == null) {
                    newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find((int) 52001));
                }
                //SI NO SE DETERMINA EL BARRIO SE COLOCA SIN DATO URBANO
                if (newFatalInjurie.getInjuryNeighborhoodId() == null) {
                    newFatalInjurie.setInjuryNeighborhoodId((int) 52001);
                }

                //SI NO HAY FECHA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES dia_evento, mes_evento, año_evento
                if (newFatalInjurie.getInjuryDate() == null) {
                    dia = haveData(dia);
                    mes = haveData(mes);
                    ao = haveData(ao);
                    if (dia != null && mes != null && ao != null) {
                        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                        Date fechaI;
                        fechaI = formato.parse(dia + "/" + mes + "/" + ao);
                        newFatalInjurie.setInjuryDate(fechaI);
                    }
                }

                //SI NO HAY HORA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES hora_evento,minuto_evento,am_pm
                if (newFatalInjurie.getInjuryTime() == null) {
                    horas = haveData(horas);
                    minutos = haveData(minutos);
                    ampm = haveData(ampm);
                    if (horas != null && minutos != null && ampm != null) {
                        hourInt = Integer.parseInt(horas);
                        minuteInt = Integer.parseInt(minutos);
                        if (ampm.compareTo("2") == 0) {
                            hourInt = hourInt + 12;
                            if (hourInt == 24) {
                                hourInt = 0;
                            }
                        }
                        currentDate = new Date();
                        currentDate.setHours(hourInt);
                        currentDate.setMinutes(minuteInt);
                        currentDate.setSeconds(0);
                        newFatalInjurie.setInjuryTime(currentDate);
                    }
                }


                //SI NO HAY DIA DE LA SEMANA DEL EVENTO SE CALCULA
                if (newFatalInjurie.getInjuryDate() != null) {
                    if (newFatalInjurie.getInjuryDayOfWeek() == null) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(newFatalInjurie.getInjuryDate());
                        newFatalInjurie.setInjuryDayOfWeek(intToDay(cal.get(Calendar.DAY_OF_WEEK)));
                    }
                }

                //UNION DE NOMBRES Y APELLIOS
                name = name + " " + surname;
                if (name.trim().length() > 1) {
                    newVictim.setVictimName(name.trim());
                }
                //EDAD Y TIPO DE EDAD
                if (newVictim.getVictimAge() != null) {//HAY EDAD 
                    if (newVictim.getAgeTypeId() == null) {//no hay tipo de edad
                        newVictim.setAgeTypeId((short) 1);//tiṕo de edad años
                    }
                } else {
                    newVictim.setAgeTypeId((short) 4);//tiṕo de edad sin determinar
                }
                //SI NO SE DETERMINA LA EDAD VERIFICAR SI HAY FECHA DE NACIMIENTO
                if (newVictim.getVictimDateOfBirth() != null) {
                    if (newVictim.getVictimAge() == null) {
                        int birthMonths;
                        int eventMonths;

                        Calendar systemCalendar = Calendar.getInstance();
                        Calendar birthCalendar = Calendar.getInstance();
                        birthCalendar.setTime(newVictim.getVictimDateOfBirth());

                        try {//DETERMINO LA EDAD EN MESES
                            birthMonths = birthCalendar.get(Calendar.YEAR);
                            birthMonths = birthMonths * 12;
                            birthMonths = birthMonths + birthCalendar.get(Calendar.MONTH);
                            if (newFatalInjurie.getInjuryDate() != null) {//SE CALCULA EDAD SEGUN LA FECHA DE EVENTO
                                systemCalendar.setTime(newFatalInjurie.getInjuryDate());
                            }
                            eventMonths = systemCalendar.get(Calendar.YEAR);
                            eventMonths = eventMonths * 12;
                            eventMonths = eventMonths + systemCalendar.get(Calendar.MONTH);

                            int ageMonths = eventMonths - birthMonths;
                            if (ageMonths < 0) {
                                System.out.println("ERROR fecha de nacimiento mayor a la del sistema o evento: ");
                            } else {
                                //SI EXISTE EDAD Y NO HAY TIPO DE EDAD DETERMINARLA EN AÑOS
                                int ageYears = (int) (ageMonths / 12);
                                if (ageYears == 0) {
                                    ageYears = 1;
                                }
                                newVictim.setVictimAge((short) ageYears);
                                newVictim.setAgeTypeId((short) 1);//aqui por defecto seria sin dato, si no se conoce
                            }
                        } catch (Exception ex) {
                            System.out.println("Error 13 en " + this.getClass().getName() + ":" + ex.toString());
                        }
                    }
                }

                if (newVictim.getVictimNid() == null) {//NO HAY NUMERO DE IDENTIFICACION 
                    newVictim.setVictimNid(String.valueOf(genNnFacade.findMax() + 1));//asigno un consecutivo a la identificacion
                    newVictim.setVictimClass((short) 2);//nn
                    if (newVictim.getTypeId() == null) {//no hay tipo de identificacion
                        if (newVictim.getVictimAge() != null && newVictim.getAgeTypeId() != null && newVictim.getAgeTypeId() == 1) {//HAY EDAD Y HAY tipo de edad                            
                            if (newVictim.getVictimAge() > 17) {
                                newVictim.setTypeId(idTypesFacade.find((short) 6));//adulto sin identificacion                                
                            } else {
                                newVictim.setTypeId(idTypesFacade.find((short) 7));//menor sin identificacion
                            }
                        } else {//NO HAY EDAD
                            newVictim.setTypeId(idTypesFacade.find((short) 9));//tipo de identificacoin sin determinar
                        }
                    }
                    int newGenNnId = genNnFacade.findMax() + 1;
                    connectionJdbcMB.non_query("UPDATE gen_nn SET cod_nn = " + newGenNnId + " where cod_nn IN (SELECT MAX(cod_nn) from gen_nn)");
                } else {//HAY NUMERO DE IDENTIFICACION
                    if (newVictim.getTypeId() == null) {//no hay tipo de identificacion
                        if (newVictim.getVictimAge() != null && newVictim.getAgeTypeId() != null && newVictim.getAgeTypeId() == 1) {//HAY EDAD Y HAY tipo de edad                            
                            if (newVictim.getVictimAge() > 17) {
                                newVictim.setTypeId(idTypesFacade.find((short) 6));//adulto sin identificacion                                
                            } else {
                                newVictim.setTypeId(idTypesFacade.find((short) 7));//menor sin identificacion
                            }
                        } else {//NO HAY EDAD
                            newVictim.setTypeId(idTypesFacade.find((short) 9));//tipo de identificacoin sin determinar
                        }
                    }
                }

                //CORRESPONDENCIA ENTRE EDAD Y TIPO DE IDENTIFICACION
                if (newVictim.getTypeId() != null) {//no hay tipo de identificacion
                    if (newVictim.getVictimAge() != null) {//HAY EDAD Y HAY tipo de edad
                        if (newVictim.getVictimAge() < 18) {//menor de edad
                            if (newVictim.getTypeId().getTypeId() == (short) 1 ||//cedula de ciudadania
                                    newVictim.getTypeId().getTypeId() == (short) 2 ||//cedula de extranjeria
                                    newVictim.getTypeId().getTypeId() == (short) 3 ||//pasaporte
                                    newVictim.getTypeId().getTypeId() == (short) 6) {//adulto sin identificacion
                                newVictim.setTypeId(idTypesFacade.find((short) 9));//sin determinar
                            }
                        } else {//mayor de edad
                            if (newVictim.getTypeId().getTypeId() == (short) 5 ||//tarjeta de identidad
                                    newVictim.getTypeId().getTypeId() == (short) 4 ||//registro civil
                                    newVictim.getTypeId().getTypeId() == (short) 7) {//menor sin identificacion
                                newVictim.setTypeId(idTypesFacade.find((short) 9));//sin determinar
                            }
                        }

                    }
                }
                //PERSISTO
                try {
                    victimsFacade.create(newVictim);
                    fatalInjuriesFacade.create(newFatalInjurie);
                    fatalInjurySuicideFacade.create(newFatalInjurySuicide);
                    applicationControlMB.removeFatalReservedIdentifiers(newFatalInjurie.getFatalInjuryId());
                    applicationControlMB.removeVictimsReservedIdentifiers(newVictim.getVictimId());
                } catch (Exception e) {
                    System.out.println("Error 14 en " + this.getClass().getName() + ":" + e.toString());
                }
                tuplesProcessed++;
                progress = (int) (tuplesProcessed * 100) / tuplesNumber;
                System.out.println("PROGRESO INGRESANDO SUICIDIOS: " + String.valueOf(progress));
            }
            progress = 100;
            System.out.println("PROGRESO INGRESANDO SUICIDIOS: " + String.valueOf(progress));
        } catch (SQLException ex) {
            System.out.println("Error 15 en " + this.getClass().getName() + ":" + ex.toString());
        } catch (Exception ex) {
            System.out.println("Error 16 en " + this.getClass().getName() + ":" + ex.toString());
        }
    }

    /**
     * allows to load the record corresponding to the tab the accidental deaths.
     */
    public void registerSCC_F_031() {
        /**
         * *********************************************************************
         * CARGA DE REGISTROS DE LA FICHA MUERTES ACCIDENTALES
         * ********************************************************************
         */
        tuplesNumber = 0;
        tuplesProcessed = 0;
        progress = 0;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            tuplesNumber = determineTuplesNumber();//determino numero de tuplas  
            resultSetFileData = determineRecords();//resulset con los registros a procesar

            newUngroupedTags = new UngroupedTags();
            newUngroupedTags.setUngroupedTagId(ungroupedTagsFacade.findMax() + 1);
            newUngroupedTags.setUngroupedTagName(determineTagName(projectsMB.getCurrentProjectName()));
            newUngroupedTags.setUngroupedTagDate(new Date());
            newUngroupedTags.setFormId(nameForm);
            newUngroupedTags.setCurrentTagId(ungroupedTagsFacade.findMax() + 1);
            ungroupedTagsFacade.create(newUngroupedTags);

            newTag = new Tags();//VARIABLES PARA CONJUNTOS DE REGISTROS
            newTag.setTagId(ungroupedTagsFacade.findMax());
            newTag.setTagName(determineTagName(projectsMB.getCurrentProjectName()));
            newTag.setTagFileInput(projectsMB.getCurrentFileName());
            newTag.setTagFileStored(projectsMB.getCurrentFileName());
            newTag.setFormId(formsFacade.find(nameForm));
            tagsFacade.create(newTag);

            lastTagNameCreated = newTag.getTagName();

            while (resultSetFileData.next()) {//recorro cada uno de los registros de la tabla temp                    
                Victims newVictim = new Victims();
                newVictim.setVictimId(applicationControlMB.addVictimsReservedIdentifiers());
                newVictim.setVictimClass((short) 1);
                FatalInjuries newFatalInjurie = new FatalInjuries();
                newFatalInjurie.setFatalInjuryId(applicationControlMB.addFatalReservedIdentifiers());
                newFatalInjurie.setInputTimestamp(new Date());

                newFatalInjurie.setUserId(usersFacade.find(1));//usuario que se encuentre logueado
                newFatalInjurie.setVictimId(newVictim);
                FatalInjuryAccident newFatalInjuryAccident = new FatalInjuryAccident();
                newFatalInjuryAccident.setFatalInjuryId(newFatalInjurie.getFatalInjuryId());
                newVictim.setTagId(tagsFacade.find(newTag.getTagId()));
                newVictim.setFirstTagId(newVictim.getTagId().getTagId());
                newFatalInjurie.setInjuryId(injuriesFacade.find((short) 13));//es 13 por ser muerte accidental
                value = "";
                name = "";
                surname = "";
                dia = "";//dia evento
                mes = "";//mes evento
                ao = "";//año del evento
                //diacon = "";//dia de la semana cnsulta                                
                dia1 = "";//dia de la consulta
                mes1 = "";//mes de la consulta
                ao1 = "";//año de la consulta
                horas = "";//hora evento
                minutos = "";//minuto evento
                ampm = "";//ampm evento
                horas1 = "";//hora consulta
                minutos1 = "";//minuto consulta
                ampm1 = "";//ampm consulta
                hourInt = 0;
                minuteInt = 0;
                narrative = "";
                Object[] arrayInJava = (Object[]) resultSetFileData.getArray(3).getArray();
                for (int posCol = 0; posCol < arrayInJava.length; posCol++) {
                    value = null;
                    String splitColumnAndValue[] = arrayInJava[posCol].toString().split("<=>");
                    relationVar = currentRelationsGroup.findRelationVarByNameFound(splitColumnAndValue[0]);//determino la relacion de variables
                    if (splitColumnAndValue[0].indexOf("munres") != -1) {
                        splitColumnAndValue[0] = splitColumnAndValue[0] + "";
                    }
                    if (relationVar != null) {
                        switch (DataTypeEnum.convert(relationVar.getFieldType())) {//determino valor a ingresar usando: isNumeric,isAge... etc
                            case text:
                                value = splitColumnAndValue[1];
                                break;
                            case integer:
                                value = isNumeric(splitColumnAndValue[1]);
                                break;
                            case age:
                                value = isAge(splitColumnAndValue[1]);
                                break;
                            case date:
                                value = isDate(splitColumnAndValue[1], relationVar.getDateFormat());
                                break;
                            case military:
                                value = isMilitary(splitColumnAndValue[1]);
                                break;
                            case hour:
                                value = isHour(splitColumnAndValue[1]);
                                break;
                            case minute:
                                value = isMinute(splitColumnAndValue[1]);
                                break;
                            case day:
                                value = isDay(splitColumnAndValue[1]);
                                break;
                            case month:
                                value = isMonth(splitColumnAndValue[1]);
                                break;
                            case year:
                                value = isYear(splitColumnAndValue[1]);
                                break;
                            case percentage:
                                value = isPercentage(splitColumnAndValue[1]);
                                break;
                            case NOVALUE:
                                value = isCategorical(splitColumnAndValue[1], relationVar);
                                break;
                        }
                    }

                    continueProcces = false;
                    if (value != null) {
                        if (value.trim().length() != 0) {
                            continueProcces = true;
                        }
                    }
                    if (continueProcces) {
                        switch (SCC_F_031Enum.convert(relationVar.getNameExpected())) {
                            // ************************************************DATOS PARA LA TABLA victims                                
                            case departamento_evento:
                                break;
                            case municipio_evento:
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
                            case certificado_defuncion:
                                newFatalInjurie.setCode(value);
                                break;
                            case dia_evento://dia del evento
                                dia = value;
                                break;
                            case mes_evento://mes evento
                                mes = value;
                                break;
                            case año_evento://año del evento
                                ao = value;
                                break;
                            case fecha_evento:
                                try {
                                    currentDate = dateFormat.parse(value);
                                    newFatalInjurie.setInjuryDate(currentDate);
                                } catch (ParseException ex) {
                                }
                                break;
                            case hora_evento:
                                horas = value;
                                break;
                            case minuto_evento:
                                minutos = value;
                                break;
                            case am_pm:
                                ampm = value;
                                break;
                            case hora_militar_evento:
                                n = new Date();
                                hourInt = Integer.parseInt(value.substring(0, 2));
                                minuteInt = Integer.parseInt(value.substring(2, 4));
                                n.setHours(hourInt);
                                n.setMinutes(minuteInt);
                                n.setSeconds(0);
                                newFatalInjurie.setInjuryTime(n);
                                break;
                            case direccion_evento:
                                newFatalInjurie.setInjuryAddress(value);
                                break;
                            case barrio_evento:
                                newFatalInjurie.setInjuryNeighborhoodId(Integer.parseInt(value));
                                break;
                            case area_evento://ZONA URBANA O RURAL //SE DETERMINA CON EL BARRIO
                                newFatalInjurie.setAreaId(areasFacade.find(Short.parseShort(value)));
                                break;
                            case clase_lugar_evento:
                                newFatalInjurie.setInjuryPlaceId(placesFacade.find(Short.parseShort(value)));
                                break;
                            case dia_semana_evento:
                                newFatalInjurie.setInjuryDayOfWeek(daysFacade.find(Short.parseShort(value)).getDaysName());
                                break;
                            case numero_victimas_fatales:
                                newFatalInjurie.setVictimNumber(Short.parseShort(value));
                                break;
                            case numero_lesionados_evento:
                                newFatalInjuryAccident.setNumberNonFatalVictims(Short.parseShort(value));
                                break;
                            case nombres_victima:
                                name = value;
                                break;
                            case apellidos_victima:
                                surname = value;
                                break;
                            case sexo_victima:
                                newVictim.setGenderId(gendersFacade.find(Short.parseShort(value)));
                                break;
                            case tipo_edad_victima:
                                newVictim.setAgeTypeId(Short.parseShort(value));
                                break;
                            case edad_victima:
                                newVictim.setVictimAge(Short.parseShort(value));
                                break;
                            case ocupacion_victima:
                                newVictim.setJobId(jobsFacade.find(Integer.parseInt(value)));
                                break;
                            case municipio_residencia:
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
                            case barrio_residencia_victima:
                                newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(value)));
                                break;
                            case tipo_identificacion_victima:
                                newVictim.setTypeId(idTypesFacade.find(Short.parseShort(value)));
                                break;
                            case numero_identificacion_victima:
                                newVictim.setVictimNid(value);
                                break;
                            case procedencia_victima:
                                splitArray = value.split("-");
                                if (splitArray.length == 1) {
                                    newFatalInjurie.setVictimPlaceOfOrigin(value + "-0-0");
                                }
                                if (splitArray.length == 2) {
                                    newFatalInjurie.setVictimPlaceOfOrigin(value + "-0");
                                }
                                if (splitArray.length == 3) {
                                    newFatalInjurie.setVictimPlaceOfOrigin(value);
                                }

                                break;
                            case arma_o_causa_muerte:
                                newFatalInjuryAccident.setDeathMechanismId(accidentMechanismsFacade.find(Short.parseShort(value)));
                                break;
                            case narracion_evento:
                            case narracion_evento_1:
                            case narracion_evento_2:
                                narrative = narrative + " " + value;
                                break;
                            case nivel_alcohol_victima:
                                newFatalInjurie.setAlcoholLevelVictim(Short.parseShort(value));
                                break;
                            case nivel_alcohol_sin_dato:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 2));
                                }
                                break;
                            case nivel_alcohol_pendiente:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 4));
                                }
                                break;
                            case nivel_alcohol_desconocido:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 3));
                                }
                                break;
                            case nivel_alcohol_negativo:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 5));
                                }
                                break;
                            default:

                        }
                    }
                }

                //ASIGNO LA NARRACION DE LOS HECHOS
                if (narrative.trim().length() != 0) {
                    newFatalInjurie.setInjuryDescription(narrative);
                }
                //SI NO SE DETERMINA EL BARRIO SE COLOCA SIN DATO URBANO
                if (newVictim.getVictimNeighborhoodId() == null) {
                    newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find((int) 52001));
                }
                //SI NO SE DETERMINA EL BARRIO SE COLOCA SIN DATO URBANO
                if (newFatalInjurie.getInjuryNeighborhoodId() == null) {
                    newFatalInjurie.setInjuryNeighborhoodId((int) 52001);
                }

                //SI NO HAY FECHA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES dia_evento, mes_evento, año_evento
                if (newFatalInjurie.getInjuryDate() == null) {
                    dia = haveData(dia);
                    mes = haveData(mes);
                    ao = haveData(ao);
                    if (dia != null && mes != null && ao != null) {
                        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                        Date fechaI;
                        fechaI = formato.parse(dia + "/" + mes + "/" + ao);
                        newFatalInjurie.setInjuryDate(fechaI);
                    }
                }

                //SI NO HAY HORA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES hora_evento,minuto_evento,am_pm
                if (newFatalInjurie.getInjuryTime() == null) {
                    horas = haveData(horas);
                    minutos = haveData(minutos);
                    ampm = haveData(ampm);
                    if (horas != null && minutos != null && ampm != null) {
                        hourInt = Integer.parseInt(horas);
                        minuteInt = Integer.parseInt(minutos);
                        if (ampm.compareTo("2") == 0) {
                            hourInt = hourInt + 12;
                            if (hourInt == 24) {
                                hourInt = 0;
                            }
                        }
                        currentDate = new Date();
                        currentDate.setHours(hourInt);
                        currentDate.setMinutes(minuteInt);
                        currentDate.setSeconds(0);
                        newFatalInjurie.setInjuryTime(currentDate);
                    }
                }

                //SI NO HAY DIA DE LA SEMANA DEL EVENTO SE CALCULA
                if (newFatalInjurie.getInjuryDate() != null) {
                    if (newFatalInjurie.getInjuryDayOfWeek() == null) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(newFatalInjurie.getInjuryDate());
                        newFatalInjurie.setInjuryDayOfWeek(intToDay(cal.get(Calendar.DAY_OF_WEEK)));
                    }
                }

                //UNION DE NOMBRES Y APELLIOS
                name = name + " " + surname;
                if (name.trim().length() > 1) {
                    newVictim.setVictimName(name.trim());
                }
                //EDAD Y TIPO DE EDAD
                if (newVictim.getVictimAge() != null) {//HAY EDAD 
                    if (newVictim.getAgeTypeId() == null) {//no hay tipo de edad
                        newVictim.setAgeTypeId((short) 1);//tiṕo de edad años
                    }
                } else {
                    newVictim.setAgeTypeId((short) 4);//tiṕo de edad sin determinar
                }
                //SI NO SE DETERMINA LA EDAD VERIFICAR SI HAY FECHA DE NACIMIENTO
                if (newVictim.getVictimDateOfBirth() != null) {
                    if (newVictim.getVictimAge() == null) {
                        int birthMonths;
                        int eventMonths;

                        Calendar systemCalendar = Calendar.getInstance();
                        Calendar birthCalendar = Calendar.getInstance();
                        birthCalendar.setTime(newVictim.getVictimDateOfBirth());

                        try {//DETERMINO LA EDAD EN MESES
                            birthMonths = birthCalendar.get(Calendar.YEAR);
                            birthMonths = birthMonths * 12;
                            birthMonths = birthMonths + birthCalendar.get(Calendar.MONTH);
                            if (newFatalInjurie.getInjuryDate() != null) {//SE CALCULA EDAD SEGUN LA FECHA DE EVENTO
                                systemCalendar.setTime(newFatalInjurie.getInjuryDate());
                            }
                            eventMonths = systemCalendar.get(Calendar.YEAR);
                            eventMonths = eventMonths * 12;
                            eventMonths = eventMonths + systemCalendar.get(Calendar.MONTH);

                            int ageMonths = eventMonths - birthMonths;
                            if (ageMonths < 0) {
                                System.out.println("ERROR fecha de nacimiento mayor a la del sistema o evento: ");
                            } else {
                                //SI EXISTE EDAD Y NO HAY TIPO DE EDAD DETERMINARLA EN AÑOS
                                int ageYears = (int) (ageMonths / 12);
                                if (ageYears == 0) {
                                    ageYears = 1;
                                }
                                newVictim.setVictimAge((short) ageYears);
                                newVictim.setAgeTypeId((short) 1);//aqui por defecto seria sin dato, si no se conoce
                            }
                        } catch (Exception ex) {
                            System.out.println("Error 17 en " + this.getClass().getName() + ":" + ex.toString());
                        }
                    }
                }

                if (newVictim.getVictimNid() == null) {//NO HAY NUMERO DE IDENTIFICACION 
                    newVictim.setVictimNid(String.valueOf(genNnFacade.findMax() + 1));//asigno un consecutivo a la identificacion
                    newVictim.setVictimClass((short) 2);//nn
                    if (newVictim.getTypeId() == null) {//no hay tipo de identificacion
                        if (newVictim.getVictimAge() != null && newVictim.getAgeTypeId() != null && newVictim.getAgeTypeId() == 1) {//HAY EDAD Y HAY tipo de edad                            
                            if (newVictim.getVictimAge() > 17) {
                                newVictim.setTypeId(idTypesFacade.find((short) 6));//adulto sin identificacion                                
                            } else {
                                newVictim.setTypeId(idTypesFacade.find((short) 7));//menor sin identificacion
                            }
                        } else {//NO HAY EDAD
                            newVictim.setTypeId(idTypesFacade.find((short) 9));//tipo de identificacoin sin determinar
                        }
                    }
                    int newGenNnId = genNnFacade.findMax() + 1;
                    connectionJdbcMB.non_query("UPDATE gen_nn SET cod_nn = " + newGenNnId + " where cod_nn IN (SELECT MAX(cod_nn) from gen_nn)");
                } else {//HAY NUMERO DE IDENTIFICACION
                    if (newVictim.getTypeId() == null) {//no hay tipo de identificacion
                        if (newVictim.getVictimAge() != null && newVictim.getAgeTypeId() != null && newVictim.getAgeTypeId() == 1) {//HAY EDAD Y HAY tipo de edad                            
                            if (newVictim.getVictimAge() > 17) {
                                newVictim.setTypeId(idTypesFacade.find((short) 6));//adulto sin identificacion                                
                            } else {
                                newVictim.setTypeId(idTypesFacade.find((short) 7));//menor sin identificacion
                            }
                        } else {//NO HAY EDAD
                            newVictim.setTypeId(idTypesFacade.find((short) 9));//tipo de identificacoin sin determinar
                        }
                    }
                }

                //CORRESPONDENCIA ENTRE EDAD Y TIPO DE IDENTIFICACION
                if (newVictim.getTypeId() != null) {//no hay tipo de identificacion
                    if (newVictim.getVictimAge() != null) {//HAY EDAD Y HAY tipo de edad
                        if (newVictim.getVictimAge() < 18) {//menor de edad
                            if (newVictim.getTypeId().getTypeId() == (short) 1 ||//cedula de ciudadania
                                    newVictim.getTypeId().getTypeId() == (short) 2 ||//cedula de extranjeria
                                    newVictim.getTypeId().getTypeId() == (short) 3 ||//pasaporte
                                    newVictim.getTypeId().getTypeId() == (short) 6) {//adulto sin identificacion
                                newVictim.setTypeId(idTypesFacade.find((short) 9));//sin determinar
                            }
                        } else {//mayor de edad
                            if (newVictim.getTypeId().getTypeId() == (short) 5 ||//tarjeta de identidad
                                    newVictim.getTypeId().getTypeId() == (short) 4 ||//registro civil
                                    newVictim.getTypeId().getTypeId() == (short) 7) {//menor sin identificacion
                                newVictim.setTypeId(idTypesFacade.find((short) 9));//sin determinar
                            }
                        }

                    }
                }

                //PERSISTO
                try {
                    victimsFacade.create(newVictim);
                    fatalInjuriesFacade.create(newFatalInjurie);
                    fatalInjuryAccidentFacade.create(newFatalInjuryAccident);
                    applicationControlMB.removeFatalReservedIdentifiers(newFatalInjurie.getFatalInjuryId());
                    applicationControlMB.removeVictimsReservedIdentifiers(newVictim.getVictimId());
                } catch (Exception e) {
                    System.out.println("Error 18 en " + this.getClass().getName() + ":" + e.toString());
                }
                tuplesProcessed++;
                progress = (int) (tuplesProcessed * 100) / tuplesNumber;
                System.out.println("PROGRESO INGRESANDO ACCIDENTALES: " + String.valueOf(progress));
            }
            progress = 100;
            System.out.println("PROGRESO INGRESANDO ACCIDENTALES: " + String.valueOf(progress));
        } catch (SQLException ex) {
            System.out.println("Error 19 en " + this.getClass().getName() + ":" + ex.toString());
        } catch (Exception ex) {
            System.out.println("Error 20 en " + this.getClass().getName() + ":" + ex.toString());
        }
    }

    /**
     * load the records of the LCENF tab ( injuries of external causes no
     * Fatales).
     */
    public void registerSCC_F_032() {
        /**
         * *********************************************************************
         * CARGA DE REGISTROS DE LA FICHA LCENF
         * ********************************************************************
         */
        tuplesNumber = 0;
        tuplesProcessed = 0;
        progress = 0;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            tuplesNumber = determineTuplesNumber();//determino numero de tuplas  
            resultSetFileData = determineRecords();//resulset con los registros a procesar
            newUngroupedTags = new UngroupedTags();
            newUngroupedTags.setUngroupedTagId(ungroupedTagsFacade.findMax() + 1);
            newUngroupedTags.setUngroupedTagName(determineTagName(projectsMB.getCurrentProjectName()));
            newUngroupedTags.setUngroupedTagDate(new Date());
            newUngroupedTags.setFormId(nameForm);
            newUngroupedTags.setCurrentTagId(ungroupedTagsFacade.findMax() + 1);
            ungroupedTagsFacade.create(newUngroupedTags);

            newTag = new Tags();//VARIABLES PARA CONJUNTOS DE REGISTROS
            newTag.setTagId(ungroupedTagsFacade.findMax());
            newTag.setTagName(determineTagName(projectsMB.getCurrentProjectName()));
            newTag.setTagFileInput(projectsMB.getCurrentFileName());
            newTag.setTagFileStored(projectsMB.getCurrentFileName());
            newTag.setFormId(formsFacade.find(nameForm));
            tagsFacade.create(newTag);
            lastTagNameCreated = newTag.getTagName();
            while (resultSetFileData.next()) {//recorro cada uno de los registros de la tabla temp                    
                Victims newVictim = new Victims();
                //newVictim.setVictimId(victimsFacade.findMax() + 1);
                newVictim.setVictimId(applicationControlMB.addVictimsReservedIdentifiers());
                newVictim.setVictimClass((short) 1);
                newVictim.setTagId(tagsFacade.find(newTag.getTagId()));
                newVictim.setFirstTagId(newVictim.getTagId().getTagId());
                NonFatalInjuries newNonFatalInjury = new NonFatalInjuries();
                //newNonFatalInjury.setNonFatalInjuryId(nonFatalInjuriesFacade.findMax() + 1);
                newNonFatalInjury.setNonFatalInjuryId(applicationControlMB.addNonfatalReservedIdentifiers());
                NonFatalDomesticViolence newNonFatalDomesticViolence = new NonFatalDomesticViolence();
                newNonFatalDomesticViolence.setNonFatalInjuryId(newNonFatalInjury.getNonFatalInjuryId());
                newNonFatalDomesticViolence.setNonFatalInjuries(newNonFatalInjury);

                Injuries selectInjuryDetermined = null;
                newNonFatalInjury.setInputTimestamp(new Date());
                newNonFatalInjury.setUserId(usersFacade.find(1));//usuario que se encuentre logueado
                newNonFatalTransport = new NonFatalTransport();//nuevo non_fatal_transport
                newNonFatalTransport.setNonFatalInjuryId(newNonFatalInjury.getNonFatalInjuryId());
                newNonFatalInterpersonal = new NonFatalInterpersonal();//nuevo non_fatal_Interpersonal
                newNonFatalInterpersonal.setNonFatalInjuryId(newNonFatalInjury.getNonFatalInjuryId());
                newNonFatalSelfInflicted = new NonFatalSelfInflicted();//nuevo non_fatal_Self-Inflicted
                newNonFatalSelfInflicted.setNonFatalInjuryId(newNonFatalInjury.getNonFatalInjuryId());
                List<SecurityElements> securityElementList = new ArrayList<>();//lista non_fatal_transport_security_element                
                List<AbuseTypes> abuseTypesList = new ArrayList<>();//lista domestic_violence_abuse_type
                List<AggressorTypes> aggressorTypesList = new ArrayList<>();//lista domestic_violence_aggressor_type                
                List<AnatomicalLocations> anatomicalLocationsList = new ArrayList<>();//lista non_fatal_anatomical_location
                List<KindsOfInjury> kindsOfInjurysList = new ArrayList<>();//lista non_fatal_kind_of_injury
                List<Diagnoses> diagnosesList = new ArrayList<>();//lista non_fatal_diagnosis
                List<VulnerableGroups> vulnerableGroupList = new ArrayList<>();// lista vector victim_vulnerable_group
                List<Others> othersList = new ArrayList<>();
                value = "";
                name = "";
                surname = "";
                dia = "";//dia evento
                mes = "";//mes evento
                ao = "";//año del evento
                //diacon = "";//dia de la semana cnsulta                                
                dia1 = "";//dia de la consulta
                mes1 = "";//mes de la consulta
                ao1 = "";//año de la consulta
                horas = "";//hora evento
                minutos = "";//minuto evento
                ampm = "";//ampm evento
                horas1 = "";//hora consulta
                minutos1 = "";//minuto consulta
                ampm1 = "";//ampm consulta
                hourInt = 0;
                minuteInt = 0;
                Object[] arrayInJava = (Object[]) resultSetFileData.getArray(3).getArray();
                for (int posCol = 0; posCol < arrayInJava.length; posCol++) {
                    value = null;
                    String splitColumnAndValue[] = arrayInJava[posCol].toString().split("<=>");
                    relationVar = currentRelationsGroup.findRelationVarByNameFound(splitColumnAndValue[0]);//determino la relacion de variables
                    if (relationVar != null) {
                        switch (DataTypeEnum.convert(relationVar.getFieldType())) {//determino valor a ingresar usando: isNumeric,isAge... etc
                            case text:
                                value = splitColumnAndValue[1];
                                break;
                            case integer:
                                value = isNumeric(splitColumnAndValue[1]);
                                break;
                            case age:
                                value = isAge(splitColumnAndValue[1]);
                                break;
                            case date:
                                value = isDate(splitColumnAndValue[1], relationVar.getDateFormat());
                                break;
                            case military:
                                value = isMilitary(splitColumnAndValue[1]);
                                break;
                            case hour:
                                value = isHour(splitColumnAndValue[1]);
                                break;
                            case minute:
                                value = isMinute(splitColumnAndValue[1]);
                                break;
                            case day:
                                value = isDay(splitColumnAndValue[1]);
                                break;
                            case month:
                                value = isMonth(splitColumnAndValue[1]);
                                break;
                            case year:
                                value = isYear(splitColumnAndValue[1]);
                                break;
                            case percentage:
                                value = isPercentage(splitColumnAndValue[1]);
                                break;
                            case NOVALUE:
                                try {
                                    value = isCategorical(splitColumnAndValue[1], relationVar);
                                } catch (ArrayIndexOutOfBoundsException ex) {
                                    System.out.println("Captura excepcion cuando splitColumnAndValue no es un arreglo valido. La varaible value conserva el valor de null.");
                                }
                                break;
                        }
                    }

                    continueProcces = false;
                    if (value != null) {
                        //if (value.compareTo("87450201") == 0 ) {System.out.print("-");}
                        if (value.trim().length() != 0) {
                            continueProcces = true;
                        }
                    }
                    if (continueProcces) {
                        switch (SCC_F_032Enum.convert(relationVar.getNameExpected())) {
                            // ************************************************DATOS PARA LA TABLA victims                                
                            case primer_nombre:
                                if (name.trim().length() != 0) {
                                    name = value + " " + name;
                                } else {
                                    name = value;
                                }
                                break;
                            case segundo_nombre:
                                if (name.trim().length() != 0) {
                                    name = name + " " + value;
                                } else {
                                    name = value;
                                }
                                break;
                            case primer_apellido:
                                if (surname.trim().length() != 0) {
                                    surname = value + " " + surname;
                                } else {
                                    surname = value;
                                }
                                break;
                            case segundo_apellido:
                                if (surname.trim().length() != 0) {
                                    surname = surname + " " + value;
                                } else {
                                    surname = value;
                                }
                                break;
                            case tipo_identificacion_victima:
                                newVictim.setTypeId(idTypesFacade.find(Short.parseShort(value)));
                                break;
                            case numero_identificacion_victima:
                                newVictim.setVictimNid(value);
                                break;
                            case aseguradora:
                                newVictim.setInsuranceId(insuranceFacade.find(value));
                                break;
                            case fecha_nacimiento:
                                try {
                                    currentDate = dateFormat.parse(value);
                                    newVictim.setVictimDateOfBirth(currentDate);
                                } catch (ParseException ex) {
                                }
                                break;
                            case medida_edad:
                                newVictim.setAgeTypeId(Short.parseShort(value));
                                break;
                            case edad_paciente:
                                newVictim.setVictimAge(Short.parseShort(value));
                                break;
                            case sexo_paciente:
                                newVictim.setGenderId(gendersFacade.find(Short.parseShort(value)));
                                break;
                            case ocupacion_paciente:
                                try {
                                    newVictim.setJobId(jobsFacade.find(Integer.parseInt(value)));
                                } catch (Exception e) {
                                }
                                break;
                            case grupo_etnico:
                                newVictim.setEthnicGroupId(ethnicGroupsFacade.find(Short.parseShort(value)));
                                break;
                            case barrio_residencia_victima://barrio de barrio_residencia_victima
                                newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(value)));
                                break;
                            case direccion_residencia://direccion de barrio_residencia_victima
                                newVictim.setVictimAddress(value);
                                break;
                            case telefono_residencia:
                                newVictim.setVictimTelephone(value);
                                break;
                            case departamento_residencia:
                                newVictim.setResidenceDepartment(Short.parseShort(value));
                                break;
                            case municipio_residencia://municipio barrio_residencia_victima
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_transport
                            case tipo_transporte_lesionado:
                                newNonFatalTransport.setTransportTypeId(transportTypesFacade.find(Short.parseShort(value)));
                                selectInjuryDetermined = injuriesFacade.find((short) 51);
                                break;
                            case tipo_transporte_contraparte:
                                newNonFatalTransport.setTransportCounterpartId(transportCounterpartsFacade.find(Short.parseShort(value)));
                                selectInjuryDetermined = injuriesFacade.find((short) 51);
                                break;
                            case tipo_usuario_transporte:
                                newNonFatalTransport.setTransportUserId(transportUsersFacade.find(Short.parseShort(value)));
                                selectInjuryDetermined = injuriesFacade.find((short) 51);
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_interpersonal
                            case antecedentes_previos_agresion://boleano->previous_antecedent 
                                newNonFatalInterpersonal.setPreviousAntecedent(boolean3Facade.find(Short.parseShort(value)));
                                if (value.equals("1")) {//SI
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case relacion_agresor_victima://categorico->relationships_to_victim                                    
                                newNonFatalInterpersonal.setRelationshipVictimId(relationshipsToVictimFacade.find(Short.parseShort(value)));
                                selectInjuryDetermined = injuriesFacade.find((short) 50);
                                break;
                            case contexto_en_violencia_interpersonal:
                                newNonFatalInterpersonal.setContextId(contextsFacade.find(Short.parseShort(value)));
                                selectInjuryDetermined = injuriesFacade.find((short) 50);
                                break;
                            case sexo_agresores:
                                newNonFatalInterpersonal.setAggressorGenderId(aggressorGendersFacade.find(Short.parseShort(value)));
                                selectInjuryDetermined = injuriesFacade.find((short) 50);
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_selft-inflicted
                            case intento_previo_autoinflingida:
                                newNonFatalSelfInflicted.setPreviousAttempt(boolean3Facade.find(Short.parseShort(value)));//si                                    
                                if (value.equals("1")) {//SI
                                    selectInjuryDetermined = injuriesFacade.find((short) 52);
                                }
                                break;
                            case antecedentes_transtorno_mental:
                                newNonFatalSelfInflicted.setMentalAntecedent(boolean3Facade.find(Short.parseShort(value)));//si                                    
                                if (value.equals("1")) {//SI
                                    selectInjuryDetermined = injuriesFacade.find((short) 52);
                                }
                                break;
                            case factores_precipitantes:
                                newNonFatalSelfInflicted.setPrecipitatingFactorId(precipitatingFactorsFacade.find(Short.parseShort(value)));
                                if (value.equals("1")) {//SI
                                    selectInjuryDetermined = injuriesFacade.find((short) 52);
                                }
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_transport_security_element
                            case uso_elementos_seguridad:
                                if (value.compareTo("2") == 0 || value.compareTo("NO") == 0) {
                                    securityElementList.add(securityElementsFacade.find((short) 6));
                                    selectInjuryDetermined = injuriesFacade.find((short) 51);
                                }
                                break;
                            case usaba_cinturon:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    securityElementList.add(securityElementsFacade.find((short) 1));
                                    selectInjuryDetermined = injuriesFacade.find((short) 51);
                                }
                                break;
                            case usaba_casco_motocicleta:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    securityElementList.add(securityElementsFacade.find((short) 2));
                                    selectInjuryDetermined = injuriesFacade.find((short) 51);
                                }
                                break;
                            case usaba_casco_bicicleta:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    securityElementList.add(securityElementsFacade.find((short) 3));
                                    selectInjuryDetermined = injuriesFacade.find((short) 51);
                                }
                                break;
                            case usaba_chaleco:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    securityElementList.add(securityElementsFacade.find((short) 4));
                                    selectInjuryDetermined = injuriesFacade.find((short) 51);
                                }
                                break;
                            case otro_elemento_seguridad:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    securityElementList.add(securityElementsFacade.find((short) 5));
                                    selectInjuryDetermined = injuriesFacade.find((short) 51);
                                }
                                break;
                            // ************************************************DATOS PARA LA TABLA domestic_violence_abuse_type
                            case maltrato_fisico:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 1));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case maltrato_psicologico:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 2));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case maltrato_abuso_sexual:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 3));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case maltrato_negligencia:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 4));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case maltrato_abandono:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 5));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case maltrato_institucional:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 6));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case maltrato_sin_dato:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 7));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            // ************************************************DATOS PARA LA TABLA domestic_violence_aggressor_type
                            case agresor_padre:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 1));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case agresor_madre:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 2));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case agresor_padrastro:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 3));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case agresor_madrastra:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 4));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case agresor_conyuge:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 5));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case agresor_hermano:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 6));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case agresor_hijo:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 7));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case agresor_otro:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 8));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case agresor_sin_dato:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 9));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_anatomical_location
                            case sitio_anatomico_sistemico:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 1));
                                }
                                break;
                            case sitio_anatomico_craneo:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 2));
                                }
                                break;
                            case sitio_anatomico_afectado_ojos:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 3));
                                }
                                break;
                            case sitio_anatomico_maxilofacial:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 4));
                                }
                                break;
                            case sitio_anatomico_cuello:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 5));
                                }
                                break;
                            case sitio_anatomico_torax:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 6));
                                }
                                break;
                            case sitio_anatomico_abdomen:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 7));
                                }
                                break;
                            case sitio_anatomico_columna:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 8));
                                }
                                break;
                            case sitio_anatomico_pelvis:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 9));
                                }
                                break;
                            case sitio_anatomico_miembros_superiores:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 10));
                                }
                                break;
                            case sitio_anatomico_miembros_inferiores:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 11));
                                }
                                break;
                            case sitio_anatomico_otro:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 98));
                                }
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_kind_of_injury
                            case naturaleza_lesion_laceracion:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    kindsOfInjurysList.add(new KindsOfInjury((short) 1));
                                }
                                break;
                            case naturaleza_lesion_cortada:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    kindsOfInjurysList.add(new KindsOfInjury((short) 2));
                                }
                                break;
                            case naturaleza_lesion_profunda:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    kindsOfInjurysList.add(new KindsOfInjury((short) 3));
                                }
                                break;
                            case naturaleza_lesion_esgince:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    kindsOfInjurysList.add(new KindsOfInjury((short) 4));
                                }
                                break;
                            case naturaleza_lesion_fractura:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    kindsOfInjurysList.add(new KindsOfInjury((short) 5));
                                }
                                break;
                            case naturaleza_lesion_quemadura:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    kindsOfInjurysList.add(new KindsOfInjury((short) 6));
                                }
                                break;
                            case naturaleza_lesion_contusion:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    kindsOfInjurysList.add(new KindsOfInjury((short) 7));
                                }
                                break;
                            case naturaleza_lesion_organo_sitemica:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    kindsOfInjurysList.add(new KindsOfInjury((short) 8));
                                }
                                break;
                            case naturaleza_lesion_trauma_craneoencefalico:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    kindsOfInjurysList.add(new KindsOfInjury((short) 9));
                                }
                                break;
                            case naturaleza_lesion_otro_tipo:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    kindsOfInjurysList.add(new KindsOfInjury((short) 98));
                                }
                                break;
                            case naturaleza_lesion_no_se_sabe:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    kindsOfInjurysList.add(new KindsOfInjury((short) 99));
                                }
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_diagnosis
                            case CIE_1:
                            case CIE_2:
                            case CIE_3:
                            case CIE_4:
                                Diagnoses d = diagnosesFacade.find(value);
                                boolean addDiagnose = true;
                                if (d != null) {
                                    for (int i = 0; i < diagnosesList.size(); i++) {
                                        if (d.getDiagnosisId().compareTo(diagnosesList.get(i).getDiagnosisId()) == 0) {
                                            addDiagnose = false;
                                            break;
                                        }
                                    }
                                }
                                if (addDiagnose) {
                                    diagnosesList.add(d);
                                }
                                break;
                            // ************************************************DATOS PARA LA TABLA victim_vulnerable_group
                            case desplazado:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    vulnerableGroupList.add(vulnerableGroupsFacade.find((short) 1));
                                }
                                break;
                            case discapacitado:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    vulnerableGroupList.add(vulnerableGroupsFacade.find((short) 2));
                                }
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_injuries
                            case institucion_salud:
                                newNonFatalInjury.setNonFatalDataSourceId(nonFatalDataSourcesFacade.find(Short.parseShort(value)));
                                newNonFatalDomesticViolence.setDomesticViolenceDataSourceId(nonFatalDataSourcesFacade.find(Short.parseShort(value)));
                                break;
                            case barrio_evento://bario donde ocurrio el evento
                                newNonFatalInjury.setInjuryNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(value)));
                                break;
                            case direccion_evento://Direccion del evento
                                newNonFatalInjury.setInjuryAddress(value);
                                break;
                            case fecha_evento:
                                try {
                                    currentDate = dateFormat.parse(value);
                                    newNonFatalInjury.setInjuryDate(currentDate);
                                } catch (ParseException ex) {
                                }
                                break;
                            case dia_semana_evento://duai de la semana evento
                                //newNonFatalInjuries.setInjuryDayOfWeek(value);
                                break;
                            case dia_evento://dia del evento
                                dia = value;
                                break;
                            case mes_evento://mes evento
                                mes = value;
                                break;
                            case año_evento://año del evento
                                ao = value;
                                break;
                            case hora_militar_evento:
                                n = new Date();
                                hourInt = Integer.parseInt(value.substring(0, 2));
                                minuteInt = Integer.parseInt(value.substring(2, 4));
                                n.setHours(hourInt);
                                n.setMinutes(minuteInt);
                                n.setSeconds(0);
                                newNonFatalInjury.setInjuryTime(n);
                                break;
                            case hora_evento://hora evento
                                horas = value;
                                break;
                            case minuto_evento://minuto evento
                                minutos = value;
                                break;
                            case am_pm://ampm evento
                                ampm = value;
                                break;
                            case fecha_consulta:
                                try {
                                    currentDate = dateFormat.parse(value);
                                    newNonFatalInjury.setCheckupDate(currentDate);
                                } catch (ParseException ex) {
                                }
                                break;
                            case dia_semana_consulta://dia de la semana cnsulta                                
                                newNonFatalInjury.setInjuryDayOfWeek(daysFacade.find(Short.parseShort(value)).getDaysName());
                                break;
                            case dia_consulta://dia de la consulta
                                dia1 = value;
                                break;
                            case mes_consulta://mes de la consulta
                                mes1 = value;
                                break;
                            case año_consulta://año de la consulta
                                ao1 = value;

                                break;
                            case hora_militar_consulta:
                                n = new Date();
                                hourInt = Integer.parseInt(value.substring(0, 2));
                                minuteInt = Integer.parseInt(value.substring(2, 4));
                                n.setHours(hourInt);
                                n.setMinutes(minuteInt);
                                n.setSeconds(0);
                                newNonFatalInjury.setCheckupTime(n);
                                break;
                            case hora_consulta://hora consulta
                                horas1 = value;
                                break;
                            case minuto_consulta://minuto consulta
                                minutos1 = value;
                                break;
                            case am_pm1://ampm consulta
                                ampm1 = value;
                                break;
                            case remitido:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newNonFatalInjury.setSubmittedPatient(true);
                                } else {
                                    newNonFatalInjury.setSubmittedPatient(false);
                                }
                                break;
                            case de_que_ips:
                                newNonFatalInjury.setSubmittedDataSourceId(nonFatalDataSourcesFacade.find(Short.parseShort(value)));
                                break;
                            case intencionalidad:
                                newNonFatalInjury.setIntentionalityId(intentionalitiesFacade.find(Short.parseShort(value)));
                                break;
                            case lugar_ocurrio_lesion:
                                newNonFatalInjury.setInjuryPlaceId(nonFatalPlacesFacade.find(Short.parseShort(value)));
                                break;
                            case activida_que_realizaba:
                                newNonFatalInjury.setActivityId(activitiesFacade.find(Short.parseShort(value)));
                                break;
                            case mecanismo_objeto_lesion:
                                newNonFatalInjury.setMechanismId(mechanismsFacade.find(Short.parseShort(value)));
                                break;
                            case uso_alcohol:
                                newNonFatalInjury.setUseAlcoholId(useAlcoholDrugsFacade.find(Short.parseShort(value)));
                                break;
                            case uso_drogas:
                                newNonFatalInjury.setUseDrugsId(useAlcoholDrugsFacade.find(Short.parseShort(value)));
                                break;
                            case grado_mas_grave_quemadura:
                                newNonFatalInjury.setBurnInjuryDegree(Short.parseShort(value));
                                break;
                            case porcentaje_cuerpo_quemado:
                                newNonFatalInjury.setBurnInjuryPercentage(Short.parseShort(value));
                                break;
                            case destino_paciente:
                                DestinationsOfPatient selectDestinationsOfPatient = destinationsOfPatientFacade.find(Short.parseShort(value));
                                newNonFatalInjury.setDestinationPatientId(selectDestinationsOfPatient);
                                break;
                            //guardar campos otros
                            case otro_grupo_etnico://otro gupo etnico
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 1));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case otro_lugar://otro lugar_ocurrio_lesion
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 2));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case otra_actividad://otra actividad
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 3));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case altura://caul caida_a_que_altura
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 4));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case polvora_cual:
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 5));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case tipo_desastre_natural:
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 6));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case otro_tipo_mecanismo_objeto_lesion://otro mecanismo u objeto de la lesion
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 7));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case otro_factor_precipitante://otro factor precipitante
                                selectInjuryDetermined = injuriesFacade.find((short) 52);//autoinflingida intencional
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 9));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case cual_otro_tipo_agresor://otro tipo de agresor(vif)
                                selectInjuryDetermined = injuriesFacade.find((short) 53);//intrafamiliar
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 10));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case cual_otro_tipo_maltrato://otro tipo de maltrato(vif)  
                                selectInjuryDetermined = injuriesFacade.find((short) 53);//intrafamiliar
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 11));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case otro_tipo_relacion_victima:
                                selectInjuryDetermined = injuriesFacade.find((short) 50);//interpersonal
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 12));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case otro_tipo_transporte_usuario:
                                selectInjuryDetermined = injuriesFacade.find((short) 51);//transito
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 13));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case otro_tipo_transporte_contraparte:
                                selectInjuryDetermined = injuriesFacade.find((short) 51);//transito
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 14));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case otro_tipo_de_usuario:
                                selectInjuryDetermined = injuriesFacade.find((short) 51);//transito
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 15));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case sitio_anatomico_cual_otro://cual otro_tipo_sitio anatomico
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 16));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case naturaleza_lesion_cual_otro:
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 17));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case otro_destino_paciente://otro destino_paciente del paciente
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 18));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case medico_diligencio_ficha:
                                newNonFatalInjury.setHealthProfessionalId(healthProfessionalsFacade.find(Integer.parseInt(value)));
                                break;
                            default:
                        }
                    }
                }

                //SI NO HAY INSTITUCION RECEPTORA SE TRATA DE COLOCAR LA FUENTE DEL PROYECTO
                if (newNonFatalDomesticViolence.getDomesticViolenceDataSourceId() == null) {
                    if (projectsMB.getCurrentSourceId() != 75) {//si la fuente es diferente de observatorio del delito
                        newNonFatalDomesticViolence.setDomesticViolenceDataSourceId(nonFatalDataSourcesFacade.find(projectsMB.getCurrentSourceId()));
                        newNonFatalInjury.setNonFatalDataSourceId(nonFatalDataSourcesFacade.find(projectsMB.getCurrentSourceId()));
                    }
                }

                //SI NO HAY FECHA DE CONSULTA TRATAR DE CALCULAR MEDIANTE LAS VARIABLES dia_evento, mes_evento, año_evento
                if (newNonFatalInjury.getCheckupDate() == null) {
                    dia1 = haveData(dia1);
                    mes1 = haveData(mes1);
                    ao1 = haveData(ao1);
                    if (dia1 != null && mes1 != null && ao1 != null) {
                        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                        Date fechaI;
                        try {
                            fechaI = formato.parse(dia1 + "/" + mes1 + "/" + ao1);
                            newNonFatalInjury.setCheckupDate(fechaI);
                        } catch (Exception e) {
                        }
                    }
                }
                //SI NO HAY HORA DE CONSULTA TRATAR DE CALCULAR MEDIANTE LAS VARIABLES hora_evento,minuto_evento,am_pm
                if (newNonFatalInjury.getCheckupTime() == null) {
                    horas1 = haveData(horas1);
                    minutos1 = haveData(minutos1);
                    ampm1 = haveData(ampm1);
                    if (horas1 != null && minutos1 != null && ampm1 != null) {
                        hourInt = Integer.parseInt(horas1);
                        minuteInt = Integer.parseInt(minutos1);
                        if (ampm1.compareTo("2") == 0) {
                            hourInt = hourInt + 12;
                            if (hourInt == 24) {
                                hourInt = 0;
                            }
                        }
                        currentDate = new Date();
                        currentDate.setHours(hourInt);
                        currentDate.setMinutes(minuteInt);
                        currentDate.setSeconds(0);
                        newNonFatalInjury.setCheckupTime(currentDate);
                    }
                }

                //SI NO HAY FECHA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES dia_evento, mes_evento, año_evento
                if (newNonFatalInjury.getInjuryDate() == null) {
                    dia = haveData(dia);
                    mes = haveData(mes);
                    ao = haveData(ao);
                    if (dia != null && mes != null && ao != null) {
                        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                        Date fechaI;
                        try {
                            fechaI = formato.parse(dia + "/" + mes + "/" + ao);
                            newNonFatalInjury.setInjuryDate(fechaI);
                        } catch (Exception e) {
                        }
                    }
                }

                //SI NO HAY HORA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES hora_evento,minuto_evento,am_pm
                if (newNonFatalInjury.getInjuryTime() == null) {
                    horas = haveData(horas);
                    minutos = haveData(minutos);
                    ampm = haveData(ampm);
                    if (horas != null && minutos != null && ampm != null) {
                        hourInt = Integer.parseInt(horas);
                        minuteInt = Integer.parseInt(minutos);
                        if (ampm.compareTo("2") == 0) {
                            hourInt = hourInt + 12;
                            if (hourInt == 24) {
                                hourInt = 0;
                            }
                        }
                        currentDate = new Date();
                        currentDate.setHours(hourInt);
                        currentDate.setMinutes(minuteInt);
                        currentDate.setSeconds(0);
                        newNonFatalInjury.setInjuryTime(currentDate);
                    }
                }

                //SI LA HORA DE LA CONSULTA ES 0000 PASAR LA HORA DEL EVENTO A LA DE LA CONSULTA
                if (newNonFatalInjury.getCheckupTime() != null) {
                    if (newNonFatalInjury.getInjuryTime() != null) {
                        int hour = newNonFatalInjury.getInjuryTime().getHours();
                        int minute = newNonFatalInjury.getInjuryTime().getMinutes();
                        if (hour == 0 && minute == 0) {
                            newNonFatalInjury.setInjuryTime(newNonFatalInjury.getCheckupTime());
                        }
                    } else {
                        newNonFatalInjury.setInjuryTime(newNonFatalInjury.getCheckupTime());
                    }
                }

                //SI NO HAY FECHA DE EVENTO PASAR LA DE CONSULTA
                if (newNonFatalInjury.getInjuryDate() == null) {
                    if (newNonFatalInjury.getCheckupDate() != null) {
                        newNonFatalInjury.setInjuryDate(newNonFatalInjury.getCheckupDate());
                    }
                }

                //SI NO HAY HORA DE EVENTO PASAR LA DE CONSULTA
                if (newNonFatalInjury.getInjuryTime() == null) {
                    if (newNonFatalInjury.getCheckupTime() != null) {
                        newNonFatalInjury.setInjuryTime(newNonFatalInjury.getCheckupTime());
                    }
                }

                //SI NO HAY FECHA DE CONSULTA PASAR LA DE EVENTO
                if (newNonFatalInjury.getCheckupDate() == null) {
                    if (newNonFatalInjury.getInjuryDate() != null) {
                        newNonFatalInjury.setCheckupDate(newNonFatalInjury.getInjuryDate());
                    }
                }
                //SI NO HAY HORA DE CONSULTA PASAR LA DE EVENTO
                if (newNonFatalInjury.getCheckupTime() == null) {
                    if (newNonFatalInjury.getInjuryTime() != null) {
                        newNonFatalInjury.setCheckupTime(newNonFatalInjury.getInjuryTime());
                    }
                }
                //SI NO HAY DIA DE LA SEMANA DEL EVENTO SE CALCULA
                if (newNonFatalInjury.getInjuryDate() != null) {
                    if (newNonFatalInjury.getInjuryDayOfWeek() == null) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(newNonFatalInjury.getInjuryDate());
                        newNonFatalInjury.setInjuryDayOfWeek(intToDay(cal.get(Calendar.DAY_OF_WEEK)));
                    }
                }
                //SI FECHA DE EVENTO MAYOR A FECHA DE CONSULTA SE INVIERTEN                
                if (newNonFatalInjury.getInjuryDate().getTime() > newNonFatalInjury.getCheckupDate().getTime()) {
                    Date auxInjuryDate = newNonFatalInjury.getInjuryDate();
                    newNonFatalInjury.setInjuryDate(newNonFatalInjury.getCheckupDate());
                    newNonFatalInjury.setCheckupDate(auxInjuryDate);
                }

                //SI NO SE DETERMINA EL BARRIO SE COLOCA SIN DATO URBANO
                if (newVictim.getVictimNeighborhoodId() == null) {
                    newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find((int) 52001));
                }
                //SI NO SE DETERMINA EL BARRIO SE COLOCA SIN DATO URBANO
                if (newNonFatalInjury.getInjuryNeighborhoodId() == null) {
                    newNonFatalInjury.setInjuryNeighborhoodId(neighborhoodsFacade.find((int) 52001));
                }

                //UNION DE NOMBRES Y APELLIOS
                name = name + " " + surname;
                if (name.trim().length() > 1) {
                    newVictim.setVictimName(name.trim());
                }
                //EDAD Y TIPO DE EDAD
                if (newVictim.getVictimAge() != null) {//HAY EDAD 
                    if (newVictim.getAgeTypeId() == null) {//no hay tipo de edad
                        newVictim.setAgeTypeId((short) 1);//tiṕo de edad años
                    }
                } else {
                    newVictim.setAgeTypeId((short) 4);//tiṕo de edad sin determinar
                }
                //SI NO SE DETERMINA LA INSTITUCION DE SALUD SE ALMACENA LA QUE VIENE DEL FORMULARIO                
                if (newNonFatalInjury.getNonFatalDataSourceId() == null) {
                    if (currentSource != 21) {//"OBSERVATORIO DEL DELITO")
                        newNonFatalInjury.setNonFatalDataSourceId(nonFatalDataSourcesFacade.find((short) currentSource));
                    }
                }

                //SI NO SE DETERMINA LA EDAD VERIFICAR SI HAY FECHA DE NACIMIENTO
                if (newVictim.getVictimDateOfBirth() != null) {
                    if (newVictim.getVictimAge() == null) {
                        int birthMonths;
                        int eventMonths;

                        Calendar systemCalendar = Calendar.getInstance();
                        Calendar birthCalendar = Calendar.getInstance();
                        birthCalendar.setTime(newVictim.getVictimDateOfBirth());

                        try {//DETERMINO LA EDAD EN MESES
                            birthMonths = birthCalendar.get(Calendar.YEAR);
                            birthMonths = birthMonths * 12;
                            birthMonths = birthMonths + birthCalendar.get(Calendar.MONTH);
                            if (newNonFatalInjury.getInjuryDate() != null) {//SE CALCULA EDAD SEGUN LA FECHA DE EVENTO
                                systemCalendar.setTime(newNonFatalInjury.getInjuryDate());
                            }
                            eventMonths = systemCalendar.get(Calendar.YEAR);
                            eventMonths = eventMonths * 12;
                            eventMonths = eventMonths + systemCalendar.get(Calendar.MONTH);

                            int ageMonths = eventMonths - birthMonths;
                            if (ageMonths < 0) {
                                System.out.println("ERROR fecha de nacimiento mayor a la del sistema o evento: ");
                            } else {
                                int ageYears = (int) (ageMonths / 12);
                                if (ageYears == 0) {
                                    ageYears = 1;
                                }
                                newVictim.setVictimAge((short) ageYears);
                                newVictim.setAgeTypeId((short) 1);//aqui por defecto seria sin dato, si no se conoce
                            }
                        } catch (Exception ex) {
                            System.out.println("Error 21 en " + this.getClass().getName() + ":" + ex.toString());
                        }
                    }
                }



                //DETERMINO TIPO DE LESION//////////////////////////////////////
                if (selectInjuryDetermined == null) {//no se pudo determinar se coloca por defecto //54. No intencional
                    if (newNonFatalInjury.getMechanismId() != null && newNonFatalInjury.getMechanismId().getMechanismId() == 1) {//mecanismo es transito 
                        newNonFatalInjury.setInjuryId(injuriesFacade.find((short) 51));//lesion en accidente de transito
                    } else {//el mecanismo no es transito se evalua segun la intecionalidad
                        if (newNonFatalInjury.getIntentionalityId().getIntentionalityId() != null) {
                            if (newNonFatalInjury.getIntentionalityId().getIntentionalityId() == (short) 1) {//no intencional
                                newNonFatalInjury.setInjuryId(injuriesFacade.find((short) 54));//no intencional
                            }
                            if (newNonFatalInjury.getIntentionalityId().getIntentionalityId() == (short) 2) {//auntoinflingida
                                newNonFatalInjury.setInjuryId(injuriesFacade.find((short) 52));//autoinflingida
                            }
                            if (newNonFatalInjury.getIntentionalityId().getIntentionalityId() == (short) 3) {//violencia agresion o sospecha
                                newNonFatalInjury.setInjuryId(injuriesFacade.find((short) 50));//interpersonal
                            }
                        } else {
                            newNonFatalInjury.setInjuryId(injuriesFacade.find((short) 54));//no intencional
                            newNonFatalInjury.setIntentionalityId(intentionalitiesFacade.find((short) 1));//no intencional
                        }
                    }
                } else {//se pudo determinar una intencionalidad segun los datos que llegaron                        
                    if (newNonFatalInjury.getMechanismId() != null && newNonFatalInjury.getMechanismId().getMechanismId() == 1) {//mecanismo es transito 
                        newNonFatalInjury.setInjuryId(injuriesFacade.find((short) 51));//lesion en accidente de transito
                    } else {//el mecanismo no es transito se asigna el determinado segun los datos que llegaron
                        newNonFatalInjury.setInjuryId(selectInjuryDetermined);
                    }
                }

                if (newNonFatalInjury.getInjuryId().getInjuryId() == (short) 53) {//53 ES POR QUE ES VIF 
                    newNonFatalInjury.setInjuryId(injuriesFacade.find((short) 55));//CAMBIA A 55 PARA SER VIF INGRESADA DESDE LCENF
                }

                //destino del paciente
                //
                if (newNonFatalInjury.getDestinationPatientId() == null) {
                    newNonFatalInjury.setDestinationPatientId(destinationsOfPatientFacade.find((short) 11));//se usa acciones en salud

                }

                //AGREGO LAS LISTAS NO VACIAS///////////////////////////////////




                if (!anatomicalLocationsList.isEmpty()) {
                    newNonFatalInjury.setAnatomicalLocationsList(anatomicalLocationsList);
                } else {
                    anatomicalLocationsList.add(new AnatomicalLocations((short) 99));
                    newNonFatalInjury.setAnatomicalLocationsList(anatomicalLocationsList);
                }

                if (!abuseTypesList.isEmpty()) {
                    newNonFatalDomesticViolence.setAbuseTypesList(abuseTypesList);
                } else {
                    abuseTypesList.add(new AbuseTypes((short) 7));
                    newNonFatalDomesticViolence.setAbuseTypesList(abuseTypesList);
                }

                if (!aggressorTypesList.isEmpty()) {
                    newNonFatalDomesticViolence.setAggressorTypesList(aggressorTypesList);
                } else {
                    aggressorTypesList.add(new AggressorTypes((short) 9));
                    newNonFatalDomesticViolence.setAggressorTypesList(aggressorTypesList);
                }

                if (!kindsOfInjurysList.isEmpty()) {
                    newNonFatalInjury.setKindsOfInjuryList(kindsOfInjurysList);
                } else {
                    kindsOfInjurysList.add(new KindsOfInjury((short) 99));
                    newNonFatalInjury.setKindsOfInjuryList(kindsOfInjurysList);
                }

                if (!diagnosesList.isEmpty()) {
                    newNonFatalInjury.setDiagnosesList(diagnosesList);
                }

                if (!vulnerableGroupList.isEmpty()) {
                    newVictim.setVulnerableGroupsList(vulnerableGroupList);
                }

                if (!securityElementList.isEmpty()) {
                    newNonFatalTransport.setSecurityElementsList(securityElementList);
                } else {
                    securityElementList.add(new SecurityElements((short) 7));
                    newNonFatalTransport.setSecurityElementsList(securityElementList);
                }

                if (newVictim.getVictimNid() == null) {//NO HAY NUMERO DE IDENTIFICACION 
                    newVictim.setVictimNid(String.valueOf(genNnFacade.findMax() + 1));//asigno un consecutivo a la identificacion
                    newVictim.setVictimClass((short) 2);//nn
                    if (newVictim.getTypeId() == null) {//no hay tipo de identificacion
                        if (newVictim.getVictimAge() != null && newVictim.getAgeTypeId() != null && newVictim.getAgeTypeId() == 1) {//HAY EDAD Y HAY tipo de edad                            
                            if (newVictim.getVictimAge() > 17) {
                                newVictim.setTypeId(idTypesFacade.find((short) 6));//adulto sin identificacion                                
                            } else {
                                newVictim.setTypeId(idTypesFacade.find((short) 7));//menor sin identificacion
                            }
                        } else {//NO HAY EDAD
                            newVictim.setTypeId(idTypesFacade.find((short) 9));//tipo de identificacoin sin determinar
                        }
                    }
                    int newGenNnId = genNnFacade.findMax() + 1;
                    connectionJdbcMB.non_query("UPDATE gen_nn SET cod_nn = " + newGenNnId + " where cod_nn IN (SELECT MAX(cod_nn) from gen_nn)");
                } else {//HAY NUMERO DE IDENTIFICACION
                    if (newVictim.getTypeId() == null) {//no hay tipo de identificacion
                        if (newVictim.getVictimAge() != null && newVictim.getAgeTypeId() != null && newVictim.getAgeTypeId() == 1) {//HAY EDAD Y HAY tipo de edad                            
                            if (newVictim.getVictimAge() > 17) {
                                newVictim.setTypeId(idTypesFacade.find((short) 6));//adulto sin identificacion                                
                            } else {
                                newVictim.setTypeId(idTypesFacade.find((short) 7));//menor sin identificacion
                            }
                        } else {//NO HAY EDAD
                            newVictim.setTypeId(idTypesFacade.find((short) 9));//tipo de identificacoin sin determinar
                        }
                    }
                }
                //CORRESPONDENCIA ENTRE EDAD Y TIPO DE IDENTIFICACION
                if (newVictim.getTypeId() != null) {//no hay tipo de identificacion
                    if (newVictim.getVictimAge() != null) {//HAY EDAD Y HAY tipo de edad
                        if (newVictim.getVictimAge() < 18) {//menor de edad
                            if (newVictim.getTypeId().getTypeId() == (short) 1 ||//cedula de ciudadania
                                    newVictim.getTypeId().getTypeId() == (short) 2 ||//cedula de extranjeria
                                    newVictim.getTypeId().getTypeId() == (short) 3 ||//pasaporte
                                    newVictim.getTypeId().getTypeId() == (short) 6) {//adulto sin identificacion
                                newVictim.setTypeId(idTypesFacade.find((short) 9));//sin determinar
                            }
                        } else {//mayor de edad
                            if (newVictim.getTypeId().getTypeId() == (short) 5 ||//tarjeta de identidad
                                    newVictim.getTypeId().getTypeId() == (short) 4 ||//registro civil
                                    newVictim.getTypeId().getTypeId() == (short) 7) {//menor sin identificacion
                                newVictim.setTypeId(idTypesFacade.find((short) 9));//sin determinar
                            }
                        }

                    }
                }
                //PERSISTO//////////////////////////////////////////////////////
                try {
                    if (!othersList.isEmpty()) {
                        newVictim.setOthersList(othersList);
                    }
                    newNonFatalInjury.setVictimId(newVictim);
                    victimsFacade.create(newVictim);//PERSISTO LA VICTIMA                
                    nonFatalInjuriesFacade.create(newNonFatalInjury);//PERSISTO LA LESION NO FATAL                    
                    if (newNonFatalInjury.getInjuryId().getInjuryId().compareTo((short) 50) == 0) {//VIOLENCIA INTERPERSONAL
                        newNonFatalInterpersonal.setNonFatalInjuries(nonFatalInjuriesFacade.find(newNonFatalInjury.getNonFatalInjuryId()));
                        nonFatalInterpersonalFacade.create(newNonFatalInterpersonal);
                    } else if (newNonFatalInjury.getInjuryId().getInjuryId().compareTo((short) 51) == 0) {//ACCIDENTE DE TRANSITO 
                        newNonFatalTransport.setNonFatalInjuries(nonFatalInjuriesFacade.find(newNonFatalInjury.getNonFatalInjuryId()));
                        nonFatalTransportFacade.create(newNonFatalTransport);
                    } else if (newNonFatalInjury.getInjuryId().getInjuryId().compareTo((short) 52) == 0) {//INTENCIONAL AUTOINFLINGIDA 
                        newNonFatalSelfInflicted.setNonFatalInjuries(nonFatalInjuriesFacade.find(newNonFatalInjury.getNonFatalInjuryId()));
                        nonFatalSelfInflictedFacade.create(newNonFatalSelfInflicted);
                    } else if (newNonFatalInjury.getInjuryId().getInjuryId().compareTo((short) 53) == 0
                            || newNonFatalInjury.getInjuryId().getInjuryId().compareTo((short) 55) == 0) {//VIOLENCIA INTRAFAMILIAR

                        newNonFatalDomesticViolence.setNonFatalInjuries(nonFatalInjuriesFacade.find(newNonFatalInjury.getNonFatalInjuryId()));
                        nonFatalDomesticViolenceFacade.create(newNonFatalDomesticViolence);
                    } else if (newNonFatalInjury.getInjuryId().getInjuryId().compareTo((short) 54) == 0) {//NO INTENCIONAL 
                        //POR DEFECTO ES NO INTENCIONAL
                    }
                    applicationControlMB.removeNonfatalReservedIdentifiers(newNonFatalInjury.getNonFatalInjuryId());
                    applicationControlMB.removeVictimsReservedIdentifiers(newVictim.getVictimId());
                } catch (Exception e) {
                    System.out.println("Error 22 en " + this.getClass().getName() + ":" + e.toString());
                }
                tuplesProcessed++;
                progress = (int) (tuplesProcessed * 100) / tuplesNumber;
                System.out.println("PROGRESO INGRESANDO LCENF: " + String.valueOf(progress));
            }
            progress = 100;
            System.out.println("PROGRESO INGRESANDO LCENF: " + String.valueOf(progress));

        } catch (SQLException ex) {
            System.out.println("Error 23 en " + this.getClass().getName() + ":" + ex.toString());
        }
    }

    /**
     * allows to load the records the VIF tab (family intra violence).
     */
    public void registerSCC_F_033() {
        /**
         * *********************************************************************
         * CARGA DE REGISTROS DE LA FICHA VIF
         * ********************************************************************
         */
        tuplesNumber = 0;
        tuplesProcessed = 0;
        progress = 0;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            tuplesNumber = determineTuplesNumber();//determino numero de tuplas  
            resultSetFileData = determineRecords();//resulset con los registros a procesar

            newUngroupedTags = new UngroupedTags();
            newUngroupedTags.setUngroupedTagId(ungroupedTagsFacade.findMax() + 1);
            newUngroupedTags.setUngroupedTagName(determineTagName(projectsMB.getCurrentProjectName()));
            newUngroupedTags.setUngroupedTagDate(new Date());
            newUngroupedTags.setFormId(nameForm);
            newUngroupedTags.setCurrentTagId(ungroupedTagsFacade.findMax() + 1);
            ungroupedTagsFacade.create(newUngroupedTags);

            newTag = new Tags();//(maxTag, projectsMB.getNameFile(), projectsMB.getNameFile());
            newTag.setTagId(ungroupedTagsFacade.findMax());
            newTag.setTagName(determineTagName(projectsMB.getCurrentProjectName()));
            newTag.setTagFileInput(projectsMB.getCurrentFileName());
            newTag.setTagFileStored(projectsMB.getCurrentFileName());
            newTag.setFormId(formsFacade.find(nameForm));
            lastTagNameCreated = newTag.getTagName();

            tagsFacade.create(newTag);
            while (resultSetFileData.next()) {//recorro cada uno de los registros de la tabla temp                    
                Victims newVictim = new Victims();
                newVictim.setVictimId(applicationControlMB.addVictimsReservedIdentifiers());
                newVictim.setVictimClass((short) 1);
                newVictim.setTagId(tagsFacade.find(newTag.getTagId()));
                newVictim.setFirstTagId(newVictim.getTagId().getTagId());
                NonFatalInjuries newNonFatalInjury = new NonFatalInjuries();
                newNonFatalInjury.setUserId(usersFacade.find(1));//usuario administrador
                NonFatalDomesticViolence newNonFatalDomesticViolence = new NonFatalDomesticViolence();
                newNonFatalInjury.setNonFatalInjuryId(applicationControlMB.addNonfatalReservedIdentifiers());
                newNonFatalInjury.setInputTimestamp(new Date());
                List<ActionsToTake> actionsToTakeList = new ArrayList<>();
                List<AnatomicalLocations> anatomicalLocationsList = new ArrayList<>();
                List<AbuseTypes> abuseTypesList = new ArrayList<>();
                List<AggressorTypes> aggressorTypesList = new ArrayList<>();
                List<Others> othersList = new ArrayList<>();
                List<VulnerableGroups> vulnerableGroupList = new ArrayList<>();// lista vector victim_vulnerable_group

                value = "";
                name = "";
                surname = "";
                dia = "";//dia evento
                mes = "";//mes evento
                ao = "";//año del evento
                dia1 = "";//dia de la consulta
                mes1 = "";//mes de la consulta
                ao1 = "";//año de la consulta
                horas = "";//hora evento
                minutos = "";//minuto evento
                ampm = "";//ampm evento
                horas1 = "";//hora consulta
                minutos1 = "";//minuto consulta
                ampm1 = "";//ampm consulta
                hourInt = 0;
                minuteInt = 0;
                Object[] arrayInJava = (Object[]) resultSetFileData.getArray(3).getArray();
                for (int posCol = 0; posCol < arrayInJava.length; posCol++) {
                    value = null;
                    String splitColumnAndValue[] = arrayInJava[posCol].toString().split("<=>");
                    relationVar = currentRelationsGroup.findRelationVarByNameFound(splitColumnAndValue[0]);//determino la relacion de variables
                    if (relationVar != null) {
                        switch (DataTypeEnum.convert(relationVar.getFieldType())) {//determino valor a ingresar usando: isNumeric,isAge... etc
                            case text:
                                value = splitColumnAndValue[1];
                                break;
                            case integer:
                                value = isNumeric(splitColumnAndValue[1]);
                                break;
                            case age:
                                value = isAge(splitColumnAndValue[1]);
                                break;
                            case date:
                                value = isDate(splitColumnAndValue[1], relationVar.getDateFormat());
                                break;
                            case military:
                                value = isMilitary(splitColumnAndValue[1]);
                                break;
                            case hour:
                                value = isHour(splitColumnAndValue[1]);
                                break;
                            case minute:
                                value = isMinute(splitColumnAndValue[1]);
                                break;
                            case day:
                                value = isDay(splitColumnAndValue[1]);
                                break;
                            case month:
                                value = isMonth(splitColumnAndValue[1]);
                                break;
                            case year:
                                value = isYear(splitColumnAndValue[1]);
                                break;
                            case percentage:
                                value = isPercentage(splitColumnAndValue[1]);
                                break;
                            case NOVALUE:
                                value = isCategorical(splitColumnAndValue[1], relationVar);
                                break;
                        }
                    }

                    continueProcces = false;
                    if (value != null) {
                        if (value.trim().length() != 0) {
                            continueProcces = true;
                        }
                    }
                    if (continueProcces) {

                        switch (SCC_F_033Enum.convert(relationVar.getNameExpected())) {
                            // ************************************************DATOS PARA LA TABLA victims                                
                            case institucion_receptora:
                                newNonFatalDomesticViolence.setDomesticViolenceDataSourceId(nonFatalDataSourcesFacade.find(Short.parseShort(value)));
                                newNonFatalInjury.setNonFatalDataSourceId(nonFatalDataSourcesFacade.find(Short.parseShort(value)));
                                break;
                            case primer_apellido:
                                if (surname.trim().length() != 0) {
                                    surname = value + " " + surname;
                                } else {
                                    surname = value;
                                }
                                break;
                            case segundo_apellido:
                                if (surname.trim().length() != 0) {
                                    surname = surname + " " + value;
                                } else {
                                    surname = value;
                                }
                                break;
                            case primer_nombre:
                                if (name.trim().length() != 0) {
                                    name = value + " " + name;
                                } else {
                                    name = value;
                                }
                                break;
                            case segundo_nombre:
                                if (name.trim().length() != 0) {
                                    name = name + " " + value;
                                } else {
                                    name = value;
                                }
                                break;
                            case tipo_identificacion_victima:
                                newVictim.setTypeId(idTypesFacade.find(Short.parseShort(value)));
                                break;
                            case numero_identificacion_victima:
                                newVictim.setVictimNid(value);
                                break;
                            case medida_edad:
                                newVictim.setAgeTypeId(Short.parseShort(value));
                                break;
                            case edad_victima:
                                newVictim.setVictimAge(Short.parseShort(value));
                                break;
                            case sexo_victima:
                                newVictim.setGenderId(gendersFacade.find(Short.parseShort(value)));
                                break;
                            case ocupacion_victima:
                                newVictim.setJobId(jobsFacade.find(Integer.parseInt(value)));
                                break;
                            case aseguradora:
                                newVictim.setInsuranceId(insuranceFacade.find(value));
                                break;
                            case departamento_residencia:
                                newVictim.setResidenceDepartment(Short.parseShort(value));
                                break;
                            case municipio_residencia:
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
                            case barrio_residencia_victima://barrio barrio_residencia_victima
                                newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(value)));
                                break;
                            case direccion_residencia://direccion barrio_residencia_victima
                                newVictim.setVictimAddress(value);
                                break;
                            case telefono_residencia:
                                newVictim.setVictimTelephone(value);
                                break;
                            case barrio_evento://barrio evento
                                newNonFatalInjury.setInjuryNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(value)));
                                break;
                            case fecha_nacimiento://fecha nacimiento
                                try {
                                    currentDate = dateFormat.parse(value);
                                    newVictim.setVictimDateOfBirth(currentDate);
                                } catch (ParseException ex) {
                                }
                                break;
                            case direccion_evento://direccion evento
                                newNonFatalInjury.setInjuryAddress(value);
                                break;
                            case dia_evento://dia del evento
                                dia = value;
                                break;
                            case mes_evento:
                                mes = value;
                                break;
                            case año_evento:
                                ao = value;
                                break;
                            case fecha_evento://fecha del evento
                                try {
                                    currentDate = dateFormat.parse(value);
                                    newNonFatalInjury.setInjuryDate(currentDate);
                                } catch (ParseException ex) {
                                }
                                break;
                            case dia_semana_evento://dia de la semana del evento
                                newNonFatalInjury.setInjuryDayOfWeek(daysFacade.find(Short.parseShort(value)).getDaysName());
                                break;
                            case hora_evento://hora del evento
                                horas = value;
                                break;
                            case minuto_evento://minuto del evento
                                minutos = value;
                                break;
                            case am_pm:
                                ampm = value;
                                break;
                            case hora_militar_evento://hora militar del evento
                                n = new Date();
                                hourInt = Integer.parseInt(value.substring(0, 2));
                                minuteInt = Integer.parseInt(value.substring(2, 4));
                                n.setHours(hourInt);
                                n.setMinutes(minuteInt);
                                n.setSeconds(0);
                                newNonFatalInjury.setInjuryTime(n);
                                break;
                            case dia_consulta://dia de la consulta
                                dia1 = value;
                                break;
                            case mes_consulta://mes de la consulta
                                mes1 = value;
                                break;
                            case año_consulta:
                                break;
                            case fecha_consulta://fecha de la consulta
                                try {
                                    currentDate = dateFormat.parse(value);
                                    newNonFatalInjury.setCheckupDate(currentDate);
                                } catch (ParseException ex) {
                                }
                                break;
                            case dia_semana_consulta://dia de la semana de la consuta

                                break;
                            case hora_consulta://hora de la consulta
                                horas1 = value;
                                break;
                            case minuto_consulta://minuto de la consulta
                                minutos1 = value;
                                break;
                            case am_pm1://ampm consulta
                                ampm1 = value;
                                break;
                            case hora_militar_consulta://hora militar de la consulta
                                n = new Date();
                                hourInt = Integer.parseInt(value.substring(0, 2));
                                minuteInt = Integer.parseInt(value.substring(2, 4));
                                n.setHours(hourInt);
                                n.setMinutes(minuteInt);
                                n.setSeconds(0);
                                newNonFatalInjury.setCheckupTime(n);
                                break;
                            case es_remitido:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newNonFatalInjury.setSubmittedPatient(true);
                                } else {
                                    newNonFatalInjury.setSubmittedPatient(false);
                                }
                                break;
                            case de_que_ips:
                                newNonFatalInjury.setSubmittedDataSourceId(nonFatalDataSourcesFacade.find(Short.parseShort(value)));
                                break;
                            case lugar_ocurrio_evento:
                                newNonFatalInjury.setInjuryPlaceId(nonFatalPlacesFacade.find(Short.parseShort(value)));
                                break;

                            case activida_que_realizaba:
                                newNonFatalInjury.setActivityId(activitiesFacade.find(Short.parseShort(value)));
                                break;

                            case mecanismo_objeto_lesion:
                                newNonFatalInjury.setMechanismId(mechanismsFacade.find(Short.parseShort(value)));
                                break;
                            //CAMPOS OTROS
                            case otro_lugar://otro lugar_ocurrio_lesion
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 1));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case otra_actividad://otra actividad
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 2));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case caida_a_que_altura://"En caso de caida: a que caida_a_que_altura fue"
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 3));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case que_tipo_polvora://"En caso de quemadura por polvora: que tipo_identificacion_victima de polvora fue"
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 4));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case cual_desastre_natural://"En caso de desastre natural: que tipo_identificacion_victima de desastre fue"                                
                                break;
                            case otro_tipo_mecanismo_objeto_lesion://"Otro tipo_identificacion_victima de mecanismo u objeto"
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 6));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case otro_grupo_etnico://otro grupos_vulnerables etnico
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 8));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case otro_grupo_vulnerable://otro grupos_vulnerables vulnerable
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 9));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case cual_otro_tipo_agresor://"En caso de otro_tipo_mecanismo_objeto_lesion tipo_identificacion_victima de agresor: Cual"
                                booleanValue = false;
                                for (int i = 0; i < aggressorTypesList.size(); i++) {
                                    if (aggressorTypesList.get(i).getAggressorTypeId() == (short) 10) {
                                        booleanValue = true;
                                        break;
                                    }
                                }
                                if (!booleanValue) {
                                    aggressorTypesList.add(new AggressorTypes((short) 10));
                                }
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 10));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case cual_otro_tipo_maltrato://"En caso de otro_tipo_mecanismo_objeto_lesion tipo_identificacion_victima de maltrato: Cual"
                                booleanValue = false;
                                for (int i = 0; i < abuseTypesList.size(); i++) {
                                    if (abuseTypesList.get(i).getAbuseTypeId() == (short) 8) {
                                        booleanValue = true;
                                        break;
                                    }
                                }
                                if (!booleanValue) {
                                    abuseTypesList.add(new AbuseTypes((short) 8));
                                }
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 11));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case cual_otra_accion_realizar://"En caso de accion_realizar_otra acción a realizar: Cual"
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 12));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            //FIN CAMPOS OTROS---------------------------------------
                            case uso_alcohol://"Uso de uso_alcohol"
                                newNonFatalInjury.setUseAlcoholId(useAlcoholDrugsFacade.find(Short.parseShort(value)));
                                break;
                            case uso_drogas://"Uso de uso_drogas"
                                newNonFatalInjury.setUseDrugsId(useAlcoholDrugsFacade.find(Short.parseShort(value)));
                                break;
                            case grado_mas_grave_quemadura://"En caso de quemadura: Grado más grave"
                                newNonFatalInjury.setBurnInjuryDegree(Short.parseShort(value));
                                break;
                            case porcentaje_cuerpo_quemado://"En caso de quemadura: Porcentaje del cuerpo quemado"
                                newNonFatalInjury.setBurnInjuryPercentage(Short.parseShort(value));
                                break;
                            case grupos_vulnerables://GRUPO VULNERABLE
                                VulnerableGroups auxVulnerableGroups = vulnerableGroupsFacade.find(Short.parseShort(value));
                                //List<VulnerableGroups> vulnerableGroupsList = new ArrayList<VulnerableGroups>();
                                vulnerableGroupList.add(auxVulnerableGroups);
                                newVictim.setVulnerableGroupsList(vulnerableGroupList);
                                break;
                            case grupos_etnicos://GRUPO ETNICO
                                newVictim.setEthnicGroupId(ethnicGroupsFacade.find(Short.parseShort(value)));
                                break;
                            case maltrato_fisico:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 1));
                                }
                                break;
                            case maltrato_psicologico:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 2));
                                }
                                break;
                            case maltrato_abuso_sexual:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 3));
                                }
                                break;
                            case maltrato_negligencia:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 4));
                                }
                                break;
                            case maltrato_abandono:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 5));
                                }
                                break;
                            case maltrato_institucional:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 6));
                                }
                                break;
                            case maltrato_sin_dato:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 7));
                                }
                                break;
                            case maltrato_otro:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    booleanValue = false;
                                    for (int i = 0; i < abuseTypesList.size(); i++) {
                                        if (abuseTypesList.get(i).getAbuseTypeId() == (short) 8) {
                                            booleanValue = true;
                                            break;
                                        }
                                    }
                                    if (!booleanValue) {
                                        abuseTypesList.add(new AbuseTypes((short) 8));
                                    }
                                }
                                break;
                            case tipo_agresor_padre:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 1));
                                }
                                break;
                            case tipo_agresor_madre:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 2));
                                }
                                break;
                            case tipo_agresor_padrastro:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 3));
                                }
                                break;
                            case tipo_agresor_madrastra:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 4));
                                }
                                break;
                            case tipo_agresor_conyuge:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 5));
                                }
                                break;
                            case tipo_agresor_hermano:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 6));
                                }
                                break;
                            case tipo_agresor_hijo:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 7));
                                }
                                break;
                            case tipo_agresor_otro:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 8));
                                }
                                break;
                            case tipo_agresor_sin_dato:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 9));
                                }
                                break;
                            case tipo_agresor_novio:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    booleanValue = false;
                                    for (int i = 0; i < aggressorTypesList.size(); i++) {
                                        if (aggressorTypesList.get(i).getAggressorTypeId() == (short) 10) {
                                            booleanValue = true;
                                            break;
                                        }
                                    }
                                    if (!booleanValue) {
                                        aggressorTypesList.add(new AggressorTypes((short) 10));
                                    }
                                }
                                break;
                            case accion_realizar_conciliacion:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    actionsToTakeList.add(actionsToTakeFacade.find((short) 1));
                                }
                                break;
                            case accion_realizar_caucion:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    actionsToTakeList.add(actionsToTakeFacade.find((short) 2));
                                }
                                break;
                            case accion_realizar_dictamen:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    actionsToTakeList.add(actionsToTakeFacade.find((short) 3));
                                }
                                break;
                            case accion_realizar_remision_fiscalia:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    actionsToTakeList.add(actionsToTakeFacade.find((short) 4));
                                }
                                break;
                            case accion_realizar_remision_medicina_legal:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    actionsToTakeList.add(actionsToTakeFacade.find((short) 5));
                                }
                                break;
                            case accion_realizar_remision_comisaria:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    actionsToTakeList.add(actionsToTakeFacade.find((short) 6));
                                }
                                break;
                            case accion_realizar_remision_icbf:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    actionsToTakeList.add(actionsToTakeFacade.find((short) 7));
                                }
                                break;
                            case accion_realizar_medidas_proteccion:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    actionsToTakeList.add(actionsToTakeFacade.find((short) 8));
                                }
                                break;
                            case accion_realizar_resimison_salud:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    actionsToTakeList.add(actionsToTakeFacade.find((short) 9));
                                }
                                break;
                            case accion_realizar_atencion_psicologica:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    actionsToTakeList.add(actionsToTakeFacade.find((short) 10));
                                }
                                break;
                            case accion_realizar_restablecimiento_derechos:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    actionsToTakeList.add(actionsToTakeFacade.find((short) 11));
                                }
                                break;
                            case accion_realizar_otra:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    actionsToTakeList.add(actionsToTakeFacade.find((short) 12));
                                }
                                break;
                            case accion_realizar_sin_dato:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    actionsToTakeList.add(actionsToTakeFacade.find((short) 13));
                                }
                                break;
                            case responsable:
                                //newNonFatalInjury.setUserId(usersFacade.find(Integer.parseInt(value)));
                                break;
                            default:
                        }
                    }
                }
                //SI NO HAY INSTITUCION RECEPTORA SE TRATA DE COLOCAR LA FUENTE DEL PROYECTO
                if (newNonFatalDomesticViolence.getDomesticViolenceDataSourceId() == null) {
                    if (projectsMB.getCurrentSourceId() != 75) {//si la fuente es diferente de observatorio del delito
                        newNonFatalDomesticViolence.setDomesticViolenceDataSourceId(nonFatalDataSourcesFacade.find(projectsMB.getCurrentSourceId()));
                        newNonFatalInjury.setNonFatalDataSourceId(nonFatalDataSourcesFacade.find(projectsMB.getCurrentSourceId()));
                    }
                }

                //SI NO HAY FECHA DE CONSULTA TRATAR DE CALCULAR MEDIANTE LAS VARIABLES dia_evento, mes_evento, año_evento
                if (newNonFatalInjury.getCheckupDate() == null) {
                    dia1 = haveData(dia1);
                    mes1 = haveData(mes1);
                    ao1 = haveData(ao1);
                    if (dia1 != null && mes1 != null && ao1 != null) {
                        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                        Date fechaI;
                        fechaI = formato.parse(dia1 + "/" + mes1 + "/" + ao1);
                        newNonFatalInjury.setCheckupDate(fechaI);
                    }
                }
                //SI NO HAY HORA DE CONSULTA TRATAR DE CALCULAR MEDIANTE LAS VARIABLES hora_evento,minuto_evento,am_pm
                if (newNonFatalInjury.getCheckupTime() == null) {
                    horas1 = haveData(horas1);
                    minutos1 = haveData(minutos1);
                    ampm1 = haveData(ampm1);
                    if (horas1 != null && minutos1 != null && ampm1 != null) {
                        hourInt = Integer.parseInt(horas1);
                        minuteInt = Integer.parseInt(minutos1);
                        if (ampm1.compareTo("2") == 0) {
                            hourInt = hourInt + 12;
                            if (hourInt == 24) {
                                hourInt = 0;
                            }
                        }
                        currentDate = new Date();
                        currentDate.setHours(hourInt);
                        currentDate.setMinutes(minuteInt);
                        currentDate.setSeconds(0);
                        newNonFatalInjury.setCheckupTime(currentDate);
                    }
                }


                //SI NO HAY FECHA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES dia_evento, mes_evento, año_evento
                if (newNonFatalInjury.getInjuryDate() == null) {
                    dia = haveData(dia);
                    mes = haveData(mes);
                    ao = haveData(ao);
                    if (dia != null && mes != null && ao != null) {
                        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                        Date fechaI;
                        fechaI = formato.parse(dia + "/" + mes + "/" + ao);
                        newNonFatalInjury.setInjuryDate(fechaI);
                    }
                }
                //SI NO HAY HORA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES hora_evento,minuto_evento,am_pm
                if (newNonFatalInjury.getInjuryTime() == null) {
                    horas = haveData(horas);
                    minutos = haveData(minutos);
                    ampm = haveData(ampm);
                    if (horas != null && minutos != null && ampm != null) {
                        hourInt = Integer.parseInt(horas);
                        minuteInt = Integer.parseInt(minutos);
                        if (ampm.compareTo("2") == 0) {
                            hourInt = hourInt + 12;
                            if (hourInt == 24) {
                                hourInt = 0;
                            }
                        }
                        currentDate = new Date();
                        currentDate.setHours(hourInt);
                        currentDate.setMinutes(minuteInt);
                        currentDate.setSeconds(0);
                        newNonFatalInjury.setInjuryTime(currentDate);
                    }
                }
                //SI NO HAY FECHA DE CONSULTA PASAR LA DE EVENTO
                if (newNonFatalInjury.getCheckupDate() == null) {
                    if (newNonFatalInjury.getInjuryDate() != null) {
                        newNonFatalInjury.setCheckupDate(newNonFatalInjury.getInjuryDate());
                    }
                }
                //SI NO HAY HORA DE CONSULTA PASAR LA DE EVENTO
                if (newNonFatalInjury.getCheckupTime() == null) {
                    if (newNonFatalInjury.getInjuryTime() != null) {
                        newNonFatalInjury.setCheckupTime(newNonFatalInjury.getInjuryTime());
                    }
                }

                //SI NO HAY FECHA DE EVENTO PASAR LA DE CONSULTA
                if (newNonFatalInjury.getInjuryDate() == null) {
                    if (newNonFatalInjury.getCheckupDate() != null) {
                        newNonFatalInjury.setInjuryDate(newNonFatalInjury.getCheckupDate());
                    }
                }


                //SI NO HAY HORA DE EVENTO PASAR LA DE CONSULTA
                if (newNonFatalInjury.getInjuryTime() == null) {
                    if (newNonFatalInjury.getCheckupTime() != null) {
                        newNonFatalInjury.setInjuryTime(newNonFatalInjury.getCheckupTime());
                    }
                }

                //SI LA HORA DE LA CONSULTA ES 0000 PASAR LA HORA DEL EVENTO A LA DE LA CONSULTA
                if (newNonFatalInjury.getCheckupTime() != null) {
                    if (newNonFatalInjury.getInjuryTime() != null) {
                        int hour = newNonFatalInjury.getInjuryTime().getHours();
                        int minute = newNonFatalInjury.getInjuryTime().getMinutes();
                        if (hour == 0 && minute == 0) {
                            newNonFatalInjury.setInjuryTime(newNonFatalInjury.getCheckupTime());
                        }
                    } else {
                        newNonFatalInjury.setInjuryTime(newNonFatalInjury.getCheckupTime());
                    }
                }

                //SI NO HAY DIA DE LA SEMANA DEL EVENTO SE CALCULA
                if (newNonFatalInjury.getInjuryDate() != null) {
                    if (newNonFatalInjury.getInjuryDayOfWeek() == null) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(newNonFatalInjury.getInjuryDate());
                        newNonFatalInjury.setInjuryDayOfWeek(intToDay(cal.get(Calendar.DAY_OF_WEEK)));
                    }
                }

                //SI FECHA DE EVENTO MAYOR A FECHA DE CONSULTA SE INVIERTEN                
                if (newNonFatalInjury.getInjuryDate().getTime() > newNonFatalInjury.getCheckupDate().getTime()) {
                    Date auxInjuryDate = newNonFatalInjury.getInjuryDate();
                    newNonFatalInjury.setInjuryDate(newNonFatalInjury.getCheckupDate());
                    newNonFatalInjury.setCheckupDate(auxInjuryDate);
                }

                //SI NO SE DETERMINA EL BARRIO SE COLOCA SIN DATO URBANO
                if (newVictim.getVictimNeighborhoodId() == null) {
                    newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find((int) 52001));
                }
                //SI NO SE DETERMINA EL BARRIO SE COLOCA SIN DATO URBANO
                if (newNonFatalInjury.getInjuryNeighborhoodId() == null) {
                    newNonFatalInjury.setInjuryNeighborhoodId(neighborhoodsFacade.find((int) 52001));
                }


                //UNION DE NOMBRES Y APELLIOS
                name = name + " " + surname;
                if (name.trim().length() > 1) {
                    newVictim.setVictimName(name.trim());
                }
                //EDAD Y TIPO DE EDAD
                if (newVictim.getVictimAge() != null) {//HAY EDAD 
                    if (newVictim.getAgeTypeId() == null) {//no hay tipo de edad
                        newVictim.setAgeTypeId((short) 1);//tiṕo de edad años
                    }
                } else {
                    newVictim.setAgeTypeId((short) 4);//tiṕo de edad sin determinar
                }
                //SI NO SE DETERMINA LA INSTITUCION DE SALUD SE ALMACENA LA QUE VIENE DEL FORMULARIO                
                if (newNonFatalInjury.getNonFatalDataSourceId() == null) {
                    if (currentSource != 21) {//1=compareTo("OBSERVATORIO DEL DELITO")
                        newNonFatalInjury.setNonFatalDataSourceId(nonFatalDataSourcesFacade.find((short) currentSource));
                    }
                }
                //SI NO SE DETERMINA LA EDAD VERIFICAR SI HAY FECHA DE NACIMIENTO
                if (newVictim.getVictimDateOfBirth() != null) {
                    if (newVictim.getVictimAge() == null) {
                        int birthMonths;
                        int eventMonths;

                        Calendar systemCalendar = Calendar.getInstance();
                        Calendar birthCalendar = Calendar.getInstance();
                        birthCalendar.setTime(newVictim.getVictimDateOfBirth());

                        try {//DETERMINO LA EDAD EN MESES
                            birthMonths = birthCalendar.get(Calendar.YEAR);
                            birthMonths = birthMonths * 12;
                            birthMonths = birthMonths + birthCalendar.get(Calendar.MONTH);
                            if (newNonFatalInjury.getInjuryDate() != null) {//SE CALCULA EDAD SEGUN LA FECHA DE EVENTO
                                systemCalendar.setTime(newNonFatalInjury.getInjuryDate());
                            }
                            eventMonths = systemCalendar.get(Calendar.YEAR);
                            eventMonths = eventMonths * 12;
                            eventMonths = eventMonths + systemCalendar.get(Calendar.MONTH);

                            int ageMonths = eventMonths - birthMonths;
                            if (ageMonths < 0) {
                                System.out.println("ERROR fecha de nacimiento mayor a la del sistema o evento: ");
                            } else {
                                int ageYears = (int) (ageMonths / 12);
                                if (ageYears == 0) {
                                    ageYears = 1;
                                }
                                newVictim.setVictimAge((short) ageYears);
                                newVictim.setAgeTypeId((short) 1);//aqui por defecto seria sin dato, si no se conoce
                            }
                        } catch (Exception ex) {
                            System.out.println("Error 24 en " + this.getClass().getName() + ":" + ex.toString());
                        }
                    }
                }

                //agrego las listas no vacias
                if (!abuseTypesList.isEmpty()) {
                    newNonFatalDomesticViolence.setAbuseTypesList(abuseTypesList);
                } else {
                    abuseTypesList.add(new AbuseTypes((short) 7));
                    newNonFatalDomesticViolence.setAbuseTypesList(abuseTypesList);
                }

                if (!aggressorTypesList.isEmpty()) {
                    newNonFatalDomesticViolence.setAggressorTypesList(aggressorTypesList);
                } else {
                    aggressorTypesList.add(new AggressorTypes((short) 9));
                    newNonFatalDomesticViolence.setAggressorTypesList(aggressorTypesList);
                }

                //acciones a realizar                
                boolean actionIsEmpty;
                if (actionsToTakeList.isEmpty()) {//esta vacio
                    actionIsEmpty = true;
                } else {//no esta vacio
                    if (actionsToTakeList.size() == 1) {//puede que diga sin dato
                        if (actionsToTakeList.get(0).getActionId() == 13) {//es sin dato
                            actionIsEmpty = true;
                        } else {//no esta vacio
                            actionIsEmpty = false;
                        }
                    } else {//tiene mas de uno no esta vacio
                        actionIsEmpty = false;
                    }
                }

                if (!actionIsEmpty) {//no esta vacio se ingresa
                    newNonFatalDomesticViolence.setActionsToTakeList(actionsToTakeList);
                } else {//se debe calcular en base a source(fuente) y form(ficha)
                    actionsToTakeList = calculeActionToTake(projectsMB.getCurrentSourceId(), projectsMB.getCurrentFormId());

                    if (actionsToTakeList.isEmpty()) { //no se pudo determinar accion a realizar a traves de fuente
                        actionsToTakeList.add(new ActionsToTake((short) 13));
                    } else { //se determino la accion a realizar a traves de la fuente
                        newNonFatalDomesticViolence.setActionsToTakeList(actionsToTakeList);
                    }
                }

                //grupo vulnerable
                if (!vulnerableGroupList.isEmpty()) {
                    newVictim.setVulnerableGroupsList(vulnerableGroupList);
                }
                if (!othersList.isEmpty()) {
                    newVictim.setOthersList(othersList);
                }
                newNonFatalInjury.setInjuryId(injuriesFacade.find(Short.parseShort("53")));

                if (newVictim.getVictimNid() == null) {//NO HAY NUMERO DE IDENTIFICACION 
                    newVictim.setVictimNid(String.valueOf(genNnFacade.findMax() + 1));//asigno un consecutivo a la identificacion
                    newVictim.setVictimClass((short) 2);//nn
                    if (newVictim.getTypeId() == null) {//no hay tipo de identificacion
                        if (newVictim.getVictimAge() != null && newVictim.getAgeTypeId() != null && newVictim.getAgeTypeId() == 1) {//HAY EDAD Y HAY tipo de edad                            
                            if (newVictim.getVictimAge() > 17) {
                                newVictim.setTypeId(idTypesFacade.find((short) 6));//adulto sin identificacion                                
                            } else {
                                newVictim.setTypeId(idTypesFacade.find((short) 7));//menor sin identificacion
                            }
                        } else {//NO HAY EDAD
                            newVictim.setTypeId(idTypesFacade.find((short) 9));//tipo de identificacoin sin determinar
                        }
                    }
                    int newGenNnId = genNnFacade.findMax() + 1;
                    connectionJdbcMB.non_query("UPDATE gen_nn SET cod_nn = " + newGenNnId + " where cod_nn IN (SELECT MAX(cod_nn) from gen_nn)");
                } else {//HAY NUMERO DE IDENTIFICACION
                    if (newVictim.getTypeId() == null) {//no hay tipo de identificacion
                        if (newVictim.getVictimAge() != null && newVictim.getAgeTypeId() != null && newVictim.getAgeTypeId() == 1) {//HAY EDAD Y HAY tipo de edad                            
                            if (newVictim.getVictimAge() > 17) {
                                newVictim.setTypeId(idTypesFacade.find((short) 6));//adulto sin identificacion                                
                            } else {
                                newVictim.setTypeId(idTypesFacade.find((short) 7));//menor sin identificacion
                            }
                        } else {//NO HAY EDAD
                            newVictim.setTypeId(idTypesFacade.find((short) 9));//tipo de identificacoin sin determinar
                        }
                    }
                }

                //CORRESPONDENCIA ENTRE EDAD Y TIPO DE IDENTIFICACION
                if (newVictim.getTypeId() != null) {//no hay tipo de identificacion
                    if (newVictim.getVictimAge() != null) {//HAY EDAD Y HAY tipo de edad
                        if (newVictim.getVictimAge() < 18) {//menor de edad
                            if (newVictim.getTypeId().getTypeId() == (short) 1 ||//cedula de ciudadania
                                    newVictim.getTypeId().getTypeId() == (short) 2 ||//cedula de extranjeria
                                    newVictim.getTypeId().getTypeId() == (short) 3 ||//pasaporte
                                    newVictim.getTypeId().getTypeId() == (short) 6) {//adulto sin identificacion
                                newVictim.setTypeId(idTypesFacade.find((short) 9));//sin determinar
                            }
                        } else {//mayor de edad
                            if (newVictim.getTypeId().getTypeId() == (short) 5 ||//tarjeta de identidad
                                    newVictim.getTypeId().getTypeId() == (short) 4 ||//registro civil
                                    newVictim.getTypeId().getTypeId() == (short) 7) {//menor sin identificacion
                                newVictim.setTypeId(idTypesFacade.find((short) 9));//sin determinar
                            }
                        }

                    }
                }

                //PERSISTO//////////////////////////////////////////////////////////////////
                try {
                    newNonFatalInjury.setVictimId(newVictim);
                    victimsFacade.create(newVictim);//PERSISTO LA VICTIMA                
                    nonFatalInjuriesFacade.create(newNonFatalInjury);//PERSISTO LA LESION NO FATAL                    
                    newNonFatalDomesticViolence.setNonFatalInjuryId(newNonFatalInjury.getNonFatalInjuryId());
                    nonFatalDomesticViolenceFacade.create(newNonFatalDomesticViolence);
                    applicationControlMB.removeNonfatalReservedIdentifiers(newNonFatalInjury.getNonFatalInjuryId());
                    applicationControlMB.removeVictimsReservedIdentifiers(newVictim.getVictimId());

                } catch (Exception e) {
                    System.out.println("Error 25 en " + this.getClass().getName() + ":" + e.toString());
                }
                tuplesProcessed++;
                progress = (int) (tuplesProcessed * 100) / tuplesNumber;
                System.out.println("PROGRESO INGRESANDO VIF: " + String.valueOf(progress));
            }
            progress = 100;
            System.out.println("PROGRESO INGRESANDO VIF: " + String.valueOf(progress));

        } catch (SQLException ex) {
            System.out.println("Error 26 en " + this.getClass().getName() + ":" + ex.toString());
        } catch (Exception ex) {
            System.out.println("Error 27 en " + this.getClass().getName() + ":" + ex.toString());
        }
    }

    /**
     * allows charge the tab SIVIGILA-VIF.
     */
    private List<ActionsToTake> calculeActionToTake(int source, String form) {
        List<ActionsToTake> ids = new ArrayList<>();
        switch (source) {
            case 70://INSTITUTO DE MEDICINA LEGAL Y CIENCIAS FORENSES
                ids.add(actionsToTakeFacade.find((short) 3));//dictamen medicina legal
                break;
            case 66://ZONAL 1 ICBF
            case 67://ZONAL 2 ICBF
                ids.add(actionsToTakeFacade.find((short) 8));//medidas de proteccion
                ids.add(actionsToTakeFacade.find((short) 10));//atencion psicosocial
                ids.add(actionsToTakeFacade.find((short) 11));//restablecimiento de derechos                
                break;
            case 82://"CAIVAS 15 FISCALIA"
            case 80://CAIVAS 52"
            case 68://CAIVAS FISCALIA 15"
            case 71://CAIVAS FISCALIA 52"
                ids.add(actionsToTakeFacade.find((short) 5));//remision a medicina legal
                break;

            case 69://CAVIF  FISCALIA 10"
                ids.add(actionsToTakeFacade.find((short) 12));//OTRO
                break;
        }
        //si ids sigue vacio se verifica si la fuente 
        if (ids.isEmpty()) {
            switch (form) {
                case "SCC-F-032"://LCENF
                case "SCC-F-033"://VIF      //si esta vacio es por que no era los validados por source
                case "SIVIGILA-VIF"://SIVIGILA
                    ids.add(actionsToTakeFacade.find((short) 14));//atencion en salud
                    break;
            }
        }
        return ids;
    }

    public void register_SIVIGILA() {
        /*
         * *********************************************************************
         * CARGA DE REGISTROS DE LA FICHA SIVIGILA-VIF
         * ********************************************************************
         */
        tuplesNumber = 0;
        tuplesProcessed = 0;
        progress = 0;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            tuplesNumber = determineTuplesNumber();//determino numero de tuplas  
            resultSetFileData = determineRecords();//resulset con los registros a procesar

            newUngroupedTags = new UngroupedTags();
            newUngroupedTags.setUngroupedTagId(ungroupedTagsFacade.findMax() + 1);
            newUngroupedTags.setUngroupedTagName(determineTagName(projectsMB.getCurrentProjectName()));
            newUngroupedTags.setUngroupedTagDate(new Date());
            newUngroupedTags.setFormId(nameForm);
            newUngroupedTags.setCurrentTagId(ungroupedTagsFacade.findMax() + 1);
            ungroupedTagsFacade.create(newUngroupedTags);

            newTag = new Tags();//(maxTag, projectsMB.getNameFile(), projectsMB.getNameFile());
            newTag.setTagId(ungroupedTagsFacade.findMax());
            newTag.setTagName(determineTagName(projectsMB.getCurrentProjectName()));
            newTag.setTagFileInput(projectsMB.getCurrentFileName());
            newTag.setTagFileStored(projectsMB.getCurrentFileName());
            newTag.setFormId(formsFacade.find(nameForm));
            tagsFacade.create(newTag);

            lastTagNameCreated = newTag.getTagName();

            while (resultSetFileData.next()) {//recorro cada uno de los registros de la tabla temp                    
                Victims newVictim = new Victims();
                newVictim.setVictimId(applicationControlMB.addVictimsReservedIdentifiers());
                newVictim.setVictimClass((short) 1);
                newVictim.setTagId(tagsFacade.find(newTag.getTagId()));
                newVictim.setFirstTagId(newVictim.getTagId().getTagId());
                NonFatalInjuries newNonFatalInjury = new NonFatalInjuries();

                NonFatalDomesticViolence newNonFatalDomesticViolence = new NonFatalDomesticViolence();
                newNonFatalInjury.setNonFatalInjuryId(applicationControlMB.addNonfatalReservedIdentifiers());
                SivigilaEvent newSivigilaEvent = new SivigilaEvent(newNonFatalInjury.getNonFatalInjuryId());
                SivigilaVictim newSivigilaVictim = new SivigilaVictim(applicationControlMB.addSivigilaVictimReservedIdentifiers());
                SivigilaAggresor newSivigilaAggresor = new SivigilaAggresor(applicationControlMB.addSivigilaAggresorReservedIdentifiers());
                newNonFatalInjury.setInputTimestamp(new Date());
                List<AbuseTypes> abuseTypesList = new ArrayList<>();
                List<PublicHealthActions> publicHealthActionsList = new ArrayList<>();
                List<VulnerableGroups> vulnerableGroupList = new ArrayList<>();// lista vector victim_vulnerable_group

                value = "";
                name = "";
                surname = "";

                Object[] arrayInJava = (Object[]) resultSetFileData.getArray(3).getArray();
                for (int posCol = 0; posCol < arrayInJava.length; posCol++) {
                    value = null;
                    String splitColumnAndValue[] = arrayInJava[posCol].toString().split("<=>");
                    relationVar = currentRelationsGroup.findRelationVarByNameFound(splitColumnAndValue[0]);//determino la relacion de variables
                    if (relationVar != null) {
                        switch (DataTypeEnum.convert(remove_v(relationVar.getFieldType()))) {//determino valor a ingresar usando: isNumeric,isAge... etc
                            case text:
                                value = splitColumnAndValue[1];
                                break;
                            case integer:
                                value = isNumeric(splitColumnAndValue[1]);
                                break;
                            case age:
                                value = isAge(splitColumnAndValue[1]);
                                break;
                            case date:
                                value = isDate(splitColumnAndValue[1], relationVar.getDateFormat());
                                break;
                            case military:
                                value = isMilitary(splitColumnAndValue[1]);
                                break;
                            case hour:
                                value = isHour(splitColumnAndValue[1]);
                                break;
                            case minute:
                                value = isMinute(splitColumnAndValue[1]);
                                break;
                            case day:
                                value = isDay(splitColumnAndValue[1]);
                                break;
                            case month:
                                value = isMonth(splitColumnAndValue[1]);
                                break;
                            case year:
                                value = isYear(splitColumnAndValue[1]);
                                break;
                            case percentage:
                                value = isPercentage(splitColumnAndValue[1]);
                                break;
                            case NOVALUE:
                                value = isCategorical(splitColumnAndValue[1], relationVar);
                                break;
                        }
                    }

                    continueProcces = false;
                    if (value != null) {
                        if (value.trim().length() != 0) {
                            continueProcces = true;
                        }
                    }
                    if (continueProcces) {
                        switch (SIVIGILA_VIF_enum.convert(relationVar.getNameExpected())) {
                            // ************************************************DATOS PARA LA TABLA victims                                
                            case primer_apellido:
                                if (surname.trim().length() != 0) {
                                    surname = value + " " + surname;
                                } else {
                                    surname = value;
                                }
                                break;
                            case segundo_apellido:
                                if (surname.trim().length() != 0) {
                                    surname = surname + " " + value;
                                } else {
                                    surname = value;
                                }
                                break;
                            case primer_nombre:
                                if (name.trim().length() != 0) {
                                    name = value + " " + name;
                                } else {
                                    name = value;
                                }
                                break;
                            case segundo_nombre:
                                if (name.trim().length() != 0) {
                                    name = name + " " + value;
                                } else {
                                    name = value;
                                }
                                break;
                            case tipo_de_identificacion:
                                newVictim.setTypeId(idTypesFacade.find(Short.parseShort(value)));
                                break;
                            case numero_de_identificacion:
                                newVictim.setVictimNid(value);
                                break;
                            case edad_de_la_victima:
                                newVictim.setVictimAge(Short.parseShort(value));
                                break;
                            case medida_edad:
                                newVictim.setAgeTypeId(Short.parseShort(value));
                                break;
                            case genero_victima:
                                newVictim.setGenderId(gendersFacade.find(Short.parseShort(value)));
                                break;
                            case area_ocurrencia_de_los_hechos:
                                newSivigilaEvent.setArea(areasFacade.find(Short.parseShort(value)));
                                break;
                            case barrio_del_evento:
                                newNonFatalInjury.setInjuryNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(value)));
                                break;
                            case direccion_residencia:
                                newVictim.setVictimAddress(value);
                                break;
                            case ocupacion_victima:
                                newVictim.setJobId(jobsFacade.find(Integer.parseInt(value)));
                                break;
                            case tipo_de_regimen_en_salud:
                                newSivigilaVictim.setHealthCategory(sivigilaTipSsFacade.find(Integer.parseInt(value)));
                                break;
                            case aseguradora:
                                newVictim.setInsuranceId(insuranceFacade.find(value));
                                break;
                            case pertenencia_etnica:
                                newVictim.setEthnicGroupId(ethnicGroupsFacade.find(Short.parseShort(value)));
                                break;
                            case grupo_vulnerable:
                                VulnerableGroups auxVulnerableGroups = vulnerableGroupsFacade.find(Short.parseShort(value));
                                vulnerableGroupList.add(auxVulnerableGroups);
                                newVictim.setVulnerableGroupsList(vulnerableGroupList);
                                break;
                            case municipio_de_residencia:
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
                            case fecha_consulta:
                                try {
                                    currentDate = dateFormat.parse(value);
                                    newNonFatalInjury.setCheckupDate(currentDate);
                                } catch (ParseException ex) {
                                }
                                break;
                            case telefono_paciente:
                                newVictim.setVictimTelephone(value);
                                break;
                            case fecha_nacimiento:
                                try {
                                    currentDate = dateFormat.parse(value);
                                    newVictim.setVictimDateOfBirth(currentDate);
                                } catch (ParseException ex) {
                                }
                                break;
                            case nombre_profesional_salud:
                                newNonFatalInjury.setHealthProfessionalId(healthProfessionalsFacade.find(Integer.parseInt(value)));
                                break;
                            case naturaleza_violencia:
                                abuseTypesList.add(new AbuseTypes(Short.parseShort(value)));
                                break;
                            case escolaridad_victima:
                                newSivigilaVictim.setEducationalLevelId(sivigilaEducationalLevelFacade.find(Short.parseShort(value)));
                                break;
                            case factor_vulnerabilidad_victima:
                                newSivigilaVictim.setVulnerabilityId(sivigilaVulnerabilityFacade.find(Short.parseShort(value)));
                                break;
                            case otro_factor_vulnerabilidad:
                                newSivigilaVictim.setOtherVulnerability(value);
                                break;
                            case antecedentes_hecho_similar:
                                newSivigilaVictim.setAntecedent(boolean3Facade.find(Short.parseShort(value)));
                                break;
                            case presencia_alcohol_victima:
                                newNonFatalInjury.setUseAlcoholId(useAlcoholDrugsFacade.find(Short.parseShort(value)));
                                break;
                            case edad_del_agresor:
                                newSivigilaAggresor.setAge(Integer.parseInt(value));
                                break;
                            case genero_agresor:
                                newSivigilaAggresor.setGender(gendersFacade.find(Short.parseShort(value)));
                                break;
                            case ocupacion_agresor:
                                newSivigilaAggresor.setOccupation(jobsFacade.find(Integer.parseInt(value)));
                                break;
                            case escolaridad_agresor:
                                newSivigilaAggresor.setEducationalLevelId(sivigilaEducationalLevelFacade.find(Short.parseShort(value)));
                                break;
                            case relacion_familiar_victima:
                                newSivigilaAggresor.setRelativeId(aggressorTypesFacade.find(Short.parseShort(value)));
                                break;
                            case otra_relacion_familiar:
                                newSivigilaAggresor.setOtherRelative(value);
                                break;
                            case convive_con_agresor:
                                newSivigilaAggresor.setLiveTogether(boolean3Facade.find(Short.parseShort(value)));
                                break;
                            case relacion_no_familiar_victima:
                                newSivigilaAggresor.setNoRelativeId(sivigilaNoRelativeFacade.find(Short.parseShort(value)));
                                break;
                            case otra_relacion_no_familiar:
                                newSivigilaAggresor.setOtherNoRelative(value);
                                break;
                            case grupo_agresor:
                                newSivigilaAggresor.setGroupId(sivigilaGroupFacade.find(Short.parseShort(value)));
                                break;
                            case otro_grupo_agresor:
                                newSivigilaAggresor.setOtherGroup(value);
                                break;
                            case presencia_alcohol_agresor:
                                newSivigilaAggresor.setAlcoholOrDrugs(boolean3Facade.find(Short.parseShort(value)));
                                break;
                            case armas_utilizadas:
                                newSivigilaEvent.setMechanismId(sivigilaMechanismFacade.find(Short.parseShort(value)));
                                break;
                            case sustancia_intoxicacion:
                                newSivigilaEvent.setIntoxication(value);
                                break;
                            case otra_arma:
                                newSivigilaEvent.setOthers(value);
                                break;
                            case otro_mecanismo:
                                newSivigilaEvent.setOtherMechanismId(sivigilaOtherMechanismFacade.find(Short.parseShort(value)));
                                break;
                            case cual_otro_mecanismo:
                                newSivigilaEvent.setOthers(value);
                                break;
                            case fecha_evento:
                                try {
                                    currentDate = dateFormat.parse(value);
                                    newNonFatalInjury.setInjuryDate(currentDate);
                                } catch (ParseException ex) {
                                }
                                break;
                            case hora_evento:
                                n = new Date();
                                hourInt = Integer.parseInt(value.substring(0, 2));
                                minuteInt = Integer.parseInt(value.substring(2, 4));
                                n.setHours(hourInt);
                                n.setMinutes(minuteInt);
                                n.setSeconds(0);
                                newNonFatalInjury.setInjuryTime(n);
                                break;
                            case escenario_hechos:
                                newNonFatalInjury.setInjuryPlaceId(nonFatalPlacesFacade.find(Short.parseShort(value)));
                                break;
                            case direccion_evento:
                                newNonFatalInjury.setInjuryAddress(value);
                                break;
                            case zona_conflicto:
                                newSivigilaEvent.setConflictZone(boolean3Facade.find(Short.parseShort(value)));
                                break;
                            case accion_salud_atencion:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    publicHealthActionsList.add(new PublicHealthActions((short) 1));
                                }
                                break;
                            case accion_salud_profilaxis:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    publicHealthActionsList.add(new PublicHealthActions((short) 2));
                                }
                                break;
                            case accion_salud_anticonceptivo:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    publicHealthActionsList.add(new PublicHealthActions((short) 3));
                                }
                                break;
                            case accion_salud_orientacion:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    publicHealthActionsList.add(new PublicHealthActions((short) 4));
                                }
                                break;
                            case accion_salud_mental:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    publicHealthActionsList.add(new PublicHealthActions((short) 5));
                                }
                                break;
                            case accion_salud_autoridad:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    publicHealthActionsList.add(new PublicHealthActions((short) 6));
                                }
                                break;
                            case accion_salud_otro:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    publicHealthActionsList.add(new PublicHealthActions((short) 7));
                                }
                                break;
                            case recomienda_proteccion:
                                newSivigilaEvent.setRecommendedProtection(boolean3Facade.find(Short.parseShort(value)));
                                break;
                            case trabajo_de_campo:
                                newSivigilaEvent.setFurtherFieldwork(boolean3Facade.find(Short.parseShort(value)));
                                break;
                            case institucion_de_salud:
                                newNonFatalInjury.setNonFatalDataSourceId(nonFatalDataSourcesFacade.find(Short.parseShort(value)));
                                newNonFatalDomesticViolence.setDomesticViolenceDataSourceId(nonFatalDataSourcesFacade.find(Short.parseShort(value)));
                                break;
                            default:
                        }
                    }
                }

                //SI NO HAY INSTITUCION RECEPTORA SE TRATA DE COLOCAR LA FUENTE DEL PROYECTO
                if (newNonFatalDomesticViolence.getDomesticViolenceDataSourceId() == null) {
                    if (projectsMB.getCurrentSourceId() != 75) {//si la fuente es diferente de observatorio del delito
                        newNonFatalDomesticViolence.setDomesticViolenceDataSourceId(nonFatalDataSourcesFacade.find(projectsMB.getCurrentSourceId()));
                        newNonFatalInjury.setNonFatalDataSourceId(nonFatalDataSourcesFacade.find(projectsMB.getCurrentSourceId()));
                    }
                }

                //DATOS DE LA CONLUSTA..........................................
                //SI NO HAY FECHA DE CONSULTA PASAR LA DE EVENTO
                if (newNonFatalInjury.getCheckupDate() == null) {
                    if (newNonFatalInjury.getInjuryDate() != null) {
                        newNonFatalInjury.setCheckupDate(newNonFatalInjury.getInjuryDate());
                    }
                }

                //SI NO HAY HORA DE CONSULTA PASAR LA DE EVENTO
                if (newNonFatalInjury.getCheckupTime() == null) {
                    if (newNonFatalInjury.getInjuryTime() != null) {
                        newNonFatalInjury.setCheckupTime(newNonFatalInjury.getInjuryTime());
                    }
                }

                //SI NO HAY DIA DE LA SEMANA DEL EVENTO SE CALCULA
                if (newNonFatalInjury.getInjuryDate() != null) {
                    if (newNonFatalInjury.getInjuryDayOfWeek() == null) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(newNonFatalInjury.getInjuryDate());
                        newNonFatalInjury.setInjuryDayOfWeek(intToDay(cal.get(Calendar.DAY_OF_WEEK)));
                    }
                }
                //SI FECHA DE EVENTO MAYOR A FECHA DE CONSULTA SE INVIERTEN                
                if (newNonFatalInjury.getInjuryDate().getTime() > newNonFatalInjury.getCheckupDate().getTime()) {
                    Date auxInjuryDate = newNonFatalInjury.getInjuryDate();
                    newNonFatalInjury.setInjuryDate(newNonFatalInjury.getCheckupDate());
                    newNonFatalInjury.setCheckupDate(auxInjuryDate);
                }

                //SI NO SE DETERMINA EL BARRIO SE COLOCA SIN DATO URBANO
                if (newVictim.getVictimNeighborhoodId() == null) {
                    newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find((int) 52001));
                }
                //SI NO SE DETERMINA EL BARRIO SE COLOCA SIN DATO URBANO
                if (newNonFatalInjury.getInjuryNeighborhoodId() == null) {
                    newNonFatalInjury.setInjuryNeighborhoodId(neighborhoodsFacade.find((int) 52001));
                }
                //UNION DE NOMBRES Y APELLIOS
                name = name + " " + surname;
                if (name.trim().length() > 1) {
                    newVictim.setVictimName(name.trim());
                }
                //EDAD Y TIPO DE EDAD
                if (newVictim.getVictimAge() != null) {//HAY EDAD 
                    if (newVictim.getAgeTypeId() == null) {//no hay tipo de edad
                        newVictim.setAgeTypeId((short) 1);//tiṕo de edad años
                    }
                } else {
                    newVictim.setAgeTypeId((short) 4);//tiṕo de edad sin determinar
                }
                //SI NO SE DETERMINA LA INSTITUCION DE SALUD SE ALMACENA LA QUE VIENE DEL FORMULARIO                
                if (newNonFatalInjury.getNonFatalDataSourceId() == null) {
                    if (currentSource != 21) {//1=compareTo("OBSERVATORIO DEL DELITO")
                        newNonFatalInjury.setNonFatalDataSourceId(nonFatalDataSourcesFacade.find((short) currentSource));
                    }
                }
                //SI NO SE DETERMINA LA EDAD VERIFICAR SI HAY FECHA DE NACIMIENTO
                if (newVictim.getVictimDateOfBirth() != null) {
                    if (newVictim.getVictimAge() == null) {
                        int birthMonths;
                        int eventMonths;

                        Calendar systemCalendar = Calendar.getInstance();
                        Calendar birthCalendar = Calendar.getInstance();
                        birthCalendar.setTime(newVictim.getVictimDateOfBirth());

                        try {//DETERMINO LA EDAD EN MESES
                            birthMonths = birthCalendar.get(Calendar.YEAR);
                            birthMonths = birthMonths * 12;
                            birthMonths = birthMonths + birthCalendar.get(Calendar.MONTH);
                            if (newNonFatalInjury.getInjuryDate() != null) {//SE CALCULA EDAD SEGUN LA FECHA DE EVENTO
                                systemCalendar.setTime(newNonFatalInjury.getInjuryDate());
                            }
                            eventMonths = systemCalendar.get(Calendar.YEAR);
                            eventMonths = eventMonths * 12;
                            eventMonths = eventMonths + systemCalendar.get(Calendar.MONTH);

                            int ageMonths = eventMonths - birthMonths;
                            if (ageMonths < 0) {
                                System.out.println("ERROR fecha de nacimiento mayor a la del sistema o evento: ");
                            } else {
                                int ageYears = (int) (ageMonths / 12);
                                if (ageYears == 0) {
                                    ageYears = 1;
                                }
                                newVictim.setVictimAge((short) ageYears);
                                newVictim.setAgeTypeId((short) 1);//aqui por defecto seria sin dato, si no se conoce
                            }
                        } catch (Exception ex) {
                            System.out.println("Error 28 en " + this.getClass().getName() + ":" + ex.toString());
                        }
                    }
                }

                //agrego las listas no vacias
                if (!publicHealthActionsList.isEmpty()) {
                    newSivigilaEvent.setPublicHealthActionsList(publicHealthActionsList);
                } else {
                    publicHealthActionsList.add(new PublicHealthActions((short) 9));//ATENCION en salud
                    newSivigilaEvent.setPublicHealthActionsList(publicHealthActionsList);
                }

                if (!abuseTypesList.isEmpty()) {
                    newNonFatalDomesticViolence.setAbuseTypesList(abuseTypesList);
                } else {
                    abuseTypesList.add(new AbuseTypes((short) 7));
                    newNonFatalDomesticViolence.setAbuseTypesList(abuseTypesList);
                }

                newNonFatalInjury.setInjuryId(injuriesFacade.find(Short.parseShort("56")));

                if (newVictim.getVictimNid() == null) {//NO HAY NUMERO DE IDENTIFICACION 
                    newVictim.setVictimNid(String.valueOf(genNnFacade.findMax() + 1));//asigno un consecutivo a la identificacion
                    newVictim.setVictimClass((short) 2);//nn
                    if (newVictim.getTypeId() == null) {//no hay tipo de identificacion
                        if (newVictim.getVictimAge() != null && newVictim.getAgeTypeId() != null && newVictim.getAgeTypeId() == 1) {//HAY EDAD Y HAY tipo de edad                            
                            if (newVictim.getVictimAge() > 17) {
                                newVictim.setTypeId(idTypesFacade.find((short) 6));//adulto sin identificacion                                
                            } else {
                                newVictim.setTypeId(idTypesFacade.find((short) 7));//menor sin identificacion
                            }
                        } else {//NO HAY EDAD
                            newVictim.setTypeId(idTypesFacade.find((short) 9));//tipo de identificacoin sin determinar
                        }
                    }
                    int newGenNnId = genNnFacade.findMax() + 1;
                    connectionJdbcMB.non_query("UPDATE gen_nn SET cod_nn = " + newGenNnId + " where cod_nn IN (SELECT MAX(cod_nn) from gen_nn)");
                } else {//HAY NUMERO DE IDENTIFICACION
                    if (newVictim.getTypeId() == null) {//no hay tipo de identificacion
                        if (newVictim.getVictimAge() != null && newVictim.getAgeTypeId() != null && newVictim.getAgeTypeId() == 1) {//HAY EDAD Y HAY tipo de edad                            
                            if (newVictim.getVictimAge() > 17) {
                                newVictim.setTypeId(idTypesFacade.find((short) 6));//adulto sin identificacion                                
                            } else {
                                newVictim.setTypeId(idTypesFacade.find((short) 7));//menor sin identificacion
                            }
                        } else {//NO HAY EDAD
                            newVictim.setTypeId(idTypesFacade.find((short) 9));//tipo de identificacoin sin determinar
                        }
                    }
                }

                //CORRESPONDENCIA ENTRE EDAD Y TIPO DE IDENTIFICACION
                if (newVictim.getTypeId() != null) {//no hay tipo de identificacion
                    if (newVictim.getVictimAge() != null) {//HAY EDAD Y HAY tipo de edad
                        if (newVictim.getVictimAge() < 18) {//menor de edad
                            if (newVictim.getTypeId().getTypeId() == (short) 1 ||//cedula de ciudadania
                                    newVictim.getTypeId().getTypeId() == (short) 2 ||//cedula de extranjeria
                                    newVictim.getTypeId().getTypeId() == (short) 3 ||//pasaporte
                                    newVictim.getTypeId().getTypeId() == (short) 6) {//adulto sin identificacion
                                newVictim.setTypeId(idTypesFacade.find((short) 9));//sin determinar
                            }
                        } else {//mayor de edad
                            if (newVictim.getTypeId().getTypeId() == (short) 5 ||//tarjeta de identidad
                                    newVictim.getTypeId().getTypeId() == (short) 4 ||//registro civil
                                    newVictim.getTypeId().getTypeId() == (short) 7) {//menor sin identificacion
                                newVictim.setTypeId(idTypesFacade.find((short) 9));//sin determinar
                            }
                        }
                    }
                }

                //PERSISTO//////////////////////////////////////////////////////////////////
                try {
                    newNonFatalInjury.setVictimId(newVictim);
                    victimsFacade.create(newVictim);//PERSISTO LA VICTIMA                
                    nonFatalInjuriesFacade.create(newNonFatalInjury);//PERSISTO LA LESION NO FATAL                    
                    newNonFatalDomesticViolence.setNonFatalInjuryId(newNonFatalInjury.getNonFatalInjuryId());
                    nonFatalDomesticViolenceFacade.create(newNonFatalDomesticViolence);
                    newSivigilaVictim.setSivigilaVictimId(newVictim.getVictimId());
                    sivigilaVictimFacade.create(newSivigilaVictim);
                    newNonFatalInjury.setVictimId(newVictim);
                    sivigilaAggresorFacade.create(newSivigilaAggresor);
                    newSivigilaEvent.setNonFatalInjuryId(newNonFatalInjury.getNonFatalInjuryId());
                    newSivigilaEvent.setSivigilaVictimId(newSivigilaVictim);
                    newSivigilaEvent.setNonFatalDomesticViolence(newNonFatalDomesticViolence);
                    newSivigilaEvent.setSivigilaAgresorId(newSivigilaAggresor);
                    sivigilaEventFacade.create(newSivigilaEvent);

                    applicationControlMB.removeNonfatalReservedIdentifiers(newNonFatalInjury.getNonFatalInjuryId());
                    applicationControlMB.removeVictimsReservedIdentifiers(newVictim.getVictimId());
                    applicationControlMB.removeSivigilaAggresorReservedIdentifiers(newSivigilaAggresor.getSivigilaAgresorId());
                    applicationControlMB.removeSivigilaVictimReservedIdentifiers(newSivigilaVictim.getSivigilaVictimId());

                } catch (Exception e) {
                    System.out.println("Error 29 en " + this.getClass().getName() + ":" + e.toString());
                }
                tuplesProcessed++;
                progress = (int) (tuplesProcessed * 100) / tuplesNumber;
                System.out.println("PROGRESO INGRESANDO SIVIGILA: " + String.valueOf(progress));
            }
            progress = 100;
            System.out.println("PROGRESO INGRESANDO SIVIGILA: " + String.valueOf(progress));

        } catch (SQLException ex) {
            System.out.println("Error 30 en " + this.getClass().getName() + ":" + ex.toString());
        } catch (Exception ex) {
            System.out.println("Error 31 en " + this.getClass().getName() + ":" + ex.toString());
        }
    }

    /**
     * allows recording data on the various tabs or forms.
     *
     * @throws ParseException
     */
    public void btnRegisterDataClick() throws ParseException {
        nameForm = projectsMB.getCurrentFormId();
        currentRelationsGroup = relationGroupFacade.find(projectsMB.getCurrentRelationsGroupId());//tomo el grupos_vulnerables de relaciones de valores y de variables
        btnRegisterDataDisabled = true;
        errorOnComplete = "";

        if (errorsControlMB.getErrorsList() != null && errorsControlMB.getErrorsList().isEmpty()) {
            continueProcces = true;
        } else {
            errorOnComplete = "Se deben corregir todos los errores para poder realizar la carga.";
            continueProcces = false;
            progress = 100;
        }
        //verifico que no exista una carga con este nombre ya realizada
        try {
            ResultSet rs = connectionJdbcMB.consult("SELECT * FROM ungrouped_tags WHERE ungrouped_tag_name ILIKE '" + projectsMB.getCurrentProjectName() + "'");
            if (rs.next()) {
                errorOnComplete = "Ya existe una conjunto de registros cargados con el mismo nombre.    \n "
                        + "Para poder realizar la carga de estos registros se debe dirigir a la sección: "
                        + "'Gestión de conjuntos' y eliminar el conjunto: '" + projectsMB.getCurrentProjectName() + "'. "
                        + " Si este conjunto ya esta agrupado en otro se debe desagrupar para poder eliminarlo.";
                progress = 100;
                continueProcces = false;
            }
        } catch (Exception e) {
        }
        if (continueProcces == true) {
            switch (FormsEnum.convert(nameForm.replace("-", "_"))) {
                case SCC_F_028:
                    registerSCC_F_028();
                    break;
                case SCC_F_029:
                    registerSCC_F_029();
                    break;
                case SCC_F_030:
                    registerSCC_F_030();
                    break;
                case SCC_F_031:
                    registerSCC_F_031();
                    break;
                case SCC_F_032:
                    registerSCC_F_032();
                    break;
                case SCC_F_033:
                    registerSCC_F_033();
                    break;
                case SIVIGILA_VIF:
                    register_SIVIGILA();
                    break;
            }
        }

    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //VALIDACIONES ---------------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * determines the day.
     *
     * @param i: variable that contains the day.
     * @return
     */
    private String intToDay(int i) {
        if (i == Calendar.MONDAY) {
            return "Lunes";
        } else if (i == Calendar.TUESDAY) {
            return "Martes";
        } else if (i == Calendar.WEDNESDAY) {
            return "Miércoles";
        } else if (i == Calendar.THURSDAY) {
            return "Jueves";
        } else if (i == Calendar.FRIDAY) {
            return "Viernes";
        } else if (i == Calendar.SATURDAY) {
            return "Sábado";
        } else {//if (i == Calendar.SUNDAY) 
            return "Domingo";
        }
    }

    /**
     * it is responsible for performing the validation of an identification
     * number of victims in percentage 1-100
     *
     * @param str
     * @return
     */
    private String isPercentage(String str) {
        /*
         * validacion de si un numero_identificacion_victima es porcentaje 1-100
         */
        if (str.trim().length() == 0 || str.compareTo("0") == 0 || str.compareTo("NULL") == 0) {
            return "";
        }
        try {
            int i = Integer.parseInt(str);
            if (i == 0) {
                return "";
            }
            if (i < 1 || i > 100) {
                return null;
            }
            return String.valueOf(i);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    /**
     * allows perform validation of whether an identification number of a victim
     * is greater or equal to zero
     *
     * @param str
     * @return
     */
    private String isLevel(String str) {
        /*
         * validacion de si un numero_identificacion_victima es >= 0
         */
        if (str.trim().length() == 0 || str.compareTo("NULL") == 0) {
            return "";
        }
        try {
            int i = Integer.parseInt(str);
            if (i >= 0) {
                return String.valueOf(i);
            }
            return null;
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    /**
     * allows to validate whether an identification number victim 1-31
     *
     * @param str
     * @return
     */
    private String isDay(String str) {
        /*
         * validacion de si un numero_identificacion_victima de 1 y 31
         * null=invalido ""=aceptado pero vacio "valor"=aceptado y me dice el
         * valor
         */
        if (str.trim().length() == 0) {
            return "";
        }
        try {
            int i = Integer.parseInt(str);
            if (i > 0 && i < 32) {
                return String.valueOf(i);
            }
            return null;
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    /**
     * validates that the month this between 1-12
     *
     * @param str
     * @return
     */
    private String isMonth(String str) {
        /*
         * validacion de si un numero_identificacion_victima de 1 y 12
         * null=invalido ""=aceptado pero vacio "valor"=aceptado y me dice el
         * valor
         */
        if (str.trim().length() == 0) {
            return "";
        }
        try {
            int i = Integer.parseInt(str);
            if (i > 0 && i < 13) {
                return String.valueOf(i);
            }
            return null;
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    /**
     * validate the range of the year this between 1-12
     *
     * @param str
     * @return
     */
    private String isYear(String str) {
        /*
         * validacion de si un numero_identificacion_victima de 1 y 12
         * null=invalido ""=aceptado pero vacio "valor"=aceptado y me dice el
         * valor
         */
        if (str.trim().length() == 0) {
            return "";
        }
        if (str.trim().length() != 2 && str.trim().length() != 4) {
            return null;
        }
        try {
            int i = Integer.parseInt(str);
            return String.valueOf(i);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    /**
     * validates if is a number between 0-59
     *
     * @param str
     * @return
     */
    private String isMinute(String str) {
        /*
         * validacion de si un numero_identificacion_victima de 1 y 12
         * null=invalido ""=aceptado pero vacio "valor"=aceptado y me dice el
         * valor
         */
        if (str.trim().length() == 0) {
            return "";
        }
        try {
            int i = Integer.parseInt(str);
            if (i > -1 && i < 60) {
                return String.valueOf(i);
            }
            return null;
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    /**
     * validates if is a number between 1 - 12
     *
     * @param str
     * @return
     */
    private String isHour(String str) {
        /*
         * validacion de si un numero_identificacion_victima de 1 y 12
         * null=invalido ""=aceptado pero vacio "valor"=aceptado y me dice el
         * valor
         */
        if (str.trim().length() == 0) {
            return "";
        }
        try {
            int i = Integer.parseInt(str);
            if (i > 0 && i < 25) {
                return String.valueOf(i);
            }
            return null;
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    /**
     * validates if a string is integer
     *
     * @param str
     * @return
     */
    private String isNumeric(String str) {
        /*
         * validacion de si un string es entero null=invalido ""=aceptado pero
         * vacio "valor"=aceptado y me dice el valor
         */
        if (str.trim().length() == 0) {
            return "";
        }
        try {
            str = str.replaceAll(",", "");
            str = str.replaceAll("\\.", "");
            Integer.parseInt(str);
            return str;
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    /**
     * determine if a date is found from the year 2002 to the current year.
     *
     * @param f: year format
     * @param format: variable that holds the format to convert
     * @return
     */
    private boolean validYear(String f, String format) {
        /*
         *  determinar si una fecha se encuentra desde el año 2002 hasta el año actual
         */

        if (f.trim().length() == 0) {
            return false;
        }
        try {
            //DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
            DateTimeFormatter fmt2 = DateTimeFormat.forPattern(format);
            DateTime the_date = DateTime.parse(f, fmt2);//trata de convertir al formato "format"(me llega por parametro)
            if (the_date.getYear() >= 2002 && the_date.getYear() < new Date().getYear() + 1901) {
                return true;
            } else {
                return false;
            }
        } catch (Throwable ex) {
            return false;//invalida
        }
    }

    /**
     * determine if a birth date does not exceed the system date.
     *
     * @param f
     * @param format
     * @return
     */
    private boolean validDateOfBirth(String f, String format) {
        /*
         *  determinar si una fecha de naciemiento no supera a la fecha de sistema
         */

        if (f.trim().length() == 0) {
            return false;
        }
        try {
            //DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
            DateTimeFormatter fmt2 = DateTimeFormat.forPattern(format);
            DateTime the_date = DateTime.parse(f, fmt2);//trata de convertir al formato "format"(me llega por parametro)
            if (the_date.getYear() < new Date().getYear() + 1901) {
                return true;
            } else {
                return false;
            }
        } catch (Throwable ex) {
            return false;//invalida
        }
    }

    /**
     * validates the format of a date, given a text string and the supplied
     * format.
     *
     * @param f
     * @param format
     * @return
     */
    private String isDate(String f, String format) {
        /*
         *  null=invalido ""=aceptado pero vacio "valor"=aceptado (valor para db)
         */
        if (f.trim().length() == 0) {
            return "";
        }
        try {
            DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
            DateTimeFormatter fmt2 = DateTimeFormat.forPattern(format);
            DateTime the_date = DateTime.parse(f, fmt2);//trata de convertir al formato "format"(me llega por parametro)
            return fmt.print(the_date);//lo imprime en el formato "yyyy-MM-dd"
        } catch (Throwable ex) {
            return null;//invalida
        }
    }

    /**
     * valid if the string contains a military hour format
     *
     * @param strIn
     * @return
     */
    private String isMilitary(String strIn) {
        /*
         * validacion de si un string es un hora_evento militar null=invalido
         * ""=aceptado pero vacio "valor"=aceptado y me dice el valor en formato militar
         */
        String str = strIn;

        //----------------------------------------------
        //determinar si hay caracteres
        if (str.trim().length() == 0) {
            return "";
            //return "no se acepta cadenas vacias";
        }

        //----------------------------------------------
        //quitar " AM A.M.
        boolean incrementPM = false;
        str = str.toUpperCase();
        if (str.indexOf("PM") != -1) {
            incrementPM = true;

        }
        str = str.replace(" ", "");
        str = str.replace("AM", "").replace("A.M.", "").replace("\"", "");
        str = str.replace("PM", "").replace("P.M.", "");

        //determinar si es un timestamp
        if (str.trim().length() == 12 || str.trim().length() == 8 || str.trim().length() == 7) {
            String[] splitMilitary = str.split(":");
            if (splitMilitary.length == 3) {
                try {
                    int h = Integer.parseInt(splitMilitary[0]);
                    int m = Integer.parseInt(splitMilitary[1]);
                    if (incrementPM) {
                        h = h + 12;
                        if (h == 24) {
                            h = 0;
                        }
                    }
                    splitMilitary[0] = String.valueOf(h);


                    if (splitMilitary[0].length() == 1) {
                        splitMilitary[0] = "0" + splitMilitary[0];
                    }
                    if (splitMilitary[1].length() == 1) {
                        splitMilitary[1] = "0" + splitMilitary[1];
                    }
                    if (h > 24 || h < 0) {
                        return null;
                        //return "La hora_evento debe estar entre 0 y 23";
                    }
                    if (m > 59 || m < 0) {
                        return null;
                        //return "los minuto_evento deben estar entre 0 y 59";
                    }
                    if (h == 24 && m != 0) {
                        return null;
                        //return "Si la hora_evento es 24 los minuto_evento deben ser cero";
                    }
                    return splitMilitary[0] + splitMilitary[1];
                } catch (Exception ex) {
                }
            }
        }


        //----------------------------------------------
        //determinar si tiene como separador   ; + . , :
        String[] splitMilitary = null;
        if (str.split(":").length == 2) {
            splitMilitary = str.split(":");
        } else if (str.split(",").length == 2) {
            splitMilitary = str.split(",");
        } else if (str.split(";").length == 2) {
            splitMilitary = str.split(";");
        } else if (str.split("\\+").length == 2) {
            splitMilitary = str.split("\\+");
        } else if (str.split("\\.").length == 2) {
            splitMilitary = str.split("\\.");
        }
        if (splitMilitary != null) {
            try {
                int h = Integer.parseInt(splitMilitary[0]);
                int m = Integer.parseInt(splitMilitary[1]);
                if (incrementPM) {
                    h = h + 12;
                    if (h == 24) {
                        h = 0;
                    }
                }
                splitMilitary[0] = String.valueOf(h);


                if (splitMilitary[0].length() == 1) {
                    splitMilitary[0] = "0" + splitMilitary[0];
                }
                if (splitMilitary[1].length() == 1) {
                    splitMilitary[1] = "0" + splitMilitary[1];
                }
                if (h == 24) {
                    if (m == 0) {
                        return splitMilitary[0] + splitMilitary[1];
                    } else {
                        return null;
                        //return "Si la hora_evento es 24 los minuto_evento solo pueden ser 0";
                    }
                }
                if (h > 24 || h < 0) {
                    return null;
                    //return "La hora_evento debe estar entre 0 y 24";
                }
                if (m > 59 || m < 0) {
                    return null;
                    //return "los minuto_evento deben estar entre 0 y 59";
                }
                return splitMilitary[0] + splitMilitary[1];
            } catch (Exception ex) {
            }
        }

        //----------------------------------------------
        //determinar si tiene caracteres diferentes a    0123456789
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) != '0' && str.charAt(i) != '1' && str.charAt(i) != '2'
                    && str.charAt(i) != '3' && str.charAt(i) != '4' && str.charAt(i) != '5'
                    && str.charAt(i) != '6' && str.charAt(i) != '7' && str.charAt(i) != '8'
                    && str.charAt(i) != '9') {
                return null;
                //return "Valor no aceptado como hora_evento militar";
            }
        }

        //----------------------------------------------
        //verificar si tiene menos de 4 cifras 
        if (str.trim().length() < 5) {
            //lo completo con ceros
            if (str.trim().length() == 3) {
                str = "0" + str;
            } else if (str.trim().length() == 2) {
                str = "00" + str;
            } else if (str.trim().length() == 1) {
                str = "000" + str;
            }
            try {
                int h = Integer.parseInt(str.substring(0, 2));
                int m = Integer.parseInt(str.substring(2, 4));

                if (incrementPM) {
                    h = h + 12;
                    if (h == 24) {
                        h = 0;
                        str = "00" + str.substring(2, 4);
                    }
                }

                if (h > 24 || h < 0) {
                    return null;
                    //return "La hora_evento debe estar entre 0 y 23";
                }
                if (m > 59 || m < 0) {
                    return null;
                    //return "los minuto_evento deben estar entre 0 y 59";
                }
                if (h == 24 && m != 0) {
                    return null;
                    //return "Si la hora_evento es 24 los minuto_evento deben ser cero";
                }
                return str;
            } catch (Exception ex) {
            }
        } else {
            return null;
            //return "Una hora_evento militar debe tener menos de 5 digitos";
        }
        return null;
        //return "Valor no aceptado como hora_evento militar";
    }

    /**
     * validates if a string is an integer or a defined age in months and years.
     *
     * @param str
     * @return
     */
    private String isAge(String str) {
        /*
         * validacion de si un string es numero_identificacion_victima entero o
         * edad_victima definida en meses y años null = invalido "" = aceptado
         * pero vacio "valor" = aceptado y me dice el valor
         */
        //String[] splitAge;
        if (str.trim().length() == 0) {
            //splitAge = new String[1];
            //splitAge[0] = "";
            return "";
        }
        try {//intento convertirlo en entero
            int a = Integer.parseInt(str);
            if (a > 150 || a < 0) {
                return null;
            }
            if (a == 0) {
                return "1";
            }
            return String.valueOf(a);
            //splitAge = new String[1];
            //splitAge[0] = str;
            //return splitAge;
        } catch (Exception ex) {
        }
        try {//determinar si esta definida en años meses
            String[] splitAge = str.split(" ");
            if (splitAge.length == 4) {
                int m = Integer.parseInt(splitAge[0]);
                int y = Integer.parseInt(splitAge[2]);
                if (y > 150) {
                    return null;
                }
                if (y == 0) {
                    return "1";
                }

                return String.valueOf(y);
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * validate whether a value is within a category, or discarded, return the
     * id respective of the table categorical
     *
     * @param valueFound
     * @param relationVar
     * @return
     */
    private String isCategorical(String valueFound, RelationVariables relationVar) {
        /*
         * validacion de si un valor esta dentro de una categoria, o es
         * descartado, retorna el id respectivo a la tabla categorica
         */
        if (valueFound.trim().length() == 0) {
            return "";
        }

        if (relationVar.getFieldType().compareTo("municipalities") == 0 || relationVar.getFieldType().compareTo("countries") == 0) {
            relationVar.setComparisonForCode(false);//siempre se busca por nombre         
        }

        //se valida con respecto a las relaciones de valores
        if (relationVar.getComparisonForCode() == true) {
            for (int i = 0; i < relationVar.getRelationValuesList().size(); i++) {
                if (relationVar.getRelationValuesList().get(i).getNameFound().compareTo(valueFound) == 0) {
                    return relationVar.getRelationValuesList().get(i).getNameExpected();
                }
            }
        } else {
            for (int i = 0; i < relationVar.getRelationValuesList().size(); i++) {
                if (relationVar.getRelationValuesList().get(i).getNameFound().compareTo(valueFound) == 0) {
                    return connectionJdbcMB.findCodeByCategoricalName(remove_v(relationVar.getFieldType()), relationVar.getRelationValuesList().get(i).getNameExpected());
                }
            }
        }
        //verificar si es descartado
        for (int i = 0; i < relationVar.getRelationsDiscardedValuesList().size(); i++) {
            if (valueFound.compareTo(relationVar.getRelationsDiscardedValuesList().get(i).getDiscardedValueName()) == 0) {
                return "";
            }
        }
        //se valida con respecto a los valores esperados
        if (relationVar.getComparisonForCode() == true) {
            return valueFound;
        } else {
            return connectionJdbcMB.findCodeByCategoricalName(remove_v(relationVar.getFieldType()), valueFound);
        }
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES GET Y SET DE LAS VARIABLES ---------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public ErrorsControlMB getErrorsControlMB() {
        return errorsControlMB;
    }

    public void setErrorsControlMB(ErrorsControlMB errorsControlMB) {
        this.errorsControlMB = errorsControlMB;
    }

    public LoginMB getLoginMB() {
        return loginMB;
    }

    public void setLoginMB(LoginMB loginMB) {
        this.loginMB = loginMB;
    }

    public String getNameForm() {
        return nameForm;
    }

    public void setNameForm(String nameForm) {
        this.nameForm = nameForm;
    }

    public boolean isBtnRegisterDataDisabled() {
        return btnRegisterDataDisabled;
    }

    public void setBtnRegisterDataDisabled(boolean btnRegisterDataDisabled) {
        this.btnRegisterDataDisabled = btnRegisterDataDisabled;
    }

    public Integer getProgressValidate() {
        return progressValidate;
    }

    public void setProgressValidate(Integer progressValidate) {
        this.progressValidate = progressValidate;
    }

    public ProjectsMB getProjectsMB() {
        return projectsMB;
    }

    public void setProjectsMB(ProjectsMB projectsMB) {
        this.projectsMB = projectsMB;
    }

    public int getCurrentSource() {
        return currentSource;
    }

    public void setCurrentSource(int currentSource) {
        this.currentSource = currentSource;
    }
}
