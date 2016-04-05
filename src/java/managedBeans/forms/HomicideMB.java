/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.forms;

import beans.connection.ConnectionJdbcMB;
import beans.util.RowDataTable;
import java.io.Serializable;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import managedBeans.login.ApplicationControlMB;
import managedBeans.login.LoginMB;
import model.dao.*;
import model.pojo.*;

/**
 *
 * @author SANTOS
 */
/**
 * HomicideMB Is responsible to request the user data about the occurrence of
 * events of homicide and details of the victim to be processed and recorded in
 * the database.
 *
 */
@ManagedBean(name = "homicideMB")
@SessionScoped
public class HomicideMB implements Serializable {
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    // DECLARACION DE VARIABLES --------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------

    @EJB
    TagsFacade tagsFacade;
    private SelectItem[] tags;
    private int currentTag;
    //-------------------
    @EJB
    AreasFacade areasFacade;
    private SelectItem[] areas;
    private Short currentArea = 0;
    //-------------------
    @EJB
    QuadrantsFacade quadrantsFacade;
    private SelectItem[] quadrantsEvent;
    private int currentQuadrantEvent = 0;
    //-------------------- 
    @EJB
    WeaponTypesFacade weaponTypesFacade;
    private Short currentWeaponType;
    private SelectItem[] weaponTypes;
    //--------------------
    @EJB
    CountriesFacade countriesFacade;
    private Short currentSourceCountry = 0;
    private SelectItem[] sourceCountries;
    //-------------------- 
    @EJB
    DepartamentsFacade departamentsFacade;
    private Short currentSourceDepartament = 0;
    private SelectItem[] sourceDepartaments;
    private SelectItem[] homeDepartaments;
    private Short currentDepartamentHome = 52;//nariño    
    private boolean currentDepartamentHomeDisabled = false;
    //--------------------    
    @EJB
    MunicipalitiesFacade municipalitiesFacade;
    private boolean currentMunicipalitieDisabled = false;
    private Short currentMunicipalitie = 1;//pasto
    private SelectItem[] municipalities;
    private Short currentSourceMunicipalitie = 0;
    private SelectItem[] sourceMunicipalities;
    //--------------------
    @EJB
    PlacesFacade placesFacade;
    private Short currentPlace;
    private SelectItem[] places;
    //--------------------
    @EJB
    GendersFacade gendersFacade;
    private Short currentGender;
    private SelectItem[] genders;
    //--------------------    
    @EJB
    JobsFacade jobsFacade;
    private String currentJob;
    //--------------------
    @EJB
    NeighborhoodsFacade neighborhoodsFacade;
    private String currentNeighborhoodHome = "";
    private String currentNeighborhoodHomeCode = "";
    private String currentNeighborhoodEvent = "";
    private String currentNeighborhoodEventCode = "";
    boolean neighborhoodHomeNameDisabled = true;
    //------------------
    @EJB
    IdTypesFacade idTypesFacade;
    private SelectItem[] identificationsTypes;
    private Short currentIdentificationType;
    //------------------
    @EJB
    AgeTypesFacade ageTypesFacade;
    private SelectItem[] measuresOfAge;
    private Short currentMeasureOfAge;
    private String currentAge = "";
    private boolean valueAgeDisabled = true;
    //--------------------
    @EJB
    GenNnFacade genNnFacade;
    //--------------------
    private boolean strangerDisabled = true;
    private boolean currentDayEventDisabled = false;
    private boolean currentMonthEventDisabled = false;
    private boolean currentYearEventDisabled = false;
    private boolean currentHourEventDisabled = false;
    private boolean currentMinuteEventDisabled = false;
    private boolean currentAmPmEventDisabled = false;
    private boolean stranger = false;
    //------------------
    @EJB
    MurderContextsFacade murderContextsFacade;
    private Short currentMurderContext;
    private SelectItem[] murderContexts;
    //------------------
    @EJB
    VictimsFacade victimsFacade;
    @EJB
    FatalInjuryMurderFacade fatalInjuryMurderFacade;
    @EJB
    FatalInjuriesFacade fatalInjuriesFacade;
    @EJB
    InjuriesFacade injuriesFacade;
    @EJB
    AlcoholLevelsFacade alcoholLevelsFacade;
    //------------------
    private boolean currentAlcoholLevelDisabled = false;
    private boolean isNoDataAlcoholLevel = false;
    private boolean isPendentAlcoholLevel = false;
    private boolean isUnknownAlcoholLevel = false;
    private boolean isNegativeAlcoholLevel = false;
    private boolean isNoDataAlcoholLevelDisabled = false;
    private boolean isPendentAlcoholLevelDisabled = false;
    private boolean isUnknownAlcoholLevelDisabled = false;
    private boolean isNegativeAlcoholLevelDisabled = false;
    private boolean identificationNumberDisabled = true;
    private boolean save = true;//variable que me dice si el registro esta guadado o no    
    private boolean loading = false;//me dice si se esta cargando (para no tener en cuenta los eventos)
    private String currentDayEvent = "";
    private String currentMonthEvent = "";
    private String currentYearEvent = "";
    private String currentDateEvent = "";
    private String currentWeekdayEvent = "";
    private String currentNarrative = "";
    private String currentAlcoholLevel = "";
    private String currentMilitaryHourEvent = "";
    private String currentName = "";
    private String currentIdentificationNumber = "";
    private String currentCode = "";
    private String currentDirectionEvent = "";
    private String currentNumberVictims = "1";
    private String currentPosition = "";
    private int totalRegisters = 0;//cantidad total de registros en homicidios
    private int currentFatalInjuriId = -1;//registro actual 
    private String currentHourEvent;
    private String currentMinuteEvent;
    private String currentAmPmEvent = "AM";
    private FatalInjuryMurder currentFatalInjuryMurder;
    private FatalInjuryMurder auxFatalInjuryMurder;
    private SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
    private Date fechaI;
    private String openDialogFirst = "";
    private String openDialogNext = "";
    private String openDialogLast = "";
    private String openDialogPrevious = "";
    private String openDialogNew = "";
    private String openDialogDelete = "";
    private ArrayList<String> validationsErrors;
    private Calendar c = Calendar.getInstance();
    private String stylePosition = "color: #1471B1;";
    private String currentIdForm = "";
    private Users currentUser;
    ConnectionJdbcMB connectionJdbcMB;
    private LoginMB loginMB;
    private ApplicationControlMB applicationControlMB;

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    // FUNCIONES VARIAS ----------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * This constructor is responsible for verifying the start of session and
     * make the connection to database
     *
     */
    public HomicideMB() {
        loginMB = (LoginMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{loginMB}", LoginMB.class);
        applicationControlMB = (ApplicationControlMB) FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get("applicationControlMB");
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
    }

    /**
     * This method is responsible to load the information corresponding to a
     * victim within the form.
     *
     * @param tagsList
     * @param currentFatalInjuryM
     */
    public void loadValues(List<Tags> tagsList, FatalInjuryMurder currentFatalInjuryM) {
        for (int i = 0; i < tagsList.size(); i++) {
            try {
                reset();
                clearForm();
                currentTag = tagsList.get(i).getTagId();
                this.currentFatalInjuryMurder = currentFatalInjuryM;
                currentFatalInjuriId = currentFatalInjuryM.getFatalInjuryId();
                determinePosition();
                loadValues();
            } catch (Exception e) {
                reset();
                noSaveAndGoNew();
            }
        }
    }

    /**
     * This method is responsible to load the information corresponding to a
     * victim within the form.
     */
    public void loadValues() {

        loading = true;
        openDialogFirst = "";
        openDialogNext = "";
        openDialogLast = "";
        openDialogPrevious = "";
        openDialogNew = "";
        //------------------------------------------------------------
        //SE CARGAN VALORES PARA LA NUEVA VICTIMA
        //------------------------------------------------------------
        //******stranger
        try {
            stranger = currentFatalInjuryMurder.getFatalInjuries().getVictimId().getStranger();
        } catch (Exception e) {
            stranger = false;
        }
        changeStranger();
        //******type_id
        try {
            currentIdentificationType = currentFatalInjuryMurder.getFatalInjuries().getVictimId().getTypeId().getTypeId();
        } catch (Exception e) {
            currentIdentificationType = 0;
        }
        changeIdentificationType();
        //******victim_nid        
        try {
            currentIdentificationNumber = currentFatalInjuryMurder.getFatalInjuries().getVictimId().getVictimNid();
            if (currentIdentificationType == 6 || currentIdentificationType == 7 || currentIdentificationType == 0) {
                identificationNumberDisabled = true;
                currentIdentificationNumber = "";
            } else {
                identificationNumberDisabled = false;
            }
        } catch (Exception e) {
            identificationNumberDisabled = true;
            currentIdentificationNumber = "";
        }
        //******victim_name
        currentName = currentFatalInjuryMurder.getFatalInjuries().getVictimId().getVictimName();
        if (currentName == null) {
            currentName = "";
        }
        //******age_type_id
        try {
            currentMeasureOfAge = currentFatalInjuryMurder.getFatalInjuries().getVictimId().getAgeTypeId();
            if (currentMeasureOfAge == 4) {
                valueAgeDisabled = true;
            } else {
                valueAgeDisabled = false;
            }
        } catch (Exception e) {
            currentMeasureOfAge = 0;
            valueAgeDisabled = true;
        }
        //******victim_age
        try {
            currentAge = currentFatalInjuryMurder.getFatalInjuries().getVictimId().getVictimAge().toString();
        } catch (Exception e) {
            currentAge = "";
        }
        //******gender_id
        try {
            currentGender = currentFatalInjuryMurder.getFatalInjuries().getVictimId().getGenderId().getGenderId();
        } catch (Exception e) {
            currentGender = 0;
        }
        //******job_id
        try {
            currentJob = currentFatalInjuryMurder.getFatalInjuries().getVictimId().getJobId().getJobName();
        } catch (Exception e) {
            currentJob = "";
        }
        //******vulnerable_group_id
        //******ethnic_group_id
        //******victim_telephone
        //******victim_address

        //******victim_neighborhood_id
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getVictimId().getVictimNeighborhoodId().getNeighborhoodId() != null) {
                currentNeighborhoodHomeCode = String.valueOf(currentFatalInjuryMurder.getFatalInjuries().getVictimId().getVictimNeighborhoodId().getNeighborhoodId());
                currentNeighborhoodHome = neighborhoodsFacade.find(currentFatalInjuryMurder.getFatalInjuries().getVictimId().getVictimNeighborhoodId().getNeighborhoodId()).getNeighborhoodName();
            }
        } catch (Exception e) {
            currentNeighborhoodHomeCode = "";
            currentNeighborhoodHome = "";
        }
        //******victim_date_of_birth
        //******eps_id
        //******victim_class
        //******victim_id
        //******residence_municipality residence_department
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getVictimId().getResidenceDepartment() != null) {
                currentDepartamentHome = currentFatalInjuryMurder.getFatalInjuries().getVictimId().getResidenceDepartment();
                changeDepartamentHome();
                if (currentFatalInjuryMurder.getFatalInjuries().getVictimId().getResidenceMunicipality() != null) {
                    currentMunicipalitie = currentFatalInjuryMurder.getFatalInjuries().getVictimId().getResidenceMunicipality();
                    if (currentDepartamentHome == 52 && currentMunicipalitie == 1) {
                        neighborhoodHomeNameDisabled = false;
                    }
                } else {
                    currentMunicipalitie = 0;
                }
            } else {
                currentDepartamentHome = 0;
                currentMunicipalitie = 0;
            }
        } catch (Exception e) {
            currentDepartamentHome = 0;
            currentMunicipalitie = 0;
        }
        //------------------------------------------------------------
        //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA FATAL
        //------------------------------------------------------------
        //******injury_id
        //******injury_date
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentFatalInjuryMurder.getFatalInjuries().getInjuryDate());
            currentDayEvent = String.valueOf(cal.get(Calendar.DATE));
            currentMonthEvent = String.valueOf(cal.get(Calendar.MONTH) + 1);
            currentYearEvent = String.valueOf(cal.get(Calendar.YEAR));
            calculateDate1();
        } catch (Exception e) {
            currentDateEvent = "";
        }
        //******injury_time
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getInjuryTime().getHours() == 0) {
                currentHourEvent = "12";
                currentAmPmEvent = "AM";
            } else {
                currentHourEvent = String.valueOf(currentFatalInjuryMurder.getFatalInjuries().getInjuryTime().getHours());
                if (Integer.parseInt(currentHourEvent) != 12) {
                    if (Integer.parseInt(currentHourEvent) > 12) {
                        currentHourEvent = String.valueOf(Integer.parseInt(currentHourEvent) - 12);
                        currentAmPmEvent = "PM";
                    } else {
                        currentAmPmEvent = "AM";
                    }
                } else {
                    currentHourEvent = "12";
                    currentAmPmEvent = "PM";
                }
            }
            currentMinuteEvent = String.valueOf(currentFatalInjuryMurder.getFatalInjuries().getInjuryTime().getMinutes());

            calculateTime1();
        } catch (Exception e) {
            currentHourEvent = "";
            currentMinuteEvent = "";
            currentAmPmEvent = "SIN DATO";
            changeAmPmEvent();
        }
        //******injury_address
        currentDirectionEvent = currentFatalInjuryMurder.getFatalInjuries().getInjuryAddress();
        if (currentDirectionEvent == null) {
            currentDirectionEvent = "";
        }
        //******injury_neighborhood_id
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getInjuryNeighborhoodId() != null) {
                currentNeighborhoodEventCode = String.valueOf(currentFatalInjuryMurder.getFatalInjuries().getInjuryNeighborhoodId());
                currentNeighborhoodEvent = neighborhoodsFacade.find(currentFatalInjuryMurder.getFatalInjuries().getInjuryNeighborhoodId()).getNeighborhoodName();
                //cargo cuadrantes
                try {
                    ResultSet rs = connectionJdbcMB.consult(""
                            + " SELECT COUNT(*) FROM quadrants WHERE quadrant_id IN "
                            + " (SELECT quadrant_id FROM neighborhood_quadrant "
                            + " WHERE neighborhood_id = " + currentNeighborhoodEventCode + ") ");
                    if (rs.next()) {
                        quadrantsEvent = new SelectItem[rs.getInt(1)];
                        rs = connectionJdbcMB.consult(""
                                + " SELECT * FROM quadrants WHERE quadrant_id IN "
                                + " (SELECT quadrant_id FROM neighborhood_quadrant "
                                + " WHERE neighborhood_id = " + currentNeighborhoodEventCode + ") ");
                        currentQuadrantEvent = -1;
                        int pos = 0;
                        while (rs.next()) {
                            //if (currentQuadrantEvent == -1) {
                            if (currentQuadrantEvent == -1 && currentFatalInjuryMurder.getFatalInjuries().getQuadrantId() != null) {
                                currentQuadrantEvent = currentFatalInjuryMurder.getFatalInjuries().getQuadrantId().getQuadrantId();
                            }
                            quadrantsEvent[pos] = new SelectItem(rs.getInt("quadrant_id"), rs.getString("quadrant_name"));
                            pos++;
                        }
                    }
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            currentNeighborhoodEventCode = "";
            currentNeighborhoodEvent = "";
        }
        //******injury_place_id
        try {
            currentPlace = currentFatalInjuryMurder.getFatalInjuries().getInjuryPlaceId().getPlaceId();
        } catch (Exception e) {
            currentPlace = 0;
        }
        //******victim_number
        if (currentFatalInjuryMurder.getFatalInjuries().getVictimNumber() != null) {
            currentNumberVictims = String.valueOf(currentFatalInjuryMurder.getFatalInjuries().getVictimNumber());
        } else {
            currentNumberVictims = "";
        }
        //******injury_description
        currentNarrative = currentFatalInjuryMurder.getFatalInjuries().getInjuryDescription();
        if (currentNarrative == null) {
            currentNarrative = "";
        }
        //******user_id	
        //******input_timestamp	
        //******injury_day_of_week
        currentWeekdayEvent = currentFatalInjuryMurder.getFatalInjuries().getInjuryDayOfWeek();
        if (currentWeekdayEvent == null) {
            currentWeekdayEvent = "";
        }
        //******victim_id
        //******fatal_injury_id
        //******alcohol_level_victim_id, alcohol_level_victim
        if (currentFatalInjuryMurder.getFatalInjuries().getAlcoholLevelVictim() != null) {
            isNoDataAlcoholLevelDisabled = false;
            isUnknownAlcoholLevelDisabled = false;
            isPendentAlcoholLevelDisabled = false;
            isNegativeAlcoholLevelDisabled = false;
            currentAlcoholLevelDisabled = false;
            currentAlcoholLevel = String.valueOf(currentFatalInjuryMurder.getFatalInjuries().getAlcoholLevelVictim());
            isNoDataAlcoholLevel = false;
            isUnknownAlcoholLevel = false;
            isPendentAlcoholLevel = false;
            isNegativeAlcoholLevel = false;
        } else {
            try {
                Short level = currentFatalInjuryMurder.getFatalInjuries().getAlcoholLevelVictimId().getAlcoholLevelId();
                if (level == 2) {//isNoDataAlcoholLevel
                    isNoDataAlcoholLevelDisabled = false;
                    isUnknownAlcoholLevelDisabled = true;
                    isPendentAlcoholLevelDisabled = true;
                    isNegativeAlcoholLevelDisabled = true;
                    currentAlcoholLevelDisabled = true;
                    currentAlcoholLevel = "";
                    isNoDataAlcoholLevel = true;
                    isUnknownAlcoholLevel = false;
                    isPendentAlcoholLevel = false;
                    isNegativeAlcoholLevel = false;
                }
                if (level == 3) {//isUnknownAlcoholLevel
                    isNoDataAlcoholLevelDisabled = true;
                    isUnknownAlcoholLevelDisabled = false;
                    isPendentAlcoholLevelDisabled = true;
                    isNegativeAlcoholLevelDisabled = true;
                    currentAlcoholLevelDisabled = true;
                    currentAlcoholLevel = "";
                    isNoDataAlcoholLevel = false;
                    isUnknownAlcoholLevel = true;
                    isPendentAlcoholLevel = false;
                    isNegativeAlcoholLevel = false;
                }
                if (level == 4) {//isPendentAlcoholLevel                
                    isNoDataAlcoholLevelDisabled = true;
                    isUnknownAlcoholLevelDisabled = true;
                    isPendentAlcoholLevelDisabled = false;
                    isNegativeAlcoholLevelDisabled = true;
                    currentAlcoholLevelDisabled = true;
                    currentAlcoholLevel = "";
                    isNoDataAlcoholLevel = false;
                    isUnknownAlcoholLevel = false;
                    isPendentAlcoholLevel = true;
                    isNegativeAlcoholLevel = false;
                }
                if (level == 5) {//isNegativeAlcoholLevel

                    isNoDataAlcoholLevelDisabled = true;
                    isUnknownAlcoholLevelDisabled = true;
                    isPendentAlcoholLevelDisabled = true;
                    isNegativeAlcoholLevelDisabled = false;
                    currentAlcoholLevelDisabled = true;
                    currentAlcoholLevel = "";
                    isNoDataAlcoholLevel = false;
                    isUnknownAlcoholLevel = false;
                    isPendentAlcoholLevel = false;
                    isNegativeAlcoholLevel = true;
                }
            } catch (Exception e) {
                isNoDataAlcoholLevelDisabled = false;
                isUnknownAlcoholLevelDisabled = false;
                isPendentAlcoholLevelDisabled = false;
                isNegativeAlcoholLevelDisabled = false;
                currentAlcoholLevelDisabled = false;
                currentAlcoholLevel = "";
                isNoDataAlcoholLevel = false;
                isUnknownAlcoholLevel = false;
                isPendentAlcoholLevel = false;
                isNegativeAlcoholLevel = false;
            }
        }
        //******code
        currentCode = currentFatalInjuryMurder.getFatalInjuries().getCode();
        if (currentCode == null) {
            currentCode = "";
        }
        //******area_id
        try {
            currentArea = currentFatalInjuryMurder.getFatalInjuries().getAreaId().getAreaId();
        } catch (Exception e) {
            currentArea = 0;
        }
        //******victim_place_of_origin
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getVictimPlaceOfOrigin() != null) {
                String source = currentFatalInjuryMurder.getFatalInjuries().getVictimPlaceOfOrigin();
                String[] sourceSplit = source.split("-");
                //determino pais
                currentSourceCountry = Short.parseShort(sourceSplit[0]);
                if (currentSourceCountry == 52) {//colombia
                    findSourceDepartaments();
                    currentSourceDepartament = Short.parseShort(sourceSplit[1]);
                    findSourceMunicipalities();
                    currentSourceMunicipalitie = Short.parseShort(sourceSplit[2]);
                } else {
                    sourceMunicipalities = new SelectItem[1];
                    sourceMunicipalities[0] = new SelectItem(0, "");
                    sourceDepartaments = new SelectItem[1];
                    sourceDepartaments[0] = new SelectItem(0, "");
                    currentSourceDepartament = 0;
                    currentSourceMunicipalitie = 0;
                }
            }
        } catch (Exception e) {

            sourceMunicipalities = new SelectItem[1];
            sourceMunicipalities[0] = new SelectItem(0, "");
            sourceMunicipalities = new SelectItem[1];
            sourceMunicipalities[0] = new SelectItem(0, "");
            currentSourceCountry = 0;
            currentSourceDepartament = 0;
            currentSourceMunicipalitie = 0;
        }
        //------------------------------------------------------------
        //SE CARGA DATOS PARA LA NUEVA LESION FATAL POR HOMICIDIOS
        //------------------------------------------------------------

        //******murder_context_id
        try {
            currentMurderContext = currentFatalInjuryMurder.getMurderContextId().getMurderContextId();
        } catch (Exception e) {
            currentMurderContext = 0;
        }
        //******weapon_type_id
        try {
            currentWeaponType = currentFatalInjuryMurder.getWeaponTypeId().getWeaponTypeId();
        } catch (Exception e) {
            currentWeaponType = 0;
        }
        //******fatal_injury_id
        save = true;
        stylePosition = "color: #1471B1;";
        loading = false;
    }

    /**
     * validates required fields before register a form. validates user
     * permission, the date of the event and the existence of errors.
     *
     * @return
     */
    private boolean validateFields() {
        validationsErrors = new ArrayList<>();
        //---------VALIDAR EL USUARIO TENGA PERMISMOS SUFIENTES
        if (currentFatalInjuriId != -1) {//SE ESTA ACTUALIZANDO UN REGISTRO
            if (!loginMB.isPermissionAdministrator() && loginMB.getCurrentUser().getUserId() != currentFatalInjuryMurder.getFatalInjuries().getUserId().getUserId()) {
                validationsErrors.add("Este registro solo puede ser modificado por un administrador o por el usuario que creo el registro");
            }
        }
        //---------VALIDAR QUE EXISTA FECHA DE HECHO
        if (currentDateEvent.trim().length() == 0) {
            validationsErrors.add("Es obligatorio ingresar la fecha del evento");
        }
        //---------VALIDAR QUE LA FECHA DEL SISTEMA SEA MAYOR A LA FECHA DEL HECHO 
        if (currentDateEvent.trim().length() != 0) {
            try {
                Calendar currentDate = Calendar.getInstance();
                Calendar eventDate = Calendar.getInstance();
                Date dateEvent = formato.parse(currentDateEvent);
                eventDate.setTime(dateEvent);
                if (currentDate.compareTo(eventDate) < 0) {
                    validationsErrors.add("La fecha del evento: (" + currentDateEvent + ") es mayor que la fecha del sistema");
                }
            } catch (ParseException ex) {
                Logger.getLogger(HomicideMB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //---------MOSTRAR LOS ERRORES SI EXISTEN
        if (validationsErrors.isEmpty()) {
            return true;
        } else {
            for (int i = 0; i < validationsErrors.size(); i++) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error de validación", validationsErrors.get(i));
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            return false;
        }
    }

    /**
     * register all data of a new victim obtained from the form, this registry
     * is made in the database, whether it is a form already registered, can
     * update the information.
     *
     * @return
     */
    private boolean saveRegistry() {

        //realizo validaciones
        if (validateFields()) {
            try {
                //------------------------------------------------------------
                //SE CREA VARIABLE PARA LA NUEVA VICTIMA
                //------------------------------------------------------------
                Victims newVictim = new Victims();
                //******type_id
                if (currentIdentificationType != 0) {
                    newVictim.setTypeId(idTypesFacade.find(currentIdentificationType));
                }
                //******stranger                
                newVictim.setStranger(stranger);
                //******victim_nid	    
                if (currentIdentificationNumber.trim().length() != 0) {
                    newVictim.setVictimNid(currentIdentificationNumber);
                }
//                //******victim_firstname
//                if (currentName.trim().length() != 0) {
//                    newVictim.setVictimFirstname(currentName);
//                }
//                //******victim_lastname
//                if (currentSurname.trim().length() != 0) {
//                    newVictim.setVictimLastname(currentSurname);
//                }
                //******victim_firstname
                if (currentName.trim().length() != 0) {
                    newVictim.setVictimName(currentName);
                }
                //******age_type_id
                if (currentMeasureOfAge != 0) {
                    newVictim.setAgeTypeId(currentMeasureOfAge);
                }
                //******victim_age
                if (currentAge.trim().length() != 0) {
                    newVictim.setVictimAge(Short.parseShort(currentAge));
                }
                //******gender_id
                if (currentGender != 0) {
                    newVictim.setGenderId(gendersFacade.find(currentGender));
                }
                //******job_id
                if (currentJob != null && currentJob.trim().length() != 0) {
                    newVictim.setJobId(jobsFacade.findByName(currentJob));
                }
                //******vulnerable_group_id
                //******ethnic_group_id
                //******victim_telephone
                //******victim_address
                if (currentDirectionEvent.trim().length() != 0) {
                    newVictim.setVictimAddress(currentDirectionEvent);
                }
                //******victim_neighborhood_id
                if (currentNeighborhoodHomeCode.trim().length() != 0) {
                    newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(currentNeighborhoodHomeCode)));
                }
                //******residence_municipality
                newVictim.setResidenceMunicipality(currentMunicipalitie);
                newVictim.setResidenceDepartment(currentDepartamentHome);
                //******victim_date_of_birth
                //******eps_id
                //******victim_class
                //******victim_id
                //newVictim.setVictimId(victimsFacade.findMax() + 1);
                newVictim.setVictimId(applicationControlMB.addVictimsReservedIdentifiers());

                //------------------------------------------------------------
                //SE CREA VARIABLE PARA LA NUEVA LESION DE CAUSA EXTERNA FATAL
                //------------------------------------------------------------
                FatalInjuries newFatalInjurie = new FatalInjuries();
                //******injury_id
                newFatalInjurie.setInjuryId(injuriesFacade.find((short) 10));//es 10 por ser homicidio
                //******injury_date
                if (currentDateEvent.trim().length() != 0) {
                    newFatalInjurie.setInjuryDate(formato.parse(currentDateEvent));
                }
                //******injury_time
                if (currentMilitaryHourEvent.trim().length() != 0) {
                    int hourInt = Integer.parseInt(currentMilitaryHourEvent.substring(0, 2));
                    int minuteInt = Integer.parseInt(currentMilitaryHourEvent.substring(2, 4));
                    Date n = new Date();
                    n.setHours(hourInt);
                    n.setMinutes(minuteInt);
                    n.setSeconds(0);
                    newFatalInjurie.setInjuryTime(n);
                }
                //******injury_address
                if (currentDirectionEvent.trim().length() != 0) {
                    newFatalInjurie.setInjuryAddress(currentDirectionEvent);
                }
                //******injury_neighborhood_id
                if (currentNeighborhoodEventCode.trim().length() != 0) {
                    newFatalInjurie.setInjuryNeighborhoodId(Integer.parseInt(currentNeighborhoodEventCode));
                }
                //******injury_place_id
                if (currentPlace != 0) {
                    newFatalInjurie.setInjuryPlaceId(placesFacade.find(currentPlace));
                }
                //******victim_number
                if (currentNumberVictims.trim().length() != 0) {
                    newFatalInjurie.setVictimNumber(Short.parseShort(currentNumberVictims));
                }
                //******injury_description
                if (currentNarrative.trim().length() != 0) {
                    newFatalInjurie.setInjuryDescription(currentNarrative);
                }
                //******user_id		    
                newFatalInjurie.setUserId(currentUser);//usuario que se encuentre logueado

                //******input_timestamp	
                newFatalInjurie.setInputTimestamp(new Date());//momento en que se capturo el registro
                //******injury_day_of_week
                if (currentWeekdayEvent.trim().length() != 0) {
                    newFatalInjurie.setInjuryDayOfWeek(currentWeekdayEvent);
                }
                //******victim_id
                newFatalInjurie.setVictimId(newVictim);
                //******fatal_injury_id
                //newFatalInjurie.setFatalInjuryId(fatalInjuriesFacade.findMax() + 1);
                newFatalInjurie.setFatalInjuryId(applicationControlMB.addFatalReservedIdentifiers());
                //******alcohol_level_victim_id, alcohol_level_victim
                if (currentAlcoholLevel.trim().length() != 0) {
                    newFatalInjurie.setAlcoholLevelVictim(Short.parseShort(currentAlcoholLevel));
                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 1));
                } else {
                    if (isNoDataAlcoholLevel) {
                        newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 2));
                    }
                    if (isUnknownAlcoholLevel) {
                        newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 3));
                    }
                    if (isPendentAlcoholLevel) {
                        newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 4));
                    }
                    if (isNegativeAlcoholLevel) {
                        newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 5));
                    }
                }
                //******code
                if (currentCode.trim().length() != 0) {
                    newFatalInjurie.setCode(currentCode);
                }
                //******area_id
                if (currentArea != 0) {
                    newFatalInjurie.setAreaId(areasFacade.find(currentArea));
                }
                //****quadrant_id
                if (currentQuadrantEvent != -1) {
                    newFatalInjurie.setQuadrantId(quadrantsFacade.find(currentQuadrantEvent));
                }
                //******victim_place_of_origin
                if (currentSourceCountry != 0) {
                    String source = String.valueOf(currentSourceCountry);
                    source = source + "-" + String.valueOf(currentSourceDepartament);
                    source = source + "-" + String.valueOf(currentSourceMunicipalitie);
                    newFatalInjurie.setVictimPlaceOfOrigin(source);
                }
                //------------------------------------------------------------
                //SE CREA VARIABLE PARA LA NUEVA LESION FATAL POR HOMICIDIOS
                //------------------------------------------------------------
                FatalInjuryMurder newMurder = new FatalInjuryMurder();

                //******murder_context_id	    	    
                if (currentMurderContext != 0) {
                    newMurder.setMurderContextId(murderContextsFacade.find(currentMurderContext));
                }
                //******weapon_type_id 
                if (currentWeaponType != 0) {
                    newMurder.setWeaponTypeId(weaponTypesFacade.find(currentWeaponType));
                }
                //******fatal_injury_id
                newMurder.setFatalInjuryId(newFatalInjurie.getFatalInjuryId());

                //--------------------------------------------------------------
                //--------------AUTOCOMPLETAR LOS FORMULARIOS-------------------
                //EDAD Y TIPO DE EDAD
                if (newVictim.getVictimAge() != null) {//HAY EDAD 
                    if (newVictim.getAgeTypeId() == null) {//no hay tipo de edad
                        newVictim.setAgeTypeId((short) 1);//tiṕo de edad años
                    }
                } else {
                    newVictim.setAgeTypeId((short) 4);//tiṕo de edad sin determinar
                }
                //DETERMINAR EL NUMERO DE IDENTIFICACION
                newVictim.setVictimClass((short) 1);
                if (newVictim.getVictimNid() != null && newVictim.getVictimNid().trim().length() == 0) {
                    newVictim.setVictimNid(null);
                }
                if (newVictim.getVictimNid() == null) {
                    newVictim.setVictimNid(String.valueOf(genNnFacade.findMax() + 1));
                    newVictim.setVictimClass((short) 2);//nn
                    newVictim.setTypeId(null);
                    int newGenNnId = genNnFacade.findMax() + 1;
                    connectionJdbcMB.non_query("UPDATE gen_nn SET cod_nn = " + newGenNnId + " where cod_nn IN (SELECT MAX(cod_nn) from gen_nn)");
                }
                //SI NO SE DETERMINA EL BARRIO SE COLOCA SIN DATO URBANO
                if (newVictim.getVictimNeighborhoodId() == null) {
                    newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find((int) 52001));
                }
                //SI NO SE DETERMINA EL BARRIO SE COLOCA SIN DATO URBANO
                if (newFatalInjurie.getInjuryNeighborhoodId() == null) {
                    newFatalInjurie.setInjuryNeighborhoodId((int) 52001);
                }
                //DETERMINAR TIPO DE IDENTIFICACION
                if (newVictim.getVictimNid() != null) {
                    if (newVictim.getTypeId() == null) {
                        //si tiene edad menor o mayor sin identificacion, si no hay edad dejar sin determinar
                        if (newVictim.getVictimAge() != null) {
                            if (newVictim.getVictimAge() >= 18) {
                                newVictim.setTypeId(idTypesFacade.find((short) 6));//6. ADULTO SIN IDENTIFICACION
                            } else {
                                newVictim.setTypeId(idTypesFacade.find((short) 7));//7. MENOR SIN IDENTIFICACION
                            }
                        } else {
                            newVictim.setTypeId(idTypesFacade.find((short) 9));//9. SIN DETERMINAR
                        }
                    }
                }

                //-------------------GUARDAR----------------------------
                //if (validationsErrors.isEmpty()) {
                openDialogFirst = "";
                openDialogNext = "";
                openDialogLast = "";
                openDialogPrevious = "";
                openDialogNew = "";
                openDialogDelete = "";
                if (currentFatalInjuriId == -1) {//ES UN NUEVO REGISTRO SE DEBE PERSISTIR
                    //System.out.println("guardando nuevo registro");
                    newVictim.setTagId(tagsFacade.find(currentTag));
                    newVictim.setFirstTagId(newVictim.getTagId().getTagId());
                    victimsFacade.create(newVictim);
                    fatalInjuriesFacade.create(newFatalInjurie);
                    fatalInjuryMurderFacade.create(newMurder);

                    save = true;
                    stylePosition = "color: #1471B1;";
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "NUEVO REGISTRO ALMACENADO");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                } else {//ES UN REGISTRO EXISTENTE SE DEBE ACTUALIZAR
                    //System.out.println("actualizando registro existente");
                    updateRegistry(newVictim, newFatalInjurie, newMurder);
                    save = true;
                    stylePosition = "color: #1471B1;";
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "REGISTRO ACTUALIZADO");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
                applicationControlMB.removeFatalReservedIdentifiers(newFatalInjurie.getFatalInjuryId());
                applicationControlMB.removeVictimsReservedIdentifiers(newVictim.getVictimId());
                return true;
            } catch (NumberFormatException | ParseException e) {
                System.out.println("Error 1 en " + this.getClass().getName() + ":" + e.toString());
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.toString());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return false;
            }
        } else {
            return false;
        }
    }

    public void nada() {
    }

    /**
     * This method deletes a record from the database, for this verifies that
     * the user has sufficient privileges.
     */
    public void deleteRegistry() {
        if (currentFatalInjuriId != -1) {
            if (!loginMB.isPermissionAdministrator() && loginMB.getCurrentUser().getUserId() != currentFatalInjuryMurder.getFatalInjuries().getUserId().getUserId()) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Este registro solo puede ser modificado por un administrador o por el usuario que creo el registro");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                FatalInjuries auxFatalInjuries = currentFatalInjuryMurder.getFatalInjuries();
                Victims auxVictims = currentFatalInjuryMurder.getFatalInjuries().getVictimId();
                fatalInjuryMurderFacade.remove(currentFatalInjuryMurder);
                fatalInjuriesFacade.remove(auxFatalInjuries);
                victimsFacade.remove(auxVictims);
                //System.out.println("registro eliminado");
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha eliminado el registro");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                noSaveAndGoNew();
            }
        }
    }

    /**
     * updated data of a victim who is already registered
     *
     * @param victim
     * @param fatalInjurie
     * @param fatalInjuryMurder
     */
    private void updateRegistry(Victims victim, FatalInjuries fatalInjurie, FatalInjuryMurder fatalInjuryMurder) {
        try {
            //------------------------------------------------------------
            //DATOS VICTIMA
            //------------------------------------------------------------
            currentFatalInjuryMurder.getFatalInjuries().getVictimId().setTypeId(victim.getTypeId());
            currentFatalInjuryMurder.getFatalInjuries().getVictimId().setVictimNid(victim.getVictimNid());
            currentFatalInjuryMurder.getFatalInjuries().getVictimId().setVictimName(victim.getVictimName());
            currentFatalInjuryMurder.getFatalInjuries().getVictimId().setAgeTypeId(victim.getAgeTypeId());
            currentFatalInjuryMurder.getFatalInjuries().getVictimId().setVictimAge(victim.getVictimAge());
            currentFatalInjuryMurder.getFatalInjuries().getVictimId().setGenderId(victim.getGenderId());
            currentFatalInjuryMurder.getFatalInjuries().getVictimId().setJobId(victim.getJobId());
            currentFatalInjuryMurder.getFatalInjuries().getVictimId().setVictimAddress(victim.getVictimAddress());
            currentFatalInjuryMurder.getFatalInjuries().getVictimId().setVictimNeighborhoodId(victim.getVictimNeighborhoodId());
            currentFatalInjuryMurder.getFatalInjuries().getVictimId().setResidenceMunicipality(victim.getResidenceMunicipality());
            currentFatalInjuryMurder.getFatalInjuries().getVictimId().setResidenceDepartment(victim.getResidenceDepartment());
            //------------------------------------------------------------
            //DATOS LESION DE CAUSA EXTERNA FATAL
            //------------------------------------------------------------
            currentFatalInjuryMurder.getFatalInjuries().setInjuryDate(fatalInjurie.getInjuryDate());
            currentFatalInjuryMurder.getFatalInjuries().setInjuryTime(fatalInjurie.getInjuryTime());
            currentFatalInjuryMurder.getFatalInjuries().setInjuryAddress(fatalInjurie.getInjuryAddress());
            currentFatalInjuryMurder.getFatalInjuries().setInjuryNeighborhoodId(fatalInjurie.getInjuryNeighborhoodId());
            currentFatalInjuryMurder.getFatalInjuries().setQuadrantId(fatalInjurie.getQuadrantId());
            currentFatalInjuryMurder.getFatalInjuries().setInjuryPlaceId(fatalInjurie.getInjuryPlaceId());
            currentFatalInjuryMurder.getFatalInjuries().setVictimNumber(fatalInjurie.getVictimNumber());
            currentFatalInjuryMurder.getFatalInjuries().setInjuryDescription(fatalInjurie.getInjuryDescription());
            currentFatalInjuryMurder.getFatalInjuries().setInjuryDayOfWeek(fatalInjurie.getInjuryDayOfWeek());
            currentFatalInjuryMurder.getFatalInjuries().setAlcoholLevelVictim(fatalInjurie.getAlcoholLevelVictim());
            currentFatalInjuryMurder.getFatalInjuries().setAlcoholLevelVictimId(fatalInjurie.getAlcoholLevelVictimId());
            currentFatalInjuryMurder.getFatalInjuries().setCode(fatalInjurie.getCode());
            currentFatalInjuryMurder.getFatalInjuries().setAreaId(fatalInjurie.getAreaId());
            currentFatalInjuryMurder.getFatalInjuries().setVictimPlaceOfOrigin(fatalInjurie.getVictimPlaceOfOrigin());
            //------------------------------------------------------------
            //DATOS LESION FATAL POR HOMICIDIOS
            //------------------------------------------------------------
            currentFatalInjuryMurder.setMurderContextId(fatalInjuryMurder.getMurderContextId());
            currentFatalInjuryMurder.setWeaponTypeId(fatalInjuryMurder.getWeaponTypeId());
            victimsFacade.edit(currentFatalInjuryMurder.getFatalInjuries().getVictimId());
            fatalInjuriesFacade.edit(currentFatalInjuryMurder.getFatalInjuries());
            fatalInjuryMurderFacade.edit(currentFatalInjuryMurder);
        } catch (Exception e) {
            System.out.println("Error 2 en " + this.getClass().getName() + ":" + e.toString());
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    /**
     * This method deletes a record from the database, for this verifies that
     * the user has sufficient privileges.
     */
    public void determinePosition() {
        totalRegisters = fatalInjuryMurderFacade.countMurder(currentTag);
        if (currentFatalInjuriId == -1) {
            currentPosition = "new" + "/" + String.valueOf(totalRegisters);
            currentIdForm = String.valueOf(fatalInjuriesFacade.findMax() + 1);
            openDialogDelete = "";//es nuevo no se puede borrar
        } else {
            int position = fatalInjuryMurderFacade.findPosition(currentFatalInjuryMurder.getFatalInjuryId(), currentTag);
            currentIdForm = String.valueOf(currentFatalInjuryMurder.getFatalInjuryId());
            currentPosition = position + "/" + String.valueOf(totalRegisters);
            openDialogDelete = "dialogDelete.show();";
        }
        //System.out.println("POSICION DETERMINADA: " + currentPosition);
    }

    /**
     * save changes realized to a victim and proceeds to next form
     */
    public void saveAndGoNext() {//guarda cambios si se han realizado y se dirije al siguiente
        if (saveRegistry()) {
            next();
        } else {
            //System.out.println("No se guardo");
        }
    }

    /**
     * save changes realized to a victim and proceeds to previous form
     */
    public void saveAndGoPrevious() {//guarda cambios si se han realizado y se dirije al anterior
        if (currentFatalInjuriId != -1) {
            if (saveRegistry()) {
                previous();
            }
        } else {
            if (saveRegistry()) {
                last();
            }
        }
    }

    /**
     * This method save all changes realized to a victim and proceeds to the
     * first form.
     */
    public void saveAndGoFirst() {//guarda cambios si se han realizado y se dirije al primero
        if (saveRegistry()) {
            first();
        }
    }

    /**
     * This method save all changes realized to a victim and proceeds to the
     * last form.
     */
    public void saveAndGoLast() {//guarda cambios si se han realizado y se dirije al ultimo
        if (saveRegistry()) {
            last();
        }
    }

    /**
     * This method save all changes realized to a victim and this method creates
     * a new form.
     */
    public void saveAndGoNew() {//guarda cambios si se han realizado y se dirije al ultimo
        if (saveRegistry()) {
            newForm();
        }
    }

    /**
     * Discards all changes realized to a victim and this method creates a new
     * form.
     */
    public void noSaveAndGoNew() {//guarda cambios si se han realizado y se dirije al ultimo
        openDialogFirst = "";
        openDialogNext = "";
        openDialogLast = "";
        openDialogPrevious = "";
        openDialogNew = "";
        openDialogDelete = "";
        save = true;
        stylePosition = "color: #1471B1;";
        newForm();

    }

    /**
     * This method save all changes realized to a victim and proceeds to the
     * next form.
     */
    public void noSaveAndGoNext() {//va al siguiente sin guardar cambios si se han realizado
        openDialogFirst = "";
        openDialogNext = "";
        openDialogLast = "";
        openDialogPrevious = "";
        openDialogNew = "";
        openDialogDelete = "";
        save = true;
        stylePosition = "color: #1471B1;";
        next();
    }

    /**
     * This method save all changes realized to a victim and proceeds to the
     * previous form.
     */
    public void noSaveAndGoPrevious() {//va al anterior sin guardar cambios si se han realizado
        openDialogFirst = "";
        openDialogNext = "";
        openDialogLast = "";
        openDialogPrevious = "";
        openDialogNew = "";
        openDialogDelete = "";
        save = true;
        stylePosition = "color: #1471B1;";
        if (currentFatalInjuriId != -1) {
            previous();
        } else {
            last();
        }
    }

    /**
     * Discards all changes realized to a victim and proceeds to the first form.
     */
    public void noSaveAndGoFirst() {//va al primero sin guardar cambios si se han realizado
        openDialogFirst = "";
        openDialogNext = "";
        openDialogLast = "";
        openDialogPrevious = "";
        openDialogNew = "";
        openDialogDelete = "";
        save = true;
        stylePosition = "color: #1471B1;";
        first();
    }

    /**
     * Discards all changes realized to a victim and proceeds to the first form.
     */
    public void noSaveAndGoLast() {//va al ultimo sin guardar cambios si se han realizado
        openDialogFirst = "";
        openDialogNext = "";
        openDialogLast = "";
        openDialogPrevious = "";
        openDialogNew = "";
        openDialogDelete = "";
        save = true;
        stylePosition = "color: #1471B1;";
        last();
    }

    /**
     * This method displays the next record, if the current record is not
     * recorded then this method displays a dialog that allows the user to save
     * the current record.
     */
    public void next() {
        if (save) {//se busca el siguiente se el registro esta guardado (si esta guardado se abrira un dialogo que pregunta si guardar)             
            //System.out.println("cargando siguiente registro");
            if (currentFatalInjuriId == -1) {//esta en registro nuevo                
            } else {
                auxFatalInjuryMurder = fatalInjuryMurderFacade.findNext(currentFatalInjuriId, currentTag);
                if (auxFatalInjuryMurder != null) {
                    clearForm();
                    currentFatalInjuryMurder = fatalInjuryMurderFacade.findNext(currentFatalInjuriId, currentTag);
                    currentFatalInjuriId = currentFatalInjuryMurder.getFatalInjuryId();
                    determinePosition();
                    loadValues();
                }
            }
        } else {
            //System.out.println("No esta guardadado (para poder cargar siguiente registro)");
        }
    }

    /**
     * This method displays the previous record, if the current record is not
     * recorded then this method displays a dialog that allows the user to save
     * the current record.
     */
    public void previous() {
        if (save) {
            //System.out.println("cargando anterior registro");
            if (currentFatalInjuriId == -1) {//esta en registro nuevo
                last();
            } else {
                auxFatalInjuryMurder = fatalInjuryMurderFacade.findPrevious(currentFatalInjuriId, currentTag);
                if (auxFatalInjuryMurder != null) {
                    clearForm();
                    currentFatalInjuryMurder = fatalInjuryMurderFacade.findPrevious(currentFatalInjuriId, currentTag);
                    currentFatalInjuriId = currentFatalInjuryMurder.getFatalInjuryId();
                    determinePosition();
                    loadValues();
                }
            }
        } else {
            //System.out.println("No esta guardadado (para poder cargar anterior registro)");
        }
    }

    /**
     * This method displays the first record, if the current record is not
     * recorded then this method displays a dialog that allows the user to save
     * the current record.
     */
    public void first() {
        if (save) {
            //System.out.println("cargando primer registro");
            auxFatalInjuryMurder = fatalInjuryMurderFacade.findFirst(currentTag);
            if (auxFatalInjuryMurder != null) {
                clearForm();
                currentFatalInjuryMurder = fatalInjuryMurderFacade.findFirst(currentTag);
                currentFatalInjuriId = currentFatalInjuryMurder.getFatalInjuryId();
                determinePosition();
                loadValues();
            }
        } else {
            //System.out.println("No esta guardadado (para poder cargar primer registro)");
        }
    }

    /**
     * This method displays the last record, if the current record is not
     * recorded then this method displays a dialog that allows the user to save
     * the current record.
     */
    public void last() {
        if (save) {
            //System.out.println("cargando ultimo registro");
            auxFatalInjuryMurder = fatalInjuryMurderFacade.findLast(currentTag);
            if (auxFatalInjuryMurder != null) {
                clearForm();
                currentFatalInjuryMurder = fatalInjuryMurderFacade.findLast(currentTag);
                currentFatalInjuriId = currentFatalInjuryMurder.getFatalInjuryId();
                determinePosition();
                loadValues();
            }
        } else {
            //System.out.println("No esta guardadado (para poder cargar ultimo registro)");
        }
    }

    /**
     * This method clears all form fields to enter data for a new victim.
     */
    public void clearForm() {

        //System.out.println("Limpiando formulario");        

        currentAmPmEvent = "AM";

        strangerDisabled = true;
        stranger = false;
        //------------------------------------------------------------
        //REINICIAR VALORES PARA LA NUEVA VICTIMA
        //------------------------------------------------------------	

        loading = true;
        currentDayEventDisabled = false;
        currentMonthEventDisabled = false;
        currentYearEventDisabled = false;
        currentMinuteEventDisabled = false;
        currentHourEventDisabled = false;
        currentAmPmEventDisabled = false;

        currentIdentificationType = 0;
        currentIdentificationNumber = "";
        currentName = "";
        currentMeasureOfAge = 0;
        valueAgeDisabled = true;
        currentAge = "";
        currentGender = 0;
        currentJob = "";
        currentDirectionEvent = "";
        currentNeighborhoodEventCode = "";
        currentNeighborhoodEvent = "";

        quadrantsEvent = new SelectItem[1];
        quadrantsEvent[0] = new SelectItem(0, "SIN DATO");
        currentQuadrantEvent = 0;
        //------------------------------------------------------------
        //REINICIAR VARIABLES LESION DE CAUSA EXTERNA FATAL
        //------------------------------------------------------------                

        currentSourceDepartament = 0;
        currentSourceMunicipalitie = 0;
        currentSourceCountry = 52;

        findSourceDepartaments();

        currentDateEvent = "";
        currentDayEvent = "";
        currentMonthEvent = "";
        currentYearEvent = Integer.toString(c.get(Calendar.YEAR));
        currentHourEvent = "";
        currentMinuteEvent = "";
        currentMilitaryHourEvent = "";
        currentDirectionEvent = "";
        currentNeighborhoodHomeCode = "";
        currentNeighborhoodHome = "";
        neighborhoodHomeNameDisabled = false;
        currentDepartamentHomeDisabled = false;
        currentDepartamentHome = 52;
        changeDepartamentHome();
        currentMunicipalitie = 1;

        currentMunicipalitieDisabled = false;
        neighborhoodHomeNameDisabled = false;
        currentPlace = 0;
        currentNumberVictims = "";
        currentNarrative = "";
        currentWeekdayEvent = "";
        isNoDataAlcoholLevelDisabled = false;
        isUnknownAlcoholLevelDisabled = false;
        isPendentAlcoholLevelDisabled = false;
        isNegativeAlcoholLevelDisabled = false;
        currentAlcoholLevelDisabled = false;
        currentAlcoholLevel = "";
        isNoDataAlcoholLevel = false;
        isUnknownAlcoholLevel = false;
        isPendentAlcoholLevel = false;
        isNegativeAlcoholLevel = false;
        currentCode = "";
        //------------------------------------------------------------
        //LIMPIAR PARA LA NUEVA LESION FATAL POR HOMICIDIOS
        //------------------------------------------------------------
        //currentVictimSource = "";
        currentMurderContext = 0;
        currentWeaponType = 0;
        currentArea = 0;
        loading = false;
    }

    /**
     * This method is responsible to load the information corresponding to a
     * victim within the form.
     */
    public void newForm() {
        //currentFatalInjuryMurder = null;
        if (save) {
            clearForm();
            currentFatalInjuriId = -1;
            determinePosition();
        } else {
            //System.out.println("No esta guardado (para poder limpiar formulario)");
        }
    }

    /**
     * This method is responsible for reset all form fields, also this method
     * load the default values for that the user can to register data of a
     * victim.
     */
    public void reset() {
        currentUser = loginMB.getCurrentUser();
        loading = true;
        currentYearEvent = Integer.toString(c.get(Calendar.YEAR));
        quadrantsEvent = new SelectItem[1];
        quadrantsEvent[0] = new SelectItem(0, "SIN DATO");
        currentQuadrantEvent = 0;
        try {
            //cargo los conjuntos de registros
            List<Tags> tagsList = tagsFacade.findAll();
            int count = 0;
            for (int i = 0; i < tagsList.size(); i++) {
                if (tagsList.get(i).getFormId().getFormId().compareTo("SCC-F-028") == 0) {
                    count++;
                }
            }
            tags = new SelectItem[count];
            count = 0;
            currentTag = 0;
            for (int i = 0; i < tagsList.size(); i++) {
                if (tagsList.get(i).getFormId().getFormId().compareTo("SCC-F-028") == 0) {
                    if (currentTag == 0) {
                        currentTag = tagsList.get(i).getTagId();
                    }
                    tags[count] = new SelectItem(tagsList.get(i).getTagId(), tagsList.get(i).getTagName());
                    count++;
                }
            }
            //cargo los tipos de identificacion
            List<IdTypes> idTypesList = idTypesFacade.findAll();
            identificationsTypes = new SelectItem[idTypesList.size() + 1];
            identificationsTypes[0] = new SelectItem(0, "");
            for (int i = 0; i < idTypesList.size(); i++) {
                identificationsTypes[i + 1] = new SelectItem(idTypesList.get(i).getTypeId(), idTypesList.get(i).getTypeName());
            }
            //cargo las medidas de edad
            List<AgeTypes> ageTypesList = ageTypesFacade.findAll();
            measuresOfAge = new SelectItem[ageTypesList.size() + 1];
            measuresOfAge[0] = new SelectItem(0, "");
            for (int i = 0; i < ageTypesList.size(); i++) {
                measuresOfAge[i + 1] = new SelectItem(ageTypesList.get(i).getAgeTypeId(), ageTypesList.get(i).getAgeTypeName());
            }
            //contexto relacionado con el hecho
            List<MurderContexts> murderContextsList = murderContextsFacade.findAll();
            murderContexts = new SelectItem[murderContextsList.size() + 1];
            murderContexts[0] = new SelectItem(0, "");
            for (int i = 0; i < murderContextsList.size(); i++) {
                murderContexts[i + 1] = new SelectItem(murderContextsList.get(i).getMurderContextId(), murderContextsList.get(i).getMurderContextName());
            }
            //cargo los paises de procedencia
            List<Countries> countriesList = countriesFacade.findAll();
            sourceCountries = new SelectItem[countriesList.size() + 1];
            sourceCountries[0] = new SelectItem(0, "");
            for (int i = 0; i < countriesList.size(); i++) {
                sourceCountries[i + 1] = new SelectItem(countriesList.get(i).getIdCountry(), countriesList.get(i).getName());
            }

            //cargo departamentos residencia
            List<Departaments> departamentsHomeList = departamentsFacade.findAll();
            homeDepartaments = new SelectItem[departamentsHomeList.size() + 1];
            homeDepartaments[0] = new SelectItem(0, "");
            for (int i = 0; i < departamentsHomeList.size(); i++) {
                homeDepartaments[i + 1] = new SelectItem(departamentsHomeList.get(i).getDepartamentId(), departamentsHomeList.get(i).getDepartamentName());
            }



            //cargo municipios de residencia
            findMunicipalities();
            currentSourceCountry = 52;
            findSourceDepartaments();
            //clase de lugares donde ocurrieron los hechos
            List<Places> placesList = placesFacade.findAll();
            places = new SelectItem[placesList.size() + 1];
            places[0] = new SelectItem(0, "");
            for (int i = 0; i < placesList.size(); i++) {
                places[i + 1] = new SelectItem(placesList.get(i).getPlaceId(), placesList.get(i).getPlaceName());
            }
            //generos
            List<Genders> gendersList = gendersFacade.findAll();
            genders = new SelectItem[gendersList.size() + 1];
            genders[0] = new SelectItem(0, "");
            for (int i = 0; i < gendersList.size(); i++) {
                genders[i + 1] = new SelectItem(gendersList.get(i).getGenderId(), gendersList.get(i).getGenderName());
            }
            //cargo las areas del hecho
            List<Areas> areasList = areasFacade.findAll();
            areas = new SelectItem[areasList.size() + 1];
            areas[0] = new SelectItem(0, "");
            for (int i = 0; i < areasList.size(); i++) {
                areas[i + 1] = new SelectItem(areasList.get(i).getAreaId(), areasList.get(i).getAreaName());
            }
            //cargo los tipos de armas
            List<WeaponTypes> weaponTypesList = weaponTypesFacade.findAll();
            weaponTypes = new SelectItem[weaponTypesList.size() + 1];
            weaponTypes[0] = new SelectItem(0, "");
            for (int i = 0; i < weaponTypesList.size(); i++) {
                weaponTypes[i + 1] = new SelectItem(weaponTypesList.get(i).getWeaponTypeId(), weaponTypesList.get(i).getWeaponTypeName());
            }
            //lista de criterios de busqueda            
            searchCriteriaList = new SelectItem[3];
            searchCriteriaList[0] = new SelectItem(1, "IDENTIFICACION");
            searchCriteriaList[1] = new SelectItem(2, "NOMBRE");
            searchCriteriaList[2] = new SelectItem(3, "CODIGO INTERNO");
            //
            rowDataTableList = new ArrayList<>();
            determinePosition();
            openDialogFirst = "";
            openDialogNext = "";
            openDialogLast = "";
            openDialogPrevious = "";
            openDialogNew = "";
            save = true;
            //System.out.println("Save=true");
            stylePosition = "color: #1471B1;";
        } catch (Exception e) {
            System.out.println("Error 3 en " + this.getClass().getName() + ":" + e.toString());
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        //System.out.println("//////////////FORMULARIO REINICIADO//////////////////////////");
        loading = false;
    }
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    // FUNCIONES PARA BUSCAR UN REGISTRO -----------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    private Date date1;
    private Date date2;
    private int currentSearchCriteria = 0;
    private SelectItem[] searchCriteriaList;
    private String currentSearchValue = "";
    /*
     * get the  row list corresponding to the result of search
     */

    public List<RowDataTable> getRowDataTableList() {
        return rowDataTableList;
    }
    /*
     * 
     */

    public void setRowDataTableList(List<RowDataTable> rowDataTableList) {
        this.rowDataTableList = rowDataTableList;
    }
    /*
     * get the selected row corresponding to the form of a victim
     */

    public RowDataTable getSelectedRowDataTable() {
        return selectedRowDataTable;
    }

    public void setSelectedRowDataTable(RowDataTable selectedRowDataTable) {
        this.selectedRowDataTable = selectedRowDataTable;
    }

    /**
     * This method is responsible to load the corresponding form to a victim who
     * was selected in the option "Buscar" .
     */
    public void openForm() {
        if (selectedRowDataTable != null) {
            //auxFatalInjuryMurder = fatalInjuryMurderFacade.findByIdVictim(selectedRowDataTable.getColumn1());
            auxFatalInjuryMurder = fatalInjuryMurderFacade.find(Integer.parseInt(selectedRowDataTable.getColumn1()));
            if (auxFatalInjuryMurder != null) {
                clearForm();
                currentFatalInjuryMurder = auxFatalInjuryMurder;
                currentFatalInjuriId = currentFatalInjuryMurder.getFatalInjuryId();
                determinePosition();
                loadValues();
            }
        }
        clearSearch();
    }

    /**
     * This method cleans all fields of result of a search for realize a new
     * one.
     */
    public void clearSearch() {
        currentSearchValue = "";
        currentSearchCriteria = 1;
        rowDataTableList = new ArrayList<>();

    }

    /**
     * This method creates a Dinamic Table to display the fields of a search
     * realized.
     */
    public void createDynamicTable() {
        boolean s = true;
        if (currentSearchValue.trim().length() == 0) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar un valor a buscar");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            s = false;
        }
        if (s) {
            try {
                rowDataTableList = new ArrayList<>();
                String sql = "";
                sql = sql + "SELECT ";
                sql = sql + "fatal_injuries.fatal_injury_id, ";
                sql = sql + "victims.victim_nid, ";
                sql = sql + "victims.victim_name ";
                sql = sql + "FROM ";
                sql = sql + "victims, ";
                sql = sql + "fatal_injuries, ";
                sql = sql + "injuries ";
                sql = sql + "WHERE ";
                sql = sql + "fatal_injuries.victim_id = victims.victim_id AND ";
                sql = sql + "injuries.injury_id = fatal_injuries.injury_id AND ";

                switch (currentSearchCriteria) {
                    case 1://Identificación
                        sql = sql + "victims.victim_nid LIKE '" + currentSearchValue + "%' AND ";
                        break;
                    case 2://nombres
                        sql = sql + "UPPER(victims.victim_name) LIKE UPPER('%" + currentSearchValue + "%') AND ";
                        break;
                    case 3://codigo interno
                        sql = sql + "fatal_injuries.fatal_injury_id = " + currentSearchValue + " AND ";
                        break;

                }
                sql = sql + "fatal_injuries.injury_id = 10";
                //System.out.println(sql);
                ResultSet rs = connectionJdbcMB.consult(sql);
                while (rs.next()) {
                    rowDataTableList.add(new RowDataTable(rs.getString(1), rs.getString(2), rs.getString(3)));
                    s = false;//aqui se usa para saber si hay registros
                }
                if (s) {//si es true no hay registros
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "No hay coincidencias", "No se encontraron registros para esta búsqueda");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
            } catch (Exception ex) {
            }
        }
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    // FUNCIONES PARA AUTOCOMPLETAR ----------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * This method is responsible to display a jobs list that have a similar
     * name to which the user is typing.
     *
     * @param entered
     * @return
     */
    public List<String> suggestNeighborhoods(String entered) {
        List<String> list = new ArrayList<>();
        try {
            ResultSet rs;
            String sql = ""
                    + " SELECT "
                    + "    neighborhoods.neighborhood_name"
                    + " FROM "
                    + "    public.neighborhoods"
                    + " WHERE "
                    + "    neighborhoods.neighborhood_name ILIKE '" + entered + "%'"
                    + " LIMIT 10;";
            rs = connectionJdbcMB.consult(sql);
            while (rs.next()) {
                list.add(rs.getString(1));
            }
        } catch (Exception e) {
        }
        return list;
    }

    /**
     * This method is responsible to display a jobs list that have a similar
     * name to which the user is typing.
     *
     * @param entered
     * @return
     */
    public List<String> suggestJobs(String entered) {
        List<String> list = new ArrayList<>();
        try {
            ResultSet rs;
            String sql = ""
                    + " SELECT "
                    + "    jobs.job_name"
                    + " FROM "
                    + "    public.jobs"
                    + " WHERE "
                    + "    jobs.job_name ILIKE '%" + entered + "%'"
                    + " LIMIT 10;";
            rs = connectionJdbcMB.consult(sql);
            while (rs.next()) {
                list.add(rs.getString(1));
            }
        } catch (Exception e) {
        }
        return list;
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    // FUNCIONES CUANDO LISTAS Y CAMPOS CAMBIAN DE VALOR -------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * This method restores the values of stranger
     */
    public void changeTag() {//cambia el conjunto de registros
        noSaveAndGoNew();
    }

    /**
     * This method restores the values of stranger
     */
    public void changeStranger() {
        if (loading == false) {
            changeForm();
        }
        if (stranger) {
            currentMunicipalitieDisabled = true;
            neighborhoodHomeNameDisabled = true;
            currentDepartamentHomeDisabled = true;
            currentDepartamentHome = 0;
            municipalities = new SelectItem[1];
            municipalities[0] = new SelectItem(0, "");
            currentMunicipalitie = 0;
            currentNeighborhoodHome = "";
        } else {
            currentDepartamentHomeDisabled = false;
            currentMunicipalitieDisabled = false;
        }
    }

    /**
     * This method is responsible to display all departments corresponding to a
     * country.
     */
    public void findSourceDepartaments() {

        if (loading == false) {
            changeForm();
        }

        if (currentSourceCountry == 52) {//colombia
            //cargo departamentos
            List<Departaments> departamentsList = departamentsFacade.findAll();
            sourceDepartaments = new SelectItem[departamentsList.size() + 1];
            sourceDepartaments[0] = new SelectItem(0, "");
            for (int i = 0; i < departamentsList.size(); i++) {
                sourceDepartaments[i + 1] = new SelectItem(departamentsList.get(i).getDepartamentId(), departamentsList.get(i).getDepartamentName());
            }
            currentSourceDepartament = 52;
            //municipio de procedencia queda en blanco
            Departaments d = departamentsFacade.findById(currentSourceDepartament);
            sourceMunicipalities = new SelectItem[d.getMunicipalitiesList().size() + 1];
            sourceMunicipalities[0] = new SelectItem(0, "");
            for (int i = 0; i < sourceMunicipalities.length - 1; i++) {
                sourceMunicipalities[i + 1] = new SelectItem(d.getMunicipalitiesList().get(i).getMunicipalitiesPK().getMunicipalityId(), d.getMunicipalitiesList().get(i).getMunicipalityName());
            }
            currentSourceMunicipalitie = 1;
        } else {
            //departamentos de procedencia queda en blanco
            sourceDepartaments = new SelectItem[1];
            sourceDepartaments[0] = new SelectItem(0, "");

            //municipio de procedencia queda en blanco
            sourceMunicipalities = new SelectItem[1];
            sourceMunicipalities[0] = new SelectItem(0, "");
        }

    }

    /**
     * This method is responsible to display all municipalities corresponding to
     * a departament.
     */
    public void findSourceMunicipalities() {

        if (loading == false) {
            changeForm();
        }

        if (currentSourceDepartament != 0) {
            Departaments d = departamentsFacade.findById(currentSourceDepartament);
            sourceMunicipalities = new SelectItem[d.getMunicipalitiesList().size() + 1];
            sourceMunicipalities[0] = new SelectItem(0, "");
            for (int i = 0; i < sourceMunicipalities.length - 1; i++) {
                sourceMunicipalities[i + 1] = new SelectItem(d.getMunicipalitiesList().get(i).getMunicipalitiesPK().getMunicipalityId(), d.getMunicipalitiesList().get(i).getMunicipalityName());
            }
            currentSourceMunicipalitie = 0;
        } else {
            sourceMunicipalities = new SelectItem[1];
            sourceMunicipalities[0] = new SelectItem(0, "");
        }

    }

    /**
     * This method is responsible to Load municipalities of residence.
     */
    public void findMunicipalities() {

        if (loading == false) {
            changeForm();
        }

        Departaments d = departamentsFacade.findById((short) 52);
        municipalities = new SelectItem[d.getMunicipalitiesList().size()];

        for (int i = 0; i < municipalities.length; i++) {
            municipalities[i] = new SelectItem(d.getMunicipalitiesList().get(i).getMunicipalitiesPK().getMunicipalityId(), d.getMunicipalitiesList().get(i).getMunicipalityName());
        }
        currentMunicipalitie = d.getMunicipalitiesList().get(0).getMunicipalitiesPK().getMunicipalityId();


        if (currentMunicipalitie == 1) {
            neighborhoodHomeNameDisabled = false;
        } else {
            neighborhoodHomeNameDisabled = true;
            currentNeighborhoodHome = "";
            currentNeighborhoodHomeCode = "";
        }
    }

    /**
     * This method displays all departments of residence.
     */
    public void changeDepartamentHome() {
        if (loading == false) {
            changeForm();
            currentNeighborhoodHome = "";
            currentNeighborhoodHomeCode = "";
        }
        if (currentDepartamentHome != 0) {
            Departaments d = departamentsFacade.findById(currentDepartamentHome);
            municipalities = new SelectItem[d.getMunicipalitiesList().size() + 1];
            municipalities[0] = new SelectItem(0, "");
            for (int i = 0; i < d.getMunicipalitiesList().size(); i++) {
                municipalities[i + 1] = new SelectItem(d.getMunicipalitiesList().get(i).getMunicipalitiesPK().getMunicipalityId(), d.getMunicipalitiesList().get(i).getMunicipalityName());
            }
            if (currentDepartamentHome == 52) {
                currentMunicipalitie = 1;
            } else {
                currentMunicipalitie = 0;
            }
        } else {
            municipalities = new SelectItem[1];
            municipalities[0] = new SelectItem(0, "");
            currentMunicipalitie = 0;
        }
        //neighborhoodHomeNameDisabled = true;

        changeMunicipalitieHome();
    }

    /**
     * This method is responsible to Show all municipalities of residence
     */
    public void changeMunicipalitieHome() {
        //Municipalities m = municipalitiesFacade.findById(currentMunicipalitie, currentDepartamentHome);
        if (loading == false) {
            changeForm();
        }
        if (currentMunicipalitie == 1 && currentDepartamentHome == 52) {
            neighborhoodHomeNameDisabled = false;
        } else {
            neighborhoodHomeNameDisabled = true;
            currentNeighborhoodHome = "";
            currentNeighborhoodHomeCode = "";
        }
    }

    /**
     * This method is responsible to display all levels of alcohol, so that the
     * user can select one. The alcohol levels are: NO DATO, PENDIENTE, NEGATIVO
     * and DESCONOCIDO.
     */
    public void changeAlcoholLevel() {
        if (loading == false) {
            changeForm();
        }
        if (!isNoDataAlcoholLevel && !isPendentAlcoholLevel
                && !isUnknownAlcoholLevel && !isNegativeAlcoholLevel) {
            currentAlcoholLevelDisabled = false;
            currentAlcoholLevel = "";
            isNoDataAlcoholLevelDisabled = false;
            isPendentAlcoholLevelDisabled = false;
            isUnknownAlcoholLevelDisabled = false;
            isNegativeAlcoholLevelDisabled = false;
        } else {
            if (isNoDataAlcoholLevel) {
                currentAlcoholLevelDisabled = true;
                currentAlcoholLevel = "";
                isNoDataAlcoholLevelDisabled = false;
                isPendentAlcoholLevelDisabled = true;
                isUnknownAlcoholLevelDisabled = true;
                isNegativeAlcoholLevelDisabled = true;
            }
            if (isPendentAlcoholLevel) {
                currentAlcoholLevelDisabled = true;
                currentAlcoholLevel = "";
                isNoDataAlcoholLevelDisabled = true;
                isPendentAlcoholLevelDisabled = false;
                isUnknownAlcoholLevelDisabled = true;
                isNegativeAlcoholLevelDisabled = true;
            }
            if (isUnknownAlcoholLevel) {
                currentAlcoholLevelDisabled = true;
                currentAlcoholLevel = "";
                isNoDataAlcoholLevelDisabled = true;
                isPendentAlcoholLevelDisabled = true;
                isUnknownAlcoholLevelDisabled = false;
                isNegativeAlcoholLevelDisabled = true;
            }
            if (isNegativeAlcoholLevel) {
                currentAlcoholLevelDisabled = true;
                currentAlcoholLevel = "";
                isNoDataAlcoholLevelDisabled = true;
                isPendentAlcoholLevelDisabled = true;
                isUnknownAlcoholLevelDisabled = true;
                isNegativeAlcoholLevelDisabled = false;
            }
        }
    }

    /**
     * Show all Neighborhoods having a similar name to which the user is
     * writing.
     */
    public void changeNeighborhoodHomeName() {
        if (loading == false) {
            changeForm();
        }
        List<Neighborhoods> neighborhoodsList = neighborhoodsFacade.findAll();
        for (int i = 0; i < neighborhoodsList.size(); i++) {
            if (neighborhoodsList.get(i).getNeighborhoodName().compareTo(currentNeighborhoodHome) == 0) {
                currentNeighborhoodHomeCode = String.valueOf(neighborhoodsList.get(i).getNeighborhoodId());
                break;
            }
        }
    }

    /**
     * this method is responsible to complete the fields: CODIGO BARRIO,
     * CUADRANTE and AREA DEL HECHO when a neighborhood is selected by the user.
     */
    public void changeNeighborhoodEvent() {
        if (loading == false) {
            changeForm();
        }
        if (currentNeighborhoodEvent != null) {
            if (currentNeighborhoodEvent.length() != 0) {
                Neighborhoods n = neighborhoodsFacade.findByName(currentNeighborhoodEvent);
                if (n != null) {
                    currentNeighborhoodEventCode = String.valueOf(n.getNeighborhoodId());
                    currentArea = Short.parseShort(n.getNeighborhoodArea().toString());
                    //cargo cuadrantes
                    try {
                        ResultSet rs = connectionJdbcMB.consult(""
                                + " SELECT COUNT(*) FROM quadrants WHERE quadrant_id IN "
                                + " (SELECT quadrant_id FROM neighborhood_quadrant "
                                + " WHERE neighborhood_id = " + currentNeighborhoodEventCode + ") ");
                        if (rs.next()) {
                            quadrantsEvent = new SelectItem[rs.getInt(1)];
                            rs = connectionJdbcMB.consult(""
                                    + " SELECT * FROM quadrants WHERE quadrant_id IN "
                                    + " (SELECT quadrant_id FROM neighborhood_quadrant "
                                    + " WHERE neighborhood_id = " + currentNeighborhoodEventCode + ") ");
                            currentQuadrantEvent = -1;
                            int pos = 0;
                            while (rs.next()) {
                                if (currentQuadrantEvent == -1) {
                                    currentQuadrantEvent = rs.getInt("quadrant_id");
                                }
                                quadrantsEvent[pos] = new SelectItem(rs.getInt("quadrant_id"), rs.getString("quadrant_name"));
                                pos++;
                            }
                        }
                    } catch (Exception e) {
                    }
                } else {
                    currentNeighborhoodEvent = "";
                    currentNeighborhoodEventCode = "";
                    currentArea = 0;
                }
            } else {
                currentNeighborhoodEvent = "";
                currentNeighborhoodEventCode = "";
                currentArea = 0;
            }
        } else {
            currentNeighborhoodEvent = "";
            currentNeighborhoodEventCode = "";
            currentArea = 0;
        }
    }

    /**
     * This method enables or disables the types of identification depending on
     * the selected option.
     */
    public void changeMeasuresOfAge() {
        if (loading == false) {
            changeForm();
        }
        currentAge = "";
        if (currentMeasureOfAge == 0 || currentMeasureOfAge == 4) {
            valueAgeDisabled = true;

        } else {
            valueAgeDisabled = false;
        }
    }

    /**
     * This method enables or disables the types of identification depending on
     * the selected option.
     */
    public void changeIdentificationType() {

        if (loading == false) {
            changeForm();
        }

        if (currentIdentificationType == 3 || currentIdentificationType == 2) {//pasaporte
            strangerDisabled = false;
        } else {
            strangerDisabled = true;
        }
        if (currentIdentificationType == 6 || currentIdentificationType == 7 || currentIdentificationType == 0) {
            identificationNumberDisabled = true;
            currentIdentificationNumber = "";
        } else {
            identificationNumberDisabled = false;
            currentIdentificationNumber = "";
        }
    }

    /**
     * This method changes the form according to the modifications that have
     * been made.
     */
    public void changeForm() {//el formulario fue modificado        
        openDialogFirst = "dialogFirst.show();";
        openDialogNext = "dialogNext.show();";
        openDialogLast = "dialogLast.show();";
        openDialogPrevious = "dialogPrevious.show();";
        openDialogNew = "dialogNew.show();";
        openDialogDelete = "dialogDelete.show();";
        save = false;
        stylePosition = "color: red; font-weight: 900;";
    }

    /**
     * This method verifies that the entered date is correct, if it is wrong
     * then display an error message and clean the field.
     */
    public void changeDayEvent() {

        try {
            int dayInt = Integer.parseInt(currentDayEvent);
            if (dayInt < 1 || dayInt > 31) {
                currentDayEvent = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El dia del evento debe ser un número del 1 al 31");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        } catch (Exception e) {
            if (currentDayEvent.length() != 0) {
                currentDayEvent = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El dia del evento debe ser un número del 1 al 31");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
        calculateDate1();

    }

    /**
     * This method verifies that the minutes entered are correct, if not, then
     * display an error message and clean the field.
     */
    public void changeMonthEvent() {
        try {
            int monthInt = Integer.parseInt(currentMonthEvent);
            if (monthInt < 1 || monthInt > 12) {
                currentMonthEvent = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El mes del evento debe ser un número del 1 al 12");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        } catch (Exception e) {
            if (currentMonthEvent.length() != 0) {
                currentMonthEvent = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El mes del evento debe ser un número del 1 al 12");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
        calculateDate1();
    }

    /**
     * This method verifies that the value of age is correct, if not, then
     * display an error message and clean the field.
     */
    public void changeYearEvent() {
        Calendar cal = Calendar.getInstance();
        int yearSystem = cal.get(Calendar.YEAR);
        try {
            int yearInt = Integer.parseInt(currentYearEvent);
            if (yearInt < 2003 || yearInt > yearSystem) {
                currentYearEvent = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El año del evento debe ser un número del 2003 hasta " + String.valueOf(yearSystem));
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }

        } catch (Exception e) {
            if (currentYearEvent.length() != 0) {
                currentYearEvent = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El año del evento debe ser un número del 2003 hasta " + String.valueOf(yearSystem));
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
        calculateDate1();
    }

    /**
     * This method checks that the hour entered is correct, if it is not then
     * display an error message and clean the field.
     */
    public void changeHourEvent() {
        try {
            int hourInt = Integer.parseInt(currentHourEvent);
            if (hourInt < 1 || hourInt > 12) {
                currentHourEvent = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La hora del evento debe ser un número de 1 a 12");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }

        } catch (Exception e) {
            if (currentHourEvent.length() != 0) {
                currentHourEvent = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La hora del evento debe ser un número de 1 a 12");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
        calculateTime1();
    }

    /**
     * This method verifies that the hour AM PM entered is correct, if is NO
     * DATO, then disable the box of event time and the minute event.
     */
    public void changeAmPmEvent() {

        if (loading == false) {
            changeForm();
        }

        try {
            if (currentAmPmEvent.compareTo("AM") == 0 || currentAmPmEvent.compareTo("PM") == 0) {
                currentMinuteEventDisabled = false;
                currentHourEventDisabled = false;
            } else {
                currentMinuteEventDisabled = true;
                currentHourEventDisabled = true;
                currentMinuteEvent = "";
                currentHourEvent = "";
                currentMilitaryHourEvent = "";
            }
        } catch (Exception e) {
            currentMinuteEventDisabled = false;
            currentHourEventDisabled = false;
        }
    }

    /**
     * This method verifies that the minutes entered are correct, if not, then
     * display an error message and clean the field.
     */
    public void changeMinuteEvent() {
        try {
            int minuteInt = Integer.parseInt(currentMinuteEvent);
            if (minuteInt < 0 || minuteInt > 59) {
                currentMinuteEvent = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El minuto del evento debe ser un número de 0 a 59");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }

        } catch (Exception e) {
            if (currentMinuteEvent.length() != 0) {
                currentMinuteEvent = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El minuto del evento debe ser un número de 0 a 59");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
        calculateTime1();
    }

    /**
     * This method verifies that the number of victims entered is correct, if
     * not, then display an error message and clean the field.
     */
    public void changeNumberVictims() {
        //if (loading == false) {             changeForm();         }
        try {
            int numberInt = Integer.parseInt(currentNumberVictims);
            if (numberInt < 1) {
                currentNumberVictims = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El número de victimas debe ser un número, y mayor que cero");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }

        } catch (Exception e) {
            if (currentNumberVictims.length() != 0) {
                currentNumberVictims = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El número de victimas debe ser un número, y mayor que cero.");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }

    /**
     * This method verifies that the value of age is correct, if not, then
     * display an error message and clean the field.
     */
    public void changeValueAge() {
        try {
            int ageInt = Integer.parseInt(currentAge);
            if (ageInt < 1) {
                currentAge = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La edad debe ser un número,y mayor que cero");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }

        } catch (Exception e) {
            if (currentAge.length() != 0) {
                currentAge = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La edad debe ser un número y mayor que cero");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }

    /**
     * This method verifies that the alcohol level is entered correctly, if is
     * wrong then display an error message and clean the field.
     */
    public void changeAlcoholLevelNumber() {
        try {
            int alcoholLevel = Integer.parseInt(currentAlcoholLevel);
            if (alcoholLevel < 0) {
                currentAlcoholLevel = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El nivel de alcohol debe ser un número, mayor o igual a cero");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        } catch (Exception e) {
            if (currentAlcoholLevel.length() != 0) {
                currentAlcoholLevel = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El nivel de alcohol debe ser un número, mayor o igual a cero");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    // FUNCIONES DE CALCULO DE FECHA Y HORA MILITAR ------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------  
    /**
     * gets the name day from the number day
     *
     * @param i
     * @return name day
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
     * calculates the date on which the event occurred
     */
    private void calculateDate1() {
        try {
            fechaI = formato.parse(currentDayEvent + "/" + currentMonthEvent + "/" + currentYearEvent);
            Calendar cal = Calendar.getInstance();
            cal.setTime(fechaI);
            currentDateEvent = formato.format(fechaI);
            currentWeekdayEvent = intToDay(cal.get(Calendar.DAY_OF_WEEK));
        } catch (ParseException ex) {
            currentDateEvent = "";
            currentWeekdayEvent = "";
        }
    }

    /**
     * calculates the time on which the event occurred
     */
    private boolean calculateTime1() {
        int hourInt = 0;
        int minuteInt = 0;
        int timeInt;
        boolean continuar = true;
        try {
            hourInt = Integer.parseInt(currentHourEvent);
        } catch (Exception ex) {
            continuar = false;
            currentMilitaryHourEvent = "";
        }
        try {
            minuteInt = Integer.parseInt(currentMinuteEvent);
        } catch (Exception ex) {
            continuar = false;
            currentMilitaryHourEvent = "";
        }
        if (continuar) {
            try {
                if (currentAmPmEvent.length() != 0) {
                    String hourStr;
                    String minuteStr;
                    String timeStr;
                    if (hourInt > 0 && hourInt < 13 && minuteInt > -1 && minuteInt < 60) {
                        if (currentAmPmEvent.compareTo("PM") == 0) {//hora PM

                            if (hourInt != 12) {
                                hourInt = hourInt + 12;
                            }
                            if (continuar) {
                                hourStr = String.valueOf(hourInt);
                                minuteStr = String.valueOf(minuteInt);
                                if (hourStr.length() == 1) {
                                    hourStr = "0" + hourStr;
                                }
                                if (minuteStr.length() == 1) {
                                    minuteStr = "0" + minuteStr;
                                }
                                timeStr = hourStr + minuteStr;
                                timeInt = Integer.parseInt(timeStr);
                                if (timeInt > 2400) {
                                    timeStr = "00" + minuteStr;
                                }
                                if (timeStr.compareTo("2400") == 0) {
                                    timeStr = "0000";
                                }
                                currentMilitaryHourEvent = timeStr;
                            }
                        } else {//hora AM
                            if (hourInt == 12) {
                                hourInt = hourInt + 12;
                            }
                        }
                        if (continuar) {
                            hourStr = String.valueOf(hourInt);
                            minuteStr = String.valueOf(minuteInt);
                            if (hourStr.length() == 1) {
                                hourStr = "0" + hourStr;
                            }
                            if (minuteStr.length() == 1) {
                                minuteStr = "0" + minuteStr;
                            }
                            timeStr = hourStr + minuteStr;
                            timeInt = Integer.parseInt(timeStr);
                            if (timeInt > 2400) {
                                timeStr = "00" + minuteStr;
                            }
                            if (timeStr.compareTo("2400") == 0) {
                                timeStr = "0000";
                            }
                            currentMilitaryHourEvent = timeStr;
                        }
                    } else {
                        currentMilitaryHourEvent = "";
                        continuar = false;
                    }
                }
            } catch (Exception ex) {
                currentMilitaryHourEvent = "" + ex.toString();
                continuar = false;
            }
        } else {
            currentMilitaryHourEvent = "";
            return false;
        }
        return continuar;
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    // GET Y SET DE VARIABLES ----------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public Short getCurrentIdentificationType() {
        return currentIdentificationType;
    }

    public void setCurrentIdentificationType(Short currentIdentificationType) {
        this.currentIdentificationType = currentIdentificationType;
    }

    public SelectItem[] getIdentificationsTypes() {
        return identificationsTypes;
    }

    public void setIdentificationsTypes(SelectItem[] identificationsTypes) {
        this.identificationsTypes = identificationsTypes;
    }

    public SelectItem[] getMeasuresOfAge() {
        return measuresOfAge;
    }

    public void setMeasuresOfAge(SelectItem[] measuresOfAge) {
        this.measuresOfAge = measuresOfAge;
    }

    public SelectItem[] getGenders() {
        return genders;
    }

    public void setGenders(SelectItem[] genders) {
        this.genders = genders;
    }

    public SelectItem[] getMunicipalities() {
        return municipalities;
    }

    public void setMunicipalities(SelectItem[] municipalities) {
        this.municipalities = municipalities;
    }

    public String getCurrentAmPmEvent() {
        return currentAmPmEvent;
    }

    public void setCurrentAmPmEvent(String currentAmPmEvent) {
        this.currentAmPmEvent = currentAmPmEvent;
        calculateTime1();
    }

    public String getCurrentDateEvent() {
        return currentDateEvent;
    }

    public void setCurrentDateEvent(String currentDateEvent) {
        this.currentDateEvent = currentDateEvent;
    }

    public String getCurrentDayEvent() {
        return currentDayEvent;
    }

    public void setCurrentDayEvent(String currentDayEvent) {
        this.currentDayEvent = currentDayEvent;
        calculateDate1();
    }

    public String getCurrentHourEvent() {
        return currentHourEvent;
    }

    public void setCurrentHourEvent(String currentHourEvent) {
        this.currentHourEvent = currentHourEvent;
        calculateTime1();
    }

    public String getCurrentMilitaryHourEvent() {
        return currentMilitaryHourEvent;
    }

    public void setCurrentMilitaryHourEvent(String currentMilitaryHourEvent) {
        this.currentMilitaryHourEvent = currentMilitaryHourEvent;
    }

    public String getCurrentMinuteEvent() {
        return currentMinuteEvent;
    }

    public void setCurrentMinuteEvent(String currentMinuteEvent) {
        this.currentMinuteEvent = currentMinuteEvent;
        calculateTime1();
    }

    public String getCurrentMonthEvent() {
        return currentMonthEvent;
    }

    public void setCurrentMonthEvent(String currentMonthEvent) {
        this.currentMonthEvent = currentMonthEvent;
        calculateDate1();
    }

    public String getCurrentWeekdayEvent() {
        return currentWeekdayEvent;
    }

    public void setCurrentWeekdayEvent(String currentWeekdayEvent) {
        this.currentWeekdayEvent = currentWeekdayEvent;
    }

    public String getCurrentYearEvent() {
        return currentYearEvent;
    }

    public void setCurrentYearEvent(String currentYearEvent) {
        this.currentYearEvent = currentYearEvent;
        calculateDate1();
    }

    public SelectItem[] getPlaces() {
        return places;
    }

    public void setPlaces(SelectItem[] places) {
        this.places = places;
    }

    public boolean isValueAgeDisabled() {
        return valueAgeDisabled;
    }

    public void setValueAgeDisabled(boolean valueAgeDisabled) {
        this.valueAgeDisabled = valueAgeDisabled;
    }

    public boolean isNeighborhoodHomeNameDisabled() {
        return neighborhoodHomeNameDisabled;
    }

    public void setNeighborhoodHomeNameDisabled(boolean neighborhoodHomeNameDisabled) {
        this.neighborhoodHomeNameDisabled = neighborhoodHomeNameDisabled;
    }

    public Short getCurrentGender() {
        return currentGender;
    }

    public void setCurrentGender(Short currentGender) {
        this.currentGender = currentGender;
    }

    public String getCurrentJob() {
        return currentJob;
    }

    public void setCurrentJob(String currentJob) {
        this.currentJob = currentJob;
    }

    public Short getCurrentMeasureOfAge() {
        return currentMeasureOfAge;
    }

    public void setCurrentMeasureOfAge(Short currentMeasureOfAge) {
        this.currentMeasureOfAge = currentMeasureOfAge;
    }

    public Short getCurrentMunicipalitie() {
        return currentMunicipalitie;
    }

    public void setCurrentMunicipalitie(Short currentMunicipalitie) {
        this.currentMunicipalitie = currentMunicipalitie;
    }

    public String getCurrentNeighborhoodEventCode() {
        return currentNeighborhoodEventCode;
    }

    public void setCurrentNeighborhoodEventCode(String currentNeighborhoodEventCode) {
        this.currentNeighborhoodEventCode = currentNeighborhoodEventCode;
    }

    public String getCurrentNeighborhoodHome() {
        return currentNeighborhoodHome;
    }

    public void setCurrentNeighborhoodHome(String currentNeighborhoodHome) {
        this.currentNeighborhoodHome = currentNeighborhoodHome;
    }

    public String getCurrentNeighborhoodHomeCode() {
        return currentNeighborhoodHomeCode;
    }

    public void setCurrentNeighborhoodHomeCode(String currentNeighborhoodHomeCode) {
        this.currentNeighborhoodHomeCode = currentNeighborhoodHomeCode;
    }

    public String getCurrentNeighborhoodEvent() {
        return currentNeighborhoodEvent;
    }

    public void setCurrentNeighborhoodEvent(String currentNeighborhoodEvent) {
        this.currentNeighborhoodEvent = currentNeighborhoodEvent;
    }

    public Short getCurrentPlace() {
        return currentPlace;
    }

    public void setCurrentPlace(Short currentPlace) {
        this.currentPlace = currentPlace;
    }

    public String getCurrentAge() {
        return currentAge;
    }

    public void setCurrentAge(String currentAge) {
        this.currentAge = currentAge;
    }

    public String getCurrentDirectionEvent() {
        return currentDirectionEvent;
    }

    public void setCurrentDirectionEvent(String currentDirectionEvent) {
        this.currentDirectionEvent = currentDirectionEvent;
    }

    public String getCurrentIdentificationNumber() {
        return currentIdentificationNumber;
    }

    public void setCurrentIdentificationNumber(String currentIdentificationNumber) {
        this.currentIdentificationNumber = currentIdentificationNumber;
    }

    public String getCurrentName() {
        return currentName;
    }

    public void setCurrentName(String currentName) {
        this.currentName = currentName;
    }

    public String getCurrentNumberVictims() {
        return currentNumberVictims;
    }

    public void setCurrentNumberVictims(String currentNumberVictims) {
        this.currentNumberVictims = currentNumberVictims;
    }

    public String getCurrentNarrative() {
        return currentNarrative;
    }

    public void setCurrentNarrative(String currentNarrative) {
        this.currentNarrative = currentNarrative;
    }

    public String getCurrentAlcoholLevel() {
        return currentAlcoholLevel;
    }

    public void setCurrentAlcoholLevel(String currentAlcoholLevel) {
        this.currentAlcoholLevel = currentAlcoholLevel;
    }

    public boolean isIsNegativeAlcoholLevel() {
        return isNegativeAlcoholLevel;
    }

    public void setIsNegativeAlcoholLevel(boolean isNegativeAlcoholLevel) {
        this.isNegativeAlcoholLevel = isNegativeAlcoholLevel;
    }

    public boolean isIsNoDataAlcoholLevel() {
        return isNoDataAlcoholLevel;
    }

    public void setIsNoDataAlcoholLevel(boolean isNoDataAlcoholLevel) {
        this.isNoDataAlcoholLevel = isNoDataAlcoholLevel;
    }

    public boolean isIsPendentAlcoholLevel() {
        return isPendentAlcoholLevel;
    }

    public void setIsPendentAlcoholLevel(boolean isPendentAlcoholLevel) {
        this.isPendentAlcoholLevel = isPendentAlcoholLevel;
    }

    public boolean isIsUnknownAlcoholLevel() {
        return isUnknownAlcoholLevel;
    }

    public void setIsUnknownAlcoholLevel(boolean isUnknownAlcoholLevel) {
        this.isUnknownAlcoholLevel = isUnknownAlcoholLevel;
    }

    public boolean isIsNegativeAlcoholLevelDisabled() {
        return isNegativeAlcoholLevelDisabled;
    }

    public void setIsNegativeAlcoholLevelDisabled(boolean isNegativeAlcoholLevelDisabled) {
        this.isNegativeAlcoholLevelDisabled = isNegativeAlcoholLevelDisabled;
    }

    public boolean isIsNoDataAlcoholLevelDisabled() {
        return isNoDataAlcoholLevelDisabled;
    }

    public void setIsNoDataAlcoholLevelDisabled(boolean isNoDataAlcoholLevelDisabled) {
        this.isNoDataAlcoholLevelDisabled = isNoDataAlcoholLevelDisabled;
    }

    public boolean isIsPendentAlcoholLevelDisabled() {
        return isPendentAlcoholLevelDisabled;
    }

    public void setIsPendentAlcoholLevelDisabled(boolean isPendentAlcoholLevelDisabled) {
        this.isPendentAlcoholLevelDisabled = isPendentAlcoholLevelDisabled;
    }

    public boolean isIsUnknownAlcoholLevelDisabled() {
        return isUnknownAlcoholLevelDisabled;
    }

    public void setIsUnknownAlcoholLevelDisabled(boolean isUnknownAlcoholLevelDisabled) {
        this.isUnknownAlcoholLevelDisabled = isUnknownAlcoholLevelDisabled;
    }

    public boolean isCurrentAlcoholLevelDisabled() {
        return currentAlcoholLevelDisabled;
    }

    public void setCurrentAlcoholLevelDisabled(boolean currentAlcoholLevelDisabled) {
        this.currentAlcoholLevelDisabled = currentAlcoholLevelDisabled;
    }

    public SelectItem[] getAreas() {
        return areas;
    }

    public void setAreas(SelectItem[] areas) {
        this.areas = areas;
    }

    public Short getCurrentArea() {
        return currentArea;
    }

    public void setCurrentArea(Short currentArea) {
        this.currentArea = currentArea;
    }

    public Short getCurrentMurderContext() {
        return currentMurderContext;
    }

    public void setCurrentMurderContext(Short currentMurderContext) {
        this.currentMurderContext = currentMurderContext;
    }

    public Short getCurrentWeaponType() {
        return currentWeaponType;
    }

    public void setCurrentWeaponType(Short currentWeaponType) {
        this.currentWeaponType = currentWeaponType;
    }

    public SelectItem[] getMurderContexts() {
        return murderContexts;
    }

    public void setMurderContexts(SelectItem[] murderContexts) {
        this.murderContexts = murderContexts;
    }

    public SelectItem[] getWeaponTypes() {
        return weaponTypes;
    }

    public void setWeaponTypes(SelectItem[] weaponTypes) {
        this.weaponTypes = weaponTypes;
    }

    public int getTotalRegisters() {
        return totalRegisters;
    }

    public void setTotalRegisters(int totalRegisters) {
        this.totalRegisters = totalRegisters;
    }

    public String getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(String currentPosition) {
        this.currentPosition = currentPosition;
    }

    public String getCurrentCode() {
        return currentCode;
    }

    public void setCurrentCode(String currentCode) {
        this.currentCode = currentCode;
    }

    public String getOpenDialogDelete() {
        return openDialogDelete;
    }

    public void setOpenDialogDelete(String openDialogDelete) {
        this.openDialogDelete = openDialogDelete;
    }

    public String getOpenDialogFirst() {
        return openDialogFirst;
    }

    public void setOpenDialogFirst(String openDialogFirst) {
        this.openDialogFirst = openDialogFirst;
    }

    public String getOpenDialogLast() {
        return openDialogLast;
    }

    public void setOpenDialogLast(String openDialogLast) {
        this.openDialogLast = openDialogLast;
    }

    public String getOpenDialogNew() {
        return openDialogNew;
    }

    public void setOpenDialogNew(String openDialogNew) {
        this.openDialogNew = openDialogNew;
    }

    public String getOpenDialogNext() {
        return openDialogNext;
    }

    public void setOpenDialogNext(String openDialogNext) {
        this.openDialogNext = openDialogNext;
    }

    public String getOpenDialogPrevious() {
        return openDialogPrevious;
    }

    public void setOpenDialogPrevious(String openDialogPrevious) {
        this.openDialogPrevious = openDialogPrevious;
    }

    public Short getCurrentSourceDepartament() {
        return currentSourceDepartament;
    }

    public void setCurrentSourceDepartament(Short currentSourceDepartament) {
        this.currentSourceDepartament = currentSourceDepartament;
    }

    public Short getCurrentSourceMunicipalitie() {
        return currentSourceMunicipalitie;
    }

    public void setCurrentSourceMunicipalitie(Short currentSourceMunicipalitie) {
        this.currentSourceMunicipalitie = currentSourceMunicipalitie;
    }

    public SelectItem[] getSourceDepartaments() {
        return sourceDepartaments;
    }

    public void setSourceDepartaments(SelectItem[] sourceDepartaments) {
        this.sourceDepartaments = sourceDepartaments;
    }

    public SelectItem[] getHomeDepartaments() {
        return homeDepartaments;
    }

    public void setHomeDepartaments(SelectItem[] homeDepartaments) {
        this.homeDepartaments = homeDepartaments;
    }

    public SelectItem[] getSourceMunicipalities() {
        return sourceMunicipalities;
    }

    public void setSourceMunicipalities(SelectItem[] sourceMunicipalities) {
        this.sourceMunicipalities = sourceMunicipalities;
    }

    public boolean isIdentificationNumberDisabled() {
        return identificationNumberDisabled;
    }

    public void setIdentificationNumberDisabled(boolean identificationNumberDisabled) {
        this.identificationNumberDisabled = identificationNumberDisabled;
    }

    public Short getCurrentSourceCountry() {
        return currentSourceCountry;
    }

    public void setCurrentSourceCountry(Short currentSourceCountry) {
        this.currentSourceCountry = currentSourceCountry;
    }

    public SelectItem[] getSourceCountries() {
        return sourceCountries;
    }

    public void setSourceCountries(SelectItem[] sourceCountries) {
        this.sourceCountries = sourceCountries;
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

    public SelectItem[] getSearchCriteriaList() {
        return searchCriteriaList;
    }

    public void setSearchCriteriaList(SelectItem[] searchCriteriaList) {
        this.searchCriteriaList = searchCriteriaList;
    }

    public Date getDate1() {
        return date1;
    }

    public void setDate1(Date date1) {
        this.date1 = date1;
    }

    public Date getDate2() {
        return date2;
    }

    public void setDate2(Date date2) {
        this.date2 = date2;
    }

    public String getStylePosition() {
        return stylePosition;
    }

    public void setStylePosition(String stylePosition) {
        this.stylePosition = stylePosition;
    }

    public boolean isCurrentDayEventDisabled() {
        return currentDayEventDisabled;
    }

    public void setCurrentDayEventDisabled(boolean currentDayEventDisabled) {
        this.currentDayEventDisabled = currentDayEventDisabled;
    }

    public boolean isCurrentMonthEventDisabled() {
        return currentMonthEventDisabled;
    }

    public void setCurrentMonthEventDisabled(boolean currentMonthEventDisabled) {
        this.currentMonthEventDisabled = currentMonthEventDisabled;
    }

    public boolean isCurrentYearEventDisabled() {
        return currentYearEventDisabled;
    }

    public void setCurrentYearEventDisabled(boolean currentYearEventDisabled) {
        this.currentYearEventDisabled = currentYearEventDisabled;
    }

    public boolean isCurrentAmPmEventDisabled() {
        return currentAmPmEventDisabled;
    }

    public void setCurrentAmPmEventDisabled(boolean currentAmPmEventDisabled) {
        this.currentAmPmEventDisabled = currentAmPmEventDisabled;
    }

    public boolean isCurrentHourEventDisabled() {
        return currentHourEventDisabled;
    }

    public void setCurrentHourEventDisabled(boolean currentHourEventDisabled) {
        this.currentHourEventDisabled = currentHourEventDisabled;
    }

    public boolean isCurrentMinuteEventDisabled() {
        return currentMinuteEventDisabled;
    }

    public void setCurrentMinuteEventDisabled(boolean currentMinuteEventDisabled) {
        this.currentMinuteEventDisabled = currentMinuteEventDisabled;
    }

    public boolean isStranger() {
        return stranger;
    }

    public void setStranger(boolean stranger) {
        this.stranger = stranger;
    }

    public boolean isCurrentMunicipalitieDisabled() {
        return currentMunicipalitieDisabled;
    }

    public void setCurrentMunicipalitieDisabled(boolean currentMunicipalitieDisabled) {
        this.currentMunicipalitieDisabled = currentMunicipalitieDisabled;
    }

    public boolean isStrangerDisabled() {
        return strangerDisabled;
    }

    public void setStrangerDisabled(boolean strangerDisabled) {
        this.strangerDisabled = strangerDisabled;
    }

    public String getCurrentIdForm() {
        return currentIdForm;
    }

    public void setCurrentIdForm(String currentIdForm) {
        this.currentIdForm = currentIdForm;
    }

    public Short getCurrentDepartamentHome() {
        return currentDepartamentHome;
    }

    public void setCurrentDepartamentHome(Short currentDepartamentHome) {
        this.currentDepartamentHome = currentDepartamentHome;
    }

    public boolean isCurrentDepartamentHomeDisabled() {
        return currentDepartamentHomeDisabled;
    }

    public void setCurrentDepartamentHomeDisabled(boolean currentDepartamentHomeDisabled) {
        this.currentDepartamentHomeDisabled = currentDepartamentHomeDisabled;
    }

    public int getCurrentTag() {
        return currentTag;
    }

    public void setCurrentTag(int currentTag) {
        this.currentTag = currentTag;
    }

    public SelectItem[] getTags() {
        return tags;
    }

    public void setTags(SelectItem[] tags) {
        this.tags = tags;
    }

    public SelectItem[] getQuadrantsEvent() {
        return quadrantsEvent;
    }

    public void setQuadrantsEvent(SelectItem[] quadrantsEvent) {
        this.quadrantsEvent = quadrantsEvent;
    }

    public int getCurrentQuadrantEvent() {
        return currentQuadrantEvent;
    }

    public void setCurrentQuadrantEvent(int currentQuadrantEvent) {
        this.currentQuadrantEvent = currentQuadrantEvent;
    }
}
