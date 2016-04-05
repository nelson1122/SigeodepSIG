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
import model.dao.CountriesFacade;
import model.pojo.Countries;
import org.apache.poi.hssf.usermodel.*;

/**
 * The CountriesVariableMB class is responsible for managing everything related
 * to countries, allowing user to have available a list of countries available
 * which can be added, edited and deleted.
 *
 * @author SANTOS
 */
@ManagedBean(name = "countriesVariableMB")
@SessionScoped
public class CountriesVariableMB implements Serializable {

    /**
     * PAISES
     */
    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    @EJB
    CountriesFacade countriesFacade;
    private List<Countries> countriesList;
    private Countries currentCountry;
    private String name = "";//Nombre del barrio.
    private String newName = "";//Nombre del barrio.
    private boolean btnEditDisabled = true;
    private boolean btnRemoveDisabled = true;
    private ConnectionJdbcMB connectionJdbcMB;

    /**
     * This method is the class constructor.
     */
    public CountriesVariableMB() {
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
     * runs a xls file where the user insert a row within a worksheet where two
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
        countriesList = countriesFacade.findAll();
        for (int i = 0; i < countriesList.size(); i++) {
            row = sheet.createRow(i + 1);
            createCell(row, 0, countriesList.get(i).getIdCountry().toString());//CODIGO
            createCell(row, 1, countriesList.get(i).getName());//NOMBRE            
        }
    }

    /**
     * This method is responsible for loading the required values when required
     * to handle countries.
     */
    public void load() {
        currentCountry = null;
        if (selectedRowDataTable != null) {
            currentCountry = countriesFacade.find(Short.parseShort(selectedRowDataTable.getColumn1()));
        }
        if (currentCountry != null) {
            btnEditDisabled = false;
            btnRemoveDisabled = false;
            if (currentCountry.getName() != null) {
                name = currentCountry.getName();
            } else {
                name = "";
            }
        }
    }

    /**
     * Deletes a selected record.
     */
    public void deleteRegistry() {
        if (currentCountry != null) {
            countriesFacade.remove(currentCountry);
            currentCountry = null;
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
        if (currentCountry != null) {
            if (name.trim().length() != 0) {
                name = name.toUpperCase();
                currentCountry.setName(name);
                countriesFacade.edit(currentCountry);
                name = "";
                currentCountry = null;
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
     * This method is responsible to save a new record
     */
    public void saveRegistry() {
        //determinar consecutivo
        if (newName.trim().length() != 0) {
            int max = countriesFacade.findMax() + 1;
            newName = newName.toUpperCase();
            Countries newRegistry = new Countries((short) max);
            newRegistry.setName(newName);
            countriesFacade.create(newRegistry);
            newName = "";
            currentCountry = null;
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
     * Create a dynamic table with the results of a search.
     */
    public void createDynamicTable() {
        if (currentSearchValue.trim().length() == 0) {
            reset();
        } else {
            currentSearchValue = currentSearchValue.toUpperCase();
            rowDataTableList = new ArrayList<>();
            ResultSet rs;
            String type;
            try {

                if (currentSearchCriteria == 2) {
                    rs = connectionJdbcMB.consult("select * from countries where name like '%" + currentSearchValue + "%'");
                } else {
                    rs = connectionJdbcMB.consult("select * from countries where id_country::text like '%" + currentSearchValue + "%'");
                }
                while (rs.next()) {
                    rowDataTableList.add(new RowDataTable(rs.getString("id_country"), rs.getString("name")));
                }
            } catch (SQLException ex) {
            }
        }
    }

    /**
     * Resets the values of the Dynamic Table.
     */
    public void reset() {
        rowDataTableList = new ArrayList<>();
        countriesList = countriesFacade.findAll();
        for (int i = 0; i < countriesList.size(); i++) {
            rowDataTableList.add(new RowDataTable(
                    countriesList.get(i).getIdCountry().toString(),
                    countriesList.get(i).getName()));
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

    public Countries getCurrentCountry() {
        return currentCountry;
    }

    public void setCurrentCountry(Countries currentCountry) {
        this.currentCountry = currentCountry;
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
