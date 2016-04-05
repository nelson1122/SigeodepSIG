/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.RelationVariables;

/**
 *
 * @author SANTOS
 */
@Stateless
public class RelationVariablesFacade extends AbstractFacade<RelationVariables> {

    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public RelationVariablesFacade() {
	super(RelationVariables.class);
    }

    public int findMaxId() {
	try {
	    String hql = "Select MAX(x.idRelationVariables) from RelationVariables x";
	    return em.createQuery(hql, Integer.class).getSingleResult();
	} catch (Exception e) {
	    return 0;
	}
    }

    public List<RelationVariables> findByRelationGroup(Integer idRelationGroup) {
	String hql = "Select x from RelationVariables x where x.idRelationGroup.idRelationGroup=:id";
	return em.createQuery(hql).setParameter("id", idRelationGroup).getResultList();
    }
}
