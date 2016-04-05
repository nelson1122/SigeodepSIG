/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author SANTOS
 */
@Entity
@Table(name = "non_fatal_transport", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "NonFatalTransport.findAll", query = "SELECT n FROM NonFatalTransport n"),
    @NamedQuery(name = "NonFatalTransport.findByNonFatalInjuryId", query = "SELECT n FROM NonFatalTransport n WHERE n.nonFatalInjuryId = :nonFatalInjuryId")})
public class NonFatalTransport implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "non_fatal_injury_id", nullable = false)
    private Integer nonFatalInjuryId;
    @JoinTable(name = "non_fatal_transport_security_element", joinColumns = {
        @JoinColumn(name = "non_fatal_injury_id", referencedColumnName = "non_fatal_injury_id", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "security_element_id", referencedColumnName = "security_element_id", nullable = false)})
    @ManyToMany
    private List<SecurityElements> securityElementsList;
    @JoinColumn(name = "transport_user_id", referencedColumnName = "transport_user_id")
    @ManyToOne
    private TransportUsers transportUserId;
    @JoinColumn(name = "transport_type_id", referencedColumnName = "transport_type_id")
    @ManyToOne
    private TransportTypes transportTypeId;
    @JoinColumn(name = "transport_counterpart_id", referencedColumnName = "transport_counterpart_id")
    @ManyToOne
    private TransportCounterparts transportCounterpartId;
    @JoinColumn(name = "non_fatal_injury_id", referencedColumnName = "non_fatal_injury_id", nullable = false, insertable = false, updatable = false)
    @OneToOne(optional = false)
    private NonFatalInjuries nonFatalInjuries;
    
    public NonFatalTransport() {
    }
    
    public NonFatalTransport(Integer nonFatalInjuryId) {
	this.nonFatalInjuryId = nonFatalInjuryId;
    }

    public Integer getNonFatalInjuryId() {
	return nonFatalInjuryId;
    }

    public void setNonFatalInjuryId(Integer nonFatalInjuryId) {
	this.nonFatalInjuryId = nonFatalInjuryId;
    }

    @XmlTransient
    public List<SecurityElements> getSecurityElementsList() {
	return securityElementsList;
    }

    public void setSecurityElementsList(List<SecurityElements> securityElementsList) {
	this.securityElementsList = securityElementsList;
    }

    public TransportUsers getTransportUserId() {
	return transportUserId;
    }

    public void setTransportUserId(TransportUsers transportUserId) {
	this.transportUserId = transportUserId;
    }

    public TransportTypes getTransportTypeId() {
	return transportTypeId;
    }

    public void setTransportTypeId(TransportTypes transportTypeId) {
	this.transportTypeId = transportTypeId;
    }

    public TransportCounterparts getTransportCounterpartId() {
	return transportCounterpartId;
    }

    public void setTransportCounterpartId(TransportCounterparts transportCounterpartId) {
	this.transportCounterpartId = transportCounterpartId;
    }

    public NonFatalInjuries getNonFatalInjuries() {
	return nonFatalInjuries;
    }

    public void setNonFatalInjuries(NonFatalInjuries nonFatalInjuries) {
	this.nonFatalInjuries = nonFatalInjuries;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (nonFatalInjuryId != null ? nonFatalInjuryId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof NonFatalTransport)) {
	    return false;
	}
	NonFatalTransport other = (NonFatalTransport) object;
	if ((this.nonFatalInjuryId == null && other.nonFatalInjuryId != null) || (this.nonFatalInjuryId != null && !this.nonFatalInjuryId.equals(other.nonFatalInjuryId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.NonFatalTransport[ nonFatalInjuryId=" + nonFatalInjuryId + " ]";
    }
    
}
