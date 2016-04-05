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
@Table(name = "accident_mechanisms", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"accident_mechanism_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AccidentMechanisms.findAll", query = "SELECT a FROM AccidentMechanisms a"),
    @NamedQuery(name = "AccidentMechanisms.findByAccidentMechanismId", query = "SELECT a FROM AccidentMechanisms a WHERE a.accidentMechanismId = :accidentMechanismId"),
    @NamedQuery(name = "AccidentMechanisms.findByAccidentMechanismName", query = "SELECT a FROM AccidentMechanisms a WHERE a.accidentMechanismName = :accidentMechanismName")})
public class AccidentMechanisms implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "accident_mechanism_id", nullable = false)
    private Short accidentMechanismId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "accident_mechanism_name", nullable = false, length = 2147483647)
    private String accidentMechanismName;
    @OneToMany(mappedBy = "deathMechanismId")
    private List<FatalInjuryAccident> fatalInjuryAccidentList;

    public AccidentMechanisms() {
    }

    public AccidentMechanisms(Short accidentMechanismId) {
	this.accidentMechanismId = accidentMechanismId;
    }

    public AccidentMechanisms(Short accidentMechanismId, String accidentMechanismName) {
	this.accidentMechanismId = accidentMechanismId;
	this.accidentMechanismName = accidentMechanismName;
    }

    public Short getAccidentMechanismId() {
	return accidentMechanismId;
    }

    public void setAccidentMechanismId(Short accidentMechanismId) {
	this.accidentMechanismId = accidentMechanismId;
    }

    public String getAccidentMechanismName() {
	return accidentMechanismName;
    }

    public void setAccidentMechanismName(String accidentMechanismName) {
	this.accidentMechanismName = accidentMechanismName;
    }

    @XmlTransient
    public List<FatalInjuryAccident> getFatalInjuryAccidentList() {
	return fatalInjuryAccidentList;
    }

    public void setFatalInjuryAccidentList(List<FatalInjuryAccident> fatalInjuryAccidentList) {
	this.fatalInjuryAccidentList = fatalInjuryAccidentList;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (accidentMechanismId != null ? accidentMechanismId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof AccidentMechanisms)) {
	    return false;
	}
	AccidentMechanisms other = (AccidentMechanisms) object;
	if ((this.accidentMechanismId == null && other.accidentMechanismId != null) || (this.accidentMechanismId != null && !this.accidentMechanismId.equals(other.accidentMechanismId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.AccidentMechanisms[ accidentMechanismId=" + accidentMechanismId + " ]";
    }
    
}
