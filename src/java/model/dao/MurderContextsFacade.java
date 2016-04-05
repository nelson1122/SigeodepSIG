/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.MurderContexts;

/**
 *
 * @author SANTOS
 */
@Stateless
public class MurderContextsFacade extends AbstractFacade<MurderContexts> {

    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MurderContextsFacade() {
        super(MurderContexts.class);
    }
    
    public int findMax() {
        try {
            String hql = "Select MAX(x.murderContextId) from MurderContexts x";
            return em.createQuery(hql, Short.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }
}
