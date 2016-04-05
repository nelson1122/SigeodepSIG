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
@Table(name = "involved_vehicles", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"involved_vehicle_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "InvolvedVehicles.findAll", query = "SELECT i FROM InvolvedVehicles i"),
    @NamedQuery(name = "InvolvedVehicles.findByInvolvedVehicleId", query = "SELECT i FROM InvolvedVehicles i WHERE i.involvedVehicleId = :involvedVehicleId"),
    @NamedQuery(name = "InvolvedVehicles.findByInvolvedVehicleName", query = "SELECT i FROM InvolvedVehicles i WHERE i.involvedVehicleName = :involvedVehicleName")})
public class InvolvedVehicles implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "involved_vehicle_id", nullable = false)
    private Short involvedVehicleId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "involved_vehicle_name", nullable = false, length = 2147483647)
    private String involvedVehicleName;
    @OneToMany(mappedBy = "involvedVehicleId")
    private List<FatalInjuryTraffic> fatalInjuryTrafficList;

    public InvolvedVehicles() {
    }

    public InvolvedVehicles(Short involvedVehicleId) {
	this.involvedVehicleId = involvedVehicleId;
    }

    public InvolvedVehicles(Short involvedVehicleId, String involvedVehicleName) {
	this.involvedVehicleId = involvedVehicleId;
	this.involvedVehicleName = involvedVehicleName;
    }

    public Short getInvolvedVehicleId() {
	return involvedVehicleId;
    }

    public void setInvolvedVehicleId(Short involvedVehicleId) {
	this.involvedVehicleId = involvedVehicleId;
    }

    public String getInvolvedVehicleName() {
	return involvedVehicleName;
    }

    public void setInvolvedVehicleName(String involvedVehicleName) {
	this.involvedVehicleName = involvedVehicleName;
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
	hash += (involvedVehicleId != null ? involvedVehicleId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof InvolvedVehicles)) {
	    return false;
	}
	InvolvedVehicles other = (InvolvedVehicles) object;
	if ((this.involvedVehicleId == null && other.involvedVehicleId != null) || (this.involvedVehicleId != null && !this.involvedVehicleId.equals(other.involvedVehicleId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.InvolvedVehicles[ involvedVehicleId=" + involvedVehicleId + " ]";
    }
    
}
