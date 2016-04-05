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
import model.pojo.FatalInjuryTraffic;

/**
 *
 * @author SANTOS
 */
@Stateless
public class FatalInjuryTrafficFacade extends AbstractFacade<FatalInjuryTraffic> {
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

    public FatalInjuryTrafficFacade() {
	super(FatalInjuryTraffic.class);
    }
    
    public List<FatalInjuryTraffic> findFromTag(int id_tag) {
        String hql;
        try {
            hql = "Select x from FatalInjuryTraffic x where x.fatalInjuries.victimId.tagId.tagId = " + String.valueOf(id_tag);
            return  em.createQuery(hql).getResultList();
        } catch (Exception e) {
            System.out.println(e.toString() + "----------------------------------------------------");
            return null;
        }
    }
    
    public int countFromTag(int id_tag) {
        String hql;
        try {
            hql = "Select count(x) from FatalInjuryTraffic x where x.fatalInjuries.victimId.tagId.tagId = " + String.valueOf(id_tag);
            long l=em.createQuery(hql, Long.class).getSingleResult();
            String s=String.valueOf(l);
            return Integer.parseInt(s);
        } catch (Exception e) {
            System.out.println(e.toString() + "-----------------------------------------------");
            return 0;
        }
    }
    
    public int countTraffic(int idTag) {
	try {
            ResultSet rs = connectionJdbcMB.consult(""
                    + "SELECT "
                    + "    count(*) "
                    + "FROM "
                    + "    public.fatal_injuries, "
                    + "    public.victims "
                    + "WHERE "                    
                    + "    fatal_injuries.injury_id = 11 AND "
                    + "    fatal_injuries.victim_id = victims.victim_id AND "
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

    public int findPosition(int injury_id,int id_tag) {
        try {
            
            ResultSet rs = connectionJdbcMB.consult(""
                    + "SELECT item from"
                    + "("
                    + "   SELECT "
                    + "     ROW_NUMBER() OVER (ORDER BY fatal_injury_id) AS item, "
                    + "     fat_inj.* "
                    + "   FROM "
                    + "     fatal_injuries fat_inj, "
                    + "      victims vic"
                    + "   WHERE"
                    + "      fat_inj.injury_id = 11 AND"
                    + "      fat_inj.victim_id = vic.victim_id AND "
                    + "      vic.tag_id = " + String.valueOf(id_tag) + " "
                    + ") "
                    + "as a WHERE fatal_injury_id=" + String.valueOf(injury_id) + "");
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }
        } catch (Exception ex) {
            return 0;
        }
    }
    
    
    public FatalInjuryTraffic findByIdVictim(String id) {
//        try {
//            String hql = "SELECT x FROM NonFatalInjuries x where x.nonFatalInjuryId>:id AND x.injuryId.injuryId != 53 order by x.nonFatalInjuryId asc";
//            return (NonFatalInjuries) em.createQuery(hql).setMaxResults(1).setParameter("id", id).getSingleResult();
//        } catch (Exception e) {
//            return null;//no existe siguiente
//        }
        try {
            ResultSet rs = connectionJdbcMB.consult(""
                    + "SELECT fatal_injury_id FROM fatal_injuries, victims "
                    + "WHERE victims.victim_id = " + id + " "
                    + "AND victims.victim_id = fatal_injuries.victim_id ");
            if (rs.next()) {
                return this.find(Integer.parseInt(rs.getString(1)));
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;//no existe siguiente
        }
    }


    public FatalInjuryTraffic findNext(int injury_id, int id_tag) {
        
        try {
            ResultSet rs = connectionJdbcMB.consult(""
                    + "SELECT "
                    + "  fatal_injuries.fatal_injury_id "
                    + "FROM "
                    + "  public.fatal_injuries, "
                    + "    public.victims "
                    + "WHERE "
                    + "  fatal_injuries.injury_id = 11 AND "
                    + "    fatal_injuries.victim_id = victims.victim_id AND "
                    + "  victims.tag_id = " + String.valueOf(id_tag) + " AND "
                    + "  fatal_injuries.fatal_injury_id > " + String.valueOf(injury_id) + " "
                    + "ORDER BY "
                    + "  fatal_injuries.fatal_injury_id ASC "
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

    public FatalInjuryTraffic findPrevious(int injury_id, int id_tag) {
        try {
            ResultSet rs = connectionJdbcMB.consult(""
                    + "SELECT "
                    + "  fatal_injuries.fatal_injury_id "
                    + "FROM "
                    + "  public.fatal_injuries, "
                    + "    public.victims "
                    + "WHERE "
                    + "  fatal_injuries.injury_id = 11 AND "
                    + "    fatal_injuries.victim_id = victims.victim_id AND "
                    + "  victims.tag_id = " + String.valueOf(id_tag) + " AND "
                    + "  fatal_injuries.fatal_injury_id < " + String.valueOf(injury_id) + " "
                    + "ORDER BY "
                    + "  fatal_injuries.fatal_injury_id DESC "
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
    
    public FatalInjuryTraffic findFirst(int id_tag) {
//        try {
//            //select * from usuarios where id > 5 order by id asc limit 1;
//            String hql = "Select x from FatalInjuryTraffic x order by x.fatalInjuryId asc";
//            return (FatalInjuryTraffic) em.createQuery(hql).setMaxResults(1).getSingleResult();
//        } catch (Exception e) {
//            //System.out.println(e.toString());
//            return null;//no existe primero
//        }
        try {
            ResultSet rs = connectionJdbcMB.consult(""
                    + "SELECT "
                    + "  fatal_injuries.fatal_injury_id "
                    + "FROM "
                    + "  public.fatal_injuries, "
                    + "    public.victims "
                    + "WHERE "
                    + "  fatal_injuries.injury_id = 11 AND "
                    + "    fatal_injuries.victim_id = victims.victim_id AND "
                    + "  victims.tag_id = " + String.valueOf(id_tag) + " "                    
                    + "ORDER BY "
                    + "  fatal_injuries.fatal_injury_id ASC "
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

    public FatalInjuryTraffic findLast(int id_tag) {
//        try {
//            //select * from usuarios where id > 5 order by id asc limit 1;
//            String hql = "Select x from FatalInjuryTraffic x order by x.fatalInjuryId desc";
//            return (FatalInjuryTraffic) em.createQuery(hql).setMaxResults(1).getSingleResult();
//        } catch (Exception e) {
//            //System.out.println(e.toString());
//            return null;//no existe ultimo
//        }
        try {
            ResultSet rs = connectionJdbcMB.consult(""
                    + "SELECT "
                    + "  fatal_injuries.fatal_injury_id "
                    + "FROM "
                    + "  public.fatal_injuries, "
                    + "    public.victims "
                    + "WHERE "
                    + "  fatal_injuries.injury_id = 11 AND "
                    + "    fatal_injuries.victim_id = victims.victim_id AND "
                    + "  victims.tag_id = " + String.valueOf(id_tag) + " "
                    + "ORDER BY "
                    + "  fatal_injuries.fatal_injury_id DESC "
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
    
}
