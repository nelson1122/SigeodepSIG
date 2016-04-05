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
@Table(name = "places", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"place_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Places.findAll", query = "SELECT p FROM Places p"),
    @NamedQuery(name = "Places.findByPlaceId", query = "SELECT p FROM Places p WHERE p.placeId = :placeId"),
    @NamedQuery(name = "Places.findByPlaceName", query = "SELECT p FROM Places p WHERE p.placeName = :placeName")})
public class Places implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "place_id", nullable = false)
    private Short placeId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "place_name", nullable = false, length = 2147483647)
    private String placeName;
    @OneToMany(mappedBy = "injuryPlaceId")
    private List<FatalInjuries> fatalInjuriesList;

    public Places() {
    }

    public Places(Short placeId) {
	this.placeId = placeId;
    }

    public Places(Short placeId, String placeName) {
	this.placeId = placeId;
	this.placeName = placeName;
    }

    public Short getPlaceId() {
	return placeId;
    }

    public void setPlaceId(Short placeId) {
	this.placeId = placeId;
    }

    public String getPlaceName() {
	return placeName;
    }

    public void setPlaceName(String placeName) {
	this.placeName = placeName;
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
	hash += (placeId != null ? placeId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof Places)) {
	    return false;
	}
	Places other = (Places) object;
	if ((this.placeId == null && other.placeId != null) || (this.placeId != null && !this.placeId.equals(other.placeId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.Places[ placeId=" + placeId + " ]";
    }
    
}
