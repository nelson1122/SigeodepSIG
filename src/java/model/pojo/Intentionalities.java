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
@Table(name = "intentionalities", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"intentionality_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Intentionalities.findAll", query = "SELECT i FROM Intentionalities i"),
    @NamedQuery(name = "Intentionalities.findByIntentionalityId", query = "SELECT i FROM Intentionalities i WHERE i.intentionalityId = :intentionalityId"),
    @NamedQuery(name = "Intentionalities.findByIntentionalityName", query = "SELECT i FROM Intentionalities i WHERE i.intentionalityName = :intentionalityName")})
public class Intentionalities implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "intentionality_id", nullable = false)
    private Short intentionalityId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "intentionality_name", nullable = false, length = 40)
    private String intentionalityName;
    @OneToMany(mappedBy = "intentionalityId")
    private List<NonFatalInjuries> nonFatalInjuriesList;

    public Intentionalities() {
    }

    public Intentionalities(Short intentionalityId) {
	this.intentionalityId = intentionalityId;
    }

    public Intentionalities(Short intentionalityId, String intentionalityName) {
	this.intentionalityId = intentionalityId;
	this.intentionalityName = intentionalityName;
    }

    public Short getIntentionalityId() {
	return intentionalityId;
    }

    public void setIntentionalityId(Short intentionalityId) {
	this.intentionalityId = intentionalityId;
    }

    public String getIntentionalityName() {
	return intentionalityName;
    }

    public void setIntentionalityName(String intentionalityName) {
	this.intentionalityName = intentionalityName;
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
	hash += (intentionalityId != null ? intentionalityId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof Intentionalities)) {
	    return false;
	}
	Intentionalities other = (Intentionalities) object;
	if ((this.intentionalityId == null && other.intentionalityId != null) || (this.intentionalityId != null && !this.intentionalityId.equals(other.intentionalityId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.Intentionalities[ intentionalityId=" + intentionalityId + " ]";
    }
    
}
