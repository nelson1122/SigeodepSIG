/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.RelationValues;

/**
 *
 * @author SANTOS
 */
@Stateless
public class RelationValuesFacade extends AbstractFacade<RelationValues> {

    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public RelationValuesFacade() {
	super(RelationValues.class);
    }

    public int findMaxId() {
	try {
	    String hql = "Select MAX(x.idRelationValues) from RelationValues x";
	    return em.createQuery(hql, Integer.class).getSingleResult();
	} catch (Exception e) {
	    return 0;
	}
    }

    public List<RelationValues> findByRelationVariables(Integer idRelationVariables) {
	String hql = "Select x from RelationValues x where x.idRelationVariables.idRelationVariables=:id";
	return em.createQuery(hql).setParameter("id", idRelationVariables).getResultList();
    }
}
