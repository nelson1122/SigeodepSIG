/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.SivigilaAggresor;

/**
 *
 * @author santos
 */
@Stateless
public class SivigilaAggresorFacade extends AbstractFacade<SivigilaAggresor> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SivigilaAggresorFacade() {
        super(SivigilaAggresor.class);
    }
    
    public int findMax() {
        try {
            String hql = "Select MAX(x.sivigilaAgresorId) from SivigilaAggresor x";
            return em.createQuery(hql, Integer.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }
    
}
