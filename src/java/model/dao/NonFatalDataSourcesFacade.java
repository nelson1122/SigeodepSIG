/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.NonFatalDataSources;

/**
 *
 * @author SANTOS
 */
@Stateless
public class NonFatalDataSourcesFacade extends AbstractFacade<NonFatalDataSources> {

    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public NonFatalDataSourcesFacade() {
        super(NonFatalDataSources.class);
    }

    public NonFatalDataSources findByName(String nameDataSource) {
        try {
            String hql = "SELECT f FROM NonFatalDataSources f WHERE f.nonFatalDataSourceName = :name";
            return (NonFatalDataSources) em.createQuery(hql).setParameter("name", nameDataSource).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
    
    public int findMax() {
        try {
            String hql = "Select MAX(x.nonFatalDataSourceId) from NonFatalDataSources x";
            return em.createQuery(hql, Short.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }

//    public List<NonFatalDataSources> findCriteria(int variable, String value) {
//        String hql;
//        try {
//            switch (variable) {
//                case 1:
//                    hql = "Select x from NonFatalDataSources x where x.nonFatalDataSourceName like '%" + value + "%'";
//                    return em.createQuery(hql).getResultList();
//                case 2:
//                    hql = "Select x from NonFatalDataSources x where x.nonFatalDataSourceName like '%" + value + "%'";
//                    return em.createQuery(hql).getResultList();
//            }
//        } catch (Exception e) {
//            System.out.println(e.toString() + "----------------------------------------------------");
//            return null;
//        }
//        return null;
//    }
}
