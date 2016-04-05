/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.geo2;

/**
 * This class is a utility class which allows other classes to find ramped and
 * interpolated color values.
 *
 * @author and
 */
/*
 * Licensed under LGPL v. 2.1 or any later version; see GNU LGPL for details.
 * Original Author: Frank Hardisty
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * This class is a utility class which allows other classes to find ramped and
 * interpolated color values.
 */
public class ColorRamp {

    private ArrayList<Color> MyRamp;
    private transient Color lowColor; // associated with low values
    private transient Color highColor; // associated with high values
    static final Logger logger = Logger.getLogger(ColorRamp.class.getName());

    public ColorRamp() {
        lowColor = Color.white;
        highColor = Color.red;
        MyRamp = new ArrayList<>(100);
    }

    public void rampInts(int[] intList, boolean[] anchored) {
        if (intList == null || intList.length < 1) {
            return; // nothing do do
        }
        int lowInt = 0;
        int highInt = Integer.MAX_VALUE;
        anchored[0] = true;
        anchored[intList.length - 1] = true;
        // here we walk through and ramp based on the anchored colors
        int currLowSwatch = 0;
        int currHighSwatch = 0;
        for (int j = 0; j < intList.length - 1; j++) {
            // find a locked color
            if (anchored[j]) {
                lowInt = (intList[j]);
                currLowSwatch = j;
                // look for the next anchored color
                for (int k = j + 1; k < intList.length; k++) {
                    if (anchored[k]) {
                        highInt = (intList[k]);
                        currHighSwatch = k;
                        // if there are any colors in between
                        int numSwatches = currHighSwatch - currLowSwatch - 1;
                        if (numSwatches > 0) {
                            int currSwat = 1;
                            for (int l = currLowSwatch + 1; l < currHighSwatch; l++) {
                                float prop;
                                prop = currSwat / ((float) numSwatches + 1);
                                currSwat++;
                                float diff = highInt - lowInt;
                                float newVal = diff * prop + lowInt;

                                intList[l] = Math.round(newVal);
                            }// next l
                        } // if swatches > 0
                        // currLowSwatch = k;
                        break;// found that anchor, don't find more.
                    }// end if k locked

                }// next k
            }// end if j locked
        }// next j

        // this.lowColor = (colorList[0]);//????

    }

    public void rampColors(Color[] colorList, boolean[] anchored) {
        if (colorList == null || colorList.length < 1) {
            return; // nothing do do
        }

        anchored[0] = true;
        anchored[colorList.length - 1] = true;
        // here we walk through and ramp based on the anchored colors
        int currLowSwatch = 0;
        int currHighSwatch = 0;
        for (int j = 0; j < colorList.length - 1; j++) {
            // find a locked color
            if (anchored[j]) {
                lowColor = (colorList[j]);
                currLowSwatch = j;
                // look for the next anchored color
                for (int k = j + 1; k < colorList.length; k++) {
                    if (anchored[k]) {
                        highColor = (colorList[k]);
                        currHighSwatch = k;
                        // if there are any colors in between
                        int numSwatches = currHighSwatch - currLowSwatch - 1;
                        if (numSwatches > 0) {
                            int currSwat = 1;
                            for (int l = currLowSwatch + 1; l < currHighSwatch; l++) {
                                double prop;
                                prop = currSwat / ((double) numSwatches + 1);
                                currSwat++;
                                Color back = new Color(getRampedValueRGB(prop));
                                colorList[l] = back;
                            }// next l
                        } // if swatches > 0
                        // currLowSwatch = k;
                        break;// found that anchor, don't find more.
                    }// end if k locked

                }// next k
            }// end if j locked
        }// next j

        lowColor = (colorList[0]);// ????

    }

    public void rampColors(Color[] colorList) {
        boolean[] anchors = new boolean[colorList.length];
        anchors[0] = true;
        anchors[colorList.length - 1] = true;
        this.rampColors(colorList, anchors);
    }

    public int getRampedValueRGB(double prop) {
        // hack for hsv
        // int i = 0;
        // if (i == 0){
        // return this.getRampedValueHSB(prop);
        // }
        if (highColor == null || lowColor == null) {
            logger.info("hit null color");
            return Color.black.getRGB();
        }
        int redRange = highColor.getRed() - lowColor.getRed();
        int newRed = (int) Math.round(prop * redRange);
        newRed = newRed + lowColor.getRed();

        int GreenRange = highColor.getGreen() - lowColor.getGreen();
        int newGreen = (int) Math.round(prop * GreenRange);
        newGreen = newGreen + lowColor.getGreen();

        int BlueRange = highColor.getBlue() - lowColor.getBlue();
        int newBlue = (int) Math.round(prop * BlueRange);
        newBlue = newBlue + lowColor.getBlue();

        int intARGB = (255 << 24) | (newRed << 16) | (newGreen << 8)
                | (newBlue << 0);
        return intARGB;
    }

    public int getRampedValueHSB(double aProp) {
        float prop = (float) aProp;
        float[] lowVals;
        float[] highVals;

        lowVals = Color.RGBtoHSB(lowColor.getRed(), lowColor.getGreen(),
                lowColor.getBlue(), null);
        highVals = Color.RGBtoHSB(highColor.getRed(), highColor.getGreen(),
                highColor.getBlue(), null);

        float hueRange = highVals[0] - lowVals[0];
        float newHue = prop * hueRange;
        newHue = newHue + lowVals[0];

        float saturationRange = highVals[1] - lowVals[1];
        float newSaturation = prop * saturationRange;
        newSaturation = newSaturation + lowVals[1];

        float brightnessRange = highVals[2] - lowVals[2];
        float newBrightness = prop * brightnessRange;
        newBrightness = newBrightness + lowVals[2];

        int intARGB = Color.HSBtoRGB(newHue, newSaturation, newBrightness);

        return intARGB;
    }

    public int getRampedValueHB(double aProp) {
        float prop = (float) aProp;
        float[] lowVals;
        float[] highVals;
        lowVals = Color.RGBtoHSB(lowColor.getRed(), lowColor.getGreen(),
                lowColor.getBlue(), null);
        highVals = Color.RGBtoHSB(highColor.getRed(), highColor.getGreen(),
                highColor.getBlue(), null);

        float hueRange = highVals[0] - lowVals[0];
        float newHue = prop * hueRange;
        newHue = newHue + lowVals[0];

        float brightnessRange = highVals[2] - lowVals[2];
        float newBrightness = prop * brightnessRange;
        newBrightness = newBrightness + lowVals[2];

        int intARGB = Color.HSBtoRGB(newHue, 1.0f, newBrightness);

        return intARGB;
    }

    public ArrayList<String> createBiRamp(Color start, Color end, double bins) {
        ArrayList<String> scale = new ArrayList<>((int) bins);
        ColorRamp ramp = new ColorRamp();
        Color[] colors = new Color[2];
        colors[0] = start;
        colors[1] = end;
        ramp.rampColors(colors);
        for (double i = 0; i <= bins; i++) {
            Color back = new Color(ramp.getRampedValueRGB(i / bins));
            MyRamp.add(back);
            String hexString = Integer.toHexString(back.getRGB() & 0x00FFFFFF);
            while (hexString.length() < 6) {
                hexString = "0" + hexString;
            }
            scale.add(hexString);
        }
        return scale;
    }

    public ArrayList<String> createTriRamp(Color start, Color middle, Color end, double bins) {
        double limit1 = Math.ceil(bins / 2) - 1.0;
        double limit2 = bins - limit1 - 1.0;

        ArrayList<String> scale = new ArrayList<>((int) bins);
        ColorRamp ramp = new ColorRamp();
        Color[] colors = new Color[2];
        colors[0] = start;
        colors[1] = middle;
        ramp.rampColors(colors);
        for (double i = 0; i <= limit1; i++) {
            Color back = new Color(ramp.getRampedValueRGB(i / limit1));
            MyRamp.add(back);
            String hexString = Integer.toHexString(back.getRGB() & 0x00FFFFFF);
            while (hexString.length() < 6) {
                hexString = "0" + hexString;
            }
            scale.add(hexString);
        }
        colors[0] = middle;
        colors[1] = end;
        ramp.rampColors(colors);
        for (double i = 1; i <= limit2; i++) {
            Color back = new Color(ramp.getRampedValueRGB(i / limit2));
            MyRamp.add(back);
            String hexString = Integer.toHexString(back.getRGB() & 0x00FFFFFF);
            while (hexString.length() < 6) {
                hexString = "0" + hexString;
            }
            scale.add(hexString);
        }
        return scale;
    }

    public ArrayList<Color> getMyRamp() {
        return MyRamp;
    }

    /**
     * Main method for testing.
     */
    public static void main(String[] args) {
        JFrame app = new JFrame();
        app.getContentPane().setLayout(new BorderLayout());

        // make color ramp
        JPanel rampPan = new JPanel();
        rampPan.setLayout(new FlowLayout());
        int bins = 25;
        JPanel[] panSet = new JPanel[bins];
        ColorRamp ramp = new ColorRamp();
        ArrayList<String> cramp = ramp.createTriRamp(Color.RED, Color.WHITE, Color.BLUE, bins);
        //ArrayList<String> cramp = ramp.createBiRamp(Color.yellow, Color.RED, bins);
        System.out.println(cramp);

        for (int i = 0; i < panSet.length; i++) {
            panSet[i] = new JPanel();
            panSet[i].setPreferredSize(new Dimension(25, 25));
            panSet[i].setBackground(ramp.getMyRamp().get(i));
            rampPan.add(panSet[i]);
        }
        app.getContentPane().add(rampPan, BorderLayout.SOUTH);

        // app.getContentPane().add(setColorsPan);

        app.pack();
        app.setVisible(true);
    }
}