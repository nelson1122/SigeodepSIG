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
@Table(name = "insurance", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"insurance_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Insurance.findAll", query = "SELECT i FROM Insurance i"),
    @NamedQuery(name = "Insurance.findByInsuranceId", query = "SELECT i FROM Insurance i WHERE i.insuranceId = :insuranceId"),
    @NamedQuery(name = "Insurance.findByInsuranceName", query = "SELECT i FROM Insurance i WHERE i.insuranceName = :insuranceName")})
public class Insurance implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "insurance_id", nullable = false, length = 2147483647)
    private String insuranceId;
    @Size(max = 2147483647)
    @Column(name = "insurance_name", length = 2147483647)
    private String insuranceName;
    @OneToMany(mappedBy = "insuranceId")
    private List<Victims> victimsList;

    public Insurance() {
    }

    public Insurance(String insuranceId) {
        this.insuranceId = insuranceId;
    }

    public String getInsuranceId() {
        return insuranceId;
    }

    public void setInsuranceId(String insuranceId) {
        this.insuranceId = insuranceId;
    }

    public String getInsuranceName() {
        return insuranceName;
    }

    public void setInsuranceName(String insuranceName) {
        this.insuranceName = insuranceName;
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
        hash += (insuranceId != null ? insuranceId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Insurance)) {
            return false;
        }
        Insurance other = (Insurance) object;
        if ((this.insuranceId == null && other.insuranceId != null) || (this.insuranceId != null && !this.insuranceId.equals(other.insuranceId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pojos.Insurance[ insuranceId=" + insuranceId + " ]";
    }
    
}
