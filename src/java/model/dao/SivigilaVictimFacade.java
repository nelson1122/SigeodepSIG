/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.SivigilaVictim;

/**
 *
 * @author santos
 */
@Stateless
public class SivigilaVictimFacade extends AbstractFacade<SivigilaVictim> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SivigilaVictimFacade() {
        super(SivigilaVictim.class);
    }
    
    public int findMax() {
        try {
            String hql = "Select MAX(x.sivigilaVictimId) from SivigilaVictim x";
            return em.createQuery(hql, Integer.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }
    
}
