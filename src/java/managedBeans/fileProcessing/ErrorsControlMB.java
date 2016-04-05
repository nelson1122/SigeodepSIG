/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.fileProcessing;

import beans.connection.ConnectionJdbcMB;
import beans.enumerators.DataTypeEnum;
import beans.util.RowDataTable;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import managedBeans.filters.ValueNewValue;
import model.dao.ProjectsFacade;
import model.dao.RelationGroupFacade;
import model.pojo.Projects;
import model.pojo.RelationGroup;
import model.pojo.RelationValues;
import model.pojo.RelationVariables;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * The ErrorsControlMB class is responsible for keeping track of errors that are
 * executed during the processing that is done to the data or uploaded files in
 * a new project or an existing one.
 *
 * @author santos
 */
@ManagedBean(name = "errorsControlMB")
@SessionScoped
public class ErrorsControlMB implements Serializable {

    @EJB
    RelationGroupFacade relationGroupFacade;
    @EJB
    ProjectsFacade projectsFacade;
    private boolean btnSolveDisabled = true;
    private boolean btnViewDisabled = true;
    private String currentAceptedValue = "";
    private SelectItem[] aceptedValues;
    private int sizeErrorsList = 0;
    private String currentDateFormat = "dd/MM/yyyy";
    private String currentDateFormatAcepted;
    private String currentNewValue;
    private String valueFound = "";
    private RecordDataMB recordDataMB;
    private ProjectsMB projectsMB;
    private RelationGroup currentRelationsGroup;
    private RelationshipOfVariablesMB relationshipOfVariablesMB;
    DinamicTable dinamicTable = new DinamicTable();
    ConnectionJdbcMB connectionJdbcMB;
    private RowDataTable selectedErrorRowTable;
    private List<RowDataTable> errorsList = new ArrayList<>();
    private RowDataTable selectedCorrectionRowTable;
    private List<RowDataTable> correctionList = new ArrayList<>();
    private List<ValueNewValue> moreInfoModel;

    /**
     * first function executed after the constructor that initializes variables
     * and load the connection to the database.
     */
    @PostConstruct
    private void initialize() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
        recordDataMB = (RecordDataMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{recordDataMB}", RecordDataMB.class);
    }

    /**
     * It is the class constructor.
     */
    public ErrorsControlMB() {
    }

    /**
     * This method is responsible to load the errors occurred in each record.
     */
    public void loadErrorData() {
        /*
         * se selecciona una fila de la tabla e errores
         */
        if (selectedErrorRowTable != null) {
            if (selectedErrorRowTable.getColumn2().compareTo("-") == 0) {
                //NO EXISTE NUMERO DE LINEA, POR QUE ES UN ERROR POR QUE FALTA RELACION DE VARIABLES
                btnSolveDisabled = true;
                btnViewDisabled = true;
                valueFound = "";//valor actual
                currentNewValue = "";
                currentDateFormatAcepted = "";
                aceptedValues = new SelectItem[0];
                currentAceptedValue = "";
            } else if (selectedErrorRowTable.getColumn3().compareTo("-") == 0 && selectedErrorRowTable.getColumn3().compareTo("-") == 0) {
                //SE TRATA DE UN ERROR POR INCONSISTENCIA (ej: llegan datos de intrafamiliar y transito al mismo tiempo)
                btnSolveDisabled = true;
                btnViewDisabled = false;
                valueFound = "";//valor actual
                currentNewValue = "";
                currentDateFormatAcepted = "";
                aceptedValues = new SelectItem[0];
                currentAceptedValue = "";
                createDynamicTable();
            } else {
                //SE TRATA DE UN ERROR POR QUE UN VALOR ESTA PRESENTANDO CONFLICTO
                switch (DataTypeEnum.convert(selectedErrorRowTable.getColumn7())) {//tipo de relacion()
                    case text:
                        aceptedValues = new SelectItem[]{new SelectItem("1", "Cualquier texto"),};
                        break;
                    case integer:
                        aceptedValues = new SelectItem[]{new SelectItem("1", "Número entero"),};
                        break;
                    case age:
                        aceptedValues = new SelectItem[]{new SelectItem("1", "Número entero"),};
                        break;
                    case military:
                        aceptedValues = new SelectItem[]{new SelectItem("1", "Hora militar"),};
                        break;
                    case date:
                        aceptedValues = new SelectItem[]{new SelectItem("1", "Fecha segun el formato"),};
                        break;
                    case day:
                        aceptedValues = new SelectItem[]{new SelectItem("1", "Numero de 1 a 31"),};
                        break;
                    case month:
                        aceptedValues = new SelectItem[]{new SelectItem("1", "Numero de 1 a 12"),};
                        break;
                    case year:
                        aceptedValues = new SelectItem[]{new SelectItem("1", "Entero de 4 cifras"),};
                        break;
                    case minute:
                        aceptedValues = new SelectItem[]{new SelectItem("1", "Numero de 0 a 59"),};
                        break;
                    case hour:
                        aceptedValues = new SelectItem[]{new SelectItem("1", "Numero de 0 a 24"),};
                        break;
//                    case degree:
//                        aceptedValues = new SelectItem[]{new SelectItem("1", "Numero 1, 2, 3"),};
//                        break;
                    case percentage:
                        aceptedValues = new SelectItem[]{new SelectItem("1", "Numero de 1 a 100"),};
                        break;
                    case error:
                        aceptedValues = new SelectItem[]{new SelectItem("1", " "),};
                        break;
                    case NOVALUE:
                        ArrayList<String> categoricalList;
                        if (Boolean.parseBoolean(selectedErrorRowTable.getColumn8())) {//tipo de comparacion
                            categoricalList = connectionJdbcMB.categoricalCodeList(selectedErrorRowTable.getColumn7(), 0);
                        } else {
                            categoricalList = connectionJdbcMB.categoricalNameList(selectedErrorRowTable.getColumn7(), 0);
                        }
                        aceptedValues = new SelectItem[categoricalList.size()];
                        for (int j = 0; j < categoricalList.size(); j++) {
                            aceptedValues[j] = new SelectItem(categoricalList.get(j));
                        }
                        break;
                }
                createDynamicTable();
                btnSolveDisabled = false;
                btnViewDisabled = false;
                valueFound = selectedErrorRowTable.getColumn4();//valor actual
                currentNewValue = "";
                currentDateFormatAcepted = selectedErrorRowTable.getColumn9();
            }
        }
    }

    /**
     * delete a record and corresponding registration errors.
     */
    public void deleteRecordClick() {
        if (selectedErrorRowTable != null) {
            ResultSet rs;
            try {
                //se elimina los registros que correspondan a este registro                
                String sql = ""
                        + " DELETE "
                        + " FROM "
                        + "   project_records "
                        + " WHERE \n"
                        + "    record_id = " + selectedErrorRowTable.getColumn2() + " AND "
                        + "    project_id = " + projectsMB.getCurrentProjectId();
                connectionJdbcMB.non_query(sql);

                String idRow = selectedErrorRowTable.getColumn2();
                //elimino los errores que tengan como id este registro 
                for (int i = 0; i < errorsList.size(); i++) {
                    if (errorsList.get(i).getColumn2().compareTo(idRow) == 0) {
                        //elimino de la lista de errores
                        errorsList.remove(i);
                        sizeErrorsList--;
                        i--;
                    }
                }
                //reseteo variables
                selectedErrorRowTable = null;
                btnSolveDisabled = true;
                btnViewDisabled = true;
                valueFound = "";//valor actual
                currentNewValue = "";
                currentDateFormatAcepted = "";
                aceptedValues = new SelectItem[0];

                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "El registro ha sido eliminado"));
            } catch (Exception e) {
                System.out.println("Error 1 en " + this.getClass().getName() + ":" + e.toString());
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar un error de la lista"));
        }
    }

    /**
     * This method is used to update a record stored in the database.
     */
    public void updateRecordClick() {
        if (selectedErrorRowTable != null) {
            ResultSet rs;
            try {
                //se elimina los registros que correspondan a este registro                
                String sql = ""
                        + " DELETE "
                        + " FROM "
                        + "   project_records "
                        + " WHERE \n"
                        + "    record_id = " + selectedErrorRowTable.getColumn2() + " AND "
                        + "    project_id = " + projectsMB.getCurrentProjectId();
                connectionJdbcMB.non_query(sql);
                //se almacenan los campos que contenga este registro 
                String values = "";
                boolean first = true;
                for (ValueNewValue fields : moreInfoModel) {//recorro cada una de las filas de la tabla
                    if (fields.getOldValue() != null && fields.getOldValue().trim().length() != 0) {
                        if (first) {
                            first = false;
                        } else {
                            values = values + ",";
                        }
                        values = values
                                + "(" + projectsMB.getCurrentProjectId()
                                + "," + selectedErrorRowTable.getColumn2()
                                + "," + fields.getNewValue()
                                + ",'" + fields.getOldValue()
                                + "')";
                    }
                }
                if (values.length() != 0) {//existen datos para registrar                    
                    connectionJdbcMB.non_query("INSERT INTO project_records VALUES " + values + ";");
                }
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "El registro ha sido actualizado"));
            } catch (Exception e) {
                System.out.println("Error 1 en " + this.getClass().getName() + ":" + e.toString());
            }
        }
    }

    /**
     * creates a table with information from the records that have a conflict.
     */
    public final void createDynamicTable() {
        /*
         * crea una tabla con los datos del registro que presenta el conflicto
         */
        ResultSet rs;
        //moreInfoDataTableList = new ArrayList<RowDataTable>();
        moreInfoModel = new ArrayList<>();
        ArrayList<String> column_names = new ArrayList<>();
        ArrayList<String> column_identifiers = new ArrayList<>();
        try {
            rs = connectionJdbcMB.consult(""
                    + " SELECT "
                    + "    project_columns.column_name, "
                    + "    project_columns.column_id "
                    + " FROM "
                    + "    public.project_columns, "
                    + "    public.project_records "
                    + " WHERE "
                    + "    project_columns.column_id = project_records.column_id AND "
                    + "    project_records.project_id = " + projectsMB.getCurrentProjectId()
                    + " GROUP BY "
                    + "    project_columns.column_name, "
                    + "    project_columns.column_id   "
                    + " ORDER BY "
                    + "    project_columns.column_id");
            while (rs.next()) {
                column_names.add(rs.getString("column_name"));
                column_identifiers.add(rs.getString("column_id"));
            }
            rs = connectionJdbcMB.consult(""
                    + " SELECT "
                    + "    project_records.project_id, "
                    + "    project_records.record_id, "
                    + "    array_agg(project_columns.column_name || '<=>' || project_records.data_value) "
                    + " FROM "
                    + "    project_records,project_columns "
                    + " WHERE "
                    + "    project_records.project_id = " + projectsMB.getCurrentProjectId() + " AND "
                    + "    project_columns.column_id = project_records.column_id AND "
                    + "    project_records.record_id = " + selectedErrorRowTable.getColumn2() + " "
                    + " GROUP BY "
                    + "    project_records.project_id, "
                    + "    project_records.record_id ");
            if (rs.next()) {
                //en la tercer columna esta definido un arreglo con id_columna <=> valor encontrado
                String[] newRow = new String[column_names.size()];
                Object[] arrayInJava = (Object[]) rs.getArray(3).getArray();
                for (int i = 0; i < arrayInJava.length; i++) {
                    String splitElement[] = arrayInJava[i].toString().split("<=>");
                    for (int j = 0; j < column_names.size(); j++) {
                        if (column_names.get(j).compareTo(splitElement[0]) == 0) {
                            newRow[j] = splitElement[1];
                            break;
                        }
                    }
                }
                ArrayList<String> newRow2 = new ArrayList<>();
                newRow2.addAll(Arrays.asList(newRow));

                for (int i = 0; i < column_names.size(); i++) {
                    //moreInfoDataTableList.add(new RowDataTable(titles.get(i), newRow2.get(i)));
                    moreInfoModel.add(new ValueNewValue(column_names.get(i), newRow2.get(i), column_identifiers.get(i)));//en new value quedara el id de la columna
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error 2 en " + this.getClass().getName() + ":" + ex.toString());
        }
    }

    /**
     * restore the values.
     */
    public void reset() {
        //correctionList = new ArrayList<RowDataTable>();
        moreInfoModel = null;
        errorsList = new ArrayList<>();
        aceptedValues = new SelectItem[0];
        selectedErrorRowTable = null;
        sizeErrorsList = 0;
        btnSolveDisabled = true;
        btnViewDisabled = true;
        valueFound = "";//valor actual
        currentNewValue = "";
        currentDateFormatAcepted = "";
    }

    /**
     * added when there are errors: inconsistency in registration, no mandatory
     * relationship and a value failures.
     *
     * @param errorsNumber: error number.
     * @param relationVar: relationship of variables obligatory.
     * @param value: error description.
     * @param rowId: record identifier.
     */
    public void addError(int errorsNumber, RelationVariables relationVar, String value, String rowId) {
        if (errorsNumber < 0) {//error cuando existe incoherencia en el registro(ejem: vienen datos de transito y Vif al mismo tiempo)
            errorsList.add(new RowDataTable(
                    String.valueOf(errorsNumber * -1), //  column1 ==> numero del error 
                    rowId, //                         column2 ==> identificador del registro
                    "-", //                            column3 ==> columna (nombre encontrado)
                    "-", //                            column4 ==> valor que presenta el conflicto
                    value //                          column5 ==> descripcion de error(como corregir)
                    ));

        } else if (relationVar == null) {//error cuando falta relacion obligatoria
            errorsList.add(new RowDataTable(
                    String.valueOf(errorsNumber), //                    column1 ==> numero del error 
                    "-", //no rowId (problema en toda columna)          column2 ==> identificador del registro
                    "-", //                                             column3 ==> columna (nombre encontrado)
                    value, //                                             column4 ==> valor que presenta el conflicto
                    rowId)); //                                        column5 ==> descripcion de error(como corregir)
        } else {//error cuando falla el valor 
            errorsList.add(new RowDataTable(
                    String.valueOf(errorsNumber), //                    column1 ==> numero del error 
                    rowId, //                                           column2 ==> identificador del registro
                    relationVar.getNameFound(), //                      column3 ==> columna (nombre encontrado)
                    value, //                                           column4 ==> valor que presenta el conflicto
                    createDescriptions(relationVar, value), //          column5 ==> descripcion de error(como corregir)
                    relationVar.getNameExpected(), //                   column6 ==> nombre variable esperada
                    relationVar.getFieldType(), //                      column7 ==> tipo de variable o tabla categorica
                    relationVar.getComparisonForCode().toString(), //   column8 ==> coparacion por codigo
                    relationVar.getDateFormat()));                  //  column9 ==> formato de fecha    
        }
    }

    /**
     * Create descriptions of error.
     *
     * @param relationVar: relationship of variables obligatory.
     * @param value: error description.
     * @return
     */
    private String createDescriptions(RelationVariables relationVar, String value) {
        String strReturn = null;

        switch (DataTypeEnum.convert(remove_v(relationVar.getFieldType()))) {//tipo de relacion
            case text:
                break;
            case integer:
                strReturn = "Digite en la casilla 'nuevo valor' un número entero  y presione resolver";
                break;
            case age:
                strReturn = "Digite en la casilla 'nuevo valor' una edad válida  y presione resolver. La edad debe ser un número entero o ser especificada en años y meses ejemplo: 3 años 4 meses";
                break;
            case military:
                strReturn = "Una hora militar válida es: * numero de 4 cifras de 0000 a 2400    * horas + minutos  * horas:minutos  4. horas:minutos:segundos:centesimas.milesimas ejemplo 20:23:12.200";
                break;
            case date:
                if (value.length() == 0) {//el valor nulo no es aceptado
                    strReturn = "El valor es obligatorio, ingrese una fecha segun el formato indicado.";
                } else {
                    strReturn = "El valor (" + value + ") debe tener el formato (" + relationVar.getDateFormat() + ") y debe estar entre el 2002 al " + String.valueOf(new Date().getYear() + 1900);
                }
                break;
            case date_of_birth:
                strReturn = "El valor (" + value + ") debe tener el formato (" + relationVar.getDateFormat() + ") y no ser superior a " + String.valueOf(new Date().getYear() + 1900);
                break;
            case day:
                strReturn = "Digite en la casilla 'nuevo valor' un dia válido y presione resolver. El dia debe ser un número entero de 1 a 31";
                break;
            case month:
                strReturn = "Digite en la casilla 'nuevo valor' un mes válido y presione resolver. El mes es un número entero de 1 a 12";
                break;
            case year:
                strReturn = "Digite en la casilla 'nuevo valor' un año válido y presione resolver. El año es un número de 2 o cuatro cifras";
                break;
            case minute:
                strReturn = "Digite en la casilla 'nuevo valor' un minuto válido y presione resolver. El minuto es un número de 0 59";
                break;
            case hour:
                strReturn = "Digite en la casilla 'nuevo valor' una hora válida y presione resolver. La hora es un número de 0 a 24";
                break;
            case percentage:
                strReturn = "Digite en la casilla 'nuevo valor' un porcentaje válido y presione resolver. El porcentaje es un número de 1 a 100";
                break;
            //            case degree:
            //                errorSolution = "Digite en la casilla 'nuevo valor' una grado válido y presione resolver. El grado puede ser: 1, 2, 3";
            //                break;
            case NOVALUE:
                if (value.length() == 0) {//el valor nulo no es aceptado
                    strReturn = "El valor es obligatorio, seleccione un valor de la lista de valores aceptados o digite un valor válido en la casilla (nuevo valor) y presione resolver";
                } else {
                    strReturn = "El valor esperado debe ser: (" + relationVar.getNameExpected() + ") seleccione un valor de la lista de valores aceptados o digite un valor válido en la casilla (nuevo valor) y presione resolver";
                }
                break;

        }
        return strReturn;
    }

    /**
     * discard the errors when they are selected from the list of errors to
     * correct.
     *
     * @return
     */
    public int discardError() {
        boolean correction = false;
        if (selectedErrorRowTable == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Debe seleccionar un error de la lista"));
            return 0;
        } else {
            //verifico que la columna a descartar no sea la de intencionalidad ni fecha evento
            //switch (DataTypeEnum.convert(errorControlArrayList.get(i).getRelationVar().getFieldType())) {//tipo de relacion 
            switch (DataTypeEnum.convert(remove_v(selectedErrorRowTable.getColumn7()))) {//tipo de relacion
                case integer:
                case text:

                case age:
                case military:
                case minute:
                case hour:
                case day:
                case month:
                case year:
                case percentage:
                    correction = true;
                    break;
                case date:
                    if (selectedErrorRowTable.getColumn6().compareTo("fecha_evento") != 0) {//estas relaciones no pueden ser descartadas                                                
                        correction = true;
                    }
                    break;
                case NOVALUE://categorical
                    if (selectedErrorRowTable.getColumn6().compareTo("intencionalidad") != 0) {//estas relaciones no pueden ser descartadas
                        correction = true;
                    }
                    break;
            }
        }
        if (!correction) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Este campo debe contener un valor obligatoriamente; por ello no puede ser descartado");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {//se realiza la actualizacion de la tabla                    
            try {
                Projects currentProject = projectsFacade.find(projectsMB.getCurrentProjectId());
                long columnId = -1;//determino el identificador de la columna
                String sql = ""
                        + "       SELECT \n"
                        + "          project_columns.column_id \n"
                        + "       FROM \n"
                        + "          public.project_records, \n"
                        + "          public.project_columns, \n"
                        + "          public.projects \n"
                        + "       WHERE \n"
                        + "          project_columns.column_id = project_records.column_id AND \n"
                        + "          projects.project_id = project_records.project_id AND \n"
                        + "          project_columns.column_name LIKE '" + selectedErrorRowTable.getColumn3() + "' AND \n"//nombre de la columna                        
                        + "          projects.start_column_id >= " + currentProject.getStartColumnId() + " AND \n"//solo las columnas del proyecto start - end
                        + "          projects.end_column_id <= " + currentProject.getEndColumnId() + " \n"
                        + "       GROUP BY \n"
                        + "          project_columns.column_id \n";
                ResultSet rs = connectionJdbcMB.consult(sql);
                if (rs.next()) {
                    columnId = rs.getLong(1);
                }
                if (columnId != -1) {
                    sql = ""
                            + " DELETE FROM \n"
                            + "    project_records \n"
                            + " WHERE \n"
                            + "    project_id = " + currentProject.getProjectId() + " AND \n"
                            + "    record_id = " + selectedErrorRowTable.getColumn2() + " AND \n"//identificador del registro
                            + "    column_id = " + columnId + " \n";//identificador de la columna
                    connectionJdbcMB.non_query(sql);
                }
                //se elimina el error de la lista de errores y se agrega a lista de correcciones                    
                for (int i = 0; i < errorsList.size(); i++) {
                    if (errorsList.get(i).getColumn1().compareTo(selectedErrorRowTable.getColumn1()) == 0) {
                        //elimino de la lista de errores
                        errorsList.remove(i);
                        sizeErrorsList--;
                        //agrego a la lista de correciones
                        correctionList.add(new RowDataTable(
                                String.valueOf(correctionList.size() + 1), //       column1 ==> identificador de la correcion
                                selectedErrorRowTable.getColumn2(), //              column2 ==> identificador de registro
                                selectedErrorRowTable.getColumn3(), //              column3 ==> nombre columna
                                selectedErrorRowTable.getColumn4(), //              column4 ==> valor anterior
                                "", //  es vacio por que fue descartado             column5 ==> valor actual
                                String.valueOf(columnId), //                        column6 ==> identificador del nombre de columna
                                String.valueOf(currentProject.getProjectId())));//  column7 ==> identificador del proyecto
                        //reseteo variables
                        selectedErrorRowTable = null;
                        btnSolveDisabled = true;
                        btnViewDisabled = true;
                        valueFound = "";//valor actual
                        currentNewValue = "";
                        currentDateFormatAcepted = "";
                        aceptedValues = new SelectItem[0];
                        break;
                    }
                }
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "El valor solucionó el error"));
                return 0;
            } catch (Exception e) {
                System.out.println("Error 3 en " + this.getClass().getName() + ":" + e.toString());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "ALERTA", "no se realizo por: " + e.toString()));
                return 0;
            }
        }
        return 0;
    }

    /**
     * It is responsible to resolve errors by entering a valid value for the
     * user to run the correction.
     *
     * @return
     */
    public int solveError() {
        boolean correction = false;
        if (currentNewValue == null || currentNewValue.trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Debe ingresar un valor para realizar la correción"));
            return 0;
        }
        if (selectedErrorRowTable != null) {
            switch (DataTypeEnum.convert(remove_v(selectedErrorRowTable.getColumn7()))) {//tipo de relacion()
                case text:
                    break;
                case date_of_birth:
                    if (validDateOfBirth(currentNewValue, selectedErrorRowTable.getColumn9())) {
                        correction = true;
                    }
                    break;
                case integer:
                    if (isNumeric(currentNewValue)) {
                        correction = true;
                    }
                    break;
                case age:
                    if (isAge(currentNewValue)) {
                        correction = true;
                    }
                    break;
                case date:
                    if (isDate(currentNewValue, selectedErrorRowTable.getColumn9())) {
                        correction = true;//valor es aceptado como fecha
                        if (selectedErrorRowTable.getColumn6().compareTo("fecha_evento") == 0 || selectedErrorRowTable.getColumn6().compareTo("fecha_consulta") == 0) {
                            if (!validYear(currentNewValue, selectedErrorRowTable.getColumn9())) {//si se trata de una fecha de evento o consulta debe estar del 2003 al año actual
                                correction = false;
                            }
                        }
                    }
                    break;
                case military:
                    if (isMilitary(currentNewValue) == null) {
                        correction = true;
                    }
                    break;
                case hour:
                    if (isHour(currentNewValue)) {
                        correction = true;
                    }
                    break;
                case minute:
                    if (isMinute(currentNewValue)) {
                        correction = true;
                    }
                    break;
                case day:
                    if (isDay(currentNewValue)) {
                        correction = true;
                    }
                    break;
                case month:
                    if (isMonth(currentNewValue)) {
                        correction = true;
                    }
                    break;
                case year:
                    if (isYear(currentNewValue)) {
                        correction = true;
                    }
                    break;
                case percentage:
                    if (isPercentage(currentNewValue)) {
                        correction = true;
                    }
                    break;
                case NOVALUE://categorical
                    //correction = false;
                    if (Boolean.parseBoolean(selectedErrorRowTable.getColumn8()) == true) {
                        if (connectionJdbcMB.findNameByCategoricalCode(remove_v(selectedErrorRowTable.getColumn7()), currentNewValue) != null) {
                            correction = true;
                        }
                    } else {
                        if (connectionJdbcMB.findCodeByCategoricalName(remove_v(selectedErrorRowTable.getColumn7()), currentNewValue) != null) {
                            correction = true;
                        }
                    }
                    break;
            }
            if (!correction) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "El valor ingresado no es aceptado como válido"));
            } else {//se realiza la actualizacion en db
                Projects currentProject = projectsFacade.find(projectsMB.getCurrentProjectId());
                try {
                    long columnId = -1;//determino el identificador de la columna
                    String sql = ""
                            + "       SELECT \n"
                            + "          project_columns.column_id \n"
                            + "       FROM \n"
                            + "          public.project_records, \n"
                            + "          public.project_columns, \n"
                            + "          public.projects \n"
                            + "       WHERE \n"
                            + "          project_columns.column_id = project_records.column_id AND \n"
                            + "          projects.project_id = project_records.project_id AND \n"
                            + "          project_columns.column_name LIKE '" + selectedErrorRowTable.getColumn3() + "' AND \n"//nombre de la columna                        
                            + "          projects.start_column_id >= " + currentProject.getStartColumnId() + " AND \n"//solo las columnas del proyecto start - end
                            + "          projects.end_column_id <= " + currentProject.getEndColumnId() + " \n"
                            + "       GROUP BY \n"
                            + "          project_columns.column_id \n";
                    ResultSet rs = connectionJdbcMB.consult(sql);
                    if (rs.next()) {
                        columnId = rs.getLong(1);
                    }
                    if (columnId != -1) {
                        //determino si este registro existe sino debo crearlo
                        boolean exist = false;
                        sql = ""
                                + " SELECT \n"
                                + "    * \n"
                                + " FROM \n"
                                + "    project_records \n"
                                + " WHERE \n"
                                + "    project_id = " + currentProject.getProjectId() + " AND \n"
                                + "    record_id = " + selectedErrorRowTable.getColumn2() + " AND \n"//identificador del registro
                                + "    column_id = " + columnId + " \n";//identificador de la columna
                        rs = connectionJdbcMB.consult(sql);
                        if (rs.next()) {
                            exist = true;
                        }
                        if (exist) {//si existe se actualiza
                            sql = ""
                                    + " UPDATE \n"
                                    + " project_records \n"
                                    + " SET data_value = '" + currentNewValue + "' \n"
                                    + " WHERE \n"
                                    + "    project_id = " + currentProject.getProjectId() + " AND \n"
                                    + "    record_id = " + selectedErrorRowTable.getColumn2() + " AND \n"//identificador del registro
                                    + "    column_id = " + columnId + " \n";//identificador de la columna                        
                            connectionJdbcMB.non_query(sql);//System.out.println("sql para corregir error \n" + sql);
                        } else {//si no existe se crea
                            sql = ""
                                    + " INSERT INTO project_records VALUES ("
                                    + currentProject.getProjectId() + ","
                                    + selectedErrorRowTable.getColumn2() + ","//identificador del registro
                                    + columnId + ","//identificador de la columna                        
                                    + "'" + currentNewValue + "')";
                            connectionJdbcMB.non_query(sql);//System.out.println("sql para corregir error \n" + sql);
                        }
                    }
                    //se elimina el error de la lista de errores y se agrega a lista de correcciones                    
                    for (int i = 0; i < errorsList.size(); i++) {
                        if (errorsList.get(i).getColumn1().compareTo(selectedErrorRowTable.getColumn1()) == 0) {
                            //elimino de la lista de errores
                            errorsList.remove(i);
                            sizeErrorsList--;
                            //agrego a la lista de correciones
                            correctionList.add(new RowDataTable(
                                    String.valueOf(correctionList.size() + 1), //       column1 ==> identificador de la correcion
                                    selectedErrorRowTable.getColumn2(), //              column2 ==> identificador de registro
                                    selectedErrorRowTable.getColumn3(), //              column3 ==> nombre columna
                                    selectedErrorRowTable.getColumn4(), //              column4 ==> valor anterior
                                    currentNewValue, //                                 column5 ==> valor actual
                                    String.valueOf(columnId), //                        column6 ==> identificador del nombre de columna
                                    String.valueOf(currentProject.getProjectId())));//  column7 ==> identificador del proyecto
                            //reseteo variables
                            selectedErrorRowTable = null;
                            btnSolveDisabled = true;
                            btnViewDisabled = true;
                            valueFound = "";//valor actual
                            currentNewValue = "";
                            currentDateFormatAcepted = "";
                            aceptedValues = new SelectItem[0];
                            break;
                        }
                    }
                    recordDataMB.setBtnRegisterDataDisabled(true);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "El valor solucionó el error"));
                    return 0;
                } catch (Exception e) {
                    System.out.println("Error 4 en " + this.getClass().getName() + ":" + e.toString());
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "ALERTA", "no se realizo por: " + e.toString()));
                    return 0;
                }
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Seleccione un error de la lista"));
        }
        return 0;
    }

    /**
     * is responsible to remove '_v', of a type of data (to take the categorical
     * table)
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

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //VALIDACIONES ---------------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * valid if it is a number from 1 to 31.
     *
     * @param str
     * @return
     */
    private boolean isDay(String str) {
        /*
         * validacion de si un numero de 1 y 31
         */
        if (str.trim().length() == 0) {
            return true;
        }
        try {
            int i = Integer.parseInt(str);
            if (i > 0 && i < 32) {
                return true;
            }
            return false;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * valid if it is a number from 1 to 12.
     *
     * @param str
     * @return
     */
    private boolean isMonth(String str) {
        /*
         * validacion de si un numero de 1 y 12
         */
        if (str.trim().length() == 0) {
            return true;
        }
        try {
            int i = Integer.parseInt(str);
            if (i > 0 && i < 13) {
                return true;
            }
            return false;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * valid if it is a number from 1 to 12.
     *
     * @param str
     * @return
     */
    private boolean isYear(String str) {
        /*
         * validacion de si un numero de 1 y 12
         */
        if (str.trim().length() == 0) {
            return true;
        }
        if (str.trim().length() != 2 && str.trim().length() != 4) {
            return false;
        }
        try {
            int i = Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * valid if a number of percentage comprised in the range 1-100.
     *
     * @param str
     * @return
     */
    private boolean isPercentage(String str) {
        /*
         * validacion de si un numero es porcentaje 1-100
         */
        if (str.trim().length() == 0) {
            return true;
        }
        try {
            int i = Integer.parseInt(str);
            if (i < 0 || i > 100) {
                return false;
            }
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * validates the degree of burn: grade 1, grade 2 and grade 3.
     *
     * @param str
     * @return
     */
    private boolean isDegree(String str) {
        /*
         * Grado quemadura 1,2,3
         */
        if (str.trim().length() == 0) {
            return true;
        }
        try {
            int i = Integer.parseInt(str);
            if (i == 1 || i == 2 || i == 3) {
                return true;
            }
            return false;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * valid if it is a number between 0-59.
     *
     * @param str
     * @return
     */
    private boolean isMinute(String str) {
        /*
         * validacion de si un numero de 1 y 12
         */
        if (str.trim().length() == 0) {
            return true;
        }
        try {
            int i = Integer.parseInt(str);
            if (i > -1 && i < 60) {
                return true;
            }
            return false;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * valid if it is a number between 1 – 12.
     *
     * @param str
     * @return
     */
    private boolean isHour(String str) {
        /*
         * validacion de si un numero de 1 y 12
         */
        if (str.trim().length() == 0) {
            return true;
        }
        try {
            int i = Integer.parseInt(str);
            if (i > 0 && i < 25) {
                return true;
            }
            return false;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * is responsible to determine if a date of birth does not exceed the system
     * date.
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
     * validates if a string is an integer
     *
     * @param str
     * @return
     */
    private boolean isNumeric(String str) {
        /*
         * validacion de si un string es entero
         */
        if (str.trim().length() == 0) {
            return true;
        }
        try {
            str = str.replaceAll(",", "");
            str = str.replaceAll("\\.", "");
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * determine if a date is from 2002 to the current year.
     *
     * @param f
     * @param format
     * @return
     */
    private boolean validYear(String f, String format) {
        /*
         *  determinar si una fecha se encuentra desde el año 2002 hasta el año actual
         */
        boolean booleanreturn = false;
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
     * validates the format of a date, given a text string and format supplied.
     *
     * @param f
     * @param format
     * @return
     */
    private boolean isDate(String f, String format) {
        /*
         *  null=inválido ""=aceptado pero vacio "valor"=aceptado (valor para db)
         */
        if (f.trim().length() == 0) {
            return true;
        }
        try {
            DateTimeFormatter fmt2 = DateTimeFormat.forPattern(format);
            DateTime the_date = DateTime.parse(f, fmt2);//trata de convertir al formato "format"(me llega por parametro)
            return true;
        } catch (Throwable ex) {
            return false;//invalida
        }
    }

    /**
     * validates if the string contains military time format
     *
     * @param str
     * @return
     */
    private String isMilitary(String str) {
        /*
         * validacion de si un string es un hora miitar
         */
        //determinar si hay caracteres----------------------------------------------
        if (str.trim().length() == 0) {
            return "no se acepta cadenas vacias";
        }
        //quitar " AM A.M.----------------------------------------------
        str = str.trim().toUpperCase();
        str = str.replace("AM", "");
        str = str.replace("A.M.", "");
        str = str.replace("\"", "");

        //determinar si es un timestamp----------------------------------------------
        if (str.trim().length() == 12 || str.trim().length() == 8) {
            String[] splitMilitary = str.split(":");
            if (splitMilitary.length == 3) {
                try {
                    int h = Integer.parseInt(splitMilitary[0]);
                    int m = Integer.parseInt(splitMilitary[1]);
                    if (h > 23 || h < 0) {
                        return "La hora debe estar entre 0 y 23";
                    }
                    if (m > 59 || m < 0) {
                        return "los minutos deben estar entre 0 y 59";
                    }
                    return null;
                } catch (Exception ex) {
                }
            }
        }
        //determinar si tiene como separador un :----------------------------------------------
        boolean length2 = false;
        String[] splitMilitary;
        splitMilitary = str.split(":");
        if (splitMilitary.length == 2) {
            length2 = true;
        } else {
            splitMilitary = str.split(",");
            if (splitMilitary.length == 2) {
                length2 = true;
            } else {
                splitMilitary = str.split(";");
                if (splitMilitary.length == 2) {
                    length2 = true;
                } else {
                    splitMilitary = str.split("\\+");
                    if (splitMilitary.length == 2) {
                        length2 = true;
                    } else {
                        splitMilitary = str.split(".");
                        if (splitMilitary.length == 2) {
                            length2 = true;
                        }
                    }
                }
            }
        }

        if (length2) {
            try {
                int h = Integer.parseInt(splitMilitary[0]);
                int m = Integer.parseInt(splitMilitary[1]);
                if (h == 24) {
                    if (m == 0) {
                        return null;
                    } else {
                        return "Si la hora es 24 los minutos solo pueden ser 0";
                    }
                }
                if (h > 24 || h < 0) {
                    return "La hora debe estar entre 0 y 24";
                }
                if (m > 59 || m < 0) {
                    return "los minutos deben estar entre 0 y 59";
                }
                return null;
            } catch (Exception ex) {
            }
        }
        //determinar si tiene caracteres diferentes a    0123456789----------------------------------------------
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) != '0' && str.charAt(i) != '1' && str.charAt(i) != '2'
                    && str.charAt(i) != '3' && str.charAt(i) != '4' && str.charAt(i) != '5'
                    && str.charAt(i) != '6' && str.charAt(i) != '7' && str.charAt(i) != '8'
                    && str.charAt(i) != '9') {
                return "Valor no aceptado como hora militar";
            }
        }
        //verificar si tiene menos de 4 cifras ----------------------------------------------
        if (str.trim().length() < 4) {
            //con tres cifras y mayor de 241            
            //y se evalua los dos ultimos digitos < 60
            //3 40 válido
            //9 99 no válido
            return "Una hora militar con menos de 3 cifras es ambigua";
        }

        //----------------------------------------------
        //verificar si puede ser convertida en numero
        try {
            int n = Integer.parseInt(str);
            if (n <= 2400) {
                return null;
            } else {
                return "Una hora militar tiene como valor maximo 2400";
            }
        } catch (Exception ex) {
        }
        //----------------------------------------------
        //si llego a esta linea es que no supero ningun tipo de validacion
        return "Valor no aceptado como hora militar";
    }

    /**
     * validates if a string is an integer or a defined age in months and years
     *
     * @param str
     * @return
     */
    private boolean isAge(String str) {
        /*
         * validacion de si un string es numero entero o edad definida en meses
         * y años
         */
        if (str.trim().length() == 0) {
            return true;
        }
        try {//intento convertirlo en entero
            int a = Integer.parseInt(str);
            if (a > 150 || a < 0) {
                return false;
            }
            return true;
        } catch (Exception ex) {
        }
        try {//determinar si esta definida en años meses
            String[] splitAge = str.split(" ");
            if (splitAge.length == 4) {
                int m = Integer.parseInt(splitAge[0]);
                int y = Integer.parseInt(splitAge[2]);
                return true;
            }
            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * validates if a string is within a category.
     *
     * @param str
     * @param category
     * @param compareForCode
     * @param relationValueList
     * @return
     */
    private boolean isCategorical(String str, String category, boolean compareForCode, List<RelationValues> relationValueList) {
        /*
         * validacion de si un string esta dentro de una categoria
         */
        if (str.trim().length() == 0) {
            return true;
        }
        ArrayList<String> categoryList = new ArrayList<String>();
        //se valida con respecto a las relaciones de valores
        for (int i = 0; i < relationValueList.size(); i++) {//le paso a categoriList los valores encontrados en la relacion de valores
            categoryList.add(relationValueList.get(i).getNameFound());
        }
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).compareTo(str) == 0) {
                return true;
            }
        }
        //se valida con respecto a los valores esperados
        if (compareForCode == true) {
            categoryList = connectionJdbcMB.categoricalCodeList(category, 0);
        } else {
            categoryList = connectionJdbcMB.categoricalNameList(category, 0);
        }
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).compareTo(str) == 0) {
                return true;
            }
        }
        return false;
    }
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //CLIK SOBRE BOTONES Y LISTAS --------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------

    /**
     * undoes the correction of an current error.
     */
    public void btnUndoErrorClick() {
        String sql;
        if (selectedCorrectionRowTable != null) {
            if (selectedCorrectionRowTable.getColumn4() == null || selectedCorrectionRowTable.getColumn4().trim().length() == 0) {
                //se debe eliminar un registro
                sql = ""
                        + " DELETE FROM \n"
                        + "    project_records \n"
                        + " WHERE \n"
                        + "    project_id = " + selectedCorrectionRowTable.getColumn7() + " AND \n"
                        + "    record_id = " + selectedCorrectionRowTable.getColumn2() + " AND \n"//identificador del registro
                        + "    column_id = " + selectedCorrectionRowTable.getColumn6() + " \n";//identificador de la columna                        

                connectionJdbcMB.non_query(sql);//System.out.println("sql para revertir correcion \n" + sql);
            } else if (selectedCorrectionRowTable.getColumn5() == null || selectedCorrectionRowTable.getColumn5().trim().length() == 0) {
                //se debe insertar nuevo registro
                sql = ""
                        + " INSERT INTO project_records VALUES ("
                        + selectedCorrectionRowTable.getColumn7() + ","//identificador del proyecto
                        + selectedCorrectionRowTable.getColumn2() + ","//identificador del registro
                        + selectedCorrectionRowTable.getColumn6() + ","//identificador de la columna                        
                        + "'" + selectedCorrectionRowTable.getColumn4() + "')";//valor anterior
                connectionJdbcMB.non_query(sql);//System.out.println("sql para revertir correcion \n" + sql);
            } else {//se debe actualizar un registro
                sql = ""
                        + " UPDATE \n"
                        + " project_records \n"
                        + " SET data_value = '" + selectedCorrectionRowTable.getColumn4() + "' \n"//valor anterior
                        + " WHERE \n"
                        + "    project_id = " + selectedCorrectionRowTable.getColumn7() + " AND \n"
                        + "    record_id = " + selectedCorrectionRowTable.getColumn2() + " AND \n"//identificador del registro
                        + "    column_id = " + selectedCorrectionRowTable.getColumn6() + " \n";//identificador de la columna                        
                connectionJdbcMB.non_query(sql);//System.out.println("sql para revertir correcion \n" + sql);
            }
            //se elimina la correcion de la lista de correcciones 
            for (int i = 0; i < correctionList.size(); i++) {
                if (correctionList.get(i).getColumn1().compareTo(selectedCorrectionRowTable.getColumn1()) == 0) {
                    //elimino de la lista de correciones                    
                    correctionList.remove(i);
                    selectedCorrectionRowTable = null;
                    break;
                }
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "El cambio se a revertido"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Seleccione una corrección de la lista"));
        }
    }

    /**
     * establishes a list of accepted values to change.
     */
    public void changeAcceptedValuesList() {
        currentNewValue = currentAceptedValue;
        btnSolveDisabled = false;
        btnViewDisabled = false;
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES GET Y SET DE LAS VARIABLES ---------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public SelectItem[] getAceptedValues() {
        return aceptedValues;
    }

    public void setAceptedValues(SelectItem[] aceptedValues) {
        this.aceptedValues = aceptedValues;
    }

    public boolean isBtnSolveDisabled() {
        return btnSolveDisabled;
    }

    public void setBtnSolveDisabled(boolean btnSolveDisabled) {
        this.btnSolveDisabled = btnSolveDisabled;
    }

    public String getCurrentAceptedValue() {
        return currentAceptedValue;
    }

    public void setCurrentAceptedValue(String currentAceptedValue) {
        this.currentAceptedValue = currentAceptedValue;
    }

    public int getSizeErrorsList() {
        return sizeErrorsList;
    }

    public void setSizeErrorsList(int sizeErrorsList) {
        this.sizeErrorsList = sizeErrorsList;
    }

    public String getValueFound() {
        return valueFound;
    }

    public void setValueFound(String valueFound) {
        this.valueFound = valueFound;
    }

    public String getCurrentDateFormat() {
        return currentDateFormat;
    }

    public void setCurrentDateFormat(String currentDateFormat) {
        this.currentDateFormat = currentDateFormat;
    }

    public String getCurrentNewValue() {
        return currentNewValue;
    }

    public void setCurrentNewValue(String currentNewValue) {
        this.currentNewValue = currentNewValue;
    }

    public RelationGroup getCurrentRelationsGroup() {
        return currentRelationsGroup;
    }

    public void setCurrentRelationsGroup(RelationGroup currentRelationsGroup) {
        this.currentRelationsGroup = currentRelationsGroup;
    }

    public RelationshipOfVariablesMB getRelationshipOfVariablesMB() {
        return relationshipOfVariablesMB;
    }

    public void setRelationshipOfVariablesMB(RelationshipOfVariablesMB relationshipOfVariablesMB) {
        this.relationshipOfVariablesMB = relationshipOfVariablesMB;
    }

    public String getCurrentDateFormatAcepted() {
        return currentDateFormatAcepted;
    }

    public void setCurrentDateFormatAcepted(String currentDateFormatAcepted) {
        this.currentDateFormatAcepted = currentDateFormatAcepted;
    }

    public RowDataTable getSelectedCorrectionRowTable() {
        return selectedCorrectionRowTable;
    }

    public void setSelectedCorrectionRowTable(RowDataTable selectedCorrectionRowTable) {
        this.selectedCorrectionRowTable = selectedCorrectionRowTable;
    }

    public List<RowDataTable> getCorrectionList() {
        return correctionList;
    }

    public void setCorrectionList(List<RowDataTable> correctionList) {
        this.correctionList = correctionList;
    }

    public DinamicTable getDinamicTable() {
        return dinamicTable;
    }

    public void setDinamicTable(DinamicTable dinamicTable) {
        this.dinamicTable = dinamicTable;
    }

    public void setProjectsMB(ProjectsMB projectsMB) {
        this.projectsMB = projectsMB;
    }

    public RowDataTable getSelectedErrorRowTable() {
        return selectedErrorRowTable;
    }

    public void setSelectedErrorRowTable(RowDataTable selectedErrorRowTable) {
        this.selectedErrorRowTable = selectedErrorRowTable;
    }

    public List<RowDataTable> getErrorsList() {
        return errorsList;
    }

    public void setErrorsList(List<RowDataTable> errorsList) {
        this.errorsList = errorsList;
    }
    
    public List<ValueNewValue> getMoreInfoModel() {
        return moreInfoModel;
    }

    public void setMoreInfoModel(List<ValueNewValue> moreInfoModel) {
        this.moreInfoModel = moreInfoModel;
    }

    public boolean isBtnViewDisabled() {
        return btnViewDisabled;
    }

    public void setBtnViewDisabled(boolean btnViewDisabled) {
        this.btnViewDisabled = btnViewDisabled;
    }
}
