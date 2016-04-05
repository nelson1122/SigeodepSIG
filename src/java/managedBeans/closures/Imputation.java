/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.closures;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;
import weka.filters.unsupervised.attribute.StringToNominal;

/**
 * Imputation is a class that is responsible for conducting the process of
 * replacing null values to non-zero values.
 *
 * @author santos
 */
public class Imputation {

    private StringToNominal stringToNominal;
    private String strings;
    private NumericToNominal numericToNominal;
    private String numerics;
    private int classIndex;
    private Instances train;
    private Instances predict;
    private ArrayList<Imputed> J48Prediction;
    private ArrayList<Imputed> KNNPrediction;

    /**
     * This method is responsible for replacing null values for non-zero values.
     */
    public Imputation() {
        stringToNominal = new StringToNominal();
        numericToNominal = new NumericToNominal();
        strings = "";
        numerics = "";
    }

    /**
     * This method is responsible of establishing the files not null that are
     * prepared for the imputation by model. This type of imputation is realize
     * when the percentage of null is greater than 10% and less than or equal to
     * 33%.
     *
     * @param to_train
     */
    public void setTrainingSet(String to_train) {
        try {
            train = ConverterUtils.DataSource.read(to_train);
            train = this.filterAttributeTypes(getTrain());
        } catch (Exception ex) {
            Logger.getLogger(Imputation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Sets the class index of the set.
     *
     * @param theClass
     */
    public void setClassIndex(String theClass) {
        classIndex = getTrain().attribute(theClass).index();
        getTrain().setClassIndex(getClassIndex()); //O-index based
    }

    /**
     * This method is responsible of establishing the files null that are used
     * to predict the result of the imputation by model this type of imputation
     * is realize when the percentage of null is greater than 10% and less than
     * or equal to 33%.
     *
     * @param to_predict
     */
    public void setPredictionSet(String to_predict) {
        try {
            predict = ConverterUtils.DataSource.read(to_predict);
            predict = this.filterAttributeTypes(getPredict());
            getPredict().deleteAttributeAt(getClassIndex());
            getPredict().insertAttributeAt(getTrain().attribute(getClassIndex()), getClassIndex());
            getPredict().setClassIndex(getClassIndex());
            J48Prediction = new ArrayList<>(getPredict().numInstances());
            KNNPrediction = new ArrayList<>(getPredict().numInstances());
        } catch (Exception ex) {
            Logger.getLogger(Imputation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<Imputed> runJ48Prediction() {
        try {
            String[] options = new String[2];
            options[0] = "-C 0.5";
            int M = (int) Math.round(getTrain().numInstances() * 0.1);
            options[1] = "-M " + M;
            J48 tree = new J48();

            tree.setOptions(options);
            tree.buildClassifier(getTrain());

            for (int i = 0; i < getPredict().numInstances(); i++) {
                double pred = tree.classifyInstance(getPredict().instance(i));
                double[] dist = tree.distributionForInstance(getPredict().instance(i));
                Imputed imputed = new Imputed();
                imputed.setOrder(i + 1);
                // print the actual value
                imputed.setActual(getPredict().instance(i).toString(getPredict().classIndex()));
                // print the prediceted value
                imputed.setPredicted(getPredict().classAttribute().value((int) pred));
                // print the confidence of the prediction
                imputed.setConfidence(dist[(int) pred]);
                getJ48Prediction().add(imputed);
            }
        } catch (Exception ex) {
            Logger.getLogger(Imputation.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return getJ48Prediction();
        }
    }

    /**
     * It is the function that is using the algorithm of neighbors.
     *
     * @return
     */
    public ArrayList<Imputed> runKNNPrediction() {
        try {
            IBk knn = new IBk(1);
            knn.buildClassifier(getTrain());

            for (int i = 0; i < getPredict().numInstances(); i++) {
                double pred = knn.classifyInstance(getPredict().instance(i));
                Instance returned = knn.getNearestNeighbourSearchAlgorithm().nearestNeighbour(getPredict().instance(i));
                //System.out.println(returned);//+"--"
                double[] dist = knn.distributionForInstance(getPredict().instance(i));
                Imputed imputed = new Imputed();

                imputed.setTuple(returned.toString());//colocar la tupla correspondiente

                imputed.setOrder(i + 1);
                // print the actual value
                imputed.setActual(getPredict().instance(i).toString(getPredict().classIndex()));
                // print the prediceted value
                imputed.setPredicted(getPredict().classAttribute().value((int) pred));
                // print the confidence of the prediction
                imputed.setConfidence(dist[(int) pred]);
                getKNNPrediction().add(imputed);
            }
        } catch (Exception ex) {
            Logger.getLogger(Imputation.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return getKNNPrediction();
        }
    }

    /**
     * This method is responsible of establishing the files null and not null
     * that are prepared and used to predict the result of the imputation by
     * model this type of imputation is realize when the percentage of null is
     * greater than 10% and less than or equal to 33%.
     *
     * @param data
     * @return
     * @throws Exception
     */
    private Instances filterAttributeTypes(Instances data) throws Exception {
        for (int i = 0; i < data.numAttributes(); i++) {
            if (data.attribute(i).isString()) {
                strings += (i + 1) + ",";
            }
            if (data.attribute(i).isNumeric()) {
                numerics += (i + 1) + ",";
            }
        }
        if (!strings.isEmpty()) {
            stringToNominal.setAttributeRange(strings.substring(0, strings.length() - 1));
            stringToNominal.setInputFormat(data);
            data = Filter.useFilter(data, stringToNominal);
        }
        if (!numerics.isEmpty()) {
            numericToNominal.setAttributeIndices(numerics.substring(0, numerics.length() - 1));
            numericToNominal.setInputFormat(data);
            data = Filter.useFilter(data, numericToNominal);
        }
        return data;
    }

    /**
     * @return the classIndex
     */
    public int getClassIndex() {
        return classIndex;
    }

    /**
     * @return the train
     */
    public Instances getTrain() {
        return train;
    }

    /**
     * @return the predict
     */
    public Instances getPredict() {
        return predict;
    }

    /**
     * @return the J48Prediction
     */
    public ArrayList<Imputed> getJ48Prediction() {
        return J48Prediction;
    }

    /**
     * @return the KNNPrediction
     */
    public ArrayList<Imputed> getKNNPrediction() {
        return KNNPrediction;
    }
}
