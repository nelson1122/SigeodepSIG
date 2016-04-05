/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.Boolean2;

/**
 *
 * @author SANTOS
 */
@Stateless
public class Boolean2Facade extends AbstractFacade<Boolean2> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Boolean2Facade() {
        super(Boolean2.class);
    }
    
}
