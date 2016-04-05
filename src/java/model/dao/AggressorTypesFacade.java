/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.AggressorTypes;

/**
 *
 * @author SANTOS
 */
@Stateless
public class AggressorTypesFacade extends AbstractFacade<AggressorTypes> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public AggressorTypesFacade() {
	super(AggressorTypes.class);
    }
    
}
