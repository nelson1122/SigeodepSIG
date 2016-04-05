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
import managedBeans.forms.LcenfMB;
import model.dao.*;
import model.pojo.NonFatalInjuries;
import model.pojo.Tags;
import org.apache.poi.hssf.usermodel.*;
import org.primefaces.model.LazyDataModel;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "recordSetsLcenfMB")
@SessionScoped
/**
 * This class handles record sets that correspond to LCENF
 */
public class RecordSetsLcenfMB implements Serializable {

    //--------------------
    @EJB
    TagsFacade tagsFacade;
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
    NonFatalInjuriesFacade nonFatalInjuriesFacade;
    private List<Tags> tagsList;
    private NonFatalInjuries currentNonFatalInjury;
    private LazyDataModel<RowDataTable> table_model;
    private ArrayList<RowDataTable> rowsDataTableArrayList;
    private RowDataTable[] selectedRowsDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    private String name = "";
    private String newName = "";
    private boolean btnEditDisabled = true;
    private String data = "-";
    private LcenfMB lcenfMB;
    private String openForm = "";
    private ConnectionJdbcMB connection;
    private String totalRecords = "0";
    private String initialDateStr = "";
    private String endDateStr = "";
    private int tuplesNumber;
    private Integer tuplesProcessed;
    private int progress = 0;//PROGRESO AL CREAR XLS
    private String sql = "";
    private String exportFileName = "";

    /**
     * This method Instance tag list, the table model and gets the current
     * instance of the connection to the database.
     */
    public RecordSetsLcenfMB() {
        tagsList = new ArrayList<>();
        table_model = new LazyRecordSetsDataModel(0, "", FormsEnum.SCC_F_032);
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
        lcenfMB = (LcenfMB) context.getApplication().evaluateExpressionGet(context, "#{lcenfMB}", LcenfMB.class);
        lcenfMB.loadValues(tagsList, currentNonFatalInjury);
        openForm = "LCENF";
    }
    
    void loadValues(RowDataTable[] selectedRowsDataTableTags) {
        try {
            //CREO LA LISTA DE TAGS SELECCIONADOS   
            exportFileName = "LCENF - " + initialDateStr + " - " + endDateStr;
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
            sql = sql + " ) AND non_fatal_injuries.injury_date >= to_date('" + initialDateStr + "','dd/MM/yyyy') AND \n";
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
            sql = sql + " ) AND non_fatal_injuries.injury_date >= to_date('" + initialDateStr + "','dd/MM/yyyy') AND \n";
            sql = sql + " non_fatal_injuries.injury_date <= to_date('" + endDateStr + "','dd/MM/yyyy') \n";

            //CONSTRUYO EL TABLE_MODEL
            table_model = new LazyRecordSetsDataModel(Integer.parseInt(totalRecords), sql, FormsEnum.SCC_F_032);

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
                rowsDataTableArrayList.add(connection.loadNonFatalInjuryRecord(resultSet.getString(1)));
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


            createCell(cellStyle, row, colPosition++, "CODIGO");//width="50">#{rowX.column1}</p:column>
            createCell(cellStyle, row, colPosition++, "INSTITUCION DE SALUD");//250">#{rowX.column58}</p:column>
            createCell(cellStyle, row, colPosition++, "NOMBRES Y APELLIDOS");//400">#{rowX.column4}</p:column>
            createCell(cellStyle, row, colPosition++, "TIPO IDENTIFICACION");//200">#{rowX.column2}</p:column>
            createCell(cellStyle, row, colPosition++, "IDENTIFICACION");//100">#{rowX.column3}</p:column>                
            createCell(cellStyle, row, colPosition++, "TIPO EDAD");//100">#{rowX.column6}</p:column>                
            createCell(cellStyle, row, colPosition++, "EDAD");//100">#{rowX.column7}</p:column>
            createCell(cellStyle, row, colPosition++, "GENERO");//100">#{rowX.column8}</p:column>                
            createCell(cellStyle, row, colPosition++, "OCUPACION");//100">#{rowX.column9}</p:column>
            createCell(cellStyle, row, colPosition++, "ASEGURADORA");//300">#{rowX.column18}</p:column>
            createCell(cellStyle, row, colPosition++, "DESPLAZADO");//100">#{rowX.column16}</p:column>
            createCell(cellStyle, row, colPosition++, "DISCAPACITADO");//100">#{rowX.column17}</p:column>
            createCell(cellStyle, row, colPosition++, "GRUPO ETNICO");//100">#{rowX.column10}</p:column>
            createCell(cellStyle, row, colPosition++, "OTRO GRUPO ETNICO");//100">#{rowX.column19}</p:column>
            createCell(cellStyle, row, colPosition++, "EXTRANJERO");//100">#{rowX.column5}</p:column>        
            createCell(cellStyle, row, colPosition++, "DEPARTAMENTO RESIDENCIA");//100">#{rowX.column13}</p:column>
            createCell(cellStyle, row, colPosition++, "MUNICIPIO RESIDENCIA");//100">#{rowX.column12}</p:column>
            createCell(cellStyle, row, colPosition++, "BARRIO RESIDENCIA");//250">#{rowX.column15}</p:column>
            createCell(cellStyle, row, colPosition++, "COMUNA BARRIO RESIDENCIA");//250">#{rowX.column15}</p:column>
            createCell(cellStyle, row, colPosition++, "DIRECCION RESIDENCIA");//400">#{rowX.column14}</p:column>
            createCell(cellStyle, row, colPosition++, "TELEFONO");//100">#{rowX.column11}</p:column>

            createCell(cellStyle, row, colPosition++, "BARRIO EVENTO");//250">#{rowX.column42}</p:column>
            createCell(cellStyle, row, colPosition++, "CUADRANTE EVENTO");
            createCell(cellStyle, row, colPosition++, "COMUNA BARRIO EVENTO");//250">#{rowX.column15}</p:column>
            createCell(cellStyle, row, colPosition++, "DIRECCION EVENTO");//400">#{rowX.column41}</p:column>

            //------------------------------------------------------------
            //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA NO FATAL
            //------------------------------------------------------------        
            createCell(cellStyle, row, colPosition++, "DIA EVENTO");//100">#{rowX.column37}</p:column>
            createCell(cellStyle, row, colPosition++, "MES EVENTO");//100">#{rowX.column37}</p:column>
            createCell(cellStyle, row, colPosition++, "AÑO EVENTO");//100">#{rowX.column37}</p:column>
            createCell(cellStyle, row, colPosition++, "FECHA EVENTO");//100">#{rowX.column39}</p:column>
            createCell(cellStyle, row, colPosition++, "HORA EVENTO");//100">#{rowX.column40}</p:column>
            createCell(cellStyle, row, colPosition++, "DIA SEMANA EVENTO");//100">#{rowX.column57}</p:column>

            createCell(cellStyle, row, colPosition++, "DIA CONSULTA");//100">#{rowX.column37}</p:column>
            createCell(cellStyle, row, colPosition++, "MES CONSULTA");//100">#{rowX.column37}</p:column>
            createCell(cellStyle, row, colPosition++, "AÑO CONSULTA");//100">#{rowX.column37}</p:column>
            createCell(cellStyle, row, colPosition++, "FECHA CONSULTA");//100">#{rowX.column37}</p:column>
            createCell(cellStyle, row, colPosition++, "HORA CONSULTA");//100">#{rowX.column38}</p:column>
            createCell(cellStyle, row, colPosition++, "REMITIDO");//100">#{rowX.column50}</p:column>
            createCell(cellStyle, row, colPosition++, "REMITIDO DE DONDE");//250">#{rowX.column51}</p:column>
            createCell(cellStyle, row, colPosition++, "INTENCIONALIDAD");//250">#{rowX.column45}</p:column>
            createCell(cellStyle, row, colPosition++, "LUGAR DEL HECHO");//200">#{rowX.column43}</p:column>
            createCell(cellStyle, row, colPosition++, "OTRO LUGAR DEL HECHO");//200">#{rowX.column20}</p:column>
            createCell(cellStyle, row, colPosition++, "ACTIVIDAD");//250">#{rowX.column44}</p:column>
            createCell(cellStyle, row, colPosition++, "OTRA ACTIVIDAD");//250">#{rowX.column21}</p:column>
            createCell(cellStyle, row, colPosition++, "MECANISMO / OBJETO DE LA LESION");//200">#{rowX.column55}</p:column>
            createCell(cellStyle, row, colPosition++, "CUAL ALTURA");//100">#{rowX.column22}</p:column>
            createCell(cellStyle, row, colPosition++, "CUAL POLVORA");//100">#{rowX.column23}</p:column>
            createCell(cellStyle, row, colPosition++, "CUAL DESASTRE NATURAL");//100">#{rowX.column24}</p:column>
            createCell(cellStyle, row, colPosition++, "CUAL OTRO MECANISMO / OBJETO");//100">#{rowX.column25}</p:column>
            createCell(cellStyle, row, colPosition++, "CUAL ANIMAL");//100">#{rowX.column26}</p:column>
            createCell(cellStyle, row, colPosition++, "USO DE ALCOHOL");//150">#{rowX.column46}</p:column>
            createCell(cellStyle, row, colPosition++, "USO DE DROGAS");//150">#{rowX.column47}</p:column>

            createCell(cellStyle, row, colPosition++, "GRADO (QUEMADOS)");//100">#{rowX.column48}</p:column>
            createCell(cellStyle, row, colPosition++, "PORCENTAJE(QUEMADOS)");//100">#{rowX.column49}</p:column>
            //<!-- p:column headerText="injury_id" );//250">{rowX.column59}</p:column -->
            //sitios anatomicos
            createCell(cellStyle, row, colPosition++, "SA1 SISTEMICO");//100">#{rowX.column82}</p:column>
            createCell(cellStyle, row, colPosition++, "SA2 CRANEO");//100">#{rowX.column83}</p:column>
            createCell(cellStyle, row, colPosition++, "SA3 OJOS");//100">#{rowX.column84}</p:column>
            createCell(cellStyle, row, colPosition++, "SA4 MAXILOFACIAL / NARIZ / OIDOS");//100">#{rowX.column85}</p:column>
            createCell(cellStyle, row, colPosition++, "SA5 CUELLO");//100">#{rowX.column86}</p:column>
            createCell(cellStyle, row, colPosition++, "SA6 TORAX");//100">#{rowX.column87}</p:column>
            createCell(cellStyle, row, colPosition++, "SA7 ABDOMEN");//100">#{rowX.column88}</p:column>
            createCell(cellStyle, row, colPosition++, "SA8 COLUMNA");//100">#{rowX.column89}</p:column>
            createCell(cellStyle, row, colPosition++, "SA9 PELVIS / GENITALES");//100">#{rowX.column90}</p:column>
            createCell(cellStyle, row, colPosition++, "SA10 MIEMBROS SUPERIORES");//100">#{rowX.column91}</p:column>
            createCell(cellStyle, row, colPosition++, "SA11 MIEMBROS INFERIORES");//100">#{rowX.column92}</p:column>
            createCell(cellStyle, row, colPosition++, "SA OTRO");//100">#{rowX.column93}</p:column>
            createCell(cellStyle, row, colPosition++, "CUAL OTRO SITIO");//100">#{rowX.column34}</p:column>

            //cargo la naturaleza de la lesion
            createCell(cellStyle, row, colPosition++, "NL1 LACERACION");//100">#{rowX.column94}</p:column>
            createCell(cellStyle, row, colPosition++, "NL2 CORTADA");//100">#{rowX.column95}</p:column>
            createCell(cellStyle, row, colPosition++, "NL3 LESION PROFUNDA");//100">#{rowX.column96}</p:column>
            createCell(cellStyle, row, colPosition++, "NL4 ESGUINCE LUXACION");//100">#{rowX.column97}</p:column>
            createCell(cellStyle, row, colPosition++, "NL5 FRACTURA");//100">#{rowX.column98}</p:column>
            createCell(cellStyle, row, colPosition++, "NL6 QUEMADURA");//100">#{rowX.column99}</p:column>
            createCell(cellStyle, row, colPosition++, "NL7 CONTUSION");//100">#{rowX.column100}</p:column>
            createCell(cellStyle, row, colPosition++, "NL8 ORGANICA SISTEMICA");//100">#{rowX.column101}</p:column>
            createCell(cellStyle, row, colPosition++, "NL9 TRAUMA CRANEOENCEFALICO");//100">#{rowX.column102}</p:column>
            createCell(cellStyle, row, colPosition++, "NL SIN DATO");//100">#{rowX.column104}</p:column>
            createCell(cellStyle, row, colPosition++, "NL OTRO");//100">#{rowX.column103}</p:column>
            createCell(cellStyle, row, colPosition++, "CUAL OTRA NATURALEZA");//150">#{rowX.column35}</p:column>

            createCell(cellStyle, row, colPosition++, "DESTINO DEL PACIENTE");//250">#{rowX.column52}</p:column>
            createCell(cellStyle, row, colPosition++, "CUAL OTRO DESTINO");//100">#{rowX.column36}</p:column>

            //cargo los diagnosticos

            createCell(cellStyle, row, colPosition++, "CIE10_1 CODIGO");//500">#{rowX.column105}</p:column>
            createCell(cellStyle, row, colPosition++, "CIE10_1 DESCRIPCION");//500">#{rowX.column105}</p:column>
            createCell(cellStyle, row, colPosition++, "CIE10_2 CODIGO");//500">#{rowX.column105}</p:column>
            createCell(cellStyle, row, colPosition++, "CIE10_2 DESCRIPCION");//500">#{rowX.column106}</p:column>
            createCell(cellStyle, row, colPosition++, "CIE10_3 CODIGO");//500">#{rowX.column105}</p:column>
            createCell(cellStyle, row, colPosition++, "CIE10_3 DESCRIPCION");//500">#{rowX.column107}</p:column>
            createCell(cellStyle, row, colPosition++, "CIE10_4 CODIGO");//500">#{rowX.column105}</p:column>
            createCell(cellStyle, row, colPosition++, "CIE10_4 DESCRIPCION");//500">#{rowX.column108}</p:column>

            createCell(cellStyle, row, colPosition++, "MEDICO");//300">#{rowX.column54}</p:column>
            createCell(cellStyle, row, colPosition++, "DIGITADOR");//100">#{rowX.column56}</p:column>

            //------------------------------------------------------------
            //AUTOINFLINGIDA INTENCIONAL
            //------------------------------------------------------------
            createCell(cellStyle, row, colPosition++, "INTENTO PREVIO (INTENCIONAL AUTOINFLIGIDA)");//100">#{rowX.column109}</p:column>
            createCell(cellStyle, row, colPosition++, "ANTECEDENTES MENTALES (INTENCIONAL AUTOINFLIGIDA)");//100">#{rowX.column110}</p:column>
            createCell(cellStyle, row, colPosition++, "FACTOR PRECIPITANTE (INTENCIONAL AUTOINFLIGIDA)");//100">#{rowX.column111}</p:column>
            createCell(cellStyle, row, colPosition++, "CUAL OTRO FACTOR (INTENCIONAL AUTOINFLIGIDA)");//100">#{rowX.column27}</p:column>                                

            //------------------------------------------------------------
            //SE CARGA VARIABLE PARA VIOLENCIA INTERPERSONAL
            //-----------------------------------------------------------
            createCell(cellStyle, row, colPosition++, "ANTECEDENTES AGRESION (INTERPERSONAL)");//100">#{rowX.column60}</p:column>
            createCell(cellStyle, row, colPosition++, "RELACION AGRESOR-VICTIMA (INTERPERSONAL)");//150">#{rowX.column61}</p:column>
            createCell(cellStyle, row, colPosition++, "CUAL OTRA RELACION (INTERPERSONAL)");//100">#{rowX.column30}</p:column>
            createCell(cellStyle, row, colPosition++, "CONTEXTO (INTERPERSONAL)");//200">#{rowX.column62}</p:column>
            createCell(cellStyle, row, colPosition++, "SEXO AGRESORES (INTERPERSONAL)");//100">#{rowX.column63}</p:column>

            //------------------------------------------------------------
            //SE CARGA DATOS PARA VIOLENCIA INTRAFAMILIAR
            //------------------------------------------------------------        
            //tipo de agresor
            createCell(cellStyle, row, colPosition++, "AG1 PADRE(VIF)");//100">#{rowX.column64}</p:column>
            createCell(cellStyle, row, colPosition++, "AG2 MADRE(VIF)");//100">#{rowX.column65}</p:column>
            createCell(cellStyle, row, colPosition++, "AG3 PADRASTRO(VIF)");//100">#{rowX.column67}</p:column>
            createCell(cellStyle, row, colPosition++, "AG4 MADRASTRA(VIF)");//100">#{rowX.column67}</p:column>
            createCell(cellStyle, row, colPosition++, "AG5 CONYUGE(VIF)");//100">#{rowX.column68}</p:column>
            createCell(cellStyle, row, colPosition++, "AG6 HERMANO(VIF)");//100">#{rowX.column69}</p:column>
            createCell(cellStyle, row, colPosition++, "AG7 HIJO(VIF)");//100">#{rowX.column70}</p:column>
            createCell(cellStyle, row, colPosition++, "AG8 OTRO(VIF)");//100">#{rowX.column71}</p:column>
            createCell(cellStyle, row, colPosition++, "CUAL OTRO AGRESOR(VIF)");//100">#{rowX.column28}</p:column>
            createCell(cellStyle, row, colPosition++, "AG9 SIN DATO(VIF)");//100">#{rowX.column72}</p:column>
            createCell(cellStyle, row, colPosition++, "AG10 NOVIO(VIF)");//100">#{rowX.column73}</p:column>                                

            //tipo de maltrato
            createCell(cellStyle, row, colPosition++, "MA1 FISICO(VIF)");//100">#{rowX.column74}</p:column>
            createCell(cellStyle, row, colPosition++, "MA2 PSICOLOGICO(VIF)");//100">#{rowX.column75}</p:column>
            createCell(cellStyle, row, colPosition++, "MA3 VIOLENCIA SEXUAL(VIF)");//100">#{rowX.column76}</p:column>
            createCell(cellStyle, row, colPosition++, "MA4 NEGLIGENCIA(VIF)");//100">#{rowX.column77}</p:column>
            createCell(cellStyle, row, colPosition++, "MA5 ABANDONO(VIF)");//100">#{rowX.column78}</p:column>
            createCell(cellStyle, row, colPosition++, "MA6 INSTITUCIONAL(VIF)");//100">#{rowX.column79}</p:column>
            createCell(cellStyle, row, colPosition++, "MA SIN DATO(VIF)");//100">#{rowX.column80}</p:column>
            createCell(cellStyle, row, colPosition++, "MA8 OTRO(VIF)");//100">#{rowX.column81}</p:column>
            createCell(cellStyle, row, colPosition++, "CUAL OTRO TIPO MALTRATO(VIF)");//100">#{rowX.column29}</p:column>


            //------------------------------------------------------------
            //SE CARGA DATOS PARA TRANSITO
            //------------------------------------------------------------
            createCell(cellStyle, row, colPosition++, "TIPO DE TRANSPORTE");//100">#{rowX.column112}</p:column>
            createCell(cellStyle, row, colPosition++, "CUAL OTRO TIPO DE TRANSPORTE");//100">#{rowX.column31}</p:column>                                
            createCell(cellStyle, row, colPosition++, "TIPO TRANSPORTE CONTRAPARTE");//100">#{rowX.column113}</p:column>
            createCell(cellStyle, row, colPosition++, "CUAL OTRO TIPO TRANSPORTE CONTRAPARTE");//100">#{rowX.column32}</p:column>                                
            createCell(cellStyle, row, colPosition++, "TIPO DE TRANSPORTE DEL USUARIO");//100">#{rowX.column114}</p:column>
            createCell(cellStyle, row, colPosition++, "CUAL OTRO TIPO DE TRANSPORTE DEL USUARIO");//100">#{rowX.column33}</p:column>

            createCell(cellStyle, row, colPosition++, "CINTURON");//100">#{rowX.column115}</p:column>
            createCell(cellStyle, row, colPosition++, "CASCO MOTO");//100">#{rowX.column116}</p:column>
            createCell(cellStyle, row, colPosition++, "CASCO BICICLETA");//100">#{rowX.column117}</p:column>
            createCell(cellStyle, row, colPosition++, "CHALECO");//100">#{rowX.column118}</p:column>
            createCell(cellStyle, row, colPosition++, "OTRO ELEMENTO");//100">#{rowX.column119}</p:column>

            String[] splitDate;
            for (int i = 0; i < rowsDataTableArrayList.size(); i++) {
                colPosition = 0;
                RowDataTable rowDataTableList = rowsDataTableArrayList.get(i);
                rowPosition++;
                row = sheet.createRow(rowPosition);
                createCell(row, colPosition++, rowDataTableList.getColumn1());//"CODIGO");
                createCell(row, colPosition++, rowDataTableList.getColumn58());//"INSTITUCION DE SALUD");
                createCell(row, colPosition++, rowDataTableList.getColumn4());//"NOMBRES Y APELLIDOS");
                createCell(row, colPosition++, rowDataTableList.getColumn2());//"TIPO IDENTIFICACION");
                createCell(row, colPosition++, rowDataTableList.getColumn3());//"IDENTIFICACION");
                createCell(row, colPosition++, rowDataTableList.getColumn6());//"TIPO EDAD");
                createCell(row, colPosition++, rowDataTableList.getColumn7());//"EDAD");
                createCell(row, colPosition++, rowDataTableList.getColumn8());//"GENERO");
                createCell(row, colPosition++, rowDataTableList.getColumn9());//"OCUPACION");
                createCell(row, colPosition++, rowDataTableList.getColumn18());//"ASEGURADORA");
                createCell(row, colPosition++, rowDataTableList.getColumn16());//"DESPLAZADO");
                createCell(row, colPosition++, rowDataTableList.getColumn17());//"DISCAPACITADO");
                createCell(row, colPosition++, rowDataTableList.getColumn10());//"GRUPO ETNICO");
                createCell(row, colPosition++, rowDataTableList.getColumn19());//"OTRO GRUPO ETNICO");
                createCell(row, colPosition++, rowDataTableList.getColumn5());//"EXTRANJERO");
                createCell(row, colPosition++, rowDataTableList.getColumn13());//"DEPARTAMENTO RESIDENCIA");
                createCell(row, colPosition++, rowDataTableList.getColumn12());//"MUNICIPIO RESIDENCIA");
                createCell(row, colPosition++, rowDataTableList.getColumn15());//"BARRIO RESIDENCIA");
                createCell(row, colPosition++, rowDataTableList.getColumn121());//"COMUNA BARRIO RESIDENCIA");
                createCell(row, colPosition++, rowDataTableList.getColumn14());//"DIRECCION RESIDENCIA");
                createCell(row, colPosition++, rowDataTableList.getColumn11());//"TELEFONO");
                createCell(row, colPosition++, rowDataTableList.getColumn42());//"BARRIO EVENTO");
                createCell(row, colPosition++, rowDataTableList.getColumn126());//CUADRANTE EVENTO
                createCell(row, colPosition++, rowDataTableList.getColumn120());//"COMUNA BARRIO EVENTO");

                createCell(row, colPosition++, rowDataTableList.getColumn41());//"DIRECCION EVENTO");
                //------------------------------------------------------------
                //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA NO FATAL
                //------------------------------------------------------------        
                if (rowDataTableList.getColumn39() != null) {
                    splitDate = rowDataTableList.getColumn39().split("/");
                    if (splitDate.length == 3) {
                        createCell(row, colPosition++, splitDate[0]);//"DIA EVENTO");
                        createCell(row, colPosition++, splitDate[1]);//"MES EVENTO");
                        createCell(row, colPosition++, splitDate[2]);//"AÑO EVENTO");
                    } else {
                        colPosition = colPosition + 3;
                    }
                } else {
                    colPosition = colPosition + 3;
                }

                createCell(row, colPosition++, rowDataTableList.getColumn39());//"FECHA EVENTO");
                createCell(row, colPosition++, rowDataTableList.getColumn40());//"HORA EVENTO");
                createCell(row, colPosition++, rowDataTableList.getColumn57());//"DIA SEMANA EVENTO");

                if (rowDataTableList.getColumn37() != null) {
                    splitDate = rowDataTableList.getColumn37().split("/");
                    if (splitDate.length == 3) {
                        createCell(row, colPosition++, splitDate[0]);//"DIA CONSULTA");
                        createCell(row, colPosition++, splitDate[1]);//"MES CONSULTA");
                        createCell(row, colPosition++, splitDate[2]);//"AÑO CONSULTA");
                    } else {
                        colPosition = colPosition + 3;
                    }
                } else {
                    colPosition = colPosition + 3;
                }
                createCell(row, colPosition++, rowDataTableList.getColumn37());//"FECHA CONSULTA");
                createCell(row, colPosition++, rowDataTableList.getColumn38());//"HORA CONSULTA");
                createCell(row, colPosition++, rowDataTableList.getColumn50());//"REMITIDO");
                createCell(row, colPosition++, rowDataTableList.getColumn51());//"REMITIDO DE DONDE");
                createCell(row, colPosition++, rowDataTableList.getColumn45());//"INTENCIONALIDAD");
                createCell(row, colPosition++, rowDataTableList.getColumn43());//"LUGAR DEL HECHO");
                createCell(row, colPosition++, rowDataTableList.getColumn20());//"OTRO LUGAR DEL HECHO");
                createCell(row, colPosition++, rowDataTableList.getColumn44());//"ACTIVIDAD");
                createCell(row, colPosition++, rowDataTableList.getColumn21());//"OTRA ACTIVIDAD");
                createCell(row, colPosition++, rowDataTableList.getColumn55());//"MECANISMO / OBJETO DE LA LESION" 
                createCell(row, colPosition++, rowDataTableList.getColumn22());//"CUAL ALTURA");
                createCell(row, colPosition++, rowDataTableList.getColumn23());//"CUAL POLVORA");
                createCell(row, colPosition++, rowDataTableList.getColumn24());//"CUAL DESASTRE NATURAL");
                createCell(row, colPosition++, rowDataTableList.getColumn25());//"CUAL OTRO MECANISMO / OBJETO");
                createCell(row, colPosition++, rowDataTableList.getColumn26());//"CUAL ANIMAL");
                createCell(row, colPosition++, rowDataTableList.getColumn46());//"USO DE ALCOHOL");
                createCell(row, colPosition++, rowDataTableList.getColumn47());//"USO DE DROGAS");
                createCell(row, colPosition++, rowDataTableList.getColumn48());//"GRADO (QUEMADOS)");
                createCell(row, colPosition++, rowDataTableList.getColumn49());//"PORCENTAJE(QUEMADOS)");
                //sitios anatomicos
                createCell(row, colPosition++, rowDataTableList.getColumn82());//"SA1 SISTEMICO");
                createCell(row, colPosition++, rowDataTableList.getColumn83());//"SA2 CRANEO");
                createCell(row, colPosition++, rowDataTableList.getColumn84());//"SA3 OJOS");
                createCell(row, colPosition++, rowDataTableList.getColumn85());//"SA4 MAXILOFACIAL / NARIZ / OIDOS");
                createCell(row, colPosition++, rowDataTableList.getColumn86());//"SA5 CUELLO");
                createCell(row, colPosition++, rowDataTableList.getColumn87());//"SA6 TORAX");
                createCell(row, colPosition++, rowDataTableList.getColumn88());//"SA7 ABDOMEN");
                createCell(row, colPosition++, rowDataTableList.getColumn89());//"SA8 COLUMNA");
                createCell(row, colPosition++, rowDataTableList.getColumn90());//"SA9 PELVIS / GENITALES");
                createCell(row, colPosition++, rowDataTableList.getColumn91());//"SA10 MIEMBROS SUPERIORES");
                createCell(row, colPosition++, rowDataTableList.getColumn92());//"SA11 MIEMBROS INFERIORES");
                createCell(row, colPosition++, rowDataTableList.getColumn93());//"SA OTRO");
                createCell(row, colPosition++, rowDataTableList.getColumn34());//"CUAL OTRO SITIO");
                //cargo la naturaleza de la lesion
                createCell(row, colPosition++, rowDataTableList.getColumn94());//"NL1 LACERACION");
                createCell(row, colPosition++, rowDataTableList.getColumn95());//"NL2 CORTADA");
                createCell(row, colPosition++, rowDataTableList.getColumn96());//"NL3 LESION PROFUNDA");
                createCell(row, colPosition++, rowDataTableList.getColumn97());//"NL4 ESGUINCE LUXACION");
                createCell(row, colPosition++, rowDataTableList.getColumn98());//"NL5 FRACTURA");
                createCell(row, colPosition++, rowDataTableList.getColumn99());//"NL6 QUEMADURA");
                createCell(row, colPosition++, rowDataTableList.getColumn100());//"NL7 CONTUSION");
                createCell(row, colPosition++, rowDataTableList.getColumn101());//"NL8 ORGANICA SISTEMICA");
                createCell(row, colPosition++, rowDataTableList.getColumn102());//"NL9 TRAUMA CRANEOENCEFALICO");
                createCell(row, colPosition++, rowDataTableList.getColumn104());//"NL SIN DATO");
                createCell(row, colPosition++, rowDataTableList.getColumn103());//"NL OTRO");
                createCell(row, colPosition++, rowDataTableList.getColumn35());//"CUAL OTRA NATURALEZA");
                createCell(row, colPosition++, rowDataTableList.getColumn52());//"DESTINO DEL PACIENTE");
                createCell(row, colPosition++, rowDataTableList.getColumn36());//"CUAL OTRO DESTINO");
                //cargo los diagnosticos
                createCell(row, colPosition++, rowDataTableList.getColumn122());//"CIE-10 1");
                createCell(row, colPosition++, rowDataTableList.getColumn105());//"CIE-10 1");

                createCell(row, colPosition++, rowDataTableList.getColumn123());//"CIE-10 1");
                createCell(row, colPosition++, rowDataTableList.getColumn106());//"CIE-10 2");

                createCell(row, colPosition++, rowDataTableList.getColumn124());//"CIE-10 1");
                createCell(row, colPosition++, rowDataTableList.getColumn107());//"CIE-10 3");

                createCell(row, colPosition++, rowDataTableList.getColumn125());//"CIE-10 1");
                createCell(row, colPosition++, rowDataTableList.getColumn108());//"CIE-10 4");



                createCell(row, colPosition++, rowDataTableList.getColumn54());//"MEDICO");
                createCell(row, colPosition++, rowDataTableList.getColumn56());//"DIGITADOR");
                //------------------------------------------------------------
                //AUTOINFLINGIDA INTENCIONAL
                //------------------------------------------------------------
                createCell(row, colPosition++, rowDataTableList.getColumn109());//"INTENTO PREVIO (INTENCIONAL AUTOINFLIGIDA)");//100">#{rowX.column109}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn110());//"ANTECEDENTES MENTALES (INTENCIONAL AUTOINFLIGIDA)");//100">#{rowX.column110}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn111());//"FACTOR PRECIPITANTE (INTENCIONAL AUTOINFLIGIDA)");//100">#{rowX.column111}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn27());//"CUAL OTRO FACTOR (INTENCIONAL AUTOINFLIGIDA)");//100">#{rowX.column27}</p:column>                                
                //------------------------------------------------------------
                //SE CARGA VARIABLE PARA VIOLENCIA INTERPERSONAL
                //-----------------------------------------------------------
                createCell(row, colPosition++, rowDataTableList.getColumn60());//"ANTECEDENTES AGRESION (INTERPERSONAL)");//100">#{rowX.column60}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn61());//"RELACION AGRESOR-VICTIMA (INTERPERSONAL)");//150">#{rowX.column61}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn30());//"CUAL OTRA RELACION (INTERPERSONAL)");//100">#{rowX.column30}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn62());//"CONTEXTO (INTERPERSONAL)");//200">#{rowX.column62}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn63());//"SEXO AGRESORES (INTERPERSONAL)");//100">#{rowX.column63}</p:c
                //------------------------------------------------------------
                //SE CARGA DATOS PARA VIOLENCIA INTRAFAMILIAR
                //------------------------------------------------------------        
                //tipo de agresor
                createCell(row, colPosition++, rowDataTableList.getColumn64());//"AG1 PADRE(VIF)");//100">#{rowX.column64}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn65());//"AG2 MADRE(VIF)");//100">#{rowX.column65}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn66());//"AG3 PADRASTRO(VIF)");//100">#{rowX.column67}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn67());//"AG4 MADRASTRA(VIF)");//100">#{rowX.column67}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn68());//"AG5 CONYUGE(VIF)");//100">#{rowX.column68}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn69());//"AG6 HERMANO(VIF)");//100">#{rowX.column69}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn70());//"AG7 HIJO(VIF)");//100">#{rowX.column70}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn71());//"AG8 OTRO(VIF)");//100">#{rowX.column71}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn28());//"CUAL OTRO AGRESOR(VIF)");//100">#{rowX.column28}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn72());//"AG9 SIN DATO(VIF)");//100">#{rowX.column72}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn73());//"AG10 NOVIO(VIF)");//100">#{rowX.column73}</p:column>                                
                //tipo de maltrato
                createCell(row, colPosition++, rowDataTableList.getColumn74());//"MA1 FISICO(VIF)");//100">#{rowX.column74}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn75());//"MA2 PSICOLOGICO(VIF)");//100">#{rowX.column75}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn76());//"MA3 VIOLENCIA SEXUAL(VIF)");//100">#{rowX.column76}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn77());//"MA4 NEGLIGENCIA(VIF)");//100">#{rowX.column77}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn78());//"MA5 ABANDONO(VIF)");//100">#{rowX.column78}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn79());//"MA6 INSTITUCIONAL(VIF)");//100">#{rowX.column79}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn80());//"MA SIN DATO(VIF)");//100">#{rowX.column80}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn81());//"MA8 OTRO(VIF)");//100">#{rowX.column81}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn29());//"CUAL OTRO TIPO MALTRATO(VIF)");//100">#{rowX.column29}</p:column>
                //------------------------------------------------------------
                //SE CARGA DATOS PARA TRANSITO
                //------------------------------------------------------------
                createCell(row, colPosition++, rowDataTableList.getColumn112());//"TIPO DE TRANSPORTE");//100">#{rowX.column112}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn31());//"CUAL OTRO TIPO DE TRANSPORTE");//100">#{rowX.column31}</p:column>                                
                createCell(row, colPosition++, rowDataTableList.getColumn113());//"TIPO TRANSPORTE CONTRAPARTE");//100">#{rowX.column113}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn32());//"CUAL OTRO TIPO TRANSPORTE CONTRAPARTE");//100">#{rowX.column32}</p:column>                                
                createCell(row, colPosition++, rowDataTableList.getColumn114());//"TIPO DE TRANSPORTE DEL USUARIO");//100">#{rowX.column114}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn33());//"CUAL OTRO TIPO DE TRANSPORTE DEL USUARIO");//100">#{rowX.column33}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn115());//"CINTURON");//100">#{rowX.column115}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn116());//"CASCO MOTO");//100">#{rowX.column116}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn117());//"CASCO BICICLETA");//100">#{rowX.column117}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn118());//"CHALECO");//100">#{rowX.column118}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn119());//"OTRO ELEMENTO");//100">#{rowX.column119}</p:column>
                tuplesProcessed++;
                progress = (int) (tuplesProcessed * 100) / tuplesNumber;
                System.out.println(progress);
            }
        } catch (Exception ex) {
            Logger.getLogger(RecordSetsHomicideMB.class.getName()).log(Level.SEVERE, null, ex);
        }
        progress = 100;
    }

    /**
     * This method enables or disables the button “MOSTRAR FORMULARIO” according
     * to the selected rows and then display the content.
     */
    public void load() {
        currentNonFatalInjury = null;
        btnEditDisabled = true;
        if (selectedRowsDataTable != null) {
            if (selectedRowsDataTable.length == 1) {
                currentNonFatalInjury = nonFatalInjuriesFacade.find(Integer.parseInt(selectedRowsDataTable[0].getColumn1()));
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
            List<NonFatalInjuries> nonFatalInjuriesList = new ArrayList<NonFatalInjuries>();
            for (int j = 0; j < selectedRowsDataTable.length; j++) {
                nonFatalInjuriesList.add(nonFatalInjuriesFacade.find(Integer.parseInt(selectedRowsDataTable[j].getColumn1())));
            }
            if (nonFatalInjuriesList != null) {
                for (int j = 0; j < nonFatalInjuriesList.size(); j++) {
                    if (nonFatalInjuriesList.get(j).getNonFatalDomesticViolence() != null) {
                        nonFatalDomesticViolenceFacade.remove(nonFatalInjuriesList.get(j).getNonFatalDomesticViolence());
                    }
                    if (nonFatalInjuriesList.get(j).getNonFatalInterpersonal() != null) {
                        nonFatalInterpersonalFacade.remove(nonFatalInjuriesList.get(j).getNonFatalInterpersonal());
                    }
                    if (nonFatalInjuriesList.get(j).getNonFatalSelfInflicted() != null) {
                        nonFatalSelfInflictedFacade.remove(nonFatalInjuriesList.get(j).getNonFatalSelfInflicted());
                    }
                    if (nonFatalInjuriesList.get(j).getNonFatalTransport() != null) {
                        nonFatalTransportFacade.remove(nonFatalInjuriesList.get(j).getNonFatalTransport());
                    }
                    nonFatalInjuriesFacade.remove(nonFatalInjuriesList.get(j));
                    victimsFacade.remove(nonFatalInjuriesList.get(j).getVictimId());
                    //----------------------------------------------------------
                }
            }//deselecciono los controles
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
