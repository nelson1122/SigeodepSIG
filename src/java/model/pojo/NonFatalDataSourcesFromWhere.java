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
@Table(name = "non_fatal_data_sources_from_where", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "NonFatalDataSourcesFromWhere.findAll", query = "SELECT n FROM NonFatalDataSourcesFromWhere n"),
    @NamedQuery(name = "NonFatalDataSourcesFromWhere.findByNonFatalDataSourcesFromWhereId", query = "SELECT n FROM NonFatalDataSourcesFromWhere n WHERE n.nonFatalDataSourcesFromWhereId = :nonFatalDataSourcesFromWhereId"),
    @NamedQuery(name = "NonFatalDataSourcesFromWhere.findByNonFatalDataSourcesFromWhereName", query = "SELECT n FROM NonFatalDataSourcesFromWhere n WHERE n.nonFatalDataSourcesFromWhereName = :nonFatalDataSourcesFromWhereName")})
public class NonFatalDataSourcesFromWhere implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "non_fatal_data_sources_from_where_id", nullable = false)
    private Short nonFatalDataSourcesFromWhereId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "non_fatal_data_sources_from_where_name", nullable = false, length = 100)
    private String nonFatalDataSourcesFromWhereName;
    @OneToMany(mappedBy = "submittedFormWhereId")
    private List<NonFatalDomesticViolence> nonFatalDomesticViolenceList;

    public NonFatalDataSourcesFromWhere() {
    }

    public NonFatalDataSourcesFromWhere(Short nonFatalDataSourcesFromWhereId) {
	this.nonFatalDataSourcesFromWhereId = nonFatalDataSourcesFromWhereId;
    }

    public NonFatalDataSourcesFromWhere(Short nonFatalDataSourcesFromWhereId, String nonFatalDataSourcesFromWhereName) {
	this.nonFatalDataSourcesFromWhereId = nonFatalDataSourcesFromWhereId;
	this.nonFatalDataSourcesFromWhereName = nonFatalDataSourcesFromWhereName;
    }

    public Short getNonFatalDataSourcesFromWhereId() {
	return nonFatalDataSourcesFromWhereId;
    }

    public void setNonFatalDataSourcesFromWhereId(Short nonFatalDataSourcesFromWhereId) {
	this.nonFatalDataSourcesFromWhereId = nonFatalDataSourcesFromWhereId;
    }

    public String getNonFatalDataSourcesFromWhereName() {
	return nonFatalDataSourcesFromWhereName;
    }

    public void setNonFatalDataSourcesFromWhereName(String nonFatalDataSourcesFromWhereName) {
	this.nonFatalDataSourcesFromWhereName = nonFatalDataSourcesFromWhereName;
    }

    @XmlTransient
    public List<NonFatalDomesticViolence> getNonFatalDomesticViolenceList() {
	return nonFatalDomesticViolenceList;
    }

    public void setNonFatalDomesticViolenceList(List<NonFatalDomesticViolence> nonFatalDomesticViolenceList) {
	this.nonFatalDomesticViolenceList = nonFatalDomesticViolenceList;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (nonFatalDataSourcesFromWhereId != null ? nonFatalDataSourcesFromWhereId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof NonFatalDataSourcesFromWhere)) {
	    return false;
	}
	NonFatalDataSourcesFromWhere other = (NonFatalDataSourcesFromWhere) object;
	if ((this.nonFatalDataSourcesFromWhereId == null && other.nonFatalDataSourcesFromWhereId != null) || (this.nonFatalDataSourcesFromWhereId != null && !this.nonFatalDataSourcesFromWhereId.equals(other.nonFatalDataSourcesFromWhereId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.NonFatalDataSourcesFromWhere[ nonFatalDataSourcesFromWhereId=" + nonFatalDataSourcesFromWhereId + " ]";
    }
    
}
