/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author SANTOS
 */
@Entity
@Table(name = "municipalities", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Municipalities.findAll", query = "SELECT m FROM Municipalities m"),
    @NamedQuery(name = "Municipalities.findByDepartamentId", query = "SELECT m FROM Municipalities m WHERE m.municipalitiesPK.departamentId = :departamentId"),
    @NamedQuery(name = "Municipalities.findByMunicipalityId", query = "SELECT m FROM Municipalities m WHERE m.municipalitiesPK.municipalityId = :municipalityId"),
    @NamedQuery(name = "Municipalities.findByMunicipalityName", query = "SELECT m FROM Municipalities m WHERE m.municipalityName = :municipalityName")})
public class Municipalities implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected MunicipalitiesPK municipalitiesPK;
    @Size(max = 50)
    @Column(name = "municipality_name", length = 50)
    private String municipalityName;
    @JoinColumn(name = "departament_id", referencedColumnName = "departament_id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Departaments departaments;

    public Municipalities() {
    }

    public Municipalities(MunicipalitiesPK municipalitiesPK) {
	this.municipalitiesPK = municipalitiesPK;
    }

    public Municipalities(short departamentId, short municipalityId) {
	this.municipalitiesPK = new MunicipalitiesPK(departamentId, municipalityId);
    }

    public MunicipalitiesPK getMunicipalitiesPK() {
	return municipalitiesPK;
    }

    public void setMunicipalitiesPK(MunicipalitiesPK municipalitiesPK) {
	this.municipalitiesPK = municipalitiesPK;
    }

    public String getMunicipalityName() {
	return municipalityName;
    }

    public void setMunicipalityName(String municipalityName) {
	this.municipalityName = municipalityName;
    }

    public Departaments getDepartaments() {
	return departaments;
    }

    public void setDepartaments(Departaments departaments) {
	this.departaments = departaments;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (municipalitiesPK != null ? municipalitiesPK.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof Municipalities)) {
	    return false;
	}
	Municipalities other = (Municipalities) object;
	if ((this.municipalitiesPK == null && other.municipalitiesPK != null) || (this.municipalitiesPK != null && !this.municipalitiesPK.equals(other.municipalitiesPK))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.Municipalities[ municipalitiesPK=" + municipalitiesPK + " ]";
    }
    
}
