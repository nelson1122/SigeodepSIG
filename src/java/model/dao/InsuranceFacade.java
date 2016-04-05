/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.Insurance;

/**
 *
 * @author SANTOS
 */
@Stateless
public class InsuranceFacade extends AbstractFacade<Insurance> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public InsuranceFacade() {
        super(Insurance.class);
    }
    
//    public int findMax() {
//        try {
//            String hql = "Select MAX(x.insuranceId) from Insurance x";
//            return em.createQuery(hql, Short.class).getSingleResult();
//        } catch (Exception e) {
//            return 0;
//        }
//    }
    
    public Insurance findByName(String name) {
        String hql = "Select x from Insurance x where x.insuranceName=:name";
        try {
            return (Insurance) em.createQuery(hql).setParameter("name", name).getSingleResult();
        } catch (Exception e) {
            System.out.println(e.toString() + "----------------------------------------------------");
            return null;
        }
    }
    
    public Insurance findByCode(String id) {
        
        String hql = "Select x from Insurance x where x.insuranceId = '"+id+"'";
        try {
            return (Insurance) em.createQuery(hql).getSingleResult();
        } catch (Exception e) {
            //System.out.println(e.toString() + "----------------------------------------------------");
            return null;
        }
    }
    
}
