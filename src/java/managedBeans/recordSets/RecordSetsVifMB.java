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
import managedBeans.forms.VIFMB;
import model.dao.*;
import model.pojo.NonFatalDomesticViolence;
import model.pojo.NonFatalInjuries;
import model.pojo.Tags;
import model.pojo.Victims;
import org.apache.poi.hssf.usermodel.*;
import org.primefaces.model.LazyDataModel;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "recordSetsVifMB")
@SessionScoped
/**
 * This class handles record set that correspond to VIF
 */
public class RecordSetsVifMB implements Serializable {

    @EJB
    TagsFacade tagsFacade;
    @EJB
    VictimsFacade victimsFacade;
    @EJB
    NonFatalDomesticViolenceFacade nonFatalDomesticViolenceFacade;
    @EJB
    NonFatalInjuriesFacade nonFatalInjuriesFacade;
    private List<Tags> tagsList;
    private NonFatalDomesticViolence currentNonFatalDomesticViolence;
    private RowDataTable[] selectedRowsDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    private String name = "";
    private String newName = "";
    private boolean btnEditDisabled = true;
    private boolean renderControls = true;//se esta combinando con un conjunto de tipo sivigila
    private String data = "-";
    private VIFMB vifMB;
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
    public RecordSetsVifMB() {
        tagsList = new ArrayList<Tags>();
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
        vifMB = (VIFMB) context.getApplication().evaluateExpressionGet(context, "#{vifMB}", VIFMB.class);
        vifMB.loadValues(tagsList, currentNonFatalDomesticViolence);
        openForm = "VIF";
    }

    /**
     * load the information corresponding to a victim within the form
     *
     * @param selectedRowsDataTableTags
     */
    void loadValues(RowDataTable[] selectedRowsDataTableTags) {
        try {
            //CREO LA LISTA DE TAGS SELECCIONADOS 
            exportFileName = "VIF - " + initialDateStr + " - " + endDateStr;
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
            table_model = new LazyRecordSetsDataModel(Integer.parseInt(totalRecords), sql, FormsEnum.SCC_F_033);

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
                rowsDataTableArrayList.add(connection.loadNonFatalDomesticViolenceRecord(resultSet.getString(1)));
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

            createCell(cellStyle, row, colPosition++, "CODIGO INTERNO");
            createCell(cellStyle, row, colPosition++, "INSTITUCION RECEPTORA");
            createCell(cellStyle, row, colPosition++, "NOMBRES Y APELLIDOS");
            createCell(cellStyle, row, colPosition++, "TIPO IDENTIFICACION");
            createCell(cellStyle, row, colPosition++, "IDENTIFICACION");
            createCell(cellStyle, row, colPosition++, "TIPO EDAD");
            createCell(cellStyle, row, colPosition++, "EDAD CANT");
            createCell(cellStyle, row, colPosition++, "GENERO");
            createCell(cellStyle, row, colPosition++, "OCUPACION");
            createCell(cellStyle, row, colPosition++, "ASEGURADORA");
            createCell(cellStyle, row, colPosition++, "EXTRANJERO");
            createCell(cellStyle, row, colPosition++, "DEPARTAMENTO RESIDENCIA");
            createCell(cellStyle, row, colPosition++, "MUNICIPIO RESIDENCIA");
            createCell(cellStyle, row, colPosition++, "BARRIO RESIDENCIA");
            createCell(cellStyle, row, colPosition++, "COMUNA BARRIO RESIDENCIA");

            createCell(cellStyle, row, colPosition++, "DIRECCION RESIDENCIA");
            createCell(cellStyle, row, colPosition++, "TELEFONO");
            createCell(cellStyle, row, colPosition++, "BARRIO EVENTO");
            createCell(cellStyle, row, colPosition++, "CUADRANTE EVENTO");
            createCell(cellStyle, row, colPosition++, "COMUNA BARRIO EVENTO");

            createCell(cellStyle, row, colPosition++, "DIRECCION EVENTO");
            //------------------------------------------------------------
            //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA NO FATAL
            //------------------------------------------------------------        
            createCell(cellStyle, row, colPosition++, "DIA EVENTO");//100">
            createCell(cellStyle, row, colPosition++, "MES EVENTO");//100">
            createCell(cellStyle, row, colPosition++, "AÑO EVENTO");//100">
            createCell(cellStyle, row, colPosition++, "FECHA EVENTO");//100
            createCell(cellStyle, row, colPosition++, "HORA EVENTO");//100"
            createCell(cellStyle, row, colPosition++, "DIA SEMANA EVENTO");
            createCell(cellStyle, row, colPosition++, "DIA CONSULTA");//100
            createCell(cellStyle, row, colPosition++, "MES CONSULTA");//100
            createCell(cellStyle, row, colPosition++, "AÑO CONSULTA");//100
            createCell(cellStyle, row, colPosition++, "FECHA CONSULTA");//1
            createCell(cellStyle, row, colPosition++, "HORA CONSULTA");//10
            createCell(cellStyle, row, colPosition++, "REMITIDO");//100">#{
            createCell(cellStyle, row, colPosition++, "REMITIDO DE DONDE");

            createCell(cellStyle, row, colPosition++, "LUGAR DEL HECHO");//
            createCell(cellStyle, row, colPosition++, "OTRO LUGAR DEL HECHO");
            createCell(cellStyle, row, colPosition++, "ACTIVIDAD");//250">#{row
            createCell(cellStyle, row, colPosition++, "OTRA ACTIVIDAD");//250">
            createCell(cellStyle, row, colPosition++, "MECANISMO / OBJETO DE LA LESION");
            createCell(cellStyle, row, colPosition++, "CUAL ALTURA");//100">#{rowX.column
            createCell(cellStyle, row, colPosition++, "CUAL POLVORA");//100">#{rowX.colum        
            createCell(cellStyle, row, colPosition++, "CUAL OTRO MECANISMO / OBJETO");//1                        
            createCell(cellStyle, row, colPosition++, "USO DE ALCOHOL");//150">#{rowX.col
            createCell(cellStyle, row, colPosition++, "USO DE DROGAS");//150">#{rowX.colu

            createCell(cellStyle, row, colPosition++, "GRADO (QUEMADOS)");//100">#{rowX.c
            createCell(cellStyle, row, colPosition++, "PORCENTAJE(QUEMADOS)");//100">#{ro


            createCell(cellStyle, row, colPosition++, "GRUPO ETNICO");//100">#{
            createCell(cellStyle, row, colPosition++, "OTRO GRUPO ETNICO");//10
            createCell(cellStyle, row, colPosition++, "GRUPO VULNERABLE");//100
            createCell(cellStyle, row, colPosition++, "OTRO GRUPO VULNERABLE");

            //tipo de agresor
            createCell(cellStyle, row, colPosition++, "AG1 PADRE(VIF)");
            createCell(cellStyle, row, colPosition++, "AG2 MADRE(VIF)");
            createCell(cellStyle, row, colPosition++, "AG3 PADRASTRO(VIF)");
            createCell(cellStyle, row, colPosition++, "AG4 MADRASTRA(VIF)");
            createCell(cellStyle, row, colPosition++, "AG5 CONYUGE(VIF)");
            createCell(cellStyle, row, colPosition++, "AG6 HERMANO(VIF)");
            createCell(cellStyle, row, colPosition++, "AG7 HIJO(VIF)");
            createCell(cellStyle, row, colPosition++, "AG8 OTRO(VIF)");
            createCell(cellStyle, row, colPosition++, "CUAL OTRO AGRESOR(VIF)");
            createCell(cellStyle, row, colPosition++, "AG9 SIN DATO(VIF)");
            createCell(cellStyle, row, colPosition++, "AG10 NOVIO(VIF)");

            //tipo de maltrato
            createCell(cellStyle, row, colPosition++, "MA1 FISICO(VIF)");
            createCell(cellStyle, row, colPosition++, "MA2 PSICOLOGICO(VIF)");
            createCell(cellStyle, row, colPosition++, "MA3 VIOLENCIA SEXUAL(VIF)");
            createCell(cellStyle, row, colPosition++, "MA4 NEGLIGENCIA(VIF)");
            createCell(cellStyle, row, colPosition++, "MA5 ABANDONO(VIF)");
            createCell(cellStyle, row, colPosition++, "MA6 INSTITUCIONAL(VIF)");
            createCell(cellStyle, row, colPosition++, "MA SIN DATO(VIF)");
            createCell(cellStyle, row, colPosition++, "MA8 OTRO(VIF)");
            createCell(cellStyle, row, colPosition++, "CUAL OTRO TIPO MALTRATO(VIF)");

            //acciones a realizar
            createCell(cellStyle, row, colPosition++, "AR1 CONCILIACION");
            createCell(cellStyle, row, colPosition++, "AR2 CAUCION");
            createCell(cellStyle, row, colPosition++, "AR3 DICTAMEN MEDICINA LEGAL");
            createCell(cellStyle, row, colPosition++, "AR4 REMISION FISCALIA");
            createCell(cellStyle, row, colPosition++, "AR5 REMISION MEDICINA LEGAL");
            createCell(cellStyle, row, colPosition++, "AR6 REMISION COM FAMILIA");
            createCell(cellStyle, row, colPosition++, "AR7 REMISION ICBF");
            createCell(cellStyle, row, colPosition++, "AR8 MEDIDAS PROTECCION");//100
            createCell(cellStyle, row, colPosition++, "AR9 REMISION A SALUD");//100">
            createCell(cellStyle, row, colPosition++, "AR10 ATENCION PSICOSOCIAL");//
            createCell(cellStyle, row, colPosition++, "AR11 RESTABLECIMIENTO DERECHOS");
            createCell(cellStyle, row, colPosition++, "AR12 OTRA?");//
            createCell(cellStyle, row, colPosition++, "AR CUAL OTRA");
            createCell(cellStyle, row, colPosition++, "AR13 SIN DATO");

            String[] splitDate;
            for (int i = 0; i < rowsDataTableArrayList.size(); i++) {
                colPosition = 0;
                RowDataTable rowDataTableList = rowsDataTableArrayList.get(i);
                rowPosition++;
                row = sheet.createRow(rowPosition);

                createCell(row, colPosition++, rowDataTableList.getColumn1());//"CODIGO INTERNO");//50">#{rowX.column1}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn80());//"INSTITUCION RECEPTORA");//50">#{rowX.column80}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn4());//"NOMBRES Y APELLIDOS");//400">#{rowX.column4}</p:column>                                                
                createCell(row, colPosition++, rowDataTableList.getColumn2());//"TIPO IDENTIFICACION");//200">#{rowX.column2}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn3());//"IDENTIFICACION");//100">#{rowX.column3}</p:column>                
                createCell(row, colPosition++, rowDataTableList.getColumn6());//"TIP EDAD");//100">#{rowX.column6}</p:column>                
                createCell(row, colPosition++, rowDataTableList.getColumn7());//"EDAD CANT");//100">#{rowX.column7}</p:column>                
                createCell(row, colPosition++, rowDataTableList.getColumn8());//"GENERO");//100">#{rowX.column8}</p:column>                
                createCell(row, colPosition++, rowDataTableList.getColumn9());//"OCUPACION");//100">#{rowX.column9}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn18());//"ASEGURADORA");//300">#{rowX.column18}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn5());//"EXTRANJERO");//100">#{rowX.column5}</p:column>  
                createCell(row, colPosition++, rowDataTableList.getColumn13());//"DEPARTAMENTO RESIDENCIA");//100">#{rowX.column13}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn12());//"MUNICIPIO RESIDENCIA");//100">#{rowX.column12}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn15());//"BARRIO RESIDENCIA");//250">#{rowX.column15}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn81());//"COMUNA BARRIO RESIDENCIA");//250">#{rowX.column15}</p:column>

                createCell(row, colPosition++, rowDataTableList.getColumn14());//"DIRECCION RESIDENCIA");//400">#{rowX.column14}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn11());//"TELEFONO");//100">#{rowX.column11}</p:column>

                createCell(row, colPosition++, rowDataTableList.getColumn36());//"BARRIO EVENTO");//250">#{rowX.column36}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn45());//CUADRANTE EVENTO
                createCell(row, colPosition++, rowDataTableList.getColumn82());//"COMUNA BARRIO EVENTO");//250">#{rowX.column36}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn35());//"DIRECCION EVENTO");//400">#{rowX.column35}</p:column>


                //------------------------------------------------------------
                //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA NO FATAL
                //------------------------------------------------------------        
                if (rowDataTableList.getColumn33() != null) {
                    splitDate = rowDataTableList.getColumn33().split("/");
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
                createCell(row, colPosition++, rowDataTableList.getColumn33());//"FECHA EVENTO");//100">#{rowX.column33}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn34());//"HORA EVENTO");//100">#{rowX.column48}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn48());//"DIA SEMANA EVENTO");//100">#{rowX.column57}</p:column>
                if (rowDataTableList.getColumn31() != null) {
                    splitDate = rowDataTableList.getColumn31().split("/");
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
                createCell(row, colPosition++, rowDataTableList.getColumn31());//"FECHA CONSULTA");//100">#{rowX.column31}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn32());//"HORA CONSULTA");//100">#{rowX.column32}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn43());//"REMITIDO");//100">#{rowX.column43}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn44());//"REMITIDO DE DONDE");//250">#{rowX.column44}</p:column>

                createCell(row, colPosition++, rowDataTableList.getColumn37());//"LUGAR DEL HECHO");//200">#{rowX.column37}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn19());//"OTRO LUGAR DEL HECHO");//200">#{rowX.column19}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn38());//"ACTIVIDAD");//250">#{rowX.column38}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn20());//"OTRA ACTIVIDAD");//250">#{rowX.column20}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn46());//"MECANISMO / OBJETO DE LA LESION");//200">#{rowX.column46}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn21());//"CUAL ALTURA");//100">#{rowX.column21}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn22());//"CUAL POLVORA");//100">#{rowX.column22}</p:column>                                
                createCell(row, colPosition++, rowDataTableList.getColumn24());//"CUAL OTRO MECANISMO / OBJETO");//100">#{rowX.column24}</p:column>                                
                createCell(row, colPosition++, rowDataTableList.getColumn39());//"USO DE ALCOHOL");//150">#{rowX.column39}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn40());//"USO DE DROGAS");//150">#{rowX.column40}</p:column>

                createCell(row, colPosition++, rowDataTableList.getColumn41());//"GRADO (QUEMADOS)");//100">#{rowX.column41}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn42());//"PORCENTAJE(QUEMADOS)");//100">#{rowX.column42}</p:column>

                createCell(row, colPosition++, rowDataTableList.getColumn10());//"GRUPO ETNICO");//100">#{rowX.column10}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn26());//"OTRO GRUPO ETNICO");//100">#{rowX.column26}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn16());//"GRUPO VULNERABLE");//100">#{rowX.column16}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn27());//"OTRO GRUPO VULNERABLE");//100">#{rowX.column27}</p:column>

                //tipo de agresor
                createCell(row, colPosition++, rowDataTableList.getColumn49());//"AG1 PADRE(VIF)");//100">#{rowX.column49}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn50());//"AG2 MADRE(VIF)");//100">#{rowX.column50}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn51());//"AG3 PADRASTRO(VIF)");//100">#{rowX.column51}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn52());//"AG4 MADRASTRA(VIF)");//100">#{rowX.column52}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn53());//"AG5 CONYUGE(VIF)");//100">#{rowX.column53}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn54());//"AG6 HERMANO(VIF)");//100">#{rowX.column54}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn55());//"AG7 HIJO(VIF)");//100">#{rowX.column55}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn56());//"AG8 OTRO(VIF)");//100">#{rowX.column56}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn28());//"CUAL OTRO AGRESOR(VIF)");//100">#{rowX.column28}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn57());//"AG9 SIN DATO(VIF)");//100">#{rowX.column57}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn58());//"AG10 NOVIO(VIF)");//100">#{rowX.column58}</p:column>                                

                //tipo de maltrato
                createCell(row, colPosition++, rowDataTableList.getColumn59());//"MA1 FISICO(VIF)");//100">#{rowX.column59}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn60());//"MA2 PSICOLOGICO(VIF)");//100">#{rowX.column60}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn61());//"MA3 VIOLENCIA SEXUAL(VIF)");//100">#{rowX.column61}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn62());//"MA4 NEGLIGENCIA(VIF)");//100">#{rowX.column62}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn62());//"MA5 ABANDONO(VIF)");//100">#{rowX.column63}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn64());//"MA6 INSTITUCIONAL(VIF)");//100">#{rowX.column64}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn65());//"MA SIN DATO(VIF)");//100">#{rowX.column65}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn66());//"MA8 OTRO(VIF)");//100">#{rowX.column66}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn29());//"CUAL OTRO TIPO MALTRATO(VIF)");//100">#{rowX.column29}</p:column>

                //acciones a realizar
                createCell(row, colPosition++, rowDataTableList.getColumn67());//"AR1 CONCILIACION");//100">#{rowX.column67}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn68());//"AR2 CAUCION");//100">#{rowX.column68}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn69());//"AR3 DICTAMEN MEDICINA LEGAL");//100">#{rowX.column69}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn70());//"AR4 REMISION FISCALIA");//100">#{rowX.column70}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn71());//"AR5 REMISION MEDICINA LEGAL");//100">#{rowX.column71}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn72());//"AR6 REMISION COM FAMILIA");//100">#{rowX.column72}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn73());//"AR7 REMISION ICBF");//100">#{rowX.column73}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn74());//"AR8 MEDIDAS PROTECCION");//100">#{rowX.column74}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn75());//"AR9 REMISION A SALUD");//100">#{rowX.column75}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn76());//"AR10 ATENCION PSICOSOCIAL");//100">#{rowX.column76}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn77());//"AR11 RESTABLECIMIENTO DERECHOS");//100">#{rowX.column77}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn78());//"AR12 OTRA?");//100">#{rowX.column78}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn30());//"AR CUAL OTRA");//100">#{rowX.column30}</p:column>
                createCell(row, colPosition++, rowDataTableList.getColumn79());//"AR13 SIN DATO");//100">#{rowX.column79}</p:column>
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
        currentNonFatalDomesticViolence = null;
        btnEditDisabled = true;
        if (selectedRowsDataTable != null) {
            if (selectedRowsDataTable.length == 1) {
                currentNonFatalDomesticViolence = nonFatalDomesticViolenceFacade.find(Integer.parseInt(selectedRowsDataTable[0].getColumn1()));
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
            List<NonFatalDomesticViolence> nonFatalDomesticViolenceList = new ArrayList<NonFatalDomesticViolence>();
            for (int j = 0; j < selectedRowsDataTable.length; j++) {
                nonFatalDomesticViolenceList.add(nonFatalDomesticViolenceFacade.find(Integer.parseInt(selectedRowsDataTable[j].getColumn1())));
            }
            if (nonFatalDomesticViolenceList != null) {
                for (int j = 0; j < nonFatalDomesticViolenceList.size(); j++) {
                    NonFatalInjuries auxNonFatalInjuries = nonFatalDomesticViolenceList.get(j).getNonFatalInjuries();
                    Victims auxVictims = nonFatalDomesticViolenceList.get(j).getNonFatalInjuries().getVictimId();
                    nonFatalDomesticViolenceFacade.remove(nonFatalDomesticViolenceList.get(j));
                    nonFatalInjuriesFacade.remove(auxNonFatalInjuries);
                    victimsFacade.remove(auxVictims);
                }
            }
            selectedRowsDataTable = null;//deselecciono los controles
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

    public boolean isRenderControls() {
        return renderControls;
    }

    public void setRenderControls(boolean renderControls) {
        this.renderControls = renderControls;
    }
}
