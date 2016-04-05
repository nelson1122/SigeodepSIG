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
@Table(name = "protective_measures", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"protective_measures_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProtectiveMeasures.findAll", query = "SELECT p FROM ProtectiveMeasures p"),
    @NamedQuery(name = "ProtectiveMeasures.findByProtectiveMeasuresId", query = "SELECT p FROM ProtectiveMeasures p WHERE p.protectiveMeasuresId = :protectiveMeasuresId"),
    @NamedQuery(name = "ProtectiveMeasures.findByProtectiveMeasuresName", query = "SELECT p FROM ProtectiveMeasures p WHERE p.protectiveMeasuresName = :protectiveMeasuresName")})
public class ProtectiveMeasures implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "protective_measures_id", nullable = false)
    private Short protectiveMeasuresId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "protective_measures_name", nullable = false, length = 2147483647)
    private String protectiveMeasuresName;
    @OneToMany(mappedBy = "protectionMeasureId")
    private List<FatalInjuryTraffic> fatalInjuryTrafficList;

    public ProtectiveMeasures() {
    }

    public ProtectiveMeasures(Short protectiveMeasuresId) {
	this.protectiveMeasuresId = protectiveMeasuresId;
    }

    public ProtectiveMeasures(Short protectiveMeasuresId, String protectiveMeasuresName) {
	this.protectiveMeasuresId = protectiveMeasuresId;
	this.protectiveMeasuresName = protectiveMeasuresName;
    }

    public Short getProtectiveMeasuresId() {
	return protectiveMeasuresId;
    }

    public void setProtectiveMeasuresId(Short protectiveMeasuresId) {
	this.protectiveMeasuresId = protectiveMeasuresId;
    }

    public String getProtectiveMeasuresName() {
	return protectiveMeasuresName;
    }

    public void setProtectiveMeasuresName(String protectiveMeasuresName) {
	this.protectiveMeasuresName = protectiveMeasuresName;
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
	hash += (protectiveMeasuresId != null ? protectiveMeasuresId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof ProtectiveMeasures)) {
	    return false;
	}
	ProtectiveMeasures other = (ProtectiveMeasures) object;
	if ((this.protectiveMeasuresId == null && other.protectiveMeasuresId != null) || (this.protectiveMeasuresId != null && !this.protectiveMeasuresId.equals(other.protectiveMeasuresId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.ProtectiveMeasures[ protectiveMeasuresId=" + protectiveMeasuresId + " ]";
    }
    
}
