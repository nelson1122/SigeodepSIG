/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.SivigilaEvent;

/**
 *
 * @author santos
 */
@Stateless
public class SivigilaEventFacade extends AbstractFacade<SivigilaEvent> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SivigilaEventFacade() {
        super(SivigilaEvent.class);
    }
    
}
