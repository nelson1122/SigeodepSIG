/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.RoadTypes;

/**
 *
 * @author SANTOS
 */
@Stateless
public class RoadTypesFacade extends AbstractFacade<RoadTypes> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public RoadTypesFacade() {
	super(RoadTypes.class);
    }
    
}
