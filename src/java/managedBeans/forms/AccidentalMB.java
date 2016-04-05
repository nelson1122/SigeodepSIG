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
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
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
 * AccidentalMB is responsible to request the user data about the occurrence of
 * events of accident and details of the victim to be processed and recorded in
 * the database.
 *
 */
@ManagedBean(name = "accidentalMB")
@SessionScoped
public class AccidentalMB implements Serializable {
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    // DECLARACION DE VARIABLES --------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------

    @EJB
    QuadrantsFacade quadrantsFacade;
    private SelectItem[] quadrantsEvent;
    private int currentQuadrantEvent = -1;
    //------------------    
    @EJB
    TagsFacade tagsFacade;
    private SelectItem[] tags;
    private int currentTag;
    //-------------------- 
    @EJB
    AccidentMechanismsFacade accidentMechanismsFacade;
    //WeaponTypesFacade weaponTypesFacade;
    //private Short currentWeaponType;
    private Short currentAccidentMechanisms = 0;
    private SelectItem[] accidentMechanisms;
    //--------------------
    @EJB
    AreasFacade areasFacade;
    private SelectItem[] areas;
    private Short currentArea = 0;
    //-------------------- //procedencia     
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
    private Short currentMunicipalitie = 1;//pasto
    private SelectItem[] municipalities;
    private Short currentSourceMunicipalitie = 0;
    private SelectItem[] sourceMunicipalities;
    private boolean currentMunicipalitieDisabled = false;
    //--------------------
    @EJB
    PlacesFacade placesFacade;
    private Short currentPlace = 0;
    private SelectItem[] places;
    //--------------------
    @EJB
    GenNnFacade genNnFacade;
    //--------------------
    @EJB
    GendersFacade gendersFacade;
    private Short currentGender = 0;
    private SelectItem[] genders;
    //--------------------    
    @EJB
    JobsFacade jobsFacade;
    private String currentJob = null;
    //private SelectItem[] jobs;
    //--------------------
    @EJB
    NeighborhoodsFacade neighborhoodsFacade;
    private String currentNeighborhoodHome = "";
    private String currentNeighborhoodHomeCode = "";
    private String currentNeighborhoodEvent = "";
    private String currentNeighborhoodEventCode = "";
    boolean neighborhoodHomeNameDisabled = true;
    //--------------------
    @EJB
    IdTypesFacade idTypesFacade;
    private SelectItem[] identifications;
    private Short currentIdentification = 0;
    //------------------
    @EJB
    AgeTypesFacade ageTypesFacade;
    private SelectItem[] measuresOfAge;
    private Short currentMeasureOfAge = 0;
    private String currentAge = "";
    private boolean valueAgeDisabled = true;
    //------------------
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
    FatalInjuryAccidentFacade fatalInjuryAccidentFacade;
    @EJB
    VictimsFacade victimsFacade;
    @EJB
    FatalInjuriesFacade fatalInjuriesFacade;
    @EJB
    InjuriesFacade injuriesFacade;
    @EJB
    AlcoholLevelsFacade alcoholLevelsFacade;
    private String currentNarrative = "";
    private String currentAlcoholLevel = "";
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
    private String currentDayEvent = "";
    private String currentMonthEvent = "";
    private String currentYearEvent = "";
    private String currentDateEvent = "";
    private String currentWeekdayEvent = "";
    private String currentHourEvent = "";
    private String currentMinuteEvent = "";
    private String currentAmPmEvent = "AM";
    private String currentMilitaryHourEvent = "";
    private String currentName = "";
    private String currentSurame = "";
    private String currentIdentificationNumber = "";
    private String currentDirectionEvent = "";
    private String currentNumberVictims = "1";
    private String currentNumberInjured = "";
    private SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
    private Date fechaI;
    private boolean save = true;//variable que me dice si el registro esta guadado o no   
    private boolean loading = false;//me dice si se esta cargando (para no tener en cuenta los eventos)
    private int currentFatalInjuriId = -1;//registro actual 
    private FatalInjuryAccident currentFatalInjuryAccident;
    private FatalInjuryAccident auxFatalInjuryAccident;
    private ArrayList<String> validationsErrors;
    private String currentCode = "";
    private String currentPosition = "";
    private int totalRegisters = 0;//cantidad total de registros en transito
    private String openDialogFirst = "";
    private String openDialogNext = "";
    private String openDialogLast = "";
    private String openDialogPrevious = "";
    private String openDialogNew = "";
    private String openDialogDelete = "";
    private Calendar c = Calendar.getInstance();
    private String stylePosition = "color: #1471B1;";
    private String currentIdForm = "";
    private Users currentUser;
    private ConnectionJdbcMB connectionJdbcMB;
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
    public AccidentalMB() {
        loginMB = (LoginMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{loginMB}", LoginMB.class);
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
        applicationControlMB = (ApplicationControlMB) FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get("applicationControlMB");
    }

    /**
     * This method is responsible to load the information corresponding to a
     * victim within the form.
     *
     * @param tagsList
     * @param currentFatalInjuryA
     */
    public void loadValues(List<Tags> tagsList, FatalInjuryAccident currentFatalInjuryA) {
        for (int i = 0; i < tagsList.size(); i++) {
            try {
                reset();
                clearForm();
                currentTag = tagsList.get(i).getTagId();
                this.currentFatalInjuryAccident = currentFatalInjuryA;
                currentFatalInjuriId = currentFatalInjuryA.getFatalInjuryId();
                determinePosition();
                loadValues();
            } catch (Exception e) {
                reset();
                noSaveAndGoNew();
            }
        }
    }

    /**
     * This method is responsible for reset all form fields, also this method
     * load the default values for that the user can to register data of a
     * victim.
     */
    public void reset() {
        currentUser = loginMB.getCurrentUser();
        currentYearEvent = Integer.toString(c.get(Calendar.YEAR));
        quadrantsEvent = new SelectItem[1];
        quadrantsEvent[0] = new SelectItem(0, "SIN DATO");
        currentQuadrantEvent = 0;
        loading = true;
        try {
            //cargo los conjuntos de registros
            List<Tags> tagsList = tagsFacade.findAll();
            int count = 0;
            for (int i = 0; i < tagsList.size(); i++) {
                if (tagsList.get(i).getFormId().getFormId().compareTo("SCC-F-031") == 0) {
                    count++;
                }
            }
            tags = new SelectItem[count];
            count = 0;
            currentTag = 0;
            for (int i = 0; i < tagsList.size(); i++) {
                if (tagsList.get(i).getFormId().getFormId().compareTo("SCC-F-031") == 0) {
                    if (currentTag == 0) {
                        currentTag = tagsList.get(i).getTagId();
                    }
                    tags[count] = new SelectItem(tagsList.get(i).getTagId(), tagsList.get(i).getTagName());
                    count++;
                }
            }
            //cargo los tipos de identificacion
            List<IdTypes> idTypesList = idTypesFacade.findAll();
            identifications = new SelectItem[idTypesList.size() + 1];
            identifications[0] = new SelectItem(0, "");
            for (int i = 0; i < idTypesList.size(); i++) {
                identifications[i + 1] = new SelectItem(idTypesList.get(i).getTypeId(), idTypesList.get(i).getTypeName());
            }
            //cargo las medidas de edad
            List<AgeTypes> ageTypesList = ageTypesFacade.findAll();
            measuresOfAge = new SelectItem[ageTypesList.size() + 1];
            measuresOfAge[0] = new SelectItem(0, "");
            for (int i = 0; i < ageTypesList.size(); i++) {
                measuresOfAge[i + 1] = new SelectItem(ageTypesList.get(i).getAgeTypeId(), ageTypesList.get(i).getAgeTypeName());
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
            //cargo los lugares donde ocurrieron los hechos
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
            List<AccidentMechanisms> accidentMechanismsList = accidentMechanismsFacade.findAll();
            accidentMechanisms = new SelectItem[accidentMechanismsList.size() + 1];
            accidentMechanisms[0] = new SelectItem(0, "");
            for (int i = 0; i < accidentMechanismsList.size(); i++) {
                accidentMechanisms[i + 1] = new SelectItem(accidentMechanismsList.get(i).getAccidentMechanismId(), accidentMechanismsList.get(i).getAccidentMechanismName());
            }
            //lista de criterios de busqueda            
            searchCriteriaList = new SelectItem[3];
            searchCriteriaList[0] = new SelectItem(1, "IDENTIFICACION");
            searchCriteriaList[1] = new SelectItem(2, "NOMBRE");
            searchCriteriaList[2] = new SelectItem(3, "CODIGO INTERNO");

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
            neighborhoodHomeNameDisabled = false;

        } catch (Exception e) {
            System.out.println("Error 1 en " + this.getClass().getName() + ":" + e.toString());
        }
        loading = false;
    }

    /**
     * This method changes the records set.
     */
    public void changeTag() {//cambia el conjunto de registros
        noSaveAndGoNew();
    }

    /**
     * This method is responsible to load the information corresponding to a
     * victim within the form.
     */
    public void loadValues() {
        save = true;
        stylePosition = "color: #1471B1;";
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
            stranger = currentFatalInjuryAccident.getFatalInjuries().getVictimId().getStranger();
        } catch (Exception e) {
            stranger = false;
        }
        changeStranger();
        //******type_id
        try {
            currentIdentification = currentFatalInjuryAccident.getFatalInjuries().getVictimId().getTypeId().getTypeId();
        } catch (Exception e) {
            currentIdentification = 0;
        }
        changeIdentificationType();
        //******victim_nid
        try {
            currentIdentificationNumber = currentFatalInjuryAccident.getFatalInjuries().getVictimId().getVictimNid();
            if (currentIdentification == 6 || currentIdentification == 7 || currentIdentification == 0) {
                identificationNumberDisabled = true;
                currentIdentificationNumber = "";
            } else {
                identificationNumberDisabled = false;
            }
        } catch (Exception e) {
            identificationNumberDisabled = true;
            currentIdentificationNumber = "";
        }
        //******victim_firstname
        currentName = currentFatalInjuryAccident.getFatalInjuries().getVictimId().getVictimName();
        if (currentName == null) {
            currentName = "";
        }
        //******age_type_id
        try {
            currentMeasureOfAge = currentFatalInjuryAccident.getFatalInjuries().getVictimId().getAgeTypeId();
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
            currentAge = currentFatalInjuryAccident.getFatalInjuries().getVictimId().getVictimAge().toString();
        } catch (Exception e) {
            currentAge = "";
        }
        //******gender_id
        try {
            currentGender = currentFatalInjuryAccident.getFatalInjuries().getVictimId().getGenderId().getGenderId();
        } catch (Exception e) {
            currentGender = 0;
        }
        //******job_id
        try {
            currentJob = currentFatalInjuryAccident.getFatalInjuries().getVictimId().getJobId().getJobName();
        } catch (Exception e) {
            currentJob = null;
        }
        //******vulnerable_group_id
        //******ethnic_group_id
        //******victim_telephone
        //******victim_address
        //******victim_neighborhood_id
        try {
            if (currentFatalInjuryAccident.getFatalInjuries().getVictimId().getVictimNeighborhoodId().getNeighborhoodId() != null) {
                currentNeighborhoodHomeCode = String.valueOf(currentFatalInjuryAccident.getFatalInjuries().getVictimId().getVictimNeighborhoodId().getNeighborhoodId());
                currentNeighborhoodHome = neighborhoodsFacade.find(currentFatalInjuryAccident.getFatalInjuries().getVictimId().getVictimNeighborhoodId().getNeighborhoodId()).getNeighborhoodName();
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
            if (currentFatalInjuryAccident.getFatalInjuries().getVictimId().getResidenceDepartment() != null) {
                currentDepartamentHome = currentFatalInjuryAccident.getFatalInjuries().getVictimId().getResidenceDepartment();
                changeDepartamentHome();
                if (currentFatalInjuryAccident.getFatalInjuries().getVictimId().getResidenceMunicipality() != null) {
                    currentMunicipalitie = currentFatalInjuryAccident.getFatalInjuries().getVictimId().getResidenceMunicipality();
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
            currentDateEvent = currentFatalInjuryAccident.getFatalInjuries().getInjuryDate().toString();
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentFatalInjuryAccident.getFatalInjuries().getInjuryDate());
            currentDayEvent = String.valueOf(cal.get(Calendar.DATE));
            currentMonthEvent = String.valueOf(cal.get(Calendar.MONTH) + 1);
            currentYearEvent = String.valueOf(cal.get(Calendar.YEAR));
            calculateDate1();
        } catch (Exception e) {
            currentDateEvent = "";
        }
        //******injury_time
        try {
            if (currentFatalInjuryAccident.getFatalInjuries().getInjuryTime().getHours() == 0) {
                currentHourEvent = "12";
                currentAmPmEvent = "AM";
            } else {
                currentHourEvent = String.valueOf(currentFatalInjuryAccident.getFatalInjuries().getInjuryTime().getHours());
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
            currentMinuteEvent = String.valueOf(currentFatalInjuryAccident.getFatalInjuries().getInjuryTime().getMinutes());

            calculateTime1();
        } catch (Exception e) {
            currentHourEvent = "";
            currentMinuteEvent = "";
            currentAmPmEvent = "SIN DATO";
            changeAmPmEvent();
        }
        //******injury_address
        currentDirectionEvent = currentFatalInjuryAccident.getFatalInjuries().getInjuryAddress();
        if (currentDirectionEvent == null) {
            currentDirectionEvent = "";
        }
        //******injury_neighborhood_id
        try {
            if (currentFatalInjuryAccident.getFatalInjuries().getInjuryNeighborhoodId() != null) {
                currentNeighborhoodEventCode = String.valueOf(currentFatalInjuryAccident.getFatalInjuries().getInjuryNeighborhoodId());
                currentNeighborhoodEvent = neighborhoodsFacade.find(currentFatalInjuryAccident.getFatalInjuries().getInjuryNeighborhoodId()).getNeighborhoodName();
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
                            if (currentQuadrantEvent == -1 && currentFatalInjuryAccident.getFatalInjuries().getQuadrantId() != null) {
                                currentQuadrantEvent = currentFatalInjuryAccident.getFatalInjuries().getQuadrantId().getQuadrantId();
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
            currentPlace = currentFatalInjuryAccident.getFatalInjuries().getInjuryPlaceId().getPlaceId();
        } catch (Exception e) {
            currentPlace = 0;
        }
        //******victim_number
        if (currentFatalInjuryAccident.getFatalInjuries().getVictimNumber() != null) {
            currentNumberVictims = String.valueOf(currentFatalInjuryAccident.getFatalInjuries().getVictimNumber());
        } else {
            currentNumberVictims = "";
        }

        //******injury_description
        currentNarrative = currentFatalInjuryAccident.getFatalInjuries().getInjuryDescription();
        if (currentNarrative == null) {
            currentNarrative = "";
        }
        //******user_id	
        //******input_timestamp	
        //******injury_day_of_week
        currentWeekdayEvent = currentFatalInjuryAccident.getFatalInjuries().getInjuryDayOfWeek();
        if (currentWeekdayEvent == null) {
            currentWeekdayEvent = "";
        }
        //******victim_id
        //******fatal_injury_id
        //******alcohol_level_victim_id, alcohol_level_victim
        if (currentFatalInjuryAccident.getFatalInjuries().getAlcoholLevelVictim() != null) {
            isNoDataAlcoholLevelDisabled = false;
            isUnknownAlcoholLevelDisabled = false;
            isPendentAlcoholLevelDisabled = false;
            isNegativeAlcoholLevelDisabled = false;
            currentAlcoholLevelDisabled = false;
            currentAlcoholLevel = String.valueOf(currentFatalInjuryAccident.getFatalInjuries().getAlcoholLevelVictim());
            isNoDataAlcoholLevel = false;
            isUnknownAlcoholLevel = false;
            isPendentAlcoholLevel = false;
            isNegativeAlcoholLevel = false;
        } else {
            try {
                Short level = currentFatalInjuryAccident.getFatalInjuries().getAlcoholLevelVictimId().getAlcoholLevelId();
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
        currentCode = currentFatalInjuryAccident.getFatalInjuries().getCode();
        if (currentCode == null) {
            currentWeekdayEvent = "";
        }
        //******area_id
        try {
            currentArea = currentFatalInjuryAccident.getFatalInjuries().getAreaId().getAreaId();
        } catch (Exception e) {
            currentArea = 0;
        }
        //******victim_place_of_origin
        try {
            if (currentFatalInjuryAccident.getFatalInjuries().getVictimPlaceOfOrigin() != null) {
                String source = currentFatalInjuryAccident.getFatalInjuries().getVictimPlaceOfOrigin();
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
        //SE CARGA DATOS PARA LA NUEVA LESION FATAL POR MUERTE ACCIDENTAL
        //------------------------------------------------------------
        //******number_non_fatal_victims
        if (currentFatalInjuryAccident.getNumberNonFatalVictims() != null) {
            currentNumberInjured = String.valueOf(currentFatalInjuryAccident.getNumberNonFatalVictims());
        } else {
            currentNumberInjured = "";
        }
        //******death_mechanism_id
        if (currentFatalInjuryAccident.getDeathMechanismId() != null) {
            currentAccidentMechanisms = currentFatalInjuryAccident.getDeathMechanismId().getAccidentMechanismId();
        }
        //******fatal_injury_id
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
            if (!loginMB.isPermissionAdministrator() && loginMB.getCurrentUser().getUserId() != currentFatalInjuryAccident.getFatalInjuries().getUserId().getUserId()) {
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
                if (currentIdentification != 0) {
                    newVictim.setTypeId(idTypesFacade.find(currentIdentification));
                }
                //******stranger                
                newVictim.setStranger(stranger);
                //******victim_nid
                if (currentIdentificationNumber.trim().length() != 0) {
                    newVictim.setVictimNid(currentIdentificationNumber);
                }
                //******victim_firstname
                if (currentName.trim().length() != 0) {
                    newVictim.setVictimName(currentName);
                }
//                //******victim_firstname
//                if (currentName.trim().length() != 0) {
//                    newVictim.setVictimFirstname(currentName);
//                }
//                //******victim_lastname
//                if (currentSurname.trim().length() != 0) {
//                    newVictim.setVictimLastname(currentSurname);
//                }
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
                newFatalInjurie.setInjuryId(injuriesFacade.find((short) 13));//es 13 por ser muerte accidental
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
                //****quadrant_id
                if (currentQuadrantEvent != -1) {
                    newFatalInjurie.setQuadrantId(quadrantsFacade.find(currentQuadrantEvent));
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
                try {
                    newFatalInjurie.setUserId(currentUser);//usuario que se encuentre logueado
                } catch (Exception e) {
                }
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
                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 1));//con dato
                } else {
                    if (isNoDataAlcoholLevel) {
                        newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 2));//sin dato
                    }
                    if (isUnknownAlcoholLevel) {
                        newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 3));//no suministrado
                    }
                    if (isPendentAlcoholLevel) {
                        newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 4));//pendiente
                    }
                    if (isNegativeAlcoholLevel) {
                        newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 5));//negativo
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
                //******victim_place_of_origin
                if (currentSourceCountry != 0) {
                    String source = String.valueOf(currentSourceCountry);
                    source = source + "-" + String.valueOf(currentSourceDepartament);
                    source = source + "-" + String.valueOf(currentSourceMunicipalitie);
                    newFatalInjurie.setVictimPlaceOfOrigin(source);
                }
                //------------------------------------------------------------
                //SE CREA VARIABLE PARA LA NUEVA LESION FATAL POR ACCIDENTE
                //------------------------------------------------------------
                FatalInjuryAccident newFatalInjuryAccident = new FatalInjuryAccident();
                //******number_non_fatal_victims
                if (currentNumberInjured.trim().length() != 0) {
                    newFatalInjuryAccident.setNumberNonFatalVictims(Short.parseShort(currentNumberInjured));
                }
                //******death_mechanism_id
                if (currentAccidentMechanisms != 0) {
                    newFatalInjuryAccident.setDeathMechanismId(accidentMechanismsFacade.find(currentAccidentMechanisms));
                }
                //******fatal_injury_id
                newFatalInjuryAccident.setFatalInjuryId(newFatalInjurie.getFatalInjuryId());

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

                //-------------------------------------------------------------------------------
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
                    fatalInjuryAccidentFacade.create(newFatalInjuryAccident);

                    save = true;
                    stylePosition = "color: #1471B1;";
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "NUEVO REGISTRO ALMACENADO");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                } else {//ES UN REGISTRO EXISTENTE SE DEBE ACTUALIZAR                    
                    updateRegistry(newVictim, newFatalInjurie, newFatalInjuryAccident);
                    save = true;
                    stylePosition = "color: #1471B1;";
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "REGISTRO ACTUALIZADO");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
                applicationControlMB.removeFatalReservedIdentifiers(newFatalInjurie.getFatalInjuryId());
                applicationControlMB.removeVictimsReservedIdentifiers(newVictim.getVictimId());
                return true;
            } catch (NumberFormatException | ParseException e) {
                System.out.println("Error 2 en " + this.getClass().getName() + ":" + e.toString());
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.toString());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * updated data of a victim who is already registered
     *
     * @param victim
     * @param fatalInjurie
     * @param fatalInjuryAccident
     */
    private void updateRegistry(Victims victim, FatalInjuries fatalInjurie, FatalInjuryAccident fatalInjuryAccident) {
        try {
            //------------------------------------------------------------
            //DATOS VICTIMA
            //------------------------------------------------------------
            currentFatalInjuryAccident.getFatalInjuries().getVictimId().setTypeId(victim.getTypeId());
            currentFatalInjuryAccident.getFatalInjuries().getVictimId().setVictimNid(victim.getVictimNid());

            currentFatalInjuryAccident.getFatalInjuries().getVictimId().setVictimName(victim.getVictimName());
            currentFatalInjuryAccident.getFatalInjuries().getVictimId().setAgeTypeId(victim.getAgeTypeId());
            currentFatalInjuryAccident.getFatalInjuries().getVictimId().setVictimAge(victim.getVictimAge());
            currentFatalInjuryAccident.getFatalInjuries().getVictimId().setGenderId(victim.getGenderId());
            currentFatalInjuryAccident.getFatalInjuries().getVictimId().setJobId(victim.getJobId());

            currentFatalInjuryAccident.getFatalInjuries().getVictimId().setVictimAddress(victim.getVictimAddress());
            currentFatalInjuryAccident.getFatalInjuries().getVictimId().setVictimNeighborhoodId(victim.getVictimNeighborhoodId());
            currentFatalInjuryAccident.getFatalInjuries().getVictimId().setResidenceMunicipality(victim.getResidenceMunicipality());
            currentFatalInjuryAccident.getFatalInjuries().getVictimId().setResidenceDepartment(victim.getResidenceDepartment());

            //------------------------------------------------------------
            //DATOS LESION DE CAUSA EXTERNA FATAL
            //------------------------------------------------------------

            currentFatalInjuryAccident.getFatalInjuries().setInjuryDate(fatalInjurie.getInjuryDate());
            currentFatalInjuryAccident.getFatalInjuries().setInjuryTime(fatalInjurie.getInjuryTime());
            currentFatalInjuryAccident.getFatalInjuries().setInjuryAddress(fatalInjurie.getInjuryAddress());
            currentFatalInjuryAccident.getFatalInjuries().setInjuryNeighborhoodId(fatalInjurie.getInjuryNeighborhoodId());
            currentFatalInjuryAccident.getFatalInjuries().setQuadrantId(fatalInjurie.getQuadrantId());
            currentFatalInjuryAccident.getFatalInjuries().setInjuryPlaceId(fatalInjurie.getInjuryPlaceId());
            currentFatalInjuryAccident.getFatalInjuries().setVictimNumber(fatalInjurie.getVictimNumber());
            currentFatalInjuryAccident.getFatalInjuries().setInjuryDescription(fatalInjurie.getInjuryDescription());

            currentFatalInjuryAccident.getFatalInjuries().setInjuryDayOfWeek(fatalInjurie.getInjuryDayOfWeek());
            currentFatalInjuryAccident.getFatalInjuries().setAlcoholLevelVictim(fatalInjurie.getAlcoholLevelVictim());
            currentFatalInjuryAccident.getFatalInjuries().setAlcoholLevelVictimId(fatalInjurie.getAlcoholLevelVictimId());
            currentFatalInjuryAccident.getFatalInjuries().setCode(fatalInjurie.getCode());
            currentFatalInjuryAccident.getFatalInjuries().setAreaId(fatalInjurie.getAreaId());
            currentFatalInjuryAccident.getFatalInjuries().setVictimPlaceOfOrigin(fatalInjurie.getVictimPlaceOfOrigin());
            //------------------------------------------------------------
            //DATOS LESION FATAL POR ACCIDENTE
            //------------------------------------------------------------
            currentFatalInjuryAccident.setNumberNonFatalVictims(fatalInjuryAccident.getNumberNonFatalVictims());
            currentFatalInjuryAccident.setDeathMechanismId(fatalInjuryAccident.getDeathMechanismId());

            victimsFacade.edit(currentFatalInjuryAccident.getFatalInjuries().getVictimId());
            fatalInjuriesFacade.edit(currentFatalInjuryAccident.getFatalInjuries());
            fatalInjuryAccidentFacade.edit(currentFatalInjuryAccident);
            //System.out.println("registro actualizado");

        } catch (Exception e) {
            System.out.println("Error 3 en " + this.getClass().getName() + ":" + e.toString());
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    /**
     * This method determines the position of the data of a victim to be loaded
     * into the form, this method is used with the following function: next,
     * previous, first, last, and when the form is reset.
     */
    public void determinePosition() {
        totalRegisters = fatalInjuryAccidentFacade.countAccidental(currentTag);
        if (currentFatalInjuriId == -1) {
            currentPosition = "new" + "/" + String.valueOf(totalRegisters);
            currentIdForm = String.valueOf(fatalInjuriesFacade.findMax() + 1);
            openDialogDelete = "";//es nuevo no se puede borrar
        } else {
            int position = fatalInjuryAccidentFacade.findPosition(currentFatalInjuryAccident.getFatalInjuryId(), currentTag);
            currentIdForm = String.valueOf(currentFatalInjuryAccident.getFatalInjuryId());
            currentPosition = position + "/" + String.valueOf(totalRegisters);
            openDialogDelete = "dialogDelete.show();";
        }
        //System.out.println("POSICION DETERMINADA: " + currentPosition);
    }

    /**
     * This method save all changes realized to a victim and proceeds to the
     * next form.
     */
    public void saveAndGoNext() {//guarda cambios si se han realizado y se dirije al siguiente
        if (saveRegistry()) {
            next();
        } else {
            //System.out.println("No se guardo");
        }
    }

    /**
     * This method save all changes realized to a victim and proceeds to the
     * previous form.
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
     * first form.
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
     * Discards all changes realized to a victim and proceeds to the next form.
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
     * Discards all changes realized to a victim and proceeds to the previous
     * form.
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
     * Discards all changes realized to a victim and proceeds to the last form.
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
                auxFatalInjuryAccident = fatalInjuryAccidentFacade.findNext(currentFatalInjuriId, currentTag);
                if (auxFatalInjuryAccident != null) {
                    clearForm();
                    currentFatalInjuryAccident = auxFatalInjuryAccident;
                    currentFatalInjuriId = currentFatalInjuryAccident.getFatalInjuryId();
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
                auxFatalInjuryAccident = fatalInjuryAccidentFacade.findPrevious(currentFatalInjuriId, currentTag);
                if (auxFatalInjuryAccident != null) {
                    clearForm();
                    currentFatalInjuryAccident = auxFatalInjuryAccident;
                    currentFatalInjuriId = currentFatalInjuryAccident.getFatalInjuryId();
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
            auxFatalInjuryAccident = fatalInjuryAccidentFacade.findFirst(currentTag);
            if (auxFatalInjuryAccident != null) {
                clearForm();
                currentFatalInjuryAccident = auxFatalInjuryAccident;
                currentFatalInjuriId = currentFatalInjuryAccident.getFatalInjuryId();
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
            auxFatalInjuryAccident = fatalInjuryAccidentFacade.findLast(currentTag);
            if (auxFatalInjuryAccident != null) {
                clearForm();
                currentFatalInjuryAccident = auxFatalInjuryAccident;
                currentFatalInjuriId = currentFatalInjuryAccident.getFatalInjuryId();
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
        currentMinuteEventDisabled = false;
        currentHourEventDisabled = false;
        currentAmPmEventDisabled = false;


        loading = true;

        strangerDisabled = true;
        stranger = false;
        //------------------------------------------------------------
        //REINICIAR VALORES PARA LA NUEVA VICTIMA
        //------------------------------------------------------------	        
        currentIdentification = 0;
        currentIdentificationNumber = "";
        currentName = "";
        //currentSurname = "";
        valueAgeDisabled = true;
        currentMeasureOfAge = 0;
        currentAge = "";
        currentGender = 0;
        currentJob = null;
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
        //currentMunicipalitie = 1;

        currentDepartamentHomeDisabled = false;
        currentDepartamentHome = 52;
        changeDepartamentHome();
        currentMunicipalitie = 1;


        currentMunicipalitieDisabled = false;
        neighborhoodHomeNameDisabled = false;
        currentPlace = 0;
        currentNumberVictims = "1";
        currentNumberInjured = "";
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

        currentAccidentMechanisms = 0;
        currentArea = 0;

        loading = false;
    }

    /**
     * This method displays a blank form for the user to enter data about a
     * victim, if fields without saving then this method displays a dialog that
     * allows the user to save the changes.
     */
    public void newForm() {
        //currentFatalInjuryTraffic = null;
        if (save) {
            clearForm();
            currentFatalInjuriId = -1;
            determinePosition();
        } else {
            //System.out.println("No esta guardado (para poder limpiar formulario)");
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
            if (!loginMB.isPermissionAdministrator() && loginMB.getCurrentUser().getUserId() != currentFatalInjuryAccident.getFatalInjuries().getUserId().getUserId()) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Este registro solo puede ser modificado por un administrador o por el usuario que creo el registro");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                FatalInjuries auxFatalInjuries = currentFatalInjuryAccident.getFatalInjuries();
                Victims auxVictims = currentFatalInjuryAccident.getFatalInjuries().getVictimId();
                fatalInjuryAccidentFacade.remove(currentFatalInjuryAccident);
                fatalInjuriesFacade.remove(auxFatalInjuries);
                victimsFacade.remove(auxVictims);
                //System.out.println("registro eliminado");
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha eliminado el registro");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                noSaveAndGoNew();
            }
        }
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

    public List<RowDataTable> getRowDataTableList() {
        return rowDataTableList;
    }

    public void setRowDataTableList(List<RowDataTable> rowDataTableList) {
        this.rowDataTableList = rowDataTableList;
    }

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
            //auxFatalInjuryAccident = fatalInjuryAccidentFacade.findByIdVictim(selectedRowDataTable.getColumn1());
            auxFatalInjuryAccident = fatalInjuryAccidentFacade.find(Integer.parseInt(selectedRowDataTable.getColumn1()));
            if (auxFatalInjuryAccident != null) {
                clearForm();
                currentFatalInjuryAccident = auxFatalInjuryAccident;
                currentFatalInjuriId = currentFatalInjuryAccident.getFatalInjuryId();
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
     * This method creates a Dinamic Table to display the fields with the
     * specified values of a search realized.
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
                sql = sql + "fatal_injuries.injury_id = 13";
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
     * This method is responsible to display a neighborhoods list that have a
     * similar name to which the user is typing.
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
    // FUNCIONES CUANDO LISTAS Y CAMPOS CAMBIAN DE VALOR ----------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------

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
            neighborhoodHomeNameDisabled = true;
            currentNeighborhoodHome = "";
            currentNeighborhoodHomeCode = "";
        }
    }

    /**
     *
     */
    public void findMunicipalitieCode() {
        //Municipalities m = municipalitiesFacade.findById(currentMunicipalitie, currentDepartamentHome);
        if (currentMunicipalitie == 1) {
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
     * This method is responsible to show all Neighborhoods having a similar
     * name to which the user is writing.
     */
    public void changeNeighborhoodHomeName() {
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
     * CUADRANTE and AREA DEL HECHO when a neighborhood is selected by the user
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
     * This method enables or disables the measure of age according to the
     * selected option.
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

        if (currentIdentification == 3 || currentIdentification == 2) {//pasaporte
            strangerDisabled = false;
        } else {
            strangerDisabled = true;
        }
        if (currentIdentification == 6 || currentIdentification == 7 || currentIdentification == 0) {
            identificationNumberDisabled = true;
            currentIdentificationNumber = "";
        } else {
            identificationNumberDisabled = false;
            currentIdentificationNumber = "";
        }
    }

    /**
     * This method changes the form according to the modifications that the user
     * is realizing.
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
     * This method checks that the month entered is correct, if not, then
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
     * This method verifies that the year of the event entered is correct, if
     * not, then display a message and clean the field.
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
     * This method verifies that the number of injured entered is correct, if
     * not, then display an error message and clean the field.
     */
    public void changeNumberInjured() {
        try {
            int numberInt = Integer.parseInt(currentNumberInjured);
            if (numberInt < -1) {
                currentNumberInjured = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El número de lesionados debe ser un número, y mayor o igual a cero");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        } catch (Exception e) {
            if (currentNumberInjured.length() != 0) {
                currentNumberInjured = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El número de lesionados debe ser un número, y mayor o igual a cero");
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

    public SelectItem[] getIdentifications() {
        return identifications;
    }

    public void setIdentifications(SelectItem[] identifications) {
        this.identifications = identifications;
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

//    public SelectItem[] getJobs() {
//        return jobs;
//    }
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

    public Short getCurrentIdentification() {
        return currentIdentification;
    }

    public void setCurrentIdentification(Short currentIdentification) {
        this.currentIdentification = currentIdentification;
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

    public String getCurrentSurame() {
        return currentSurame;
    }

    public void setCurrentSurame(String currentSurame) {
        this.currentSurame = currentSurame;
    }

//    public String getCurrentSurname() {
//        return currentSurname;
//    }
//
//    public void setCurrentSurname(String currentSurname) {
//        this.currentSurname = currentSurname;
//    }
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

    public String getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(String currentPosition) {
        this.currentPosition = currentPosition;
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

    public int getTotalRegisters() {
        return totalRegisters;
    }

    public void setTotalRegisters(int totalRegisters) {
        this.totalRegisters = totalRegisters;
    }

    public String getCurrentCode() {
        return currentCode;
    }

    public void setCurrentCode(String currentCode) {
        this.currentCode = currentCode;
    }

    public String getCurrentNumberInjured() {
        return currentNumberInjured;
    }

    public void setCurrentNumberInjured(String currentNumberInjured) {
        this.currentNumberInjured = currentNumberInjured;
    }

    public SelectItem[] getAccidentMechanisms() {
        return accidentMechanisms;
    }

    public void setAccidentMechanisms(SelectItem[] accidentMechanisms) {
        this.accidentMechanisms = accidentMechanisms;
    }

    public Short getCurrentAccidentMechanisms() {
        return currentAccidentMechanisms;
    }

    public void setCurrentAccidentMechanisms(Short currentAccidentMechanisms) {
        this.currentAccidentMechanisms = currentAccidentMechanisms;
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

    public SelectItem[] getHomeDepartaments() {
        return homeDepartaments;
    }

    public void setHomeDepartaments(SelectItem[] homeDepartaments) {
        this.homeDepartaments = homeDepartaments;
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
