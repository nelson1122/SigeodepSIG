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
@Table(name = "activities", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"activity_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Activities.findAll", query = "SELECT a FROM Activities a"),
    @NamedQuery(name = "Activities.findByActivityId", query = "SELECT a FROM Activities a WHERE a.activityId = :activityId"),
    @NamedQuery(name = "Activities.findByActivityName", query = "SELECT a FROM Activities a WHERE a.activityName = :activityName")})
public class Activities implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "activity_id", nullable = false)
    private Short activityId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "activity_name", nullable = false, length = 2147483647)
    private String activityName;
    @OneToMany(mappedBy = "activityId")
    private List<NonFatalInjuries> nonFatalInjuriesList;

    public Activities() {
    }

    public Activities(Short activityId) {
	this.activityId = activityId;
    }

    public Activities(Short activityId, String activityName) {
	this.activityId = activityId;
	this.activityName = activityName;
    }

    public Short getActivityId() {
	return activityId;
    }

    public void setActivityId(Short activityId) {
	this.activityId = activityId;
    }

    public String getActivityName() {
	return activityName;
    }

    public void setActivityName(String activityName) {
	this.activityName = activityName;
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
	hash += (activityId != null ? activityId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof Activities)) {
	    return false;
	}
	Activities other = (Activities) object;
	if ((this.activityId == null && other.activityId != null) || (this.activityId != null && !this.activityId.equals(other.activityId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.Activities[ activityId=" + activityId + " ]";
    }
    
}
