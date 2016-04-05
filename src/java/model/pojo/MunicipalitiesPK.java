/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author SANTOS
 */
@Embeddable
public class MunicipalitiesPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "departament_id", nullable = false)
    private short departamentId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "municipality_id", nullable = false)
    private short municipalityId;

    public MunicipalitiesPK() {
    }

    public MunicipalitiesPK(short departamentId, short municipalityId) {
	this.departamentId = departamentId;
	this.municipalityId = municipalityId;
    }

    public short getDepartamentId() {
	return departamentId;
    }

    public void setDepartamentId(short departamentId) {
	this.departamentId = departamentId;
    }

    public short getMunicipalityId() {
	return municipalityId;
    }

    public void setMunicipalityId(short municipalityId) {
	this.municipalityId = municipalityId;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (int) departamentId;
	hash += (int) municipalityId;
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof MunicipalitiesPK)) {
	    return false;
	}
	MunicipalitiesPK other = (MunicipalitiesPK) object;
	if (this.departamentId != other.departamentId) {
	    return false;
	}
	if (this.municipalityId != other.municipalityId) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.MunicipalitiesPK[ departamentId=" + departamentId + ", municipalityId=" + municipalityId + " ]";
    }
    
}
