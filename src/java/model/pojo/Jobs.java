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
@Table(name = "jobs", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"job_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Jobs.findAll", query = "SELECT j FROM Jobs j"),
    @NamedQuery(name = "Jobs.findByJobId", query = "SELECT j FROM Jobs j WHERE j.jobId = :jobId"),
    @NamedQuery(name = "Jobs.findByJobName", query = "SELECT j FROM Jobs j WHERE j.jobName = :jobName")})
public class Jobs implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "job_id", nullable = false)
    private Integer jobId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "job_name", nullable = false, length = 2147483647)
    private String jobName;
    @OneToMany(mappedBy = "jobId")
    private List<Victims> victimsList;
    @OneToMany(mappedBy = "occupation")
    private List<SivigilaAggresor> sivigilaAggresorList;

    public Jobs() {
    }

    public Jobs(Integer jobId) {
	this.jobId = jobId;
    }

    public Jobs(Integer jobId, String jobName) {
	this.jobId = jobId;
	this.jobName = jobName;
    }

    public Integer getJobId() {
	return jobId;
    }

    public void setJobId(Integer jobId) {
	this.jobId = jobId;
    }

    public String getJobName() {
	return jobName;
    }

    public void setJobName(String jobName) {
	this.jobName = jobName;
    }

    @XmlTransient
    public List<Victims> getVictimsList() {
	return victimsList;
    }

    public void setVictimsList(List<Victims> victimsList) {
	this.victimsList = victimsList;
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
	hash += (jobId != null ? jobId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof Jobs)) {
	    return false;
	}
	Jobs other = (Jobs) object;
	if ((this.jobId == null && other.jobId != null) || (this.jobId != null && !this.jobId.equals(other.jobId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.Jobs[ jobId=" + jobId + " ]";
    }
    
}
