/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;


import beans.connection.ConnectionJdbcMB;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.NonFatalDomesticViolence;

/**
 *
 * @author SANTOS
 */
@Stateless
public class NonFatalDomesticViolenceFacade extends AbstractFacade<NonFatalDomesticViolence> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

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

    public NonFatalDomesticViolenceFacade() {
	super(NonFatalDomesticViolence.class);
    }
    
    public List<NonFatalDomesticViolence> findFromTag(int id_tag) {
        String hql;
        try {
            hql = "Select x from NonFatalDomesticViolence x where x.nonFatalInjuries.victimId.tagId.tagId = " + String.valueOf(id_tag);
            return em.createQuery(hql).getResultList();
        } catch (Exception e) {
            System.out.println(e.toString() + "----------------------------------------------------");
            return null;
        }

    }
    
    public int findPosition(int id,int id_tag) {
	try {
	    ResultSet rs = connectionJdbcMB.consult(""
		    + "SELECT item from"
		    + "("
		    + "   SELECT "
                    + "     ROW_NUMBER() OVER (ORDER BY non_fatal_injury_id) AS item, "
                    + "     non_fat_in.* "
                    + "   FROM "
                    + "      non_fatal_injuries non_fat_in, "
                    + "      victims vic"
                    + "   WHERE "
                    + "      vic.tag_id = " + String.valueOf(id_tag) + " AND "
                    + "      non_fat_in.victim_id = vic.victim_id AND "
                    + "      non_fat_in.injury_id = 53"
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
    
    public NonFatalDomesticViolence findByIdVictim(String id) {
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
            if (rs.next()) {
                return this.find(Integer.parseInt(rs.getString(1)));
            } else {
                return null;
            }
        } catch (SQLException | NumberFormatException e) {
            return null;//no existe siguiente
        }
    }
    
    public int countVIF(int idTag) {
	try {
            ResultSet rs = connectionJdbcMB.consult(""
                    + "SELECT "
                    + "    count(*) "
                    + "FROM "
                    + "    public.non_fatal_injuries, "
                    + "    public.victims "
                    + "WHERE "
                    + "    non_fatal_injuries.injury_id = 53 AND "
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
    
    public NonFatalDomesticViolence findNext(int injury_id, int id_tag) {
        try {
            ResultSet rs = connectionJdbcMB.consult(""
                    + "SELECT "
                    + "  non_fatal_injuries.non_fatal_injury_id "
                    + "FROM "
                    + "  public.non_fatal_injuries, "
                    + "    public.victims "
                    + "WHERE "                                        
                    + "  non_fatal_injuries.injury_id = 53 AND "
                    + "    non_fatal_injuries.victim_id = victims.victim_id AND "
                    + "  victims.tag_id = " + String.valueOf(id_tag) + " AND "
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
        } catch (SQLException | NumberFormatException e) {
            return null;//no existe siguiente
        }
    }

    public NonFatalDomesticViolence findPrevious(int injury_id, int id_tag) {
        try {
            ResultSet rs = connectionJdbcMB.consult(""
                    + "SELECT "
                    + "  non_fatal_injuries.non_fatal_injury_id "
                    + "FROM "
                    + "  public.non_fatal_injuries, "
                    + "    public.victims "
                    + "WHERE "                                        
                    + "  non_fatal_injuries.injury_id = 53 AND "
                    + "    non_fatal_injuries.victim_id = victims.victim_id AND "
                    + "  victims.tag_id = " + String.valueOf(id_tag) + " AND "
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
        } catch (SQLException | NumberFormatException e) {
            return null;//no existe siguiente
        }
    }   

    public NonFatalDomesticViolence findFirst(int id_tag) {
//        try {
//            String hql = "Select x from NonFatalDomesticViolence x WHERE x.nonFatalInjuries.injuryId.injuryId = 53 order by x.nonFatalInjuryId asc";
//            return (NonFatalDomesticViolence) em.createQuery(hql).setMaxResults(1).getSingleResult();
//        } catch (Exception e) {
//            return null;//no existe primero
//        }
        try {
            ResultSet rs = connectionJdbcMB.consult(""
                    + "SELECT "
                    + "  non_fatal_injuries.non_fatal_injury_id "
                    + "FROM "
                    + "  non_fatal_injuries, "
                    + "    public.victims "
                    + "WHERE "
                    + "  non_fatal_injuries.injury_id = 53 AND "
                    + "    non_fatal_injuries.victim_id = victims.victim_id AND "
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
        } catch (SQLException | NumberFormatException e) {
            return null;//no existe siguiente
        }
    }

    public NonFatalDomesticViolence findLast(int id_tag) {
//        try {
//            String hql = "Select x from NonFatalDomesticViolence x WHERE x.nonFatalInjuries.injuryId.injuryId = 53 order by x.nonFatalInjuryId desc";
//            return (NonFatalDomesticViolence) em.createQuery(hql).setMaxResults(1).getSingleResult();
//        } catch (Exception e) {
//            return null;//no existe ultimo
//        }
        try {
            ResultSet rs = connectionJdbcMB.consult(""
                    + "SELECT "
                    + "  non_fatal_injuries.non_fatal_injury_id "
                    + "FROM "                    
                    + "  non_fatal_injuries, "
                    + "    public.victims "
                    + "WHERE "
                    + "  non_fatal_injuries.injury_id = 53 AND "
                    + "    non_fatal_injuries.victim_id = victims.victim_id AND "
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
        } catch (SQLException | NumberFormatException e) {
            return null;//no existe siguiente
        }
    }    
}
