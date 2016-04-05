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
@Table(name = "days", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Days.findAll", query = "SELECT d FROM Days d"),
    @NamedQuery(name = "Days.findByDaysId", query = "SELECT d FROM Days d WHERE d.daysId = :daysId"),
    @NamedQuery(name = "Days.findByDaysName", query = "SELECT d FROM Days d WHERE d.daysName = :daysName")})
public class Days implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "days_id", nullable = false)
    private Short daysId;
    @Size(max = 2147483647)
    @Column(name = "days_name", length = 2147483647)
    private String daysName;

    public Days() {
    }

    public Days(Short daysId) {
	this.daysId = daysId;
    }

    public Short getDaysId() {
	return daysId;
    }

    public void setDaysId(Short daysId) {
	this.daysId = daysId;
    }

    public String getDaysName() {
	return daysName;
    }

    public void setDaysName(String daysName) {
	this.daysName = daysName;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (daysId != null ? daysId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof Days)) {
	    return false;
	}
	Days other = (Days) object;
	if ((this.daysId == null && other.daysId != null) || (this.daysId != null && !this.daysId.equals(other.daysId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.Days[ daysId=" + daysId + " ]";
    }
    
}
