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
@Table(name = "ethnic_groups", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"ethnic_group_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EthnicGroups.findAll", query = "SELECT e FROM EthnicGroups e"),
    @NamedQuery(name = "EthnicGroups.findByEthnicGroupId", query = "SELECT e FROM EthnicGroups e WHERE e.ethnicGroupId = :ethnicGroupId"),
    @NamedQuery(name = "EthnicGroups.findByEthnicGroupName", query = "SELECT e FROM EthnicGroups e WHERE e.ethnicGroupName = :ethnicGroupName")})
public class EthnicGroups implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ethnic_group_id", nullable = false)
    private Short ethnicGroupId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "ethnic_group_name", nullable = false, length = 2147483647)
    private String ethnicGroupName;
    @OneToMany(mappedBy = "ethnicGroupId")
    private List<Victims> victimsList;

    public EthnicGroups() {
    }

    public EthnicGroups(Short ethnicGroupId) {
	this.ethnicGroupId = ethnicGroupId;
    }

    public EthnicGroups(Short ethnicGroupId, String ethnicGroupName) {
	this.ethnicGroupId = ethnicGroupId;
	this.ethnicGroupName = ethnicGroupName;
    }

    public Short getEthnicGroupId() {
	return ethnicGroupId;
    }

    public void setEthnicGroupId(Short ethnicGroupId) {
	this.ethnicGroupId = ethnicGroupId;
    }

    public String getEthnicGroupName() {
	return ethnicGroupName;
    }

    public void setEthnicGroupName(String ethnicGroupName) {
	this.ethnicGroupName = ethnicGroupName;
    }

    @XmlTransient
    public List<Victims> getVictimsList() {
	return victimsList;
    }

    public void setVictimsList(List<Victims> victimsList) {
	this.victimsList = victimsList;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (ethnicGroupId != null ? ethnicGroupId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof EthnicGroups)) {
	    return false;
	}
	EthnicGroups other = (EthnicGroups) object;
	if ((this.ethnicGroupId == null && other.ethnicGroupId != null) || (this.ethnicGroupId != null && !this.ethnicGroupId.equals(other.ethnicGroupId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.EthnicGroups[ ethnicGroupId=" + ethnicGroupId + " ]";
    }
    
}
