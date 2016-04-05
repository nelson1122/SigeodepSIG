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
@Table(name = "destinations_of_patient", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"destination_patient_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DestinationsOfPatient.findAll", query = "SELECT d FROM DestinationsOfPatient d"),
    @NamedQuery(name = "DestinationsOfPatient.findByDestinationPatientId", query = "SELECT d FROM DestinationsOfPatient d WHERE d.destinationPatientId = :destinationPatientId"),
    @NamedQuery(name = "DestinationsOfPatient.findByDestinationPatientName", query = "SELECT d FROM DestinationsOfPatient d WHERE d.destinationPatientName = :destinationPatientName")})
public class DestinationsOfPatient implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "destination_patient_id", nullable = false)
    private Short destinationPatientId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "destination_patient_name", nullable = false, length = 30)
    private String destinationPatientName;
    @OneToMany(mappedBy = "destinationPatientId")
    private List<NonFatalInjuries> nonFatalInjuriesList;

    public DestinationsOfPatient() {
    }

    public DestinationsOfPatient(Short destinationPatientId) {
	this.destinationPatientId = destinationPatientId;
    }

    public DestinationsOfPatient(Short destinationPatientId, String destinationPatientName) {
	this.destinationPatientId = destinationPatientId;
	this.destinationPatientName = destinationPatientName;
    }

    public Short getDestinationPatientId() {
	return destinationPatientId;
    }

    public void setDestinationPatientId(Short destinationPatientId) {
	this.destinationPatientId = destinationPatientId;
    }

    public String getDestinationPatientName() {
	return destinationPatientName;
    }

    public void setDestinationPatientName(String destinationPatientName) {
	this.destinationPatientName = destinationPatientName;
    }

    @XmlTransient
    public List<NonFatalInjuries> getNonFatalInjuriesList() {
	return nonFatalInjuriesList;
    }

    public void setNonFatalInjuriesList(List<NonFatalInjuries> nonFatalInjuriesList) {
	this.nonFatalInjuriesList = nonFatalInjuriesList;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (destinationPatientId != null ? destinationPatientId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof DestinationsOfPatient)) {
	    return false;
	}
	DestinationsOfPatient other = (DestinationsOfPatient) object;
	if ((this.destinationPatientId == null && other.destinationPatientId != null) || (this.destinationPatientId != null && !this.destinationPatientId.equals(other.destinationPatientId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.DestinationsOfPatient[ destinationPatientId=" + destinationPatientId + " ]";
    }
    
}
