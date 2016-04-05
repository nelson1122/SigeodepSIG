/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.Departaments;

/**
 *
 * @author SANTOS
 */
@Stateless
public class DepartamentsFacade extends AbstractFacade<Departaments> {

    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DepartamentsFacade() {
        super(Departaments.class);
    }

    public Departaments findByName(String name) {
        try {
            String hql = "Select x from Departaments x where x.departamentName=:name";
            return (Departaments) em.createQuery(hql).setParameter("name", name).getSingleResult();
        } catch (Exception e) {
            System.out.print("Error: " + e.toString() + "------------------------");
            return null;
        }
    }

    public Departaments findById(Short id) {
        try {
            String hql = "Select x from Departaments x where x.departamentId=:id";
            return (Departaments) em.createQuery(hql).setParameter("id", id).getSingleResult();
        } catch (Exception e) {
            System.out.print("Error: " + e.toString() + "------------------------");
            return null;
        }
    }

    public int findMax() {
        try {
            String hql = "Select MAX(x.departamentId) from Departaments x";
            return em.createQuery(hql, Short.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }

//    public List<Departaments> findCriteria(int variable, String value) {
//        String hql;
//        try {
//            switch (variable) {
//                case 1:
//                    hql = "Select x from Departaments x where x.departamentName like '%" + value + "%'";
//                    return em.createQuery(hql).getResultList();
//                case 2:
//                    List<Departaments> neighborhoodsList = (List<Departaments>) em.createNativeQuery("select * from departaments where departament_id::text like '%" + value + "%';", Departaments.class).getResultList();
//                    return neighborhoodsList;
//            }
//        } catch (Exception e) {
//            System.out.println(e.toString() + "----------------------------------------------------");
//            return null;
//        }
//        return null;
//    }
}
