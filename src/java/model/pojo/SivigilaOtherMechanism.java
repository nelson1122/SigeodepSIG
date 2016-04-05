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
@Table(name = "sivigila_other_mechanism", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"sivigila_other_mechanism_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SivigilaOtherMechanism.findAll", query = "SELECT s FROM SivigilaOtherMechanism s"),
    @NamedQuery(name = "SivigilaOtherMechanism.findBySivigilaOtherMechanismId", query = "SELECT s FROM SivigilaOtherMechanism s WHERE s.sivigilaOtherMechanismId = :sivigilaOtherMechanismId"),
    @NamedQuery(name = "SivigilaOtherMechanism.findBySivigilaOtherMechanismName", query = "SELECT s FROM SivigilaOtherMechanism s WHERE s.sivigilaOtherMechanismName = :sivigilaOtherMechanismName")})
public class SivigilaOtherMechanism implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "sivigila_other_mechanism_id", nullable = false)
    private Short sivigilaOtherMechanismId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "sivigila_other_mechanism_name", nullable = false, length = 2147483647)
    private String sivigilaOtherMechanismName;
    @OneToMany(mappedBy = "otherMechanismId")
    private List<SivigilaEvent> sivigilaEventList;

    public SivigilaOtherMechanism() {
    }

    public SivigilaOtherMechanism(Short sivigilaOtherMechanismId) {
        this.sivigilaOtherMechanismId = sivigilaOtherMechanismId;
    }

    public SivigilaOtherMechanism(Short sivigilaOtherMechanismId, String sivigilaOtherMechanismName) {
        this.sivigilaOtherMechanismId = sivigilaOtherMechanismId;
        this.sivigilaOtherMechanismName = sivigilaOtherMechanismName;
    }

    public Short getSivigilaOtherMechanismId() {
        return sivigilaOtherMechanismId;
    }

    public void setSivigilaOtherMechanismId(Short sivigilaOtherMechanismId) {
        this.sivigilaOtherMechanismId = sivigilaOtherMechanismId;
    }

    public String getSivigilaOtherMechanismName() {
        return sivigilaOtherMechanismName;
    }

    public void setSivigilaOtherMechanismName(String sivigilaOtherMechanismName) {
        this.sivigilaOtherMechanismName = sivigilaOtherMechanismName;
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
        hash += (sivigilaOtherMechanismId != null ? sivigilaOtherMechanismId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SivigilaOtherMechanism)) {
            return false;
        }
        SivigilaOtherMechanism other = (SivigilaOtherMechanism) object;
        if ((this.sivigilaOtherMechanismId == null && other.sivigilaOtherMechanismId != null) || (this.sivigilaOtherMechanismId != null && !this.sivigilaOtherMechanismId.equals(other.sivigilaOtherMechanismId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.pojo.SivigilaOtherMechanism[ sivigilaOtherMechanismId=" + sivigilaOtherMechanismId + " ]";
    }
    
}
