/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author SANTOS
 */
@Entity
@Table(name = "ampm", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ampm.findAll", query = "SELECT a FROM Ampm a"),
    @NamedQuery(name = "Ampm.findByAmpmId", query = "SELECT a FROM Ampm a WHERE a.ampmId = :ampmId"),
    @NamedQuery(name = "Ampm.findByAmpmName", query = "SELECT a FROM Ampm a WHERE a.ampmName = :ampmName")})
public class Ampm implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ampm_id", nullable = false)
    private Short ampmId;
    @Size(max = 2147483647)
    @Column(name = "ampm_name", length = 2147483647)
    private String ampmName;

    public Ampm() {
    }

    public Ampm(Short ampmId) {
	this.ampmId = ampmId;
    }

    public Short getAmpmId() {
	return ampmId;
    }

    public void setAmpmId(Short ampmId) {
	this.ampmId = ampmId;
    }

    public String getAmpmName() {
	return ampmName;
    }

    public void setAmpmName(String ampmName) {
	this.ampmName = ampmName;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (ampmId != null ? ampmId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof Ampm)) {
	    return false;
	}
	Ampm other = (Ampm) object;
	if ((this.ampmId == null && other.ampmId != null) || (this.ampmId != null && !this.ampmId.equals(other.ampmId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.Ampm[ ampmId=" + ampmId + " ]";
    }
    
}
