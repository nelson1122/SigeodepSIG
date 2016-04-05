/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author SANTOS
 */
@Entity
@Table(name = "counterpart_involved_vehicle", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CounterpartInvolvedVehicle.findAll", query = "SELECT c FROM CounterpartInvolvedVehicle c"),
    @NamedQuery(name = "CounterpartInvolvedVehicle.findByCounterpartInvolvedVehicleId", query = "SELECT c FROM CounterpartInvolvedVehicle c WHERE c.counterpartInvolvedVehicleId = :counterpartInvolvedVehicleId")})
public class CounterpartInvolvedVehicle implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "counterpart_involved_vehicle_id", nullable = false)
    private Integer counterpartInvolvedVehicleId;
    @JoinColumn(name = "involved_vehicle_id", referencedColumnName = "involved_vehicle_id", nullable = false)
    @ManyToOne(optional = false)
    private InvolvedVehicles involvedVehicleId;
    @JoinColumn(name = "fatal_injury_id", referencedColumnName = "fatal_injury_id", nullable = false)
    @ManyToOne(optional = false)
    private FatalInjuries fatalInjuryId;

    public CounterpartInvolvedVehicle() {
    }

    public CounterpartInvolvedVehicle(Integer counterpartInvolvedVehicleId) {
        this.counterpartInvolvedVehicleId = counterpartInvolvedVehicleId;
    }

    public Integer getCounterpartInvolvedVehicleId() {
        return counterpartInvolvedVehicleId;
    }

    public void setCounterpartInvolvedVehicleId(Integer counterpartInvolvedVehicleId) {
        this.counterpartInvolvedVehicleId = counterpartInvolvedVehicleId;
    }

    public InvolvedVehicles getInvolvedVehicleId() {
        return involvedVehicleId;
    }

    public void setInvolvedVehicleId(InvolvedVehicles involvedVehicleId) {
        this.involvedVehicleId = involvedVehicleId;
    }

    public FatalInjuries getFatalInjuryId() {
        return fatalInjuryId;
    }

    public void setFatalInjuryId(FatalInjuries fatalInjuryId) {
        this.fatalInjuryId = fatalInjuryId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (counterpartInvolvedVehicleId != null ? counterpartInvolvedVehicleId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CounterpartInvolvedVehicle)) {
            return false;
        }
        CounterpartInvolvedVehicle other = (CounterpartInvolvedVehicle) object;
        if ((this.counterpartInvolvedVehicleId == null && other.counterpartInvolvedVehicleId != null) || (this.counterpartInvolvedVehicleId != null && !this.counterpartInvolvedVehicleId.equals(other.counterpartInvolvedVehicleId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pojos.CounterpartInvolvedVehicle[ counterpartInvolvedVehicleId=" + counterpartInvolvedVehicleId + " ]";
    }
    
}
