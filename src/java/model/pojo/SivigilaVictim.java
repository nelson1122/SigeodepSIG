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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author santos
 */
@Entity
@Table(name = "sivigila_victim", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SivigilaVictim.findAll", query = "SELECT s FROM SivigilaVictim s"),
    @NamedQuery(name = "SivigilaVictim.findBySivigilaVictimId", query = "SELECT s FROM SivigilaVictim s WHERE s.sivigilaVictimId = :sivigilaVictimId"),
    @NamedQuery(name = "SivigilaVictim.findByOtherVulnerability", query = "SELECT s FROM SivigilaVictim s WHERE s.otherVulnerability = :otherVulnerability")})
public class SivigilaVictim implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "sivigila_victim_id", nullable = false)
    private Integer sivigilaVictimId;
    @Size(max = 2147483647)
    @Column(name = "other_vulnerability", length = 2147483647)
    private String otherVulnerability;
    @OneToMany(mappedBy = "sivigilaVictimId")
    private List<SivigilaEvent> sivigilaEventList;
    @JoinColumn(name = "vulnerability_id", referencedColumnName = "sivigila_vulnerability_id")
    @ManyToOne
    private SivigilaVulnerability vulnerabilityId;
    @JoinColumn(name = "health_category", referencedColumnName = "sivigila_tip_ss_id")
    @ManyToOne
    private SivigilaTipSs healthCategory;
    @JoinColumn(name = "educational_level_id", referencedColumnName = "sivigila_educational_level_id")
    @ManyToOne
    private SivigilaEducationalLevel educationalLevelId;
    @JoinColumn(name = "antecedent", referencedColumnName = "boolean_id")
    @ManyToOne
    private Boolean3 antecedent;

    public SivigilaVictim() {
    }

    public SivigilaVictim(Integer sivigilaVictimId) {
        this.sivigilaVictimId = sivigilaVictimId;
    }

    public Integer getSivigilaVictimId() {
        return sivigilaVictimId;
    }

    public void setSivigilaVictimId(Integer sivigilaVictimId) {
        this.sivigilaVictimId = sivigilaVictimId;
    }

    public String getOtherVulnerability() {
        return otherVulnerability;
    }

    public void setOtherVulnerability(String otherVulnerability) {
        this.otherVulnerability = otherVulnerability;
    }

    @XmlTransient
    public List<SivigilaEvent> getSivigilaEventList() {
        return sivigilaEventList;
    }

    public void setSivigilaEventList(List<SivigilaEvent> sivigilaEventList) {
        this.sivigilaEventList = sivigilaEventList;
    }

    public SivigilaVulnerability getVulnerabilityId() {
        return vulnerabilityId;
    }

    public void setVulnerabilityId(SivigilaVulnerability vulnerabilityId) {
        this.vulnerabilityId = vulnerabilityId;
    }

    public SivigilaTipSs getHealthCategory() {
        return healthCategory;
    }

    public void setHealthCategory(SivigilaTipSs healthCategory) {
        this.healthCategory = healthCategory;
    }

    public SivigilaEducationalLevel getEducationalLevelId() {
        return educationalLevelId;
    }

    public void setEducationalLevelId(SivigilaEducationalLevel educationalLevelId) {
        this.educationalLevelId = educationalLevelId;
    }

    public Boolean3 getAntecedent() {
        return antecedent;
    }

    public void setAntecedent(Boolean3 antecedent) {
        this.antecedent = antecedent;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (sivigilaVictimId != null ? sivigilaVictimId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SivigilaVictim)) {
            return false;
        }
        SivigilaVictim other = (SivigilaVictim) object;
        if ((this.sivigilaVictimId == null && other.sivigilaVictimId != null) || (this.sivigilaVictimId != null && !this.sivigilaVictimId.equals(other.sivigilaVictimId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pojos.SivigilaVictim[ sivigilaVictimId=" + sivigilaVictimId + " ]";
    }
    
}
