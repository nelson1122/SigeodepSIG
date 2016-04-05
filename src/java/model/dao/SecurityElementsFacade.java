/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.SecurityElements;

/**
 *
 * @author SANTOS
 */
@Stateless
public class SecurityElementsFacade extends AbstractFacade<SecurityElements> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SecurityElementsFacade() {
	super(SecurityElements.class);
    }
    
}
