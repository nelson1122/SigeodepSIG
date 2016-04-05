/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.Boolean3;

/**
 *
 * @author SANTOS
 */
@Stateless
public class Boolean3Facade extends AbstractFacade<Boolean3> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Boolean3Facade() {
        super(Boolean3.class);
    }
    
}
