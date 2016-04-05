/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author SANTOS
 */
@Entity
@Table(name = "neighborhoods", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Neighborhoods.findAll", query = "SELECT n FROM Neighborhoods n"),
    @NamedQuery(name = "Neighborhoods.findByNeighborhoodName", query = "SELECT n FROM Neighborhoods n WHERE n.neighborhoodName = :neighborhoodName"),
    @NamedQuery(name = "Neighborhoods.findByNeighborhoodId", query = "SELECT n FROM Neighborhoods n WHERE n.neighborhoodId = :neighborhoodId"),    
    @NamedQuery(name = "Neighborhoods.findByNeighborhoodLevel", query = "SELECT n FROM Neighborhoods n WHERE n.neighborhoodLevel = :neighborhoodLevel"),
    @NamedQuery(name = "Neighborhoods.findByNeighborhoodArea", query = "SELECT n FROM Neighborhoods n WHERE n.neighborhoodArea = :neighborhoodArea")})
public class Neighborhoods implements Serializable {
    private static final long serialVersionUID = 1L;
    @Size(max = 75)
    @Column(name = "neighborhood_name", length = 75)
    private String neighborhoodName;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "neighborhood_id", nullable = false)
    private Integer neighborhoodId;
    
    @Column(name = "neighborhood_quadrant")
    private Short neighborhoodQuadrant;
    @Column(name = "neighborhood_suburb")
    private Short neighborhoodSuburb;
    @Column(name = "neighborhood_area")
    private Short neighborhoodArea;
    @Column(name = "neighborhood_corridor")
    private Short neighborhoodCorridor;
    @Column(name = "neighborhood_level")
    private Short neighborhoodLevel;
    
    @Column(name = "population")
    private Integer population;
    @Size(max = 2147483647)
    @Column(name = "geom", length = 2147483647)
    private String geom;
       
    @OneToMany(mappedBy = "victimNeighborhoodId")
    private List<Victims> victimsList;
    @OneToMany(mappedBy = "injuryNeighborhoodId")
    private List<NonFatalInjuries> nonFatalInjuriesList;

    public Neighborhoods() {
    }

    public Neighborhoods(Integer neighborhoodId) {
	this.neighborhoodId = neighborhoodId;
    }

    public String getNeighborhoodName() {
	return neighborhoodName;
    }

    public void setNeighborhoodName(String neighborhoodName) {
	this.neighborhoodName = neighborhoodName;
    }

    public Integer getNeighborhoodId() {
	return neighborhoodId;
    }

    public void setNeighborhoodId(Integer neighborhoodId) {
	this.neighborhoodId = neighborhoodId;
    }

    

    public Short getNeighborhoodQuadrant() {
        return neighborhoodQuadrant;
    }

    public void setNeighborhoodQuadrant(Short neighborhoodQuadrant) {
        this.neighborhoodQuadrant = neighborhoodQuadrant;
    }

    public Short getNeighborhoodSuburb() {
        return neighborhoodSuburb;
    }

    public void setNeighborhoodSuburb(Short neighborhoodSuburb) {
        this.neighborhoodSuburb = neighborhoodSuburb;
    }

    public Short getNeighborhoodArea() {
        return neighborhoodArea;
    }

    public void setNeighborhoodArea(Short neighborhoodArea) {
        this.neighborhoodArea = neighborhoodArea;
    }

    public Short getNeighborhoodCorridor() {
        return neighborhoodCorridor;
    }

    public void setNeighborhoodCorridor(Short neighborhoodCorridor) {
        this.neighborhoodCorridor = neighborhoodCorridor;
    }

    public Short getNeighborhoodLevel() {
        return neighborhoodLevel;
    }

    public void setNeighborhoodLevel(Short neighborhoodLevel) {
        this.neighborhoodLevel = neighborhoodLevel;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public String getGeom() {
        return geom;
    }

    public void setGeom(String geom) {
        this.geom = geom;
    }


    @XmlTransient
    public List<Victims> getVictimsList() {
	return victimsList;
    }

    public void setVictimsList(List<Victims> victimsList) {
	this.victimsList = victimsList;
    }

    @XmlTransient
    public List<NonFatalInjuries> getNonFatalInjuriesList() {
	return nonFatalInjuriesList;
    }

    public void setNonFatalInjuriesList(List<NonFatalInjuries> nonFatalInjuriesList) {
	this.nonFatalInjuriesList = nonFatalInjuriesList;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (neighborhoodId != null ? neighborhoodId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof Neighborhoods)) {
	    return false;
	}
	Neighborhoods other = (Neighborhoods) object;
	if ((this.neighborhoodId == null && other.neighborhoodId != null) || (this.neighborhoodId != null && !this.neighborhoodId.equals(other.neighborhoodId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.Neighborhoods[ neighborhoodId=" + neighborhoodId + " ]";
    }
    
}
