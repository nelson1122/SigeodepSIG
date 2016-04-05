/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.recordSets;

import beans.connection.ConnectionJdbcMB;
import beans.enumerators.FormsEnum;
import beans.util.RowDataTable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 * This class allows the system to handle the pagination in the tables in
 * recordset, allows to system to query the database to be realized each time
 * the user presses on the next, previous button or any position in the table to
 * not consume a lot of server memory.
 *
 * @author and
 */
public class LazyRecordSetsDataModel extends LazyDataModel<RowDataTable> {

    private List<RowDataTable> datasource;
    private ConnectionJdbcMB connection;
    private String sqlTags = "";
    private FormsEnum currentForm;
    private int rowCountAux = 0;

    public LazyRecordSetsDataModel(int rowCountAux, String sqlTags, FormsEnum form) {


        this.setRowCount(rowCountAux);
        this.rowCountAux = rowCountAux;
        this.sqlTags = sqlTags;
        this.currentForm = form;
        connection = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
        datasource = new ArrayList<RowDataTable>();//connection.getListFromQuery(first, pageSize);
    }

    @Override
    public void setRowIndex(int rowIndex) {
        /*
         * La siguiente es en el ancestro (LazyDataModel): This.rowIndex =
         * rowIndex == -1? rowIndex: (rowIndex pageSize%);
         */
        if (rowIndex == -1 || getPageSize() == 0) {
            super.setRowIndex(-1);
        } else {
            super.setRowIndex(rowIndex % getPageSize());
        }
    }

    @Override
    public RowDataTable getRowData(String rowKey) {
        for (int i = 0; i < datasource.size(); i++) {
            if (datasource.get(i).getColumn1().compareTo(rowKey) == 0) {
                return datasource.get(i);
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(RowDataTable row) {
        return row.getColumn1();
    }

    @Override
    public List<RowDataTable> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
        datasource = new ArrayList<>();
        try {
            this.setRowCount(rowCountAux);
            if (this.getRowCount() != 0) {
                ResultSet resultSet = connection.consult(" "
                        + sqlTags
                        + " LIMIT " + String.valueOf(pageSize)
                        + " OFFSET " + String.valueOf(first));
                switch (currentForm) {
                    case SCC_F_028:
                        while (resultSet.next()) {
                            datasource.add(connection.loadFatalInjuryMurderRecord(resultSet.getString(1)));
                        }
                        break;
                    case SCC_F_029:
                        while (resultSet.next()) {
                            datasource.add(connection.loadFatalInjuryTraafficRecord(resultSet.getString(1)));
                        }
                        break;
                    case SCC_F_030:
                        while (resultSet.next()) {
                            datasource.add(connection.loadFatalInjurySuicideRecord(resultSet.getString(1)));
                        }
                        break;
                    case SCC_F_031:
                        while (resultSet.next()) {
                            datasource.add(connection.loadFatalInjuryAccidentRecord(resultSet.getString(1)));
                        }
                        break;
                    case SCC_F_032:
                        while (resultSet.next()) {
                            datasource.add(connection.loadNonFatalInjuryRecord(resultSet.getString(1)));
                        }
                        break;
                    case SCC_F_033:
                        while (resultSet.next()) {
                            datasource.add(connection.loadNonFatalDomesticViolenceRecord(resultSet.getString(1)));
                        }
                        break;
                    case SIVIGILA_VIF:
                        while (resultSet.next()) {
                            datasource.add(connection.loadSivigilaVifRecord(resultSet.getString(1)));
                        }
                        break;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(LazyRecordSetsDataModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return datasource;
    }

    public List<RowDataTable> getDatasource() {
        return datasource;
    }

    public void setDatasource(List<RowDataTable> datasource) {
        this.datasource = datasource;
    }
}
