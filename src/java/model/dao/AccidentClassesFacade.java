/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.AccidentClasses;

/**
 *
 * @author SANTOS
 */
@Stateless
public class AccidentClassesFacade extends AbstractFacade<AccidentClasses> {

    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AccidentClassesFacade() {
        super(AccidentClasses.class);
    }

    public int findMax() {
        try {
            String hql = "Select MAX(x.accidentClassId) from AccidentClasses x";
            return em.createQuery(hql, Short.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }
}
