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
@Table(name = "alcohol_levels", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"alcohol_level_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AlcoholLevels.findAll", query = "SELECT a FROM AlcoholLevels a"),
    @NamedQuery(name = "AlcoholLevels.findByAlcoholLevelId", query = "SELECT a FROM AlcoholLevels a WHERE a.alcoholLevelId = :alcoholLevelId"),
    @NamedQuery(name = "AlcoholLevels.findByAlcoholLevelName", query = "SELECT a FROM AlcoholLevels a WHERE a.alcoholLevelName = :alcoholLevelName")})
public class AlcoholLevels implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "alcohol_level_id", nullable = false)
    private Short alcoholLevelId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "alcohol_level_name", nullable = false, length = 2147483647)
    private String alcoholLevelName;
    @OneToMany(mappedBy = "alcoholLevelVictimId")
    private List<FatalInjuries> fatalInjuriesList;
    @OneToMany(mappedBy = "alcoholLevelCounterpartId")
    private List<FatalInjuryTraffic> fatalInjuryTrafficList;

    public AlcoholLevels() {
    }

    public AlcoholLevels(Short alcoholLevelId) {
	this.alcoholLevelId = alcoholLevelId;
    }

    public AlcoholLevels(Short alcoholLevelId, String alcoholLevelName) {
	this.alcoholLevelId = alcoholLevelId;
	this.alcoholLevelName = alcoholLevelName;
    }

    public Short getAlcoholLevelId() {
	return alcoholLevelId;
    }

    public void setAlcoholLevelId(Short alcoholLevelId) {
	this.alcoholLevelId = alcoholLevelId;
    }

    public String getAlcoholLevelName() {
	return alcoholLevelName;
    }

    public void setAlcoholLevelName(String alcoholLevelName) {
	this.alcoholLevelName = alcoholLevelName;
    }

    @XmlTransient
    public List<FatalInjuries> getFatalInjuriesList() {
	return fatalInjuriesList;
    }

    public void setFatalInjuriesList(List<FatalInjuries> fatalInjuriesList) {
	this.fatalInjuriesList = fatalInjuriesList;
    }

    @XmlTransient
    public List<FatalInjuryTraffic> getFatalInjuryTrafficList() {
	return fatalInjuryTrafficList;
    }

    public void setFatalInjuryTrafficList(List<FatalInjuryTraffic> fatalInjuryTrafficList) {
	this.fatalInjuryTrafficList = fatalInjuryTrafficList;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (alcoholLevelId != null ? alcoholLevelId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof AlcoholLevels)) {
	    return false;
	}
	AlcoholLevels other = (AlcoholLevels) object;
	if ((this.alcoholLevelId == null && other.alcoholLevelId != null) || (this.alcoholLevelId != null && !this.alcoholLevelId.equals(other.alcoholLevelId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.AlcoholLevels[ alcoholLevelId=" + alcoholLevelId + " ]";
    }
    
}
