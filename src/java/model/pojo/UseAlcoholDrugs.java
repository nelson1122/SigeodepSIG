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
@Table(name = "use_alcohol_drugs", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"use_alcohol_drugs_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UseAlcoholDrugs.findAll", query = "SELECT u FROM UseAlcoholDrugs u"),
    @NamedQuery(name = "UseAlcoholDrugs.findByUseAlcoholDrugsId", query = "SELECT u FROM UseAlcoholDrugs u WHERE u.useAlcoholDrugsId = :useAlcoholDrugsId"),
    @NamedQuery(name = "UseAlcoholDrugs.findByUseAlcoholDrugsName", query = "SELECT u FROM UseAlcoholDrugs u WHERE u.useAlcoholDrugsName = :useAlcoholDrugsName")})
public class UseAlcoholDrugs implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "use_alcohol_drugs_id", nullable = false)
    private Short useAlcoholDrugsId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "use_alcohol_drugs_name", nullable = false, length = 20)
    private String useAlcoholDrugsName;
    @OneToMany(mappedBy = "useDrugsId")
    private List<NonFatalInjuries> nonFatalInjuriesList;
    @OneToMany(mappedBy = "useAlcoholId")
    private List<NonFatalInjuries> nonFatalInjuriesList1;

    public UseAlcoholDrugs() {
    }

    public UseAlcoholDrugs(Short useAlcoholDrugsId) {
	this.useAlcoholDrugsId = useAlcoholDrugsId;
    }

    public UseAlcoholDrugs(Short useAlcoholDrugsId, String useAlcoholDrugsName) {
	this.useAlcoholDrugsId = useAlcoholDrugsId;
	this.useAlcoholDrugsName = useAlcoholDrugsName;
    }

    public Short getUseAlcoholDrugsId() {
	return useAlcoholDrugsId;
    }

    public void setUseAlcoholDrugsId(Short useAlcoholDrugsId) {
	this.useAlcoholDrugsId = useAlcoholDrugsId;
    }

    public String getUseAlcoholDrugsName() {
	return useAlcoholDrugsName;
    }

    public void setUseAlcoholDrugsName(String useAlcoholDrugsName) {
	this.useAlcoholDrugsName = useAlcoholDrugsName;
    }

    @XmlTransient
    public List<NonFatalInjuries> getNonFatalInjuriesList() {
	return nonFatalInjuriesList;
    }

    public void setNonFatalInjuriesList(List<NonFatalInjuries> nonFatalInjuriesList) {
	this.nonFatalInjuriesList = nonFatalInjuriesList;
    }

    @XmlTransient
    public List<NonFatalInjuries> getNonFatalInjuriesList1() {
	return nonFatalInjuriesList1;
    }

    public void setNonFatalInjuriesList1(List<NonFatalInjuries> nonFatalInjuriesList1) {
	this.nonFatalInjuriesList1 = nonFatalInjuriesList1;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (useAlcoholDrugsId != null ? useAlcoholDrugsId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof UseAlcoholDrugs)) {
	    return false;
	}
	UseAlcoholDrugs other = (UseAlcoholDrugs) object;
	if ((this.useAlcoholDrugsId == null && other.useAlcoholDrugsId != null) || (this.useAlcoholDrugsId != null && !this.useAlcoholDrugsId.equals(other.useAlcoholDrugsId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.UseAlcoholDrugs[ useAlcoholDrugsId=" + useAlcoholDrugsId + " ]";
    }
    
}
