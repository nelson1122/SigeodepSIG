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
@Table(name = "sivigila_no_relative", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"sivigila_no_relative_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SivigilaNoRelative.findAll", query = "SELECT s FROM SivigilaNoRelative s"),
    @NamedQuery(name = "SivigilaNoRelative.findBySivigilaNoRelativeId", query = "SELECT s FROM SivigilaNoRelative s WHERE s.sivigilaNoRelativeId = :sivigilaNoRelativeId"),
    @NamedQuery(name = "SivigilaNoRelative.findBySivigilaNoRelativeName", query = "SELECT s FROM SivigilaNoRelative s WHERE s.sivigilaNoRelativeName = :sivigilaNoRelativeName")})
public class SivigilaNoRelative implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "sivigila_no_relative_id", nullable = false)
    private Short sivigilaNoRelativeId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "sivigila_no_relative_name", nullable = false, length = 2147483647)
    private String sivigilaNoRelativeName;
    @OneToMany(mappedBy = "noRelativeId")
    private List<SivigilaAggresor> sivigilaAggresorList;

    public SivigilaNoRelative() {
    }

    public SivigilaNoRelative(Short sivigilaNoRelativeId) {
        this.sivigilaNoRelativeId = sivigilaNoRelativeId;
    }

    public SivigilaNoRelative(Short sivigilaNoRelativeId, String sivigilaNoRelativeName) {
        this.sivigilaNoRelativeId = sivigilaNoRelativeId;
        this.sivigilaNoRelativeName = sivigilaNoRelativeName;
    }

    public Short getSivigilaNoRelativeId() {
        return sivigilaNoRelativeId;
    }

    public void setSivigilaNoRelativeId(Short sivigilaNoRelativeId) {
        this.sivigilaNoRelativeId = sivigilaNoRelativeId;
    }

    public String getSivigilaNoRelativeName() {
        return sivigilaNoRelativeName;
    }

    public void setSivigilaNoRelativeName(String sivigilaNoRelativeName) {
        this.sivigilaNoRelativeName = sivigilaNoRelativeName;
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
        hash += (sivigilaNoRelativeId != null ? sivigilaNoRelativeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SivigilaNoRelative)) {
            return false;
        }
        SivigilaNoRelative other = (SivigilaNoRelative) object;
        if ((this.sivigilaNoRelativeId == null && other.sivigilaNoRelativeId != null) || (this.sivigilaNoRelativeId != null && !this.sivigilaNoRelativeId.equals(other.sivigilaNoRelativeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.pojo.SivigilaNoRelative[ sivigilaNoRelativeId=" + sivigilaNoRelativeId + " ]";
    }
    
}
