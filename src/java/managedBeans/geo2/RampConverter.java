/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.geo2;

import java.awt.Color;
import java.util.ArrayList;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

/**
 *
 * @author and
 */
@ManagedBean
@SessionScoped
public class RampConverter implements Converter {

    public static ArrayList<Ramp> rampDB;

    static {
        rampDB = new ArrayList<>();
        Ramp ramp1 = new Ramp(1, "Semaforo", "../geo2/images/semaforo.png");
        ramp1.setColors(Color.GREEN, Color.yellow, Color.RED);
        rampDB.add(ramp1);
        Ramp ramp2 = new Ramp(2, "Amarillo-Rojo", "../geo2/images/amarillo-rojo.png");
        ramp2.setColors(Color.yellow, Color.RED);
        rampDB.add(ramp2);
        Ramp ramp3 = new Ramp(2, "Azul-Rojo", "../geo2/images/azul-rojo.png");
        ramp3.setColors(Color.BLUE, Color.RED);
        rampDB.add(ramp3);
        Ramp ramp = new Ramp(2, "Azules", "../geo2/images/azules.png");
        ramp.setColors(Color.WHITE, Color.BLUE);
        rampDB.add(ramp);
        ramp = new Ramp(2, "Grises 1", "../geo2/images/grises1.png");
        ramp.setColors(Color.WHITE, Color.BLACK);
        rampDB.add(ramp);
        ramp = new Ramp(2, "Grises 2", "../geo2/images/grises2.png");
        ramp.setColors(Color.BLACK, Color.WHITE);
        rampDB.add(ramp);
        ramp = new Ramp(2, "Rojos", "../geo2/images/rojos.png");
        ramp.setColors(Color.WHITE, Color.RED);
        rampDB.add(ramp);
        ramp = new Ramp(2, "Verdes", "../geo2/images/verdes.png");
        ramp.setColors(Color.WHITE, Color.GREEN);
        rampDB.add(ramp);
    }

    /**
     * Convert the specified string value, which is associated with the
     * specified UIComponent, into a model data object that is appropriate for
     * being stored during the Apply Request Values phase .
     *
     * @param facesContext
     * @param component
     * @param submittedValue
     * @return
     */
    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
        if (submittedValue.trim().equals("")) {
            return null;
        } else {
            try {
                for (Ramp r : rampDB) {
                    if (r.getName().equals(submittedValue)) {
                        return r;
                    }
                }
            } catch (NumberFormatException exception) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid Ramp"));
            }
        }
        return null;
    }

    /**
     * This method is responsible for converting an object in string.
     *
     * @param facesContext
     * @param component
     * @param value
     * @return
     */
    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            return value.toString();
        }
    }
}
