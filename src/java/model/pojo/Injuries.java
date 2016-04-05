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
@Table(name = "injuries", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"injury_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Injuries.findAll", query = "SELECT i FROM Injuries i"),
    @NamedQuery(name = "Injuries.findByInjuryId", query = "SELECT i FROM Injuries i WHERE i.injuryId = :injuryId"),
    @NamedQuery(name = "Injuries.findByInjuryName", query = "SELECT i FROM Injuries i WHERE i.injuryName = :injuryName")})
public class Injuries implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "injury_id", nullable = false)
    private Short injuryId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "injury_name", nullable = false, length = 50)
    private String injuryName;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "injuryId")
    private List<NonFatalInjuries> nonFatalInjuriesList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "injuryId")
    private List<FatalInjuries> fatalInjuriesList;

    public Injuries() {
    }

    public Injuries(Short injuryId) {
	this.injuryId = injuryId;
    }

    public Injuries(Short injuryId, String injuryName) {
	this.injuryId = injuryId;
	this.injuryName = injuryName;
    }

    public Short getInjuryId() {
	return injuryId;
    }

    public void setInjuryId(Short injuryId) {
	this.injuryId = injuryId;
    }

    public String getInjuryName() {
	return injuryName;
    }

    public void setInjuryName(String injuryName) {
	this.injuryName = injuryName;
    }

    @XmlTransient
    public List<NonFatalInjuries> getNonFatalInjuriesList() {
	return nonFatalInjuriesList;
    }

    public void setNonFatalInjuriesList(List<NonFatalInjuries> nonFatalInjuriesList) {
	this.nonFatalInjuriesList = nonFatalInjuriesList;
    }

    @XmlTransient
    public List<FatalInjuries> getFatalInjuriesList() {
	return fatalInjuriesList;
    }

    public void setFatalInjuriesList(List<FatalInjuries> fatalInjuriesList) {
	this.fatalInjuriesList = fatalInjuriesList;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (injuryId != null ? injuryId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof Injuries)) {
	    return false;
	}
	Injuries other = (Injuries) object;
	if ((this.injuryId == null && other.injuryId != null) || (this.injuryId != null && !this.injuryId.equals(other.injuryId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.Injuries[ injuryId=" + injuryId + " ]";
    }
    
}
