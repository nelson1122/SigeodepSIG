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
@Table(name = "mechanisms", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"mechanism_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Mechanisms.findAll", query = "SELECT m FROM Mechanisms m"),
    @NamedQuery(name = "Mechanisms.findByMechanismId", query = "SELECT m FROM Mechanisms m WHERE m.mechanismId = :mechanismId"),
    @NamedQuery(name = "Mechanisms.findByMechanismName", query = "SELECT m FROM Mechanisms m WHERE m.mechanismName = :mechanismName")})
public class Mechanisms implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "mechanism_id", nullable = false)
    private Short mechanismId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "mechanism_name", nullable = false, length = 2147483647)
    private String mechanismName;
    @OneToMany(mappedBy = "mechanismId")
    private List<NonFatalInjuries> nonFatalInjuriesList;

    public Mechanisms() {
    }

    public Mechanisms(Short mechanismId) {
	this.mechanismId = mechanismId;
    }

    public Mechanisms(Short mechanismId, String mechanismName) {
	this.mechanismId = mechanismId;
	this.mechanismName = mechanismName;
    }

    public Short getMechanismId() {
	return mechanismId;
    }

    public void setMechanismId(Short mechanismId) {
	this.mechanismId = mechanismId;
    }

    public String getMechanismName() {
	return mechanismName;
    }

    public void setMechanismName(String mechanismName) {
	this.mechanismName = mechanismName;
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
	hash += (mechanismId != null ? mechanismId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof Mechanisms)) {
	    return false;
	}
	Mechanisms other = (Mechanisms) object;
	if ((this.mechanismId == null && other.mechanismId != null) || (this.mechanismId != null && !this.mechanismId.equals(other.mechanismId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.Mechanisms[ mechanismId=" + mechanismId + " ]";
    }
    
}
