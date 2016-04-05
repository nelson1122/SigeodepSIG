/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.closures;

import java.text.DecimalFormat;

/**
 * This class is used by the imputation class which is responsible for managing
 * everything related to data imputation.
 *
 * @author and
 */
public class Imputed {

    private DecimalFormat f;
    private int order;
    private String actual;
    private String predicted;
    private double confidence;
    private String tuple;

    public Imputed() {
        f = new DecimalFormat("##.00");
    }

    /**
     * @return the order
     */
    public int getOrder() {
        return order;
    }

    /**
     * @param order the order to set
     */
    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * @return the actual
     */
    public String getActual() {
        return actual;
    }

    /**
     * @param actual the actual to set
     */
    public void setActual(String actual) {
        this.actual = actual;
    }

    /**
     * @return the predicted
     */
    public String getPredicted() {
        return predicted;
    }

    /**
     * @param predicted the predicted to set
     */
    public void setPredicted(String predicted) {
        this.predicted = predicted;
    }

    /**
     * @return the confidence
     */
    public double getConfidence() {
        return confidence;
    }

    /**
     * @param confidence the confidence to set
     */
    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String getTuple() {
        return tuple;
    }

    public void setTuple(String tuple) {
        this.tuple = tuple;
    }

    /**
     * is a string representation
     *
     * @return
     */
    @Override
    public String toString() {
        return "# " + order
                + "\tActual: " + actual
                + "\tPredicted: " + predicted
                + "\tConfidence: " + f.format(confidence * 100) + "%"
                + "\tTuple: " + tuple;
    }
}
