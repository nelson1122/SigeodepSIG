/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.configurations;

import beans.connection.ConnectionJdbcMB;
import beans.util.RowDataTable;
import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import model.dao.FormsFacade;
import model.dao.NonFatalDataSourcesFacade;
import model.pojo.Forms;
import model.pojo.NonFatalDataSources;

/**
 * he FormSourceMB class is responsible for managing everything related to the
 * sources of data for each record of the forms, enabling aggregated or
 * disaggregated sources that are available.
 *
 * @author SANTOS
 */
@ManagedBean(name = "formSourceMB")
@SessionScoped
public class FormSourceMB implements Serializable {

    @EJB
    FormsFacade formsFacade;
    @EJB
    NonFatalDataSourcesFacade nonFatalDataSourcesFacade;
    private ConnectionJdbcMB connectionJdbcMB;
    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    private List<Forms> formsList;
    private Forms currentForm;
    private String name = "";
    private String newName = "";
    private boolean btnEditDisabled = true;
    private List<String> availableSources = new ArrayList<>();
    private List<String> selectedAvailableSources = new ArrayList<>();
    private List<String> availableAddSources = new ArrayList<>();
    private List<String> selectedAvailableAddSources = new ArrayList<>();

    /**
     * It is the class constructor and is also responsible for connecting to the
     * database.
     */
    public FormSourceMB() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
    }

    /**
     * allows the user to load all related forms and also charges the sources
     * for a record.
     */
    public void load() {
        currentForm = null;
        if (selectedRowDataTable != null) {
            currentForm = formsFacade.find(selectedRowDataTable.getColumn1());
        }
        if (currentForm != null) {
            btnEditDisabled = false;

            if (currentForm.getFormName() != null) {
                name = currentForm.getFormName();
            } else {
                name = "";
            }
            //cargar las fuentes para este registro
            List<NonFatalDataSources> nfdsList = currentForm.getNonFatalDataSourcesList();
            availableAddSources = new ArrayList<>();
            for (int i = 0; i < nfdsList.size(); i++) {
                availableAddSources.add(nfdsList.get(i).getNonFatalDataSourceName());
            }
            //cargar fuentes de datos no adicionadas
            nfdsList = nonFatalDataSourcesFacade.findAll();
            availableSources = new ArrayList<>();
            boolean found;
            for (int i = 0; i < nfdsList.size(); i++) {
                found = false;
                for (int j = 0; j < availableAddSources.size(); j++) {
                    if (availableAddSources.get(j).compareTo(nfdsList.get(i).getNonFatalDataSourceName()) == 0) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    availableSources.add(nfdsList.get(i).getNonFatalDataSourceName());
                }
            }
        }
    }

    /**
     * Add a quadrant in a new neighborhood.
     */
    public void addSourceClick() {
        /*
         * adicionar un cuadrante en un nuevo barrio
         */
        if (selectedAvailableSources != null && !selectedAvailableSources.isEmpty()) {
            for (int i = 0; i < selectedAvailableSources.size(); i++) {
                //lo adiciono a la lista de agregados
                availableAddSources.add(selectedAvailableSources.get(i));
                //lo elimino de disponibles
                for (int j = 0; j < availableSources.size(); j++) {
                    if (availableSources.get(j).compareTo(selectedAvailableSources.get(i)) == 0) {
                        availableSources.remove(j);
                        break;
                    }
                }
            }
            selectedAvailableAddSources = new ArrayList<>();
        }
        //newQuadrantsFilter = "";
        //changeNewQuadrantsFilter();
    }

    /**
     * Remove one quadrant of the aggregate list when you are creating a new
     * neighborhood.
     */
    public void removeSourceClick() {
        /*
         * quitar un cuadrante de la lista de agregados, cuando se esta creando un nuevo barrio
         */
        if (selectedAvailableAddSources != null && !selectedAvailableAddSources.isEmpty()) {
            for (int i = 0; i < selectedAvailableAddSources.size(); i++) {
                //lo adiciono a la lista de disonibles
                availableSources.add(selectedAvailableAddSources.get(i));
                //lo elimino de agregados
                for (int j = 0; j < availableAddSources.size(); j++) {
                    if (availableAddSources.get(j).compareTo(selectedAvailableAddSources.get(i)) == 0) {
                        availableAddSources.remove(j);
                        break;
                    }
                }
            }
            selectedAvailableAddSources = new ArrayList<>();
        }
        //newQuadrantsFilter = "";
        //changeNewQuadrantsFilter();
    }

    /**
     * This method allows relating the sources indicated to the page and save
     * the update.
     */
    public void updateRegistry() {
        if (currentForm != null) {
            try {
                //se elimina las fuentes que tenga esta ficha
                connectionJdbcMB.non_query("DELETE FROM form_source WHERE form_id like '" + currentForm.getFormId() + "'");
                //se relaciona las fuentes indicadas a la ficha
                for (int j = 0; j < availableAddSources.size(); j++) {
                    ResultSet rs = connectionJdbcMB.consult(""
                            + " SELECT non_fatal_data_source_id "
                            + " FROM non_fatal_data_sources "
                            + " WHERE non_fatal_data_source_name ILIKE '" + availableAddSources.get(j) + "'");
                    if (rs.next()) {
                        connectionJdbcMB.non_query(""
                                + " INSERT INTO form_source "
                                + " VALUES ('"
                                + currentForm.getFormId() + "','"
                                + rs.getString(1)
                                + "')");
                    }
                }
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Registro actualizado");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } catch (Exception e) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar la fuente a editar");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    /**
     * Resets the values of the Dynamic Table.
     */
    public void reset() {
        rowDataTableList = new ArrayList<>();
        formsList = formsFacade.findAll();
        for (int i = 0; i < formsList.size(); i++) {
            rowDataTableList.add(new RowDataTable(
                    formsList.get(i).getFormId().toString(),
                    formsList.get(i).getFormName()));
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

    public List<String> getAvailableSources() {
        return availableSources;
    }

    public void setAvailableSources(List<String> availableSources) {
        this.availableSources = availableSources;
    }

    public List<String> getSelectedAvailableSources() {
        return selectedAvailableSources;
    }

    public void setSelectedAvailableSources(List<String> selectedAvailableSources) {
        this.selectedAvailableSources = selectedAvailableSources;
    }

    public List<String> getAvailableAddSources() {
        return availableAddSources;
    }

    public void setAvailableAddSources(List<String> availableAddSources) {
        this.availableAddSources = availableAddSources;
    }

    public List<String> getSelectedAvailableAddSources() {
        return selectedAvailableAddSources;
    }

    public void setSelectedAvailableAddSources(List<String> selectedAvailableAddSources) {
        this.selectedAvailableAddSources = selectedAvailableAddSources;
    }
}
