/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.Projects;

/**
 *
 * @author santos
 */
@Stateless
public class ProjectsFacade extends AbstractFacade<Projects> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProjectsFacade() {
        super(Projects.class);
    }
    public int findMax() {
        try {
            String hql = "Select MAX(x.projectId) from Projects x";
            return em.createQuery(hql, Integer.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }
    
}
