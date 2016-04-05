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
@Table(name = "accident_classes", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"accident_class_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AccidentClasses.findAll", query = "SELECT a FROM AccidentClasses a"),
    @NamedQuery(name = "AccidentClasses.findByAccidentClassId", query = "SELECT a FROM AccidentClasses a WHERE a.accidentClassId = :accidentClassId"),
    @NamedQuery(name = "AccidentClasses.findByAccidentClassName", query = "SELECT a FROM AccidentClasses a WHERE a.accidentClassName = :accidentClassName")})
public class AccidentClasses implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "accident_class_id", nullable = false)
    private Short accidentClassId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "accident_class_name", nullable = false, length = 2147483647)
    private String accidentClassName;
    @OneToMany(mappedBy = "accidentClassId")
    private List<FatalInjuryTraffic> fatalInjuryTrafficList;

    public AccidentClasses() {
    }

    public AccidentClasses(Short accidentClassId) {
	this.accidentClassId = accidentClassId;
    }

    public AccidentClasses(Short accidentClassId, String accidentClassName) {
	this.accidentClassId = accidentClassId;
	this.accidentClassName = accidentClassName;
    }

    public Short getAccidentClassId() {
	return accidentClassId;
    }

    public void setAccidentClassId(Short accidentClassId) {
	this.accidentClassId = accidentClassId;
    }

    public String getAccidentClassName() {
	return accidentClassName;
    }

    public void setAccidentClassName(String accidentClassName) {
	this.accidentClassName = accidentClassName;
    }

    @XmlTransient
    public List<FatalInjuryTraffic> getFatalInjuryTrafficList() {
	return fatalInjuryTrafficList;
    }

    public void setFatalInjuryTrafficList(List<FatalInjuryTraffic> fatalInjuryTrafficList) {
	this.fatalInjuryTrafficList = fatalInjuryTrafficList;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (accidentClassId != null ? accidentClassId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof AccidentClasses)) {
	    return false;
	}
	AccidentClasses other = (AccidentClasses) object;
	if ((this.accidentClassId == null && other.accidentClassId != null) || (this.accidentClassId != null && !this.accidentClassId.equals(other.accidentClassId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.AccidentClasses[ accidentClassId=" + accidentClassId + " ]";
    }
    
}
