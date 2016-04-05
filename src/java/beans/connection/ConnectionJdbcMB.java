/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.connection;

import beans.util.RowDataTable;
import java.io.Serializable;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.sql.DataSource;
import model.dao.*;
import model.pojo.*;

/**
 * This class is responsible for the management connection to the database, also
 * this class is responsible to make the necessary queries for the system to
 * function properly.
 *
 * @author SANTOS
 */
@ManagedBean(name = "connectionJdbcMB")
@SessionScoped
/**
 *
 */
public class ConnectionJdbcMB implements Serializable {

//    @Resource(name = "jdbc/od")
//    private DataSource ds;
    @EJB
    ConfigurationsFacade configurationsFacade;
    @EJB
    NonFatalDomesticViolenceFacade nonFatalDomesticViolenceFacade;
    @EJB
    CountriesFacade countriesFacade;
    @EJB
    AgeTypesFacade ageTypesFacade;
    @EJB
    MunicipalitiesFacade municipalitiesFacade;
    @EJB
    DepartamentsFacade departamentsFacade;
    @EJB
    FatalInjuryMurderFacade fatalInjuryMurderFacade;
    @EJB
    FatalInjuryAccidentFacade fatalInjuryAccidentFacade;
    @EJB
    FatalInjurySuicideFacade fatalInjurySuicideFacade;
    @EJB
    FatalInjuryTrafficFacade fatalInjuryTrafficFacade;
    @EJB
    InjuriesFacade injuriesFacade;
    @EJB
    NeighborhoodsFacade neighborhoodsFacade;
    @EJB
    NonFatalInjuriesFacade nonFatalInjuriesFacade;
    private String hours = "";
    private String minutes = "";
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy hh:mm");
    public Connection conn;
    private String tableName = "temp";//nombre de la tabla temp para carga masiva
    private Statement st;
    private ResultSet rs;
    private String msj;
    private String user;
    private String db;
    private String db_dwh;
    private String password;
    private String server;
    private String url = "";
    private boolean connectionIsConfigured = true;
    private boolean connectionNotConfigured = false;
    private Users currentUser;
    private boolean showMessages = true;//determinar si mostrar o no los mensajes de error

    @PostConstruct
    private void event() {
        Configurations dbConfiguration = configurationsFacade.findAll().get(0);
        user = dbConfiguration.getUserDb();
        db = dbConfiguration.getNameDb();
        db_dwh = dbConfiguration.getNameDbDwh();
        password = dbConfiguration.getPasswordDb();
        server = dbConfiguration.getServerDb();
    }

    /**
     * This method is responsible to creates a new instance of ConnectionJdbcMB.
     */
    public ConnectionJdbcMB() {
    }

    /**
     * This method is responsible for closing the connection established to the
     * database
     */
    @PreDestroy
    public synchronized void disconnect() {
        try {
            if (!conn.isClosed()) {
                conn.close();
                System.out.println("Cerrada conexion a base de datos " + url + " ... OK  " + this.getClass().getName());
            }
        } catch (Exception e) {
            System.out.println("Error al cerrar conexion a base de datos " + url + " " + this.getClass().getName() + " ... " + e.toString());
        }
    }

    /**
     * This method is responsible for checking if the system can to connect to
     * the data warehouse and database, and stores this data in the table
     * settings
     *
     * @return
     */
    public String checkConnection() {
        /*
         * verifica si se puede conectar a la bodega y db observatorio y almacena
         * estos datos en la tabla configuraciones
         * si se conecta retorna "index" que es la pagina de inicio de la aplicacion
         */
        boolean continueProcess = true;
        try {
            if (continueProcess) {
                try {
                    Class.forName("org.postgresql.Driver").newInstance();// seleccionar SGBD
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    msj = e.toString() + " --- Clase: " + this.getClass().getName();
                    continueProcess = false;
                }
            }
            if (continueProcess) {

                boolean correctConnection = true;

                try {
                    url = "jdbc:postgresql://" + server + "/" + db_dwh;
                    conn = DriverManager.getConnection(url, user, password);//conectarse a bodega de datos                
                    if (conn != null && !conn.isClosed()) {
                        msj = " Conexión a bodega de datos Correcta.";
                        if (!conn.isClosed()) {
                            conn.close();
                        }
                    } else {
                        msj = " Conexión a bodega de datos Fallida.";
                    }
                } catch (Exception e) {
                    msj = " Conexión a bodega de datos Fallida.";
                    correctConnection = false;
                    conn = null;
                }
                try {
                    url = "jdbc:postgresql://" + server + "/" + db;
                    conn = DriverManager.getConnection(url, user, password);//conectarse db del observatorio
                    if (conn != null && !conn.isClosed()) {
                        msj = msj + " Conexión a base de datos observatorio Correcta.";
                    } else {
                        msj = msj + " Conexión a base de datos observatorio Fallida.";
                        correctConnection = false;
                    }
                } catch (Exception e) {
                    msj = msj + " Conexión a base de datos observatorio Fallida.";
                    correctConnection = false;
                    conn = null;
                }
                if (correctConnection) {//se realizaron las dos conexiones(observatorio y bodega)                    
                    non_query("delete from configurations");
                    insert("configurations", "user_db,password_db,name_db,server_db,name_db_dwh",
                            "'" + user + "','" + password + "','" + db + "','" + server + "','" + db_dwh + "'");
                    //conn.close();
                    return "index";//se redirirecciona a la pagina principal
                }
            }
        } catch (Exception e) {
            msj = e.toString();
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "La conexion no pudo ser creada con los datos suministrados. " + msj));
        return "";
    }

    /**
     * This method is responsible for connecting to the database, if there is an
     * error, it is shown in the screen
     *
     * @return
     */
    public final boolean connectToDb() {
        /*
         * conectarse a la base datos observatorio y bodega
         * y retornar true si se realizo la conexion
         */
        boolean continueProcess = true;
        try {
            if (conn == null || conn.isClosed()) {
                if (continueProcess) {
                    try {
                        Class.forName("org.postgresql.Driver").newInstance();// seleccionar SGBD
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                        System.out.println("Error1: " + e.toString() + " --- Clase: " + this.getClass().getName());
                        continueProcess = false;
                    }
                }
                if (continueProcess) {
                    boolean correctConnection = true;
                    try {
                        url = "jdbc:postgresql://" + server + "/" + db_dwh;
                        conn = DriverManager.getConnection(url, user, password);//conectarse a bodega de datos                
                        if (conn != null && !conn.isClosed()) {
                            msj = " Conexión a bodega de datos Correcta.";
                            if (!conn.isClosed()) {
                                conn.close();
                            }
                        } else {
                            msj = " Conexión a bodega de datos Fallida.";
                        }
                    } catch (Exception e) {
                        msj = " Conexión a bodega de datos Fallida.";
                        correctConnection = false;
                        conn = null;
                    }
                    try {
                        url = "jdbc:postgresql://" + server + "/" + db;
                        conn = DriverManager.getConnection(url, user, password);//conectarse db del observatorio
                        if (conn != null && !conn.isClosed()) {
                            msj = msj + " Conexión a base de datos observatorio Correcta.";
                        } else {
                            msj = msj + " Conexión a base de datos observatorio Fallida.";
                            correctConnection = false;
                        }
                    } catch (Exception e) {
                        msj = msj + " Conexión a base de datos observatorio Fallida.";
                        correctConnection = false;
                        conn = null;
                    }
                    if (correctConnection) {//se realizaron las dos conexiones(observatorio y bodega)                    
                        System.out.println("Conexion a base de datos " + url + " ... OK  " + this.getClass().getName());
                        return true;
                    } else {
                        System.out.println("No se pudo conectar a base de datos " + url + " " + this.getClass().getName() + " ... FAIL");
                        return false;
                    }
                }
            }
        } catch (Exception e) {
        }
        return true;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection con) {
        this.conn = con;
    }

    /**
     * this method is used to query to database.
     *
     * @param query
     * @return
     */
    public ResultSet consult(String query) {
        msj = "";
        try {
            if (conn != null) {
                st = conn.createStatement();
                rs = st.executeQuery(query);
                //System.out.println("---- CONSULTA: " + query);
                return rs;
            } else {
                msj = "There don't exist connection";
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.toString() + " - Clase: " + this.getClass().getName());
            msj = "ERROR: " + e.getMessage() + "---- CONSULTA:" + query;
            return null;
        }
    }

    /**
     * this method is used to query to database that do not return any value as
     * INSERT, UPDATE or DELETE.
     *
     * @param query
     * @return
     */
    public int non_query(String query) {
        msj = "";
        int reg;
        reg = 0;
        try {
            if (conn != null) {
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    reg = stmt.executeUpdate();
                }
            }

        } catch (SQLException e) {
            if (showMessages) {
                System.out.println("Error: " + e.toString() + " -- Clase: " + this.getClass().getName() + " -  " + query);
            }
            msj = "ERROR: " + e.getMessage();
        }
        return reg;
    }

    /**
     * this method is used to insert data into a specific table in the database.
     *
     * @param Tabla
     * @param elementos
     * @param registro
     * @return
     */
    public String insert(String Tabla, String elementos, String registro) {
        msj = "";
        int reg = 1;
        String success;
        try {
            if (conn != null) {
                st = conn.createStatement();
                st.execute("INSERT INTO " + Tabla + " (" + elementos + ") VALUES (" + registro + ")");
                if (reg > 0) {
                    success = "true";
                } else {
                    success = "false";
                }
                st.close();
            } else {
                success = "false";
                msj = "ERROR: There don't exist connection...";
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.toString() + " --- Clase: " + this.getClass().getName());
            System.out.println("numero: " + e.getErrorCode());
            success = e.getMessage();
            msj = "ERROR: " + e.getMessage();
        }
        return success;
    }

    /**
     * this method is used to remove data from a specified table in the
     * database.
     *
     * @param Tabla
     * @param condicion
     */
    public void remove(String Tabla, String condicion) {
        msj = "";

        int reg;
        try {
            if (conn != null) {
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM " + Tabla + " WHERE " + condicion)) {
                    reg = stmt.executeUpdate();
                    if (reg > 0) {
                    } else {
                    }
                }
            } else {
                msj = "ERROR: There don't exist connection";
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.toString() + " ---- Clase: " + this.getClass().getName());
            msj = "ERROR: " + e.getMessage();
        }
    }

    /**
     * this method is used to update data into a specified table in the
     * database.
     *
     * @param Tabla
     * @param campos
     * @param donde
     */
    public void update(String Tabla, String campos, String donde) {
        msj = "";
        int reg;
        try {
            if (conn != null) {
                try (PreparedStatement stmt = conn.prepareStatement("UPDATE " + Tabla + " SET " + campos + " WHERE " + donde)) {
                    reg = stmt.executeUpdate();
                    if (reg > 0) {
                    } else {
                    }
                }
            } else {
                msj = "ERROR: There don't exist connection";
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.toString() + " ----- Clase: " + this.getClass().getName());
            msj = "ERROR: " + e.getMessage();
        }
    }

    public String getMsj() {
        return msj;
    }

    public void setMsj(String mens) {
        msj = mens;
    }

    /*
     * ------------METODOS PARA MANEJOS DE CONJUNTOS----------
     */
    /**
     * This method is responsible to load all data corresponding to a murder
     * victim selected for subsequently display the data in a table.
     *
     * @param idVIctim
     * @return
     */
    public RowDataTable loadFatalInjuryMurderRecord(String idVIctim) {
        //CARGO LOS DATOS DE UN REGISTRO DE LESION FATAL EN UNA FILA PARA LA TABLA
        FatalInjuryMurder currentFatalInjuryMurder = fatalInjuryMurderFacade.findByIdVictim(idVIctim);

        if (currentFatalInjuryMurder == null) {
            return null;
        }

        RowDataTable newRowDataTable = new RowDataTable();
        //------------------------------------------------------------
        //SE CARGAN VALORES PARA LA NUEVA VICTIMA
        //------------------------------------------------------------

        //******non_fatal_injury_id
        newRowDataTable.setColumn1(currentFatalInjuryMurder.getFatalInjuries().getFatalInjuryId().toString());

        //******type_id
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getVictimId().getTypeId() != null) {
                newRowDataTable.setColumn2(currentFatalInjuryMurder.getFatalInjuries().getVictimId().getTypeId().getTypeName());
            }
        } catch (Exception e) {
        }
        //******victim_nid
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getVictimId().getVictimNid() != null) {
                newRowDataTable.setColumn3(currentFatalInjuryMurder.getFatalInjuries().getVictimId().getVictimNid());
            }
        } catch (Exception e) {
        }
        //******victim_name
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getVictimId().getVictimName() != null) {
                newRowDataTable.setColumn4(currentFatalInjuryMurder.getFatalInjuries().getVictimId().getVictimName());
            }
        } catch (Exception e) {
        }
        //******stranger
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getVictimId().getStranger() != null) {
                if (currentFatalInjuryMurder.getFatalInjuries().getVictimId().getStranger() == true) {
                    newRowDataTable.setColumn5("SI");
                } else {
                    newRowDataTable.setColumn5("NO");
                }
            }
        } catch (Exception e) {
        }
        //******age_type_id
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getVictimId().getAgeTypeId() != null) {
                newRowDataTable.setColumn6(ageTypesFacade.find(currentFatalInjuryMurder.getFatalInjuries().getVictimId().getAgeTypeId()).getAgeTypeName());
            }
        } catch (Exception e) {
        }
        //******victim_age
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getVictimId().getVictimAge() != null) {
                newRowDataTable.setColumn7(currentFatalInjuryMurder.getFatalInjuries().getVictimId().getVictimAge().toString());
            }
        } catch (Exception e) {
        }
        //******gender_id
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getVictimId().getGenderId() != null) {
                newRowDataTable.setColumn8(currentFatalInjuryMurder.getFatalInjuries().getVictimId().getGenderId().getGenderName());
            }
        } catch (Exception e) {
        }
        //******job_id
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getVictimId().getJobId() != null) {
                newRowDataTable.setColumn9(currentFatalInjuryMurder.getFatalInjuries().getVictimId().getJobId().getJobName());
            }
        } catch (Exception e) {
        }

        //******vulnerable_group_id
        //******ethnic_group_id        
        //******victim_telephone
        //******victim_address
        //******victim_neighborhood_id
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getVictimId().getVictimNeighborhoodId() != null) {
                newRowDataTable.setColumn10(currentFatalInjuryMurder.getFatalInjuries().getVictimId().getVictimNeighborhoodId().getNeighborhoodName());
                newRowDataTable.setColumn31(String.valueOf(currentFatalInjuryMurder.getFatalInjuries().getVictimId().getVictimNeighborhoodId().getNeighborhoodSuburb()));
            }
        } catch (Exception e) {
        }

        //******victim_date_of_birth
        //******eps_id
        //******victim_class
        //******victim_id       
        //******residence_municipality
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getVictimId().getResidenceDepartment() != null && (currentFatalInjuryMurder.getFatalInjuries().getVictimId().getResidenceMunicipality() != null)) {
                short departamentId = currentFatalInjuryMurder.getFatalInjuries().getVictimId().getResidenceDepartment();
                short municipalityId = currentFatalInjuryMurder.getFatalInjuries().getVictimId().getResidenceMunicipality();
                MunicipalitiesPK mPk = new MunicipalitiesPK(departamentId, municipalityId);
                newRowDataTable.setColumn11(municipalitiesFacade.find(mPk).getMunicipalityName());
            }
        } catch (Exception e) {
        }
        //******residence_department
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getVictimId().getResidenceDepartment() != null) {
                newRowDataTable.setColumn12(departamentsFacade.find(currentFatalInjuryMurder.getFatalInjuries().getVictimId().getResidenceDepartment()).getDepartamentName());
            }
        } catch (Exception e) {
        }

        //******insurance_id
        //------------------------------------------------------------
        //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA FATAL
        //------------------------------------------------------------
        //******injury_id
        //******injury_date
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getInjuryDate() != null) {
                newRowDataTable.setColumn13(sdf.format(currentFatalInjuryMurder.getFatalInjuries().getInjuryDate()));
            }
        } catch (Exception e) {
        }
        //******injury_time
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getInjuryTime() != null) {
                hours = String.valueOf(currentFatalInjuryMurder.getFatalInjuries().getInjuryTime().getHours());
                minutes = String.valueOf(currentFatalInjuryMurder.getFatalInjuries().getInjuryTime().getMinutes());
                if (hours.length() != 2) {
                    hours = "0" + hours;
                }
                if (minutes.length() != 2) {
                    minutes = "0" + minutes;
                }
                newRowDataTable.setColumn14(hours + minutes);
            }
        } catch (Exception e) {
        }
        //******injury_address
        if (currentFatalInjuryMurder.getFatalInjuries().getInjuryAddress() != null) {
            newRowDataTable.setColumn15(currentFatalInjuryMurder.getFatalInjuries().getInjuryAddress());
        }
        //******injury_neighborhood_id
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getInjuryNeighborhoodId() != null) {
                newRowDataTable.setColumn16(neighborhoodsFacade.find(currentFatalInjuryMurder.getFatalInjuries().getInjuryNeighborhoodId()).getNeighborhoodName());
                newRowDataTable.setColumn30(String.valueOf(neighborhoodsFacade.find(currentFatalInjuryMurder.getFatalInjuries().getInjuryNeighborhoodId()).getNeighborhoodSuburb()));
            }
        } catch (Exception e) {
        }
        //******quadrant_id
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getQuadrantId() != null) {
                newRowDataTable.setColumn32(currentFatalInjuryMurder.getFatalInjuries().getQuadrantId().getQuadrantName());
            }
        } catch (Exception e) {
        }
        //******injury_place_id
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getInjuryPlaceId() != null) {
                newRowDataTable.setColumn17(currentFatalInjuryMurder.getFatalInjuries().getInjuryPlaceId().getPlaceName());
            }
        } catch (Exception e) {
        }

        //******victim_number
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getVictimNumber() != null) {
                newRowDataTable.setColumn18(currentFatalInjuryMurder.getFatalInjuries().getVictimNumber().toString());

            }
        } catch (Exception e) {
        }
        //******injury_description
        if (currentFatalInjuryMurder.getFatalInjuries().getInjuryDescription() != null) {
            newRowDataTable.setColumn19(currentFatalInjuryMurder.getFatalInjuries().getInjuryDescription());
        }
        //******user_id	
        //******input_timestamp	
        //******injury_day_of_week
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getInjuryDayOfWeek() != null) {
                newRowDataTable.setColumn20(currentFatalInjuryMurder.getFatalInjuries().getInjuryDayOfWeek());
            }
        } catch (Exception e) {
        }

        //******victim_id
        //******fatal_injury_id
        //******alcohol_level_victim
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getAlcoholLevelVictim() != null) {
                newRowDataTable.setColumn21(currentFatalInjuryMurder.getFatalInjuries().getAlcoholLevelVictim().toString());
            }
        } catch (Exception e) {
        }
        //******alcohol_level_victimId
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getAlcoholLevelVictimId() != null) {
                newRowDataTable.setColumn22(currentFatalInjuryMurder.getFatalInjuries().getAlcoholLevelVictimId().getAlcoholLevelName());
            }
        } catch (Exception e) {
        }
        //******code
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getCode() != null) {
                newRowDataTable.setColumn23(currentFatalInjuryMurder.getFatalInjuries().getCode());
            }
        } catch (Exception e) {
        }
        //******area_id
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getAreaId() != null) {
                newRowDataTable.setColumn24(currentFatalInjuryMurder.getFatalInjuries().getAreaId().getAreaName());
            }
        } catch (Exception e) {
        }
        //******victim_place_of_origin
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getVictimPlaceOfOrigin() != null) {
                String source = currentFatalInjuryMurder.getFatalInjuries().getVictimPlaceOfOrigin();
                String[] sourceSplit = source.split("-");
                //determino pais
                newRowDataTable.setColumn25(countriesFacade.find(Short.parseShort(sourceSplit[0])).getName());
                if (Short.parseShort(sourceSplit[0]) == 52) {//colombia
                    newRowDataTable.setColumn26(departamentsFacade.find(Short.parseShort(sourceSplit[1])).getDepartamentName());
                    MunicipalitiesPK municipalitiesPK = new MunicipalitiesPK(Short.parseShort(sourceSplit[1]), Short.parseShort(sourceSplit[2]));
                    newRowDataTable.setColumn27(municipalitiesFacade.find(municipalitiesPK).getMunicipalityName());
                }
            }
        } catch (Exception e) {
        }
        //------------------------------------------------------------
        //SE CARGA DATOS PARA LA NUEVA LESION FATAL POR HOMICIDIOS
        //------------------------------------------------------------

        //******murder_context_id
        try {
            if (currentFatalInjuryMurder.getMurderContextId() != null) {
                newRowDataTable.setColumn28(currentFatalInjuryMurder.getMurderContextId().getMurderContextName());
            }
        } catch (Exception e) {
        }
        //******weapon_type_id
        try {
            if (currentFatalInjuryMurder.getWeaponTypeId() != null) {
                newRowDataTable.setColumn29(currentFatalInjuryMurder.getWeaponTypeId().getWeaponTypeName());
            }
        } catch (Exception e) {
        }
        //******fatal_injury_id
        return newRowDataTable;
    }

    /**
     * This method is responsible to load all data corresponding to a accident
     * victim selected for subsequently display the data in a table.
     *
     * @param idVIctim
     * @return
     */
    public RowDataTable loadFatalInjuryAccidentRecord(String idVIctim) {
        //CARGO LOS DATOS DE UNA DETERMINA FATAL ACCIDENTAL EN UNA FILA PARA LA TABLA
        //btnEditDisabled = true;
        //btnRemoveDisabled = true;
        FatalInjuryAccident currentFatalInjuryA = fatalInjuryAccidentFacade.findByIdVictim(idVIctim);

        if (currentFatalInjuryA == null) {
            return null;
        }
        RowDataTable newRowDataTable = new RowDataTable();
        //------------------------------------------------------------
        //SE CARGAN VALORES PARA LA NUEVA VICTIMA
        //------------------------------------------------------------

        //******non_fatal_injury_id
        newRowDataTable.setColumn1(currentFatalInjuryA.getFatalInjuries().getFatalInjuryId().toString());

        //******type_id
        try {
            if (currentFatalInjuryA.getFatalInjuries().getVictimId().getTypeId() != null) {
                newRowDataTable.setColumn2(currentFatalInjuryA.getFatalInjuries().getVictimId().getTypeId().getTypeName());
            }
        } catch (Exception e) {
        }
        //******victim_nid
        try {
            if (currentFatalInjuryA.getFatalInjuries().getVictimId().getVictimNid() != null) {
                newRowDataTable.setColumn3(currentFatalInjuryA.getFatalInjuries().getVictimId().getVictimNid());
            }
        } catch (Exception e) {
        }
        //******victim_name
        try {
            if (currentFatalInjuryA.getFatalInjuries().getVictimId().getVictimName() != null) {
                newRowDataTable.setColumn4(currentFatalInjuryA.getFatalInjuries().getVictimId().getVictimName());
            }
        } catch (Exception e) {
        }
        //******stranger
        try {
            if (currentFatalInjuryA.getFatalInjuries().getVictimId().getStranger() != null) {
                if (currentFatalInjuryA.getFatalInjuries().getVictimId().getStranger() == true) {
                    newRowDataTable.setColumn5("SI");
                } else {
                    newRowDataTable.setColumn5("NO");
                }
            }
        } catch (Exception e) {
        }
        //******age_type_id
        try {
            if (currentFatalInjuryA.getFatalInjuries().getVictimId().getAgeTypeId() != null) {
                newRowDataTable.setColumn6(ageTypesFacade.find(currentFatalInjuryA.getFatalInjuries().getVictimId().getAgeTypeId()).getAgeTypeName());
            }
        } catch (Exception e) {
        }
        //******victim_age
        try {
            if (currentFatalInjuryA.getFatalInjuries().getVictimId().getVictimAge() != null) {
                newRowDataTable.setColumn7(currentFatalInjuryA.getFatalInjuries().getVictimId().getVictimAge().toString());
            }
        } catch (Exception e) {
        }
        //******gender_id
        try {
            if (currentFatalInjuryA.getFatalInjuries().getVictimId().getGenderId() != null) {
                newRowDataTable.setColumn8(currentFatalInjuryA.getFatalInjuries().getVictimId().getGenderId().getGenderName());
            }
        } catch (Exception e) {
        }
        //******job_id
        try {
            if (currentFatalInjuryA.getFatalInjuries().getVictimId().getJobId() != null) {
                newRowDataTable.setColumn9(currentFatalInjuryA.getFatalInjuries().getVictimId().getJobId().getJobName());
            }
        } catch (Exception e) {
        }

        //******vulnerable_group_id
        //******ethnic_group_id        
        //******victim_telephone
        //******victim_address
        //******victim_neighborhood_id
        try {
            if (currentFatalInjuryA.getFatalInjuries().getVictimId().getVictimNeighborhoodId() != null) {
                newRowDataTable.setColumn10(currentFatalInjuryA.getFatalInjuries().getVictimId().getVictimNeighborhoodId().getNeighborhoodName());
                newRowDataTable.setColumn31(String.valueOf(currentFatalInjuryA.getFatalInjuries().getVictimId().getVictimNeighborhoodId().getNeighborhoodSuburb()));
            }
        } catch (Exception e) {
        }

        //******victim_date_of_birth
        //******eps_id
        //******victim_class
        //******victim_id       
        //******residence_municipality
        try {
            if (currentFatalInjuryA.getFatalInjuries().getVictimId().getResidenceDepartment() != null && (currentFatalInjuryA.getFatalInjuries().getVictimId().getResidenceMunicipality() != null)) {
                short departamentId = currentFatalInjuryA.getFatalInjuries().getVictimId().getResidenceDepartment();
                short municipalityId = currentFatalInjuryA.getFatalInjuries().getVictimId().getResidenceMunicipality();
                MunicipalitiesPK mPk = new MunicipalitiesPK(departamentId, municipalityId);
                newRowDataTable.setColumn11(municipalitiesFacade.find(mPk).getMunicipalityName());
            }
        } catch (Exception e) {
        }
        //******residence_department
        try {
            if (currentFatalInjuryA.getFatalInjuries().getVictimId().getResidenceDepartment() != null) {
                newRowDataTable.setColumn12(departamentsFacade.find(currentFatalInjuryA.getFatalInjuries().getVictimId().getResidenceDepartment()).getDepartamentName());
            }
        } catch (Exception e) {
        }

        //******insurance_id
        //------------------------------------------------------------
        //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA FATAL
        //------------------------------------------------------------
        //******injury_id
        //******injury_date
        try {
            if (currentFatalInjuryA.getFatalInjuries().getInjuryDate() != null) {
                newRowDataTable.setColumn13(sdf.format(currentFatalInjuryA.getFatalInjuries().getInjuryDate()));
            }
        } catch (Exception e) {
        }
        //******injury_time
        try {
            if (currentFatalInjuryA.getFatalInjuries().getInjuryTime() != null) {
                hours = String.valueOf(currentFatalInjuryA.getFatalInjuries().getInjuryTime().getHours());
                minutes = String.valueOf(currentFatalInjuryA.getFatalInjuries().getInjuryTime().getMinutes());
                if (hours.length() != 2) {
                    hours = "0" + hours;
                }
                if (minutes.length() != 2) {
                    minutes = "0" + minutes;
                }
                newRowDataTable.setColumn14(hours + minutes);
            }
        } catch (Exception e) {
        }
        //******injury_address
        if (currentFatalInjuryA.getFatalInjuries().getInjuryAddress() != null) {
            newRowDataTable.setColumn15(currentFatalInjuryA.getFatalInjuries().getInjuryAddress());
        }
        //******injury_neighborhood_id
        try {
            if (currentFatalInjuryA.getFatalInjuries().getInjuryNeighborhoodId() != null) {
                newRowDataTable.setColumn16(neighborhoodsFacade.find(currentFatalInjuryA.getFatalInjuries().getInjuryNeighborhoodId()).getNeighborhoodName());
                newRowDataTable.setColumn30(String.valueOf(neighborhoodsFacade.find(currentFatalInjuryA.getFatalInjuries().getInjuryNeighborhoodId()).getNeighborhoodSuburb()));
            }
        } catch (Exception e) {
        }
        //******quadrant_id
        try {
            if (currentFatalInjuryA.getFatalInjuries().getQuadrantId() != null) {
                newRowDataTable.setColumn32(currentFatalInjuryA.getFatalInjuries().getQuadrantId().getQuadrantName());
            }
        } catch (Exception e) {
        }
        //******injury_place_id
        try {
            if (currentFatalInjuryA.getFatalInjuries().getInjuryPlaceId() != null) {
                newRowDataTable.setColumn17(currentFatalInjuryA.getFatalInjuries().getInjuryPlaceId().getPlaceName());
            }
        } catch (Exception e) {
        }

        //******victim_number
        try {
            if (currentFatalInjuryA.getFatalInjuries().getVictimNumber() != null) {
                newRowDataTable.setColumn18(currentFatalInjuryA.getFatalInjuries().getVictimNumber().toString());

            }
        } catch (Exception e) {
        }
        //******injury_description
        if (currentFatalInjuryA.getFatalInjuries().getInjuryDescription() != null) {
            newRowDataTable.setColumn19(currentFatalInjuryA.getFatalInjuries().getInjuryDescription());
        }
        //******user_id	
        //******input_timestamp	
        //******injury_day_of_week
        try {
            if (currentFatalInjuryA.getFatalInjuries().getInjuryDayOfWeek() != null) {
                newRowDataTable.setColumn20(currentFatalInjuryA.getFatalInjuries().getInjuryDayOfWeek());
            }
        } catch (Exception e) {
        }

        //******victim_id
        //******fatal_injury_id
        //******alcohol_level_victim
        try {
            if (currentFatalInjuryA.getFatalInjuries().getAlcoholLevelVictim() != null) {
                newRowDataTable.setColumn21(currentFatalInjuryA.getFatalInjuries().getAlcoholLevelVictim().toString());
            }
        } catch (Exception e) {
        }
        //******alcohol_level_victimId
        try {
            if (currentFatalInjuryA.getFatalInjuries().getAlcoholLevelVictimId() != null) {
                newRowDataTable.setColumn22(currentFatalInjuryA.getFatalInjuries().getAlcoholLevelVictimId().getAlcoholLevelName());
            }
        } catch (Exception e) {
        }
        //******code
        try {
            if (currentFatalInjuryA.getFatalInjuries().getCode() != null) {
                newRowDataTable.setColumn23(currentFatalInjuryA.getFatalInjuries().getCode());
            }
        } catch (Exception e) {
        }
        //******area_id
        try {
            if (currentFatalInjuryA.getFatalInjuries().getAreaId() != null) {
                newRowDataTable.setColumn24(currentFatalInjuryA.getFatalInjuries().getAreaId().getAreaName());
            }
        } catch (Exception e) {
        }
        //******victim_place_of_origin
        try {
            if (currentFatalInjuryA.getFatalInjuries().getVictimPlaceOfOrigin() != null) {
                String source = currentFatalInjuryA.getFatalInjuries().getVictimPlaceOfOrigin();
                String[] sourceSplit = source.split("-");
                //determino pais
                newRowDataTable.setColumn25(countriesFacade.find(Short.parseShort(sourceSplit[0])).getName());
                if (Short.parseShort(sourceSplit[0]) == 52) {//colombia
                    newRowDataTable.setColumn26(departamentsFacade.find(Short.parseShort(sourceSplit[1])).getDepartamentName());
                    MunicipalitiesPK municipalitiesPK = new MunicipalitiesPK(Short.parseShort(sourceSplit[1]), Short.parseShort(sourceSplit[2]));
                    newRowDataTable.setColumn27(municipalitiesFacade.find(municipalitiesPK).getMunicipalityName());
                }
            }
        } catch (Exception e) {
        }
        //------------------------------------------------------------
        //SE CARGA DATOS PARA LA NUEVA LESION FATAL POR HOMICIDIOS
        //------------------------------------------------------------

        //******number_non_fatal_victims
        try {
            if (currentFatalInjuryA.getNumberNonFatalVictims() != null) {
                newRowDataTable.setColumn28(currentFatalInjuryA.getNumberNonFatalVictims().toString());
            }
        } catch (Exception e) {
        }
        //******death_mechanism_id
        try {
            if (currentFatalInjuryA.getDeathMechanismId() != null) {
                newRowDataTable.setColumn29(currentFatalInjuryA.getDeathMechanismId().getAccidentMechanismName());
            }
        } catch (Exception e) {
        }
        //******fatal_injury_id

        return newRowDataTable;
    }

    /**
     * This method is responsible to load all data corresponding to a LCENF
     * victim selected for subsequently display the data in a table.
     *
     * @param idVIctim
     * @return
     */
    public RowDataTable loadNonFatalInjuryRecord(String idVIctim) {
        //CARGO LOS DATOS DE UNA DETERMINA LESION NO FATAL EN UNA FILA PARA LA TABLA
        //btnEditDisabled = true;
        //btnRemoveDisabled = true;
        NonFatalInjuries currentNonFatalI = nonFatalInjuriesFacade.findByIdVictim(idVIctim);
        if (currentNonFatalI == null) {
            return null;
        }
        RowDataTable newRowDataTable = new RowDataTable();
        //------------------------------------------------------------
        //SE CARGAN VALORES PARA LA NUEVA VICTIMA
        //------------------------------------------------------------

        //******non_fatal_injury_id
        newRowDataTable.setColumn1(currentNonFatalI.getNonFatalInjuryId().toString());
        //******type_id
        try {
            if (currentNonFatalI.getVictimId().getTypeId() != null) {
                newRowDataTable.setColumn2(currentNonFatalI.getVictimId().getTypeId().getTypeName());
            }
        } catch (Exception e) {
        }
        //******victim_nid
        try {
            if (currentNonFatalI.getVictimId().getVictimNid() != null) {
                newRowDataTable.setColumn3(currentNonFatalI.getVictimId().getVictimNid());
            }
        } catch (Exception e) {
        }
        //******victim_name
        try {
            if (currentNonFatalI.getVictimId().getVictimName() != null) {
                newRowDataTable.setColumn4(currentNonFatalI.getVictimId().getVictimName());
            }
        } catch (Exception e) {
        }
        //******stranger
        try {
            if (currentNonFatalI.getVictimId().getStranger() != null) {
                if (currentNonFatalI.getVictimId().getStranger()) {
                    newRowDataTable.setColumn5("SI");
                } else {
                    newRowDataTable.setColumn5("NO");
                }

            }
        } catch (Exception e) {
        }
        //******age_type_id
        try {
            if (currentNonFatalI.getVictimId().getAgeTypeId() != null) {
                newRowDataTable.setColumn6(ageTypesFacade.find(currentNonFatalI.getVictimId().getAgeTypeId()).getAgeTypeName());
            }
        } catch (Exception e) {
        }
        //******victim_age
        try {
            if (currentNonFatalI.getVictimId().getVictimAge() != null) {
                newRowDataTable.setColumn7(currentNonFatalI.getVictimId().getVictimAge().toString());
            }
        } catch (Exception e) {
        }
        //******gender_id
        try {
            if (currentNonFatalI.getVictimId().getGenderId() != null) {
                newRowDataTable.setColumn8(currentNonFatalI.getVictimId().getGenderId().getGenderName());
            }
        } catch (Exception e) {
        }
        //******job_id
        try {
            if (currentNonFatalI.getVictimId().getJobId() != null) {
                newRowDataTable.setColumn9(currentNonFatalI.getVictimId().getJobId().getJobName());
            }
        } catch (Exception e) {
        }

        //******ethnic_group_id
        try {
            if (currentNonFatalI.getVictimId().getEthnicGroupId() != null) {
                newRowDataTable.setColumn10(currentNonFatalI.getVictimId().getEthnicGroupId().getEthnicGroupName());
            }
        } catch (Exception e) {
        }
        //******victim_telephone
        try {
            if (currentNonFatalI.getVictimId().getVictimTelephone() != null) {
                newRowDataTable.setColumn11(currentNonFatalI.getVictimId().getVictimTelephone());
            }
        } catch (Exception e) {
        }

        //******victim_date_of_birth
        //******eps_id
        //******victim_class
        //******victim_id        
        //******residence_municipality
        try {
            if (currentNonFatalI.getVictimId().getResidenceDepartment() != null && (currentNonFatalI.getVictimId().getResidenceMunicipality() != null)) {
                short departamentId = currentNonFatalI.getVictimId().getResidenceDepartment();
                short municipalityId = currentNonFatalI.getVictimId().getResidenceMunicipality();
                MunicipalitiesPK mPk = new MunicipalitiesPK(departamentId, municipalityId);
                newRowDataTable.setColumn12(municipalitiesFacade.find(mPk).getMunicipalityName());
            }
        } catch (Exception e) {
        }
        //******residence_department
        try {
            if (currentNonFatalI.getVictimId().getResidenceDepartment() != null) {
                newRowDataTable.setColumn13(departamentsFacade.find(currentNonFatalI.getVictimId().getResidenceDepartment()).getDepartamentName());
            }
        } catch (Exception e) {
        }
        //******victim_address
        try {
            if (currentNonFatalI.getVictimId().getVictimAddress() != null) {
                newRowDataTable.setColumn14(currentNonFatalI.getVictimId().getVictimAddress());
            }
        } catch (Exception e) {
        }
        //******victim_neighborhood_id
        try {
            if (currentNonFatalI.getVictimId().getVictimNeighborhoodId() != null) {
                newRowDataTable.setColumn15(currentNonFatalI.getVictimId().getVictimNeighborhoodId().getNeighborhoodName());
                newRowDataTable.setColumn121(String.valueOf(currentNonFatalI.getVictimId().getVictimNeighborhoodId().getNeighborhoodSuburb()));
            }
        } catch (Exception e) {
        }
        //informacion de grupos vunerables
        if (currentNonFatalI.getVictimId().getVulnerableGroupsList() != null) {
            if (!currentNonFatalI.getVictimId().getVulnerableGroupsList().isEmpty()) {
                for (int i = 0; i < currentNonFatalI.getVictimId().getVulnerableGroupsList().size(); i++) {
                    if (1 == currentNonFatalI.getVictimId().getVulnerableGroupsList().get(i).getVulnerableGroupId()) {
                        newRowDataTable.setColumn16("SI");//isDisplaced = true;
                    }
                    if (2 == currentNonFatalI.getVictimId().getVulnerableGroupsList().get(i).getVulnerableGroupId()) {
                        //isHandicapped = true;
                        newRowDataTable.setColumn17("SI");
                    }
                }
            }
        }

        //******insurance_id
        try {
            if (currentNonFatalI.getVictimId().getInsuranceId() != null) {
                newRowDataTable.setColumn18(currentNonFatalI.getVictimId().getInsuranceId().getInsuranceName());
            }
        } catch (Exception e) {
        }

        //-----CARGAR CAMPOS OTROS----------------
        if (currentNonFatalI.getVictimId().getOthersList() != null) {
            List<Others> othersList = currentNonFatalI.getVictimId().getOthersList();
            for (int i = 0; i < othersList.size(); i++) {
                switch (othersList.get(i).getOthersPK().getFieldId()) {
                    case 1://1.	Cual otro grupo etnico
                        newRowDataTable.setColumn19(othersList.get(i).getValueText());
                        break;
                    case 2://2.	Cual otro de lugar del hecho
                        newRowDataTable.setColumn20(othersList.get(i).getValueText());
                        break;
                    case 3://3.	Cual otra actividad
                        newRowDataTable.setColumn21(othersList.get(i).getValueText());
                        break;
                    case 4://4.	Cual altura
                        newRowDataTable.setColumn22(othersList.get(i).getValueText());
                        break;
                    case 5://5.	Cual polvora
                        newRowDataTable.setColumn23(othersList.get(i).getValueText());
                        break;
                    case 6://6.	Cual desastre natural
                        newRowDataTable.setColumn24(othersList.get(i).getValueText());
                        break;
                    case 7://7.	Cual otro mecanismo de objeto
                        newRowDataTable.setColumn25(othersList.get(i).getValueText());
                        break;
                    case 8://8.	Cual otro animal
                        newRowDataTable.setColumn26(othersList.get(i).getValueText());
                        break;
                    case 9://9.	Cual otro factor precipitante(Autoinflingida intencional)
                        newRowDataTable.setColumn27(othersList.get(i).getValueText());
                        break;
                    case 10://10.	Cual otro tipo de agresor(intrafamiliar)
                        newRowDataTable.setColumn28(othersList.get(i).getValueText());
                        break;
                    case 11://11.	Cual otro tipo de maltrato(intrafamiliar)
                        newRowDataTable.setColumn29(othersList.get(i).getValueText());
                        break;
                    case 12://12.	Cual otra relación (violencia interpersonal)
                        newRowDataTable.setColumn30(othersList.get(i).getValueText());
                        break;
                    case 13://13.	Cual otro tipo de transporte(transporte)
                        newRowDataTable.setColumn31(othersList.get(i).getValueText());
                        break;
                    case 14://14.	Cual otro tipo de transporte de contraparte(transporte)
                        newRowDataTable.setColumn32(othersList.get(i).getValueText());
                        break;
                    case 15://15.	Cual otro tipo de transporte de usuario(transporte)
                        newRowDataTable.setColumn33(othersList.get(i).getValueText());
                        break;
                    case 16://16.	Cual otro sitio anatomico
                        newRowDataTable.setColumn34(othersList.get(i).getValueText());
                        break;
                    case 17://17.	Cual otra naturaleza de la lesión
                        newRowDataTable.setColumn35(othersList.get(i).getValueText());
                        break;
                    case 18://18.	Cual otro destino del paciente
                        newRowDataTable.setColumn36(othersList.get(i).getValueText());
                        break;
                }
            }
        }

        //------------------------------------------------------------
        //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA NO FATAL
        //------------------------------------------------------------        
        //******checkup_date
        try {
            if (currentNonFatalI.getCheckupDate() != null) {
                newRowDataTable.setColumn37(sdf.format(currentNonFatalI.getCheckupDate()));
            }
        } catch (Exception e) {
        }
        //******checkup_time
        try {
            if (currentNonFatalI.getCheckupTime() != null) {
                hours = String.valueOf(currentNonFatalI.getCheckupTime().getHours());
                minutes = String.valueOf(currentNonFatalI.getCheckupTime().getMinutes());
                if (hours.length() != 2) {
                    hours = "0" + hours;
                }
                if (minutes.length() != 2) {
                    minutes = "0" + minutes;
                }
                newRowDataTable.setColumn38(hours + minutes);
            }
        } catch (Exception e) {
        }
        //******injury_date
        try {
            if (currentNonFatalI.getInjuryDate() != null) {
                newRowDataTable.setColumn39(sdf.format(currentNonFatalI.getInjuryDate()));
            }
        } catch (Exception e) {
        }
        //******injury_time
        try {
            if (currentNonFatalI.getInjuryTime() != null) {
                hours = String.valueOf(currentNonFatalI.getInjuryTime().getHours());
                minutes = String.valueOf(currentNonFatalI.getInjuryTime().getMinutes());
                if (hours.length() != 2) {
                    hours = "0" + hours;
                }
                if (minutes.length() != 2) {
                    minutes = "0" + minutes;
                }
                newRowDataTable.setColumn40(hours + minutes);
            }
        } catch (Exception e) {
        }
        //******injury_address
        if (currentNonFatalI.getInjuryAddress() != null) {
            newRowDataTable.setColumn41(currentNonFatalI.getInjuryAddress());
        }
        //******injury_neighborhood_id
        try {
            if (currentNonFatalI.getInjuryNeighborhoodId() != null) {
                newRowDataTable.setColumn42(currentNonFatalI.getInjuryNeighborhoodId().getNeighborhoodName());
                newRowDataTable.setColumn120(String.valueOf(currentNonFatalI.getInjuryNeighborhoodId().getNeighborhoodSuburb()));
            }
        } catch (Exception e) {
        }
        //******quadrant_id
        try {
            if (currentNonFatalI.getQuadrantId() != null) {
                newRowDataTable.setColumn126(currentNonFatalI.getQuadrantId().getQuadrantName());
            }
        } catch (Exception e) {
        }
        //******injury_place_id
        try {
            if (currentNonFatalI.getInjuryPlaceId() != null) {
                newRowDataTable.setColumn43(currentNonFatalI.getInjuryPlaceId().getNonFatalPlaceName());
            }
        } catch (Exception e) {
        }
        //******activity_id
        try {
            if (currentNonFatalI.getActivityId() != null) {
                newRowDataTable.setColumn44(currentNonFatalI.getActivityId().getActivityName());
            }
        } catch (Exception e) {
        }
        //******intentionality_id
        try {
            if (currentNonFatalI.getIntentionalityId() != null) {
                newRowDataTable.setColumn45(currentNonFatalI.getIntentionalityId().getIntentionalityName());
            }
        } catch (Exception e) {
        }
        //******use_alcohol_id
        try {
            if (currentNonFatalI.getUseAlcoholId() != null) {
                newRowDataTable.setColumn46(currentNonFatalI.getUseAlcoholId().getUseAlcoholDrugsName());
            }
        } catch (Exception e) {
        }
        //******use_drugs_id
        try {
            if (currentNonFatalI.getUseDrugsId() != null) {
                newRowDataTable.setColumn47(currentNonFatalI.getUseDrugsId().getUseAlcoholDrugsName());
            }
        } catch (Exception e) {
        }
        //******burn_injury_degree
        try {
            if (currentNonFatalI.getBurnInjuryDegree() != null) {
                newRowDataTable.setColumn48(currentNonFatalI.getBurnInjuryDegree().toString());
            }
        } catch (Exception e) {
        }
        //******burn_injury_percentage
        try {
            if (currentNonFatalI.getBurnInjuryPercentage() != null) {
                newRowDataTable.setColumn49(currentNonFatalI.getBurnInjuryPercentage().toString());
            }
        } catch (Exception e) {
        }
        //******submitted_patient

        try {
            if (currentNonFatalI.getSubmittedPatient() != null) {
                if (currentNonFatalI.getSubmittedPatient()) {
                    newRowDataTable.setColumn50("SI");
                } else {
                    newRowDataTable.setColumn50("NO");
                }

            }
        } catch (Exception e) {
        }
        //******eps_id
        try {
            if (currentNonFatalI.getSubmittedDataSourceId() != null) {
                newRowDataTable.setColumn51(currentNonFatalI.getSubmittedDataSourceId().getNonFatalDataSourceName());
            }
        } catch (Exception e) {
        }
        //******destination_patient_id
        try {
            if (currentNonFatalI.getDestinationPatientId() != null) {
                newRowDataTable.setColumn52(currentNonFatalI.getDestinationPatientId().getDestinationPatientName());
            }
        } catch (Exception e) {
        }
        //******input_timestamp
        try {
            if (currentNonFatalI.getInputTimestamp() != null) {
                newRowDataTable.setColumn53(sdf2.format(currentNonFatalI.getInputTimestamp()));
            }
        } catch (Exception e) {
        }
        //******health_professional_id
        try {
            if (currentNonFatalI.getHealthProfessionalId() != null) {
                newRowDataTable.setColumn54(currentNonFatalI.getHealthProfessionalId().getHealthProfessionalName());
            }
        } catch (Exception e) {
        }
        //******non_fatal_data_source_id
        //******mechanism_id
        try {
            if (currentNonFatalI.getMechanismId() != null) {
                newRowDataTable.setColumn55(currentNonFatalI.getMechanismId().getMechanismName());
            }
        } catch (Exception e) {
        }
        //******user_id
        try {
            if (currentNonFatalI.getUserId() != null) {
                newRowDataTable.setColumn56(currentNonFatalI.getUserId().getUserName());
            }
        } catch (Exception e) {
        }
        //******injury_day_of_week
        try {
            if (currentNonFatalI.getInjuryDayOfWeek() != null) {
                newRowDataTable.setColumn57(currentNonFatalI.getInjuryDayOfWeek());
            }
        } catch (Exception e) {
        }
        //******non_fatal_data_source_id
        try {
            if (currentNonFatalI.getNonFatalDataSourceId() != null) {
                newRowDataTable.setColumn58(currentNonFatalI.getNonFatalDataSourceId().getNonFatalDataSourceName());
            }
        } catch (Exception e) {
        }
        //******injury_id
        try {
            if (currentNonFatalI.getInjuryId() != null) {
                //if (injuriesFacade.find(currentNonFatalI.getInjuryId().getInjuryId()) != null) {
                newRowDataTable.setColumn59(injuriesFacade.find(currentNonFatalI.getInjuryId().getInjuryId()).getInjuryName());
                //}
            }
        } catch (Exception e) {
            System.out.println("Error por" + e.toString());
        }
        //------------------------------------------------------------
        //SE CARGA VARIABLE PARA VIOLENCIA INTERPERSONAL
        //-----------------------------------------------------------

        try {
            if (currentNonFatalI.getNonFatalInterpersonal() != null) {
                if (currentNonFatalI.getNonFatalInterpersonal().getPreviousAntecedent() != null) {
                    newRowDataTable.setColumn60(currentNonFatalI.getNonFatalInterpersonal().getPreviousAntecedent().getBooleanName());
                }
            }
        } catch (Exception e) {
        }
        try {
            if (currentNonFatalI.getNonFatalInterpersonal() != null) {
                if (currentNonFatalI.getNonFatalInterpersonal().getRelationshipVictimId() != null) {
                    newRowDataTable.setColumn61(currentNonFatalI.getNonFatalInterpersonal().getRelationshipVictimId().getRelationshipVictimName());
                }
            }
        } catch (Exception e) {
        }

        try {
            if (currentNonFatalI.getNonFatalInterpersonal() != null) {
                if (currentNonFatalI.getNonFatalInterpersonal().getContextId() != null) {
                    newRowDataTable.setColumn62(currentNonFatalI.getNonFatalInterpersonal().getContextId().getContextName());
                }
            }
        } catch (Exception e) {
        }
        try {
            if (currentNonFatalI.getNonFatalInterpersonal() != null) {
                if (currentNonFatalI.getNonFatalInterpersonal().getRelationshipVictimId() != null) {
                    newRowDataTable.setColumn63(currentNonFatalI.getNonFatalInterpersonal().getAggressorGenderId().getGenderName());
                }
            }
        } catch (Exception e) {
        }
        //------------------------------------------------------------
        //SE CARGA DATOS PARA VIOLENCIA INTRAFAMILIAR
        //------------------------------------------------------------
        //cargo la lista de agresores-----------------------------------
        try {
            if (currentNonFatalI.getNonFatalDomesticViolence() != null) {
                if (currentNonFatalI.getNonFatalDomesticViolence().getAggressorTypesList() != null) {
                    List<AggressorTypes> aggressorTypesList = currentNonFatalI.getNonFatalDomesticViolence().getAggressorTypesList();
                    for (int i = 0; i < aggressorTypesList.size(); i++) {
                        int caso = (int) aggressorTypesList.get(i).getAggressorTypeId();
                        switch (caso) {
                            case 1://isAG1
                                newRowDataTable.setColumn64("SI");
                                break;
                            case 2://isAG2
                                newRowDataTable.setColumn65("SI");
                                break;
                            case 3://isAG3
                                newRowDataTable.setColumn66("SI");
                                break;
                            case 4://isAG4
                                newRowDataTable.setColumn67("SI");
                                break;
                            case 5://isAG5
                                newRowDataTable.setColumn68("SI");
                                break;
                            case 6://isAG6
                                newRowDataTable.setColumn69("SI");
                                break;
                            case 7://isAG7
                                newRowDataTable.setColumn70("SI");
                                break;
                            case 8://isAG8
                                newRowDataTable.setColumn71("SI");
                                break;
                            case 9://isUnknownAG
                                newRowDataTable.setColumn72("SI");
                                break;
                            case 10://isAG10
                                newRowDataTable.setColumn73("SI");
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            //System.out.println("no se cargo violencia intrafamiliar"+e.toString());
        }
        //cargo la lista de abusos(tipos de maltrato)-----------------------------------
        try {
            if (currentNonFatalI.getNonFatalDomesticViolence() != null) {
                if (currentNonFatalI.getNonFatalDomesticViolence().getAbuseTypesList() != null) {
                    List<AbuseTypes> abuseTypesList = currentNonFatalI.getNonFatalDomesticViolence().getAbuseTypesList();
                    for (int i = 0; i < abuseTypesList.size(); i++) {
                        int caso = (int) abuseTypesList.get(i).getAbuseTypeId();
                        switch (caso) {
                            case 1://isMA1
                                newRowDataTable.setColumn74("SI");
                                break;
                            case 2://isMA2
                                newRowDataTable.setColumn75("SI");
                                break;
                            case 3://isMA3
                                newRowDataTable.setColumn76("SI");
                                break;
                            case 4://isMA4
                                newRowDataTable.setColumn77("SI");
                                break;
                            case 5://isMA5
                                newRowDataTable.setColumn78("SI");
                                break;
                            case 6://isMA6
                                newRowDataTable.setColumn79("SI");
                                break;
                            case 7://isUnknowMA
                                newRowDataTable.setColumn80("SI");
                                break;
                            case 8://isMA8
                                newRowDataTable.setColumn81("SI");
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            //System.out.println("no se cargo tipos de maltrato"+e.toString());
        }

        //cargo la lista de abusos(tipos de maltrato)-----------------------------------
        try {

            if (currentNonFatalI.getAnatomicalLocationsList() != null) {
                List<AnatomicalLocations> anatomicalLocationsList = currentNonFatalI.getAnatomicalLocationsList();
                for (int i = 0; i < anatomicalLocationsList.size(); i++) {
                    int caso = (int) anatomicalLocationsList.get(i).getAnatomicalLocationId();
                    switch (caso) {
                        case 1://isAnatomicalSite1
                            newRowDataTable.setColumn82("SI");
                            break;
                        case 2://isAnatomicalSite1
                            newRowDataTable.setColumn83("SI");
                            break;
                        case 3://isAnatomicalSite1
                            newRowDataTable.setColumn84("SI");
                            break;
                        case 4://isAnatomicalSite1
                            newRowDataTable.setColumn85("SI");
                            break;
                        case 5://isAnatomicalSite1
                            newRowDataTable.setColumn86("SI");
                            break;
                        case 6://isAnatomicalSite1
                            newRowDataTable.setColumn87("SI");
                            break;
                        case 7://isAnatomicalSite1
                            newRowDataTable.setColumn88("SI");
                            break;
                        case 8://isAnatomicalSite1
                            newRowDataTable.setColumn89("SI");
                            break;
                        case 9://isAnatomicalSite1
                            newRowDataTable.setColumn90("SI");
                            break;
                        case 10://isAnatomicalSite1
                            newRowDataTable.setColumn91("SI");
                            break;
                        case 11://isAnatomicalSite1    
                            newRowDataTable.setColumn92("SI");
                            break;
                        case 98://checkOtherPlace  otherAnatomicalPlaceDisabled 
                            newRowDataTable.setColumn93("SI");
                            break;
                    }
                }
            }
        } catch (Exception e) {
            //System.out.println("no se cargo sitios anatomicos"+e.toString());
        }
        //cargo la naturaleza de la lesion
        try {

            if (currentNonFatalI.getKindsOfInjuryList() != null) {
                List<KindsOfInjury> kindsOfInjuryList = currentNonFatalI.getKindsOfInjuryList();
                for (int i = 0; i < kindsOfInjuryList.size(); i++) {
                    int caso = (int) kindsOfInjuryList.get(i).getKindInjuryId();
                    switch (caso) {
                        case 1://isNatureOfInjurye1
                            newRowDataTable.setColumn94("SI");
                            break;
                        case 2://isNatureOfInjurye1
                            newRowDataTable.setColumn95("SI");
                            break;
                        case 3://isNatureOfInjurye1
                            newRowDataTable.setColumn96("SI");
                            break;
                        case 4://isNatureOfInjurye1
                            newRowDataTable.setColumn97("SI");
                            break;
                        case 5://isNatureOfInjurye1
                            newRowDataTable.setColumn98("SI");
                            break;
                        case 6://isNatureOfInjurye1
                            newRowDataTable.setColumn99("SI");
                            break;
                        case 7://isNatureOfInjurye1
                            newRowDataTable.setColumn100("SI");
                            break;
                        case 8://isNatureOfInjurye1
                            newRowDataTable.setColumn101("SI");
                            break;
                        case 9://isNatureOfInjurye1
                            newRowDataTable.setColumn102("SI");
                            break;
                        case 98://checkOtherInjury
                            newRowDataTable.setColumn103("SI");
                            break;
                        case 99:// isUnknownNatureOfInjurye
                            newRowDataTable.setColumn104("SI");
                            break;
                    }
                }
            }
        } catch (Exception e) {
            //System.out.println("no se cargo naturaleza de la lesion"+e.toString());
        }
        //cargo los diagnosticos
        try {

            if (currentNonFatalI.getDiagnosesList() != null) {
                List<Diagnoses> diagnosesList = currentNonFatalI.getDiagnosesList();
                for (int i = 0; i < diagnosesList.size(); i++) {
                    switch (i) {
                        case 0:
                            newRowDataTable.setColumn105(diagnosesList.get(i).getDiagnosisName());
                            newRowDataTable.setColumn122(diagnosesList.get(i).getDiagnosisId());
                            break;
                        case 1:
                            newRowDataTable.setColumn106(diagnosesList.get(i).getDiagnosisName());
                            newRowDataTable.setColumn123(diagnosesList.get(i).getDiagnosisId());
                            break;
                        case 2:
                            newRowDataTable.setColumn107(diagnosesList.get(i).getDiagnosisName());
                            newRowDataTable.setColumn124(diagnosesList.get(i).getDiagnosisId());
                            break;
                        case 3:
                            newRowDataTable.setColumn108(diagnosesList.get(i).getDiagnosisName());
                            newRowDataTable.setColumn125(diagnosesList.get(i).getDiagnosisId());
                            break;
                    }
                }
            }
        } catch (Exception e) {
            //System.out.println("no se cargo codigo CIE"+e.toString());
        }

        //------------------------------------------------------------
        //AUTOINFLINGIDA INTENCIONAL
        //------------------------------------------------------------
        try {

            if (currentNonFatalI.getNonFatalSelfInflicted() != null) {
                if (currentNonFatalI.getNonFatalSelfInflicted().getPreviousAttempt() != null) {
                    newRowDataTable.setColumn109(currentNonFatalI.getNonFatalSelfInflicted().getPreviousAttempt().getBooleanName());
                }
            }
        } catch (Exception e) {
        }

        try {
            if (currentNonFatalI.getNonFatalSelfInflicted() != null) {
                if (currentNonFatalI.getNonFatalSelfInflicted().getMentalAntecedent() != null) {
                    newRowDataTable.setColumn110(currentNonFatalI.getNonFatalSelfInflicted().getMentalAntecedent().getBooleanName());
                }
            }
        } catch (Exception e) {
        }

        try {
            if (currentNonFatalI.getNonFatalSelfInflicted() != null) {
                if (currentNonFatalI.getNonFatalSelfInflicted().getPrecipitatingFactorId() != null) {
                    newRowDataTable.setColumn111(currentNonFatalI.getNonFatalSelfInflicted().getPrecipitatingFactorId().getPrecipitatingFactorName());
                }
            }
        } catch (Exception e) {
        }

        //------------------------------------------------------------
        //SE CARGA DATOS PARA TRANSITO
        //------------------------------------------------------------
        try {
            if (currentNonFatalI.getNonFatalTransport() != null) {
                if (currentNonFatalI.getNonFatalTransport().getTransportTypeId() != null) {
                    newRowDataTable.setColumn112(currentNonFatalI.getNonFatalTransport().getTransportTypeId().getTransportTypeName());
                }
            }
        } catch (Exception e) {
        }

        try {
            if (currentNonFatalI.getNonFatalTransport() != null) {
                if (currentNonFatalI.getNonFatalTransport().getTransportCounterpartId() != null) {
                    newRowDataTable.setColumn113(currentNonFatalI.getNonFatalTransport().getTransportCounterpartId().getTransportCounterpartName());
                }
            }
        } catch (Exception e) {
        }

        try {
            if (currentNonFatalI.getNonFatalTransport() != null) {
                if (currentNonFatalI.getNonFatalTransport().getTransportUserId() != null) {
                    newRowDataTable.setColumn114(currentNonFatalI.getNonFatalTransport().getTransportUserId().getTransportUserName());
                }
            }
        } catch (Exception e) {
        }

        try {
            if (currentNonFatalI.getNonFatalTransport() != null) {
                if (currentNonFatalI.getNonFatalTransport().getSecurityElementsList() != null) {
                    List<SecurityElements> securityElementsList = currentNonFatalI.getNonFatalTransport().getSecurityElementsList();
                    for (int i = 0; i < securityElementsList.size(); i++) {
                        switch (securityElementsList.get(i).getSecurityElementId()) {
                            case 1://isBeltUse
                                newRowDataTable.setColumn115("SI");
                                break;
                            case 2://isHelmetUse
                                newRowDataTable.setColumn116("SI");
                                break;
                            case 3://isBicycleHelmetUse
                                newRowDataTable.setColumn117("SI");
                                break;
                            case 4://isVestUse
                                newRowDataTable.setColumn118("SI");
                                break;
                            case 5://isOtherElementUse                        
                                newRowDataTable.setColumn119("SI");
                                break;
                            case 6://currentSecurityElements  "NO";
                                break;
                            case 7://currentSecurityElements = "NO SE SABE";
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            //System.out.println("no se cargo elementos de seguridad"+e.toString());
        }

        return newRowDataTable;
    }

    /**
     * This method is responsible to load all data corresponding to a suicide
     * victim selected for subsequently display the data in a table.
     *
     * @param idVIctim
     * @return
     */
    public RowDataTable loadFatalInjurySuicideRecord(String idVIctim) {
        //CARGO LOS DATOS DE UN SUICIDIO EN UNA FILA PARA LA TABLA
        //btnEditDisabled = true;
        //btnRemoveDisabled = true;

        FatalInjurySuicide currentFatalInjuryS = fatalInjurySuicideFacade.findByIdVictim(idVIctim);
        if (currentFatalInjuryS == null) {
            return null;
        }
        RowDataTable newRowDataTable = new RowDataTable();
        //------------------------------------------------------------
        //SE CARGAN VALORES PARA LA NUEVA VICTIMA
        //------------------------------------------------------------

        //******non_fatal_injury_id
        newRowDataTable.setColumn1(currentFatalInjuryS.getFatalInjuries().getFatalInjuryId().toString());

        //******type_id
        try {
            if (currentFatalInjuryS.getFatalInjuries().getVictimId().getTypeId() != null) {
                newRowDataTable.setColumn2(currentFatalInjuryS.getFatalInjuries().getVictimId().getTypeId().getTypeName());
            }
        } catch (Exception e) {
        }
        //******victim_nid
        try {
            if (currentFatalInjuryS.getFatalInjuries().getVictimId().getVictimNid() != null) {
                newRowDataTable.setColumn3(currentFatalInjuryS.getFatalInjuries().getVictimId().getVictimNid());
            }
        } catch (Exception e) {
        }
        //******victim_name
        try {
            if (currentFatalInjuryS.getFatalInjuries().getVictimId().getVictimName() != null) {
                newRowDataTable.setColumn4(currentFatalInjuryS.getFatalInjuries().getVictimId().getVictimName());
            }
        } catch (Exception e) {
        }
        //******stranger
        try {
            if (currentFatalInjuryS.getFatalInjuries().getVictimId().getStranger() != null) {
                if (currentFatalInjuryS.getFatalInjuries().getVictimId().getStranger() == true) {
                    newRowDataTable.setColumn5("SI");
                } else {
                    newRowDataTable.setColumn5("NO");
                }
            }
        } catch (Exception e) {
        }
        //******age_type_id
        try {
            if (currentFatalInjuryS.getFatalInjuries().getVictimId().getAgeTypeId() != null) {
                newRowDataTable.setColumn6(ageTypesFacade.find(currentFatalInjuryS.getFatalInjuries().getVictimId().getAgeTypeId()).getAgeTypeName());
            }
        } catch (Exception e) {
        }
        //******victim_age
        try {
            if (currentFatalInjuryS.getFatalInjuries().getVictimId().getVictimAge() != null) {
                newRowDataTable.setColumn7(currentFatalInjuryS.getFatalInjuries().getVictimId().getVictimAge().toString());
            }
        } catch (Exception e) {
        }
        //******gender_id
        try {
            if (currentFatalInjuryS.getFatalInjuries().getVictimId().getGenderId() != null) {
                newRowDataTable.setColumn8(currentFatalInjuryS.getFatalInjuries().getVictimId().getGenderId().getGenderName());
            }
        } catch (Exception e) {
        }
        //******job_id
        try {
            if (currentFatalInjuryS.getFatalInjuries().getVictimId().getJobId() != null) {
                newRowDataTable.setColumn9(currentFatalInjuryS.getFatalInjuries().getVictimId().getJobId().getJobName());
            }
        } catch (Exception e) {
        }

        //******vulnerable_group_id
        //******ethnic_group_id        
        //******victim_telephone
        //******victim_address
        //******victim_neighborhood_id
        try {
            if (currentFatalInjuryS.getFatalInjuries().getVictimId().getVictimNeighborhoodId() != null) {
                newRowDataTable.setColumn10(currentFatalInjuryS.getFatalInjuries().getVictimId().getVictimNeighborhoodId().getNeighborhoodName());
                newRowDataTable.setColumn32(String.valueOf(currentFatalInjuryS.getFatalInjuries().getVictimId().getVictimNeighborhoodId().getNeighborhoodSuburb()));
            }
        } catch (Exception e) {
        }

        //******victim_date_of_birth
        //******eps_id
        //******victim_class
        //******victim_id       
        //******residence_municipality
        try {
            if (currentFatalInjuryS.getFatalInjuries().getVictimId().getResidenceDepartment() != null && (currentFatalInjuryS.getFatalInjuries().getVictimId().getResidenceMunicipality() != null)) {
                short departamentId = currentFatalInjuryS.getFatalInjuries().getVictimId().getResidenceDepartment();
                short municipalityId = currentFatalInjuryS.getFatalInjuries().getVictimId().getResidenceMunicipality();
                MunicipalitiesPK mPk = new MunicipalitiesPK(departamentId, municipalityId);
                newRowDataTable.setColumn11(municipalitiesFacade.find(mPk).getMunicipalityName());
            }
        } catch (Exception e) {
        }
        //******residence_department
        try {
            if (currentFatalInjuryS.getFatalInjuries().getVictimId().getResidenceDepartment() != null) {
                newRowDataTable.setColumn12(departamentsFacade.find(currentFatalInjuryS.getFatalInjuries().getVictimId().getResidenceDepartment()).getDepartamentName());
            }
        } catch (Exception e) {
        }

        //******insurance_id
        //------------------------------------------------------------
        //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA FATAL
        //------------------------------------------------------------
        //******injury_id
        //******injury_date
        try {
            if (currentFatalInjuryS.getFatalInjuries().getInjuryDate() != null) {
                newRowDataTable.setColumn13(sdf.format(currentFatalInjuryS.getFatalInjuries().getInjuryDate()));
            }
        } catch (Exception e) {
        }
        //******injury_time
        try {
            if (currentFatalInjuryS.getFatalInjuries().getInjuryTime() != null) {
                hours = String.valueOf(currentFatalInjuryS.getFatalInjuries().getInjuryTime().getHours());
                minutes = String.valueOf(currentFatalInjuryS.getFatalInjuries().getInjuryTime().getMinutes());
                if (hours.length() != 2) {
                    hours = "0" + hours;
                }
                if (minutes.length() != 2) {
                    minutes = "0" + minutes;
                }
                newRowDataTable.setColumn14(hours + minutes);
            }
        } catch (Exception e) {
        }
        //******injury_address
        if (currentFatalInjuryS.getFatalInjuries().getInjuryAddress() != null) {
            newRowDataTable.setColumn15(currentFatalInjuryS.getFatalInjuries().getInjuryAddress());
        }
        //******injury_neighborhood_id
        try {
            if (currentFatalInjuryS.getFatalInjuries().getInjuryNeighborhoodId() != null) {
                newRowDataTable.setColumn16(neighborhoodsFacade.find(currentFatalInjuryS.getFatalInjuries().getInjuryNeighborhoodId()).getNeighborhoodName());
                newRowDataTable.setColumn33(String.valueOf(neighborhoodsFacade.find(currentFatalInjuryS.getFatalInjuries().getInjuryNeighborhoodId()).getNeighborhoodSuburb()));
            }
        } catch (Exception e) {
        }
        //******quadrant_id
        try {
            if (currentFatalInjuryS.getFatalInjuries().getQuadrantId() != null) {
                newRowDataTable.setColumn34(currentFatalInjuryS.getFatalInjuries().getQuadrantId().getQuadrantName());
            }
        } catch (Exception e) {
        }
        //******injury_place_id
        try {
            if (currentFatalInjuryS.getFatalInjuries().getInjuryPlaceId() != null) {
                newRowDataTable.setColumn17(currentFatalInjuryS.getFatalInjuries().getInjuryPlaceId().getPlaceName());
            }
        } catch (Exception e) {
        }

        //******victim_number
        try {
            if (currentFatalInjuryS.getFatalInjuries().getVictimNumber() != null) {
                newRowDataTable.setColumn18(currentFatalInjuryS.getFatalInjuries().getVictimNumber().toString());

            }
        } catch (Exception e) {
        }
        //******injury_description
        if (currentFatalInjuryS.getFatalInjuries().getInjuryDescription() != null) {
            newRowDataTable.setColumn19(currentFatalInjuryS.getFatalInjuries().getInjuryDescription());
        }
        //******user_id	
        //******input_timestamp	
        //******injury_day_of_week
        try {
            if (currentFatalInjuryS.getFatalInjuries().getInjuryDayOfWeek() != null) {
                newRowDataTable.setColumn20(currentFatalInjuryS.getFatalInjuries().getInjuryDayOfWeek());
            }
        } catch (Exception e) {
        }

        //******victim_id
        //******fatal_injury_id
        //******alcohol_level_victim
        try {
            if (currentFatalInjuryS.getFatalInjuries().getAlcoholLevelVictim() != null) {
                newRowDataTable.setColumn21(currentFatalInjuryS.getFatalInjuries().getAlcoholLevelVictim().toString());
            }
        } catch (Exception e) {
        }
        //******alcohol_level_victimId
        try {
            if (currentFatalInjuryS.getFatalInjuries().getAlcoholLevelVictimId() != null) {
                newRowDataTable.setColumn22(currentFatalInjuryS.getFatalInjuries().getAlcoholLevelVictimId().getAlcoholLevelName());
            }
        } catch (Exception e) {
        }
        //******code
        try {
            if (currentFatalInjuryS.getFatalInjuries().getCode() != null) {
                newRowDataTable.setColumn23(currentFatalInjuryS.getFatalInjuries().getCode());
            }
        } catch (Exception e) {
        }
        //******area_id
        try {
            if (currentFatalInjuryS.getFatalInjuries().getAreaId() != null) {
                newRowDataTable.setColumn24(currentFatalInjuryS.getFatalInjuries().getAreaId().getAreaName());
            }
        } catch (Exception e) {
        }
        //******victim_place_of_origin
        try {
            if (currentFatalInjuryS.getFatalInjuries().getVictimPlaceOfOrigin() != null) {
                String source = currentFatalInjuryS.getFatalInjuries().getVictimPlaceOfOrigin();
                String[] sourceSplit = source.split("-");
                //determino pais
                newRowDataTable.setColumn25(countriesFacade.find(Short.parseShort(sourceSplit[0])).getName());
                if (Short.parseShort(sourceSplit[0]) == 52) {//colombia
                    newRowDataTable.setColumn26(departamentsFacade.find(Short.parseShort(sourceSplit[1])).getDepartamentName());
                    MunicipalitiesPK municipalitiesPK = new MunicipalitiesPK(Short.parseShort(sourceSplit[1]), Short.parseShort(sourceSplit[2]));
                    newRowDataTable.setColumn27(municipalitiesFacade.find(municipalitiesPK).getMunicipalityName());
                }
            }
        } catch (Exception e) {
        }
        //------------------------------------------------------------
        //SE CARGA DATOS PARA LA NUEVA LESION FATAL POR SUICIDIO
        //------------------------------------------------------------

        //******previous_attempt
        try {
            if (currentFatalInjuryS.getPreviousAttempt() != null) {
                newRowDataTable.setColumn28(currentFatalInjuryS.getPreviousAttempt().getBooleanName());
            }
        } catch (Exception e) {
        }
        //******mental_antecedent
        try {
            if (currentFatalInjuryS.getMentalAntecedent() != null) {
                newRowDataTable.setColumn29(currentFatalInjuryS.getMentalAntecedent().getBooleanName());
            }
        } catch (Exception e) {
        }
        //******related_event_id
        try {
            if (currentFatalInjuryS.getRelatedEventId() != null) {
                newRowDataTable.setColumn30(currentFatalInjuryS.getRelatedEventId().getRelatedEventName());
            }
        } catch (Exception e) {
        }
        //******suicide_death_mechanism_id
        try {
            if (currentFatalInjuryS.getSuicideDeathMechanismId() != null) {
                newRowDataTable.setColumn31(currentFatalInjuryS.getSuicideDeathMechanismId().getSuicideMechanismName());
            }
        } catch (Exception e) {
        }
        //******fatal_injury_id

        return newRowDataTable;
    }

    /**
     * This method is responsible to load all data corresponding to a traffic
     * accident victim selected for subsequently display the data in a table.
     *
     * @param idVIctim
     * @return
     */
    public RowDataTable loadFatalInjuryTraafficRecord(String idVIctim) {
        //CARGO LOS DATOS ACCIDENTE FALTAL POR TRANSITO
        //btnEditDisabled = true;
        //btnRemoveDisabled = true;
        FatalInjuryTraffic currentFatalInjuryT = fatalInjuryTrafficFacade.findByIdVictim(idVIctim);
        if (currentFatalInjuryT == null) {
            return null;
        }
        RowDataTable newRowDataTable = new RowDataTable();
        //------------------------------------------------------------
        //SE CARGAN VALORES PARA LA NUEVA VICTIMA
        //------------------------------------------------------------

        //******non_fatal_injury_id
        newRowDataTable.setColumn1(currentFatalInjuryT.getFatalInjuries().getFatalInjuryId().toString());

        //******type_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getTypeId() != null) {
                newRowDataTable.setColumn2(currentFatalInjuryT.getFatalInjuries().getVictimId().getTypeId().getTypeName());
            }
        } catch (Exception e) {
        }
        //******victim_nid
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getVictimNid() != null) {
                newRowDataTable.setColumn3(currentFatalInjuryT.getFatalInjuries().getVictimId().getVictimNid());
            }
        } catch (Exception e) {
        }
        //******victim_name
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getVictimName() != null) {
                newRowDataTable.setColumn4(currentFatalInjuryT.getFatalInjuries().getVictimId().getVictimName());
            }
        } catch (Exception e) {
        }
        //******stranger
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getStranger() != null) {
                if (currentFatalInjuryT.getFatalInjuries().getVictimId().getStranger() == true) {
                    newRowDataTable.setColumn5("SI");
                } else {
                    newRowDataTable.setColumn5("NO");
                }
            }
        } catch (Exception e) {
        }
        //******age_type_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getAgeTypeId() != null) {
                newRowDataTable.setColumn6(ageTypesFacade.find(currentFatalInjuryT.getFatalInjuries().getVictimId().getAgeTypeId()).getAgeTypeName());
            }
        } catch (Exception e) {
        }
        //******victim_age
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getVictimAge() != null) {
                newRowDataTable.setColumn7(currentFatalInjuryT.getFatalInjuries().getVictimId().getVictimAge().toString());
            }
        } catch (Exception e) {
        }
        //******gender_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getGenderId() != null) {
                newRowDataTable.setColumn8(currentFatalInjuryT.getFatalInjuries().getVictimId().getGenderId().getGenderName());
            }
        } catch (Exception e) {
        }
        //******job_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getJobId() != null) {
                newRowDataTable.setColumn9(currentFatalInjuryT.getFatalInjuries().getVictimId().getJobId().getJobName());
            }
        } catch (Exception e) {
        }

        //******vulnerable_group_id
        //******ethnic_group_id        
        //******victim_telephone
        //******victim_address
        //******victim_neighborhood_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getVictimNeighborhoodId() != null) {
                newRowDataTable.setColumn10(currentFatalInjuryT.getFatalInjuries().getVictimId().getVictimNeighborhoodId().getNeighborhoodName());
                newRowDataTable.setColumn43(String.valueOf(currentFatalInjuryT.getFatalInjuries().getVictimId().getVictimNeighborhoodId().getNeighborhoodSuburb()));
            }
        } catch (Exception e) {
        }

        //******victim_date_of_birth
        //******eps_id
        //******victim_class
        //******victim_id       
        //******residence_municipality
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getResidenceDepartment() != null && (currentFatalInjuryT.getFatalInjuries().getVictimId().getResidenceMunicipality() != null)) {
                short departamentId = currentFatalInjuryT.getFatalInjuries().getVictimId().getResidenceDepartment();
                short municipalityId = currentFatalInjuryT.getFatalInjuries().getVictimId().getResidenceMunicipality();
                MunicipalitiesPK mPk = new MunicipalitiesPK(departamentId, municipalityId);
                newRowDataTable.setColumn11(municipalitiesFacade.find(mPk).getMunicipalityName());
            }
        } catch (Exception e) {
        }
        //******residence_department
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getResidenceDepartment() != null) {
                newRowDataTable.setColumn12(departamentsFacade.find(currentFatalInjuryT.getFatalInjuries().getVictimId().getResidenceDepartment()).getDepartamentName());
            }
        } catch (Exception e) {
        }

        //******insurance_id
        //------------------------------------------------------------
        //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA FATAL
        //------------------------------------------------------------
        //******injury_id
        //******injury_date
        try {
            if (currentFatalInjuryT.getFatalInjuries().getInjuryDate() != null) {
                newRowDataTable.setColumn13(sdf.format(currentFatalInjuryT.getFatalInjuries().getInjuryDate()));
            }
        } catch (Exception e) {
        }
        //******injury_time
        try {
            if (currentFatalInjuryT.getFatalInjuries().getInjuryTime() != null) {
                hours = String.valueOf(currentFatalInjuryT.getFatalInjuries().getInjuryTime().getHours());
                minutes = String.valueOf(currentFatalInjuryT.getFatalInjuries().getInjuryTime().getMinutes());
                if (hours.length() != 2) {
                    hours = "0" + hours;
                }
                if (minutes.length() != 2) {
                    minutes = "0" + minutes;
                }
                newRowDataTable.setColumn14(hours + minutes);
            }
        } catch (Exception e) {
        }
        //******injury_address
        if (currentFatalInjuryT.getFatalInjuries().getInjuryAddress() != null) {
            newRowDataTable.setColumn15(currentFatalInjuryT.getFatalInjuries().getInjuryAddress());
        }
        //******injury_neighborhood_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getInjuryNeighborhoodId() != null) {
                newRowDataTable.setColumn16(neighborhoodsFacade.find(currentFatalInjuryT.getFatalInjuries().getInjuryNeighborhoodId()).getNeighborhoodName());
                newRowDataTable.setColumn44(String.valueOf(neighborhoodsFacade.find(currentFatalInjuryT.getFatalInjuries().getInjuryNeighborhoodId()).getNeighborhoodSuburb()));
            }
        } catch (Exception e) {
        }
        //******quadrant_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getQuadrantId() != null) {
                newRowDataTable.setColumn45(currentFatalInjuryT.getFatalInjuries().getQuadrantId().getQuadrantName());
            }
        } catch (Exception e) {
        }
        //******injury_place_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getInjuryPlaceId() != null) {
                newRowDataTable.setColumn17(currentFatalInjuryT.getFatalInjuries().getInjuryPlaceId().getPlaceName());
            }
        } catch (Exception e) {
        }

        //******victim_number
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimNumber() != null) {
                newRowDataTable.setColumn18(currentFatalInjuryT.getFatalInjuries().getVictimNumber().toString());

            }
        } catch (Exception e) {
        }
        //******injury_description
        if (currentFatalInjuryT.getFatalInjuries().getInjuryDescription() != null) {
            newRowDataTable.setColumn19(currentFatalInjuryT.getFatalInjuries().getInjuryDescription());
        }
        //******user_id	
        //******input_timestamp	
        //******injury_day_of_week
        try {
            if (currentFatalInjuryT.getFatalInjuries().getInjuryDayOfWeek() != null) {
                newRowDataTable.setColumn20(currentFatalInjuryT.getFatalInjuries().getInjuryDayOfWeek());
            }
        } catch (Exception e) {
        }

        //******victim_id
        //******fatal_injury_id
        //******alcohol_level_victim
        try {
            if (currentFatalInjuryT.getFatalInjuries().getAlcoholLevelVictim() != null) {
                newRowDataTable.setColumn21(currentFatalInjuryT.getFatalInjuries().getAlcoholLevelVictim().toString());
            }
        } catch (Exception e) {
        }
        //******alcohol_level_victimId
        try {
            if (currentFatalInjuryT.getFatalInjuries().getAlcoholLevelVictimId() != null) {
                newRowDataTable.setColumn22(currentFatalInjuryT.getFatalInjuries().getAlcoholLevelVictimId().getAlcoholLevelName());
            }
        } catch (Exception e) {
        }
        //******code
        try {
            if (currentFatalInjuryT.getFatalInjuries().getCode() != null) {
                newRowDataTable.setColumn23(currentFatalInjuryT.getFatalInjuries().getCode());
            }
        } catch (Exception e) {
        }
        //******area_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getAreaId() != null) {
                newRowDataTable.setColumn24(currentFatalInjuryT.getFatalInjuries().getAreaId().getAreaName());
            }
        } catch (Exception e) {
        }
        //******victim_place_of_origin
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimPlaceOfOrigin() != null) {
                String source = currentFatalInjuryT.getFatalInjuries().getVictimPlaceOfOrigin();
                String[] sourceSplit = source.split("-");
                //determino pais
                newRowDataTable.setColumn25(countriesFacade.find(Short.parseShort(sourceSplit[0])).getName());
                if (Short.parseShort(sourceSplit[0]) == 52) {//colombia
                    newRowDataTable.setColumn26(departamentsFacade.find(Short.parseShort(sourceSplit[1])).getDepartamentName());
                    MunicipalitiesPK municipalitiesPK = new MunicipalitiesPK(Short.parseShort(sourceSplit[1]), Short.parseShort(sourceSplit[2]));
                    newRowDataTable.setColumn27(municipalitiesFacade.find(municipalitiesPK).getMunicipalityName());
                }
            }
        } catch (Exception e) {
        }
        //------------------------------------------------------------
        //SE CARGA DATOS PARA LA NUEVA LESION FATAL POR ACCIDENTE DE TRANSITO
        //------------------------------------------------------------
        //cargar vehiculos de la contraparte
        List<CounterpartInvolvedVehicle> involvedVehiclesList = currentFatalInjuryT.getFatalInjuries().getCounterpartInvolvedVehicleList();
        if (involvedVehiclesList != null) {
            for (int i = 0; i < involvedVehiclesList.size(); i++) {
                if (i == 0) {
                    newRowDataTable.setColumn28(involvedVehiclesList.get(0).getInvolvedVehicleId().getInvolvedVehicleName());
                }
                if (i == 1) {
                    newRowDataTable.setColumn29(involvedVehiclesList.get(1).getInvolvedVehicleId().getInvolvedVehicleName());
                }
                if (i == 2) {
                    newRowDataTable.setColumn30(involvedVehiclesList.get(2).getInvolvedVehicleId().getInvolvedVehicleName());
                }
            }
        }

        //cargar tipo de servcio de la contraparte
        List<CounterpartServiceType> serviceTypesList = currentFatalInjuryT.getFatalInjuries().getCounterpartServiceTypeList();

        if (involvedVehiclesList != null) {
            for (int i = 0; i < serviceTypesList.size(); i++) {
                if (i == 0) {
                    newRowDataTable.setColumn31(serviceTypesList.get(0).getServiceTypeId().getServiceTypeName());
                }
                if (i == 1) {
                    newRowDataTable.setColumn32(serviceTypesList.get(1).getServiceTypeId().getServiceTypeName());
                }
                if (i == 2) {
                    newRowDataTable.setColumn33(serviceTypesList.get(2).getServiceTypeId().getServiceTypeName());
                }
            }
        }

        //******number_non_fatal_victims
        if (currentFatalInjuryT.getNumberNonFatalVictims() != null) {
            newRowDataTable.setColumn34(currentFatalInjuryT.getNumberNonFatalVictims().toString());
        }
        //******victim_characteristic_id
        if (currentFatalInjuryT.getVictimCharacteristicId() != null) {
            newRowDataTable.setColumn35(currentFatalInjuryT.getVictimCharacteristicId().getCharacteristicName());
        }
        //******protection_measure_id
        if (currentFatalInjuryT.getProtectionMeasureId() != null) {
            newRowDataTable.setColumn36(currentFatalInjuryT.getProtectionMeasureId().getProtectiveMeasuresName());
        }
        //******road_type_id
        if (currentFatalInjuryT.getRoadTypeId() != null) {
            newRowDataTable.setColumn37(currentFatalInjuryT.getRoadTypeId().getRoadTypeName());
        }
        //******accident_class_id
        if (currentFatalInjuryT.getAccidentClassId() != null) {
            newRowDataTable.setColumn38(currentFatalInjuryT.getAccidentClassId().getAccidentClassName());
        }
        //******involved_vehicle_id
        if (currentFatalInjuryT.getInvolvedVehicleId() != null) {
            newRowDataTable.setColumn39(currentFatalInjuryT.getInvolvedVehicleId().getInvolvedVehicleName());
        }
        //******service_type_id
        if (currentFatalInjuryT.getServiceTypeId() != null) {
            newRowDataTable.setColumn40(currentFatalInjuryT.getServiceTypeId().getServiceTypeName());
        }

        //******alcohol_level_counterpart
        try {
            if (currentFatalInjuryT.getAlcoholLevelCounterpart() != null) {
                newRowDataTable.setColumn41(currentFatalInjuryT.getAlcoholLevelCounterpart().toString());
            }
        } catch (Exception e) {
        }
        //******alcohol_level_counterpart_id
        try {
            if (currentFatalInjuryT.getAlcoholLevelCounterpartId() != null) {
                newRowDataTable.setColumn42(currentFatalInjuryT.getAlcoholLevelCounterpartId().getAlcoholLevelName());
            }
        } catch (Exception e) {
        }
        //******fatal_injury_id

        return newRowDataTable;
    }

    /**
     * This method is responsible to load all data corresponding to a domestic
     * violence victim selected for subsequently display the data in a table.
     *
     * @param idVIctim
     * @return
     */
    public RowDataTable loadNonFatalDomesticViolenceRecord(String idVIctim) {
        //CARGO LOS DATOS DE UNA DETERMINADA LESION VIF EN UNA FILA PARA LA TABLA

        NonFatalDomesticViolence currentNonFatalDomesticV = nonFatalDomesticViolenceFacade.findByIdVictim(idVIctim);
        if (currentNonFatalDomesticV == null) {
            return null;
        }
        RowDataTable newRowDataTable = new RowDataTable();
        //------------------------------------------------------------
        //SE CARGAN VALORES PARA LA NUEVA VICTIMA
        //------------------------------------------------------------

        //******non_fatal_injury_id
        newRowDataTable.setColumn1(currentNonFatalDomesticV.getNonFatalInjuries().getNonFatalInjuryId().toString());
        //******type_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getTypeId() != null) {
                newRowDataTable.setColumn2(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getTypeId().getTypeName());
            }
        } catch (Exception e) {
        }
        //******victim_nid
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimNid() != null) {
                newRowDataTable.setColumn3(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimNid());
            }
        } catch (Exception e) {
        }
        //******victim_name
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimName() != null) {
                newRowDataTable.setColumn4(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimName());
            }
        } catch (Exception e) {
        }
        //******stranger
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getStranger() != null) {
                if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getStranger() == true) {
                    newRowDataTable.setColumn5("SI");
                } else {
                    newRowDataTable.setColumn5("NO");
                }
            }
        } catch (Exception e) {
        }
        //******age_type_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getAgeTypeId() != null) {
                newRowDataTable.setColumn6(ageTypesFacade.find(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getAgeTypeId()).getAgeTypeName());
            }
        } catch (Exception e) {
        }
        //******victim_age
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimAge() != null) {
                newRowDataTable.setColumn7(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimAge().toString());
            }
        } catch (Exception e) {
        }
        //******gender_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getGenderId() != null) {
                newRowDataTable.setColumn8(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getGenderId().getGenderName());
            }
        } catch (Exception e) {
        }
        //******job_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getJobId() != null) {
                newRowDataTable.setColumn9(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getJobId().getJobName());
            }
        } catch (Exception e) {
        }

        //******ethnic_group_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getEthnicGroupId() != null) {
                newRowDataTable.setColumn10(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getEthnicGroupId().getEthnicGroupName());
            }
        } catch (Exception e) {
        }
        //******victim_telephone
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimTelephone() != null) {
                newRowDataTable.setColumn11(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimTelephone());
            }
        } catch (Exception e) {
        }

        //******victim_date_of_birth
        //******eps_id
        //******victim_class
        //******victim_id        
        //******residence_municipality
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getResidenceDepartment() != null && (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getResidenceMunicipality() != null)) {
                short departamentId = currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getResidenceDepartment();
                short municipalityId = currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getResidenceMunicipality();
                MunicipalitiesPK mPk = new MunicipalitiesPK(departamentId, municipalityId);
                newRowDataTable.setColumn12(municipalitiesFacade.find(mPk).getMunicipalityName());
            }
        } catch (Exception e) {
        }
        //******residence_department
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getResidenceDepartment() != null) {
                newRowDataTable.setColumn13(departamentsFacade.find(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getResidenceDepartment()).getDepartamentName());
            }
        } catch (Exception e) {
        }
        //******victim_address
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimAddress() != null) {
                newRowDataTable.setColumn14(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimAddress());
            }
        } catch (Exception e) {
        }
        //******victim_neighborhood_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimNeighborhoodId() != null) {
                newRowDataTable.setColumn15(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimNeighborhoodId().getNeighborhoodName());
                newRowDataTable.setColumn81(String.valueOf(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimNeighborhoodId().getNeighborhoodSuburb()));
            }
        } catch (Exception e) {
        }
        //informacion de grupos vunerables
        if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVulnerableGroupsList() != null) {
            if (!currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVulnerableGroupsList().isEmpty()) {
                newRowDataTable.setColumn16(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVulnerableGroupsList().get(0).getVulnerableGroupName());
            }
        }

        //******insurance_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getInsuranceId() != null) {
                newRowDataTable.setColumn18(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getInsuranceId().getInsuranceName());
            }
        } catch (Exception e) {
        }

        //-----CARGAR CAMPOS OTROS----------------
        if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getOthersList() != null) {
            List<Others> othersList = currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getOthersList();
            for (int i = 0; i < othersList.size(); i++) {
                switch (othersList.get(i).getOthersPK().getFieldId()) {
                    case 1://1.	Otro lugar del hecho
                        newRowDataTable.setColumn19(othersList.get(i).getValueText());
                        break;
                    case 2://2.	Otra actividad
                        newRowDataTable.setColumn20(othersList.get(i).getValueText());
                        break;
                    case 3://3.	Altura
                        newRowDataTable.setColumn21(othersList.get(i).getValueText());
                        break;
                    case 4://4.	Cual polvora			
                        newRowDataTable.setColumn22(othersList.get(i).getValueText());
                        break;
                    case 5://5.	Cual desastre natural
                        newRowDataTable.setColumn23(othersList.get(i).getValueText());
                        break;
                    case 6://6.	Otro mecanismo objeto
                        newRowDataTable.setColumn24(othersList.get(i).getValueText());
                        break;
                    case 7://7.	Animal cual
                        newRowDataTable.setColumn25(othersList.get(i).getValueText());
                        break;
                    case 8://8.	Otro grupo étnico
                        newRowDataTable.setColumn26(othersList.get(i).getValueText());
                        break;
                    case 9://9.	Otro grupo vulnerable
                        newRowDataTable.setColumn27(othersList.get(i).getValueText());
                        break;
                    case 10://10.	Otro tipo de agresor
                        newRowDataTable.setColumn28(othersList.get(i).getValueText());
                        break;
                    case 11://11.	Otro tipo de maltrato
                        newRowDataTable.setColumn29(othersList.get(i).getValueText());
                        break;
                    case 12://12.	Otro tipo de agresion
                        newRowDataTable.setColumn30(othersList.get(i).getValueText());
                        break;
                }
            }
        }

        //------------------------------------------------------------
        //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA NO FATAL
        //------------------------------------------------------------        
        //******checkup_date
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getCheckupDate() != null) {
                newRowDataTable.setColumn31(sdf.format(currentNonFatalDomesticV.getNonFatalInjuries().getCheckupDate()));
            }
        } catch (Exception e) {
        }
        //******checkup_time
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getCheckupTime() != null) {
                hours = String.valueOf(currentNonFatalDomesticV.getNonFatalInjuries().getCheckupTime().getHours());
                minutes = String.valueOf(currentNonFatalDomesticV.getNonFatalInjuries().getCheckupTime().getMinutes());
                if (hours.length() != 2) {
                    hours = "0" + hours;
                }
                if (minutes.length() != 2) {
                    minutes = "0" + minutes;
                }
                newRowDataTable.setColumn32(hours + minutes);
            }
        } catch (Exception e) {
        }
        //******injury_date
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getInjuryDate() != null) {
                newRowDataTable.setColumn33(sdf.format(currentNonFatalDomesticV.getNonFatalInjuries().getInjuryDate()));
            }
        } catch (Exception e) {
        }
        //******injury_time
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getInjuryTime() != null) {
                hours = String.valueOf(currentNonFatalDomesticV.getNonFatalInjuries().getInjuryTime().getHours());
                minutes = String.valueOf(currentNonFatalDomesticV.getNonFatalInjuries().getInjuryTime().getMinutes());
                if (hours.length() != 2) {
                    hours = "0" + hours;
                }
                if (minutes.length() != 2) {
                    minutes = "0" + minutes;
                }
                newRowDataTable.setColumn34(hours + minutes);
            }
        } catch (Exception e) {
        }
        //******injury_address
        if (currentNonFatalDomesticV.getNonFatalInjuries().getInjuryAddress() != null) {
            newRowDataTable.setColumn35(currentNonFatalDomesticV.getNonFatalInjuries().getInjuryAddress());
        }
        //******injury_neighborhood_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getInjuryNeighborhoodId() != null) {
                newRowDataTable.setColumn36(currentNonFatalDomesticV.getNonFatalInjuries().getInjuryNeighborhoodId().getNeighborhoodName());
                newRowDataTable.setColumn82(String.valueOf(currentNonFatalDomesticV.getNonFatalInjuries().getInjuryNeighborhoodId().getNeighborhoodSuburb()));
            }
        } catch (Exception e) {
        }
        //******quadrant_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getQuadrantId() != null) {
                newRowDataTable.setColumn45(currentNonFatalDomesticV.getNonFatalInjuries().getQuadrantId().getQuadrantName());
            }
        } catch (Exception e) {
        }
        //******injury_place_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getInjuryPlaceId() != null) {
                newRowDataTable.setColumn37(currentNonFatalDomesticV.getNonFatalInjuries().getInjuryPlaceId().getNonFatalPlaceName());
            }
        } catch (Exception e) {
        }
        //******activity_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getActivityId() != null) {
                newRowDataTable.setColumn38(currentNonFatalDomesticV.getNonFatalInjuries().getActivityId().getActivityName());
            }
        } catch (Exception e) {
        }
        //******intentionality_id        
        //******use_alcohol_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getUseAlcoholId() != null) {
                newRowDataTable.setColumn39(currentNonFatalDomesticV.getNonFatalInjuries().getUseAlcoholId().getUseAlcoholDrugsName());
            }
        } catch (Exception e) {
        }
        //******use_drugs_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getUseDrugsId() != null) {
                newRowDataTable.setColumn40(currentNonFatalDomesticV.getNonFatalInjuries().getUseDrugsId().getUseAlcoholDrugsName());
            }
        } catch (Exception e) {
        }
        //******burn_injury_degree
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getBurnInjuryDegree() != null) {
                newRowDataTable.setColumn41(currentNonFatalDomesticV.getNonFatalInjuries().getBurnInjuryDegree().toString());
            }
        } catch (Exception e) {
        }
        //******burn_injury_percentage
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getBurnInjuryPercentage() != null) {
                newRowDataTable.setColumn42(currentNonFatalDomesticV.getNonFatalInjuries().getBurnInjuryPercentage().toString());
            }
        } catch (Exception e) {
        }
        //******submitted_patient

        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getSubmittedPatient() != null) {
                newRowDataTable.setColumn43(currentNonFatalDomesticV.getNonFatalInjuries().getSubmittedPatient().toString());
            }
        } catch (Exception e) {
        }
        //******eps_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getSubmittedDataSourceId() != null) {
                newRowDataTable.setColumn44(currentNonFatalDomesticV.getNonFatalInjuries().getSubmittedDataSourceId().getNonFatalDataSourceName());
            }
        } catch (Exception e) {
        }
        //******destination_patient_id        
        //******input_timestamp
//        try {
//            if (currentNonFatalDomesticV.getNonFatalInjuries().getInputTimestamp() != null) {
//                newRowDataTable.setColumn45(sdf2.format(currentNonFatalDomesticV.getNonFatalInjuries().getInputTimestamp()));
//            }
//        } catch (Exception e) {
//        }
        //******health_professional_id        
        //******non_fatal_data_source_id
        //******mechanism_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getMechanismId() != null) {
                newRowDataTable.setColumn46(currentNonFatalDomesticV.getNonFatalInjuries().getMechanismId().getMechanismName());
            }
        } catch (Exception e) {
        }
        //******user_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getUserId() != null) {
                newRowDataTable.setColumn47(currentNonFatalDomesticV.getNonFatalInjuries().getUserId().getUserName());
            }
        } catch (Exception e) {
        }
        //******injury_day_of_week
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getInjuryDayOfWeek() != null) {
                newRowDataTable.setColumn48(currentNonFatalDomesticV.getNonFatalInjuries().getInjuryDayOfWeek());
            }
        } catch (Exception e) {
        }
        //******non_fatal_data_source_id        
        //******injury_id        

        //------------------------------------------------------------
        //SE CARGA DATOS PARA VIOLENCIA INTRAFAMILIAR
        //------------------------------------------------------------
        //cargo la lista de agresores-----------------------------------
        try {
            if (currentNonFatalDomesticV.getAggressorTypesList() != null) {
                if (currentNonFatalDomesticV.getAggressorTypesList() != null) {
                    List<AggressorTypes> aggressorTypesList = currentNonFatalDomesticV.getAggressorTypesList();
                    for (int i = 0; i < aggressorTypesList.size(); i++) {
                        int caso = (int) aggressorTypesList.get(i).getAggressorTypeId();
                        switch (caso) {
                            case 1://isAG1
                                newRowDataTable.setColumn49("SI");
                                break;
                            case 2://isAG2
                                newRowDataTable.setColumn50("SI");
                                break;
                            case 3://isAG3
                                newRowDataTable.setColumn51("SI");
                                break;
                            case 4://isAG4
                                newRowDataTable.setColumn52("SI");
                                break;
                            case 5://isAG5
                                newRowDataTable.setColumn53("SI");
                                break;
                            case 6://isAG6
                                newRowDataTable.setColumn54("SI");
                                break;
                            case 7://isAG7
                                newRowDataTable.setColumn55("SI");
                                break;
                            case 8://isAG8
                                newRowDataTable.setColumn56("SI");
                                break;
                            case 9://isUnknownAG
                                newRowDataTable.setColumn57("SI");
                                break;
                            case 10://isAG10
                                newRowDataTable.setColumn58("SI");
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            //System.out.println("no se cargo violencia intrafamiliar"+e.toString());
        }
        //cargo la lista de abusos(tipos de maltrato)-----------------------------------
        try {
            if (currentNonFatalDomesticV.getAbuseTypesList() != null) {
                if (currentNonFatalDomesticV.getAbuseTypesList() != null) {
                    List<AbuseTypes> abuseTypesList = currentNonFatalDomesticV.getAbuseTypesList();
                    for (int i = 0; i < abuseTypesList.size(); i++) {
                        int caso = (int) abuseTypesList.get(i).getAbuseTypeId();
                        switch (caso) {
                            case 1://isMA1
                                newRowDataTable.setColumn59("SI");
                                break;
                            case 2://isMA2
                                newRowDataTable.setColumn60("SI");
                                break;
                            case 3://isMA3
                                newRowDataTable.setColumn61("SI");
                                break;
                            case 4://isMA4
                                newRowDataTable.setColumn62("SI");
                                break;
                            case 5://isMA5
                                newRowDataTable.setColumn63("SI");
                                break;
                            case 6://isMA6
                                newRowDataTable.setColumn64("SI");
                                break;
                            case 7://isUnknowMA
                                newRowDataTable.setColumn65("SI");
                                break;
                            case 8://isMA8
                                newRowDataTable.setColumn66("SI");
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            //System.out.println("no se cargo tipos de maltrato"+e.toString());
        }

        //cargo la lista de acciones a realizar
        try {
            if (currentNonFatalDomesticV.getActionsToTakeList() != null) {
                List<ActionsToTake> actionsToTakesList = currentNonFatalDomesticV.getActionsToTakeList();
                for (int i = 0; i < actionsToTakesList.size(); i++) {
                    int caso = (int) actionsToTakesList.get(i).getActionId();
                    switch (caso) {
                        case 1://AR1 Conciliación
                            newRowDataTable.setColumn67("SI");
                            break;
                        case 2://AR2 Caución
                            newRowDataTable.setColumn68("SI");
                            break;
                        case 3://AR3 Dictamén Medicina Legal
                            newRowDataTable.setColumn69("SI");
                            break;
                        case 4://AR4 Remisión a Fiscalía
                            newRowDataTable.setColumn70("SI");
                            break;
                        case 5://AR5 Remisión a Medicina Legal
                            newRowDataTable.setColumn71("SI");
                            break;
                        case 6://AR6 Remisión a Comisaría de Familia
                            newRowDataTable.setColumn72("SI");
                            break;
                        case 7://AR7 Remisión a ICBF
                            newRowDataTable.setColumn73("SI");
                            break;
                        case 8://AR8 Medidas de protección
                            newRowDataTable.setColumn74("SI");
                            break;
                        case 9://AR9 Remisión a salud
                            newRowDataTable.setColumn75("SI");
                            break;
                        case 10://AR10 Atención psicosocial
                            newRowDataTable.setColumn76("SI");
                            break;
                        case 11://AR11 Restablecimiento de derechos
                            newRowDataTable.setColumn77("SI");
                            break;
                        case 12://AR12 Otro, cual?
                            newRowDataTable.setColumn78("SI");
                            break;
                        case 13://AR13 Sin dato
                            newRowDataTable.setColumn79("SI");
                            break;
                    }
                }
            }
        } catch (Exception e) {
            //System.out.println("no se cargo naturaleza de la lesion"+e.toString());
        }

        if (currentNonFatalDomesticV.getDomesticViolenceDataSourceId() != null) {
            newRowDataTable.setColumn80(currentNonFatalDomesticV.getDomesticViolenceDataSourceId().getNonFatalDataSourceName());
        }

        return newRowDataTable;
    }

    /**
     * This method is responsible to load all data corresponding to a sivigila
     * vif victim selected for subsequently display the data in a table.
     *
     * @param idVIctim
     * @return
     */
    public RowDataTable loadSivigilaVifRecord(String idVIctim) {
        //CARGO LOS DATOS DE UN EVENTO SIVIGILA EN UNA FILA PARA LA TABLA

        NonFatalDomesticViolence currentNonFatalDomesticV = nonFatalDomesticViolenceFacade.findByIdVictim(idVIctim);
        if (currentNonFatalDomesticV == null) {
            return null;
        }
        RowDataTable newRowDataTable = new RowDataTable();
        //------------------------------------------------------------
        //SE CARGAN VALORES PARA LA VICTIMA
        //------------------------------------------------------------               
        //******non_fatal_injury_id
        newRowDataTable.setColumn1(currentNonFatalDomesticV.getNonFatalInjuries().getNonFatalInjuryId().toString());
        //******victim_name        
        if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimName() != null) {
            newRowDataTable.setColumn2(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimName());
        }
        //******type_id
        if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getTypeId() != null) {
            newRowDataTable.setColumn3(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getTypeId().getTypeName());
        }
        //******victim_nid
        if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimNid() != null) {
            newRowDataTable.setColumn4(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimNid());
        }
        //******age_type_id
        if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getAgeTypeId() != null) {
            newRowDataTable.setColumn5(ageTypesFacade.find(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getAgeTypeId()).getAgeTypeName());
        }
        //******victim_age
        if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimAge() != null) {
            newRowDataTable.setColumn6(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimAge().toString());
        }
        //******gender_id
        if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getGenderId() != null) {
            newRowDataTable.setColumn7(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getGenderId().getGenderName());
        }
        //******job_id        
        if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getJobId() != null) {
            newRowDataTable.setColumn8(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getJobId().getJobName());
        }
        //******victim_address
        if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimAddress() != null) {
            newRowDataTable.setColumn9(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimAddress());
        }
        //******insurance_id
        if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getInsuranceId() != null) {
            newRowDataTable.setColumn10(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getInsuranceId().getInsuranceName());
        }
        //******ethnic_group_id
        if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getEthnicGroupId() != null) {
            newRowDataTable.setColumn11(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getEthnicGroupId().getEthnicGroupName());
        }
        //informacion de grupos vunerables
        if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVulnerableGroupsList() != null) {
            if (!currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVulnerableGroupsList().isEmpty()) {
                newRowDataTable.setColumn12(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVulnerableGroupsList().get(0).getVulnerableGroupName());
            }
        }
        //******residence_municipality
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getResidenceDepartment() != null && (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getResidenceMunicipality() != null)) {
                short departamentId = currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getResidenceDepartment();
                short municipalityId = currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getResidenceMunicipality();
                MunicipalitiesPK mPk = new MunicipalitiesPK(departamentId, municipalityId);
                newRowDataTable.setColumn13(municipalitiesFacade.find(mPk).getMunicipalityName());
            }
        } catch (Exception e) {
        }
        //******residence_department
        if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getResidenceDepartment() != null) {
            newRowDataTable.setColumn14(departamentsFacade.find(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getResidenceDepartment()).getDepartamentName());
        }
        //******victim_telephone
        if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimTelephone() != null) {
            newRowDataTable.setColumn15(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimTelephone());
        }
        //******victim_date_of_birth
        if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimDateOfBirth() != null) {
            newRowDataTable.setColumn16(sdf.format(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimDateOfBirth()));
        }
        //------------------------------------------------------------
        //SE CARGAN VALORES PARA SIVIGILA VICTIM
        //------------------------------------------------------------               
        //******sivigila_victim.health_category
        if (currentNonFatalDomesticV.getSivigilaEvent().getSivigilaVictimId().getHealthCategory() != null) {
            newRowDataTable.setColumn17(currentNonFatalDomesticV.getSivigilaEvent().getSivigilaVictimId().getHealthCategory().getSivigilaTipSsName());
        }
        //escolaridad victima
        if (currentNonFatalDomesticV.getSivigilaEvent().getSivigilaVictimId().getEducationalLevelId() != null) {
            newRowDataTable.setColumn18(currentNonFatalDomesticV.getSivigilaEvent().getSivigilaVictimId().getEducationalLevelId().getSivigilaEducationalLevelName());
        }
        //factor vulnerabilidad victima
        if (currentNonFatalDomesticV.getSivigilaEvent().getSivigilaVictimId().getVulnerabilityId() != null) {
            newRowDataTable.setColumn19(currentNonFatalDomesticV.getSivigilaEvent().getSivigilaVictimId().getVulnerabilityId().getSivigilaVulnerabilityName());
        }
        //otro factor vulnerabilidad victima
        if (currentNonFatalDomesticV.getSivigilaEvent().getSivigilaVictimId().getOtherVulnerability() != null) {
            newRowDataTable.setColumn20(currentNonFatalDomesticV.getSivigilaEvent().getSivigilaVictimId().getOtherVulnerability());
        }
        //antecedentes de hecho similar        
        if (currentNonFatalDomesticV.getSivigilaEvent().getSivigilaVictimId().getAntecedent() != null) {
            newRowDataTable.setColumn21(currentNonFatalDomesticV.getSivigilaEvent().getSivigilaVictimId().getAntecedent().getBooleanName());
        }
        //------------------------------------------------------------
        //SE CARGAN VALORES PARA LA LESION
        //------------------------------------------------------------       
        //******injury_neighborhood_id//y comuna
        if (currentNonFatalDomesticV.getNonFatalInjuries().getInjuryNeighborhoodId() != null) {
            newRowDataTable.setColumn22(currentNonFatalDomesticV.getNonFatalInjuries().getInjuryNeighborhoodId().getNeighborhoodName());
            newRowDataTable.setColumn23(String.valueOf(currentNonFatalDomesticV.getNonFatalInjuries().getInjuryNeighborhoodId().getNeighborhoodSuburb()));
        }
        //******quadrant_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getQuadrantId() != null) {
                newRowDataTable.setColumn71(currentNonFatalDomesticV.getNonFatalInjuries().getQuadrantId().getQuadrantName());
            }
        } catch (Exception e) {
        }
        //******direccion evento
        if (currentNonFatalDomesticV.getNonFatalInjuries().getInjuryAddress() != null) {
            newRowDataTable.setColumn40(currentNonFatalDomesticV.getNonFatalInjuries().getInjuryAddress());
        }
        //******checkup_date
        if (currentNonFatalDomesticV.getNonFatalInjuries().getCheckupDate() != null) {
            newRowDataTable.setColumn24(sdf.format(currentNonFatalDomesticV.getNonFatalInjuries().getCheckupDate()));
        }
        //******checkup_time
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getCheckupTime() != null) {
                hours = String.valueOf(currentNonFatalDomesticV.getNonFatalInjuries().getCheckupTime().getHours());
                minutes = String.valueOf(currentNonFatalDomesticV.getNonFatalInjuries().getCheckupTime().getMinutes());
                if (hours.length() != 2) {
                    hours = "0" + hours;
                }
                if (minutes.length() != 2) {
                    minutes = "0" + minutes;
                }
                newRowDataTable.setColumn25(hours + minutes);
            }
        } catch (Exception e) {
        }
        //******injury_date
        if (currentNonFatalDomesticV.getNonFatalInjuries().getInjuryDate() != null) {
            newRowDataTable.setColumn26(sdf.format(currentNonFatalDomesticV.getNonFatalInjuries().getInjuryDate()));
        }
        //******injury_time
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getInjuryTime() != null) {
                hours = String.valueOf(currentNonFatalDomesticV.getNonFatalInjuries().getInjuryTime().getHours());
                minutes = String.valueOf(currentNonFatalDomesticV.getNonFatalInjuries().getInjuryTime().getMinutes());
                if (hours.length() != 2) {
                    hours = "0" + hours;
                }
                if (minutes.length() != 2) {
                    minutes = "0" + minutes;
                }
                newRowDataTable.setColumn27(hours + minutes);
            }
        } catch (Exception e) {
        }
        //nombre profesional salud
        if (currentNonFatalDomesticV.getNonFatalInjuries().getHealthProfessionalId() != null) {
            newRowDataTable.setColumn28(currentNonFatalDomesticV.getNonFatalInjuries().getHealthProfessionalId().getHealthProfessionalName());
        }
        //cargo la lista de abusos(tipos de maltrato)-----------------------------------
        try {
            if (currentNonFatalDomesticV.getAbuseTypesList() != null) {
                List<AbuseTypes> abuseTypesList = currentNonFatalDomesticV.getAbuseTypesList();
                for (int i = 0; i < abuseTypesList.size(); i++) {
                    int caso = (int) abuseTypesList.get(i).getAbuseTypeId();
                    String sql = "SELECT abuse_type_name FROM abuse_types WHERE abuse_type_id=" + caso;
                    rs = consult(sql);
                    if (rs.next()) {
                        newRowDataTable.setColumn29(rs.getString(1));
                    }
                }
            }

        } catch (Exception e) {
            //System.out.println("no se cargo tipos de maltrato"+e.toString());
        }
        //******injury_place_id
        if (currentNonFatalDomesticV.getNonFatalInjuries().getInjuryPlaceId() != null) {
            newRowDataTable.setColumn39(currentNonFatalDomesticV.getNonFatalInjuries().getInjuryPlaceId().getNonFatalPlaceName());
        }
        //uso de alcohol victima
        if (currentNonFatalDomesticV.getNonFatalInjuries().getUseAlcoholId() != null) {
            newRowDataTable.setColumn41(currentNonFatalDomesticV.getNonFatalInjuries().getUseAlcoholId().getUseAlcoholDrugsName());
        }
        //******non_fatal_data_source_id
        if (currentNonFatalDomesticV.getNonFatalInjuries().getNonFatalDataSourceId() != null) {
            newRowDataTable.setColumn42(currentNonFatalDomesticV.getNonFatalInjuries().getNonFatalDataSourceId().getNonFatalDataSourceName());
        }
        //------------------------------------------------------------
        //SE CARGAN VALORES PARA SIVIGILA EVENT
        //------------------------------------------------------------               
        //******sivigila_event.area
        if (currentNonFatalDomesticV.getSivigilaEvent().getArea() != null) {
            newRowDataTable.setColumn43(currentNonFatalDomesticV.getSivigilaEvent().getArea().getAreaName());
        }
        //edad agresor
        try {
            if (currentNonFatalDomesticV.getSivigilaEvent().getSivigilaAgresorId().getAge() != null) {
                newRowDataTable.setColumn44(currentNonFatalDomesticV.getSivigilaEvent().getSivigilaAgresorId().getAge().toString());
            }
        } catch (Exception e) {
        }
        //genero agresor
        if (currentNonFatalDomesticV.getSivigilaEvent().getSivigilaAgresorId().getGender() != null) {
            newRowDataTable.setColumn45(currentNonFatalDomesticV.getSivigilaEvent().getSivigilaAgresorId().getGender().getGenderName());
        }
        //ocupacion agresor
        try {
            if (currentNonFatalDomesticV.getSivigilaEvent().getSivigilaAgresorId().getOccupation() != null) {
                newRowDataTable.setColumn46(currentNonFatalDomesticV.getSivigilaEvent().getSivigilaAgresorId().getOccupation().getJobName());
            }
        } catch (Exception e) {
        }
        //escolaridad agresor
        if (currentNonFatalDomesticV.getSivigilaEvent().getSivigilaAgresorId().getEducationalLevelId() != null) {
            newRowDataTable.setColumn47(currentNonFatalDomesticV.getSivigilaEvent().getSivigilaAgresorId().getEducationalLevelId().getSivigilaEducationalLevelName());
        }
        //relacion familiar victima
        if (currentNonFatalDomesticV.getSivigilaEvent().getSivigilaAgresorId().getRelativeId() != null) {
            newRowDataTable.setColumn48(currentNonFatalDomesticV.getSivigilaEvent().getSivigilaAgresorId().getRelativeId().getAggressorTypeName());
        }
        //otra relacion familiar victima
        if (currentNonFatalDomesticV.getSivigilaEvent().getSivigilaAgresorId().getOtherRelative() != null) {
            newRowDataTable.setColumn49(currentNonFatalDomesticV.getSivigilaEvent().getSivigilaAgresorId().getOtherRelative());
        }
        //convive con agresor
        if (currentNonFatalDomesticV.getSivigilaEvent().getSivigilaAgresorId().getLiveTogether() != null) {
            newRowDataTable.setColumn50(currentNonFatalDomesticV.getSivigilaEvent().getSivigilaAgresorId().getLiveTogether().getBooleanName());
        }
        //relacion no familiar victima
        if (currentNonFatalDomesticV.getSivigilaEvent().getSivigilaAgresorId().getNoRelativeId() != null) {
            newRowDataTable.setColumn51(currentNonFatalDomesticV.getSivigilaEvent().getSivigilaAgresorId().getNoRelativeId().getSivigilaNoRelativeName());
        }
        //otra relacion no familiar victima
        if (currentNonFatalDomesticV.getSivigilaEvent().getSivigilaAgresorId().getOtherNoRelative() != null) {
            newRowDataTable.setColumn52(currentNonFatalDomesticV.getSivigilaEvent().getSivigilaAgresorId().getOtherNoRelative());
        }
        //grupo agresor
        if (currentNonFatalDomesticV.getSivigilaEvent().getSivigilaAgresorId().getGroupId() != null) {
            newRowDataTable.setColumn53(currentNonFatalDomesticV.getSivigilaEvent().getSivigilaAgresorId().getGroupId().getSivigilaGroupName());
        }
        //OTRO grupo agresor
        if (currentNonFatalDomesticV.getSivigilaEvent().getSivigilaAgresorId().getOtherGroup() != null) {
            newRowDataTable.setColumn54(currentNonFatalDomesticV.getSivigilaEvent().getSivigilaAgresorId().getOtherGroup());
        }
        //precencia alcohol agresor
        if (currentNonFatalDomesticV.getSivigilaEvent().getSivigilaAgresorId().getAlcoholOrDrugs() != null) {
            newRowDataTable.setColumn55(currentNonFatalDomesticV.getSivigilaEvent().getSivigilaAgresorId().getAlcoholOrDrugs().getBooleanName());
        }
        //armas utilizadas
        if (currentNonFatalDomesticV.getSivigilaEvent().getMechanismId() != null) {
            newRowDataTable.setColumn56(currentNonFatalDomesticV.getSivigilaEvent().getMechanismId().getSivigilaMechanismName());
        }
        //sustancia intoxicacion
        if (currentNonFatalDomesticV.getSivigilaEvent().getIntoxication() != null) {
            newRowDataTable.setColumn57(currentNonFatalDomesticV.getSivigilaEvent().getIntoxication());
        }
        //otra arma        
        if (currentNonFatalDomesticV.getSivigilaEvent().getOthers() != null) {
            newRowDataTable.setColumn58(currentNonFatalDomesticV.getSivigilaEvent().getOthers());
        }
        //otro mecanismo
        if (currentNonFatalDomesticV.getSivigilaEvent().getOtherMechanismId() != null) {
            newRowDataTable.setColumn59(currentNonFatalDomesticV.getSivigilaEvent().getOtherMechanismId().getSivigilaOtherMechanismName());
        }
        //zona conflicto
        if (currentNonFatalDomesticV.getSivigilaEvent().getConflictZone() != null) {
            newRowDataTable.setColumn60(currentNonFatalDomesticV.getSivigilaEvent().getConflictZone().getBooleanName());
        }
        //CARGO ACCIONES EN SALUD------
        try {
            if (currentNonFatalDomesticV.getSivigilaEvent().getPublicHealthActionsList() != null
                    && !currentNonFatalDomesticV.getSivigilaEvent().getPublicHealthActionsList().isEmpty()) {
                List<PublicHealthActions> publicHealthActionsList = currentNonFatalDomesticV.getSivigilaEvent().getPublicHealthActionsList();
                for (int i = 0; i < publicHealthActionsList.size(); i++) {
                    int caso = (int) publicHealthActionsList.get(i).getActionId();
                    switch (caso) {
                        case 1://"ATENCION PSICOSOCIAL"
                            newRowDataTable.setColumn62("SI");
                            break;
                        case 2://"PROFILAXIS ITS"
                            newRowDataTable.setColumn63("SI");
                            break;
                        case 3://"ANTICONCEPCION DE EMERGENCIA"
                            newRowDataTable.setColumn64("SI");
                            break;
                        case 4://"ORIENTACION IVE"
                            newRowDataTable.setColumn65("SI");
                            break;
                        case 5://"ATENCION EN SALUD MENTAL ESPECIALIZADA"
                            newRowDataTable.setColumn66("SI");
                            break;
                        case 6://"INFORME A LA AUTORIDAD"
                            newRowDataTable.setColumn67("SI");
                            break;
                        case 7://"OTRO"
                            newRowDataTable.setColumn68("SI");
                            break;
                    }
                }
            }
        } catch (Exception e) {
            //System.out.println("no se cargo tipos de maltrato"+e.toString());
        }

        //recomienda proteccion
        if (currentNonFatalDomesticV.getSivigilaEvent().getRecommendedProtection() != null) {
            newRowDataTable.setColumn69(currentNonFatalDomesticV.getSivigilaEvent().getRecommendedProtection().getBooleanName());
        }
        //trabajo de campo
        if (currentNonFatalDomesticV.getSivigilaEvent().getFurtherFieldwork() != null) {
            newRowDataTable.setColumn70(currentNonFatalDomesticV.getSivigilaEvent().getFurtherFieldwork().getBooleanName());
        }

        return newRowDataTable;
    }

    /*
     * METODOS PARA RELACIONES DE VARIABLES
     */
    /**
     * This method is responsible to obtain the identification of a country with
     * the department and municipality through a string containing its name.
     *
     * @param value
     * @return
     */
    public String searchCountry(String value) {
        /*
         * COMO PARAMETRO LLEGA UNA CADENA: COLOMBIA-NARIÑO-PASTO ME RETORNA UNA
         * CADENA CON : 20-52-1 OSEA: id_pais - id_departamento - id_municipio
         */
        try {
            String[] splitValue = value.split("-");
            ArrayList<String> arrayValue = new ArrayList<>();
            if (splitValue.length == 1) {
                arrayValue.add(splitValue[0]);
                arrayValue.add("SIN DATO");
                arrayValue.add("SIN DATO");
            } else if (splitValue.length == 2) {
                arrayValue.add(splitValue[0]);
                arrayValue.add(splitValue[1]);
                arrayValue.add("SIN DATO");
            } else if (splitValue.length == 3) {
                arrayValue.add(splitValue[0]);
                arrayValue.add(splitValue[1]);
                arrayValue.add(splitValue[2]);
            } else {
                arrayValue.add("SIN DATO");
                arrayValue.add("SIN DATO");
                arrayValue.add("SIN DATO");
            }
            String strReturn = "";
            String sql = ""
                    + " SELECT \n"
                    + "    countries.id_country \n"
                    + " FROM \n"
                    + "  public.countries \n"
                    + "WHERE \n"
                    + "  countries.name ILIKE '" + arrayValue.get(0) + "' ";
            rs = consult(sql);
            if (rs.next()) {
                strReturn = strReturn + rs.getString(1);
            } else {
                strReturn = "0x";
            }
            sql = ""
                    + " SELECT \n"
                    + "    departaments.departament_id, \n"
                    + "    municipalities.municipality_id, \n"
                    + "    departaments.departament_name, \n"
                    + "    municipalities.municipality_name \n"
                    + " FROM \n"
                    + "    public.departaments, \n"
                    + "    public.municipalities \n"
                    + " WHERE \n"
                    + "    departaments.departament_id = municipalities.departament_id AND \n"
                    + "    departaments.departament_name ILIKE '" + arrayValue.get(1) + "' AND \n"
                    + "    municipalities.municipality_name ILIKE '" + arrayValue.get(2) + "'";
            rs = consult(sql);
            if (rs.next()) {
                strReturn = strReturn + "-" + rs.getString(1) + "-" + rs.getString(2);
            } else {
                strReturn = strReturn + "-0x-0x";
            }
            return strReturn;
        } catch (Exception e) {
            System.out.println("Error 12 en " + this.getClass().getName() + ":" + e.toString());
            return null;
        }
    }

    /**
     * This method is responsible to obtain the identification of a municipality
     * through its name.
     *
     * @param value
     * @return
     */
    public String searchMunicipalitie(String value) {

        List<Municipalities> municipalitiesList = municipalitiesFacade.findAll();
        for (int k = 0; k < municipalitiesList.size(); k++) {
            String name = municipalitiesList.get(k).getMunicipalityName() + " - " + municipalitiesList.get(k).getDepartaments().getDepartamentName();
            if (name.compareTo(value) == 0) {
                name = String.valueOf(municipalitiesList.get(k).getMunicipalitiesPK().getDepartamentId());
                name = name + "-";
                name = name + String.valueOf(municipalitiesList.get(k).getMunicipalitiesPK().getMunicipalityId());
                return name;
            }
        }
        return null;
    }

    /**
     * This method is responsible to search a code in a category and returns its
     * name, if the system does not find any related code then returns null.
     *
     * @param category
     * @param value
     * @return
     */
    public String findNameByCategoricalCode(String category, String value) {
        /*
         * busca un codigo dentro de una categoria y me retorna su nombre,
         * cuando retorna null es por que no fue encontrado
         */
        try {
            if (category.compareTo("municipalities") == 0) {
                return searchMunicipalitie(value);
            }
            if (category.compareTo("countries") == 0) {
                return searchCountry(value);
            }
            //se busca dentro de una tabla que representa la categoria
            ResultSet resultSetCategory = consult("SELECT * FROM " + category);
            while (resultSetCategory.next()) {
                if (resultSetCategory.getString(1).compareTo(value) == 0) {
                    return resultSetCategory.getString(2);
                }
            }
        } catch (SQLException ex) {
            return null;
        }
        return null;
    }

    /**
     * This method is responsible to search a name in a category and returns its
     * ID, if the system does not find any related name then returns null.
     *
     * @param category
     * @param value
     * @return
     */
    public String findCodeByCategoricalName(String category, String value) {
        /*
         * busca un nombre dentro de una categoria y me retorna su id, cuando
         * retorna null es por que no fue encontrado
         */
        try {
            if (category.compareTo("municipalities") == 0) {
                return searchMunicipalitie(value);
            }
            if (category.compareTo("countries") == 0) {
                return searchCountry(value);
            }
            //se busca dentro de una tabla que representa la categoria
            ResultSet resultSetCategory = consult("SELECT * FROM " + category);
            while (resultSetCategory.next()) {
                if (resultSetCategory.getString(2).compareTo(value) == 0) {
                    return resultSetCategory.getString(1);
                }
            }
        } catch (SQLException ex) {
            return null;
        }
        return null;
    }

    /**
     * This method is responsible to returns a list of names belonging to a
     * category. amount tells the system how many records have the list, if
     * amount is 0 then indicates to system that the list contain all the
     * records that exist.
     *
     * @param typeVarExepted
     * @param amount
     * @return
     */
    public ArrayList<String> categoricalNameList(String typeVarExepted, int amount) {
        /*
         * retorna una lista con los nombres pertenecientes a una categoria
         * amount me idica cuantos registros tendra la lista, si amount es 0
         * indica que se la lista contendra todos los que existan.
         */
        ArrayList<String> returnList = new ArrayList<>();
        if (typeVarExepted.compareTo("municipalities") == 0) {
            List<Municipalities> municipalitiesList = municipalitiesFacade.findAll();
            for (int k = 0; k < municipalitiesList.size(); k++) {
                if (k > amount && amount != 0) {
                    break;
                }
                returnList.add(municipalitiesList.get(k).getMunicipalityName() + " - " + municipalitiesList.get(k).getDepartaments().getDepartamentName());
            }
            return returnList;
        }
        if (typeVarExepted.compareTo("countries") == 0) {
            List<Countries> countriesList = countriesFacade.findAll();
            for (int k = 0; k < countriesList.size(); k++) {
                if (k > amount && amount != 0) {
                    break;
                }

                if (countriesList.get(k).getName().compareTo("COLOMBIA") == 0) {
                    String returnItem;
                    List<Departaments> departamentsList = departamentsFacade.findAll();
                    for (int l = 0; l < departamentsList.size(); l++) {
                        returnItem = countriesList.get(k).getName() + "-" + departamentsList.get(l).getDepartamentName();
                        returnList.add(returnItem);
                        for (int m = 0; m < departamentsList.get(l).getMunicipalitiesList().size(); m++) {
                            returnList.add(returnItem + "-" + departamentsList.get(l).getMunicipalitiesList().get(m).getMunicipalityName());
                        }
                    }
                } else {
                    //returnItem=returnItem+"- - ";
                    returnList.add(countriesList.get(k).getName());
                }
            }
            return returnList;
        }
        //RETORNAR LISTA DE NOMBRES A PARTIR DE UNA TABLA CATEGORICA
        try {
            ResultSet resultSetCategory = consult("SELECT * FROM " + typeVarExepted);
            if (amount != 0) {
                while (resultSetCategory.next()) {
                    returnList.add(resultSetCategory.getString(2));
                }
            } else {
                while (resultSetCategory.next()) {
                    returnList.add(resultSetCategory.getString(2));
                }
            }
        } catch (SQLException ex) {
        }
        return returnList;
    }

    /**
     * This method is responsible to returns a list of codes belonging to a
     * category. amount tells the system how many records have the list, if
     * amount is 0 then indicates to system that the list contain all the
     * records that exist.
     *
     * @param typeVarExepted
     * @param amount
     * @return
     */
    public ArrayList<String> categoricalCodeList(String typeVarExepted, int amount) {
        /*
         * retorna una lista con los codigos pertenecientes a una categoria
         * amount me idica cuantos registros tendra la lista, si amount es 0
         * indica que se la lista contendra todos los que existan.
         */
        ArrayList<String> returnList = new ArrayList<>();

        if (typeVarExepted.compareTo("municipalities") == 0) {
            List<Municipalities> municipalitiesList = municipalitiesFacade.findAll();
            for (int k = 0; k < municipalitiesList.size(); k++) {
                if (k > amount && amount != 0) {
                    break;
                }
                returnList.add(municipalitiesList.get(k).getMunicipalityName() + " - " + municipalitiesList.get(k).getDepartaments().getDepartamentName());
            }
            return returnList;
        }
        if (typeVarExepted.compareTo("countries") == 0) {
            List<Countries> countriesList = countriesFacade.findAll();
            for (int k = 0; k < countriesList.size(); k++) {
                if (k > amount && amount != 0) {
                    break;
                }
                if (countriesList.get(k).getName().compareTo("COLOMBIA") == 0) {
                    String returnItem;
                    List<Departaments> departamentsList = departamentsFacade.findAll();
                    for (int l = 0; l < departamentsList.size(); l++) {
                        returnItem = countriesList.get(k).getName() + "-" + departamentsList.get(l).getDepartamentName();
                        returnList.add(returnItem);
                        for (int m = 0; m < departamentsList.get(l).getMunicipalitiesList().size(); m++) {
                            returnList.add(returnItem + "-" + departamentsList.get(l).getMunicipalitiesList().get(m).getMunicipalityName());
                        }
                    }
                } else {
                    //returnItem=returnItem+"- - ";
                    returnList.add(countriesList.get(k).getName());
                }
            }
            return returnList;
        }
        //RETORNAR LISTA DE CODIGOS A PARTIR DE UNA TABLA CATEGORICA
        try {
            ResultSet resultSetCategory = consult("SELECT * FROM " + typeVarExepted);
            if (amount != 0) {
                while (resultSetCategory.next()) {
                    returnList.add(resultSetCategory.getString(1));
                }
            } else {
                while (resultSetCategory.next()) {
                    returnList.add(resultSetCategory.getString(1));
                }
            }
        } catch (SQLException ex) {
        }
        return returnList;
    }

    /*     
     * -- METODOS DE FILTER CONNECTION -----
     */
    /**
     * This method is responsible to obtain the number of rows which will be
     * shown in the results to do more agile the data queries.
     *
     * @param projectId
     * @return
     */
    public int getTempRowCount(int projectId) {
        try {
            String query = ""
                    + " SELECT "
                    + "    count(DISTINCT(record_id)) "
                    + " FROM "
                    + "    project_records "
                    + " WHERE "
                    + "    project_records.project_id = " + String.valueOf(projectId) + " ";
//            String query = "SELECT count(*) FROM temp";
            ResultSet count = this.consult(query);
            count.next();
            return count.getInt(1);
        } catch (SQLException ex) {
            return 0;
        }
    }

    /**
     * This method returns a list with the names of the columns of a project.
     *
     * @param projectId
     * @return
     */
    public List<String> getColumns(int projectId) {
        /*
         * retorna una lista con los nombres de las columnas de un proyecto
         */
        try {
            ArrayList<String> fields = new ArrayList<>();

            //funciona---funciona-----funciona
//            String query = "SELECT column_name FROM information_schema.columns "
//                    + " WHERE table_name = 'temp'"
//                    + " ORDER BY ordinal_position";
//            ResultSet field_names = consult(query);
            //funciona---funciona-----funciona           
            String query = ""
                    + " SELECT "
                    + "    column_name, "
                    + "    column_id "
                    + " FROM "
                    + "    project_columns "
                    + " WHERE "
                    + "     project_id = " + projectId + " "
                    + " ORDER BY "
                    + "     column_id ";
            ResultSet field_names = consult(query);
            while (field_names.next()) {
                fields.add(field_names.getString(1));
            }
            return fields;
        } catch (SQLException ex) {
            //Logger.getLogger(FilterConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * This method returns a list with the query result, is necessary to define
     * the beginning of the list, the page size and the identification of the
     * project in which the user is working.
     *
     * @param first
     * @param pageSize
     * @param pojectId
     * @return
     */
    public List<List> getListFromQuery(int first, int pageSize, int pojectId) {
        /*
         * Devuelve una lista con el resultado del query.
         */
        List<List> data = new ArrayList<>();
        ArrayList<String> newRow;
        List<String> titles;
        try {
            String query = ""
                    + " SELECT "
                    + "    project_records.project_id, "
                    + "    project_records.record_id, "
                    + "    array_agg(project_columns.column_name || '<=>' || project_records.data_value) "
                    + " FROM "
                    + "    project_records,project_columns "
                    + " WHERE "
                    + "    project_records.project_id = " + pojectId + " AND "
                    + "    project_columns.column_id = project_records.column_id "
                    + " GROUP BY "
                    + "    project_records.project_id, "
                    + "    project_records.record_id "
                    + " ORDER BY"
                    + "    project_records.record_id"
                    + " LIMIT " + pageSize + " OFFSET " + first;
            ResultSet records = this.consult(query);
            titles = getColumns(pojectId);

            while (records.next()) {
                //en la tercer columna esta definido un arreglo con id_columna <=> valor encontrado                
                newRow = new ArrayList<>();
                for (int j = 0; j < titles.size(); j++) {
                    newRow.add("");
                }
                Object[] arrayInJava = (Object[]) records.getArray(3).getArray();
                for (int i = 0; i < arrayInJava.length; i++) {
                    String splitElement[] = arrayInJava[i].toString().split("<=>");
                    for (int j = 0; j < titles.size(); j++) {
                        if (titles.get(j).compareTo(splitElement[0]) == 0) {
                            if (splitElement.length > 1) {
                                newRow.set(j, splitElement[1]);
                            }
                            break;
                        }
                    }
                }
                data.add(newRow);
            }
            return data;
        } catch (Exception ex) {
            System.out.println("Error 34 " + ex.getMessage());
            //System.out.println("\nI:" + estai + "\nJ:" + estaj + "\nTITULOS(" + String.valueOf(titles.size()) + "):" + titles + "\nNEW ROW(" + String.valueOf(newRow.size()) + "):" + newRow);
            return null;
        }
    }

    // --------------------------    
    // -- METODOS GET Y SET -----
    // --------------------------    
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public boolean isConnectionIsConfigured() {
        return connectionIsConfigured;
    }

    public void setConnectionIsConfigured(boolean connectionIsConfigured) {
        this.connectionIsConfigured = connectionIsConfigured;
    }

    public boolean isConnectionNotConfigured() {
        return connectionNotConfigured;
    }

    public void setConnectionNotConfigured(boolean connectionNotConfigured) {
        this.connectionNotConfigured = connectionNotConfigured;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Users getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Users currentUser) {
        this.currentUser = currentUser;
    }

    public boolean isShowMessages() {
        return showMessages;
    }

    public void setShowMessages(boolean showMessages) {
        this.showMessages = showMessages;
    }

    public String getDb_dwh() {
        return db_dwh;
    }

    public void setDb_dwh(String db_dwh) {
        this.db_dwh = db_dwh;
    }
}
