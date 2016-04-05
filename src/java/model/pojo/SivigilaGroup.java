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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
@Table(name = "sivigila_group", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"sivigila_group_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SivigilaGroup.findAll", query = "SELECT s FROM SivigilaGroup s"),
    @NamedQuery(name = "SivigilaGroup.findBySivigilaGroupId", query = "SELECT s FROM SivigilaGroup s WHERE s.sivigilaGroupId = :sivigilaGroupId"),
    @NamedQuery(name = "SivigilaGroup.findBySivigilaGroupName", query = "SELECT s FROM SivigilaGroup s WHERE s.sivigilaGroupName = :sivigilaGroupName")})
public class SivigilaGroup implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "sivigila_group_id", nullable = false)
    private Short sivigilaGroupId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "sivigila_group_name", nullable = false, length = 2147483647)
    private String sivigilaGroupName;
    @OneToMany(mappedBy = "groupId")
    private List<SivigilaAggresor> sivigilaAggresorList;

    public SivigilaGroup() {
    }

    public SivigilaGroup(Short sivigilaGroupId) {
        this.sivigilaGroupId = sivigilaGroupId;
    }

    public SivigilaGroup(Short sivigilaGroupId, String sivigilaGroupName) {
        this.sivigilaGroupId = sivigilaGroupId;
        this.sivigilaGroupName = sivigilaGroupName;
    }

    public Short getSivigilaGroupId() {
        return sivigilaGroupId;
    }

    public void setSivigilaGroupId(Short sivigilaGroupId) {
        this.sivigilaGroupId = sivigilaGroupId;
    }

    public String getSivigilaGroupName() {
        return sivigilaGroupName;
    }

    public void setSivigilaGroupName(String sivigilaGroupName) {
        this.sivigilaGroupName = sivigilaGroupName;
    }

    @XmlTransient
    public List<SivigilaAggresor> getSivigilaAggresorList() {
        return sivigilaAggresorList;
    }

    public void setSivigilaAggresorList(List<SivigilaAggresor> sivigilaAggresorList) {
        this.sivigilaAggresorList = sivigilaAggresorList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (sivigilaGroupId != null ? sivigilaGroupId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SivigilaGroup)) {
            return false;
        }
        SivigilaGroup other = (SivigilaGroup) object;
        if ((this.sivigilaGroupId == null && other.sivigilaGroupId != null) || (this.sivigilaGroupId != null && !this.sivigilaGroupId.equals(other.sivigilaGroupId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.pojo.SivigilaGroup[ sivigilaGroupId=" + sivigilaGroupId + " ]";
    }
    
}
