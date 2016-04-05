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
@Table(name = "forms", catalog = "od", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"form_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Forms.findAll", query = "SELECT f FROM Forms f"),
    @NamedQuery(name = "Forms.findByFormId", query = "SELECT f FROM Forms f WHERE f.formId = :formId"),
    @NamedQuery(name = "Forms.findByFormName", query = "SELECT f FROM Forms f WHERE f.formName = :formName")})
public class Forms implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 12)
    @Column(name = "form_id", nullable = false, length = 12)
    private String formId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "form_name", nullable = false, length = 50)
    private String formName;
    @JoinTable(name = "form_source", joinColumns = {
        @JoinColumn(name = "form_id", referencedColumnName = "form_id", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "source_id", referencedColumnName = "non_fatal_data_source_id", nullable = false)})
    @ManyToMany
    private List<NonFatalDataSources> nonFatalDataSourcesList;
    @OneToMany(mappedBy = "formId")
    private List<RelationGroup> relationGroupList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "forms")
    private List<Fields> fieldsList;
    @OneToMany(mappedBy = "formId")
    private List<Tags> tagsList;
    

    public Forms() {
    }

    public Forms(String formId) {
	this.formId = formId;
    }

    public Forms(String formId, String formName) {
	this.formId = formId;
	this.formName = formName;
    }

    public String getFormId() {
	return formId;
    }

    public void setFormId(String formId) {
	this.formId = formId;
    }

    public String getFormName() {
	return formName;
    }

    public void setFormName(String formName) {
	this.formName = formName;
    }

//    @XmlTransient
//    public List<Sources> getSourcesList() {
//	return sourcesList;
//    }
//
//    public void setSourcesList(List<Sources> sourcesList) {
//	this.sourcesList = sourcesList;
//    }
    
    @XmlTransient
    public List<NonFatalDataSources> getNonFatalDataSourcesList() {
        return nonFatalDataSourcesList;
    }

    public void setNonFatalDataSourcesList(List<NonFatalDataSources> nonFatalDataSourcesList) {
        this.nonFatalDataSourcesList = nonFatalDataSourcesList;
    }

    @XmlTransient
    public List<RelationGroup> getRelationGroupList() {
	return relationGroupList;
    }

    public void setRelationGroupList(List<RelationGroup> relationGroupList) {
	this.relationGroupList = relationGroupList;
    }
    
    @XmlTransient
    public List<Tags> getTagsList() {
        return tagsList;
    }

    public void setTagsList(List<Tags> tagsList) {
        this.tagsList = tagsList;
    }    

    @XmlTransient
    public List<Fields> getFieldsList() {
	return fieldsList;
    }

    public void setFieldsList(List<Fields> fieldsList) {
	this.fieldsList = fieldsList;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (formId != null ? formId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof Forms)) {
	    return false;
	}
	Forms other = (Forms) object;
	if ((this.formId == null && other.formId != null) || (this.formId != null && !this.formId.equals(other.formId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return formId;
    }
    
}
