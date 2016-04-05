/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import beans.connection.ConnectionJdbcMB;
import java.sql.ResultSet;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.NonFatalInjuries;

/**
 *
 * @author SANTOS
 */
@Stateless
public class NonFatalInjuriesFacade extends AbstractFacade<NonFatalInjuries> {

//    @EJB
//    TagsFacade tagsFacade;
//    @EJB
//    LoadsFacade loadsFacade;
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;
    private String message = "";
    private String query = "";

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    ConnectionJdbcMB connectionJdbcMB;
    /*
     * primer funcion que se ejecuta despues del constructor que inicializa 
     * variables y carga la conexion por jdbc
     */
    @PostConstruct
    private void initialize() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);        
    }

    public List<NonFatalInjuries> findFromTag(int id_tag) {
        String hql;
        try {
            hql = "Select x from NonFatalInjuries x where x.victimId.tagId.tagId = " + String.valueOf(id_tag);
            return em.createQuery(hql).getResultList();
        } catch (Exception e) {
            System.out.println(e.toString() + "----------------------------------------------------");
            return null;
        }
    }

    public int countFromTag(int id_tag) {
        String hql;
        try {
            hql = "Select count(x) from NonFatalInjuries x where x.victimId.tagId.tagId = " + String.valueOf(id_tag);
            long l = em.createQuery(hql, Long.class).getSingleResult();
            String s = String.valueOf(l);
            return Integer.parseInt(s);
        } catch (Exception e) {
            System.out.println(e.toString() + "-----------------------------------------------");
            return 0;
        }
    }

    public NonFatalInjuriesFacade() {
        super(NonFatalInjuries.class);
    }

    public int findMax() {
        try {
            String hql = "Select MAX(x.nonFatalInjuryId) from NonFatalInjuries x";
            return em.createQuery(hql, Integer.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }

    public int findMaxId2() {        
        try {
            ResultSet rs = connectionJdbcMB.consult(""
                    + "SELECT MAX(id_relation_group) "
                    + "FROM relation_group;");
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }
        } catch (Exception ex) {
            return 0;
        }
    }

    public int countLCENF(int idTag) {
        //determina cuantos registros de lesiones no fatales existen
        //dado un determinado conjunto de datos
        try {
            ResultSet rs = connectionJdbcMB.consult(""
                    + "SELECT "
                    + "    count(*) "
                    + "FROM "
                    + "    public.non_fatal_injuries, "
                    + "    public.victims "
                    + "WHERE "
                    + "    non_fatal_injuries.injury_id != 53 AND "
                    + "    non_fatal_injuries.victim_id = victims.victim_id AND "
                    + "    victims.tag_id = " + String.valueOf(idTag) + "; ");
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }
        } catch (Exception ex) {
            return 0;
        }
    }

    public int findPosition(int id, int id_tag) {
        try {
            ResultSet rs = connectionJdbcMB.consult(""
                    + "SELECT item from"
                    + "("
                    + "   SELECT "
                    + "     ROW_NUMBER() OVER (ORDER BY non_fatal_injury_id) AS item, "
                    + "     non_fat_in.*"
                    + "   FROM "
                    + "      non_fatal_injuries non_fat_in, "
                    + "      victims vic"
                    + "   WHERE "
                    + "      vic.tag_id = " + String.valueOf(id_tag) + " AND "
                    + "      non_fat_in.victim_id = vic.victim_id AND "
                    + "      non_fat_in.injury_id != 53 "
                    + ") "
                    + "AS "
                    + "   a "
                    + "WHERE "
                    + "   non_fatal_injury_id=" + String.valueOf(id) + ";");
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }
        } catch (Exception ex) {
            return 0;
        }
    }

    public NonFatalInjuries findByIdVictim(String id) {
//        try {
//            String hql = "SELECT x FROM NonFatalInjuries x where x.nonFatalInjuryId>:id AND x.injuryId.injuryId != 53 order by x.nonFatalInjuryId asc";
//            return (NonFatalInjuries) em.createQuery(hql).setMaxResults(1).setParameter("id", id).getSingleResult();
//        } catch (Exception e) {
//            return null;//no existe siguiente
//        }
        try {
            ResultSet rs = connectionJdbcMB.consult(""
                    + "SELECT non_fatal_injury_id FROM non_fatal_injuries, victims "
                    + "WHERE victims.victim_id = " + id + " "
                    + "AND victims.victim_id = non_fatal_injuries.victim_id ");
            //+ "ORDER BY non_fatal_injury_id ASC;");
            if (rs.next()) {
                return this.find(Integer.parseInt(rs.getString(1)));
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;//no existe siguiente
        }
    }

    public NonFatalInjuries findNext(int injury_id, int id_tag) {
//        try {
//            String hql = "SELECT x FROM NonFatalInjuries x where x.nonFatalInjuryId>:id AND x.injuryId.injuryId != 53 order by x.nonFatalInjuryId asc";
//            return (NonFatalInjuries) em.createQuery(hql).setMaxResults(1).setParameter("id", id).getSingleResult();
//        } catch (Exception e) {
//            return null;//no existe siguiente
//        }
        try {
            ResultSet rs = connectionJdbcMB.consult(""
                    + "SELECT "
                    + "  non_fatal_injuries.non_fatal_injury_id "
                    + "FROM "
                    + "  public.non_fatal_injuries, "
                    + "  public.victims "
                    + "WHERE "
                    + "  non_fatal_injuries.injury_id != 53 AND "
                    + "  victims.tag_id = " + String.valueOf(id_tag) + " AND "
                    + "  non_fatal_injuries.victim_id = victims.victim_id AND "
                    + "  non_fatal_injuries.non_fatal_injury_id > " + String.valueOf(injury_id) + " "
                    + "ORDER BY "
                    + "  non_fatal_injuries.non_fatal_injury_id ASC "
                    + "LIMIT "
                    + "  1;");
            if (rs.next()) {
                return this.find(Integer.parseInt(rs.getString(1)));
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;//no existe siguiente
        }
    }

    public NonFatalInjuries findPrevious(int injury_id, int id_tag) {
//        try {
//            String hql = "Select x from NonFatalInjuries x where x.nonFatalInjuryId<:id AND x.injuryId.injuryId != 53 order by x.nonFatalInjuryId desc";
//            return (NonFatalInjuries) em.createQuery(hql).setMaxResults(1).setParameter("id", id).getSingleResult();
//        } catch (Exception e) {
//            return null;//no existe anterior
//        }
        try {
            ResultSet rs = connectionJdbcMB.consult(""
                    + "SELECT "
                    + "  non_fatal_injuries.non_fatal_injury_id "
                    + "FROM "
                    + "  public.non_fatal_injuries, "
                    + "  public.victims "
                    + "WHERE "
                    + "  non_fatal_injuries.injury_id != 53 AND "
                    + "  victims.tag_id = " + String.valueOf(id_tag) + " AND "
                    + "  non_fatal_injuries.victim_id = victims.victim_id AND "
                    + "  non_fatal_injuries.non_fatal_injury_id < " + String.valueOf(injury_id) + " "
                    + "ORDER BY "
                    + "  non_fatal_injuries.non_fatal_injury_id DESC "
                    + "LIMIT "
                    + "  1;");
            if (rs.next()) {
                return this.find(Integer.parseInt(rs.getString(1)));
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;//no existe siguiente
        }
    }

    public NonFatalInjuries findFirst(int id_tag) {
//	try {
//	    String hql = "Select x from NonFatalInjuries x WHERE x.injuryId.injuryId != 53 order by x.nonFatalInjuryId asc";
//            NonFatalInjuries nfi=(NonFatalInjuries) em.createQuery(hql).setMaxResults(1).getSingleResult();
//	    return nfi;
//	} catch (Exception e) {
//          return null;            
//	}
        try {
            ResultSet rs = connectionJdbcMB.consult(""
                    + "SELECT "
                    + "  non_fatal_injuries.non_fatal_injury_id "
                    + "FROM "
                    + "  public.non_fatal_injuries, "
                    + "  public.victims "
                    + "WHERE "
                    + "  non_fatal_injuries.injury_id != 53 AND "
                    + "  non_fatal_injuries.victim_id = victims.victim_id AND "
                    + "  victims.tag_id = " + String.valueOf(id_tag) + " "
                    
                    + "ORDER BY "
                    + "  non_fatal_injuries.non_fatal_injury_id ASC "
                    + "LIMIT "
                    + "  1;");
            if (rs.next()) {
                return this.find(Integer.parseInt(rs.getString(1)));
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;//no existe siguiente
        }
    }

    public NonFatalInjuries findLast(int id_tag) {
//        try {
//            String hql = "Select x from NonFatalInjuries x WHERE x.injuryId.injuryId != 53 order by x.nonFatalInjuryId desc";
//            return (NonFatalInjuries) em.createQuery(hql).setMaxResults(1).getSingleResult();
//        } catch (Exception e) {
//            return null;//no existe ultimo
//        }
        try {
            ResultSet rs = connectionJdbcMB.consult(""
                    + "SELECT "
                    + "  non_fatal_injuries.non_fatal_injury_id "
                    + "FROM "
                    + "  public.non_fatal_injuries, "
                    + "  public.victims "
                    + "WHERE "
                    + "  non_fatal_injuries.injury_id != 53 AND "
                    + "  non_fatal_injuries.victim_id = victims.victim_id AND "
                    + "  victims.tag_id = " + String.valueOf(id_tag) + " "
                    + "ORDER BY "
                    + "  non_fatal_injuries.non_fatal_injury_id DESC "
                    + "LIMIT "
                    + "  1;");
            if (rs.next()) {
                return this.find(Integer.parseInt(rs.getString(1)));
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;//no existe siguiente
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
