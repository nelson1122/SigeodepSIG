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
@Table(name = "non_fatal_places", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"non_fatal_place_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "NonFatalPlaces.findAll", query = "SELECT n FROM NonFatalPlaces n"),
    @NamedQuery(name = "NonFatalPlaces.findByNonFatalPlaceId", query = "SELECT n FROM NonFatalPlaces n WHERE n.nonFatalPlaceId = :nonFatalPlaceId"),
    @NamedQuery(name = "NonFatalPlaces.findByNonFatalPlaceName", query = "SELECT n FROM NonFatalPlaces n WHERE n.nonFatalPlaceName = :nonFatalPlaceName")})
public class NonFatalPlaces implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "non_fatal_place_id", nullable = false)
    private Short nonFatalPlaceId;
    @Size(max = 40)
    @Column(name = "non_fatal_place_name", length = 40)
    private String nonFatalPlaceName;
    @OneToMany(mappedBy = "injuryPlaceId")
    private List<NonFatalInjuries> nonFatalInjuriesList;

    public NonFatalPlaces() {
    }

    public NonFatalPlaces(Short nonFatalPlaceId) {
	this.nonFatalPlaceId = nonFatalPlaceId;
    }

    public Short getNonFatalPlaceId() {
	return nonFatalPlaceId;
    }

    public void setNonFatalPlaceId(Short nonFatalPlaceId) {
	this.nonFatalPlaceId = nonFatalPlaceId;
    }

    public String getNonFatalPlaceName() {
	return nonFatalPlaceName;
    }

    public void setNonFatalPlaceName(String nonFatalPlaceName) {
	this.nonFatalPlaceName = nonFatalPlaceName;
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
	hash += (nonFatalPlaceId != null ? nonFatalPlaceId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof NonFatalPlaces)) {
	    return false;
	}
	NonFatalPlaces other = (NonFatalPlaces) object;
	if ((this.nonFatalPlaceId == null && other.nonFatalPlaceId != null) || (this.nonFatalPlaceId != null && !this.nonFatalPlaceId.equals(other.nonFatalPlaceId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.NonFatalPlaces[ nonFatalPlaceId=" + nonFatalPlaceId + " ]";
    }
    
}
