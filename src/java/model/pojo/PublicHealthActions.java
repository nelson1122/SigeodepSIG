/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author santos
 */
@Entity
@Table(name = "public_health_actions", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"action_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PublicHealthActions.findAll", query = "SELECT p FROM PublicHealthActions p"),
    @NamedQuery(name = "PublicHealthActions.findByActionId", query = "SELECT p FROM PublicHealthActions p WHERE p.actionId = :actionId"),
    @NamedQuery(name = "PublicHealthActions.findByActionName", query = "SELECT p FROM PublicHealthActions p WHERE p.actionName = :actionName")})
public class PublicHealthActions implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "action_id", nullable = false)
    private Short actionId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "action_name", nullable = false, length = 2147483647)
    private String actionName;
//    @JoinTable(name = "sivigila_event_public_health", joinColumns = {
//        @JoinColumn(name = "action_id", referencedColumnName = "action_id", nullable = false)}, inverseJoinColumns = {
//        @JoinColumn(name = "non_fatal_injury_id", referencedColumnName = "non_fatal_injury_id", nullable = false)})
//    @ManyToMany
//    private List<SivigilaEvent> sivigilaEventList;
    @ManyToMany(mappedBy = "publicHealthActionsList")
    private List<SivigilaEvent> sivigilaEventList;
    
    
    public PublicHealthActions() {
    }

    public PublicHealthActions(Short actionId) {
        this.actionId = actionId;
    }

    public PublicHealthActions(Short actionId, String actionName) {
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
    public List<SivigilaEvent> getSivigilaEventList() {
        return sivigilaEventList;
    }

    public void setSivigilaEventList(List<SivigilaEvent> sivigilaEventList) {
        this.sivigilaEventList = sivigilaEventList;
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
        if (!(object instanceof PublicHealthActions)) {
            return false;
        }
        PublicHealthActions other = (PublicHealthActions) object;
        if ((this.actionId == null && other.actionId != null) || (this.actionId != null && !this.actionId.equals(other.actionId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.pojo.PublicHealthActions[ actionId=" + actionId + " ]";
    }
    
}
