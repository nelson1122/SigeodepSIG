/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.Fields;
import model.pojo.Forms;
import model.pojo.NonFatalDataSources;


/**
 *
 * @author SANTOS
 */
@Stateless
public class FormsFacade extends AbstractFacade<Forms> {

    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public FormsFacade() {
	super(Forms.class);
    }

    public Forms findByFormId(String idForm) {
	String hql = "Select x from Forms x where x.formId=:id";
	return (Forms) em.createQuery(hql).setParameter("id", idForm).getSingleResult();
    }

    public List<NonFatalDataSources> findSources(String nameForm) {
	String hql = "Select x.sourcesList from Forms x where x.formId=:id";
	return em.createQuery(hql).setParameter("id", nameForm).getResultList();
    }

    public List<Fields> findVarsExecpted(String nameForm) {    //COMO SE ORDENA POR UN DETERMINADO CAPO
	//String hql="SELECT x.fieldsList FROM Forms x WHERE x.formId=:id";
	//String hql = "SELECT f FROM Forms x JOIN x.fieldsList f WHERE x.formId=:id ORDER BY f.fieldOrder";
        String hql = "SELECT x FROM Fields x WHERE x.fieldsPK.formId=:id ORDER BY x.fieldOrder";
	return em.createQuery(hql).setParameter("id", nameForm).getResultList();
    }
//    public List<Fields> findByForm(String nameForm)                
//    {
//        String hql="SELECT x.fieldsList FROM Forms x WHERE x.fielPK=:id";
//        return em.createQuery(hql).setParameter("id", nameForm).getResultList();
//    }
}
