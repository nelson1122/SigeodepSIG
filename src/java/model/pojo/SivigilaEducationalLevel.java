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
@Table(name = "sivigila_educational_level", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"sivigila_educational_level_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SivigilaEducationalLevel.findAll", query = "SELECT s FROM SivigilaEducationalLevel s"),
    @NamedQuery(name = "SivigilaEducationalLevel.findBySivigilaEducationalLevelId", query = "SELECT s FROM SivigilaEducationalLevel s WHERE s.sivigilaEducationalLevelId = :sivigilaEducationalLevelId"),
    @NamedQuery(name = "SivigilaEducationalLevel.findBySivigilaEducationalLevelName", query = "SELECT s FROM SivigilaEducationalLevel s WHERE s.sivigilaEducationalLevelName = :sivigilaEducationalLevelName")})
public class SivigilaEducationalLevel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "sivigila_educational_level_id", nullable = false)
    private Short sivigilaEducationalLevelId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "sivigila_educational_level_name", nullable = false, length = 2147483647)
    private String sivigilaEducationalLevelName;
    @OneToMany(mappedBy = "educationalLevelId")
    private List<SivigilaAggresor> sivigilaAggresorList;
    @OneToMany(mappedBy = "educationalLevelId")
    private List<SivigilaVictim> sivigilaVictimList;

    public SivigilaEducationalLevel() {
    }

    public SivigilaEducationalLevel(Short sivigilaEducationalLevelId) {
        this.sivigilaEducationalLevelId = sivigilaEducationalLevelId;
    }

    public SivigilaEducationalLevel(Short sivigilaEducationalLevelId, String sivigilaEducationalLevelName) {
        this.sivigilaEducationalLevelId = sivigilaEducationalLevelId;
        this.sivigilaEducationalLevelName = sivigilaEducationalLevelName;
    }

    public Short getSivigilaEducationalLevelId() {
        return sivigilaEducationalLevelId;
    }

    public void setSivigilaEducationalLevelId(Short sivigilaEducationalLevelId) {
        this.sivigilaEducationalLevelId = sivigilaEducationalLevelId;
    }

    public String getSivigilaEducationalLevelName() {
        return sivigilaEducationalLevelName;
    }

    public void setSivigilaEducationalLevelName(String sivigilaEducationalLevelName) {
        this.sivigilaEducationalLevelName = sivigilaEducationalLevelName;
    }

    @XmlTransient
    public List<SivigilaAggresor> getSivigilaAggresorList() {
        return sivigilaAggresorList;
    }

    public void setSivigilaAggresorList(List<SivigilaAggresor> sivigilaAggresorList) {
        this.sivigilaAggresorList = sivigilaAggresorList;
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
        hash += (sivigilaEducationalLevelId != null ? sivigilaEducationalLevelId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SivigilaEducationalLevel)) {
            return false;
        }
        SivigilaEducationalLevel other = (SivigilaEducationalLevel) object;
        if ((this.sivigilaEducationalLevelId == null && other.sivigilaEducationalLevelId != null) || (this.sivigilaEducationalLevelId != null && !this.sivigilaEducationalLevelId.equals(other.sivigilaEducationalLevelId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.pojo.SivigilaEducationalLevel[ sivigilaEducationalLevelId=" + sivigilaEducationalLevelId + " ]";
    }
    
}
