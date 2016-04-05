/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.Quadrants;

/**
 *
 * @author santos
 */
@Stateless
public class QuadrantsFacade extends AbstractFacade<Quadrants> {

    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public QuadrantsFacade() {
        super(Quadrants.class);
    }

    public int findMax() {
        try {
            String hql = "Select MAX(x.quadrantId) from Quadrants x";
            return em.createQuery(hql, Integer.class).getSingleResult();
        } catch (Exception e) {
            System.out.println("sssssss"+e.getMessage());
            return 0;
        }
    }

//    public List<Quadrants> findCriteria(int variable, String value) {
//        try {
//            switch (variable) {
//                case 1:
//                    if (value == null || value.trim().length() == 0) {
//                        List<Quadrants> quadrantsList = (List<Quadrants>) em.createNativeQuery("select * from quadrants ORDER BY quadrant_id;", Quadrants.class).getResultList();
//                        return quadrantsList;
//                    } else {
//                        List<Quadrants> quadrantsList = (List<Quadrants>) em.createNativeQuery("select * from quadrants where quadrant_id::text like '" + value + "' ORDER BY quadrant_id;", Quadrants.class).getResultList();
//                        return quadrantsList;
//                    }
//                case 2:
//                    if (value == null || value.trim().length() == 0) {
//                        String hql = "Select x from Quadrants x ORDER BY x.quadrantId";
//                        return em.createQuery(hql).getResultList();
//                    } else {
//                        String hql = "Select x from Quadrants x where x.quadrantName like '%" + value + "%' ORDER BY x.quadrantId";
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
