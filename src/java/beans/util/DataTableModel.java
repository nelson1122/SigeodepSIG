/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.util;

import java.io.Serializable;
import java.util.List;
import javax.faces.model.ListDataModel;
import managedBeans.filters.FieldCount;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author and
 */
public class DataTableModel extends ListDataModel<FieldCount> implements SelectableDataModel<FieldCount>, Serializable {

    private List<FieldCount> model;

    public DataTableModel(List<FieldCount> model) {
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
