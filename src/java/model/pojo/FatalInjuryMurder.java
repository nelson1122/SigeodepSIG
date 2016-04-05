/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author SANTOS
 */
@Entity
@Table(name = "fatal_injury_murder", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FatalInjuryMurder.findAll", query = "SELECT f FROM FatalInjuryMurder f"),
    @NamedQuery(name = "FatalInjuryMurder.findByFatalInjuryId", query = "SELECT f FROM FatalInjuryMurder f WHERE f.fatalInjuryId = :fatalInjuryId")})
public class FatalInjuryMurder implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "fatal_injury_id", nullable = false)
    private Integer fatalInjuryId;
    @JoinColumn(name = "weapon_type_id", referencedColumnName = "weapon_type_id")
    @ManyToOne
    private WeaponTypes weaponTypeId;
    @JoinColumn(name = "murder_context_id", referencedColumnName = "murder_context_id")
    @ManyToOne
    private MurderContexts murderContextId;
    @JoinColumn(name = "fatal_injury_id", referencedColumnName = "fatal_injury_id", nullable = false, insertable = false, updatable = false)
    @OneToOne(optional = false)
    private FatalInjuries fatalInjuries;

    public FatalInjuryMurder() {
    }

    public FatalInjuryMurder(Integer fatalInjuryId) {
	this.fatalInjuryId = fatalInjuryId;
    }

    public Integer getFatalInjuryId() {
	return fatalInjuryId;
    }

    public void setFatalInjuryId(Integer fatalInjuryId) {
	this.fatalInjuryId = fatalInjuryId;
    }

    public WeaponTypes getWeaponTypeId() {
	return weaponTypeId;
    }

    public void setWeaponTypeId(WeaponTypes weaponTypeId) {
	this.weaponTypeId = weaponTypeId;
    }

    public MurderContexts getMurderContextId() {
	return murderContextId;
    }

    public void setMurderContextId(MurderContexts murderContextId) {
	this.murderContextId = murderContextId;
    }

    public FatalInjuries getFatalInjuries() {
	return fatalInjuries;
    }

    public void setFatalInjuries(FatalInjuries fatalInjuries) {
	this.fatalInjuries = fatalInjuries;
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
	if (!(object instanceof FatalInjuryMurder)) {
	    return false;
	}
	FatalInjuryMurder other = (FatalInjuryMurder) object;
	if ((this.fatalInjuryId == null && other.fatalInjuryId != null) || (this.fatalInjuryId != null && !this.fatalInjuryId.equals(other.fatalInjuryId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.FatalInjuryMurder[ fatalInjuryId=" + fatalInjuryId + " ]";
    }
    
}
