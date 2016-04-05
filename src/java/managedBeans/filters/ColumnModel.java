/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.filters;

/**
 * This class allows the software to set a header and a alias to a particular
 * column.
 *
 * @author and
 */
public class ColumnModel {

    private String header;
    private String alias;

    /**
     * This method is the class constructor, this method receives as a parameter
     * the header and alias.
     *
     * @param header
     * @param alias
     */
    public ColumnModel(String header, String alias) {
        this.header = header;
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }
}
