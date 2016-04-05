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
@Table(name = "genders", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Genders.findAll", query = "SELECT g FROM Genders g"),
    @NamedQuery(name = "Genders.findByGenderId", query = "SELECT g FROM Genders g WHERE g.genderId = :genderId"),
    @NamedQuery(name = "Genders.findByGenderName", query = "SELECT g FROM Genders g WHERE g.genderName = :genderName")})
public class Genders implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "gender_id", nullable = false)
    private Short genderId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "gender_name", nullable = false, length = 15)
    private String genderName;
    @OneToMany(mappedBy = "genderId")
    private List<Victims> victimsList;
    @OneToMany(mappedBy = "gender")
    private List<SivigilaAggresor> sivigilaAggresorList;

    public Genders() {
    }

    public Genders(Short genderId) {
	this.genderId = genderId;
    }

    public Genders(Short genderId, String genderName) {
	this.genderId = genderId;
	this.genderName = genderName;
    }

    public Short getGenderId() {
	return genderId;
    }

    public void setGenderId(Short genderId) {
	this.genderId = genderId;
    }

    public String getGenderName() {
	return genderName;
    }

    public void setGenderName(String genderName) {
	this.genderName = genderName;
    }

    @XmlTransient
    public List<Victims> getVictimsList() {
	return victimsList;
    }

    public void setVictimsList(List<Victims> victimsList) {
	this.victimsList = victimsList;
    }

    @XmlTransient
    public List<SivigilaAggresor> getSivigilaAggresorList() {
        return sivigilaAggresorList;
    }

    public void setSivigilaAggresorList(List<SivigilaAggresor> sivigilaAggresorList) {
        this.sivigilaAggresorList = sivigilaAggresorList;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (genderId != null ? genderId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof Genders)) {
	    return false;
	}
	Genders other = (Genders) object;
	if ((this.genderId == null && other.genderId != null) || (this.genderId != null && !this.genderId.equals(other.genderId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.Genders[ genderId=" + genderId + " ]";
    }
    
}
