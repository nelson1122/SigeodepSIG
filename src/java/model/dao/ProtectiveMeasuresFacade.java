/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.ProtectiveMeasures;

/**
 *
 * @author SANTOS
 */
@Stateless
public class ProtectiveMeasuresFacade extends AbstractFacade<ProtectiveMeasures> {

    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProtectiveMeasuresFacade() {
        super(ProtectiveMeasures.class);
    }
    
    public int findMax() {
        try {
            String hql = "Select MAX(x.protectiveMeasuresId) from ProtectiveMeasures x";
            return em.createQuery(hql, Short.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }
}
