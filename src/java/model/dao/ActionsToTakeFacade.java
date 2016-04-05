/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.ActionsToTake;

/**
 *
 * @author SANTOS
 */
@Stateless
public class ActionsToTakeFacade extends AbstractFacade<ActionsToTake> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public ActionsToTakeFacade() {
	super(ActionsToTake.class);
    }
    
}
