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
@Table(name = "kinds_of_injury", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"kind_injury_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "KindsOfInjury.findAll", query = "SELECT k FROM KindsOfInjury k"),
    @NamedQuery(name = "KindsOfInjury.findByKindInjuryId", query = "SELECT k FROM KindsOfInjury k WHERE k.kindInjuryId = :kindInjuryId"),
    @NamedQuery(name = "KindsOfInjury.findByKindInjuryName", query = "SELECT k FROM KindsOfInjury k WHERE k.kindInjuryName = :kindInjuryName")})
public class KindsOfInjury implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "kind_injury_id", nullable = false)
    private Short kindInjuryId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 80)
    @Column(name = "kind_injury_name", nullable = false, length = 80)
    private String kindInjuryName;
    @ManyToMany(mappedBy = "kindsOfInjuryList")    
    private List<NonFatalInjuries> nonFatalInjuriesList;

    public KindsOfInjury() {
    }

    public KindsOfInjury(Short kindInjuryId) {
	this.kindInjuryId = kindInjuryId;
    }

    public KindsOfInjury(Short kindInjuryId, String kindInjuryName) {
	this.kindInjuryId = kindInjuryId;
	this.kindInjuryName = kindInjuryName;
    }

    public Short getKindInjuryId() {
	return kindInjuryId;
    }

    public void setKindInjuryId(Short kindInjuryId) {
	this.kindInjuryId = kindInjuryId;
    }

    public String getKindInjuryName() {
	return kindInjuryName;
    }

    public void setKindInjuryName(String kindInjuryName) {
	this.kindInjuryName = kindInjuryName;
    }

    @XmlTransient
    public List<NonFatalInjuries> getNonFatalInjuriesList() {
	return nonFatalInjuriesList;
    }

    public void setNonFatalInjuriesList(List<NonFatalInjuries> nonFatalInjuriesList) {
	this.nonFatalInjuriesList = nonFatalInjuriesList;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (kindInjuryId != null ? kindInjuryId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof KindsOfInjury)) {
	    return false;
	}
	KindsOfInjury other = (KindsOfInjury) object;
	if ((this.kindInjuryId == null && other.kindInjuryId != null) || (this.kindInjuryId != null && !this.kindInjuryId.equals(other.kindInjuryId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.KindsOfInjury[ kindInjuryId=" + kindInjuryId + " ]";
    }
    
}
