/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.RelationGroup;

/**
 *
 * @author SANTOS
 */
@Stateless
public class RelationGroupFacade extends AbstractFacade<RelationGroup> {

    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RelationGroupFacade() {
        super(RelationGroup.class);
    }

    public int findMaxId() {
        try {
            String hql = "Select MAX(x.idRelationGroup) from RelationGroup x";
            return em.createQuery(hql, Integer.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }

    public RelationGroup findByName(String name) {
        try {
            String hql = "Select x from RelationGroup x where x.nameRelationGroup=:name";
            return (RelationGroup) em.createQuery(hql).setParameter("name", name).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
