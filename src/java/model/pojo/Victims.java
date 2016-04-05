/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
import java.util.Date;
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
@Table(name = "victims", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"victim_id"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Victims.findAll", query = "SELECT v FROM Victims v"),
    @NamedQuery(name = "Victims.findByVictimNid", query = "SELECT v FROM Victims v WHERE v.victimNid = :victimNid"),
    @NamedQuery(name = "Victims.findByVictimName", query = "SELECT v FROM Victims v WHERE v.victimName = :victimName"),
    @NamedQuery(name = "Victims.findByVictimAge", query = "SELECT v FROM Victims v WHERE v.victimAge = :victimAge"),
    @NamedQuery(name = "Victims.findByAgeTypeId", query = "SELECT v FROM Victims v WHERE v.ageTypeId = :ageTypeId"),
    @NamedQuery(name = "Victims.findByVictimTelephone", query = "SELECT v FROM Victims v WHERE v.victimTelephone = :victimTelephone"),
    @NamedQuery(name = "Victims.findByVictimAddress", query = "SELECT v FROM Victims v WHERE v.victimAddress = :victimAddress"),
    @NamedQuery(name = "Victims.findByVictimDateOfBirth", query = "SELECT v FROM Victims v WHERE v.victimDateOfBirth = :victimDateOfBirth"),
    @NamedQuery(name = "Victims.findByVictimClass", query = "SELECT v FROM Victims v WHERE v.victimClass = :victimClass"),
    @NamedQuery(name = "Victims.findByVictimId", query = "SELECT v FROM Victims v WHERE v.victimId = :victimId"),
    @NamedQuery(name = "Victims.findByResidenceMunicipality", query = "SELECT v FROM Victims v WHERE v.residenceMunicipality = :residenceMunicipality")})
public class Victims implements Serializable {

    private static final long serialVersionUID = 1L;
//    @Size(max = 20)
//    @Column(name = "victim_nid", length = 20)
    @Size(max = 2147483647)
    @Column(name = "victim_nid", length = 2147483647)
    private String victimNid;
    @Size(max = 2147483647)
    @Column(name = "victim_name", length = 2147483647)
    private String victimName;
    @Column(name = "victim_age")
    private Short victimAge;
    @Column(name = "age_type_id")
    private Short ageTypeId;
    //@Size(max = 20)
    //@Column(name = "victim_telephone", length = 20)
    @Size(max = 2147483647)
    @Column(name = "victim_telephone", length = 2147483647)
    private String victimTelephone;
    @Size(max = 2147483647)
    @Column(name = "victim_address", length = 2147483647)
    private String victimAddress;
    @Column(name = "victim_date_of_birth")
    @Temporal(TemporalType.DATE)
    private Date victimDateOfBirth;
    @Column(name = "victim_class")
    private Short victimClass;
    @Column(name = "stranger")
    private Boolean stranger;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "victim_id", nullable = false)
    private Integer victimId;
    @Column(name = "residence_municipality")
    private Short residenceMunicipality;
    @Column(name = "residence_department")
    private Short residenceDepartment;
    @JoinTable(name = "victim_vulnerable_group", joinColumns = {
        @JoinColumn(name = "victim_id", referencedColumnName = "victim_id", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "vulnerable_group_id", referencedColumnName = "vulnerable_group_id", nullable = false)})
    @ManyToMany
    private List<VulnerableGroups> vulnerableGroupsList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "victims")
    private List<Others> othersList;
    @JoinColumn(name = "victim_neighborhood_id", referencedColumnName = "neighborhood_id")
    @ManyToOne
    private Neighborhoods victimNeighborhoodId;
    @JoinColumn(name = "job_id", referencedColumnName = "job_id")
    @ManyToOne
    private Jobs jobId;
    @JoinColumn(name = "insurance_id", referencedColumnName = "insurance_id")
    @ManyToOne
    private Insurance insuranceId;
    @JoinColumn(name = "type_id", referencedColumnName = "type_id")
    @ManyToOne
    private IdTypes typeId;
    @JoinColumn(name = "gender_id", referencedColumnName = "gender_id")
    @ManyToOne
    private Genders genderId;
    @JoinColumn(name = "ethnic_group_id", referencedColumnName = "ethnic_group_id")
    @ManyToOne
    private EthnicGroups ethnicGroupId;
    @OneToMany(mappedBy = "victimId")
    private List<NonFatalInjuries> nonFatalInjuriesList;
    @OneToMany(mappedBy = "victimId")
    private List<FatalInjuries> fatalInjuriesList;
    @JoinColumn(name = "tag_id", referencedColumnName = "tag_id")
    @ManyToOne
    private Tags tagId;
    @Column(name = "first_tag_id")
    private Integer firstTagId;

    public Victims() {
    }

    public Victims(Integer victimId) {
        this.victimId = victimId;
    }

    public String getVictimNid() {
        return victimNid;
    }

    public void setVictimNid(String victimNid) {
        this.victimNid = victimNid;
    }

    public Boolean getStranger() {
        return stranger;
    }

    public void setStranger(Boolean stranger) {
        this.stranger = stranger;
    }

    public String getVictimName() {
        return victimName;
    }

    public void setVictimName(String victimName) {
        this.victimName = victimName;
    }

    public Short getVictimAge() {
        return victimAge;
    }

    public void setVictimAge(Short victimAge) {
        this.victimAge = victimAge;
    }

    public Short getAgeTypeId() {
        return ageTypeId;
    }

    public void setAgeTypeId(Short ageTypeId) {
        this.ageTypeId = ageTypeId;
    }

    public String getVictimTelephone() {
        return victimTelephone;
    }

    public void setVictimTelephone(String victimTelephone) {
        this.victimTelephone = victimTelephone;
    }

    public String getVictimAddress() {
        return victimAddress;
    }

    public void setVictimAddress(String victimAddress) {
        this.victimAddress = victimAddress;
    }

    public Date getVictimDateOfBirth() {
        return victimDateOfBirth;
    }

    public void setVictimDateOfBirth(Date victimDateOfBirth) {
        this.victimDateOfBirth = victimDateOfBirth;
    }

    public Short getVictimClass() {
        return victimClass;
    }

    public void setVictimClass(Short victimClass) {
        this.victimClass = victimClass;
    }

    public Integer getVictimId() {
        return victimId;
    }

    public void setVictimId(Integer victimId) {
        this.victimId = victimId;
    }

    public Short getResidenceMunicipality() {
        return residenceMunicipality;
    }

    public void setResidenceMunicipality(Short residenceMunicipality) {
        this.residenceMunicipality = residenceMunicipality;
    }

    public Short getResidenceDepartment() {
        return residenceDepartment;
    }

    public void setResidenceDepartment(Short residenceDepartment) {
        this.residenceDepartment = residenceDepartment;
    }

    public Tags getTagId() {
        return tagId;
    }

    public void setTagId(Tags tagId) {
        this.tagId = tagId;
    }

    public Integer getFirstTagId() {
        return firstTagId;
    }

    public void setFirstTagId(Integer firstTagId) {
        this.firstTagId = firstTagId;
    }

    @XmlTransient
    public List<VulnerableGroups> getVulnerableGroupsList() {
        return vulnerableGroupsList;
    }

    public void setVulnerableGroupsList(List<VulnerableGroups> vulnerableGroupsList) {
        this.vulnerableGroupsList = vulnerableGroupsList;
    }

    @XmlTransient
    public List<Others> getOthersList() {
        return othersList;
    }

    public void setOthersList(List<Others> othersList) {
        this.othersList = othersList;
    }

    public Neighborhoods getVictimNeighborhoodId() {
        return victimNeighborhoodId;
    }

    public void setVictimNeighborhoodId(Neighborhoods victimNeighborhoodId) {
        this.victimNeighborhoodId = victimNeighborhoodId;
    }

    public Jobs getJobId() {
        return jobId;
    }

    public void setJobId(Jobs jobId) {
        this.jobId = jobId;
    }

    public Insurance getInsuranceId() {
        return insuranceId;
    }

    public void setInsuranceId(Insurance insuranceId) {
        this.insuranceId = insuranceId;
    }

    public IdTypes getTypeId() {
        return typeId;
    }

    public void setTypeId(IdTypes typeId) {
        this.typeId = typeId;
    }

    public Genders getGenderId() {
        return genderId;
    }

    public void setGenderId(Genders genderId) {
        this.genderId = genderId;
    }

    public EthnicGroups getEthnicGroupId() {
        return ethnicGroupId;
    }

    public void setEthnicGroupId(EthnicGroups ethnicGroupId) {
        this.ethnicGroupId = ethnicGroupId;
    }

    @XmlTransient
    public List<NonFatalInjuries> getNonFatalInjuriesList() {
        return nonFatalInjuriesList;
    }

    public void setNonFatalInjuriesList(List<NonFatalInjuries> nonFatalInjuriesList) {
        this.nonFatalInjuriesList = nonFatalInjuriesList;
    }

    @XmlTransient
    public List<FatalInjuries> getFatalInjuriesList() {
        return fatalInjuriesList;
    }

    public void setFatalInjuriesList(List<FatalInjuries> fatalInjuriesList) {
        this.fatalInjuriesList = fatalInjuriesList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (victimId != null ? victimId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Victims)) {
            return false;
        }
        Victims other = (Victims) object;
        if ((this.victimId == null && other.victimId != null) || (this.victimId != null && !this.victimId.equals(other.victimId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.pojo.Victims[ victimId=" + victimId + " ]";
    }
}
