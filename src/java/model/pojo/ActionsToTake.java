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
@Table(name = "actions_to_take", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"action_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ActionsToTake.findAll", query = "SELECT a FROM ActionsToTake a"),
    @NamedQuery(name = "ActionsToTake.findByActionId", query = "SELECT a FROM ActionsToTake a WHERE a.actionId = :actionId"),
    @NamedQuery(name = "ActionsToTake.findByActionName", query = "SELECT a FROM ActionsToTake a WHERE a.actionName = :actionName")})
public class ActionsToTake implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "action_id", nullable = false)
    private Short actionId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "action_name", nullable = false, length = 40)
    private String actionName;
    @ManyToMany(mappedBy = "actionsToTakeList")
    private List<NonFatalDomesticViolence> nonFatalDomesticViolenceList;

    public ActionsToTake() {
    }

    public ActionsToTake(Short actionId) {
	this.actionId = actionId;
    }

    public ActionsToTake(Short actionId, String actionName) {
	this.actionId = actionId;
	this.actionName = actionName;
    }

    public Short getActionId() {
	return actionId;
    }

    public void setActionId(Short actionId) {
	this.actionId = actionId;
    }

    public String getActionName() {
	return actionName;
    }

    public void setActionName(String actionName) {
	this.actionName = actionName;
    }

    @XmlTransient
    public List<NonFatalDomesticViolence> getNonFatalDomesticViolenceList() {
	return nonFatalDomesticViolenceList;
    }

    public void setNonFatalDomesticViolenceList(List<NonFatalDomesticViolence> nonFatalDomesticViolenceList) {
	this.nonFatalDomesticViolenceList = nonFatalDomesticViolenceList;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (actionId != null ? actionId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof ActionsToTake)) {
	    return false;
	}
	ActionsToTake other = (ActionsToTake) object;
	if ((this.actionId == null && other.actionId != null) || (this.actionId != null && !this.actionId.equals(other.actionId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.ActionsToTake[ actionId=" + actionId + " ]";
    }
    
}
