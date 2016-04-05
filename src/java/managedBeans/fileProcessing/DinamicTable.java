/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.fileProcessing;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The DinamicTable class is responsible for making everything about creating
 * dynamic tables if needed.
 *
 * @author santos
 */
public class DinamicTable implements Serializable {

    private ArrayList<ArrayList<String>> listOfRecords = new ArrayList<>();
    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<String> titles2 = new ArrayList<>();

    /**
     * Class constructor.
     */
    public DinamicTable() {
    }

    /**
     * establishes a pivot table where a list of files is displayed.
     *
     * @param listOfRecords
     * @param titles
     */
    public DinamicTable(ArrayList<ArrayList<String>> listOfRecords, ArrayList<String> titles) {
        this.listOfRecords = listOfRecords;
        this.titles = titles;
    }

    /**
     * establishes a dynamic table where a file list is displayed
     *
     * @param listOfRecords
     * @param titles
     * @param titles2
     */
    public DinamicTable(ArrayList<ArrayList<String>> listOfRecords, ArrayList<String> titles, ArrayList<String> titles2) {
        this.listOfRecords = listOfRecords;
        this.titles = titles;
        this.titles2 = titles2;
    }

    public ArrayList<String> getTitles() {
        return titles;
    }

    public void setTitles(ArrayList<String> titles) {
        this.titles = titles;
    }

    public ArrayList<ArrayList<String>> getListOfRecords() {
        return listOfRecords;
    }

    public void setListOfRecords(ArrayList<ArrayList<String>> listOfRecords) {
        this.listOfRecords = listOfRecords;
    }

    public ArrayList<String> getTitles2() {
        return titles2;
    }

    public void setTitles2(ArrayList<String> titles2) {
        this.titles2 = titles2;
    }
}
