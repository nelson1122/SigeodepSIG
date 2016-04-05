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
@Table(name = "suicide_mechanisms", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"suicide_mechanism_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SuicideMechanisms.findAll", query = "SELECT s FROM SuicideMechanisms s"),
    @NamedQuery(name = "SuicideMechanisms.findBySuicideMechanismId", query = "SELECT s FROM SuicideMechanisms s WHERE s.suicideMechanismId = :suicideMechanismId"),
    @NamedQuery(name = "SuicideMechanisms.findBySuicideMechanismName", query = "SELECT s FROM SuicideMechanisms s WHERE s.suicideMechanismName = :suicideMechanismName")})
public class SuicideMechanisms implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "suicide_mechanism_id", nullable = false)
    private Short suicideMechanismId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "suicide_mechanism_name", nullable = false, length = 2147483647)
    private String suicideMechanismName;
    @OneToMany(mappedBy = "suicideDeathMechanismId")
    private List<FatalInjurySuicide> fatalInjurySuicideList;

    public SuicideMechanisms() {
    }

    public SuicideMechanisms(Short suicideMechanismId) {
	this.suicideMechanismId = suicideMechanismId;
    }

    public SuicideMechanisms(Short suicideMechanismId, String suicideMechanismName) {
	this.suicideMechanismId = suicideMechanismId;
	this.suicideMechanismName = suicideMechanismName;
    }

    public Short getSuicideMechanismId() {
	return suicideMechanismId;
    }

    public void setSuicideMechanismId(Short suicideMechanismId) {
	this.suicideMechanismId = suicideMechanismId;
    }

    public String getSuicideMechanismName() {
	return suicideMechanismName;
    }

    public void setSuicideMechanismName(String suicideMechanismName) {
	this.suicideMechanismName = suicideMechanismName;
    }

    @XmlTransient
    public List<FatalInjurySuicide> getFatalInjurySuicideList() {
	return fatalInjurySuicideList;
    }

    public void setFatalInjurySuicideList(List<FatalInjurySuicide> fatalInjurySuicideList) {
	this.fatalInjurySuicideList = fatalInjurySuicideList;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (suicideMechanismId != null ? suicideMechanismId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof SuicideMechanisms)) {
	    return false;
	}
	SuicideMechanisms other = (SuicideMechanisms) object;
	if ((this.suicideMechanismId == null && other.suicideMechanismId != null) || (this.suicideMechanismId != null && !this.suicideMechanismId.equals(other.suicideMechanismId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.SuicideMechanisms[ suicideMechanismId=" + suicideMechanismId + " ]";
    }
    
}
