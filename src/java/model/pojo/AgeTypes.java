/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author SANTOS
 */
@Entity
@Table(name = "age_types", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AgeTypes.findAll", query = "SELECT a FROM AgeTypes a"),
    @NamedQuery(name = "AgeTypes.findByAgeTypeId", query = "SELECT a FROM AgeTypes a WHERE a.ageTypeId = :ageTypeId"),
    @NamedQuery(name = "AgeTypes.findByAgeTypeName", query = "SELECT a FROM AgeTypes a WHERE a.ageTypeName = :ageTypeName")})
public class AgeTypes implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "age_type_id", nullable = false)
    private Short ageTypeId;
    @Size(max = 2147483647)
    @Column(name = "age_type_name", length = 2147483647)
    private String ageTypeName;

    public AgeTypes() {
    }

    public AgeTypes(Short ageTypeId) {
	this.ageTypeId = ageTypeId;
    }

    public Short getAgeTypeId() {
	return ageTypeId;
    }

    public void setAgeTypeId(Short ageTypeId) {
	this.ageTypeId = ageTypeId;
    }

    public String getAgeTypeName() {
	return ageTypeName;
    }

    public void setAgeTypeName(String ageTypeName) {
	this.ageTypeName = ageTypeName;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (ageTypeId != null ? ageTypeId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof AgeTypes)) {
	    return false;
	}
	AgeTypes other = (AgeTypes) object;
	if ((this.ageTypeId == null && other.ageTypeId != null) || (this.ageTypeId != null && !this.ageTypeId.equals(other.ageTypeId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.AgeTypes[ ageTypeId=" + ageTypeId + " ]";
    }
    
}
