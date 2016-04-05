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
@Table(name = "murder_contexts", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"murder_context_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MurderContexts.findAll", query = "SELECT m FROM MurderContexts m"),
    @NamedQuery(name = "MurderContexts.findByMurderContextId", query = "SELECT m FROM MurderContexts m WHERE m.murderContextId = :murderContextId"),
    @NamedQuery(name = "MurderContexts.findByMurderContextName", query = "SELECT m FROM MurderContexts m WHERE m.murderContextName = :murderContextName")})
public class MurderContexts implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "murder_context_id", nullable = false)
    private Short murderContextId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "murder_context_name", nullable = false, length = 2147483647)
    private String murderContextName;
    @OneToMany(mappedBy = "murderContextId")
    private List<FatalInjuryMurder> fatalInjuryMurderList;

    public MurderContexts() {
    }

    public MurderContexts(Short murderContextId) {
	this.murderContextId = murderContextId;
    }

    public MurderContexts(Short murderContextId, String murderContextName) {
	this.murderContextId = murderContextId;
	this.murderContextName = murderContextName;
    }

    public Short getMurderContextId() {
	return murderContextId;
    }

    public void setMurderContextId(Short murderContextId) {
	this.murderContextId = murderContextId;
    }

    public String getMurderContextName() {
	return murderContextName;
    }

    public void setMurderContextName(String murderContextName) {
	this.murderContextName = murderContextName;
    }

    @XmlTransient
    public List<FatalInjuryMurder> getFatalInjuryMurderList() {
	return fatalInjuryMurderList;
    }

    public void setFatalInjuryMurderList(List<FatalInjuryMurder> fatalInjuryMurderList) {
	this.fatalInjuryMurderList = fatalInjuryMurderList;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (murderContextId != null ? murderContextId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof MurderContexts)) {
	    return false;
	}
	MurderContexts other = (MurderContexts) object;
	if ((this.murderContextId == null && other.murderContextId != null) || (this.murderContextId != null && !this.murderContextId.equals(other.murderContextId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.MurderContexts[ murderContextId=" + murderContextId + " ]";
    }
    
}
