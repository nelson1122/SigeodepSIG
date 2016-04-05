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
@Table(name = "non_fatal_data_sources", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "NonFatalDataSources.findAll", query = "SELECT n FROM NonFatalDataSources n"),
    @NamedQuery(name = "NonFatalDataSources.findByNonFatalDataSourceId", query = "SELECT n FROM NonFatalDataSources n WHERE n.nonFatalDataSourceId = :nonFatalDataSourceId"),
    @NamedQuery(name = "NonFatalDataSources.findByNonFatalDataSourceName", query = "SELECT n FROM NonFatalDataSources n WHERE n.nonFatalDataSourceName = :nonFatalDataSourceName"),
    @NamedQuery(name = "NonFatalDataSources.findByNonFatalDataSourceAddress", query = "SELECT n FROM NonFatalDataSources n WHERE n.nonFatalDataSourceAddress = :nonFatalDataSourceAddress"),
    @NamedQuery(name = "NonFatalDataSources.findByNonFatalDataSourceNeighborhoodId", query = "SELECT n FROM NonFatalDataSources n WHERE n.nonFatalDataSourceNeighborhoodId = :nonFatalDataSourceNeighborhoodId"),
    @NamedQuery(name = "NonFatalDataSources.findByNonFatalDataSourceType", query = "SELECT n FROM NonFatalDataSources n WHERE n.nonFatalDataSourceType = :nonFatalDataSourceType")})
public class NonFatalDataSources implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "non_fatal_data_source_id", nullable = false)
    private Short nonFatalDataSourceId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "non_fatal_data_source_name", nullable = false, length = 100)
    private String nonFatalDataSourceName;
    @Size(max = 100)
    @Column(name = "non_fatal_data_source_address", length = 100)
    private String nonFatalDataSourceAddress;
    @Size(max = 10)
    @Column(name = "non_fatal_data_source_neighborhood_id", length = 10)
    private String nonFatalDataSourceNeighborhoodId;
    @Column(name = "non_fatal_data_source_type")
    private Short nonFatalDataSourceType;
    @Column(name = "non_fatal_data_source_form")
    private Short nonFatalDataSourceForm;
    @OneToMany(mappedBy = "nonFatalDataSourceId")
    private List<NonFatalInjuries> nonFatalInjuriesList;
    @OneToMany(mappedBy = "submittedDataSourceId")
    private List<NonFatalInjuries> nonFatalInjuriesList1;

    public NonFatalDataSources() {
    }

    public NonFatalDataSources(Short nonFatalDataSourceId) {
        this.nonFatalDataSourceId = nonFatalDataSourceId;
    }

    public NonFatalDataSources(Short nonFatalDataSourceId, String nonFatalDataSourceName) {
        this.nonFatalDataSourceId = nonFatalDataSourceId;
        this.nonFatalDataSourceName = nonFatalDataSourceName;
    }

    public Short getNonFatalDataSourceId() {
        return nonFatalDataSourceId;
    }

    public void setNonFatalDataSourceId(Short nonFatalDataSourceId) {
        this.nonFatalDataSourceId = nonFatalDataSourceId;
    }

    public String getNonFatalDataSourceName() {
        return nonFatalDataSourceName;
    }

    public void setNonFatalDataSourceName(String nonFatalDataSourceName) {
        this.nonFatalDataSourceName = nonFatalDataSourceName;
    }

    public String getNonFatalDataSourceAddress() {
        return nonFatalDataSourceAddress;
    }

    public void setNonFatalDataSourceAddress(String nonFatalDataSourceAddress) {
        this.nonFatalDataSourceAddress = nonFatalDataSourceAddress;
    }

    public String getNonFatalDataSourceNeighborhoodId() {
        return nonFatalDataSourceNeighborhoodId;
    }

    public void setNonFatalDataSourceNeighborhoodId(String nonFatalDataSourceNeighborhoodId) {
        this.nonFatalDataSourceNeighborhoodId = nonFatalDataSourceNeighborhoodId;
    }

    public Short getNonFatalDataSourceType() {
        return nonFatalDataSourceType;
    }

    public void setNonFatalDataSourceType(Short nonFatalDataSourceType) {
        this.nonFatalDataSourceType = nonFatalDataSourceType;
    }

    public Short getNonFatalDataSourceForm() {
        return nonFatalDataSourceForm;
    }

    public void setNonFatalDataSourceForm(Short nonFatalDataSourceForm) {
        this.nonFatalDataSourceForm = nonFatalDataSourceForm;
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
        hash += (nonFatalDataSourceId != null ? nonFatalDataSourceId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof NonFatalDataSources)) {
            return false;
        }
        NonFatalDataSources other = (NonFatalDataSources) object;
        if ((this.nonFatalDataSourceId == null && other.nonFatalDataSourceId != null) || (this.nonFatalDataSourceId != null && !this.nonFatalDataSourceId.equals(other.nonFatalDataSourceId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.pojo.NonFatalDataSources[ nonFatalDataSourceId=" + nonFatalDataSourceId + " ]";
    }
}
