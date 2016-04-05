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
@Table(name = "aggressor_genders", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"gender_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AggressorGenders.findAll", query = "SELECT a FROM AggressorGenders a"),
    @NamedQuery(name = "AggressorGenders.findByGenderId", query = "SELECT a FROM AggressorGenders a WHERE a.genderId = :genderId"),
    @NamedQuery(name = "AggressorGenders.findByGenderName", query = "SELECT a FROM AggressorGenders a WHERE a.genderName = :genderName")})
public class AggressorGenders implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "gender_id", nullable = false)
    private Short genderId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 12)
    @Column(name = "gender_name", nullable = false, length = 12)
    private String genderName;
    @OneToMany(mappedBy = "aggressorGenderId")
    private List<NonFatalInterpersonal> nonFatalInterpersonalList;

    public AggressorGenders() {
    }

    public AggressorGenders(Short genderId) {
	this.genderId = genderId;
    }

    public AggressorGenders(Short genderId, String genderName) {
	this.genderId = genderId;
	this.genderName = genderName;
    }

    public Short getGenderId() {
	return genderId;
    }

    public void setGenderId(Short genderId) {
	this.genderId = genderId;
    }

    public String getGenderName() {
	return genderName;
    }

    public void setGenderName(String genderName) {
	this.genderName = genderName;
    }

    @XmlTransient
    public List<NonFatalInterpersonal> getNonFatalInterpersonalList() {
	return nonFatalInterpersonalList;
    }

    public void setNonFatalInterpersonalList(List<NonFatalInterpersonal> nonFatalInterpersonalList) {
	this.nonFatalInterpersonalList = nonFatalInterpersonalList;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (genderId != null ? genderId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof AggressorGenders)) {
	    return false;
	}
	AggressorGenders other = (AggressorGenders) object;
	if ((this.genderId == null && other.genderId != null) || (this.genderId != null && !this.genderId.equals(other.genderId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.AggressorGenders[ genderId=" + genderId + " ]";
    }
    
}
