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
@Table(name = "weapon_types", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"weapon_type_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "WeaponTypes.findAll", query = "SELECT w FROM WeaponTypes w"),
    @NamedQuery(name = "WeaponTypes.findByWeaponTypeId", query = "SELECT w FROM WeaponTypes w WHERE w.weaponTypeId = :weaponTypeId"),
    @NamedQuery(name = "WeaponTypes.findByWeaponTypeName", query = "SELECT w FROM WeaponTypes w WHERE w.weaponTypeName = :weaponTypeName")})
public class WeaponTypes implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "weapon_type_id", nullable = false)
    private Short weaponTypeId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "weapon_type_name", nullable = false, length = 2147483647)
    private String weaponTypeName;
    @OneToMany(mappedBy = "weaponTypeId")
    private List<FatalInjuryMurder> fatalInjuryMurderList;

    public WeaponTypes() {
    }

    public WeaponTypes(Short weaponTypeId) {
	this.weaponTypeId = weaponTypeId;
    }

    public WeaponTypes(Short weaponTypeId, String weaponTypeName) {
	this.weaponTypeId = weaponTypeId;
	this.weaponTypeName = weaponTypeName;
    }

    public Short getWeaponTypeId() {
	return weaponTypeId;
    }

    public void setWeaponTypeId(Short weaponTypeId) {
	this.weaponTypeId = weaponTypeId;
    }

    public String getWeaponTypeName() {
	return weaponTypeName;
    }

    public void setWeaponTypeName(String weaponTypeName) {
	this.weaponTypeName = weaponTypeName;
    }

    @XmlTransient
    public List<FatalInjuryMurder> getFatalInjuryMurderList() {
	return fatalInjuryMurderList;
    }

    public void setFatalInjuryMurderList(List<FatalInjuryMurder> fatalInjuryMurderList) {
	this.fatalInjuryMurderList = fatalInjuryMurderList;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (weaponTypeId != null ? weaponTypeId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof WeaponTypes)) {
	    return false;
	}
	WeaponTypes other = (WeaponTypes) object;
	if ((this.weaponTypeId == null && other.weaponTypeId != null) || (this.weaponTypeId != null && !this.weaponTypeId.equals(other.weaponTypeId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.WeaponTypes[ weaponTypeId=" + weaponTypeId + " ]";
    }
    
}
