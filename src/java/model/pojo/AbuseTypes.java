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
@Table(name = "abuse_types", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"abuse_type_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AbuseTypes.findAll", query = "SELECT a FROM AbuseTypes a"),
    @NamedQuery(name = "AbuseTypes.findByAbuseTypeId", query = "SELECT a FROM AbuseTypes a WHERE a.abuseTypeId = :abuseTypeId"),
    @NamedQuery(name = "AbuseTypes.findByAbuseTypeName", query = "SELECT a FROM AbuseTypes a WHERE a.abuseTypeName = :abuseTypeName")})
public class AbuseTypes implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "abuse_type_id", nullable = false)
    private Short abuseTypeId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "abuse_type_name", nullable = false, length = 25)
    private String abuseTypeName;
    @ManyToMany(mappedBy = "abuseTypesList")
    private List<NonFatalDomesticViolence> nonFatalDomesticViolenceList;

    public AbuseTypes() {
    }

    public AbuseTypes(Short abuseTypeId) {
	this.abuseTypeId = abuseTypeId;
    }

    public AbuseTypes(Short abuseTypeId, String abuseTypeName) {
	this.abuseTypeId = abuseTypeId;
	this.abuseTypeName = abuseTypeName;
    }

    public Short getAbuseTypeId() {
	return abuseTypeId;
    }

    public void setAbuseTypeId(Short abuseTypeId) {
	this.abuseTypeId = abuseTypeId;
    }

    public String getAbuseTypeName() {
	return abuseTypeName;
    }

    public void setAbuseTypeName(String abuseTypeName) {
	this.abuseTypeName = abuseTypeName;
    }

    @XmlTransient
    public List<NonFatalDomesticViolence> getNonFatalDomesticViolenceList() {
	return nonFatalDomesticViolenceList;
    }

    public void setNonFatalDomesticViolenceList(List<NonFatalDomesticViolence> nonFatalDomesticViolenceList) {
	this.nonFatalDomesticViolenceList = nonFatalDomesticViolenceList;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (abuseTypeId != null ? abuseTypeId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof AbuseTypes)) {
	    return false;
	}
	AbuseTypes other = (AbuseTypes) object;
	if ((this.abuseTypeId == null && other.abuseTypeId != null) || (this.abuseTypeId != null && !this.abuseTypeId.equals(other.abuseTypeId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.AbuseTypes[ abuseTypeId=" + abuseTypeId + " ]";
    }
    
}
