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
@Table(name = "fatal_injury_suicide", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FatalInjurySuicide.findAll", query = "SELECT f FROM FatalInjurySuicide f"),
    @NamedQuery(name = "FatalInjurySuicide.findByFatalInjuryId", query = "SELECT f FROM FatalInjurySuicide f WHERE f.fatalInjuryId = :fatalInjuryId")})
public class FatalInjurySuicide implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "fatal_injury_id", nullable = false)
    private Integer fatalInjuryId;
    @JoinColumn(name = "suicide_death_mechanism_id", referencedColumnName = "suicide_mechanism_id")
    @ManyToOne
    private SuicideMechanisms suicideDeathMechanismId;
    @JoinColumn(name = "related_event_id", referencedColumnName = "related_event_id")
    @ManyToOne
    private RelatedEvents relatedEventId;
    @JoinColumn(name = "fatal_injury_id", referencedColumnName = "fatal_injury_id", nullable = false, insertable = false, updatable = false)
    @OneToOne(optional = false)
    private FatalInjuries fatalInjuries;
    @JoinColumn(name = "previous_attempt", referencedColumnName = "boolean_id")
    @ManyToOne
    private Boolean3 previousAttempt;
    @JoinColumn(name = "mental_antecedent", referencedColumnName = "boolean_id")
    @ManyToOne
    private Boolean3 mentalAntecedent;

    public FatalInjurySuicide() {
    }

    public FatalInjurySuicide(Integer fatalInjuryId) {
	this.fatalInjuryId = fatalInjuryId;
    }

    public Integer getFatalInjuryId() {
	return fatalInjuryId;
    }

    public void setFatalInjuryId(Integer fatalInjuryId) {
	this.fatalInjuryId = fatalInjuryId;
    }

    public SuicideMechanisms getSuicideDeathMechanismId() {
	return suicideDeathMechanismId;
    }

    public void setSuicideDeathMechanismId(SuicideMechanisms suicideDeathMechanismId) {
	this.suicideDeathMechanismId = suicideDeathMechanismId;
    }

    public RelatedEvents getRelatedEventId() {
	return relatedEventId;
    }

    public void setRelatedEventId(RelatedEvents relatedEventId) {
	this.relatedEventId = relatedEventId;
    }

    public FatalInjuries getFatalInjuries() {
	return fatalInjuries;
    }

    public void setFatalInjuries(FatalInjuries fatalInjuries) {
	this.fatalInjuries = fatalInjuries;
    }

    public Boolean3 getPreviousAttempt() {
	return previousAttempt;
    }

    public void setPreviousAttempt(Boolean3 previousAttempt) {
	this.previousAttempt = previousAttempt;
    }

    public Boolean3 getMentalAntecedent() {
	return mentalAntecedent;
    }

    public void setMentalAntecedent(Boolean3 mentalAntecedent) {
	this.mentalAntecedent = mentalAntecedent;
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
	if (!(object instanceof FatalInjurySuicide)) {
	    return false;
	}
	FatalInjurySuicide other = (FatalInjurySuicide) object;
	if ((this.fatalInjuryId == null && other.fatalInjuryId != null) || (this.fatalInjuryId != null && !this.fatalInjuryId.equals(other.fatalInjuryId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.FatalInjurySuicide[ fatalInjuryId=" + fatalInjuryId + " ]";
    }
    
}
