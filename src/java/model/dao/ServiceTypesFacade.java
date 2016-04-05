/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.ServiceTypes;

/**
 *
 * @author SANTOS
 */
@Stateless
public class ServiceTypesFacade extends AbstractFacade<ServiceTypes> {

    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ServiceTypesFacade() {
        super(ServiceTypes.class);
    }

    public int findMax() {
        try {
            String hql = "Select MAX(x.serviceTypeId) from ServiceTypes x";
            return em.createQuery(hql, Short.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }
}
