/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.Jobs;

/**
 *
 * @author SANTOS
 */
@Stateless
public class JobsFacade extends AbstractFacade<Jobs> {

    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public JobsFacade() {
        super(Jobs.class);
    }
    
    public Jobs findByName(String name) {
        String hql = "Select x from Jobs x where x.jobName=:name";
        try {
            return (Jobs) em.createQuery(hql).setParameter("name", name).getSingleResult();
        } catch (Exception e) {
            System.out.println(e.toString() + "----------------------------------------------------");
            return null;
        }
    }

    public List<Jobs> findAllOrder() {
        String hql = "Select x from Jobs x ORDER BY x.jobName asc";
        return em.createQuery(hql).getResultList();
    }
    
    public int findMax() {
        try {
            String hql = "Select MAX(x.jobId) from Jobs x";
            return em.createQuery(hql, Short.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }
}
