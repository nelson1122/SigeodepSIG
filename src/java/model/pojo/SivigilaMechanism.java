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
@Table(name = "sivigila_mechanism", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"sivigila_mechanism_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SivigilaMechanism.findAll", query = "SELECT s FROM SivigilaMechanism s"),
    @NamedQuery(name = "SivigilaMechanism.findBySivigilaMechanismId", query = "SELECT s FROM SivigilaMechanism s WHERE s.sivigilaMechanismId = :sivigilaMechanismId"),
    @NamedQuery(name = "SivigilaMechanism.findBySivigilaMechanismName", query = "SELECT s FROM SivigilaMechanism s WHERE s.sivigilaMechanismName = :sivigilaMechanismName")})
public class SivigilaMechanism implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "sivigila_mechanism_id", nullable = false)
    private Short sivigilaMechanismId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "sivigila_mechanism_name", nullable = false, length = 2147483647)
    private String sivigilaMechanismName;
    @OneToMany(mappedBy = "mechanismId")
    private List<SivigilaEvent> sivigilaEventList;

    public SivigilaMechanism() {
    }

    public SivigilaMechanism(Short sivigilaMechanismId) {
        this.sivigilaMechanismId = sivigilaMechanismId;
    }

    public SivigilaMechanism(Short sivigilaMechanismId, String sivigilaMechanismName) {
        this.sivigilaMechanismId = sivigilaMechanismId;
        this.sivigilaMechanismName = sivigilaMechanismName;
    }

    public Short getSivigilaMechanismId() {
        return sivigilaMechanismId;
    }

    public void setSivigilaMechanismId(Short sivigilaMechanismId) {
        this.sivigilaMechanismId = sivigilaMechanismId;
    }

    public String getSivigilaMechanismName() {
        return sivigilaMechanismName;
    }

    public void setSivigilaMechanismName(String sivigilaMechanismName) {
        this.sivigilaMechanismName = sivigilaMechanismName;
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
        hash += (sivigilaMechanismId != null ? sivigilaMechanismId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SivigilaMechanism)) {
            return false;
        }
        SivigilaMechanism other = (SivigilaMechanism) object;
        if ((this.sivigilaMechanismId == null && other.sivigilaMechanismId != null) || (this.sivigilaMechanismId != null && !this.sivigilaMechanismId.equals(other.sivigilaMechanismId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pojos.SivigilaMechanism[ sivigilaMechanismId=" + sivigilaMechanismId + " ]";
    }
    
}
