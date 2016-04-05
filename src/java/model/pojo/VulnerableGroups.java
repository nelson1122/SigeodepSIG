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
@Table(name = "vulnerable_groups", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"vulnerable_group_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "VulnerableGroups.findAll", query = "SELECT v FROM VulnerableGroups v"),
    @NamedQuery(name = "VulnerableGroups.findByVulnerableGroupId", query = "SELECT v FROM VulnerableGroups v WHERE v.vulnerableGroupId = :vulnerableGroupId"),
    @NamedQuery(name = "VulnerableGroups.findByVulnerableGroupName", query = "SELECT v FROM VulnerableGroups v WHERE v.vulnerableGroupName = :vulnerableGroupName")})
public class VulnerableGroups implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "vulnerable_group_id", nullable = false)
    private Short vulnerableGroupId;
    @Size(max = 2147483647)
    @Column(name = "vulnerable_group_name", length = 2147483647)
    private String vulnerableGroupName;
    @ManyToMany(mappedBy = "vulnerableGroupsList")
    private List<Victims> victimsList;

    public VulnerableGroups() {
    }

    public VulnerableGroups(Short vulnerableGroupId) {
	this.vulnerableGroupId = vulnerableGroupId;
    }

    public Short getVulnerableGroupId() {
	return vulnerableGroupId;
    }

    public void setVulnerableGroupId(Short vulnerableGroupId) {
	this.vulnerableGroupId = vulnerableGroupId;
    }

    public String getVulnerableGroupName() {
	return vulnerableGroupName;
    }

    public void setVulnerableGroupName(String vulnerableGroupName) {
	this.vulnerableGroupName = vulnerableGroupName;
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
	hash += (vulnerableGroupId != null ? vulnerableGroupId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof VulnerableGroups)) {
	    return false;
	}
	VulnerableGroups other = (VulnerableGroups) object;
	if ((this.vulnerableGroupId == null && other.vulnerableGroupId != null) || (this.vulnerableGroupId != null && !this.vulnerableGroupId.equals(other.vulnerableGroupId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.VulnerableGroups[ vulnerableGroupId=" + vulnerableGroupId + " ]";
    }
    
}
