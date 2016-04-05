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
@Table(name = "fatal_injury_accident", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FatalInjuryAccident.findAll", query = "SELECT f FROM FatalInjuryAccident f"),
    @NamedQuery(name = "FatalInjuryAccident.findByNumberNonFatalVictims", query = "SELECT f FROM FatalInjuryAccident f WHERE f.numberNonFatalVictims = :numberNonFatalVictims"),
    @NamedQuery(name = "FatalInjuryAccident.findByFatalInjuryId", query = "SELECT f FROM FatalInjuryAccident f WHERE f.fatalInjuryId = :fatalInjuryId")})
public class FatalInjuryAccident implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column(name = "number_non_fatal_victims")
    private Short numberNonFatalVictims;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "fatal_injury_id", nullable = false)
    private Integer fatalInjuryId;
    @JoinColumn(name = "fatal_injury_id", referencedColumnName = "fatal_injury_id", nullable = false, insertable = false, updatable = false)
    @OneToOne(optional = false)
    private FatalInjuries fatalInjuries;
    @JoinColumn(name = "death_mechanism_id", referencedColumnName = "accident_mechanism_id")
    @ManyToOne
    private AccidentMechanisms deathMechanismId;

    public FatalInjuryAccident() {
    }

    public FatalInjuryAccident(Integer fatalInjuryId) {
	this.fatalInjuryId = fatalInjuryId;
    }

    public Short getNumberNonFatalVictims() {
	return numberNonFatalVictims;
    }

    public void setNumberNonFatalVictims(Short numberNonFatalVictims) {
	this.numberNonFatalVictims = numberNonFatalVictims;
    }

    public Integer getFatalInjuryId() {
	return fatalInjuryId;
    }

    public void setFatalInjuryId(Integer fatalInjuryId) {
	this.fatalInjuryId = fatalInjuryId;
    }

    public FatalInjuries getFatalInjuries() {
	return fatalInjuries;
    }

    public void setFatalInjuries(FatalInjuries fatalInjuries) {
	this.fatalInjuries = fatalInjuries;
    }

    public AccidentMechanisms getDeathMechanismId() {
	return deathMechanismId;
    }

    public void setDeathMechanismId(AccidentMechanisms deathMechanismId) {
	this.deathMechanismId = deathMechanismId;
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
	if (!(object instanceof FatalInjuryAccident)) {
	    return false;
	}
	FatalInjuryAccident other = (FatalInjuryAccident) object;
	if ((this.fatalInjuryId == null && other.fatalInjuryId != null) || (this.fatalInjuryId != null && !this.fatalInjuryId.equals(other.fatalInjuryId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.FatalInjuryAccident[ fatalInjuryId=" + fatalInjuryId + " ]";
    }
    
}
