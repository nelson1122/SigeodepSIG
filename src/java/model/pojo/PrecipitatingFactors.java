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
@Table(name = "precipitating_factors", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"precipitating_factor_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PrecipitatingFactors.findAll", query = "SELECT p FROM PrecipitatingFactors p"),
    @NamedQuery(name = "PrecipitatingFactors.findByPrecipitatingFactorId", query = "SELECT p FROM PrecipitatingFactors p WHERE p.precipitatingFactorId = :precipitatingFactorId"),
    @NamedQuery(name = "PrecipitatingFactors.findByPrecipitatingFactorName", query = "SELECT p FROM PrecipitatingFactors p WHERE p.precipitatingFactorName = :precipitatingFactorName")})
public class PrecipitatingFactors implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "precipitating_factor_id", nullable = false)
    private Short precipitatingFactorId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "precipitating_factor_name", nullable = false, length = 40)
    private String precipitatingFactorName;
    @OneToMany(mappedBy = "precipitatingFactorId")
    private List<NonFatalSelfInflicted> nonFatalSelfInflictedList;

    public PrecipitatingFactors() {
    }

    public PrecipitatingFactors(Short precipitatingFactorId) {
	this.precipitatingFactorId = precipitatingFactorId;
    }

    public PrecipitatingFactors(Short precipitatingFactorId, String precipitatingFactorName) {
	this.precipitatingFactorId = precipitatingFactorId;
	this.precipitatingFactorName = precipitatingFactorName;
    }

    public Short getPrecipitatingFactorId() {
	return precipitatingFactorId;
    }

    public void setPrecipitatingFactorId(Short precipitatingFactorId) {
	this.precipitatingFactorId = precipitatingFactorId;
    }

    public String getPrecipitatingFactorName() {
	return precipitatingFactorName;
    }

    public void setPrecipitatingFactorName(String precipitatingFactorName) {
	this.precipitatingFactorName = precipitatingFactorName;
    }

    @XmlTransient
    public List<NonFatalSelfInflicted> getNonFatalSelfInflictedList() {
	return nonFatalSelfInflictedList;
    }

    public void setNonFatalSelfInflictedList(List<NonFatalSelfInflicted> nonFatalSelfInflictedList) {
	this.nonFatalSelfInflictedList = nonFatalSelfInflictedList;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (precipitatingFactorId != null ? precipitatingFactorId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof PrecipitatingFactors)) {
	    return false;
	}
	PrecipitatingFactors other = (PrecipitatingFactors) object;
	if ((this.precipitatingFactorId == null && other.precipitatingFactorId != null) || (this.precipitatingFactorId != null && !this.precipitatingFactorId.equals(other.precipitatingFactorId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.PrecipitatingFactors[ precipitatingFactorId=" + precipitatingFactorId + " ]";
    }
    
}
