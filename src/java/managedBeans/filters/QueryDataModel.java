/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.filters;

import java.io.Serializable;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 * This class allows the system to handle the pagination in the tables, allows
 * the system to query the database to be realized each time the user presses on
 * the next, previous button or any position in the table to not consume a lot
 * of server memory.
 *
 */
public class QueryDataModel extends ListDataModel<FieldCount> implements SelectableDataModel<FieldCount>, Serializable {

    private List<FieldCount> model;

    public QueryDataModel(List<FieldCount> model) {
        super(model);
    }

    @Override
    public Object getRowKey(FieldCount record) {
        return record.getField();
    }

    @Override
    public FieldCount getRowData(String key) {
        for (FieldCount record : model) {
            if (record.getField().equals(key)) {
                return record;
            }
        }
        return null;
    }

    public List<FieldCount> getModel() {
        return model;
    }

    public void setModel(List<FieldCount> model) {
        this.model = model;
    }
}
