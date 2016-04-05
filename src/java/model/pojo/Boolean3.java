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
@Table(name = "boolean3", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Boolean3.findAll", query = "SELECT b FROM Boolean3 b"),
    @NamedQuery(name = "Boolean3.findByBooleanId", query = "SELECT b FROM Boolean3 b WHERE b.booleanId = :booleanId"),
    @NamedQuery(name = "Boolean3.findByBooleanName", query = "SELECT b FROM Boolean3 b WHERE b.booleanName = :booleanName")})
public class Boolean3 implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "boolean_id", nullable = false)
    private Short booleanId;
    @Size(max = 2147483647)
    @Column(name = "boolean_name", length = 2147483647)
    private String booleanName;
    @OneToMany(mappedBy = "previousAttempt")
    private List<NonFatalSelfInflicted> nonFatalSelfInflictedList;
    @OneToMany(mappedBy = "mentalAntecedent")
    private List<NonFatalSelfInflicted> nonFatalSelfInflictedList1;
    @OneToMany(mappedBy = "previousAntecedent")
    private List<NonFatalInterpersonal> nonFatalInterpersonalList;
    @OneToMany(mappedBy = "previousAttempt")
    private List<FatalInjurySuicide> fatalInjurySuicideList;
    @OneToMany(mappedBy = "mentalAntecedent")
    private List<FatalInjurySuicide> fatalInjurySuicideList1;
    @OneToMany(mappedBy = "furtherFieldwork")
    private List<SivigilaEvent> sivigilaEventList;
    @OneToMany(mappedBy = "recommendedProtection")
    private List<SivigilaEvent> sivigilaEventList1;
    @OneToMany(mappedBy = "conflictZone")
    private List<SivigilaEvent> sivigilaEventList2;
    @OneToMany(mappedBy = "alcoholOrDrugs")
    private List<SivigilaAggresor> sivigilaAggresorList;
    @OneToMany(mappedBy = "liveTogether")
    private List<SivigilaAggresor> sivigilaAggresorList1;
    @OneToMany(mappedBy = "antecedent")
    private List<SivigilaVictim> sivigilaVictimList;
    
    public Boolean3() {
    }

    public Boolean3(Short booleanId) {
        this.booleanId = booleanId;
    }

    public Short getBooleanId() {
        return booleanId;
    }

    public void setBooleanId(Short booleanId) {
        this.booleanId = booleanId;
    }

    public String getBooleanName() {
        return booleanName;
    }

    public void setBooleanName(String booleanName) {
        this.booleanName = booleanName;
    }

    @XmlTransient
    public List<NonFatalSelfInflicted> getNonFatalSelfInflictedList() {
        return nonFatalSelfInflictedList;
    }

    public void setNonFatalSelfInflictedList(List<NonFatalSelfInflicted> nonFatalSelfInflictedList) {
        this.nonFatalSelfInflictedList = nonFatalSelfInflictedList;
    }

    @XmlTransient
    public List<NonFatalSelfInflicted> getNonFatalSelfInflictedList1() {
        return nonFatalSelfInflictedList1;
    }

    public void setNonFatalSelfInflictedList1(List<NonFatalSelfInflicted> nonFatalSelfInflictedList1) {
        this.nonFatalSelfInflictedList1 = nonFatalSelfInflictedList1;
    }

    @XmlTransient
    public List<NonFatalInterpersonal> getNonFatalInterpersonalList() {
        return nonFatalInterpersonalList;
    }

    public void setNonFatalInterpersonalList(List<NonFatalInterpersonal> nonFatalInterpersonalList) {
        this.nonFatalInterpersonalList = nonFatalInterpersonalList;
    }

    @XmlTransient
    public List<FatalInjurySuicide> getFatalInjurySuicideList() {
        return fatalInjurySuicideList;
    }

    public void setFatalInjurySuicideList(List<FatalInjurySuicide> fatalInjurySuicideList) {
        this.fatalInjurySuicideList = fatalInjurySuicideList;
    }

    @XmlTransient
    public List<FatalInjurySuicide> getFatalInjurySuicideList1() {
        return fatalInjurySuicideList1;
    }

    public void setFatalInjurySuicideList1(List<FatalInjurySuicide> fatalInjurySuicideList1) {
        this.fatalInjurySuicideList1 = fatalInjurySuicideList1;
    }

    @XmlTransient
    public List<SivigilaEvent> getSivigilaEventList() {
        return sivigilaEventList;
    }

    public void setSivigilaEventList(List<SivigilaEvent> sivigilaEventList) {
        this.sivigilaEventList = sivigilaEventList;
    }

    @XmlTransient
    public List<SivigilaEvent> getSivigilaEventList1() {
        return sivigilaEventList1;
    }

    public void setSivigilaEventList1(List<SivigilaEvent> sivigilaEventList1) {
        this.sivigilaEventList1 = sivigilaEventList1;
    }

    @XmlTransient
    public List<SivigilaEvent> getSivigilaEventList2() {
        return sivigilaEventList2;
    }

    public void setSivigilaEventList2(List<SivigilaEvent> sivigilaEventList2) {
        this.sivigilaEventList2 = sivigilaEventList2;
    }

    @XmlTransient
    public List<SivigilaAggresor> getSivigilaAggresorList() {
        return sivigilaAggresorList;
    }

    public void setSivigilaAggresorList(List<SivigilaAggresor> sivigilaAggresorList) {
        this.sivigilaAggresorList = sivigilaAggresorList;
    }

    @XmlTransient
    public List<SivigilaAggresor> getSivigilaAggresorList1() {
        return sivigilaAggresorList1;
    }

    public void setSivigilaAggresorList1(List<SivigilaAggresor> sivigilaAggresorList1) {
        this.sivigilaAggresorList1 = sivigilaAggresorList1;
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
        hash += (booleanId != null ? booleanId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Boolean3)) {
            return false;
        }
        Boolean3 other = (Boolean3) object;
        if ((this.booleanId == null && other.booleanId != null) || (this.booleanId != null && !this.booleanId.equals(other.booleanId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pojos.Boolean3[ booleanId=" + booleanId + " ]";
    }
    
}
