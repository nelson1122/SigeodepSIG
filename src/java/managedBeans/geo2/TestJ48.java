/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.geo2;

import java.io.FileNotFoundException;
import java.io.IOException;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/**
 *
 * @author santos
 */
public class TestJ48 {

    public static void main(String[] args) throws FileNotFoundException, IOException, Exception {
        // TODO code application logic here
        Instances data;
        data = DataSource.read("/tmp/ejemplo.csv");
        String[] options = new String[1];
        options[0] = "-C 0.75";
        options[0] = "-M 150";
        data.setClassIndex(3);
        System.out.println(data.get(0).classIndex());
        J48 tree = new J48();
        tree.setOptions(options);
        tree.buildClassifier(data);
        System.out.println(tree.toString());
    }
}
