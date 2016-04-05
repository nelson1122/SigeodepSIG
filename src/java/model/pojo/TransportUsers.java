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
@Table(name = "transport_users", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"transport_user_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransportUsers.findAll", query = "SELECT t FROM TransportUsers t"),
    @NamedQuery(name = "TransportUsers.findByTransportUserId", query = "SELECT t FROM TransportUsers t WHERE t.transportUserId = :transportUserId"),
    @NamedQuery(name = "TransportUsers.findByTransportUserName", query = "SELECT t FROM TransportUsers t WHERE t.transportUserName = :transportUserName")})
public class TransportUsers implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "transport_user_id", nullable = false)
    private Short transportUserId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "transport_user_name", nullable = false, length = 15)
    private String transportUserName;
    @OneToMany(mappedBy = "transportUserId")
    private List<NonFatalTransport> nonFatalTransportList;

    public TransportUsers() {
    }

    public TransportUsers(Short transportUserId) {
	this.transportUserId = transportUserId;
    }

    public TransportUsers(Short transportUserId, String transportUserName) {
	this.transportUserId = transportUserId;
	this.transportUserName = transportUserName;
    }

    public Short getTransportUserId() {
	return transportUserId;
    }

    public void setTransportUserId(Short transportUserId) {
	this.transportUserId = transportUserId;
    }

    public String getTransportUserName() {
	return transportUserName;
    }

    public void setTransportUserName(String transportUserName) {
	this.transportUserName = transportUserName;
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
	hash += (transportUserId != null ? transportUserId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof TransportUsers)) {
	    return false;
	}
	TransportUsers other = (TransportUsers) object;
	if ((this.transportUserId == null && other.transportUserId != null) || (this.transportUserId != null && !this.transportUserId.equals(other.transportUserId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.TransportUsers[ transportUserId=" + transportUserId + " ]";
    }
    
}
