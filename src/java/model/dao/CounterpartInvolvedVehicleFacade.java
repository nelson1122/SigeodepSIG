/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.CounterpartInvolvedVehicle;

/**
 *
 * @author SANTOS
 */
@Stateless
public class CounterpartInvolvedVehicleFacade extends AbstractFacade<CounterpartInvolvedVehicle> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CounterpartInvolvedVehicleFacade() {
        super(CounterpartInvolvedVehicle.class);
    }
    
    public int findMax() {
        try {
            String hql = "Select MAX(x.counterpartInvolvedVehicleId) from CounterpartInvolvedVehicle x";
            return em.createQuery(hql, Integer.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }
    
}
