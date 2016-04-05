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
@Table(name = "diagnoses", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Diagnoses.findAll", query = "SELECT d FROM Diagnoses d"),
    @NamedQuery(name = "Diagnoses.findByDiagnosisId", query = "SELECT d FROM Diagnoses d WHERE d.diagnosisId = :diagnosisId"),
    @NamedQuery(name = "Diagnoses.findByDiagnosisName", query = "SELECT d FROM Diagnoses d WHERE d.diagnosisName = :diagnosisName")})
public class Diagnoses implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "diagnosis_id", nullable = false, length = 10)
    private String diagnosisId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 400)
    @Column(name = "diagnosis_name", nullable = false, length = 400)
    private String diagnosisName;
//    @JoinTable(name = "non_fatal_diagnosis", joinColumns = {
//        @JoinColumn(name = "diagnosis_id", referencedColumnName = "diagnosis_id", nullable = false)}, inverseJoinColumns = {
//        @JoinColumn(name = "non_fatal_injury_id", referencedColumnName = "non_fatal_injury_id", nullable = false)})
//    @ManyToMany
//    private List<NonFatalInjuries> nonFatalInjuriesList;
    @ManyToMany(mappedBy = "diagnosesList")    
    private List<NonFatalInjuries> nonFatalInjuriesList;

    public Diagnoses() {
    }

    public Diagnoses(String diagnosisId) {
	this.diagnosisId = diagnosisId;
    }

    public Diagnoses(String diagnosisId, String diagnosisName) {
	this.diagnosisId = diagnosisId;
	this.diagnosisName = diagnosisName;
    }

    public String getDiagnosisId() {
	return diagnosisId;
    }

    public void setDiagnosisId(String diagnosisId) {
	this.diagnosisId = diagnosisId;
    }

    public String getDiagnosisName() {
	return diagnosisName;
    }

    public void setDiagnosisName(String diagnosisName) {
	this.diagnosisName = diagnosisName;
    }

    @XmlTransient
    public List<NonFatalInjuries> getNonFatalInjuriesList() {
	return nonFatalInjuriesList;
    }

    public void setNonFatalInjuriesList(List<NonFatalInjuries> nonFatalInjuriesList) {
	this.nonFatalInjuriesList = nonFatalInjuriesList;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (diagnosisId != null ? diagnosisId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof Diagnoses)) {
	    return false;
	}
	Diagnoses other = (Diagnoses) object;
	if ((this.diagnosisId == null && other.diagnosisId != null) || (this.diagnosisId != null && !this.diagnosisId.equals(other.diagnosisId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.Diagnoses[ diagnosisId=" + diagnosisId + " ]";
    }
    
}
