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
@Table(name = "anatomical_locations", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"anatomical_location_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AnatomicalLocations.findAll", query = "SELECT a FROM AnatomicalLocations a"),
    @NamedQuery(name = "AnatomicalLocations.findByAnatomicalLocationId", query = "SELECT a FROM AnatomicalLocations a WHERE a.anatomicalLocationId = :anatomicalLocationId"),
    @NamedQuery(name = "AnatomicalLocations.findByAnatomicalLocationName", query = "SELECT a FROM AnatomicalLocations a WHERE a.anatomicalLocationName = :anatomicalLocationName")})
public class AnatomicalLocations implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "anatomical_location_id", nullable = false)
    private Short anatomicalLocationId;
    @Size(max = 80)
    @Column(name = "anatomical_location_name", length = 80)
    private String anatomicalLocationName;
    
    @ManyToMany(mappedBy = "anatomicalLocationsList")    
    private List<NonFatalInjuries> nonFatalInjuriesList;

    public AnatomicalLocations() {
    }

    public AnatomicalLocations(Short anatomicalLocationId) {
	this.anatomicalLocationId = anatomicalLocationId;
    }

    public Short getAnatomicalLocationId() {
	return anatomicalLocationId;
    }

    public void setAnatomicalLocationId(Short anatomicalLocationId) {
	this.anatomicalLocationId = anatomicalLocationId;
    }

    public String getAnatomicalLocationName() {
	return anatomicalLocationName;
    }

    public void setAnatomicalLocationName(String anatomicalLocationName) {
	this.anatomicalLocationName = anatomicalLocationName;
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
	hash += (anatomicalLocationId != null ? anatomicalLocationId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof AnatomicalLocations)) {
	    return false;
	}
	AnatomicalLocations other = (AnatomicalLocations) object;
	if ((this.anatomicalLocationId == null && other.anatomicalLocationId != null) || (this.anatomicalLocationId != null && !this.anatomicalLocationId.equals(other.anatomicalLocationId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.AnatomicalLocations[ anatomicalLocationId=" + anatomicalLocationId + " ]";
    }
    
}
