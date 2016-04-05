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
@Table(name = "non_fatal_interpersonal", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "NonFatalInterpersonal.findAll", query = "SELECT n FROM NonFatalInterpersonal n"),
    @NamedQuery(name = "NonFatalInterpersonal.findByNonFatalInjuryId", query = "SELECT n FROM NonFatalInterpersonal n WHERE n.nonFatalInjuryId = :nonFatalInjuryId")})
public class NonFatalInterpersonal implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "non_fatal_injury_id", nullable = false)
    private Integer nonFatalInjuryId;
    @JoinColumn(name = "relationship_victim_id", referencedColumnName = "relationship_victim_id")
    @ManyToOne
    private RelationshipsToVictim relationshipVictimId;
    @JoinColumn(name = "non_fatal_injury_id", referencedColumnName = "non_fatal_injury_id", nullable = false, insertable = false, updatable = false)
    @OneToOne(optional = false)
    private NonFatalInjuries nonFatalInjuries;
    @JoinColumn(name = "context_id", referencedColumnName = "context_id")
    @ManyToOne
    private Contexts contextId;
    @JoinColumn(name = "previous_antecedent", referencedColumnName = "boolean_id")
    @ManyToOne
    private Boolean3 previousAntecedent;
    @JoinColumn(name = "aggressor_gender_id", referencedColumnName = "gender_id")
    @ManyToOne
    private AggressorGenders aggressorGenderId;

    public NonFatalInterpersonal() {
    }

    public NonFatalInterpersonal(Integer nonFatalInjuryId) {
	this.nonFatalInjuryId = nonFatalInjuryId;
    }

    public Integer getNonFatalInjuryId() {
	return nonFatalInjuryId;
    }

    public void setNonFatalInjuryId(Integer nonFatalInjuryId) {
	this.nonFatalInjuryId = nonFatalInjuryId;
    }

    public RelationshipsToVictim getRelationshipVictimId() {
	return relationshipVictimId;
    }

    public void setRelationshipVictimId(RelationshipsToVictim relationshipVictimId) {
	this.relationshipVictimId = relationshipVictimId;
    }

    public NonFatalInjuries getNonFatalInjuries() {
	return nonFatalInjuries;
    }

    public void setNonFatalInjuries(NonFatalInjuries nonFatalInjuries) {
	this.nonFatalInjuries = nonFatalInjuries;
    }

    public Contexts getContextId() {
	return contextId;
    }

    public void setContextId(Contexts contextId) {
	this.contextId = contextId;
    }

    public Boolean3 getPreviousAntecedent() {
        return previousAntecedent;
    }

    public void setPreviousAntecedent(Boolean3 previousAntecedent) {
        this.previousAntecedent = previousAntecedent;
    }

    public AggressorGenders getAggressorGenderId() {
	return aggressorGenderId;
    }

    public void setAggressorGenderId(AggressorGenders aggressorGenderId) {
	this.aggressorGenderId = aggressorGenderId;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (nonFatalInjuryId != null ? nonFatalInjuryId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof NonFatalInterpersonal)) {
	    return false;
	}
	NonFatalInterpersonal other = (NonFatalInterpersonal) object;
	if ((this.nonFatalInjuryId == null && other.nonFatalInjuryId != null) || (this.nonFatalInjuryId != null && !this.nonFatalInjuryId.equals(other.nonFatalInjuryId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.NonFatalInterpersonal[ nonFatalInjuryId=" + nonFatalInjuryId + " ]";
    }
    
}
