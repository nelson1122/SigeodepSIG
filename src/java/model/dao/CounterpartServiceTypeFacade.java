/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.CounterpartServiceType;

/**
 *
 * @author SANTOS
 */
@Stateless
public class CounterpartServiceTypeFacade extends AbstractFacade<CounterpartServiceType> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CounterpartServiceTypeFacade() {
        super(CounterpartServiceType.class);
    }
    public int findMax() {
        try {
            String hql = "Select MAX(x.counterpartServiceTypeId) from CounterpartServiceType x";
            return em.createQuery(hql, Integer.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }
    
}
