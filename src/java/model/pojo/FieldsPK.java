/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pojo;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author SANTOS
 */
    @Embeddable
public class FieldsPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 12)
    @Column(name = "form_id", nullable = false, length = 12)
    private String formId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "field_name", nullable = false, length = 20)
    private String fieldName;

    public FieldsPK() {
    }

    public FieldsPK(String formId, String fieldName) {
	this.formId = formId;
	this.fieldName = fieldName;
    }

    public String getFormId() {
	return formId;
    }

    public void setFormId(String formId) {
	this.formId = formId;
    }

    public String getFieldName() {
	return fieldName;
    }

    public void setFieldName(String fieldName) {
	this.fieldName = fieldName;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (formId != null ? formId.hashCode() : 0);
	hash += (fieldName != null ? fieldName.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof FieldsPK)) {
	    return false;
	}
	FieldsPK other = (FieldsPK) object;
	if ((this.formId == null && other.formId != null) || (this.formId != null && !this.formId.equals(other.formId))) {
	    return false;
	}
	if ((this.fieldName == null && other.fieldName != null) || (this.fieldName != null && !this.fieldName.equals(other.fieldName))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "model.pojo.FieldsPK[ formId=" + formId + ", fieldName=" + fieldName + " ]";
    }
    
}
