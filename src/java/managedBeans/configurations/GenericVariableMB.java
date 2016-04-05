/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.configurations;

import beans.connection.ConnectionJdbcMB;
import beans.util.RowDataTable;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.apache.poi.hssf.usermodel.*;

/**
 * GenericVariableMB class allows the management of a variable (edit, delete,
 * create,categories):accidentClass,activities,destinations_of_patient,diagnoses,ethnic_groups,involved_vehicles,jobs,health_professionals,insurance,places,non_fatal_places,non_fatal_data_sources_from_where,murder_contexts,
 * mechanisms, protectiveMeasures, relatedEvents, serviceType,
 * victimCharacteristics, vulnerableGroup, weaponType.
 *
 * @author SANTOS
 */
@ManagedBean(name = "genericVariableMB")
@SessionScoped
public class GenericVariableMB implements Serializable {

    /**
     * Esta clase permite la gestion de una variable(editar, eliminar, crear
     * categorias): accidentClass, activities, destinations_of_patient,
     * diagnoses, ethnic_groups, involved_vehicles, jobs, health_professionals,
     * insurance, places, non_fatal_places, non_fatal_data_sources_from_where,
     * murder_contexts, mechanisms, protectiveMeasures, relatedEvents,
     * serviceType, victimCharacteristics, vulnerableGroup, weaponType
     */
    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    private ConnectionJdbcMB connectionJdbcMB;
    private String name = "";
    private String newName = "";
    private String code = "";
    private boolean codeDisabled = true;
    private String newCode = "";
    private boolean btnEditDisabled = true;
    private boolean btnRemoveDisabled = true;
    private String currentVariableName = "";
    private String currentVariableNameXLSX = "";//nombre para la exportacion de datos
    private String currentVariableId = "";
    private String currentVariableTable = "";
    private String currentVariableTypePrimaryKey = "";
    private String currentVariableObligatoryId = "";

    /**
     * This method is the class constructor and is responsible for connecting to
     * the database.
     */
    public GenericVariableMB() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
    }

    /**
     * This method is used to load the data of the variable that is to be
     * managed.
     *
     * @param variableId: id variable to manage.
     */
    public void loadVariableData(int variableId) {
        //funcion para cargar los datos de la variable que se va a gestionar
        try {
            ResultSet rs = connectionJdbcMB.consult(""
                    + " SELECT * "
                    + " FROM categorical_variables "
                    + " WHERE categorical_variable_id = " + variableId);

            rs.next();
            currentVariableName = rs.getString(2);
            currentVariableId = rs.getString(1);
            currentVariableTable = rs.getString(3);
            currentVariableTypePrimaryKey = rs.getString(4);
            currentVariableObligatoryId = rs.getString(6);
            //nombre para el archivo XLS
            currentVariableNameXLSX = "";
            for (int i = 0; i < currentVariableName.length(); i++) {
                if ((currentVariableName.charAt(i) >= 'a' && currentVariableName.charAt(i) <= 'z')
                        || (currentVariableName.charAt(i) >= 'A' && currentVariableName.charAt(i) <= 'Z')) {
                    currentVariableNameXLSX = currentVariableNameXLSX + String.valueOf(currentVariableName.charAt(i));
                } else {
                    currentVariableNameXLSX = currentVariableNameXLSX + "_";
                }

            }
            //determinar si la llave primaria puede calcularse
            if (currentVariableTypePrimaryKey.compareTo("smallint") == 0 || currentVariableTypePrimaryKey.compareTo("integer") == 0) {
                rs = connectionJdbcMB.consult("SELECT * FROM " + currentVariableTable);
                String namePrimaryKey = rs.getMetaData().getColumnName(1);
                rs = connectionJdbcMB.consult(""
                        + " SELECT MAX(" + namePrimaryKey + ") FROM " + currentVariableTable);
                rs.next();
                newCode = String.valueOf(rs.getInt(1) + 1);
                codeDisabled = true;
                newName = "";

            } else {
                codeDisabled = false;
                newCode = "";
                newName = "";
            }
        } catch (Exception e) {
        }
        rowDataTableList = new ArrayList<>();
        selectedRowDataTable = null;
        currentSearchValue = "";
        currentSearchCriteria = 1;
        createDynamicTable();
    }

    /**
     * It is responsible to create a cell within the row.
     *
     * @param cellStyle: Style that will have the cell.
     * @param fila: row where create the cell
     * @param position: Determines the position where anger cell within the row.
     * @param value: Sets the value that will be created within the cell.
     */
    private void createCell(HSSFCellStyle cellStyle, HSSFRow fila, int position, String value) {
        HSSFCell cell;
        cell = fila.createCell((short) position);// Se crea una cell dentro de la fila                        
        cell.setCellValue(new HSSFRichTextString(value));
        cell.setCellStyle(cellStyle);
    }

    /**
     * It is responsible to create a cell within the row.
     *
     * @param fila: row where create the cell
     * @param position: Determines the position where anger cell within the row.
     * @param value: Sets the value that will be created within the cell.
     */
    private void createCell(HSSFRow fila, int position, String value) {
        HSSFCell cell;
        cell = fila.createCell((short) position);// Se crea una cell dentro de la fila                        
        cell.setCellValue(new HSSFRichTextString(value));
    }

    /**
     * runs a xls file where the user inserts a row within a worksheet where two
     * fields are set: CODE, NAME.
     *
     * @param document: Document to modify the name and code field.
     */
    public void postProcessXLS(Object document) {
        HSSFWorkbook book = (HSSFWorkbook) document;
        HSSFSheet sheet = book.getSheetAt(0);// Se toma hoja del libro
        HSSFRow row;
        HSSFCellStyle cellStyle = book.createCellStyle();
        HSSFFont font = book.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        cellStyle.setFont(font);

        row = sheet.createRow(0);// Se crea una fila dentro de la hoja        
        createCell(cellStyle, row, 0, "CODIGO");//"100">#{rowX.column1}</p:column>
        createCell(cellStyle, row, 1, "NOMBRE");//"100">#{rowX.column23}</p:column>                                
        try {
            int i = 0;
            String sql = " SELECT * FROM " + currentVariableTable;
            ResultSet rs = connectionJdbcMB.consult(sql);
            while (rs.next()) {
                row = sheet.createRow(i + 1);
                createCell(row, 0, rs.getString(1));//CODIGO
                createCell(row, 1, rs.getString(2));//NOMBRE            
                i++;
            }
        } catch (Exception e) {
        }
    }

    /**
     * load data categorical a manage
     */
    public void loadCategoryData() {
        if (selectedRowDataTable != null) {
            btnEditDisabled = true;
            btnRemoveDisabled = true;
            try {
                String sql = " SELECT * FROM " + currentVariableTable;
                ResultSet rs = connectionJdbcMB.consult(sql);
                sql = sql + " WHERE " + rs.getMetaData().getColumnName(1) + "::text like '" + selectedRowDataTable.getColumn1() + "'";
                rs = connectionJdbcMB.consult(sql);
                if (rs.next()) {
                    name = rs.getString(2);
                    code = rs.getString(1);
                    btnEditDisabled = false;
                    btnRemoveDisabled = false;
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * tries to delete a record.
     */
    private void tryDeleteRegistry() {
        /*
         * se trata de eliminar un registro 
         */
        try {
            String sql = " SELECT * FROM " + currentVariableTable;
            ResultSet rs = connectionJdbcMB.consult(sql);
            sql = " DELETE FROM " + currentVariableTable + " WHERE " + rs.getMetaData().getColumnName(1) + "::text like '" + selectedRowDataTable.getColumn1() + "'";
            connectionJdbcMB.non_query(sql);
            if (connectionJdbcMB.getMsj().startsWith("ERROR")) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se puede eliminar esta categoria por que existen registros que estan haciendo uso de esta");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "La categoria se ha eliminado correctamente");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                loadVariableData(Integer.parseInt(currentVariableId));
                createDynamicTable();
            }
        } catch (SQLException | NumberFormatException e) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se puede eliminar esta categoria por que existen registros que estan haciendo uso de esta");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    /**
     * It is called when the delete record button is pressed, this method is
     * responsible to remove the selected category.
     */
    public void deleteRegistry() {
        /*
         * se llama cuando se presiona el boton eliminar registro
         */
        if (selectedRowDataTable != null) {
            if (currentVariableTypePrimaryKey.compareTo("smallint") == 0 || currentVariableTypePrimaryKey.compareTo("integer") == 0) {
                //se puede determinar si hay categorias que no pueden ser eliminadas
                if (Integer.parseInt(selectedRowDataTable.getColumn1()) <= Integer.parseInt(currentVariableObligatoryId)) {
                    //pertenece a una categoria obligatoria no puede ser eliminada
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se puede eliminar esta categoria por que existen registros que estan haciendo uso de esta");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                } else {
                    //se tratara de eliminar, si se esta usando no se puede eliminar
                    tryDeleteRegistry();
                }
            } else {
                //se tratara de eliminar, si se esta usando no se puede eliminar
                tryDeleteRegistry();
            }
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar la categoria a eliminar");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        createDynamicTable();
        btnEditDisabled = true;
        btnRemoveDisabled = true;
    }

    /**
     * Allows perform updating a category.
     */
    public void updateRegistry() {
        boolean continueProcess = false;
        String categoryColumnName = "";
        String categoryColumnCode = "";
        if (selectedRowDataTable != null) {
            continueProcess = true;
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar la categoria a editar");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }

        if (continueProcess) {
            if (name != null && code != null && name.trim().length() != 0 && code.trim().length() != 0) {
                name = name.toUpperCase();
                code = code.toUpperCase();
            } else {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Codigo y nombre son obligatorios");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                continueProcess = false;
            }
        }

        if (continueProcess) {
            try {
                String sqlName, sqlCode;
                ResultSet rs = connectionJdbcMB.consult(" SELECT * FROM " + currentVariableTable);
                categoryColumnCode = rs.getMetaData().getColumnName(1);
                categoryColumnName = rs.getMetaData().getColumnName(2);


                sqlCode = ""
                        + " SELECT * FROM " + currentVariableTable
                        + " WHERE " + categoryColumnCode + "::text ilike '" + code + "'"
                        + " AND " + categoryColumnCode + "::text not ilike '" + selectedRowDataTable.getColumn1() + "'";
                sqlName = ""
                        + " SELECT * FROM " + currentVariableTable
                        + " WHERE " + categoryColumnName + "::text ilike '" + name + "'"
                        + " AND " + categoryColumnCode + "::text not ilike '" + selectedRowDataTable.getColumn1() + "'";

                rs = connectionJdbcMB.consult(sqlName);
                if (rs.next()) {
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe otra categoria con el mismo nombre");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    continueProcess = false;
                    code = selectedRowDataTable.getColumn1();
                    name = selectedRowDataTable.getColumn2();
                } else {
                    rs = connectionJdbcMB.consult(sqlCode);
                    if (rs.next()) {
                        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe otra categoria con el mismo codigo");
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                        continueProcess = false;
                        code = selectedRowDataTable.getColumn1();
                        name = selectedRowDataTable.getColumn2();
                    }
                }
            } catch (Exception e) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al validar los datos");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
        if (continueProcess) {//realizar la actualizacion de los datos
            if (currentVariableTypePrimaryKey.compareTo("smallint") == 0 || currentVariableTypePrimaryKey.compareTo("integer") == 0) {
                connectionJdbcMB.non_query(""
                        + " UPDATE " + currentVariableTable + " SET "
                        + categoryColumnCode + " = " + code + ", "
                        + categoryColumnName + " = '" + name + "' "
                        + " WHERE " + categoryColumnCode + " = " + selectedRowDataTable.getColumn1());
            } else {
                connectionJdbcMB.non_query(""
                        + " UPDATE " + currentVariableTable + " SET "
                        + categoryColumnCode + " = '" + code + "', "
                        + categoryColumnName + " = '" + name + "' "
                        + " WHERE " + categoryColumnCode + " like '" + selectedRowDataTable.getColumn1() + "'");
            }
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "El nuevo registro se ha actualizado correctamente");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            loadVariableData(Integer.parseInt(currentVariableId));
            createDynamicTable();
        }
    }

    /**
     * Saves a record, but in turn makes the verification code and name exist,
     * otherwise sends an erros saying that these fields are mandatory.
     */
    public void saveRegistry() {
        boolean continueProcess = true;

        if (newName != null && newCode != null && newName.trim().length() != 0 && newCode.trim().length() != 0) {
            newName = newName.toUpperCase();
            newCode = newCode.toUpperCase();
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Codigo y nombre son obligatorios");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            continueProcess = false;
        }

        if (continueProcess) {
            try {
                String sqlName, sqlCode;
                ResultSet rs = connectionJdbcMB.consult(" SELECT * FROM " + currentVariableTable);
                sqlCode = "SELECT * FROM " + currentVariableTable + " WHERE " + rs.getMetaData().getColumnName(1) + "::text ilike '" + newCode + "'";
                sqlName = "SELECT * FROM " + currentVariableTable + " WHERE " + rs.getMetaData().getColumnName(2) + "::text ilike '" + newName + "'";

                rs = connectionJdbcMB.consult(sqlName);
                if (rs.next()) {
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe una categoria con el mismo nombre");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    continueProcess = false;
                } else {
                    rs = connectionJdbcMB.consult(sqlCode);
                    if (rs.next()) {
                        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe una categoria con el mismo codigo");
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                        continueProcess = false;
                    }
                }
            } catch (Exception e) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al validar los datos");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
        if (continueProcess) {//registrar los datos
            if (currentVariableTypePrimaryKey.compareTo("smallint") == 0 || currentVariableTypePrimaryKey.compareTo("integer") == 0) {
                connectionJdbcMB.non_query("INSERT INTO " + currentVariableTable + " VALUES (" + newCode + ",'" + newName + "')");
            } else {
                connectionJdbcMB.non_query("INSERT INTO " + currentVariableTable + " VALUES ('" + newCode + "','" + newName + "')");
            }
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "El nuevo registro se ha adicionado correctamente");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            loadVariableData(Integer.parseInt(currentVariableId));
            createDynamicTable();
        }
    }

    /**
     * Initializes the fields to add a new record.
     */
    public void newRegistry() {
        name = "";
        newName = "";
    }

    /**
     * Create a dynamic table with the results of a search.
     */
    public void createDynamicTable() {
        rowDataTableList = new ArrayList<>();
        selectedRowDataTable = null;
        btnEditDisabled = true;
        btnRemoveDisabled = true;
        try {
            String sql = " SELECT * FROM " + currentVariableTable;
            ResultSet rs = connectionJdbcMB.consult(sql);
            String column1Name = rs.getMetaData().getColumnName(1);
            String column2Name = rs.getMetaData().getColumnName(2);
            if (currentSearchValue.trim().length() != 0) {
                if (currentSearchCriteria == 1) {//filtrar por nombre
                    sql = sql + " WHERE " + column2Name + "::text ILIKE '%" + currentSearchValue + "%'";
                }
                if (currentSearchCriteria == 2) {//filtrar por codigo
                    sql = sql + " WHERE " + column1Name + "::text ILIKE '%" + currentSearchValue + "%'";
                }
            }
            rs = connectionJdbcMB.consult(sql);
            while (rs.next()) {
                rowDataTableList.add(new RowDataTable(rs.getString(1), rs.getString(2)));
            }
        } catch (Exception e) {
        }
    }

    public void reset() {
    }

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNewCode() {
        return newCode;
    }

    public void setNewCode(String newCode) {
        this.newCode = newCode;
    }

    public boolean isBtnEditDisabled() {
        return btnEditDisabled;
    }

    public void setBtnEditDisabled(boolean btnEditDisabled) {
        this.btnEditDisabled = btnEditDisabled;
    }

    public boolean isBtnRemoveDisabled() {
        return btnRemoveDisabled;
    }

    public void setBtnRemoveDisabled(boolean btnRemoveDisabled) {
        this.btnRemoveDisabled = btnRemoveDisabled;
    }

    public String getCurrentVariableName() {
        return currentVariableName;
    }

    public void setCurrentVariableName(String currentVariableName) {
        this.currentVariableName = currentVariableName;
    }

    public String getCurrentVariableNameXLSX() {
        return currentVariableNameXLSX;
    }

    public void setCurrentVariableNameXLSX(String currentVariableNameXLSX) {
        this.currentVariableNameXLSX = currentVariableNameXLSX;
    }

    public boolean isCodeDisabled() {
        return codeDisabled;
    }

    public void setCodeDisabled(boolean codeDisabled) {
        this.codeDisabled = codeDisabled;
    }
}
