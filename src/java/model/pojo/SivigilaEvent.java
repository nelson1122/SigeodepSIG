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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author santos
 */
@Entity
@Table(name = "sivigila_event", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SivigilaEvent.findAll", query = "SELECT s FROM SivigilaEvent s"),
    @NamedQuery(name = "SivigilaEvent.findByNonFatalInjuryId", query = "SELECT s FROM SivigilaEvent s WHERE s.nonFatalInjuryId = :nonFatalInjuryId"),
    @NamedQuery(name = "SivigilaEvent.findByIntoxication", query = "SELECT s FROM SivigilaEvent s WHERE s.intoxication = :intoxication"),
    @NamedQuery(name = "SivigilaEvent.findByOthers", query = "SELECT s FROM SivigilaEvent s WHERE s.others = :others")})
public class SivigilaEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "non_fatal_injury_id", nullable = false)
    private Integer nonFatalInjuryId;
    @Size(max = 2147483647)
    @Column(name = "intoxication", length = 2147483647)
    private String intoxication;
    @Size(max = 2147483647)
    @Column(name = "others", length = 2147483647)
    private String others;
    //@ManyToMany(mappedBy = "sivigilaEventList")    
    //private List<PublicHealthActions> publicHealthActionsList;
    @JoinTable(name = "sivigila_event_public_health", joinColumns = {
        @JoinColumn(name = "non_fatal_injury_id", referencedColumnName = "non_fatal_injury_id", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "action_id", referencedColumnName = "action_id", nullable = false)})
    @ManyToMany
    private List<PublicHealthActions> publicHealthActionsList;
    @JoinColumn(name = "sivigila_victim_id", referencedColumnName = "sivigila_victim_id")
    @ManyToOne
    private SivigilaVictim sivigilaVictimId;
    @JoinColumn(name = "other_mechanism_id", referencedColumnName = "sivigila_other_mechanism_id")
    @ManyToOne
    private SivigilaOtherMechanism otherMechanismId;
    @JoinColumn(name = "mechanism_id", referencedColumnName = "sivigila_mechanism_id")
    @ManyToOne
    private SivigilaMechanism mechanismId;
    @JoinColumn(name = "sivigila_agresor_id", referencedColumnName = "sivigila_agresor_id")
    @ManyToOne
    private SivigilaAggresor sivigilaAgresorId;
    @JoinColumn(name = "non_fatal_injury_id", referencedColumnName = "non_fatal_injury_id", nullable = false, insertable = false, updatable = false)
    @OneToOne(optional = false)
    private NonFatalDomesticViolence nonFatalDomesticViolence;
    @JoinColumn(name = "further_fieldwork", referencedColumnName = "boolean_id")
    @ManyToOne
    private Boolean3 furtherFieldwork;
    @JoinColumn(name = "recommended_protection", referencedColumnName = "boolean_id")
    @ManyToOne
    private Boolean3 recommendedProtection;
    @JoinColumn(name = "conflict_zone", referencedColumnName = "boolean_id")
    @ManyToOne
    private Boolean3 conflictZone;
    @JoinColumn(name = "area", referencedColumnName = "area_id")
    @ManyToOne
    private Areas area;

    public SivigilaEvent() {
    }

    public SivigilaEvent(Integer nonFatalInjuryId) {
        this.nonFatalInjuryId = nonFatalInjuryId;
    }

    public Integer getNonFatalInjuryId() {
        return nonFatalInjuryId;
    }

    public void setNonFatalInjuryId(Integer nonFatalInjuryId) {
        this.nonFatalInjuryId = nonFatalInjuryId;
    }

    public String getIntoxication() {
        return intoxication;
    }

    public void setIntoxication(String intoxication) {
        this.intoxication = intoxication;
    }

    public String getOthers() {
        return others;
    }

    public void setOthers(String others) {
        this.others = others;
    }

    @XmlTransient
    public List<PublicHealthActions> getPublicHealthActionsList() {
        return publicHealthActionsList;
    }

    public void setPublicHealthActionsList(List<PublicHealthActions> publicHealthActionsList) {
        this.publicHealthActionsList = publicHealthActionsList;
    }

    public SivigilaVictim getSivigilaVictimId() {
        return sivigilaVictimId;
    }

    public void setSivigilaVictimId(SivigilaVictim sivigilaVictimId) {
        this.sivigilaVictimId = sivigilaVictimId;
    }

    public SivigilaOtherMechanism getOtherMechanismId() {
        return otherMechanismId;
    }

    public void setOtherMechanismId(SivigilaOtherMechanism otherMechanismId) {
        this.otherMechanismId = otherMechanismId;
    }

    public SivigilaMechanism getMechanismId() {
        return mechanismId;
    }

    public void setMechanismId(SivigilaMechanism mechanismId) {
        this.mechanismId = mechanismId;
    }

    public SivigilaAggresor getSivigilaAgresorId() {
        return sivigilaAgresorId;
    }

    public void setSivigilaAgresorId(SivigilaAggresor sivigilaAgresorId) {
        this.sivigilaAgresorId = sivigilaAgresorId;
    }

    public NonFatalDomesticViolence getNonFatalDomesticViolence() {
        return nonFatalDomesticViolence;
    }

    public void setNonFatalDomesticViolence(NonFatalDomesticViolence nonFatalDomesticViolence) {
        this.nonFatalDomesticViolence = nonFatalDomesticViolence;
    }

    public Boolean3 getFurtherFieldwork() {
        return furtherFieldwork;
    }

    public void setFurtherFieldwork(Boolean3 furtherFieldwork) {
        this.furtherFieldwork = furtherFieldwork;
    }

    public Boolean3 getRecommendedProtection() {
        return recommendedProtection;
    }

    public void setRecommendedProtection(Boolean3 recommendedProtection) {
        this.recommendedProtection = recommendedProtection;
    }

    public Boolean3 getConflictZone() {
        return conflictZone;
    }

    public void setConflictZone(Boolean3 conflictZone) {
        this.conflictZone = conflictZone;
    }

    public Areas getArea() {
        return area;
    }

    public void setArea(Areas area) {
        this.area = area;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (nonFatalInjuryId != null ? nonFatalInjuryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SivigilaEvent)) {
            return false;
        }
        SivigilaEvent other = (SivigilaEvent) object;
        if ((this.nonFatalInjuryId == null && other.nonFatalInjuryId != null) || (this.nonFatalInjuryId != null && !this.nonFatalInjuryId.equals(other.nonFatalInjuryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pojos.SivigilaEvent[ nonFatalInjuryId=" + nonFatalInjuryId + " ]";
    }
    
}
