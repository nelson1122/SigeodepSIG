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
@Table(name = "health_professionals", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "HealthProfessionals.findAll", query = "SELECT h FROM HealthProfessionals h"),
    @NamedQuery(name = "HealthProfessionals.findByHealthProfessionalId", query = "SELECT h FROM HealthProfessionals h WHERE h.healthProfessionalId = :healthProfessionalId"),
    @NamedQuery(name = "HealthProfessionals.findByHealthProfessionalName", query = "SELECT h FROM HealthProfessionals h WHERE h.healthProfessionalName = :healthProfessionalName"),
    @NamedQuery(name = "HealthProfessionals.findByHealthProfessionalIdentification", query = "SELECT h FROM HealthProfessionals h WHERE h.healthProfessionalIdentification = :healthProfessionalIdentification"),
    @NamedQuery(name = "HealthProfessionals.findByHealthProfessionalSpecialty", query = "SELECT h FROM HealthProfessionals h WHERE h.healthProfessionalSpecialty = :healthProfessionalSpecialty")})
public class HealthProfessionals implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "health_professional_id", nullable = false)
    private Integer healthProfessionalId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "health_professional_name", nullable = false, length = 100)
    private String healthProfessionalName;
    @Size(max = 12)
    @Column(name = "health_professional_identification", length = 12)
    private String healthProfessionalIdentification;
    @Size(max = 50)
    @Column(name = "health_professional_specialty", length = 50)
    private String healthProfessionalSpecialty;
    @OneToMany(mappedBy = "healthProfessionalId")
    private List<NonFatalInjuries> nonFatalInjuriesList;

    public HealthProfessionals() {
    }

    public HealthProfessionals(Integer healthProfessionalId) {
	this.healthProfessionalId = healthProfessionalId;
    }

    public HealthProfessionals(Integer healthProfessionalId, String healthProfessionalName, String healthProfessionalIdentification) {
	this.healthProfessionalId = healthProfessionalId;
	this.healthProfessionalName = healthProfessionalName;
	this.healthProfessionalIdentification = healthProfessionalIdentification;
    }

    public Integer getHealthProfessionalId() {
	return healthProfessionalId;
    }

    public void setHealthProfessionalId(Integer healthProfessionalId) {
	this.healthProfessionalId = healthProfessionalId;
    }

    public String getHealthProfessionalName() {
	return healthProfessionalName;
    }

    public void setHealthProfessionalName(String healthProfessionalName) {
	this.healthProfessionalName = healthProfessionalName;
    }

    public String getHealthProfessionalIdentification() {
	return healthProfessionalIdentification;
    }

    public void setHealthProfessionalIdentification(String healthProfessionalIdentification) {
	this.healthProfessionalIdentification = healthProfessionalIdentification;
    }

    public String getHealthProfessionalSpecialty() {
	return healthProfessionalSpecialty;
    }

    public void setHealthProfessionalSpecialty(String healthProfessionalSpecialty) {
	this.healthProfessionalSpecialty = healthProfessionalSpecialty;
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
	hash += (healthProfessionalId != null ? healthProfessionalId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof HealthProfessionals)) {
	    return false;
	}
	HealthProfessionals other = (HealthProfessionals) object;
	if ((this.healthProfessionalId == null && other.healthProfessionalId != null) || (this.healthProfessionalId != null && !this.healthProfessionalId.equals(other.healthProfessionalId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.HealthProfessionals[ healthProfessionalId=" + healthProfessionalId + " ]";
    }
    
}
