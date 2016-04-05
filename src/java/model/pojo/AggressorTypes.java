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
@Table(name = "aggressor_types", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"aggressor_type_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AggressorTypes.findAll", query = "SELECT a FROM AggressorTypes a"),
    @NamedQuery(name = "AggressorTypes.findByAggressorTypeId", query = "SELECT a FROM AggressorTypes a WHERE a.aggressorTypeId = :aggressorTypeId"),
    @NamedQuery(name = "AggressorTypes.findByAggressorTypeName", query = "SELECT a FROM AggressorTypes a WHERE a.aggressorTypeName = :aggressorTypeName")})
public class AggressorTypes implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "aggressor_type_id", nullable = false)
    private Short aggressorTypeId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "aggressor_type_name", nullable = false, length = 45)
    private String aggressorTypeName;
    @ManyToMany(mappedBy = "aggressorTypesList")
    private List<NonFatalDomesticViolence> nonFatalDomesticViolenceList;
    @OneToMany(mappedBy = "relativeId")
    private List<SivigilaAggresor> sivigilaAggresorList;

    public AggressorTypes() {
    }

    public AggressorTypes(Short aggressorTypeId) {
	this.aggressorTypeId = aggressorTypeId;
    }

    public AggressorTypes(Short aggressorTypeId, String aggressorTypeName) {
	this.aggressorTypeId = aggressorTypeId;
	this.aggressorTypeName = aggressorTypeName;
    }

    public Short getAggressorTypeId() {
	return aggressorTypeId;
    }

    public void setAggressorTypeId(Short aggressorTypeId) {
	this.aggressorTypeId = aggressorTypeId;
    }

    public String getAggressorTypeName() {
	return aggressorTypeName;
    }

    public void setAggressorTypeName(String aggressorTypeName) {
	this.aggressorTypeName = aggressorTypeName;
    }

    @XmlTransient
    public List<NonFatalDomesticViolence> getNonFatalDomesticViolenceList() {
	return nonFatalDomesticViolenceList;
    }

    public void setNonFatalDomesticViolenceList(List<NonFatalDomesticViolence> nonFatalDomesticViolenceList) {
	this.nonFatalDomesticViolenceList = nonFatalDomesticViolenceList;
    }

    @XmlTransient
    public List<SivigilaAggresor> getSivigilaAggresorList() {
        return sivigilaAggresorList;
    }

    public void setSivigilaAggresorList(List<SivigilaAggresor> sivigilaAggresorList) {
        this.sivigilaAggresorList = sivigilaAggresorList;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (aggressorTypeId != null ? aggressorTypeId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof AggressorTypes)) {
	    return false;
	}
	AggressorTypes other = (AggressorTypes) object;
	if ((this.aggressorTypeId == null && other.aggressorTypeId != null) || (this.aggressorTypeId != null && !this.aggressorTypeId.equals(other.aggressorTypeId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.AggressorTypes[ aggressorTypeId=" + aggressorTypeId + " ]";
    }
    
}
