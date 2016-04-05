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
@Table(name = "sivigila_tip_ss", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"sivigila_tip_ss_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SivigilaTipSs.findAll", query = "SELECT s FROM SivigilaTipSs s"),
    @NamedQuery(name = "SivigilaTipSs.findBySivigilaTipSsId", query = "SELECT s FROM SivigilaTipSs s WHERE s.sivigilaTipSsId = :sivigilaTipSsId"),
    @NamedQuery(name = "SivigilaTipSs.findBySivigilaTipSsName", query = "SELECT s FROM SivigilaTipSs s WHERE s.sivigilaTipSsName = :sivigilaTipSsName")})
public class SivigilaTipSs implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "sivigila_tip_ss_id", nullable = false)
    private Integer sivigilaTipSsId;
    @Size(max = 2147483647)
    @Column(name = "sivigila_tip_ss_name", length = 2147483647)
    private String sivigilaTipSsName;
    @OneToMany(mappedBy = "healthCategory")
    private List<SivigilaVictim> sivigilaVictimList;

    public SivigilaTipSs() {
    }

    public SivigilaTipSs(Integer sivigilaTipSsId) {
        this.sivigilaTipSsId = sivigilaTipSsId;
    }

    public Integer getSivigilaTipSsId() {
        return sivigilaTipSsId;
    }

    public void setSivigilaTipSsId(Integer sivigilaTipSsId) {
        this.sivigilaTipSsId = sivigilaTipSsId;
    }

    public String getSivigilaTipSsName() {
        return sivigilaTipSsName;
    }

    public void setSivigilaTipSsName(String sivigilaTipSsName) {
        this.sivigilaTipSsName = sivigilaTipSsName;
    }

    @XmlTransient
    public List<SivigilaVictim> getSivigilaVictimList() {
        return sivigilaVictimList;
    }

    public void setSivigilaVictimList(List<SivigilaVictim> sivigilaVictimList) {
        this.sivigilaVictimList = sivigilaVictimList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (sivigilaTipSsId != null ? sivigilaTipSsId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SivigilaTipSs)) {
            return false;
        }
        SivigilaTipSs other = (SivigilaTipSs) object;
        if ((this.sivigilaTipSsId == null && other.sivigilaTipSsId != null) || (this.sivigilaTipSsId != null && !this.sivigilaTipSsId.equals(other.sivigilaTipSsId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.pojo.SivigilaTipSs[ sivigilaTipSsId=" + sivigilaTipSsId + " ]";
    }
    
}
