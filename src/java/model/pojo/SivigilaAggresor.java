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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
@Table(name = "sivigila_aggresor", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SivigilaAggresor.findAll", query = "SELECT s FROM SivigilaAggresor s"),
    @NamedQuery(name = "SivigilaAggresor.findBySivigilaAgresorId", query = "SELECT s FROM SivigilaAggresor s WHERE s.sivigilaAgresorId = :sivigilaAgresorId"),
    @NamedQuery(name = "SivigilaAggresor.findByAge", query = "SELECT s FROM SivigilaAggresor s WHERE s.age = :age"),
    @NamedQuery(name = "SivigilaAggresor.findByOtherRelative", query = "SELECT s FROM SivigilaAggresor s WHERE s.otherRelative = :otherRelative"),
    @NamedQuery(name = "SivigilaAggresor.findByOtherNoRelative", query = "SELECT s FROM SivigilaAggresor s WHERE s.otherNoRelative = :otherNoRelative"),
    @NamedQuery(name = "SivigilaAggresor.findByOtherGroup", query = "SELECT s FROM SivigilaAggresor s WHERE s.otherGroup = :otherGroup")})
public class SivigilaAggresor implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "sivigila_agresor_id", nullable = false)
    private Integer sivigilaAgresorId;
    @Column(name = "age")
    private Integer age;
    @Size(max = 2147483647)
    @Column(name = "other_relative", length = 2147483647)
    private String otherRelative;
    @Size(max = 2147483647)
    @Column(name = "other_no_relative", length = 2147483647)
    private String otherNoRelative;
    @Size(max = 2147483647)
    @Column(name = "other_group", length = 2147483647)
    private String otherGroup;
    @OneToMany(mappedBy = "sivigilaAgresorId")
    private List<SivigilaEvent> sivigilaEventList;
    @JoinColumn(name = "no_relative_id", referencedColumnName = "sivigila_no_relative_id")
    @ManyToOne
    private SivigilaNoRelative noRelativeId;
    @JoinColumn(name = "group_id", referencedColumnName = "sivigila_group_id")
    @ManyToOne
    private SivigilaGroup groupId;
    @JoinColumn(name = "educational_level_id", referencedColumnName = "sivigila_educational_level_id")
    @ManyToOne
    private SivigilaEducationalLevel educationalLevelId;
    @JoinColumn(name = "occupation", referencedColumnName = "job_id")
    @ManyToOne
    private Jobs occupation;
    @JoinColumn(name = "gender", referencedColumnName = "gender_id")
    @ManyToOne
    private Genders gender;
    @JoinColumn(name = "alcohol_or_drugs", referencedColumnName = "boolean_id")
    @ManyToOne
    private Boolean3 alcoholOrDrugs;
    @JoinColumn(name = "live_together", referencedColumnName = "boolean_id")
    @ManyToOne
    private Boolean3 liveTogether;
    @JoinColumn(name = "relative_id", referencedColumnName = "aggressor_type_id")
    @ManyToOne
    private AggressorTypes relativeId;

    public SivigilaAggresor() {
    }

    public SivigilaAggresor(Integer sivigilaAgresorId) {
        this.sivigilaAgresorId = sivigilaAgresorId;
    }

    public Integer getSivigilaAgresorId() {
        return sivigilaAgresorId;
    }

    public void setSivigilaAgresorId(Integer sivigilaAgresorId) {
        this.sivigilaAgresorId = sivigilaAgresorId;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getOtherRelative() {
        return otherRelative;
    }

    public void setOtherRelative(String otherRelative) {
        this.otherRelative = otherRelative;
    }

    public String getOtherNoRelative() {
        return otherNoRelative;
    }

    public void setOtherNoRelative(String otherNoRelative) {
        this.otherNoRelative = otherNoRelative;
    }

    public String getOtherGroup() {
        return otherGroup;
    }

    public void setOtherGroup(String otherGroup) {
        this.otherGroup = otherGroup;
    }

    @XmlTransient
    public List<SivigilaEvent> getSivigilaEventList() {
        return sivigilaEventList;
    }

    public void setSivigilaEventList(List<SivigilaEvent> sivigilaEventList) {
        this.sivigilaEventList = sivigilaEventList;
    }

    public SivigilaNoRelative getNoRelativeId() {
        return noRelativeId;
    }

    public void setNoRelativeId(SivigilaNoRelative noRelativeId) {
        this.noRelativeId = noRelativeId;
    }

    public SivigilaGroup getGroupId() {
        return groupId;
    }

    public void setGroupId(SivigilaGroup groupId) {
        this.groupId = groupId;
    }

    public SivigilaEducationalLevel getEducationalLevelId() {
        return educationalLevelId;
    }

    public void setEducationalLevelId(SivigilaEducationalLevel educationalLevelId) {
        this.educationalLevelId = educationalLevelId;
    }

    public Jobs getOccupation() {
        return occupation;
    }

    public void setOccupation(Jobs occupation) {
        this.occupation = occupation;
    }

    public Genders getGender() {
        return gender;
    }

    public void setGender(Genders gender) {
        this.gender = gender;
    }

    public Boolean3 getAlcoholOrDrugs() {
        return alcoholOrDrugs;
    }

    public void setAlcoholOrDrugs(Boolean3 alcoholOrDrugs) {
        this.alcoholOrDrugs = alcoholOrDrugs;
    }

    public Boolean3 getLiveTogether() {
        return liveTogether;
    }

    public void setLiveTogether(Boolean3 liveTogether) {
        this.liveTogether = liveTogether;
    }

    public AggressorTypes getRelativeId() {
        return relativeId;
    }

    public void setRelativeId(AggressorTypes relativeId) {
        this.relativeId = relativeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (sivigilaAgresorId != null ? sivigilaAgresorId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SivigilaAggresor)) {
            return false;
        }
        SivigilaAggresor other = (SivigilaAggresor) object;
        if ((this.sivigilaAgresorId == null && other.sivigilaAgresorId != null) || (this.sivigilaAgresorId != null && !this.sivigilaAgresorId.equals(other.sivigilaAgresorId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pojos.SivigilaAggresor[ sivigilaAgresorId=" + sivigilaAgresorId + " ]";
    }
    
}
