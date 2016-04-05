/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.Corridors;

/**
 *
 * @author santos
 */
@Stateless
public class CorridorsFacade extends AbstractFacade<Corridors> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public int findMax() {
        try {
            String hql = "Select MAX(x.corridorId) from Corridors x";
            return em.createQuery(hql, Short.class).getSingleResult();
        } catch (Exception e) {
            System.out.println("err1: "+e.getMessage());
            return 0;
        }
    }
//
//    public List<Corridors> findCriteria(int variable, String value) {
//        try {
//            switch (variable) {
//                case 1:
//                    if (value == null || value.trim().length() == 0) {
//                        List<Corridors> quadrantsList = (List<Corridors>) em.createNativeQuery("select * from corridors ORDER BY corridor_id;", Corridors.class).getResultList();
//                        return quadrantsList;
//                    } else {
//                        List<Corridors> quadrantsList = (List<Corridors>) em.createNativeQuery("select * from corridors where corridor_id::text like '" + value + "' ORDER BY corridor_id;", Corridors.class).getResultList();
//                        return quadrantsList;
//                    }
//                case 2:
//                    if (value == null || value.trim().length() == 0) {
//                        String hql = "Select x from Corridors x ORDER BY x.corridorId";
//                        return em.createQuery(hql).getResultList();
//                    } else {
//                        String hql = "Select x from Corridors x where x.corridorName like '%" + value + "%' ORDER BY x.corridorId";
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

    public CorridorsFacade() {
        super(Corridors.class);
    }
    
}
