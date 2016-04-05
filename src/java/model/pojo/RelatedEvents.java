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
@Table(name = "related_events", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"related_event_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RelatedEvents.findAll", query = "SELECT r FROM RelatedEvents r"),
    @NamedQuery(name = "RelatedEvents.findByRelatedEventId", query = "SELECT r FROM RelatedEvents r WHERE r.relatedEventId = :relatedEventId"),
    @NamedQuery(name = "RelatedEvents.findByRelatedEventName", query = "SELECT r FROM RelatedEvents r WHERE r.relatedEventName = :relatedEventName")})
public class RelatedEvents implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "related_event_id", nullable = false)
    private Short relatedEventId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "related_event_name", nullable = false, length = 2147483647)
    private String relatedEventName;
    @OneToMany(mappedBy = "relatedEventId")
    private List<FatalInjurySuicide> fatalInjurySuicideList;

    public RelatedEvents() {
    }

    public RelatedEvents(Short relatedEventId) {
	this.relatedEventId = relatedEventId;
    }

    public RelatedEvents(Short relatedEventId, String relatedEventName) {
	this.relatedEventId = relatedEventId;
	this.relatedEventName = relatedEventName;
    }

    public Short getRelatedEventId() {
	return relatedEventId;
    }

    public void setRelatedEventId(Short relatedEventId) {
	this.relatedEventId = relatedEventId;
    }

    public String getRelatedEventName() {
	return relatedEventName;
    }

    public void setRelatedEventName(String relatedEventName) {
	this.relatedEventName = relatedEventName;
    }

    @XmlTransient
    public List<FatalInjurySuicide> getFatalInjurySuicideList() {
	return fatalInjurySuicideList;
    }

    public void setFatalInjurySuicideList(List<FatalInjurySuicide> fatalInjurySuicideList) {
	this.fatalInjurySuicideList = fatalInjurySuicideList;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (relatedEventId != null ? relatedEventId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof RelatedEvents)) {
	    return false;
	}
	RelatedEvents other = (RelatedEvents) object;
	if ((this.relatedEventId == null && other.relatedEventId != null) || (this.relatedEventId != null && !this.relatedEventId.equals(other.relatedEventId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.RelatedEvents[ relatedEventId=" + relatedEventId + " ]";
    }
    
}
