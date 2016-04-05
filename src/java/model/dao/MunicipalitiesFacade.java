/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.Municipalities;

/**
 *
 * @author SANTOS
 */
@Stateless
public class MunicipalitiesFacade extends AbstractFacade<Municipalities> {

    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MunicipalitiesFacade() {
        super(Municipalities.class);
    }

    public int findMax() {
        try {
            String hql = "Select MAX(x.jobId) from Municipalities x";
            return em.createQuery(hql, Short.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }

    /*
     * determinar maximo segun departamento
     */
    public short findMax(Short dep) {
        String hql;
        try {
            hql = "Select MAX(x.municipalitiesPK.municipalityId) from Municipalities x where "
                    + "x.municipalitiesPK.departamentId = " + dep + "";
            return em.createQuery(hql, Short.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }

//    public List<Municipalities> findCriteria(int variable, String value) {
//        String hql;
//        try {
//            switch (variable) {
//                case 1:
//                    hql = "Select x from Municipalities x where x.municipalityName like '%" + value + "%'";
//                    return em.createQuery(hql).getResultList();
//                case 2:
//                    List<Municipalities> neighborhoodsList = (List<Municipalities>) em.createNativeQuery("select * from municipalities where municipality_id::text ilike '%" + value + "%';", Municipalities.class).getResultList();
//                    return neighborhoodsList;
//                case 3:
//                    hql = "Select x from Municipalities x where x.departaments.departamentName like '" + value + "%'";
//                    return em.createQuery(hql).getResultList();
//            }
//        } catch (Exception e) {
//            System.out.println(e.toString() + "----------------------------------------------------");
//            return null;
//        }
//        return null;
//    }
}
