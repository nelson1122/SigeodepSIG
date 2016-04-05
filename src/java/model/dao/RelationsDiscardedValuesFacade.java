/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.Departaments;
import model.pojo.RelationsDiscardedValues;

/**
 *
 * @author SANTOS
 */
@Stateless
public class RelationsDiscardedValuesFacade extends AbstractFacade<RelationsDiscardedValues> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RelationsDiscardedValuesFacade() {
        super(RelationsDiscardedValues.class);
    }
    
    public int findMaxId() {
	try {
	    String hql = "Select MAX(x.idDiscardedValue) from RelationsDiscardedValues x";
	    return em.createQuery(hql, Integer.class).getSingleResult();
	} catch (Exception e) {
	    return 0;
	}
    }
//    public Departaments findByName(String name) {
//        try {
//            String hql = "Select x from Departaments x where x.departamentName=:name";
//            return (Departaments) em.createQuery(hql).setParameter("name", name).getSingleResult();
//        } catch (Exception e) {
//            System.out.print("Error: " + e.toString() + "------------------------");
//            return null;
//        }
//    }
    
    public RelationsDiscardedValues findIdByNameAndIdRelation(Integer relationVariableId,String valueName) {        
	try {
	    String hql = "Select x from RelationsDiscardedValues x where x.discardedValueName=:name and x.idRelationVariables.idRelationVariables=:id";
	    return (RelationsDiscardedValues) em.createQuery(hql).setParameter("name", valueName).setParameter("id", relationVariableId).getSingleResult();
	} catch (Exception e) {
	    return null;
	}
    }
    
}
