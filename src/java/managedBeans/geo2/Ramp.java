/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.geo2;

/**
 * This class is responsible for everything related to the color and images that
 * will have the polygons.
 *
 * @author and
 */
import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

public class Ramp implements Serializable {

    private int number;
    private String name;
    private String image;
    private ArrayList<Color> colors;

    /**
     * class constructor also is responsible of initializing variables.
     *
     * @param number
     * @param name
     * @param image
     */
    public Ramp(int number, String name, String image) {
        this.number = number;
        this.name = name;
        this.image = image;
        this.colors = new ArrayList<>();
        this.colors.add(Color.GREEN);
        this.colors.add(Color.yellow);
        this.colors.add(Color.RED);
    }

    public ArrayList<Color> getColors() {
        return colors;
    }

    public void setColors(ArrayList<Color> colors) {
        this.colors = colors;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getStartColor() {
        return this.colors.get(0);
    }

    public Color getMiddleColor() {
        return this.colors.get(1);
    }

    public Color getEndColor() {
        return this.colors.get(2);
    }

    public void setColors(Color startColor, Color middleColor, Color endColor) {
        this.colors.clear();
        this.colors.add(startColor);
        this.colors.add(middleColor);
        this.colors.add(endColor);
    }

    public void setColors(Color startColor, Color endColor) {
        this.colors.clear();
        this.colors.add(startColor);
        this.colors.add(endColor);
    }

    /**
     * It is a string of representation.
     *
     * @return
     */
    @Override
    public String toString() {
        return this.name;
    }
}
