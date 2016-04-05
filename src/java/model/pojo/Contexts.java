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
@Table(name = "contexts", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"context_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Contexts.findAll", query = "SELECT c FROM Contexts c"),
    @NamedQuery(name = "Contexts.findByContextId", query = "SELECT c FROM Contexts c WHERE c.contextId = :contextId"),
    @NamedQuery(name = "Contexts.findByContextName", query = "SELECT c FROM Contexts c WHERE c.contextName = :contextName")})
public class Contexts implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "context_id", nullable = false)
    private Short contextId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "context_name", nullable = false, length = 20)
    private String contextName;
    @OneToMany(mappedBy = "contextId")
    private List<NonFatalInterpersonal> nonFatalInterpersonalList;

    public Contexts() {
    }

    public Contexts(Short contextId) {
	this.contextId = contextId;
    }

    public Contexts(Short contextId, String contextName) {
	this.contextId = contextId;
	this.contextName = contextName;
    }

    public Short getContextId() {
	return contextId;
    }

    public void setContextId(Short contextId) {
	this.contextId = contextId;
    }

    public String getContextName() {
	return contextName;
    }

    public void setContextName(String contextName) {
	this.contextName = contextName;
    }

    @XmlTransient
    public List<NonFatalInterpersonal> getNonFatalInterpersonalList() {
	return nonFatalInterpersonalList;
    }

    public void setNonFatalInterpersonalList(List<NonFatalInterpersonal> nonFatalInterpersonalList) {
	this.nonFatalInterpersonalList = nonFatalInterpersonalList;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (contextId != null ? contextId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof Contexts)) {
	    return false;
	}
	Contexts other = (Contexts) object;
	if ((this.contextId == null && other.contextId != null) || (this.contextId != null && !this.contextId.equals(other.contextId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.Contexts[ contextId=" + contextId + " ]";
    }
    
}
