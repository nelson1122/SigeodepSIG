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
@Table(name = "transport_counterparts", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"transport_counterpart_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransportCounterparts.findAll", query = "SELECT t FROM TransportCounterparts t"),
    @NamedQuery(name = "TransportCounterparts.findByTransportCounterpartId", query = "SELECT t FROM TransportCounterparts t WHERE t.transportCounterpartId = :transportCounterpartId"),
    @NamedQuery(name = "TransportCounterparts.findByTransportCounterpartName", query = "SELECT t FROM TransportCounterparts t WHERE t.transportCounterpartName = :transportCounterpartName")})
public class TransportCounterparts implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "transport_counterpart_id", nullable = false)
    private Short transportCounterpartId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "transport_counterpart_name", nullable = false, length = 30)
    private String transportCounterpartName;
    @OneToMany(mappedBy = "transportCounterpartId")
    private List<NonFatalTransport> nonFatalTransportList;

    public TransportCounterparts() {
    }

    public TransportCounterparts(Short transportCounterpartId) {
	this.transportCounterpartId = transportCounterpartId;
    }

    public TransportCounterparts(Short transportCounterpartId, String transportCounterpartName) {
	this.transportCounterpartId = transportCounterpartId;
	this.transportCounterpartName = transportCounterpartName;
    }

    public Short getTransportCounterpartId() {
	return transportCounterpartId;
    }

    public void setTransportCounterpartId(Short transportCounterpartId) {
	this.transportCounterpartId = transportCounterpartId;
    }

    public String getTransportCounterpartName() {
	return transportCounterpartName;
    }

    public void setTransportCounterpartName(String transportCounterpartName) {
	this.transportCounterpartName = transportCounterpartName;
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
	hash += (transportCounterpartId != null ? transportCounterpartId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof TransportCounterparts)) {
	    return false;
	}
	TransportCounterparts other = (TransportCounterparts) object;
	if ((this.transportCounterpartId == null && other.transportCounterpartId != null) || (this.transportCounterpartId != null && !this.transportCounterpartId.equals(other.transportCounterpartId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.TransportCounterparts[ transportCounterpartId=" + transportCounterpartId + " ]";
    }
    
}
