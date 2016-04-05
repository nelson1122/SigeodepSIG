/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.Communes;
import model.pojo.Quadrants;

/**
 *
 * @author santos
 */
@Stateless
public class CommunesFacade extends AbstractFacade<Communes> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CommunesFacade() {
        super(Communes.class);
    }
    
    public int findMax() {
        try {
            String hql = "Select MAX(x.communeId) from Communes x";
            return em.createQuery(hql, Short.class).getSingleResult();
        } catch (Exception e) {
            System.out.println("err1: "+e.getMessage());
            return 0;
        }
    }

//    public List<Communes> findCriteria(int variable, String value) {
//        try {
//            switch (variable) {
//                case 1:
//                    if (value == null || value.trim().length() == 0) {
//                        List<Communes> quadrantsList = (List<Communes>) em.createNativeQuery("select * from communes ORDER BY commune_id;", Quadrants.class).getResultList();
//                        return quadrantsList;
//                    } else {
//                        List<Communes> quadrantsList = (List<Communes>) em.createNativeQuery("select * from communes where commune_id::text like '" + value + "' ORDER BY commune_id;", Quadrants.class).getResultList();
//                        return quadrantsList;
//                    }
//                case 2:
//                    if (value == null || value.trim().length() == 0) {
//                        String hql = "Select x from Communes x ORDER BY x.communeId";
//                        return em.createQuery(hql).getResultList();
//                    } else {
//                        String hql = "Select x from Communes x where x.communeName like '%" + value + "%' ORDER BY x.communeId";
//                        return em.createQuery(hql).getResultList();
//                    }
//
//            }
//        } catch (Exception e) {
//            System.out.println(e.toString() + "----------------------------------------------------");
//            return null;
//        }
//        return null;
//    }
    
}
