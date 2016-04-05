/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author SANTOS
 */
@Entity
@Table(name = "fields", catalog = "od", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Fields.findAll", query = "SELECT f FROM Fields f"),
    @NamedQuery(name = "Fields.findByFormId", query = "SELECT f FROM Fields f WHERE f.fieldsPK.formId = :formId"),
    @NamedQuery(name = "Fields.findByFieldName", query = "SELECT f FROM Fields f WHERE f.fieldsPK.fieldName = :fieldName"),
    @NamedQuery(name = "Fields.findByFieldOrder", query = "SELECT f FROM Fields f WHERE f.fieldOrder = :fieldOrder"),
    @NamedQuery(name = "Fields.findByFieldDescription", query = "SELECT f FROM Fields f WHERE f.fieldDescription = :fieldDescription"),
    @NamedQuery(name = "Fields.findByFieldType", query = "SELECT f FROM Fields f WHERE f.fieldType = :fieldType"),
    @NamedQuery(name = "Fields.findByFieldOptional", query = "SELECT f FROM Fields f WHERE f.fieldOptional = :fieldOptional")})
public class Fields implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected FieldsPK fieldsPK;
    @Column(name = "field_order")
    private Integer fieldOrder;
    @Size(max = 2147483647)
    @Column(name = "field_description", length = 2147483647)
    private String fieldDescription;
    @Size(max = 2147483647)
    @Column(name = "field_type", length = 2147483647)
    private String fieldType;
    @Basic(optional = false)
    @NotNull
    @Column(name = "field_optional", nullable = false)
    private boolean fieldOptional;
    @Size(max = 2147483647)
    @Column(name = "field_name_small", length = 2147483647)
    private String fieldNameSmall;
    @JoinColumn(name = "form_id", referencedColumnName = "form_id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Forms forms;

    public Fields() {
    }

    public Fields(FieldsPK fieldsPK) {
	this.fieldsPK = fieldsPK;
    }

    public Fields(FieldsPK fieldsPK, boolean fieldOptional) {
	this.fieldsPK = fieldsPK;
	this.fieldOptional = fieldOptional;
    }

    public Fields(String formId, String fieldName) {
	this.fieldsPK = new FieldsPK(formId, fieldName);
    }

    public FieldsPK getFieldsPK() {
	return fieldsPK;
    }

    public void setFieldsPK(FieldsPK fieldsPK) {
	this.fieldsPK = fieldsPK;
    }

    public Integer getFieldOrder() {
	return fieldOrder;
    }

    public void setFieldOrder(Integer fieldOrder) {
	this.fieldOrder = fieldOrder;
    }

    public String getFieldDescription() {
	return fieldDescription;
    }

    public void setFieldDescription(String fieldDescription) {
	this.fieldDescription = fieldDescription;
    }

    public String getFieldType() {
	return fieldType;
    }

    public void setFieldType(String fieldType) {
	this.fieldType = fieldType;
    }

    public boolean getFieldOptional() {
	return fieldOptional;
    }

    public void setFieldOptional(boolean fieldOptional) {
	this.fieldOptional = fieldOptional;
    }
    
    public String getFieldNameSmall() {
        return fieldNameSmall;
    }

    public void setFieldNameSmall(String fieldNameSmall) {
        this.fieldNameSmall = fieldNameSmall;
    }

    public Forms getForms() {
	return forms;
    }

    public void setForms(Forms forms) {
	this.forms = forms;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (fieldsPK != null ? fieldsPK.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof Fields)) {
	    return false;
	}
	Fields other = (Fields) object;
	if ((this.fieldsPK == null && other.fieldsPK != null) || (this.fieldsPK != null && !this.fieldsPK.equals(other.fieldsPK))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return fieldsPK.getFieldName();
    }
    
}
