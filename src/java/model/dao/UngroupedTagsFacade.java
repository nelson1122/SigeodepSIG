/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.UngroupedTags;

/**
 *
 * @author santos
 */
@Stateless
public class UngroupedTagsFacade extends AbstractFacade<UngroupedTags> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UngroupedTagsFacade() {
        super(UngroupedTags.class);
    }
    
    public int findMax() {
        try {
            String hql = "Select MAX(x.ungroupedTagId) from UngroupedTags x";
            return em.createQuery(hql, Integer.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }
    
}
