/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.configurations;

import beans.connection.ConnectionJdbcMB;
import beans.util.RowDataTable;
import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;

/**
 * The CategoricalVariablesMB class is responsible for handling categorical
 * variables, which refer to the different characteristics that are managed by
 * the observatory.
 *
 * @author SANTOS
 */
@ManagedBean(name = "categoricalVariablesMB")
@SessionScoped
public class CategoricalVariablesMB implements Serializable {

    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    private String currentSearchValue = "";
    private ConnectionJdbcMB connectionJdbcMB;
    private GenericVariableMB genericVariableMB;
    private QuadrantsVariableMB quadrantsVariableMB;
    private HealtInstitutionsVariableMB healtInstitutionsVariableMB;//non_fatal_data_sources
    private NeighborhoodsVariableMB neighborhoodsVariableMB;
    private MunicipalitiesVariableMB municipalitiesVariableMB;
    private DepartamentsVariableMB departamentsVariableMB;
    private CountriesVariableMB countriesVariableMB;
    private CorridorsVariableMB corridorsVariableMB;
    private CommunesVariableMB communesVariableMB;
    private FormSourceMB formSourceMB;
    private Short currentInjury = 0;
    private SelectItem[] injuries;

    /**
     * It is responsible for making a connection to the database and also load
     * those variables that contain the various injuries this makes using the
     * filter.
     */
    public CategoricalVariablesMB() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
        genericVariableMB = (GenericVariableMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{genericVariableMB}", GenericVariableMB.class);
        quadrantsVariableMB = (QuadrantsVariableMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{quadrantsVariableMB}", QuadrantsVariableMB.class);
        healtInstitutionsVariableMB = (HealtInstitutionsVariableMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{healtInstitutionsVariableMB}", HealtInstitutionsVariableMB.class);
        neighborhoodsVariableMB = (NeighborhoodsVariableMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{neighborhoodsVariableMB}", NeighborhoodsVariableMB.class);
        municipalitiesVariableMB = (MunicipalitiesVariableMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{municipalitiesVariableMB}", MunicipalitiesVariableMB.class);
        departamentsVariableMB = (DepartamentsVariableMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{departamentsVariableMB}", DepartamentsVariableMB.class);
        countriesVariableMB = (CountriesVariableMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{countriesVariableMB}", CountriesVariableMB.class);
        corridorsVariableMB = (CorridorsVariableMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{corridorsVariableMB}", CorridorsVariableMB.class);
        communesVariableMB = (CommunesVariableMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{communesVariableMB}", CommunesVariableMB.class);
        formSourceMB = (FormSourceMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{formSourceMB}", FormSourceMB.class);
    }

    /**
     * Displays a page depending on the variable you have selected.
     */
    public void open() {
        /*
         * cargar una pagina dependiendo de la variable que se haya seleccionado          
         */
        try {
            ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
            String ctxPath = ((ServletContext) ctx.getContext()).getContextPath();
            if (selectedRowDataTable != null) {
                int variableId = Integer.parseInt(selectedRowDataTable.getColumn1());
                switch (variableId) {
                    case 8://COMUNAS
                        communesVariableMB.reset();
                        ctx.redirect(ctxPath + "/faces/web/configurations/communes.xhtml");
                        break;
                    case 10://CORREDORES
                        corridorsVariableMB.reset();
                        ctx.redirect(ctxPath + "/faces/web/configurations/corridors.xhtml");
                        break;
                    case 11://PAISES
                        countriesVariableMB.reset();
                        ctx.redirect(ctxPath + "/faces/web/configurations/countries.xhtml");
                        break;
                    case 12://DEPARTAMENTOS
                        departamentsVariableMB.reset();
                        ctx.redirect(ctxPath + "/faces/web/configurations/departaments.xhtml");
                        break;
                    case 16://FUENTES DE DATOS PARA CADA FICHA
                        formSourceMB.reset();
                        ctx.redirect(ctxPath + "/faces/web/configurations/formSource.xhtml");
                        break;
                    case 23://MUNICIPIOS
                        municipalitiesVariableMB.reset();
                        ctx.redirect(ctxPath + "/faces/web/configurations/municipalities.xhtml");
                        break;
                    case 25://BARRIOS
                        neighborhoodsVariableMB.reset();
                        ctx.redirect(ctxPath + "/faces/web/configurations/neighborhoods.xhtml");
                        break;
                    case 26://INSTITUCIONES DE SALUD / RECEPTORAS                        
                        healtInstitutionsVariableMB.reset();
                        ctx.redirect(ctxPath + "/faces/web/configurations/healtInstitutions.xhtml");
                        break;
                    case 33://CUADRANTES
                        quadrantsVariableMB.reset();
                        ctx.redirect(ctxPath + "/faces/web/configurations/quadrants.xhtml");
                        break;
                    default://LAS DEMAS VARIABLES (GENERICAS)
                        genericVariableMB.loadVariableData(variableId);
                        ctx.redirect(ctxPath + "/faces/web/configurations/genericVariable.xhtml");
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se ha selecciondo la variable categorica que desea gestionar."));
            }
        } catch (IOException | NumberFormatException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo redireccionar a la pagina correspondiente"));
        }
    }

    /**
     * create a dynamic table where the data is loaded variable that was
     * selected and stored in a list.
     */
    public void createDynamicTable() {
        selectedRowDataTable = null;
        rowDataTableList = new ArrayList<>();
        try {
            String sql = ""
                    + " SELECT * "
                    + " FROM categorical_variables ";
            if (currentSearchValue.trim().length() != 0) {
                sql = sql + " WHERE categorical_variable_name ILIKE '%" + currentSearchValue + "%'";
            }
            if (currentInjury != 0) {
                if (sql.indexOf("WHERE") != -1) {
                    sql = sql + " AND categorical_variable_injuries LIKE '%" + currentInjury + "%'";
                } else {
                    sql = sql + " WHERE categorical_variable_injuries LIKE '%" + currentInjury + "%'";
                }
            }
            sql = sql + " ORDER BY categorical_variable_name ASC ";
            ResultSet rs = connectionJdbcMB.consult(sql);
            while (rs.next()) {
                rowDataTableList.add(new RowDataTable(
                        rs.getString(1), //id
                        rs.getString(2), //name
                        rs.getString(3), //table
                        rs.getString(4), //type_primary_key
                        rs.getString(5), //page
                        rs.getString(6)));  //default_id
            }
        } catch (Exception e) {
        }
    }

    /**
     * Resets the fields with the values of the selected variable.
     */
    public void reset() {
        currentSearchValue = "";
        injuries = new SelectItem[12];
        injuries[0] = new SelectItem(0, "Todos");
        injuries[1] = new SelectItem(10, "HOMICIDIO");
        injuries[2] = new SelectItem(11, "MUERTE EN ACCIDENTE DE TRANSITO");
        injuries[3] = new SelectItem(12, "SUICIDIO");
        injuries[4] = new SelectItem(13, "MUERTE ACCIDENTAL");
        injuries[5] = new SelectItem(50, "VIOLENCIA INTERPERSONAL");
        injuries[6] = new SelectItem(51, "LESION EN ACCIDENTE DE TRANSITO");
        injuries[7] = new SelectItem(52, "INTENCIONAL AUTOINFLINGIDA");
        injuries[8] = new SelectItem(53, "VIOLENCIA INTRAFAMILIAR");
        injuries[9] = new SelectItem(54, "NO INTENCIONAL");
        injuries[10] = new SelectItem(55, "VIOLENCIA INTRAFAMILIAR LCENF");
        injuries[11] = new SelectItem(56, "SIVIGILA-VIF");
        currentInjury = 0;
        rowDataTableList = new ArrayList<>();
        selectedRowDataTable = null;
        try {
            ResultSet rs = connectionJdbcMB.consult(""
                    + " SELECT * "
                    + " FROM categorical_variables "
                    + " ORDER BY categorical_variable_name ASC");
            while (rs.next()) {
                rowDataTableList.add(new RowDataTable(
                        rs.getString(1), //id
                        rs.getString(2), //name
                        rs.getString(3), //table
                        rs.getString(4), //type_primary_key
                        rs.getString(5), //page
                        rs.getString(6)));  //default_id
            }
        } catch (Exception e) {
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

    public String getCurrentSearchValue() {
        return currentSearchValue;
    }

    public void setCurrentSearchValue(String currentSearchValue) {
        this.currentSearchValue = currentSearchValue;
    }

    public Short getCurrentInjury() {
        return currentInjury;
    }

    public void setCurrentInjury(Short currentInjury) {
        this.currentInjury = currentInjury;
    }

    public SelectItem[] getInjuries() {
        return injuries;
    }

    public void setInjuries(SelectItem[] injuries) {
        this.injuries = injuries;
    }
}
