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
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import model.dao.DepartamentsFacade;
import model.pojo.Departaments;
import org.apache.poi.hssf.usermodel.*;

/**
 * The DepartamentsVariableMB class is responsible for managing everything
 * related departments, allowing user to have available a list of apartments
 * available which can be added, edited, deleted and exported.
 *
 * @author SANTOS
 */
@ManagedBean(name = "departamentsVariableMB")
@SessionScoped
public class DepartamentsVariableMB implements Serializable {

    /**
     * DEPARTAMENTOS
     */
    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    @EJB
    DepartamentsFacade departamentsFacade;
    private List<Departaments> departamentsList;
    private Departaments currentDepartament;
    private String name = "";
    private String newName = "";
    private boolean btnEditDisabled = true;
    private boolean btnRemoveDisabled = true;
    private ConnectionJdbcMB connectionJdbcMB;

    /**
     * This method is the class constructor.
     */
    public DepartamentsVariableMB() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
    }

    /**
     * It is responsible for creating a cell within the row.
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
     * It is responsible for creating a cell within the row.
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
        departamentsList = departamentsFacade.findAll();
        for (int i = 0; i < departamentsList.size(); i++) {
            row = sheet.createRow(i + 1);
            createCell(row, 0, departamentsList.get(i).getDepartamentId().toString());//CODIGO
            createCell(row, 1, departamentsList.get(i).getDepartamentName());//NOMBRE            
        }
    }

    /**
     * This method is responsible for loading the required values when required
     * to manage departments.
     */
    public void load() {
        currentDepartament = null;
        if (selectedRowDataTable != null) {
            currentDepartament = departamentsFacade.find(Short.parseShort(selectedRowDataTable.getColumn1()));
        }
        if (currentDepartament != null) {
            btnEditDisabled = false;
            btnRemoveDisabled = false;
            if (currentDepartament.getDepartamentName() != null) {
                name = currentDepartament.getDepartamentName();
            } else {
                name = "";
            }
        }
    }

    /**
     * This method is responsible to delete a selected record.
     */
    public void deleteRegistry() {
        if (currentDepartament != null) {
            departamentsFacade.remove(currentDepartament);
            currentDepartament = null;
            selectedRowDataTable = null;
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "El registro fue eliminado");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        createDynamicTable();
        btnEditDisabled = true;
        btnRemoveDisabled = true;
    }

    /**
     * This method allows to update a record.
     */
    public void updateRegistry() {
        //determinar consecutivo
        if (currentDepartament != null) {
            if (name.trim().length() != 0) {
                name = name.toUpperCase();
                currentDepartament.setDepartamentName(name);
                departamentsFacade.edit(currentDepartament);
                name = "";
                currentDepartament = null;
                selectedRowDataTable = null;
                createDynamicTable();
                btnEditDisabled = true;
                btnRemoveDisabled = true;
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Registro actualizado");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "SIN NOMBRE", "Se debe digitar un nombre");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }

    }

    /**
     * This method is responsible to save a new record.
     */
    public void saveRegistry() {
        //determinar consecutivo
        if (newName.trim().length() != 0) {
            int max = departamentsFacade.findMax() + 1;
            newName = newName.toUpperCase();
            Departaments newRegistry = new Departaments((short) max, newName);
            departamentsFacade.create(newRegistry);
            newName = "";
            currentDepartament = null;
            selectedRowDataTable = null;
            createDynamicTable();
            btnEditDisabled = true;
            btnRemoveDisabled = true;
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Nuevo registro almacenado");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "SIN NOMBRE", "Se debe digitar un nombre");
            FacesContext.getCurrentInstance().addMessage(null, msg);
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
     * Create a Dynamic table with the results of a search.
     */
    public void createDynamicTable() {
        boolean s = true;
        if (currentSearchValue.trim().length() == 0) {
            reset();
        } else {
            currentSearchValue = currentSearchValue.toUpperCase();
            rowDataTableList = new ArrayList<>();
            ResultSet rs;
            try {
                if (currentSearchCriteria == 2) {
                    rs = connectionJdbcMB.consult("select * from departaments where departament_name like '%" + currentSearchValue + "%'");
                } else {
                    rs = connectionJdbcMB.consult("select * from departaments where departament_id::text like '%" + currentSearchValue + "%'");
                }
                while (rs.next()) {
                    rowDataTableList.add(new RowDataTable(rs.getString("departament_id"), rs.getString("departament_name")));
                }
            } catch (SQLException ex) {
            }
            if (rowDataTableList.isEmpty()) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "SIN DATOS", "No existen resultados para esta busqueda");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
//            departamentsList = departamentsFacade.findCriteria(currentSearchCriteria, currentSearchValue);
//            if (departamentsList.isEmpty()) {
//                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "SIN DATOS", "No existen resultados para esta busqueda");
//                FacesContext.getCurrentInstance().addMessage(null, msg);
//            }
//            for (int i = 0; i < departamentsList.size(); i++) {
//                rowDataTableList.add(new RowDataTable(
//                        departamentsList.get(i).getDepartamentId().toString(),
//                        departamentsList.get(i).getDepartamentName()));
//            }
        }
    }

    /**
     * Resets the values of the DynamicTable.
     */
    public void reset() {
        rowDataTableList = new ArrayList<RowDataTable>();
        departamentsList = departamentsFacade.findAll();
        for (int i = 0; i < departamentsList.size(); i++) {
            rowDataTableList.add(new RowDataTable(
                    departamentsList.get(i).getDepartamentId().toString(),
                    departamentsList.get(i).getDepartamentName()));
        }
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
}
