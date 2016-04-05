/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import beans.connection.ConnectionJdbcMB;
import java.sql.ResultSet;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.Fields;
import model.pojo.FieldsPK;

/**
 *
 * @author SANTOS
 */
@Stateless
public class FieldsFacade extends AbstractFacade<Fields> {

    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public FieldsFacade() {
        super(Fields.class);
    }

//    public Fields findFieldTypeByFieldNameAndFormId(String currentVarExpected, String formId) {
//        try {
//            FieldsPK newFieldPk = new FieldsPK(formId, currentVarExpected);
//            return this.find(newFieldPk);
//        } catch (Exception ex) {
//            return null;
//        }
//    }
}
