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
@Table(name = "service_types", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"service_type_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ServiceTypes.findAll", query = "SELECT s FROM ServiceTypes s"),
    @NamedQuery(name = "ServiceTypes.findByServiceTypeId", query = "SELECT s FROM ServiceTypes s WHERE s.serviceTypeId = :serviceTypeId"),
    @NamedQuery(name = "ServiceTypes.findByServiceTypeName", query = "SELECT s FROM ServiceTypes s WHERE s.serviceTypeName = :serviceTypeName")})
public class ServiceTypes implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "service_type_id", nullable = false)
    private Short serviceTypeId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "service_type_name", nullable = false, length = 2147483647)
    private String serviceTypeName;
    
    @OneToMany(mappedBy = "serviceTypeId")
    private List<FatalInjuryTraffic> fatalInjuryTrafficList;

    public ServiceTypes() {
    }

    public ServiceTypes(Short serviceTypeId) {
	this.serviceTypeId = serviceTypeId;
    }

    public ServiceTypes(Short serviceTypeId, String serviceTypeName) {
	this.serviceTypeId = serviceTypeId;
	this.serviceTypeName = serviceTypeName;
    }

    public Short getServiceTypeId() {
	return serviceTypeId;
    }

    public void setServiceTypeId(Short serviceTypeId) {
	this.serviceTypeId = serviceTypeId;
    }

    public String getServiceTypeName() {
	return serviceTypeName;
    }

    public void setServiceTypeName(String serviceTypeName) {
	this.serviceTypeName = serviceTypeName;
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
	hash += (serviceTypeId != null ? serviceTypeId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof ServiceTypes)) {
	    return false;
	}
	ServiceTypes other = (ServiceTypes) object;
	if ((this.serviceTypeId == null && other.serviceTypeId != null) || (this.serviceTypeId != null && !this.serviceTypeId.equals(other.serviceTypeId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.ServiceTypes[ serviceTypeId=" + serviceTypeId + " ]";
    }
    
}
