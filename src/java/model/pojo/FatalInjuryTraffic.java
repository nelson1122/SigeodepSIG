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
@Table(name = "fatal_injury_traffic", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FatalInjuryTraffic.findAll", query = "SELECT f FROM FatalInjuryTraffic f"),
    @NamedQuery(name = "FatalInjuryTraffic.findByNumberNonFatalVictims", query = "SELECT f FROM FatalInjuryTraffic f WHERE f.numberNonFatalVictims = :numberNonFatalVictims"),
    @NamedQuery(name = "FatalInjuryTraffic.findByAlcoholLevelCounterpart", query = "SELECT f FROM FatalInjuryTraffic f WHERE f.alcoholLevelCounterpart = :alcoholLevelCounterpart"),
    @NamedQuery(name = "FatalInjuryTraffic.findByFatalInjuryId", query = "SELECT f FROM FatalInjuryTraffic f WHERE f.fatalInjuryId = :fatalInjuryId")})
public class FatalInjuryTraffic implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column(name = "number_non_fatal_victims")
    private Short numberNonFatalVictims;
    @Column(name = "alcohol_level_counterpart")
    private Short alcoholLevelCounterpart;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "fatal_injury_id", nullable = false)
    private Integer fatalInjuryId;
    @JoinColumn(name = "victim_characteristic_id", referencedColumnName = "characteristic_id")
    @ManyToOne
    private VictimCharacteristics victimCharacteristicId;
    @JoinColumn(name = "service_type_id", referencedColumnName = "service_type_id")
    @ManyToOne
    private ServiceTypes serviceTypeId;
    @JoinColumn(name = "road_type_id", referencedColumnName = "road_type_id")
    @ManyToOne
    private RoadTypes roadTypeId;
    @JoinColumn(name = "protection_measure_id", referencedColumnName = "protective_measures_id")
    @ManyToOne
    private ProtectiveMeasures protectionMeasureId;
    @JoinColumn(name = "involved_vehicle_id", referencedColumnName = "involved_vehicle_id")
    @ManyToOne
    private InvolvedVehicles involvedVehicleId;
    @JoinColumn(name = "fatal_injury_id", referencedColumnName = "fatal_injury_id", nullable = false, insertable = false, updatable = false)
    @OneToOne(optional = false)
    private FatalInjuries fatalInjuries;
    @JoinColumn(name = "alcohol_level_counterpart_id", referencedColumnName = "alcohol_level_id")
    @ManyToOne
    private AlcoholLevels alcoholLevelCounterpartId;
    @JoinColumn(name = "accident_class_id", referencedColumnName = "accident_class_id")
    @ManyToOne
    private AccidentClasses accidentClassId;

    public FatalInjuryTraffic() {
    }

    public FatalInjuryTraffic(Integer fatalInjuryId) {
	this.fatalInjuryId = fatalInjuryId;
    }

    public Short getNumberNonFatalVictims() {
	return numberNonFatalVictims;
    }

    public void setNumberNonFatalVictims(Short numberNonFatalVictims) {
	this.numberNonFatalVictims = numberNonFatalVictims;
    }

    public Short getAlcoholLevelCounterpart() {
	return alcoholLevelCounterpart;
    }

    public void setAlcoholLevelCounterpart(Short alcoholLevelCounterpart) {
	this.alcoholLevelCounterpart = alcoholLevelCounterpart;
    }

    public Integer getFatalInjuryId() {
	return fatalInjuryId;
    }

    public void setFatalInjuryId(Integer fatalInjuryId) {
	this.fatalInjuryId = fatalInjuryId;
    }

    public VictimCharacteristics getVictimCharacteristicId() {
	return victimCharacteristicId;
    }

    public void setVictimCharacteristicId(VictimCharacteristics victimCharacteristicId) {
	this.victimCharacteristicId = victimCharacteristicId;
    }

    public ServiceTypes getServiceTypeId() {
	return serviceTypeId;
    }

    public void setServiceTypeId(ServiceTypes serviceTypeId) {
	this.serviceTypeId = serviceTypeId;
    }

    public RoadTypes getRoadTypeId() {
	return roadTypeId;
    }

    public void setRoadTypeId(RoadTypes roadTypeId) {
	this.roadTypeId = roadTypeId;
    }

    public ProtectiveMeasures getProtectionMeasureId() {
	return protectionMeasureId;
    }

    public void setProtectionMeasureId(ProtectiveMeasures protectionMeasureId) {
	this.protectionMeasureId = protectionMeasureId;
    }

    public InvolvedVehicles getInvolvedVehicleId() {
	return involvedVehicleId;
    }

    public void setInvolvedVehicleId(InvolvedVehicles involvedVehicleId) {
	this.involvedVehicleId = involvedVehicleId;
    }

    public FatalInjuries getFatalInjuries() {
	return fatalInjuries;
    }

    public void setFatalInjuries(FatalInjuries fatalInjuries) {
	this.fatalInjuries = fatalInjuries;
    }

    public AlcoholLevels getAlcoholLevelCounterpartId() {
	return alcoholLevelCounterpartId;
    }

    public void setAlcoholLevelCounterpartId(AlcoholLevels alcoholLevelCounterpartId) {
	this.alcoholLevelCounterpartId = alcoholLevelCounterpartId;
    }

    public AccidentClasses getAccidentClassId() {
	return accidentClassId;
    }

    public void setAccidentClassId(AccidentClasses accidentClassId) {
	this.accidentClassId = accidentClassId;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (fatalInjuryId != null ? fatalInjuryId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof FatalInjuryTraffic)) {
	    return false;
	}
	FatalInjuryTraffic other = (FatalInjuryTraffic) object;
	if ((this.fatalInjuryId == null && other.fatalInjuryId != null) || (this.fatalInjuryId != null && !this.fatalInjuryId.equals(other.fatalInjuryId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.FatalInjuryTraffic[ fatalInjuryId=" + fatalInjuryId + " ]";
    }
    
}
