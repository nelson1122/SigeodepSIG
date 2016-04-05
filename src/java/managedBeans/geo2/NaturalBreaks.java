/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.geo2;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.trees.J48;
import weka.clusterers.SimpleKMeans;
import weka.core.*;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;

/**
 * The class NaturalBreaks uses a statistical formula to determine natural
 * clusters of attribute values. This attempts to minimize the variance within a
 * class and to maximize the variance between classes.
 *
 * @author and
 */
public class NaturalBreaks {

    private ArrayList<Double> breaks = new ArrayList<>();
    private ArrayList<Double> numbers = new ArrayList<>();
    private boolean integerType = true;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
        GeoDBConnection geo = new GeoDBConnection();
        ArrayList<Double> numbers = geo.getTestNumbers();
        Collections.sort(numbers);
        int bins = 5;
        NaturalBreaks nb;
        nb = new NaturalBreaks();
        nb.setNumbers(numbers);
        ArrayList equalBreaks = nb.getEqualBreaks(bins);
        System.out.println(equalBreaks + "\n" + equalBreaks.size());
        ArrayList jenksBreaks = nb.getJenksBreaks(bins);
        System.out.println(jenksBreaks + "\n" + jenksBreaks.size());
        ColorRamp ramp = new ColorRamp();
        ArrayList<String> createTriRamp = ramp.createTriRamp(Color.GREEN, Color.yellow, Color.RED, bins);
        System.out.println(createTriRamp);
    }

    /**
     * This method performs a grouping unrestricted of a set given data element
     * in a given number of groups.
     *
     * @param numclass
     * @return
     */
    public ArrayList<String> getJenksBreaks(int numclass) {
        breaks.clear();
        ArrayList<Double> list = numbers;
        int numdata = list.size();
        double[][] mat1 = new double[numdata + 1][numclass + 1];
        double[][] mat2 = new double[numdata + 1][numclass + 1];
        double[] st = new double[numdata];

        for (int i = 1; i <= numclass; i++) {
            mat1[1][i] = 1;
            mat2[1][i] = 0;
            for (int j = 2; j <= numdata; j++) {
                mat2[j][i] = Double.MAX_VALUE;
            }
        }
        double v = 0;
        for (int l = 2; l <= numdata; l++) {
            double s1 = 0;
            double s2 = 0;
            double w = 0;
            for (int m = 1; m <= l; m++) {
                int i3 = l - m + 1;
                double val = ((Double) list.get(i3 - 1)).doubleValue();
                s2 += val * val;
                s1 += val;
                w++;
                v = s2 - (s1 * s1) / w;
                int i4 = i3 - 1;
                if (i4 != 0) {
                    for (int j = 2; j <= numclass; j++) {
                        if (mat2[l][j] >= (v + mat2[i4][j - 1])) {
                            mat1[l][j] = i3;
                            mat2[l][j] = v + mat2[i4][j - 1];
                        }
                    }
                }
            }
            mat1[l][1] = 1;
            mat2[l][1] = v;
        }
        int k = numdata;
        int[] kclass = new int[numclass];
        kclass[numclass - 1] = list.size() - 1;
        for (int j = numclass; j >= 2; j--) {
            //System.out.println("rank = " + mat1[k][j]);
            int id = (int) (mat1[k][j]) - 2;
            //System.out.println("val = " + list.get(id));
            getBreaks().add((Double) list.get(id));
            kclass[j - 2] = id;
            k = (int) mat1[k][j] - 1;
        }
        Collections.reverse(getBreaks());
        //System.out.println(this.completeRanges());
        return this.completeRanges();
    }

    /**
     * This method is reponsible of find the significance that exist between
     * classes.
     *
     * @param bins
     * @return
     */
    public ArrayList<String> getKMeansBreaks(int bins) {
        try {
            breaks.clear();
            Attribute num1 = new Attribute("num1");
            ArrayList<Attribute> attributes = new ArrayList<>();
            attributes.add(num1);
            Instances dataset = new Instances("Test-dataset", attributes, numbers.size());
            for (Double number : numbers) {
                double[] values = new double[dataset.numAttributes()];
                values[0] = number;
                Instance inst = new DenseInstance(1.0, values);
                dataset.add(inst);
            }
            dataset.compactify();
            String[] options = new String[2];
            options[0] = "-N";
            options[1] = "" + bins;
            SimpleKMeans clusterer = new SimpleKMeans();
            clusterer.setOptions(options);
            clusterer.buildClusterer(dataset);
            Instances centroids = clusterer.getClusterCentroids();
            centroids.sort(0);
            ArrayList<Double> prebreaks = new ArrayList<>(bins);
            for (Instance centroid : centroids) {
                prebreaks.add(centroid.value(0));
            }
            for (int i = 0; i < prebreaks.size() - 1; i++) {
                Double value = (prebreaks.get(i) + prebreaks.get(i + 1)) / 2;
                breaks.add(Math.floor(value));
            }
            return this.completeRanges();
        } catch (Exception ex) {
            Logger.getLogger(NaturalBreaks.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * This method allows obtain the division of the variation in the data by
     * the number of the desired rating.
     *
     * @param bins
     * @return
     */
    public ArrayList<String> getEqualBreaks(int bins) {
        try {
            breaks.clear();
            if (bins == 1) {
                return this.completeRanges();
            }
            Attribute num1 = new Attribute("num1");
            ArrayList<Attribute> attributes = new ArrayList<>();
            attributes.add(num1);
            Instances dataset = new Instances("Test-dataset", attributes, numbers.size());
            for (Double number : numbers) {
                double[] values = new double[dataset.numAttributes()];
                values[0] = number;
                Instance inst = new DenseInstance(1.0, values);
                dataset.add(inst);
            }
            dataset.compactify();
            String[] options = Utils.splitOptions("-unset-class-temporarily -B " + bins + " -M -1.0 -R 1");
            Discretize discretize = new Discretize();
            discretize.setOptions(options);
            discretize.setInputFormat(dataset);
            Instances newData = Filter.useFilter(dataset, discretize); // apply filter
            Attribute attribute = newData.attribute(0);
            String ranges_txt = attribute.toString();
            ranges_txt = ranges_txt.replace("\\", "").replace("'", "").replace("{", "").replace("}", "").replace("@attribute num1 ", "").replace("(-inf", Collections.min(numbers).toString()).replace("(", "").replace("]", "");
            String[] split = ranges_txt.split(",");
            for (String range : split) {
                Double value = Double.parseDouble(range.split("-")[0]);
                breaks.add(Math.floor(value));
            }
            breaks.remove(0);
            return this.completeRanges();
        } catch (Exception ex) {
            Logger.getLogger(NaturalBreaks.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * This method allows obtain the same frequency between class.
     *
     * @param bins
     * @return
     */
    public ArrayList<String> getEqualFrequency(int bins) {
        try {
            breaks.clear();
            if (bins == 1) {
                return this.completeRanges();
            }
            Attribute num1 = new Attribute("num1");
            ArrayList<Attribute> attributes = new ArrayList<>();
            attributes.add(num1);
            Instances dataset = new Instances("Test-dataset", attributes, numbers.size());
            for (Double number : numbers) {
                double[] values = new double[dataset.numAttributes()];
                values[0] = number;
                Instance inst = new DenseInstance(1.0, values);
                dataset.add(inst);
            }
            dataset.compactify();
            String[] options = Utils.splitOptions("-unset-class-temporarily -F -B " + bins + " -M -1.0 -R 1");
            Discretize discretize = new Discretize();
            discretize.setOptions(options);
            discretize.setInputFormat(dataset);
            Instances newData = Filter.useFilter(dataset, discretize); // apply filter
            Attribute attribute = newData.attribute(0);
            String ranges_txt = attribute.toString();
            ranges_txt = ranges_txt.replace("\\", "").replace("'", "").replace("{", "").replace("}", "").replace("@attribute num1 ", "").replace("(-inf", Collections.min(numbers).toString()).replace("(", "").replace("]", "");
            String[] split = ranges_txt.split(",");
            for (String range : split) {
                Double value = Double.parseDouble(range.split("-")[0]);
                breaks.add(Math.floor(value));
            }
            breaks.remove(0);
            return this.completeRanges();
        } catch (Exception ex) {
            Logger.getLogger(NaturalBreaks.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * This method is responsible of that the established range are complete.
     *
     * @return
     */
    public ArrayList<String> completeRanges() {
        ArrayList<String> ranges = new ArrayList<>();
        ArrayList<Double> copy = (ArrayList<Double>) breaks.clone();
        Double min = Collections.min(numbers);
        copy.add(0, min);
        Double max = Collections.max(numbers);
        copy.add(max);
        int stop = copy.size() - 1;
        for (int i = 0; i < stop; i++) {
            ranges.add(copy.get(i) + ";" + copy.get(i + 1));
            if (this.isIntegerType()) {
                copy.set(i + 1, copy.get(i + 1) + 1);
            }
        }
        return ranges;
    }

    /**
     * @return the breaks
     */
    public ArrayList<Double> getBreaks() {
        return breaks;
    }

    /**
     * @param breaks the breaks to set
     */
    public void setBreaks(ArrayList<Double> breaks) {
        this.breaks = breaks;
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

    public boolean isIntegerType() {
        return integerType;
    }

    public void setIntegerType(boolean integerType) {
        this.integerType = integerType;
    }
}
