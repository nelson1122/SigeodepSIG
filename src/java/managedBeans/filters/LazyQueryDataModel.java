/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.filters;

import beans.connection.ConnectionJdbcMB;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 * This class allows the system to handle the pagination in the tables, allows
 * to system to query the database to be realized each time the user presses on
 * the next, previous button or any position in the table to not consume a lot
 * of server memory.
 *
 */
public class LazyQueryDataModel extends LazyDataModel<List> {

    private List<List> datasource;
    private ConnectionJdbcMB connection;
    private int pojectId = -1;

    /**
     * This is the constructor method of the class, this method receives as
     * parameter the identify of the project for which the user is working.
     *
     * @param pojectId
     */
    public LazyQueryDataModel(int pojectId) {
        this.pojectId = pojectId;
        connection = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
        datasource = connection.getListFromQuery(0, 10, pojectId);
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
    public List getRowData(String rowKey) {
        for (List list : datasource) {
            if (list.get(0).equals(rowKey)) {
                return list;
            }
        }

        return null;
    }

    @Override
    public Object getRowKey(List list) {
        return list.get(0);
    }

    @Override
    public List<List> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {

        datasource = connection.getListFromQuery(first, pageSize, this.pojectId);
        int dataSize = connection.getTempRowCount(this.pojectId);
        this.setRowCount(dataSize);
        return datasource;
    }
}
