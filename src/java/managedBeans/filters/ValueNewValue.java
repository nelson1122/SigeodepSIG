/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.filters;

/**
 * This class is used to give a better structure and management to the classes
 * who use it, the clases are ErrorsControlMB and FilterMB.
 *
 */
public class ValueNewValue {

    private String columnName;
    private String oldValue;
    private String newValue;

    public ValueNewValue(String oldValue, String newValue) {
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public ValueNewValue(String columnName, String oldValue, String newValue) {
        this.columnName = columnName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
}
