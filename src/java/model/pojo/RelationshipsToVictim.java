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
@Table(name = "relationships_to_victim", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"relationship_victim_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RelationshipsToVictim.findAll", query = "SELECT r FROM RelationshipsToVictim r"),
    @NamedQuery(name = "RelationshipsToVictim.findByRelationshipVictimId", query = "SELECT r FROM RelationshipsToVictim r WHERE r.relationshipVictimId = :relationshipVictimId"),
    @NamedQuery(name = "RelationshipsToVictim.findByRelationshipVictimName", query = "SELECT r FROM RelationshipsToVictim r WHERE r.relationshipVictimName = :relationshipVictimName")})
public class RelationshipsToVictim implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "relationship_victim_id", nullable = false)
    private Short relationshipVictimId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "relationship_victim_name", nullable = false, length = 20)
    private String relationshipVictimName;
    @OneToMany(mappedBy = "relationshipVictimId")
    private List<NonFatalInterpersonal> nonFatalInterpersonalList;

    public RelationshipsToVictim() {
    }

    public RelationshipsToVictim(Short relationshipVictimId) {
	this.relationshipVictimId = relationshipVictimId;
    }

    public RelationshipsToVictim(Short relationshipVictimId, String relationshipVictimName) {
	this.relationshipVictimId = relationshipVictimId;
	this.relationshipVictimName = relationshipVictimName;
    }

    public Short getRelationshipVictimId() {
	return relationshipVictimId;
    }

    public void setRelationshipVictimId(Short relationshipVictimId) {
	this.relationshipVictimId = relationshipVictimId;
    }

    public String getRelationshipVictimName() {
	return relationshipVictimName;
    }

    public void setRelationshipVictimName(String relationshipVictimName) {
	this.relationshipVictimName = relationshipVictimName;
    }

    @XmlTransient
    public List<NonFatalInterpersonal> getNonFatalInterpersonalList() {
	return nonFatalInterpersonalList;
    }

    public void setNonFatalInterpersonalList(List<NonFatalInterpersonal> nonFatalInterpersonalList) {
	this.nonFatalInterpersonalList = nonFatalInterpersonalList;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (relationshipVictimId != null ? relationshipVictimId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof RelationshipsToVictim)) {
	    return false;
	}
	RelationshipsToVictim other = (RelationshipsToVictim) object;
	if ((this.relationshipVictimId == null && other.relationshipVictimId != null) || (this.relationshipVictimId != null && !this.relationshipVictimId.equals(other.relationshipVictimId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.RelationshipsToVictim[ relationshipVictimId=" + relationshipVictimId + " ]";
    }
    
}
