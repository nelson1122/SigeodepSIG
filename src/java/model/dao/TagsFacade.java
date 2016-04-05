/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.Tags;

/**
 *
 * @author SANTOS
 */
@Stateless
public class TagsFacade extends AbstractFacade<Tags> {

    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TagsFacade() {
        super(Tags.class);
    }

    public List<Tags> findCriteria(int variable, String value) {
        String hql;

        if (value == null) {//busco todos
            hql = "Select x from Tags x order by x.tagId";
                        return em.createQuery(hql).getResultList();
        } else {
            try {
                switch (variable) {
                    case 1:
                        //hql = "Select x from Tags x where x.tagName like '" + value + "%'";
                        return (List<Tags>) em.createNativeQuery("select * from tags where tag_id::text like '" + value + "%';", Tags.class).getResultList();
                    case 2:
                        hql = "Select x from Tags x where x.tagName like '%" + value + "%'";
                        return em.createQuery(hql).getResultList();
                    case 3:
                        hql = "Select x from Tags x where x.formId.formId like '%" + value + "%'";
                        return em.createQuery(hql).getResultList();
                }
            } catch (Exception e) {
                System.out.println(e.toString() + "----------------------------------------------------");
                return null;
            }
        }
        return null;
    }

    public int findMax() {
        try {
            String hql = "Select MAX(x.tagId) from Tags x";
            return em.createQuery(hql, Integer.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }

    public Tags findByName(String name) {
        try {
            String hql = "Select x from Tags x where x.tagName=:name";
            return (Tags) em.createQuery(hql).setParameter("name", name).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
