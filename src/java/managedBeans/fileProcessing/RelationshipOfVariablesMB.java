/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.fileProcessing;

import beans.connection.ConnectionJdbcMB;
import beans.enumerators.DataTypeEnum;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import model.dao.FieldsFacade;
import model.dao.ProjectsFacade;
import model.dao.RelationGroupFacade;
import model.dao.RelationVariablesFacade;
import model.pojo.Projects;
import model.pojo.RelationVariables;

/**
 * The class RelationshipOfVariablesMB allows perform the relationship of
 * variable that done reference a coming from the correspondence between a
 * variable that form part of a type of injury with a variable (column) found in
 * the records
 *
 * @author santos
 */
@ManagedBean(name = "relationshipOfVariablesMB")
@SessionScoped
public class RelationshipOfVariablesMB implements Serializable {

    private boolean btnSaveConfigurationDisabled = true;
    private boolean btnLoadConfigurationDisabled = false;
    private boolean btnJoinColumnsDisabled;
    private boolean btnDivideColumnsDisabled;
    private boolean selectDateFormatDisabled = true;
    private boolean compareForCode = false;
    private List<String> variablesExpected;
    private List<String> varsFound;
    private List<String> valuesExpected;
    private List<String> valuesDiscarded;
    private List<String> valuesFound;
    private List<String> relatedVars;
    private String currentRelationGroupName = "";
    private String currentDateFormat = "dd/MM/yyyy";//tipo de formato de fecha actual
    private List<String> currentVariableExpected = new ArrayList<>();
    private List<String> currentVariableFound = new ArrayList<>();
    private List<String> currentRelatedVariables = new ArrayList<>();
    private String typeVarExepted;
    private String possibleVariableFound = "";//posible variable encontrada para un variable esperada
    private ProjectsMB projectsMB;
    private RelationshipOfValuesMB relationshipOfValuesMB;
    private String filterConsult = "";
    private String expectedVariablesFilter = "";
    private String foundVariablesFilter = "";
    private String relatedVariablesFilter = "";
    String fieldType = "";
    ConnectionJdbcMB connectionJdbcMB;
    String sql = "";
    @EJB
    FieldsFacade fieldsFacade;
    @EJB
    RelationVariablesFacade relationVariablesFacade;
    @EJB
    RelationGroupFacade relationGroupFacade;
    @EJB
    ProjectsFacade projectsFacade;

    /**
     * first function executed after the constructor that initializes variables
     * and establishes the connection to the database
     */
    @PostConstruct
    private void initialize() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES DE PROPOSITO GENERAL ---------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * class constructor and responsible of the connect to the database.
     */
    public RelationshipOfVariablesMB() {
        /*
         * Constructor de la clase
         */
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
    }

    /**
     * it is responsible for refresh list of variable expected and variables
     * found .
     */
    public void refresh() {
        loadVarsExpectedAndFound();//recargo listas de variables esperadas y encontradas                       
        changeVarExpected();
        changeVarFound();
    }

    /**
     * performs the conversions necessary to pass a file to SIVIGILA .
     */
    public void convertAllIdSivigila() {
        /*
         * realiza las conversiones necesarias para pasar de archivo a sivigila
         */
        if (projectsMB.getCurrentFormName().compareTo("SIVIGILA-VIF") == 0) {//ES FORMULARIO SIVIGILA-VIF
            try {
                ResultSet rs;
                sql = ""
                        + " SELECT \n"
                        + "    relation_variables.name_expected, \n"
                        + "    relation_variables.name_found \n"
                        + " FROM \n"
                        + "    public.relation_group, \n"
                        + "    public.relation_variables \n"
                        + " WHERE \n"
                        + "    relation_variables.id_relation_group = relation_group.id_relation_group AND \n"
                        + "    relation_group.name_relation_group LIKE '" + projectsMB.getCurrentRelationsGroupName() + "' \n";
                rs = connectionJdbcMB.consult(sql);
                while (rs.next()) {
                    convertIdToNameSIVIGILA(rs.getString(1), rs.getString(2), projectsFacade.find(projectsMB.getCurrentProjectId()));
                }
            } catch (Exception e) {
                System.out.println("Error 13 en " + this.getClass().getName() + ":" + e.toString());
            }
        }
    }

    /**
     * restores the initial values of the variables.
     */
    public void reset() {//@PostConstruct ejecutar despues de el constructor
        this.relatedVars = new ArrayList<>();
        this.valuesExpected = new ArrayList<>();
        this.valuesFound = new ArrayList<>();
        this.varsFound = new ArrayList<>();
        this.variablesExpected = new ArrayList<>();
        this.currentVariableFound = new ArrayList<>();
        this.currentVariableExpected = new ArrayList<>();
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES QUE CARGAN VALORES -----------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * This method allows to load the expected variable and this in turn is used
     * by other methods.
     */
    private void loadExpectedVariables() {
        try {
            possibleVariableFound = "";
            filterConsult = "";
            if (expectedVariablesFilter != null && expectedVariablesFilter.trim().length() != 0) {
                filterConsult = "fields.field_name ILIKE '%" + expectedVariablesFilter + "%' AND \n";
            }
            ResultSet rs;//vaiables esperadas-----------------------------------
            sql = ""
                    + " SELECT \n"
                    + "    fields.field_name \n"
                    + " FROM \n"
                    + "    public.fields \n"
                    + " WHERE \n"
                    + "    fields.form_id LIKE '" + projectsMB.getCurrentFormId() + "' AND \n"
                    + "    " + filterConsult
                    + "    fields.field_name NOT IN \n"
                    + "    (SELECT \n"
                    + "        relation_variables.name_expected \n"
                    + "     FROM \n"
                    + "        public.relation_group, \n"
                    + "        public.relation_variables \n"
                    + "     WHERE \n"
                    + "        relation_variables.id_relation_group = relation_group.id_relation_group AND \n"
                    + "        relation_group.name_relation_group LIKE '" + projectsMB.getCurrentRelationsGroupName() + "' \n"
                    + "     )\n"
                    + " ORDER BY \n"
                    + "    fields.field_order;\n";//System.out.println("A001\n" + sql);
            variablesExpected = new ArrayList<>();
            rs = connectionJdbcMB.consult(sql);
            while (rs.next()) {
                variablesExpected.add(rs.getString(1));
            }
        } catch (Exception e) {
            System.out.println("Error 1 " + this.getClass().getName() + ":" + e.toString());
        }
    }

    /**
     * creates a list of variables from a file not repeated variables.
     */
    private void loadFoundVariables() {
        try {
            ResultSet rs;
            varsFound = new ArrayList<>();//vaiables encontradas----------            
            filterConsult = "";
            if (foundVariablesFilter != null && foundVariablesFilter.trim().length() != 0) {
                filterConsult = "project_columns.column_name ILIKE '%" + foundVariablesFilter + "%' AND \n";
            }
            sql = ""
                    + " SELECT \n"
                    + "	   project_columns.column_name \n"
                    + " FROM \n"
                    + "	   public.project_columns \n"
                    + " WHERE \n" + filterConsult
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
                varsFound.add(rs.getString(1));
            }
        } catch (Exception e) {
            System.out.println("Error 2 en " + this.getClass().getName() + ":" + e.toString());
        }
    }

    /**
     * creates a list of related variables.
     */
    private void loadRelatedVariables() {
        try {
            ResultSet rs;
            relatedVars = new ArrayList<>();//variables relacionadas------
            filterConsult = "";
            if (relatedVariablesFilter != null && relatedVariablesFilter.trim().length() != 0) {
                filterConsult = " AND \n    (relation_variables.name_expected ILIKE '%" + relatedVariablesFilter + "%' OR \n";
                filterConsult = filterConsult + "    relation_variables.name_found ILIKE '%" + relatedVariablesFilter + "%' )";
            }
            sql = ""
                    + " SELECT \n"
                    + "    relation_variables.name_expected, \n"
                    + "    relation_variables.name_found \n"
                    + " FROM \n"
                    + "    public.relation_group, \n"
                    + "    public.relation_variables \n"
                    + " WHERE \n"
                    + "    relation_variables.id_relation_group = relation_group.id_relation_group AND \n"
                    + "    relation_group.name_relation_group LIKE '" + projectsMB.getCurrentRelationsGroupName() + "' \n"
                    + filterConsult;
            rs = connectionJdbcMB.consult(sql);
            while (rs.next()) {
                relatedVars.add(rs.getString(1) + "->" + rs.getString(2));
            }
            relationshipOfValuesMB.setCategoricalRelationsFilter("");
            relationshipOfValuesMB.loadCategoricalRelatedVariables();
        } catch (Exception e) {
            System.out.println("Error 3 en " + this.getClass().getName() + ":" + e.toString());
        }
    }

    /**
     * It is responsible for loading the lists of variables found and expected.
     */
    public void loadVarsExpectedAndFound() {
        /*
         * cargar las listas de variables encontradas y esperadas
         */
        loadExpectedVariables();
        loadFoundVariables();
        loadRelatedVariables();
        valuesExpected = new ArrayList<>();
        valuesFound = new ArrayList<>();
    }

    /**
     * allows to obtain or determine the type of variable expected.
     *
     * @return
     */
    private String getTypeVariableExpected() {
        String strReturn = "";
        try {
            //determino el ty            
            ResultSet rs = connectionJdbcMB.consult(""
                    + " SELECT \n"
                    + "    fields.field_type \n"
                    + " FROM \n"
                    + "    public.fields \n"
                    + " WHERE "
                    + "    fields.form_id LIKE '" + projectsMB.getCurrentFormId() + "' AND \n"
                    + "    fields.field_name LIKE '" + currentVariableExpected.get(0) + "' \n");
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException ex) {
            System.out.println("Error 4 en " + this.getClass().getName() + ":" + ex.toString());
        }
        return strReturn;
    }

    /**
     * This method allows search the possible variables found for to be related.
     *
     * @return
     */
    private String findPossibleVariableFound() {
        //buscar la posible variable encontrada para ser relacionada
        String strReturn = "";
        try {
            //determino el ty            
            ResultSet rs = connectionJdbcMB.consult(""
                    + " SELECT \n"
                    + "    fields.field_name_small \n"
                    + " FROM \n"
                    + "    public.fields \n"
                    + " WHERE "
                    + "    fields.form_id LIKE '" + projectsMB.getCurrentFormId() + "' AND \n"
                    + "    fields.field_name LIKE '" + currentVariableExpected.get(0) + "' \n");
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException ex) {
            System.out.println("Error 4 en " + this.getClass().getName() + ":" + ex.toString());
        }
        return strReturn;
    }

    /**
     * allows obtain a description of the expected variables.
     *
     * @return
     */
    private String getDescriptionVariableExpected() {
        String strReturn = "";
        try {
            //determino el ty            
            ResultSet rs = connectionJdbcMB.consult(""
                    + " SELECT \n"
                    + "    fields.field_description \n"
                    + " FROM \n"
                    + "    public.fields, \n"
                    + "    public.relation_group \n"
                    + " WHERE \n"
                    + "    relation_group.form_id = fields.form_id AND \n"
                    + "    relation_group.id_relation_group = " + projectsMB.getCurrentRelationsGroupId() + " AND \n"
                    + "    fields.form_id LIKE '" + projectsMB.getCurrentFormId() + "' AND \n"
                    + "    fields.field_name LIKE '" + currentVariableExpected.get(0) + "' \n");
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException ex) {
            System.out.println("Error 5 en " + this.getClass().getName() + ":" + ex.toString());
        }
        return strReturn;
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
     * This method is responsible for loading the expected values depending on
     * the variable expected.
     */
    public void loadValuesExpected() {
        /*
         * cargar los valores esperados dependiendo la variable esperada
         */
        if (currentVariableExpected != null && !currentVariableExpected.isEmpty()) {
            typeVarExepted = getTypeVariableExpected();
            possibleVariableFound = findPossibleVariableFound();
            valuesExpected = new ArrayList<>();//borro la lista de valores esperados 
            selectDateFormatDisabled = true;
            fieldType = remove_v(typeVarExepted);
            switch (DataTypeEnum.convert(fieldType)) {//tipo de relacion
                case integer:
                    valuesExpected.add("Cualquier entero");
                    break;
                case text:
                    valuesExpected.add("Cualquier texto");
                    break;
                case date:
                    valuesExpected.add("Cualquier fecha");
                    selectDateFormatDisabled = false;
                    break;
                case age:
                    valuesExpected.add("Edad : entero o definida en meses y años");
                    break;
                case military:
                    valuesExpected.add("Hora militar");
                    break;
                case minute:
                    valuesExpected.add("Minutos representados por un entero de 1 a 59");
                    break;
                case date_of_birth:
                    valuesExpected.add("Cualquier fecha");
                    selectDateFormatDisabled = false;
                    break;
                case hour:
                    valuesExpected.add("La hora se representa por un entero de 0 a 24");
                    break;
                case day:
                    valuesExpected.add("El dia se representa por un entero de 1 a 31");
                    break;
                case month:
                    valuesExpected.add("El mes se representa por un entero de 1 a 12");
                    break;
                case year:
                    valuesExpected.add("El año es un valor entero de dos o 4 cifras");
                    break;
                case percentage:
                    valuesExpected.add("El porcentaje es un valor entero de 1 a 100");
                    break;
                case level:
                    valuesExpected.add("Nivel de alcohol en la víctima");
                    break;
                case NOVALUE://se espera un valor categorico compareForCodeDisabled = false;
                    if (compareForCode == true) {
                        valuesExpected = connectionJdbcMB.categoricalCodeList(fieldType, 20);
                    } else {
                        valuesExpected = connectionJdbcMB.categoricalNameList(fieldType, 20);
                    }
                    break;
            }
        }
    }

    /**
     * create a list of values of a detemined column from of the record with no
     * duplicate values
     *
     * @param column
     */
    public void loadValuesFound(String column) {
        /*
         * crear una lista de valores de una determinada columna proveniente del
         * archivo con valores no repetidos
         */
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

            valuesFound = new ArrayList<>();
            while (rs.next()) {
                valuesFound.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            System.out.println("Error 6 en " + this.getClass().getName() + ":" + ex.toString());
        }
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES CUANDO LISTAS CAMBIAN DE VALOR -----------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * This method is responsable changing a variable related to another.
     */
    public void changeRelatedVariables() {
        //la funcion se encargda de limpiar la seccion "RELACION DE VALORES"
        relationshipOfValuesMB.setCategoricalRelationsFilter("");
        relationshipOfValuesMB.loadCategoricalRelatedVariables();
    }

    /**
     * allows change of the expected variable to another.
     */
    public void changeVarExpected() {
        valuesExpected = new ArrayList<>();//borro la lista de valores esperados 
        if (currentVariableExpected != null && !currentVariableExpected.isEmpty()) {
            //variableDescription = getDescriptionVariableExpected();
            loadValuesExpected();
        }
    }

    /**
     * This method allows to change of a variable found to another.
     */
    public void changeVarFound() {
        valuesFound = new ArrayList<>();//borro la lista de valores esperados 
        if (currentVariableFound != null && !currentVariableFound.isEmpty()) {
            currentRelatedVariables = new ArrayList<>();
            loadValuesFound(currentVariableFound.get(0));
        }
    }
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //CLIK SOBRE BOTONOES --------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------

    /**
     * convert identifier name for the SIVIGILA table.
     *
     * @param variableExpectedSIVIGILA
     * @param variableFoundSIVIGILA
     * @param currentProject
     */
    private void convertIdToNameSIVIGILA(String variableExpectedSIVIGILA, String variableFoundSIVIGILA, Projects currentProject) {
        /*
         * convertir identificador en nombre para la tabla sivigila
         */
        String sql1 = ""
                + " UPDATE \n"
                + " project_records \n"
                + " SET data_value = '";//aqui se adiciona nombre que coresponde a un identificador segun sivigila
        String sql2 = ""
                + "' \n"
                + " WHERE \n"
                + "    data_value like '";//codigo se adiciona el identificador segun sivigila
        String sql3 = ""
                + "' AND \n"
                + "    project_id = " + currentProject.getProjectId() + " AND \n"
                + "    column_id IN ( \n"
                + "       SELECT \n"
                + "          project_columns.column_id \n"
                + "       FROM \n"
                + "          public.project_records, \n"
                + "          public.project_columns, \n"
                + "          public.projects \n"
                + "       WHERE \n"
                + "          project_columns.column_id = project_records.column_id AND \n"
                + "          projects.project_id = project_records.project_id AND \n"
                + "          project_columns.column_name LIKE '" + variableFoundSIVIGILA + "' AND \n"//nombre de la columna
                + "          projects.start_column_id >= " + currentProject.getStartColumnId() + " AND \n"//solo las columnas del proyecto start - end
                + "          projects.end_column_id <= " + currentProject.getEndColumnId() + " \n"
                + "       GROUP BY \n"
                + "          project_columns.column_id) \n";

        if (variableExpectedSIVIGILA.compareTo("relacion_familiar_victima") == 0) {
            compareForCode = false;
            connectionJdbcMB.non_query(sql1 + "ESPOSO(A)" + sql2 + "1" + sql3);
            connectionJdbcMB.non_query(sql1 + "CONYUGE / COMPAÑERO" + sql2 + "2" + sql3);
            connectionJdbcMB.non_query(sql1 + "NOVIO/A" + sql2 + "3" + sql3);
            connectionJdbcMB.non_query(sql1 + "AMANTE" + sql2 + "4" + sql3);
            connectionJdbcMB.non_query(sql1 + "EX-ESPOSO(A)" + sql2 + "5" + sql3);
            connectionJdbcMB.non_query(sql1 + "EX-NOVIO(A)" + sql2 + "6" + sql3);
            connectionJdbcMB.non_query(sql1 + "EX-AMANTE" + sql2 + "7" + sql3);
            connectionJdbcMB.non_query(sql1 + "PADRE" + sql2 + "8" + sql3);
            connectionJdbcMB.non_query(sql1 + "MADRE" + sql2 + "9" + sql3);
            connectionJdbcMB.non_query(sql1 + "HIJO" + sql2 + "10" + sql3);
            connectionJdbcMB.non_query(sql1 + "ENCARGADO(A) DEL NNA O ADULTO MAYOR" + sql2 + "11" + sql3);
            connectionJdbcMB.non_query(sql1 + "HERMANO/A" + sql2 + "12" + sql3);
            connectionJdbcMB.non_query(sql1 + "ABUELO(A)" + sql2 + "13" + sql3);
            connectionJdbcMB.non_query(sql1 + "PADRASTRO" + sql2 + "14" + sql3);
            connectionJdbcMB.non_query(sql1 + "MADRASTRA" + sql2 + "15" + sql3);
            connectionJdbcMB.non_query(sql1 + "TIO(A)" + sql2 + "16" + sql3);
            connectionJdbcMB.non_query(sql1 + "PRIMO(A)" + sql2 + "17" + sql3);
            connectionJdbcMB.non_query(sql1 + "CUÑADO(A)" + sql2 + "18" + sql3);
            connectionJdbcMB.non_query(sql1 + "SUEGRO(A)" + sql2 + "19" + sql3);
            connectionJdbcMB.non_query(sql1 + "OTROS FAMILIARES CIVILES O CONSANGUINEOS" + sql2 + "20" + sql3);
            connectionJdbcMB.non_query(sql1 + "SIN DATO" + sql2 + "21" + sql3);
            connectionJdbcMB.non_query(sql1 + "OTRO, CUAL?" + sql2 + "22" + sql3);
        }
        if (variableExpectedSIVIGILA.compareTo("pertenencia_etnica") == 0) {
            compareForCode = false;
            connectionJdbcMB.non_query(sql1 + "INDIGENAS" + sql2 + "1" + sql3);
            connectionJdbcMB.non_query(sql1 + "ROM,GITANO" + sql2 + "2" + sql3);
            connectionJdbcMB.non_query(sql1 + "RAIZAL" + sql2 + "3" + sql3);
            connectionJdbcMB.non_query(sql1 + "PALENQUERO" + sql2 + "4" + sql3);
            connectionJdbcMB.non_query(sql1 + "AFRO COLOMBIANO" + sql2 + "5" + sql3);
            connectionJdbcMB.non_query(sql1 + "OTRO" + sql2 + "6" + sql3);
            //connectionJdbcMB.non_query(sql1 + "NINGUNO"+ sql2 + "1" + sql3);
            //connectionJdbcMB.non_query(sql1 + "MESTIZO"+ sql2 + "1" + sql3);
        }
        if (variableExpectedSIVIGILA.compareTo("naturaleza_violencia") == 0) {
            compareForCode = false;
            connectionJdbcMB.non_query(sql1 + "FISICO" + sql2 + "1" + sql3);
            connectionJdbcMB.non_query(sql1 + "PSICOLOGICO / VERBAL" + sql2 + "2" + sql3);
            connectionJdbcMB.non_query(sql1 + "NEGLIGENCIA" + sql2 + "3" + sql3);
            connectionJdbcMB.non_query(sql1 + "ABUSO SEXUAL" + sql2 + "4" + sql3);
            connectionJdbcMB.non_query(sql1 + "ACOSO SEXUAL" + sql2 + "5" + sql3);
            connectionJdbcMB.non_query(sql1 + "ASALTO SEXUAL" + sql2 + "6" + sql3);
            connectionJdbcMB.non_query(sql1 + "EXPLOTACION SEXUAL" + sql2 + "7" + sql3);
            connectionJdbcMB.non_query(sql1 + "TURISMO SEXUAL" + sql2 + "8" + sql3);
            connectionJdbcMB.non_query(sql1 + "PORNOGRAFIA CON NNA" + sql2 + "9" + sql3);
            connectionJdbcMB.non_query(sql1 + "TRATA DE PERSONAL PARA EXPLOTACION SEXUAL" + sql2 + "10" + sql3);
            //connectionJdbcMB.non_query(sql1 + "VIOLENCIA SEXUAL" + sql2 + "5" + sql3);                
            //connectionJdbcMB.non_query(sql1 + "ABANDONO" + sql2 + "5" + sql3);
            //connectionJdbcMB.non_query(sql1 + "INSTITUCIONAL" + sql2 + "5" + sql3);
            //connectionJdbcMB.non_query(sql1 + "SIN DATO" + sql2 + "5" + sql3);
            //connectionJdbcMB.non_query(sql1 + "OTRO" + sql2 + "5" + sql3);
        }
        if (variableExpectedSIVIGILA.compareTo("grupo_vulnerable") == 0) {
            compareForCode = false;
            connectionJdbcMB.non_query(sql1 + "CAMPESINO" + sql2 + "98" + sql3);
            connectionJdbcMB.non_query(sql1 + "DISCAPACITADO" + sql2 + "7" + sql3);
            connectionJdbcMB.non_query(sql1 + "DESPLAZADO" + sql2 + "9" + sql3);
            connectionJdbcMB.non_query(sql1 + "MIGRANTES" + sql2 + "13" + sql3);
            connectionJdbcMB.non_query(sql1 + "CARCELARIOS" + sql2 + "14" + sql3);
            connectionJdbcMB.non_query(sql1 + "GESTANTES" + sql2 + "16" + sql3);
            //connectionJdbcMB.non_query(sql1 + "DISCAPACITADO" + sql2 + "1" + sql3);
            //connectionJdbcMB.non_query(sql1 + "DESPLAZADO" + sql2 + "2" + sql3);
            //connectionJdbcMB.non_query(sql1 + "DESMOVILIZADO" + sql2 + "3" + sql3);
            //connectionJdbcMB.non_query(sql1 + "REFUGIADO" + sql2 + "4" + sql3);
            //connectionJdbcMB.non_query(sql1 + "CARCELARIOS" + sql2 + "14" + sql3);
            //connectionJdbcMB.non_query(sql1 + "GESTANTES" + sql2 + "16" + sql3);                                
        }
        if (variableExpectedSIVIGILA.compareTo("escenario_hechos") == 0) {
            compareForCode = false;
            connectionJdbcMB.non_query(sql1 + "ESPACIO O VIA PUBLICA" + sql2 + "1" + sql3);
            connectionJdbcMB.non_query(sql1 + "CASA" + sql2 + "2" + sql3);
            connectionJdbcMB.non_query(sql1 + "ESCUELA" + sql2 + "3" + sql3);
            connectionJdbcMB.non_query(sql1 + "LUGAR DE TRABAJO" + sql2 + "4" + sql3);
            connectionJdbcMB.non_query(sql1 + "SITIO DE DIVERSION" + sql2 + "5" + sql3);
            connectionJdbcMB.non_query(sql1 + "DEPORTIVO" + sql2 + "6" + sql3);
            connectionJdbcMB.non_query(sql1 + "OTRO LUGAR" + sql2 + "7" + sql3);
            //connectionJdbcMB.non_query(sql1 + "FINCA O CAMPO" + sql2 + "1" + sql3);
            //connectionJdbcMB.non_query(sql1 + "BAR O SIMILARES" + sql2 + "1" + sql3);
            //connectionJdbcMB.non_query(sql1 + "RIO" + sql2 + "1" + sql3);
            //connectionJdbcMB.non_query(sql1 + "CARCEL" + sql2 + "1" + sql3);
            //connectionJdbcMB.non_query(sql1 + "NO SE SABE" + sql2 + "1" + sql3);
        }
    }

    /**
     * convert the name in identifier for the table SIVIGILA .
     *
     * @param variableExpectedSIVIGILA
     * @param variableFoundSIVIGILA
     * @param currentProject
     */
    private void convertNameToIdSIVIGILA(String variableExpectedSIVIGILA, String variableFoundSIVIGILA, Projects currentProject) {
        /*
         * convertir nombre en identificador para la tabla sivigila
         */
        String sql1 = ""
                + " UPDATE \n"
                + " project_records \n"
                + " SET data_value = '";//aqui se adiciona identificador segun sivigila
        String sql2 = ""
                + "' \n"
                + " WHERE \n"
                + "    data_value like '";//aqui se adiciona el nombre segun sivigila
        String sql3 = ""
                + "' AND \n"
                + "    project_id = " + currentProject.getProjectId() + " AND \n"
                + "    column_id IN ( \n"
                + "       SELECT \n"
                + "          project_columns.column_id \n"
                + "       FROM \n"
                + "          public.project_records, \n"
                + "          public.project_columns, \n"
                + "          public.projects \n"
                + "       WHERE \n"
                + "          project_columns.column_id = project_records.column_id AND \n"
                + "          projects.project_id = project_records.project_id AND \n"
                + "          project_columns.column_name LIKE '" + variableFoundSIVIGILA + "' AND \n"
                + "          projects.start_column_id >= " + currentProject.getStartColumnId() + " AND \n"
                + "          projects.end_column_id <= " + currentProject.getEndColumnId() + " \n"
                + "       GROUP BY \n"
                + "          project_columns.column_id) \n";

        if (variableExpectedSIVIGILA.compareTo("relacion_familiar_victima") == 0) {
            connectionJdbcMB.non_query(sql1 + "1" + sql2 + "ESPOSO(A)" + sql3);
            connectionJdbcMB.non_query(sql1 + "2" + sql2 + "CONYUGE / COMPAÑERO" + sql3);
            connectionJdbcMB.non_query(sql1 + "3" + sql2 + "NOVIO/A" + sql3);
            connectionJdbcMB.non_query(sql1 + "4" + sql2 + "AMANTE" + sql3);
            connectionJdbcMB.non_query(sql1 + "5" + sql2 + "EX-ESPOSO(A)" + sql3);
            connectionJdbcMB.non_query(sql1 + "6" + sql2 + "EX-NOVIO(A)" + sql3);
            connectionJdbcMB.non_query(sql1 + "7" + sql2 + "EX-AMANTE" + sql3);
            connectionJdbcMB.non_query(sql1 + "8" + sql2 + "PADRE" + sql3);
            connectionJdbcMB.non_query(sql1 + "9" + sql2 + "MADRE" + sql3);
            connectionJdbcMB.non_query(sql1 + "10" + sql2 + "HIJO" + sql3);
            connectionJdbcMB.non_query(sql1 + "11" + sql2 + "ENCARGADO(A) DEL NNA O ADULTO MAYOR" + sql3);
            connectionJdbcMB.non_query(sql1 + "12" + sql2 + "HERMANO/A" + sql3);
            connectionJdbcMB.non_query(sql1 + "13" + sql2 + "ABUELO(A)" + sql3);
            connectionJdbcMB.non_query(sql1 + "14" + sql2 + "PADRASTRO" + sql3);
            connectionJdbcMB.non_query(sql1 + "15" + sql2 + "MADRASTRA" + sql3);
            connectionJdbcMB.non_query(sql1 + "16" + sql2 + "TIO(A)" + sql3);
            connectionJdbcMB.non_query(sql1 + "17" + sql2 + "PRIMO(A)" + sql3);
            connectionJdbcMB.non_query(sql1 + "18" + sql2 + "CUÑADO(A)" + sql3);
            connectionJdbcMB.non_query(sql1 + "19" + sql2 + "SUEGRO(A)" + sql3);
            connectionJdbcMB.non_query(sql1 + "20" + sql2 + "OTROS FAMILIARES CIVILES O CONSANGUINEOS" + sql3);
            connectionJdbcMB.non_query(sql1 + "21" + sql2 + "SIN DATO" + sql3);
            connectionJdbcMB.non_query(sql1 + "22" + sql2 + "OTRO, CUAL?" + sql3);
        }
        if (variableExpectedSIVIGILA.compareTo("pertenencia_etnica") == 0) {
            connectionJdbcMB.non_query(sql1 + "1" + sql2 + "INDIGENAS" + sql3);
            connectionJdbcMB.non_query(sql1 + "2" + sql2 + "ROM,GITANO" + sql3);
            connectionJdbcMB.non_query(sql1 + "3" + sql2 + "RAIZAL" + sql3);
            connectionJdbcMB.non_query(sql1 + "4" + sql2 + "PALENQUERO" + sql3);
            connectionJdbcMB.non_query(sql1 + "5" + sql2 + "AFRO COLOMBIANO" + sql3);
            connectionJdbcMB.non_query(sql1 + "6" + sql2 + "OTRO" + sql3);
            //connectionJdbcMB.non_query(sql1 + "" + sql2 + "NINGUNO" + sql3);
            //connectionJdbcMB.non_query(sql1 + "" + sql2 + "MESTIZO" +sql3);
        }
        if (variableExpectedSIVIGILA.compareTo("naturaleza_violencia") == 0) {
            connectionJdbcMB.non_query(sql1 + "1" + sql2 + "FISICO" + sql3);
            connectionJdbcMB.non_query(sql1 + "2" + sql2 + "PSICOLOGICO / VERBAL" + sql3);
            connectionJdbcMB.non_query(sql1 + "3" + sql2 + "NEGLIGENCIA" + sql3);
            connectionJdbcMB.non_query(sql1 + "4" + sql2 + "ABUSO SEXUAL" + sql3);
            connectionJdbcMB.non_query(sql1 + "5" + sql2 + "ACOSO SEXUAL" + sql3);
            connectionJdbcMB.non_query(sql1 + "6" + sql2 + "ASALTO SEXUAL" + sql3);
            connectionJdbcMB.non_query(sql1 + "7" + sql2 + "EXPLOTACION SEXUAL" + sql3);
            connectionJdbcMB.non_query(sql1 + "8" + sql2 + "TURISMO SEXUAL" + sql3);
            connectionJdbcMB.non_query(sql1 + "9" + sql2 + "PORNOGRAFIA CON NNA" + sql3);
            connectionJdbcMB.non_query(sql1 + "10" + sql2 + "TRATA DE PERSONAL PARA EXPLOTACION SEXUAL" + sql3);
            //connectionJdbcMB.non_query(sql1 + "" + sql2 + "VIOLENCIA SEXUAL" + sql3);                
            //connectionJdbcMB.non_query(sql1 + "" + sql2 + "ABANDONO" + sql3);
            //connectionJdbcMB.non_query(sql1 + "" + sql2 + "INSTITUCIONAL" + sql3);
            //connectionJdbcMB.non_query(sql1 + "" + sql2 + "SIN DATO" + sql3);
            //connectionJdbcMB.non_query(sql1 + "" + sql2 + "OTRO" + sql3);
        }
        if (variableExpectedSIVIGILA.compareTo("grupo_vulnerable") == 0) {
            connectionJdbcMB.non_query(sql1 + "5" + sql2 + "OTRO" + sql3);
            connectionJdbcMB.non_query(sql1 + "7" + sql2 + "DISCAPACITADO" + sql3);
            connectionJdbcMB.non_query(sql1 + "9" + sql2 + "DESPLAZADO" + sql3);
            connectionJdbcMB.non_query(sql1 + "13" + sql2 + "MIGRANTES" + sql3);
            connectionJdbcMB.non_query(sql1 + "14" + sql2 + "CARCELARIOS" + sql3);
            connectionJdbcMB.non_query(sql1 + "16" + sql2 + "GESTANTES" + sql3);
            //connectionJdbcMB.non_query(sql1 + "" + sql2 + "DISCAPACITADO" + sql3);
            //connectionJdbcMB.non_query(sql1 + "" + sql2 + "DESPLAZADO" + sql3);
            //connectionJdbcMB.non_query(sql1 + "" + sql2 + "DESMOVILIZADO" + sql3);
            //connectionJdbcMB.non_query(sql1 + "" + sql2 + "REFUGIADO" + sql3);
            //connectionJdbcMB.non_query(sql1 + "" + sql2 + "CARCELARIOS" + sql3);
            //connectionJdbcMB.non_query(sql1 + "" + sql2 + "GESTANTES" +sql3);                                
        }
        if (variableExpectedSIVIGILA.compareTo("escenario_hechos") == 0) {
            connectionJdbcMB.non_query(sql1 + "1" + sql2 + "ESPACIO O VIA PUBLICA" + sql3);
            connectionJdbcMB.non_query(sql1 + "2" + sql2 + "CASA" + sql3);
            connectionJdbcMB.non_query(sql1 + "3" + sql2 + "ESCUELA" + sql3);
            connectionJdbcMB.non_query(sql1 + "4" + sql2 + "LUGAR DE TRABAJO" + sql3);
            connectionJdbcMB.non_query(sql1 + "5" + sql2 + "SITIO DE DIVERSION" + sql3);
            connectionJdbcMB.non_query(sql1 + "6" + sql2 + "DEPORTIVO" + sql3);
            connectionJdbcMB.non_query(sql1 + "7" + sql2 + "OTRO LUGAR" + sql3);
            //connectionJdbcMB.non_query(sql1 + "2" + sql2 + "FINCA O CAMPO" + sql3);
            //connectionJdbcMB.non_query(sql1 + "2" + sql2 + "BAR O SIMILARES" + sql3);
            //connectionJdbcMB.non_query(sql1 + "2" + sql2 + "RIO" + sql3);
            //connectionJdbcMB.non_query(sql1 + "2" + sql2 + "CARCEL" + sql3);
            //connectionJdbcMB.non_query(sql1 + "2" + sql2 + "NO SE SABE" + sql3);
        }
    }

    /**
     * This method is responsible for performing the association of variables to
     * related, this method is executed when the button is pressed "CREAR
     * RELACION DE VARIABLES".
     */
    public void btnAssociateVarClick() {
        String error = "";
        boolean nextStep = true;
        //--------------------------------------------------------------------------------------------
        //--- se detrmina si hat seleccionada una variable encontrada y una variable esperada
        //--------------------------------------------------------------------------------------------
        if (nextStep) {
            if (currentVariableExpected == null || currentVariableExpected.isEmpty()) {
                error = "Debe seleccionarse una variable esperada";
                nextStep = false;
            }
        }
        if (nextStep) {
            if (currentVariableFound == null || currentVariableFound.isEmpty()) {
                error = "Debe seleccionarse una variable encontrada";
                nextStep = false;
            }
        }
        if (nextStep) {
            if (projectsMB.getCurrentFormName().compareTo("SIVIGILA-VIF") == 0) {//ES FORMULARIO SIVIGILA-VIF
                convertIdToNameSIVIGILA(currentVariableExpected.get(0), currentVariableFound.get(0), projectsFacade.find(projectsMB.getCurrentProjectId()));
            }
            selectDateFormatDisabled = true;

            RelationVariables newRelationVariables = new RelationVariables();
            newRelationVariables.setIdRelationVariables(relationVariablesFacade.findMaxId() + 1);
            newRelationVariables.setNameExpected(currentVariableExpected.get(0));
            newRelationVariables.setNameFound(currentVariableFound.get(0));
            newRelationVariables.setFieldType(typeVarExepted);
            newRelationVariables.setComparisonForCode(compareForCode);
            newRelationVariables.setDateFormat(currentDateFormat);
            newRelationVariables.setIdRelationGroup(relationGroupFacade.find(projectsMB.getCurrentRelationsGroupId()));
            relationVariablesFacade.create(newRelationVariables);
            loadVarsExpectedAndFound();//recargo listas de variables esperadas y encontradas   
        }
        if (nextStep) {//no se produjeron errores
            if (error.length() == 0) {//no existieron errores            
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto!!", "Asociación de variables realizada."));
            } else {//hay  errores al relacionar la variables 
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", error));
            }
        } else {//se produjo un error
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", error));
        }
    }

    /**
     * This method is responsible for obtaining the identifier of the
     * relationship variables by matching the parameters and name_found
     * name_expected.
     *
     * @param name_expected: Variable name expected
     * @param name_found: variable name found
     * @return
     */
    private int getRelationVariablesId(String name_expected, String name_found) {
        int intReturn = -1;
        try {
            ResultSet rs = connectionJdbcMB.consult(""
                    + " SELECT \n"
                    + " 	id_relation_variables \n"
                    + " FROM \n"
                    + " 	relation_variables \n"
                    + " WHERE \n"
                    + " 	id_relation_group=" + projectsMB.getCurrentRelationsGroupId() + " AND \n"
                    + " 	name_expected LIKE '" + name_expected + "' AND \n"
                    + " 	name_found LIKE '" + name_found + "' \n");
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            System.out.println("Error 7 en " + this.getClass().getName() + ":" + ex.toString());
        }
        return intReturn;
    }

    /**
     * This method is responsible of removing the relationships of variables and
     * in turn it executes when the button is pressed "QUITAR RELACION DE
     * VARIABLES".
     */
    public void btnRemoveRelationVarClick() {
        /*
         * click sobre boton remover relacion de variables
         */
        if (currentRelatedVariables == null || currentRelatedVariables.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar la relación a eliminar."));
        } else {
            //elimino los item de la lista de variables relacionadas
            for (int i = 0; i < currentRelatedVariables.size(); i++) {
                String[] splitVarRelated = currentRelatedVariables.get(i).split("->");
                try {
                    if (projectsMB.getCurrentFormName().compareTo("SIVIGILA-VIF") == 0) {//ES FORMULARIO SIVIGILA-VIF
                        convertNameToIdSIVIGILA(splitVarRelated[0], splitVarRelated[1], projectsFacade.find(projectsMB.getCurrentProjectId()));
                        compareForCode = false;
                    }
                    int relationVariablesId = getRelationVariablesId(splitVarRelated[0], splitVarRelated[1]);
                    connectionJdbcMB.remove("relations_discarded_values", "id_relation_variables = " + relationVariablesId);
                    connectionJdbcMB.remove("relation_values", "id_relation_variables = " + relationVariablesId);
                    connectionJdbcMB.remove("relation_variables", "id_relation_variables = " + relationVariablesId);
                } catch (Exception e) {
                    System.out.println("Error 8 en " + this.getClass().getName() + ":" + e.toString());
                }
                //relationVariablesFacade.remove(relationVariablesFacade.find(getRelationVariablesId(splitVarRelated[0], splitVarRelated[1])));
            }
            currentRelatedVariables = new ArrayList<>();
            loadVarsExpectedAndFound();//recargo lista de variables esperadas y encontradas
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Correcto!!", "Las relaciónes seleccionadas han sido eliminadas."));
        }
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES GET Y SET DE LAS VARIABLES ---------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public List<String> getCurrentVariableFound() {
        return currentVariableFound;
    }

    public void setCurrentVariableFound(List<String> currentVariableFound) {
        this.currentVariableFound = currentVariableFound;
    }

    public List<String> getValuesExpected() {
        return valuesExpected;
    }

    public void setValuesExpected(List<String> valuesExpected) {
        this.valuesExpected = valuesExpected;
    }

    public List<String> getValuesFound() {
        return valuesFound;
    }

    public void setValuesFound(List<String> valuesFound) {
        this.valuesFound = valuesFound;
    }

    public List<String> getVarsFound() {
        return varsFound;
    }

    public void setVarsFound(List<String> varsFound) {
        this.varsFound = varsFound;
    }

    public List<String> getVariablesExpected() {
        return variablesExpected;
    }

    public void setVariablesExpected(List<String> variablesExpected) {
        this.variablesExpected = variablesExpected;
    }

    public List<String> getCurrentRelatedVariables() {
        return currentRelatedVariables;
    }

    public void setCurrentRelatedVariables(List<String> currentRelatedVariables) {
        this.currentRelatedVariables = currentRelatedVariables;
    }

    public List<String> getRelatedVars() {
        return relatedVars;
    }

    public void setRelatedVars(List<String> relatedVars) {
        this.relatedVars = relatedVars;
    }

    public boolean isCompareForCode() {
        return compareForCode;
    }

    public void setCompareForCode(boolean compareForCode) {
        this.compareForCode = compareForCode;
    }

    public String getCurrentRelationGroupName() {
        return currentRelationGroupName;
    }

    public void setCurrentRelationGroupName(String currentRelationGroupName) {
        this.currentRelationGroupName = currentRelationGroupName;
    }

    public boolean isBtnLoadConfigurationDisabled() {
        return btnLoadConfigurationDisabled;
    }

    public void setBtnLoadConfigurationDisabled(boolean btnLoadConfigurationDisabled) {
        this.btnLoadConfigurationDisabled = btnLoadConfigurationDisabled;
    }

    public boolean isBtnSaveConfigurationDisabled() {
        return btnSaveConfigurationDisabled;
    }

    public void setBtnSaveConfigurationDisabled(boolean btnSaveConfigurationDisabled) {
        this.btnSaveConfigurationDisabled = btnSaveConfigurationDisabled;
    }

    public String getCurrentDateFormat() {
        return currentDateFormat;
    }

    public void setCurrentDateFormat(String currentDateFormat) {
        this.currentDateFormat = currentDateFormat;
    }

    public boolean isSelectDateFormatDisabled() {
        return selectDateFormatDisabled;
    }

    public void setSelectDateFormatDisabled(boolean selectDateFormatDisabled) {
        this.selectDateFormatDisabled = selectDateFormatDisabled;
    }
    
    public void setProjectsMB(ProjectsMB projectsMB) {
        this.projectsMB = projectsMB;
    }

    public RelationshipOfValuesMB getRelationshipOfValuesMB() {
        return relationshipOfValuesMB;
    }

    public void setRelationshipOfValuesMB(RelationshipOfValuesMB relationshipOfValuesMB) {
        this.relationshipOfValuesMB = relationshipOfValuesMB;
    }

    public boolean isBtnDivideColumnsDisabled() {
        return btnDivideColumnsDisabled;
    }

    public void setBtnDivideColumnsDisabled(boolean btnDivideColumnsDisabled) {
        this.btnDivideColumnsDisabled = btnDivideColumnsDisabled;
    }

    public boolean isBtnJoinColumnsDisabled() {
        return btnJoinColumnsDisabled;
    }

    public void setBtnJoinColumnsDisabled(boolean btnJoinColumnsDisabled) {
        this.btnJoinColumnsDisabled = btnJoinColumnsDisabled;
    }

    public List<String> getValuesDiscarded() {
        return valuesDiscarded;
    }

    public void setValuesDiscarded(List<String> valuesDiscarded) {
        this.valuesDiscarded = valuesDiscarded;
    }

    public String getExpectedVariablesFilter() {
        return expectedVariablesFilter;
    }

    public void setExpectedVariablesFilter(String expectedVariablesFilter) {
        this.expectedVariablesFilter = expectedVariablesFilter;
    }

    public String getFoundVariablesFilter() {
        return foundVariablesFilter;
    }

    public void setFoundVariablesFilter(String foundVariablesFilter) {
        this.foundVariablesFilter = foundVariablesFilter;
    }

    public String getRelatedVariablesFilter() {
        return relatedVariablesFilter;
    }

    public void setRelatedVariablesFilter(String relatedVariablesFilter) {
        this.relatedVariablesFilter = relatedVariablesFilter;
    }

    public List<String> getCurrentVariableExpected() {
        return currentVariableExpected;
    }

    public void setCurrentVariableExpected(List<String> currentVariableExpected) {
        this.currentVariableExpected = currentVariableExpected;
    }

    public void changeRelatedVariablesFilter() {
        loadRelatedVariables();
    }

    public void changeFoundVariablesFilter() {
        loadFoundVariables();
    }

    public void changeExpectedVariablesFilter() {

        loadExpectedVariables();
    }

    public String getPossibleVariableFound() {
        return possibleVariableFound;
    }

    public void setPossibleVariableFound(String possibleVariableFound) {
        this.possibleVariableFound = possibleVariableFound;
    }
}
