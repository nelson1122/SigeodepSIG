/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.Days;

/**
 *
 * @author SANTOS
 */
@Stateless
public class DaysFacade extends AbstractFacade<Days> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public DaysFacade() {
	super(Days.class);
    }
    
}
