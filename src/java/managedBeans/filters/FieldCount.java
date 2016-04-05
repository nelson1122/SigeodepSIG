/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.filters;

/**
 * This class manages the number of fields of a table.
 *
 * @author and
 */
public class FieldCount {

    private String field;
    private int count;

    /**
     * This method is the class constructor, receives as a parameter the field
     * and quantity.
     *
     * @param field
     * @param count
     */
    public FieldCount(String field, int count) {
        this.field = field;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
