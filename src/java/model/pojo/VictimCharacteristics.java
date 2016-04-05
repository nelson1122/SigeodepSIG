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
@Table(name = "victim_characteristics", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"characteristic_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "VictimCharacteristics.findAll", query = "SELECT v FROM VictimCharacteristics v"),
    @NamedQuery(name = "VictimCharacteristics.findByCharacteristicId", query = "SELECT v FROM VictimCharacteristics v WHERE v.characteristicId = :characteristicId"),
    @NamedQuery(name = "VictimCharacteristics.findByCharacteristicName", query = "SELECT v FROM VictimCharacteristics v WHERE v.characteristicName = :characteristicName")})
public class VictimCharacteristics implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "characteristic_id", nullable = false)
    private Short characteristicId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "characteristic_name", nullable = false, length = 2147483647)
    private String characteristicName;
    @OneToMany(mappedBy = "victimCharacteristicId")
    private List<FatalInjuryTraffic> fatalInjuryTrafficList;

    public VictimCharacteristics() {
    }

    public VictimCharacteristics(Short characteristicId) {
	this.characteristicId = characteristicId;
    }

    public VictimCharacteristics(Short characteristicId, String characteristicName) {
	this.characteristicId = characteristicId;
	this.characteristicName = characteristicName;
    }

    public Short getCharacteristicId() {
	return characteristicId;
    }

    public void setCharacteristicId(Short characteristicId) {
	this.characteristicId = characteristicId;
    }

    public String getCharacteristicName() {
	return characteristicName;
    }

    public void setCharacteristicName(String characteristicName) {
	this.characteristicName = characteristicName;
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
	hash += (characteristicId != null ? characteristicId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof VictimCharacteristics)) {
	    return false;
	}
	VictimCharacteristics other = (VictimCharacteristics) object;
	if ((this.characteristicId == null && other.characteristicId != null) || (this.characteristicId != null && !this.characteristicId.equals(other.characteristicId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.VictimCharacteristics[ characteristicId=" + characteristicId + " ]";
    }
    
}
