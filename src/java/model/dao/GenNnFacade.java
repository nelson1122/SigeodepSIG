/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.GenNn;

/**
 *
 * @author SANTOS
 */
@Stateless
public class GenNnFacade extends AbstractFacade<GenNn> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public GenNnFacade() {
	super(GenNn.class);
    }
    
    public int findMax() {
        try {
            String hql = "Select MAX(x.codNn) from GenNn x";
            return em.createQuery(hql, Integer.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }
    
}
