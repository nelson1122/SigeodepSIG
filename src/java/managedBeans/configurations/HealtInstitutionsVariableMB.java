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
import model.dao.NeighborhoodsFacade;
import model.dao.NonFatalDataSourcesFacade;
import model.pojo.Neighborhoods;
import model.pojo.NonFatalDataSources;
import org.apache.poi.hssf.usermodel.*;

/**
 * The HealtInstitutionsVariableMB class is responsible for managing everything
 * related to health institutions, allowing the user to edit, delete, add and
 * export the list of health institutions / receiver.
 *
 * @author SANTOS
 */
@ManagedBean(name = "healtInstitutionsVariableMB")
@SessionScoped
public class HealtInstitutionsVariableMB implements Serializable {

    /**
     * INSTITUCIONES DE SALUD(LCENF)
     */
    @EJB
    NeighborhoodsFacade neighborhoodsFacade;
    @EJB
    NonFatalDataSourcesFacade nonFatalDataSourcesFacade;
    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    private List<NonFatalDataSources> nonFatalDataSourcesList;
    private NonFatalDataSources currentNonFatalDataSources;
    private String nameInstitution = "";
    private String addressInstitution = "";
    private String neighborhoodInstitution = "";
    private Short typeInstitution = 0;
    private boolean receptorInstitution = true;
    private boolean healtInstitution = true;
    private String newNameInstitution = "";
    private String newAddressInstitution = "";
    private String newNeighborhoodInstitution = "";
    private Short newTypeInstitution = 0;
    private boolean newReceptorInstitution = true;
    private boolean newHealtInstitution = true;
    private ConnectionJdbcMB connectionJdbcMB;
    private boolean btnEditDisabled = true;
    private boolean btnRemoveDisabled = true;

    /**
     * class constructor, This method is responsible of connect to system to
     * database.
     */
    public HealtInstitutionsVariableMB() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
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
        nonFatalDataSourcesList = nonFatalDataSourcesFacade.findAll();
        for (int i = 0; i < nonFatalDataSourcesList.size(); i++) {
            row = sheet.createRow(i + 1);
            createCell(row, 0, nonFatalDataSourcesList.get(i).getNonFatalDataSourceId().toString());//CODIGO
            createCell(row, 1, nonFatalDataSourcesList.get(i).getNonFatalDataSourceName());//NOMBRE            
        }
    }

    /**
     * Suggests a name list of neighborhoods according to an initial letter to
     * then be added to a health institution.
     *
     * @param entered
     * @return
     */
    public List<String> suggestNeighborhoods(String entered) {
        List<Neighborhoods> neighborhoodsList = neighborhoodsFacade.findAll();
        List<String> list = new ArrayList<>();
        entered = entered.toUpperCase();
        int amount = 0;
        for (int i = 0; i < neighborhoodsList.size(); i++) {
            if (neighborhoodsList.get(i).getNeighborhoodName().startsWith(entered)) {
                list.add(neighborhoodsList.get(i).getNeighborhoodName());
                amount++;
            }
            if (amount == 10) {
                break;
            }
        }
        return list;
    }

    /**
     * Load the data belonging to a health facility.
     */
    public void load() {
        currentNonFatalDataSources = null;
        if (selectedRowDataTable != null) {
            currentNonFatalDataSources = nonFatalDataSourcesFacade.find(Short.parseShort(selectedRowDataTable.getColumn1()));
        }
        if (currentNonFatalDataSources != null) {
            btnEditDisabled = false;
            btnRemoveDisabled = false;
            if (currentNonFatalDataSources.getNonFatalDataSourceName() != null) {
                nameInstitution = currentNonFatalDataSources.getNonFatalDataSourceName();
            } else {
                nameInstitution = "";
            }
            if (currentNonFatalDataSources.getNonFatalDataSourceAddress() != null) {
                addressInstitution = currentNonFatalDataSources.getNonFatalDataSourceAddress();
            } else {
                addressInstitution = "";
            }
            if (currentNonFatalDataSources.getNonFatalDataSourceNeighborhoodId() != null && currentNonFatalDataSources.getNonFatalDataSourceNeighborhoodId().length() != 0) {
                neighborhoodInstitution = neighborhoodsFacade.find(Integer.parseInt(currentNonFatalDataSources.getNonFatalDataSourceNeighborhoodId())).getNeighborhoodName();
            } else {
                neighborhoodInstitution = "";
            }
            if (currentNonFatalDataSources.getNonFatalDataSourceType() != null) {
                typeInstitution = currentNonFatalDataSources.getNonFatalDataSourceType();
            } else {
                typeInstitution = 0;
            }

            if (currentNonFatalDataSources.getNonFatalDataSourceForm() != null) {
                typeInstitution = currentNonFatalDataSources.getNonFatalDataSourceType();
                if (currentNonFatalDataSources.getNonFatalDataSourceForm() == 1) {
                    healtInstitution = false;
                    receptorInstitution = true;
                } else if (currentNonFatalDataSources.getNonFatalDataSourceForm() == 2) {
                    healtInstitution = true;
                    receptorInstitution = false;
                } else {
                    healtInstitution = true;
                    receptorInstitution = true;
                }
            } else {
                healtInstitution = false;
                receptorInstitution = false;
            }
        }
    }

    /**
     * Deletes a category prior to verify that this is not associated with any
     * record.
     */
    public void deleteRegistry() {
        if (currentNonFatalDataSources != null) {
            if (currentNonFatalDataSources.getNonFatalDataSourceId() <= 77) {//hasta el 77 vienen las categorias por defecto
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se puede eliminar esta categoria por que existen registros que estan haciendo uso de esta");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                try {
                    String sql = " DELETE FROM non_fatal_data_sources WHERE non_fatal_data_source_id::text like '" + selectedRowDataTable.getColumn1() + "'";
                    connectionJdbcMB.setShowMessages(false);
                    connectionJdbcMB.non_query(sql);
                    connectionJdbcMB.setShowMessages(true);
                    if (connectionJdbcMB.getMsj().startsWith("ERROR")) {
                        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se puede eliminar esta categoria por que existen registros que estan haciendo uso de esta");
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                    } else {
                        currentNonFatalDataSources = null;
                        selectedRowDataTable = null;
                        createDynamicTable();
                        btnEditDisabled = true;
                        btnRemoveDisabled = true;
                        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "El registro fue eliminado");
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                    }
                } catch (Exception e) {
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se puede eliminar esta categoria por que existen registros que estan haciendo uso de esta");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
            }
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar la categoria a eliminar");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    /**
     * Updates the information in a record and verify that there is no
     * duplication of information to be added.
     */
    public void updateRegistry() {
        boolean continueProcess = true;
        if (currentNonFatalDataSources == null) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se ha seleccionado la categoria a editar");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            continueProcess = false;
        }

        if (continueProcess) {
            if (nameInstitution.trim().length() == 0) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe digitar un nombre");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                continueProcess = false;
            }
            if (receptorInstitution == false && healtInstitution == false) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe indicar si la institucion es: 'RECEPTORA', 'SALUD' o ambas");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                continueProcess = false;
            }
        }
        if (continueProcess) {//determinar si el nombre ya existe
            try {
                ResultSet rs = connectionJdbcMB.consult(""
                        + " SELECT * "
                        + " FROM non_fatal_data_sources "
                        + " WHERE non_fatal_data_source_name ilike '" + nameInstitution + "' "
                        + " AND non_fatal_data_source_id != " + currentNonFatalDataSources.getNonFatalDataSourceId());
                if (rs.next()) {
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El nombre ya fue registrado, se debe digitar uno diferente");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    continueProcess = false;
                }
            } catch (Exception e) {
            }
        }

        if (continueProcess) {
            nameInstitution = nameInstitution.toUpperCase();
            currentNonFatalDataSources.setNonFatalDataSourceName(nameInstitution);
            currentNonFatalDataSources.setNonFatalDataSourceAddress(addressInstitution);
            currentNonFatalDataSources.setNonFatalDataSourceType(typeInstitution);
            try {
                if (neighborhoodInstitution != null && neighborhoodInstitution.length() != 0) {
                    currentNonFatalDataSources.setNonFatalDataSourceNeighborhoodId(neighborhoodsFacade.findByName(neighborhoodInstitution).getNeighborhoodId().toString());
                } else {
                    currentNonFatalDataSources.setNonFatalDataSourceNeighborhoodId(null);
                }
            } catch (Exception e) {
                currentNonFatalDataSources.setNonFatalDataSourceNeighborhoodId(null);
            }
            if (receptorInstitution == true && healtInstitution == true) {
                currentNonFatalDataSources.setNonFatalDataSourceForm((short) 3);
            } else if (healtInstitution == true) {
                currentNonFatalDataSources.setNonFatalDataSourceForm((short) 2);
            } else {
                currentNonFatalDataSources.setNonFatalDataSourceForm((short) 1);
            }
            nonFatalDataSourcesFacade.edit(currentNonFatalDataSources);
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Registro actualizado");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            currentNonFatalDataSources = null;
            selectedRowDataTable = null;
            createDynamicTable();
            btnEditDisabled = true;
            btnRemoveDisabled = true;
        }
    }

    /**
     * Save a new record, also evaluates or makes a check that the name of the
     * new record does not exist.
     */
    public void saveRegistry() {
        boolean continueProcess = true;

        if (newNameInstitution.trim().length() == 0) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe digitar un nombre");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            continueProcess = false;
        }

        if (newReceptorInstitution == false && newHealtInstitution == false) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe indicar si la institucion es: 'RECEPTORA', 'SALUD' o ambas");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            continueProcess = false;
        }

        if (continueProcess) {//determinar si el nombre ya existe
            try {
                ResultSet rs = connectionJdbcMB.consult(""
                        + " SELECT * "
                        + " FROM non_fatal_data_sources "
                        + " WHERE non_fatal_data_source_name ilike '" + newNameInstitution + "' ");
                if (rs.next()) {
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El nombre ya fue registrado, se debe digitar uno diferente");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    continueProcess = false;
                }
            } catch (Exception e) {
            }
        }

        if (continueProcess) {
            int max = nonFatalDataSourcesFacade.findMax() + 1;
            newNameInstitution = newNameInstitution.toUpperCase();
            NonFatalDataSources newRegistry = new NonFatalDataSources((short) max, newNameInstitution);
            newRegistry.setNonFatalDataSourceAddress(newAddressInstitution);
            newRegistry.setNonFatalDataSourceType(newTypeInstitution);

            if (newReceptorInstitution == true && newHealtInstitution == true) {
                newRegistry.setNonFatalDataSourceForm((short) 3);
            } else if (newHealtInstitution == true) {
                newRegistry.setNonFatalDataSourceForm((short) 2);
            } else {
                newRegistry.setNonFatalDataSourceForm((short) 1);
            }

            try {
                if (newNeighborhoodInstitution != null && newNeighborhoodInstitution.length() != 0) {
                    newRegistry.setNonFatalDataSourceNeighborhoodId(neighborhoodsFacade.findByName(newNeighborhoodInstitution).getNeighborhoodId().toString());
                } else {
                    newRegistry.setNonFatalDataSourceNeighborhoodId(null);
                }
            } catch (Exception e) {
                newRegistry.setNonFatalDataSourceNeighborhoodId(null);
            }

            nonFatalDataSourcesFacade.create(newRegistry);

            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Nuevo registro almacenado");
            FacesContext.getCurrentInstance().addMessage(null, msg);

            currentNonFatalDataSources = null;
            selectedRowDataTable = null;
            createDynamicTable();
            btnEditDisabled = true;
            btnRemoveDisabled = true;
            newRegistry();
        }
    }

    /**
     * Initializes the fields to add a new record.
     */
    public void newRegistry() {
        nameInstitution = "";
        addressInstitution = "";
        typeInstitution = 0;
        neighborhoodInstitution = "";
        newNameInstitution = "";
        receptorInstitution = true;
        healtInstitution = true;
        newReceptorInstitution = true;
        newHealtInstitution = true;
    }

    /**
     * Create a dynamic table with the results of a search.
     */
    public void createDynamicTable() {
        btnEditDisabled = true;
        btnRemoveDisabled = true;
        currentNonFatalDataSources = null;
        selectedRowDataTable = null;
        if (currentSearchValue.trim().length() == 0) {
            reset();
        } else {
            currentSearchValue = currentSearchValue.toUpperCase();
            rowDataTableList = new ArrayList<>();
            ResultSet rs;
            try {

                if (currentSearchCriteria == 2) {
                    rs = connectionJdbcMB.consult("select * from non_fatal_data_sources where non_fatal_data_source_name like '%" + currentSearchValue + "%'");
                } else {
                    rs = connectionJdbcMB.consult("select * from non_fatal_data_sources where non_fatal_data_source_id::text like '%" + currentSearchValue + "%'");
                }
                while (rs.next()) {
                    rowDataTableList.add(new RowDataTable(rs.getString("non_fatal_data_source_id"), rs.getString("non_fatal_data_source_name")));
                }
            } catch (SQLException ex) {
            }
            if (rowDataTableList.isEmpty()) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "SIN DATOS", "No existen resultados para esta busqueda");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }

    /**
     * Resets the values of the Dynamic Table.
     */
    public void reset() {
        rowDataTableList = new ArrayList<>();
        nonFatalDataSourcesList = nonFatalDataSourcesFacade.findAll();
        for (int i = 0; i < nonFatalDataSourcesList.size(); i++) {
            rowDataTableList.add(new RowDataTable(
                    nonFatalDataSourcesList.get(i).getNonFatalDataSourceId().toString(),
                    nonFatalDataSourcesList.get(i).getNonFatalDataSourceName()));
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

    public String getAddressInstitution() {
        return addressInstitution;
    }

    public void setAddressInstitution(String addressInstitution) {
        this.addressInstitution = addressInstitution;
    }

    public String getNameInstitution() {
        return nameInstitution;
    }

    public void setNameInstitution(String nameInstitution) {
        this.nameInstitution = nameInstitution;
    }

    public String getNeighborhoodInstitution() {
        return neighborhoodInstitution;
    }

    public void setNeighborhoodInstitution(String neighborhoodInstitution) {
        this.neighborhoodInstitution = neighborhoodInstitution;
    }

    public String getNewAddressInstitution() {
        return newAddressInstitution;
    }

    public void setNewAddressInstitution(String newAddressInstitution) {
        this.newAddressInstitution = newAddressInstitution;
    }

    public String getNewNameInstitution() {
        return newNameInstitution;
    }

    public void setNewNameInstitution(String newNameInstitution) {
        this.newNameInstitution = newNameInstitution;
    }

    public String getNewNeighborhoodInstitution() {
        return newNeighborhoodInstitution;
    }

    public void setNewNeighborhoodInstitution(String newNeighborhoodInstitution) {
        this.newNeighborhoodInstitution = newNeighborhoodInstitution;
    }

    public Short getNewTypeInstitution() {
        return newTypeInstitution;
    }

    public void setNewTypeInstitution(Short newTypeInstitution) {
        this.newTypeInstitution = newTypeInstitution;
    }

    public Short getTypeInstitution() {
        return typeInstitution;
    }

    public void setTypeInstitution(Short typeInstitution) {
        this.typeInstitution = typeInstitution;
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

    public boolean isReceptorInstitution() {
        return receptorInstitution;
    }

    public void setReceptorInstitution(boolean receptorInstitution) {
        this.receptorInstitution = receptorInstitution;
    }

    public boolean isHealtInstitution() {
        return healtInstitution;
    }

    public void setHealtInstitution(boolean healtInstitution) {
        this.healtInstitution = healtInstitution;
    }

    public boolean isNewReceptorInstitution() {
        return newReceptorInstitution;
    }

    public void setNewReceptorInstitution(boolean newReceptorInstitution) {
        this.newReceptorInstitution = newReceptorInstitution;
    }

    public boolean isNewHealtInstitution() {
        return newHealtInstitution;
    }

    public void setNewHealtInstitution(boolean newHealtInstitution) {
        this.newHealtInstitution = newHealtInstitution;
    }
}
