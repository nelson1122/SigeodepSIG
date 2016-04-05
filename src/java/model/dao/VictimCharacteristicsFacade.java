/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.VictimCharacteristics;

/**
 *
 * @author SANTOS
 */
@Stateless
public class VictimCharacteristicsFacade extends AbstractFacade<VictimCharacteristics> {

    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VictimCharacteristicsFacade() {
        super(VictimCharacteristics.class);
    }
    
    public int findMax() {
        try {
            String hql = "Select MAX(x.characteristicId) from VictimCharacteristics x";
            return em.createQuery(hql, Short.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }
}
