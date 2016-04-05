/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.geo2;

import java.awt.Color;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * The class RangeFactory is responsible of the creation and account of the
 * ranges that are established.
 *
 * @author and
 */
@ManagedBean
@SessionScoped
public class RangeFactory {

    private final static int KMEANS_METHOD = 3;
    private final static int FREQUENCY_METHOD = 2;
    private final static int JENKS_METHOD = 1;
    private final static int EQUAL_METHOD = 0;
    private int bins;
    private int splitMethod;
    private ArrayList<Double> numbers;
    private ArrayList<Range> ranges;
    private Color startColor;
    private Color middleColor;
    private Color endColor;
    private Ramp selectedRamp;
    private ArrayList<Ramp> ramps;

    /**
     * Class constructor and initiates variables.
     */
    public RangeFactory() {
        this.bins = 3;
        this.splitMethod = RangeFactory.FREQUENCY_METHOD;
        this.numbers = new ArrayList<>();
        this.ranges = new ArrayList<>();
        this.selectedRamp = RampConverter.rampDB.get(0);
        this.startColor = Color.GREEN;
        this.middleColor = Color.YELLOW;
        this.endColor = Color.RED;
        ramps = RampConverter.rampDB;
    }

    /**
     * Class constructor.
     *
     * @param params
     */
    public RangeFactory(String params) {
        System.out.println(params);
        String[] txt_ranges = params.split("<end>");
        this.bins = txt_ranges.length;
        this.ranges = new ArrayList<>();
        for (String txt_range : txt_ranges) {
            String[] txt_attr = txt_range.split("<tab>");
            Range range = new Range();
            range.setHexcolor(txt_attr[0]);
            String[] start_and_end = txt_attr[1].split("<->");
            range.setStart(new Double(start_and_end[0]));
            range.setEnd(new Double(start_and_end[1]));
            range.setLabel(txt_attr[2]);
            range.setCount(Integer.parseInt(txt_attr[3]));
            this.ranges.add(range);
        }
    }

    /**
     * This method walking the values of a array assigned number density of
     * element to each range.
     *
     * @return
     */
    public ArrayList<Range> createRanges() {
        ranges.clear();
        NaturalBreaks nb = new NaturalBreaks();
        nb.setNumbers(getNumbers());
        ArrayList<String> breaks;
        if (getSplitMethod() == 0) {
            breaks = nb.getEqualBreaks(bins);
        } else if (getSplitMethod() == 1) {
            breaks = nb.getJenksBreaks(bins);
        } else if (getSplitMethod() == 2) {
            breaks = nb.getEqualFrequency(bins);
        } else if (getSplitMethod() == 3) {
            breaks = nb.getKMeansBreaks(bins);
        } else {
            breaks = nb.getEqualFrequency(bins);
        }
        ColorRamp ramp = new ColorRamp();
        ArrayList<String> rampText;
        if (selectedRamp.getColors().size() == 2) {
            rampText = ramp.createBiRamp(selectedRamp.getStartColor(), selectedRamp.getMiddleColor(), getBins());
        } else {
            rampText = ramp.createTriRamp(selectedRamp.getStartColor(), selectedRamp.getMiddleColor(), selectedRamp.getEndColor(), getBins());
        }
        ArrayList<Color> rampColor;
        rampColor = ramp.getMyRamp();
        for (int i = 0; i < this.getBins(); i++) {
            Range range = new Range();
            String[] start_and_end = breaks.get(i).split(";");
            range.setStart(new Double(start_and_end[0]));
            range.setEnd(new Double(start_and_end[1]));
            range.setColor(rampColor.get(i));
            range.setHexcolor(rampText.get(i));
            ranges.add(range);
        }
        setCounts();
        return ranges;
    }

    /**
     * This method is responsible for perform a recount of the ranges
     */
    public void setCounts() {
        for (Double number : numbers) {
            for (Range range : ranges) {
                if (number <= range.getEnd()) {
                    range.increaseCount();
                    break;
                }
            }
        }
    }

    /**
     * Returns the color by the relative position on the scale with a determined
     * "resolution".
     *
     * @param value
     * @return
     */
    public String getColorByValue(Double value) {
        for (Iterator<Range> it = getRanges().iterator(); it.hasNext();) {
            Range range = it.next();
            if (value <= range.getEnd()) {
                return range.getHexcolor();
            }
        }
        return "FFFFFF";
    }

    public void printRanges() {
        System.out.println(this.toString());
    }

    /**
     * This method is responsible for export ranges
     *
     * @return
     */
    public String exportRanges() {
        StringBuilder sb = new StringBuilder();
        for (Iterator<Range> it = getRanges().iterator(); it.hasNext();) {
            Range range = it.next();
            sb.append(range.toString());
        }
        return sb.toString();
    }

    /**
     * It is a string of representation.
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Iterator<Range> it = getRanges().iterator(); it.hasNext();) {
            Range range = it.next();
            String line = range.toString().replaceAll("<tab>", "\t").replaceAll("<->", " : ").replaceAll("<end>", "\n");
            sb.append(line);
        }
        return sb.toString();
    }

    /**
     * @return the bins
     */
    public int getBins() {
        return bins;
    }

    /**
     * @param bins the bins to set
     */
    public void setBins(int bins) {
        this.bins = bins;
    }

    /**
     * @return the splitMethod
     */
    public int getSplitMethod() {
        return splitMethod;
    }

    /**
     * @param splitMethod the splitMethod to set
     */
    public void setSplitMethod(int splitMethod) {
        this.splitMethod = splitMethod;
    }

    /**
     * @return the numbers
     */
    public ArrayList<Double> getNumbers() {
        return numbers;
    }

    /**
     * @param numbers the numbers to set
     */
    public void setNumbers(ArrayList<Double> numbers) {
        this.numbers = numbers;
    }

    /**
     * @return the ranges
     */
    public ArrayList<Range> getRanges() {
        return ranges;
    }

    /**
     * @param ranges the ranges to set
     */
    public void setRanges(ArrayList<Range> ranges) {
        this.ranges = ranges;
    }

    /**
     * @return the startColor
     */
    public Color getStartColor() {
        return startColor;
    }

    /**
     * @param startColor the startColor to set
     */
    public void setStartColor(Color startColor) {
        this.startColor = startColor;
    }

    /**
     * @return the middleColor
     */
    public Color getMiddleColor() {
        return middleColor;
    }

    /**
     * @param middleColor the middleColor to set
     */
    public void setMiddleColor(Color middleColor) {
        this.middleColor = middleColor;
    }

    /**
     * @return the endColor
     */
    public Color getEndColor() {
        return endColor;
    }

    /**
     * @param endColor the endColor to set
     */
    public void setEndColor(Color endColor) {
        this.endColor = endColor;
    }

    public Ramp getSelectedRamp() {
        return selectedRamp;
    }

    public void setSelectedRamp(Ramp selectedRamp) {
        this.selectedRamp = selectedRamp;
    }

    public ArrayList<Ramp> getRamps() {
        return ramps;
    }

    public void setRamps(ArrayList<Ramp> ramps) {
        this.ramps = ramps;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
        File file = new File("/home/and/serie.csv");
        FileReader reader = new FileReader(file);
        ArrayList<Double> numbers;
        try (BufferedReader br = new BufferedReader(reader)) {
            String line;
            numbers = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                numbers.add(new Double(line));
            }
        }
        Collections.sort(numbers);
        int bins = 6;
        RangeFactory rf = new RangeFactory();
        rf.setBins(bins);
        rf.setNumbers(numbers);
        rf.setSplitMethod(RangeFactory.KMEANS_METHOD);
        rf.createRanges();
        rf.printRanges();
        rf.setSplitMethod(RangeFactory.JENKS_METHOD);
        rf.createRanges();
        rf.printRanges();
        rf.setSplitMethod(RangeFactory.EQUAL_METHOD);
        rf.createRanges();
        rf.printRanges();
        rf.setSplitMethod(RangeFactory.FREQUENCY_METHOD);
        rf.createRanges();
        rf.printRanges();

    }
}
