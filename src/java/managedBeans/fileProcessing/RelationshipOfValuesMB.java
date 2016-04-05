/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.fileProcessing;

import beans.connection.ConnectionJdbcMB;
import beans.enumerators.DataTypeEnum;
import beans.util.DamerauLevenshtein;
import beans.util.RowDataTable;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import managedBeans.login.LoginMB;
import model.dao.RelationGroupFacade;
import model.dao.RelationValuesFacade;
import model.dao.RelationVariablesFacade;
import model.dao.RelationsDiscardedValuesFacade;
import model.pojo.RelationGroup;
import model.pojo.RelationValues;
import model.pojo.RelationVariables;
import model.pojo.RelationsDiscardedValues;
import org.primefaces.model.LazyDataModel;

/**
 *The RelationshipOfValuesMB class shows the relationship of values is performed only with the relations of variables that are categorical since they must find the correspondence between the expected values and  the values found.
 * @author santos
 */
@ManagedBean(name = "relationshipOfValuesMB")
@SessionScoped
public class RelationshipOfValuesMB implements Serializable {

    @EJB
    RelationGroupFacade relationGroupFacade;
    @EJB
    RelationVariablesFacade relationVariablesFacade;
    @EJB
    RelationValuesFacade relationValuesFacade;
    @EJB
    RelationsDiscardedValuesFacade relationsDiscardedValuesFacade;
    private List<String> categoricalRelatedVariables;
    private boolean newValueDisabled = true;
    private List<String> valuesDiscarded;
    private List<String> valuesExpected;
    private List<String> valuesFoundSelectedInRelationValues = new ArrayList<>();
    private List<String> valuesRelatedSelectedInRelationValues = new ArrayList<>();
    private List<String> valuesDiscardedSelectedInRelationValues = new ArrayList<>();
    private List<String> valuesFound;
    private List<String> valuesRelated;
    private DamerauLevenshtein damerauLevenshtein = new DamerauLevenshtein();    
    private String[] splitFilterText;
    private String[] splitFoundText;
    private String sql;
    private RelationGroup currentRelationsGroup;
    private LoginMB loginMB;
    private String variableFoundToModify = "";//valor Encontrado para realizar modificacion en opcion "VER"
    private List<String> currentValueExpected = new ArrayList<>();
    private List<String> currentCategoricalRelatedVariables = new ArrayList<>();
    private String coincidentNewValue = "";
    private String expectedValuesFilter = "";
    private String discardedValuesFilter = "";
    private String relatedValuesFilter = "";
    private String copyGroupsFilter = "";
    private String copyRelationVariablesFilter = "";
    private String foundValuesFilter = "";
    private String categoricalRelationsFilter = "";
    private String nameOfValueExpected = "";
    String fieldType = "";
    private DinamicTable dinamicTable = new DinamicTable();
    private ConnectionJdbcMB connectionJdbcMB;
    private ProjectsMB projectsMB;
    private ArrayList<String> selectedRowDataTable = new ArrayList<>();
    private String nameTableTemp = "temp";
    private List<String> copyRelationGroupsList;
    private List<String> copyRelationGroup;
    private List<String> copyRelationsVariablesList;
    private List<String> copyRelationVariablesSelected;
    private RelationVariables currentRelationVariables;
    private List<RowDataTable> moreInfoDataTableList;
    
    
/**
 * first function executed after the constructor that initializes variables and establishes the connection to the database.
 */
    @PostConstruct
    private void initialize() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
    }
/**
 * allows loading of matching records belonging to a categorical variable.
 */
    public void loadCoincidentsRgisters() {
        ResultSet rs;
        coincidentNewValue = "";
        if (selectedRowDataTable != null) {
            newValueDisabled = false;
            try {
                if (selectedRowDataTable != null && !selectedRowDataTable.isEmpty()) {
                    rs = connectionJdbcMB.consult(""
                            + " SELECT \n"
                            + "    project_records.data_value \n"
                            + " FROM \n"
                            + "    project_columns,project_records \n"
                            + " WHERE \n"
                            + "    project_records.project_id = " + projectsMB.getCurrentProjectId() + " AND \n"
                            + "    project_columns.column_id = project_records.column_id AND \n"
                            + "    project_records.record_id = " + selectedRowDataTable.get(0) + " AND \n"
                            + "    project_columns.column_name like '" + currentRelationVariables.getNameFound() + "' \n");

                    if (rs.next()) {
                        coincidentNewValue = rs.getString(1);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error 1 en " + this.getClass().getName() + ":" + e.toString());
            }
            //recargo la tabla de MoreInfo
            moreInfoDataTableList = new ArrayList<>();
            ArrayList<String> titles = new ArrayList<>();
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
                    titles.add(rs.getString(1));
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
                        + "    project_records.record_id = " + selectedRowDataTable.get(0) + " "
                        + " GROUP BY "
                        + "    project_records.project_id, "
                        + "    project_records.record_id ");
                if (rs.next()) {
                    //en la tercer columna esta definido un arreglo con id_columna <=> valor encontrado
                    String[] newRow = new String[titles.size()];
                    Object[] arrayInJava = (Object[]) rs.getArray(3).getArray();
                    for (int i = 0; i < arrayInJava.length; i++) {
                        String splitElement[] = arrayInJava[i].toString().split("<=>");
                        for (int j = 0; j < titles.size(); j++) {
                            if (titles.get(j).compareTo(splitElement[0]) == 0) {
                                newRow[j] = splitElement[1];
                                break;
                            }
                        }
                    }
                    ArrayList<String> newRow2 = new ArrayList<>();
                    newRow2.addAll(Arrays.asList(newRow));

                    for (int i = 0; i < titles.size(); i++) {
                        moreInfoDataTableList.add(new RowDataTable(titles.get(i), newRow2.get(i)));
                    }
                }
            } catch (SQLException ex) {
                System.out.println("Error 2 en " + this.getClass().getName() + ":" + ex.toString());
            }
        }
    }
/**
 * allows change  the values   a data,  found  in the coincidence of the values related this change is executed when button is pressed "change".
 */
    public void changeCoincidentValue() {
        try {
            sql = ""
                    + " UPDATE \n"
                    + "    project_records \n"
                    + " SET \n"
                    + "    data_value= '" + coincidentNewValue + "' \n"
                    + " FROM \n"
                    + "    project_columns \n"
                    + " WHERE \n"
                    + "    project_records.project_id = " + projectsMB.getCurrentProjectId() + " AND \n"
                    + "    project_columns.column_id = project_records.column_id AND \n"
                    + "    project_records.record_id = " + selectedRowDataTable.get(0) + " AND \n"
                    + "    project_columns.column_name like '" + currentRelationVariables.getNameFound() + "' \n";
            connectionJdbcMB.non_query(sql);
            //actualizar tabla
            for (int i = 0; i < dinamicTable.getListOfRecords().size(); i++) {
                if (dinamicTable.getListOfRecords().get(i).get(0).compareTo(selectedRowDataTable.get(0)) == 0) {
                    //inicialmente actualizo el campo indicado en la fila seleccionada
                    for (int j = 0; j < dinamicTable.getTitles().size(); j++) {
                        if (dinamicTable.getTitles().get(j).compareTo(currentRelationVariables.getNameFound()) == 0) {
                            dinamicTable.getListOfRecords().get(i).set(j, coincidentNewValue);
                            break;//rompo ciclo de busqueda en titulos
                        }
                    }
                    break;//rompo ciclo de busqueda en filas
                }
            }
            selectedRowDataTable = null;
            coincidentNewValue = "";
            newValueDisabled = true;
        } catch (Exception e) {
            System.out.println("Error 3 en " + this.getClass().getName() + ":" + e.toString());
        }
    }
/**
 * allows to change the relations of categorical variables.
 */
    public void changeCategoricalRelatedVariables() {
        expectedValuesFilter = "";
        foundValuesFilter = "";
        nameOfValueExpected = "";
        String[] splitValuesRelated;
        currentValueExpected = null;
        valuesFound = new ArrayList<>();
        valuesExpected = new ArrayList<>();
        valuesRelated = new ArrayList<>();
        valuesDiscarded = new ArrayList<>();
        currentRelationVariables = null;
        if (currentCategoricalRelatedVariables != null && !currentCategoricalRelatedVariables.isEmpty()) {
            splitValuesRelated = currentCategoricalRelatedVariables.get(0).split("->");
            try {
                ResultSet rs;
                sql = ""
                        + "SELECT \n"
                        + "  relation_variables.id_relation_variables, \n"
                        + "  relation_variables.id_relation_group, \n"
                        + "  relation_variables.name_expected, \n"
                        + "  relation_variables.name_found, \n"
                        + "  relation_variables.date_format, \n"
                        + "  relation_variables.field_type, \n"
                        + "  relation_variables.comparison_for_code \n"
                        + "FROM \n"
                        + "  public.relation_group, \n"
                        + "  public.relation_variables \n"
                        + "WHERE \n"
                        + "  relation_variables.id_relation_group = relation_group.id_relation_group AND \n"
                        + "  relation_variables.name_expected ILIKE '" + splitValuesRelated[0] + "' AND  \n"
                        + "  relation_variables.name_found ILIKE '" + splitValuesRelated[1] + "' AND  \n"
                        + "  relation_group.id_relation_group = " + projectsMB.getCurrentRelationsGroupId() + ";";               //System.out.println("001 \n" + sql);
                rs = connectionJdbcMB.consult(sql);
                if (rs.next()) {
                    currentRelationVariables = new RelationVariables(rs.getInt("id_relation_variables"));
                    currentRelationVariables.setComparisonForCode(rs.getBoolean("comparison_for_code"));
                    currentRelationVariables.setDateFormat(rs.getString("date_format"));
                    currentRelationVariables.setFieldType(rs.getString("field_type"));
                    currentRelationVariables.setIdRelationGroup(relationGroupFacade.find(rs.getInt("id_relation_group")));
                    currentRelationVariables.setNameExpected(rs.getString("name_expected"));
                    currentRelationVariables.setNameFound(rs.getString("name_found"));
                }
            } catch (SQLException ex) {
                System.out.println("Error 4 en " + this.getClass().getName() + ":" + ex.toString());
            }
            loadFoundValues();
            loadExpectedValues();
            loadRelatedValues();
            loadDiscardedValues();
        }
    }

    public void setProjectsMB(ProjectsMB projectsMB) {
        this.projectsMB = projectsMB;
    }
/**
 * allows to obtain or determine the  type of variable expected.
 * @param varExpected: variable expected
 * @return 
 */
    private String getTypeVariableExpected(String varExpected) {
        String strReturn = "";
        try {
            //determino el ty            
            sql = ""
                    + " SELECT \n"
                    + "    fields.field_type \n"
                    + " FROM \n"
                    + "    public.fields "//, \n"
                    //+ "    public.relation_group \n"
                    + " WHERE \n"
                    //+ "    relation_group.form_id = fields.form_id AND \n"
                    //+ "    relation_group.id_relation_group = " + projectsMB.getCurrentRelationsGroupId() + " AND \n"
                    + "    fields.form_id LIKE '" + projectsMB.getCurrentFormId() + "' AND \n"
                    + "    fields.field_name LIKE '" + varExpected + "'; \n";
            ResultSet rs = connectionJdbcMB.consult(sql);//System.out.println("002\n" + sql);
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException ex) {
            System.out.println("Error 5 en " + this.getClass().getName() + ":" + ex.toString());
        }
        return strReturn;
    }
/**
 * allows load the relationship of categorical variable
 */
    public void loadCategoricalRelatedVariables() {

        valuesFound = new ArrayList<>();
        valuesFoundSelectedInRelationValues = new ArrayList<>();
        foundValuesFilter = "";

        valuesExpected = new ArrayList<>();
        expectedValuesFilter = "";
        currentValueExpected = new ArrayList<>();//valor esperado                
        nameOfValueExpected = "";

        valuesRelated = new ArrayList<>();
        valuesRelatedSelectedInRelationValues = new ArrayList<>();
        relatedValuesFilter = "";

        valuesDiscarded = new ArrayList<>();
        valuesDiscardedSelectedInRelationValues = new ArrayList<>();
        discardedValuesFilter = "";

        currentCategoricalRelatedVariables = new ArrayList<>();
        currentRelationVariables = null;
        try {
            ResultSet rs;
            categoricalRelatedVariables = new ArrayList<>();
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
            if (categoricalRelationsFilter != null && categoricalRelationsFilter.trim().length() != 0) {
                sql = sql + "    AND (relation_variables.name_expected ILIKE '%" + categoricalRelationsFilter + "%' OR \n"
                        + "    relation_variables.name_found ILIKE '%" + categoricalRelationsFilter + "%' ) \n";
            }//System.out.println("003\n" + sql);
            rs = connectionJdbcMB.consult(sql);
            while (rs.next()) {
                fieldType = remove_v(getTypeVariableExpected(rs.getString(1)));
                switch (DataTypeEnum.convert(fieldType)) {
                    case NOVALUE://idica que NO es: integer,date,minute,hour,day,month,year,age,military,degree,percentage ni las _v (requieren validacion)
                        categoricalRelatedVariables.add(rs.getString(1) + "->" + rs.getString(2));
                        break;
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error 6 en " + this.getClass().getName() + ":" + ex.toString());
        }
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES DE PROPOSITO GENERAL ---------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
/**
 * class constructor, responsible for connecting to the database and user verification.
 */
    public RelationshipOfValuesMB() {
        /*
         * Constructor de la clase
         */
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
        loginMB = (LoginMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{loginMB}", LoginMB.class);
        nameTableTemp = "temp" + loginMB.getLoginname();
    }
/**
 *  initializes or restores the initial values of the fields.
 */
    public void reset() {
        nameOfValueExpected = "";
        expectedValuesFilter = "";
        foundValuesFilter = "";
        categoricalRelatedVariables = new ArrayList<>();
        currentCategoricalRelatedVariables = null;
        valuesExpected = new ArrayList<>();
        currentValueExpected = null;
        valuesRelated = new ArrayList<>();
        valuesRelatedSelectedInRelationValues = new ArrayList<>();
        valuesFound = new ArrayList<>();
        valuesFoundSelectedInRelationValues = new ArrayList<>();
        valuesDiscarded = new ArrayList<>();
        valuesDiscardedSelectedInRelationValues = new ArrayList<>();
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES QUE CARGAN VALORES -----------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
/**
 * allows  to load those values that are discarded once they are relations made categorical variables this function is performed in various methods
 */
    private void loadDiscardedValues() {
        valuesDiscardedSelectedInRelationValues = new ArrayList<>();
        valuesDiscarded = new ArrayList<>();
        try {
            ResultSet rs;
            if (currentRelationVariables != null) {
                sql = ""
                        + " SELECT \n"
                        + "   relations_discarded_values.discarded_value_name \n"
                        + " FROM \n"
                        + "   public.relations_discarded_values \n"
                        + " WHERE \n"
                        + "   relations_discarded_values.id_relation_variables = " + currentRelationVariables.getIdRelationVariables() + " \n";
                if (discardedValuesFilter != null && discardedValuesFilter.trim().length() != 0) {
                    sql = sql + " AND relations_discarded_values.discarded_value_name ILIKE '%" + discardedValuesFilter + "%' \n";
                }
                sql = sql + " LIMIT 50";//System.out.println("004 descrtados\n" + sql);
                rs = connectionJdbcMB.consult(sql);
                while (rs.next()) {
                    valuesDiscarded.add(rs.getString(1));
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error 7 en " + this.getClass().getName() + ":" + ex.toString());
        }
    }
/**
 * allows  to load the list of relations of values, this function is used by different methods.
 */
    private void loadRelatedValues() {
        valuesRelatedSelectedInRelationValues = new ArrayList<>();
        valuesRelated = new ArrayList<>();
        try {
            ResultSet rs;
            if (currentRelationVariables != null) {
                sql = ""
                        + " SELECT \n"
                        + "    relation_values.name_expected, \n"
                        + "    relation_values.name_found \n"
                        + " FROM \n"
                        + "    public.relation_values \n"
                        + " WHERE \n"
                        + "    relation_values.id_relation_variables = " + currentRelationVariables.getIdRelationVariables() + " AND \n"
                        + "    --LO QUE SIGUE ES PARA MOSTRAR SOLO LOS USADOS \n"
                        + "    relation_values.name_found IN \n"
                        + "    (SELECT \n"
                        + "        DISTINCT(project_records.data_value) \n"
                        + "     FROM \n"
                        + "        project_records \n"
                        + "     WHERE \n"
                        + "        project_id = " + projectsMB.getCurrentProjectId() + " AND \n"
                        + "        column_id IN \n"
                        + "        (SELECT \n"
                        + "            column_id \n"
                        + "         FROM \n"
                        + "            project_columns \n"
                        + "         WHERE \n"
                        + "            project_columns.column_name LIKE '" + currentRelationVariables.getNameFound() + "' \n"
                        + "        ) \n"
                        + "    ) \n";
                if (relatedValuesFilter != null && relatedValuesFilter.trim().length() != 0) {
                    sql = sql + " AND ( relation_values.name_expected ILIKE '%" + relatedValuesFilter + "%' \n";
                    sql = sql + " OR relation_values.name_found ILIKE '%" + relatedValuesFilter + "%' ) \n";
                }
                sql = sql + " ORDER BY relation_values.id_relation_values DESC \n";
                sql = sql + " LIMIT 50 \n"; //System.out.println("005 relacion de valores\n" + sql);
                rs = connectionJdbcMB.consult(sql);
                while (rs.next()) {
                    valuesRelated.add(rs.getString(1) + "->" + rs.getString(2));
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error 8 en " + this.getClass().getName() + ":" + ex.toString());
        }
    }
/**
 * This method allows to return a list of  found values  for a certain categorical relationship
 * @param limit: variable to set the limit of the list and the limit is 50.
 * @return 
 */
    private ArrayList<String> foundValuesList(boolean limit) {
        /*
         *RETORNA UNA LISTA DE VALORES ENCONTRADOS PARA UNA DETERMINADA RELACION CATEGORICA 
         */
        ArrayList<String> returnList = new ArrayList<>();
        if (currentRelationVariables != null) {
            try {
                sql = ""
                        + " SELECT \n"
                        + " 	DISTINCT(project_records.data_value) \n"
                        + " FROM \n"
                        + " 	project_records \n"
                        + " WHERE   \n"
                        + " 	project_id=" + projectsMB.getCurrentProjectId() + " AND \n"
                        + " 	column_id IN \n"
                        + " 		(SELECT \n"
                        + " 			column_id \n"
                        + " 		FROM \n"
                        + " 			project_columns \n"
                        + " 		WHERE \n"
                        + " 			project_columns.column_name LIKE '" + currentRelationVariables.getNameFound() + "' \n"
                        + " 		) AND \n"
                        + "         data_value NOT IN \n"
                        + "                 (SELECT \n"
                        + "                     relations_discarded_values.discarded_value_name \n"
                        + "                 FROM \n"
                        + "                     public.relations_discarded_values \n"
                        + "                 WHERE \n"
                        + "                     relations_discarded_values.id_relation_variables = " + currentRelationVariables.getIdRelationVariables() + " \n"
                        + "                 ) AND \n"
                        + "         data_value NOT IN \n"
                        + "                 (SELECT \n"
                        + "                     relation_values.name_found  \n"
                        + "                 FROM \n"
                        + "                     public.relation_values \n"
                        + "                 WHERE \n"
                        + "                     relation_values.id_relation_variables = " + currentRelationVariables.getIdRelationVariables() + " \n"
                        + "                 ) \n";
                if (foundValuesFilter != null && foundValuesFilter.trim().length() != 0) {
                    sql = sql + " AND data_value ILIKE '%" + foundValuesFilter + "%' \n";
                }
                if (limit) {
                    sql = sql + " LIMIT 50; \n";
                }//System.out.println("007 encontrados\n" + sql);
                ResultSet rs = connectionJdbcMB.consult(sql);
                while (rs.next()) {
                    returnList.add(rs.getString(1));
                }
            } catch (SQLException ex) {
                System.out.println("Error 9 en " + this.getClass().getName() + ":" + ex.toString());
            }
        }
        return returnList;
    }

/**
 * create a list of values from a certain column  coming from file    with no duplicate values.
 */
    public void loadFoundValues() {
        valuesFoundSelectedInRelationValues = new ArrayList<>();
        valuesFound = foundValuesList(true);
        dinamicTable = new DinamicTable();//elimino los datos del dialog coincidentes
        newValueDisabled = true;//elimino los datos del dialog coincidentes
        coincidentNewValue = "";//elimino los datos del dialog coincidentes
        selectedRowDataTable = new ArrayList<>();//elimino los datos del dialog coincidentes
    }
/**
 * returns a list with the expected values for a realtionship of categorical variables.
 * @param limit
 * @return 
 */
    public ArrayList<String> categoricalList(boolean limit) {
        /*
         * RETORNA UNA LISTA CON LOS VALORES ESPERADOS PARA UNA RELACION DE VARIABLES
         * CATEGORICA, LIMIT ME INDICA SI LA LISTA DEBE SER LIMITADA
         */
        ArrayList<String> returnList = new ArrayList<>();
        try {
            ResultSet resultSetCategory;
            fieldType = remove_v(currentRelationVariables.getFieldType());
            if (fieldType.compareTo("municipalities") == 0) {
                sql = ""
                        + " SELECT "
                        + "    municipalities.municipality_name, \n"
                        + "    departaments.departament_name \n"
                        + " FROM \n"
                        + "    public.municipalities, \n"
                        + "    public.departaments \n"
                        + " WHERE \n"
                        + "    departaments.departament_id = municipalities.departament_id \n";
                if (expectedValuesFilter != null && expectedValuesFilter.trim().length() != 0) {
                    sql = sql + ""
                            + "    AND (municipalities.municipality_name ILIKE '%" + expectedValuesFilter + "%' \n"
                            + "    OR departaments.departament_name ILIKE '%" + expectedValuesFilter + "%') \n";
                }
                if (limit) {
                    sql = sql + " LIMIT 50 \n";
                }
                resultSetCategory = connectionJdbcMB.consult(sql);
                while (resultSetCategory.next()) {
                    returnList.add(resultSetCategory.getString("municipality_name") + " - " + resultSetCategory.getString("departament_name"));
                }
                return returnList;
            } else if (fieldType.compareTo("countries") == 0) {                
                sql = ""
                        + " SELECT \n"
                        + "    * \n"
                        + " FROM \n"
                        + " ( \n"
                        + "      SELECT \n"
                        + "            name AS pais, '' AS departamento, '' AS municipio \n"
                        + "      FROM \n"
                        + "            countries \n"
                        + "      UNION \n"
                        + "      SELECT \n"                                    
                        + "            'COLOMBIA' AS pais, departament_name AS departamento, municipality_name AS municipio \n"
                        + "      FROM \n"
                        + "            public.municipalities \n"
                        + "      JOIN \n"
                        + "            public.departaments \n"
                        + "      USING \n"
                        + "            (departament_id) \n"
                        + "      ORDER BY \n"
                        + "            1, 2, 3 \n"
                        + " ) AS consulta \n"
                        + " WHERE \n";
                if (expectedValuesFilter != null && expectedValuesFilter.trim().length() != 0) {
                    sql = sql + ""
                            + "        municipio ILIKE '%" + expectedValuesFilter + "%' OR \n"
                            + "        departamento ILIKE '%" + expectedValuesFilter + "%' OR \n"
                            + "        pais ILIKE '%" + expectedValuesFilter + "%' \n";
                } else {//se filtra solo por nariño
                    sql = sql + "        departamento ILIKE 'NARIÑO%' \n";
                }
                if (limit) {
                    sql = sql + " LIMIT 200 \n";
                } //System.out.println("030 \n" + sql);
                returnList = new ArrayList<>();
                resultSetCategory = connectionJdbcMB.consult(sql);
                String result;
                while (resultSetCategory.next()) {
                    result = resultSetCategory.getString(1);
                    if (resultSetCategory.getString(2) != null) {
                        result = result + "-" + resultSetCategory.getString(2);
                    }
                    if (resultSetCategory.getString(2) != null) {
                        result = result + "-" + resultSetCategory.getString(3);
                    }
                    returnList.add(result);
                }
                return returnList;
            } else {
                sql = ""
                        + " SELECT \n"
                        + "     * \n"
                        + " FROM \n"
                        + "     " + fieldType + " \n";
                resultSetCategory = connectionJdbcMB.consult(sql);
                String columName;
                if (currentRelationVariables.getComparisonForCode()) {
                    columName = resultSetCategory.getMetaData().getColumnName(1);
                } else {
                    columName = resultSetCategory.getMetaData().getColumnName(2);
                }
                sql = ""
                        + " SELECT \n"
                        + "     * \n"
                        + " FROM \n"
                        + "     " + fieldType + " \n";
                if (expectedValuesFilter != null && expectedValuesFilter.trim().length() != 0) {
                    sql = sql + "    WHERE CAST(" + columName + " as text) ILIKE '%" + expectedValuesFilter + "%' \n";
                }
                if (limit) {
                    sql = sql + " ORDER BY 1 \n LIMIT 50 \n";
                }
                //System.out.println("012 \n" + sql);
                resultSetCategory = connectionJdbcMB.consult(sql);
                while (resultSetCategory.next()) {
                    if (currentRelationVariables.getComparisonForCode()) {
                        returnList.add(resultSetCategory.getString(1));
                    } else {
                        returnList.add(resultSetCategory.getString(2));
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Error 10 en " + this.getClass().getName() + ":" + ex.toString());
        }
        return returnList;
    }
/**
 * This method it is responsible  eliminate or remove the expression "_v" of a data type, so as to take the categorical table.
 * @param field_type
 * @return 
 */
    private String remove_v(String field_type) {
        /*
         * remueve '_v' de un tipo de dato (para que tome la tabla categorica)
         */
        String strReturn = "";
        if (field_type != null && field_type.trim().length() != 0) {
            strReturn = field_type.substring(field_type.length() - 2, field_type.length());
            if (strReturn.compareTo("_v") == 0) {
                strReturn = field_type.substring(0, field_type.length() - 2);
            } else {
                strReturn = field_type;
            }
        }
        return strReturn;
    }
/**
 * This method allows  to load  the expected values depending on  the variable expected.
 */
    public void loadExpectedValues() {
        /*
         * cargar los valores esperados dependiendo la variable esperada
         */
        valuesExpected = new ArrayList<>();//borro la lista de valores esperados 
        nameOfValueExpected = "";
        currentValueExpected = new ArrayList<>();
        if (currentRelationVariables != null) {
            fieldType = remove_v(currentRelationVariables.getFieldType());
            switch (DataTypeEnum.convert(fieldType)) {//tipo de relacion
                case NOVALUE://se espera un valor categorico compareForCodeDisabled = false;
                    valuesExpected = categoricalList(true);
                    break;
            }
        }
    }
/**
 * allows create a list of valuess of a determined column coming from  a record
 * @param column
 * @return 
 */
    public ArrayList createListOfValuesFromFile(String column) {
        /*
         * crear una lista de valores de una determinada columna proveniente del
         * archivo
         */
        ArrayList<String> array = new ArrayList<>();
        try {
            //determino el nombre de la columna
            ResultSet rs = connectionJdbcMB.consult("SELECT " + column + " FROM " + nameTableTemp + "; ");
            while (rs.next()) {
                array.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            System.out.println("Error 11 en " + this.getClass().getName() + ":" + ex.toString());
        }
        return array;
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES CUANDO LISTAS CAMBIAN DE VALOR -----------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
/**
 * allows to obtain the expected values of a variable  selected for  user, this method is called when user selects a value to related.
 */
    public void changeValuesExpected() {
        nameOfValueExpected = "";
        projectsMB.setToolTipText("");
        //busco el nombre o codigo del valor esperado
        if (currentRelationVariables != null && currentValueExpected != null && !currentValueExpected.isEmpty()) {
            fieldType = remove_v(currentRelationVariables.getFieldType());
            if (currentRelationVariables.getComparisonForCode()) {
                nameOfValueExpected = connectionJdbcMB.findNameByCategoricalCode(fieldType, currentValueExpected.get(0));
            } else {
                nameOfValueExpected = connectionJdbcMB.findCodeByCategoricalName(fieldType, currentValueExpected.get(0));
            }
            projectsMB.setToolTipText(currentValueExpected.get(0));
        }
    }
/**
 * allows  to change a value related to another.
 */
    public void changeValuesRelated() {
        if (valuesRelatedSelectedInRelationValues != null && !valuesRelatedSelectedInRelationValues.isEmpty()) {
            projectsMB.setToolTipText(valuesRelatedSelectedInRelationValues.get(0));
        } else {
            projectsMB.setToolTipText("");
        }
    }
/**
 * allows change from one value to another of the  list of   values discarded selected.
 */
    public void changeValuesDiscarded() {
        if (valuesDiscardedSelectedInRelationValues != null && !valuesDiscardedSelectedInRelationValues.isEmpty()) {
            projectsMB.setToolTipText(valuesDiscardedSelectedInRelationValues.get(0));
        } else {
            projectsMB.setToolTipText("");
        }
    }
/**
 * allows change of a values to another of the list of values found.
 */
    public void changeValuesFound() {
        coincidentNewValue = "";
        variableFoundToModify = "";
        newValueDisabled = true;
        if (currentRelationVariables != null) {
            variableFoundToModify = currentRelationVariables.getNameFound();
        }
        if (valuesFoundSelectedInRelationValues != null && !valuesFoundSelectedInRelationValues.isEmpty()) {
            projectsMB.setToolTipText(valuesFoundSelectedInRelationValues.get(0));
        } else {
            projectsMB.setToolTipText("");
        }
    }
/**
 * create a dynamic table in which you can display a list of records of the values found in the relations of variables.
 */
    public final void createDynamicTable() {
        ArrayList<String> titles = new ArrayList<>();
        ArrayList<ArrayList<String>> listOfRecords = new ArrayList<>();

        try {
            if (currentRelationVariables != null) {
                if (valuesFoundSelectedInRelationValues.size() == 1) {
                    //determino nombres de columnas
                    ResultSet rs;
                    ResultSet rs2;
                    String[] newRow;
                    //ArrayList<String> newRow = new ArrayList<String>();
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
                    titles.add("#");
                    while (rs.next()) {
                        titles.add(rs.getString(1));
                    }
                    //System.out.println("Titulos: "+ titles);
                    //determino identificadores de registros contienen el valor encontrado en la columna encontrada
                    rs = connectionJdbcMB.consult(""
                            + " SELECT"
                            + "	   project_records.record_id "//--identificador de registro que coinciden
                            + " FROM"
                            + "	   project_records,project_columns"
                            + " WHERE"
                            + "	   project_columns.column_id = project_records.column_id AND "
                            + "	   project_records.project_id = " + projectsMB.getCurrentProjectId() + " AND "//id_proyecto
                            + "	   project_columns.column_name LIKE '" + currentRelationVariables.getNameFound() + "' AND "//variable_found"
                            + "	   project_records.data_value LIKE '" + valuesFoundSelectedInRelationValues.get(0) + "' "//valuesFoundSelectedInRelationValues.get(0)"
                            + " GROUP BY "
                            + "    project_records.project_id, "
                            + "    project_records.record_id "
                            + "    limit 100");
                    while (rs.next()) {
                        //determino los datos segun el identificador contenido en rs
                        rs2 = connectionJdbcMB.consult(""
                                + " SELECT "
                                + "    project_records.project_id, "
                                + "    project_records.record_id, "
                                + "    array_agg(project_columns.column_name || '<=>' || project_records.data_value) "
                                + " FROM "
                                + "    project_records,project_columns "
                                + " WHERE "
                                + "    project_records.project_id = " + projectsMB.getCurrentProjectId() + " AND "
                                + "    project_columns.column_id = project_records.column_id AND "
                                + "    project_records.record_id = " + rs.getString(1) + " "
                                + " GROUP BY "
                                + "    project_records.project_id, "
                                + "    project_records.record_id ");
                        if (rs2.next()) {
                            //en la tercer columna esta definido un arreglo con id_columna <=> valor encontrado
                            newRow = new String[titles.size()];//+1 para aumentar el identificador del registro
                            Object[] arrayInJava = (Object[]) rs2.getArray(3).getArray();
                            for (int i = 0; i < arrayInJava.length; i++) {
                                String splitElement[] = arrayInJava[i].toString().split("<=>");
                                //System.out.println(i + ": " + splitElement[0] + " --- " + splitElement[1]);

                                for (int j = 0; j < titles.size(); j++) {
                                    if (titles.get(j).compareTo(splitElement[0]) == 0) {
                                        newRow[j] = splitElement[1];
                                        break;
                                    }
                                    if (titles.get(j).compareTo("#") == 0) {
                                        newRow[j] = rs2.getString(2);//identificador del registro
                                    }
                                }
                            }
                            ArrayList<String> newRow2 = new ArrayList<>();
                            newRow2.addAll(Arrays.asList(newRow));
                            listOfRecords.add(newRow2);
                        }
                    }
                    dinamicTable = new DinamicTable(listOfRecords, titles);
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar una valor encontrado"));
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar una relacion categórica"));
            }
        } catch (SQLException ex) {
            System.out.println("Error 12 en " + this.getClass().getName() + ":" + ex.toString());
        }
    }
/**
 * allows to change a group of copy relationships to another.
 */
    public void changeCopyRelationGroup() {
        //SE CARGAN LAS RELACIONES DE VARIABLES PERTENECIENTES A ESTE CONJUNTO
        copyRelationsVariablesList = new ArrayList<>();
        copyRelationVariablesSelected = null;
        List<RelationGroup> relationGroupList = relationGroupFacade.findAll();//buscar si ya existe el nombre
        for (int i = 0; i < relationGroupList.size(); i++) {
            if (relationGroupList.get(i).getNameRelationGroup().compareTo(copyRelationGroup.get(0)) == 0) {
                List<RelationVariables> relationVariablesList = relationGroupList.get(i).getRelationVariablesList();
                for (int j = 0; j < relationVariablesList.size(); j++) {
                    if (copyRelationVariablesFilter != null && copyRelationVariablesFilter.trim().length() != 0) {
                        if (relationVariablesList.get(j).getNameExpected().toUpperCase().indexOf(copyRelationVariablesFilter.toUpperCase()) != -1
                                || relationVariablesList.get(j).getNameFound().toUpperCase().indexOf(copyRelationVariablesFilter.toUpperCase()) != -1) {
                            copyRelationsVariablesList.add(relationVariablesList.get(j).getNameExpected() + "->" + relationVariablesList.get(j).getNameFound());
                        }
                    } else {
                        copyRelationsVariablesList.add(relationVariablesList.get(j).getNameExpected() + "->" + relationVariablesList.get(j).getNameFound());
                    }
                }
                break;
            }
        }
    }
/**
 * load each of these existent relationships that can be visualized by using the button "importar relaciones de valores".
 */
    public void loadRelationsGroups() {
        //CARGAR GRUPO DE RELACIONES EXISTENTES
        copyRelationGroupsList = new ArrayList<>();
        copyRelationGroup = null;
        copyRelationVariablesFilter = "";
        copyRelationsVariablesList = new ArrayList<>();
        copyRelationVariablesSelected = null;
        //btnCopyFrom2Disabled = true;
        if (currentCategoricalRelatedVariables != null && !currentCategoricalRelatedVariables.isEmpty()) {
            List<RelationGroup> relationGroupList = relationGroupFacade.findAll();
            for (int i = 0; i < relationGroupList.size(); i++) {
                if (copyGroupsFilter != null && copyGroupsFilter.trim().length() != 0) {
                    if (relationGroupList.get(i).getNameRelationGroup().toUpperCase().indexOf(copyGroupsFilter.toUpperCase()) != -1) {
                        copyRelationGroupsList.add(relationGroupList.get(i).getNameRelationGroup());
                    }
                } else {
                    copyRelationGroupsList.add(relationGroupList.get(i).getNameRelationGroup());
                }
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar una relacion categorica de la lista"));
        }
    }
/**
 * allows do copying relationships created in other projects.
 */
    public void btnCopyFromClick() {
        int numberCopy = 0;
        foundValuesFilter = "";
        if (copyRelationGroup != null && !copyRelationGroup.isEmpty()) {
            if (copyRelationVariablesSelected != null && !copyRelationVariablesSelected.isEmpty()) {
                ResultSet rs;
                try {
                    String[] splitRelationVariables = copyRelationVariablesSelected.get(0).split("->");
                    ////////////////////////////////////
                    sql = ""
                            + " SELECT  \n"
                            + " 	* \n"
                            + " FROM \n"
                            + " 	--subconsulta de donde se va a copiar \n"
                            + " 	(SELECT  \n"
                            + " 		relation_values.name_expected as valor_esperado,  \n"
                            + " 		relation_values.name_found    as valor_encontrado \n"
                            + " 	FROM  \n"
                            + " 		public.relation_values,  \n"
                            + " 		public.relation_variables,  \n"
                            + " 		public.relation_group \n"
                            + " 	WHERE  \n"
                            + " 		relation_values.id_relation_variables = relation_variables.id_relation_variables AND \n"
                            + " 		relation_variables.id_relation_group = relation_group.id_relation_group AND \n"
                            + " 		relation_group.name_relation_group LIKE '" + copyRelationGroup.get(0) + "' AND  \n"
                            + " 		relation_variables.name_expected LIKE '" + splitRelationVariables[0] + "' AND  \n"
                            + " 		relation_variables.name_found LIKE '" + splitRelationVariables[1] + "' \n"
                            + " 	) as subconsulta1, \n"
                            + " 	--subconsulta de valores del archivo \n"
                            + " 	(SELECT  \n"
                            + " 		DISTINCT(project_records.data_value) as valor_archivo \n"
                            + " 	 FROM  \n"
                            + " 		project_records  \n"
                            + " 	 WHERE    \n"
                            + " 		project_id = " + projectsMB.getCurrentProjectId() + " AND  \n"
                            + " 		column_id IN  \n"
                            + " 			(SELECT  \n"
                            + " 				column_id  \n"
                            + " 			FROM  \n"
                            + " 				project_columns  \n"
                            + " 			WHERE  \n"
                            + " 				project_columns.column_name LIKE '" + currentRelationVariables.getNameFound() + "'  \n"
                            + " 			) AND  \n"
                            + " 		 data_value NOT IN  \n"
                            + " 			 (SELECT  \n"
                            + " 			     relations_discarded_values.discarded_value_name  \n"
                            + " 			 FROM  \n"
                            + " 			     public.relations_discarded_values  \n"
                            + " 			 WHERE  \n"
                            + " 			     relations_discarded_values.id_relation_variables = " + currentRelationVariables.getIdRelationVariables() + "  \n"
                            + " 			 ) AND  \n"
                            + " 		 data_value NOT IN  \n"
                            + " 			 (SELECT  \n"
                            + " 			     relation_values.name_found   \n"
                            + " 			 FROM  \n"
                            + " 			     public.relation_values  \n"
                            + " 			 WHERE  \n"
                            + " 			     relation_values.id_relation_variables = " + currentRelationVariables.getIdRelationVariables() + "  \n"
                            + " 			 ) \n"
                            + " 	) as subconsulta2 \n"
                            + " WHERE \n"
                            + " 	valor_encontrado LIKE valor_archivo  \n";
                    ////////////////////////////////////
                    //System.out.println("001" + sql);
                    rs = connectionJdbcMB.consult(sql);
                    while (rs.next()) {

                        //System.out.println("Se puede copiar la relacion" + rs.getString(1) + "->" + rs.getString(2));
                        //currentRelationVariables.addRelationValue(relationValuesListToCopy.get(i).getNameExpected(), relationValuesListToCopy.get(i).getNameFound());
                        RelationValues newRelationValues = new RelationValues(relationValuesFacade.findMaxId() + 1);
                        newRelationValues.setIdRelationVariables(currentRelationVariables);
                        newRelationValues.setNameExpected(rs.getString(1));
                        newRelationValues.setNameFound(rs.getString(2));
                        relationValuesFacade.create(newRelationValues);
                        numberCopy++;
                    }
                } catch (Exception e) {
                    System.out.println("Error 13 en " + this.getClass().getName() + ":" + e.toString());
                }
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Finalizado", "Se copiaron: (" + String.valueOf(numberCopy) + ") relaciones de valores "));
                //changeCategoricalRelatedVariables();
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Debe seleccionar una relación de variables de la lista"));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar un conjunto de relaciones de la lista"));
        }
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //CLIK SOBRE BOTONOES --------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
/**
 * This method allows to remove a value discarded  from the list discarded by using the button "Remove value Discarded".
 */
    public void btnRemoveDiscardedValuesClick() {
//        //---------------------------------------------------------------------------
//        //como se quita de la lista un item se determina que item quedara seleccionado
//        //---------------------------------------------------------------------------        
        if (valuesDiscardedSelectedInRelationValues != null && !valuesDiscardedSelectedInRelationValues.isEmpty()) {
            for (int i = 0; i < valuesDiscardedSelectedInRelationValues.size(); i++) {
                try {
                    connectionJdbcMB.remove(
                            "relations_discarded_values",
                            "discarded_value_name LIKE '" + valuesDiscardedSelectedInRelationValues.get(i)
                            + "' AND id_relation_variables = " + currentRelationVariables.getIdRelationVariables());
                    //System.out.println("(((((((((((((((Se elimino el valor: " + valuesDiscardedSelectedInRelationValues.get(i));
                } catch (Exception e) {
                    System.out.println("Error 14 en " + this.getClass().getName() + ":" + e.toString());
                }
            }
            loadFoundValues();
            loadDiscardedValues();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se eliminaron los valores descartados seleccionados"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar un los valores descartados que desea quitar"));
        }
    }
/**
 * This method is executed when a value from the list of values found is selected and pressed the "Discard value" which makes pass this value to the list of discarded values.
 */
    public void btnDiscardValueClick() {

//        //---------------------------------------------------------------------------
//        //como se quita de la lista un item se determina que item quedara seleccionado
//        //---------------------------------------------------------------------------        
        if (currentRelationVariables != null && !valuesFoundSelectedInRelationValues.isEmpty()) {
            //RelationVariables currentRelationVar = currentRelationsGroup.findRelationVar(currentRelationVariables.getNameExpected(), currentRelationVariables.getNameFound());
            for (int i = 0; i < valuesFoundSelectedInRelationValues.size(); i++) {
                RelationsDiscardedValues newRelationsDiscardedValues = new RelationsDiscardedValues(relationsDiscardedValuesFacade.findMaxId() + 1);
                newRelationsDiscardedValues.setIdRelationVariables(currentRelationVariables);
                newRelationsDiscardedValues.setDiscardedValueName(valuesFoundSelectedInRelationValues.get(i));
                relationsDiscardedValuesFacade.create(newRelationsDiscardedValues);
            }
            loadFoundValues();
            //loadExpectedValues();
            //loadRelatedValues();
            loadDiscardedValues();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Los valores se descartaron"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se deben seleccionar los valores encontrados que desea descartar"));
        }
    }
/**
 * This method is responsible for perform  relationship the values by selecting two values and use button "Relating values."
 */
    public void btnAssociateRelationValueClick() {
        //---------------------------------------------------------------------------
        //como se quita de la lista un item se determina que item quedara seleccionado
        //---------------------------------------------------------------------------            
        if (currentValueExpected != null && !currentValueExpected.isEmpty()) {
            if (valuesFoundSelectedInRelationValues != null && !valuesFoundSelectedInRelationValues.isEmpty()) {

                for (int i = 0; i < valuesFoundSelectedInRelationValues.size(); i++) {
                    RelationValues newRelationValues = new RelationValues(relationValuesFacade.findMaxId() + 1);
                    newRelationValues.setIdRelationVariables(currentRelationVariables);
                    newRelationValues.setNameExpected(currentValueExpected.get(0));
                    newRelationValues.setNameFound(valuesFoundSelectedInRelationValues.get(i));
                    relationValuesFacade.create(newRelationValues);
                }
                loadFoundValues();
                loadRelatedValues();
                valuesFoundSelectedInRelationValues = new ArrayList<>();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Relación de valores creada correctamente"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar uno o varios valores encontrados de la lista"));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar un valor esperado de la lista"));
        }
    }

    private boolean calculateLevenstein(String filterText, String foundText) {
        //damerauLevenshtein = new DamerauLevenshtein();
        //Similarity = 0;
        //creo un arreglo de cadenas con cada palabra
        splitFilterText = filterText.split(" ");
        splitFoundText = foundText.split(" ");
        //elimino las cadenas de cada arreglo que tengan menos de 4s caracteres
        for (int i = 0; i < splitFilterText.length; i++) {
            if (splitFilterText[i].length() <= 3) {
                splitFilterText[i] = "";
            }
        }
        for (int i = 0; i < splitFoundText.length; i++) {
            if (splitFoundText[i].length() <= 3) {
                splitFoundText[i] = "";
            }
        }
        //realizo el calculo de levenstein de todoas las palabras
        for (int i = 0; i < splitFilterText.length; i++) {
            for (int j = 0; j < splitFoundText.length; j++) {
                if (splitFilterText[i].length() != 0 && splitFoundText[j].length() != 0) {
                    if (damerauLevenshtein.getSimilarity(splitFilterText[i], splitFoundText[j]) < 2) {
                        return true;
                    }

//                    System.out.println(
//                            "COMPARACION: " + String.valueOf(jaroWinkler.getSimilarity(splitFilterText[i], splitFoundText[j]))
//                            + " CADENA1: " + splitFilterText[i]
//                            + " CADENA2: " + splitFoundText[j]);//jaroWinkler.getSimilarity(splitFilterText[i], splitFoundText[j]);
//
//                    if (jaroWinkler.getSimilarity(splitFilterText[i], splitFoundText[j]) > 0.8) {
//                        return true;
//                    }
                }
            }
        }
        return false;
    }
/**
 * allows perform an automatic association of expected values and the values found.
 */
    public void btnAutomaticRelationClick() {
        /*
         * Asociación automática de valores esperados y valores encontrados
         */

        int numberOfCreate = 0;//numero de relaciones creadas
        if (currentRelationVariables != null) {
            foundValuesFilter = "";
            expectedValuesFilter = "";
            List<String> valuesFoundList = foundValuesList(false);
            List<String> valuesExpectedList = categoricalList(false);

            for (int i = 0; i < valuesFoundList.size(); i++) {
                for (int j = 0; j < valuesExpectedList.size(); j++) {
                    String valueFoundNoAccent = valuesFoundList.get(i).trim().toUpperCase().replace("\\.", "").replace(";", "");
                    String valueExpectedNoAccent = valuesExpectedList.get(j).trim().toUpperCase().replace("\\.", "").replace(";", "");
                    valueFoundNoAccent = valueFoundNoAccent.replace("Á", "A").replace("É", "E").replace("Í", "I").replace("Ó", "O").replace("Ú", "U");
                    valueExpectedNoAccent = valueExpectedNoAccent.replace("Á", "A").replace("É", "E").replace("Í", "I").replace("Ó", "O").replace("Ú", "U");

                    if (valueFoundNoAccent.compareTo(valueExpectedNoAccent) == 0) {
                        RelationValues newRelationValues = new RelationValues(relationValuesFacade.findMaxId() + 1);
                        newRelationValues.setIdRelationVariables(currentRelationVariables);
                        newRelationValues.setNameExpected(valuesExpectedList.get(j));
                        newRelationValues.setNameFound(valuesFoundList.get(i));
                        relationValuesFacade.create(newRelationValues);
                        //currentRelationVariables.addRelationValue(valuesExpectedList.get(j), valuesFoundList.get(i));
                        numberOfCreate++;
                        break;
                    }
                }
            }
            if (numberOfCreate != 0) {
                loadFoundValues();
                loadExpectedValues();
                loadRelatedValues();
                loadDiscardedValues();
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Finalizado", "El proceso automático realizó: (" + String.valueOf(numberOfCreate) + ") relaciones de valores"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar una relacion categórica de la lista para generar las relaciones de valores automáticamente"));
        }
    }
/**
 * This method allows you to remove the relationship of values performed  by the selecting one of these, this method runs when pressed the "Remove  values relationship   " button.
 */
    public void btnRemoveRelationValueClick() {
//        //---------------------------------------------------------------------------
//        //como se quita de la lista un item se determina que item quedara seleccionado
//        //---------------------------------------------------------------------------        
        if (valuesRelatedSelectedInRelationValues != null && !valuesRelatedSelectedInRelationValues.isEmpty()) {
            for (int i = 0; i < valuesRelatedSelectedInRelationValues.size(); i++) {
                String[] splitValuedRelated = valuesRelatedSelectedInRelationValues.get(i).split("->");
                try {//remuevo la relacion de valores 
                    connectionJdbcMB.remove("relation_values",
                            "id_relation_variables = " + currentRelationVariables.getIdRelationVariables() + " AND "
                            + " name_expected LIKE '" + splitValuedRelated[0] + "' AND "
                            + " name_found LIKE '" + splitValuedRelated[1] + "' ");
                } catch (Exception e) {
                    System.out.println("Error 15 en " + this.getClass().getName() + ":" + e.toString());
                }
            }
            loadFoundValues();
            loadRelatedValues();
            loadDiscardedValues();
            valuesRelatedSelectedInRelationValues = new ArrayList<>();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se eliminaron los valores descartados seleccionados"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "SE deben seleccionar las relaciones de valores a eliminar"));
        }
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES GET Y SET DE LAS VARIABLES ---------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
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

    public RelationGroup getCurrentRelationsGroup() {
        return currentRelationsGroup;
    }

    public void setCurrentRelationsGroup(RelationGroup currentRelationsGroup) {
        this.currentRelationsGroup = currentRelationsGroup;
    }

    public List<String> getCurrentValueExpected() {
        return currentValueExpected;
    }

    public void setCurrentValueExpected(List<String> currentValueExpected) {
        this.currentValueExpected = currentValueExpected;
    }

    public List<String> getValuesRelated() {
        return valuesRelated;
    }

    public void setValuesRelated(List<String> valuesRelated) {
        this.valuesRelated = valuesRelated;
    }

    public List<String> getCategoricalRelatedVariables() {
        return categoricalRelatedVariables;
    }

    public void setCategoricalRelatedVariables(List<String> categoricalRelatedVariables) {
        this.categoricalRelatedVariables = categoricalRelatedVariables;
    }

    public List<String> getCurrentCategoricalRelatedVariables() {
        return currentCategoricalRelatedVariables;
    }

    public void setCurrentCategoricalRelatedVariables(List<String> currentCategoricalRelatedVariables) {
        this.currentCategoricalRelatedVariables = currentCategoricalRelatedVariables;
    }

    public List<String> getValuesDiscarded() {
        return valuesDiscarded;
    }

    public void setValuesDiscarded(List<String> valuesDiscarded) {
        this.valuesDiscarded = valuesDiscarded;
    }

    public List<String> getValuesDiscardedSelectedInRelationValues() {
        return valuesDiscardedSelectedInRelationValues;
    }

    public void setValuesDiscardedSelectedInRelationValues(List<String> valuesDiscardedSelectedInRelationValues) {
        this.valuesDiscardedSelectedInRelationValues = valuesDiscardedSelectedInRelationValues;
    }

    public List<String> getValuesFoundSelectedInRelationValues() {
        return valuesFoundSelectedInRelationValues;
    }

    public void setValuesFoundSelectedInRelationValues(List<String> valuesFoundSelectedInRelationValues) {
        this.valuesFoundSelectedInRelationValues = valuesFoundSelectedInRelationValues;
    }

    public List<String> getValuesRelatedSelectedInRelationValues() {
        return valuesRelatedSelectedInRelationValues;
    }

    public void setValuesRelatedSelectedInRelationValues(List<String> valuesRelatedSelectedInRelationValues) {
        this.valuesRelatedSelectedInRelationValues = valuesRelatedSelectedInRelationValues;
    }

    public String getNameOfValueExpected() {
        return nameOfValueExpected;
    }

    public void setNameOfValueExpected(String nameOfValueExpected) {
        this.nameOfValueExpected = nameOfValueExpected;
    }

    public DinamicTable getDinamicTable() {
        return dinamicTable;
    }

    public void setDinamicTable(DinamicTable dinamicTable) {
        this.dinamicTable = dinamicTable;
    }

    public ArrayList<String> getSelectedRowDataTable() {
        return selectedRowDataTable;
    }

    public void setSelectedRowDataTable(ArrayList<String> selectedRowDataTable) {
        this.selectedRowDataTable = selectedRowDataTable;
    }

    public String getCategoricalRelationsFilter() {
        return categoricalRelationsFilter;
    }

    public void setCategoricalRelationsFilter(String categoricalRelationsFilter) {
        this.categoricalRelationsFilter = categoricalRelationsFilter;
    }

    public String getCoincidentNewValue() {
        return coincidentNewValue;
    }

    public void setCoincidentNewValue(String coincidentNewValue) {
        this.coincidentNewValue = coincidentNewValue;
    }

    public String getVariableFoundToModify() {
        return variableFoundToModify;
    }

    public void setVariableFoundToModify(String variableFoundToModify) {
        this.variableFoundToModify = variableFoundToModify;
    }

    public boolean isNewValueDisabled() {
        return newValueDisabled;
    }

    public void setNewValueDisabled(boolean newValueDisabled) {
        this.newValueDisabled = newValueDisabled;
    }

    public List<String> getCopyRelationGroupsList() {
        return copyRelationGroupsList;
    }

    public void setCopyRelationGroupsList(List<String> copyRelationGroupsList) {
        this.copyRelationGroupsList = copyRelationGroupsList;
    }

    public List<String> getCopyRelationGroup() {
        return copyRelationGroup;
    }

    public void setCopyRelationGroup(List<String> copyRelationGroup) {
        this.copyRelationGroup = copyRelationGroup;
    }

    public List<String> getCopyRelationVariablesSelected() {
        return copyRelationVariablesSelected;
    }

    public void setCopyRelationVariablesSelected(List<String> copyRelationVariablesSelected) {
        this.copyRelationVariablesSelected = copyRelationVariablesSelected;
    }

    public List<String> getCopyRelationsVariablesList() {
        return copyRelationsVariablesList;
    }

    public void setCopyRelationsVariablesList(List<String> copyRelationsVariablesList) {
        this.copyRelationsVariablesList = copyRelationsVariablesList;
    }

    public String getDiscardedValuesFilter() {
        return discardedValuesFilter;
    }

    public void setDiscardedValuesFilter(String discardedValuesFilter) {
        this.discardedValuesFilter = discardedValuesFilter;
    }

    public String getRelatedValuesFilter() {
        return relatedValuesFilter;
    }

    public void setRelatedValuesFilter(String relatedValuesFilter) {
        this.relatedValuesFilter = relatedValuesFilter;
    }

    public List<RowDataTable> getMoreInfoDataTableList() {
        return moreInfoDataTableList;
    }

    public void setMoreInfoDataTableList(List<RowDataTable> moreInfoDataTableList) {
        this.moreInfoDataTableList = moreInfoDataTableList;
    }

    public String getCopyGroupsFilter() {
        return copyGroupsFilter;
    }

    public void setCopyGroupsFilter(String copyGroupsFilter) {
        this.copyGroupsFilter = copyGroupsFilter;
    }

    public String getCopyRelationVariablesFilter() {
        return copyRelationVariablesFilter;
    }

    public void setCopyRelationVariablesFilter(String copyRelationVariablesFilter) {
        this.copyRelationVariablesFilter = copyRelationVariablesFilter;
    }

    public String getExpectedValuesFilter() {
        return expectedValuesFilter;
    }

    public void setExpectedValuesFilter(String expectedValuesFilter) {
        this.expectedValuesFilter = expectedValuesFilter;
    }

    public String getFoundValuesFilter() {
        return foundValuesFilter;
    }

    public void setFoundValuesFilter(String foundValuesFilter) {
        this.foundValuesFilter = foundValuesFilter;
    }

    public void changeCategoricalRelationsFilter() {
        loadCategoricalRelatedVariables();
    }

    public void changeFoundValuesFilter() {
        loadFoundValues();
    }

    public void changeRelatedValuesFilter() {
        loadRelatedValues();
    }

    public void changeCopyGroupsFilter() {
        loadRelationsGroups();
    }

    public void changeCopyRelationVariablesFilter() {
        changeCopyRelationGroup();
    }

    public void changeDiscardedValuesFilter() {
        loadDiscardedValues();
    }

    public void changeExpectedValuesFilter() {
        loadExpectedValues();
    }   
}
