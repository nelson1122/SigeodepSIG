/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.filters;

import beans.connection.ConnectionJdbcMB;
import beans.util.RowDataTable;
import java.io.IOException;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import managedBeans.fileProcessing.ErrorsControlMB;
import managedBeans.fileProcessing.ProjectsMB;
import managedBeans.fileProcessing.RecordDataMB;
import managedBeans.fileProcessing.RelationshipOfVariablesMB;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.primefaces.model.DualListModel;
import org.primefaces.model.LazyDataModel;

/**
 * This class allows the user to copy, delete, split, join columns, filter,
 * replicate records, rename values​​, alsto this class is responsible to tandle
 * a history of the realized filters.
 *
 * @author santos
 */
@ManagedBean(name = "filterMB")
@SessionScoped
public class FilterMB {

    private RelationshipOfVariablesMB relationshipOfVariablesMB;
    private ErrorsControlMB errorsControlMB;
    private RecordDataMB recordDataMB;
    private ProjectsMB projectsMB;
    private ConnectionJdbcMB connectionJdbcMB;
    private String sql;
    //copy
    private String variableNameToCopy;
    private List<String> variablesFoundToCopy;
    private List<String> valuesFoundToCopy;
    private int numberOfCopies = 1;
    private String copyPrefix;
    private String variableNameToCopyFilter;
    //delete
    private DualListModel<String> variablesPickToDelete;
    //split
    private String variableNameToSplit;
    private List<String> variablesFoundToSplit;
    private List<String> valuesFoundToSplit;
    private String splitFieldName1;
    private String splitFieldName2;
    private String splitDelimiter;
    private boolean splitRendered = false;
    private boolean splitInclude;
    //merge
    private DualListModel<String> variablesPickToMerge;
    private String variableNameToMerge;
    private String mergeDelimiter;
    //filter records
    private QueryDataModel filter_queryModel;
    private List<String> filter_headers;
    private List<String> filter_field_names;
    private FieldCount[] filter_selected;
    //private boolean btnFilterDisable;
    private int redoFilter;
    private String filter_field;
    private List<String> variablesFoundToFilterRecords;
    //rename
    private List<ValueNewValue> rename_model;
    private List<String> rename_headers;
    private List<String> rename_field_names;
    private int redoRename;
    private String the_field;
    private List<String> variablesFoundToRename;
    //replicate
    private List<String> replicate_source;
    private List<String> replicate_target;
    private List<String> replicateFields;
    //current data
    private LazyDataModel<List> replicate_model2;
    private List<String> replicate_columns2;
    //historial filters
    private List<RowDataTable> filtersAppliedList = new ArrayList<>();
    private String sqlUndo = "";
    private String filterDescription = "";
    private String filterName = "";

    /**
     * This method is responsible of instantiating variables needed to filter
     * data as relations variables, error control, record data, and connection
     * to the database.
     */
    public FilterMB() {
        FacesContext context = FacesContext.getCurrentInstance();
        relationshipOfVariablesMB = (RelationshipOfVariablesMB) context.getApplication().evaluateExpressionGet(context, "#{relationshipOfVariablesMB}", RelationshipOfVariablesMB.class);
        errorsControlMB = (ErrorsControlMB) context.getApplication().evaluateExpressionGet(context, "#{errorsControlMB}", ErrorsControlMB.class);
        recordDataMB = (RecordDataMB) context.getApplication().evaluateExpressionGet(context, "#{recordDataMB}", RecordDataMB.class);
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
        try {
            cpManager = new CopyManager((BaseConnection) connectionJdbcMB.getConn());
        } catch (SQLException ex) {
            System.out.println("Error 1 en " + this.getClass().getName() + ":" + ex.toString());
        }
    }
    //boolean ya = true;

    /**
     * This method is used to set all variables to their default values to begin
     * the processing the data.
     */
    public void reset() {

        //---- copy ------
        variableNameToCopy = "";
        variableNameToCopyFilter = "";
        variablesFoundToCopy = loadFoundVariables(variableNameToCopyFilter);
        valuesFoundToCopy = new ArrayList<>();
        numberOfCopies = 1;
        copyPrefix = "";

        //--- delete ----
        List<String> fieldsSource = loadFoundVariablesRemovingRelated();
        List<String> fieldsTarget = new ArrayList<>();
        variablesPickToDelete = new DualListModel<>(fieldsSource, fieldsTarget);
        //--- split ----

        variableNameToSplit = "";
        variablesFoundToSplit = loadFoundVariables(null);
        valuesFoundToSplit = new ArrayList<>();

        //--- merge ----
        variableNameToMerge = "";
        List<String> fieldsSource2 = loadFoundVariables(null);
        List<String> fieldsTarget2 = new ArrayList<>();
        variablesPickToMerge = new DualListModel<>(fieldsSource2, fieldsTarget2);
        mergeDelimiter = "-";

        //---- filter records -----
        variablesFoundToFilterRecords = loadFoundVariables(null);
        filter_field = "";
        filter_queryModel = new QueryDataModel(new ArrayList<FieldCount>());
        filter_headers = new ArrayList<>();
        filter_headers.add("field");
        filter_headers.add("count");
        filter_field_names = new ArrayList<>();
        filter_field_names.add(filter_field);
        filter_field_names.add("# de Registros");

        //-----remame--------------

        the_field = "";
        variablesFoundToRename = loadFoundVariables(null);
        //btnRenameDisable = true;
        redoRename = 0;
        rename_model = null;
        rename_headers = new ArrayList<>();
        rename_headers.add("oldvalue");
        rename_headers.add("newvalue");
        rename_field_names = new ArrayList<>();
        rename_field_names.add("-");
        rename_field_names.add("# de Registros");

        //-----replicate-----
        replicateFields = loadFoundVariables(null);
        replicate_source = new ArrayList<>();
        replicate_target = new ArrayList<>();

        //---------------------
        replicate_columns2 = connectionJdbcMB.getColumns(projectsMB.getCurrentProjectId());
        replicate_model2 = new LazyQueryDataModel(projectsMB.getCurrentProjectId());

        //if (ya) {ya = false;}

        refreshHistoryList();



    }
    //--------------------------------------------------------------------------
    //-------------------------- FUNCIONES GENERALES ---------------------------
    //--------------------------------------------------------------------------    
    private StringBuilder sb;
    private StringBuilder sb2;
    private CopyManager cpManager;
    private int maxNumberInserts = 1000000;//numero de insert por copy realizado
    private int currentNumberInserts = 0;//numero de inserts actual

    /**
     * This method adds a record to the table project_records using the COPY
     * method of postgres
     *
     * @param projectId
     * @param recordId
     * @param columnId
     * @param dataValue
     */
    private void addTableProjectRecords(int projectId, int recordId, int columnId, String dataValue) {
        /*
         * AGREGA UN REGISTRO A LA TABLA project_records usando el metodo COPY de postgres
         */
        try {
            if (recordId == -1) {
                cpManager.copyIn("COPY project_records FROM STDIN", new StringReader(sb.toString()));
                sb.delete(0, sb.length());
            } else {//continuar agregando los valores para copy                
                sb.
                        append(projectId).append("\t").
                        append(recordId).append("\t").
                        append(columnId).append("\t").
                        append(dataValue).append("\n");
                if (currentNumberInserts % maxNumberInserts == 0) {//se llego al limite de inserts
                    cpManager.copyIn("COPY project_records FROM STDIN", new StringReader(sb.toString()));
                    sb.delete(0, sb.length());
                }

            }
        } catch (SQLException | IOException ex) {
            System.out.println("Error 2 en " + this.getClass().getName() + ":" + ex.toString());
            System.out.println(sb.toString());
        }
    }

    /**
     * This method is used to check if a string has spaces.
     *
     * @param text
     * @return
     */
    private boolean haveSpaces(String text) {
        boolean returnBoolean = false;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == 32) {
                return true;
            }
        }
        return returnBoolean;
    }

    /**
     * This method is used to search columns that contain a specific text.
     *
     * @param text
     * @return
     */
    private boolean searchColumn(String text) {
        boolean returnBoolean = false;
        if (projectsMB != null) {
            try {
                ResultSet rs;
                sql = ""
                        + " SELECT \n"
                        + "	   column_name \n"
                        + " FROM \n"
                        + "	   project_columns \n"
                        + " WHERE \n"
                        + "	   project_id = " + projectsMB.getCurrentProjectId() + " AND \n"
                        + "        column_name LIKE '" + text + "'";
                rs = connectionJdbcMB.consult(sql);            //System.out.println("A002\n" + sql);
                if (rs.next()) {
                    return true;
                }
            } catch (Exception e) {
                System.out.println("Error 3 en " + this.getClass().getName() + ":" + e.toString());
            }
        }
        return returnBoolean;
    }

    /**
     * This method is used to determine the name of a column
     *
     * @param name
     * @return
     */
    private String determineColumnName(String name) {
        String strReturn = name;
        int number = 0;
        boolean continueProcess = true;
        try {
            while (continueProcess) {
                number++;
                strReturn = name + "_" + String.valueOf(number);
                ResultSet rs = connectionJdbcMB.consult(""
                        + " SELECT \n"
                        + "    * \n"
                        + " FROM \n"
                        + "    project_columns \n"
                        + " WHERE \n"
                        + "    project_columns.project_id = " + projectsMB.getCurrentProjectId() + " AND \n"
                        + "    project_columns.column_name LIKE '" + strReturn + "' \n");
                if (!rs.next()) {
                    continueProcess = false;
                }
            }

        } catch (Exception e) {
            System.out.println("Error 4 en " + this.getClass().getName() + ":" + e.toString());
        }
        return strReturn;
    }

    /**
     * This method is responsible to create a list of values of a particular
     * column from the file with no repeated values
     *
     * @param column
     * @return
     */
    private ArrayList<String> loadFoundValues(String column) {
        /*
         * crear una lista de valores de una determinada columna proveniente del
         * archivo con valores no repetidos
         */
        ArrayList<String> arrayReturn = new ArrayList<>();
        try {
            ResultSet rs = connectionJdbcMB.consult(""
                    + " SELECT \n"
                    + " 	DISTINCT(project_records.data_value) \n"
                    + " FROM \n"
                    + " 	project_records \n"
                    + " WHERE \n"
                    + " 	project_id=" + projectsMB.getCurrentProjectId() + " AND \n"
                    + " 	column_id IN \n"
                    + " 		(SELECT \n"
                    + " 			column_id \n"
                    + " 		FROM \n"
                    + " 			project_columns \n"
                    + " 		WHERE \n"
                    + " 			project_columns.column_name LIKE '" + column + "' \n"
                    + " 		) \n"
                    + " LIMIT 50 \n");

            while (rs.next()) {
                arrayReturn.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            System.out.println("Error 5 en " + this.getClass().getName() + ":" + ex.toString());
        }
        return arrayReturn;
    }

    /**
     * This method is responsible for obtaining the list of variables found in a
     * specific project, regardless if they are related or not
     *
     * @param filter
     * @return
     */
    private ArrayList<String> loadFoundVariables(String filter) {
        /*
         * se saca el listado de variables encontradas, sin importar
         * que se encuentren relacionadas o no
         */
        ArrayList<String> arrayReturn = new ArrayList<>();
        if (projectsMB != null) {
            try {
                ResultSet rs;
                String filterConsult = "";
                if (filter != null && filter.trim().length() != 0) {
                    filterConsult = "column_name ILIKE '%" + filter + "%' AND \n";
                }
                sql = ""
                        + " SELECT \n"
                        + "	   column_name,column_id \n"
                        + " FROM \n"
                        + "	   project_columns \n"
                        + " WHERE \n" + filterConsult
                        + "	   project_id = " + projectsMB.getCurrentProjectId() + " \n"
                        + " ORDER BY \n"
                        + "	   column_id \n";
                rs = connectionJdbcMB.consult(sql);            //System.out.println("A002\n" + sql);
                while (rs.next()) {
                    arrayReturn.add(rs.getString(1));
                }
            } catch (Exception e) {
                System.out.println("Error 6 en " + this.getClass().getName() + ":" + e.toString());
            }
        }
        return arrayReturn;
    }

    /**
     * This method gets the list of variables found removing the variables which
     * are related.
     *
     * @return
     */
    private ArrayList<String> loadFoundVariablesRemovingRelated() {
        /*
         * se saca el listado de variables encontradas, se quitan las 
         * que se encuentren ya relacionadas (en relacion de variables)
         */
        ArrayList<String> arrayReturn = new ArrayList<>();
        if (projectsMB != null) {
            try {
                ResultSet rs;
                sql = ""
                        + " SELECT \n"
                        + "	   project_columns.column_name \n"
                        + " FROM \n"
                        + "	   project_columns \n"
                        + " WHERE \n"
                        + "	   project_columns.project_id = " + projectsMB.getCurrentProjectId() + " AND \n"
                        + "	   project_columns.column_name NOT IN \n"
                        + "	   (SELECT \n"
                        + "		relation_variables.name_found \n"
                        + "	   FROM \n"
                        + "		public.relation_group, \n"
                        + "		public.relation_variables \n"
                        + "	   WHERE \n"
                        + "		relation_variables.id_relation_group = relation_group.id_relation_group AND \n"
                        + "		relation_group.name_relation_group LIKE '" + projectsMB.getCurrentRelationsGroupName() + "' \n"
                        + "	   ) \n"
                        + " ORDER BY \n"
                        + "	   project_columns.column_id \n";
                rs = connectionJdbcMB.consult(sql);            //System.out.println("A002\n" + sql);
                while (rs.next()) {
                    arrayReturn.add(rs.getString(1));
                }
            } catch (Exception e) {
                System.out.println("Error 7 en " + this.getClass().getName() + ":" + e.toString());
            }
        }
        return arrayReturn;
    }

    /**
     * This method is used to undo the last filter has been done in the current
     * project.
     */
    public void btnUndoFilterClick() {
        if (filtersAppliedList != null && !filtersAppliedList.isEmpty()) {
            try {
                System.out.println("CONSULTA \n " + filtersAppliedList.get(0).getColumn5() + "");
                connectionJdbcMB.non_query(filtersAppliedList.get(0).getColumn5().replaceAll("\"", "'"));
                connectionJdbcMB.non_query("DELETE FROM project_history_filters WHERE history_id = " + filtersAppliedList.get(0).getColumn2());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "El filtro (" + filtersAppliedList.get(0).getColumn3() + ") se ha revertido."));
                reset();
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo deshacer el filtro. " + e.toString()));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se han aplicado filtros."));
        }
    }

    //--------------------------------------------------------------------------
    //-------------------------- COPY ------------------------------------------
    //--------------------------------------------------------------------------
    /**
     * This method is responsible of obtain the values of the variable to copy,
     * this method also is responsible to get the name in a new variable.
     */
    public void changeVariableFoundToCopy() {
        valuesFoundToCopy = loadFoundValues(variableNameToCopy);
        copyPrefix = variableNameToCopy;
    }

    /**
     * This method is responsible to copy a selected column, the user must
     * specify the name of the new column, select an existing column and finally
     * this method inserts the new column created in project_columns
     */
    public void copyColumnsClick() {
        boolean continueProcess = true;
        if (continueProcess) {
            if (haveSpaces(splitFieldName1) || haveSpaces(splitFieldName2)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Los nuevos nombres de columnas no pueden contener espacios"));
                continueProcess = false;
            }
        }
        if (continueProcess) {
            if (variableNameToCopy == null || variableNameToCopy.length() == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe selecciona una variable de la lista"));
                continueProcess = false;
            }
        }
        if (continueProcess) {
            if (copyPrefix == null || copyPrefix.length() == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe digitar un prefijo para nombrar las nuevas columnas"));
                continueProcess = false;
            }
        }

        if (continueProcess) {
            copyPrefix = copyPrefix.toLowerCase();
            sb = new StringBuilder();
            currentNumberInserts = 0;
            try {
                //determino el maximo id column_id (tabla project_columns)    
                ResultSet rs = connectionJdbcMB.consult(""
                        + " SELECT \n"
                        + "    MAX(column_id) \n"
                        + " FROM \n"
                        + "    project_columns \n");
                rs.next();
                int maxColumnId = rs.getInt(1);
                String newColumn;
                for (int i = 0; i < numberOfCopies; i++) {
                    maxColumnId++;
                    //inserto una nueva columna en project_columns          
                    newColumn = determineColumnName(copyPrefix);
                    sql = ""
                            + " INSERT INTO project_columns VALUES ("
                            + String.valueOf(maxColumnId) + ",'"
                            + String.valueOf(newColumn) + "',"
                            + String.valueOf(projectsMB.getCurrentProjectId()) + ")";
                    connectionJdbcMB.non_query(sql);

                    if (filterDescription.length() == 0) {
                        filterDescription = "" + newColumn;
                    } else {
                        filterDescription = filterDescription + ", " + newColumn;
                    }
                    sqlUndo = sqlUndo + " DELETE FROM project_columns WHERE column_id = " + String.valueOf(maxColumnId) + " AND project_id = " + String.valueOf(projectsMB.getCurrentProjectId()) + ";\n ";

                    //realizo la copia de la columna
                    rs = connectionJdbcMB.consult(""
                            + " SELECT \n"
                            + "    * \n"
                            + " FROM \n"
                            + "    project_records \n"
                            + " WHERE \n"
                            + "    column_id IN "
                            + "    ( "
                            + "      SELECT \n"
                            + "         column_id \n"
                            + "      FROM \n"
                            + "         project_columns \n"
                            + "      WHERE \n"
                            + "         project_columns.project_id = " + projectsMB.getCurrentProjectId() + " AND "
                            + "         project_columns.column_name LIKE '" + variableNameToCopy + "' "
                            + "    )");
                    while (rs.next()) {
                        addTableProjectRecords(rs.getInt("project_id"), rs.getInt("record_id"), maxColumnId, rs.getString("data_value"));
                        currentNumberInserts++;
                    }
                    sqlUndo = " DELETE FROM project_records WHERE column_id = " + String.valueOf(maxColumnId) + " AND project_id = " + String.valueOf(projectsMB.getCurrentProjectId()) + ";\n " + sqlUndo;
                }
                addTableProjectRecords(-1, -1, -1, "");//terminar de guardar los restantes
                filterDescription = "Se realizó la copia de (" + variableNameToCopy + ") en (" + filterDescription + ")";
                filterName = "COPIAR COLUMNAS";
                insertingFilterInHistory(filterName, filterDescription, sqlUndo);
                reset();//limpiar 
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha realizado la copia de columnas"));
            } catch (SQLException ex) {
                System.out.println("Error 8 en " + this.getClass().getName() + ":" + ex.toString());
            }
        }
    }

    /**
     * This method is used to obtain the values of a variable to be copied.
     */
    public void changeVariableNameToCopyFilter() {
        loadFoundVariables(variableNameToCopyFilter);
        copyPrefix = "";
        valuesFoundToCopy = new ArrayList<>();
        variableNameToCopy = "";
    }

    //--------------------------------------------------------------------------
    //------------------------- DELETE -----------------------------------------
    //--------------------------------------------------------------------------
    /**
     * This method is responsible for deleting a selected column of the project
     * the user is working, to remove the column is necessary that the user has
     * previously selected a column.
     */
    public void deleteVariables() {
        if (variablesPickToDelete.getTarget().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Agrege a la segunda lista las variables a eliminar"));
        } else {
            try {
                ResultSet rs, rs2;
                for (int i = 0; i < variablesPickToDelete.getTarget().size(); i++) {
                    //determino el identificador de la columna
                    rs = connectionJdbcMB.consult(""
                            + " SELECT \n"
                            + "    * \n"
                            + " FROM \n"
                            + "    project_records \n"
                            + " WHERE \n"
                            + "    column_id IN "
                            + "    ( "
                            + "      SELECT \n"
                            + "         column_id \n"
                            + "      FROM \n"
                            + "         project_columns \n"
                            + "      WHERE \n"
                            + "         project_columns.project_id = " + projectsMB.getCurrentProjectId() + " AND \n"
                            + "         project_columns.column_name LIKE '" + variablesPickToDelete.getTarget().get(i) + "' "
                            + "    )");
                    if (rs.next()) {
                        //genero el sql para deshacer eliminaciones en project_records
                        rs2 = connectionJdbcMB.consult(""
                                + " SELECT \n"
                                + "    * \n"
                                + " FROM \n"
                                + "    project_records \n"
                                + " WHERE \n"
                                + "    column_id = " + rs.getString("column_id") + " AND \n"
                                + "    project_id = " + projectsMB.getCurrentProjectId() + " \n");
                        sqlUndo = sqlUndo + "INSERT INTO project_records VALUES ";
                        boolean first = true;
                        while (rs2.next()) {
                            if (first) {
                                first = false;
                            } else {
                                sqlUndo = sqlUndo + ",";
                            }
                            sqlUndo = sqlUndo
                                    + "(" + rs2.getString(1)
                                    + "," + rs2.getString(2)
                                    + "," + rs2.getString(3)
                                    + ",'" + rs2.getString(4)
                                    + "')\n";
                        }
                        sqlUndo = sqlUndo + ";\n";

                        //elimino lol registros de esta columna
                        connectionJdbcMB.non_query(""
                                + " DELETE FROM \n"
                                + "    project_records \n"
                                + " WHERE \n"
                                + "    column_id = " + rs.getString("column_id") + " AND \n"
                                + "    project_id = " + projectsMB.getCurrentProjectId() + " \n");
                    }
                    //genero el sql para deshacer eliminaciones en project_columns
                    rs = connectionJdbcMB.consult(""
                            + "      SELECT \n"
                            + "         column_id \n"
                            + "      FROM \n"
                            + "         project_columns \n"
                            + "      WHERE \n"
                            + "         project_columns.project_id = " + projectsMB.getCurrentProjectId() + " AND \n"
                            + "         project_columns.column_name LIKE '" + variablesPickToDelete.getTarget().get(i) + "'");
                    if (rs.next()) {
                        sqlUndo = " INSERT INTO project_columns VALUES ("
                                + rs.getString("column_id") + ",'"
                                + variablesPickToDelete.getTarget().get(i) + "',"
                                + projectsMB.getCurrentProjectId() + ");\n" + sqlUndo;
                        if (filterDescription.length() == 0) {
                            filterDescription = variablesPickToDelete.getTarget().get(i);
                        } else {
                            filterDescription = filterDescription + ", " + variablesPickToDelete.getTarget().get(i);
                        }
                    }
                    //elimino la columna
                    connectionJdbcMB.non_query(""
                            + " DELETE FROM \n"
                            + "    project_columns \n"
                            + " WHERE \n"
                            + "    project_columns.project_id = " + projectsMB.getCurrentProjectId() + " AND \n"
                            + "    project_columns.column_name LIKE '" + variablesPickToDelete.getTarget().get(i) + "' ");
                }
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "La eliminacion de las columnas se ha realizado correctamente"));

                filterDescription = "Se realizo la eliminacion de (" + filterDescription + ")";
                filterName = "ELIMINAR COLUMNAS";
                insertingFilterInHistory(filterName, filterDescription, sqlUndo);
                reset();
                relationshipOfVariablesMB.refresh();
            } catch (Exception e) {
                System.out.println("Error 9 en " + this.getClass().getName() + ":" + e.toString());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.toString()));
            }
        }
    }

    //--------------------------------------------------------------------------
    //------------------------- SPLIT -----------------------------------------
    //--------------------------------------------------------------------------
    /**
     * This method is responsible to Load the values corresponding to the
     * variable to be separated.
     */
    public void changeVariableFoundToSplit() {
        valuesFoundToSplit = loadFoundValues(variableNameToSplit);
    }

    /**
     * This method is responsible for separating the selected column in 2 new
     * columns, for it is necessary to specify a separator and assign different
     * names to the new generated columns.
     */
    public void splitColumnsClick() {
        boolean continueProcess = true;
        if (continueProcess) {
            if (variableNameToSplit == null || variableNameToSplit.trim().length() == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se deben seleccionar la variable a dividir"));
                continueProcess = false;
            }
        }
        if (continueProcess) {
            if (splitFieldName1 == null || splitFieldName1.trim().length() == 0 || splitFieldName2 == null || splitFieldName2.trim().length() == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se deben digitar los dos nuevos nombres de columna"));
                continueProcess = false;
            }
        }
        if (continueProcess) {
            if (splitDelimiter == null || splitDelimiter.length() == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe digitar un separador"));
                continueProcess = false;
            }
        }
        if (continueProcess) {
            if (splitFieldName1.compareToIgnoreCase(splitFieldName2) == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Los nuevos nombres de columnas no pueden ser iguales"));
                continueProcess = false;
            }
        }
        if (continueProcess) {
            if (haveSpaces(splitFieldName1) || haveSpaces(splitFieldName2)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Los nuevos nombres de columnas no pueden contener espacios"));
                continueProcess = false;
            }
        }
        if (continueProcess) {
            splitFieldName1 = splitFieldName1.toLowerCase();
            splitFieldName2 = splitFieldName2.toLowerCase();
            //determinar si ya existen estos nombres 
            if (searchColumn(splitFieldName1)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe una columna de nombre (" + splitFieldName1 + "), debe ser cambiado"));
                continueProcess = false;
            }
            if (searchColumn(splitFieldName2)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe una columna de nombre (" + splitFieldName2 + "), debe ser cambiado"));
                continueProcess = false;
            }
        }
        if (continueProcess) {
            try {
                sb = new StringBuilder();
                currentNumberInserts = 0;
                //determino el maximo id column_id (tabla project_columns)    
                ResultSet rs = connectionJdbcMB.consult(""
                        + " SELECT \n"
                        + "    MAX(column_id) \n"
                        + " FROM \n"
                        + "    project_columns \n");
                rs.next();
                int maxColumnId = rs.getInt(1);

                maxColumnId++;
                //inserto una nueva columna en project_columns
                connectionJdbcMB.non_query(""
                        + " INSERT INTO project_columns VALUES ("
                        + String.valueOf(maxColumnId) + ",'"
                        + String.valueOf(splitFieldName1) + "',"
                        + String.valueOf(projectsMB.getCurrentProjectId()) + ")");

                sqlUndo = sqlUndo + " DELETE FROM project_columns WHERE column_id = " + String.valueOf(maxColumnId) + " AND project_id = " + String.valueOf(projectsMB.getCurrentProjectId()) + ";\n ";

                connectionJdbcMB.non_query(""
                        + " INSERT INTO project_columns VALUES ("
                        + String.valueOf(maxColumnId + 1) + ",'"
                        + String.valueOf(splitFieldName2) + "',"
                        + String.valueOf(projectsMB.getCurrentProjectId()) + ")");
                sqlUndo = sqlUndo + " DELETE FROM project_columns WHERE column_id = " + String.valueOf(maxColumnId + 1) + " AND project_id = " + String.valueOf(projectsMB.getCurrentProjectId()) + ";\n ";

                //realizo la copia de la columna
                rs = connectionJdbcMB.consult(""
                        + " SELECT \n"
                        + "    * \n"
                        + " FROM \n"
                        + "    project_records \n"
                        + " WHERE \n"
                        + "    column_id IN "
                        + "    ( "
                        + "      SELECT \n"
                        + "         column_id \n"
                        + "      FROM \n"
                        + "         project_columns \n"
                        + "      WHERE \n"
                        + "         project_columns.project_id = " + projectsMB.getCurrentProjectId() + " AND "
                        + "         project_columns.column_name LIKE '" + variableNameToSplit + "' "
                        + "    )");
                String[] splitValue;
                while (rs.next()) {
                    splitValue = splitByDigit(rs.getString("data_value"));
                    if (splitValue != null && splitValue.length > 0) {
                        if (splitValue[0].trim().length() != 0) {
                            addTableProjectRecords(rs.getInt("project_id"), rs.getInt("record_id"), maxColumnId, splitValue[0]);
                            currentNumberInserts++;
                        }
                    }
                    if (splitValue != null && splitValue.length > 1) {
                        if (splitValue[1].trim().length() != 0) {
                            addTableProjectRecords(rs.getInt("project_id"), rs.getInt("record_id"), maxColumnId + 1, splitValue[1]);
                            currentNumberInserts++;
                        }//                      
                    }
                }
                addTableProjectRecords(-1, -1, -1, "");//terminar de guardar los registros restantes
                sqlUndo = sqlUndo + " DELETE FROM project_records WHERE column_id = " + String.valueOf(maxColumnId) + " AND project_id = " + String.valueOf(projectsMB.getCurrentProjectId()) + ";\n " + sqlUndo;
                sqlUndo = sqlUndo + " DELETE FROM project_records WHERE column_id = " + String.valueOf(maxColumnId + 1) + " AND project_id = " + String.valueOf(projectsMB.getCurrentProjectId()) + ";\n " + sqlUndo;


                filterDescription = "Se realizo la división de la columna (" + variableNameToSplit + ") en (" + splitFieldName1 + ", " + splitFieldName2 + ")";
                filterName = "DIVIDIR COLUMNAS";
                insertingFilterInHistory(filterName, filterDescription, sqlUndo);
                reset();
                relationshipOfVariablesMB.refresh();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha realizado la división de columnas"));
            } catch (SQLException ex) {
                System.out.println("Error 10 en " + this.getClass().getName() + ":" + ex.toString());
            }
        }
    }

    /**
     * This method is responsible to divide the values of a column through a
     * delimiter that is digit. the part that is before the specified digit
     * corresponds to the first column and the part that is after the digit
     * corresponding to the second column. if the delimiter is not a digit then
     * do a normal division by character.
     *
     * @param text
     * @return
     */
    public String[] splitByDigit(String text) {
        String[] split;
        char chr;
        boolean foundDigit = false;
        String string1 = "";
        String string2 = "";

        if (text == null || text.length() == 0) {
            return null;
        }
        split = new String[2];
        split[0] = "";
        split[1] = "";
        if (splitDelimiter.compareTo("#") == 0) {//REALIZAR DIVISION POR NUMEROS            
            for (int i = 0; i < text.length(); i++) {
                chr = text.charAt(i);
                if (isDigit(chr)) {//es digito
                    foundDigit = true;//digito enconrado
                    if (splitInclude) {//incluir digitos                    
                        string2 = string2 + chr;
                    }
                } else {//no es digito
                    if (foundDigit) {//se encontro digito
                        string2 = string2 + chr;
                    } else {
                        string1 = string1 + chr;
                    }
                }
            }
            split[0] = string1;
            split[1] = string2;
        } else {//REALIZAR SPLIT NORMAL
            if (text.indexOf(splitDelimiter) == -1) {
                split[0] = text;
            } else {
                split[0] = text.substring(0, text.indexOf(splitDelimiter));
                split[1] = text.substring(text.indexOf(splitDelimiter) + splitDelimiter.length(), text.length());
            }
        }
        return split;
    }

    /**
     * This method is used to check if a character corresponds to a digit or
     * not.
     *
     * @param chr
     * @return
     */
    private boolean isDigit(char chr) {
        if (chr >= 48 && chr <= 57) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method is used to check if a delimiter is set to realize the
     * division.
     */
    public void setRenders() {
        if ("#".equals(splitDelimiter.trim())) {
            splitRendered = true;
        } else {
            splitRendered = false;
        }
    }

    //--------------------------------------------------------------------------
    //---------------------------- MERGE ---------------------------------------
    //--------------------------------------------------------------------------
    /**
     * This method is responsible to join two or more columns and generate a new
     * column that subsequently is goint to register in project_columns. The
     * records that made up the ancient columns are joined according to the
     * specified separator.
     */
    public void mergeFieldsClick() {
        boolean continueProcess = true;
        if (mergeDelimiter == null) {
            mergeDelimiter = "";
        }
        if (continueProcess) {
            if (variablesPickToMerge.getTarget().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Agrege a la segunda lista las variables a unir"));
                continueProcess = false;
            }
        }
        if (continueProcess) {
            if (variablesPickToMerge.getTarget().size() < 2) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se deben agregar mínimo dos variables para realizar la unión"));
                continueProcess = false;
            }
        }
        if (continueProcess) {
            if (variableNameToMerge == null || variableNameToMerge.trim().length() == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe digitar el nombre de la nueva columna"));
                continueProcess = false;
            }
        }
        if (continueProcess) {
            if (haveSpaces(variableNameToMerge)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El nuevos nombre de columna no puede contener espacios"));
                continueProcess = false;
            }
        }
        if (continueProcess) {
            variableNameToMerge = variableNameToMerge.toLowerCase();
            //determinar si ya existen estos nombres 
            if (searchColumn(variableNameToMerge)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe una columna de nombre (" + variableNameToMerge + "), debe ser cambiado"));
                continueProcess = false;
            }
        }
        if (continueProcess) {
            try {
                sb = new StringBuilder();
                currentNumberInserts = 0;
                //determino el maximo id column_id (tabla project_columns)    
                ResultSet rs = connectionJdbcMB.consult(""
                        + " SELECT \n"
                        + "    MAX(column_id) \n"
                        + " FROM \n"
                        + "    project_columns \n");
                rs.next();
                int maxColumnId = rs.getInt(1);

                maxColumnId++;
                //inserto una nueva columna en project_columns
                sql = ""
                        + " INSERT INTO project_columns VALUES ("
                        + String.valueOf(maxColumnId) + ",'"
                        + String.valueOf(variableNameToMerge) + "',"
                        + String.valueOf(projectsMB.getCurrentProjectId()) + ")";
                connectionJdbcMB.non_query(sql);
                sqlUndo = sqlUndo + " DELETE FROM project_columns WHERE column_id = " + String.valueOf(maxColumnId) + " AND project_id = " + String.valueOf(projectsMB.getCurrentProjectId()) + ";\n " + sqlUndo;
                ArrayList<String> dataRecords = new ArrayList<>();

                //determinamos las variables(column_id)
                ArrayList<String> columns_id = new ArrayList<>();
                for (int i = 0; i < variablesPickToMerge.getTarget().size(); i++) {
                    rs = connectionJdbcMB.consult(""
                            + " SELECT \n"
                            + "    column_id \n"
                            + " FROM \n"
                            + "    project_columns \n"
                            + " WHERE \n"
                            + "    project_columns.project_id = " + projectsMB.getCurrentProjectId() + " AND "
                            + "    project_columns.column_name LIKE '" + variablesPickToMerge.getTarget().get(i) + "' ");
                    if (filterDescription.length() == 0) {
                        filterDescription = variablesPickToMerge.getTarget().get(i);
                    } else {
                        filterDescription = filterDescription + ", " + variablesPickToMerge.getTarget().get(i);
                    }
                    if (rs.next()) {
                        columns_id.add(rs.getString(1));
                    }
                }
                //determinamos los identificadores de los registros
                ArrayList<String> records_id = new ArrayList<>();
                rs = connectionJdbcMB.consult(""
                        + " SELECT \n"
                        + "    DISTINCT(record_id) \n"
                        + " FROM \n"
                        + "    project_records \n"
                        + " WHERE \n"
                        + "    project_id = " + projectsMB.getCurrentProjectId() + " \n"
                        + " ORDER BY "
                        + "    record_id ");
                while (rs.next()) {
                    records_id.add(rs.getString(1));
                }
                //consulta que retorna los valores a agrupar por cada fila
                for (int j = 0; j < records_id.size(); j++) {
                    sql = " SELECT ";
                    for (int i = 0; i < columns_id.size(); i++) {
                        sql = sql
                                + " ( SELECT \n"
                                + "      data_value \n"
                                + "   FROM \n"
                                + "      project_records \n"
                                + "   WHERE \n"
                                + "      project_id = " + projectsMB.getCurrentProjectId() + " AND \n"
                                + "      record_id = " + records_id.get(j) + " AND \n"
                                + "      column_id = " + columns_id.get(i) + "  \n"
                                + " ) AS var" + String.valueOf(i) + "";
                        if (i != columns_id.size() - 1) {
                            sql = sql + ", \n";
                        }
                    }
                    rs = connectionJdbcMB.consult(sql);
                    if (rs.next()) {
                        String salida = "";
                        for (int i = 0; i < columns_id.size(); i++) {
                            if (rs.getString(i + 1) != null && rs.getString(i + 1).length() != 0) {
                                if (i != columns_id.size() - 1) {
                                    salida = salida + rs.getString(i + 1) + mergeDelimiter;
                                } else {
                                    salida = salida + rs.getString(i + 1);
                                }
                            }
                        }
                        addTableProjectRecords(projectsMB.getCurrentProjectId(), j, maxColumnId, salida);
                        currentNumberInserts++;
//                        sql = ""
//                                + " INSERT INTO project_records VALUES ("
//                                + projectsMB.getCurrentProjectId() + ","
//                                + String.valueOf(j) + ","
//                                + String.valueOf(maxColumnId) + ",'"
//                                + salida + "')";
//                        connectionJdbcMB.non_query(sql);
                    }
                }
                addTableProjectRecords(-1, -1, -1, "");//terminar de guardar los registros restantes
                sqlUndo = sqlUndo + " DELETE FROM project_records WHERE column_id = " + String.valueOf(maxColumnId) + " AND project_id = " + String.valueOf(projectsMB.getCurrentProjectId()) + ";\n " + sqlUndo;
                filterDescription = "Se realizo en (" + variableNameToMerge + ") la unión de las columnas (" + filterDescription + ")";
                filterName = "UNIR COLUMNAS";
                insertingFilterInHistory(filterName, filterDescription, sqlUndo);
                reset();
                relationshipOfVariablesMB.refresh();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha realizado la union de columnas"));
            } catch (SQLException ex) {
                System.out.println("Error 11 en " + this.getClass().getName() + ":" + ex.toString());
            }

        }

    }

    //--------------------------------------------------------------------------
    //---------------------- FILTER RECORDS ------------------------------------
    //--------------------------------------------------------------------------
    /**
     * This method is responsible for allocating all the records to be filtered
     * according to the variables selected by the user.
     */
    public void changeVariablesFoundToFilterRecords() {
        filter_queryModel = new QueryDataModel(getFieldCounts(filter_field));
        filter_field_names = new ArrayList<>();
        filter_field_names.add(filter_field);
        filter_field_names.add("# de Registros");
    }

    /**
     * This method is responsible to obtain all records be filtered according to
     * what the user has selected.
     *
     * @param field
     * @return
     */
    public List<FieldCount> getFieldCounts(String field) {
        try {
            List<FieldCount> data = new ArrayList<>();
            String query = ""
                    + " SELECT "
                    + "   data_value, count(*) "
                    + " FROM "
                    + "   project_records "
                    + " WHERE \n"
                    + "    column_id IN "
                    + "    ( "
                    + "      SELECT \n"
                    + "         column_id \n"
                    + "      FROM \n"
                    + "         project_columns \n"
                    + "      WHERE \n"
                    + "         project_columns.project_id = " + projectsMB.getCurrentProjectId() + " AND "
                    + "         project_columns.column_name LIKE '" + field + "' "
                    + "    ) AND "
                    + "    project_id = " + projectsMB.getCurrentProjectId() + ""
                    + " GROUP BY 1 ORDER BY 2 DESC"
                    + "";
            ResultSet records = connectionJdbcMB.consult(query);
            while (records.next()) {
                FieldCount fc = new FieldCount(records.getString(1), records.getInt(2));
                data.add(fc);
            }
            return data;
        } catch (SQLException ex) {
            System.out.println("Error 12 en " + this.getClass().getName() + ":" + ex.toString());
            return null;
        }
    }

    /**
     * This method allows the user to filter the records according to the
     * selection made by that user, so that it displays only the records that
     * are really important to be stored in the current project. for the filter
     * the user need to select a variable and a least one value to filter.
     */
    public void filterRecordsClick() {
        boolean continueProcess = true;

        if (continueProcess) {
            if (filter_field == null || filter_field.length() == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar la variable a filtrar"));
                continueProcess = false;
            }
        }
        if (continueProcess) {
            if (filter_selected == null || filter_selected.length == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se ha seleccionado ningun valor a filtrar"));
                continueProcess = false;
            }
        }

        if (continueProcess) {
            try {
                for (FieldCount record : filter_selected) {

                    if (filterDescription.length() == 0) {
                        filterDescription = record.getField();
                    } else {
                        filterDescription = filterDescription + "," + record.getField();
                    }
                    //System.out.println("Deleted " + record.getField());
                    sql = ""
                            + " SELECT record_id "
                            + " FROM "
                            + "   project_records "
                            + " WHERE \n"
                            + "    column_id IN "
                            + "    ( "
                            + "      SELECT \n"
                            + "         column_id \n"
                            + "      FROM \n"
                            + "         project_columns \n"
                            + "      WHERE \n"
                            + "         project_columns.project_id = " + projectsMB.getCurrentProjectId() + " AND "
                            + "         project_columns.column_name LIKE '" + filter_field + "' "
                            + "    ) AND "
                            + "    project_id = " + projectsMB.getCurrentProjectId() + " AND "
                            + "    data_value LIKE '" + record.getField() + "'";
                    ResultSet rs = connectionJdbcMB.consult(sql);
                    ResultSet rs2;
                    sqlUndo = sqlUndo + " INSERT INTO project_records VALUES ";
                    boolean first = true;
                    while (rs.next()) {
                        rs2 = connectionJdbcMB.consult(""
                                + " SELECT "
                                + "    * "
                                + " FROM "
                                + "    project_records "
                                + " WHERE "
                                + "    record_id = " + rs.getString(1) + " AND "
                                + "    project_id = " + projectsMB.getCurrentProjectId());
                        while (rs2.next()) {
                            if (first) {
                                first = false;
                            } else {
                                sqlUndo = sqlUndo + ",";
                            }
                            sqlUndo = sqlUndo
                                    + "(" + rs2.getString(1)
                                    + "," + rs2.getString(2)
                                    + "," + rs2.getString(3)
                                    + ",'" + rs2.getString(4)
                                    + "')";
                        }
                        sql = ""
                                + " DELETE "
                                + " FROM "
                                + "   project_records "
                                + " WHERE \n"
                                + "    record_id = " + rs.getString(1) + " AND "
                                + "    project_id = " + projectsMB.getCurrentProjectId();
                        connectionJdbcMB.non_query(sql);
                    }
                    sqlUndo = sqlUndo + "; \n";
                }
                filterDescription = "De la columna: (" + filter_field + ") Se filtraron los valores: (" + filterDescription + ")";
                filterName = "FILTRAR REGISTROS";
                insertingFilterInHistory(filterName, filterDescription, sqlUndo);

                reset();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se han filtrado los valores seleccionados"));
            } catch (Exception e) {
                System.out.println("Error 13 en " + this.getClass().getName() + ":" + e.toString());
            }
        }
    }

    //-------------------------------------------------------------
    //---------------------- RENAME -------------------------------
    //-------------------------------------------------------------
    /**
     * This method is responsible to Returns the values of a field sorted by
     * frequency
     *
     * @param field
     * @return
     */
    public List<ValueNewValue> getValuesOrderedByFrecuency(String field) {
        /*
         * Retorna los valores de un campo ordenados por su frecuencia
         */
        try {
            List<ValueNewValue> values = new ArrayList<>();
            String query = ""
                    + " SELECT "
                    + "   data_value, count(*) "
                    + " FROM "
                    + "   project_records "
                    + " WHERE \n"
                    + "    column_id IN "
                    + "    ( "
                    + "      SELECT \n"
                    + "         column_id \n"
                    + "      FROM \n"
                    + "         project_columns \n"
                    + "      WHERE \n"
                    + "         project_columns.project_id = " + projectsMB.getCurrentProjectId() + " AND "
                    + "         project_columns.column_name LIKE '" + field + "' "
                    + "    ) AND "
                    + "    project_id = " + projectsMB.getCurrentProjectId() + ""
                    + " GROUP BY 1 ORDER BY 2 DESC"
                    + "";
            ResultSet rows = connectionJdbcMB.consult(query);
            while (rows.next()) {
                values.add(new ValueNewValue(rows.getString(1), ""));
            }
            return values;
        } catch (SQLException ex) {
            System.out.println("Error 14 en " + this.getClass().getName() + ":" + ex.toString());
            return null;
        }
    }

    /**
     * This method allows the user to get the value that is to rename of
     * selected column.
     */
    public void changeFieldRename() {
        rename_model = getValuesOrderedByFrecuency(the_field);
        rename_field_names = new ArrayList<>();
        rename_field_names.add(the_field);
        rename_field_names.add("# de Registros");
    }

    /**
     * This method allows to rename all values that were changed corresponding
     * to a selected variable.
     */
    public void renameRecordsClick() {
        boolean continueProcess = true;
        sqlUndo = "";
        if (continueProcess) {
            if (the_field == null || the_field.length() == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar una variable"));
                continueProcess = false;
            }
        }
        if (continueProcess) {
            try {
                for (ValueNewValue values : rename_model) {//recorro cada una de las filas de la tabla
                    if (values.getNewValue() != null && values.getNewValue().trim().length() != 0) {
                        ResultSet rs = connectionJdbcMB.consult(""
                                + "      SELECT \n"
                                + "         column_id \n"
                                + "      FROM \n"
                                + "         project_columns \n"
                                + "      WHERE \n"
                                + "         project_columns.project_id = " + projectsMB.getCurrentProjectId() + " AND "
                                + "         project_columns.column_name LIKE '" + the_field + "' ");
                        String columnId = "";
                        if (rs.next()) {
                            columnId = rs.getString(1);
                        }

                        if (filterDescription.length() == 0) {
                            filterDescription = values.getNewValue() + "->" + values.getOldValue();
                        } else {
                            filterDescription = filterDescription + ", " + values.getNewValue() + "->" + values.getOldValue();
                        }

                        sqlUndo = sqlUndo + ""
                                + " UPDATE "
                                + "    project_records "
                                + " SET "
                                + "    data_value = '" + values.getOldValue() + "'"
                                + " WHERE \n"
                                + "    column_id = " + columnId + " AND "
                                + "    project_id = " + projectsMB.getCurrentProjectId() + " AND "
                                + "    data_value LIKE '" + values.getNewValue() + "' ;";
                        sql = ""
                                + " UPDATE "
                                + "    project_records "
                                + " SET "
                                + "    data_value = '" + values.getNewValue() + "'"
                                + " WHERE \n"
                                + "    column_id = " + columnId + " AND "
                                + "    project_id = " + projectsMB.getCurrentProjectId() + " AND "
                                + "    data_value LIKE '" + values.getOldValue() + "'";
                        connectionJdbcMB.non_query(sql);
                    }
                }
                filterDescription = "De la columna: (" + the_field + ") Se renombro: (" + filterDescription + ")";
                filterName = "FILTRAR REGISTROS";
                insertingFilterInHistory(filterName, filterDescription, sqlUndo);
                reset();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se han renombrado los valores indicados"));
            } catch (Exception e) {
                System.out.println("Error 15 en " + this.getClass().getName() + ":" + e.toString());
            }
        }
    }

    //--------------------------------------------------------------------------
    //---------------------- REPLICATE -------------------------------
    //--------------------------------------------------------------------------
    /**
     * This method is responsible to perform the necessary operations to a
     * variable that is loaded into an array to be replicated correctly.
     *
     * @param arrayInJava
     * @param splitOperations
     * @return
     */
    private ArrayList<String> realizeOperations(Object[] arrayInJava, String[] splitOperations) {
        /*
         * arrayInJava      arreglo con todos los campos de un registro
         * splitOperations  son los cambios que se realizan sobre este registro
         */
        ArrayList<String> arrayReturn = new ArrayList<>();
        //= arrayInJava;
        for (int i = 0; i < arrayInJava.length; i++) {
            arrayReturn.add(arrayInJava[i].toString());
        }
        boolean haveInserts = false;
        String source;
        String target;
        String newValue;
        String id_target;
        boolean foundFieldTarget;//si se encuentra el campo destino se modifica, de lo contrario se agrega un nuevo campo en arrayReturn
        for (int i = 0; i < splitOperations.length; i++) {
            foundFieldTarget = false;
            source = splitOperations[i].split("->")[0];// == source    : nombre origen
            target = splitOperations[i].split("->")[1];// == target    : nombre destino                            
            id_target = splitOperations[i].split("->")[2];// == id_target : identificador destino
            newValue = searchValueAcoordinColumn(arrayReturn, source);
            if (newValue != null && newValue.length() != 0) {
                haveInserts = true;
                //se busca el campo para realizar modificacion
                for (int j = 0; j < arrayReturn.size(); j++) {//Ciclo para cada uno de los campos del registro
                    String[] splitElement = arrayReturn.get(j).toString().split("<=>"); //splitElement[0]=columnId splitElement[1]=columnName splitElement[2]=datavalue
                    if (splitElement[1].compareTo(target) == 0) {
                        arrayReturn.set(j, splitElement[0] + "<=>" + splitElement[1] + "<=>" + newValue);//se modifica el valor del campo
                        foundFieldTarget = true;
                    }
                }
                //si no se encontro el campo se debe a gregar este campo al arreglo
                if (!foundFieldTarget) {
                    arrayReturn.add(id_target + "<=>" + target + "<=>" + newValue);
                }
            }
        }
        if (haveInserts) {//se debe ingresar nuevo registro por que si hay valores en las columnas origen
            return arrayReturn;
        } else {
            return null;
        }
    }

    /**
     * This method is responsable to obtain all records corresponding to a
     * variable, this method is called when a variables replication is
     * performed.
     *
     * @param arrayInJava
     * @param columnName
     * @return
     */
    private String searchValueAcoordinColumn(ArrayList<String> arrayInJava, String columnName) {
        for (int i = 0; i < arrayInJava.size(); i++) {//Ciclo para cada uno de los registros
            String[] splitElement = arrayInJava.get(i).toString().split("<=>"); //splitElement[0]=columnId splitElement[1]=columnName splitElement[2]=datavalue
            if (splitElement[1].compareTo(columnName) == 0) {
                return splitElement[2];
            }
        }
        return null;
    }

    /**
     * This method is responsible to replicate the selected variables for the
     * user.
     */
    public void replicateClick() {
        boolean continueProcess = true;
        String fieldsSource = "";
        String fieldsTarget = "";
        sqlUndo = "";
        if (continueProcess) {
            if (replicate_source == null || replicate_source.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se ha seleccionado las variables a replicar"));
                continueProcess = false;
            }
        }
        if (continueProcess) {
            if (replicate_target == null || replicate_target.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se ha seleccionado las variables correspondientes"));
                continueProcess = false;
            }
        }
        if (continueProcess) {
            if (replicate_source.size() % replicate_target.size() != 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                        FacesMessage.SEVERITY_ERROR, "Error",
                        "El número de variables correspondientes debería ser factor del "
                        + "número de variables a replicar."));
                continueProcess = false;
            }
        }
        if (continueProcess) {

            //cadena con las variables origen a replicar
            for (int r = 0; r < replicate_source.size(); r++) {
                if (r == 0) {
                    fieldsSource = replicate_source.get(r);
                } else {
                    fieldsSource = fieldsSource + ", " + replicate_source.get(r);
                }
            }
            //cadena con las variables destino a replicar
            for (int r = 0; r < replicate_target.size(); r++) {
                if (r == 0) {
                    fieldsTarget = replicate_target.get(r);
                } else {
                    fieldsTarget = fieldsTarget + ", " + replicate_target.get(r);
                }
            }

            try {
                int maxRecordId;
                sb = new StringBuilder();
                currentNumberInserts = 0;
                ArrayList operationsTmp = new ArrayList<>();
                int posTarget = 0;
                ResultSet rs;
                ResultSet rs2;
                //--------------------------------------------------------------------
                //creo un arreglo con las operaciones que se realizaran por registro
                //--------------------------------------------------------------------

                for (int r = 0; r < replicate_source.size(); r++) {

                    //determino el id de la columna destino
                    rs = connectionJdbcMB.consult(""
                            + " SELECT "
                            + "    column_id "
                            + " FROM "
                            + "    project_columns "
                            + " WHERE "
                            + "    column_name LIKE '" + replicate_target.get(posTarget) + "'");
                    if (rs.next()) {
                        operationsTmp.add(replicate_source.get(r) + "->" + replicate_target.get(posTarget) + "->" + rs.getString(1));//columnaOrigen->columnaDestino->idColumnDestino
                        posTarget++;
                        if (posTarget == replicate_target.size()) {
                            posTarget = 0;
                        }
                    }
                }
                //--------------------------------------------------------------------            
                // operations.size() = # nuevos registros
                // operations.get = "cod_pre->semana->id_semana;cod_pre->nombres->id_nombres" = cambios del nuevo registro
                //--------------------------------------------------------------------
                posTarget = 0;
                ArrayList operations = new ArrayList<>();
                String op = "";
                for (int i = 0; i < operationsTmp.size(); i++) {
                    if (op.length() == 0) {
                        op = operationsTmp.get(i).toString();
                    } else {
                        op = op + ";" + operationsTmp.get(i).toString();//agrupo las operaciones para un registro
                    }
                    posTarget++;
                    if (posTarget == replicate_target.size()) {
                        operations.add(op);
                        posTarget = 0;
                        op = "";
                    }
                }

                for (int i = 0; i < operations.size(); i++) {
                    System.out.println(operations.get(i).toString());
                }

                rs = connectionJdbcMB.consult(""
                        + " SELECT \n"
                        + "    MAX(record_id) \n"
                        + " FROM \n"
                        + "    project_records \n"
                        + " WHERE \n"
                        + "    project_id = " + projectsMB.getCurrentProjectId());
                rs.next();
                maxRecordId = rs.getInt(1);

                //realizo la consulta que me determina todos los registros
                rs = connectionJdbcMB.consult(""
                        + " SELECT "
                        + "    project_records.project_id, "
                        + "    project_records.record_id, "
                        + "    array_agg(project_columns.column_id|| '<=>' ||project_columns.column_name || '<=>' || project_records.data_value) "
                        + " FROM "
                        + "    project_records,project_columns "
                        + " WHERE "
                        + "    project_records.project_id = " + projectsMB.getCurrentProjectId() + " AND "
                        + "    project_columns.column_id = project_records.column_id  "
                        + " GROUP BY "
                        + "    project_records.project_id, "
                        + "    project_records.record_id ");

                ArrayList<String> arrayResult;
                String[] splitElement;
                String registerOut;
                while (rs.next()) {//Ciclo para cada registro                
                    //registerOut = "";
                    Object[] arrayInJava = (Object[]) rs.getArray(3).getArray();
                    for (int i = 0; i < operations.size(); i++) {
                        arrayResult = realizeOperations(arrayInJava, operations.get(i).toString().split(";"));
                        if (arrayResult != null) {//insertar nuevo registro                        
                            maxRecordId++;
                            if (sqlUndo.length() == 0) {
                                sqlUndo = String.valueOf(maxRecordId);
                            } else {
                                sqlUndo = sqlUndo + "," + String.valueOf(maxRecordId);
                            }
                            for (int x = 0; x < arrayResult.size(); x++) {
                                splitElement = arrayResult.get(x).toString().split("<=>"); //splitElement[0]=columnId splitElement[1]=columnName splitElement[2]=datavalue
                                //if (splitElement[2] != null && splitElement[2].length() != 0) {
                                addTableProjectRecords(projectsMB.getCurrentProjectId(), maxRecordId, Integer.parseInt(splitElement[0]), splitElement[2]);
                                currentNumberInserts++;
                                //}
                            }
                        }
                    }
                }
                addTableProjectRecords(-1, -1, -1, "");//terminar de guardar los registros restantes                
                if (sqlUndo.length() != 0) {//en sqlUndo quedan los identificadores a eliminar
                    sqlUndo = " DELETE FROM project_records WHERE record_id IN (" + sqlUndo + ") AND project_id = " + String.valueOf(projectsMB.getCurrentProjectId()) + ";\n ";
                }
                String columnId;
                for (int i = 0; i < replicate_source.size(); i++) {//elimino los de la columna source
                    rs = connectionJdbcMB.consult(""
                            + "    SELECT "
                            + "       column_id "
                            + "    FROM "
                            + "       project_columns "
                            + "    WHERE "
                            + "       column_name LIKE '" + replicate_source.get(i) + "' AND"
                            + "       project_id = " + projectsMB.getCurrentProjectId() + " ");
                    if (rs.next()) {
                        columnId = rs.getString(1);
                        rs2 = connectionJdbcMB.consult(""//genero sql para revertir
                                + " SELECT "
                                + "    * "
                                + " FROM "
                                + "    project_records "
                                + " WHERE "
                                + "   column_id = " + columnId + " AND "
                                + "   project_id = " + projectsMB.getCurrentProjectId());
                        String values = "";
                        boolean first = true;
                        while (rs2.next()) {
                            if (first) {
                                first = false;
                            } else {
                                values = values + ",";
                            }
                            values = values
                                    + "(" + rs2.getString(1)
                                    + "," + rs2.getString(2)
                                    + "," + rs2.getString(3)
                                    + ",'" + rs2.getString(4)
                                    + "')";
                        }
                        if (values.length() != 0) {
                            sqlUndo = "INSERT INTO project_records VALUES " + values + "; \n" + sqlUndo;
                        }//fin sql revertir
                        connectionJdbcMB.remove("project_records", " column_id = " + columnId + " AND  project_id = " + projectsMB.getCurrentProjectId());
                    }
                }

                filterDescription = "Se replico: (" + fieldsSource + ") en: (" + fieldsTarget + ")";
                filterName = "REPLICAR REGISTROS";
                insertingFilterInHistory(filterName, filterDescription, sqlUndo);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Replicación realizada correctamente"));
                reset();

            } catch (SQLException | NumberFormatException e) {
                System.out.println("Error 16 en " + this.getClass().getName() + ":" + e.toString());
            }
        }

    }

    //--------------------------------------------------------------------------
    //---------------------- FILTER HISTORY ------------------------------------
    //--------------------------------------------------------------------------
    /**
     * This method is responsible to insert the name of a filter along with its
     * description in the history of filters for the user can look all the
     * filters that have been realized and if necessary to undo.
     *
     * @param filterName_:Filter Name
     * @param filterDescription_:Filter Description
     * @param sqlUndo_ :Sql that undo the filter
     */
    private void insertingFilterInHistory(String filterName_, String filterDescription_, String sqlUndo_) {
        try {
            sqlUndo_ = sqlUndo_.replaceAll("'", "\"");
            //determino maximo de historial 
            ResultSet rs = connectionJdbcMB.consult(""
                    + " SELECT \n"
                    + "    MAX(history_id) \n"
                    + " FROM \n"
                    + "    project_history_filters \n"
                    + " WHERE \n"
                    + "    project_id = " + projectsMB.getCurrentProjectId());
            rs.next();
            int maxhistoryId = rs.getInt(1);
            maxhistoryId++;
            sql = ""
                    + " INSERT INTO project_history_filters VALUES ("
                    + String.valueOf(projectsMB.getCurrentProjectId()) + ","
                    + String.valueOf(maxhistoryId) + ",'"
                    + filterName_ + "','"
                    + filterDescription_ + "','"
                    + sqlUndo_ + "')";
            connectionJdbcMB.non_query(sql);
            refreshHistoryList();

        } catch (Exception e) {
            System.out.println("Error 17 en " + this.getClass().getName() + ":" + e.toString());
        }
        filterName = "";
        filterDescription = "";
        sqlUndo = "";
    }

    /**
     * This method is responsible to update the history of realized filters,
     * this is done every time a new filter is recorded in the history.
     */
    private void refreshHistoryList() {
        filtersAppliedList = new ArrayList<>();
        //selectedFiltersAppliedRow = null;
        try {
            ResultSet rs = connectionJdbcMB.consult(""
                    + " SELECT "
                    + "    * "
                    + " FROM "
                    + "    project_history_filters "
                    + " WHERE "
                    + "    project_id = " + projectsMB.getCurrentProjectId() + ""
                    + " ORDER BY "
                    + "    history_id DESC");
            while (rs.next()) {//Ciclo para cada registro                
                filtersAppliedList.add(new RowDataTable(
                        rs.getString(1), //       column1 ==> identificador del proyecto
                        rs.getString(2), //       column2 ==> identificador del historial
                        rs.getString(3), //       column3 ==> nombre del filtro
                        rs.getString(4), //       column4 ==> descripcion
                        rs.getString(5))); //       column6 ==> sql para deshacer
            }
        } catch (Exception e) {
            System.out.println("Error 18 en " + this.getClass().getName() + ":" + e.toString());
        }
    }

    //--------------------------------------------------------------------------
    //---------------------- GET Y SET VARIABLES -------------------------------
    //--------------------------------------------------------------------------
    public ProjectsMB getProjectsMB() {
        return projectsMB;
    }

    public void setProjectsMB(ProjectsMB projectsMB) {
        this.projectsMB = projectsMB;
    }

    public String getVariableNameToCopy() {
        return variableNameToCopy;
    }

    public void setVariableNameToCopy(String variableNameToCopy) {
        this.variableNameToCopy = variableNameToCopy;
    }

    public List<String> getVariablesFoundToCopy() {
        return variablesFoundToCopy;
    }

    public void setVariablesFoundToCopy(List<String> variablesFoundToCopy) {
        this.variablesFoundToCopy = variablesFoundToCopy;
    }

    public List<String> getValuesFoundToCopy() {
        return valuesFoundToCopy;
    }

    public void setValuesFoundToCopy(List<String> valuesFoundToCopy) {
        this.valuesFoundToCopy = valuesFoundToCopy;
    }

    public int getNumberOfCopies() {
        return numberOfCopies;
    }

    public void setNumberOfCopies(int numberOfCopies) {
        this.numberOfCopies = numberOfCopies;
    }

    public String getCopyPrefix() {
        return copyPrefix;
    }

    public void setCopyPrefix(String copyPrefix) {
        this.copyPrefix = copyPrefix;
    }

    public String getVariableNameToCopyFilter() {
        return variableNameToCopyFilter;
    }

    public void setVariableNameToCopyFilter(String variableNameToCopyFilter) {
        this.variableNameToCopyFilter = variableNameToCopyFilter;
    }

    public DualListModel<String> getVariablesPickToDelete() {
        return variablesPickToDelete;
    }

    public void setVariablesPickToDelete(DualListModel<String> variablesPickToDelete) {
        this.variablesPickToDelete = variablesPickToDelete;
    }

    public String getVariableNameToSplit() {
        return variableNameToSplit;
    }

    public void setVariableNameToSplit(String variableNameToSplit) {
        this.variableNameToSplit = variableNameToSplit;
    }

    public List<String> getVariablesFoundToSplit() {
        return variablesFoundToSplit;
    }

    public void setVariablesFoundToSplit(List<String> variablesFoundToSplit) {
        this.variablesFoundToSplit = variablesFoundToSplit;
    }

    public List<String> getValuesFoundToSplit() {
        return valuesFoundToSplit;
    }

    public void setValuesFoundToSplit(List<String> valuesFoundToSplit) {
        this.valuesFoundToSplit = valuesFoundToSplit;
    }

    public String getSplitFieldName1() {
        return splitFieldName1;
    }

    public void setSplitFieldName1(String splitFieldName1) {
        this.splitFieldName1 = splitFieldName1;
    }

    public String getSplitFieldName2() {
        return splitFieldName2;
    }

    public void setSplitFieldName2(String splitFieldName2) {
        this.splitFieldName2 = splitFieldName2;
    }

    public String getSplitDelimiter() {
        return splitDelimiter;
    }

    public void setSplitDelimiter(String splitDelimiter) {
        this.splitDelimiter = splitDelimiter;
    }

    public boolean isSplitRendered() {
        return splitRendered;
    }

    public void setSplitRendered(boolean splitRendered) {
        this.splitRendered = splitRendered;
    }

    public boolean isSplitInclude() {
        return splitInclude;
    }

    public void setSplitInclude(boolean splitInclude) {
        this.splitInclude = splitInclude;
    }

    public DualListModel<String> getVariablesPickToMerge() {
        return variablesPickToMerge;
    }

    public void setVariablesPickToMerge(DualListModel<String> variablesPickToMerge) {
        this.variablesPickToMerge = variablesPickToMerge;
    }

    public String getVariableNameToMerge() {
        return variableNameToMerge;
    }

    public void setVariableNameToMerge(String variableNameToMerge) {
        this.variableNameToMerge = variableNameToMerge;
    }

    public String getMergeDelimiter() {
        return mergeDelimiter;
    }

    public void setMergeDelimiter(String mergeDelimiter) {
        this.mergeDelimiter = mergeDelimiter;
    }

    public QueryDataModel getFilter_queryModel() {
        return filter_queryModel;
    }

    public void setFilter_queryModel(QueryDataModel filter_queryModel) {
        this.filter_queryModel = filter_queryModel;
    }

    public List<String> getFilter_headers() {
        return filter_headers;
    }

    public void setFilter_headers(List<String> filter_headers) {
        this.filter_headers = filter_headers;
    }

    public List<String> getFilter_field_names() {
        return filter_field_names;
    }

    public void setFilter_field_names(List<String> filter_field_names) {
        this.filter_field_names = filter_field_names;
    }

    public FieldCount[] getFilter_selected() {
        return filter_selected;
    }

    public void setFilter_selected(FieldCount[] filter_selected) {
        this.filter_selected = filter_selected;
    }

    public int getRedoFilter() {
        return redoFilter;
    }

    public void setRedoFilter(int redoFilter) {
        this.redoFilter = redoFilter;
    }

    public String getFilter_field() {
        return filter_field;
    }

    public void setFilter_field(String filter_field) {
        this.filter_field = filter_field;
    }

    public List<String> getVariablesFoundToFilterRecords() {
        return variablesFoundToFilterRecords;
    }

    public void setVariablesFoundToFilterRecords(List<String> variablesFoundToFilterRecords) {
        this.variablesFoundToFilterRecords = variablesFoundToFilterRecords;
    }

    public List<ValueNewValue> getRename_model() {
        return rename_model;
    }

    public void setRename_model(List<ValueNewValue> rename_model) {
        this.rename_model = rename_model;
    }

    public List<String> getRename_headers() {
        return rename_headers;
    }

    public void setRename_headers(List<String> rename_headers) {
        this.rename_headers = rename_headers;
    }

    public List<String> getRename_field_names() {
        return rename_field_names;
    }

    public void setRename_field_names(List<String> rename_field_names) {
        this.rename_field_names = rename_field_names;
    }

    public int getRedoRename() {
        return redoRename;
    }

    public void setRedoRename(int redoRename) {
        this.redoRename = redoRename;
    }

    public String getThe_field() {
        return the_field;
    }

    public void setThe_field(String the_field) {
        this.the_field = the_field;
    }

    public List<String> getVariablesFoundToRename() {
        return variablesFoundToRename;
    }

    public void setVariablesFoundToRename(List<String> variablesFoundToRename) {
        this.variablesFoundToRename = variablesFoundToRename;
    }

    public List<String> getReplicate_source() {
        return replicate_source;
    }

    public void setReplicate_source(List<String> replicate_source) {
        this.replicate_source = replicate_source;
    }

    public List<String> getReplicate_target() {
        return replicate_target;
    }

    public void setReplicate_target(List<String> replicate_target) {
        this.replicate_target = replicate_target;
    }

    public List<String> getReplicateFields() {
        return replicateFields;
    }

    public void setReplicateFields(List<String> replicateFields) {
        this.replicateFields = replicateFields;
    }

    public LazyDataModel<List> getReplicate_model2() {
        return replicate_model2;
    }

    public void setReplicate_model2(LazyDataModel<List> replicate_model2) {
        this.replicate_model2 = replicate_model2;
    }

    public List<String> getReplicate_columns2() {
        return replicate_columns2;
    }

    public void setReplicate_columns2(List<String> replicate_columns2) {
        this.replicate_columns2 = replicate_columns2;
    }

    public List<RowDataTable> getFiltersAppliedList() {
        return filtersAppliedList;
    }

    public void setFiltersAppliedList(List<RowDataTable> filtersAppliedList) {
        this.filtersAppliedList = filtersAppliedList;
    }
}
