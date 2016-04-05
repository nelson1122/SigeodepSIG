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
@Table(name = "transport_types", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"transport_type_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransportTypes.findAll", query = "SELECT t FROM TransportTypes t"),
    @NamedQuery(name = "TransportTypes.findByTransportTypeId", query = "SELECT t FROM TransportTypes t WHERE t.transportTypeId = :transportTypeId"),
    @NamedQuery(name = "TransportTypes.findByTransportTypeName", query = "SELECT t FROM TransportTypes t WHERE t.transportTypeName = :transportTypeName")})
public class TransportTypes implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "transport_type_id", nullable = false)
    private Short transportTypeId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "transport_type_name", nullable = false, length = 30)
    private String transportTypeName;
    @OneToMany(mappedBy = "transportTypeId")
    private List<NonFatalTransport> nonFatalTransportList;

    public TransportTypes() {
    }

    public TransportTypes(Short transportTypeId) {
	this.transportTypeId = transportTypeId;
    }

    public TransportTypes(Short transportTypeId, String transportTypeName) {
	this.transportTypeId = transportTypeId;
	this.transportTypeName = transportTypeName;
    }

    public Short getTransportTypeId() {
	return transportTypeId;
    }

    public void setTransportTypeId(Short transportTypeId) {
	this.transportTypeId = transportTypeId;
    }

    public String getTransportTypeName() {
	return transportTypeName;
    }

    public void setTransportTypeName(String transportTypeName) {
	this.transportTypeName = transportTypeName;
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
	hash += (transportTypeId != null ? transportTypeId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof TransportTypes)) {
	    return false;
	}
	TransportTypes other = (TransportTypes) object;
	if ((this.transportTypeId == null && other.transportTypeId != null) || (this.transportTypeId != null && !this.transportTypeId.equals(other.transportTypeId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.TransportTypes[ transportTypeId=" + transportTypeId + " ]";
    }
    
}
