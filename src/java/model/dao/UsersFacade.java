/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.Users;

/**
 *
 * @author SANTOS
 */
@Stateless
public class UsersFacade extends AbstractFacade<Users> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsersFacade() {
        super(Users.class);
    }
    
    public int findMax() {
        try {
            String hql = "Select MAX(x.userId) from Users x";
            return em.createQuery(hql, Integer.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }

//    public List<Users> findCriteria(int variable, String value) {
//        String hql;
//        try {
//            switch (variable) {
//                case 1:
//                    hql = "Select x from Users x where upper(x.userName) like '" + value + "%'";
//                    return em.createQuery(hql).getResultList();
//                case 2:
//                    hql = "Select x from Users x where upper(x.userLogin) like '" + value + "%'";
//                    return em.createQuery(hql).getResultList();
//                case 3:
//                    hql = "Select x from Users x where upper(x.userJob) like '" + value + "%'";
//                    return em.createQuery(hql).getResultList();
//            }
//        } catch (Exception e) {
//            System.out.println(e.toString() + "----------------------------------------------------");
//            return null;
//        }
//        return null;
//    }

    public Users findByLogin(String newLogin) {
        List<Users> usersList = this.findAll();
        for (int i = 0; i < usersList.size(); i++) {
            if ( usersList.get(i).getUserLogin().compareTo(newLogin) == 0) {
                return usersList.get(i);
            }
        }
        return null;
    }

    public Users findUser(String loginname, String password) {
        List<Users> usersList = this.findAll();
        for (int i = 0; i < usersList.size(); i++) {
            if (usersList.get(i).getUserPassword().compareTo(password) == 0
                    && usersList.get(i).getUserLogin().compareTo(loginname) == 0) {
                return usersList.get(i);
            }
        }

        return null;

    }
    
}
