/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.enumerators;

/**
 * This class handles enumerators related with forms
 *
 * @author SANTOS
 */
public enum FormsEnum {

    SCC_F_028,
    SCC_F_029,
    SCC_F_030,
    SCC_F_031,
    SCC_F_032,
    SCC_F_033,
    SIVIGILA_VIF,
    NOVALUE;

    /**
     * converts a string to an enumerator, if not possible then returns NOVALUE
     *
     * @param str
     * @return
     */
    public static FormsEnum convert(String str) {
        try {
            return valueOf(str);
        } catch (IllegalArgumentException ex) {
            return NOVALUE;
        }
    }
}
