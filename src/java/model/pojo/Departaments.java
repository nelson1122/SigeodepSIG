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
@Table(name = "departaments", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Departaments.findAll", query = "SELECT d FROM Departaments d"),
    @NamedQuery(name = "Departaments.findByDepartamentId", query = "SELECT d FROM Departaments d WHERE d.departamentId = :departamentId"),
    @NamedQuery(name = "Departaments.findByDepartamentName", query = "SELECT d FROM Departaments d WHERE d.departamentName = :departamentName")})
public class Departaments implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "departament_id", nullable = false)
    private Short departamentId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "departament_name", nullable = false, length = 30)
    private String departamentName;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "departaments")
    private List<Municipalities> municipalitiesList;

    public Departaments() {
    }

    public Departaments(Short departamentId) {
	this.departamentId = departamentId;
    }

    public Departaments(Short departamentId, String departamentName) {
	this.departamentId = departamentId;
	this.departamentName = departamentName;
    }

    public Short getDepartamentId() {
	return departamentId;
    }

    public void setDepartamentId(Short departamentId) {
	this.departamentId = departamentId;
    }

    public String getDepartamentName() {
	return departamentName;
    }

    public void setDepartamentName(String departamentName) {
	this.departamentName = departamentName;
    }

    @XmlTransient
    public List<Municipalities> getMunicipalitiesList() {
	return municipalitiesList;
    }

    public void setMunicipalitiesList(List<Municipalities> municipalitiesList) {
	this.municipalitiesList = municipalitiesList;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (departamentId != null ? departamentId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof Departaments)) {
	    return false;
	}
	Departaments other = (Departaments) object;
	if ((this.departamentId == null && other.departamentId != null) || (this.departamentId != null && !this.departamentId.equals(other.departamentId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.Departaments[ departamentId=" + departamentId + " ]";
    }
    
}
