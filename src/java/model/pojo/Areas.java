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
@Table(name = "areas", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"area_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Areas.findAll", query = "SELECT a FROM Areas a"),
    @NamedQuery(name = "Areas.findByAreaId", query = "SELECT a FROM Areas a WHERE a.areaId = :areaId"),
    @NamedQuery(name = "Areas.findByAreaName", query = "SELECT a FROM Areas a WHERE a.areaName = :areaName")})
public class Areas implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "area_id", nullable = false)
    private Short areaId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "area_name", nullable = false, length = 15)
    private String areaName;
    @Column(name = "population")
    private Integer population;
    @Size(max = 2147483647)
    @Column(name = "geom", length = 2147483647)
    private String geom;
    @OneToMany(mappedBy = "areaId")
    private List<FatalInjuries> fatalInjuriesList;
    @OneToMany(mappedBy = "area")
    private List<SivigilaEvent> sivigilaEventList;
    public Areas() {
    }

    public Areas(Short areaId) {
	this.areaId = areaId;
    }

    public Areas(Short areaId, String areaName) {
	this.areaId = areaId;
	this.areaName = areaName;
    }

    public Short getAreaId() {
	return areaId;
    }

    public void setAreaId(Short areaId) {
	this.areaId = areaId;
    }

    public String getAreaName() {
	return areaName;
    }

    public void setAreaName(String areaName) {
	this.areaName = areaName;
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
    public List<FatalInjuries> getFatalInjuriesList() {
	return fatalInjuriesList;
    }

    public void setFatalInjuriesList(List<FatalInjuries> fatalInjuriesList) {
	this.fatalInjuriesList = fatalInjuriesList;
    }

    @XmlTransient
    public List<SivigilaEvent> getSivigilaEventList() {
        return sivigilaEventList;
    }

    public void setSivigilaEventList(List<SivigilaEvent> sivigilaEventList) {
        this.sivigilaEventList = sivigilaEventList;
    }
    
    @Override
    public int hashCode() {
	int hash = 0;
	hash += (areaId != null ? areaId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof Areas)) {
	    return false;
	}
	Areas other = (Areas) object;
	if ((this.areaId == null && other.areaId != null) || (this.areaId != null && !this.areaId.equals(other.areaId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.Areas[ areaId=" + areaId + " ]";
    }
    
}
