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
@Table(name = "security_elements", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"security_element_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SecurityElements.findAll", query = "SELECT s FROM SecurityElements s"),
    @NamedQuery(name = "SecurityElements.findBySecurityElementId", query = "SELECT s FROM SecurityElements s WHERE s.securityElementId = :securityElementId"),
    @NamedQuery(name = "SecurityElements.findBySecurityElementName", query = "SELECT s FROM SecurityElements s WHERE s.securityElementName = :securityElementName")})
public class SecurityElements implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "security_element_id", nullable = false)
    private Short securityElementId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "security_element_name", nullable = false, length = 30)
    private String securityElementName;
    @ManyToMany(mappedBy = "securityElementsList")
    private List<NonFatalTransport> nonFatalTransportList;

    public SecurityElements() {
    }

    public SecurityElements(Short securityElementId) {
	this.securityElementId = securityElementId;
    }

    public SecurityElements(Short securityElementId, String securityElementName) {
	this.securityElementId = securityElementId;
	this.securityElementName = securityElementName;
    }

    public Short getSecurityElementId() {
	return securityElementId;
    }

    public void setSecurityElementId(Short securityElementId) {
	this.securityElementId = securityElementId;
    }

    public String getSecurityElementName() {
	return securityElementName;
    }

    public void setSecurityElementName(String securityElementName) {
	this.securityElementName = securityElementName;
    }

    @XmlTransient
    public List<NonFatalTransport> getNonFatalTransportList() {
	return nonFatalTransportList;
    }

    public void setNonFatalTransportList(List<NonFatalTransport> nonFatalTransportList) {
	this.nonFatalTransportList = nonFatalTransportList;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (securityElementId != null ? securityElementId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof SecurityElements)) {
	    return false;
	}
	SecurityElements other = (SecurityElements) object;
	if ((this.securityElementId == null && other.securityElementId != null) || (this.securityElementId != null && !this.securityElementId.equals(other.securityElementId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.SecurityElements[ securityElementId=" + securityElementId + " ]";
    }
    
}
