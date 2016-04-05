/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.PrecipitatingFactors;

/**
 *
 * @author SANTOS
 */
@Stateless
public class PrecipitatingFactorsFacade extends AbstractFacade<PrecipitatingFactors> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public PrecipitatingFactorsFacade() {
	super(PrecipitatingFactors.class);
    }
    
}
