/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.UseAlcoholDrugs;

/**
 *
 * @author SANTOS
 */
@Stateless
public class UseAlcoholDrugsFacade extends AbstractFacade<UseAlcoholDrugs> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public UseAlcoholDrugsFacade() {
	super(UseAlcoholDrugs.class);
    }
    
}
