/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.Victims;

/**
 *
 * @author SANTOS
 */
@Stateless
public class VictimsFacade extends AbstractFacade<Victims> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public VictimsFacade() {
	super(Victims.class);
    }
    
    public int findMax() {
        try {
            String hql = "Select MAX(x.victimId) from Victims x";
            return em.createQuery(hql, Integer.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }
    
}
