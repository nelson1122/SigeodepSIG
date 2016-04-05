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
@Table(name = "road_types", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"road_type_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RoadTypes.findAll", query = "SELECT r FROM RoadTypes r"),
    @NamedQuery(name = "RoadTypes.findByRoadTypeId", query = "SELECT r FROM RoadTypes r WHERE r.roadTypeId = :roadTypeId"),
    @NamedQuery(name = "RoadTypes.findByRoadTypeName", query = "SELECT r FROM RoadTypes r WHERE r.roadTypeName = :roadTypeName")})
public class RoadTypes implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "road_type_id", nullable = false)
    private Short roadTypeId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "road_type_name", nullable = false, length = 2147483647)
    private String roadTypeName;
    @OneToMany(mappedBy = "roadTypeId")
    private List<FatalInjuryTraffic> fatalInjuryTrafficList;

    public RoadTypes() {
    }

    public RoadTypes(Short roadTypeId) {
	this.roadTypeId = roadTypeId;
    }

    public RoadTypes(Short roadTypeId, String roadTypeName) {
	this.roadTypeId = roadTypeId;
	this.roadTypeName = roadTypeName;
    }

    public Short getRoadTypeId() {
	return roadTypeId;
    }

    public void setRoadTypeId(Short roadTypeId) {
	this.roadTypeId = roadTypeId;
    }

    public String getRoadTypeName() {
	return roadTypeName;
    }

    public void setRoadTypeName(String roadTypeName) {
	this.roadTypeName = roadTypeName;
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
	hash += (roadTypeId != null ? roadTypeId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof RoadTypes)) {
	    return false;
	}
	RoadTypes other = (RoadTypes) object;
	if ((this.roadTypeId == null && other.roadTypeId != null) || (this.roadTypeId != null && !this.roadTypeId.equals(other.roadTypeId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.RoadTypes[ roadTypeId=" + roadTypeId + " ]";
    }
    
}
