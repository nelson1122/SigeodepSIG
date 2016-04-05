/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.enumerators;

/**
 * This class handles enumerators related with indicator types
 *
 * @author SANTOS
 */
public enum IndicatorTypeEnum {

    count,
    percentage,
    variation,
    percentage_variation,
    average,
    rate,
    specified_rate,
    specified_percentage,
    NOVALUE;

    /**
     * converts a string to an enumerator, if not possible then returns NOVALUE
     *
     * @param str
     * @return
     */
    public static IndicatorTypeEnum convert(String str) {
        try {
            return valueOf(str);
        } catch (IllegalArgumentException ex) {
            return NOVALUE;
        }
    }
}
