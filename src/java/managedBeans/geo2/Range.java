/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.geo2;

import java.awt.Color;

/**
 * The Range class represents a range of numbers of the same type.
 *
 * @author and
 */
public class Range {

    private Double start;
    private Double end;
    private Color color;
    private String hexcolor;
    private String label;
    private Integer count;

    /**
     * This is the constructor of the class and is responsible for building a
     * new range.
     */
    public Range() {
        this.start = 0.0;
        this.end = 0.0;
        this.color = Color.white;
        this.label = "Sin dato.";
        this.count = 0;
    }

    /**
     * @return the start
     */
    public double getStart() {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(Double start) {
        this.start = start;
        setLabel();
    }

    /**
     * @return the end
     */
    public double getEnd() {
        return end;
    }

    /**
     * @param end the end to set
     */
    public void setEnd(Double end) {
        this.end = end;
        setLabel();
    }

    /**
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * @return the hexadecimal representation of color
     */
    public String getHexcolor() {
        return hexcolor;
    }

    /**
     * @param hexcolor the hexadecimal representation of color
     */
    public void setHexcolor(String hexcolor) {
        this.hexcolor = hexcolor;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    private void setLabel() {
        if (start.equals(end)) {
            this.label = "Igual a " + start;
        } else {
            this.label = "De " + start + " a " + end;
        }
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void increaseCount() {
        this.count++;
    }

    /**
     * It is a string of representation.
     *
     * @return
     */
    @Override
    public String toString() {
        String str;
        str = this.hexcolor + "<tab>"
                + this.start + "<->" + this.end + "<tab>"
                + this.label + "<tab>" + this.count + "<end>";
        return str;
    }
}
